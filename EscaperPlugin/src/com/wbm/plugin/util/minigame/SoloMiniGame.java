package com.wbm.plugin.util.minigame;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

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

public abstract class SoloMiniGame implements Serializable, MiniGame {
    private static final long serialVersionUID = 1L;
    /*
     * 모든 솔로 미니게임은 이 클래스를 상속받아서 만들어져야 함
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
    transient protected Player player;
    transient protected boolean activated;
    transient protected int score;
    transient protected int waitingTime;
    transient protected int fee;

    transient protected int timeLimit;
    protected MiniGameType gameType;

    // 각 미니게임의 랭크데이터 관리 변수
    protected Map<String, Integer> rankData;

    transient protected BukkitTask startTask, exitTask, timerTask;

    public SoloMiniGame(MiniGameType gameType) {
	this.gameType = gameType;
	this.initGameSettings();

	this.rankData = new HashMap<>();
    }

    public void initGameSettings() {
	this.player = null;
	this.activated = false;
	this.score = 0;
	// 먼저 실행중인 task취소하고 초기화
	this.stopAllTasks();
	this.startTask = this.exitTask = this.timerTask = null;
	this.waitingTime = 10;
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

	// 누군가 있을때
	if (this.isSomeoneInGameRoom()) {
	    BroadcastTool.sendMessage(p, "someone is already playing game");
	    return;
	} else { // 아무도 없을때
	    // 먼저: token충분한지 검사
	    if (!pData.minusToken(fee)) {
		BroadcastTool.sendMessage(p, "you need more token");
		return;
	    }
	    // init variables
	    this.prepareGame(p, pData);
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
		BroadcastTool.sendTitle(player, timer.getCount() + "", "", 0.2, 0.6, 0.2);
		timer.removeCount(1);

		// 0이하에서는 취소
		if (timer.getCount() <= 0) {
		    timerTask.cancel();
		}
	    }
	}, 0, 20);
    }

    private void prepareGame(Player p, PlayerData pData) {
	/*
	 * 게임 초기화하고, 게임 준비
	 */
	// 게임 초기화
	this.initGameSettings();

	// player 등록
	this.player = p;

	// 게임룸 위치로 tp
	Location gameRoom = this.gameType.getRoomLocation();
	TeleportTool.tp(this.player, gameRoom);

	// info 전달
	this.notifyInfo(p);

	// pdata에 미니게임 등록
	pData.setMinigame(this.gameType);

	// count down 시작
	this.startTimer();
    }

    void notifyInfo(Player p) {
	// player에게 정보 전달
	this.printGameTutorial(p);

	// print all rank
	MiniGameRankManager.printAllRank(this.rankData, p);
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

		BroadcastTool.sendTitle(player, "START", "");

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

	// score rank 처리
	MiniGameRankManager.updatePlayerRankData(this.rankData, this.player.getName(), this.score);

	// player lobby로 tp
	TeleportTool.tp(this.player, SpawnLocationTool.LOBBY);

	// inventory 초기화
	InventoryTool.clearPlayerInv(this.player);

	// pData minigame 초기화
	PlayerData pData = pDataManager.getPlayerData(this.player.getUniqueId());
	pData.setNull();

	// 초기화
	this.initGameSettings();
    }

    private void printGameResult() {
	// GAME END print
	BroadcastTool.sendMessage(this.player, "=================================");
	BroadcastTool.sendMessage(this.player, "" + ChatColor.RED + ChatColor.BOLD + "Game End");
	BroadcastTool.sendMessage(this.player, "=================================");

	// score 공개
	BroadcastTool.sendMessage(this.player, "Your score: " + this.score);

	// send title
	BroadcastTool.sendTitle(this.player, "Game End", "");
	BroadcastTool.sendMessage(this.player, "");
    }

    // 사분위수에서 오름차순으로 FEE의 1/2, 2/2, 3/2, 4/2 배수 토큰 지급, 1등은 6/2배
    public void payReward(PlayerDataManager pDataManager) {
	/*
	 * 오름차순 score (-34, -13, 3, 14, 50 ...)
	 */
	PlayerData pData = pDataManager.getPlayerData(this.player.getUniqueId());

	// 1,2,3,4분위 안에 속해있을떄 token 지급
	for (int i = 1; i <= 4; i++) {
	    String quartilePlayerName = MiniGameRankManager.getQuartilePlayerName(this.rankData, i);
	    int quartileScore = MiniGameRankManager.getScore(this.rankData, quartilePlayerName);
	    if (this.score <= quartileScore) {
		int rewardToken = (int) ((i / (double) 2) * fee);
		BroadcastTool.sendMessage(this.player, "You are in " + i + " quartile");
		BroadcastTool.sendMessage(this.player, "Reward token: " + rewardToken);

		pData.plusToken(rewardToken);

		return;
	    }
	}

	// 1,2,3,4 분위 안에 속해있지 않다는것 = 1등 점수
	BroadcastTool.sendMessage(this.player, "You are first place");
	BroadcastTool.sendMessage(this.player, "Reward token: " + fee * 3);

	pData.plusToken(fee * 3);
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

	BroadcastTool.sendMessage(p, "");
	int lastScore = MiniGameRankManager.getScore(this.rankData, p.getName());
	BroadcastTool.sendMessage(p, "Your last score: " + lastScore);

//	BroadcastTool.sendMessage(p, "");
//	BroadcastTool.sendMessage(p, this.gameType.name() + " game starts in " + waitingTime + " sec");
//	BroadcastTool.sendMessage(p, "");
    }

    public void runTaskAfterStartGame() {
    }

    public int getGameBlockCount() {
	return MiniGameLocation.getGameBlockCount(this.gameType);
    }

    public void stopAllTasks() {
	if (this.startTask != null) {
	    this.startTask.cancel();
	}
	if (this.exitTask != null) {
	    this.exitTask.cancel();
	}
	if (this.timerTask != null) {
	    this.timerTask.cancel();
	}
    }

    public boolean isPlayerPlayingGame(Player p) {
	return p.equals(this.player);
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
	    // player lobby로 tp
	    TeleportTool.tp(p, SpawnLocationTool.LOBBY);

	    // inventory 초기화
	    InventoryTool.clearPlayerInv(p);

	    // pData minigame 초기화
	    PlayerData pData = pDataManager.getPlayerData(p.getUniqueId());
	    pData.setNull();

	    // 패널티
	    pData.minusToken(this.fee * 2);

	    this.initGameSettings();
	} else if (reason == MiniGame.ExitReason.RELAY_TIME_CHANGED) {
	    this.exitGame(pDataManager);
	}
    }

    // GETTER, SETTER =============================================

    public Player getPlayer() {
	return player;
    }

    public void setPlayer(Player player) {
	this.player = player;
    }

    public boolean isSomeoneInGameRoom() {
	// 해당 게임룸에 누군가 플레이 중인지 반환
	return this.player != null;
    }

    public boolean isActivated() {
	return this.activated;
    }

    public void setActivated(boolean activated) {
	this.activated = activated;
    }

    public void plusScore(int amount) {
	this.score += amount;
    }

    public void minusScore(int amount) {
	this.score -= amount;
    }

    public int getScore() {
	return score;
    }

    public void setScore(int score) {
	this.score = score;
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
	return "MiniGame " + "\nplayer=" + player + ", \nActivated=" + activated + ", \nscore=" + score
		+ ", \ntimeLimit=" + timeLimit + ", \ngameType=" + gameType + "]";
    }

}
