package com.spillhuset.oddjob.Commands.Shops;

import com.spillhuset.oddjob.Enums.Plugin;
import com.spillhuset.oddjob.Enums.Role;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.Utils.SubCommand;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;

import java.util.List;

public class ShopsAddItemCommand extends SubCommand {
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
        return Plugin.shops;
    }

    @Override
    public String getName() {
        return "additem";
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
        return "shops";
    }

    @Override
    public int minArgs() {
        return 2;
    }

    @Override
    public int maxArgs() {
        return 2;
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
        if (!argsLength(sender, args.length)) {
            return;
        }
        if (!can(sender, false, true)) {
            return;
        }

        Material material = null;
        try {
            material = Material.valueOf(args[1]);
        } catch (IllegalArgumentException | NullPointerException ex) {
            ex.printStackTrace();
        }
        if (material == null) {
            sender.sendMessage("error");
            return;
        }
        OddJob.getInstance().getShopsManager().addItem(sender, material);
    }

    @Override
    public List<String> getTabCompleter(CommandSender sender, String[] args) {
        return null;
    }
}
