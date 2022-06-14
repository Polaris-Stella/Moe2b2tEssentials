package moe._2b2t.essentials.listeners.commandmanager;

import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import moe._2b2t.essentials.managers.CmdCooldownManager;
import moe._2b2t.essentials.utils.MoeI18n;
import moe._2b2t.essentials.utils.CmdUtils;

import java.util.Map;

public class CmdCooldownListener implements Listener
{
    private static final CmdCooldownListener instance = new CmdCooldownListener();

    public static CmdCooldownListener getInstance()
    {
        return instance;
    }

    private CmdCooldownListener()
    {
        super();
    }

    private boolean enabled;
    private Map<Command, Integer> cmdCooldownTimeMap;

    public CmdCooldownListener setEnabled(boolean enabled)
    {
        this.enabled = enabled;
        return this;
    }

    public CmdCooldownListener setCmdCooldownTimeMap(Map<Command, Integer> cmdCooldownTimeMap)
    {
        this.cmdCooldownTimeMap = cmdCooldownTimeMap;
        return this;
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event)
    {
        if (enabled)
        {
            Command cmd = CmdUtils.getCmdFromMsg(event.getMessage());
            if (cmdCooldownTimeMap.containsKey(cmd))
            {
                Player player = event.getPlayer();
                CmdCooldownManager manager = CmdCooldownManager.getInstance(player);
                int timeLeft = manager.getTimeLeft(cmd);
                if (timeLeft > 0)
                {
                    event.setCancelled(true);
                    player.sendMessage(MoeI18n.format(player, "cmdCoolingDown", timeLeft));
                } else
                {
                    manager.setCmdCooldown(cmd, cmdCooldownTimeMap.get(cmd));
                }
            }
        }
    }
}
