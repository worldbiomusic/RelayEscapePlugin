package com.wbm.plugin.util.shop;

import org.bukkit.entity.Player;

import com.wbm.plugin.data.PlayerData;
import com.wbm.plugin.util.PlayerDataManager;
import com.wbm.plugin.util.general.BroadcastTool;

public class ShopManager {
	/*
	 * 표지판으로 구매하는 것을 관리 ShopGoods로 굿즈 관리
	 */

	PlayerDataManager pDataManager;

	public ShopManager(PlayerDataManager pDataManager) {
		this.pDataManager = pDataManager;
	}

	public void purchase(Player p, String goodsString, String purchaseType, int cost) {
		PlayerData pData = this.pDataManager.getPlayerData(p.getUniqueId());
		ShopGoods goods = ShopGoods.valueOf(goodsString);

		// 중복(duplicate) check
		if (pData.hasGoods(goods)) {
			BroadcastTool.sendMessage(p, "이미 " + goods.name() + " 굿즈를 가지고 있습니다");
			return;
		}

		// check cost
		if (purchaseType.equalsIgnoreCase("토큰")) {
			// check token
			if (!pData.minusToken(cost)) {
				BroadcastTool.sendMessage(p, "토큰이 부족합니다");
				return;
			}
		} else if (purchaseType.equalsIgnoreCase("캐쉬")) {
			// check cash
			if (!pData.minusCash(cost)) {
				BroadcastTool.sendMessage(p, "캐쉬가 부족합니다");
				return;
			}
		}

		// give goods
		// add goods data to PlayerData
		pData.addGoods(goods);

		// notify
		BroadcastTool.sendMessage(p, goods + " 굿즈를 구매했습니다");
	}
}
