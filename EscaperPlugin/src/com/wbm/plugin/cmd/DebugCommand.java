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
import com.wbm.plugin.data.ShopGoods;
import com.wbm.plugin.util.PlayerDataManager;
import com.wbm.plugin.util.RelayManager;
import com.wbm.plugin.util.RoomManager;
import com.wbm.plugin.util.enums.RelayTime;
import com.wbm.plugin.util.enums.Role;
import com.wbm.plugin.util.enums.RoomType;
import com.wbm.plugin.util.general.BroadcastTool;


public class DebugCommand implements CommandExecutor
{
	PlayerDataManager pDataManager;
	RelayManager relayManager;
	RoomManager roomManager;
	
	public DebugCommand(
			PlayerDataManager pDataManager,
			RelayManager relayManager,
			RoomManager roomManager)
	{
		this.pDataManager = pDataManager;
		this.relayManager = relayManager;
		this.roomManager = roomManager;
	}
	

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if(!(sender instanceof Player )) {
			sender.sendMessage("only player");
		}
		
		Player p = (Player) sender;
		
		String first = args[0];
		
		switch(first) {
			case "d":  // debug
				p.sendMessage("==========debug cmd=============");
				this.debug(p, args);
				break;
			case "room":
				this.room(p, args);
				break;
		}
		
		return true;
	}
	
	private void room(Player p, String[] args)
	{	
		PlayerData pData = this.pDataManager.getOnlinePlayerData(p.getUniqueId());
		if(! pData.doesHaveGoods(ShopGoods.ROOM_MANAGER) ) {
			BroadcastTool.sendMessage(p, "you need \"ROOM_MANAGER\" for this command");
			return;
		}
		// TODO: 밑의 if문 조건을 통과 못할때가 있음 (player의 위치때문에 RoomType이 잘 안되는거같음)
		// Main room, RelayTime.Making, Role Maker 체크
		if(this.relayManager.checkRoomAndRelayTimeAndRole(
				RoomType.MAIN, RelayTime.MAKING, Role.MAKER, p)) {
			String second = args[1];
			
			switch(second) {
				case "load":
					this.loadRoom(p, args);
					break;
				case "empty":
					this.emtpyRoom(p, args);
					break;
				case "list":
					this.printRoomList(p, args);
					break;
			}
		}
	}

	private void loadRoom(Player p, String[] args)
	{
		// re room load [title]
		String title = args[2];
		Room room = this.roomManager.getRoomData(title);
		
		// room maker가 아닐시 반환
		if(!room.getMaker().equals(p.getName())) {
			BroadcastTool.sendMessage(p, "You are not Maker of " + title + " room");
			return;
		}
		
		// set corePlaced TRUE! (이전room은 모두 test통과했으므로 core가 무조건 있음)
		this.relayManager.setCorePlaced(true);
		
		
		// set room 
		this.roomManager.setRoom(RoomType.MAIN, room);
		BroadcastTool.sendMessage(p, title + " room is loading...");
	}
	
	private void emtpyRoom(Player p, String[] args)
	{
		// re room empty
		this.roomManager.setRoomEmpty(RoomType.MAIN);
	}
	
	private void printRoomList(Player p, String[] args)
	{
		// print room list
		this.roomManager.printRoomList(p);
	}


	private void debug(Player p, String[] args) {
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
	}
	
	private void printPlayerData(Player p ) {
		PlayerData pData = this.pDataManager.getOnlinePlayerData(p.getUniqueId());
		BroadcastTool.sendMessage(p, pData.toString());
	}
	
	private void printAllPlayerData(Player p)
	{
		Map<UUID, PlayerData> players = this.pDataManager.getAllOnlinePlayers();
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
		PlayerData pData = this.pDataManager.getOnlinePlayerData(p.getUniqueId());
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
			PlayerData allData = this.pDataManager.getOnlinePlayerData(each.getUniqueId());
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
}























