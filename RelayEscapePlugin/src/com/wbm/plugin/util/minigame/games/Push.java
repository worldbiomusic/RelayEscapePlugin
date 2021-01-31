package com.wbm.plugin.util.minigame.games;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import com.wbm.plugin.util.Setting;
import com.wbm.plugin.util.enums.MiniGameType;
import com.wbm.plugin.util.general.BroadcastTool;
import com.wbm.plugin.util.general.InventoryTool;
import com.wbm.plugin.util.general.ItemStackTool;
import com.wbm.plugin.util.general.TeleportTool;
import com.wbm.plugin.util.minigame.BattleMiniGame;

public class Push extends BattleMiniGame {

    List<Location> pSpawnLocs;
    Material deathBlock;
    private int killCount;

    Location viewLoc;

    public Push() {
	super(MiniGameType.PUSH);

	this.pSpawnLocs = new ArrayList<>();
	this.pSpawnLocs.add(Setting.getAbsoluteLocation(-80,8,249));
	this.pSpawnLocs.add(Setting.getAbsoluteLocation(-104,8,249));
	this.pSpawnLocs.add(Setting.getAbsoluteLocation(-104,8,221));
	this.pSpawnLocs.add(Setting.getAbsoluteLocation(-80,8,221));
	
	viewLoc = Setting.getAbsoluteLocation(-93, 17, 234);
	deathBlock = Material.JACK_O_LANTERN;
    }

    @Override
    public void initGameSettings() {
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
		BroadcastTool.sendMessage(loser, "you died!");
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

	} else if (event instanceof EntityDamageByEntityEvent) {
	    EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
	    // player가 player직접 때릴 떄 데미지 없게 (죽으면 안됨)
	    if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
		e.setCancelled(false);
		e.setDamage(0);
	    }
	}
    }

    private boolean checkGameFinish() {
	// ex. 3명이면 (3명 == 2명죽음+1) 일때 게임 종료
	return this.getAllPlayer().size() == (this.killCount + 1);
    }

    @Override
    public void runTaskAfterStartGame() {
	super.runTaskAfterStartGame();

	// 각 스폰위치로 이동, 점수 미리 부여
	for (int i = 0; i < this.getAllPlayer().size(); i++) {
	    Player p = this.getAllPlayer().get(i);
	    Location loc = this.pSpawnLocs.get(i);
	    TeleportTool.tp(p, loc);

	    // 처음에 playerCount만큼 점수 부여
	    this.plusScore(p, this.getAllPlayer().size());

	    // 밀치기 막대 부여
	    InventoryTool.addItemToPlayer(p,
		    ItemStackTool.enchant(ItemStackTool.item(Material.STICK), Enchantment.KNOCKBACK, 2));
	}
    }

    @Override
    public String[] getGameTutorialStrings() {
	return new String[] { "GameStart: +(playerCount)", "fall: -(diedPlayerCount+1)" };
    }

}
