package moe._2b2t.essentials;

import io.papermc.lib.PaperLib;
import moe._2b2t.essentials.commands.*;
import moe._2b2t.essentials.listeners.*;
import moe._2b2t.essentials.listeners.commandmanager.CmdCooldownListener;
import moe._2b2t.essentials.listeners.commandmanager.CmdReplaceListener;
import moe._2b2t.essentials.listeners.commandmanager.CmdWhiteListListener;
import moe._2b2t.essentials.managers.*;
import moe._2b2t.essentials.utils.MoeI18n;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import moe._2b2t.essentials.utils.CmdUtils;

import java.io.File;
import java.util.*;

public final class Moe2b2tEssentials extends JavaPlugin
{
    private static Moe2b2tEssentials instance;
    private static String pluginName;
    private static String pluginVersion;
    private static boolean papiExists;

    public static void pluginMessage(String msg)
    {
        getInstance().getLogger().info(
                ChatColor.DARK_GRAY + "[" + ChatColor.RED + "!" + ChatColor.DARK_GRAY + "]" +
                        ChatColor.AQUA + pluginName + " " +
                        ChatColor.WHITE + pluginVersion + " " +
                        ChatColor.GRAY + (msg.contains("\n") ? "\n" + msg : msg)//防止前缀导致多行文本不被左对齐
        );
    }

    public static Moe2b2tEssentials getInstance()
    {
        return instance;
    }

    private void addPermissions()
    {
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.addPermission(new Permission("moe2b2t.admin"));
        pluginManager.addPermission(new Permission("moe2b2t.copy"));
        pluginManager.addPermission(new Permission("moe2b2t.toggle.changeothers"));
        pluginManager.addPermission(new Permission("moe2b2t.stat.viewothers"));
        pluginManager.addPermission(new Permission("moe2b2t.spawnprotection.bypass"));
        pluginManager.addPermission(new Permission("moe2b2t.commandwhitelist.bypass"));
        pluginManager.addPermission(new Permission("moe2b2t.commandcooldown.bypass"));
        pluginManager.addPermission(new Permission("moe2b2t.fly"));
    }

    private void regListeners()
    {
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(ShowAlertsListener.getInstance(), this);
        pluginManager.registerEvents(ShowDeathMsgsListener.getInstance(), this);
        pluginManager.registerEvents(AntiSpamListener.getInstance(), this);
        pluginManager.registerEvents(VisualListener.getInstance(), this);
        pluginManager.registerEvents(ProtectorListener.getInstance(), this);
        pluginManager.registerEvents(AntiLagListener.getInstance(), this);
        pluginManager.registerEvents(NewbieKitListener.getInstance(), this);
        pluginManager.registerEvents(RandomRespawnListener.getInstance(), this);
        pluginManager.registerEvents(CopyItemListener.getInstance(), this);
        pluginManager.registerEvents(I18nListener.getInstance(), this);
        pluginManager.registerEvents(CmdReplaceListener.getInstance(), this);
        pluginManager.registerEvents(CmdCooldownListener.getInstance(), this);
        pluginManager.registerEvents(CmdWhiteListListener.getInstance(), this);
    }

    private void regCommands()
    {
        Objects.requireNonNull(getCommand("moe2b2t")).setExecutor(Moe2b2t.getInstance());
        Objects.requireNonNull(getCommand("moe2b2t")).setTabCompleter(Moe2b2t.getInstance());
        Objects.requireNonNull(getCommand("help")).setExecutor(Help.getInstance());
        Objects.requireNonNull(getCommand("stat")).setExecutor(Stat.getInstance());
        Objects.requireNonNull(getCommand("stat")).setTabCompleter(Stat.getInstance());
        Objects.requireNonNull(getCommand("stats")).setExecutor(Stats.getInstance());
        Objects.requireNonNull(getCommand("toggle")).setExecutor(Toggle.getInstance());
        Objects.requireNonNull(getCommand("toggle")).setTabCompleter(Toggle.getInstance());
        Objects.requireNonNull(getCommand("suicide")).setExecutor(Suicide.getInstance());
        Objects.requireNonNull(getCommand("ignore")).setExecutor(Ignore.getInstance());
        Objects.requireNonNull(getCommand("delay")).setExecutor(Delay.getInstance());
        Objects.requireNonNull(getCommand("copy")).setExecutor(Copy.getInstance());
        Objects.requireNonNull(getCommand("plugins")).setExecutor(Plugins.getInstance());
        Objects.requireNonNull(getCommand("fly")).setExecutor(Fly.getInstance());
    }

