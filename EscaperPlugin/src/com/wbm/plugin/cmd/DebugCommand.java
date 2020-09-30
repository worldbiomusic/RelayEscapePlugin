package com.wbm.plugin.cmd;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.wbm.plugin.data.PlayerData;
import com.wbm.plugin.util.PlayerDataManager;
import com.wbm.plugin.util.RelayManager;
import com.wbm.plugin.util.enums.RelayTime;
import com.wbm.plugin.util.enums.Role;
import com.wbm.plugin.util.general.BroadcastTool;


public class DebugCommand implements CommandExecutor
{
	PlayerDataManager pDataManager;
	RelayManager relayManager;
	
	
	public DebugCommand(
			PlayerDataManager pDataManager,
			RelayManager relayManager)
	{
		this.pDataManager = pDataManager;
		this.relayManager = relayManager;
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
		}
		
		return true;
	}
	
	void debug(Player p, String[] args) {
		String second = args[1];
		
		switch(second) {
			case "all":
				this.printAllDebugInfo(p);
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


	private void printAllDebugInfo(Player p)
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
}























