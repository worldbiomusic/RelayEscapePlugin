package com.wbm.plugin.listener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

import com.wbm.plugin.Main;
import com.wbm.plugin.util.MiniGameManager;
import com.wbm.plugin.util.PlayerDataManager;
import com.wbm.plugin.util.RelayManager;
import com.wbm.plugin.util.enums.MiniGame;
import com.wbm.plugin.util.enums.RelayTime;
import com.wbm.plugin.util.enums.Role;
import com.wbm.plugin.util.enums.RoomType;
import com.wbm.plugin.util.general.BanItemTool;
import com.wbm.plugin.util.general.BroadcastTool;
import com.wbm.plugin.util.general.ItemStackTool;
import com.wbm.plugin.util.general.NPCManager;
import com.wbm.plugin.util.general.PotionEffectTool;
import com.wbm.plugin.util.general.SoundTool;
import com.wbm.plugin.util.general.SpawnLocationTool;
import com.wbm.plugin.util.general.TeleportTool;
import com.wbm.plugin.util.general.shop.ShopGoods;
import com.wbm.plugin.util.general.shop.ShopManager;
import com.wbm.plugin.util.general.skin.SkinManager;

public class CommonListener implements Listener
{
	/*
	 * class 설명:
	 * 어디서나 언제나 해당되는 리스너
	 */
	PlayerDataManager pDataManager;
	ShopManager shopManager;
	BanItemTool banItems;
	NPCManager npc;
	SkinManager skinManager;
	MiniGameManager miniGameManager;
	RelayManager relayManager;
	
	public CommonListener(PlayerDataManager pDataManager,
			ShopManager shopManager,
			BanItemTool banItems,
			NPCManager npc,
			SkinManager skinManager,
			MiniGameManager miniGameManager,
			RelayManager relayManager)
	{
		this.pDataManager = pDataManager;
		this.shopManager = shopManager;
		this.banItems = banItems;
		this.npc = npc;
		this.skinManager = skinManager;
		this.miniGameManager = miniGameManager;
		this.relayManager = relayManager;
	}

