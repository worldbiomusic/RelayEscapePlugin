package com.wbm.plugin.listener;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.wbm.plugin.data.PlayerData;
import com.wbm.plugin.data.PlayerData.CheckList;
import com.wbm.plugin.data.Room;
import com.wbm.plugin.data.RoomLocation;
import com.wbm.plugin.util.PlayerDataManager;
import com.wbm.plugin.util.RelayManager;
import com.wbm.plugin.util.RoomManager;
import com.wbm.plugin.util.Setting;
import com.wbm.plugin.util.StageManager;
import com.wbm.plugin.util.enums.RelayTime;
import com.wbm.plugin.util.enums.Role;
import com.wbm.plugin.util.enums.RoomType;
import com.wbm.plugin.util.general.BanItemTool;
import com.wbm.plugin.util.general.BroadcastTool;
import com.wbm.plugin.util.general.InventoryTool;
import com.wbm.plugin.util.general.PlayerTool;
import com.wbm.plugin.util.general.SpawnLocationTool;
import com.wbm.plugin.util.general.TeleportTool;
import com.wbm.plugin.util.minigame.MiniGameManager;
import com.wbm.plugin.util.shop.ShopGoods;

public class GameManager implements Listener {
	/*
	 * class 설명: 어디서인지(Room), 언제인지(RelayTime), 역할이 누구인지(Role)이 3단계를 거치고 실행되야 하는
	 * 리스너들(RelayManager에 검사 메소드 있음)
	 */
	PlayerDataManager pDataManager;
	RoomManager roomManager;
	RelayManager relayManager;
	BanItemTool banItems;
	MiniGameManager miniGameManager;
	StageManager stageManager;

	public GameManager(PlayerDataManager pDataManager, RoomManager roomManager, RelayManager relayManager,
			MiniGameManager miniGameManager, StageManager stageManager) {
		this.pDataManager = pDataManager;
		this.roomManager = roomManager;
		this.relayManager = relayManager;
		this.miniGameManager = miniGameManager;
		this.stageManager = stageManager;

		// init
		this.init();
	}

	void init() {
		// 1.resetRelay
		this.relayManager.resetRelay();

		// 2.서버 리로드하면 서버에 남아있는 플레이어들 다시 등록
		this.reRegisterAllPlayer();
	}

	void processPlayerData(Player p) {
		// data 처리
		UUID uuid = p.getUniqueId();

		// 모든 player는 무조건 Challenger or Waiter이고, 각 Time에 맞는 Challenger의 Role로 역할이 배정됨!
		// (w, m, t = Waiter, c = Challenger)
		// (Challeging때 나간 Maker가 다시 들어온 경우 Viewer로)
		PlayerData pData;

		// RelayTime = WAITING or MAKING or TESTING일때는 Waiter
		Role role = Role.WAITER;
		RelayTime time = this.relayManager.getCurrentTime();

		// RelayTime = CHALLENGING일때 Challenger
		if (time == RelayTime.CHALLENGING) {
			role = Role.CHALLENGER;
		}

		// PlayerDataManager에 데이터 없는지 확인 (= 서버 처음 들어옴)
		if (this.pDataManager.isFirstJoin(uuid)) {
			String name = p.getName();
			pData = new PlayerData(uuid, name, role);
			pData.plusToken(50);
		}
		// 전에 들어온적 있을 때(바꿀것은 Role밖에 없음)
		else {
			pData = this.pDataManager.getPlayerData(uuid);

			// 현재 ChallengingTime일때 Room의 Maker와 같으면
			// Role을 Vewer로 바꿔서 자신이 만든룸을 clear못하게 만들어야 함
			if (time == RelayTime.CHALLENGING) {
				Room room = this.roomManager.getRoom(RoomType.MAIN);
				// p가 현재 room maker일 때
				if (pData.getName().equals(room.getMaker())) {
					BroadcastTool.sendMessage(p, "you are Viewer in your room");
					role = Role.VIEWER;
				}
			}
		}

		// playerDataManager에 데이터 add
		this.pDataManager.addPlayerData(pData);

		// role 처리
		pData.setRole(role);
	}

