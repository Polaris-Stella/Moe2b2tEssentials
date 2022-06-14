package moe._2b2t.essentials;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import moe._2b2t.essentials.managers.AutoRestarter;
import moe._2b2t.essentials.utils.NmsViaVersionHelper;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;

import java.io.File;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

public class Moe2b2tExpansion extends PlaceholderExpansion
{
    private static final Moe2b2tExpansion instance = new Moe2b2tExpansion();
    private final Moe2b2tEssentials moe2b2tEssentials = Moe2b2tEssentials.getInstance();

    private String mapSize;
    private String playerNum;

    private Object craftServer;
    private Field tps;

    private int autoRefreshTaskId = -1;

    private Moe2b2tExpansion()
    {
        super();
        initNms();
    }

    public static Moe2b2tExpansion getInstance()
    {
        return Moe2b2tExpansion.instance;
    }

    private void initNms()
    {
        craftServer = NmsViaVersionHelper.getNmsObject();
        try
        {
            assert craftServer != null;
            tps = craftServer.getClass().getField("recentTps");
        } catch (NoSuchFieldException e)
        {
            e.printStackTrace();
        }
    }


    private double getTps()
    {
        try
        {
            return ((double[]) tps.get(craftServer))[0];
        } catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
        return 0.0d;
    }

    private void refreshValue()
    {
        String serverFolderPath = Bukkit.getServer().getWorldContainer().getAbsolutePath();
        long totalSize = 0;

        File regionFolder = new File(serverFolderPath + File.separator + "world" + File.separator + "region");
        totalSize += regionFolder.exists() ? FileUtils.sizeOfDirectory(regionFolder) : 0;
        regionFolder = new File(serverFolderPath + File.separator + "world_nether" + File.separator + "DIM-1" + File.separator + "region");
        totalSize += regionFolder.exists() ? FileUtils.sizeOfDirectory(regionFolder) : 0;
        regionFolder = new File(serverFolderPath + File.separator + "world_the_end" + File.separator + "DIM1" + File.separator + "region");
        totalSize += regionFolder.exists() ? FileUtils.sizeOfDirectory(regionFolder) : 0;

        mapSize = formatFileSize(totalSize);

        File playerDataFolder = new File(Bukkit.getServer().getWorldContainer(), "world" + File.separator + "playerdata");
        playerNum = "" + Arrays.stream(Objects.requireNonNull(playerDataFolder.listFiles())).filter(f -> f.getName().endsWith(".dat")).count();
    }

    public void startAutoRefresh()
    {
        //10min刷新一次
        autoRefreshTaskId = Bukkit.getScheduler().runTaskTimerAsynchronously(
                moe2b2tEssentials,
                this::refreshValue,
                0L,
                10L * 60L * 20L).getTaskId();
    }

    public void stopAutoRefresh()
    {
        Bukkit.getScheduler().cancelTask(autoRefreshTaskId);
    }

    @Override
    public boolean persist()
    {
        return true;
    }

    @Override
    public String getIdentifier()
    {
        return "moe2b2t";
    }

    @Override
    public String getAuthor()
    {
        return moe2b2tEssentials.getDescription().getAuthors().toString();
    }

    @Override
    public boolean canRegister()
    {
        return true;
    }

    private static String formatFileSize(long fileSize)
    {
        // 转换文件大小
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString;
        if (fileSize < 1024)
        {
            fileSizeString = df.format((double) fileSize) + "B";
        } else if (fileSize < 1048576)
        {
            fileSizeString = df.format((double) fileSize / 1024) + "KB";
        } else if (fileSize < 1073741824)
        {
            fileSizeString = df.format((double) fileSize / 1048576) + "MB";
        } else
        {
            fileSizeString = df.format((double) fileSize / 1073741824) + "GB";
        }
        return fileSizeString;
    }

    @Override
    public String getVersion()
    {
        return moe2b2tEssentials.getDescription().getVersion();
    }


    @Override
    public String onPlaceholderRequest(Player p, String params)
    {
        switch (params)
        {
            case "kills":
                return String.valueOf(p.getStatistic(Statistic.PLAYER_KILLS) + p.getStatistic(Statistic.MOB_KILLS));
            case "deaths":
                return String.valueOf(p.getStatistic(Statistic.DEATHS));
            case "mobs":
                return String.valueOf(Bukkit.getWorlds().stream().mapToInt(world -> world.getEntities().size()).sum());
            case "days":
                return String.valueOf(((int) (Bukkit.getWorld("world").getFullTime() / 24000)));
            case "border":
                return "±" + ((int) (Bukkit.getWorld("world").getWorldBorder().getSize() / 2));
            case "online_amount":
                return String.valueOf(Bukkit.getOnlinePlayers().size());
            case "tps":
                return String.format("%.2f", getTps());
            case "mapsize":
                return mapSize;
            case "playernum":
                return playerNum;
            case "next_restart":
                Date nextRestart = new Date(AutoRestarter.getNextRestartMillis());
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                return sdf.format(nextRestart);
            default:
                return "ILLEGAL_ARGUEMENT";
        }
    }
}