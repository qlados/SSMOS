package SSM.GameManagers.Disguise;

import SSM.Utilities.Utils;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.*;
import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import javax.xml.crypto.Data;

public abstract class Disguise {

    protected String name;
    protected EntityType type;
    protected Player owner;
    protected EntityLiving living;
    protected EntityArmorStand armorstand;
    protected EntitySquid squid;

    public Disguise(Player owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public EntityType getType() {
        return type;
    }

    public Player getOwner() {
        return owner;
    }

    public EntityLiving getLiving() {
        return living;
    }

    public EntityArmorStand getArmorStand() {
        return armorstand;
    }

    public EntitySquid getSquid() {
        return squid;
    }

    public void spawnLiving() {
        if(living != null) {
            deleteLiving();
        }
        living = newLiving();
        armorstand = new EntityArmorStand(((CraftWorld) owner.getWorld()).getHandle());
        armorstand.setCustomName(ChatColor.YELLOW + owner.getName());
        armorstand.setCustomNameVisible(true);
        squid = new EntitySquid(((CraftWorld) owner.getWorld()).getHandle());
        /*ArmorStand as = (ArmorStand) owner.getWorld().spawnEntity(owner.getLocation(), EntityType.ARMOR_STAND);
        as.setCustomName(ChatColor.YELLOW + owner.getName());
        as.setCustomNameVisible(true);
        as.setGravity(false);
        as.setVisible(true);
        as.setSmall(false);
        Squid squid = (Squid) owner.getWorld().spawnEntity(owner.getLocation(), EntityType.SQUID);
        squid.setPassenger(as);
        living.getBukkitEntity().setPassenger(squid);*/
        //squid.getBukkitEntity().setPassenger(armorstand.getBukkitEntity());
        //living.getBukkitEntity().setPassenger(armorstand.getBukkitEntity());
        for(Player player : Bukkit.getOnlinePlayers()) {
            if(player.equals(owner)) {
                continue;
            }
            showDisguise(player);
        }
    }

    public void showDisguise(Player player) {
        living.setLocation(owner.getLocation().getX(), owner.getLocation().getY(), owner.getLocation().getZ(),
                owner.getLocation().getYaw(), owner.getLocation().getPitch());
        PacketPlayOutSpawnEntityLiving living_packet = new PacketPlayOutSpawnEntityLiving(living);
        Utils.sendPacket(player, living_packet);
        // Armor Stand Spawn
        armorstand.setLocation(owner.getLocation().getX(), owner.getLocation().getY(), owner.getLocation().getZ(),
                owner.getLocation().getYaw(), owner.getLocation().getPitch());
        PacketPlayOutSpawnEntityLiving armorstand_packet = new PacketPlayOutSpawnEntityLiving(armorstand);
        Utils.sendPacket(player, armorstand_packet);
        // Squid Spawn
        squid.setLocation(owner.getLocation().getX(), owner.getLocation().getY(), owner.getLocation().getZ(),
                owner.getLocation().getYaw(), owner.getLocation().getPitch());
        PacketPlayOutSpawnEntityLiving squid_packet = new PacketPlayOutSpawnEntityLiving(squid);
        Utils.sendPacket(player, squid_packet);
        // Invisibility for Armor Stand
        /*DataWatcher dw = armorstand.getDataWatcher();
        dw.watch(0, (byte) 0x20);
        PacketPlayOutEntityMetadata invisiblity_packet = new PacketPlayOutEntityMetadata(armorstand.getId(), dw, true);
        Utils.sendPacketToAll(invisiblity_packet);
        // Invisibility for Squid
        dw = squid.getDataWatcher();
        dw.watch(0, (byte) 0x20);
        invisiblity_packet = new PacketPlayOutEntityMetadata(squid.getId(), dw, true);
        Utils.sendPacketToAll(invisiblity_packet);*/
    }

    public void update() {
        Location location = owner.getLocation();
        // Hide the disguised player from other players
        owner.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 10000000, 1, false, false));
        // Remove invisibility effect from disguised player
        PacketPlayOutRemoveEntityEffect remove_effect_packet = new PacketPlayOutRemoveEntityEffect(owner.getEntityId(), new MobEffect(
                MobEffectList.INVISIBILITY.id, 0));
        Utils.sendPacket(owner, remove_effect_packet);
        // Hide the armor too
        PacketPlayOutEntityEquipment handPacket = new PacketPlayOutEntityEquipment(owner.getEntityId(), 0, null);
        PacketPlayOutEntityEquipment helmetPacket = new PacketPlayOutEntityEquipment(owner.getEntityId(), 1, null);
        PacketPlayOutEntityEquipment chestPacket = new PacketPlayOutEntityEquipment(owner.getEntityId(), 2, null);
        PacketPlayOutEntityEquipment legPacket = new PacketPlayOutEntityEquipment(owner.getEntityId(), 3, null);
        PacketPlayOutEntityEquipment bootsPacket = new PacketPlayOutEntityEquipment(owner.getEntityId(), 4, null);
        for(Player hidefrom : Bukkit.getOnlinePlayers()) {
            if(owner.equals(hidefrom)) {
                continue;
            }
            Utils.sendPacket(hidefrom, handPacket);
            Utils.sendPacket(hidefrom, helmetPacket);
            Utils.sendPacket(hidefrom, chestPacket);
            Utils.sendPacket(hidefrom, legPacket);
            Utils.sendPacket(hidefrom, bootsPacket);
        }
        // Hide the mob from the disguised player
        PacketPlayOutEntityDestroy disguise_destroy_packet = new PacketPlayOutEntityDestroy(living.getId());
        Utils.sendPacket(owner, disguise_destroy_packet);
        // Don't teleport to spectator player if the mob is dead
        if(living.dead) {
            return;
        }
        living.setPositionRotation(location.getX(), location.getY(), location.getZ(),
                owner.getLocation().getYaw(), owner.getLocation().getPitch());
        PacketPlayOutEntityTeleport teleport_packet = new PacketPlayOutEntityTeleport(living);
        Utils.sendPacketToAllBut(owner, teleport_packet);
        PacketPlayOutEntityHeadRotation head_packet= new PacketPlayOutEntityHeadRotation(living,
                (byte) ((location.getYaw() * 256.0F) / 360.0F));
        Utils.sendPacketToAllBut(owner, head_packet);
        // From living.mount source code all the way to Entity.class mount
        // In the Entity.class al() method appears to be where it sets the passengers position
        squid.setPositionRotation(location.getX(), living.locY + living.an() + squid.am(), location.getZ(),
                owner.getLocation().getYaw(), owner.getLocation().getPitch());
        teleport_packet = new PacketPlayOutEntityTeleport(squid);
        Utils.sendPacketToAllBut(owner, teleport_packet);
        armorstand.setPositionRotation(location.getX(), squid.locY - 1.25f, location.getZ(),
                owner.getLocation().getYaw(), owner.getLocation().getPitch());
        teleport_packet = new PacketPlayOutEntityTeleport(armorstand);
        Utils.sendPacketToAllBut(owner, teleport_packet);
    }

    public void deleteLiving() {
        if(living == null) {
            return;
        }
        PacketPlayOutEntityDestroy destroy_living_packet = new PacketPlayOutEntityDestroy(living.getId());
        PacketPlayOutEntityDestroy destroy_armorstand_packet = new PacketPlayOutEntityDestroy(armorstand.getId());
        PacketPlayOutEntityDestroy destroy_squid_packet = new PacketPlayOutEntityDestroy(squid.getId());
        for(Player player : Bukkit.getOnlinePlayers()) {
            Utils.sendPacket(player, destroy_living_packet);
            Utils.sendPacket(player, destroy_armorstand_packet);
            Utils.sendPacket(player, destroy_squid_packet);
        }
        owner.removePotionEffect(PotionEffectType.INVISIBILITY);
        living = null;
        armorstand = null;
        squid = null;
    }

    protected abstract EntityLiving newLiving();

}