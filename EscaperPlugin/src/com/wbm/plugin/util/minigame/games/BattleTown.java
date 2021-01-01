package com.wbm.plugin.util.minigame.games;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import com.wbm.plugin.util.Setting;
import com.wbm.plugin.util.enums.MiniGameType;
import com.wbm.plugin.util.general.BroadcastTool;
import com.wbm.plugin.util.general.InventoryTool;
import com.wbm.plugin.util.general.PlayerTool;
import com.wbm.plugin.util.general.TeleportTool;
import com.wbm.plugin.util.minigame.BattleMiniGame;

public class BattleTown extends BattleMiniGame {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private int killCount;

    public BattleTown() {
	super(MiniGameType.BATTLE_TOWN);
    }

    @Override
    public void processEvent(Event event) {
	if (event instanceof EntityDamageByEntityEvent) {
	    EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
	    // 기본적으로 true이지만 여기선 false 로 변경해서 때리기 가능하게
	    e.setCancelled(false);
	    BroadcastTool.debug("EVENt!!!!!!!!!!!");

	    // player가 player직접 때려 죽였을때
	    Player victim = null, damager = null;
	    if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
		victim = (Player) e.getEntity();
		damager = (Player) e.getDamager();
	    } else if (e.getEntity() instanceof Player && e.getDamager() instanceof Arrow) {
		// 화살처리
		BroadcastTool.debug("ARROW HIT!!!!!!!!!!!");
		victim = (Player) e.getEntity();
		LivingEntity shooter = (LivingEntity) ((Arrow) e.getDamager()).getShooter();
		if (shooter instanceof Player) {
		    damager = (Player) shooter;
		} else {
		    return;
		}

	    } else {
		return;
	    }

	    // 처리 시작
	    boolean victimIsDead = victim.getHealth() <= e.getFinalDamage();
	    if (victimIsDead) {
		BroadcastTool.debug("VICTIM DEAD!!!!!!!!!!!");
		// victim은 맵 위에서 구경해야 함 (다른 미니게임 활동 못하게(게임 끝날때 위치이동때문예))
		TeleportTool.tp(victim, this.getDeadPlayerRepsawnLocation());
		PlayerTool.heal(victim);
		BroadcastTool.sendTitle(victim, "YOU DIE", "");
		BroadcastTool.sendMessage(victim, "You have to stay this minigame area until minigame finish");
		// damager
		this.plusScore(damager, 1);
		BroadcastTool.sendMessage(damager, "+1");

		// killcount 증가
		this.killCount += 1;

		// 몇명남은지 체크 (1명 남으면 게임 종료)
		int remainPlayers = this.getAllPlayer().size() - killCount;
		if (remainPlayers <= 1) {
		    this.exitGame(pDataManager);
		}
	    }
	}

    }

    @Override
    public void runTaskAfterStartGame() {
	super.runTaskAfterStartGame();
	// 기본 킷
	for (Player p : this.getAllPlayer()) {
	    InventoryTool.addItemToPlayer(p, new ItemStack(Material.WOOD_SWORD));
	    InventoryTool.addItemToPlayer(p, new ItemStack(Material.GOLDEN_APPLE));
	    InventoryTool.addItemToPlayer(p, new ItemStack(Material.BOW));
	    InventoryTool.addItemToPlayer(p, new ItemStack(Material.ARROW, 10));
	}
	// killcount 초기화
	this.killCount = 0;
    }

    @Override
    public String[] getGameTutorialStrings() {
	String[] msg = { "Kill: +1", "Death: out" };

	return msg;
    }

    Location getDeadPlayerRepsawnLocation() {
	return new Location(Setting.world, 55, 28, 98);
    }
}
