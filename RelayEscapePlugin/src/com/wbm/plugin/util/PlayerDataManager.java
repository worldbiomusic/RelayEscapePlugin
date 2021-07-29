package com.wbm.plugin.util;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.wbm.plugin.data.PlayerData;
import com.wbm.plugin.util.data.serial.SerialDataMember;
import com.wbm.plugin.util.general.BroadcastTool;

public class PlayerDataManager implements SerialDataMember {
	// server 켜질때, 꺼질때 config데이터 담긴 데이터
	// 이것 하나만 가지고 관리하므로 실시간 데이터임
	private Map<UUID, PlayerData> playerData;

	// 실제적인 Role에 상관없이 현재 Time의 제작자를 가리키는 변수
	// Waiting, Making, Testing Time에서는 무조건 있고,
	// ChallengingTime에는 있을수도(Challenging일때 나간 경우) 없을수도 있음(Challenge아니었을때 나가서 null인
	// 경우)
	Player maker;


	public PlayerDataManager() {
		this.playerData = new HashMap<UUID, PlayerData>();
	}

	public Map<UUID, PlayerData> getOnlyOnlinePlayerData() {
		/*
		 * this.playerData중에서 온라인유저만 간추려서 반환
		 */
		Map<UUID, PlayerData> list = new HashMap<UUID, PlayerData>();
		for (Player p : Bukkit.getOnlinePlayers()) {
			UUID uuid = p.getUniqueId();
			PlayerData pData = this.playerData.get(uuid);
			list.put(uuid, pData);
		}

		return list;
	}

	public Map<UUID, PlayerData> getPlayerData() {
		return this.playerData;
	}

	public void addPlayerData(PlayerData pData) {
		this.playerData.put(pData.getUUID(), pData);
	}

	public PlayerData getPlayerData(UUID uuid) {
		return this.playerData.get(uuid);
	}

	public PlayerData getPlayerData(String name) {
		for (PlayerData pData : this.getPlayerData().values()) {
			if (pData.getName().equalsIgnoreCase(name)) {
				return pData;
			}
		}
		return null;
	}

	public boolean isFirstJoin(UUID uuid) {
		return !(this.playerData.containsKey(uuid));
	}

	public Player getMaker() {
		return this.maker;
	}

	public void registerMaker(Player p) {
		this.maker = p;
	}

	public void unregisterMaker() {
		this.maker = null;
	}

	public boolean doesMakerExist() {
		return this.maker != null;
	}

	public boolean isMaker(Player p) {
		return (p.equals(this.maker));
	}

	@SuppressWarnings("unchecked")
	@Override
	public void installData(Object obj) {
		// TODO Auto-generated method stub
		this.playerData = (Map<UUID, PlayerData>) obj;

		// print console
		BroadcastTool.debug("==================PLAYER DATA==================");
		for (PlayerData pData : this.playerData.values()) {
			BroadcastTool.debug(pData.toString());
		}
	}

	@Override
	public Object getData() {
		// TODO Auto-generated method stub
		return this.playerData;
	}

	@Override
	public String getDataMemberName() {
		// TODO Auto-generated method stub
		return "player";
	}

}

/*
 * 옛날 코드
 */

