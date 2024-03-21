package ssm.commands;

import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ssm.managers.GameManager;
import ssm.managers.gamemodes.*;
import ssm.managers.smashserver.SmashServer;
import ssm.utilities.ServerMessageType;
import ssm.utilities.Utils;

public class CommandMakeServer implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] args) {
        if(!commandSender.isOp()) {
            return true;
        }
        if(args.length == 1){
            switch (args[0]){
                case "solo":
                    SmashServer server = GameManager.createSmashServer(new SoloGamemode());
                    commandSender.sendMessage(ServerMessageType.ADMIN + " Created SSM Solo Server: " + server.toString());
                    if(commandSender instanceof Player) {
                        Player player = (Player) commandSender;
                        player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1f, 1f);
                    }
                    return true;
                case "ssmos":
                    SmashServer server2 = GameManager.createSmashServer(new SSMOSGamemode());
                    commandSender.sendMessage(ServerMessageType.ADMIN + " Created SSMOS Solo Server: " + server2.toString());
                    if(commandSender instanceof Player) {
                        Player player = (Player) commandSender;
                        player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1f, 1f);
                    }
                    return true;
                case "teams":
                    SmashServer server3 = GameManager.createSmashServer(new TeamsGamemode());
                    commandSender.sendMessage(ServerMessageType.ADMIN + " Created SSM Teams Server: " + server3.toString());
                    if(commandSender instanceof Player) {
                        Player player = (Player) commandSender;
                        player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1f, 1f);
                    }
                    return true;
                case "ssmosteams":
                    SmashServer server4 = GameManager.createSmashServer(new SSMOSTeamsGamemode());
                    commandSender.sendMessage(ServerMessageType.ADMIN + " Created SSMOS Teams Server: " + server4.toString());
                    if(commandSender instanceof Player) {
                        Player player = (Player) commandSender;
                        player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1f, 1f);
                    }
                    return true;
                case "boss":
                    SmashServer server5 = GameManager.createSmashServer(new BossGamemode());
                    commandSender.sendMessage(ServerMessageType.ADMIN + " Created Boss Server: " + server5.toString());
                    if(commandSender instanceof Player) {
                        Player player = (Player) commandSender;
                        player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1f, 1f);
                    }
                    return true;
                case "test":
                    SmashServer server6 = GameManager.createSmashServer(new TestingGamemode());
                    commandSender.sendMessage(ServerMessageType.ADMIN + " Created Testing Server: " + server6.toString());
                    if(commandSender instanceof Player) {
                        Player player = (Player) commandSender;
                        player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1f, 1f);
                    }
                    return true;
            }
        }
        commandSender.sendMessage("Use /makeserver solo/teams/ssmos/ssmosteams/boss/test");
        return false;
    }

}
