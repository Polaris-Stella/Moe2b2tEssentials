package moe._2b2t.essentials.listeners.commandmanager;

import org.bukkit.command.Command;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import moe._2b2t.essentials.utils.CmdUtils;

import java.util.Map;
import java.util.Objects;

public class CmdReplaceListener implements Listener
{
    private static final CmdReplaceListener instance = new CmdReplaceListener();

    private boolean enabled;
    private Map<Command, String> originalReplacedMap;

    private CmdReplaceListener()
    {
        super();
    }

    public static CmdReplaceListener getInstance()
    {
        return instance;
    }

    public CmdReplaceListener setEnabled(boolean enabled)
    {
        this.enabled = enabled;
        return this;
    }

    public CmdReplaceListener setOriginalReplacedMap(Map<Command, String> originalReplacedMap)
    {
        this.originalReplacedMap = originalReplacedMap;
        return this;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onCommand(PlayerCommandPreprocessEvent event)
    {
        if (enabled)
        {
            String[] args = event.getMessage().split(" ");
            Command playerExecuted = CmdUtils.getCmdFromMsg(args[0]);
            if (playerExecuted == null)
            {
                return;
            }
            //遍历需要替换的指令列表，并判断这次执行的命令是否需要替换
            originalReplacedMap.forEach((source, target) ->
            {
                if (Objects.equals(playerExecuted, source))
                {
                    args[0] = "/" + target;
                }
            });
            //将拆散的字符串再合并起来
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < args.length; i++)
            {
                stringBuilder.append(args[i]);
                if (!(i >= args.length - 1)) //最后一次循环不加空格，避免在末尾也加上空格
                {
                    stringBuilder.append(" ");
                }
            }
            event.setMessage(stringBuilder.toString());
        }
    }
}
