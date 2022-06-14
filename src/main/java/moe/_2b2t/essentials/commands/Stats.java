package moe._2b2t.essentials.commands;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import moe._2b2t.essentials.Moe2b2tEssentials;
import moe._2b2t.essentials.utils.MoeI18n;

public class Stats implements CommandExecutor
{
    private static final Stats instance = new Stats();

    private Stats()
    {
        super();
    }

    public static Stats getInstance()
    {
        return instance;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args)
    {
        if ("stats".equalsIgnoreCase(command.getName()))
        {
            //处理文本中的papi表达式
            String originalMsg = MoeI18n.format(sender, "statsMsg");
            Player player = sender instanceof Player ? (Player) sender : null;
            String papiSettedMsg = null;
            try
            {
                papiSettedMsg = Moe2b2tEssentials.isPapiExists() ? PlaceholderAPI.setPlaceholders(player, originalMsg) : originalMsg;
            } catch (NullPointerException e)
            {
                sender.sendMessage(MoeI18n.format("consoleRequestedPlayerOnlyPapi"));
            }
            //由于用户输入百分号时需要输入两个以避免干扰表达式解析
            //所以现在将两个百分号替换为一个
            assert papiSettedMsg != null;
            papiSettedMsg = papiSettedMsg.replace("%%", "%");
            sender.sendMessage(papiSettedMsg);
            return true;
        }
        return true;
    }
}
