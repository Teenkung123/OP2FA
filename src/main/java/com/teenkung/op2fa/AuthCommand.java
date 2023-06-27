package com.teenkung.op2fa;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AuthCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            if (args.length != 0 && !OP2FA.authorizedPlayers.contains(player) && OP2FA.authorizedWaitPlayers.contains(player)) {
                if (args[0].equals(OP2FA.instance.getConfig().getString("Password"))) {
                    player.sendMessage(ChatColor.GREEN + "Verify Session Completed!, You can use op now.");
                    OP2FA.authorizedWaitPlayers.remove(player);
                    OP2FA.authorizedPlayers.add(player);
                    OP2FA.permAttachment.remove(player);
                    OP2FA.pendingPermissions.remove(player);
                    if (OP2FA.pendingPermissions.get(player).equals(Pendings.OPS)) {
                        player.setOp(true);
                    } else if (OP2FA.pendingPermissions.get(player).equals(Pendings.PERMISSION)) {
                        OP2FA.permAttachment.get(player).setPermission("*", true);
                    } else if (OP2FA.pendingPermissions.get(player).equals(Pendings.BOTH)) {
                        player.setOp(true);
                        OP2FA.permAttachment.get(player).setPermission("*", true);
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "Invalid Password! Please try again");
                }
            }
        }

        return false;
    }
}
