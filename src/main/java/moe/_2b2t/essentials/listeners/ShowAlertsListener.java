package moe._2b2t.essentials.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import moe._2b2t.essentials.utils.MoeI18n;
import moe._2b2t.essentials.utils.Tools;

public class ShowAlertsListener implements Listener
{
    private static final ShowAlertsListener instance = new ShowAlertsListener();

    private ShowAlertsListener()
    {
        super();
    }

    public static ShowAlertsListener getInstance()
    {
        return instance;
    }

    @EventHandler
    public void onLogin(PlayerJoinEvent event)
    {
        event.setJoinMessage("");
        Player playerJoin = event.getPlayer();
        if (!playerJoin.hasPlayedBefore()) //若是新玩家，则显示欢迎语
        {
            Tools.broadcastMsgWithFilter("showJoinAndLeaveAlerts", "broadcastNewbieWelcomeMsg", playerJoin.getName());
        } else
        {
            Tools.broadcastMsgWithFilter("showJoinAndLeaveAlerts", "broadcastWelcomeMsg", playerJoin.getName());
        }
        playerJoin.sendMessage(MoeI18n.format(playerJoin, "welcomeMsg", playerJoin.getName(), Bukkit.getVersion()));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event)
    {
        event.setQuitMessage("");
        Player playerQuit = event.getPlayer();
        Tools.broadcastMsgWithFilter("showJoinAndLeaveAlerts", "playerQuitMsg", playerQuit.getName());
    }
}
