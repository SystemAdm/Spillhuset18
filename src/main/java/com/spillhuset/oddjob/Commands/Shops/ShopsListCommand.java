package com.spillhuset.oddjob.Commands.Shops;

import com.spillhuset.oddjob.Enums.Plugin;
import com.spillhuset.oddjob.Enums.Role;
import com.spillhuset.oddjob.Managers.MessageManager;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.Utils.Shop;
import com.spillhuset.oddjob.Utils.SubCommand;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class ShopsListCommand extends SubCommand {
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
        return "list";
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
        return 1;
    }

    @Override
    public int maxArgs() {
        return 1;
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
        if(!argsLength(sender,args.length)) {
            return;
        }
        if (!can(sender,false,true)) {
            return;
        }
        List<TextComponent> list = new ArrayList<>();
        for (Shop shop:OddJob.getInstance().getShopsManager().shops.values()){
            TextComponent message = new TextComponent(shop.getName());
            message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/shops info "+shop.getUuid().toString()));
            list.add(message);
        }
        MessageManager.shops_list(sender,list);
    }

    @Override
    public List<String> getTabCompleter(CommandSender sender, String[] args) {
        return null;
    }
}
