package com.teenkung.op2fa;

import org.bukkit.Bukkit;
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
        if (OP2FA.authorizedWaitPlayers.contains(player)) {
            giveOP(player);
        }
        OP2FA.authorizedWaitPlayers.remove(player);
        OP2FA.authorizedPlayers.remove(player);
        OP2FA.permAttachment.remove(player);
        OP2FA.pendingPermissions.remove(player);
    }

    static void giveOP(Player player) {
        if (OP2FA.pendingPermissions.get(player).equals(Pendings.OPS)) {
            player.setOp(true);
        } else if (OP2FA.pendingPermissions.get(player).equals(Pendings.PERMISSION)) {
            OP2FA.permAttachment.get(player).setPermission("*", true);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + player.getName() + " permission set * true");
        } else if (OP2FA.pendingPermissions.get(player).equals(Pendings.BOTH)) {
            player.setOp(true);
            OP2FA.permAttachment.get(player).setPermission("*", true);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + player.getName() + " permission set * true");
        }
    }
}
