package com.wbm.plugin.util.minigame.games;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import com.wbm.plugin.util.Setting;
import com.wbm.plugin.util.enums.MiniGameType;
import com.wbm.plugin.util.general.BroadcastTool;
import com.wbm.plugin.util.general.TeleportTool;
import com.wbm.plugin.util.minigame.BattleMiniGame;

public class Bridge extends BattleMiniGame {

	List<Location> pSpawnLocs;
	static Material deathBlock = Material.JACK_O_LANTERN;

	public Bridge() {
		super(MiniGameType.외나무다리);

		this.pSpawnLocs = new ArrayList<>();
		this.pSpawnLocs.add(Setting.getAbsoluteLocation(-58.5, 12, 101.5));
		this.pSpawnLocs.add(Setting.getAbsoluteLocation(-58.5, 12, 149.5));
	}

	@Override
	public void processEvent(Event event) {
		if (event instanceof PlayerMoveEvent) {
			PlayerMoveEvent e = (PlayerMoveEvent) event;
			Player loser = e.getPlayer();
			Material belowBlock = loser.getLocation().subtract(0, 1, 0).getBlock().getType();

			// 바닥에 deathBlock으로 떨어진지 확인, 게임 finish!
			if (belowBlock == deathBlock) {
				BroadcastTool.sendMessage(loser, "패배");

				Player winner = this.getWinner(loser);
				BroadcastTool.sendMessage(winner, "승리");
				this.plusScore(winner, 1);
				this.exitGame(pDataManager);
			}
		} else if (event instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
			// player가 player직접 때릴 떄 데미지 없게 (죽으면 안됨)
			if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
				e.setCancelled(false);
				e.setDamage(0);
			}
		}
	}

	Player getWinner(Player loser) {
		// 어차피 2명이서 하므로 루저가 아니면 무조건 위너
		for (Player p : this.getAllPlayer()) {
			if (!p.equals(loser)) {
				return p;
			}
		}

		return null;
	}

	@Override
	public void runTaskAfterStartGame() {
		super.runTaskAfterStartGame();

		// 각 스폰위치로 이동
		for (int i = 0; i < this.getAllPlayer().size(); i++) {
			Player p = this.getAllPlayer().get(i);
			Location loc = this.pSpawnLocs.get(i);
			TeleportTool.tp(p, loc);
		}
	}

	@Override
	public String[] getGameTutorialStrings() {
		return new String[] { "떨어짐: 게임오버" };
	}

}
