package com.wbm.plugin.util.minigame.games;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;

import com.wbm.plugin.data.MiniGameLocation;
import com.wbm.plugin.util.enums.MiniGameType;
import com.wbm.plugin.util.general.BlockTool;
import com.wbm.plugin.util.general.BroadcastTool;
import com.wbm.plugin.util.minigame.SoloMiniGame;

public class FindTheRed extends SoloMiniGame {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /*
     * 게임소개 노랑꽃중에서 빨간꽃을 부수면 점수 얻는 게임
     */
    public FindTheRed() {
	super(MiniGameType.FIND_THE_RED);
    }

    @Override
    public void processEvent(Event event) {
	if (event instanceof BlockBreakEvent) {
	    BlockBreakEvent e = (BlockBreakEvent) event;
	    Block b = e.getBlock();

	    // score
	    if (b.getType() == Material.RED_ROSE) {
		BroadcastTool.sendMessage(this.getAllPlayer(), "+1");
		this.plusScore(1);
	    } else if (b.getType() == Material.YELLOW_FLOWER) {
		BroadcastTool.sendMessage(this.getAllPlayer(), "-2");
		this.minusScore(2);
	    }

	    // 블럭 재정비
	    this.generateNewBlocks();
	}
    }

    @Override
    public void runTaskAfterStartGame() {
	// 블럭 재정비
	this.generateNewBlocks();
    }

    @Override
    public String[] getGameTutorialStrings() {
	String[] msg = { "Break Red Rose: +1", "Break Yellow flower: -2", };

	return msg;
    }

    private void generateNewBlocks() {
	// 블럭 재정비
	Material yellow = Material.YELLOW_FLOWER;
	List<Material> flowers = new ArrayList<>();

	for (int i = 0; i < this.getGameBlockCount(); i++) {
	    flowers.add(yellow);
	}

	int r = (int) (Math.random() * 16);
	flowers.set(r, Material.RED_ROSE);

	BlockTool.setBlockWithMaterial(MiniGameLocation.FIND_THE_RED_POS1, MiniGameLocation.FIND_THE_RED_POS2, flowers);
    }

}
