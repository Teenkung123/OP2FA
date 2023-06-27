package com.teenkung.op2fa;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class EventListeners implements Listener {

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        if (OP2FA.authorizedWaitPlayers.contains(event.getPlayer())) {
            if (!event.getMessage().startsWith("/auth")) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(ChatColor.RED + "Could not run the command, Please run /auth <password> to verify the session.");
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        OP2FA.authorizedWaitPlayers.remove(player);
        OP2FA.authorizedPlayers.remove(player);
        OP2FA.permAttachment.remove(player);
        OP2FA.pendingPermissions.remove(player);
    }
}
