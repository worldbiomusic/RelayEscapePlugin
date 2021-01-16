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
	    BroadcastTool.sendMessage(p, "You already have " + goods.name());
	    return;
	}

	// check cost
	if (purchaseType.equalsIgnoreCase("token")) {
	    // check token
	    if (!pData.minusToken(cost)) {
		BroadcastTool.sendMessage(p, "You don't have enough token");
		return;
	    }
	} else if (purchaseType.equalsIgnoreCase("cash")) {
	    // check cash
	    if (!pData.minusCash(cost)) {
		BroadcastTool.sendMessage(p, "You don't have enough cash");
		return;
	    }
	}

	// give goods
	// add goods data to PlayerData
	pData.addGoods(goods);

	// notify
	BroadcastTool.sendMessage(p, "You purchased " + goods);
    }
}
