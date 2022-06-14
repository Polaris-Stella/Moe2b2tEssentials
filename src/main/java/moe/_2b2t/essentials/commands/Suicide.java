package moe._2b2t.essentials.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import moe._2b2t.essentials.utils.MoeI18n;

public class Suicide implements CommandExecutor
{
    private static final Suicide instance = new Suicide();

    private Suicide()
    {
        super();
    }

    public static Suicide getInstance()
    {
        return instance;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args)
    {
        if ("suicide".equalsIgnoreCase(command.getName()))
        {
            if (sender instanceof Player)
            {
                sender.sendMessage(MoeI18n.format(sender, "suicideMsg"));
                ((Player) sender).setHealth(0);
            } else //控制台无法自杀
            {
                sender.sendMessage(MoeI18n.format(sender, "onlyPlayerCanExecute"));
            }
        }
        return true;
    }
}
