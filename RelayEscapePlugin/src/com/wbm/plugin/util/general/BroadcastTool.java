package com.wbm.plugin.util.general;

import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.wbm.plugin.util.Setting;

public class BroadcastTool {
    // broadcast해줄때 앞에 "[serverName] " 붙여주는 기능
    public static String prefix = "";

    public static void setMessagePrefix(String name) {
	prefix = name;
    }

    // sendMessage
    public static void sendMessage(Player p, String msg) {
	p.sendMessage(prefix + msg);
    }
    
    // sendMessage
    public static void sendMessage(CommandSender sender, String msg) {
	sender.sendMessage(prefix + msg);
    }

    public static void sendMessage(List<Player> many, String msg) {
	for (Player p : many) {
	    sendMessage(p, msg);
	}
    }

    public static void sendMessageToEveryone(String msg) {
	for (Player p : Bukkit.getOnlinePlayers()) {
	    sendMessage(p, msg);
	}
    }

    public static void sendMessageToEveryoneWithoutPrefix(String msg) {
	for (Player p : Bukkit.getOnlinePlayers()) {
	    p.sendMessage(msg);
	}
    }

    // sendTitle
    public static void sendTitle(Player p, String title, String subTitle, double fadeIn, double stay, double fadeOut) {
	p.sendTitle(title, subTitle, (int) (20 * fadeIn), (int) (20 * stay), (int) (20 * fadeOut));
    }

    public static void sendTitle(Player p, String title, String subTitle) {
	p.sendTitle(title, subTitle, 20 * 1, 20 * 3, 20 * 1);
    }

    public static void sendTitle(Player[] players, String title, String subTitle) {
	for (Player p : players) {
	    p.sendTitle(title, subTitle, 20 * 1, 20 * 3, 20 * 1);
	}
    }

    public static void sendTitle(Player[] players, String title, String subTitle, double fadeIn, double stay,
	    double fadeOut) {
	for (Player p : players) {
	    BroadcastTool.sendTitle(p, title, subTitle, fadeIn, stay, fadeOut);
	}
    }

    public static void sendTitle(List<Player> players, String title, String subTitle) {
	for (Player p : players) {
	    p.sendTitle(title, subTitle, 20 * 1, 20 * 3, 20 * 1);
	}
    }

    public static void sendTitle(List<Player> players, String title, String subTitle, double fadeIn, double stay,
	    double fadeOut) {
	for (Player p : players) {
	    BroadcastTool.sendTitle(p, title, subTitle, fadeIn, stay, fadeOut);
	}
    }

    public static void sendTitleToEveryone(String title, String subTitle) {
	for (Player p : Bukkit.getOnlinePlayers()) {
	    p.sendTitle(title, subTitle, 20 * 1, 20 * 3, 20 * 1);
	}
    }

    public static void sendTitleToEveryone(String title, String subTitle, double fadeIn, double stay, double fadeOut) {
	for (Player p : Bukkit.getOnlinePlayers()) {
	    sendTitle(p, title, subTitle, fadeIn, stay, fadeOut);
	}
    }

//    public static void sendCountDownTitle(Player p, int n) {
//	/*
//	 * n ~ 1까지 delay초마다 카운트 다운
//	 */
//	    Bukkit.getScheduler().runTaskTimer(Main.getInstance(), new Runnable() {
//
//		@Override
//		public void run() {
//		    sendTitle(p, this., "", 0.5, 1, 0.5);
//		}
//	    }, 20 * i);
//    }
//
//    public static void sendCountDownTitleToEveryone(int n) {
//	/*
//	 * 모두에게 n ~ 1까지 delay초마다 카운트 다운
//	 */
//	for (Player p : Bukkit.getOnlinePlayers()) {
//	    sendCountDownTitle(p, n);
//	}
//    }

    // Console 전용
    public static void printConsoleMessage(String msg) {
	Bukkit.getConsoleSender().sendMessage(msg);
    }

    public static void log(String msg) {
	Bukkit.getLogger().log(Level.INFO, msg);
    }

    public static void debug(String msg) {
	if (Setting.DEBUG) {
	    Bukkit.getConsoleSender().sendMessage("" + ChatColor.BOLD + ChatColor.RED + "[Debug] " + msg);
	}
    }

    public static void reportBug(String msg) {
	Bukkit.getConsoleSender().sendMessage("" + ChatColor.BOLD + ChatColor.RED + "[Bug] " + msg);
    }

}
