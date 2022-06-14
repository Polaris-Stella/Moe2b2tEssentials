package moe._2b2t.essentials.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import moe._2b2t.essentials.utils.MoeI18n;

public class Copy implements CommandExecutor
{
    private static final Copy instance = new Copy();

    private Copy()
    {
        super();
    }

    public static Copy getInstance()
    {
        return instance;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args)
    {
        if ("copy".equalsIgnoreCase(command.getName()))
        {
            //只有玩家才能刷物品，控制台不能
            if (sender instanceof Player)
            {
                //检查是否有权限
                if (sender.hasPermission("moe2b2t.copy"))
                {
                    Player player = (Player) sender;
                    PlayerInventory inventory = player.getInventory();
                    ItemStack mainHandItem = inventory.getItemInMainHand();
                    player.getInventory().addItem(mainHandItem);
                } else
                {
                    sender.sendMessage(MoeI18n.format(sender, "accessDenied"));
                }
            } else
            {
                sender.sendMessage(MoeI18n.format(sender, "onlyPlayerCanExecute"));
            }
        }
        return true;
    }
}
