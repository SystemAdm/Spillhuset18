package com.spillhuset.oddjob.Commands;

import com.spillhuset.oddjob.Enums.Plugin;
import com.spillhuset.oddjob.Enums.Role;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.Utils.SubCommand;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class ArenaListCommand extends SubCommand {
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
        return Plugin.arena;
    }

    @Override
    public String getName() {
        return "list";
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
        return "arena";
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
        if (OddJob.getInstance().getArenaManager().arena.isEmpty()) {
            sender.sendMessage("No arena found");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (String name : OddJob.getInstance().getArenaManager().arena.keySet()) {
            sb.append(name).append(", ");
        }
        if (!sb.isEmpty()) sb.deleteCharAt(sb.lastIndexOf(","));
        sender.sendMessage(sb.toString());
    }

    @Override
    public List<String> getTabCompleter(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }
}
