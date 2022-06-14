package moe._2b2t.essentials.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import moe._2b2t.essentials.Moe2b2tEssentials;
import moe._2b2t.essentials.utils.MoeI18n;

public class I18nListener implements Listener
{
    private static final I18nListener instance = new I18nListener();

    private I18nListener()
    {
        super();
    }

    public static I18nListener getInstance()
    {
        return instance;
    }

    //获取玩家语言并且展示相应语言的提示
    @EventHandler
    public void onJoin(PlayerJoinEvent event)
    {
        Bukkit.getScheduler().runTaskLater(Moe2b2tEssentials.getInstance(), () ->
        {
            Player player = event.getPlayer();
            String playerLang = player.getLocale().toLowerCase().replace('-', '_');
            if (MoeI18n.hasLanguage(playerLang))
            {
                player.sendMessage(MoeI18n.format(playerLang, "changeLanguageHint", playerLang));
            }
        }, 5L * 20L);//缓5秒获取玩家语言，避免客户端没发送语言信息时就读取导致每次读到的都是默认的en_us

    }
}
