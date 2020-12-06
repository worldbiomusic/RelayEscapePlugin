package com.wbm.plugin.util;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
import com.wbm.plugin.util.general.shop.ShopGoods;
import com.wbm.plugin.util.minigame.MiniGameManager;

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
    private StageManager stageManager;

    // TODO: RelayTime이름보단 다른것찾아보기 예> RelayTurn ??
    private RelayTime currentTime;

    private BukkitTask currentCountDownTask;

    private boolean corePlaced;
    private String roomTitle;

    private Counter timer;
    private int timerTask;

    private BukkitTask reservationTask;

    private MiniGameManager miniGameManager;

    public RelayManager(PlayerDataManager pDataManager, RoomManager roomManager, StageManager stageManager,
	    MiniGameManager miniGameManager) {
	this.pDataManager = pDataManager;
	this.roomManager = roomManager;
	this.stageManager = stageManager;

	this.currentTime = RelayTime.CHALLENGING;
	this.corePlaced = false;
	this.timer = new Counter();

	this.miniGameManager = miniGameManager;
    }

//    // Waiting이 시작하려면 무조건 maker가 등록되어 있어야 함!
//    private void startWaiting() {
//	// RelayTime 관리
//	this.currentTime = RelayTime.WAITING;
//
//	// unlock main room
//	this.roomManager.unlockRoom(RoomType.MAIN);
//
//	// maker(waiter) 관리
//	if (this.getMaker() == null) {
//	    BroadcastTool.printConsoleMessage(ChatColor.RED + "[Bug] No Maker in WaitingTime!!!!");
//	}
//
//	// 역할 변경
//	this.changeEveryoneRoleWithRelayTime(this.getMaker());
//	// maker 정보 알림
//	BroadcastTool.sendTitle(this.getMaker(), "Maker", "you are maker");
//
//	// tp 관리
//	TeleportTool.allTpToLocation(SpawnLocationTool.RESPAWN);
//
//	// Goods제공 (role변경후 호출되야함)
//	giveGoodsToPlayer(this.getMaker());
//
//	// maker제외한 challenger(waiter) 관리
//	for (Player p : this.getChallengers()) {
//	    this.changeEveryoneRoleWithRelayTime(p);
//	    // core 부서짐 정보 알림
//	    BroadcastTool.sendTitle(this.getMaker(), "" + ChatColor.RED + ChatColor.BOLD + "!", "core is broken");
//	    // Goods제공 (role변경후 호출되야함)
//	    giveGoodsToPlayer(p);
//	}
//
//	// ranking system(stage) 업데이트
//	this.stageManager.updateAllStage();
//
//	RelayTimeCommonTODOList();
//    }
//
//    private void startMaking() {
//	// RelayTime 관리
//	this.currentTime = RelayTime.MAKING;
//
//	// Main Room Locker
//	this.roomManager.lockRoom(RoomType.MAIN);
//
//	// maker 역할 변경
//	this.pDataManager.changePlayerRole(this.getMaker().getUniqueId(), Role.MAKER);
//
//	// message 관리
//	BroadcastTool.sendTitle(this.getMaker(), "MakingTime", "");
//	BroadcastTool.sendMessage(this.getMaker(), "You can save room with title " + "/re room title [title] "
//		+ "\n(basic title: " + this.roomTitle + ")");
//
//	// teleport
//	TeleportTool.tp(this.getMaker(), SpawnLocationTool.JOIN);
//
//	// Goods제공 (role변경후 호출되야함)
//	this.giveGoodsToPlayer(this.getMaker());
//
//	// room basic title 을 "maker이름 + n"으로 설정
//	this.roomTitle = this.roomManager.getNextTitleWithMakerName(this.getMaker().getName());
//
//	// maker제외한 challenger(waiter) 관리
//	for (Player p : this.getChallengers()) {
//	    this.pDataManager.changePlayerRole(p.getUniqueId(), Role.WAITER);
//	    // Goods제공 (role변경후 호출되야함)
//	    this.giveGoodsToPlayer(p);
//	    // teleport
//	    TeleportTool.tp(p, SpawnLocationTool.LOBBY);
//	}
//
//	// practice room 설정
//	Room randomRoom = this.roomManager.getRandomRoomData();
//	this.roomManager.setRoom(RoomType.PRACTICE, randomRoom);
//
//	RelayTimeCommonTODOList();
//    }
//
//    private void startTesting() {
//	// RelayTime 관리
//	this.currentTime = RelayTime.TESTING;
//
//	// maker(tester) 관리
//	this.pDataManager.changePlayerRole(this.getMaker().getUniqueId(), Role.TESTER);
//	// Goods제공 (role변경후 호출되야함)
//	giveGoodsToPlayer(this.getMaker());
//	// teleport
//	TeleportTool.tp(this.getMaker(), SpawnLocationTool.JOIN);
//
//	// maker제외한 challenger(waiter) 관리
//	for (Player p : this.getChallengers()) {
//	    this.pDataManager.changePlayerRole(p.getUniqueId(), Role.WAITER);
//	    // Goods제공 (role변경후 호출되야함)
//	    giveGoodsToPlayer(p);
//	}
//
//	RelayTimeCommonTODOList();
//    }
//
//    private void startChallenging() {
//	// RelayTime 관리
//	this.currentTime = RelayTime.CHALLENGING;
//
//	// room challeningCount + 1
//	this.roomManager.getRoom(RoomType.MAIN).addChallengingCount(1);
//
//	// maker(viewer) 관리
//	// if문 넣은이유: Maker가 만들고 나갔을때 위해서 or 처음시작시 maker가없기 때문
//	if (this.pDataManager.doesMakerExist()) {
//	    this.pDataManager.changePlayerRole(this.getMaker().getUniqueId(), Role.VIEWER);
//	    // Goods제공 (role변경후 호출되야함)
//	    this.giveGoodsToPlayer(this.getMaker());
//	    // teleport
//	    TeleportTool.tp(this.getMaker(), SpawnLocationTool.JOIN);
//
//	}
//
//	// maker제외한 challenger(challenger) 관리
//	for (Player p : this.getChallengers()) {
//	    // minigame 중지 (Maker는 minigame을 못하니까 상관x)
//	    this.miniGameManager.handlePlayerCurrentMiniGameAndExitGame(p);
//
//	    // role 변경
//	    this.pDataManager.changePlayerRole(p.getUniqueId(), Role.CHALLENGER);
//
//	    // challengingCount +1
//	    UUID uuid = p.getUniqueId();
//	    PlayerData pData = this.pDataManager.getPlayerData(uuid);
//	    pData.addChallengingCount(1);
//
//	    // Goods제공 (role변경후 호출되야함)
//	    giveGoodsToPlayer(p);
//
//	    // heal
//	    PlayerTool.heal(p);
//	}
//
//	// 모두 respawn으로 tp
//	TeleportTool.allTpToLocation(SpawnLocationTool.RESPAWN);
//
//	// start room duration time
//	this.roomManager.startMainRoomDurationTime();
//
//	// room state 알림
//	Room room = this.roomManager.getRoom(RoomType.MAIN);
//	String roomTitle = room.getTitle();
//	String roomMaker = room.getMaker();
//	BroadcastTool.sendMessageToEveryone("Main room: " + roomTitle + "(" + roomMaker + ")");
//	if (Setting.DEBUG) {
//	    BroadcastTool.debug("roomData count : " + this.roomManager.getAllRoomCount());
//	}
//
//	RelayTimeCommonTODOList();
//
//    }

    // Waiting이 시작하려면 무조건 maker가 등록되어 있어야 함!
    private void startWaiting() {
//	 RelayTime 관리
	this.currentTime = RelayTime.WAITING;

	// common todo list
	RelayTimeCommonTODOList();

	// unlock main room
	this.roomManager.unlockRoom(RoomType.MAIN);

	// maker(waiter) 관리
	if (this.getMaker() == null) {
	    BroadcastTool.printConsoleMessage(ChatColor.RED + "[Bug] No Maker in WaitingTime!!!!");
	}

	// ranking system(stage) 업데이트
	this.stageManager.updateAllStage();

    }

    private void startMaking() {
//	 RelayTime 관리
	this.currentTime = RelayTime.MAKING;
	
	// common todo list
	RelayTimeCommonTODOList();

	// Main Room Locker
	this.roomManager.lockRoom(RoomType.MAIN);

	// room basic title 을 "maker이름 + n"으로 설정
	this.roomTitle = this.roomManager.getNextTitleWithMakerName(this.getMaker().getName());

	// practice room 설정
	Room randomRoom = this.roomManager.getRandomRoomData();
	this.roomManager.setRoom(RoomType.PRACTICE, randomRoom);
    }

    private void startTesting() {
//	 RelayTime 관리
	this.currentTime = RelayTime.TESTING;
	
	// common todo list
	RelayTimeCommonTODOList();
    }

    private void startChallenging() {
//	 RelayTime 관리
	this.currentTime = RelayTime.CHALLENGING;
	
	// common todo list
	RelayTimeCommonTODOList();

	// room challeningCount + 1
	this.roomManager.getRoom(RoomType.MAIN).addChallengingCount(1);

	// maker제외한 challenger(challenger) 관리
	for (Player p : this.getChallengers()) {
	    // minigame 중지 (Maker는 minigame을 못하니까 상관x)
	    this.miniGameManager.handlePlayerCurrentMiniGameExiting(p);

	    // challengingCount +1
	    UUID uuid = p.getUniqueId();
	    PlayerData pData = this.pDataManager.getPlayerData(uuid);
	    pData.addChallengingCount(1);
	}

	// start recording room duration time
	this.roomManager.startMainRoomDurationTime();
    }

    private void RelayTimeCommonTODOList() {
	/*
	 * 순서상관 있음: RelayManager는 실제적으로 Maker를 관리하는 PlayerDataManaer에서 변수를 직접받아와서 판단하지만,
	 * 
	 * 다른 클래스와 밑의 메소드들은 Role을 가지고 판단하므로 Role변경을 time변경 다음으로 최우선으로 해야 한다(Role을 time에
	 * 맞게 수정하므로)
	 * 
	 * 대부분 메소드들은 각 플레이어의 Role을 가지고 구분해서 수행함
	 * 
	 * 1.여기서 this.currentTime = RelayTime.getNextTime(this.currentTime); 사용하면 안되는 이유:
	 * 항상 다음것으로 흘러가지 않고 중간에 Making이나 Testing이 실패할 수 도 있는 상황이 있기 떄문에 각자의 메소드에서 직접
	 * 설정해줘야 함(예)this.current = RelayTime.TESTING)
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
	 */

//	2.역할 변경
	for (Player p : Bukkit.getOnlinePlayers()) {
	    this.changeEveryoneRoleWithRelayTime(p);
	}

//	3.maker, everyone에게 메세지 전송
	this.sendMessageEveryoneWithRole();

//	4.위치 변경
	this.tpEveryoneWithRole();

//	5.인벤토리 관리(초기화 후, Goods 제공)
	InventoryTool.clearAllPlayerInv();
	this.giveGoodsToEveryone();

	// 6.다음 태스크 예약
	reserveNextTask(this.currentTime.getAmount());

	// 7.힐
	for (Player p : Bukkit.getOnlinePlayers()) {
	    PlayerTool.heal(p);
	}
    }

    private void reserveNextTask(int durationTime) {
	this.reservationTask = this.currentCountDownTask = Bukkit.getScheduler().runTaskLater(Main.getInstance(),
		new Runnable() {

		    @Override
		    public void run() {
			// time에 따른 실행
			RelayTime currentTime = getCurrentTime();
			if (currentTime == RelayTime.WAITING) {
			    startNextTime();
			} else if (currentTime == RelayTime.MAKING) {
			    if (!isCorePlaced()) {
				resetRelay();
			    } else {
				BroadcastTool.sendMessageToEveryone("Maker failed to make the room");
				startNextTime();
			    }
			} else if (currentTime == RelayTime.TESTING) {
			    // reset relay
			    BroadcastTool.sendMessageToEveryone("Maker couldn't pass the room test");
			    resetRelay();
			} else if (currentTime == RelayTime.CHALLENGING) {
			    BroadcastTool.sendMessageToEveryone("No one pass the room");

			    resetRelay();
			}
		    }
		}, 20 * durationTime);
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
    private Player getMaker() {
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

	if (time == RelayTime.WAITING) {
	    this.startMaking();
	} else if (time == RelayTime.MAKING) {
	    this.startTesting();
	} else if (time == RelayTime.TESTING) {
	    // core부수면 바로 시작
	    this.startChallenging();
	} else if (time == RelayTime.CHALLENGING) {
	    this.startWaiting();
	}

	this.startNewCountDownTimer(RelayTime.getNextTime(time));
    }

    // 예외적인 상황이 발생했을 때 사용 (Maker가 방을 중간에 나가거나 or 조건이 불만족되었을때)
    public void startAnotherTime(RelayTime anotherTime) {
	// 먼저 현재 time task 중지
	this.stopCurrentTime();

	if (anotherTime == RelayTime.WAITING) {
	    this.startWaiting();
	} else if (anotherTime == RelayTime.MAKING) {
	    this.startMaking();
	} else if (anotherTime == RelayTime.TESTING) {
	    this.startTesting();
	} else if (anotherTime == RelayTime.CHALLENGING) {
	    this.startChallenging();
	}

	this.startNewCountDownTimer(anotherTime);
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
	this.roomManager.setRoom(RoomType.MAIN, randomRoom);

	// RelayTime set to CHALLENGING
	this.startAnotherTime(RelayTime.CHALLENGING);
    }

    public void resetRelaySetting() {
	// corePlaced 초기화
	this.corePlaced = false;

	// PlayerDataManager maker = null 처리
	this.pDataManager.unregisterMaker();

	// Inventory clear
	InventoryTool.clearAllPlayerInv();
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

    public String getRoomTitle() {
	return roomTitle;
    }

    public void setRoomTitle(String roomTitle) {
	this.roomTitle = roomTitle;
    }

    public int getLeftTime() {
	return this.timer.getCount();
    }

    private void startNewCountDownTimer(RelayTime newTime) {
	// stop current timer
	Bukkit.getScheduler().cancelTask(this.timerTask);

	this.timer.setCount(newTime.getAmount());

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

	this.timer.removeCount(reductionTime);
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

    private void giveGoodsToPlayer(Player p) {
	/*
	 * playerData가 가지고 있는 good중에서만 해당 role에 맞는 good만을 인벤토리에 추가함 이 메소드가 실행되기 전에 선행되야
	 * 하는 것: player role 변경!
	 * 
	 * 각 Role에 맞는 Goods중에서 가지고 있는 Goods 인벤에 지급
	 */

	PlayerData pData = this.pDataManager.getPlayerData(p.getUniqueId());
	for (ShopGoods good : ShopGoods.getRoleGoods(pData.getRole())) {
	    if (pData.doesHaveGoods(good)) {
		InventoryTool.addItemToPlayer(p, good.getGoods());
	    }
	}
    }

    private void giveGoodsToEveryone() {
	/*
	 * playerData가 가지고 있는 good중 해당 role에 맞는 good만을 인벤토리에 추가함 이 메소드가 실행되기 전에 선행되야 하는
	 * 것: player role 변경! * 각 Role에 맞는 Goods중에서 가지고 있는 Goods 인벤에 지급
	 */
	for (Player p : Bukkit.getOnlinePlayers()) {
	    PlayerData pData = this.pDataManager.getPlayerData(p.getUniqueId());
	    for (ShopGoods good : ShopGoods.getRoleGoods(pData.getRole())) {
		if (pData.doesHaveGoods(good)) {
		    InventoryTool.addItemToPlayer(p, good.getGoods());
		}
	    }
	}
    }

    public void changeEveryoneRoleWithRelayTime(Player p) {
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
	Role role = Role.WAITER; // 기본값

	if (this.currentTime == RelayTime.MAKING && this.pDataManager.isMaker(p)) {
	    role = Role.MAKER;
	} else if (this.currentTime == RelayTime.TESTING && this.pDataManager.isMaker(p)) {
	    role = Role.TESTER;
	} else if (this.currentTime == RelayTime.CHALLENGING) {
	    if (this.pDataManager.isMaker(p)) {
		role = Role.VIEWER;
	    } else {
		role = Role.CHALLENGER;
	    }
	}

	pData.setRole(role);
    }

    private void tpEveryoneWithRole() {
	/*
	 * Role에 맞게 Maker tp
	 * 
	 * waiter: LOBBY
	 * 
	 * maker, tester, challenger, viewer: RESPAWN
	 */
	for (Player p : Bukkit.getOnlinePlayers()) {
	    PlayerData pData = this.pDataManager.getPlayerData(p.getUniqueId());
	    Role role = pData.getRole();
	    if (role == Role.WAITER) {
		if (!(this.currentTime == RelayTime.TESTING)) {
		    // making -> testing 으로 넘어갈때 waiter는 tp 할 필요 없음(게임중일 수도 있음)
		    TeleportTool.tp(p, SpawnLocationTool.LOBBY);
		}
	    } else {
		TeleportTool.tp(p, SpawnLocationTool.RESPAWN);
	    }
	}
    }

    private void sendMessageEveryoneWithRole() {
	/*
	 * 이 메소드는 Role로 구별해서 메세지 전송
	 * 
	 * 메세지 내용: 이전 RelayTime에서 넘어온것이므로 현재 Time이 시작되었을떄의 내용
	 */
	for (Player p : Bukkit.getOnlinePlayers()) {
	    PlayerData pData = this.pDataManager.getPlayerData(p.getUniqueId());
	    Role role = pData.getRole();
	    if (role == Role.WAITER) {
	    } else if (role == Role.MAKER) {
		BroadcastTool.sendMessage(p, "You can save room with title " + "/re room title [title] "
			+ "\n(basic title: " + this.roomTitle + ")");
	    } else if (role == Role.TESTER) {
		BroadcastTool.sendMessage(p, "You must break core to save this room");
	    } else if (role == Role.CHALLENGER) {
		Room room = this.roomManager.getRoom(RoomType.MAIN);
		String roomTitle = room.getTitle();
		String roomMaker = room.getMaker();
		BroadcastTool.sendMessage(p, "Main room: " + roomTitle + "(" + roomMaker + ")");
		if (Setting.DEBUG) {
		    BroadcastTool.debug("roomData count : " + this.roomManager.getAllRoomCount());
		}
	    } else if (role == Role.VIEWER) {
		BroadcastTool.sendMessage(p, "You cannot clear your own room");
	    }

	    BroadcastTool.sendTitle(p, this.currentTime.name(), "you are " + role.name());
	}
    }
}
