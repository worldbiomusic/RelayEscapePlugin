package com.wbm.plugin.util.minigame.games;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import com.wbm.plugin.Main;
import com.wbm.plugin.util.Setting;
import com.wbm.plugin.util.enums.MiniGameType;
import com.wbm.plugin.util.general.BroadcastTool;
import com.wbm.plugin.util.general.ItemStackTool;
import com.wbm.plugin.util.general.PlayerTool;
import com.wbm.plugin.util.general.TeleportTool;
import com.wbm.plugin.util.minigame.BattleMiniGame;

public class Bang extends BattleMiniGame {

    /*
     * 
     */

    private ItemStack bangItem;

    private List<Location> locs;

    private Location bangBlock;

    private BukkitTask bangTask;

    int killCount;

    public Bang() {
	super(MiniGameType.빵);
    }

    @Override
    public void initGameSettings() {
	super.initGameSettings();
//	setup
	this.bangItem = ItemStackTool.item(Material.WOODEN_SWORD);

	this.locs = new ArrayList<>();
	locs.add(Setting.getAbsoluteLocation(-103.5, 8, 117.5));
	locs.add(Setting.getAbsoluteLocation(-104.5, 8, 118.5));
	locs.add(Setting.getAbsoluteLocation(-103.5, 8, 119.5));
	locs.add(Setting.getAbsoluteLocation(-102.5, 8, 118.5));

	this.bangBlock = Setting.getAbsoluteLocation(-104, 9, 118);

	this.killCount = 0;

	// task 취소
	if (this.bangTask != null) {
	    this.bangTask.cancel();
	}
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

		// bangItem으로 떄린지 체크
		ItemStack damagerItem = damager.getInventory().getItemInMainHand();
		if (!ItemStackTool.isSameWithMaterialNData(this.bangItem, damagerItem)) {
		    // 이벤트 취소
		    e.setCancelled(true);
		    return;
		}

		// victim 죽은지 체크
		boolean victimIsDead = victim.getHealth() <= e.getFinalDamage();
		if (victimIsDead) {
		    // victim은 맵 위에서 구경해야 함 (다른 미니게임 활동 못하게(게임 끝날때 위치이동때문예))

		    TeleportTool.tp(victim, this.gameType.getSpawnLocation());
		    PlayerTool.heal(victim);
		    BroadcastTool.sendTitle(victim, "죽었습니다!", "");
		    BroadcastTool.sendMessage(victim, "미니게임이 끝날 때까지 여기서 기다리세요");
		    // damager
		    this.plusScore(damager, 1);
		    BroadcastTool.sendMessage(damager, victim.getName() + "를 죽였습니다");

		    // killcount 증가
		    this.killCount += 1;

		    // 몇명남은지 체크 (1명 남으면 게임 종료)
		    this.checkGameFinish();
		}
	    }
	}
    }

    private void checkGameFinish() {
	int remainPlayers = this.getAllPlayer().size() - killCount;
	if (remainPlayers <= 1) {
	    this.exitGame(pDataManager);
	    return;
	}
    }

    @Override
    public void runTaskAfterStartGame() {
	super.runTaskAfterStartGame();

	// bang블럭 생성
	bangBlock.getBlock().setType(Material.WHITE_CONCRETE);

	// task 취소
	if (this.bangTask != null) {
	    this.bangTask.cancel();
	}

	this.startBangTimer();
    }

    void startBangTimer() {
	for (int i = 0; i < getAllPlayer().size(); i++) {
	    Player p = getAllPlayer().get(i);
	    // 자리 이동
	    TeleportTool.tp(p, locs.get(i));
	    // 체력, 배고픔 설정
	    p.setHealth(1);
	    PlayerTool.setHungry(p, 1);
	}

	// 랜덤 타임 시작
	int maxBagnTime = this.gameType.getTimeLimit() / 2;

	// 최소 5초 후 시작
	int randomBangTime = (int) (Math.random() * maxBagnTime) + 5;

	this.bangTask = Bukkit.getScheduler().runTaskLater(Main.getInstance(), new Runnable() {

	    @Override
	    public void run() {
		// bang블럭 제거
		bangBlock.getBlock().setType(Material.AIR);

		for (int i = 0; i < getAllPlayer().size(); i++) {
		    Player p = getAllPlayer().get(i);
		    // bangItem 지급
		    int r = (int) (Math.random() * 9);
		    p.getInventory().setItem(r, bangItem);
		    // title
		    BroadcastTool.sendTitle(p, "빵!", "");
		}

	    }
	}, 20 * randomBangTime);

    }

    @Override
    public void runTaskBeforeExitGame() {
	super.runTaskBeforeExitGame();

	// bang블럭 생성
	bangBlock.getBlock().setType(Material.WHITE_CONCRETE);

	// task 취소
	if (this.bangTask != null) {
	    this.bangTask.cancel();
	}
    }

    @Override
    public String[] getGameTutorialStrings() {
	String[] tutorial = { "나무 칼로 때리기: +1", "죽음: 게임 오버" };
	return tutorial;
    }

}
