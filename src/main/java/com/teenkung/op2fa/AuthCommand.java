package com.teenkung.op2fa;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AuthCommand implements CommandExecutor {
    @SuppressWarnings("NullableProblems")
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            if (args.length != 0 && !OP2FA.authorizedPlayers.contains(player) && OP2FA.authorizedWaitPlayers.contains(player)) {
                if (OP2FA.encryptSHA256(args[0]).equals(OP2FA.instance.getConfig().getString("Password"))) {

                    EventListeners.giveOP(player);
                    OP2FA.authorizedWaitPlayers.remove(player);
                    OP2FA.authorizedPlayers.add(player);
                    OP2FA.permAttachment.remove(player);
                    OP2FA.pendingPermissions.remove(player);
                    player.sendMessage(ChatColor.GREEN + "Verify Session Completed!, You can use op now.");
                } else {
                    player.sendMessage(ChatColor.RED + "Invalid Password! Please try again");
                }
            }
        }

        return false;
    }
}
