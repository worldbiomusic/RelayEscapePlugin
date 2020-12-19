package com.wbm.plugin.util.minigame;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import com.wbm.plugin.util.general.InventoryTool;
import com.wbm.plugin.util.general.SpawnLocationTool;
import com.wbm.plugin.util.general.TeleportTool;

import net.md_5.bungee.api.ChatColor;

public abstract class CooperativeMiniGame implements Serializable, MiniGameInterface {

    /*
     * 모든 협동 미니게임은 이 클래스를 상속받아서 만들어져야 함
     * 
     * - 협동이라서 SoloMiniGame과 많이 달라서 일단 상속 사용하지 않고 코드 고쳐서 만듬
     * 
     * - MiniGameRankManager에서 Map<String, Integer>를 사용하기 때문에 협동게임의 여러사람 이름을 콤마(,) 로
     * 구분해서 함 (검색은 이름을 포함여부를 통해서 관리=멤버 구성만 같으면 같은 String으로 판별)
     * 
     * 
     * 튜닝할 수 있는 것
     * 
     * -runTaskAfterStartGame(): 메소드 오버라이딩하면 시작시 실행해줌(예.게임 아이템 추가)
     * 
     * [주의] timeLimit, gameType, rankData는 파일로 저장되야되서 transient 선언안함 새로운 변수 추가할때
     * transient 항상 고려하기
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
    private static final long serialVersionUID = 1L;

    // 방장=master
    transient protected Player master;
    // 모든 플레이어들(방장 포함)
    transient protected List<Player> player;
    transient protected boolean activated;
    transient protected int score;
    transient protected static int waitingTime = 30;
    protected int fee;

    protected int timeLimit;
    protected MiniGameType gameType;

    // 각 미니게임의 랭크데이터 관리 변수
    protected Map<String, Integer> rankData;

    transient protected BukkitTask startTask, exitTask;

    transient protected List<Player> waitPlayers;

    transient protected int counted;

    public CooperativeMiniGame(MiniGameType gameType) {
	this.player = new ArrayList<>();
	this.master = null;
	this.waitPlayers = new ArrayList<>();
	this.activated = false;
	this.score = 0;
	this.gameType = gameType;
	this.timeLimit = gameType.getTimeLimit();
	this.fee = gameType.getFee();
	this.counted = 0;

	this.rankData = new HashMap<>();
    }

    public void initGame() {
	this.master = null;
	this.player = new ArrayList<>();
	this.waitPlayers = new ArrayList<>();
	this.activated = false;
	this.score = 0;
	this.startTask = this.exitTask = null;
	this.counted = 0;
    }

    @Override
    public void enterRoom(Player p, PlayerDataManager pDataManager) {
	PlayerData pData = pDataManager.getPlayerData(p.getUniqueId());

	// 사람 들어있는지 확인
	// MiniGame: 사람 있으면 못들어감
	// MultiCooperativeMiniGame: master가 있으면 허락맡고 입장
	// MultiBattleMiniGame: 인원수 full 아니면 그냥 입장
	if (this.isSomeoneInGameRoom()) {
	    BroadcastTool.sendMessage(p, "Wait for master's OK");

	    // 기다리는 player목록에 넣기
	    this.addPlayerToWaitList(p);
	    return;
	} else { // 아무도 없으므로 처음 들어오는 사람이 master
	    // token충분한지 검사
	    if (!pData.minusToken(fee)) {
		BroadcastTool.sendMessage(p, "you need more token");
		return;
	    }
	    // init variables
	    this.initVariables(p);
	    this.startGame(pDataManager);
	}
    }

    private void addPlayerToWaitList(Player p) {
	/*
	 * 기다리는 대기열에 플레이어 추가
	 */
	if (!this.waitPlayers.contains(p)) {
	    // 넣기
	    this.waitPlayers.add(p);
	    // master에게 알리기
	    BroadcastTool.sendMessage(this.master, p.getName() + " wanted to join this game.");
	}
    }

    public void okWaitingPlayer(Player waiter, PlayerData waiterPData) {
	/*
	 * 기다리고 있는 플레이어 수락
	 * 
	 * waitingList에 등록될 조건(/re minigame ok <name>)
	 * 
	 */

	// token충분한지 검사
	if (!waiterPData.minusToken(fee)) {
	    BroadcastTool.sendMessage(waiter, "you need more token to enter minigame " + this.gameType);
	    BroadcastTool.sendMessage(this.master, waiter + " need more token to enter this minigame ");
	    
	    return;
	}

	// 멤버에 추가
	this.player.add(waiter);

	// info 전달
	this.notifyInfo();

	// tp
	Location gameRoom = this.gameType.getRoomLocation();
	TeleportTool.tp(waiter, gameRoom);

	// info
	BroadcastTool.sendMessage(waiter, "you are accepted to this minigame by " + this.master.getName());
    }

    public void kickPlayer(Player p) {
	/*
	 * 같이 할 플레이어(this.players)중 p 강퇴
	 */

	// 멤버 삭제
	int index = this.player.indexOf(p);
	this.player.remove(index);

	// 로비로 위치로 tp
	TeleportTool.tp(p, SpawnLocationTool.LOBBY);

	// info
	BroadcastTool.sendMessage(p, "you kicked by " + this.master.getName());
    }

    private void initVariables(Player p) {
	this.initGame();
	this.master = p;
	this.player.add(p);

	// 게임룸 위치로 tp
	Location gameRoom = this.gameType.getRoomLocation();
	TeleportTool.tp(p, gameRoom);

	// info 전달
	this.notifyInfo();
    }

    private void notifyInfo() {
	for (Player p : this.player) {
	    // player에게 정보 전달
	    this.printGameTutorial(p);

	    // print all rank
	    MiniGameRankManager.printAllRank(this.rankData, p);

	    // count down 시작
	    // 새로 들어왔을때 또 실행하면 겹쳐서 오류남
	    BroadcastTool.sendCountDownTitle(p, waitingTime);
	}
    }

    private void startGame(PlayerDataManager pDataManager) {
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
	String names = "";
	for (Player all : this.player) {
	    names += all.getName() + ", ";
	}
	MiniGameRankManager.updatePlayerRankData(this.rankData, names, this.score);

	// player lobby로 tp
	TeleportTool.tp(this.player, SpawnLocationTool.LOBBY);

	// inventory 초기화
	InventoryTool.clearPlayerInv(this.player);

	// 초기화
	this.initGame();
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
	 * 
	 * 협동 게임은 이름이 ,로 구분되어서 저장됨
	 * 
	 * 예) LLLJH, Realniceness, Steve
	 * 
	 * 순서는 정해지지 않았기 때문에 포함여부 메소드를 따로 만들어야 함
	 */

	for (Player all : this.player) {
	    PlayerData pData = pDataManager.getPlayerData(all.getUniqueId());

	    // 1,2,3,4분위 안에 속해있을떄 token 지급
	    for (int i = 1; i <= 4; i++) {
		String quartilePlayerName = MiniGameRankManager.getQuartilePlayerName(this.rankData, i);
		int quartileScore = MiniGameRankManager.getScore(this.rankData, quartilePlayerName);
		if (this.score <= quartileScore) {
		    int rewardToken = (int) ((i / (double) 2) * fee);
		    BroadcastTool.sendMessage(this.player, "Your team is in " + i + " quartile");
		    BroadcastTool.sendMessage(this.player, "Reward token: " + rewardToken);

		    pData.plusToken(rewardToken);

		    return;
		}
	    }

	    // 1,2,3,4 분위 안에 속해있지 않다는것 = 1등 점수
	    BroadcastTool.sendMessage(this.player, "Your team is first place");
	    BroadcastTool.sendMessage(this.player, "Reward token: " + fee * 3);

	    pData.plusToken(fee * 3);
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
	if (this.startTask != null)
	    this.startTask.cancel();
	if (this.exitTask != null) {
	    this.exitTask.cancel();
	}
    }

    public boolean isPlayerPlayingGame(Player p) {
	/*
	 * master가 this.player에 들어가있으므로 this.player만 검사하면 됨
	 */
	return (this.player.contains(p));
    }

    // GETTER, SETTER =============================================
    public Player getMaster() {
	return this.master;
    }

    public List<Player> getPlayer() {
	return player;
    }

    public void setPlayer(List<Player> player) {
	this.player = player;
    }

    public boolean isSomeoneInGameRoom() {
	// Cooperative에서 방장이 있으면 누군가 필히 존재한다는 뜻
	return this.master != null;
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
