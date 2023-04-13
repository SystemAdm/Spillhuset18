package com.spillhuset.oddjob.Commands.Shops;

import com.spillhuset.oddjob.Enums.Plugin;
import com.spillhuset.oddjob.Enums.Role;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.Utils.Shop;
import com.spillhuset.oddjob.Utils.SubCommand;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class ShopsInfoCommand extends SubCommand {
    @Override
    public boolean denyConsole() {
        return true;
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
        return "info";
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public String getSyntax() {
        return "";
    }

    @Override
    public String getPermission() {
        return "shops.admin";
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

        Shop shop = OddJob.getInstance().getShopsManager().get(args[1]);
        if (shop == null) {
            OddJob.getInstance().log("shop not found");
            return;
        }

        TextComponent message = new TextComponent();
        TextComponent extra = new TextComponent();

        message.setColor(ChatColor.YELLOW);
        message.setText("Info about " + shop.getName());
        sender.spigot().sendMessage(message);

        message = new TextComponent();
        message.setColor(ChatColor.BLUE);
        message.setText("UUID: ");
        extra.setColor(ChatColor.GOLD);
        extra.setText(shop.getUuid().toString());
        message.addExtra(extra);
        sender.spigot().sendMessage(message);

        message = new TextComponent();
        extra = new TextComponent();
        message.setColor(ChatColor.BLUE);
        message.setText("Name: ");
        extra.setColor(ChatColor.GOLD);
        extra.setText(shop.getName());
        message.addExtra(extra);
        sender.spigot().sendMessage(message);

        message = new TextComponent();
        extra = new TextComponent();
        message.setColor(ChatColor.BLUE);
        message.setText("Open: ");
        if (shop.isOpen()) {
            extra.setColor(ChatColor.GREEN);
            extra.setText("true");
            extra.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to close").create()));
            extra.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/shops set open " + shop.getUuid().toString() + " false"));
        } else {
            extra.setColor(ChatColor.RED);
            extra.setText("false");
            extra.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click to open")));
            extra.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/shops set open " + shop.getUuid().toString() + " true"));
        }
        message.addExtra(extra);
        sender.spigot().sendMessage(message);

        StringBuilder stringBuilder = new StringBuilder();
        for (String string : shop.listProfessions()) {
            stringBuilder.append(string).append(",");
        }
        message = new TextComponent();
        extra = new TextComponent();
        message.setColor(ChatColor.BLUE);
        message.setText("Strict to profession: ");

        if (stringBuilder.isEmpty()) {
            extra.setText("none");
            extra.setColor(ChatColor.GOLD);
        } else {
            extra.setText(stringBuilder.substring(0, stringBuilder.lastIndexOf(",")));
            extra.setColor(ChatColor.GOLD);
        }
        message.addExtra(extra);
        sender.spigot().sendMessage(message);
    }

    @Override
    public List<String> getTabCompleter(CommandSender sender, String[] args) {
        List<String> list = new ArrayList<>();
        for (Shop shop : OddJob.getInstance().getShopsManager().shops.values()) {
            if (args[1].isEmpty() || shop.getName().startsWith(args[1]))
                list.add(shop.getUuid().toString() + " " + shop.getName());
        }
        return list;
    }
}
