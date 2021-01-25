package com.wbm.plugin.util.minigame.games;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import com.wbm.plugin.Main;
import com.wbm.plugin.util.enums.MiniGameType;
import com.wbm.plugin.util.general.BroadcastTool;
import com.wbm.plugin.util.minigame.SoloMiniGame;

public class JumpMap extends SoloMiniGame {

    /*
     * 처음에 timeLimit만큼 점수 주고 5초에 -1씩
     * 
     * event block처리 (RESPAWN, HRUT 사용불가능!)
     * 
     * 호박부섰을떄 finishTimer stop하기, 후에 exitGame()
     * 
     */
    int finishTimer;
    int minusDelay;

    public JumpMap() {
	super(MiniGameType.JUMP_MAP);
	this.minusDelay = 5;
    }

    @Override
    public void processEvent(Event event) {
	if (event instanceof BlockBreakEvent) {
	    BlockBreakEvent e = (BlockBreakEvent) event;
	    Block b = e.getBlock();

	    // score
	    if (b.getType() == Material.JACK_O_LANTERN) {
		this.stopFinishTimer();
		this.exitGame(pDataManager);
	    }
	} else if (event instanceof PlayerMoveEvent) {
	    /*
	     * RESPAWN처리
	     */
//
//	    PlayerMoveEvent e = (PlayerMoveEvent) event;
//	    Player p = e.getPlayer();
//	    Block b = p.getLocation().subtract(0, 1, 0).getBlock();
//	    
//	    // event block 처리
//	    if (ItemStackTool.isSameWithMaterialNData(ItemStackTool.block2ItemStack(b),
//		    ShopGoods.RESPAWN.getItemStack())) {
//		System.out.println("RESPAWN!!!!!!!!!!!!!!!!!");
//		TeleportTool.tp(this.getAllPlayer(), this.getGameType().getSpawnLocation());
//	    }
	}
//	else if (event instanceof EntityDamageEvent)
//	{
//	    EntityDamageEvent e = (EntityDamageEvent) event;
//	    e.setCancelled(true);
//	    
//	    if (e.getEntity() instanceof Player) {
//		Player victim = (Player) e.getEntity();
//		boolean victimIsDead = victim.getHealth() <= e.getFinalDamage();
//		if (victimIsDead) {
//		    PlayerTool.heal(victim);
//		    TeleportTool.tp(this.getAllPlayer(), this.getGameType().getSpawnLocation());
//		}
//	    }
//
//	}

    }

    @Override
    public String[] getGameTutorialStrings() {
	String[] tutorials = { "break JACK_O_LANTERN: finish game", "Start: +" + this.getTimeLimit(),
		"every " + this.minusDelay + " sec: -1" };
	return tutorials;
    }

    @Override
    public void runTaskAfterStartGame() {
	super.runTaskAfterStartGame();

	// 처음에 timeLimit만큼 점수 주기
	this.plusScore(this.getTimeLimit());

	// finishTimer 체크시작
	this.stopFinishTimer();

	this.startFinishTimer();

    }

    private void startFinishTimer() {
	this.finishTimer = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), new Runnable() {

	    @Override
	    public void run() {
		minusScore(1);
	    }
	}, 0, 20 * this.minusDelay);
    }

    private void stopFinishTimer() {
	Bukkit.getScheduler().cancelTask(this.finishTimer);

	BroadcastTool.debug("STOP JUMP MAP!!!!");
    }

    @Override
    public void runTaskBeforeExitGame() {
	super.runTaskBeforeExitGame();

	this.stopFinishTimer();
    }
}
