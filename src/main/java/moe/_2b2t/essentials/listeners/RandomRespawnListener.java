package moe._2b2t.essentials.listeners;

import io.papermc.lib.PaperLib;
import moe._2b2t.essentials.utils.Tools;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import moe._2b2t.essentials.Moe2b2tEssentials;
import moe._2b2t.essentials.utils.MoeI18n;
import moe._2b2t.essentials.utils.FastRand;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RandomRespawnListener implements Listener
{
    private static final RandomRespawnListener instance = new RandomRespawnListener();

    private boolean enabled;
    private Location center;
    private int radius;
    private Material[] avoidBlocks;
    private final Map<Player, Location> nextRespawnLocation = new HashMap<>();

    private RandomRespawnListener()
    {
        super();
    }

    public static RandomRespawnListener getInstance()
    {
        return instance;
    }

    public RandomRespawnListener setEnabled(boolean enabled)
    {
        this.enabled = enabled;
        return this;
    }

    public RandomRespawnListener setCenter(Location center)
    {
        this.center = center;
        return this;
    }

    public RandomRespawnListener setRadius(int radius)
    {
        this.radius = radius;
        return this;
    }

    public RandomRespawnListener setAvoidBlocks(Material[] avoidBlocks)
    {
        this.avoidBlocks = avoidBlocks;
        return this;
    }

    private boolean isBedOrAnchorSpawn(PlayerRespawnEvent event)
    {
        try
        {
            return event.isAnchorSpawn() || event.isBedSpawn();
        } catch (NoSuchMethodError e) //旧版没重生锚，在旧版执行时会抛异常，所以需要捕获
        {
            //由于旧版没有重生锚，所以只需检测是否使用床复活
            return event.isBedSpawn();
        }

    }

    private boolean isBlockSafe(Material material)
    {
        for (Material avoidBlockMaterial : avoidBlocks)
        {
            if (avoidBlockMaterial == material)
            {
                return false;
            }
        }
        return true;
    }

    private int getWorldMinHeight(World world)
    {
        try
        {
            return world.getMinHeight();
        } catch (NoSuchMethodError e) //没有这个方法证明这个版本还没有把世界高度最小值扩展到-64，所以返回0
        {
            return 0;
        }
    }

    private int getHighestBlockY(World world, int x, int z) //用Location::getHighestBlockYAt会忽略透明方块，所以用这个方法代替
    {
        Block block;
        Material material;
        int y = world.getMaxHeight() - 1;

        boolean hasAirVariants = Tools.hasAirVariants();
        for (; y > getWorldMinHeight(world); y--)
        {
            block = new Location(world, x, y, z).getBlock();
            material = block.getType();
            if (Tools.isNotAir(material, hasAirVariants))
            {
                return y;
            }
        }
        return y;
    }

    private Location getRandomLocation()
    {
        FastRand fastRand = new FastRand();
        int x;
        int z;
        Location newLocation;
        do
        {
            x = center.getBlockX() + (fastRand.nextInt(radius * 2) - radius);
            z = center.getBlockZ() + (fastRand.nextInt(radius * 2) - radius);
            newLocation = new Location(center.getWorld(), x, 255.0d, z);
            PaperLib.getChunkAtAsync(newLocation, true);
            newLocation.setY(getHighestBlockY(Objects.requireNonNull(center.getWorld()), x, z));
        } while (!isBlockSafe(newLocation.getBlock().getType()));
        return newLocation;
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e)
    {
        //在玩家死亡时就开始异步计算下一个随机重生点位置
        //因为区块未生成时Location.getBlock()奇慢无比，所以我选择这样做
        if (enabled)
        {
            Bukkit.getScheduler().runTaskAsynchronously(Moe2b2tEssentials.getInstance(), () ->
                    nextRespawnLocation.put(e.getEntity(), getRandomLocation()));
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event)
    {
        if (enabled && !isBedOrAnchorSpawn(event))
        {
            Player player = event.getPlayer();
            Location location = nextRespawnLocation.get(player);
            player.sendMessage(MoeI18n.format(player, "bedOrAnchorUnavailable"));

            event.setRespawnLocation(location != null ? location : center);
            nextRespawnLocation.remove(player);
        }
    }
//    @EventHandler
//    public void onPlayerPortal(PlayerPortalEvent event)
//    {
//        if (enabled
//                && event.getCause() == PlayerTeleportEvent.TeleportCause.END_PORTAL
//                && "world_the_end".equals(event.getTo().getWorld().getName())
//        {
//
//        }
//    }
}
