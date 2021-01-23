package com.wbm.plugin.listener;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.wbm.plugin.data.PlayerData;
import com.wbm.plugin.data.RoomLocation;
import com.wbm.plugin.util.PlayerDataManager;
import com.wbm.plugin.util.RelayManager;
import com.wbm.plugin.util.RoomManager;
import com.wbm.plugin.util.general.BroadcastTool;
import com.wbm.plugin.util.general.ChatColorTool;
import com.wbm.plugin.util.general.InventoryTool;
import com.wbm.plugin.util.general.ItemStackTool;
import com.wbm.plugin.util.general.PlayerTool;
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
	if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
//	    if (this.relayManager.checkRoomAndRelayTimeAndRole(RoomType.MAIN, RelayTime.MAKING, Role.MAKER, p)
//		    && good.isRoleGood(Role.MAKER)) {
//		this.useMakingGoods(p, good);
//	    } else if (good.isRoleGood(Role.TESTER)) {
//		this.useTestingGoods(p, good);
//	    } else if (good.isRoleGood(Role.CHALLENGER)) {
//		this.useChallengingGoods(p, good);
//	    } else if (good.isRoleGood(Role.WAITER)) {
//		this.useWaitingGoods(p, good);
//	    }
	    
	    

//	    // 굿즈의 GoodsRole이 ALWAYS면 항상 사용할 수 있게
//	    if (good.isGoodsRoleGoods(GoodsRole.ALWAYS)) {
//		this.useAlwaysGoods(p, good);
//	    }
	    
	    this.useGoods(p, good);
	}
    }

    private Inventory getPlayerGoodsListInv(Player p, int page) {
	// 0~44 Goods 표시
	// 45 ~ 53 페이지 도구 표시
	int goodsIndex = page * 45;
	PlayerData pData = this.pDataManager.getPlayerData(p.getUniqueId());
	List<ShopGoods> goods = pData.getGoods();

	String title = p.getName() + "'s Goods List";
	Inventory inv = Bukkit.createInventory(null, 54, title);

	// 45 46 47 48 49 50 51 52 53
	inv.setItem(48, ItemStackTool.item(Material.LEVER, "previous page"));
	inv.setItem(49, ItemStackTool.item(Material.PAPER, "PAGE: " + page));
	inv.setItem(50, ItemStackTool.item(Material.REDSTONE_TORCH_ON, "next page"));

	// inventory는 무조건 0~44칸까지만 채워짐
	int invIndex = 0;
	for (int i = goodsIndex; i < goodsIndex + 45; i++) {
	    if (i >= goods.size()) {
		// 표시할 i인덱스가 플레이어가 가지고 있는 굿즈 갯수보다 많으면 끝내기
		break;
	    }
	    ShopGoods good = goods.get(i);
	    inv.setItem(invIndex++, good.getItemStack());
	}

	return inv;
    }

    private void useAlwaysGoods(Player p, ShopGoods good) {
	PlayerData pData = this.pDataManager.getPlayerData(p.getUniqueId());
	switch (good) {
	case GOODS_LIST:
	    // goods들을 포함한 GUI inventory 보여주기 (클락 불가능)
	    Inventory inv = this.getPlayerGoodsListInv(p, 0);

	    p.openInventory(inv);
	    break;
	case TOKEN_500:
	    // token 500 추가
	    pData.plusToken(500);
	    // 클릭시 playerData에서 굿즈 제거
	    pData.removeGoods(ShopGoods.TOKEN_500);
	    // 클릭시 현재 인벤토리에서 굿즈 제거
	    InventoryTool.removeItemFromPlayer(p, ShopGoods.TOKEN_500.getItemStack());
	    // 알림
	    BroadcastTool.sendMessage(p, "You got 500 Token!");
	    break;
	case COLOR_CHAT:
	    // 알림
	    ChatColor c = ChatColorTool.random();
	    p.setDisplayName(c + p.getName() + ChatColor.WHITE);
	    BroadcastTool.sendMessage(p, "Your name color set to: " + c + c.name());
	    break;
	default:
	}
    }

    @EventHandler
    public void onPlayerClickGoodsListInventory(InventoryClickEvent e) {
	Player p = (Player) e.getWhoClicked();
	String title = p.getName() + "'s Goods List";
	Inventory inv = e.getInventory();
	if (inv.getTitle().equalsIgnoreCase(title)) {
	    e.setCancelled(true);

	    ItemStack item = e.getCurrentItem();
	    if (item == null) {
		return;
	    }
	    ItemMeta meta = item.getItemMeta();
	    if (meta == null) {
		return;
	    }
	    String displayName = meta.getDisplayName();

	    // page 구하기
	    ItemStack pageItem = inv.getItem(49);
	    String pageString = pageItem.getItemMeta().getDisplayName().split(" ")[1];
	    int page = Integer.parseInt(pageString);

	    // previous, next click 구현
	    if (displayName != null) {
		if (displayName.equalsIgnoreCase("previous page")) {
		    if (page >= 1) {
			Inventory preInv = this.getPlayerGoodsListInv(p, page - 1);
			p.openInventory(preInv);
		    }
		} else if (displayName.equalsIgnoreCase("next page")) {

		    if (inv.getItem(44) != null) {
			Inventory nextInv = this.getPlayerGoodsListInv(p, page + 1);
			p.openInventory(nextInv);
		    }
		}
	    }

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

    void useMakingGoods(Player p, ShopGoods good) {
	if (good == ShopGoods.UNDER_BLOCK) {
	    PlayerData pData = this.pDataManager.getPlayerData(p.getUniqueId());
	    // 발밑에 블럭 생성
	    Location underFootLoc = p.getLocation().clone();
	    // 높이 제한 검사 (Goods) (4시작인데 4에 놓으면 0이므로 +1 을 해줌)
	    int blockHigh = (int) (underFootLoc.getY() - ((int) RoomLocation.MAIN_Pos1.getY())) + 1;

	    // HIGH_## 굿즈중에서 가장 높은 굿즈 검색
//	    String kind = ShopGoods.HIGH_10.name().split("_")[0];
	    String kind = "HIGH";
	    int allowedHigh = pData.getRoomSettingGoodsHighestValue(kind);

	    // 높이제한 검사
	    if (blockHigh > allowedHigh) {
		// 높이제한보다 높을때 취소
		BroadcastTool.sendMessage(p, "you can place block up to " + allowedHigh);
		BroadcastTool.sendMessage(p, "HIGH_## Goods can highten your limit");
		return;
	    } else {
		p.getWorld().getBlockAt(underFootLoc).setType(Material.DIRT);
	    }
	} else if (good == ShopGoods.SPAWN) {
	    // spawn
	    p.teleport(SpawnLocationTool.RESPAWN);
	} else if (good == ShopGoods.ROOM_MANAGER) {
	    // room list출력
	    this.roomManager.printRoomList(p);
	} else if (good == ShopGoods.CHEST) {
	    // makingBlock들을 담고 있는 인벤토리 오픈
	    Inventory inv = Bukkit.createInventory(null, 54, ShopGoods.CHEST.name());

	    // 자신이 구입한 Goods(MakingBLock)만 인벤토리에 추가
	    for (ShopGoods makingBlock : ShopGoods.getGoodsWithGoodsRole(GoodsRole.MAKING_BLOCK)) {
		PlayerData pData = this.pDataManager.getPlayerData(p.getUniqueId());
		if (pData.hasGoods(makingBlock)) {
		    inv.addItem(makingBlock.getItemStack());
		}
	    }
	    p.openInventory(inv);
	} else if (good == ShopGoods.FINISH) {
	    // room finish 실행
	    if (!this.relayManager.isCorePlaced()) {
		BroadcastTool.sendMessage(p, "core is not placed");
		return;
	    } else {
		this.relayManager.startNextTime();
	    }
	} else if (good == ShopGoods.BLOCK_CHANGER) {
	    // 플레이어가 들고있는 굿즈의 lore중의 3번째줄을 true or false로 변경
	    ItemStack blockChanger = p.getInventory().getItemInMainHand();
	    ItemMeta meta = blockChanger.getItemMeta();
	    // lore 조정
	    List<String> lores = meta.getLore();
	    String mode = lores.get(2);
	    if (mode.equalsIgnoreCase("on")) {
		lores.set(2, "off");
		BroadcastTool.sendMessage(p, good.name() + " mode set to off");
	    } else if (mode.equalsIgnoreCase("off")) {
		lores.set(2, "on");
		BroadcastTool.sendMessage(p, good.name() + " mode set to on");
	    }
	    meta.setLore(lores);

	    // meta 설정
	    blockChanger.setItemMeta(meta);

	    // main hand에 모드 바뀐것으로 굿즈 체인지
	    p.getInventory().setItemInMainHand(blockChanger);
	} else if (good == ShopGoods.HIDE) {
	    // 플레이어가 들고있는 굿즈의 lore중의 3번째줄을 true or false로 변경
	    ItemStack hideGoods = p.getInventory().getItemInMainHand();
	    ItemMeta meta = hideGoods.getItemMeta();
	    // lore 조정
	    List<String> lores = meta.getLore();
	    String mode = lores.get(2);
	    if (mode.equalsIgnoreCase("on")) {
		lores.set(2, "off");
		BroadcastTool.sendMessage(p, good.name() + " mode set to off");
		// unhide
		PlayerTool.unhidePlayerFromEveryone(p);
	    } else if (mode.equalsIgnoreCase("off")) {
		lores.set(2, "on");
		BroadcastTool.sendMessage(p, good.name() + " mode set to on");
		// hide
		PlayerTool.hidePlayerFromEveryone(p);
	    }
	    meta.setLore(lores);

	    // meta 설정
	    hideGoods.setItemMeta(meta);

	    // main hand에 모드 바뀐것으로 굿즈 체인지
	    p.getInventory().setItemInMainHand(hideGoods);
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
	} else if (good == ShopGoods.SUPER_STAR) {
	    // 플레이어가 들고있는 굿즈의 lore중의 3번째줄을 true or false로 변경
	    ItemStack superStar = p.getInventory().getItemInMainHand();
	    ItemMeta meta = superStar.getItemMeta();
	    // lore 조정
	    List<String> lores = meta.getLore();
	    String mode = lores.get(2);
	    if (mode.equalsIgnoreCase("on")) {
		lores.set(2, "off");
		BroadcastTool.sendMessage(p, good.name() + " mode set to off");
		p.setGlowing(false);
	    } else if (mode.equalsIgnoreCase("off")) {
		lores.set(2, "on");
		BroadcastTool.sendMessage(p, good.name() + " mode set to on");
		p.setGlowing(true);
	    }
	    meta.setLore(lores);

	    // meta 설정
	    superStar.setItemMeta(meta);

	    // main hand에 모드 바뀐것으로 굿즈 체인지
	    p.getInventory().setItemInMainHand(superStar);
	}
    }

    private void useWaitingGoods(Player p, ShopGoods good) {

    }

    private void useGoods(Player p, ShopGoods good) {
	PlayerData pData = this.pDataManager.getPlayerData(p.getUniqueId());
	if (good == ShopGoods.GOODS_LIST) {
	    // goods들을 포함한 GUI inventory 보여주기 (클락 불가능)
	    Inventory inv = this.getPlayerGoodsListInv(p, 0);
	    p.openInventory(inv);
	} else if (good == ShopGoods.TOKEN_500) {
	    // token 500 추가
	    pData.plusToken(500);
	    // 클릭시 playerData에서 굿즈 제거
	    pData.removeGoods(ShopGoods.TOKEN_500);
	    // 클릭시 현재 인벤토리에서 굿즈 제거
	    InventoryTool.removeItemFromPlayer(p, ShopGoods.TOKEN_500.getItemStack());
	    // 알림
	    BroadcastTool.sendMessage(p, "You got 500 Token!");
	} else if (good == ShopGoods.COLOR_CHAT) {
	    // 알림
	    ChatColor c = ChatColorTool.random();
	    p.setDisplayName(c + p.getName() + ChatColor.WHITE);
	    BroadcastTool.sendMessage(p, "Your name color set to: " + c + c.name());
	} else if (good == ShopGoods.UNDER_BLOCK) {
	    // 발밑에 블럭 생성
	    Location underFootLoc = p.getLocation().clone();
	    // 높이 제한 검사 (Goods) (4시작인데 4에 놓으면 0이므로 +1 을 해줌)
	    int blockHigh = (int) (underFootLoc.getY() - ((int) RoomLocation.MAIN_Pos1.getY())) + 1;

	    // HIGH_## 굿즈중에서 가장 높은 굿즈 검색
//	    String kind = ShopGoods.HIGH_10.name().split("_")[0];
	    String kind = "HIGH";
	    int allowedHigh = pData.getRoomSettingGoodsHighestValue(kind);

	    // 높이제한 검사
	    if (blockHigh > allowedHigh) {
		// 높이제한보다 높을때 취소
		BroadcastTool.sendMessage(p, "you can place block up to " + allowedHigh);
		BroadcastTool.sendMessage(p, "HIGH_## Goods can highten your limit");
		return;
	    } else {
		p.getWorld().getBlockAt(underFootLoc).setType(Material.DIRT);
	    }
	} else if (good == ShopGoods.SPAWN) {
	    // spawn
	    p.teleport(SpawnLocationTool.RESPAWN);
	} else if (good == ShopGoods.ROOM_MANAGER) {
	    // room list출력
	    this.roomManager.printRoomList(p);
	} else if (good == ShopGoods.CHEST) {
	    // makingBlock들을 담고 있는 인벤토리 오픈
	    Inventory inv = Bukkit.createInventory(null, 54, ShopGoods.CHEST.name());

	    // 자신이 구입한 Goods(MakingBLock)만 인벤토리에 추가
	    for (ShopGoods makingBlock : ShopGoods.getGoodsWithGoodsRole(GoodsRole.MAKING_BLOCK)) {
		if (pData.hasGoods(makingBlock)) {
		    inv.addItem(makingBlock.getItemStack());
		}
	    }
	    p.openInventory(inv);
	} else if (good == ShopGoods.FINISH) {
	    // room finish 실행
	    if (!this.relayManager.isCorePlaced()) {
		BroadcastTool.sendMessage(p, "core is not placed");
		return;
	    } else {
		this.relayManager.startNextTime();
	    }
	} else if (good == ShopGoods.BLOCK_CHANGER) {
	    // 플레이어가 들고있는 굿즈의 lore중의 3번째줄을 true or false로 변경
	    ItemStack blockChanger = p.getInventory().getItemInMainHand();
	    ItemMeta meta = blockChanger.getItemMeta();
	    // lore 조정
	    List<String> lores = meta.getLore();
	    String mode = lores.get(2);
	    if (mode.equalsIgnoreCase("on")) {
		lores.set(2, "off");
		BroadcastTool.sendMessage(p, good.name() + " mode set to off");
	    } else if (mode.equalsIgnoreCase("off")) {
		lores.set(2, "on");
		BroadcastTool.sendMessage(p, good.name() + " mode set to on");
	    }
	    meta.setLore(lores);

	    // meta 설정
	    blockChanger.setItemMeta(meta);

	    // main hand에 모드 바뀐것으로 굿즈 체인지
	    p.getInventory().setItemInMainHand(blockChanger);
	} else if (good == ShopGoods.HIDE) {
	    // 플레이어가 들고있는 굿즈의 lore중의 3번째줄을 true or false로 변경
	    ItemStack hideGoods = p.getInventory().getItemInMainHand();
	    ItemMeta meta = hideGoods.getItemMeta();
	    // lore 조정
	    List<String> lores = meta.getLore();
	    String mode = lores.get(2);
	    if (mode.equalsIgnoreCase("on")) {
		lores.set(2, "off");
		BroadcastTool.sendMessage(p, good.name() + " mode set to off");
		// unhide
		PlayerTool.unhidePlayerFromEveryone(p);
	    } else if (mode.equalsIgnoreCase("off")) {
		lores.set(2, "on");
		BroadcastTool.sendMessage(p, good.name() + " mode set to on");
		// hide
		PlayerTool.hidePlayerFromEveryone(p);
	    }
	    meta.setLore(lores);

	    // meta 설정
	    hideGoods.setItemMeta(meta);

	    // main hand에 모드 바뀐것으로 굿즈 체인지
	    p.getInventory().setItemInMainHand(hideGoods);
	} else if (good == ShopGoods.REDUCE_TIME) {
	    // ChallengingTime 남은 시간(1/(player수+1)) 단축
	    int leftTime = this.relayManager.getLeftTime();
	    int reductionTime = leftTime / (Bukkit.getOnlinePlayers().size() + 1);
	    this.relayManager.reduceTime(reductionTime);

	    // 사용한후에 삭제
	    InventoryTool.removeItemFromPlayer(p, good.getItemStack());

	    BroadcastTool.sendMessageToEveryone(reductionTime + " sec reduced by " + p.getName());
	} else if (good == ShopGoods.SUPER_STAR) {
	    // 플레이어가 들고있는 굿즈의 lore중의 3번째줄을 true or false로 변경
	    ItemStack superStar = p.getInventory().getItemInMainHand();
	    ItemMeta meta = superStar.getItemMeta();
	    // lore 조정
	    List<String> lores = meta.getLore();
	    String mode = lores.get(2);
	    if (mode.equalsIgnoreCase("on")) {
		lores.set(2, "off");
		BroadcastTool.sendMessage(p, good.name() + " mode set to off");
		p.setGlowing(false);
	    } else if (mode.equalsIgnoreCase("off")) {
		lores.set(2, "on");
		BroadcastTool.sendMessage(p, good.name() + " mode set to on");
		p.setGlowing(true);
	    }
	    meta.setLore(lores);

	    // meta 설정
	    superStar.setItemMeta(meta);

	    // main hand에 모드 바뀐것으로 굿즈 체인지
	    p.getInventory().setItemInMainHand(superStar);
	}
    }
}
