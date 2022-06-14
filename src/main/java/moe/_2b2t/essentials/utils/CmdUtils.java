package moe._2b2t.essentials.utils;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.SimplePluginManager;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CmdUtils
{
    //获取全部命令，包括Bukkit自带的
    public static Command getCmd(String name)
    {
        name = name.toLowerCase(Locale.ENGLISH);
        Command cmd = Bukkit.getPluginCommand(name);
        if (cmd != null)
        {
            return cmd;
        } else
        {
            Map<String, Command> map = new HashMap<>();
            SimplePluginManager spm = (SimplePluginManager) Bukkit.getPluginManager();
            try
            {
                Field commandMap = SimplePluginManager.class.getDeclaredField("commandMap");
                Field knownCommands = SimpleCommandMap.class.getDeclaredField("knownCommands");
                commandMap.setAccessible(true);
                knownCommands.setAccessible(true);
                cmd = ((Map<String, Command>) knownCommands.get(commandMap.get(spm))).get(name);
                return cmd;
            } catch (ClassCastException | NoSuchFieldException | IllegalAccessException e)
            {
                e.printStackTrace();
                return null;
            }
        }
    }

    public static Command getCmdFromMsg(String msg)
    {
        return getCmd(msg.split(" ")[0].replaceFirst("/", ""));
    }
}
