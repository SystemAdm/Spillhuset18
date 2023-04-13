package com.spillhuset.oddjob.Commands.Shops;

import com.spillhuset.oddjob.Enums.Plugin;
import com.spillhuset.oddjob.Enums.Role;
import com.spillhuset.oddjob.Managers.MessageManager;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.Utils.Shop;
import com.spillhuset.oddjob.Utils.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class ShopsSetOpenCommand extends SubCommand {
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
        return "open";
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
        return "shops.admin";
    }

    @Override
    public int minArgs() {
        return 4;
    }

    @Override
    public int maxArgs() {
        return 4;
    }

    @Override
    public int depth() {
        return 2;
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

        Shop shop = OddJob.getInstance().getShopsManager().get(args[2]);
        if (shop == null) {
            return;
        }
        boolean result = args[3].equals("true");

        shop.setOpen(result);

        MessageManager.shops_set_open(sender, shop.getName(), result);
        Bukkit.dispatchCommand(sender, "shops info " + shop.getUuid());
    }

    @Override
    public List<String> getTabCompleter(CommandSender sender, String[] args) {
        List<String> list = new ArrayList<>();
        List<String> a = new ArrayList<>();
        a.add("true");
        a.add("false");
        if (args.length == 3) {
            for (Shop shop : OddJob.getInstance().getShopsManager().shops.values()) {
                if (args[2].isEmpty() || shop.getName().toLowerCase().startsWith(args[2].toLowerCase())) {
                    list.add(shop.getName());
                }
            }
        }
        if (args.length == 4) {
            for (String string : a) {
                if (args[3].isEmpty() || string.startsWith(args[3].toLowerCase())) {
                    list.add(string);
                }
            }
        }

        return list;
    }
}
