package moe._2b2t.essentials.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import moe._2b2t.essentials.managers.ProfileManager;
import moe._2b2t.essentials.utils.MoeI18n;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class Toggle implements CommandExecutor, TabCompleter
{
    private static final Toggle instance = new Toggle();

    private Toggle()
    {
        super();
    }

    public static Toggle getInstance()
    {
        return instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if ("toggle".equalsIgnoreCase(command.getName()))
        {
            try
            {
                Player player = getPlayerToAccess(sender, args);
                //若修改的其他玩家的信息，并且玩家没有权限，则禁止更改
                if (!player.equals(sender) && !sender.hasPermission("moe2b2t.toggle.changeothers"))
                {
                    MoeI18n.format(sender, "toggleAccessDenied");
                    return true;
                }
                //判断是需要获取状态还是设置状态
                switch (args[0])
                {
                    case "get":
                        sender.sendMessage(MoeI18n.format(sender, "optionValue", player.getName(), args[1], ProfileManager.get(player, args[1])));
                        break;
                    case "set":
                        if (!isOperationLegitimate(args[1], args[2]))
                        {
                            sender.sendMessage(MoeI18n.format(sender, "toggleCmdUsage"));
                            return true;
                        }
                        ProfileManager.set(player, args[1], args[2]);
                        sender.sendMessage(MoeI18n.format(sender, "optionSaved"));
                        break;
                    default:
                        throw new IllegalArgumentException();
                }
            } catch (ArrayIndexOutOfBoundsException | IllegalArgumentException e)
            {
                sender.sendMessage(MoeI18n.format(sender, "toggleCmdUsage"));
            } catch (ClassCastException e) //getPlayerToAccess方法里抛出的，抛出此异常证明是控制台执行的命令并且没有指定玩家
            {
                sender.sendMessage(MoeI18n.format(sender, "needToSpecifyPlayer"));
            } catch (NullPointerException e)
            {
                sender.sendMessage(MoeI18n.format(sender, "playerNotFound"));
            }
        }
        return true;
    }

    //检测操作是否合法
    private static boolean isOperationLegitimate(String key, String value)
    {
        switch (key)
        {
            case "lang":
                return MoeI18n.hasLanguage(value);
            case "showJoinAndLeaveAlerts":
            case "showDeathMessages":
            case "showBroadcastMessages":
                return value.equals("true") || value.equals("false");
            default:
                return false;
        }
    }

    private static Player getPlayerToAccess(CommandSender sender, String[] args) throws IllegalArgumentException,ClassCastException
    {
        //判断玩家名称在数组的哪个位置
        int playerNameIndex;
        switch (args[0])
        {
            case "get":
                playerNameIndex = 2;
                break;
            case "set":
                playerNameIndex = 3;
                break;
            default:
                throw new IllegalArgumentException();
        }

        //若没指定，则证明是想要设置自己的信息
        if (args.length < (playerNameIndex + 1))
        {
            return (Player) sender;//如果是控制台执行的此命令并且没有指定玩家名称，则此语句会抛出ClassCastException,将在之后捕获
        }
        Player player = Bukkit.getPlayer(args[playerNameIndex]);
        //按名字找不到就按UUID找
        if (player == null)
        {
            try
            {
                player = Bukkit.getPlayer(UUID.fromString(args[playerNameIndex]));
            } catch (IllegalArgumentException e) //抛出此异常证明输入的不是合法的UUID，抛个NPE证明玩家没找到
            {
                throw new NullPointerException();
            }
            if (player == null) //按UUID还找不到证明的确没有这个玩家
            {
                throw new NullPointerException();
            }
        }
        return player;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args)
    {
        List<String> list = new ArrayList<>();
        switch (args.length)
        {
            case 1:
                list.add("set");
                list.add("get");
                break;
            case 2:
                list.add("lang");
                list.add("showJoinAndLeaveAlerts");
                list.add("showDeathMessages");
                list.add("showBroadcastMessages");
                break;
            case 3:
                switch (args[0])
                {
                    case "set":
                        switch (args[1])
                        {
                            case "lang":
                                list.addAll(MoeI18n.getAvailableLangs());
                                break;
                            case "showJoinAndLeaveAlerts":
                            case "showDeathMessages":
                                list.add("true");
                                list.add("false");
                                break;
                        }
                        break;
                    case "get":
                        list.addAll(getPlayerNames(args[2]));
                        break;
                }
        }
        return list;
    }

    private static List<String> getPlayerNames(String inputed)
    {
        return Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName)
                .filter(
                        name -> StringUtil.startsWithIgnoreCase(name, inputed.toLowerCase()))
                .collect(Collectors.toList());
    }
}
