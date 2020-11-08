package com.wbm.plugin.listener;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
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
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import com.wbm.plugin.util.PlayerDataManager;
import com.wbm.plugin.util.enums.Role;
import com.wbm.plugin.util.general.BanItemTool;
import com.wbm.plugin.util.general.BroadcastTool;
import com.wbm.plugin.util.general.SpawnLocationTool;
import com.wbm.plugin.util.general.shop.ShopGoods;
import com.wbm.plugin.util.general.shop.ShopManager;

public class CommonListener implements Listener
{
	/*
	 * class 설명:
	 * 어디서나 언제나 해당되는 리스너
	 */
	PlayerDataManager pDataManager;
	ShopManager shopManager;
	BanItemTool banItems;
	
	public CommonListener(PlayerDataManager pDataManager,
			ShopManager shopManager,
			BanItemTool banItems)
	{
		this.pDataManager = pDataManager;
		this.shopManager = shopManager;
		this.banItems = banItems;
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
		p.sendMessage("commmand");
		// check command
		String msg = e.getMessage();
		
		// LLLJH제외
		if(p.getName().equals("LLLJH")) {
			return;
		}
		
		// re명령어 빼고 모두 막기 (일반유저)
		if(msg.startsWith("/")) {
			if(!msg.startsWith("/re")) {
				BroadcastTool.sendMessage(p, ChatColor.RED + "you can only use \"re\"");
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
		
		// core부술때 바로 없어져서 nullpoiter에러 잡기용 if문
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
		
		Role role = this.pDataManager.getOnlinePlayerData(p.getUniqueId()).getRole();
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

		if(this.banItems.containsItem(blockMat)||this.banItems.containsItem(item))
		{
			BroadcastTool.sendMessage(p, "banned item");
			e.setCancelled(true);
			return;
		}
	}
}



























