package com.wbm.plugin.listener;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.inventory.ItemStack;

import com.wbm.plugin.data.PlayerData;
import com.wbm.plugin.data.RoomLocation;
import com.wbm.plugin.util.PlayerDataManager;
import com.wbm.plugin.util.RelayManager;
import com.wbm.plugin.util.Setting;
import com.wbm.plugin.util.discord.DiscordBot;
import com.wbm.plugin.util.enums.MiniGameType;
import com.wbm.plugin.util.enums.Role;
import com.wbm.plugin.util.enums.RoomType;
import com.wbm.plugin.util.general.BanItemTool;
import com.wbm.plugin.util.general.BroadcastTool;
import com.wbm.plugin.util.general.ChatColorTool;
import com.wbm.plugin.util.general.CoolDownManager;
import com.wbm.plugin.util.general.LocationTool;
import com.wbm.plugin.util.general.NPCManager;
import com.wbm.plugin.util.general.PlayerTool;
import com.wbm.plugin.util.general.SpawnLocationTool;
import com.wbm.plugin.util.general.TeleportTool;
import com.wbm.plugin.util.general.skin.SkinManager;
import com.wbm.plugin.util.minigame.MiniGame;
import com.wbm.plugin.util.minigame.MiniGameManager;
import com.wbm.plugin.util.shop.ShopGoods;
import com.wbm.plugin.util.shop.ShopManager;

public class CommonListener implements Listener {
	/*
	 * class 설명: 어디서나 언제나 해당되는 리스너
	 */
	PlayerDataManager pDataManager;
	ShopManager shopManager;
	BanItemTool banItems;
	NPCManager npc;
	SkinManager skinManager;
	MiniGameManager miniGameManager;
	RelayManager relayManager;
	DiscordBot discordBot;

	EventBlockListener eventBlockListener;

	public CommonListener(PlayerDataManager pDataManager, ShopManager shopManager, BanItemTool banItems, NPCManager npc,
			SkinManager skinManager, MiniGameManager miniGameManager, RelayManager relayManager,
			DiscordBot discordBot) {
		this.pDataManager = pDataManager;
		this.shopManager = shopManager;
		this.banItems = banItems;
		this.npc = npc;
		this.skinManager = skinManager;
		this.miniGameManager = miniGameManager;
		this.relayManager = relayManager;
		this.discordBot = discordBot;

		eventBlockListener = new EventBlockListener();
	}