	public void reRegisterAllPlayer() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			TODOListWhenplayerJoinServer(p);
		}
	}

	public void TODOListWhenplayerJoinServer(Player p) {
		// PlayerData 관련 처리
		this.processPlayerData(p);

		// time에 따라서 spawn위치 바꾸기
		if (this.relayManager.getCurrentTime() == RelayTime.CHALLENGING) {
			TeleportTool.tp(p, SpawnLocationTool.JOIN);
		} else { // Challenging 외의 타임일때
			TeleportTool.tp(p, SpawnLocationTool.LOBBY);
		}

		// 인벤 초기화 후 굿즈 제공
		this.giveGoods(p);

		// 기본 굿즈 PlayerData에 추가
		this.giveBasicGoods(p);

		// 상태효과 제거
		PlayerTool.unhidePlayerFromEveryone(p);
		p.setGlowing(false);
		PlayerTool.heal(p);
		PlayerTool.removeAllPotionEffects(p);

		// 기본적인 checkList들 모두 등록 (업데이트 효과)
		for (CheckList list : PlayerData.CheckList.values()) {
			PlayerData pData = this.pDataManager.getPlayerData(p.getUniqueId());
			pData.registerCheckList(list, list.getInitValue());
		}

		// update stage
		stageManager.updateAllStage();

	}

	void giveGoods(Player p) {
		InventoryTool.clearPlayerInv(p);
		ShopGoods.giveGoodsToPleyer(pDataManager, p);
		// 예외] REDUCE_TIME은 RelayTIme이 바뀔때 처음에만 지급
		InventoryTool.removeItemFromPlayer(p, ShopGoods.REDUCE_TIME.getItemStack());
	}

	void giveBasicGoods(Player p) {
		/*
		 * 기본굿즈: ShopGoods.CHEST, ShopGoods.HIGH_5, ShopGoods.MAKINGTIME_5
		 */
		PlayerData pData = this.pDataManager.getPlayerData(p.getUniqueId());
		ShopGoods[] basicGoods = { ShopGoods.DIRT, ShopGoods.GLOWSTONE, ShopGoods.CHEST, ShopGoods.HIGH_5,
				ShopGoods.MAKINGTIME_5, ShopGoods.FINISH, ShopGoods.GOODS_LIST, ShopGoods.SPAWN, ShopGoods.GM_CHANGER };

		// PlayerData의 goods리스트에 지급
		for (ShopGoods good : basicGoods) {
			if (!pData.hasGoods(good)) {
				pData.addGoods(good);
			}
		}
	}

	@EventHandler
	public void onChallengerClickCore(PlayerInteractEvent e) {
		// challenger와 tester는 모험모드여서 클릭대신 상호작용 이벤트 사용
		this.onTesterAndChallengerClickCore(e);
		this.onPlayerClickBlockInPracticeRoom(e);
	}

	@EventHandler
	public void onPlayerBreakBlock(BlockBreakEvent e) {
		// 모든 break event는 여기를 거쳐서 처리됨
		Block b = e.getBlock();

//		// 일단 cancel
		e.setCancelled(true);

		PlayerData pData = this.pDataManager.getPlayerData(e.getPlayer().getUniqueId());

		// Main Room 체크
		if (RoomLocation.getRoomTypeWithLocation(b.getLocation()) == RoomType.MAIN) {
			this.onPlayerBreakBlockInMainRoom(e);
		} else if (pData.isPlayingMiniGame()) {
			// minigame 플레이중일때만 e넘기기
			this.onPlayerBreakBlockInMiniGameRoom(e);
		}

	}

	private void onPlayerBreakBlockInMiniGameRoom(BlockBreakEvent e) {
		Player p = e.getPlayer();
		PlayerData pData = this.pDataManager.getPlayerData(p.getUniqueId());
		Role role = pData.getRole();
		RelayTime time = this.relayManager.getCurrentTime();

		if (time == RelayTime.MAKING || time == RelayTime.TESTING) {
			if (role == Role.WAITER) {
				this.miniGameManager.processEvent(e);
			}
		}
	}

	private void onPlayerClickBlockInPracticeRoom(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		PlayerData pData = this.pDataManager.getPlayerData(p.getUniqueId());
		Role role = pData.getRole();
		RelayTime time = this.relayManager.getCurrentTime();
		Block b = e.getClickedBlock();

		if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (RoomLocation.getRoomTypeWithLocation(b.getLocation()) == RoomType.PRACTICE) {
				if (time == RelayTime.MAKING || time == RelayTime.TESTING) {
					if (role == Role.WAITER) {

						// core 부수면 token 지급
						if (b.getType() == Material.GLOWSTONE) {
							// token 지급
							int token = Setting.PRACTICE_ROOM_CLEAR_TOKEN;

							BroadcastTool.sendMessage(p, "token + " + token);

							// practice room안에 있는 플레이어들 모두 lobby로 이동
							for (Player allP : Bukkit.getOnlinePlayers()) {
								RoomType roomType = RoomLocation.getRoomTypeWithLocation(allP.getLocation());
								if (roomType == RoomType.PRACTICE) {
									TeleportTool.tp(allP, SpawnLocationTool.LOBBY);
									// 누가 clear했는지 알려주기
									BroadcastTool.sendMessage(allP, p.getName() + " clear the PRACTICE room");
								}
							}

							// random room으로 변경
							Room randomRoom = this.roomManager.getRandomRoomData();
							this.roomManager.setRoom(RoomType.PRACTICE, randomRoom);
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onPlayerPlaceBlock(BlockPlaceEvent e) {
		// 모든 place event는 여기를 거쳐서 처리됨
		Block b = e.getBlock();

		// 일단 cancel
		e.setCancelled(true);

		// Main Room 체크
		if (RoomLocation.getRoomTypeWithLocation(b.getLocation()) == RoomType.MAIN) {
			this.onPlayerPlaceBlockInMainRoom(e);
		}
	}

	public void onTesterAndChallengerClickCore(PlayerInteractEvent e) {
		// Tester, Challenger의 core부수는 상황
		Block block = e.getClickedBlock();

		Player p = e.getPlayer();
		UUID pUuid = p.getUniqueId();
		PlayerData pData = this.pDataManager.getPlayerData(pUuid);
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Material mat = block.getType();
			// GLOWSTONE인지 체크
			if (!mat.equals(Material.GLOWSTONE)) {
				return;
			}
			// 블럭의 위치가 MAIN룸 내부인지 체크
			if (RoomLocation.getRoomTypeWithLocation(block.getLocation()) == RoomType.MAIN) {
				// 플레이어가 MAIN, TESTING, TESER 체크
				if (this.relayManager.checkRoomAndRelayTimeAndRole(RoomType.MAIN, RelayTime.TESTING, Role.TESTER, p)) {
					// 1.save room, set main room
					String title = this.relayManager.getMainRoomTitle();
					this.roomManager.saveRoomData(RoomType.MAIN, p.getName(), title);
					Room mainRoom = this.roomManager.getRoomData(title);
					this.roomManager.setRoom(RoomType.MAIN, mainRoom);

					// 2.next relay 시작
					this.relayManager.startNextTime();
				}
				// 플레이어가 MAIN, CHALLENGING, CHALLENGER 체크
				else if (this.relayManager.checkRoomAndRelayTimeAndRole(RoomType.MAIN, RelayTime.CHALLENGING,
						Role.CHALLENGER, p)) {
					// resetRelaySettings
					this.relayManager.resetRelaySetting();

					// 1. 현재 Maker에게 이제 Challenger라는 메세지 전송
					// -> core부수면 바로 waitingTime이 시작되므로 relayManager에서 관리

					// 2. 클리어한 maker는 pDataManager의 maker로 등록
					this.pDataManager.registerMaker(p);

					// 3. main room clearCount +1, time측정 후 초기화, 제작자에게 토큰 지급
					Room room = this.roomManager.getRoom(RoomType.MAIN);
					room.addClearCount(1);
					this.roomManager.recordMainRoomDurationTime();
					this.roomManager.plusTokenToRoomMaker(pDataManager, room, this.getRoomMakerToken());
					this.roomManager.setRoomEmpty(RoomType.MAIN);

					// 4.next relay 시작
					this.relayManager.startNextTime();

					// 5.player token +, clearCount +1
					pData.plusToken(Setting.MAIN_ROOM_CLEAR_TOKEN);
					pData.addClearCount(1);
				}

			}
//	    if (RoomLocation.getRoomTypeWithLocation(block.getLocation()) == RoomType.MAIN) {
//
//		// core체크
//		// Role별로 권한 체크
//		// Time: Challenging / Role: Challenger
//		if (role == Role.CHALLENGER) {
//		    // resetRelaySettings
//		    this.relayManager.resetRelaySetting();
//
//		    // 1. 현재 Maker에게 이제 Challenger라는 메세지 전송
//		    // -> core부수면 바로 waitingTime이 시작되므로 relayManager에서 관리
//
//		    // 2. 클리어한 maker는 pDataManager의 maker로 등록
//		    this.pDataManager.registerMaker(p);
//
//		    // 3. main room clearCount +1, time측정 후 초기화
//		    this.roomManager.getRoom(RoomType.MAIN).addClearCount(1);
//		    this.roomManager.recordMainRoomDurationTime();
//		    this.roomManager.setRoomEmpty(RoomType.MAIN);
//
//		    // 4.next relay 시작
//		    this.relayManager.startNextTime();
//
//		    // 5.player token +, clearCount +1
//		    int token = PlayerTool.onlinePlayersCount() / 2;
//		    pData.plusToken(token);
//		    pData.addClearCount(1);
//		}
//		// Time: Testing / Role: Tester
//		else if (role == Role.TESTER) {
//		    // 1.save room, set main room
//		    String title = this.relayManager.getMainRoomTitle();
//		    this.roomManager.saveRoomData(RoomType.MAIN, p.getName(), title);
//		    Room mainRoom = this.roomManager.getRoomData(title);
//		    this.roomManager.setRoom(RoomType.MAIN, mainRoom);
//
//		    // 2.next relay 시작
//		    this.relayManager.startNextTime();
//		}
//	    }
		}
	}

	int getRoomMakerToken() {
		// maker의 방이 클리어됬을때 사용한 시간이 ChallengingTime의 절반에 가까울 수록 토큰을 많이 얻는다
		// 현재 토큰 기준은 10개
		int tokenMax = 10;
		int challengingTimeMax = RelayTime.CHALLENGING.getAmount();
		int midTime = challengingTimeMax / 2;
		int challengingLeftTime = this.relayManager.getLeftTime();
		// 밑의 공식이 map같은 함수역할
		int roomMakerToken = (int) (tokenMax
				- Math.round(Math.abs(midTime - challengingLeftTime) * ((double) tokenMax / midTime)));

//	System.out.println("challengingTimeMax: " + challengingTimeMax);
//	System.out.println("midTime: " + midTime);
//	System.out.println("challengingLeftTime: " + challengingLeftTime);
//	System.out.println("roomMakerToken: " + roomMakerToken);
		return roomMakerToken;
	}

//	@EventHandler
	public void onPlayerBreakBlockInMainRoom(BlockBreakEvent e) {
		Block core = e.getBlock();
		RelayTime time = this.relayManager.getCurrentTime();

		// making time
		if (time == RelayTime.MAKING) {
			Player p = e.getPlayer();
			// maker
			if (this.pDataManager.isMaker(p)) {
				// 기본적으로 부술 수 있게
				e.setCancelled(false);
				e.setDropItems(false);

				// core검사
				if (core.getType() == Material.GLOWSTONE) {
					BroadcastTool.sendMessage(p, "core is broken");
					this.relayManager.setCorePlaced(false);
				}
			}
		}
	}

	// MakekingTime에서 Maker가 core를 설치했는지 확인 (최대 1개만 설치 가능)
	// priority HIGH 로 높여서 마지막에 검사하게
//	@EventHandler(priority=EventPriority.HIGH)
	public void onPlayerPlaceBlockInMainRoom(BlockPlaceEvent e) {
		Block block = e.getBlock();

		Player p = e.getPlayer();
		PlayerData pData = this.pDataManager.getPlayerData(p.getUniqueId());
		Role role = pData.getRole();
		RelayTime time = this.relayManager.getCurrentTime();
		// making time && maker
		if (time == RelayTime.MAKING && role == Role.MAKER) {
			// 기본적으로 설치할수있게
			e.setCancelled(false);

			// 높이 제한 검사 (Goods) (MainRoom공간의 맨밑의 -1 높이로부터 측정)
			int blockHigh = block.getY() - ((int) RoomLocation.MAIN_Pos1.getY() - 1);

			// HIGH_## 굿즈중에서 가장 높은 굿즈 검색
//	    String kind = ShopGoods.HIGH_10.name().split("_")[0];
			String kind = "HIGH";
			int allowedHigh = pData.getRoomSettingGoodsHighestValue(kind);

			// 높이제한 검사
			if (blockHigh > allowedHigh) {
				// 높이제한보다 높을때 취소
				BroadcastTool.sendMessage(p, "you can place block up to " + allowedHigh);
				BroadcastTool.sendMessage(p, "HIGH_## Goods can highten your limit");
				e.setCancelled(true);
			} else {
				// 높이보다 낮으면 가능, 그리고 core 체크
				if (block.getType() == Material.GLOWSTONE) {
					if (this.relayManager.isCorePlaced()) {
						// 설치 o 있을때
						BroadcastTool.sendMessage(p, "core is already placed");
						e.setCancelled(true);
					} else {
						// 설치 x 있을때
						BroadcastTool.sendMessage(p, "core is placed (max: 1)");
						this.relayManager.setCorePlaced(true);
					}
				}
			}
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		BroadcastTool.sendTitle(p, "RelayEscape", "");
		// PlayerData 처리
		this.TODOListWhenplayerJoinServer(p);
	}

	@EventHandler
	public void onMakerQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();

		if (this.pDataManager.doesMakerExist()) {
			// Maker가 나갔을 때
			if (this.pDataManager.isMaker(p)) {
				RelayTime time = this.relayManager.getCurrentTime();

				// Time = WAITING, MAKING, TESTING일떄 maker가 나가면 resetRelay()
				if (time == RelayTime.WAITING || time == RelayTime.MAKING || time == RelayTime.TESTING) {
					// msg보내기
					BroadcastTool.sendTitleToEveryone("Relay Reset", "Maker quit the server");

					// reset relay
					this.relayManager.resetRelay();
				}
			}
		}
	}

}
