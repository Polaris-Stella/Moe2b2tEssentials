package moe._2b2t.essentials.managers;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import moe._2b2t.essentials.Moe2b2tEssentials;
import moe._2b2t.essentials.utils.MoeI18n;

public class WorldBorderManager
{
    private static final WorldBorderManager instance = new WorldBorderManager();

    private WorldBorderManager()
    {
        super();
    }

    public static WorldBorderManager getInstance()
    {
        return instance;
    }

    private Plugin plugin;
    private boolean enabled;
    private boolean increaseWhenNoOne;
    private int maxSize;
    private int increaseRate;
    private int growth;
    private int finishInSeconds;
    private int taskId = -1;

    private final Runnable runnable = () ->
    {
        /*
           increaseWhenNoOne=true,有人   增加
           increaseWhenNoOne=true,没人   增加
           increaseWhenNoOne=false,有人  增加
           increaseWhenNoOne=false,没人  不增加
           由此可得两boolean值与增加与否的关系为或
        */
        if (enabled && (increaseWhenNoOne || (Bukkit.getOnlinePlayers().size() > 0)))
        {
            double newMaxSize = Bukkit.getWorld("world").getWorldBorder().getSize() + growth;
            if (newMaxSize > maxSize)
            {
                Moe2b2tEssentials.pluginMessage(MoeI18n.format("worldBorderReachedMaximumValue", maxSize));
                Bukkit.getWorld("world").getWorldBorder().setSize(maxSize);
                Bukkit.getWorld("world_nether").getWorldBorder().setSize(maxSize / 8D);
                return;
            }
            Bukkit.getWorld("world").getWorldBorder().setSize(newMaxSize, finishInSeconds);
            Bukkit.getWorld("world_nether").getWorldBorder().setSize(newMaxSize / 8D, finishInSeconds);
            Moe2b2tEssentials.pluginMessage(MoeI18n.format("worldBorderWillIncrease", (int) newMaxSize, finishInSeconds));
        }
    };

    public WorldBorderManager setPlugin(Plugin plugin)
    {
        this.plugin = plugin;
        return this;
    }

    public WorldBorderManager setEnabled(boolean enabled)
    {
        this.enabled = enabled;
        return this;
    }

    public WorldBorderManager setIncreaseWhenNoOne(boolean increaseWhenNoOne)
    {
        this.increaseWhenNoOne = increaseWhenNoOne;
        return this;
    }

    public WorldBorderManager setMaxSize(int maxSize)
    {
        this.maxSize = maxSize;
        return this;
    }

    public WorldBorderManager setGrowth(int growth)
    {
        this.growth = growth;
        return this;
    }

    public WorldBorderManager setIncreaseRate(int increaseRate)
    {
        this.increaseRate = increaseRate;
        return this;
    }

    public WorldBorderManager setFinishInSeconds(int finishInSeconds)
    {
        this.finishInSeconds = finishInSeconds;
        return this;
    }

    public void start(Plugin plugin, boolean enabled, boolean increaseWhenNoOne, int maxSize, int growth, int increaseRate, int finishInSeconds)
    {
        setPlugin(plugin);
        setEnabled(enabled);
        setIncreaseWhenNoOne(increaseWhenNoOne);
        setMaxSize(maxSize);
        setGrowth(growth);
        setIncreaseRate(increaseRate);
        setFinishInSeconds(finishInSeconds);
        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, runnable, increaseRate * 20L, increaseRate * 20L);
    }

    public void stop()
    {
        Bukkit.getScheduler().cancelTask(taskId);
    }
}
