package com.wbm.plugin.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;

import com.wbm.plugin.data.PlayerData;
import com.wbm.plugin.util.general.NPCManager;

public class StageManager {
    /*
     * 카테고리(token, 등등)에 맞게 단상에 player npc 를 관리하는 클래스
     */

    // 카테고리별(kind) stage npc
    Map<String, List<String>> stageNPCs;

    // 카테고리별(kind) stage location 리스트
    Map<String, List<Location>> stageLocations;

    RankManager rankManager;
    NPCManager npcManager;

    public StageManager(RankManager rankManager, NPCManager npcManager) {
	this.rankManager = rankManager;
	this.npcManager = npcManager;
	this.stageNPCs = new HashMap<>();
	this.stageLocations = new HashMap<>();
    }

    public void updateStage(String kind) {
	List<Location> locs = this.stageLocations.get(kind);

	// 이전에 stage를 만들었었다면 전에 있던 npc 제거
	if (this.stageNPCs.containsKey(kind)) {
	    for (String npcName : this.stageNPCs.get(kind)) {
		this.npcManager.delete(npcName);
	    }
	}

	// stage 초기화
	this.stageNPCs.put(kind, new ArrayList<>());

	// stage 구성
	List<PlayerData> pDataList = null;
	switch (kind) {
	case "tokenCount":
	    pDataList = this.rankManager.getTokenRankList();
	    break;
	case "challengingCount":
	    pDataList = this.rankManager.getChallengingCountRankList();
	    break;
	case "clearCount":
	    pDataList = this.rankManager.getClearCountRankList();
	    break;
	case "roomCount":
	    pDataList = this.rankManager.getRoomCountRankList();
	    break;
	}

	for (int i = 0; i < locs.size(); i++) {
	    if (i >= pDataList.size()) {
		break;
	    }
	    PlayerData pData = pDataList.get(i);
	    String name = pData.getName();

	    // stage list에 등록
	    this.stageNPCs.get(kind).add(name + "_" + kind);

	    // create NPC
	    this.npcManager.createNPC(locs.get(i), name + "_" + kind, name);
	}
    }

    public void registerLocations(String kind, List<Location> locs) {
	this.stageLocations.put(kind, locs);
    }

    public void updateAllStage() {
	for (String kind : this.stageLocations.keySet()) {
	    this.updateStage(kind);
	}
    }

    public void removeRemainingRankNPCs() {
	for (String kind : this.stageLocations.keySet()) {
	    if (this.stageNPCs.containsKey(kind)) {
		for (String npcName : this.stageNPCs.get(kind)) {
		    this.npcManager.delete(npcName);
		}
	    }
	}
    }
}
