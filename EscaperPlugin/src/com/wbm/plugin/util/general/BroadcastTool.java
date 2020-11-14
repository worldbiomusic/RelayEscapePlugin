package com.wbm.plugin.util.general;

import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;


public class BroadcastTool
{
	// TODO: broadcast해줄때 앞에 "[serverName] " 붙여주는 기능
	public static String serverName;
	
	public static void setServerNamePrefix(String name) {
		serverName = name;
	}
	
	private static String addPrefix(String msg) {
		return serverName + msg;
	}
	
	// sendMessage
	public static void sendMessage(Player p, String msg)
	{
		p.sendMessage(addPrefix(msg));
	}

	public static void sendMessage(List<Player> many, String msg)
	{
		for(Player p : many)
		{
			p.sendMessage(addPrefix(msg));
		}
	}

	public static void sendMessageToEveryone(String msg)
	{
		for(Player p : Bukkit.getOnlinePlayers())
		{
			p.sendMessage(addPrefix(msg));
		}
	}

	// sendTitle
	public static void sendTitle(Player p, String title, String subTitle)
	{
		p.sendTitle(title, subTitle, 1, 3, 1);
	}

	public static void sendTitle(Player p, String title, String subTitle, int fadeIn, int stay, int fadeOut)
	{
		p.sendTitle(title, subTitle, fadeIn, stay, fadeOut);
	}

	public static void sendTitleToEveryone(String title, String subTitle)
	{
		for(Player p : Bukkit.getOnlinePlayers())
		{
			p.sendTitle(title, subTitle, 1, 3, 1);
		}
	}

	public static void sendTitleToEveryone(String title, String subTitle, int fadeIn, int stay, int fadeOut)
	{
		for(Player p : Bukkit.getOnlinePlayers())
		{
			p.sendTitle(title, subTitle, fadeIn, stay, fadeOut);
		}
	}
	
	
	
	
	// Console 전용
	public static void printConsoleMessage(String msg) {
		Bukkit.getConsoleSender().sendMessage(msg);
	}
	
	public static void log(String msg) {
		Bukkit.getLogger().log(Level.INFO, msg);
	}
	
	public static void debug(String msg) {
		Bukkit.getConsoleSender().sendMessage("" + ChatColor.BOLD + ChatColor.RED + "[Debug] " + msg);
	}
	
	
}
























