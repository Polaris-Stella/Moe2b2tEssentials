package moe._2b2t.essentials.commands;

import me.clip.placeholderapi.PlaceholderAPI;
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
import moe._2b2t.essentials.Moe2b2tEssentials;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Stat implements CommandExecutor, TabCompleter
{
    private static final Stat instance = new Stat();

    private Stat()
    {
        super();
    }

    public static Stat getInstance()
    {
        return instance;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args)
    {
        if ("stat".equalsIgnoreCase(command.getName()))
        {
            Player player = null;
            switch (args.length)
            {
                case 0: //没有提供参数
                    if (sender instanceof Player) //若没有提供参数，并且是玩家执行的，则表示需要查ta本人的信息
                    {
                        player = (Player) sender;
                        break;
                    } else //若没有提供参数，并且是控制台执行的，则提示用户需要指定玩家
                    {
                        sender.sendMessage(MoeI18n.format(sender, "needToSpecifyPlayer"));
                        return true;
                    }
                case 1: //提供了一个参数
                    player = Bukkit.getPlayer(args[0]);
                    if (player == null)
                    {
                        sender.sendMessage(MoeI18n.format(sender, "playerNotFound"));
                        return true;
                    }
                    break;

            }
            //检测查的信息是否是自己的，若是则放行
            //若查的别人的，则判断是否有moe2b2t.stat.viewothers权限节点，若有则放行，若没有则提示用户没有权限并且return告辞
            if (!sender.equals(player) && !sender.hasPermission("moe2b2t.stat.viewothers"))
            {
                sender.sendMessage(MoeI18n.format(sender, "statAccessDenied"));
                return true;
            }
            //处理PAPI表达式
            String originalMsg = MoeI18n.format(sender, "statMsg");
            String papiSetMsg = null;
            try
            {
                papiSetMsg = Moe2b2tEssentials.isPapiExists() ? PlaceholderAPI.setPlaceholders(player, originalMsg) : originalMsg;
            } catch (NullPointerException e)
            {
                sender.sendMessage(MoeI18n.format("consoleRequestedPlayerOnlyPapi"));
                e.printStackTrace();
            }
            //由于用户输入百分号时需要输入两个以避免干扰表达式解析
            //所以现在将两个百分号替换为一个
            assert papiSetMsg != null;
            papiSetMsg = papiSetMsg.replace("%%", "%");
            sender.sendMessage(papiSetMsg);
            return true;
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
