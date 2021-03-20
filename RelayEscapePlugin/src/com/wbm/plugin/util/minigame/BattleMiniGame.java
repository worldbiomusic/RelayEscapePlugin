package com.wbm.plugin.util.minigame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.wbm.plugin.data.PlayerData;
import com.wbm.plugin.util.PlayerDataManager;
import com.wbm.plugin.util.enums.MiniGameType;
import com.wbm.plugin.util.general.BroadcastTool;
import com.wbm.plugin.util.general.InventoryTool;
import com.wbm.plugin.util.general.SpawnLocationTool;
import com.wbm.plugin.util.general.TeleportTool;
import com.wbm.plugin.util.shop.GoodsRole;
import com.wbm.plugin.util.shop.ShopGoods;

import net.md_5.bungee.api.ChatColor;

public abstract class BattleMiniGame extends MiniGame {

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
	private Map<String, Integer> players;

	// 게임 시작시 SUM(fee token 합) 값 저장(플레이어 나가면 몇명참가인지 못셈)
	int SUM;

	public BattleMiniGame(MiniGameType gameType) {
		super(gameType);
	}

	public void initGameSettings() {
		super.initGameSettings();
		this.players = new HashMap<>();
	}

	@Override
	public void enterRoom(Player p, PlayerDataManager pDataManager) {
		super.enterRoom(p, pDataManager);
		PlayerData pData = pDataManager.getPlayerData(p.getUniqueId());

		// 사람 들어있는지 확인
		// SoloMiniGame: 사람 있으면 못들어감
		// MultiCooperativeMiniGame: master가 있으면 허락맡고 입장
		// MultiBattleMiniGame: 인원수 full 아니면 그냥 입장

		// 인원수 꽉 찬지 검사
		if (this.checkPlayerCountFull()) {
			BroadcastTool.sendMessage(p, this.gameType.name() + " 미니게임 인원수가 꽉 찼습니다");
			return;
		}

		// 먼저: token충분한지 검사
		if (!pData.minusToken(this.getFee())) {
			BroadcastTool.sendMessage(p, "토큰이 부족합니다");
			return;
		}

		// 참가 알림
		BroadcastTool.sendMessage(this.getAllPlayer(), p.getName() + " 가 미니게임에 참여했습니다");

		// 누군가 있을때
		if (this.isSomeoneInGameRoom()) {
			// player관련 세팅
			this.setupPlayerSettings(p, pData);
		} else { // 아무도 없을때는 게임을 prepare해서 초기화상태로 만듬
			// init variables
			this.prepareGame();
			// player관련 세팅
			this.setupPlayerSettings(p, pData);
			// start game
			this.reserveGameTasks(pDataManager);
		}

	}

	@Override
	public void runTaskAfterStartGame() {
		this.SUM = this.players.size() * this.getFee();
//	BroadcastTool.debug("SUM: " + this.SUM);

		// 기본 BATTLE 굿즈 제공
		for (Player p : this.getAllPlayer()) {
			PlayerData pData = pDataManager.getPlayerData(p.getUniqueId());
			for (ShopGoods battleGood : ShopGoods.getGoodsWithGoodsRole(GoodsRole.BATTLE)) {
				if (pData.hasGoods(battleGood)) {
					InventoryTool.addItemToPlayer(p, battleGood.getItemStack());
				}
			}
		}
		
		// 1명이면 게임 바로 끝나게
		if(this.getAllPlayer().size() == 1) {
			this.exitGame(pDataManager);
//			return;
		}
	}

	private void prepareGame() {
		/*
		 * 게임 초기화하고, 게임 준비
		 */
		// 게임 초기화
		this.initGameSettings();

		// count down 시작
		this.startTimer();
	}

	@Override
	void notifyInfo(Player p) {
		// player에게 정보 전달
		this.printGameTutorial(p);
	}

	public void exitGame(PlayerDataManager pDataManager) {
		/*
		 * print game result 보상 지급 score rank 처리 player 퇴장 (lobby로) inventory 초기화 게임 초기화
		 */
		super.exitGame(pDataManager);

		// print game result
		this.printGameResult();

		// 보상 지급
		this.payReward(pDataManager);

		// pData minigame 초기화
		for (Player p : this.getAllPlayer()) {
			PlayerData pData = pDataManager.getPlayerData(p.getUniqueId());
			pData.setNull();
		}

		// 초기화
		this.initGameSettings();
	}

