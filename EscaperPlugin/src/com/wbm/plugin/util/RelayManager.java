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

    public RelayManager(PlayerDataManager pDataManager, RoomManager roomManager, StageManager stageManager, MiniGameManager miniGameManager) {
	this.pDataManager = pDataManager;
	this.roomManager = roomManager;
	this.stageManager = stageManager;

	this.currentTime = RelayTime.CHALLENGING;
	this.corePlaced = false;
	this.timer = new Counter();
	
	this.miniGameManager = miniGameManager;
    }

    // Waiting이 시작하려면 무조건 maker가 등록되어 있어야 함!
    private void startWaiting() {
	// RelayTime 관리
	this.currentTime = RelayTime.WAITING;

	// unlock main room
	this.roomManager.unlockRoom(RoomType.MAIN);

	// maker(waiter) 관리
	if (this.getMaker() == null) {
	    BroadcastTool.printConsoleMessage(ChatColor.RED + "[Bug] No Maker in WaitingTime!!!!");
	}
	BroadcastTool.sendMessage(this.getMaker(), "you are now Maker");
	this.pDataManager.changePlayerRole(this.getMaker().getUniqueId(), Role.WAITER);
	// Goods제공 (role변경후 호출되야함)
	giveGoodsToPlayer(this.getMaker());

	// maker제외한 challenger(waiter) 관리
	for (Player p : this.getChallengers()) {
	    this.pDataManager.changePlayerRole(p.getUniqueId(), Role.WAITER);
	    // Goods제공 (role변경후 호출되야함)
	    giveGoodsToPlayer(p);
	}

	// message 관리
	BroadcastTool
		.sendMessageToEveryone("waitingTime: makingTime starts in " + RelayTime.WAITING.getAmount() + " sec");

	// ranking system(stage) 업데이트
//		LocalDateTime time = LocalDateTime.now();
	this.stageManager.updateAllStage();

	// MakingTime 카운트다운
//		this.currentCountDownTask=Bukkit.getScheduler().runTaskLater(Main.getInstance(), new Runnable()
//		{
//
//			@Override
//			public void run()
//			{
//				startNextTime();
//			}
//		}, 20*RelayTime.WAITING.getAmount());
	reserveNextTask(this.getCurrentTime().getAmount());
    }

    private void startMaking() {
	// RelayTime 관리
	this.currentTime = RelayTime.MAKING;

	// Main Room Locker
	this.roomManager.lockRoom(RoomType.MAIN);

	// maker(maker) 관리
	this.pDataManager.changePlayerRole(this.getMaker().getUniqueId(), Role.MAKER);
	// Goods제공 (role변경후 호출되야함)
	this.giveGoodsToPlayer(this.getMaker());

	// teleport
	TeleportTool.tp(this.getMaker(), SpawnLocationTool.JOIN);

	// room title 을 "maker이름 + n"으로 설정
	this.roomTitle = this.roomManager.getNextTitleWithMakerName(this.getMaker().getName());

	// maker제외한 challenger(waiter) 관리
	for (Player p : this.getChallengers()) {
	    this.pDataManager.changePlayerRole(p.getUniqueId(), Role.WAITER);
	    // Goods제공 (role변경후 호출되야함)
	    this.giveGoodsToPlayer(p);
	    // teleport
	    TeleportTool.tp(p, SpawnLocationTool.LOBBY);
	}

	// message 관리
	BroadcastTool.sendMessage(this.getMaker(),
		"Enter you room title with " + "/re room title [title] " + "\n(base title: " + this.roomTitle + ")");
	BroadcastTool
		.sendMessageToEveryone("makingTime: testingTime starts in " + RelayTime.MAKING.getAmount() + " sec");

	Room randomRoom = this.roomManager.getRandomRoomData();
	this.roomManager.setRoom(RoomType.PRACTICE, randomRoom);

	// TestingTime 카운트다운
//		this.currentCountDownTask=Bukkit.getScheduler().runTaskLater(Main.getInstance(), new Runnable()
//		{
//
//			@Override
//			public void run()
//			{
//				if(!isCorePlaced())
//				{
//					resetRelay();
//				}
//				else
//				{
//					startNextTime();
//				}
//			}
//		}, 20*RelayTime.MAKING.getAmount());
	reserveNextTask(this.getCurrentTime().getAmount());
    }

    private void startTesting() {
	// RelayTime 관리
	this.currentTime = RelayTime.TESTING;

	// maker(tester) 관리
	this.pDataManager.changePlayerRole(this.getMaker().getUniqueId(), Role.TESTER);
	// Goods제공 (role변경후 호출되야함)
	giveGoodsToPlayer(this.getMaker());
	// teleport
	TeleportTool.tp(this.getMaker(), SpawnLocationTool.JOIN);

	// maker제외한 challenger(waiter) 관리
	for (Player p : this.getChallengers()) {
	    this.pDataManager.changePlayerRole(p.getUniqueId(), Role.WAITER);
	    // Goods제공 (role변경후 호출되야함)
	    giveGoodsToPlayer(p);
	}

	// message 관리
	BroadcastTool
		.sendMessageToEveryone("testTime: challengingTime starts in " + RelayTime.TESTING.getAmount() + " sec");

	// resetRelay 카운트다운
//		this.currentCountDownTask=Bukkit.getScheduler().runTaskLater(Main.getInstance(), new Runnable()
//		{
//			// TestingTime에서 시간이 다되었다는것은 통과를 못했다는 뜻 -> restRelay
//			@Override
//			public void run()
//			{
//				// reset relay
//				BroadcastTool.sendMessageToEveryone("Maker couldn't pass the test");
//				resetRelay();
//			}
//		}, 20*RelayTime.TESTING.getAmount());
	reserveNextTask(this.getCurrentTime().getAmount());
    }

    private void startChallenging() {
	// RelayTime 관리
	this.currentTime = RelayTime.CHALLENGING;

	// room challeningCount + 1
	this.roomManager.getRoom(RoomType.MAIN).addChallengingCount(1);

	// maker(viewer) 관리
	// if문 넣은이유: Maker가 만들고 나갔을때 위해서 or 처음시작시 maker가없기 때문
	if (this.pDataManager.doesMakerExist()) {
	    this.pDataManager.changePlayerRole(this.getMaker().getUniqueId(), Role.VIEWER);
	    // Goods제공 (role변경후 호출되야함)
	    this.giveGoodsToPlayer(this.getMaker());
	    // teleport
	    TeleportTool.tp(this.getMaker(), SpawnLocationTool.JOIN);

	}

	// maker제외한 challenger(challenger) 관리
	for (Player p : this.getChallengers()) {
	    // minigame 중지 (Maker는 minigame을 못하니까 상관x)
	    this.miniGameManager.handlePlayerCurrentMiniGameAndExitGame(p);
	    
	    // role 변경
	    this.pDataManager.changePlayerRole(p.getUniqueId(), Role.CHALLENGER);

	    // challengingCount +1
	    UUID uuid = p.getUniqueId();
	    PlayerData pData = this.pDataManager.getPlayerData(uuid);
	    pData.addChallengingCount(1);

	    // Goods제공 (role변경후 호출되야함)
	    giveGoodsToPlayer(p);

	    // heal
	    PlayerTool.heal(p);
	}

	// 모두 respawn으로 tp
	TeleportTool.allTpToLocation(SpawnLocationTool.RESPAWN);

	// message 관리
	BroadcastTool.sendMessageToEveryone(
		"challengingTime: new challengingTime starts in " + RelayTime.CHALLENGING.getAmount() + " sec");

	// start room duration time
	this.roomManager.startMainRoomDurationTime();

	// resetRelay 카운트다운
//		this.currentCountDownTask=Bukkit.getScheduler().runTaskLater(Main.getInstance(), new Runnable()
//		{
//			// ChallengingTime에서 시간이 다되었다는것은 사람이 없거나 난이도가 어렵다는 뜻 -> resetRelay
//			@Override
//			public void run()
//			{
//				// reset relay
//				resetRelay();
//			}
//		}, 20*RelayTime.CHALLENGING.getAmount());
	reserveNextTask(this.getCurrentTime().getAmount());

    }

    private void reserveNextTask(int durationTime) {
	this.reservationTask = this.currentCountDownTask = Bukkit.getScheduler().runTaskLater(Main.getInstance(),
		new Runnable() {

		    @Override
		    public void run() {
			// 모든유저 인벤관리
			InventoryTool.clearAllPlayerInv();

			// time에 따른 실행
			RelayTime currentTime = getCurrentTime();
			if (currentTime == RelayTime.WAITING) {
			    startNextTime();
			} else if (currentTime == RelayTime.MAKING) {
			    if (!isCorePlaced()) {
				resetRelay();
			    } else {
				startNextTime();
			    }
			} else if (currentTime == RelayTime.TESTING) {
			    // reset relay
			    BroadcastTool.sendMessageToEveryone("Maker couldn't pass the test");
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

//	// this.pDataManager.registerMaker(maker);가 너무 길어서 만든 메소드
//	// this.pDataManager.getMaker() <- 여기서만 참조해야 데이터 무결성이 보장됨
//	private void setMaker(Player maker) {
//		this.pDataManager.registerMaker(maker);
//	}

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
	BroadcastTool.sendMessageToEveryone(ChatColor.RED + "relay reset!");

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

    private List<ShopGoods> getPlayerGoods(Player p) {
	return this.pDataManager.getPlayerData(p.getUniqueId()).getGoods();
    }

    private void giveGoodsToPlayer(Player p) {
	/*
	 * playerData가 가지고 있는 good중 해당 role에 맞는 good만을 인벤토리에 추가함 이 메소드가 실행되기 전에 선행되야 하는
	 * 것: player role 변경!
	 */
	for (ShopGoods goods : this.getPlayerGoods(p)) {
	    PlayerData pData = this.pDataManager.getPlayerData(p.getUniqueId());
	    if (ShopGoods.isRoleGoods(pData.getRole(), goods)) {
		InventoryTool.addItemToPlayer(p, goods.getGoods());
	    }
	}
    }
}
