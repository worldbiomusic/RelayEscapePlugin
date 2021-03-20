package com.wbm.plugin.util.minigame;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.scheduler.BukkitTask;

import com.wbm.plugin.Main;
import com.wbm.plugin.data.PlayerData;
import com.wbm.plugin.util.PlayerDataManager;
import com.wbm.plugin.util.enums.MiniGameType;
import com.wbm.plugin.util.general.BroadcastTool;
import com.wbm.plugin.util.general.Counter;
import com.wbm.plugin.util.general.InventoryTool;
import com.wbm.plugin.util.general.PlayerTool;
import com.wbm.plugin.util.general.SpawnLocationTool;
import com.wbm.plugin.util.general.TeleportTool;
import com.wbm.plugin.util.shop.ShopGoods;

import net.md_5.bungee.api.ChatColor;

public abstract class MiniGame {
	/*
	 * 미니게임 랭크는 단순히 MiniGameRankManager에서 관리하고,
	 * 
	 * 미니게임 처리로직은 MiniGame에서 관리함
	 */
	public enum ExitReason {
		SELF_EXIT, RELAY_TIME_CHANGED;
	}

	public MiniGame(MiniGameType gameType) {
		this.gameType = gameType;
		this.initGameSettings();

		this.rankData = new HashMap<>();
	}

	public void initGameSettings() {
		this.activated = false;
		// 먼저 실행중인 task취소하고 초기화
		this.stopAllTasks();
		this.startTask = this.exitTask = this.timerTask = null;
	}

	static protected PlayerDataManager pDataManager;
	public static MiniGameRankManager miniGameRankManager;

	protected boolean activated;
	protected BukkitTask startTask, exitTask, timerTask;
	protected MiniGameType gameType;

	// 각 미니게임의 랭크데이터 관리 변수(BattleMiniGame은 사용 안함)
	protected Map<String, Integer> rankData;

	public void runTaskAfterStartGame() {

	};

	public void runTaskBeforeExitGame() {

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

	public boolean isActivated() {
		return this.activated;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
	}

	public MiniGameType getGameType() {
		return gameType;
	}

	public void setGameType(MiniGameType gameType) {
		this.gameType = gameType;
	}

	protected void reserveGameTasks(PlayerDataManager pDataManager) {
		/*
		 * 게임 활성화, 퇴장 task 예약
		 */
		// 전체 공지로 게임 룸이 만들어졌다는것을 알리기 (플레이어 모집을 위해서)
		BroadcastTool.sendMessageToEveryone(
				"" + ChatColor.GREEN + ChatColor.BOLD + this.gameType.name() + ChatColor.WHITE + " 미니게임이 만들어졌습니다");

		// this.waitingTime 초 후 실행
		this.reserveActivateGameTask();

		// exitGame(): this.waitingTime + this.timeLimit 초 후 실행
		this.reserveExitGameTask(pDataManager);
	}

	protected void reserveActivateGameTask() {
		this.startTask = Bukkit.getScheduler().runTaskLater(Main.getInstance(), new Runnable() {
			@Override
			public void run() {
				// activated = true를 waitingTime후에 실행하는 이유:
				// block event가 왔을때 activated가 true일때만 실행되게 했으므로
				activated = true;

				// title
				BroadcastTool.sendTitle(getAllPlayer(), "시작", "");

				// start game 후에 실행할 작업
				runTaskAfterStartGame();
			}
		}, 20 * getWaitingTime());
	}

	protected void reserveExitGameTask(PlayerDataManager pDataManager) {
		this.exitTask = Bukkit.getScheduler().runTaskLater(Main.getInstance(), new Runnable() {
			@Override
			public void run() {
				exitGame(pDataManager);

				// exit game 후에 실행할 작업
				runTaskBeforeExitGame();
			}
		}, 20 * (getWaitingTime() + getTimeLimit()));
	}

	protected void setupPlayerSettings(Player p, PlayerData pData) {
		/*
		 * 게임 초기화는 이미 했으므로 플레이어관련한것만 세팅
		 */
		// player 등록
		this.registerPlayer(p);

		// 게임룸 위치로 tp
		Location gameRoom = this.gameType.getSpawnLocation();
		TeleportTool.tp(p, gameRoom);

		// info 전달
		this.notifyInfo(p);

		// pdata에 미니게임 등록
		pData.setMinigame(this.gameType);

		// 플레이어 상태 초기화
		this.makePlayerPureState(p);
	}

	void makePlayerPureState(Player p) {
		// 플레이어 상태 초기화
		// 상태 초기화
		PlayerTool.removeAllState(p);
		// 힐, 배고픔 충전
		PlayerTool.heal(p);
		// inv초기화
		InventoryTool.clearPlayerInv(p);
	}

	void notifyInfo(Player p) {
		// player에게 정보 전달
		this.printGameTutorial(p);

		// print all rank
		miniGameRankManager.printAllRank(this.gameType, p);
	}

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
		BroadcastTool.sendMessage(p, ChatColor.BOLD + "[규칙]");
		BroadcastTool.sendMessage(p, "제한 시간: " + this.getTimeLimit());
		for (String msg : this.getGameTutorialStrings()) {
			BroadcastTool.sendMessage(p, msg);
		}
	}

