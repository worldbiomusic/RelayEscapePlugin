package com.wbm.plugin.util.general;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlayerTool
{
	public static Collection<? extends Player> onlinePlayers() {
		return Bukkit.getOnlinePlayers();
	}
	
	public static int onlinePlayersCount() {
		return onlinePlayers().size();
	}
	
	public static void heal(Player p) {
		p.setHealth(20);
		p.setExhaustion(0);
	}
}
