package com.spillhuset.oddjob.Commands;

import com.spillhuset.oddjob.Enums.Plugin;
import com.spillhuset.oddjob.Managers.ConfigManager;
import com.spillhuset.oddjob.Managers.MessageManager;
import com.spillhuset.oddjob.OddJob;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class LoadedCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if (args.length == 2) {
            Plugin plugin = Plugin.valueOf(args[0]);
            String enabled = args[1];

            if (plugin.name() == null) {
                OddJob.getInstance().log("e");
                return true;
            }
            OddJob.getInstance().log(plugin.name());
            if (enabled.equalsIgnoreCase("enable")) {
                ConfigManager.setConfig("plugin."+plugin.name(),true);
                OddJob.getInstance().log("enabled");
            } else {
                ConfigManager.setConfig("plugin."+plugin.name(),false);
                OddJob.getInstance().log("disabled");
            }
            ConfigManager.save();
            OddJob.getInstance().log("saved");
            //OddJob.getInstance().reloadConfig();
            ConfigManager.reload();
        }

        for (Plugin plugin : Plugin.values()) {
            MessageManager.plugin(sender, plugin.getName(), (ConfigManager.getBoolean("plugin." + plugin.getName())) ? t() : f());
        }

        return true;
    }

    public String t() {
        return ChatColor.GREEN + "Enabled";
    }

    public String f() {
        return ChatColor.RED + "Disabled";
    }
}
