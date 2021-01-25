package com.wbm.plugin.util.minigame.games;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitTask;

import com.wbm.plugin.Main;
import com.wbm.plugin.util.enums.MiniGameType;
import com.wbm.plugin.util.general.BroadcastTool;
import com.wbm.plugin.util.general.InventoryTool;
import com.wbm.plugin.util.general.ItemStackTool;
import com.wbm.plugin.util.general.PlayerTool;
import com.wbm.plugin.util.general.TeleportTool;
import com.wbm.plugin.util.minigame.BattleMiniGame;

public class Critical extends BattleMiniGame {

    int killCount;
    List<Player> deadPlayers;
    int hitIndex;

    int hitTimeLeft;
    static final int hitTimeLimit = 10;

    BukkitTask hitTask;

    public Critical() {
	super(MiniGameType.CRITICAL);

	// setup variables
	this.initVariables();
    }

    @Override
    public void processEvent(Event event) {
	if (event instanceof EntityDamageByEntityEvent) {
	    EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;

	    // player가 player직접 때려 죽였을때
	    Player victim = null;
	    Player damager = null;
	    if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
		victim = (Player) e.getEntity();
		damager = (Player) e.getDamager();

		// 때린사람이 hitIndex순서인지 체크
		if (!this.checkHitPlayer(damager)) {
		    BroadcastTool.sendMessage(damager, "You are not damanger");
		    return;
		}

		// 기본적으로 true이지만 여기선 false 로 변경해서 때리기 가능하게
		e.setCancelled(false);

		// victim 죽은지 체크
		boolean victimIsDead = victim.getHealth() <= e.getFinalDamage();
		if (victimIsDead) {
		    // victim은 맵 위에서 구경해야 함 (다른 미니게임 활동 못하게(게임 끝날때 위치이동때문예))
		    this.deadPlayers.add(victim);

		    TeleportTool.tp(victim, this.getDeadPlayerRepsawnLocation());
		    PlayerTool.heal(victim);
		    BroadcastTool.sendTitle(victim, "YOU DIE", "");
		    BroadcastTool.sendMessage(victim, "You have to stay here until minigame finish");
		    // damager
		    this.plusScore(damager, 1);
		    BroadcastTool.sendMessage(damager, "you kill " + victim.getName());

		    // killcount 증가
		    this.killCount += 1;

		    // 몇명남은지 체크 (1명 남으면 게임 종료)
		    if (this.checkGameFinish()) {
			return;
		    }
		}

		// 때리는 순서 다음차례로 넘어가기
		this.nextHitIndex();

		// hit 타이머 시작
		this.startHitTimer();

//		// 다른 모든 사람들 땅에 닿게
//		this.letPlayersOnGround();
	    }
	}
//	else if (event instanceof PlayerMoveEvent) {
//	    PlayerMoveEvent e = (PlayerMoveEvent) event;
//	    Player p = e.getPlayer();
//
//	    if (!this.checkHitPlayer(p)) {
//		this.letPlayersOnGround();
//	    }
//	}
    }

    private boolean checkGameFinish() {
	int remainPlayers = this.getAllPlayer().size() - killCount;
	if (remainPlayers <= 1) {
	    this.exitGame(pDataManager);
	    return true;
	}
	return false;
    }

    private void startHitTimer() {
	this.stopHitTask();
	hitTimeLeft = hitTimeLimit;

	this.hitTask = Bukkit.getScheduler().runTaskTimer(Main.getInstance(), new Runnable() {
	    @Override
	    public void run() {
		hitTimeLeft -= 1;
		BroadcastTool.sendMessage(getHitPlayer(), "HitTimeLeft: " + hitTimeLeft);
		if (hitTimeLeft <= 0) {
		    BroadcastTool.sendMessage(getHitPlayer(), "You waste too much time!");

		    // 때리는 순서 다음차례로 넘어가기
		    nextHitIndex();

		    // hit 타이머 시작
		    startHitTimer();

//		    // 다른 모든 사람들 땅에 닿게
//		    letPlayersOnGround();

//		    // 몇명남은지 체크 (1명 남으면 게임 종료)
//		    if (checkGameFinish()) {
//			return;
//		    }
		}
	    }
	}, 0, 20 * 1);
    }

    Player getHitPlayer() {
	return this.getAllPlayer().get(this.hitIndex);
    }

    void stopHitTask() {
	if (this.hitTask != null) {
	    this.hitTask.cancel();
	}
    }

//    private void letPlayersOnGround() {
//	for (Player p : this.getAllPlayer()) {
//	    if (!this.deadPlayers.contains(p)) {
//		LocationTool.letEntityOnGround(p);
//	    }
//	}
//    }

    boolean isPlayerDead(Player p) {
	for (Player deadP : this.deadPlayers) {
	    if (p.equals(deadP)) {
		return true;
	    }
	}
	return false;
    }

    private void nextHitIndex() {
	Player hitPlayer = null;
	while (true) {
	    if (this.getAllPlayer().size() == 0) {
		return;
	    }
	    this.hitIndex = (this.hitIndex + 1) % this.getAllPlayer().size();
	    hitPlayer = this.getAllPlayer().get(this.hitIndex);
	    if (!this.isPlayerDead(hitPlayer)) {
		break;
	    }
	}

	BroadcastTool.sendMessage(hitPlayer, "You are now damager");
    }

    private boolean checkHitPlayer(Player damager) {
	Player hitPlayer = this.getAllPlayer().get(this.hitIndex);
	return damager.equals(hitPlayer);
    }

    @Override
    public void runTaskAfterStartGame() {
	super.runTaskAfterStartGame();

	// kit
	InventoryTool.addItemToPlayers(this.getAllPlayer(), ItemStackTool.item(Material.WOOD_SWORD));

	// hungry 조절
	for (Player p : this.getAllPlayer()) {
	    PlayerTool.setHungry(p, 8);
	}

	// hit timer 시작
	this.startHitTimer();
    }

    private void initVariables() {

	// killcount 초기화
	this.killCount = 0;

	// deadPlayes 초기화
	this.deadPlayers = new ArrayList<>();

	// hitIndex 초기화
	this.hitIndex = 0;

	this.hitTimeLeft = 0;
    }

    @Override
    public void runTaskBeforeExitGame() {
	super.runTaskBeforeExitGame();

	// heal
	for (Player p : this.getAllPlayer()) {
	    PlayerTool.heal(p);
	}

	// stop task
	this.stopHitTask();

    }

    @Override
    public String[] getGameTutorialStrings() {
	String[] tutorial = { "kill: +1" };
	return tutorial;
    }

    Location getDeadPlayerRepsawnLocation() {
	return this.gameType.getSpawnLocation().clone().add(0, 4, 0);
    }
}