    public static boolean isPapiExists()
    {
        return Moe2b2tEssentials.papiExists;
    }

    public void reloadI18nCfg()
    {
        File file = new File(getDataFolder(), "i18n.yml");
        if (!file.exists())
        {
            saveResource("i18n.yml", false);
        }
        MoeI18n.init(file, getConfig().getString("consoleLang"));

        Broadcaster.stop();
        if (getConfig().getBoolean("Broadcaster.enabled"))
        {
            Broadcaster.init(this, getConfig().getLong("Broadcaster.period"));
        }
    }

    @Override
    public void onEnable()
    {
        Moe2b2tEssentials.pluginName = getName();
        Moe2b2tEssentials.pluginVersion = getDescription().getVersion();
        Moe2b2tEssentials.instance = this;

        saveDefaultConfig();
        regCommands();
        addPermissions();
        regListeners();

        super.reloadConfig();
        reloadI18nCfg();
        reloadConfig();
        if (isPapiExists())
        {
            Moe2b2tExpansion.getInstance().startAutoRefresh();
            Moe2b2tExpansion.getInstance().register();
        }
        PaperLib.suggestPaper(this);
        pluginMessage(MoeI18n.format("pluginLoaded"));
    }

    @Override
    public void onDisable()
    {
        Bukkit.getScheduler().cancelTasks(this);
    /*
        AutoRestarter.cancel();
        WorldBorderManager.getInstance().stop();
        moe2b2tExpansion.getInstance().stopAutoRefresh();
        Broadcaster.stop();
    */
        pluginMessage(MoeI18n.format("pluginUnloaded"));
    }

