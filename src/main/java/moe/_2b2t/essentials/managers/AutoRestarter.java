package moe._2b2t.essentials.managers;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import moe._2b2t.essentials.utils.MoeI18n;

public class AutoRestarter
{
    private static Plugin plugin;
    private static boolean enabled;
    private static int period;
    private static int taskID = -1;
    private static final Runnable runnable = () ->
    {
        switch (period)
        {
            //在这几个特定的时间段提醒玩家
            case 30:
            case 15:
            case 10:
            case 5:
            case 4:
            case 3:
            case 2:
            case 1:
                plugin.getLogger().info(MoeI18n.format("serverRestart", period));
                Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(MoeI18n.format(player, "serverRestart", period)));
        }
        if (period <= 0)
        {
            Bukkit.shutdown();
        }
        period--;
    };

    public static void delay(int second)
    {
        period += second;
    }

    public static void init(Plugin plugin, boolean enabled, int period)
    {
        AutoRestarter.plugin = plugin;
        AutoRestarter.enabled = enabled;
        AutoRestarter.period = period;
        cancel();
        taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, runnable, 0, 20L);
    }

    public static long getNextRestartMillis()
    {
        return System.currentTimeMillis() + (period * 1000L);
    }

    public static void cancel()
    {
        try
        {
            Bukkit.getScheduler().cancelTask(taskID);
        } catch (IllegalStateException e)
        {
            e.printStackTrace();
        }
    }
}