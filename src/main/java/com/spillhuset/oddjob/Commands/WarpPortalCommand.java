package com.spillhuset.oddjob.Commands;
import com.spillhuset.oddjob.Enums.Plugin;
import com.spillhuset.oddjob.Enums.Role;
import com.spillhuset.oddjob.Managers.WarpManager;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.Utils.SubCommand;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class WarpPortalCommand extends SubCommand {
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
        return Plugin.warps;
    }

    @Override
    public String getName() {
        return "portal";
    }

    @Override
    public String getDescription() {
        return "null";
    }

    @Override
    public String getSyntax() {
        return "null";
    }

    @Override
    public String getPermission() {
        return "warps.portal";
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
        if (!can(sender,false,true)) {
            return;
        }
        if (!argsLength(sender,args.length)) {
            return;
        }

        if (sender.isOp() && sender instanceof Player player) {
            WarpManager.name(args[1]);
            if (WarpManager.edit.contains(player.getUniqueId())) {
                WarpManager.removeTool(player);
                OddJob.getInstance().log("Removing");
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.RED + "Portal Edit Mode ended!"));
            } else {
                WarpManager.edit.add(player.getUniqueId());
                OddJob.getInstance().log("Adding "+WarpManager.tool().getType());
                player.getInventory().setItem(player.getInventory().firstEmpty(),WarpManager.tool());
                player.updateInventory();
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GOLD + "Portal Edit Mode!"));
            }
        }
    }

    @Override
    public List<String> getTabCompleter(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }
}
