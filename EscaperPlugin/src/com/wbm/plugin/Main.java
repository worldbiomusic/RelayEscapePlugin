package com.wbm.plugin;

import org.bukkit.Server;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.wbm.plugin.cmd.DebugCommand;
import com.wbm.plugin.data.PlayerData;
import com.wbm.plugin.listener.GameManager;
import com.wbm.plugin.util.PlayerDataManager;
import com.wbm.plugin.util.RelayManager;
import com.wbm.plugin.util.RoomManager;

import net.md_5.bungee.api.ChatColor;

public class Main extends JavaPlugin
{
	Server server;
	PluginManager pluginManager;
	
	GameManager gManager;
	PlayerDataManager pDataManager;
	RoomManager roomManager;
	RelayManager relayManager;
//	ConfigManager configManager;
	
	// command executor
	DebugCommand dCmd;
	
	static {
	    ConfigurationSerialization.registerClass(PlayerData.class);
	}
	
	static Main main;
	
	public static Main getInstance() {
		return main;
	}
	
	@Override 
	public void onEnable()
	{
		main = this;
		
		try {
			super.onEnable();
			this.setupMain();
			
			this.setupManagers();
			
			this.registerListeners();
			this.registerCommands();
			

			
			this.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "EscaperServerPlugin ON");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	

	

	
	void setupMain() {
		this.server = this.getServer();
		this.pluginManager = this.server.getPluginManager();
	}
	
	void setupManagers() throws Exception {
		this.pDataManager = new PlayerDataManager();
		this.roomManager = new RoomManager();
		this.relayManager = new RelayManager(this.pDataManager, this.roomManager);
//		this.configManager = new ConfigManager(this.getDataFolder().getPath());
		this.gManager = new GameManager(this.pDataManager, this.roomManager, this.relayManager);
		
		
//		this.configManager.registerMember("player", this.pDataManager);
		
//		this.configManager.installEachConfigData();
	}
	
	
	private void registerListeners()
	{
		this.registerEvent(this.gManager);
	}
	void registerEvent(Listener listener) {
		this.pluginManager.registerEvents(listener, this);
	}
	
	
	private void registerCommands()
	{
		this.dCmd = new DebugCommand(this.pDataManager, this.relayManager);
		this.getCommand("re").setExecutor(dCmd);
	}
	
	@Override
	public void onDisable()
	{
		super.onDisable();
//		try
//		{
//			this.configManager.saveFile();
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//		}
		
	}
}
