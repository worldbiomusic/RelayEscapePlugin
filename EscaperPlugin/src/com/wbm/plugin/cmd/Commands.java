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
import com.wbm.plugin.util.Setting;
import com.wbm.plugin.util.enums.RelayTime;
import com.wbm.plugin.util.enums.Role;
import com.wbm.plugin.util.enums.RoomType;
import com.wbm.plugin.util.general.BroadcastTool;
import com.wbm.plugin.util.general.CoolDownManager;
import com.wbm.plugin.util.general.NPCManager;
import com.wbm.plugin.util.minigame.CooperativeMiniGame;
import com.wbm.plugin.util.minigame.MiniGame;
import com.wbm.plugin.util.minigame.MiniGameManager;
import com.wbm.plugin.util.shop.ShopGoods;

public class Commands implements CommandExecutor {
    PlayerDataManager pDataManager;
    RelayManager relayManager;
    RoomManager roomManager;
    RankManager rankManager;
    NPCManager npc;
    MiniGameManager minigameManager;

    public Commands(PlayerDataManager pDataManager, RelayManager relayManager, RoomManager roomManager,
	    RankManager rankManager, NPCManager npc, MiniGameManager minigameManager) {
	this.pDataManager = pDataManager;
	this.relayManager = relayManager;
	this.roomManager = roomManager;
	this.rankManager = rankManager;
	this.npc = npc;
	this.minigameManager = minigameManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
	if (!(sender instanceof Player)) {
	    sender.sendMessage("only player");
	}

	Player p = (Player) sender;

	if (args.length >= 1) {
	    String first = args[0];

	    switch (first) {
	    case "d": // debug
		BroadcastTool.sendMessage(p, "==========debug cmd=============");
		return this.debug(p, args);
	    case "rank":
		return this.rank(p, args);
	    case "npc":
		return this.npc(p, args);
	    case "room":
		return this.room(p, args);
	    case "minigame":
		return this.minigame(p, args);
	    }
	}

	return false;
    }

    private boolean room(Player p, String[] args) {
	// Main room, RelayTime.Making, Role Maker 체크
	if (this.relayManager.checkRoomAndRelayTimeAndRole(RoomType.MAIN, RelayTime.MAKING, Role.MAKER, p)) {

	    // 명령어 cooldown 체크
	    if (CoolDownManager.addPlayer(Setting.CoolDown_Subject_CMD_ROOM, p)) {
		String second = args[1];

		switch (second) {
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
		case "finish":
		    return this.finishRoomMaking(p, args);
		}
	    } else {
		BroadcastTool.sendMessage(p, "too fast cmd");
		return true;
	    }
	}
	BroadcastTool.sendMessage(p, "this command is for Maker");
	return true;
    }

