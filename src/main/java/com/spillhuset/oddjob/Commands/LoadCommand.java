package com.spillhuset.oddjob.Commands;

import com.spillhuset.oddjob.OddJob;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class LoadCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        OddJob.getInstance().getGuildsManager().load();
        return true;
    }
}
