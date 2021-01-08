package com.wbm.plugin.listener;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

import com.wbm.plugin.Main;
import com.wbm.plugin.util.general.ItemStackTool;
import com.wbm.plugin.util.general.PotionEffectTool;
import com.wbm.plugin.util.general.SpawnLocationTool;
import com.wbm.plugin.util.general.TeleportTool;
import com.wbm.plugin.util.shop.ShopGoods;

public class EventBlockListener {
    public static void processEventBlockEvent(PlayerMoveEvent e) {
	Player p = e.getPlayer();
	Block b = p.getLocation().subtract(0, 1, 0).getBlock();

	if (ItemStackTool.isSameWithMaterialNData(ItemStackTool.block2ItemStack(b), ShopGoods.JUMPING.getItemStack())) {
	    JUMPING(p, b);
	} else if (ItemStackTool.isSameWithMaterialNData(ItemStackTool.block2ItemStack(b),
		ShopGoods.RESPAWN.getItemStack())) {
	    RESPAWN(p, b);
	} else if (ItemStackTool.isSameWithMaterialNData(ItemStackTool.block2ItemStack(b),
		ShopGoods.TRAP.getItemStack())) {
	    TRAP(p, b);
	} else if (ItemStackTool.isSameWithMaterialNData(ItemStackTool.block2ItemStack(b),
		ShopGoods.FLICKING.getItemStack())) {
	    FLICKING(p, b);
	} else if (ItemStackTool.isSameWithMaterialNData(ItemStackTool.block2ItemStack(b),
		ShopGoods.SOUND_TERROR.getItemStack())) {
	    SOUND_TERROR(p, b);
	} else if (ItemStackTool.isSameWithMaterialNData(ItemStackTool.block2ItemStack(b),
		ShopGoods.HURT.getItemStack())) {
	    HURT(p, b);
	} else if (ItemStackTool.isSameWithMaterialNData(ItemStackTool.block2ItemStack(b),
		ShopGoods.UP_TP.getItemStack())) {
	    UP_TP(p, b);
	} else if (ItemStackTool.isSameWithMaterialNData(ItemStackTool.block2ItemStack(b),
		ShopGoods.DOWN_TP.getItemStack())) {
	    DOWN_TP(p, b);
	}
    }

    private static void JUMPING(Player p, Block b) {
	p.setVelocity(new Vector(0, 0.65, 0));
    }

    private static void RESPAWN(Player p, Block b) {
	TeleportTool.tp(p, SpawnLocationTool.RESPAWN);
    }

    private static void TRAP(Player p, Block b) {
	if (p.getActivePotionEffects().size() >= 1) {
	    return;
	}

	PotionEffect potion = PotionEffectTool.getRandomDebuffPotionEffect();
	p.addPotionEffect(potion);
    }

    private static void FLICKING(Player p, Block b) {
	Material mat = b.getType();
	@SuppressWarnings("deprecation")
	byte data = b.getData();

	// 3초후 사라짐
	Bukkit.getScheduler().runTaskLater(Main.getInstance(), new Runnable() {

	    @Override
	    public void run() {
		b.setType(Material.AIR);
	    }
	}, 20 * 3);

	// 6초후 나타남
	Bukkit.getScheduler().runTaskLater(Main.getInstance(), new Runnable() {

	    @SuppressWarnings("deprecation")
	    @Override
	    public void run() {
		b.setType(mat);
		b.setData(data);
	    }
	}, 20 * 6);
    }

    private static void SOUND_TERROR(Player p, Block b) {
	p.playSound(p.getLocation(), Sound.ENTITY_ZOMBIE_AMBIENT, 10, 1);
    }

    private static void HURT(Player p, Block b) {
	p.setHealth(Math.floor(p.getHealth() - 1));

    }

    private static void UP_TP(Player p, Block b) {
	Location upLoc = p.getLocation().add(0, 3, 0);
	TeleportTool.tp(p, upLoc);

    }

    private static void DOWN_TP(Player p, Block b) {
	Location downLoc = p.getLocation().subtract(0, 3, 0);
	TeleportTool.tp(p, downLoc);

    }

}

//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
