package com.teenkung.op2fa;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.java.JavaPlugin;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public final class OP2FA extends JavaPlugin {

    static ArrayList<Player> authorizedWaitPlayers = new ArrayList<>();
    static ArrayList<Player> authorizedPlayers = new ArrayList<>();
    static HashMap<Player, Pendings> pendingPermissions = new HashMap<>();
    static HashMap<Player, PermissionAttachment> permAttachment = new HashMap<>();

    static OP2FA instance;

    @Override
    public void onEnable() {

        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        instance = this;
        startCheckTask();

        Bukkit.getPluginManager().registerEvents(new EventListeners(), this);
        Objects.requireNonNull(getCommand("auth")).setExecutor(new AuthCommand());
    }

    @Override
    public void onDisable() {
        getLogger().severe("Plugin Disabled!");
    }

    private void startCheckTask() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
            for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                if ((player.isOp() || player.hasPermission("*")) && !authorizedPlayers.contains(player) && !authorizedWaitPlayers.contains(player)) {
                    Bukkit.getScheduler().runTask(this, () -> {
                        if ((player.isOp() || player.hasPermission("*")) && !authorizedPlayers.contains(player) && !authorizedWaitPlayers.contains(player)) {
                            if (player.isOp() && player.hasPermission("*")) { pendingPermissions.put(player, Pendings.BOTH); }
                            else if (player.isOp()) { pendingPermissions.put(player, Pendings.OPS); }
                            else if (player.hasPermission("*")) { pendingPermissions.put(player, Pendings.PERMISSION); }


                            PermissionAttachment perm = player.addAttachment(this);
                            player.setOp(false);
                            perm.unsetPermission("*");
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + player.getName() + " permission unset *" );
                            permAttachment.put(player, perm);
                            authorizedWaitPlayers.add(player);

                            player.setGameMode(GameMode.SURVIVAL);

                            player.sendMessage(ChatColor.RED + "Your Operator Status has been removed. Type /auth <password> to gain OP back for this session");
                        }
                    });
                }
            }
        }, 1, 1);
    }

    static String encryptSHA256(String input) {
        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = sha256.digest(input.getBytes());

            // Convert the hashed bytes to hexadecimal representation
            StringBuilder hexBuilder = new StringBuilder();
            for (byte b : hashedBytes) {
                String hex = String.format("%02x", b);
                hexBuilder.append(hex);
            }
            return hexBuilder.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

}
