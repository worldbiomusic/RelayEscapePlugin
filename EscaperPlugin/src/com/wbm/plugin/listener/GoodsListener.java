package com.wbm.plugin.listener;

import java.util.List;

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
import org.bukkit.inventory.meta.ItemMeta;

import com.wbm.plugin.data.PlayerData;
import com.wbm.plugin.util.PlayerDataManager;
import com.wbm.plugin.util.RelayManager;
import com.wbm.plugin.util.RoomManager;
import com.wbm.plugin.util.enums.RelayTime;
import com.wbm.plugin.util.enums.Role;
import com.wbm.plugin.util.enums.RoomType;
import com.wbm.plugin.util.general.BroadcastTool;
import com.wbm.plugin.util.general.InventoryTool;
import com.wbm.plugin.util.general.ItemStackTool;
import com.wbm.plugin.util.general.SpawnLocationTool;
import com.wbm.plugin.util.shop.GoodsRole;
import com.wbm.plugin.util.shop.ShopGoods;

public class GoodsListener implements Listener {
    PlayerDataManager pDataManager;
    RoomManager roomManager;
    RelayManager relayManager;

    public GoodsListener(PlayerDataManager pDataManager, RoomManager roomManager, RelayManager relayManager) {
	this.pDataManager = pDataManager;
	this.roomManager = roomManager;
	this.relayManager = relayManager;
    }

    @EventHandler
    public void onPlayerUsingItem(PlayerInteractEvent e) {
	/*
	 * 정확히 하려연, Room, RelayTime, Role 3개를 다 체크해야 함 => 근데 각 Time에 맞게만 템 지급하므로 안해도
	 * 상관없을듯
	 * 
	 * 템 목록: [Maker] stick(발아래 돌 생성) wood_sword(리스폰) ACACIA_DOOR_ITEM(home기능)
	 * 
	 * [Tester] wood_sword(리스폰) ACACIA_DOOR_ITEM(home기능)
	 * 
	 * [Challenger] watch(시간 단축)
	 */
	Player p = e.getPlayer();
	PlayerData pData = this.pDataManager.getPlayerData(p.getUniqueId());
	Role role = pData.getRole();
	ItemStack item = e.getItem();
	ShopGoods good = null;

	// item이 goods인지 체크
	for (ShopGoods goods : ShopGoods.values()) {
	    if (ItemStackTool.isSameWithMaterialNDisplay(item, goods.getItemStack())) {
		good = goods;
	    }
	}

	// goods가 아닐시 반환
	if (good == null)
	    return;

	// Main room, making time, maker
	if (!(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)) {
	    return;
	}

	if (this.relayManager.checkRoomAndRelayTimeAndRole(RoomType.MAIN, RelayTime.MAKING, Role.MAKER, p)
		&& good.getGoodsRole() == GoodsRole.MAKING) {
	    this.useMakingGoods(p, good);
	} else if (role == Role.TESTER && good.getGoodsRole() == GoodsRole.TESTING) {
	    this.useTestingGoods(p, good);
	} else if (role == Role.CHALLENGER && good.getGoodsRole() == GoodsRole.CHALLENGING) {
	    this.useChallengingGoods(p, good);
	} else if (role == Role.WAITER && good.getGoodsRole() == GoodsRole.WAITING) {
	    this.useWaitingGoods(p, good);
	}
    }

//    // BLOCK_CHANGER 관련 리스너
//    @EventHandler
//    public void onPlayerUseBlockChangerGoods(PlayerInteractEvent e) {
//	/*
//	 * BLOCK_CHANGER의 모드가 on일때 들고있는 블럭으로 다른 블럭을 우 클릭 했을때 블럭 바꾸기
//	 * 
//	 * [버그]
//	 * 
//	 * -클릭한 블럭에 또 정상적으로 블럭 설치됨
//	 * 
//	 * -코어가 바뀌어지는것 막기
//	 * 
//	 * -룸 외의 것 막기
//	 */
//	Player p = e.getPlayer();
//
//	PlayerData pData = this.pDataManager.getPlayerData(p.getUniqueId());
//	if (pData.doesHaveGoods(ShopGoods.BLOCK_CHANGER)) {
//	    PlayerInventory inv = p.getInventory();
//
//	    ItemStack good = InventoryTool.getItemWithMaterialAndDisplay(p, ShopGoods.BLOCK_CHANGER.getItemStack());
//	    if (good == null) {
//		return;
//	    }
//	    String mode = good.getItemMeta().getLore().get(2);
//	    if (mode.equalsIgnoreCase("on")) {
//		ItemStack srcBlock = inv.getItemInMainHand();
//
//		if (srcBlock == null)
//		    return;
//
//		Block targetBlock = e.getClickedBlock();
//
//		if (targetBlock == null)
//		    return;
//
//		targetBlock.setType(srcBlock.getType());
//	    }
//	}
//    }

