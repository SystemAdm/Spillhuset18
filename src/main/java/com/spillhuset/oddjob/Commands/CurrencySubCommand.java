package com.spillhuset.oddjob.Commands;
import com.spillhuset.oddjob.Enums.Plugin;
import com.spillhuset.oddjob.Utils.SubCommand;
import org.bukkit.command.CommandSender;

import java.util.List;

public class CurrencySubCommand extends SubCommand {
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
        return null;
    }

    @Override
    public String getName() {
        return null;
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
        return null;
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
    public void getCommandExecutor(CommandSender sender, String[] args) {

    }

    @Override
    public List<String> getTabCompleter(CommandSender sender, String[] args) {
        return null;
    }
}
