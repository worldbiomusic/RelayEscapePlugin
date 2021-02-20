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
import com.wbm.plugin.data.RoomLocation;
import com.wbm.plugin.util.enums.RoomType;
import com.wbm.plugin.util.general.ItemStackTool;
import com.wbm.plugin.util.general.PlayerTool;
import com.wbm.plugin.util.general.PotionEffectTool;
import com.wbm.plugin.util.general.TeleportTool;
import com.wbm.plugin.util.shop.ShopGoods;

public class EventBlockListener {
	public EventBlockListener() {

	}

	public void processEventBlockEvent(PlayerMoveEvent e) {
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
		} 
//		else if (ItemStackTool.isSameWithMaterialNData(ItemStackTool.block2ItemStack(b),
//				ShopGoods.FLICKING.getItemStack())) {
//			FLICKING(p, b);
//		} 
		else if (ItemStackTool.isSameWithMaterialNData(ItemStackTool.block2ItemStack(b),
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
		} else if (ItemStackTool.isSameWithMaterialNData(ItemStackTool.block2ItemStack(b),
				ShopGoods.HEAL.getItemStack())) {
			HEAL(p, b);
		}
	}

	private void JUMPING(Player p, Block b) {
		Location pLoc = p.getLocation().clone();

		double dirX = pLoc.getDirection().multiply(0.05).getX();
		double dirZ = pLoc.getDirection().multiply(0.05).getZ();

//	System.out.println(dirX+":"+dirZ);

		p.setVelocity(new Vector(dirX, 0.65, dirZ));
	}

	private void RESPAWN(Player p, Block b) {
		RoomType roomType = RoomLocation.getRoomTypeWithLocation(b.getLocation());
		Location respawnLoc = RoomLocation.getRoomSpawnLocation(roomType);
		TeleportTool.tp(p, respawnLoc);
	}

	private void TRAP(Player p, Block b) {
		if (p.getActivePotionEffects().size() >= 1) {
			return;
		}

		PotionEffect potion = PotionEffectTool.getRandomDebuffPotionEffect();
		p.addPotionEffect(potion);
	}

	private void FLICKING(Player p, Block b) {
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

	private void SOUND_TERROR(Player p, Block b) {
		p.playSound(p.getLocation(), Sound.ENTITY_ZOMBIE_AMBIENT, 10, 1);
	}

	private void HURT(Player p, Block b) {
		p.setHealth(Math.floor(p.getHealth() - 1));

	}

	private void UP_TP(Player p, Block b) {
		Location upLoc = p.getLocation().add(0, 3, 0);
		// 옥상까지 못 tp 되게
		if (upLoc.getY() <= RoomLocation.MAIN_Pos2.getY() - 1) {
			TeleportTool.tp(p, upLoc);
		}

	}

	private void DOWN_TP(Player p, Block b) {
		Location downLoc = p.getLocation().subtract(0, 3, 0);
		if(downLoc.getY() >= RoomLocation.MAIN_Pos1.getY()) {
			TeleportTool.tp(p, downLoc);
		}
	}

	private void HEAL(Player p, Block b) {
		PlayerTool.heal(p);
		PlayerTool.removeAllPotionEffects(p);
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
