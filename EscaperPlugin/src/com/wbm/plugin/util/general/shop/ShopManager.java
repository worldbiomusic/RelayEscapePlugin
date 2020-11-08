package com.wbm.plugin.util.general.shop;

import org.bukkit.entity.Player;

import com.wbm.plugin.data.PlayerData;
import com.wbm.plugin.util.PlayerDataManager;
import com.wbm.plugin.util.general.BroadcastTool;

public class ShopManager
{
	/*
	 * 표지판으로 구매하는 것을 관리 
	 * ShopGoods로 굿즈 관리
	 */
	
	PlayerDataManager pDataManager;
	
	public ShopManager(PlayerDataManager pDataManager) {
		this.pDataManager = pDataManager;
	}
	public void purchase(Player p, String goods, int cost) {
		PlayerData pData = this.pDataManager.getOnlinePlayerData(p.getUniqueId());
		
		// check cost
		if(pData.getToken() < cost) {
			BroadcastTool.sendMessage(p, "You don't have enough token");
			return;
		}
		
		// TODO: check duplicate goods
//		if(false) {
//		}
		
		// give goods
		for(ShopGoods g : ShopGoods.values()) {
			if(goods.equals(g.name())) {
				// add to inventory
//				p.getInventory().addItem(g.getGoods());
				// set goods data to PlayerData
				pData.addGoods(g);
			}
		}
		
		// notify
		pData.subToken(cost);
		BroadcastTool.sendMessage(p, "You purchased " + goods);
	}
}


