//package com.wbm.plugin.util;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.UUID;
//
//import org.bukkit.Bukkit;
//import org.bukkit.ChatColor;
//import org.bukkit.GameMode;
//import org.bukkit.entity.Player;
//
//import com.wbm.plugin.data.PlayerData;
//import com.wbm.plugin.util.config.ConfigTest;
//import com.wbm.plugin.util.config.DataMember;
//import com.wbm.plugin.util.enums.Role;
//import com.wbm.plugin.util.general.BroadcastTool;
//
//public class PlayerDataManager implements DataMember
//{
//	// server켜져있을때 online Player만 가지고 있는 데이터
//	private Map<UUID, PlayerData> onlinePlayerList;
//
//	// server 켜질때 config데이터 담긴 데이터
//	// player quit할때 onlinePlayerList에서 데이터를 여기로 저장해주는 데이터 보관소
//	// (서버 꺼질때 config파일에 데이터 넘겨서 저장)
//	private Map<UUID, PlayerData> allPlayerSavigData;
//
//	// 실제적인 Role에 상관없이 현재 Time의 제작자를 가리키는 변수
//	// Waiting, Making, Testing Time에서는 무조건 있고,
//	// ChallengingTime에는 있을수도(Challenging일때 나간 경우) 없을수도 있음(Challenge아니었을때 나가서 null인
//	// 경우)
//	Player maker;
//
//	ConfigTest ct;
//
//	public PlayerDataManager(ConfigTest ct)
//	{
//		this.onlinePlayerList=new HashMap<UUID, PlayerData>();
//		this.allPlayerSavigData=new HashMap<UUID, PlayerData>();
//		this.ct=ct;
//	}
//
//	public void addPlayerData(PlayerData pData)
//	{
//		this.onlinePlayerList.put(pData.getUUID(), pData);
//	}
//
//	public void saveAndRemovePlayerData(UUID uuid)
//	{
//		// server quit할때 사용
//		if(this.onlinePlayerList.containsKey(uuid))
//		{
//			// this.allPlayerSavigData에 데이터 저장
//			PlayerData pData=this.onlinePlayerList.get(uuid);
//			this.allPlayerSavigData.put(uuid, pData);
//
//			// this.onlinePlayerList에서 데이터 삭제
//			this.onlinePlayerList.remove(uuid);
//		}
//	}
//	
//	public PlayerData getSavedPlayerData(UUID uuid) {
//		return this.allPlayerSavigData.get(uuid);
//	}
//
//	public PlayerData getOnlinePlayerData(UUID uuid)
//	{
//		return this.onlinePlayerList.get(uuid);
//	}
//
//	public boolean isFirstJoin(UUID uuid)
//	{
//		return ! (this.allPlayerSavigData.containsKey(uuid));
//	}
//
//	public Player getMaker()
//	{
//		return this.maker;
//	}
//
//	public void registerMaker(Player p)
//	{
//		this.maker=p;
//	}
//
//	public void unregisterMaker()
//	{
//		this.maker=null;
//	}
//
//	public boolean doesMakerExist()
//	{
//		return (this.maker==null) ? false : true;
//	}
//
//	public boolean isMaker(Player p)
//	{
//		return(p.getUniqueId().equals(this.maker.getUniqueId()));
//	}
//
//	public Map<UUID, PlayerData> getAllOnlinePlayers()
//	{
//		return this.onlinePlayerList;
//	}
//
//	public void changePlayerRole(UUID uuid, Role role)
//	{
//		// gamemode, role바꾸기: 각 Role에 정해진 Gamemode가 있기 때문
//		PlayerData pData=this.getOnlinePlayerData(uuid);
//
//		if(pData==null)
//		{
//			BroadcastTool.printConsoleMessage(ChatColor.RED + "[Bug]: changePlayerRole()- no player in onlinePlayerList");
//			return;
//		}
//
//		pData.setRole(role);
//
//		this.setPlayerGameModeWithRole(uuid);
//	}
//
//	public void setPlayerGameModeWithRole(UUID uuid)
//	{
//		PlayerData pData=this.getOnlinePlayerData(uuid);
//		if(pData==null)
//		{
//			return;
//		}
//
//		Role role=pData.getRole();
//
//		GameMode mode=GameMode.SURVIVAL;
//
//		if(role==Role.MAKER)
//		{
//			mode=GameMode.CREATIVE;
//		}
//		else if(role==Role.CHALLENGER)
//		{
//			mode=GameMode.SURVIVAL;
//		}
//		else if(role==Role.TESTER)
//		{
//			mode=GameMode.SURVIVAL;
//		}
//		else if(role==Role.VIEWER)
//		{
//			mode=GameMode.CREATIVE;
//		}
//		else if(role==Role.WAITER)
//		{
//			mode=GameMode.SURVIVAL;
//		}
//
//		Player p=Bukkit.getPlayer(uuid);
//		p.setGameMode(mode);
//	}
//
////	public void saveAllPlayerSavingData()
////	{
////		for(UUID uuid : this.allPlayerSavigData.keySet())
////		{
////			BroadcastTool.printConsoleMessage("configTest(saveAllPlayerSavingData): "+ChatColor.RED+uuid.toString());
////		}
////
////		// UUID -> String
////		Map<String, PlayerData> tmp=new HashMap<>();
////		for(UUID uuid : this.allPlayerSavigData.keySet())
////		{
////			tmp.put(uuid.toString(), this.allPlayerSavigData.get(uuid));
////		}
////
////		this.ct.saveData(tmp);
////	}
//
//	@SuppressWarnings("unchecked")
//	@Override
//	public void installData(Object obj)
//	{
//		// TODO Auto-generated method stub
//		this.allPlayerSavigData=(Map<UUID, PlayerData>)obj;
//
//		for(PlayerData pData : this.allPlayerSavigData.values())
//		{
//			BroadcastTool.printConsoleMessage(pData.toString());
//		}
//	}
//
//	@Override
//	public Object getData()
//	{
//		// TODO Auto-generated method stub
//		return this.allPlayerSavigData;
//	}
//
//	@Override
//	public String getDataMemberName()
//	{
//		// TODO Auto-generated method stub
//		return "player";
//	}
//
//}
