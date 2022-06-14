package moe._2b2t.essentials.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import moe._2b2t.essentials.Moe2b2tEssentials;
import moe._2b2t.essentials.utils.MoeI18n;

import java.util.*;

public class AntiSpamListener implements Listener
{
    private static final AntiSpamListener instance = new AntiSpamListener();

    private boolean antiRepeatEnabled = false;
    private boolean antiFloodScreenEnabled = false;
    private int minPeriod = 3;
    private List<String> ignoredCharSeq = new ArrayList<>();
    private Set<Player> coolingPlayers = new HashSet<>();
    private Map<Player, String> playerLastMsgMap = new HashMap<>();
    private Map<Player, Set<Player>> playerIgnoredSetMap = new HashMap<>();

    public AntiSpamListener setAntiRepeatEnabled(boolean antiRepeatEnabled)
    {
        this.antiRepeatEnabled = antiRepeatEnabled;
        return this;
    }

    public AntiSpamListener setAntiFloodScreenEnabled(boolean antiFloodScreenEnabled)
    {
        this.antiFloodScreenEnabled = antiFloodScreenEnabled;
        return this;
    }

    public AntiSpamListener setMinPeriod(int minPeriod)
    {
        this.minPeriod = minPeriod;
        return this;
    }

    public AntiSpamListener setIgnoredCharSeq(List<String> ignoredCharSeq)
    {
        this.ignoredCharSeq = ignoredCharSeq;
        return this;
    }

    private AntiSpamListener()
    {
        super();
    }

    public static AntiSpamListener getInstance()
    {
        return instance;
    }

    public boolean isPlayerIgnored(Player source, Player target)
    {
        try
        {
            return playerIgnoredSetMap.get(source).contains(target);
        } catch (NullPointerException e)
        {
            return false;
        }
    }

    public void addIgnoredPlayer(Player source, Player target)
    {
        if (!playerIgnoredSetMap.containsKey(source))
        {
            playerIgnoredSetMap.put(source, new HashSet<>());
        }
        Set<Player> ignoredPlayers = playerIgnoredSetMap.get(source);
        ignoredPlayers.add(target);
    }

    private String removeIgnoredChars(String str)
    {
        for (String ignoredChar : ignoredCharSeq)
        {
            str = str.replace(ignoredChar, "");
        }
        return str;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event)
    {
        Player player = event.getPlayer();
        String name = player.getName();
        String msg = event.getMessage();

        //让屏蔽该玩家的玩家收不到消息
        Set<Player> recipients = event.getRecipients();
        recipients.removeIf(recipient -> isPlayerIgnored(recipient, player));

        //防止说话间隔太快
        if (antiFloodScreenEnabled)
        {
            if (coolingPlayers.contains(player))
            {
                player.sendMessage(MoeI18n.format(player, "speakTooFastMsg", minPeriod));
                event.setCancelled(true);
                return;
            }
            coolingPlayers.add(player);
            Bukkit.getScheduler().runTaskLater(Moe2b2tEssentials.getInstance(), () ->
                    coolingPlayers.remove(player), minPeriod * 20L);
        }

        //防止复读
        if (antiRepeatEnabled)
        {
            String processedMsg = removeIgnoredChars(msg);
            if (processedMsg.equals(playerLastMsgMap.get(player)))
            {
                player.sendMessage(MoeI18n.format(player, "sayTheSameThingMsg"));
                event.setCancelled(true);
                return;
            }
            playerLastMsgMap.put(player, processedMsg);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event)
    {
        playerIgnoredSetMap.remove(event.getPlayer());
    }
}
