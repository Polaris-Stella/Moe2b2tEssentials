package moe._2b2t.essentials.managers;

import moe._2b2t.essentials.utils.IntHolder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import moe._2b2t.essentials.Moe2b2tEssentials;
import moe._2b2t.essentials.utils.MoeI18n;

import java.util.Arrays;

public class ChunkUnloader
{
    private static final ChunkUnloader instance = new ChunkUnloader();

    private ChunkUnloader()
    {
        super();
    }

    public static ChunkUnloader getInstance()
    {
        return instance;
    }

    private final Moe2b2tEssentials moe2b2tEssentials = Moe2b2tEssentials.getInstance();
    private boolean enabled;
    private int period;
    private final IntHolder chunkNum = new IntHolder();
    private final Runnable runnable = () ->
    {
        if (!enabled)
        {
            return;
        }
        chunkNum.value = 0;
        Bukkit.getWorlds().forEach(world -> Arrays.stream(world.getLoadedChunks()).forEach(chunk ->
        {
            if (Arrays.stream(chunk.getEntities()).filter(entity -> entity instanceof Player).count() <= 0L)
            {
                chunk.unload(true);
                chunkNum.value++;
            }
        }));
        Moe2b2tEssentials.pluginMessage(MoeI18n.format("chunkUnloaded", chunkNum.value));
    };
    private int taskId = -1;

    public ChunkUnloader setEnabled(boolean enabled)
    {
        this.enabled = enabled;
        return this;
    }

    public ChunkUnloader setPeriod(int period)
    {
        this.period = period;
        return this;
    }

    public int unloadNow()
    {
        Bukkit.getScheduler().runTask(moe2b2tEssentials, runnable);
        return chunkNum.value;
    }

    public void start()
    {
        stop();
        if (enabled)
        {
            taskId = Bukkit.getScheduler().runTaskTimer(moe2b2tEssentials, runnable, period * 20L, period * 20L).getTaskId();
        }
    }

    public void stop()
    {
        Bukkit.getScheduler().cancelTask(taskId);
    }
}
