package moe._2b2t.essentials.managers;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

public class CmdCooldownManager
{
    private static final Map<Player, CmdCooldownManager> instances = new HashMap<>();

    private static Plugin plugin;

    public static CmdCooldownManager getInstance(Player player)
    {
        CmdCooldownManager manager = instances.get(player);
        if (manager == null)
        {
            manager = new CmdCooldownManager();
            instances.put(player, manager);
        }
        return manager;
    }

    private final Map<Command, Integer> cmdCoolTimeMap = new HashMap<>();
    private final Map<Command, Integer> cmdTaskIdMap = new HashMap<>();

    private CmdCooldownManager()
    {
        super();
    }

    public static void setPlugin(Plugin plugin)
    {
        CmdCooldownManager.plugin = plugin;
    }

    public void setCmdCooldown(Command cmd, int time)
    {
        if (time <= 0)
        {
            return;
        }
        cmdCoolTimeMap.putIfAbsent(cmd, time);
        if (!cmdTaskIdMap.containsKey(cmd))
        {
            cmdTaskIdMap.put(cmd, Bukkit.getScheduler().runTaskTimer(plugin, () ->
            {
                int timeLeft = cmdCoolTimeMap.get(cmd) - 1;
                cmdCoolTimeMap.put(cmd, timeLeft);
                if (timeLeft <= 0)
                {

                    Bukkit.getScheduler().cancelTask(cmdTaskIdMap.get(cmd));
                    cmdCoolTimeMap.remove(cmd);
                    cmdTaskIdMap.remove(cmd);
                }
            }, 20L, 20L).getTaskId());
        }//一秒执行一次，将剩余时间值-1
    }

    public int getTimeLeft(Command cmd)
    {
        Integer timeLeft = cmdCoolTimeMap.get(cmd);
        return timeLeft == null ? 0 : timeLeft;
    }
}
