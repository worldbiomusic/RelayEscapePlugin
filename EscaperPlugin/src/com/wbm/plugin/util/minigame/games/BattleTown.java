package com.wbm.plugin.util.minigame.games;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import com.wbm.plugin.data.MiniGameLocation;
import com.wbm.plugin.util.PlayerDataManager;
import com.wbm.plugin.util.Setting;
import com.wbm.plugin.util.enums.MiniGameType;
import com.wbm.plugin.util.general.BroadcastTool;
import com.wbm.plugin.util.general.InventoryTool;
import com.wbm.plugin.util.general.LocationTool;
import com.wbm.plugin.util.minigame.BattleMiniGame;

public class BattleTown extends BattleMiniGame {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public BattleTown(PlayerDataManager pDataManager) {
	super(MiniGameType.BATTLE_TOWN, pDataManager);
    }

    @Override
    public void processEvent(Event event) {
	if (event instanceof EntityDamageByEntityEvent) {
	    EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
	    // 기본적으로 true이지만 여기선 false 로 변경해서 때리기 가능하게
	    e.setCancelled(false);
	} else if (event instanceof PlayerDeathEvent) {
	    PlayerDeathEvent e = (PlayerDeathEvent) event;
	    Player victim = (Player) e.getEntity();
	    // player가 player에게 킬당했을시 킬러에게+1
	    // 죽은사람은 그냥 다른 활동하다가 보상받음
	    if (victim instanceof Player && e.getEntity().getKiller() instanceof Player) {
		Player killer = e.getEntity().getKiller();
		this.plusScore(killer, 1);
		BroadcastTool.sendMessage(killer, "+1");

		// 몇명남은지 체크
//		if (this.getSurvivedPlayer().size() < 2) {
		    BroadcastTool.sendMessage(killer, "sur p: " + this.getSurvivedPlayer().size());
		    this.exitGame(pDataManager);
//		}
	    }

	} else if (event instanceof PlayerRespawnEvent) {
	    PlayerRespawnEvent e = (PlayerRespawnEvent) event;
	    // victim은 맵 위에서 구경해야 함 (다른 미니게임 활동 못하게(게임 끝날때 위치이동때문예))
	    e.setRespawnLocation(new Location(Setting.world, 10, 26, 150));
	    BroadcastTool.sendTitle(e.getPlayer(), "YOU DIE", "");
	    BroadcastTool.sendMessage(e.getPlayer(), "You have to stay this minigame area until minigame finish");
	    
	    BroadcastTool.sendMessage(e.getPlayer(), "sur p: " + this.getSurvivedPlayer().size());
	    this.exitGame(pDataManager);
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
    }

    @Override
    public String[] getGameTutorialStrings() {
	String[] msg = { "Kill: +1", "Death: out" };

	return msg;
    }

    // 현재 area에 몇명있는지 구함
    List<Player> getSurvivedPlayer() {
	List<Player> survivalPlayers = new ArrayList<>();
	for (Player all : this.getAllPlayer()) {
	    // 미니게임 area에 있으면(IN) 살아있는것으로 판단
	    if (LocationTool.isIn(MiniGameLocation.BATTLE_TOWN_POS1, all.getLocation(),
		    MiniGameLocation.BATTLE_TOWN_POS2)) {
		survivalPlayers.add(all);
	    }
	}
	return survivalPlayers;
    }

}
