package com.wbm.plugin.util.minigame;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import com.wbm.plugin.data.PlayerData;
import com.wbm.plugin.util.PlayerDataManager;
import com.wbm.plugin.util.enums.MiniGameType;
import com.wbm.plugin.util.general.BroadcastTool;
import com.wbm.plugin.util.general.InventoryTool;
import com.wbm.plugin.util.general.SpawnLocationTool;
import com.wbm.plugin.util.general.TeleportTool;

import net.md_5.bungee.api.ChatColor;

public abstract class SoloMiniGame extends MiniGame {
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
	private Player player;

	protected int score;

	public SoloMiniGame(MiniGameType gameType) {
		super(gameType);
	}

	public void initGameSettings() {
		super.initGameSettings();
		this.registerPlayer(null);
		this.score = 0;
	}

	@Override
	public void enterRoom(Player p, PlayerDataManager pDataManager) {
		super.enterRoom(p, pDataManager);
		PlayerData pData = pDataManager.getPlayerData(p.getUniqueId());

		// 사람 들어있는지 확인
		// SoloMiniGame: 사람 있으면 못들어감
		// MultiCooperativeMiniGame: master가 있으면 허락맡고 입장
		// MultiBattleMiniGame: 인원수 full 아니면 그냥 입장

		// 누군가 있을때
		if (this.isSomeoneInGameRoom()) {
			BroadcastTool.sendMessage(p, "누군가 이미 플레이 중입니다");
			return;
		} else { // 아무도 없을때
			// 먼저: token충분한지 검사
			if (!pData.minusToken(this.getFee())) {
				BroadcastTool.sendMessage(p, "토큰이 더 필요합니다");
				return;
			}
			// init variables
			this.prepareGame();
			// player관련 세팅
			this.setupPlayerSettings(p, pData);
			// start game
			this.reserveGameTasks(pDataManager);
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

	public void exitGame(PlayerDataManager pDataManager) {
		super.exitGame(pDataManager);
		/*
		 * print game result 보상 지급 score rank 처리 player 퇴장 (lobby로) inventory 초기화 게임 초기화
		 */

		// print game result
		this.printGameResult();

		// 보상 지급
		this.payReward(pDataManager);

		// score rank 처리
		miniGameRankManager.updatePlayerRankData(this.gameType, this.player.getName(), this.score);

		// pData minigame 초기화
		PlayerData pData = pDataManager.getPlayerData(this.player.getUniqueId());
		pData.setNull();

		// 초기화
		this.initGameSettings();
	}

	private void printGameResult() {
		// GAME END print
		BroadcastTool.sendMessage(this.player, "=================================");
		BroadcastTool.sendMessage(this.player, "" + ChatColor.RED + ChatColor.BOLD + "게임 종료");
		BroadcastTool.sendMessage(this.player, "=================================");

		// score 공개
		BroadcastTool.sendMessage(this.player, "당신의 점수: " + this.score);

		// send title
		BroadcastTool.sendTitle(this.player, "게임 종료", "");
		BroadcastTool.sendMessage(this.player, "");
	}

	// 사분위수에서 오름차순으로 FEE의 1/2, 2/2, 3/2, 4/2 배수 토큰 지급, 1등은 6/2배
	public void payReward(PlayerDataManager pDataManager) {
		/*
		 * 오름차순 score (-34, -13, 3, 14, 50 ...)
		 */
//	PlayerData pData = pDataManager.getPlayerData(this.player.getUniqueId());
//
//	// 1,2,3,4분위 안에 속해있을떄 token 지급
//	for (int i = 1; i <= 4; i++) {
//	    String quartilePlayerName = miniGameRankManager.getQuartilePlayerName(this.gameType, i);
//	    int quartileScore = miniGameRankManager.getScore(this.gameType, quartilePlayerName);
//	    if (this.score <= quartileScore) {
//		int rewardToken = (int) ((i / (double) 2) * this.getFee());
//		BroadcastTool.sendMessage(this.player, "Your score is in the top " + i + " quartile");
//		BroadcastTool.sendMessage(this.player, "Reward token: " + rewardToken);
//
//		pData.plusToken(rewardToken);
//
//		return;
//	    }
//	}
//
//	// 1,2,3,4 분위 안에 속해있지 않다는것 = 1등 점수
//	BroadcastTool.sendMessage(this.player, "You are first place");
//	BroadcastTool.sendMessage(this.player, "Reward token: " + this.getFee() * 3);
//
//	pData.plusToken(this.getFee() * 3);

		boolean isFirstScore = false;

		int quartileScore = 0;
		int quartileIndex;
		for (quartileIndex = 1; quartileIndex <= 4; quartileIndex++) {
			String quartilePlayerName = miniGameRankManager.getQuartilePlayerName(this.gameType, quartileIndex);
			quartileScore = miniGameRankManager.getScore(this.gameType, quartilePlayerName);
			// 1,2,3,4분위 어디 속한지 분위 구함
			if (this.score <= quartileScore) {
				break;
			} else if (this.score > quartileScore && quartileIndex == 4) {
				// 1등 score일때
				isFirstScore = true;
			}
		}

		for (Player all : this.getAllPlayer()) {
			PlayerData pData = pDataManager.getPlayerData(all.getUniqueId());
			if (isFirstScore == false) {
				// 속한 분위대로 token 지급
				int rewardToken = (int) ((quartileIndex / (double) 2) * this.getFee());
				String topPercent = " 상위 " + (100 - ((quartileIndex - 1) * 25)) + " ~ " + (100 - (quartileIndex * 25)) + "%";
//				BroadcastTool.sendMessage(this.player, "당신의 점수는 " + quartileIndex + " 분위수 입니다");
				BroadcastTool.sendMessage(this.player, "당신의 점수는 " + topPercent + " 입니다");
				BroadcastTool.sendMessage(this.player, "보상 토큰: " + rewardToken);

				pData.plusToken(rewardToken);
			} else if (isFirstScore) {
				// 1,2,3,4 분위 안에 속해있지 않다는것 = 1등 점수
				BroadcastTool.sendMessage(this.player, "당신의 점수는 1등 입니다");
				BroadcastTool.sendMessage(this.player, "보상 토큰: " + this.getFee() * 3);
				pData.plusToken(this.getFee() * 3);
			}
		}
	}

	/*
	 * 이 메소드는 미니게임에서 플레이어들이 발생한 이벤트를 각 게임에서 처리해주는 범용 메소드 예) if(event instanceof
	 * BlockBreakEvent) { BlockBreakEvent e = (BlockBreakEvent) event; // 생략 }
	 */

	@Override
	public void printGameTutorial(Player p) {
		super.printGameTutorial(p);

		// 자신 최고 점수 출력
		BroadcastTool.sendMessage(p, "");
		int lastScore = miniGameRankManager.getScore(this.gameType, p.getName());
		BroadcastTool.sendMessage(p, "당신의 최근 점수: " + lastScore);

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
			pData.minusToken(this.getFee() * 2);

			this.initGameSettings();
		} else if (reason == MiniGame.ExitReason.RELAY_TIME_CHANGED) {
			this.exitGame(pDataManager);
		}
	}

	// GETTER, SETTER =============================================

	@Override
	public List<Player> getAllPlayer() {
		List<Player> all = new ArrayList<>();
		all.add(this.player);
		return all;
	}

	public boolean isSomeoneInGameRoom() {
		// 해당 게임룸에 누군가 플레이 중인지 반환
		return this.player != null;
	}

	public void plusScore(int amount) {
		this.score += amount;
		BroadcastTool.sendMessage(this.getAllPlayer(), "+" + amount);
	}

	public void minusScore(int amount) {
		this.score -= amount;
		BroadcastTool.sendMessage(this.getAllPlayer(), "-" + amount);
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	@Override
	public void registerPlayer(Player p) {
		this.player = p;
	}

	@Override
	public String toString() {
		return "MiniGame " + "\nplayer=" + player + ", \nActivated=" + activated + ", \nscore=" + score
				+ ", \ngameType=" + gameType + "]";
	}

}
