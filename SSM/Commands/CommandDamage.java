package SSM.Commands;

import SSM.GameManagers.KitManager;
import SSM.Kits.Kit;
import SSM.Utilities.DamageUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CommandDamage implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] args) {
        if(!(commandSender instanceof Player)) {
            return false;
        }
        Player player = (Player) commandSender;
        if (args.length == 1) {
            try {
                int number = Integer.parseInt(args[0]);
                DamageUtil.damage(player, null, number, 0, true);
                player.sendMessage("You were dealt " + number + " damage");
                return true;
            } catch (NumberFormatException e) {
                player.sendMessage("You need to input a number!");
                return true;
            }
        }
        return false;
    }

}
