package com.wbm.plugin;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import com.wbm.plugin.cmd.Commands;
import com.wbm.plugin.data.PlayerData;
import com.wbm.plugin.listener.CommonListener;
import com.wbm.plugin.listener.GameManager;
import com.wbm.plugin.listener.ItemUsingManager;
import com.wbm.plugin.util.MiniGameManager;
import com.wbm.plugin.util.PlayerDataManager;
import com.wbm.plugin.util.RankManager;
import com.wbm.plugin.util.RelayManager;
import com.wbm.plugin.util.RoomManager;
import com.wbm.plugin.util.Setting;
import com.wbm.plugin.util.StageManager;
import com.wbm.plugin.util.config.ConfigTest;
import com.wbm.plugin.util.config.DataManager;
import com.wbm.plugin.util.enums.Role;
import com.wbm.plugin.util.general.BanItemTool;
import com.wbm.plugin.util.general.BroadcastTool;
import com.wbm.plugin.util.general.NPCManager;
import com.wbm.plugin.util.general.SpawnLocationTool;
import com.wbm.plugin.util.general.shop.ShopGoods;
import com.wbm.plugin.util.general.shop.ShopManager;
import com.wbm.plugin.util.general.skin.SkinManager;

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
	ShopManager shopManager;
	ItemUsingManager itemUsingManager;
	RankManager rankManager;
	NPCManager npcManager;
	StageManager stageManager;
	MiniGameManager miniGameManager;

	// command executor
	Commands dCmd;

	ConfigTest ct;
	
	// Tools
	SpawnLocationTool respawnManager;
	BanItemTool banItems;
	SkinManager skinManager;

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
			
//			// reRegister all player (이미 서버에 있는데 reload했을경우) 
			// GameManager에서 생성자에서 불려야 플레이어데이터가 올라가서 정상작동함 (여기서 하면 안됨)