	protected final void startTimer() {
		/*
		 * 1초마다 모든 플레이어에게 Counter의 수를 send title함
		 */
		Counter timer = new Counter(this.getWaitingTime());

		this.timerTask = Bukkit.getScheduler().runTaskTimer(Main.getInstance(), new Runnable() {
			@Override
			public void run() {
				// send title
				BroadcastTool.sendTitle(getAllPlayer(), timer.getCount() + "", "", 0.2, 0.6, 0.2);
				timer.removeCount(1);

				// 0이하에서는 취소
				if (timer.getCount() <= 0) {
					timerTask.cancel();
				}
			}
		}, 0, 20);
	}

	public int getFee() {
		return this.gameType.getFee();
	}

	public int getWaitingTime() {
		return this.gameType.getWaitingTime();
	}

	public int getTimeLimit() {
		return this.gameType.getTimeLimit();
	}

	public int getGameBlockCount() {
		return this.gameType.getGameBlockCount();
	}

	public Location getGamePos1() {
		return this.gameType.getGamePos1();
	}

	public Location getGamePos2() {
		return this.gameType.getGamePos2();
	}

	public void exitGame(PlayerDataManager pDataManager) {
		// 미니게임 종료 공지
		BroadcastTool.sendMessageToEveryone("" + ChatColor.RED + ChatColor.BOLD + this.gameType.name() + ChatColor.WHITE
				+ " 미니게임이 끝났습니다" + ChatColor.WHITE);

		// runTaskBeforeExitGame() 실행
		this.runTaskBeforeExitGame();

		// 모든 플레이어 상태 원상복구
		for (Player p : this.getAllPlayer()) {
			// 힐, 배고픔 충전
			PlayerTool.heal(p);
			// 포션효과 제거
			PlayerTool.removeAllPotionEffects(p);
		}

		// Goods제공
		InventoryTool.clearPlayerInv(this.getAllPlayer());
		ShopGoods.giveGoodsToPleyers(pDataManager, this.getAllPlayer());

		// player lobby로 tp
		TeleportTool.tp(this.getAllPlayer(), SpawnLocationTool.LOBBY);

	}

	public boolean checkPlayerCountFull() {
		// 인원수 꽉 찬지 검사
		int currentPlayerCount = this.getAllPlayer().size();
		int maxPlayerCount = this.gameType.getMaxPlayerCount();
		if (currentPlayerCount >= maxPlayerCount) {
			return true;
		} else {
			return false;
		}
	}

	public void enterRoom(Player p, PlayerDataManager pDataManager) {
	}

	// ============sub class 들에서 상황에 맞게 각각 다르게 구현되어야 하는 메소드들=============

	public abstract void processEvent(Event event);

	// tutorial strings
	public abstract String[] getGameTutorialStrings();

	public abstract boolean isPlayerPlayingGame(Player p);

	public abstract void processHandlingMiniGameExitDuringPlaying(Player p, PlayerDataManager pDataManager,
			MiniGame.ExitReason reason);

	public abstract List<Player> getAllPlayer();

	// 각 게임의 참여 멤버에 플레이어 추가
	public abstract void registerPlayer(Player p);
}
