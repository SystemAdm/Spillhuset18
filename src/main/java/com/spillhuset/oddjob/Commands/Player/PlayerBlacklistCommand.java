package com.spillhuset.oddjob.Commands.Player;

import com.spillhuset.oddjob.Enums.Plugin;
import com.spillhuset.oddjob.Enums.Role;
import com.spillhuset.oddjob.Managers.MessageManager;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.Utils.OddPlayer;
import com.spillhuset.oddjob.Utils.SubCommand;
import it.unimi.dsi.fastutil.io.MeasurableInputStream;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class PlayerBlacklistCommand extends SubCommand {
    public PlayerBlacklistCommand() {
        subCommands.add(new PlayerBlacklistAddCommand());
        subCommands.add(new PlayerBlacklistRemoveCommand());
    }
    @Override
    public boolean denyConsole() {
        return false;
    }

    @Override
    public boolean denyOp() {
        return false;
    }

    @Override
    public Plugin getPlugin() {
        return Plugin.players;
    }

    @Override
    public String getName() {
        return "blacklist";
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getSyntax() {
        return null;
    }

    @Override
    public String getPermission() {
        return "players";
    }

    @Override
    public int minArgs() {
        return 0;
    }

    @Override
    public int maxArgs() {
        return 0;
    }

    @Override
    public int depth() {
        return 1;
    }

    @Override
    public boolean noGuild() {
        return false;
    }

    @Override
    public boolean needGuild() {
        return false;
    }

    @Override
    public Role guildRole() {
        return null;
    }

    @Override
    public void getCommandExecutor(CommandSender sender, String[] args) {
        OddPlayer oddPlayer;

        if (!argsLength(sender,args.length)) {return;}
        if(!can(sender,false,true)){return;}

        if (can(sender,true,true) && args.length == 2) {
            // player blacklist [target]
            oddPlayer = OddJob.getInstance().getPlayerManager().get(args[1]);
            if(oddPlayer == null) {
                MessageManager.errors_find_player(getPlugin(),args[1],sender);
                return;
            }
            MessageManager.player_blacklist(sender,oddPlayer);
        } else if (args.length == 1 && (sender instanceof Player player)){
            // player blacklist
            oddPlayer = OddJob.getInstance().getPlayerManager().get(player.getUniqueId());
            MessageManager.player_blacklist(sender,oddPlayer);
        }

        finder(sender,args);

    }

    @Override
    public List<String> getTabCompleter(CommandSender sender, String[] args) {
        return null;
    }
}
