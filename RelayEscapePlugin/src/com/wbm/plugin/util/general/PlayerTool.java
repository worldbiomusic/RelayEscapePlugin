package com.wbm.plugin.util.general;

import java.util.Collection;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import com.wbm.plugin.Main;

public class PlayerTool {
    public static Collection<? extends Player> onlinePlayers() {
	return Bukkit.getOnlinePlayers();
    }

    public static int onlinePlayersCount() {
	return onlinePlayers().size();
    }

    public static void heal(Player p) {
	p.setHealth(20);
	p.setFoodLevel(20);
    }

    public static void setHungry(Player p, int amount) {
	p.setFoodLevel(amount);
    }

    public static void hidePlayerFromAnotherPlayer(Player hideTarget, Player anotherPlayer) {
	anotherPlayer.hidePlayer(Main.getInstance(), hideTarget);
    }

    public static void hidePlayerFromOtherPlayers(Player hideTarget, List<Player> others) {
	for (Player p : others) {
	    p.hidePlayer(Main.getInstance(), hideTarget);
	}
    }

    public static void hidePlayerFromEveryone(Player hideTarget) {
	for (Player p : Bukkit.getOnlinePlayers()) {
	    p.hidePlayer(Main.getInstance(), hideTarget);
	}
    }

    public static void unhidePlayerFromAnotherPlayer(Player unhideTarget, Player anotherPlayer) {
	anotherPlayer.showPlayer(Main.getInstance(), unhideTarget);
    }

    public static void unhidePlayerFromOtherPlayers(Player unhideTarget, List<Player> others) {
	for (Player p : others) {
	    p.showPlayer(Main.getInstance(), unhideTarget);
	}
    }

    public static void unhidePlayerFromEveryone(Player unhideTarget) {
	for (Player p : Bukkit.getOnlinePlayers()) {
	    p.showPlayer(Main.getInstance(), unhideTarget);
	}
    }

    public static void playSoundToEveryone(Sound sound) {
	for (Player p : Bukkit.getOnlinePlayers()) {
	    p.playSound(p.getLocation(), sound, 10, 1);
	}
    }
}
