package moe._2b2t.essentials.listeners.commandmanager;

import org.bukkit.command.Command;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import moe._2b2t.essentials.utils.MoeI18n;
import moe._2b2t.essentials.utils.CmdUtils;

import java.util.List;
import java.util.Objects;

public class CmdWhiteListListener implements Listener
{
    private static final CmdWhiteListListener instance = new CmdWhiteListListener();

    private CmdWhiteListListener()
    {
        super();
    }

    public static CmdWhiteListListener getInstance()
    {
        return instance;
    }

    private boolean enabled;
    private List<Command> whiteList;

    public CmdWhiteListListener setEnabled(boolean enabled)
    {
        this.enabled = enabled;
        return this;
    }

    public CmdWhiteListListener setWhiteList(List<Command> whiteList)
    {
        this.whiteList = whiteList;
        return this;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onCommand(PlayerCommandPreprocessEvent event)
    {
        if (enabled && !event.getPlayer().hasPermission("moe2b2t.commandwhitelist.bypass"))
        {
            Command commandExecuting = CmdUtils.getCmdFromMsg(event.getMessage());
            for (Command whiteListCmd :
                    whiteList)
            {
                if (Objects.equals(whiteListCmd, commandExecuting)) //如果执行的是白名单里的，则返回，不做任何处理
                {
                    return;
                }
            }
            //遍历完后还没有return证明指令不在白名单内，拒绝执行
            event.setCancelled(true);
            event.getPlayer().sendMessage(MoeI18n.format(event.getPlayer(), "cmdProcessRejected"));
        }
    }
}
