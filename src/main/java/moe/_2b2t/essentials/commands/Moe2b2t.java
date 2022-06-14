package moe._2b2t.essentials.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import moe._2b2t.essentials.Moe2b2tEssentials;
import moe._2b2t.essentials.managers.ChunkUnloader;
import moe._2b2t.essentials.utils.MoeI18n;

import java.util.ArrayList;
import java.util.List;

public class Moe2b2t implements CommandExecutor, TabCompleter
{
    private static final Moe2b2t instance = new Moe2b2t();

    private Moe2b2t()
    {
        super();
    }

    public static Moe2b2t getInstance()
    {
        return instance;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args)
    {
        if ("moe2b2t".equalsIgnoreCase(command.getName()))
        {
            //检测执行者是否有权限
            if (!sender.hasPermission("moe2b2t.admin"))
            {
                sender.sendMessage(MoeI18n.format(sender, "accessDenied"));
                return true;
            }
            //检查是否有参数
            if (args.length <= 0)
            {
                sender.sendMessage(MoeI18n.format(sender, "moe2b2tCmdUsage"));
                return true;
            }
            Moe2b2tEssentials moe2b2tEssentials = Moe2b2tEssentials.getInstance();
            switch (args[0])
            {
                case "reload":
                    if (args.length <= 1) //重载全部配置
                    {
                        moe2b2tEssentials.reloadI18nCfg();
                        moe2b2tEssentials.reloadConfig();
                        sender.sendMessage(MoeI18n.format(sender, "reloadedAllCfg"));
                        return true;
                    } else if (args.length == 2)
                    {
                        switch (args[1])
                        {
                            case "i18n":
                                //只重载本地化配置
                                moe2b2tEssentials.reloadI18nCfg();
                                sender.sendMessage(MoeI18n.format(sender, "reloadedI18nCfg"));
                                return true;
                            case "config":
                                //只重载主配置
                                moe2b2tEssentials.reloadConfig();
                                sender.sendMessage(MoeI18n.format(sender, "reloadedMainCfg"));
                                return true;
                            default:
                                sender.sendMessage(MoeI18n.format(sender, "moe2b2tCmdUsage"));
                                return true;
                        }
                    }
                    break;
                case "unloadchunk":
                    ChunkUnloader.getInstance().unloadNow();
                    sender.sendMessage(MoeI18n.format(sender, "chunkUnloadedManually"));
                    break;
                default:
                    sender.sendMessage(MoeI18n.format(sender, "moe2b2tCmdUsage"));
                    return true;
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args)
    {
        List<String> list = new ArrayList<>();
        switch (args.length)
        {
            case 1:
                list.add("reload");
                list.add("unloadchunk");
            case 2:
                list.add("config");
                list.add("i18n");
        }
        return list;
    }
}
