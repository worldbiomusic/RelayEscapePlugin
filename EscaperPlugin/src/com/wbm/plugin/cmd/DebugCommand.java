package com.wbm.plugin.cmd;

import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.wbm.plugin.data.PlayerData;
import com.wbm.plugin.data.Room;
import com.wbm.plugin.util.PlayerDataManager;
import com.wbm.plugin.util.RankManager;
import com.wbm.plugin.util.RelayManager;
import com.wbm.plugin.util.RoomManager;
import com.wbm.plugin.util.enums.RelayTime;
import com.wbm.plugin.util.enums.Role;
import com.wbm.plugin.util.enums.RoomType;
import com.wbm.plugin.util.general.BroadcastTool;
import com.wbm.plugin.util.general.shop.ShopGoods;


public class DebugCommand implements CommandExecutor
{
	PlayerDataManager pDataManager;
	RelayManager relayManager;
	RoomManager roomManager;
	RankManager rankManager;
	
	public DebugCommand(
			PlayerDataManager pDataManager,
			RelayManager relayManager,
			RoomManager roomManager,
			RankManager rankManager)
	{
		this.pDataManager = pDataManager;
		this.relayManager = relayManager;
		this.roomManager = roomManager;
		this.rankManager = rankManager;
	}
	

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if(!(sender instanceof Player )) {
			sender.sendMessage("only player");
		}
		
		Player p = (Player) sender;
		
		if(args.length >= 1) {
			String first = args[0];
			
			switch(first) {
				case "d":  // debug
					BroadcastTool.sendMessage(p, "==========debug cmd=============");
					return this.debug(p, args);
				case "room":
					return this.room(p, args);
				case "rank":
					return this.rank(p, args);
			}
		}
		
