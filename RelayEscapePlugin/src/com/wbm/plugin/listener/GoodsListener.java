package com.wbm.plugin.listener;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
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
import com.wbm.plugin.util.enums.Role;
import com.wbm.plugin.util.general.BroadcastTool;
import com.wbm.plugin.util.general.ChatColorTool;
import com.wbm.plugin.util.general.InventoryTool;
import com.wbm.plugin.util.general.ItemStackTool;
import com.wbm.plugin.util.general.PlayerTool;
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

		Block b = e.getClickedBlock();
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Material mat = b.getType();
			// GLOWSTONE인지 체크
			if (mat.equals(Material.GLOWSTONE)) {
				return;
			}
		}

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

	private Inventory getPlayerGoodsPageInv(Player p, String invTitle, int page, GoodsRole... goodsRoles) {
		// 0~44 Goods 표시
		// 45 ~ 53 페이지 도구 표시
		int goodsIndex = page * 45;
		PlayerData pData = this.pDataManager.getPlayerData(p.getUniqueId());
		List<ShopGoods> goods = pData.getGoods();
//	System.out.println("GOODS SIZE: " + goods.size());

		Inventory inv = Bukkit.createInventory(null, 54, invTitle);

		// 45 46 47 48 49 50 51 52 53
		inv.setItem(48, ItemStackTool.item(Material.LEVER, "이전"));
		inv.setItem(49, ItemStackTool.item(Material.PAPER, "페이지 " + page));
		inv.setItem(50, ItemStackTool.item(Material.REDSTONE_TORCH, "다음"));

		// inventory는 무조건 0~44칸까지만 채워짐
		int invIndex = 0;
		for (int i = goodsIndex; invIndex < 45; i++) {
			if (i >= goods.size()) {
//		System.out.println("I:" +i);
				// 표시할 i인덱스가 플레이어가 가지고 있는 굿즈 갯수보다 많으면 끝내기
				break;
			}
			ShopGoods good = goods.get(i);
//	    System.out.println("["+i+"] GOODS NAME: " + good.name());
			for (GoodsRole goodRole : goodsRoles) {
//		if (good == ShopGoods.SLIME_BLOCK) {
//		    System.out.println("GOOD_ROLE: " + good.goodsRole);
//		    System.out.println("equal goods role: " + goodRole);
//		    System.out.println("t of f: " + good.isGoodsRoleGoods(goodRole));
//		}
				if (good.isGoodsRoleGoods(goodRole)) {
					inv.setItem(invIndex++, good.getItemStack());
					break;
				}
			}
		}

		return inv;
	}

//    private void useAlwaysGoods(Player p, ShopGoods good) {
//	PlayerData pData = this.pDataManager.getPlayerData(p.getUniqueId());
//	switch (good) {
//	case GOODS_LIST:
//	    // goods들을 포함한 GUI inventory 보여주기 (클락 불가능)
//	    Inventory inv = this.getPlayerGoodsListInv(p, 0);
//
//	    p.openInventory(inv);
//	    break;
//	case TOKEN_500:
//	    // token 500 추가
//	    pData.plusToken(500);
//	    // 클릭시 playerData에서 굿즈 제거
//	    pData.removeGoods(ShopGoods.TOKEN_500);
//	    // 클릭시 현재 인벤토리에서 굿즈 제거
//	    InventoryTool.removeItemFromPlayer(p, ShopGoods.TOKEN_500.getItemStack());
//	    // 알림
//	    BroadcastTool.sendMessage(p, "You got 500 Token!");
//	    break;
//	case COLOR_CHAT:
//	    // 알림
//	    ChatColor c = ChatColorTool.random();
//	    p.setDisplayName(c + p.getName() + ChatColor.WHITE);
//	    BroadcastTool.sendMessage(p, "Your name color set to: " + c + c.name());
//	    break;
//	default:
//	}
//    }

