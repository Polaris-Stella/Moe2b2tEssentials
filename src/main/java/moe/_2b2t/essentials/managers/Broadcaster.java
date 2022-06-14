package moe._2b2t.essentials.managers;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import moe._2b2t.essentials.Moe2b2tEssentials;
import moe._2b2t.essentials.utils.MoeI18n;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Broadcaster
{
    private static final Map<String, List<String>> langMsgsMap = new HashMap<>();
    private static int index = 0;
    private static int msgsMaxIndex = 0;
    private static int taskId = -1;
    private static final Runnable runnable = () ->
    {
        //先给控制台发
        Moe2b2tEssentials.getInstance().getLogger().info(MoeI18n.format("broadcastPrefix") + langMsgsMap.get(MoeI18n.getConsoleLang()).get(index));
        //再给玩家发，并且过滤掉设置不显示自动广播消息的玩家
        Bukkit.getOnlinePlayers().stream()
                .filter(player -> Boolean.parseBoolean(ProfileManager.get(player, "showBroadcastMessages")))
                .forEach(player ->
                {
                    String playerLang = ProfileManager.get(player, "lang");
                    List<String> list = langMsgsMap.get(playerLang);
                    player.sendMessage(MoeI18n.format(playerLang, "broadcastPrefix") + list.get(index));

                });
        index++;
        if (index > msgsMaxIndex)
        {
            index = 0;
        }
    };

    public static void stop()
    {
        index = 0;
        Bukkit.getScheduler().cancelTask(taskId);
    }

    public static void init(Plugin plugin, long period)
    {
        List<String> msgsList;
        for (String lang :
                MoeI18n.getAvailableLangs())
        {
            msgsList = new ArrayList<>();
            msgsList.addAll(MoeI18n.getBroadcastMsgsInSpecifiedLang(lang));
            langMsgsMap.put(lang, msgsList);
        }
        index = 0;
        msgsMaxIndex = MoeI18n.getBroadcastMsgsInSpecifiedLang("en_us").size() - 1;
        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, runnable, 20L, period * 20L);
    }
}
