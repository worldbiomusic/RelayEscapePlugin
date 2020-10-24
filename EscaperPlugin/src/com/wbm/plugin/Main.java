package com.wbm.plugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.wbm.plugin.cmd.DebugCommand;
import com.wbm.plugin.listener.CommonListener;
import com.wbm.plugin.listener.GameManager;
import com.wbm.plugin.util.PlayerDataManager;
import com.wbm.plugin.util.RelayManager;
import com.wbm.plugin.util.RoomManager;
import com.wbm.plugin.util.config.ConfigTest;
import com.wbm.plugin.util.config.DataManager;
import com.wbm.plugin.util.general.BanItemTool;
import com.wbm.plugin.util.general.RespawnManager;

public class Main extends JavaPlugin
{
	Server server;
	PluginManager pluginManager;

	GameManager gManager;
	CommonListener commonListener;
	PlayerDataManager pDataManager;
	RoomManager roomManager;
	RelayManager relayManager;
	DataManager dataManager;

	// command executor
	DebugCommand dCmd;

	ConfigTest ct;
	
	// Tools
	RespawnManager respawnManager;
	BanItemTool banItems;

	static Main main;

	public static Main getInstance()
	{
		return main;
	}

	@Override
	public void onEnable()
	{
//		ConfigurationSerialization.registerClass(PlayerData.class);
		main=this;

		try
		{
			super.onEnable();
			this.setupMain();
			
			// tools
			this.setupTools();

			this.setupManagers();

			this.registerListeners();
			this.registerCommands();

			this.getServer().getConsoleSender().sendMessage(ChatColor.GREEN+"EscaperServerPlugin ON");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	private void setupTools()
	{
		// respawn manager
		Location loc=new Location(Bukkit.getWorld("world"), 9.5, 4, 5.5);
		this.respawnManager=new RespawnManager(loc, loc);
		
		// banItem (후원 banItems는 따로 만들기)
		this.banItems = new BanItemTool();
		this.banItems.banAllItem();
		this.banItems.unbanItem(Material.DIRT);
		this.banItems.unbanItem(Material.STONE);
		this.banItems.unbanItem(Material.WOOD);
		this.banItems.unbanItem(Material.GLOWSTONE);
		this.banItems.unbanItem(Material.GLASS);
		this.banItems.unbanItem(Material.JACK_O_LANTERN);
		
	}

	void setupMain()
	{
		this.server=this.getServer();
		this.pluginManager=this.server.getPluginManager();
	}

	void setupManagers() throws Exception
	{
//		this.ct = new ConfigTest(this.getDataFolder().getPath());
		this.dataManager=new DataManager(this.getDataFolder().getPath());

		this.pDataManager=new PlayerDataManager(this.ct);
		this.dataManager.registerMember(this.pDataManager);

		this.roomManager=new RoomManager();
		this.dataManager.registerMember(this.roomManager);

		this.dataManager.distributeData();

		this.relayManager=new RelayManager(this.pDataManager, this.roomManager);
		this.gManager=new GameManager(this.pDataManager, this.roomManager, this.relayManager, this.banItems);

	}

	private void registerListeners()
	{
		this.commonListener = new CommonListener();
		
		this.registerEvent(this.gManager);
		this.registerEvent(this.respawnManager);
		this.registerEvent(this.commonListener);
	}

	void registerEvent(Listener listener)
	{
		this.pluginManager.registerEvents(listener, this);
	}

	private void registerCommands()
	{
		this.dCmd=new DebugCommand(this.pDataManager, this.relayManager);
		this.getCommand("re").setExecutor(dCmd);
	}

	@Override
	public void onDisable()
	{
		// reload대비 처리 (다 나간것으로 처리해서 데이터 save)
		for(Player p : Bukkit.getOnlinePlayers())
		{
			this.pDataManager.saveAndRemovePlayerData(p.getUniqueId());
		}

		// file save
		this.dataManager.save();
	}
}
