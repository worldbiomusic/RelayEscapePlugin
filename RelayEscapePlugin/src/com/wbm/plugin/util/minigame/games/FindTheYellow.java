package com.wbm.plugin.util.minigame.games;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;

import com.wbm.plugin.util.enums.MiniGameType;
import com.wbm.plugin.util.general.BlockTool;
import com.wbm.plugin.util.minigame.CooperativeMiniGame;

public class FindTheYellow extends CooperativeMiniGame {

    public FindTheYellow() {
	super(MiniGameType.노란꽃_찾기);
    }

    @Override
    public void processEvent(Event event) {
	if (event instanceof BlockBreakEvent) {
	    BlockBreakEvent e = (BlockBreakEvent) event;
	    Block b = e.getBlock();

	    // score
	    if (b.getType() == Material.DANDELION) {
		this.plusScore(1);
	    } else if (b.getType() == Material.POPPY) {
		this.minusScore(2);
	    }

	    // 블럭 재정비
	    this.generateNewBlocks();
	}
    }

    @Override
    public String[] getGameTutorialStrings() {
	String[] msg = { "노란 꽃: +1", "빨간 꽃: -2", };

	return msg;
    }

    @Override
    public void runTaskAfterStartGame() {
	// 블럭 재정비
	this.generateNewBlocks();
    }

    private void generateNewBlocks() {
	// 블럭 재정비
	Material yellow = Material.POPPY;
	List<Material> flowers = new ArrayList<>();

	for (int i = 0; i < this.getGameBlockCount(); i++) {
	    flowers.add(yellow);
	}

	int r = (int) (Math.random() * 16);
	flowers.set(r, Material.DANDELION);

	BlockTool.setBlockWithMaterial(this.getGamePos1(),this.getGamePos2(), flowers);
    }

    @Override
    public void initGameSettings() {
	super.initGameSettings();
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
