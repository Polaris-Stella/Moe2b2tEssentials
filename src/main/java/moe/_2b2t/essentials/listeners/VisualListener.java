package moe._2b2t.essentials.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class VisualListener implements Listener
{
    private static final VisualListener instance = new VisualListener();

    private String chatFormat;
    private boolean chatColorEnabled;
    private boolean anvilColorEnabled;
    private boolean signColorEnabled;
    private boolean consolePrefixEnabled;
    private String consolePrefix;

    private VisualListener()
    {
        super();
    }

    public static VisualListener getInstance()
    {
        return instance;
    }

    public VisualListener setChatFormat(String chatFormat)
    {
        this.chatFormat = chatFormat;
        return this;
    }

    public VisualListener setChatColorEnabled(boolean chatColorEnabled)
    {
        this.chatColorEnabled = chatColorEnabled;
        return this;
    }

    public VisualListener setAnvilColorEnabled(boolean anvilColorEnabled)
    {
        this.anvilColorEnabled = anvilColorEnabled;
        return this;
    }

    public VisualListener setSignColorEnabled(boolean signColorEnabled)
    {
        this.signColorEnabled = signColorEnabled;
        return this;
    }

    public VisualListener setConsolePrefixEnabled(boolean consolePrefixEnabled)
    {
        this.consolePrefixEnabled = consolePrefixEnabled;
        return this;
    }

    public VisualListener setConsolePrefix(String consolePrefix)
    {
        this.consolePrefix = consolePrefix;
        return this;
    }

    @EventHandler
    public void onConsoleMessage(ServerCommandEvent event)
    {
        if (consolePrefixEnabled)
        {
            String cmd = event.getCommand();
            if (cmd.startsWith("say") || cmd.startsWith("/say"))
            {
                event.setCommand(cmd.replaceFirst("say ", "say " + consolePrefix));
            }
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event)
    {
        if (chatColorEnabled)
        {
            event.setMessage(event.getMessage().replaceAll("&", "§").replaceAll("§§", "&"));
        }
        if (!"".equals(chatFormat))
        {
            event.setFormat(chatFormat);
        }
    }

    @EventHandler
    public void onSignPlace(SignChangeEvent event)
    {
        if (!signColorEnabled)
        {
            return;
        }
        for (int i = 0; i < 4; i++)
        {
            event.setLine(i, event.getLine(i).replaceAll("&", "§").replaceAll("§§", "&"));
        }
    }

    @EventHandler
    public void onAnvilUse(InventoryClickEvent event)
    {
        if (!anvilColorEnabled
                || event.isCancelled()
                || event.getInventory().getType() != InventoryType.ANVIL
                || event.getSlotType() != InventoryType.SlotType.RESULT)
        {
            return;
        }
        ItemStack itemStack = event.getCurrentItem();
        assert itemStack != null;
        if (itemStack.hasItemMeta())
        {
            ItemMeta meta = itemStack.getItemMeta();
            assert meta != null; //上边检测了物品是否有meta,所以ItemStack::getItemMeta肯定不返回null
            if (meta.getDisplayName().contains("&"))
            {
                String name = meta.getDisplayName().replaceAll("&", "§").replaceAll("§§", "&");
                meta.setDisplayName(name);
                itemStack.setItemMeta(meta);
            }
        }
    }
}
