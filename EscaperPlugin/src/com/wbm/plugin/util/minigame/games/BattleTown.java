package com.wbm.plugin.util.minigame.games;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import com.wbm.plugin.util.enums.MiniGameType;
import com.wbm.plugin.util.general.BroadcastTool;
import com.wbm.plugin.util.general.InventoryTool;
import com.wbm.plugin.util.minigame.BattleMiniGame;

public class BattleTown extends BattleMiniGame {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public BattleTown() {
	super(MiniGameType.BATTLE_TOWN);
    }

    @Override
    public void processEvent(Event event) {
	if(event instanceof EntityDamageByEntityEvent) {
	    EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
	    // 기본적으로 true이지만 여기선 false 로 변경해서 때리기 가능하게
	    e.setCancelled(false);
	} else if(event instanceof PlayerDeathEvent) {
	    PlayerDeathEvent e = (PlayerDeathEvent) event;
	    // player가 player에게 킬당했을시 킬러에게+1
	    // 죽은사람은 그냥 다른 활동하다가 보상받음
	    if(e.getEntity() instanceof Player && e.getEntity().getKiller() instanceof Player) {
		Player killer = e.getEntity().getKiller();
		this.plusScore(killer, 1);
		BroadcastTool.sendMessage(killer, "+1");
	    }
	}
    }
    
    @Override
    public void runTaskAfterStartGame() {
	// 기본 킷
	for(Player p : this.getPlayer()) {
	    InventoryTool.addItemToPlayer(p, new ItemStack(Material.WOOD_SWORD));
	}
    }

    @Override
    public String[] getGameTutorialStrings() {
	String[] msg = { "Kill: +1", "Death: out"};
	
	return msg;
    }

}
