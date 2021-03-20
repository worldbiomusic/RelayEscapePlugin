package com.wbm.plugin.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
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
import org.bukkit.event.player.PlayerMoveEvent;
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
import com.wbm.plugin.util.general.ChatColorTool;
import com.wbm.plugin.util.general.InventoryTool;
import com.wbm.plugin.util.general.LocationTool;
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

		// 모든 player는 무조건 waiter로 입장함
		PlayerData pData;

		// PlayerDataManager에 데이터 없는지 확인 (= 서버 처음 들어옴)
		if (this.pDataManager.isFirstJoin(uuid)) {
			String name = p.getName();
			pData = new PlayerData(uuid, name, Role.웨이터);
			pData.plusToken(Setting.FIRST_JOIN_TOKEN);

			// playerDataManager에 데이터 add
			this.pDataManager.addPlayerData(pData);
		}
		// PlayerData가 이미 있을때
		else {
			// role을 waiter로만 변경
			this.pDataManager.getPlayerData(uuid).setRole(Role.웨이터);
		}
	}

	public void reRegisterAllPlayer() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			TODOListWhenplayerJoinServer(p);
		}
	}

	public void TODOListWhenplayerJoinServer(Player p) {
		// PlayerData 관련 처리
		this.processPlayerData(p);

		// 입장시 lobby
		TeleportTool.tp(p, SpawnLocationTool.LOBBY);

		// give basic goods
		// 들어올때마다 실행되야 하는 이유: BASIC_GOODS에 새로운 기본 굿즈가 추가되면 전에 있던 플레이어들도 기본 굿즈를 받아야 하기
		// 때문에
		this.giveBasicGoods(p);

		// 인벤 초기화 후 굿즈 제공
		this.giveGoods(p);

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
//		stageManager.updateAllStage();

	}

	void giveGoods(Player p) {
		InventoryTool.clearPlayerInv(p);
		ShopGoods.giveGoodsToPlayer(pDataManager, p);
		// 예외] REDUCE_TIME은 RelayTIme이 바뀔때 처음에만 지급
		InventoryTool.removeItemFromPlayer(p, ShopGoods.도전시간_줄이기.getItemStack());
	}

	void giveBasicGoods(Player p) {
		/*
		 * 기본굿즈: ShopGoods.CHEST, ShopGoods.HIGH_5, ShopGoods.MAKINGTIME_5
		 */
		PlayerData pData = this.pDataManager.getPlayerData(p.getUniqueId());

		// PlayerData의 goods리스트에 지급
		for (ShopGoods good : Setting.BASIC_GOODS) {
			pData.addGoods(good);
		}

		// 모든 ShopGOods 지급
		for (ShopGoods allGood : ShopGoods.values()) {
			if (allGood == ShopGoods.토큰_500) {
				continue;
			}
			pData.addGoods(allGood);
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
		if (RoomLocation.getRoomTypeWithLocation(b.getLocation()) == RoomType.메인) {
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

		if (pData.isPlayingMiniGame()) {
			this.miniGameManager.processEvent(e);
		}
	}

	private void onPlayerClickBlockInPracticeRoom(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		PlayerData pData = this.pDataManager.getPlayerData(p.getUniqueId());
		Role role = pData.getRole();
//		RelayTime time = this.relayManager.getCurrentTime();
		Block b = e.getClickedBlock();

		if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (RoomLocation.getRoomTypeWithLocation(b.getLocation()) == RoomType.연습) {
				if (role == Role.웨이터) {
					// core 부수면 token 지급
					if (b.getType() == Material.GLOWSTONE) {
						// token 지급
						int token = Setting.PRACTICE_ROOM_CLEAR_TOKEN;

						BroadcastTool.sendMessage(p, "토큰 + " + token);

						// practice room안에 있는 플레이어들 모두 lobby로 이동
						for (Player allP : Bukkit.getOnlinePlayers()) {
							RoomType roomType = RoomLocation.getRoomTypeWithLocation(allP.getLocation());
							if (roomType == RoomType.연습) {
								TeleportTool.tp(allP, SpawnLocationTool.LOBBY);
								// 누가 clear했는지 알려주기
								BroadcastTool.sendMessage(allP, p.getName() + " 님이 연습룸을 클리어했습니다");
							}
						}

						// random room으로 변경
						Room randomRoom = this.roomManager.getRandomRoomData();
						this.roomManager.setRoom(RoomType.연습, randomRoom);
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
		if (RoomLocation.getRoomTypeWithLocation(b.getLocation()) == RoomType.메인) {
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
			if (RoomLocation.getRoomTypeWithLocation(block.getLocation()) == RoomType.메인) {
				// 플레이어가 MAIN, TESTING, TESER 체크
				if (this.relayManager.checkRoomAndRelayTimeAndRole(RoomType.메인, RelayTime.테스팅, Role.테스터, p)) {

					BroadcastTool.sendMessage(p, "맵을 저장했습니다");

//					Bukkit
					// 1.save room, set main room
					String title = this.relayManager.getMainRoomTitle();
					this.roomManager.saveRoomData(RoomType.메인, p.getName(), title);
					Room mainRoom = this.roomManager.getRoomData(title);
					this.roomManager.setRoom(RoomType.메인, mainRoom);

					// 2.next relay 시작
					this.relayManager.startNextTime();
				}
				// 플레이어가 MAIN, CHALLENGING, CHALLENGER 체크
				else if (this.relayManager.checkRoomAndRelayTimeAndRole(RoomType.메인, RelayTime.챌린징,
						Role.챌린저, p)) {
					// resetRelaySettings
					this.relayManager.resetRelaySetting();

					// 1. 현재 Maker에게 이제 Challenger라는 메세지 전송
					// -> core부수면 바로 waitingTime이 시작되므로 relayManager에서 관리

					// 2. 클리어한 maker는 pDataManager의 maker로 등록
					this.pDataManager.registerMaker(p);

					// 3. main room clearCount +1, 제작자에게 토큰 지급
					Room room = this.roomManager.getRoom(RoomType.메인);
					room.addClearCount(1);
					this.roomManager.recordMainRoomDurationTime();
					this.roomManager.plusTokenToRoomMaker(pDataManager, room, this.getRoomMakerToken());
					this.roomManager.setRoomEmpty(RoomType.메인);

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
		int challengingTimeMax = RelayTime.챌린징.getAmount();
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
		if (time == RelayTime.메이킹) {
			Player p = e.getPlayer();
			// maker
			if (this.pDataManager.isMaker(p)) {
				// 기본적으로 부술 수 있게
				e.setCancelled(false);
				e.setDropItems(false);

				// core검사
				if (core.getType() == Material.GLOWSTONE) {
					BroadcastTool.sendMessage(p, "코어가 부숴졌습니다");
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
		if (time == RelayTime.메이킹 && role == Role.메이커) {
			// 기본적으로 설치할수있게
			e.setCancelled(false);

			// 높이 제한 검사 (Goods) (MainRoom공간의 맨밑의 -1 높이로부터 측정)
			int blockHigh = block.getY() - ((int) RoomLocation.MAIN_Pos1.getY() - 1);

			// HIGH_## 굿즈중에서 가장 높은 굿즈 검색
//	    String kind = ShopGoods.HIGH_10.name().split("_")[0];
			String kind = "높이제한";
			int allowedHigh = pData.getRoomSettingGoodsHighestValue(kind);

			// 높이제한 검사
			if (blockHigh > allowedHigh) {
				// 높이제한보다 높을때 취소
				BroadcastTool.sendMessage(p, "당신은 " + allowedHigh + "칸까지 블럭을 설치할 수 있습니다");
				BroadcastTool.sendMessage(p, "높이제한 굿즈로 높이제한을 해제할 수 있습니다");
				e.setCancelled(true);
			} else {
				// 높이보다 낮으면 가능, 그리고 core 체크
				if (block.getType() == Material.GLOWSTONE) {
					if (this.relayManager.isCorePlaced()) {
						// 설치 o 있을때
						BroadcastTool.sendMessage(p, "코어(발광석)가 이미 설치되어있습니다");
						e.setCancelled(true);
					} else {
						// 설치 x 있을때
						BroadcastTool.sendMessage(p, "코어(발광석)가 설치되었습니다(최대 갯수: 1)");
						this.relayManager.setCorePlaced(true);
					}
				}
			}
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		List<String> joinMsg = new ArrayList<>();
		joinMsg.add("반가워요!");
		joinMsg.add("ㅎㅇ!");
		joinMsg.add("안녕하세요!");
		String randomMsg = joinMsg.get((int) (Math.random() * joinMsg.size()));

		BroadcastTool.sendTitle(p, ChatColorTool.random() + randomMsg, "");
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
				if (time == RelayTime.웨이팅 || time == RelayTime.메이킹 || time == RelayTime.테스팅) {
					// msg보내기
					BroadcastTool.sendTitleToEveryone("맵 리셋", "메이커가 서버를 나갔습니다");

					// reset relay
					this.relayManager.resetRelay();
				}
			}
		}
	}

	@EventHandler
	public void onPlayerEnterRoom(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		Location pLoc = p.getLocation();

		if (LocationTool.isIn(Setting.getAbsoluteLocation(12, 4, 11), pLoc, Setting.getAbsoluteLocation(12, 6, 10))
				|| LocationTool.isIn(Setting.getAbsoluteLocation(10, 4, 12), pLoc,
						Setting.getAbsoluteLocation(12, 6, 12))) {

			RelayTime time = this.relayManager.getCurrentTime();
			PlayerData pData = this.pDataManager.getPlayerData(p.getUniqueId());
			Role role = pData.getRole();

			if (time == RelayTime.챌린징 && role == Role.웨이터) {
				BroadcastTool.sendTitle(p, "메인 방", "");

				TeleportTool.tp(p, RoomLocation.MAIN_SPAWN);

				Room room = this.roomManager.getRoom(RoomType.메인);
				if (p.getName().equalsIgnoreCase(room.getMaker())) {
					pData.setRole(Role.뷰어);
					this.relayManager.changeRoom(p);
				} else {
					pData.setRole(Role.챌린저);
					this.relayManager.changeRoom(p);
				}
			}
		}

	}

}
