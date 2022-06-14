package moe._2b2t.essentials.utils;

import moe._2b2t.essentials.managers.ProfileManager;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MoeI18n
{
    private static FileConfiguration configuration;
    private static String consoleLang;

    public static void init(File file, String defaultLang)
    {
        configuration = YamlConfiguration.loadConfiguration(file);
        MoeI18n.consoleLang = defaultLang;
    }

    public static boolean hasLanguage(String lang)
    {
        return configuration.get(lang) != null;
    }

    public static void setConsoleLang(String consoleLang)
    {
        MoeI18n.consoleLang = consoleLang;
    }

    public static String getConsoleLang()
    {
        return consoleLang;
    }

    private static String getCmdSenderLang(CommandSender sender)
    {
        return sender instanceof Player ? ProfileManager.get((Player) sender, "lang") : consoleLang;
    }

    public static List<String> getAvailableLangs()
    {
        return new ArrayList<>(configuration.getKeys(false));
    }

    public static List<String> getBroadcastMsgsInSpecifiedLang(String lang)
    {
        return configuration.getStringList(lang + "." + "broadcastMsgs");
    }

    public static String format(String lang, String key, Object... objects)
    {
        String str = hasLanguage(lang) ? configuration.getString(lang + '.' + key) : lang;
        str = str != null ? str : key;
        return objects.length == 0 ? str : String.format(str, objects);
    }

    public static String format(CommandSender sender, String key)
    {
        return MoeI18n.format(getCmdSenderLang(sender), key);
    }

    public static String format(CommandSender sender, String key, Object... objects)
    {
        return MoeI18n.format(getCmdSenderLang(sender), key, objects);
    }

    public static String format(String key, Object... objects)
    {
        return MoeI18n.format(consoleLang, key, objects);
    }

    public static String format(String key)
    {
        return MoeI18n.format(consoleLang, key);
    }
}
