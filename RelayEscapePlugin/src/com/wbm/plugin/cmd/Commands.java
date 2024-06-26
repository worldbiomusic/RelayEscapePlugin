package com.wbm.plugin.cmd;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.wbm.plugin.data.PlayerData;
import com.wbm.plugin.data.Room;
import com.wbm.plugin.data.RoomLocation;
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
import com.wbm.plugin.util.general.TeleportTool;
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
			case "d": // op
				BroadcastTool.sendMessage(p, "==========debug cmd=============");
				return this.debug(p, args);
			case "rank": // op
				return this.rank(p, args);
			case "npc": // op
				return this.npc(p, args);
			case "room":
				return this.room(p, args);
			case "minigame":
				return this.minigame(p, args);
			case "tutorial":
				return this.printTutorial(p, args);
			case "goods":
				this.printGoods(p, args);
				return true;
			case "ghost":
				return this.ghostMode(p, args);
			}
		}

		return false;
	}

	private boolean ghostMode(Player p, String[] args) {
		// /re ghost
		// 조건 1.자신의 역할이 viewer일 때, 2.GHOST굿즈 가지고 있을 때
		// SPECTATOR <-> ADVENTURE 변경
		PlayerData pData = this.pDataManager.getPlayerData(p.getUniqueId());
		if (pData.getRole() == Role.뷰어) {
			if (pData.hasGoods(ShopGoods.고스트)) {
				GameMode gm = p.getGameMode();
				if (gm == Role.뷰어.getGameMode()) {
					p.setGameMode(GameMode.SPECTATOR);
					BroadcastTool.sendMessage(p, "게임모드가 " + GameMode.SPECTATOR.name() + " 로 변경되었습니다");
				} else {
					p.setGameMode(Role.뷰어.getGameMode());
					BroadcastTool.sendMessage(p, "게임모드가 " + Role.뷰어.getGameMode().name() + " 로 변경되었습니다");
				}
				// 게임모드 변경후 join으로 이동
				TeleportTool.tp(p, RoomLocation.MAIN_SPAWN);
			} else {
				BroadcastTool.sendMessage(p, "GHOST굿즈가 필요합니다");
			}
		} else {
			BroadcastTool.sendMessage(p, "맵 제작자만 사용가능합니다");
		}
		return true;
	}

	private boolean debugRoom(Player p, String[] args) {
		/*
		 * 구조 엉망... (cmd로 분기해서 만들기)
		 * 
		 * /re d room [load | save | update] <roomType> <title>
		 * 
		 * /re d room [remove | changemaker | info] <title>
		 * 
		 * /re d room changemaker <title> <changedTitle>
		 * 
		 * /re d room roominfo <roomType>
		 */
		String cmd = args[2];
		if (cmd.equalsIgnoreCase("roominfo")) {
			// /re d room roominfo <roomType>
			String roomTypeString = args[3];
			roomTypeString = roomTypeString.toUpperCase();
			RoomType roomType = RoomType.valueOf(roomTypeString);

			Room room = this.roomManager.getRoom(roomType);
			BroadcastTool.sendMessage(p, room.toString());
		} else if (cmd.equalsIgnoreCase("changeMaker")) {
			// /re d room changemaker <title> <maker>
			String title = args[3];
			String maker = args[4];
			if (this.roomManager.isExistRoomTitle(title)) {
				// 이미있는 룸을 Maker는 변경
				Room room = this.roomManager.getRoomData(title);
				// PlayerDataManger에 존재하는 플레이어일때만 저장
				if (this.pDataManager.getPlayerData(maker) != null) {
					room.setMaker(maker);
				} else {
					BroadcastTool.sendMessage(p, "존재하지 않는 플레이어");
				}
			} else {
				BroadcastTool.sendMessage(p, "존재하지 않는 맵");
			}
		}

		else if (args.length == 4) {
			// /re d room [remove | changemaker | info] <title>
			String roomTitle = args[3];
			Room room = null;

			// 존재하는 룸인지 체크
			if (this.roomManager.isExistRoomTitle(roomTitle)) {
				room = this.roomManager.getRoomData(roomTitle);
			} else {
				BroadcastTool.sendMessage(p, "존재하지 않는 맵");
				return true;
			}

			if (cmd.equalsIgnoreCase("info")) {
				BroadcastTool.sendMessage(p, room.toString());
			} else if (cmd.equalsIgnoreCase("remove")) {
				if (this.roomManager.removeRoom(roomTitle) == null) {
					BroadcastTool.sendMessage(p, "존재하지 않는 맵");
				}
			}
		} else if (args.length == 5) {
			// /re d room [load | save | update] <roomType> <title>
			String roomTypeString = args[3];
			roomTypeString = roomTypeString.toUpperCase();
			RoomType roomType = RoomType.valueOf(roomTypeString);

			String roomTitle = args[4];
			if (cmd.equalsIgnoreCase("load")) {
				if (this.roomManager.isExistRoomTitle(roomTitle)) {
					Room room = this.roomManager.getRoomData(roomTitle);
					this.roomManager.setRoom(roomType, room);
				} else {
					BroadcastTool.sendMessage(p, "존재하지 않는 맵");
				}
			} else if (cmd.equalsIgnoreCase("save")) {
				// title이 존재하지 않는 룸일때 저장
				if (!this.roomManager.isExistRoomTitle(roomTitle)) {
					this.roomManager.saveRoomData(roomType, p.getName(), roomTitle);
				}
			} else if (cmd.equalsIgnoreCase("update")) {
				// 이미있는 룸을 Maker는 유지하고 업데이트해서 변경
				// [주의] 룸 DataBlock, maker제외하고 모두 초기화 됨
				if (this.roomManager.isExistRoomTitle(roomTitle)) {
					Room room = this.roomManager.getRoomData(roomTitle);
					this.roomManager.saveRoomData(roomType, room.getMaker(), roomTitle);
				} else {
					BroadcastTool.sendMessage(p, "존재하지 않는 맵");
				}
			}
		}

		return true;
	}

	private boolean room(Player p, String[] args) {
		// Main room, RelayTime.Making, Role Maker 체크
		if (this.relayManager.checkRoomAndRelayTimeAndRole(RoomType.메인, RelayTime.메이킹, Role.메이커, p)) {

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
				BroadcastTool.sendMessage(p, "명령어 입력이 너무 빠릅니다. 조금만 기다려주세요");
				return true;
			}
		}
		BroadcastTool.sendMessage(p, "해당 명령어는 메이커를 위한 명령어 입니다");
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
			BroadcastTool.sendMessage(p, title + " 존재하지 않는 맵");
			return true;
		}

		// room maker가 아닐시 반환
		if (!room.getMaker().equals(p.getName())) {
			BroadcastTool.sendMessage(p, "당신은 " + title + " 맵의 제작자가 아닙니다");
			return true;
		}

		// set corePlaced TRUE! (이전room은 모두 test통과했으므로 core가 무조건 있음)
		this.relayManager.setCorePlaced(true);

		// set room
		this.roomManager.fillSpace(RoomType.메인, title);
		BroadcastTool.sendMessage(p, title + " 맵을 불러왔습니다");

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
		this.roomManager.setRoomEmpty(RoomType.메인);

		// empty룸이므로 core false로 변경
		this.relayManager.setCorePlaced(false);
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
		if (this.relayManager.checkRoomAndRelayTimeAndRole(RoomType.메인, RelayTime.메이킹, Role.메이커, p)) {
			String title = args[2];
			if (this.relayManager.isMainRoomTitleExist(title)) {
				BroadcastTool.sendMessage(p, "맵 제목이 이미 존재합니다");
			} else {
				this.relayManager.setMainRoomTitle(title);
				BroadcastTool.sendMessage(p, "맵 제목이 " + title + " 로 설정되었습니다");
			}
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

		// 조건 체크
		if (time == RelayTime.메이킹) {
			if (role == Role.메이커) {

				// room finish 실행
				if (!this.relayManager.isCorePlaced()) {
					BroadcastTool.sendMessage(p, "코어(발광석)가 설치되지 않았습니다");
					return true;
				}

//		MakingTime때 최소 Setting.MinimunMakingTime초는 지나야 맵 테스트할 수 있음
				int leftTime = this.relayManager.getLeftTime();
				int highestMakingTime = pData.getRoomSettingGoodsHighestValue("MAKINGTIME");
				highestMakingTime *= 60; // 굿즈단위가 분이여서 60 곱하기
				int timeLimit = highestMakingTime - Setting.MinimunMakingTime;

				if (leftTime > timeLimit) {
					BroadcastTool.sendMessage(p, (leftTime - timeLimit) + " 초 후에 테스트가 가능합니다");
					return true;
				}

				// 위의 상황을 모두 건너면 다음타임 실행
				this.relayManager.startNextTime();

			}
		}
		return true;
	}

	private boolean hasRoomManagerItem(Player p) {
		/*
		 * ROOM_MANAGER goods 가지고 있는지 검사
		 */
		PlayerData pData = this.pDataManager.getPlayerData(p.getUniqueId());
		if (pData.hasGoods(ShopGoods.맵_관리)) {
			return true;
		}
		BroadcastTool.sendMessage(p, "\"ROOM_MANAGER\" 굿즈가 필요합니다");
		return false;
	}

	private boolean debug(Player p, String[] args) {
		// check OP
		if (!p.isOp()) {
			BroadcastTool.sendMessage(p, "OP CMD");
			return true;
		}
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
				this.printPlayerData(p, args);
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
			case "cmd":
				this.printAllCMD(p, args);
				return true;
			case "cash":
				return this.cash(p, args);
			case "room":
				return this.debugRoom(p, args);
			}
			return false;
		}
		return false;
	}

	private boolean cash(Player p, String[] args) {
		// /re d cash [plus | minus] <player> <amount>

		String cmd = args[2];
		Player targetP = Bukkit.getPlayer(args[3]);
		int amount = Integer.parseInt(args[4]);

		PlayerData targetPData = this.pDataManager.getPlayerData(targetP.getUniqueId());

		if (cmd.equalsIgnoreCase("plus")) {
			targetPData.plusCash(amount);
		} else if (cmd.equalsIgnoreCase("minus")) {
			targetPData.minusCash(amount);
		}

		// 캐쉬 잔액 알림
		BroadcastTool.sendMessage(p, targetP.getName() + " cash is now " + targetPData.getCash());

		return true;
	}

	private void printAllCMD(Player p, String[] args) {
		String cmd = "\n" + "/re d [relay | reset | pdata <player> | allpdata]\n"
				+ "/re d [token | cash] [plus | minus] <player> <amount>\n" + "/re d rolechange <playerName> <role>\n"
				+ "/re d goods [init | addall] <player>\n" + "/re d goods [add | remove] <player> <goods>\n"
				+ "/re rank [tokenrank | challengingrank | clearrank | roomcountrank]\n"
				+ "/re npc create <name> <skinName>\n" + "/re npc delete <name> \n"
				+ "/re d room [load | save | update] <roomType> <title>\n"
				+ "/re d room [remove | changemaker | info] <title>\n"
				+ "/re d room changemaker <title> <changedTitle>\n" + "/re d room roominfo <roomType>\n"
				+ "/re room [empty | list | finish]\n" + "/re minigame [ok | kick] <player>\n"
				+ "/re minigame waitlist";

		BroadcastTool.sendMessage(p, cmd);
	}

	private boolean token(Player p, String[] args) {
		// re d token [plus|minus] <player> <n>
		if (args.length != 5) {
			return false;
		}

		String order = args[2];
		String pName = args[3];
		int token = Integer.parseInt(args[4]);
		UUID uuid = Bukkit.getPlayerUniqueId(pName);

		PlayerData pData = this.pDataManager.getPlayerData(uuid);
		if (order.equalsIgnoreCase("plus")) {
			pData.plusToken(token);
		} else if (order.equalsIgnoreCase("minus")) {
			pData.minusToken(token);
		} else {
			return false;
		}

		// info
		BroadcastTool.sendMessage(p, pName + "token " + order + " to " + token);
		Player targetP = Bukkit.getPlayer(pName);
		if (targetP != null) {
			BroadcastTool.sendMessage(targetP, "your token" + order + " to " + token);
		}

		return true;
	}

	private void printPlayerData(Player p, String[] args) {
		// /re d pdata
		// /re d pdata <player>
		// target있을떄 targetP 변경
		if (args.length == 3) {
			String targetPName = args[2];
			UUID uuid = Bukkit.getPlayerUniqueId(targetPName);

			PlayerData pData = this.pDataManager.getPlayerData(uuid);
			BroadcastTool.sendMessage(p, pData.toString());
		}
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
		// print MainRoom info
		this.printAllRoomInfo(p);
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

	private void printAllRoomInfo(Player p) {
		p.sendMessage(ChatColor.BOLD + "[Room]");

		p.sendMessage(ChatColor.RED + "MainRoom" + ChatColor.WHITE);
		Room mainRoom = this.roomManager.getRoom(RoomType.메인);
		p.sendMessage(mainRoom.toString());

		p.sendMessage(ChatColor.RED + "PracticeRoom" + ChatColor.WHITE);
		Room practiceRoom = this.roomManager.getRoom(RoomType.연습);
		if (practiceRoom != null)
			p.sendMessage(practiceRoom.toString());

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
		// check OP
		if (!p.isOp()) {
			BroadcastTool.sendMessage(p, "OP CMD");
			return true;
		}
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
//		/*
//		 * /re npc create <name> <skin>
//		 * 
//		 * /re npc delete <name>
//		 */
//
//		// check OP
//		if (!p.isOp()) {
//			BroadcastTool.sendMessage(p, "OP CMD");
//			return true;
//		}
//		if (args.length >= 3) {
//			String option = args[1];
//			String name = args[2];
//			switch (option) {
//			case "create":
//				if (args.length == 4) {
//					String skin = args[3];
//					npc.createNPC(p.getLocation(), name, skin);
//					return true;
//				}
//				return false;
//			case "delete":
//				npc.delete(name);
//				return true;
//			}
//		}
		return false;
	}

	private boolean minigame(Player p, String[] args) {
		/*
		 * 설명] CooperativeMiniGame의 master만 사용가능한 명령어
		 * 
		 * /re minigame [ok | kick] <playerName>
		 * 
		 * /re minigame waitlist
		 * 
		 * /re minigame rank <minigaem>
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
		} else { // rank 출력
			String order = args[1];
			switch (order) {
			case "rank":
				this.printMiniGameRank(p, args);
				break;
			}
		}
		return true;
	}

	private void printMiniGameRank(Player p, String[] args) {

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
			BroadcastTool.sendMessage(p, "존재하지 않는 플레이어 이름");
			return;
		}

		PlayerData targetPData = this.pDataManager.getPlayerData(targetPlayer.getUniqueId());

		// 2.이미 <name> 플레이어가 다른 미니게임을 플레이하고 있지 않을 때
		if (this.minigameManager.isPlayerPlayingGame(targetPlayer)) {
			BroadcastTool.sendMessage(p, targetPlayer.getName() + "님은 이미 다른 미니게임을 플레이 중입니다");
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
		BroadcastTool.sendMessage(p, "========= 대기 리스트 =========");
		for (Player waiter : game.getAllPlayer()) {
			BroadcastTool.sendMessage(p, waiter.getName());
		}
	}

	private boolean goodsCmd(Player p, String[] args) {
		/*
		 * /re d goods [init | addall] <player>
		 * 
		 * /re d goods [add | remove] <player> <ShopGoods>
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
			case "addall":
				for (ShopGoods good : ShopGoods.values()) {
					pData.addGoods(good);
				}
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

	private boolean printTutorial(Player p, String[] args) {
		List<String> tutorials = new ArrayList<>();
		tutorials.add("=================================");
		tutorials.add("=========      튜토리얼       =========");
		tutorials.add("=================================");
		tutorials.add("- 규칙: 맵의 코어(발광석)을 찾아 우클릭 하면 메이커가 될 수 있습니다");
		tutorials.add("- 커맨드 자세히 보기: /re");
		tutorials.add(
				"- 디스코드: " + ChatColor.YELLOW + ChatColor.BOLD + " https://discord.gg/EwXk9Cd2Ya" + ChatColor.WHITE + "(클릭)");

		tutorials.add("=================================");
		tutorials.add("=========         끝         =========");
		tutorials.add("=================================");

		for (String msg : tutorials) {
			BroadcastTool.sendMessage(p, msg);
		}

		return true;
	}

	private boolean printGoods(Player p, String[] args) {
		BroadcastTool.sendMessage(p, "========= 굿즈 컬렉션 =========");
		PlayerData pData = this.pDataManager.getPlayerData(p.getUniqueId());
		for (ShopGoods good : pData.getGoods()) {
			BroadcastTool.sendMessage(p, good.name());
		}

		return true;
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
