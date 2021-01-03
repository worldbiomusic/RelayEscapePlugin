package com.wbm.plugin.listener;

import org.bukkit.Bukkit;
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
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.inventory.ItemStack;

import com.wbm.plugin.Main;
import com.wbm.plugin.data.PlayerData;
import com.wbm.plugin.data.RoomLocation;
import com.wbm.plugin.util.PlayerDataManager;
import com.wbm.plugin.util.RelayManager;
import com.wbm.plugin.util.enums.MiniGameType;
import com.wbm.plugin.util.enums.RelayTime;
import com.wbm.plugin.util.enums.Role;
import com.wbm.plugin.util.enums.RoomType;
import com.wbm.plugin.util.general.BanItemTool;
import com.wbm.plugin.util.general.BroadcastTool;
import com.wbm.plugin.util.general.ChatColorTool;
import com.wbm.plugin.util.general.NPCManager;
import com.wbm.plugin.util.general.PlayerTool;
import com.wbm.plugin.util.general.SpawnLocationTool;
import com.wbm.plugin.util.general.TPManager;
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

    public CommonListener(PlayerDataManager pDataManager, ShopManager shopManager, BanItemTool banItems, NPCManager npc,
	    SkinManager skinManager, MiniGameManager miniGameManager, RelayManager relayManager) {
	this.pDataManager = pDataManager;
	this.shopManager = shopManager;
	this.banItems = banItems;
	this.npc = npc;
	this.skinManager = skinManager;
	this.miniGameManager = miniGameManager;
	this.relayManager = relayManager;
    }

    @EventHandler
    public static void onFoodLevelChanged(FoodLevelChangeEvent e) {
	e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
	PlayerTool.playSoundToEveryone(Sound.BLOCK_END_PORTAL_FRAME_FILL);

//	Player p = e.getPlayer();
//
//	// chat 쿨다운 관리
//	if (CoolDownManager.addPlayer(Setting.CoolDown_Subject_CHAT, p)) {
//	    String msg = e.getMessage();
//
//	    String translatedMsg = "";
//	    switch (msg) {
//	    case "1":
//		translatedMsg = "HI";
//		break;
//	    case "2":
//		translatedMsg = "BYE";
//		break;
//	    case "3":
//		translatedMsg = "FUXX";
//		break;
//	    case "4":
//		translatedMsg = "FOLLOW ME";
//		break;
//	    case "5":
//		translatedMsg = "VOTE";
//		break;
//	    case "6":
//		translatedMsg = "PASS";
//		break;
//	    default:
//		e.setCancelled(true);
//		return;
//
//	    }
//
//	    e.setMessage(translatedMsg);
//	} else {
//	    BroadcastTool.sendMessage(p, "too fast chat");
//	    e.setCancelled(true);
//	}

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
		BroadcastTool.sendMessage(p, ChatColor.RED + "you can only use \"/re\"");
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
	if(e.getCause() == DamageCause.FALL) {
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
	if (role == Role.WAITER) {
	    respawnLoc = SpawnLocationTool.LOBBY;
	} else {
	    respawnLoc = SpawnLocationTool.RESPAWN;
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
	    if (b.getType() == Material.WALL_SIGN || b.getType() == Material.SIGN_POST
		    || b.getType() == Material.SIGN) {
		Action act = e.getAction();
		if (act == Action.RIGHT_CLICK_BLOCK) {
		    Sign sign = (Sign) b.getState();
		    String[] lines = sign.getLines();

		    // SHOP click
		    if (lines[0].equals("[SHOP]")) {
			String goods = lines[1];
			String[] tokenStr = lines[2].split(" ");
			// 띄어쓰기 기분으로 2번째를 token number로 봄
			int cost = Integer.parseInt(tokenStr[1]);
			this.shopManager.purchase(p, goods, cost);
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
	String invTitle = e.getInventory().getTitle();
	BroadcastTool.debug("invTitle: " + invTitle);

	Role role = this.pDataManager.getPlayerData(p.getUniqueId()).getRole();
	if (role == Role.MAKER || role == Role.VIEWER) {
	    if (!invTitle.equals(ShopGoods.CHEST.name())) {
		BroadcastTool.debug("only " + ShopGoods.CHEST.name() + " inventory is allowed");
		e.setCancelled(true);
	    }
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

	if (this.banItems.containsItem(blockMat) || this.banItems.containsItem(item)
		|| blockMat.equals(Material.CHEST)) {
	    BroadcastTool.sendMessage(p, "banned item");
	    e.setCancelled(true);
	    return;
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
	if (npc.getNPCs() == null || npc.getNPCs().isEmpty())
	    return;
	npc.sendAllNPCPacketToPlayer(p);
    }

    @EventHandler
    public void onPlayerMoveOnTheEventBlock(PlayerMoveEvent e) {
	// event block 리스너
	EventBlockListener.processEventBlockEvent(e);
    }

    @EventHandler
    public void onPlayerJoinMiniGame(PlayerInteractEvent e) {
	Player p = e.getPlayer();
	Block b = e.getClickedBlock();

	if (b != null) {
	    if (b.getType() == Material.SIGN || b.getType() == Material.SIGN_POST
		    || b.getType() == Material.WALL_SIGN) {
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.LEFT_CLICK_BLOCK) {
		    // room, time, role 체크
		    if (this.relayManager.checkRoomAndRelayTimeAndRole(RoomType.MINI_GAME, RelayTime.MAKING,
			    Role.WAITER, p)
			    || this.relayManager.checkRoomAndRelayTimeAndRole(RoomType.MINI_GAME, RelayTime.TESTING,
				    Role.WAITER, p)) {
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
			if (minigame.equalsIgnoreCase("[MINI_GAME]")) {
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
	if (e.getAction() == Action.PHYSICAL && e.getClickedBlock().getType() == Material.SOIL) {
	    e.setCancelled(true);
	}
    }

    @EventHandler
    public void onPlayerBreakTokenBlock(BlockBreakEvent e) {
	/*
	 * Token Block 부쉈을때 토큰 지급후 블럭 1분후 등장
	 */
	Block b = e.getBlock();
	Location loc = b.getLocation();
	RoomType roomType = RoomLocation.getRoomTypeWithLocation(loc);
	int bonusToken = 0;
	if (roomType == RoomType.FUN) {
	    if (b.getType() == Material.IRON_BLOCK) {
		bonusToken = 1;
	    } else if (b.getType() == Material.GOLD_BLOCK) {
		bonusToken = 2;
	    } else if (b.getType() == Material.DIAMOND_BLOCK) {
		bonusToken = 3;
	    } else if (b.getType() == Material.EMERALD_BLOCK) {
		bonusToken = 4;
	    } else {
		return;
	    }

	    Player p = e.getPlayer();
	    PlayerData pData = this.pDataManager.getPlayerData(p.getUniqueId());
	    pData.plusToken(bonusToken);
	    BroadcastTool.sendMessage(p, "Bonus token: " + bonusToken);

	    // 블럭 없에기
	    b.setType(Material.AIR);

	    // 일정시간후 블럭 다시 나타나기
	    Bukkit.getScheduler().runTaskLater(Main.getInstance(), new Runnable() {

		@Override
		public void run() {
		    double r = Math.random();
		    Material block = Material.IRON_BLOCK;

		    if (r < 0.5) {
			block = Material.IRON_BLOCK;
		    } else if (r < 0.8) {
			block = Material.GOLD_BLOCK;
		    } else if (r < 0.95) {
			block = Material.DIAMOND_BLOCK;
		    } else if (r < 1) {
			block = Material.EMERALD_BLOCK;
		    }

		    b.setType(block);
		}
	    }, 20 * 60 * 1);
	}
    }

    @EventHandler
    public void setServerMOTD(ServerListPingEvent e) {
	e.setMaxPlayers(20);

	String motd = "" + ChatColorTool.random() + ChatColor.BOLD + "Relay";
	motd += "" + ChatColorTool.random() + ChatColor.BOLD + " Escape ";
	motd += ChatColor.WHITE + "[";
	motd += ChatColorTool.random() + "1.12.2";
	motd += ChatColor.WHITE + " - ";
	motd += ChatColorTool.random() + "1.16.4";
	motd += ChatColor.WHITE + "]";
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
	 * <tp title>
	 * 
	 * TOKEN <amount>
	 */
	Player p = e.getPlayer();
	Block b = e.getClickedBlock();
	PlayerData pData = this.pDataManager.getPlayerData(p.getUniqueId());

	// 터치한 블럭 없을때 return
	if (b != null) {
	    // sign click
	    if (b.getType() == Material.WALL_SIGN || b.getType() == Material.SIGN_POST
		    || b.getType() == Material.SIGN) {
		Action act = e.getAction();
		if (act == Action.RIGHT_CLICK_BLOCK) {
		    Sign sign = (Sign) b.getState();
		    String[] lines = sign.getLines();

		    // TP sign click
		    if (lines[0].equals("[TP]")) {
			String locTitle = lines[1];
			String[] tokenStr = lines[2].split(" ");
			// 띄어쓰기 기분으로 2번째를 token number로 봄
			int cost = Integer.parseInt(tokenStr[1]);
			if (pData.minusToken(cost)) {
			    Location targetLoc = TPManager.getLocation(locTitle);
			    TeleportTool.tp(p, targetLoc);
			    BroadcastTool.sendMessage(p, "teleport to " + locTitle);
			} else {
			    BroadcastTool.sendMessage(p, "you need more token");
			}
		    }
		}
	    }
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
