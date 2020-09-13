package com.wbm.plugin.util.general;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class BroadcastTool
{
	// sendMessage
	public static void sendMessage(Player p, String msg)
	{
		p.sendMessage(msg);
	}

	public static void sendMessage(List<Player> many, String msg)
	{
		for(Player p : many)
		{
			p.sendMessage(msg);
		}
	}

	public static void sendMessageToEveryone(String msg)
	{
		for(Player p : Bukkit.getOnlinePlayers())
		{
			p.sendMessage(msg);
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
}
