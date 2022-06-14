package moe._2b2t.essentials.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityPortalEvent;

import java.util.Set;
import java.util.stream.Collectors;

public class AntiLagListener implements Listener
{
    private static final AntiLagListener instance = new AntiLagListener();

    private boolean antiNetherPortalJam;
    private boolean tntLimit;
    private int maxExplodingTnts;

    private AntiLagListener()
    {
        super();
    }

    public static AntiLagListener getInstance()
    {
        return instance;
    }

    public AntiLagListener setAntiNetherPortalJam(boolean antiNetherPortalJam)
    {
        this.antiNetherPortalJam = antiNetherPortalJam;
        return this;
    }

    public AntiLagListener setTntLimit(boolean tntLimit)
    {
        this.tntLimit = tntLimit;
        return this;
    }

    public AntiLagListener setMaxExplodingTnts(int maxExplodingTnts)
    {
        this.maxExplodingTnts = maxExplodingTnts;
        return this;
    }

    @EventHandler
    public void onEntityPortal(EntityPortalEvent event) //防止地狱门发射tnt卡服
    {
        if (antiNetherPortalJam && !(event.getEntity() instanceof LivingEntity))
        {
            event.setCancelled(true);
        }
    }


    @EventHandler
    public void onTntExplode(EntityExplodeEvent event) //限制TNT爆炸数量
    {
        if (tntLimit && event.getEntity().getType() == EntityType.PRIMED_TNT)
        {
            Entity entity = event.getEntity();
            Set<Entity> entitySet = entity.getWorld().getEntities().stream().filter(e -> e.getType() == EntityType.PRIMED_TNT).collect(Collectors.toSet());
            if (entitySet.size() > maxExplodingTnts)
            {
                entitySet.forEach(Entity::remove);
                event.setCancelled(true);
            }
        }
    }
}
