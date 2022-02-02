package com.spillhuset.oddjob.Commands;

import com.spillhuset.oddjob.Enums.Plugin;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.Utils.SubCommandInterface;
import org.bukkit.Chunk;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GuildsCommand extends SubCommandInterface implements CommandExecutor, TabCompleter {
    public GuildsCommand() {
        subCommands.add(new GuildsBuyCommand());
        subCommands.add(new GuildsCreateCommand());
        subCommands.add(new GuildsInfoCommand());
        subCommands.add(new GuildsListCommand());
        subCommands.add(new GuildsClaimCommand());
        subCommands.add(new GuildsUnclaimCommand());
        subCommands.add(new GuildsSetCommand());
        subCommands.add(new GuildsHomesCommand());
        subCommands.add(new GuildsInviteCommand());
        subCommands.add(new GuildsJoinCommand());
        subCommands.add(new GuildsLeaveCommand());
        subCommands.add(new GuildsAcceptCommand());
        subCommands.add(new GuildsDenyCommand());
        subCommands.add(new GuildsDisbandCommand());
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
    public boolean canOthers() {
        return false;
    }

    @Override
    public Plugin getPlugin() {
        return Plugin.guilds;
    }

    @Override
    public String getPermission() {
        return "guilds";
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
        return 0;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        OddJob.getInstance().log("guilds");
        if (!can(sender, false, true)) {
            return true;
        }

        if (!argsLength(sender, args.length)) {
            return true;
        }

        if (sender instanceof Player player && args.length == depth()) {
            OddJob.getInstance().getGuildsManager().info(player);
            OddJob.getInstance().log("player: x:"+player.getLocation().getChunk().getX()+" z:"+player.getLocation().getChunk().getZ()+" w:"+player.getWorld().getName());
            for (Chunk chunk : OddJob.getInstance().getGuildsManager().getChunks().keySet()) {
                OddJob.getInstance().log("chunk: x:"+chunk.getX()+" z:"+chunk.getZ()+" w:"+chunk.getWorld().getName());
            }

            OddJob.getInstance().log("Standing on: "+OddJob.getInstance().getGuildsManager().getGuildByChunk(player.getLocation().getChunk()));
        }

        // guilds buy homes
        finder(sender, args);

        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        return tabs(sender, args);
    }
}
