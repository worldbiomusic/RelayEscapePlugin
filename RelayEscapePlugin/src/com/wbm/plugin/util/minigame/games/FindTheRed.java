package com.wbm.plugin.util.minigame.games;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;

import com.wbm.plugin.util.enums.MiniGameType;
import com.wbm.plugin.util.general.BlockTool;
import com.wbm.plugin.util.minigame.SoloMiniGame;

public class FindTheRed extends SoloMiniGame {
	/**
	 * 
	 */

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
			if (b.getType() == Material.POPPY) {
				this.plusScore(1);
			} else if (b.getType() == Material.DANDELION) {
				this.minusScore(2);
			}

			// 블럭 재정비
			this.generateNewBlocks();
		}
	}

	@Override
	public void runTaskAfterStartGame() {
		super.runTaskAfterStartGame();

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
		Material yellow = Material.DANDELION;
		List<Material> flowers = new ArrayList<>();

		for (int i = 0; i < this.getGameBlockCount(); i++) {
			flowers.add(yellow);
		}

		int r = (int) (Math.random() * 16);
		flowers.set(r, Material.POPPY);

		BlockTool.setBlockWithMaterial(this.getGamePos1(), this.getGamePos2(), flowers);
	}

	@Override
	public void initGameSettings() {
		super.initGameSettings();
	}
}
