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
import com.wbm.plugin.data.RoomLocation;
import com.wbm.plugin.listener.CommonListener;
import com.wbm.plugin.listener.GameManager;
import com.wbm.plugin.listener.GoodsListener;
import com.wbm.plugin.util.PlayerDataManager;
import com.wbm.plugin.util.RankManager;
import com.wbm.plugin.util.RelayManager;
import com.wbm.plugin.util.RoomManager;
import com.wbm.plugin.util.Setting;
import com.wbm.plugin.util.StageManager;
import com.wbm.plugin.util.config.ConfigTest;
import com.wbm.plugin.util.config.DataManager;
import com.wbm.plugin.util.discord.DiscordBot;
import com.wbm.plugin.util.enums.Role;
import com.wbm.plugin.util.enums.RoomType;
import com.wbm.plugin.util.general.BanItemTool;
import com.wbm.plugin.util.general.BlockRotateTool;
import com.wbm.plugin.util.general.BroadcastTool;
import com.wbm.plugin.util.general.CoolDownManager;
import com.wbm.plugin.util.general.NPCManager;
import com.wbm.plugin.util.general.Rotationer;
import com.wbm.plugin.util.general.SpawnLocationTool;
import com.wbm.plugin.util.general.TPManager;
import com.wbm.plugin.util.general.TeleportTool;
import com.wbm.plugin.util.general.skin.SkinManager;
import com.wbm.plugin.util.minigame.MiniGameManager;
import com.wbm.plugin.util.minigame.MiniGameRankManager;
import com.wbm.plugin.util.shop.GoodsRole;
import com.wbm.plugin.util.shop.ShopGoods;
import com.wbm.plugin.util.shop.ShopManager;

public class Main extends JavaPlugin {
    Server server;
    PluginManager pluginManager;

    GameManager gManager;
    CommonListener commonListener;
    PlayerDataManager pDataManager;
    RoomManager roomManager;
    RelayManager relayManager;
    DataManager dataManager;
    ShopManager shopManager;
    GoodsListener goodsListener;
    RankManager rankManager;
    NPCManager npcManager;
    StageManager stageManager;
    MiniGameManager miniGameManager;
    MiniGameRankManager miniGameRankManager;

    // command executor
    Commands dCmd;

    ConfigTest ct;

    // Tools
    SpawnLocationTool respawnManager;
    BanItemTool banItems;
    SkinManager skinManager;
    DiscordBot discordBot;

    static Main main;

    public static Main getInstance() {
	return main;
    }

