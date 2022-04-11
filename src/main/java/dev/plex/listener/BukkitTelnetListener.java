package dev.plex.listener;

import dev.plex.cache.DataUtils;
import dev.plex.player.PlexPlayer;
import dev.plex.rank.enums.Rank;
import dev.plex.util.PlexLog;
import me.totalfreedom.bukkittelnet.BukkitTelnet;
import me.totalfreedom.bukkittelnet.api.TelnetCommandEvent;
import me.totalfreedom.bukkittelnet.api.TelnetPreLoginEvent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;

public class BukkitTelnetListener extends PlexListener
{
    @EventHandler
    public void onPreLogin(TelnetPreLoginEvent event)
    {
        final String ip = event.getIp();
        final PlexPlayer player = DataUtils.getPlayerByIP(ip);
        if (player == null) return;
        if (plugin.getSystem().equalsIgnoreCase("ranks"))
        {
            PlexLog.debug("Plex-BukkitTelnet using ranks check");
            if (player.getRankFromString().isAtLeast(Rank.ADMIN))
            {
                event.setBypassPassword(true);
                event.setName(player.getName());
            }
        } else if (plugin.getSystem().equalsIgnoreCase("permissions"))
        {
            PlexLog.debug("Plex-BukkitTelnet using permissions check");
            if (BukkitTelnet.getPlugin().handler.hasPermission(player.getName(), "plex.telnet.autoconnect"))
            {
                event.setBypassPassword(true);
                event.setName(player.getName());
            }
        }

    }

}
