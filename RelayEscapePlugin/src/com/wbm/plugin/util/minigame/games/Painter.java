package com.wbm.plugin.util.minigame.games;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;

import com.wbm.plugin.util.enums.MiniGameType;
import com.wbm.plugin.util.general.BlockTool;
import com.wbm.plugin.util.general.InventoryTool;
import com.wbm.plugin.util.general.ItemStackTool;
import com.wbm.plugin.util.minigame.SoloMiniGame;

public class Painter extends SoloMiniGame {

    /*
     * 게임소개 두가지 색깔로 칠해진 블럭들을 '좌'클릭해서 블럭들을 한가지 색으로 통일하면 점수를 얻는 게임
     */

    /**
    * 
    */
    transient private static final long serialVersionUID = 1L;
    transient private List<Material> mats;
    transient Material mat1, mat2;

    public Painter() {
	super(MiniGameType.PAINTER);
    }

    void initVariables() {
//	setup
	this.mats = new ArrayList<>();
	this.mats.add(Material.COAL_ORE);
	this.mats.add(Material.IRON_ORE);
	this.mats.add(Material.GOLD_ORE);
	this.mats.add(Material.DIAMOND_ORE);
    }

    @Override
    public void runTaskAfterStartGame() {
	// setup variables
	this.initVariables();

	// select random material
	selectRandomMat1Mat2();

	// generate new blocks
	this.generateNewBlocks();

	// 곡괭이 지급
	InventoryTool.addItemToPlayers(this.getAllPlayer(), ItemStackTool.item(Material.IRON_PICKAXE));

    }

    @Override
    public void processEvent(Event event) {
	// TODO Auto-generated method stub
	if (event instanceof BlockBreakEvent) {
	    BlockBreakEvent e = (BlockBreakEvent) event;

	    Block b = e.getBlock();

	    // 블럭 바꾸기
	    this.changeBlock(b);

	    // 완성된지 체크
	    if (this.isComplete()) {
		this.plusScore(1);
		this.generateNewBlocks();
	    }

	}
    }

    @Override
    public String[] getGameTutorialStrings() {
	// TODO Auto-generated method stub
	String[] msg = { "All Same Block: +1", };

	return msg;
    }

    private void generateNewBlocks() {
	/*
	 * 두가지 블럭 mat1, mat2으로 섞인 블럭으로 생성
	 */
	List<Material> blockMats = new ArrayList<>();

	for (int i = 0; i < this.getGameBlockCount(); i++) {
	    int r = (int) (Math.random() * 2);
	    if (r == 0) {
		blockMats.add(mat1);
	    } else {
		blockMats.add(mat2);
	    }
	}

	BlockTool.setBlockWithMaterial(this.getGamePos1(), this.getGamePos2(), blockMats);
    }

    private void selectRandomMat1Mat2() {
	// mat1
	int r = (int) (Math.random() * this.mats.size());

	this.mat1 = this.mats.get(r);

	// mat2
	List<Material> lastMats = new ArrayList<>();
	for (Material mat : this.mats) {
	    if (this.mat1 != mat) {
		lastMats.add(mat);
	    }
	}

	int r2 = (int) (Math.random() * lastMats.size());
	this.mat2 = lastMats.get(r2);
    }

    private void changeBlock(Block b) {
	if (b.getType() == mat1) {
	    b.setType(mat2);
	} else {
	    b.setType(mat1);
	}
    }

    private boolean isComplete() {
	return BlockTool.isAllSameBlock(this.getGamePos1(), this.getGamePos2());
    }

}
