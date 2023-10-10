package SSM.Abilities;

import SSM.GameManagers.OwnerEvents.OwnerRightClickEvent;
import SSM.GameManagers.OwnerEvents.OwnerTakeDamageEvent;
import SSM.Utilities.VelocityUtil;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;

public class SuperSquid extends Ability implements OwnerRightClickEvent, OwnerTakeDamageEvent {

    private int task = -1;
    private boolean active = false;

    public SuperSquid() {
        super();
        this.name = "Super Squid";
        this.cooldownTime = 8;
        this.usage = AbilityUsage.BLOCKING;
    }

    public void onOwnerRightClick(PlayerInteractEvent e) {
        checkAndActivate();
    }

    public void activate() {
        task = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            int ticks = 0;
            @Override
            public void run() {
                ticks++;
                active = true;
                if(!owner.isBlocking() || ticks >= 22) {
                    Bukkit.getScheduler().cancelTask(task);
                    active = false;
                    return;
                }
                VelocityUtil.setVelocity(owner, 0.6, 0.1, 1, true);
                owner.getWorld().playSound(owner.getLocation(), Sound.SPLASH2, 0.2f, 1f);
                owner.getWorld().playEffect(owner.getLocation(), Effect.STEP_SOUND, 8);
            }
        }, 0L, 0L);
    }

    @Override
    public void onOwnerTakeDamage(EntityDamageEvent e) {
        if(active) {
            e.setCancelled(true);
        }
    }
}