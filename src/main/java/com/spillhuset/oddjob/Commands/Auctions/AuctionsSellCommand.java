package com.spillhuset.oddjob.Commands.Auctions;

import com.spillhuset.oddjob.Enums.Plugin;
import com.spillhuset.oddjob.Enums.Role;
import com.spillhuset.oddjob.Enums.Zone;
import com.spillhuset.oddjob.Managers.MessageManager;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.Utils.Guild;
import com.spillhuset.oddjob.Utils.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class AuctionsSellCommand extends SubCommand {
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
        return Plugin.auctions;
    }

    @Override
    public String getName() {
        return "sell";
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getSyntax() {
        return "/auctions sell <bid> <buyout> <timeout>";
    }

    @Override
    public String getPermission() {
        return null;
    }

    @Override
    public int minArgs() {
        return 2;
    }

    @Override
    public int maxArgs() {
        return 4;
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
        double bid = Double.parseDouble(args[1]);
        double buyout = 0.0d;
        int timeout = 48;
        Player seller = (Player) sender;
        ItemStack itemStack = seller.getInventory().getItemInMainHand();
        Guild guild =OddJob.getInstance().getGuildsManager().getGuildByCords(seller.getLocation().getChunk());
        if (guild== null || guild.getZone() != Zone.AUCTION) {
            MessageManager.auctions_not_area(seller);
            return;
        }

        if (!can(sender, false, true)) {
            return;
        }
        if (!argsLength(sender, args.length)) {
            return;
        }
        if (args.length > 2) buyout = Double.parseDouble(args[2]);
        if (args.length == 4) timeout = Integer.parseInt(args[3]);

        OddJob.getInstance().getAuctionsManager().sell(seller,itemStack,bid,buyout,timeout);
        // auction sell <bid> <buyout> <timeout>
        //           0    1       2        3
    }

    @Override
    public List<String> getTabCompleter(CommandSender sender, String[] args) {
        return null;
    }
}
