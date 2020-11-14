package com.wbm.plugin.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.wbm.plugin.data.PlayerData;
import com.wbm.plugin.util.general.BroadcastTool;
import com.wbm.plugin.util.general.ItemStackTool;
import com.wbm.plugin.util.general.SpawnLocationTool;
import com.wbm.plugin.util.general.shop.ShopGoods;

public class ItemUsingManager implements Listener
{
	PlayerDataManager pDataManager;
	RoomManager roomManager;
	RelayManager relayManager;
	
	public ItemUsingManager(PlayerDataManager pDataManager, RoomManager roomManager, RelayManager relayManager)
	{
		this.pDataManager=pDataManager;
		this.roomManager=roomManager;
		this.relayManager=relayManager;
	}
	@EventHandler
	public void onPlayerUsingItem(PlayerInteractEvent e)
	{
		/*
		 * 정확히 하려연, Room, RelayTime, Role 3개를 다 체크해야 함
		 * => 근데 각 Time에 맞게만 템 지급하므로 안해도 상관없을듯
		 * 
		 * 템 목록: 
		 * [Maker]
		 * stick(발아래 돌 생성)
		 * wood_sword(리스폰)
		 * ACACIA_DOOR_ITEM(home기능)
		 * 
		 * [Tester]
		 * wood_sword(리스폰)
		 * ACACIA_DOOR_ITEM(home기능)
		 * 
		 * [Challenger]
		 * watch(시간 단축)
		 */
		Player p=e.getPlayer();
		ItemStack item = e.getItem();
		
		// TODO: ItemUsingManager 클래스로 관리하기
		// Main room, making time, maker
		if(!(e.getAction()==Action.RIGHT_CLICK_AIR||e.getAction()==Action.RIGHT_CLICK_BLOCK))
		{
			return;
		}
		
//		if(this.relayManager.checkRoomAndRelayTimeAndRole(RoomType.MAIN, RelayTime.MAKING, Role.MAKER, p)) 
//		{
			
			for(ShopGoods goods : ShopGoods.values()) {
				// item이 goods인지 체크
				if(ItemStackTool.isSameWithMateiralNDisplay(item, goods.getGoods()))
				{
					this.useGoods(p, goods);
				}
			}
//		}
	}
	
	void useGoods(Player p, ShopGoods goods) {
		if(goods == ShopGoods.UNDER_BLOCK) {
			// 발밑에 블럭 생성
			Location loc=p.getLocation();
			p.getWorld().getBlockAt(loc).setType(Material.STONE);
		} else if(goods == ShopGoods.SPAWN) {
			// spawn
			p.teleport(SpawnLocationTool.respawnLocation);
		} else if(goods == ShopGoods.ROOM_MANAGER) {
			// room list출력
			this.roomManager.printRoomList(p);
		} else if(goods == ShopGoods.HALF_TIME) {
			// ChallengingTime 남은 시간(1/(player수+1)) 단축
			int leftTime = this.relayManager.getLeftTime();
			int reductionTime = leftTime / (Bukkit.getOnlinePlayers().size() + 1);
			this.relayManager.reduceTime(reductionTime);
			
			// 사용한후에 삭제
			p.getInventory().remove(goods.getGoods());
			
			BroadcastTool.sendMessageToEveryone(reductionTime + " sec reduced by " + p.getName());
		} else if(goods == ShopGoods.BLOCKS) {
			// makingBlock들을 담고 있는 인벤토리 오픈
			Inventory inv = Bukkit.createInventory(null, 54, ShopGoods.BLOCKS.name());
			
			// 기본 MakingBlock제공
			inv.addItem(ShopGoods.GLOWSTONE.getGoods());
			inv.addItem(ShopGoods.DIRT.getGoods());
						
			// 자신이 구입한 Goods(MakingBLock)만 인벤토리에 추가
			for(ShopGoods makingBlock : ShopGoods.getMakingBlocks()) {
				PlayerData pData = this.pDataManager.getPlayerData(p.getUniqueId());
				if(pData.doesHaveGoods(makingBlock)) {
					inv.addItem(makingBlock.getGoods());
				}
			}
			p.openInventory(inv);
		}
	}
}



























