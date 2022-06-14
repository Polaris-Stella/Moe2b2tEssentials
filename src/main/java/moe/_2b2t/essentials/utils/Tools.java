package moe._2b2t.essentials.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import moe._2b2t.essentials.Moe2b2tEssentials;
import moe._2b2t.essentials.managers.ProfileManager;

import java.util.List;

public class Tools
{
    private static List<Material> dangerousBlocks;

    public static void setDangerousBlocks(List<Material> dangerousBlocks)
    {
        Tools.dangerousBlocks = dangerousBlocks;
    }

    public static boolean isBlockDangerous(Material material)
    {
        return dangerousBlocks.contains(material);
    }

    public static void broadcastI18nMsg(String i18nKey, Object... objects)
    {
        Moe2b2tEssentials.pluginMessage(MoeI18n.format(i18nKey, objects));
        Bukkit.getOnlinePlayers().stream().forEachOrdered(player -> player.sendMessage(MoeI18n.format(player, i18nKey, objects)));
    }

    public static void broadcastMsgWithFilter(String cfgKey, String i18nKey, Object... objects)
    {
        Bukkit.getOnlinePlayers().stream()
                //若设置了不显示加入和退出信息，则从清单中过滤掉此玩家
                .filter(player1 -> Boolean.parseBoolean(ProfileManager.get(player1, cfgKey)))
                .forEachOrdered(player2 -> player2.sendMessage(MoeI18n.format(player2, i18nKey, objects)));
    }

    public static boolean hasAirVariants()
    {
        try
        {
            Material.class.getField("CAVE_AIR");
            Material.class.getField("VOID_AIR");
            return true;
        } catch (NoSuchFieldException e)
        {
            return false;
        }
    }

    public static boolean isNotAir(Material material, boolean hasAirVariants)
    {
        return hasAirVariants ? material != Material.AIR && material != Material.CAVE_AIR && material != Material.VOID_AIR : material != Material.AIR;
    }
}
