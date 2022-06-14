package moe._2b2t.essentials.listeners;

import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CopyItemListener implements Listener
{
    private static final CopyItemListener instance = new CopyItemListener();

    private boolean tenPlusOneEnabled;
    private int placeTime;
    private final Map<Player, Integer> playerPlaceTimeMap = new HashMap<>();

    private boolean killDonkeyEnabled;
    private double probability;

    private CopyItemListener()
    {
        super();
    }

    public static CopyItemListener getInstance()
    {
        return instance;
    }

    public CopyItemListener setKillDonkeyEnabled(boolean killDonkeyEnabled)
    {
        this.killDonkeyEnabled = killDonkeyEnabled;
        return this;
    }

    public CopyItemListener setProbability(double probability)
    {
        this.probability = probability;
        return this;
    }

    public CopyItemListener setTenPlusOneEnabled(boolean tenPlusOneEnabled)
    {
        this.tenPlusOneEnabled = tenPlusOneEnabled;
        return this;
    }

    public CopyItemListener setPlaceTime(int placeTime)
    {
        this.placeTime = placeTime;
        return this;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event)
    {
        if (killDonkeyEnabled
                && (event.getEntity() instanceof ChestedHorse)
                && (Math.random() < probability)
        )
        {
            LivingEntity entity = event.getEntity();
            Player player = entity.getKiller();
            if (player != null)
            {
                List<ItemStack> donkeyInventory = event.getDrops();
                player.getInventory().addItem(donkeyInventory.toArray(new ItemStack[0]));
            }
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) //复制潜影盒
    {
        if (tenPlusOneEnabled)
        {
            Player player = event.getPlayer();
            Material material = event.getBlock().getType();
            playerPlaceTimeMap.putIfAbsent(player, 0);
            switch (material)
            {
                case SHULKER_BOX:
                case BLACK_SHULKER_BOX:
                case BLUE_SHULKER_BOX:
                case BROWN_SHULKER_BOX:
                case CYAN_SHULKER_BOX:
                case GRAY_SHULKER_BOX:
                case GREEN_SHULKER_BOX:
                case LIGHT_GRAY_SHULKER_BOX:
                case LIME_SHULKER_BOX:
                case LIGHT_BLUE_SHULKER_BOX:
                case ORANGE_SHULKER_BOX:
                case PINK_SHULKER_BOX:
                case PURPLE_SHULKER_BOX:
                case RED_SHULKER_BOX:
                case MAGENTA_SHULKER_BOX:
                case WHITE_SHULKER_BOX:
                case YELLOW_SHULKER_BOX:
                    int nowPlaceTime = playerPlaceTimeMap.get(player) + 1;
                    playerPlaceTimeMap.put(player, nowPlaceTime);
                    if (nowPlaceTime > placeTime)
                    {
                        playerPlaceTimeMap.put(player, 0);
                        ItemStack shulkerBox = new ItemStack(material);
                        shulkerBox.setItemMeta(event.getItemInHand().getItemMeta());
                        player.getInventory().addItem(shulkerBox);
                    }
            }
        }
    }
}
