package moe._2b2t.essentials.commands;

import moe._2b2t.essentials.utils.MoeI18n;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Fly implements CommandExecutor
{
    private static final Fly instance = new Fly();

    private Fly()
    {
        super();
    }

    public static Fly getInstance()
    {
        return instance;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args)
    {
        if ("fly".equals(command.getName()))
        {
            if (sender instanceof Player)
            {
                Player player = (Player) sender;
                if (player.getAllowFlight())
                {
                    player.setAllowFlight(false);
                    player.sendMessage(MoeI18n.format(player, "flyDisabled"));
                } else
                {
                    player.setAllowFlight(true);
                    player.setFlySpeed(2.0f);
                    player.sendMessage(MoeI18n.format(player, "flyEnabled"));
                }
            } else
            {
                sender.sendMessage(MoeI18n.format(sender, "onlyPlayerCanExecute"));
            }
        }
        return true;
    }
}