    private boolean loadRoom(Player p, String[] args) {
	// re room load [title]
	if (args.length != 3) {
	    return false;
	}
	if (!this.hasRoomManagerItem(p)) {
	    return true;
	}
	String title = args[2];
	Room room = this.roomManager.getRoomData(title);

	// 없는 room일경우 반환
	if (room == null) {
	    BroadcastTool.sendMessage(p, title + " room is not exist");
	    return true;
	}

	// room maker가 아닐시 반환
	if (!room.getMaker().equals(p.getName())) {
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

    private boolean emtpyRoom(Player p, String[] args) {
	// re room empty
	if (args.length != 2) {
	    return false;
	}
	if (!this.hasRoomManagerItem(p)) {
	    return true;
	}
	this.roomManager.setRoomEmpty(RoomType.MAIN);
	return true;
    }

    private boolean printRoomList(Player p, String[] args) {
	// print room list
	if (args.length != 2) {
	    return false;
	}
	if (!this.hasRoomManagerItem(p)) {
	    return true;
	}
	this.roomManager.printRoomList(p);
	return true;
    }

    private boolean setRoomTitle(Player p, String[] args) {
	// /re room title "EXAMPLE"
	if (args.length != 3) {
	    return false;
	}
	if (this.relayManager.checkRoomAndRelayTimeAndRole(RoomType.MAIN, RelayTime.MAKING, Role.MAKER, p)) {
	    String title = args[2];
	    this.relayManager.setRoomTitle(title);
	    BroadcastTool.sendMessage(p, "your room tile set to " + title);
	}
	return true;
    }

    private boolean finishRoomMaking(Player p, String[] args) {
	/*
	 * Maker가 룸 다 만들고 TestingTime으로 넘어가려 할 때
	 * 
	 */

	RelayTime time = this.relayManager.getCurrentTime();
	PlayerData pData = this.pDataManager.getPlayerData(p.getUniqueId());
	Role role = pData.getRole();

	if (time == RelayTime.MAKING) {
	    if (role == Role.MAKER) {
		if (!this.relayManager.isCorePlaced()) {
		    BroadcastTool.sendMessage(p, "core is not placed");
		} else {
		    this.relayManager.startNextTime();
		}
	    }
	}
	return true;
    }

    private boolean hasRoomManagerItem(Player p) {
	/*
	 * ROOM_MANAGER goods 가지고 있는지 검사
	 */
	PlayerData pData = this.pDataManager.getPlayerData(p.getUniqueId());
	if (pData.doesHaveGoods(ShopGoods.ROOM_MANAGER)) {
	    return true;
	}
	BroadcastTool.sendMessage(p, "you need \"ROOM_MANAGER\" for this command");
	return false;
    }

    private boolean debug(Player p, String[] args) {
	if (args.length >= 2) {
	    String second = args[1];

	    switch (second) {
	    case "relay":
		this.printRelayInfo(p);
		return true;
	    case "reset":
		this.relayManager.resetRelay();
		return true;
	    case "pdata":
		this.printPlayerData(p);
		return true;
	    case "allpdata":
		this.printAllPlayerData(p);
		return true;
	    case "token":
		return this.token(p, args);
	    case "rolechange":
		this.changeRole(p, args);
		return true;
	    case "goods":
		return this.goodsCmd(p, args);
	    }
	    return false;
	}
	return false;
    }

    private boolean token(Player p, String[] args) {
	// re d token [plus|minus] <player> <n>
	if (args.length != 5) {
	    return false;
	}

	String order = args[2];
	String pName = args[3];
	int token = Integer.parseInt(args[4]);
	Player targetP = Bukkit.getPlayer(pName);

	PlayerData pData = this.pDataManager.getPlayerData(targetP.getUniqueId());
	if (order.equalsIgnoreCase("plus")) {
	    pData.plusToken(token);
	} else if (order.equalsIgnoreCase("minus")) {
	    pData.minusToken(token);
	} else {
	    return false;
	}

	// info
	BroadcastTool.sendMessage(p, pName + "token " + order + " to " + token);
	BroadcastTool.sendMessage(targetP, "your token" + order + " to " + token + " by " + p.getName());

	return true;
    }

    private void printPlayerData(Player p) {
	PlayerData pData = this.pDataManager.getPlayerData(p.getUniqueId());
	BroadcastTool.sendMessage(p, pData.toString());
    }

    private void printAllPlayerData(Player p) {
	Map<UUID, PlayerData> players = this.pDataManager.getOnlyOnlinePlayerData();
	for (PlayerData pData : players.values()) {
	    BroadcastTool.sendMessage(p, pData.toString());
	    BroadcastTool.printConsoleMessage(pData.toString());
	}
    }

    private void printRelayInfo(Player p) {
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
	for (Player each : Bukkit.getOnlinePlayers()) {
	    String eachName = each.getName();
	    // all중에 자신이름일때 색깔 초혹
	    if (eachName.equals(p.getName())) {
		eachName = ChatColor.GREEN + eachName + ChatColor.WHITE;
	    }
	    PlayerData allData = this.pDataManager.getPlayerData(each.getUniqueId());
	    Role role = allData.getRole();
	    p.sendMessage(eachName + ": " + role.name());
	}
	p.sendMessage("------------------------------");
    }

    private void changeRole(Player p, String[] args) {
	// /re d rolechange <playerName> <role>
	String pName = args[2];
	String roleStr = args[3];

	Player targetP = Bukkit.getPlayer(pName);
	Role role = Role.valueOf(roleStr);

	PlayerData pData = this.pDataManager.getPlayerData(targetP.getUniqueId());
	pData.setRole(role);
    }

    void printCurrentRelayTime(Player p) {
	RelayTime time = this.relayManager.getCurrentTime();
	p.sendMessage(ChatColor.BOLD + "[Time]");
	p.sendMessage(time.name());
	p.sendMessage("------------------------------");
    }

    private void printMaker(Player p) {
	String makerName = "";
	Player maker = this.pDataManager.getMaker();
	if (maker != null) {
	    makerName = maker.getName();
	}
	p.sendMessage(ChatColor.BOLD + "[Maker]");
	p.sendMessage(ChatColor.RED + makerName);
	p.sendMessage("------------------------------");
    }

    private boolean rank(Player p, String[] args) {
	// /re rank [options]
	if (args.length == 2) {
	    String list = args[1];
	    switch (list) {
	    case "tokenrank":
		for (PlayerData pData : this.rankManager.getTokenRankList()) {
		    BroadcastTool.sendMessage(p, pData.getName() + ": " + pData.getToken());
		}
		break;
	    case "challengingrank":
		for (PlayerData pData : this.rankManager.getChallengingCountRankList()) {
		    BroadcastTool.sendMessage(p, pData.getName() + ": " + pData.getChallengingCount());
		}
		break;
	    case "clearrank":
		for (PlayerData pData : this.rankManager.getClearCountRankList()) {
		    BroadcastTool.sendMessage(p, pData.getName() + ": " + pData.getClearCount());
		}
		break;
	    case "roomcountrank":
		for (PlayerData pData : this.rankManager.getRoomCountRankList()) {
		    BroadcastTool.sendMessage(p,
			    pData.getName() + ": " + this.roomManager.getOwnRooms(pData.getName()).size());
		}
		break;
	    default:
		return false;
	    }

	    return true;
	}
	return false;
    }

    private boolean npc(Player p, String[] args) {
	/*
	 * /re npc create <name> <skin>
	 * 
	 * /re npc delete <name>
	 */

	if (args.length >= 3) {
	    String option = args[1];
	    String name = args[2];
	    switch (option) {
	    case "create":
		if (args.length == 4) {
		    String skin = args[3];
		    npc.createNPC(p.getLocation(), name, skin);
		    return true;
		}
		return false;
	    case "delete":
		npc.delete(name);
		return true;
	    }
	}
	return false;
    }

    private boolean minigame(Player p, String[] args) {
	/*
	 * 설명] CooperativeMiniGame의 master만 사용가능한 명령어
	 * 
	 * /re minigame [ok | kick] <playerName>
	 * 
	 * /re minigame waitlist
	 */

	MiniGame minigame = this.minigameManager.getPlayingGame(p);
	// 하고 있는 미니게임이 CooperativeMiniGame일때
	if (minigame != null && minigame instanceof CooperativeMiniGame) {
	    CooperativeMiniGame game = (CooperativeMiniGame) minigame;

	    Player master = game.getMaster();
	    // master일때
	    if (p.equals(master)) {

		// 명령어 실행
		String order = args[1];
		switch (order) {
		case "waitlist":
		    this.printWaitList(p, args, game);
		    break;
		case "ok":
		case "kick":
		    this.manageList(p, args, game);
		    break;
		}

	    }
	}
	return true;
    }

    private void manageList(Player p, String[] args, CooperativeMiniGame game) {
	/*
	 * /re minigame [ok | kick] <playerName>
	 * 
	 * 1.player가 null이 아닐때(서버 안나갔을때)(밖에서 거름)
	 * 
	 * 2.이미 <name> 플레이어가 다른 미니게임을 플레이하고 있지 않을 때
	 */

	String order = args[1];
	String targetPlayerName = args[2];
	Player targetPlayer = Bukkit.getPlayer(targetPlayerName);
	// 1.player가 null이 아닐때(서버 안나갔을때)(밖에서 거름)
	if (targetPlayer == null) {
	    BroadcastTool.sendMessage(p, "not exist player");
	    return;
	}

	PlayerData targetPData = this.pDataManager.getPlayerData(targetPlayer.getUniqueId());

	// 2.이미 <name> 플레이어가 다른 미니게임을 플레이하고 있지 않을 때
	if (this.minigameManager.isPlayerPlayingGame(targetPlayer)) {
	    BroadcastTool.sendMessage(p, targetPlayer.getName() + " is already playing another minigame");
	    return;
	}

	// 분기
	if (order.equalsIgnoreCase("ok")) {
	    game.okWaitingPlayer(targetPlayer, targetPData);
	} else if (order.equalsIgnoreCase("kick")) {
	    game.kickPlayer(targetPlayer);
	}
    }

    private void printWaitList(Player p, String[] args, CooperativeMiniGame game) {
	BroadcastTool.sendMessage(p, "========= WAIT LIST =========");
	for (Player waiter : game.getPlayer()) {
	    BroadcastTool.sendMessage(p, waiter.getName());
	}
    }

    private boolean goodsCmd(Player p, String[] args) {
	/*
	 * /re d goods init <player> /re d goods [add | remove] <player> <ShopGoods>
	 */
	String option = args[2];
	String playerName = args[3];
	PlayerData pData = this.pDataManager.getPlayerData(Bukkit.getPlayerUniqueId(playerName));

	if (args.length > 3) {
	    switch (option) {
	    case "init":
		pData.makeEmptyGoods();
		BroadcastTool.sendMessage(p, playerName + " goods made empty");
		return true;
	    case "add":
	    case "remove":
		return manageGoods(p, args);
	    }
	    return false;
	}

	return false;
    }

    private boolean manageGoods(Player p, String[] args) {
	String option = args[2];
	String playerName = args[3];
	String goodsName = args[4];
	PlayerData pData = this.pDataManager.getPlayerData(Bukkit.getPlayerUniqueId(playerName));
	ShopGoods goods = ShopGoods.valueOf(goodsName);

	if (args.length == 5) {
	    switch (option) {
	    case "add":
		pData.addGoods(goods);
		BroadcastTool.sendMessage(p, "add " + goods.name() + " to " + playerName);
		return true;
	    case "remove":
		pData.removeGoods(goods);
		BroadcastTool.sendMessage(p, "remove " + goods.name() + " from " + playerName);
		return true;
	    }
	    return false;
	}

	return false;
    }
}

//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
