package com.wbm.plugin.util.minigame.games;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import com.wbm.plugin.data.MiniGameLocation;
import com.wbm.plugin.util.PlayerDataManager;
import com.wbm.plugin.util.enums.MiniGameType;
import com.wbm.plugin.util.general.BlockTool;
import com.wbm.plugin.util.general.BroadcastTool;
import com.wbm.plugin.util.general.ItemStackTool;
import com.wbm.plugin.util.minigame.BattleMiniGame;

public class FindTheBlue extends BattleMiniGame{

    private static final long serialVersionUID = 1L;
    private static ItemStack plusItem = ItemStackTool.item(Material.RED_ROSE, (byte)1);

    public FindTheBlue(PlayerDataManager pDataManager) {
	super(MiniGameType.FIND_THE_BLUE, pDataManager);
    }

    @Override
    public void processEvent(Event event) {
	if (event instanceof BlockBreakEvent) {
	    BlockBreakEvent e = (BlockBreakEvent) event;
	    Block b = e.getBlock();
	    @SuppressWarnings("deprecation")
	    ItemStack blockItem = ItemStackTool.item(b.getType(), b.getData());
	    Player p = e.getPlayer();

	    // score
	    if (ItemStackTool.isSameWithMaterialNData(blockItem, plusItem)) {
		BroadcastTool.sendMessage(p, "+1");
		this.plusScore(p, 1);
	    } else if (b.getType() == Material.YELLOW_FLOWER) {
		BroadcastTool.sendMessage(p, "-2");
		this.minusScore(p, 2);
	    }

	    // 블럭 재정비
	    this.generateNewBlocks();
	}
    }

    @Override
    public String[] getGameTutorialStrings() {
	String[] msg = { "Break Yellow flower: +1", "Break Blue flower: -2", };

	return msg;
    }
    
    @Override
    public void runTaskAfterStartGame() {
	super.runTaskAfterStartGame();
	// 블럭 재정비
	this.generateNewBlocks();
    }
    
    private void generateNewBlocks() {
	// 블럭 재정비
	ItemStack yellow = new ItemStack(Material.YELLOW_FLOWER);
	List<ItemStack> flowers = new ArrayList<>();

	for (int i = 0; i < this.getGameBlockCount(); i++) {
	    flowers.add(yellow);
	}

	int r = (int) (Math.random() * 16);
	flowers.set(r, plusItem);

	BlockTool.setBlockWithItemStack(MiniGameLocation.FIND_THE_BLUE_POS1, MiniGameLocation.FIND_THE_BLUE_POS2, flowers);
    }
    
}

//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