    @Override
    public void reloadConfig()
    {
        super.reloadConfig();
        FileConfiguration cfg = getConfig();
        Moe2b2tEssentials.papiExists = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
        MoeI18n.setConsoleLang(cfg.getString("consoleLang"));
        if (!papiExists)
        {
            pluginMessage(MoeI18n.format("papiNotExists"));
        }
        Location spawnLocation = new Location(
                Bukkit.getWorld(Objects.requireNonNull(cfg.getString("SpawnPoint.world"))),
                cfg.getDouble("SpawnPoint.x"),
                cfg.getDouble("SpawnPoint.y"),
                cfg.getDouble("SpawnPoint.z"),
                (float) cfg.getDouble("SpawnPoint.yaw"),
                (float) cfg.getDouble("SpawnPoint.pitch")
        );
        //开启safemode后则将Y设置为当前位置最高的方块
        if (cfg.getBoolean("SpawnPoint.safeMode"))
        {
            spawnLocation.setY(Objects.requireNonNull(spawnLocation.getWorld()).getHighestBlockYAt(spawnLocation));
        }
        Objects.requireNonNull(spawnLocation.getWorld()).setSpawnLocation(spawnLocation);
        AntiSpamListener.getInstance()
                .setMinPeriod(cfg.getInt("AntiSpam.antiFloodScreenMinPeriod"))
                .setAntiFloodScreenEnabled(cfg.getBoolean("AntiSpam.antiFloodScreen"))
                .setAntiRepeatEnabled(cfg.getBoolean("AntiSpam.antiRepeat"))
                .setIgnoredCharSeq(cfg.getStringList("AntiSpam.ignoredCharSeq"))
        ;
        VisualListener.getInstance()
                .setChatColorEnabled(cfg.getBoolean("Visual.chatColorEnabled"))
                .setAnvilColorEnabled(cfg.getBoolean("Visual.anvilColorEnabled"))
                .setSignColorEnabled(cfg.getBoolean("Visual.signColorEnabled"))
                .setChatFormat(cfg.getString("Visual.chatFormat"))
                .setConsolePrefixEnabled(cfg.getBoolean("Visual.consolePrefix.enabled"))
                .setConsolePrefix(cfg.getString("Visual.consolePrefix.prefix"))
        ;
        ProtectorListener.getInstance()
                .setSpawnProtectionEnabled(cfg.getBoolean("ServerProtection.spawnProtection.enabled"))
                .setSpawnLocation(spawnLocation)
                .setRadius(cfg.getInt("ServerProtection.spawnProtection.radius"))
                .setEndPortalProtectionEnabled(cfg.getBoolean("ServerProtection.endPortalProtection"))
                .setDisableOPEnabled(cfg.getBoolean("ServerProtection.disableOP.enabled"))
                .setDisableOPWhiteList(cfg.getStringList("ServerProtection.disableOP.whitelist"))
                .setAnti32kEnabled(cfg.getBoolean("ServerProtection.anti32K"))
                .setAntiBookBanEnabled(cfg.getBoolean("ServerProtection.antiBookBan.enabled"))
                .setMaxPages(cfg.getInt("ServerProtection.antiBookBan.maxPages"))
                .setInvincibleTime(cfg.getInt("ServerProtection.invincibleTimeAfterRespawn"))
        ;
        AntiLagListener.getInstance()
                .setAntiNetherPortalJam(cfg.getBoolean("AntiLag.antiNetherPortalJam"))
                .setTntLimit(cfg.getBoolean("AntiLag.tntLimit.enabled"))
                .setMaxExplodingTnts(cfg.getInt("AntiLag.tntLimit.maxExplodingTnts"))
        ;
        WorldBorderManager worldBorderManager = WorldBorderManager.getInstance();
        worldBorderManager.stop();
        WorldBorderManager.getInstance().start(
                this,
                cfg.getBoolean("WorldBorderManager.enabled"),
                cfg.getBoolean("WorldBorderManager.increaseWhenNoOne"),
                cfg.getInt("WorldBorderManager.maxSize"),
                cfg.getInt("WorldBorderManager.growth"),
                cfg.getInt("WorldBorderManager.increaseRate"),
                cfg.getInt("WorldBorderManager.finishInSeconds")
        );
        Material[] dangerousMaterials = null;
        try
        {
            //获取配置文件中危险方块列表并转成Material数组
            List<String> list = cfg.getStringList("SpawnPoint.randomRespawn.dangerousBlocks");
            dangerousMaterials = new Material[list.size()];
            for (int i = 0; i < dangerousMaterials.length; i++)
            {
                dangerousMaterials[i] = Material.matchMaterial(list.get(i));
            }
        } catch (ClassCastException | NullPointerException | IllegalArgumentException e)
        {
            getLogger().severe(MoeI18n.format("failedToLoadConfig"));
            e.printStackTrace();
        }

        RandomRespawnListener.getInstance()
                .setEnabled(cfg.getBoolean("SpawnPoint.randomRespawn.enabled"))
                .setRadius(cfg.getInt("SpawnPoint.randomRespawn.radius"))
                .setCenter(new Location(
                        Bukkit.getWorld(Objects.requireNonNull(cfg.getString("SpawnPoint.randomRespawn.center.world"))),
                        cfg.getDouble("SpawnPoint.randomRespawn.center.x"),
                        cfg.getDouble("SpawnPoint.randomRespawn.center.y"),
                        cfg.getDouble("SpawnPoint.randomRespawn.center.z")))
                .setAvoidBlocks(dangerousMaterials)
        ;

        CopyItemListener.getInstance()
                .setTenPlusOneEnabled(cfg.getBoolean("CopyItem.tenPlusOne.enabled"))
                .setPlaceTime(cfg.getInt("CopyItem.tenPlusOne.placeTime"))
                .setKillDonkeyEnabled(cfg.getBoolean("CopyItem.killDonkey.enabled"))
                .setProbability(cfg.getDouble("CopyItem.killDonkey.probability"))
        ;

        AutoRestarter.init(this, cfg.getBoolean("AutoRestart.enabled"), cfg.getInt("AutoRestart.period"));

        Broadcaster.stop();
        if (cfg.getBoolean("Broadcaster.enabled"))
        {
            Broadcaster.init(this, cfg.getLong("Broadcaster.period"));
        }

        ProfileManager.init(Objects.requireNonNull(cfg.getConfigurationSection("defaultPlayerValues")).getValues(false));

        List<Map<String, String>> cmdReplacementMapList = new ArrayList<>();
        List<Map<?, ?>> cmdMapList = cfg.getMapList("CommandManager.commandReplacement.list");
        cmdMapList.forEach(cmdMap ->
        {
            Map<String, String> tempStrMap = new HashMap<>();
            cmdMap.forEach((obj1, obj2) ->
                    tempStrMap.put(String.valueOf(obj1), String.valueOf(obj2)));
            cmdReplacementMapList.add(tempStrMap);
        });

        Map<Command, String> originalReplacedMap = new HashMap<>();
        try
        {
            cfg.getMapList("CommandManager.commandReplacement.list")
                    .forEach(map ->
                            originalReplacedMap.put(
                                    CmdUtils.getCmd(map.get("source").toString()),
                                    map.get("target").toString()
                            )
                    )
            ;
        } catch (NullPointerException e)
        {
            getLogger().severe(MoeI18n.format("failedToLoadConfig"));
            e.printStackTrace();
        }

        CmdReplaceListener.getInstance()
                .setEnabled(cfg.getBoolean("CommandManager.commandReplacement.enabled"))
                .setOriginalReplacedMap(originalReplacedMap)
        ;

        List<Command> whiteListCommands = new ArrayList<>();
        cfg.getStringList("CommandManager.commandWhitelist.list").forEach(s -> whiteListCommands.add(CmdUtils.getCmd(s)));
        CmdWhiteListListener.getInstance()
                .setEnabled(cfg.getBoolean("CommandManager.commandWhitelist.enabled"))
                .setWhiteList(whiteListCommands)
        ;

        CmdCooldownManager.setPlugin(this);

        Map<Command, Integer> cmdCooldownTimeMap = new HashMap<>();
        try
        {
            cfg.getMapList("CommandManager.commandCooldown.list").forEach(tempMap ->
                    cmdCooldownTimeMap.put(CmdUtils.getCmd(String.valueOf(tempMap.get("command"))), Integer.parseInt(String.valueOf(tempMap.get("time")))));
        } catch (ClassCastException | NumberFormatException e)
        {
            getLogger().severe(MoeI18n.format("failedToLoadConfig"));
            e.printStackTrace();
        }
        CmdCooldownListener.getInstance()
                .setEnabled(cfg.getBoolean("CommandManager.commandCooldown.enabled"))
                .setCmdCooldownTimeMap(cmdCooldownTimeMap)
        ;

        ChunkUnloader.getInstance()
                .setEnabled(cfg.getBoolean("AntiLag.chunkUnloader.enabled"))
                .setPeriod(cfg.getInt("AntiLag.chunkUnloader.period"))
                .start()
        ;

        //从配置文件中获取新手套装信息并且转成ItemStack数组并传入NewbieKitListener中
        ItemStack[] kitItemStack = null;
        try
        {
            List<Map<?, ?>> kitMapList = cfg.getMapList("NewbieKit");
            int j = kitMapList.size();
            kitItemStack = new ItemStack[j];
            Map<?, ?> mapTemp;
            for (int i = 0; i < j; i++)
            {
                mapTemp = kitMapList.get(i);
                kitItemStack[i] = new ItemStack(
                        Objects.requireNonNull(Material.matchMaterial((String) mapTemp.get("id"))),
                        (Integer) mapTemp.get("amount")
                );
            }
        } catch (ClassCastException | NullPointerException | IllegalArgumentException e)
        {
            getLogger().severe(MoeI18n.format("failedToLoadConfig"));
            e.printStackTrace();
        }
        NewbieKitListener.getInstance().setItemStacks(kitItemStack);
    }
}
