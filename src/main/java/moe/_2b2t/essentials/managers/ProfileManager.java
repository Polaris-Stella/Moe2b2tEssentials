package moe._2b2t.essentials.managers;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import moe._2b2t.essentials.Moe2b2tEssentials;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

public class ProfileManager
{
    private static final File userDataFile = new File(Moe2b2tEssentials.getInstance().getDataFolder(), "userdata.yml");
    private static FileConfiguration configuration;
    private static Map<String, Object> defaultValues;

    public static void init(Map<String, Object> defaultValues)
    {
        if (!userDataFile.exists())
        {
            try
            {
                userDataFile.createNewFile();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        configuration = YamlConfiguration.loadConfiguration(userDataFile);
        ProfileManager.defaultValues = defaultValues;
    }

    public static void save()
    {
        try
        {
            configuration.save(userDataFile);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static boolean isKeyExists(String key)
    {
        return defaultValues.containsKey(key);
    }

    public static void set(UUID uuid, String key, String value)
    {
        configuration.set(uuid + "." + key, value);
        save();
    }

    public static void set(Player player, String key, String value)
    {
        configuration.set(player.getUniqueId() + "." + key, value);
        save();
    }

    public static String get(UUID uuid, String key)
    {
        String value = configuration.getString(uuid + "." + key);
        return value == null ? defaultValues.get(key).toString() : value;
    }

    public static String get(Player player, String key)
    {
        return get(player.getUniqueId(), key);
    }
}
