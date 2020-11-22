package com.wbm.plugin.util;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import com.wbm.plugin.data.MiniGameLocation;
import com.wbm.plugin.data.PlayerData;
import com.wbm.plugin.util.enums.MiniGame;
import com.wbm.plugin.util.general.BroadcastTool;
import com.wbm.plugin.util.general.TeleportTool;

public class MiniGameManager
{
	// MiniGame이 이용이 가능한지 체크하기 위한 Map (비어있으면 true, 이용중이면 false)
	private Map<MiniGame, Boolean> gameRoom;
	
	PlayerDataManager pDataManager;
	
	public MiniGameManager(PlayerDataManager pDataManager) {
		this.pDataManager = pDataManager;
		
		this.gameRoom = new HashMap<MiniGame, Boolean>();
		
		// 모든 미니게임 false로 초기화
		for(MiniGame game : MiniGame.values()) {
			this.gameRoom.put(game, true);
		}
	}
	
	public boolean isEmpty(MiniGame game) {
		return this.gameRoom.get(game);
	}
	
	public void enterRoom(MiniGame game, int fee, Player p) {
		/* 1.미니게임 표지판 확인
		 * 2.사람 들어있는지 확인
		 * 3.token충분한지 확인
		 * 4.title 확인해서 게임룸 위치로 tp
		 * 5.시작
		 */
		PlayerData pData = this.pDataManager.getPlayerData(p.getUniqueId());
		
		// 2.사람 들어있는지 확인
		if(! this.isEmpty(game)) {
			BroadcastTool.sendMessage(p, "someone is already playing game");
			return;
		}
		
		// 3.token충분한지 확인 후 감소
		if(pData.getToken() < fee) {
			BroadcastTool.sendMessage(p, "you need more token");
			return;
		}
		// 감소
		pData.subToken(fee);
		
		// 4.title 확인해서 게임룸 위치로 tp
		Location gameRoom = game.getRoomLocation();
		TeleportTool.tp(p, gameRoom);
		
		// player가 들어갔으므로 이용불가능(false)으로 변경 
		this.gameRoom.put(game, false);
	}
	
	public void exitRoom(MiniGame game) {
		this.gameRoom.put(game, false);
	}

	public void breakBlock(BlockBreakEvent e)
	{
		/*
		 * MiniGame 블럭이 파괴됬을때 취할 행동들 (블럭부수진 위치에 따라 각 미니게임 클래스로 넘겨주기)
		 */
		Location loc = e.getBlock().getLocation();
		
//		MiniGame game = MiniGameLocation.getMiniGameWithLocation(loc);
	}
}





















