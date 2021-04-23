package me.Halflove.DailyRewards.Main;

import me.Halflove.DailyRewards.Commands.AdminCommands;
import me.Halflove.DailyRewards.Commands.RewardCommands;
import me.Halflove.DailyRewards.Managers.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main extends JavaPlugin implements Listener {

    public SettingsManager settings = SettingsManager.getInstance();

    public static boolean papi;

    public static Connection connection;

    public static String host;

    public static String database;

    public static String username;

    public static String password;

    public int port;

    public void onEnable() {
        getCommand("dailyrewards").setExecutor((CommandExecutor) new AdminCommands(this));
        getCommand("reward").setExecutor((CommandExecutor) new RewardCommands());
        settings.setup((Plugin) this);
        registerEvents();
        if (SettingsManager.getConfig().getBoolean("mysql.enabled")) {
            mysqlSetup();
            MySQLManager.createTable();
            for (Player player : Bukkit.getOnlinePlayers())
                MySQLManager.createPlayer(player);
        }
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            papi = true;
            (new PAPIExtensions()).register();
        } else {
            papi = false;
        }

        new UpdateChecker(this, 16708).getLatestVersion(version -> {
            if (this.getDescription().getVersion().equalsIgnoreCase(version)) {
                getLogger().info("Plugin is up to date.");
            } else {
                getLogger().severe("*** Daily Rewards is Outdated! ***");
                getLogger().severe("*** You're on " + this.getDescription().getVersion() + " while " + version + " is available! ***");
                getLogger().severe("*** Update Here: https://www.spigotmc.org/resources/daily-rewards.16708/ ***");
            }
        });

    }

    private void registerEvents() {
        Bukkit.getPluginManager().registerEvents((Listener) new JoinManager(), (Plugin) this);
        Bukkit.getPluginManager().registerEvents(this, (Plugin) this);
    }

    public void mysqlSetup() {
        host = SettingsManager.getConfig().getString("mysql.host-name");
        port = SettingsManager.getConfig().getInt("mysql.port");
        database = SettingsManager.getConfig().getString("mysql.database");
        username = SettingsManager.getConfig().getString("mysql.username");
        password = SettingsManager.getConfig().getString("mysql.password");
        try {
            synchronized (this) {
                if (getConnection() != null && !getConnection().isClosed())
                    return;
                Class.forName("com.mysql.jdbc.Driver");
                setConnection(DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database,
                        username, password));
                Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Daily Rewards MySQL: Successfully Connected");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Daily Rewards MySQL: Failed To Connected");
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Daily Rewards MySQL: Error 'SQLException'");
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED
                    + "Daily Rewards MySQL: Your MySQL Configuration Information Is Invalid, Contact Halflove For Support");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Daily Rewards MySQL: Failed To Connected");
            Bukkit.getConsoleSender()
                    .sendMessage(ChatColor.RED + "Daily Rewards MySQL: Error 'ClassNotFoundException'");
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Daily Rewards MySQL: Contact Halflove For Support");
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        Main.connection = connection;
    }

}

