package com.wbm.plugin.util.minigame.games;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import com.wbm.plugin.util.enums.MiniGameType;
import com.wbm.plugin.util.general.BlockTool;
import com.wbm.plugin.util.general.InventoryTool;
import com.wbm.plugin.util.general.ItemStackTool;
import com.wbm.plugin.util.minigame.SoloMiniGame;

public class FitTool extends SoloMiniGame {

    /**
     * 
     */

    List<Material> randomBlocks;

    public FitTool() {
	super(MiniGameType.FIT_TOOL);
    }

    @Override
    public void processEvent(Event event) {
	// 부순블럭은 없어지게
	// 모두 부섰나 체크
	// 모두부수면 +1하고 랜덤 블럭으로 재생성

	if (event instanceof BlockBreakEvent) {
	    BlockBreakEvent e = (BlockBreakEvent) event;
	    // 일단 블럭 없어지게 설정
	    Block b = e.getBlock();
	    b.setType(Material.AIR);

	    if (this.checkEmpty()) {
		this.generateRandomBlocks();

		this.plusScore(1);
	    }
	}

    }

    @Override
    public void runTaskAfterStartGame() {
	super.runTaskAfterStartGame();

	// 블럭 재정비
	this.generateRandomBlocks();

	// Tool 지급
	this.giveTools();
    }

    @Override
    public void initGameSettings() {
	super.initGameSettings();
	// randomBlocks초기화
	randomBlocks = new ArrayList<>();

	// pickaxe
	randomBlocks.add(Material.STONE);
	randomBlocks.add(Material.COBBLESTONE);
	randomBlocks.add(Material.GOLD_ORE);
	randomBlocks.add(Material.END_STONE);

	// sword
	randomBlocks.add(Material.COBWEB);
	randomBlocks.add(Material.ACACIA_LEAVES);
	randomBlocks.add(Material.OAK_LEAVES);
	randomBlocks.add(Material.JUNGLE_LEAVES);

	// axe
	randomBlocks.add(Material.PUMPKIN);
	randomBlocks.add(Material.OAK_STAIRS);
	randomBlocks.add(Material.ACACIA_FENCE);
	randomBlocks.add(Material.MELON);

	// shovel
	randomBlocks.add(Material.DIRT);
	randomBlocks.add(Material.SAND);
	randomBlocks.add(Material.GRAVEL);
	randomBlocks.add(Material.SOUL_SAND);

	// etc
	randomBlocks.add(Material.WHITE_WOOL);
    }

    private void giveTools() {
	ItemStack[] items = { ItemStackTool.item(Material.IRON_AXE), ItemStackTool.item(Material.IRON_PICKAXE),
		ItemStackTool.item(Material.IRON_SWORD), ItemStackTool.item(Material.IRON_SHOVEL) };
	InventoryTool.addItemsToPlayers(this.getAllPlayer(), items);
    }

    private boolean checkEmpty() {
	return BlockTool.isAllSameBlockWithItemStack(this.getGamePos1(), this.getGamePos2(),
		ItemStackTool.item(Material.AIR));
    }

    private void generateRandomBlocks() {
	// randomBlocks에서 랜덤하게 블럭 가져와서 채우기
	List<Material> blocks = new ArrayList<>();

	for (int i = 0; i < this.getGameBlockCount(); i++) {
	    int r = (int) (Math.random() * this.randomBlocks.size());
	    blocks.add(this.randomBlocks.get(r));
	}

	BlockTool.setBlockWithMaterial(this.getGamePos1(), this.getGamePos2(), blocks);
    }

    @Override
    public String[] getGameTutorialStrings() {
	String[] tutorial = { "Break ALL Blocks: +1" };

	return tutorial;
    }

}