//			this.gManager.reRegisterAllPlayer();
			
			// update scoreboard every 1 sec
			this.loopUpdatingScoreboard();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	private void setupTools()
	{
		// BroadcastTool
		BroadcastTool.setServerNamePrefix("" + ChatColor.RED + ChatColor.BOLD + "[i] " + ChatColor.WHITE);
		
		// respawn manager
		Location loc=Setting.getLoationFromSTDLOC( 9.5, 4, 5.5, 90, 0);
		Location lobby=Setting.getLoationFromSTDLOC(16, 4, 16, 90, 0);;
		this.respawnManager=new SpawnLocationTool(loc, loc, lobby);
		
		// banItem (후원 banItems는 따로 만들기)
		this.banItems = new BanItemTool();
		this.banItems.banAllItem();
		for(ShopGoods goods : ShopGoods.values()) {
			this.banItems.unbanItem(goods.getGoods().getType());
		}
		
		// kits
//		this.makeKits();
		
		// skindata
		this.skinManager = new SkinManager();

		// NPC
		this.npcManager = new NPCManager(this.skinManager);
		
	}

	void setupMain()
	{
		this.server=this.getServer();
		this.pluginManager=this.server.getPluginManager();
	}

	void setupManagers() throws Exception
	{
		this.dataManager=new DataManager(this.getDataFolder().getPath());

		this.pDataManager=new PlayerDataManager(this.ct);
		this.dataManager.registerMember(this.pDataManager);

		this.roomManager=new RoomManager();
		this.dataManager.registerMember(this.roomManager);
		this.dataManager.registerMember(this.npcManager);
		this.dataManager.registerMember(this.skinManager);
		
//		// distribute datas (이 메소드는 this.dataManager.registerMember <- 이 메소드들이
//		// 마지막다음에 바로 실행되어야 함 
		// -> register안에 넣어버릴까?(인자 추가해서 해당 member만 데이터 받을수 있게)

		this.miniGameManager = new MiniGameManager(this.pDataManager);
		this.rankManager = new RankManager(this.pDataManager, this.roomManager);
		this.stageManager = new StageManager(this.rankManager, this.npcManager);
		// setup stages
		this.setupStages();
		
		this.relayManager=new RelayManager(this.pDataManager, this.roomManager, this.stageManager);
		this.gManager=new GameManager(this.pDataManager, this.roomManager, this.relayManager, this.miniGameManager);
		this.itemUsingManager = new ItemUsingManager(this.pDataManager, this.roomManager, this.relayManager);
		this.shopManager = new ShopManager(this.pDataManager);
		
	}

	private void registerListeners()
	{
		this.commonListener = new CommonListener(this.pDataManager, this.shopManager, 
				this.banItems, this.npcManager, this.skinManager, this.miniGameManager, this.relayManager);
		
		this.registerEvent(this.gManager);
		this.registerEvent(this.commonListener);
		this.registerEvent(this.itemUsingManager);
	}

	void registerEvent(Listener listener)
	{
		this.pluginManager.registerEvents(listener, this);
	}

	private void registerCommands()
	{
		this.dCmd=new Commands(this.pDataManager, this.relayManager, this.roomManager, this.rankManager, this.npcManager);
		this.getCommand("re").setExecutor(dCmd);
	}
	
	public void loopUpdatingScoreboard() {
		ScoreboardManager manager = Bukkit.getScoreboardManager();
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable()
		{
			@Override
			public void run()
			{
				for(Player p : Bukkit.getOnlinePlayers()) {
					Scoreboard board = manager.getNewScoreboard();
					PlayerData pData = pDataManager.getPlayerData(p.getUniqueId());
					Role r = pData.getRole();
					
					// ============sidebar============
					Objective obj = board.registerNewObjective("side", "dummy");
					obj.setDisplaySlot(DisplaySlot.SIDEBAR);
					obj.setDisplayName("=====INFO=====");
					
					Score role = obj.getScore("Role: " + r);
					role.setScore(10);
					
					Score token = obj.getScore("Token: " + pData.getToken());
					token.setScore(9);
					
					String leftTime = "" + ChatColor.RED + ChatColor.BOLD + relayManager.getLeftTime() + ChatColor.WHITE;
					Score relayTime = obj.getScore("RelayTime: " 
					+ relayManager.getCurrentTime().name() 
					+ "(" + leftTime + ")");
					relayTime.setScore(8);
					
					// ============below name============
					Objective belowNameObj = board.registerNewObjective("healthNRole", "health");
					belowNameObj.setDisplaySlot(DisplaySlot.BELOW_NAME);
					String belowRole = "";
					if(r == Role.MAKER) {
						belowRole += "" + ChatColor.RED + ChatColor.BOLD;
					}
					belowRole += r.name();
					belowNameObj.setDisplayName("/"+ pData.getName() +"Role: " + belowRole);

					p.setScoreboard(board);
					
				}
			}
		}, 20 * 1, 20 * 1);
		
	}
	
	private void setupStages() {
		/*
		 *  token] yaw, pitch: (-90, 0)
			12.5, 6, 5.5
			12.5, 5, 6.5
			12.5, 4, 4.5
			
			challenging (0, 0)
			14.5, 6, 1.5
			13.5, 5, 1.5
			15.5, 4, 1.5
			
			clear (0, 0)
			17.5, 6, 1.5
			16.5, 5, 1.5
			18.5, 4, 1.5
			
			room (90, 0)
			19.5, 6, 6.5
			19.5, 5, 5.5
			19.5, 4, 7.5
		 */
		List<Location> tokenLocs = new ArrayList<Location>();
		tokenLocs.add(Setting.getLoationFromSTDLOC(12.5, 6, 5.5, -90, 0));
		tokenLocs.add(Setting.getLoationFromSTDLOC(12.5, 5, 6.5, -90, 0));
		tokenLocs.add(Setting.getLoationFromSTDLOC(12.5, 4, 4.5, -90, 0));
		
		List<Location> challengingLocs = new ArrayList<Location>();
		tokenLocs.add(Setting.getLoationFromSTDLOC(14.5, 6, 1.5, 0, 0));
		tokenLocs.add(Setting.getLoationFromSTDLOC(13.5, 5, 1.5, 0, 0));
		tokenLocs.add(Setting.getLoationFromSTDLOC(15.5, 4, 1.5, 0, 0));

		List<Location> clearLocs = new ArrayList<Location>();
		tokenLocs.add(Setting.getLoationFromSTDLOC(17.5, 6, 1.5, 0, 0));
		tokenLocs.add(Setting.getLoationFromSTDLOC(16.5, 5, 1.5, 0, 0));
		tokenLocs.add(Setting.getLoationFromSTDLOC(18.5, 4, 1.5, 0, 0));
		
		List<Location> roomLocs = new ArrayList<Location>();
		tokenLocs.add(Setting.getLoationFromSTDLOC(19.5, 6, 6.5, 90, 0));
		tokenLocs.add(Setting.getLoationFromSTDLOC(19.5, 5, 5.5, 90, 0));
		tokenLocs.add(Setting.getLoationFromSTDLOC(19.5, 4, 7.5, 90, 0));
		
		// stage 에 드록
		this.stageManager.registerLocations("tokenCount", tokenLocs);
		this.stageManager.registerLocations("challengingCount", challengingLocs);
		this.stageManager.registerLocations("clearCount", clearLocs);
		this.stageManager.registerLocations("roomCount", roomLocs);
	}
	
//	void makeKits() {
//		KitTool.addKit("maker", 
//				new ItemStack(Material.GLOWSTONE),
//				new ItemStack(Material.DIRT),
//				new ItemStack(Material.GLASS), 
//				new ItemStack(Material.STONE), 
//				new ItemStack(Material.WOOD), 
//				new ItemStack(Material.JACK_O_LANTERN), 
//				ShopGoods.UNDER_BLOCK.getGoods(),
//				ShopGoods.SPAWN.getGoods(),
//				ShopGoods.ROOM_MANAGER.getGoods());
//		
//		KitTool.addKit("tester", ShopGoods.SPAWN.getGoods());
//		
//		KitTool.addKit("challenger", ShopGoods.HALF_TIME.getGoods());
//		
//		KitTool.addKit("viewer", ShopGoods.GHOST.getGoods());
//		
////		KitTool.addKit("waiter", ShopGoods.GHOST.getGoods());
//	}

	@Override
	public void onDisable()
	{
		// rank NPC 제거
		// rank NPC 는 NPC자체가 저장될 필요가 없음
		// 왜냐하면 각 waitingTime마다 순위에 따라서 NPC가 바뀌므로
		// StageManager에 위치만 지정해놓고 각 상황에따라 NPC를 삭제하고 불러와야 하므로. 
		this.stageManager.removeRemainingRankNPCs();
		
		// file save
		this.dataManager.save();
	}
}