	@EventHandler
	public static void onFoodLevelChanged(FoodLevelChangeEvent e)
	{
		e.setCancelled(true);
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e)
	{
		e.getDrops().clear();
	}

	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent e)
	{

		String msg=e.getMessage();

		String translatedMsg="";
		switch (msg)
		{
			case "1":
				translatedMsg="HI";
				break;
			case "2":
				translatedMsg="BYE";
				break;
			case "3":
				translatedMsg="FUXX";
				break;
			case "4":
				translatedMsg="FOLLOW ME";
				break;
			case "5":
				translatedMsg="VOTE";
				break;
			default:
				e.setCancelled(true);
				return;

		}

		e.setMessage(translatedMsg);
	}
	
	@EventHandler
	public void onPlayerUseCommand(PlayerCommandPreprocessEvent e)
	{
		Player p = e.getPlayer();
		// check command
		String msg = e.getMessage();
		
//		// LLLJH제외
//		if(p.getName().equals("LLLJH")) {
//			return;
//		}
		if(p.isOp()) {
			return;
		}
		
		// re명령어 빼고 모두 막기 (일반유저)
		if(msg.startsWith("/")) {
			if(!msg.startsWith("/re")) {
				BroadcastTool.sendMessage(p, ChatColor.RED + "you can only use \"/re\"");
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onPlayerDamanged(EntityDamageByEntityEvent e) {
		if(e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
			// 나중에 Maker가 Waiter기다리는것 때리게  만들 예정
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent e) {
		e.setRespawnLocation(SpawnLocationTool.respawnLocation);
	}
	
	@EventHandler
	public void onPlayerUseShop(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		Block b = e.getClickedBlock();
		
		// 터치한 블럭 없을때 return
		if(b == null) {
			return;
		}
		
		// sign click
		if(b.getType() == Material.WALL_SIGN || 
				b.getType() == Material.SIGN_POST||
				b.getType() == Material.SIGN) {
			Action act = e.getAction();
			if(act == Action.RIGHT_CLICK_BLOCK || act == Action.RIGHT_CLICK_AIR) {
				Sign sign = (Sign)b.getState();
				String[] lines = sign.getLines();
				
				// SHOP click
				if(lines[0].equals("[SHOP]")) {
					String goods = lines[1];
					String[] tokenStr = lines[2].split(" ");
					// 띄어쓰기 기분으로 2번째를 token number로 봄
					int cost = Integer.parseInt(tokenStr[1]);
					this.shopManager.purchase(p, goods, cost);
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerConsumeItem(PlayerItemConsumeEvent e) {
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent e) {
		e.setCancelled(true);
	}
	
//	@EventHandler
//	public void onPlayerOpenIventoryWhenMaker(InventoryOpenEvent e) {
//		Player p = (Player) e.getPlayer();
//		PlayerData pData = this.pDataManager.getOnlinePlayerData(p.getUniqueId());
//		if(pData.getRole() == Role.MAKER) {
//			BroadcastTool.sendMessage(p, "cannot open inven when Maker");
//			e.setCancelled(true);
//		}
//	}
	
	@EventHandler
	public void onPlayerOpenIventoryWhenMaker(InventoryCreativeEvent e) {
		Player p = (Player) e.getWhoClicked();
		String invTitle= e.getInventory().getTitle();
		p.sendMessage("invTitle: " +invTitle);
		
		Role role = this.pDataManager.getPlayerData(p.getUniqueId()).getRole();
		if(role == Role.MAKER) {
			if(!invTitle.equals(ShopGoods.BLOCKS.name())) {
				p.sendMessage("only BLOCKS inv is OK");
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void onPlayerPlaceBannedItem(BlockPlaceEvent e) {
		Player p =e.getPlayer();
		// banItem인지 확인!!!!!!!!!!
		// 놓인 block 체크
		Block block=e.getBlock();
		Material blockMat=block.getType();
		// mainhand 체크
		ItemStack item=p.getInventory().getItemInMainHand();

		if(this.banItems.containsItem(blockMat)
				||this.banItems.containsItem(item)
				|| blockMat.equals(Material.CHEST))
		{
			BroadcastTool.sendMessage(p, "banned item");
			e.setCancelled(true);
			return;
		}
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		
		// skin data 다운
		String pName = p.getName();
		if(! this.skinManager.doesExist(pName)) {
			this.skinManager.addPlayerSkinData(pName);
			BroadcastTool.debug("add skin: " + pName);
		}
		
		// NPC packet
		if(npc.getNPCs() == null || npc.getNPCs().isEmpty())
			return;
		npc.sendAllNPCPacketToPlayer(p);
	}
	
	@EventHandler
	public void onPlayerInteractingWithEventBlock(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		Block b = p.getLocation().subtract(0, 1, 0).getBlock();
		
		if(ItemStackTool.isSameWithMaterialNData(ItemStackTool.block2ItemStack(b), ShopGoods.JUMPING.getGoods())) {
//			p.sendMessage("JUMPING");
			p.setVelocity(new Vector(0, 0.5, 0));
		}
		else if(ItemStackTool.isSameWithMaterialNData(ItemStackTool.block2ItemStack(b), ShopGoods.RESPAWN.getGoods())) {
//			p.sendMessage("RESPAWN");
			TeleportTool.tp(p, SpawnLocationTool.respawnLocation);
		}
		else if(ItemStackTool.isSameWithMaterialNData(ItemStackTool.block2ItemStack(b), ShopGoods.TRAP.getGoods())) {
//			p.sendMessage("TRAP");
			
			if(p.getActivePotionEffects().size() >= 1) {
				return;
			}
			
			PotionEffect potion = PotionEffectTool.getRandomDebuffPotionEffect();
			p.addPotionEffect(potion);
		}
		else if(ItemStackTool.isSameWithMaterialNData(ItemStackTool.block2ItemStack(b), ShopGoods.FLICKING.getGoods())) {
//			p.sendMessage("FLICKING");
			
			Material mat = b.getType();
			@SuppressWarnings("deprecation")
			byte data = b.getData();
			
			// 3초후 사라짐
			Bukkit.getScheduler().runTaskLater(Main.getInstance(), new Runnable()
			{
				
				@Override
				public void run()
				{
					b.setType(Material.AIR);
				}
			}, 20 * 3);
			
			// 6초후 나타남
			Bukkit.getScheduler().runTaskLater(Main.getInstance(), new Runnable()
			{
				
				@SuppressWarnings("deprecation")
				@Override
				public void run()
				{
					b.setType(mat);
					b.setData(data);
				}
			}, 20 * 6);
		}
		else if(ItemStackTool.isSameWithMaterialNData(ItemStackTool.block2ItemStack(b), ShopGoods.SOUND_TERROR.getGoods())) {
//			p.sendMessage("RESPAWN");
			p.playSound(p.getLocation(), Sound.ENTITY_ZOMBIE_AMBIENT, 10, 1);
		}
		else if(ItemStackTool.isSameWithMaterialNData(ItemStackTool.block2ItemStack(b), ShopGoods.TERRORIST.getGoods())) {
//			p.sendMessage("RESPAWN");
			int r = (int)(Math.random() * 3);
			if(r==1) {
				TeleportTool.allTpToLocation(SpawnLocationTool.respawnLocation);
			} else if(r==2) {
				PotionEffect effect = PotionEffectTool.getRandomDebuffPotionEffect();
				PotionEffectTool.addPotionEffectToAll(effect);
			} else if(r==3) {
				SoundTool.playSound(p, Sound.ENTITY_ZOMBIE_AMBIENT);
			}
			
			// 일회성
			b.setType(Material.DIRT);
		}
		
	}
	
	@EventHandler
	public void onPlayerJoinMiniGame(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		Block b = e.getClickedBlock();
		
		if(b == null)
			return;
		
		if(b.getType() == Material.SIGN || 
				b.getType() == Material.SIGN_POST || 
				b.getType() == Material.WALL_SIGN) {
			if(e.getAction() == Action.RIGHT_CLICK_BLOCK || 
					e.getAction() == Action.LEFT_CLICK_BLOCK) {
				// room, time, role 체크
				if(this.relayManager.checkRoomAndRelayTimeAndRole(RoomType.MINI_GAME, RelayTime.MAKING, Role.WAITER, p) 
			|| this.relayManager.checkRoomAndRelayTimeAndRole(RoomType.MINI_GAME, RelayTime.TESTING, Role.WAITER, p)) {
					Sign sign = (Sign) b.getState();
					/*
					 * 0: [MINI_GAME]
					 * 1: <game title>
					 * 2: FEE <n> TOKEN
					 * 3: ---------
					 */
					String[] lines = sign.getLines();
					String minigame = lines[0];
					String title = lines[1];
					// string중 2번째것이 숫자이므로
					int fee = Integer.parseInt(lines[2].split(" ")[1]);
					
					// 1
					if(minigame.equalsIgnoreCase("[MINI_GAME]")) {
						this.miniGameManager.enterRoom(MiniGame.valueOf(title), fee, p);
					}
				}
			}
			
		}
	}
	
	@EventHandler
	public void onPlayerBreakMiniGameBlock(BlockBreakEvent e) {
		Player p = e.getPlayer();
		if(this.relayManager.checkRoomAndRelayTimeAndRole(RoomType.MINI_GAME, RelayTime.MAKING, Role.WAITER, p) 
				|| this.relayManager.checkRoomAndRelayTimeAndRole(RoomType.MINI_GAME, RelayTime.TESTING, Role.WAITER, p)) {
			this.miniGameManager.breakBlock(e);
		}
		
		
	}
}



























