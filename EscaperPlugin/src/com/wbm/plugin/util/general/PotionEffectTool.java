package com.wbm.plugin.util.general;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PotionEffectTool
{
	public static List<PotionEffect> getDebuffPotionEffectList() {
		List<PotionEffect> effects = new ArrayList<>();
		int duration = 20 * 20;
		effects.add(new PotionEffect(PotionEffectType.CONFUSION, duration, 3));
		effects.add(new PotionEffect(PotionEffectType.BLINDNESS, duration, 1));
		effects.add(new PotionEffect(PotionEffectType.POISON, duration, 1));
		effects.add(new PotionEffect(PotionEffectType.HUNGER, duration, 2));
		effects.add(new PotionEffect(PotionEffectType.SLOW, duration, 2));
		return effects;
	}
	public static PotionEffect getRandomDebuffPotionEffect() {
		List<PotionEffect> effects = getDebuffPotionEffectList();
		int r = (int)(Math.random() * effects.size());
		return effects.get(r);
		
	}
	public static void addPotionEffectToAll(PotionEffect effect) {
		for(Player p : Bukkit.getOnlinePlayers()) {
			p.addPotionEffect(effect);
		}
	}
}
