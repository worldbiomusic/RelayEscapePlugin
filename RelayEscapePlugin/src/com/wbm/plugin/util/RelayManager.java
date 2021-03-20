
package com.wbm.plugin.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import com.wbm.plugin.Main;
import com.wbm.plugin.data.PlayerData;
import com.wbm.plugin.data.Room;
import com.wbm.plugin.data.RoomLocation;
import com.wbm.plugin.util.enums.RelayTime;
import com.wbm.plugin.util.enums.Role;
import com.wbm.plugin.util.enums.RoomType;
import com.wbm.plugin.util.general.BroadcastTool;
import com.wbm.plugin.util.general.Counter;
import com.wbm.plugin.util.general.InventoryTool;
import com.wbm.plugin.util.general.PlayerTool;
import com.wbm.plugin.util.general.SpawnLocationTool;
import com.wbm.plugin.util.general.TeleportTool;
import com.wbm.plugin.util.minigame.MiniGameManager;
import com.wbm.plugin.util.shop.ShopGoods;

/* TODO: RelayTime의 Making -> Building, Challenging -> Finding으로 변경
 * TODO: ROle의 Maker-> Builder, Challenger -> Finder로 변경
 * 
 * 중요) 기본적인 maker는 PlayerDataManager에 변수로 등록해놓음  
 * 
 * 중요) RelayManager의 변수들과 플레이어의 상태는 매우 의존적으로 되어있으므로 수정할 때 순서가 중요해야 함
 * (최우선 순서) 1:currentTime, 2:player role (다른 작업하기 전에 이 두개의 것을 꼭! 먼저 변경하고 해야 함)
 * 
 * 중요)RelayTimeCommonTODOList에서 this.currentTime = RelayTime.getNextTime(this.currentTime); 사용하면 안되는 이유:
 * 항상 다음것으로 흘러가지 않고 중간에 Making이나 Testing이 실패할 수 도 있는 상황이 있기 떄문에 각자의 메소드에서 직접
 * 설정해줘야 함(예)this.current = RelayTime.TESTING)
 * 
 * [시간에 따른 Maker와 Challenger의 역할 변화]
 * Role: Maker, Tester, Challenger, Viewer, Waiter
 * 
 * Role\RelayTime	Waiting		Making		Testing		Challenging		
 * -Maker			Waiter		Maker		Tester		Viewer		
 * -Challenger		Waiter		Waiter		Waiter		Challenger	
 */

public class RelayManager {
	// 게임의 흐름 (시간, 유저)를 관리하는 클래스
	// 중요) startWaiting(), startMaking(), startChallenging(), startWaiting()
	// 메소드를 절대 직접 호출하지 말기 (시간이 다 되어서 자동으로 넘어갈 때만 사용해야 함)
	// 대신 stopTaskAndStartNextTime() or stopCurrentTimeAndStartAnotherTime() 사용하기!
	// (넘어가기전에 task멈춰야하기 때문)

	private PlayerDataManager pDataManager;
	private RoomManager roomManager;

	// TODO: RelayTime이름보단 다른것찾아보기 예> RelayTurn ??
	private RelayTime currentTime;

	private BukkitTask currentCountDownTask;

	private boolean corePlaced;
	private String mainRoomTitle;

	private Counter timer;
	private int timerTask;

	private BukkitTask reservationTask;

	private MiniGameManager miniGameManager;

	private Map<String, List<Player>> playerLog;

	public RelayManager(PlayerDataManager pDataManager, RoomManager roomManager, MiniGameManager miniGameManager) {
		this.pDataManager = pDataManager;
		this.roomManager = roomManager;

		this.currentTime = RelayTime.챌린징;
		this.corePlaced = false;
		this.timer = new Counter();

		this.miniGameManager = miniGameManager;
		this.playerLog = new HashMap<>();
		this.playerLog.put("USE_REDUCE_TIME", new ArrayList<Player>());
		this.playerLog.put("JOIN_MAIN_ROOM", new ArrayList<Player>());

	}

