package com.wbm.plugin.util.minigame;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.scheduler.BukkitTask;

import com.wbm.plugin.Main;
import com.wbm.plugin.data.MiniGameLocation;
import com.wbm.plugin.data.PlayerData;
import com.wbm.plugin.util.PlayerDataManager;
import com.wbm.plugin.util.enums.MiniGameType;
import com.wbm.plugin.util.general.BroadcastTool;
import com.wbm.plugin.util.general.Counter;
import com.wbm.plugin.util.general.InventoryTool;
import com.wbm.plugin.util.general.SpawnLocationTool;
import com.wbm.plugin.util.general.TeleportTool;

import net.md_5.bungee.api.ChatColor;

public abstract class BattleMiniGame implements Serializable, MiniGame {

    private static final long serialVersionUID = 1L;
    /*
     * 모든 배틀 미니게임은 이 클래스를 상속받아서 만들어져야 함
     * 
     * 튜닝할 수 있는 것
     * 
     * -runTaskAfterStartGame(): 메소드 오버라이딩하면 시작시 실행해줌(예.게임 아이템 추가)
     * 
     * [주의] gameType, rankData는 파일로 저장되야되서 transient 선언안함 새로운 변수 추가할때 transient 항상
     * 고려하기
     * 
     * 
     * [주의] MiniGame클래스의 생성자에서 만들어도 여기서 저장된 데이터가 불러들이면 생성자에서 한 행동은 모두 없어지고 저장되었던
     * 데이터로 "교체"됨! -> 생성자에서 특정 변수 선언하지 말고, static class나 method에 인자로 넘겨서 사용
     *
     * 
     * [미니게임 추가하는법]
     * 
     * 1.이 클래스를 상속하는 클래스를 하나 만들고 필요한 메소드를 다 오바라이딩해서 구현한다
     * 
     * 2.MiniGameManager의 생성자에서 "allGame.add(new FindTheRed())" 처럼 등록한다 (이유: 처음에
     * 미니게임 데이터가 없는것을 초기화해서 파일저장하려고, 나중에 파일에 저장되면 데이터 불러올때 저장된것으로 대체가 됨 = 처음에 한번
     * 초기화를 위해서 필요한 코드)
     */

    // BattleMiniGame에서는 players로 Rank판단 가능
    transient protected Map<String, Integer> players;
    transient protected boolean activated;
    transient protected int waitingTime;
    transient protected int fee;

    transient protected int timeLimit;
    protected MiniGameType gameType;

    transient protected BukkitTask startTask, exitTask, timerTask;

    public BattleMiniGame(MiniGameType gameType) {
	this.gameType = gameType;
	this.initGameSettings();
    }

    public void initGameSettings() {
	this.players = new HashMap<>();
	this.activated = false;
	// 먼저 실행중인 task취소하고 초기화
	this.stopAllTasks();
	this.startTask = this.exitTask = this.timerTask = null;
	this.waitingTime = 30;
	this.timeLimit = gameType.getTimeLimit();
	this.fee = gameType.getFee();
    }

    @Override
    public void enterRoom(Player p, PlayerDataManager pDataManager) {
	PlayerData pData = pDataManager.getPlayerData(p.getUniqueId());

	// 사람 들어있는지 확인
	// SoloMiniGame: 사람 있으면 못들어감
	// MultiCooperativeMiniGame: master가 있으면 허락맡고 입장
	// MultiBattleMiniGame: 인원수 full 아니면 그냥 입장

	// 먼저: token충분한지 검사
	if (!pData.minusToken(fee)) {
	    BroadcastTool.sendMessage(p, "you need more token");
	    return;
	}

	// 누군가 있을때
	if (this.isSomeoneInGameRoom()) {
	    // player관련 세팅
	    this.setupPlayerSettings(p, pData);
	} else { // 아무도 없을때는 게임을 prepare해서 초기화상태로 만듬
	    // init variables
	    this.prepareGame(p);
	    // player관련 세팅
	    this.setupPlayerSettings(p, pData);
	    // start game
	    this.reserveGameTasks(pDataManager);
	}

    }

