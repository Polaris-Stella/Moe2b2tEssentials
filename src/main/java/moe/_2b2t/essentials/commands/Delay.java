package moe._2b2t.essentials.commands;

import moe._2b2t.essentials.utils.Tools;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import moe._2b2t.essentials.managers.AutoRestarter;
import moe._2b2t.essentials.utils.MoeI18n;

public class Delay implements CommandExecutor
{
    private static final Delay instance = new Delay();

    private boolean hasDelayedBefore = false;

    private Delay()
    {
        super();
    }

    public static Delay getInstance()
    {
        return instance;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args)
    {
        if ("delay".equals(command.getName()))
        {
            //被玩家推迟
            if (sender instanceof Player)
            {
                if (!hasDelayedBefore)
                {
                    hasDelayedBefore = true;
                    AutoRestarter.delay(600);
                    Tools.broadcastI18nMsg("delayedByPlayer", ((Player) sender).getName());
                } else
                {
                    sender.sendMessage(MoeI18n.format(sender, "delayRejected"));
                }
            } else//被控制台推迟
            {
                AutoRestarter.delay(3600);
                Tools.broadcastI18nMsg("delayedByConsole");
            }
        }
        return true;
    }
}