    void useMakingGoods(Player p, ShopGoods goods) {
	if (goods == ShopGoods.UNDER_BLOCK) {
	    // 발밑에 블럭 생성
	    Location underFootLoc = p.getLocation();
	    p.getWorld().getBlockAt(underFootLoc).setType(Material.DIRT);
	} else if (goods == ShopGoods.SPAWN) {
	    // spawn
	    p.teleport(SpawnLocationTool.RESPAWN);
	} else if (goods == ShopGoods.ROOM_MANAGER) {
	    // room list출력
	    this.roomManager.printRoomList(p);
	} else if (goods == ShopGoods.CHEST) {
	    // makingBlock들을 담고 있는 인벤토리 오픈
	    Inventory inv = Bukkit.createInventory(null, 54, ShopGoods.CHEST.name());

	    // 기본 MakingBlock제공
	    inv.addItem(ShopGoods.GLOWSTONE.getItemStack());
	    inv.addItem(ShopGoods.DIRT.getItemStack());

	    // 자신이 구입한 Goods(MakingBLock)만 인벤토리에 추가
	    for (ShopGoods makingBlock : ShopGoods.getGoodsRoleGoods(GoodsRole.MAKING_BLOCK)) {
		PlayerData pData = this.pDataManager.getPlayerData(p.getUniqueId());
		if (pData.doesHaveGoods(makingBlock)) {
		    inv.addItem(makingBlock.getItemStack());
		}
	    }
	    p.openInventory(inv);
	} else if (goods == ShopGoods.FINISH) {
	    // room finish 실행
	    if (!this.relayManager.isCorePlaced()) {
		BroadcastTool.sendMessage(p, "core is not placed");
		return;
	    } else {
		this.relayManager.startNextTime();
	    }
	} else if (goods == ShopGoods.BLOCK_CHANGER) {
	    // 플레이어가 들고있는 굿즈의 lore중의 3번째줄을 true or false로 변경
	    ItemStack blockChanger = p.getInventory().getItemInMainHand();
	    ItemMeta meta = blockChanger.getItemMeta();
	    // lore 조정
	    List<String> lores = meta.getLore();
	    String mode = lores.get(2);
	    if (mode.equalsIgnoreCase("on")) {
		lores.set(2, "off");
		BroadcastTool.sendMessage(p, goods.name() + " mode set to off");
	    } else if (mode.equalsIgnoreCase("off")) {
		lores.set(2, "on");
		BroadcastTool.sendMessage(p, goods.name() + " mode set to on");
	    }
	    meta.setLore(lores);

	    // meta 설정
	    blockChanger.setItemMeta(meta);

	    // main hand에 모드 바뀐것으로 굿즈 체인지
	    p.getInventory().setItemInMainHand(blockChanger);
	}
    }

    private void useTestingGoods(Player p, ShopGoods good) {

    }

    private void useChallengingGoods(Player p, ShopGoods good) {
	if (good == ShopGoods.REDUCE_TIME) {
	    // ChallengingTime 남은 시간(1/(player수+1)) 단축
	    int leftTime = this.relayManager.getLeftTime();
	    int reductionTime = leftTime / (Bukkit.getOnlinePlayers().size() + 1);
	    this.relayManager.reduceTime(reductionTime);

	    // 사용한후에 삭제
	    InventoryTool.removeItemFromPlayer(p, good.getItemStack());

	    BroadcastTool.sendMessageToEveryone(reductionTime + " sec reduced by " + p.getName());
	}
    }

    private void useWaitingGoods(Player p, ShopGoods good) {

    }
}