    private void startTimer() {
	/*
	 * 1초마다 모든 플레이어에게 Counter의 수를 send title함
	 */
	Counter timer = new Counter(this.waitingTime);

	this.timerTask = Bukkit.getScheduler().runTaskTimer(Main.getInstance(), new Runnable() {
	    @Override
	    public void run() {
		// send title
		BroadcastTool.sendTitle(getPlayer(), timer.getCount() + "", "", 0.2, 0.6, 0.2);
		timer.removeCount(1);

		// 0이하에서는 취소
		if (timer.getCount() <= 0) {
		    timerTask.cancel();
		}
	    }
	}, 0, 20);
    }

    private void prepareGame(Player p) {
	/*
	 * 게임 초기화하고, 게임 준비
	 */
	// 게임 초기화
	this.initGameSettings();

	// count down 시작
	this.startTimer();
    }

    private void setupPlayerSettings(Player p, PlayerData pData) {
	/*
	 * 게임 초기화는 이미 했으므로 플레이어관련한것만 세팅
	 */
	// player 등록
	this.players.put(p.getName(), 0);

	// 게임룸 위치로 tp
	Location gameRoom = this.gameType.getRoomLocation();
	TeleportTool.tp(p, gameRoom);

	// info 전달
	this.notifyInfo(p);

	// pdata에 미니게임 등록
	pData.setMinigame(this.gameType);
    }

    void notifyInfo(Player p) {
	// player에게 정보 전달
	this.printGameTutorial(p);
    }

    private void reserveGameTasks(PlayerDataManager pDataManager) {
	/*
	 * 게임 활성화, 퇴장 task 예약
	 */
	// this.waitingTime 초 후 실행
	this.reserveActivateGameTask();

	// exitGame(): this.waitingTime + this.timeLimit 초 후 실행
	this.reserveExitGameTask(pDataManager);
    }

    private void reserveActivateGameTask() {
	this.startTask = Bukkit.getScheduler().runTaskLater(Main.getInstance(), new Runnable() {
	    @Override
	    public void run() {
		// activated = true를 waitingTime후에 실행하는 이유:
		// block event가 왔을때 activated가 true일때만 실행되게 했으므로
		activated = true;

		BroadcastTool.sendTitle(getPlayer(), "START", "");

		// start game 후에 실행할 작업
		runTaskAfterStartGame();
	    }
	}, 20 * waitingTime);
    }

    private void reserveExitGameTask(PlayerDataManager pDataManager) {
	this.exitTask = Bukkit.getScheduler().runTaskLater(Main.getInstance(), new Runnable() {
	    @Override
	    public void run() {
		exitGame(pDataManager);
	    }
	}, 20 * (waitingTime + this.timeLimit));
    }

    public void exitGame(PlayerDataManager pDataManager) {
	/*
	 * print game result 보상 지급 score rank 처리 player 퇴장 (lobby로) inventory 초기화 게임 초기화
	 */

	// print game result
	this.printGameResult();

	// 보상 지급
	this.payReward(pDataManager);

	// player lobby로 tp
	TeleportTool.tp(this.getPlayer(), SpawnLocationTool.LOBBY);

	// inventory 초기화
	InventoryTool.clearPlayerInv(this.getPlayer());

	// pData minigame 초기화
	for (Player p : this.getPlayer()) {
	    PlayerData pData = pDataManager.getPlayerData(p.getUniqueId());
	    pData.setNull();
	}

	// 초기화
	this.initGameSettings();
    }

    private void printGameResult() {
	for (Player p : this.getPlayer()) {
	    // GAME END print
	    BroadcastTool.sendMessage(p, "=================================");
	    BroadcastTool.sendMessage(p, "" + ChatColor.RED + ChatColor.BOLD + "Game End");
	    BroadcastTool.sendMessage(p, "=================================");

	    // 전체플레이어 score 공개
	    for (Player all : this.getPlayer()) {
		BroadcastTool.sendMessage(p, all.getName() + " score: " + this.players.get(all.getName()));
	    }

	    // send title
	    BroadcastTool.sendTitle(p, "Game End", "");
	    BroadcastTool.sendMessage(p, "");
	}
    }