//    private void openGUIInventory(Player p, GoodsRole... goodsRoles) {
//
//    }

	@EventHandler
	public void onPlayerClickGoodsListInventory(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		Inventory inv = e.getInventory();
		String invTitle = e.getView().getTitle();
		GoodsRole[] goodsRoles = null;

		// 해당되는 title을 가지고 있는 inventory만 클릭 반응
		if (invTitle.equalsIgnoreCase(ShopGoods.상자.name())) {
			goodsRoles = new GoodsRole[] { GoodsRole.MAKING_BLOCK };
			e.setCancelled(false);
		} else if (invTitle.equalsIgnoreCase("굿즈 컬렉션")) {
			goodsRoles = GoodsRole.values();
			e.setCancelled(true);
		} else if (e.getCurrentItem().equals(ShopGoods.상자.getItemStack())) {
			e.setCancelled(true);
		} else {
			return;
		}

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
			if (displayName.equalsIgnoreCase("이전")) {
				e.setCancelled(true);
				if (page >= 1) {
					// page 0부터 시작함
					page--;
					Inventory nextInv = this.getPlayerGoodsPageInv(p, invTitle, page, goodsRoles);
					p.openInventory(nextInv);
				}
			} else if (displayName.equalsIgnoreCase("다음")) {
				e.setCancelled(true);
				if (inv.getItem(44) != null) {
					// 44칸이 비어있으면 꽉 안 찬 것임
					page++;
					Inventory nextInv = this.getPlayerGoodsPageInv(p, invTitle, page, goodsRoles);
					p.openInventory(nextInv);
				}
			} else if (displayName.contains("페이지")) {
				e.setCancelled(true);
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

//    void useMakingGoods(Player p, ShopGoods good) {
//	if (good == ShopGoods.UNDER_BLOCK) {
//	    PlayerData pData = this.pDataManager.getPlayerData(p.getUniqueId());
//	    // 발밑에 블럭 생성
//	    Location underFootLoc = p.getLocation().clone();
//	    // 높이 제한 검사 (Goods) (4시작인데 4에 놓으면 0이므로 +1 을 해줌)
//	    int blockHigh = (int) (underFootLoc.getY() - ((int) RoomLocation.MAIN_Pos1.getY())) + 1;
//
//	    // HIGH_## 굿즈중에서 가장 높은 굿즈 검색
////	    String kind = ShopGoods.HIGH_10.name().split("_")[0];
//	    String kind = "HIGH";
//	    int allowedHigh = pData.getRoomSettingGoodsHighestValue(kind);
//
//	    // 높이제한 검사
//	    if (blockHigh > allowedHigh) {
//		// 높이제한보다 높을때 취소
//		BroadcastTool.sendMessage(p, "you can place block up to " + allowedHigh);
//		BroadcastTool.sendMessage(p, "HIGH_## Goods can highten your limit");
//		return;
//	    } else {
//		p.getWorld().getBlockAt(underFootLoc).setType(Material.DIRT);
//	    }
//	} else if (good == ShopGoods.SPAWN) {
//	    // spawn
//	    p.teleport(SpawnLocationTool.RESPAWN);
//	} else if (good == ShopGoods.ROOM_MANAGER) {
//	    // room list출력
//	    this.roomManager.printRoomList(p);
//	} else if (good == ShopGoods.CHEST) {
//	    // makingBlock들을 담고 있는 인벤토리 오픈
//	    Inventory inv = Bukkit.createInventory(null, 54, ShopGoods.CHEST.name());
//
//	    // 자신이 구입한 Goods(MakingBLock)만 인벤토리에 추가
//	    for (ShopGoods makingBlock : ShopGoods.getGoodsWithGoodsRole(GoodsRole.MAKING_BLOCK)) {
//		PlayerData pData = this.pDataManager.getPlayerData(p.getUniqueId());
//		if (pData.hasGoods(makingBlock)) {
//		    inv.addItem(makingBlock.getItemStack());
//		}
//	    }
//	    p.openInventory(inv);
//	} else if (good == ShopGoods.FINISH) {
//	    // room finish 실행
//	    if (!this.relayManager.isCorePlaced()) {
//		BroadcastTool.sendMessage(p, "core is not placed");
//		return;
//	    }
//
////	     MakingTime때 최소 60초는 지나야 맵 테스트할 수 있음
//	    int leftTime = this.relayManager.getLeftTime();
//	    int timeLimit = this.relayManager.getCurrentTime().getAmount() - 60;
//
//	    System.out.println("leftTime: " + leftTime);
//	    System.out.println("timeLimit: " + timeLimit);
//	    
//	    if (leftTime > timeLimit) {
//		BroadcastTool.sendMessage(p, "You can use this goods after " + (leftTime - timeLimit) + " sec");
//		return;
//	    }
//
//	    // 위의 상황을 모두 건너면 다음타임 실행
//	    this.relayManager.startNextTime();
//	} else if (good == ShopGoods.BLOCK_CHANGER) {
//	    // 플레이어가 들고있는 굿즈의 lore중의 3번째줄을 true or false로 변경
//	    ItemStack blockChanger = p.getInventory().getItemInMainHand();
//	    ItemMeta meta = blockChanger.getItemMeta();
//	    // lore 조정
//	    List<String> lores = meta.getLore();
//	    String mode = lores.get(2);
//	    if (mode.equalsIgnoreCase("on")) {
//		lores.set(2, "off");
//		BroadcastTool.sendMessage(p, good.name() + " mode set to off");
//	    } else if (mode.equalsIgnoreCase("off")) {
//		lores.set(2, "on");
//		BroadcastTool.sendMessage(p, good.name() + " mode set to on");
//	    }
//	    meta.setLore(lores);
//
//	    // meta 설정
//	    blockChanger.setItemMeta(meta);
//
//	    // main hand에 모드 바뀐것으로 굿즈 체인지
//	    p.getInventory().setItemInMainHand(blockChanger);
//	} else if (good == ShopGoods.HIDE) {
//	    // 플레이어가 들고있는 굿즈의 lore중의 3번째줄을 true or false로 변경
//	    ItemStack hideGoods = p.getInventory().getItemInMainHand();
//	    ItemMeta meta = hideGoods.getItemMeta();
//	    // lore 조정
//	    List<String> lores = meta.getLore();
//	    String mode = lores.get(2);
//	    if (mode.equalsIgnoreCase("on")) {
//		lores.set(2, "off");
//		BroadcastTool.sendMessage(p, good.name() + " mode set to off");
//		// unhide
//		PlayerTool.unhidePlayerFromEveryone(p);
//	    } else if (mode.equalsIgnoreCase("off")) {
//		lores.set(2, "on");
//		BroadcastTool.sendMessage(p, good.name() + " mode set to on");
//		// hide
//		PlayerTool.hidePlayerFromEveryone(p);
//	    }
//	    meta.setLore(lores);
//
//	    // meta 설정
//	    hideGoods.setItemMeta(meta);
//
//	    // main hand에 모드 바뀐것으로 굿즈 체인지
//	    p.getInventory().setItemInMainHand(hideGoods);
//	}
//    }
//
//    private void useTestingGoods(Player p, ShopGoods good) {
//
//    }
//
//    private void useChallengingGoods(Player p, ShopGoods good) {
//	if (good == ShopGoods.REDUCE_TIME) {
//	    // ChallengingTime 남은 시간(1/(player수+1)) 단축
//	    int leftTime = this.relayManager.getLeftTime();
//	    int reductionTime = leftTime / (Bukkit.getOnlinePlayers().size() + 1);
//	    this.relayManager.reduceTime(reductionTime);
//
//	    // 사용한후에 삭제
//	    InventoryTool.removeItemFromPlayer(p, good.getItemStack());
//
//	    BroadcastTool.sendMessageToEveryone(reductionTime + " sec reduced by " + p.getName());
//	} else if (good == ShopGoods.SUPER_STAR) {
//	    // 플레이어가 들고있는 굿즈의 lore중의 3번째줄을 true or false로 변경
//	    ItemStack superStar = p.getInventory().getItemInMainHand();
//	    ItemMeta meta = superStar.getItemMeta();
//	    // lore 조정
//	    List<String> lores = meta.getLore();
//	    String mode = lores.get(2);
//	    if (mode.equalsIgnoreCase("on")) {
//		lores.set(2, "off");
//		BroadcastTool.sendMessage(p, good.name() + " mode set to off");
//		p.setGlowing(false);
//	    } else if (mode.equalsIgnoreCase("off")) {
//		lores.set(2, "on");
//		BroadcastTool.sendMessage(p, good.name() + " mode set to on");
//		p.setGlowing(true);
//	    }
//	    meta.setLore(lores);
//
//	    // meta 설정
//	    superStar.setItemMeta(meta);
//
//	    // main hand에 모드 바뀐것으로 굿즈 체인지
//	    p.getInventory().setItemInMainHand(superStar);
//	}
//    }
//
//    private void useWaitingGoods(Player p, ShopGoods good) {
//
//    }

	private void useGoods(Player p, ShopGoods good) {
		PlayerData pData = this.pDataManager.getPlayerData(p.getUniqueId());
		if (good == ShopGoods.굿즈_컬렉션) {
			// goods들을 포함한 GUI inventory 보여주기 (클락 불가능)
			Inventory inv = this.getPlayerGoodsPageInv(p, "굿즈 컬렉션", 0, GoodsRole.values());
//	    Inventory inv = this.getPlayerGoodsPageInv(p, p.getName() + "굿즈 컬렉션", 0, GoodsRole.MAKING_BLOCK);
			p.openInventory(inv);
		} else if (good == ShopGoods.토큰_500) {
			// token 500 추가
			pData.plusToken(500);
			// 클릭시 playerData에서 굿즈 제거
			pData.removeGoods(ShopGoods.토큰_500);
			// 클릭시 현재 인벤토리에서 굿즈 제거
			InventoryTool.removeItemFromPlayer(p, ShopGoods.토큰_500.getItemStack());
			// 알림
			BroadcastTool.sendMessage(p, "+ 500 토큰!");
		} else if (good == ShopGoods.컬러_채팅) {
			// 알림
			ChatColor c = ChatColorTool.random();
			p.setDisplayName(c + p.getName() + ChatColor.WHITE);
			BroadcastTool.sendMessage(p, "채팅 이름 색깔: " + c + c.name());
		} else if (good == ShopGoods.공중블럭) {
			// 발밑에 블럭 생성
			Location underFootLoc = p.getLocation().clone();
			// 높이 제한 검사 (Goods) (4시작인데 4에 놓으면 0이므로 +1 을 해줌)
			int blockHigh = (int) (underFootLoc.getY() - ((int) RoomLocation.MAIN_Pos1.getY())) + 1;

			// HIGH_## 굿즈중에서 가장 높은 굿즈 검색
//	    String kind = ShopGoods.HIGH_10.name().split("_")[0];
			String kind = "높이제한";
			int allowedHigh = pData.getRoomSettingGoodsHighestValue(kind);

			// 높이제한 검사
			if (blockHigh > allowedHigh) {
				// 높이제한보다 높을때 취소
				BroadcastTool.sendMessage(p, "당신은 " + allowedHigh + " 칸 높이까지만 블럭을 설치할 수 있습니다");
				BroadcastTool.sendMessage(p, "HIGH_## 굿즈로 높이제한을 해제할 수 있습니다");
				return;
			} else {
				p.getWorld().getBlockAt(underFootLoc).setType(Material.DIRT);
			}
		} else if (good == ShopGoods.스폰) {
			// spawn
			p.teleport(RoomLocation.MAIN_SPAWN);
		} else if (good == ShopGoods.맵_관리) {
			// room list출력
			this.roomManager.printRoomList(p);
		} else if (good == ShopGoods.상자) {

			Inventory inv = this.getPlayerGoodsPageInv(p, ShopGoods.상자.name(), 0, GoodsRole.MAKING_BLOCK);
			p.openInventory(inv);

//	    // makingBlock들을 담고 있는 인벤토리 오픈
//	    Inventory inv = Bukkit.createInventory(null, 54, ShopGoods.CHEST.name());
//
//	    // 자신이 구입한 Goods(MakingBLock)만 인벤토리에 추가
//	    for (ShopGoods makingBlock : ShopGoods.getGoodsWithGoodsRole(GoodsRole.MAKING_BLOCK)) {
//		if (pData.hasGoods(makingBlock)) {
//		    inv.addItem(makingBlock.getItemStack());
//		}
//	    }
//	    p.openInventory(inv);
		} else if (good == ShopGoods.테스트) {
			// 명령어 대신 실행
			p.performCommand("re room finish");
		}
//		else if (good == ShopGoods.BLOCK_CHANGER) {
//			// 플레이어가 들고있는 굿즈의 lore중의 3번째줄을 true or false로 변경
//			ItemStack blockChanger = p.getInventory().getItemInMainHand();
//			ItemMeta meta = blockChanger.getItemMeta();
//			// lore 조정
//			List<String> lores = meta.getLore();
//			String mode = lores.get(2);
//			if (mode.equalsIgnoreCase("on")) {
//				lores.set(2, "off");
//				BroadcastTool.sendMessage(p, good.name() + " mode set to off");
//			} else if (mode.equalsIgnoreCase("off")) {
//				lores.set(2, "on");
//				BroadcastTool.sendMessage(p, good.name() + " mode set to on");
//			}
//			meta.setLore(lores);
//
//			// meta 설정
//			blockChanger.setItemMeta(meta);
//
//			// main hand에 모드 바뀐것으로 굿즈 체인지
//			p.getInventory().setItemInMainHand(blockChanger);
//		} 
		else if (good == ShopGoods.은신) {
			// 플레이어가 들고있는 굿즈의 lore중의 3번째줄을 true or false로 변경
			ItemStack hideGoods = p.getInventory().getItemInMainHand();
			ItemMeta meta = hideGoods.getItemMeta();
			// lore 조정
			List<String> lores = meta.getLore();
			String mode = lores.get(2);
			if (mode.equalsIgnoreCase("on")) {
				lores.set(2, "off");
				BroadcastTool.sendMessage(p, good.name() + " 모드가 꺼졌습니다");
				// unhide
				PlayerTool.unhidePlayerFromEveryone(p);
			} else if (mode.equalsIgnoreCase("off")) {
				lores.set(2, "on");
				BroadcastTool.sendMessage(p, good.name() + " 모드가 켜졌습니다");
				// hide
				PlayerTool.hidePlayerFromEveryone(p);
			}
			meta.setLore(lores);

			// meta 설정
			hideGoods.setItemMeta(meta);

			// main hand에 모드 바뀐것으로 굿즈 체인지
			p.getInventory().setItemInMainHand(hideGoods);
		} else if (good == ShopGoods.도전시간_줄이기) {
			if (this.relayManager.isPlayerInPlayerLog(p, "USE_REDUCE_TIME")) {
				BroadcastTool.sendMessage(p, "한개의 맵에는 한번만 사용 가능합니다");
				return;
			}
			// ChallengingTime 남은 시간(1/(player수+1)) 단축
			int leftTime = this.relayManager.getLeftTime();
			int reductionTime = leftTime / (Bukkit.getOnlinePlayers().size() + 1);
			this.relayManager.reduceTime(reductionTime);

			// 사용한후에 삭제
			InventoryTool.removeItemFromPlayer(p, good.getItemStack());

			BroadcastTool.sendMessageToEveryone(reductionTime + "초가 " + p.getName() + " 님에 의해 줄어들었습니다");

			// USE_REDUCE_TIME log 추가
			this.relayManager.addPlayerLog(p, "USE_REDUCE_TIME");
		} else if (good == ShopGoods.슈퍼스타) {
			// 플레이어가 들고있는 굿즈의 lore중의 3번째줄을 true or false로 변경
			ItemStack superStar = p.getInventory().getItemInMainHand();
			ItemMeta meta = superStar.getItemMeta();
			// lore 조정
			List<String> lores = meta.getLore();
			String mode = lores.get(2);
			if (mode.equalsIgnoreCase("on")) {
				lores.set(2, "off");
				BroadcastTool.sendMessage(p, good.name() + " 모드가 꺼졌습니다");
				p.setGlowing(false);
			} else if (mode.equalsIgnoreCase("off")) {
				lores.set(2, "on");
				BroadcastTool.sendMessage(p, good.name() + " 모드가 꺼졌습니다");
				p.setGlowing(true);
			}
			meta.setLore(lores);

			// meta 설정
			superStar.setItemMeta(meta);

			// main hand에 모드 바뀐것으로 굿즈 체인지
			p.getInventory().setItemInMainHand(superStar);
		} else if (good == ShopGoods.겜모변경) {
			GameMode mode = p.getGameMode();
			if (mode == GameMode.SURVIVAL) {
				p.setGameMode(GameMode.CREATIVE);
				BroadcastTool.sendMessage(p, "크리에이티브 모드로 변경되었습니다");
			} else if (mode == GameMode.CREATIVE) {
				p.setGameMode(GameMode.SURVIVAL);
				BroadcastTool.sendMessage(p, "모험가 모드로 변경되었습니다");
			}
		} else if (good == ShopGoods.로비) {
			// 이 굿즈는 역할이 Challenger일때 Waiter로 되어지는 상황일때 사용되는것임
			pData.setRole(Role.웨이터);
			this.relayManager.changeRoom(p);
		} else if (good == ShopGoods.고스트) {
			Bukkit.dispatchCommand(p, "re ghost");
		}
	}
}