	// Waiting이 시작하려면 무조건 maker가 등록되어 있어야 함!
	private void startWaiting() {
//	 RelayTime 관리
		this.currentTime = RelayTime.웨이팅;

		// main room 에 있는 사람들 lobby로 tp
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (RoomLocation.getRoomTypeWithLocation(p.getLocation()) == RoomType.메인) {
				PlayerData pData = this.pDataManager.getPlayerData(p.getUniqueId());
				pData.setRole(Role.웨이터);
				this.changeRoom(p);
			}
		}

		// common todo list
		RelayTimeCommonTODOList(this.getMaker());

		// maker(waiter) 관리
		if (this.getMaker() == null) {
			BroadcastTool.printConsoleMessage(ChatColor.RED + "[Bug] No Maker in WaitingTime!!!!");
		}
	}

	private void startMaking() {
		// RelayTime 관리
		this.currentTime = RelayTime.메이킹;

		// make room empty
		this.roomManager.setRoomEmpty(RoomType.메인);

		// room basic title 을 "maker이름 + n"으로 설정
		this.mainRoomTitle = this.roomManager.getNextTitleWithMakerName(this.getMaker().getName());

		// common todo list
		RelayTimeCommonTODOList(this.getMaker());
	}

	private void startTesting() {
		// RelayTime 관리
		this.currentTime = RelayTime.테스팅;

		// common todo list
		RelayTimeCommonTODOList(this.getMaker());
	}

	private void startChallenging() {
		// RelayTime 관리
		this.currentTime = RelayTime.챌린징;

		// main room에 있는 사람들 로비로
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (RoomLocation.getRoomTypeWithLocation(p.getLocation()) == RoomType.메인) {
				PlayerData pData = this.pDataManager.getPlayerData(p.getUniqueId());
				pData.setRole(Role.웨이터);
				TeleportTool.tp(p, SpawnLocationTool.LOBBY);
			}
		}

		this.roomManager.startMainRoomDurationTime();

		// common todo list
		RelayTimeCommonTODOList(this.getMaker());

		// reset playerLog
		this.playerLog.put("USE_REDUCE_TIME", new ArrayList<Player>());
		this.playerLog.put("JOIN_MAIN_ROOM", new ArrayList<Player>());
	}

	private void RelayTimeCommonTODOList(Player p) {
		/*
		 * 순서 중요함: RelayManager는 실제적으로 Maker를 관리하는 PlayerDataManaer에서 변수를 직접받아와서 판단하지만,
		 * 
		 * 다른 클래스와 밑의 메소드들은 Role을 가지고 판단하므로 Role변경을 time변경 다음으로 최우선으로 해야 한다(Role을 time에
		 * 맞게 수정하므로)
		 * 
		 * 대부분 메소드들은 각 플레이어의 Role을 가지고 구분해서 수행함
		 * 
		 * 1.여기서 this.currentTime = RelayTime.getNextTime(this.currentTime); 사용하면 안되는
		 * 이유: 항상 다음것으로 흘러가지 않고 중간에 Making이나 Testing이 실패할 수 도 있는 상황이 있기 떄문에 이 메소드를 실행하기
		 * 전에 각자시간의 메소드에서 직접 설정해줘야 함(예. startTesting()메소드에서 this.current =
		 * RelayTime.TESTING후에 이 메소드 호출)
		 * 
		 * 2.역할 변경
		 * 
		 * 3.maker, challenger 에게 메세지 전송
		 * 
		 * 4.위치 변경
		 * 
		 * 5.인벤토리 관리(초기화 후, Goods 제공)
		 * 
		 * 6.다음 태스크 예약
		 * 
		 * 7.힐
		 * 
		 * 8.소리재생
		 */

		if (p == null) {
			this.reserveNextTask(this.currentTime.getAmount());
			return;
		}

		// 2.역할 변경
		this.changeRoleWithRelayTime(p);

		// 3.maker, everyone에게 메세지 전송
		this.sendMessageWithRole(p);

		// 4.위치 변경
		this.tpWithRole(p);

		// 5.인벤토리 관리(초기화 후, Goods 제공)
		this.initInventoryAndGiveGoods(p);

		// 6.다음 태스크 예약
		this.reserveNextTask(this.currentTime.getAmount());

		// 7.힐 & 상태 효과 제거
		this.healEverything(p);

		// 8.소리재생
		this.playSoundWithRelayTime(p);

	}

	public void changeRoom(Player p) {
		// relayTime이 안바뀌고 자의적으로 main room같은곳 입장할때 처리할것들
		// [주의] 입장전에 플레이어 Role변경후 입장해야 함

		// 3.maker, everyone에게 메세지 전송
		this.sendMessageWithRole(p);

		// 4.위치 변경
		this.tpWithRole(p);
//		TeleportTool.tp(p, RoomLocation.MAIN_SPAWN);

		// 5.인벤토리 관리(초기화 후, Goods 제공)
		this.initInventoryAndGiveGoods(p);

		// 7.힐 & 상태 효과 제거
		this.healEverything(p);

		// 8.소리재생
		this.playSoundWithRelayTime(p);

		// joinLog 체크, REDUCE_TIME 굿즈 체크
		if (!this.isPlayerInPlayerLog(p, "JOIN_MAIN_ROOM")) {
			this.addPlayerLog(p, "JOIN_MAIN_ROOM");
			PlayerData pData = this.pDataManager.getPlayerData(p.getUniqueId());
			pData.addChallengingCount(1);
		}
	}

	void healEverything(Player p) {
		// 힐
		PlayerTool.heal(p);
		// 보이기
		PlayerTool.unhidePlayerFromEveryone(p);
		// 발광효과 제거
		p.setGlowing(false);
	}

	private void playSoundWithRelayTime(Player p) {
		if (this.currentTime == RelayTime.웨이팅) {
			PlayerTool.playSound(p, Sound.BLOCK_END_PORTAL_SPAWN);
		} else if (this.currentTime == RelayTime.메이킹) {
			PlayerTool.playSound(p, Sound.BLOCK_ANVIL_USE);
		} else if (this.currentTime == RelayTime.테스팅) {
			PlayerTool.playSound(p, Sound.BLOCK_ANVIL_DESTROY);
		} else if (this.currentTime == RelayTime.챌린징) {
			PlayerTool.playSound(p, Sound.ENTITY_ENDERMAN_TELEPORT);
		}
	}

	private void initInventoryAndGiveGoods(Player p) {
		InventoryTool.clearPlayerInv(p);
		ShopGoods.giveGoodsToPlayer(pDataManager, p);
	}

	private void reserveNextTask(int durationTime) {
		RelayTime currentTime = getCurrentTime();

		// MakingTime 일때 goods(MAKINGTIME_##)검사해서 추가시간 적용
		if (currentTime == RelayTime.메이킹) {
			// TIME_NN 굿즈 검사해서 MakingTime 조절
			PlayerData pData = this.pDataManager.getPlayerData(this.getMaker().getUniqueId());
			durationTime = 60 * pData.getRoomSettingGoodsHighestValue("제작시간");
		}

		// task 예약
		this.reservationTask = this.currentCountDownTask = Bukkit.getScheduler().runTaskLater(Main.getInstance(),
				new Runnable() {
					@Override
					public void run() {
						// time에 따른 실행
						if (currentTime == RelayTime.웨이팅) {
							startNextTime();
						} else if (currentTime == RelayTime.메이킹) {
							if (!isCorePlaced()) {
								BroadcastTool.sendMessageToEveryone("메이커가 맵 제작을 실패했습니다");
								resetRelay();
							} else {
								startNextTime();
							}
						} else if (currentTime == RelayTime.테스팅) {
							// reset relay
							BroadcastTool.sendMessageToEveryone("메이커가 맵 테스트를 실패했습니다");
							resetRelay();
						} else if (currentTime == RelayTime.챌린징) {
							BroadcastTool.sendMessageToEveryone("아무도 맵을 클리어하지 못했습니다");

							resetRelay();
						}
					}
				}, 20 * durationTime);

		// timer 시작
		this.startNewCountDownTimer(durationTime);
	}

	public List<Player> getChallengers() {
		List<Player> challengers = new ArrayList<Player>(Bukkit.getOnlinePlayers());

		// maker가 있을때만 maker 제거
		if (this.pDataManager.doesMakerExist()) {
			Player maker = this.getMaker();
			challengers.remove(maker);
		}

		return challengers;
	}

	// this.pDataManager.getMaker()가 너무 길어서 만든 메소드
	// this.pDataManager.getMaker() <- 여기서만 참조해야 데이터 무결성이 보장됨
	public Player getMaker() {
		return this.pDataManager.getMaker();
	}

	private void stopCurrentTime() {
		if (this.currentCountDownTask != null) {
			this.currentCountDownTask.cancel();
		}
	}

	// 일반적으로 자연스러운 Time flow (시간이 다 됬을때 or 조건이 만족되었을때(명령어))
	// 다음Time 조건 검사도 해줌
	public void startNextTime() {
		// 먼저 현재 time task 중지
		this.stopCurrentTime();

		RelayTime time = this.currentTime;

		if (time == RelayTime.웨이팅) {
			this.startMaking();
		} else if (time == RelayTime.메이킹) {
			this.startTesting();
		} else if (time == RelayTime.테스팅) {
			// core부수면 바로 시작
			this.startChallenging();
		} else if (time == RelayTime.챌린징) {
			this.startWaiting();
		}

//	this.startNewCountDownTimer(RelayTime.getNextTime(time));
	}

	// 예외적인 상황이 발생했을 때 사용 (Maker가 방을 중간에 나가거나 or 조건이 불만족되었을때)
	public void startAnotherTime(RelayTime anotherTime) {
		// 먼저 현재 time task 중지
		this.stopCurrentTime();

		if (anotherTime == RelayTime.웨이팅) {
			this.startWaiting();
		} else if (anotherTime == RelayTime.메이킹) {
			this.startMaking();
		} else if (anotherTime == RelayTime.테스팅) {
			this.startTesting();
		} else if (anotherTime == RelayTime.챌린징) {
			this.startChallenging();
		}

//	this.startNewCountDownTimer(anotherTime);
	}

	public void resetRelay() {
		// resetSettings
		this.resetRelaySetting();

		// reset message
		if (Setting.DEBUG) {
			BroadcastTool.sendMessageToEveryone(ChatColor.RED + "relay reset!");
		}

		// Room 초기화
		Room randomRoom = this.roomManager.getRandomRoomData();
		this.roomManager.setRoom(RoomType.메인, randomRoom);
		this.roomManager.setRoom(RoomType.연습, randomRoom);

		// RelayTime set to CHALLENGING
		this.startAnotherTime(RelayTime.챌린징);

		BroadcastTool.sendMessageToEveryone("새로운 맵이 등장합니다");
	}

	public void resetRelaySetting() {
		// corePlaced 초기화
		this.corePlaced = false;

		// MainRoom에 있는 maker -> waiter로 변경 (룸 만들기 실패한 메이커 대상 실행하는 부분)
		if (this.pDataManager.doesMakerExist()) {
			PlayerData makerPData = this.pDataManager.getPlayerData(this.getMaker().getUniqueId());
			// title이 time에 전송되야 되서... 어쩔수없이 변경해봄
			this.currentTime = RelayTime.챌린징;
			makerPData.setRole(Role.웨이터);
			this.changeRoom(this.getMaker());
		}

//			PlayerDataManager maker = null 처리
		this.pDataManager.unregisterMaker();
	}

	public RelayTime getCurrentTime() {
		return currentTime;
	}

	public void setCurrentTime(RelayTime currentTime) {
		this.currentTime = currentTime;
	}

	public BukkitTask getCurrentCountDownTask() {
		return currentCountDownTask;
	}

	public void setCurrentCountDownTask(BukkitTask currentCountDownTask) {
		this.currentCountDownTask = currentCountDownTask;
	}

	public boolean isCorePlaced() {
		return corePlaced;
	}

	public void setCorePlaced(boolean corePlaced) {
		this.corePlaced = corePlaced;
	}

	public String getMainRoomTitle() {
		return mainRoomTitle;
	}

	public void setMainRoomTitle(String mainRoomTitle) {
		this.mainRoomTitle = mainRoomTitle;
	}

	public boolean isMainRoomTitleExist(String mainRoomTitle) {
		return this.roomManager.isExistRoomTitle(mainRoomTitle);
	}

	public int getLeftTime() {
		return this.timer.getCount();
	}

	private void startNewCountDownTimer(int leftTime) {
		// stop current timer
		Bukkit.getScheduler().cancelTask(this.timerTask);

		this.timer.setCount(leftTime);

		this.timerTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), new Runnable() {
			@Override
			public void run() {
				timer.removeCount(1);
			}
		}, 0, 20 * 1);
	}

	public void reduceTime(int reductionTime) {
		this.reservationTask.cancel();
		this.reserveNextTask(this.timer.getCount() - reductionTime);
	}

	public boolean checkRoomAndRelayTimeAndRole(RoomType roomType, RelayTime relayTime, Role role, Player p) {
		/*
		 * 이 메소드를 사용하는 입장에서는 현재 RoomType, RelayTime, Role을 검사를 받는것이기 때문에 밑의 예시처럼 사용해야 함
		 * checkRoomAndRelayTimeAndRole(RoomType.MAIN, RelayTime.MAKING, Role.MAKER, p)
		 * = 지금 p가 MAIN room인지 MakingTime인지, p의 role이 MAKER인지?
		 */
		RoomType currentRoom = RoomLocation.getRoomTypeWithLocation(p.getLocation());
		RelayTime currentTime = this.getCurrentTime();
		PlayerData pData = this.pDataManager.getPlayerData(p.getUniqueId());

		if (currentRoom == roomType && currentTime == relayTime && pData.getRole() == role) {
			return true;
		}

		return false;
	}

