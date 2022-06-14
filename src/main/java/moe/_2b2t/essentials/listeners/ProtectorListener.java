package moe._2b2t.essentials.listeners;

import moe._2b2t.essentials.utils.Tools;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import moe._2b2t.essentials.Moe2b2tEssentials;
import moe._2b2t.essentials.utils.MoeI18n;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;

public class ProtectorListener implements Listener
{
    private static final ProtectorListener instance = new ProtectorListener();

    private boolean spawnProtectionEnabled = false;
    private Location spawnLocation;
    private int radius = 0;
    private boolean endPortalProtectionEnabled = false;
    private boolean disableOPEnabled = false;
    private List<String> disableOPWhiteList;
    private boolean anti32kEnabled = false;
    private boolean antiBookBanEnabled = false;
    private int maxPages = 0;
    private int invincibleTime = 0;

    public ProtectorListener setSpawnProtectionEnabled(boolean spawnProtectionEnabled)
    {
        this.spawnProtectionEnabled = spawnProtectionEnabled;
        return this;
    }

    public ProtectorListener setSpawnLocation(Location spawnLocation)
    {
        this.spawnLocation = spawnLocation;
        return this;
    }

    public ProtectorListener setRadius(int radius)
    {
        this.radius = radius;
        return this;
    }

    public ProtectorListener setEndPortalProtectionEnabled(boolean endPortalProtectionEnabled)
    {
        this.endPortalProtectionEnabled = endPortalProtectionEnabled;
        return this;
    }

    public ProtectorListener setDisableOPEnabled(boolean disableOPEnabled)
    {
        this.disableOPEnabled = disableOPEnabled;
        return this;
    }

    public ProtectorListener setDisableOPWhiteList(List<String> disableOPWhiteList)
    {
        this.disableOPWhiteList = disableOPWhiteList;
        return this;
    }

    public ProtectorListener setAnti32kEnabled(boolean anti32kEnabled)
    {
        this.anti32kEnabled = anti32kEnabled;
        return this;
    }

    public ProtectorListener setAntiBookBanEnabled(boolean antiBookBanEnabled)
    {
        this.antiBookBanEnabled = antiBookBanEnabled;
        return this;
    }

    public ProtectorListener setMaxPages(int maxPages)
    {
        this.maxPages = maxPages;
        return this;
    }

    public ProtectorListener setInvincibleTime(int invincibleTime)
    {
        this.invincibleTime = invincibleTime;
        return this;
    }

    private ProtectorListener()
    {
        super();
    }

    public static ProtectorListener getInstance()
    {
        return instance;
    }

    private boolean isInProtectionArea(Location location)
    {
        return location.getWorld().equals(spawnLocation.getWorld())
                && abs(location.getX() - spawnLocation.getX()) < radius
                && abs(location.getZ() - spawnLocation.getZ()) < radius;
    }

    @EventHandler
    public void onCmdProcess(PlayerCommandPreprocessEvent event) //防止熊孩子卡OP
    {
        Player player = event.getPlayer();
        if (disableOPEnabled && player.isOp() && !disableOPWhiteList.contains(player))
        {
            event.setCancelled(true);
            player.setOp(false);
            player.setGameMode(GameMode.SURVIVAL);
            player.setHealth(0.0);
        }
    }

    @EventHandler
    public void onUse(PlayerInteractEvent event)
    {
        if (anti32kEnabled)
        {
            ItemStack itemStack = event.getItem();
            if (itemStack != null
                    && Tools.isNotAir(itemStack.getType(), Tools.hasAirVariants())
                    && itemStack.hasItemMeta()
                    && itemStack.getItemMeta().hasEnchants()
            )
            {
                ItemMeta itemMeta = itemStack.getItemMeta();
                itemMeta.getEnchants().forEach((enchantment, level) ->
                {
                    if (level > enchantment.getMaxLevel())
                    {
                        itemMeta.removeEnchant(enchantment);
                        event.setCancelled(true);
                    }
                });
                itemStack.setItemMeta(itemMeta);
            }
        }
    }

    @EventHandler
    public void onTntExplode(EntityExplodeEvent event)
    {
        if (spawnProtectionEnabled)
        {
            List<Block> blocks = event.blockList();
            for (Block block : blocks)
            {
                if (isInProtectionArea(block.getLocation()))
                {
                    event.getEntity().remove();
                    event.setCancelled(true);
                    break;
                }
            }
        }
    }