    @Override
    public void onEnable() {
//		ConfigurationSerialization.registerClass(PlayerData.class);
	main = this;

	try {
	    super.onEnable();
	    this.setupMain();

	    // tools
	    this.setupTools();

	    this.setupManagers();

	    this.registerListeners();
	    this.registerCommands();

	    this.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "EscaperServerPlugin ON");
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    private void setupTools() {
	// set MOTD

//	register CoolDown subject
	CoolDownManager.registerSubject(Setting.CoolDown_Subject_CHAT, 5);
	CoolDownManager.registerSubject(Setting.CoolDown_Subject_CMD_ROOM, 10);

	// BroadcastTool
	BroadcastTool.setMessagePrefix("" + ChatColor.RED + ChatColor.BOLD + "[i] " + ChatColor.WHITE);

	// respawn manager
	Location spawn = Setting.getLoationFromSTDLOC(-1.5, 4, -1.5, 90, 0);
	Location lobby = Setting.getLoationFromSTDLOC(16, 4, 16, 90, 0);
	;
	this.respawnManager = new SpawnLocationTool(spawn, spawn, lobby);

	// banItem
	this.banItems = new BanItemTool();
	this.banItems.banAllItem();
	// MakingBLock 예외로 설치 가능
	for (ShopGoods goods : ShopGoods.values()) {
	    if (goods.isGoodsRoleGoods(GoodsRole.MAKING_BLOCK)) {
		this.banItems.unbanItem(goods.getItemStack().getType());
	    }
	}

	// TPManager
	TPManager.registerLocation("PRACTICE_SPAWN", Setting.getLoationFromSTDLOC(33.5, 4, 28.5, 90, 0));
	TPManager.registerLocation("LOBBY", SpawnLocationTool.LOBBY);
	TPManager.registerLocation("MINI_GAME_VIEW", Setting.getLoationFromSTDLOC(-116, 3, 102, 90, 0));

	// skindata
	this.skinManager = new SkinManager();

	// NPC
	this.npcManager = new NPCManager(this.skinManager);

	// update scoreboard every 1 sec
	this.loopUpdatingScoreboard();

	// play music every 4 minutes
	this.loopPlayingMusic();

	// check player too far
	this.checkPlayerIsTooFarAway();

	// TIP
	this.loopTips();

	// RotationBlock (렉 많이 걸림)
//	this.loopRotationBlock();

	// ranking system(stage) 업데이트
	this.loopUpdateAllStage();
    }

    void setupMain() {
	this.server = this.getServer();
	this.pluginManager = this.server.getPluginManager();
    }

    void setupManagers() throws Exception {
	this.dataManager = new DataManager(this.getDataFolder().getPath());

	this.pDataManager = new PlayerDataManager(this.ct);
	this.miniGameRankManager = new MiniGameRankManager();
	this.miniGameManager = new MiniGameManager(this.pDataManager, this.miniGameRankManager);
	this.dataManager.registerMember(this.pDataManager);
	this.dataManager.loopSavingData(Setting.DATA_SAVE_DELAY); // save 데이터 주기 설정
	this.roomManager = new RoomManager();

	this.dataManager.registerMember(this.roomManager);
	this.dataManager.registerMember(this.npcManager);
	this.dataManager.registerMember(this.skinManager);
	this.dataManager.registerMember(this.miniGameRankManager);

//		// distribute datas (이 메소드는 this.dataManager.registerMember <- 이 메소드들이
//		// 마지막다음에 바로 실행되어야 함 
	// -> register안에 넣어버릴까?(인자 추가해서 해당 member만 데이터 받을수 있게)

	this.rankManager = new RankManager(this.pDataManager, this.roomManager);
	this.stageManager = new StageManager(this.rankManager, this.npcManager);
	// setup Rank stages
	this.setupRankStages();

	this.relayManager = new RelayManager(this.pDataManager, this.roomManager, this.miniGameManager);

	this.shopManager = new ShopManager(this.pDataManager);

	// discord bot 실행
	this.setupDiscordBot();
    }

    private void registerListeners() {
	// 리스너 초기화
	this.gManager = new GameManager(this.pDataManager, this.roomManager, this.relayManager, this.miniGameManager,
		this.stageManager);
	this.commonListener = new CommonListener(this.pDataManager, this.shopManager, this.banItems, this.npcManager,
		this.skinManager, this.miniGameManager, this.relayManager, this.discordBot);
	this.goodsListener = new GoodsListener(this.pDataManager, this.roomManager, this.relayManager);

	// 등록
	this.registerEvent(this.gManager);
	this.registerEvent(this.commonListener);
	this.registerEvent(this.goodsListener);
    }

    void registerEvent(Listener listener) {
	this.pluginManager.registerEvents(listener, this);
    }

    private void registerCommands() {
	this.dCmd = new Commands(this.pDataManager, this.relayManager, this.roomManager, this.rankManager,
		this.npcManager, this.miniGameManager);
	this.getCommand("re").setExecutor(dCmd);
    }

    public void loopUpdatingScoreboard() {
	ScoreboardManager manager = Bukkit.getScoreboardManager();

	// belowname setting
//	Objective belowNameObj = board.registerNewObjective("below", "health");
//	belowNameObj.setDisplaySlot(DisplaySlot.BELOW_NAME);
//	String belowRole = "/ 20";
//	belowNameObj.setDisplayName(belowRole);

	// task loop
	Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
	    @Override
	    public void run() {
		for (Player p : Bukkit.getOnlinePlayers()) {
		    PlayerData pData = pDataManager.getPlayerData(p.getUniqueId());
		    Role r = pData.getRole();

		    Scoreboard board = manager.getNewScoreboard();

		    // sidebar setting
		    Objective sidebarObj = board.registerNewObjective("side", "dummy");
		    sidebarObj.setDisplaySlot(DisplaySlot.SIDEBAR);
		    sidebarObj.setDisplayName("===== INFO =====");

		    // ============sidebar============

		    Score role = sidebarObj.getScore("Role: " + r);
		    role.setScore(10);

		    Score token = sidebarObj.getScore("Token: " + pData.getToken());
		    token.setScore(1);

		    Score cash = sidebarObj.getScore("Cash: " + pData.getCash());
		    cash.setScore(0);

//		    int roomCNT = roomManager.getOwnRooms(p.getName()).size();
//		    Score roomCount = sidebarObj.getScore("Room: " + roomCNT);
//		    roomCount.setScore(8);
//
//		    int clearCNT = pData.getClearCount();
//		    Score clearCount = sidebarObj.getScore("Clear: " + clearCNT);
//		    clearCount.setScore(7);

		    // relay time
		    String leftTime = "" + ChatColor.RED + ChatColor.BOLD + relayManager.getLeftTime()
			    + ChatColor.WHITE;
		    Score relayTime = sidebarObj
			    .getScore("RelayTime: " + relayManager.getCurrentTime().name() + "(" + leftTime + ")");
		    relayTime.setScore(6);

		    // player location
		    RoomType roomType = RoomLocation.getRoomTypeWithLocation(p.getLocation());
		    String roomString;
		    if (roomType == null) {
			roomString = "NOT ROOM";
		    } else {
			roomString = roomType.name();
		    }

		    Score room = sidebarObj.getScore("Room: " + roomString);
		    room.setScore(9);

		    // ============below name============

//		    if (r == Role.MAKER) {
//			belowRole += "" + ChatColor.RED + ChatColor.BOLD;
//		    }
//		    belowRole += pData.getRole();
//		    belowNameObj.setDisplayName(p.getName() + ", Role: " + belowRole);

		    p.setScoreboard(board);
		}
	    }
	}, 20 * 1, 20 * 1);

    }

    void loopPlayingMusic() {
	Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {

	    @Override
	    public void run() {
		for (Player p : Bukkit.getOnlinePlayers()) {
		    // note block API 재생
		    String cmd = "play 1 " + p.getName();
		    Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), (cmd));
		}
	    }
	}, 20 * 10, 20 * 60 * 60);
    }

    private void setupRankStages() {
	/*
	 * token] yaw, pitch: (-90, 0) 12.5, 6, 5.5 12.5, 5, 6.5 12.5, 4, 4.5
	 * 
	 * challenging (0, 0) 14.5, 6, 1.5 13.5, 5, 1.5 15.5, 4, 1.5
	 * 
	 * clear (0, 0) 17.5, 6, 1.5 16.5, 5, 1.5 18.5, 4, 1.5
	 * 
	 * room (90, 0) 19.5, 6, 6.5 19.5, 5, 5.5 19.5, 4, 7.5
	 */
	List<Location> tokenLocs = new ArrayList<Location>();
	tokenLocs.add(Setting.getLoationFromSTDLOC(12.5, 6, 3.5, -90, 0));
	tokenLocs.add(Setting.getLoationFromSTDLOC(12.5, 5, 4.5, -90, 0));
	tokenLocs.add(Setting.getLoationFromSTDLOC(12.5, 4, 2.5, -90, 0));

	List<Location> challengingLocs = new ArrayList<Location>();
	challengingLocs.add(Setting.getLoationFromSTDLOC(12.5, 6, -0.5, -90, 0));
	challengingLocs.add(Setting.getLoationFromSTDLOC(12.5, 5, 0.5, -90, 0));
	challengingLocs.add(Setting.getLoationFromSTDLOC(12.5, 4, -1.5, -90, 0));

	List<Location> clearLocs = new ArrayList<Location>();
	clearLocs.add(Setting.getLoationFromSTDLOC(19.5, 6, -0.5, 90, 0));
	clearLocs.add(Setting.getLoationFromSTDLOC(19.5, 5, -1.5, 90, 0));
	clearLocs.add(Setting.getLoationFromSTDLOC(19.5, 4, 0.5, 90, 0));

	List<Location> roomLocs = new ArrayList<Location>();
	roomLocs.add(Setting.getLoationFromSTDLOC(19.5, 6, 3.5, 90, 0));
	roomLocs.add(Setting.getLoationFromSTDLOC(19.5, 5, 2.5, 90, 0));
	roomLocs.add(Setting.getLoationFromSTDLOC(19.5, 4, 4.5, 90, 0));

	// stage 에 드록
	this.stageManager.registerLocations("tokenCount", tokenLocs);
	this.stageManager.registerLocations("challengingCount", challengingLocs);
	this.stageManager.registerLocations("clearCount", clearLocs);
	this.stageManager.registerLocations("roomCount", roomLocs);
    }

    private void checkPlayerIsTooFarAway() {
	/*
	 * 플레이어가 너무 멀리가면 청크로딩으로 맵이 켜져서 다시 중심으로 TP
	 */
	Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
	    @Override
	    public void run() {
		for (Player p : Bukkit.getOnlinePlayers()) {
		    Location loc = Setting.STDLOC.clone().subtract(p.getLocation());
		    int dx = Math.abs((int) loc.getX());
		    int dy = Math.abs((int) loc.getY());
		    int dz = Math.abs((int) loc.getZ());

		    if (dx > 1000 || dy > 1000 || dz > 1000) {
			TeleportTool.tp(p, SpawnLocationTool.JOIN);
		    }
		}
	    }
	}, 0, 20 * 5);
    }

    private void loopTips() {
	Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), new Runnable() {

	    @Override
	    public void run() {
		List<String> tips = new ArrayList<>();
		tips.add("Discord " + ChatColor.WHITE + ":" + ChatColor.GREEN + ChatColor.UNDERLINE + ChatColor.BOLD
			+ " https://discord.gg/EwXk9Cd2Ya" + ChatColor.WHITE);
		tips.add("Tutorial: /re tutorial");
		tips.add("CHAT: 1 ~ 9 (ex. 1 = HI)");
		tips.add("Reconnect is good way to go to spawn or give up Room");

		// random tip 고르기
		String randomTip = tips.get((int) (Math.random() * tips.size()));

		// 색깔 줘서 알리기
		String tipMsg = "" + ChatColor.YELLOW + ChatColor.BOLD + "[TIP] " + ChatColor.WHITE + randomTip;
		BroadcastTool.sendMessageToEveryoneWithoutPrefix(tipMsg);
	    }
	}, 0, 20 * 60 * 5);
    }

    void loopRotationBlock() {
	/*
	 * red: 14 yellow: 4 lime: 5 cyan: 9
	 */

	List<Location> spawnRotation1 = new ArrayList<>();
	spawnRotation1.add(new Location(Setting.world, 14, 4, 14));
	spawnRotation1.add(new Location(Setting.world, 14, 4, 15));
	spawnRotation1.add(new Location(Setting.world, 14, 4, 16));

	spawnRotation1.add(new Location(Setting.world, 14, 4, 17));
	spawnRotation1.add(new Location(Setting.world, 15, 4, 17));
	spawnRotation1.add(new Location(Setting.world, 16, 4, 17));

	spawnRotation1.add(new Location(Setting.world, 17, 4, 17));
	spawnRotation1.add(new Location(Setting.world, 17, 4, 16));
	spawnRotation1.add(new Location(Setting.world, 17, 4, 15));

	spawnRotation1.add(new Location(Setting.world, 17, 4, 14));
	spawnRotation1.add(new Location(Setting.world, 16, 4, 14));
	spawnRotation1.add(new Location(Setting.world, 15, 4, 14));

	Rotationer spawn = new Rotationer("spawn", 10 * 2, spawnRotation1, Rotationer.Direction.CLOCK);
	BlockRotateTool.registerRotationer(spawn);

	List<Location> spawnRotation2 = new ArrayList<>();
	spawnRotation2.add(new Location(Setting.world, 14, 5, 14));
	spawnRotation2.add(new Location(Setting.world, 14, 5, 15));
	spawnRotation2.add(new Location(Setting.world, 14, 5, 16));

	spawnRotation2.add(new Location(Setting.world, 14, 5, 17));
	spawnRotation2.add(new Location(Setting.world, 15, 5, 17));
	spawnRotation2.add(new Location(Setting.world, 16, 5, 17));

	spawnRotation2.add(new Location(Setting.world, 17, 5, 17));
	spawnRotation2.add(new Location(Setting.world, 17, 5, 16));
	spawnRotation2.add(new Location(Setting.world, 17, 5, 15));

	spawnRotation2.add(new Location(Setting.world, 17, 5, 14));
	spawnRotation2.add(new Location(Setting.world, 16, 5, 14));
	spawnRotation2.add(new Location(Setting.world, 15, 5, 14));

	Rotationer spawn2 = new Rotationer("spawn2", 10 * 2, spawnRotation2, Rotationer.Direction.CLOCK);
	BlockRotateTool.registerRotationer(spawn2);

	List<Location> spawnRotation3 = new ArrayList<>();
	spawnRotation3.add(new Location(Setting.world, 14, 6, 14));
	spawnRotation3.add(new Location(Setting.world, 14, 6, 15));
	spawnRotation3.add(new Location(Setting.world, 14, 6, 16));

	spawnRotation3.add(new Location(Setting.world, 14, 6, 17));
	spawnRotation3.add(new Location(Setting.world, 15, 6, 17));
	spawnRotation3.add(new Location(Setting.world, 16, 6, 17));

	spawnRotation3.add(new Location(Setting.world, 17, 6, 17));
	spawnRotation3.add(new Location(Setting.world, 17, 6, 16));
	spawnRotation3.add(new Location(Setting.world, 17, 6, 15));

	spawnRotation3.add(new Location(Setting.world, 17, 6, 14));
	spawnRotation3.add(new Location(Setting.world, 16, 6, 14));
	spawnRotation3.add(new Location(Setting.world, 15, 6, 14));

	Rotationer spawn3 = new Rotationer("spawn3", 10 * 2, spawnRotation3, Rotationer.Direction.CLOCK);
	BlockRotateTool.registerRotationer(spawn3);

	// spawn loof
	List<Location> loofLocs = new ArrayList<>();
	loofLocs.add(new Location(Setting.world, 14, 7, 14));
	loofLocs.add(new Location(Setting.world, 14, 7, 15));
	loofLocs.add(new Location(Setting.world, 14, 7, 16));

	loofLocs.add(new Location(Setting.world, 14, 7, 17));
	loofLocs.add(new Location(Setting.world, 15, 7, 17));
	loofLocs.add(new Location(Setting.world, 16, 7, 17));

	loofLocs.add(new Location(Setting.world, 17, 7, 17));
	loofLocs.add(new Location(Setting.world, 17, 7, 16));
	loofLocs.add(new Location(Setting.world, 17, 7, 15));

	loofLocs.add(new Location(Setting.world, 17, 7, 14));
	loofLocs.add(new Location(Setting.world, 16, 7, 14));
	loofLocs.add(new Location(Setting.world, 15, 7, 14));

	Rotationer loof = new Rotationer("loof", 10 * 2, loofLocs, Rotationer.Direction.CLOCK);
	BlockRotateTool.registerRotationer(loof);

	// ratating 시작
	BlockRotateTool.startRotating();
    }

    private void loopUpdateAllStage() {
	Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), new Runnable() {

	    @Override
	    public void run() {
		stageManager.updateAllStage();

		// gc 실행
		System.gc();
	    }
	}, 0, 20 * 60 * 5);
    }

    private void setupDiscordBot() {
	this.discordBot = new DiscordBot(this.pDataManager);
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
    public void onDisable() {
	// rank NPC 제거
	// rank NPC 는 NPC자체가 저장될 필요가 없음
	// 왜냐하면 각 waitingTime마다 순위에 따라서 NPC가 바뀌므로
	// StageManager에 위치만 지정해놓고 각 상황에따라 NPC를 삭제하고 불러와야 하므로.
	this.stageManager.removeRemainingRankNPCs();

	// file save
	this.dataManager.save();
    }
}