//    private void giveGoodsToPlayer(Player p) {
//	/*
//	 * playerData가 가지고 있는 good중에서만 해당 role에 맞는 good만을 인벤토리에 추가함 이 메소드가 실행되기 전에 선행되야
//	 * 하는 것: player role 변경!
//	 * 
//	 * 각 Role에 맞는 Goods중에서 가지고 있는 Goods 인벤에 지급
//	 */
//
//	PlayerData pData = this.pDataManager.getPlayerData(p.getUniqueId());
//	for (ShopGoods good : ShopGoods.getPlayerRoleGoods(pData.getRole())) {
//	    if (pData.doesHaveGoods(good)) {
//		InventoryTool.addItemToPlayer(p, good.getItemStack());
//	    }
//	}
//    }

	public void changeRoleWithRelayTime(Player p) {
		/*
		 * 이 메소드는 Role 자체를 설정하는 것이기 때문에 PlayerDataManager의 변수를 가지고 설정 Maker, Non Maker 로
		 * 나뉨(PlayerDataManager 관점에서) 그리고 Time에 맞게 Role 설정
		 * 
		 * 순서]
		 * 
		 * 첫째: Maker, 둘째: non maker
		 * 
		 * Waiting: Waiter, Waiter
		 * 
		 * Making: Maker, Waiter
		 * 
		 * Testing: Tester, Waiter
		 * 
		 * Challenging: Viewer, Challenger
		 */

		PlayerData pData = this.pDataManager.getPlayerData(p.getUniqueId());
		Role role = pData.getRole(); // 기본값

		if (this.currentTime == RelayTime.메이킹 && this.pDataManager.isMaker(p)) {
			role = Role.메이커;
		} else if (this.currentTime == RelayTime.테스팅 && this.pDataManager.isMaker(p)) {
			role = Role.테스터;
		} else if (this.currentTime == RelayTime.챌린징) {
			role = Role.웨이터;
		}

		pData.setRole(role);
	}

	private void tpWithRole(Player p) {
		/*
		 * Waiting떄 main room에 있는 플레이어: lobby
		 * 
		 * maker, tester, challneger, viewer 플레이어: main room spawn
		 * 
		 */
		PlayerData pData = this.pDataManager.getPlayerData(p.getUniqueId());
		Role role = pData.getRole();

		switch (role) {
		case 웨이터:
			if (RoomLocation.getRoomTypeWithLocation(p.getLocation()) == RoomType.메인) {
				TeleportTool.tp(p, SpawnLocationTool.LOBBY);
			}
			break;
		case 메이커:
		case 테스터:
		case 챌린저:
		case 뷰어:
			TeleportTool.tp(p, RoomLocation.MAIN_SPAWN);
			break;
		}
	}

	private void sendMessageWithRole(Player p) {
		/*
		 * 이 메소드는 Role로 구별해서 메세지 전송
		 * 
		 * 메세지 내용: 이전 RelayTime에서 넘어온것이므로 현재 Time이 시작되었을떄의 내용
		 */
		PlayerData pData = this.pDataManager.getPlayerData(p.getUniqueId());
		Role role = pData.getRole();
		if (this.currentTime == RelayTime.웨이팅) {
			// 특별히 WaitingTime일때는 누군가 부신거므로 코어 부셔졌다고 모두에게 메세지 전송
			BroadcastTool.sendMessage(p, this.getMaker().getName() + " 님이 맵을 클리어했습니다");
		} else if (role == Role.메이커) {
			BroadcastTool.sendMessage(p, Material.BRICK.name() + "를 우클릭해서 블럭을 꺼내서 사용하세요");
			BroadcastTool.sendMessage(p,
					"\"/re room title <맵제목>\" 명령어로 맵 이름을 바꿔서 저장할 수 있습니다" + "\n(기본 제목: " + this.mainRoomTitle + ")");
		} else if (role == Role.테스터) {
			BroadcastTool.sendMessage(p, "코어(발광석)을 우클릭해서 테스트를 통과해야 합니다");
		} else if (role == Role.챌린저) {
			Room room = this.roomManager.getRoom(RoomType.메인);
			String roomTitle = room.getTitle();
			String roomMaker = room.getMaker();
			BroadcastTool.sendMessage(p, "맵 제목: " + roomTitle + "(제작자: " + roomMaker + ")");
			if (Setting.DEBUG) {
				BroadcastTool.debug("roomData count : " + this.roomManager.getAllRoomCount());
			}
		} else if (role == Role.뷰어) {
			Room room = this.roomManager.getRoom(RoomType.메인);
			String roomTitle = room.getTitle();
			String roomMaker = room.getMaker();
			BroadcastTool.sendMessage(p, "맵 제목: " + roomTitle + "(제작자: " + roomMaker + ")");
			BroadcastTool.sendMessage(p, "해당 맵의 메이커이므로 관전자 모드로 바뀝니다");
		}

		System.out.println(this.currentTime.name());
		// 공통 title 전송
		BroadcastTool.sendTitle(p, this.currentTime.name(), "역할 " + role.name());
	}

	public Map<String, List<Player>> getPlayerLog() {
		return playerLog;
	}

	public boolean isPlayerInPlayerLog(Player p, String logKind) {
		return this.playerLog.get(logKind).contains(p);
	}

	public boolean addPlayerLog(Player p, String logKind) {
		if (this.isPlayerInPlayerLog(p, logKind)) {
			return false;
		} else {
			this.playerLog.get(logKind).add(p);
			return true;
		}
	}

}
