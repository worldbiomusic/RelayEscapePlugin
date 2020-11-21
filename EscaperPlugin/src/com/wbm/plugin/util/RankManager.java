package com.wbm.plugin.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.wbm.plugin.data.PlayerData;
import com.wbm.plugin.util.general.BroadcastTool;

public class RankManager
{
	/*
	 * [rank list]
	 * token
	 * challengingCount
	 * clearCount
	 * RoomCount
	 */
	PlayerDataManager pDataManager;
	RoomManager roomManager;
	
	public RankManager(PlayerDataManager pDataManager,
	RoomManager roomManager) {
		this.pDataManager = pDataManager;
		this.roomManager = roomManager;
	}
	
	public List<PlayerData> getTokenRankList() {
		List<PlayerData> list = new ArrayList<PlayerData>(this.pDataManager.getPlayerData().values());
		
		Comparator<PlayerData> comparator = new Comparator<PlayerData>() {

			@Override
			public int compare(PlayerData o1, PlayerData o2)
			{
				return o2.getToken() - o1.getToken(); 
			}
			
		};
		
		Collections.sort(list, comparator);
		
		
//		BroadcastTool.debug(list.toString());
		return list;
	}
	
	public List<PlayerData> getChallengingCountRankList() {
		List<PlayerData> list = new ArrayList<PlayerData>(this.pDataManager.getPlayerData().values());
		
		Collections.sort(list, (o1, o2) -> o2.getChallengingCount() - o1.getChallengingCount());
//		BroadcastTool.debug(list.toString());
		
		return list;
	}
	
	public List<PlayerData> getClearCountRankList() {
		List<PlayerData> list = new ArrayList<PlayerData>(this.pDataManager.getPlayerData().values());
		
		Collections.sort(list, (o1, o2) -> o2.getClearCount() - o1.getClearCount());
//		BroadcastTool.debug(list.toString());
		
		return list;
	}
	
	public List<PlayerData> getRoomCountRankList() {
		/*
		 * player가 가지고 있는 room갯수에 따른 rank
		 */
		List<PlayerData> list = new ArrayList<PlayerData>(this.pDataManager.getPlayerData().values());
		
		Comparator<PlayerData> comparator = new Comparator<PlayerData>() {

			@Override
			public int compare(PlayerData o1, PlayerData o2)
			{
				int o1Count = roomManager.getOwnRooms(o1.getName()).size();
				int o2Count = roomManager.getOwnRooms(o2.getName()).size();
				return o2Count - o1Count;
			}
			
		};
		
		Collections.sort(list, comparator);
//		BroadcastTool.debug(list.toString());
		
		return list;
	}
	
}























