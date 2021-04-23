package me.Halflove.DailyRewards.Managers;

import org.bukkit.entity.Player;

public class CooldownManager {
    public static boolean getAllowRewardip(Player p) {
        long millis;
        String ip = p.getAddress().getAddress().getHostAddress();
        ip = ip.replace(".", "-");
        long current = System.currentTimeMillis();
        if (SettingsManager.getConfig().getBoolean("mysql.enabled")) {
            millis = MySQLManager.getCooldownIP(ip);
        } else {
            millis = SettingsManager.getData().getLong(String.valueOf(ip) + ".millis");
        }
        return (current > millis);
    }

    public static boolean getAllowRewardUUID(Player p) {
        long millis, current = System.currentTimeMillis();
        if (SettingsManager.getConfig().getBoolean("mysql.enabled")) {
            millis = MySQLManager.getCooldownUUID(p.getUniqueId());
        } else {
            millis = SettingsManager.getData().getLong(p.getUniqueId() + ".millis");
        }
        return (current > millis);
    }

    public static String formatTime(long secs) {
        String str;
        long seconds = secs % 60L;
        long minutes = secs / 60L;
        long hours = secs / 60L / 60L;
        if (hours != 0L) {
            if (hours > 1L) {
                str = hours + " Hours";
            } else if (minutes > 61L) {
                str = hours + " Hour " + minutes + " Minutes";
            } else if (minutes == 61L) {
                str = hours + " Hour " + minutes + " Minute";
            } else {
                str = hours + " Hour";
            }
        } else if (minutes != 0L) {
            if (seconds == 0L) {
                if (minutes == 1L) {
                    str = minutes + " Minute";
                } else {
                    str = minutes + " Minutes";
                }
            } else if (minutes == 1L) {
                if (seconds == 1L) {
                    str = minutes + " Minute " + seconds + " Second";
                } else {
                    str = minutes + " Minute " + seconds + " Seconds";
                }
            } else if (seconds == 1L) {
                str = minutes + " Minutes " + seconds + " Second";
            } else {
                str = minutes + " Minutes " + seconds + " Seconds";
            }
        } else if (seconds == 1L) {
            str = seconds + " Second";
        } else {
            str = seconds + " Seconds";
        }
        if (secs <= 0L)
            str = "0 Seconds";
        return str;
    }

    public static String getRemainingTime(long millis) {
        long seconds = millis / 1000L;
        return formatTime(seconds);
    }

    public static String getRemainingSec(long millis) {
        long seconds = millis / 1000L;
        return String.valueOf(seconds % 60L);
    }

    public static String getRemainingMin(long millis) {
        long seconds = millis / 1000L;
        long minutes = seconds / 60L;
        return String.valueOf(minutes % 60L);
    }

    public static String getRemainingHour(long millis) {
        long seconds = millis / 1000L;
        long minutes = seconds / 60L;
        return String.valueOf(minutes / 60L);
    }
}

