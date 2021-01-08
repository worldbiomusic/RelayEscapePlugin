package com.wbm.plugin.util.general;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class SoundTool
{
	public static void playSound(Location loc, Sound sound) {
		loc.getWorld().playSound(loc, sound, 10, 1);
	}
	
	public static void playSound(Player p, Sound sound) {
		p.playSound(p.getLocation(), sound, 10, 1);
	}
}