		return false;
	}
	
	


	private boolean room(Player p, String[] args)
	{	
		// TODO: 밑의 if문 조건을 통과 못할때가 있음 (player의 위치때문에 RoomType이 잘 안되는거같음)
		// Main room, RelayTime.Making, Role Maker 체크
		if(this.relayManager.checkRoomAndRelayTimeAndRole(
				RoomType.MAIN, RelayTime.MAKING, Role.MAKER, p)) {
			String second = args[1];
			
			switch(second) {
				// re room load [title]
				case "load":
					return this.loadRoom(p, args);
				// re room empty
				case "empty":
					return this.emtpyRoom(p, args);
				// re room list
				case "list":
					return this.printRoomList(p, args);
				// re room title [title]
				case "title":
					return this.setRoomTitle(p, args);
			}
		}
		BroadcastTool.sendMessage(p, "this command is for Maker");
		return true;
	}

	private boolean loadRoom(Player p, String[] args)
	{
		// re room load [title]
		if(args.length != 3) {
			return false;
		}
		if(! this.hasRoomManagerItem(p)) {
			return true;
		}
		String title = args[2];
		Room room = this.roomManager.getRoomData(title);
		
		// room maker가 아닐시 반환
		if(!room.getMaker().equals(p.getName())) {
			BroadcastTool.sendMessage(p, "You are not Maker of " + title + " room");
			return true;
		}
		
		// set corePlaced TRUE! (이전room은 모두 test통과했으므로 core가 무조건 있음)
		this.relayManager.setCorePlaced(true);
		
		
		// set room 
		this.roomManager.setRoom(RoomType.MAIN, room);
		BroadcastTool.sendMessage(p, title + " room is loading...");
		
		return true;
	}
	
	private boolean emtpyRoom(Player p, String[] args)
	{
		// re room empty
		if(args.length != 2) {
			return false;
		}
		if(! this.hasRoomManagerItem(p)) {
			return true;
		}
		this.roomManager.setRoomEmpty(RoomType.MAIN);
		return true;
	}
	
	private boolean printRoomList(Player p, String[] args)
	{
		// print room list
		if(args.length != 2) {
			return false;
		}
		if(! this.hasRoomManagerItem(p)) {
			return true;
		}
		this.roomManager.printRoomList(p);
		return true;
	}
	
	private boolean setRoomTitle(Player p, String[] args) {
		// /re room title "EXAMPLE"
		if(args.length != 3 ) {
			return false;
		}
		if(this.relayManager.checkRoomAndRelayTimeAndRole(RoomType.MAIN, RelayTime.MAKING, Role.MAKER, p)) {
			String title = args[2];
			this.relayManager.setRoomTitle(title);
		}
		return true;
	}
	
	private boolean hasRoomManagerItem(Player p) {
		/*
		 * ROOM_MANAGER goods 가지고 있는지 검사
		 */
		PlayerData pData = this.pDataManager.getPlayerData(p.getUniqueId());
		if(pData.doesHaveGoods(ShopGoods.ROOM_MANAGER) ) {
			BroadcastTool.sendMessage(p, "you need \"ROOM_MANAGER\" for this command");
			return true;
		}
		return false;
	}


	private boolean debug(Player p, String[] args) {
		if(args.length >= 2) {
			String second = args[1];
			
			switch(second) {
				case "relay":
					this.printRelayInfo(p);
					break;
				case "role": // print own role
					this.printPlayerRole(p);
					break;
				case "roles": // print all player role
					this.printAllPlayerRole(p);
					break;
				case "time":
					this.printCurrentRelayTime(p);
					break;
				case "finish":
					this.finishMakingTime(p);
					break;
				case "reset":
					this.relayManager.resetRelay();
					break;
				case "pdata":
					this.printPlayerData(p);
					break;
				case "allpdata":
					this.printAllPlayerData(p);
					break;
				
			}
			return true;
		}
		return false;
	}
	
	private void printPlayerData(Player p ) {
		PlayerData pData = this.pDataManager.getPlayerData(p.getUniqueId());
		BroadcastTool.sendMessage(p, pData.toString());
	}
	
	private void printAllPlayerData(Player p)
	{
		Map<UUID, PlayerData> players = this.pDataManager.getOnlyOnlinePlayerData();
		for(PlayerData pData : players.values()) {
			BroadcastTool.sendMessage(p, pData.toString());
			BroadcastTool.printConsoleMessage(pData.toString());
		}
	}


	private void finishMakingTime(Player p)
	{
		// MakingTime일때 Testing으로 넘어갈 수 있게 해주는 명령어
		RelayTime time = this.relayManager.getCurrentTime();
		if(time == RelayTime.MAKING) {
			if(! this.relayManager.isCorePlaced()) {
				BroadcastTool.sendMessage(p, "core is not placed");
			} else {
				this.relayManager.startNextTime();
			}
			
		}
	}


	private void printRelayInfo(Player p)
	{
		// print maker
		this.printMaker(p);
		// all playre role
		this.printAllPlayerRole(p);
		// currentTime
		this.printCurrentRelayTime(p);
	}


	void printPlayerRole(Player p) {
		PlayerData pData = this.pDataManager.getPlayerData(p.getUniqueId());
		Role role = pData.getRole();
		p.sendMessage(p.getName() + " Role: " + role.name());
	}
	
	void printAllPlayerRole(Player p) {
		p.sendMessage(ChatColor.BOLD + "[Role]");
		for(Player each : Bukkit.getOnlinePlayers()) {
			String eachName = each.getName();
			// all중에 자신이름일때 색깔 초혹
			if(eachName.equals(p.getName())) {
				eachName = ChatColor.GREEN + eachName + ChatColor.WHITE;
			}
			PlayerData allData = this.pDataManager.getPlayerData(each.getUniqueId());
			Role role = allData.getRole();
			p.sendMessage(eachName + ": " + role.name());
		}
		p.sendMessage("------------------------------");
	}
	
	void printCurrentRelayTime(Player p) {
		RelayTime time = this.relayManager.getCurrentTime();
		p.sendMessage(ChatColor.BOLD + "[Time]");
		p.sendMessage(time.name());
		p.sendMessage("------------------------------");
	}
	
	private void printMaker(Player p)
	{
		String makerName = "";
		Player maker = this.pDataManager.getMaker();
		if(maker!=null) {
			makerName = maker.getName();
		}
		p.sendMessage(ChatColor.BOLD + "[Maker]");
		p.sendMessage(ChatColor.RED + makerName);
		p.sendMessage("------------------------------");
	}
	
	
	private boolean rank(Player p, String[] args)
	{
		// /re rank [options]
		if(args.length == 2) {
			String list = args[1];
			switch(list) {
				case "tokenrank":
					for(PlayerData pData : this.rankManager.getTokenRankList()) {
						BroadcastTool.sendMessage(p, pData.getName() + ": " + pData.getToken());
					}
					break;
				case "challengingrank":
					for(PlayerData pData : this.rankManager.getChallengingCountRankList()) {
						BroadcastTool.sendMessage(p, pData.getName() + ": " + pData.getChallengingCount());
					}
					break;
				case "clearrank":
					for(PlayerData pData : this.rankManager.getClearCountRankList()) {
						BroadcastTool.sendMessage(p, pData.getName() + ": " + pData.getClearCount());
					}
					break;
				case "roomcountrank":
					for(PlayerData pData : this.rankManager.getRoomCountRankList()) {
						BroadcastTool.sendMessage(p, pData.getName() + ": " + this.roomManager.getOwnRooms(pData.getName()).size());
					}
					break;
				default:
					return false;
			}
			
			return true;
		}
		return false;
	}
}























