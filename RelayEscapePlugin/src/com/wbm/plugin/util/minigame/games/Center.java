package com.wbm.plugin.util.minigame.games;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import com.wbm.plugin.util.Setting;
import com.wbm.plugin.util.enums.MiniGameType;
import com.wbm.plugin.util.general.BroadcastTool;
import com.wbm.plugin.util.general.TeleportTool;
import com.wbm.plugin.util.minigame.BattleMiniGame;

public class Center extends BattleMiniGame {

	Location pSpawnLoc;
	Material deathBlock;
	private int killCount;

	Location viewLoc;

	public Center() {
		super(MiniGameType.중심잡기);

		this.pSpawnLoc = Setting.getAbsoluteLocation(-56.5, 9, 162.5);

		viewLoc = this.gameType.getSpawnLocation();
		deathBlock = Material.JACK_O_LANTERN;
	}

	@Override
	public void initGameSettings() {
		// TODO Auto-generated method stub
		super.initGameSettings();

		// killcount 초기화
		this.killCount = 0;
	}

	@Override
	public void processEvent(Event event) {
		if (event instanceof PlayerMoveEvent) {
			PlayerMoveEvent e = (PlayerMoveEvent) event;
			Player loser = e.getPlayer();
			Material belowBlock = loser.getLocation().subtract(0, 1, 0).getBlock().getType();

			// 바닥에 deathBlock으로 떨어진지 확인후 점수 차감
			if (belowBlock == deathBlock) {
				BroadcastTool.sendMessage(loser, "당신은 죽었습니다!");
				this.minusScore(loser, this.killCount + 1);
				// killcount 증가
				this.killCount++;
				// viewLoc으로 이동
				TeleportTool.tp(loser, viewLoc);

				// 게임 끝난지 체크
				if (this.checkGameFinish()) {
					this.exitGame(pDataManager);
				}
			}

		} else if (event instanceof PlayerToggleSneakEvent) {
//	    System.out.println("SENAK!!!!!!!!!!!!");
			PlayerToggleSneakEvent e = (PlayerToggleSneakEvent) event;
			Player p = e.getPlayer();
			BroadcastTool.sendTitle(p, "웅크렸습니다!", "");
			TeleportTool.tp(p, p.getLocation().subtract(0, 1, 0));
		}
	}

	private boolean checkGameFinish() {
		// ex. 3명이면 (3명 == 2명죽음+1) 일때 게임 종료
		return this.getAllPlayer().size() == (this.killCount + 1);
	}

	@Override
	public void runTaskAfterStartGame() {
		super.runTaskAfterStartGame();

		// 스폰위치로 이동
		for (Player p : this.getAllPlayer()) {
			TeleportTool.tp(p, this.pSpawnLoc);
			// 처음에 playerCount만큼 점수 부여
			this.plusScore(p, this.getAllPlayer().size());
		}
	}

	@Override
	public String[] getGameTutorialStrings() {
		return new String[] { "게임시작: +(참가자 수)", "fall: -(죽은사람 수+1)", "웅크리기: 떨어짐" };
	}

}
