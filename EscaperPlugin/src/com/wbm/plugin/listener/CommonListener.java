package com.wbm.plugin.listener;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import com.wbm.plugin.util.PlayerDataManager;
import com.wbm.plugin.util.ShopManager;
import com.wbm.plugin.util.general.BroadcastTool;
import com.wbm.plugin.util.general.InventoryTool;
import com.wbm.plugin.util.general.SpawnLocationTool;

public class CommonListener implements Listener
{
	/*
	 * class 설명:
	 * 어디서나 언제나 해당되는 리스너
	 */
	PlayerDataManager pDataManager;
	ShopManager shopManager;
	
	public CommonListener(PlayerDataManager pDataManager,
			ShopManager shopManager)
	{
		this.pDataManager = pDataManager;
		this.shopManager = shopManager;
	}

	@EventHandler
	public static void onFoodLevelChanged(FoodLevelChangeEvent e)
	{
		e.setCancelled(true);
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e)
	{
		e.getDrops().clear();
	}

	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent e)
	{

		String msg=e.getMessage();

		String translatedMsg="";
		switch (msg)
		{
			case "1":
				translatedMsg="HI";
				break;
			case "2":
				translatedMsg="BYE";
				break;
			case "3":
				translatedMsg="FUXX";
				break;
			case "4":
				translatedMsg="FOLLOW ME";
				break;
			case "5":
				translatedMsg="VOTE";
				break;
			default:
				e.setCancelled(true);
				return;

		}

		e.setMessage(translatedMsg);
	}
	
	@EventHandler
	public void onPlayerDamanged(EntityDamageByEntityEvent e) {
		if(e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
			// 나중에 Maker가 Waiter기다리는것 때리게  만들 예정
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		p.teleport(SpawnLocationTool.joinLocation);
		InventoryTool.clearPlayerInv(p);
	}
	
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent e) {
		e.setRespawnLocation(SpawnLocationTool.respawnLocation);
	}
	
	@EventHandler
	public void onPlayerUseShop(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		Block b = e.getClickedBlock();
		
		// core부술때 바로 없어져서 nullpoiter에러 잡기용 if문
		if(b == null) {
			return;
		}
		
		// sign click
		if(b.getType() == Material.WALL_SIGN || 
				b.getType() == Material.SIGN_POST||
				b.getType() == Material.SIGN) {
			Action act = e.getAction();
			if(act == Action.RIGHT_CLICK_BLOCK || act == Action.RIGHT_CLICK_AIR) {
				Sign sign = (Sign)b.getState();
				String[] lines = sign.getLines();
				
				// SHOP click
				if(lines[0].equals("[SHOP]")) {
					String goods = lines[1];
					int cost = Integer.parseInt(lines[2]);
					this.shopManager.purchase(p, goods, cost);
				}
			}
		}
	}
}



























