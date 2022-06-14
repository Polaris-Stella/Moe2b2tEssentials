package moe._2b2t.essentials.commands;

import moe._2b2t.essentials.listeners.AntiSpamListener;
import moe._2b2t.essentials.utils.MoeI18n;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Ignore implements CommandExecutor, TabCompleter
{
    private static final Ignore instance = new Ignore();

    private Ignore()
    {
        super();
    }

    public static Ignore getInstance()
    {
        return instance;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args)
    {
        if ("ignore".equalsIgnoreCase(command.getName()))
        {
            //只允许传入1个参数，即玩家名
            if (args.length == 1)
            {
                Player senderPlayer;
                if (!(sender instanceof Player))
                {
                    sender.sendMessage(MoeI18n.format(sender, "onlyPlayerCanExecute"));
                    return true;
                }
                senderPlayer = (Player) sender;
                Player ignoredPlayer = Bukkit.getPlayer(args[0]);
                if (ignoredPlayer == null)
                {
                    sender.sendMessage(MoeI18n.format("ignoredOfflinePlayer"));
                    return true;
                } else if (ignoredPlayer.equals(senderPlayer))
                {
                    sender.sendMessage(MoeI18n.format("ignoredOneself"));
                    return true;
                }
                AntiSpamListener.getInstance().addIgnoredPlayer(senderPlayer, ignoredPlayer);
                sender.sendMessage(MoeI18n.format(sender, "successfullyIgnored", ignoredPlayer.getName()));
            } else //否则向玩家发送指令使用方法
            {
                sender.sendMessage(MoeI18n.format(sender, "ignoreHelp"));
                return true;
            }
        }
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args)
    {
        List<String> list = new ArrayList<>();
        if (args.length == 1)
        {
            list.addAll(
                    Bukkit.getOnlinePlayers().stream()
                            .map(HumanEntity::getName)
                            .filter(name -> StringUtil.startsWithIgnoreCase(name, args[0].toLowerCase()))
                            .collect(Collectors.toList())
            );
        }
        return list;
    }
}