    public void payReward(PlayerDataManager pDataManager) {
	/*
	 * BattleMiniGame 보상 배틀 미니게임의 보상은 다른 미니게임과 다르게 적용
	 * 
	 * SUM = 모든 플레이어 입장료 합계
	 * 
	 * 1등: SUM의 30%
	 * 
	 * 2등: SUM의 20%
	 * 
	 * 3등: SUM의 10%
	 * 
	 * REMAIN = SUM - (1,2,3등 보상) (빼야하는 이유: 소수점을 그냥 내리기 때문에 직접 다 빼야함)
	 * 
	 * 참가보상: REMAIN의 (전체인원)%
	 * 
	 * 100 10명 (fee: 10)
	 * 
	 * 1등: 30 2등: 20 3등: 10
	 * 
	 * 참가보상: 40의 10%씩 = 4
	 *
	 * 50 10명(fee: 5)
	 * 
	 * 1등: 15 2등: 10 3등: 5
	 * 
	 * 참가보상: 20의 10%씩 = 2
	 * 
	 */

	int SUM = this.players.size() * this.gameType.getFee();

	// token의 내림차순으로 랭크된 플레이어 목록
	List<Entry<String, Integer>> rank = MiniGameRankManager.getDescendingSortedMapEntrys(this.players);

	int firstReward = (int) (SUM * 0.3);
	int secondReward = (int) (SUM * 0.2);
	int thirdReward = (int) (SUM * 0.1);

	int REMAIN = SUM;

	// nullPointerException피하기 위해서 코드가 더러움
	String firstPlayer = null, secondPlayer = null, thirdPlayer = null;
	firstPlayer = rank.get(0).getKey();
	REMAIN -= firstReward;
	if (rank.size() >= 2) {
	    secondPlayer = rank.get(1).getKey();
	    REMAIN -= secondReward;
	}
	if (rank.size() >= 3) {
	    thirdPlayer = rank.get(2).getKey();
	    REMAIN -= thirdReward;
	}

	// REMAIN에서 1,2,3등 뺀 것에서 참가보상 계산
	int participationReward = REMAIN * (1 / this.players.size());
	// reward
	for (String name : this.players.keySet()) {
	    Player p = Bukkit.getPlayer(name);
	    PlayerData pData = pDataManager.getPlayerData(p.getUniqueId());

	    int reward = participationReward;

	    // 1, 2, 3 reward
	    if (name.equals(firstPlayer)) {
		reward += firstReward;
	    } else if (name.equals(secondPlayer)) {
		reward += secondReward;
	    } else if (name.equals(thirdPlayer)) {
		reward += thirdReward;
	    }

	    // plus token
	    pData.plusToken(reward);

	    // msg
	    BroadcastTool.sendMessage(p, "Reward token: " + reward);
	}

    }

    /*
     * 이 메소드는 미니게임에서 플레이어들이 발생한 이벤트를 각 게임에서 처리해주는 범용 메소드 예) if(event instanceof
     * BlockBreakEvent) { BlockBreakEvent e = (BlockBreakEvent) event; // 생략 }
     */
    public abstract void processEvent(Event event);

    // tutorial strings
    public abstract String[] getGameTutorialStrings();

    public void printGameTutorial(Player p) {
	/*
	 * 기본적으로 출력되는 정보 -game name -time limit -waiting time
	 * 
	 * getGameTutorialStrings()에 추가해야 하는 정보 -game rule
	 */
	BroadcastTool.sendMessage(p, "=================================");
	BroadcastTool.sendMessage(p, "" + ChatColor.RED + ChatColor.BOLD + this.gameType.name() + ChatColor.WHITE);
	BroadcastTool.sendMessage(p, "=================================");

	// print rule
	BroadcastTool.sendMessage(p, "");
	BroadcastTool.sendMessage(p, ChatColor.BOLD + "[Rule]");
	BroadcastTool.sendMessage(p, "Time Limit: " + this.timeLimit);
	for (String msg : this.getGameTutorialStrings()) {
	    BroadcastTool.sendMessage(p, msg);
	}
    }

