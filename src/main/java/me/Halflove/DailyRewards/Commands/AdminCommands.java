package me.Halflove.DailyRewards.Commands;

import me.Halflove.DailyRewards.Main.Main;
import me.Halflove.DailyRewards.Managers.MySQLManager;
import me.Halflove.DailyRewards.Managers.SettingsManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AdminCommands implements CommandExecutor {
    private final Main plugin;


    public AdminCommands(Main plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(final CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("dailyrewards")) {
            if (sender.isOp() || sender.hasPermission("dr.admin")) {
                if (args.length == 0 || args[0].equalsIgnoreCase("help") || args.length > 2
                        || (!args[0].equalsIgnoreCase("reset") && !args[0].equalsIgnoreCase("reload"))) {
                    sender.sendMessage(ChatColor.BOLD + "DailyRewards Admin Help");
                    sender.sendMessage(ChatColor.YELLOW + "/dr reload" + ChatColor.WHITE + ChatColor.ITALIC
                            + " Reload all DR files.");
                    sender.sendMessage(ChatColor.YELLOW + "/dr reset" + ChatColor.WHITE + ChatColor.ITALIC
                            + " Reset your cooldown.");
                    sender.sendMessage(ChatColor.YELLOW + "/dr reset (player)" + ChatColor.WHITE + ChatColor.ITALIC
                            + " Reset a player's cooldown.");
                    return true;
                }
                if (args[0].equalsIgnoreCase("reload")) {
                    final boolean startmysql;
                    if (!SettingsManager.getConfig().getBoolean("mysql.enabled")) {
                        startmysql = true;
                    } else {
                        startmysql = false;
                    }
                    plugin.settings.reloadData();
                    plugin.settings.reloadConfig();
                    plugin.settings.reloadMsg();
                    SettingsManager.saveData();
                    plugin.settings.saveConfig();
                    plugin.settings.saveMsg();
                    sender.sendMessage(ChatColor.YELLOW + "DailyRewards is reloading...");
                    Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {
                        if (SettingsManager.getConfig().getBoolean("mysql.enabled"))
                            if (startmysql) {
                                plugin.mysqlSetup();
                                MySQLManager.createTable();
                            } else {
                                MySQLManager.createTable();
                            }
                        sender.sendMessage(ChatColor.GREEN + "DailyRewards has been successfully reloaded.");
                    }, 20L);
                }
                if (args[0].equalsIgnoreCase("reset")) {
                    if (sender instanceof Player) {
                        if (args.length == 1) {
                            Player player = (Player) sender;
                            String ip = player.getAddress().getAddress().getHostAddress();
                            ip = ip.replace(".", "-");
                            SettingsManager.getData().set(ip + ".millis", 0);
                            SettingsManager.getData().set(player.getUniqueId() + ".millis", 0);
                            if (SettingsManager.getConfig().getBoolean("mysql.enabled")) {
                                MySQLManager.updateCooldownIP(ip, 0L);
                                MySQLManager.updateCooldownUUID(player.getUniqueId(), 0L);
                            }
                            sender.sendMessage(ChatColor.GREEN + "You reset your cooldown.");
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "Oops, you can't do this in console");
                        sender.sendMessage(ChatColor.RED + "Try '/dr reset (player)' instead");
                    }
                    if (args.length == 2) {
                        Player target = Bukkit.getServer().getPlayer(args[1]);
                        if (target == null) {
                            sender.sendMessage(ChatColor.RED + "The specified player is offline.");
                            return true;
                        }
                        String ip = target.getAddress().getAddress().getHostAddress();
                        ip = ip.replace(".", "-");
                        SettingsManager.getData().set(ip + ".millis", 0);
                        SettingsManager.getData().set(target.getUniqueId() + ".millis", 0);
                        sender.sendMessage(ChatColor.GREEN + "You reset " + target.getName() + "'s cooldown.");
                    }
                }
                return true;
            }
            String msg = SettingsManager.getMsg().getString("no-permission");
            msg = msg.replace("%player", sender.getName());
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
        }
        return true;
    }
}