	@EventHandler
	public static void onFoodLevelChanged(FoodLevelChangeEvent e) {
		e.setCancelled(true);
	}

	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent e) {
		/*
		 * cash 굿즈 CHAT을 가지면 채팅 가능
		 */
		Player p = e.getPlayer();
//	PlayerData pData = this.pDataManager.getPlayerData(p.getUniqueId());

		// chat 쿨다운 관리
		if (CoolDownManager.addPlayer(Setting.CoolDown_Subject_CHAT, p)) {

//	    // CHAT가지고 있을떄
//	    if (pData.hasGoods(ShopGoods.CHAT)) {
//		// 동작 필요 x
//		// 소리 재생
//		PlayerTool.playSoundToEveryone(Sound.BLOCK_END_PORTAL_FRAME_FILL);
//		String discordChat = "[" + p.getName() + "] " + e.getMessage();
//		this.discordBot.sendMsgWithChannel("server-chat", discordChat);
//	    } else {
//
//		String msg = e.getMessage();
//
//		String translatedMsg = "";
//		switch (msg) {
//		case "1":
//		    translatedMsg = "HI";
//		    break;
//		case "2":
//		    translatedMsg = "BYE";
//		    break;
//		case "3":
//		    translatedMsg = "FUXX";
//		    break;
//		case "4":
//		    translatedMsg = "FOLLOW ME";
//		    break;
//		case "5":
//		    translatedMsg = "VOTE";
//		    break;
//		case "6":
//		    translatedMsg = "PASS";
//		    break;
//		case "7":
//		    translatedMsg = "WOW";
//		    break;
//		case "8":
//		    translatedMsg = "LOL";
//		    break;
//		case "9":
//		    translatedMsg = "...";
//		    break;
//		default:
//		    e.setCancelled(true);
//		    return;
//
//		}
//
//		e.setMessage(translatedMsg);
//		// 소리 재생
//		PlayerTool.playSoundToEveryone(Sound.BLOCK_END_PORTAL_FRAME_FILL);
//
//		String discordChat = "[" + p.getName() + "] " + translatedMsg;
//		this.discordBot.sendMsgWithChannel("server-chat", discordChat);
//	    }

			String msg = e.getMessage();

			String translatedMsg = msg;
			switch (msg) {
			case "1":
				translatedMsg = "안녕하세요!";
				break;
			case "2":
				translatedMsg = "잘 가요!";
				break;
			case "3":
				translatedMsg = "이런!";
				break;
			case "4":
				translatedMsg = "따라오세요";
				break;
			case "5":
				translatedMsg = "OK";
				break;
			case "6":
				translatedMsg = "패스";
				break;
			case "7":
				translatedMsg = "와우";
				break;
			case "8":
				translatedMsg = "ㅋㅋ";
				break;
			case "9":
				translatedMsg = "...";
				break;
			}

			// 소리 재생
			PlayerTool.playSoundToEveryone(Sound.BLOCK_END_PORTAL_FRAME_FILL);
			String discordChat = "[" + p.getName() + "] " + translatedMsg;
			this.discordBot.sendMsgToChannelWithTime(Setting.DISCORD_CH_SERVER_CHAT, discordChat);

			// 메세지 설정
			e.setMessage(translatedMsg);
		} else {
			BroadcastTool.sendMessage(p, "채팅속도가 너무 빠릅니다. 잠시 기다려주세요^^");
			e.setCancelled(true);
		}

	}

	@EventHandler
	public void onPlayerUseCommand(PlayerCommandPreprocessEvent e) {
		Player p = e.getPlayer();
		// check command
		String msg = e.getMessage();

//	op 제외
		if (p.isOp()) {
			return;
		}

		// re명령어 빼고 모두 막기 (일반유저)
		if (msg.startsWith("/")) {
			if (!msg.startsWith("/re")) {
				BroadcastTool.sendMessage(p, ChatColor.RED + "\"/re\"명령어만 사용 가능합니다");
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onPVP(EntityDamageByEntityEvent e) {
		// Player가 Player에게 피해입는것 기본적으로 금지
		if (e.getEntity() instanceof Player) {
			// 기본적으로 금지
			e.setCancelled(true);
		}
		// 누구에게 누가 피해 입든 pvp minigame 관련 이벤트 전송
		this.miniGameManager.processEvent(e);
	}

	@EventHandler
	public void onPVP(EntityDamageEvent e) {
		if (e.getCause() == DamageCause.FALL) {
			e.setCancelled(true);
		} else if ((e.getEntity() instanceof Player)
				&& e.getCause().equals(EntityDamageEvent.DamageCause.SUFFOCATION)) {
			e.setCancelled(true);
		}

	}

	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent e) {
		// 여러 RelayTime에 따라 리스폰 위치 조정업
		// WAITER 제외하고 모두 RESPAWN지점
		Player p = e.getPlayer();
		Role role = this.pDataManager.getPlayerData(p.getUniqueId()).getRole();

		Location respawnLoc = SpawnLocationTool.RESPAWN;
		if (role == Role.웨이터) {
			respawnLoc = SpawnLocationTool.LOBBY;
		} else {
			respawnLoc = RoomLocation.MAIN_SPAWN;
		}

		e.setRespawnLocation(respawnLoc);
	}

	@EventHandler
	public void onPlayerDeathInManyTimes(PlayerDeathEvent e) {
		// clear drops
		e.getDrops().clear();
		// inven save
		e.setKeepInventory(true);
	}

	@EventHandler
	public void onPlayerUseShop(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		Block b = e.getClickedBlock();

		// 터치한 블럭 없을때 return
		if (b != null) {

			// sign click
			if (b.getType() == Material.OAK_WALL_SIGN
					|| b.getType() == Material.OAK_SIGN) {
				Action act = e.getAction();
				if (act == Action.RIGHT_CLICK_BLOCK) {
					Sign sign = (Sign) b.getState();
					String[] lines = sign.getLines();

					// SHOP click
					if (lines[0].equals("[상점]")) {
						String goods = lines[1];
						String[] costString = lines[2].split(" ");
						String type = costString[0];
						int cost = Integer.parseInt(costString[1]);
						// 띄어쓰기 기분으로 2번째를 token number로 봄
						this.shopManager.purchase(p, goods, type, cost);
					}
				}
			}
		}
	}

	@EventHandler
	public void onPlayerConsumeItem(PlayerItemConsumeEvent e) {
//	e.setCancelled(true);
	}

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent e) {
		e.setCancelled(true);
	}

	@EventHandler
	public void onPlayerOpenIventoryWhenMaker(InventoryCreativeEvent e) {
		Player p = (Player) e.getWhoClicked();
		String invTitle = e.getView().getTitle();
		BroadcastTool.debug("invTitle: " + invTitle);

		Role role = this.pDataManager.getPlayerData(p.getUniqueId()).getRole();
		if (role == Role.메이커 || role == Role.뷰어) {
			if (!invTitle.equals(ShopGoods.상자.name())) {
				BroadcastTool.debug("only " + ShopGoods.상자.name() + " inventory is allowed");
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onPlayerJoinsaveSkin(PlayerJoinEvent e) {
		Player p = e.getPlayer();

		// 입장 소리 재생
		PlayerTool.playSoundToEveryone(Sound.BLOCK_CHEST_OPEN);

		// skin data 다운
		String pName = p.getName();
		if (!this.skinManager.doesExist(pName)) {
			this.skinManager.addPlayerSkinData(pName);
			BroadcastTool.debug("add skin: " + pName);
		}

		// NPC packet
//		if (npc.getNPCs() == null || npc.getNPCs().isEmpty())
//			return;
//		npc.sendAllNPCPacketToPlayer(p);

		// discord info
		this.discordBot.sendMsgToChannelWithTime(Setting.DISCORD_CH_SERVER_CHAT, pName + "님 서버 입장했습니다");
	}

	@EventHandler
	public void onPlayerMoveOnTheEventBlock(PlayerMoveEvent e) {
		// event block 리스너
		this.eventBlockListener.processEventBlockEvent(e);
		// minigame
		this.miniGameManager.processEvent(e);
	}

	@EventHandler
	public void onPlayerJoinMiniGame(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		Block b = e.getClickedBlock();
		PlayerData pData = this.pDataManager.getPlayerData(p.getUniqueId());

		if (b != null) {
			if (b.getType() == Material.OAK_WALL_SIGN
					|| b.getType() == Material.OAK_SIGN) {
				if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
					// room, time, role 체크
					if (pData.getRole() == Role.웨이터) {
						Sign sign = (Sign) b.getState();
						/*
						 * 0: [MINI_GAME]
						 * 
						 * 1: <game title>
						 * 
						 * 2: TOKEN <n>
						 * 
						 * 3: ( game type )
						 */
						String[] lines = sign.getLines();
						String minigame = lines[0];
						String title = lines[1];
						// token은 안 남겨줘도 gameType에서 가져옴

						// 1
						if (minigame.equalsIgnoreCase("[미니게임]")) {
							// maker일때는 제외
							if (this.pDataManager.isMaker(p)) {
								BroadcastTool.sendMessage(p, "메이커일 때는 미니게임을 플레이할 수 없습니다");
								return;
							}
							this.miniGameManager.enterRoom(MiniGameType.valueOf(title), p);
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void handleMiniGameResultonPlayerQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();

		// 퇴장 소리 재생
		PlayerTool.playSoundToEveryone(Sound.BLOCK_CHEST_CLOSE);

		// player가 플레이중이던 미니게임 종료
		this.miniGameManager.handleMiniGameExitDuringPlaying(p, MiniGame.ExitReason.SELF_EXIT);

		// discord info
		this.discordBot.sendMsgToChannelWithTime(Setting.DISCORD_CH_SERVER_CHAT, p.getName() + "님이 서버 나갔습니다");
	}

	@EventHandler
	public void onPlayerBreakHanging(HangingBreakByEntityEvent e) {
		Player p = (Player) e.getRemover();

		// op아니면 블럭 부서지는것 방지
		if (p.isOp()) {
			return;
		}

		e.setCancelled(true);
	}

	@EventHandler
	public void onItemFrameItemRemovalByPlayer(EntityDamageByEntityEvent e) {
		if (e.getEntity() instanceof ItemFrame) {
			if (e.getDamager() instanceof Player) {
				Player p = (Player) e.getDamager();
				if (!p.isOp()) {
					e.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void onCropBreakingByEntity(PlayerInteractEvent e) {
		if (e.getAction() == Action.PHYSICAL && e.getClickedBlock().getType() == Material.FARMLAND) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerBreakTokenBlock(BlockBreakEvent e) {
		/*
		 * Token Block 부쉈을때 토큰 지급후 블럭 재등장
		 */
		Block b = e.getBlock();
		Location loc = b.getLocation();
		RoomType roomType = RoomLocation.getRoomTypeWithLocation(loc);
		int bonusToken = 0;
		if (roomType == RoomType.펀) {
			if (b.getType() == Material.IRON_BLOCK) {
				bonusToken = 1;
			} else if (b.getType() == Material.GOLD_BLOCK) {
				bonusToken = 2;
			} else if (b.getType() == Material.DIAMOND_BLOCK) {
				bonusToken = 10;
			} else if (b.getType() == Material.EMERALD_BLOCK) {
				bonusToken = 50;
			} else {
				return;
			}

			Player p = e.getPlayer();
			PlayerData pData = this.pDataManager.getPlayerData(p.getUniqueId());
			pData.plusToken(bonusToken);
			BroadcastTool.sendMessage(p, "보너스 토큰: " + bonusToken);

			// 블럭 없에기
			b.setType(Material.AIR);

			// 확률 조정해서 블럭 다시 생기기
			double r = Math.random();
			Material block = Material.IRON_BLOCK;

			if (r < 0.7) {
				block = Material.IRON_BLOCK;
			} else if (r < 0.85) {
				block = Material.GOLD_BLOCK;
			} else if (r < 0.97) {
				block = Material.DIAMOND_BLOCK;
			} else if (r < 1) {
				block = Material.EMERALD_BLOCK;
			}

			b.setType(block);
		}
	}

	@EventHandler
	public void setServerMOTD(ServerListPingEvent e) {
		e.setMaxPlayers(20);

		String motd = "              " + ChatColorTool.random() + ChatColor.BOLD + "릴레이";
		motd += "" + ChatColorTool.random() + ChatColor.BOLD + " 이스케이프 ";
		motd += ChatColor.WHITE + "[";
		motd += ChatColorTool.random() + "1.16.4 - 1.16.5";
		motd += ChatColor.WHITE + "]\n";
		motd += ChatColor.WHITE + "                    현재: ";
		motd += "" + ChatColor.WHITE + ChatColor.BOLD + this.relayManager.getCurrentTime().name();
		e.setMotd(motd);
	}

	@EventHandler
	public void onBlockIgnite(BlockIgniteEvent event) {
		if (event.getCause() == IgniteCause.SPREAD) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerClickTPSign(PlayerInteractEvent e) {
		/*
		 * [TP]
		 * 
		 * <title>
		 * 
		 * TOKEN <amount>
		 * 
		 * <x> <y> <z>
		 */
		Player p = e.getPlayer();
		Block b = e.getClickedBlock();
		PlayerData pData = this.pDataManager.getPlayerData(p.getUniqueId());

		// 터치한 블럭 없을때 return
		if (b != null) {
			// sign click
			if (b.getType() == Material.OAK_WALL_SIGN
					|| b.getType() == Material.OAK_SIGN) {
				Action act = e.getAction();
				if (act == Action.RIGHT_CLICK_BLOCK) {
					Sign sign = (Sign) b.getState();
					String[] lines = sign.getLines();

					// TP sign click
					if (lines[0].equals("[텔레포트]")) {
						if (pData.isPlayingMiniGame()) {
							BroadcastTool.sendMessage(p, "미니게임 중에는 텔레포트가 불가능합니다");
							return;
						}

						String locTitle = lines[1];
						String tokenStr = lines[2].split(" ")[1];
						// 띄어쓰기 기분으로 2번째를 token number로 봄
						int cost = Integer.parseInt(tokenStr);

						// 좌표
						String[] locs = lines[3].split(" ");
						double x = Double.parseDouble(locs[0]);
						double y = Double.parseDouble(locs[1]);
						double z = Double.parseDouble(locs[2]);

						if (pData.minusToken(cost)) {
//	    Location targetLoc = TPManager.getLocation(locTitle);
//		    TeleportTool.tp(p, targetLoc);
							TeleportTool.tp(p, x, y, z);
							BroadcastTool.sendMessage(p,  locTitle + "로 TP했습니다");
						} else {
							BroadcastTool.sendMessage(p, "토큰이 부족합니다");
						}
					}
				}
			}
		}

	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerSneaking(PlayerToggleSneakEvent e) {
		Player p = e.getPlayer();
		PlayerData pData = this.pDataManager.getPlayerData(p.getUniqueId());
		if (pData.isPlayingMiniGame()) {
			this.miniGameManager.processEvent(e);
		}

//	Boolean isSneaking = player.isSneaking();
//	System.out.println("SNEAKING: " + isSneaking);
//	if (isSneaking) {
//	    e.getPlayer().sendMessage("YES SNEAK");
//	} else {
//	    e.getPlayer().sendMessage("NO SNEAK");
//	}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onOPBreakBlock(BlockBreakEvent e) {
		// 허용
		Player p = e.getPlayer();
		if (p.isOp()) {
			e.setCancelled(false);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerPlaceBannedItem(BlockPlaceEvent e) {
		Player p = e.getPlayer();
		// banItem인지 확인!!!!!!!!!!
		// 놓인 block 체크
		Block block = e.getBlock();
		Material blockMat = block.getType();
		// mainhand 체크
		ItemStack item = p.getInventory().getItemInMainHand();

		// this.banItems.containsItem(blockMat) ||
		if (this.banItems.containsItem(item) || blockMat.equals(Material.CHEST)) {
			BroadcastTool.sendMessage(p, "밴 아이템입니다");
			e.setCancelled(true);
		}

		// OP이면 허용
		if (p.isOp()) {
			e.setCancelled(false);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerEmptyBucket(PlayerBucketEmptyEvent e) {
		Player p = e.getPlayer();
		UUID uuid = p.getUniqueId();
		PlayerData pData = this.pDataManager.getPlayerData(uuid);
		Role role = pData.getRole();

		// Role별로 권한 체크
		if (role == Role.메이커) {
			Material mat = e.getBucket();
			if (this.banItems.containsItem(mat)) {
				e.setCancelled(true);
			}
		}

		// OP허용
		if (p.isOp()) {
			e.setCancelled(false);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerFillBucket(PlayerBucketFillEvent e) {
		Player p = e.getPlayer();

		e.setCancelled(true);

		// OP허용
		if (p.isOp()) {
			e.setCancelled(false);
		}
	}

	@EventHandler
	public void onPlayerHitByArrow(EntityDamageByEntityEvent e) {
//	System.out.println("ENTITY HITTTTTT");
//	
//	Entity damager = e.getDamager();
//	if(damager instanceof Arrow) {
//	    System.out.println("ENTITY ARROW");
//	    Arrow arrow = (Arrow) damager;
//	    Entity shooter = (Entity) arrow.getShooter();
//	    if(shooter instanceof Player) {
//		System.out.println("ENTITY PLLAYER HIT BY AROW");
//	    }
//	}
	}

	@EventHandler
	public void onBlockFade(BlockFadeEvent e) {
		Block b = e.getBlock();
		Material bType = b.getType();
		switch (bType) {
		case ICE:
		case PACKED_ICE:
		case FROSTED_ICE:
		case SNOW:
		case SNOW_BLOCK:
			e.setCancelled(true);
			break;
		default:
			break;
		}
	}
	
	@EventHandler
	public void onViewerMoveFarAway(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		PlayerData pData = this.pDataManager.getPlayerData(p.getUniqueId());
		if(pData.getRole() == Role.뷰어) {
			if(!LocationTool.isIn(RoomLocation.MAIN_Pos1, p.getLocation(), RoomLocation.MAIN_Pos2)) {
				TeleportTool.tp(p,RoomLocation.MAIN_SPAWN);
			}
		}
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
//
//
//
//
//
