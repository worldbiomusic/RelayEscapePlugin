package com.wbm.plugin.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockEvent;

import com.wbm.plugin.data.MiniGameLocation;
import com.wbm.plugin.data.PlayerData;
import com.wbm.plugin.util.enums.MiniGameType;
import com.wbm.plugin.util.general.BroadcastTool;
import com.wbm.plugin.util.general.TeleportTool;
import com.wbm.plugin.util.minigame.FindTheRed;
import com.wbm.plugin.util.minigame.MiniGame;
import com.wbm.plugin.util.minigame.Painter;

public class MiniGameManager
{
	// MiniGame 체크하기 위한 Map (이용중이면 true, 비어있으면 false)
	private Map<MiniGameType, MiniGame> games;
	
	PlayerDataManager pDataManager;
	
	public MiniGameManager(PlayerDataManager pDataManager) {
		this.pDataManager = pDataManager;
		
		this.games = new HashMap<>();
		
		List<MiniGame> allGame = new ArrayList<>();
		allGame.add(new FindTheRed());
		allGame.add(new Painter());
		
		
		
		// 모든 미니게임 games에 등록
		for(MiniGame game : allGame) {
			this.games.put(game.getGameType(), game);
		}
		
	}
	
	public boolean isUsing(MiniGameType gameType) {
		return this.games.get(gameType).isInUse();
	}
	
	public void enterRoom(MiniGameType gameType, int fee, Player p) {
		/* 1.미니게임 표지판 확인
		 * 2.사람 들어있는지 확인
		 * 3.token충분한지 확인
		 * 4.title 확인해서 게임룸 위치로 tp
		 * 5.시작
		 */
		PlayerData pData = this.pDataManager.getPlayerData(p.getUniqueId());
		
		// 2.사람 들어있는지 확인
		if(this.isUsing(gameType)) {
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
		Location gameRoom = gameType.getRoomLocation();
		TeleportTool.tp(p, gameRoom);
		
		// player가 들어갔으므로 이용중(true)으로 변경 -> MiniGame의 start()메소드에서 처리할것
		this.games.get(gameType).startGame(p);;
	}
	
	// MiniGame의 exitGame()메소드에서 처리할것임
//	public void exitRoom(MiniGameType game) {
//		this.games.put(game, false);
//	}
	
	public void processBlockEvent(BlockEvent e)
	{
		/*
		 * MiniGame 블럭관련 이벤트에 취할 행동들 (블럭이벤트 위치에 따라 각 미니게임 클래스로 넘겨주기)
		 * 
		 * Event클래스로 하려했는데 Location을 못 가져와서 BlockEvent 이벤트로 함
		 */
		
		Location loc = e.getBlock().getLocation();
		
		MiniGameType gameType = MiniGameLocation.getMiniGameWithLocation(loc);
		
		MiniGame game = this.games.get(gameType);
		
		// gameRoom이 활성화 됫을시에만 반응
		if(game.isInUse()) {
			game.processEvent(e);;
		}
		
		
	}
}





















