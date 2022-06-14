package moe._2b2t.essentials.utils;

import org.bukkit.Bukkit;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NmsViaVersionHelper
{
    public static Object getNmsObject()
    {
        String version;
        Object craftServer;
        try
        {
            version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
            if (getMinecraftVersion() >= 17)
            {
                craftServer = Class.forName("net.minecraft.server.MinecraftServer").getMethod("getServer", new Class[0]).invoke(null, new Object[0]);
            } else
            {
                craftServer = Class.forName("net.minecraft.server." + version + ".MinecraftServer").getMethod("getServer", new Class[0]).invoke(null, new Object[0]);
            }
            return craftServer;
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    private static int getMinecraftVersion()
    {
        try
        {
            Matcher matcher = Pattern.compile("\\(MC: (\\d)\\.(\\d+)\\.?(\\d+?)?\\)").matcher(Bukkit.getVersion());
            if (matcher.find())
                return Integer.parseInt(matcher.toMatchResult().group(2), 10);
            throw new IllegalArgumentException(String.format("No match found in '%s'", new Object[]{Bukkit.getVersion()}));
        } catch (IllegalArgumentException ex)
        {
            throw new RuntimeException("Failed to detect Minecraft version", ex);
        }
    }
}
