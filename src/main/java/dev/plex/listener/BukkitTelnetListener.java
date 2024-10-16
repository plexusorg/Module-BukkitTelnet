package dev.plex.listener;

import dev.plex.cache.DataUtils;
import dev.plex.player.PlexPlayer;
import me.totalfreedom.bukkittelnet.BukkitTelnet;
import me.totalfreedom.bukkittelnet.api.TelnetPreLoginEvent;
import org.bukkit.event.EventHandler;

public class BukkitTelnetListener extends PlexListener {
    @EventHandler
    public void onPreLogin(TelnetPreLoginEvent event) {
        final String ip = event.getIp();
        final PlexPlayer player = DataUtils.getPlayerByIP(ip);
        if (player == null) {
            return;
        }

        if (BukkitTelnet.getPlugin().handler.hasPermission(player.getName(), "plex.telnet.autoconnect")) {
            event.setBypassPassword(true);
            event.setName(player.getName());
        }
    }
}
