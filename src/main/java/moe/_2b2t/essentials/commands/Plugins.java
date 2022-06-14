package moe._2b2t.essentials.commands;

import moe._2b2t.essentials.utils.MoeI18n;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class Plugins implements CommandExecutor
{
    private static final Plugins instance = new Plugins();

    private Plugins()
    {
        super();
    }

    public static Plugins getInstance()
    {
        return instance;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args)
    {
        if ("plugins".equals(command.getName()))
            sender.sendMessage(MoeI18n.format(sender, "pluginsMessage", Arrays.toString(Bukkit.getPluginManager().getPlugins())));
        return true;
    }
}
