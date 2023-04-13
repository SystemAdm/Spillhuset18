package com.spillhuset.oddjob.Commands.Shops;

import com.spillhuset.oddjob.Enums.Plugin;
import com.spillhuset.oddjob.Enums.Price;
import com.spillhuset.oddjob.Enums.Role;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.Utils.Profession;
import com.spillhuset.oddjob.Utils.SubCommand;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class ShopsSetItemCommand extends SubCommand {
    ArrayList<String> strings = new ArrayList<>();

    public ShopsSetItemCommand() {

        strings.add("maximum");
        strings.add("normal");
        strings.add("minimum");
        strings.add("profession");
        strings.add("limit");
        strings.add("enabled");
        strings.add("buyable");
        strings.add("Sellable");
        strings.add("reset");
        strings.add("singel");
        strings.add("stack");
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
        return Plugin.shops;
    }

    @Override
    public String getName() {
        return "setitem";
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
        if (!can(sender, false, true)) {
            return;
        }
        if (!argsLength(sender, args.length)) {
            return;
        }
        Price priceTag = null;
        for (Price plu : OddJob.getInstance().getShopsManager().priceList.values()) {
            if (args[1].equalsIgnoreCase(plu.getMaterial().name())) {
                priceTag = plu;
            }
        }
        if (priceTag == null) {
            sender.sendMessage("error price");
            return;
        }

        String command = null;
        for (String string : this.strings) {
            if (args[2].equalsIgnoreCase(string)) {
                command = string;
            }
        }
        if (command == null) {
            sender.sendMessage("error string");
            return;
        }

        switch (command) {
            case "maximum" -> {
                priceTag.setMaximum(Double.parseDouble(args[3]));
            }
            case "normal" -> {
                priceTag.setNormal(Double.parseDouble(args[3]));
            }
            case "minimum" -> {
                priceTag.setMinimum(Double.parseDouble(args[3]));
            }
            case "profession" -> {
                priceTag.setProfession(Profession.valueOf(args[3]));
            }
            case "limit" -> {
                priceTag.setLimit(Integer.parseInt(args[3]));
            }
            case "enabled" -> {
                priceTag.setEnabled(Boolean.parseBoolean(args[3]));
            }
            case "buyable" -> {
                priceTag.setBuyAble(Boolean.parseBoolean(args[3]));
            }
            case "sellable" -> {
                priceTag.setSellAble(Boolean.parseBoolean(args[3]));
            }
            case "reset" -> {
                priceTag.setResetValue(Double.parseDouble(args[3]));
            }
            case "singel" -> {
                priceTag.setSingel(Boolean.parseBoolean(args[3]));
            }
            case "stack" -> {
                priceTag.setStack(Integer.parseInt(args[3]));
            }
            default -> {
                sender.sendMessage("error command");
            }
        }
        OddJob.getInstance().getShopsManager().update();
    }

    @Override
    public List<String> getTabCompleter(CommandSender sender, String[] args) {
        List<String> list = new ArrayList<>();
        // shops setItem <item> <command> <value>
        if (args.length == 2) {
            for (Price plu : OddJob.getInstance().getShopsManager().priceList.values()) {
                if (args[1].isEmpty() || plu.getMaterial().name().toLowerCase().startsWith(args[1].toLowerCase())) {
                    list.add(plu.getMaterial().name());
                }
            }
        }
        if (args.length == 3) {
            for (String string : strings) {
                if (args[2].isEmpty() || string.toLowerCase().startsWith(args[2].toLowerCase())) {
                    list.add(string);
                }
            }
        }
        if (args.length == 4) {

                switch (args[2]) {
                    case "maximum", "normal", "minimum", "reset", "limit", "stack" -> {
                        list.add("Number");
                    }
                    case "profession" -> {
                        for (Profession profession : Profession.values()) {
                            if (args[3].isEmpty() || profession.name().toLowerCase().startsWith(args[3].toLowerCase())) {
                                list.add(profession.name());
                            }
                        }
                    }
                    case "enabled", "buyable", "sellable", "singel" -> {
                        List<String> temp = new ArrayList<>();
                        temp.add("true");
                        temp.add("false");
                        for (String string : temp) {
                            if (args[3].isEmpty() || string.startsWith(args[3].toLowerCase())) {
                                list.add(string);
                            }
                        }
                    }
                    default -> {
                        sender.sendMessage("error command");
                    }

            }
        }
        return list;
    }
}
