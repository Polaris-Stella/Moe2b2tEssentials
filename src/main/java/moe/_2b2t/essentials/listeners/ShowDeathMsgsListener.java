package moe._2b2t.essentials.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import moe._2b2t.essentials.utils.Tools;

public class ShowDeathMsgsListener implements Listener
{
    private static final ShowDeathMsgsListener instance = new ShowDeathMsgsListener();

    private ShowDeathMsgsListener()
    {
        super();
    }

    public static ShowDeathMsgsListener getInstance()
    {
        return instance;
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event)
    {
        //TODO 增加对本地化的支持
        String deathMsgTemp = event.getDeathMessage();
        event.setDeathMessage("");
        Tools.broadcastMsgWithFilter("showDeathMessages", "playerDeathMsg", deathMsgTemp);
    }
}
