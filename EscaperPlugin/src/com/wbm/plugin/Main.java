package com.wbm.plugin;

import org.bukkit.Server;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.wbm.plugin.listener.GameManager;
import com.wbm.plugin.listener.PlayerManager;
import com.wbm.plugin.util.PlayerDataManager;

import net.md_5.bungee.api.ChatColor;

public class Main extends JavaPlugin
{
	Server server;
	PluginManager pluginManager;
	
	PlayerManager pManager;
	GameManager gManager;
	PlayerDataManager pDataManager;
	
	 
	@Override
	public void onEnable()
	{
		super.onEnable();
		
		this.setupMain();
		
		this.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "EscaperServerPlugin ON");
		
		this.setupManagers();
	}
	
	void setupMain() {
		this.server = this.getServer();
		this.pluginManager = this.server.getPluginManager();
	}
	
	void setupManagers() {
		this.pDataManager = new PlayerDataManager();
		this.pManager = new PlayerManager(this.pDataManager);
		this.gManager = new GameManager(this.pDataManager, this.pManager);
		
		this.registerEvent(this.pManager);
		this.registerEvent(this.gManager);
	}
	
	void registerEvent(Listener listener) {
		this.pluginManager.registerEvents(listener, this);
	}
	
	@Override
	public void onDisable()
	{
		super.onDisable();
	}
}