    @EventHandler
    public void onBookAndQuillSave(PlayerEditBookEvent event)
    {
        //防止BookBan
        //特别说明：
        // PlayerEditBookEvent取消后书也会被编辑
        // event.setNewBookMeta(event.getPreviousBookMeta())调用后新建的书页也会保留
        // 只能这样写，请勿更改
        if (antiBookBanEnabled)
        {
            if (event.getNewBookMeta().getPageCount() > maxPages)
            {
                BookMeta bookMeta = event.getNewBookMeta();
                List<String> newPages = new ArrayList<>();
                int index = 0;
                for (String str : bookMeta.getPages())
                {
                    newPages.add(str);
                    index++;
                    if (index >= maxPages)
                    {
                        break;
                    }
                }
                bookMeta.setPages(newPages);
                event.setNewBookMeta(bookMeta);
                Player player = event.getPlayer();
                player.sendMessage(MoeI18n.format(player, "AntiBookBanTriggered", maxPages));
            }
        }
    }

    @EventHandler
    public void onBuild(BlockPlaceEvent event)
    {
        if (spawnProtectionEnabled)
        {
            Location location = event.getBlockPlaced().getLocation();
            Player player = event.getPlayer();
            if (isInProtectionArea(location) && !event.getPlayer().hasPermission("moe2b2t.spawnprotection.bypass"))
            {
                event.setCancelled(true);
                player.sendMessage(MoeI18n.format(player, "spawnProtectionTriggered", radius));
            }
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event)
    {
        if (spawnProtectionEnabled)
        {
            Location location = event.getBlock().getLocation();
            Player player = event.getPlayer();
            if (isInProtectionArea(location) && !event.getPlayer().hasPermission("moe2b2t.spawnprotection.bypass"))
            {
                event.setCancelled(true);
                player.sendMessage(MoeI18n.format(player, "spawnProtectionTriggered", radius));
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event)
    {
        if (spawnProtectionEnabled && isInProtectionArea(event.getEntity().getLocation()) && event.getDamager() instanceof Player)
        {
            Player player = (Player) event.getDamager();
            if (!player.hasPermission("moe2b2t.protection.spawn.bypass"))
            {
                player.sendMessage(MoeI18n.format(player, "spawnProtectionTriggered", radius));
                event.setCancelled(true);
            }
        }
    }

    private boolean isEndPortal(Material material)
    {
        return material == Material.END_PORTAL
                || material == Material.END_PORTAL_FRAME
                || material == Material.END_GATEWAY;
    }

    private Location getWaterLocation(Block block, BlockFace blockFace)
    {
        return new Location(
                block.getWorld()
                , block.getX() + blockFace.getModX()
                , block.getY() + blockFace.getModY()
                , block.getZ() + blockFace.getModZ()
        );
    }

    @EventHandler
    public void onBucket(PlayerBucketFillEvent event)
    {
        if (spawnProtectionEnabled
                && isInProtectionArea(event.getBlockClicked().getLocation())
                && !event.getPlayer().hasPermission("moe2b2t.protection.spawn.bypass"))
        {
            event.getItemStack().setType(Material.BUCKET);
            event.setCancelled(true);
        }
        if (endPortalProtectionEnabled && isEndPortal(getWaterLocation(event.getBlockClicked(), event.getBlockFace()).getBlock().getType()))
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBucket(PlayerBucketEmptyEvent event)
    {
        if (spawnProtectionEnabled
                && isInProtectionArea(event.getBlockClicked().getLocation())
                && !event.getPlayer().hasPermission("moe2b2t.protection.spawn.bypass"))
        {
            event.getItemStack().setType(Material.BUCKET);
            event.setCancelled(true);
        }
        if (endPortalProtectionEnabled && isEndPortal(getWaterLocation(event.getBlockClicked(), event.getBlockFace()).getBlock().getType()))
        {
            event.setCancelled(true);
        }
    }

    //TODO 防止在出生点用水桶抓鱼
  /*  @EventHandler
    public void onBucket(PlayerBucketEntityEvent event)
    {
        if (spawnProtectionEnabled
                && isInProtectionArea(event.getEntity().getLocation())
                && !event.getPlayer().hasPermission("moe2b2t.protection.spawn.bypass"))
        {
            event.getEntityBucket().setType(Material.BUCKET);
            event.setCancelled(true);
        }
    }
*/
    @EventHandler
    public void onRespawn(PlayerRespawnEvent event)
    {
        //复活后无敌时间
        if (invincibleTime != 0)
        {
            Player player = event.getPlayer();
            Bukkit.getScheduler().runTaskLater(Moe2b2tEssentials.getInstance(), () ->
            {
                player.sendMessage(MoeI18n.format(player, "InvincibleEffectApplied", invincibleTime));
                //乘以20转换为tick
                player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, invincibleTime * 20, 5));
                player.addPotionEffect(new PotionEffect(PotionEffectType.HEAL, invincibleTime * 20, 2));
            }, 1L);//必须延迟1tick执行，否则药水效果应用不上
        }
    }
}
