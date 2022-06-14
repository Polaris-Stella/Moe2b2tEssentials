package moe._2b2t.essentials.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

public class NewbieKitListener implements Listener
{
    private static final NewbieKitListener instance = new NewbieKitListener();

    private ItemStack[] itemStacks;

    private NewbieKitListener()
    {
        super();
    }

    public NewbieKitListener setItemStacks(ItemStack[] itemStacks)
    {
        this.itemStacks = itemStacks;
        return this;
    }

    public static NewbieKitListener getInstance()
    {
        return instance;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) //新手礼包
    {
        if (!event.getPlayer().hasPlayedBefore()
                && itemStacks != null
                && itemStacks.length != 0
        )
        {
            event.getPlayer().getInventory().addItem(itemStacks);
        }
    }
}