    public void runTaskAfterStartGame() {
    }

    public int getGameBlockCount() {
	return MiniGameLocation.getGameBlockCount(this.gameType);
    }

    public void stopAllTasks() {
	if (this.startTask != null)
	    this.startTask.cancel();
	if (this.exitTask != null) {
	    this.exitTask.cancel();
	}
	if (this.timerTask != null) {
	    this.timerTask.cancel();
	}
    }

    public boolean isPlayerPlayingGame(Player p) {
	return this.players.containsKey(p.getName());
    }

    @Override
    public void processHandlingMiniGameExitDuringPlaying(Player p, PlayerDataManager pDataManager,
	    MiniGame.ExitReason reason) {
	/*
	 * SELF_EXIT: 혼자 퇴장, 보상 지급 없음
	 * 
	 * RELAY_TIME_CHANGED: 게임 자체 종료(보상 지급 있음)
	 */

	if (reason == MiniGame.ExitReason.SELF_EXIT) {
	    // remove exiting player from game
	    this.players.remove(p.getName());

	    // player lobby로 tp
	    TeleportTool.tp(p, SpawnLocationTool.LOBBY);

	    // inventory 초기화
	    InventoryTool.clearPlayerInv(p);

	    // pData minigame 초기화
	    PlayerData pData = pDataManager.getPlayerData(p.getUniqueId());
	    pData.setNull();

	    // 남은 인원에게 알리기
	    BroadcastTool.sendMessage(this.getPlayer(), p.getName() + " exit " + this.gameType.name());
	    
	    // 패널티
	    pData.minusToken(this.fee * 2);

	    // 게임에 아무도 없을 때 game init & stop all tasks
	    if (!this.isSomeoneInGameRoom()) {
		this.initGameSettings();
	    }
	} else if (reason == MiniGame.ExitReason.RELAY_TIME_CHANGED) {
	    this.exitGame(pDataManager);
	}

    }

    // GETTER, SETTER =============================================

    public List<Player> getPlayer() {
	/*
	 * String Player name을 Player형 리스트로 반환
	 */
	List<Player> allPlayer = new ArrayList<>();
	for (String p : this.players.keySet()) {
	    allPlayer.add(Bukkit.getPlayer(p));
	}

	return allPlayer;
    }

    public void setPlayer(List<Player> players) {
	for (Player p : players) {
	    this.players.put(p.getName(), 0);
	}

    }

    public boolean isSomeoneInGameRoom() {
	// 해당 게임룸에 누군가 플레이 중인지 반환
	return (this.players.size() > 0);
    }

    public boolean isActivated() {
	return this.activated;
    }

    public void setActivated(boolean activated) {
	this.activated = activated;
    }

    public void plusScore(Player p, int amount) {
	int previousScore = this.players.get(p.getName());
	this.players.put(p.getName(), previousScore + amount);
    }

    public void minusScore(Player p, int amount) {
	int previousScore = this.players.get(p.getName());
	this.players.put(p.getName(), previousScore - amount);
    }

    public List<Integer> getScore() {
	List<Integer> scores = new ArrayList<>();
	for (int c : this.players.values()) {
	    scores.add(c);
	}
	return scores;
    }

    public void setScore(List<Integer> scores) {
//	this.score = scores;
    }

    public int getTimeLimit() {
	return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
	this.timeLimit = timeLimit;
    }

    public MiniGameType getGameType() {
	return gameType;
    }

    public void setGameType(MiniGameType gameType) {
	this.gameType = gameType;
    }

    @Override
    public String toString() {
	return "MiniGame " + "\nplayer=" + this.getPlayer() + ", \nActivated=" + activated + ", \nscore="
		+ this.getScore() + ", \ntimeLimit=" + timeLimit + ", \ngameType=" + gameType + "]";
    }

}