	private void printGameResult() {
		for (Player p : this.getAllPlayer()) {
			// GAME END print
			BroadcastTool.sendMessage(p, "=================================");
			BroadcastTool.sendMessage(p, "" + ChatColor.RED + ChatColor.BOLD + "게임 종료");
			BroadcastTool.sendMessage(p, "=================================");

			// 전체플레이어 score 공개
			BroadcastTool.sendMessage(p, "" + ChatColor.BOLD + "[ 랭크 ]");
			List<Entry<String, Integer>> rank = miniGameRankManager.getDescendingSortedMapEntrys(this.players);
			for (int i = 0; i < rank.size(); i++) {
				Entry<String, Integer> entry = rank.get(i);
				String name = entry.getKey();
				int score = entry.getValue();
				BroadcastTool.sendMessage(p, "[" + (i + 1) + "]" + name + " 점수: " + score);
			}

			// send title
			BroadcastTool.sendTitle(p, "게임 종료", "");
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

		// this.players의 int값을 기준으로 내림차순으로 랭크된 플레이어 목록
		List<Entry<String, Integer>> rank = miniGameRankManager.getDescendingSortedMapEntrys(this.players);

		int firstReward = (int) (this.SUM * 0.3);
		int secondReward = (int) (this.SUM * 0.2);
		int thirdReward = (int) (this.SUM * 0.1);

		int REMAIN = this.SUM;

		// nullPointerException피하기 위해서 코드가 더러움
		String firstPlayer = null, secondPlayer = null, thirdPlayer = null;
		int playerCount = this.getAllPlayer().size();
		if (playerCount >= 1) {
			firstPlayer = rank.get(0).getKey();
			REMAIN -= firstReward;
		}
		if (playerCount >= 2) {
			secondPlayer = rank.get(1).getKey();
			REMAIN -= secondReward;
		}
		if (playerCount >= 3) {
			thirdPlayer = rank.get(2).getKey();
			REMAIN -= thirdReward;
		}

		// REMAIN에서 1,2,3등 뺀 것에서 참가보상 계산
//	BroadcastTool.debug("REMAIN: " + REMAIN);
		int participationReward = (int) (REMAIN * ((double) 1 / this.getAllPlayer().size()));
//	BroadcastTool.debug("participationReward: " + participationReward);
		// reward
		for (Player p : this.getAllPlayer()) {
			PlayerData pData = pDataManager.getPlayerData(p.getUniqueId());

			int reward = participationReward;

			// 1, 2, 3 reward
			if (p.getName().equals(firstPlayer)) {
				reward += firstReward;
			} else if (p.getName().equals(secondPlayer)) {
				reward += secondReward;
			} else if (p.getName().equals(thirdPlayer)) {
				reward += thirdReward;
			}

			// plus token
			pData.plusToken(reward);

			// msg
			BroadcastTool.sendMessage(p, "보상 토큰: " + reward);
		}

	}

	/*
	 * 이 메소드는 미니게임에서 플레이어들이 발생한 이벤트를 각 게임에서 처리해주는 범용 메소드 예) if(event instanceof
	 * BlockBreakEvent) { BlockBreakEvent e = (BlockBreakEvent) event; // 생략 }
	 */

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
			BroadcastTool.sendMessage(this.getAllPlayer(), p.getName() + "님이  " + this.gameType.name() + " 미니게임을 나갔습니다");

			// 패널티
			pData.minusToken(this.getFee() * 2);

			// 게임에 아무도 없을 때 game init & stop all tasks
			if (!this.isSomeoneInGameRoom()) {
				this.initGameSettings();
			}
		} else if (reason == MiniGame.ExitReason.RELAY_TIME_CHANGED) {
			this.exitGame(pDataManager);
		}

	}

	// GETTER, SETTER =============================================

	@Override
	public List<Player> getAllPlayer() {
		/*
		 * String Player name을 Player형 리스트로 반환
		 */
		List<Player> allPlayer = new ArrayList<>();
		for (String p : this.players.keySet()) {
			allPlayer.add(Bukkit.getPlayer(p));
		}

		return allPlayer;
	}

	public boolean isSomeoneInGameRoom() {
		// 해당 게임룸에 누군가 플레이 중인지 반환
		return (this.players.size() > 0);
	}

	public void plusScore(Player p, int amount) {
		int previousScore = this.players.get(p.getName());
		this.players.put(p.getName(), previousScore + amount);

		// info
		BroadcastTool.sendMessage(p, "+" + amount);
	}

	public void minusScore(Player p, int amount) {
		int previousScore = this.players.get(p.getName());
		this.players.put(p.getName(), previousScore - amount);

		// info
		BroadcastTool.sendMessage(p, "-" + amount);
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

	@Override
	public void registerPlayer(Player p) {
		this.players.put(p.getName(), 0);
	}

	@Override
	public String toString() {
		return "MiniGame " + "\nplayer=" + this.getAllPlayer() + ", \nActivated=" + activated + ", \nscore="
				+ this.getScore() + ", \ntimeLimit=" + this.getTimeLimit() + ", \ngameType=" + gameType + "]";
	}

}
