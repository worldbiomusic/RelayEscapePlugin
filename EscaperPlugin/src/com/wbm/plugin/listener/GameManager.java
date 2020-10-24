package com.wbm.plugin.listener;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import com.wbm.plugin.data.PlayerData;
import com.wbm.plugin.data.Room;
import com.wbm.plugin.data.RoomLocation;
import com.wbm.plugin.util.PlayerDataManager;
import com.wbm.plugin.util.RelayManager;
import com.wbm.plugin.util.RolePermission;
import com.wbm.plugin.util.RoomManager;
import com.wbm.plugin.util.enums.RelayTime;
import com.wbm.plugin.util.enums.Role;
import com.wbm.plugin.util.enums.RoomType;
import com.wbm.plugin.util.general.BanItemTool;
import com.wbm.plugin.util.general.BroadcastTool;
import com.wbm.plugin.util.general.RespawnManager;

public class GameManager implements Listener
{
	PlayerDataManager pDataManager;
	RoomManager roomManager;
	RelayManager relayManager;
	BanItemTool banItems;

	public GameManager(PlayerDataManager pDataManager, RoomManager roomManager, RelayManager relayManager,
			BanItemTool banItems)
	{
		this.pDataManager=pDataManager;
		this.roomManager=roomManager;
		this.relayManager=relayManager;
		this.banItems=banItems;

		// init
		this.init();
	}
	// 55KB = 55000Byte = 10 * 10 47 = 4700

	void init()
	{
		// 1.서버 리로드하면 서버에 남아있는 플레이어들 다시 등록
		this.reRegisterAllPlayer();

		// 2.resetRelay
		this.relayManager.resetRelay();
	}

	void processPlayerData(Player p)
	{
		// data 처리
		UUID uuid=p.getUniqueId();

		// 모든 player는 무조건 Challenger or Waiter이고, 각 Time에 맞는 Challenger의 Role로 역할이 배정됨!
		// (w, m, t = Waiter, c = Challenger)
		// (Challeging때 나간 Maker가 다시 들어온 경우 Viewer로)
		PlayerData pData;

		// RelayTime = WAITING or MAKING or TESTING
		Role baseRole=Role.WAITER;
		RelayTime time=this.relayManager.getCurrentTime();

		if(time==RelayTime.CHALLENGING)
		{
			baseRole=Role.CHALLENGER;
		}

		// TODO: config연동됬을때 활성화 시킬 코드
		// PlayerDataManager에 데이터 없는지 확인 (= 서버 처음 들어옴)
		if(this.pDataManager.isFirstJoin(uuid))
		{
			String name=p.getName();
			pData=new PlayerData(uuid, name, baseRole);
		}
		// 전에 들어왔음 (바꿀것은 Role밖에 없음)
		else
		{
			pData=this.pDataManager.getSavedPlayerData(uuid);

			// maker가 남아있는경우는 Maker가 ChallengingTime일떄 나간경우임!
			// -> role을 유지해서 viewer로 겜모를바꿔서 자신이 만든룸을 clear못하게 만들어야 함
			if(this.pDataManager.doesMakerExist())
			{
//				Player maker=this.pDataManager.getMaker();
				// 들어온사람이 전에 나간 Maker였을때
				if(this.pDataManager.isMaker(p))
				{
					p.sendMessage("you are Viewer in your room(structure)");
					baseRole=Role.VIEWER;
				}
			}

			// role변경
			pData.setRole(baseRole);
		}
		// TODO: config연동됬을때 활성화 시킬 코드

		// TODO: config연동 안했을때 사용코드
//		if(time == RelayTime.CHALLENGING) {
//			// maker가 남아있는경우는 Maker가 ChallengingTime일떄 나간경우임!
//			// -> role을 유지해서 viewer로 겜모를바꿔서 자신이 만든룸을 clear못하게 만들어야 함
//			if(this.pDataManager.makerExists()) {
//				// 들어온사람이 전에 나간 Maker였을때
//				Player maker = this.pDataManager.getMaker();
//				if(uuid.equals(maker.getUniqueId())) {
//					p.sendMessage("you are Viewer in your room(structure)");
//					// 바꿀 목표 데이터
//					baseRole = Role.VIEWER;
//				}
//			}
//		}
		// TODO: config연동 안했을때 사용코드

		// playerDataManager에 데이터 add
		this.pDataManager.addPlayerData(pData);
		// gamemode 처리
		this.pDataManager.setPlayerGameModeWithRole(uuid);
	}

	public void reRegisterAllPlayer()
	{
		for(Player p : Bukkit.getOnlinePlayers())
		{
			this.processPlayerData(p);
		}
	}

	@EventHandler
	public void onBlockBreakEvent(BlockBreakEvent e)
	{
		// 모든 break event는 여기를 거쳐서 처리됨
		Player p=e.getPlayer();
		p.sendMessage("break");
		Block b=e.getBlock();

		// 일단 cancel
		e.setCancelled(true);

		// Main Room 체크
		if(RoomLocation.getRoomTypeWithLocation(b.getLocation())==RoomType.MAIN)
		{
			p.sendMessage("break: Main");
			this.onTesterAndChallengerBreakCore(e);
			this.onMakerBreakCore(e);
			this.onPlayerBreakBlock(e);
		}

	}

	@EventHandler
	public void onBlockPlaceEvent(BlockPlaceEvent e)
	{
		// 모든 place event는 여기를 거쳐서 처리됨
		Player p=e.getPlayer();
		p.sendMessage("place");
		Block b=e.getBlock();

		// 일단 cancel
		e.setCancelled(true);

		// Main Room 체크
		if(RoomLocation.getRoomTypeWithLocation(b.getLocation())==RoomType.MAIN)
		{
			p.sendMessage("place: Main");
			this.onPlayerPlaceBlock(e);
			this.onMakerPlaceCore(e);
		}
	}

	@EventHandler
	public void onPlayerInteractWithItem(PlayerInteractEvent e)
	{
		Player p=e.getPlayer();

		if(RoomLocation.getRoomTypeWithLocation(p.getLocation())==RoomType.MAIN)
		{
			if(e.getItem()!=null)
			{
				Material mat = e.getItem().getType(); 
				if(mat==Material.STICK)
				{
					if(e.getAction()==Action.RIGHT_CLICK_AIR||e.getAction()==Action.RIGHT_CLICK_BLOCK)
					{
						Location loc=p.getLocation();
						p.getWorld().getBlockAt(loc).setType(Material.STONE);
					}
				} else if(mat == Material.WOOD_SWORD) {
					if(e.getAction()==Action.RIGHT_CLICK_AIR||e.getAction()==Action.RIGHT_CLICK_BLOCK)
					{
						p.teleport(RespawnManager.respawnLocation);
					}
				} else if(mat == Material.WATCH) {
					if(e.getAction()==Action.LEFT_CLICK_AIR||e.getAction()==Action.LEFT_CLICK_BLOCK)
					{
						// sethome
						// TODO: 구현하기
						
					} else if(e.getAction()==Action.RIGHT_CLICK_AIR||e.getAction()==Action.RIGHT_CLICK_BLOCK)
					{
						// gohome
						// TODO: 구현하기
					}
				}
			}
		}
	}

//	@EventHandler
	public void onTesterAndChallengerBreakCore(BlockBreakEvent e)
	{
		// Tester, Challenger의 core부수는 상황
		Block block=e.getBlock();
		Material mat=block.getType();

		Player p=e.getPlayer();
		UUID pUuid=p.getUniqueId();
		PlayerData pData=this.pDataManager.getOnlinePlayerData(pUuid);
		Role role=pData.getRole();

		// core체크
		if(mat.equals(Material.GLOWSTONE))
		{
			// Role별로 권한 체크
			// Time: Challenging / Role: Challenger
			if(role==Role.CHALLENGER)
			{
				// resetRelaySettings
				this.relayManager.resetRelaySetting();

				// 1. 현재 Maker에게 이제 Challenger라는 메세지 전송
				BroadcastTool.sendMessageToEveryone("core is broken!!!!!!!!!!!!");

				// 2. 클리어한 maker는 pDataManager의 maker로 등록
				this.pDataManager.registerMaker(p);

				// 3. main room clearCount +1, time측정 후 초기화
				this.roomManager.getRoom(RoomType.MAIN).addClearCount(1);
				this.roomManager.recordMainRoomDurationTime();
				this.roomManager.setRoomEmpty(RoomType.MAIN);

				// 4.next relay 시작
				this.relayManager.startNextTime();

				// 5.player token +1, clearCount +1
				pData.addToken(1);
				pData.addClearCount(1);
			}
			// Time: Testing / Role: Tester
			else if(role==Role.TESTER)
			{
				// 1.save room, set main room
				String title=this.roomManager.saveRoomData(RoomType.MAIN, p.getName());
				Room mainRoom=this.roomManager.getRoomData(title);
				this.roomManager.setRoom(RoomType.MAIN, mainRoom);
				mainRoom.addChallengingCount(1);

				// 2.next relay 시작
				this.relayManager.startNextTime();
			}
		}

	}

	// MakekingTime에서 Maker가 core를 설치했는지 확인 (최대 1개만 설치 가능)
	// priority HIGH 로 높여서 마지막에 검사하게
//	@EventHandler(priority=EventPriority.HIGH)
	public void onMakerPlaceCore(BlockPlaceEvent e)
	{
		Block core=e.getBlock();
		if(core.getType()==Material.GLOWSTONE)
		{
			RelayTime time=this.relayManager.getCurrentTime();
			if(time==RelayTime.MAKING)
			{
				Player p=e.getPlayer();
				if(this.pDataManager.isMaker(p))
				{
					// 이미 설치되어 있을때
					if(this.relayManager.isCorePlaced())
					{
						BroadcastTool.sendMessage(p, "core is already placed");
						e.setCancelled(true);
					}
					else
					{
						// 설치 x 있을때
						BroadcastTool.sendMessage(p, "core is placed (max: 1)");
						this.relayManager.setCorePlaced(true);
					}
				}
			}
		}
	}

//	@EventHandler
	public void onMakerBreakCore(BlockBreakEvent e)
	{
		Block core=e.getBlock();
		if(core.getType()==Material.GLOWSTONE)
		{
			RelayTime time=this.relayManager.getCurrentTime();
			if(time==RelayTime.MAKING)
			{
				Player p=e.getPlayer();
				if(this.pDataManager.isMaker(p))
				{
					BroadcastTool.sendMessage(p, "core is broken");
					this.relayManager.setCorePlaced(false);
				}
			}
		}
	}

//	@EventHandler
	public void onPlayerBreakBlock(BlockBreakEvent e)
	{
		Player p=e.getPlayer();
		UUID uuid=p.getUniqueId();
		PlayerData pData=this.pDataManager.getOnlinePlayerData(uuid);
		Role role=pData.getRole();

		boolean permission=false;

		// Role별로 권한 체크
		if(role==Role.MAKER)
		{
			permission=RolePermission.MAKER_BREAKBLOCK;
		}
		else if(role==Role.CHALLENGER)
		{
			permission=RolePermission.CHALLENGER_BREAKBLOCK;
		}
		else if(role==Role.TESTER)
		{
			permission=RolePermission.TESTER_BREAKBLOCK;
		}
		else if(role==Role.VIEWER)
		{
			permission=RolePermission.VIEWER_BREAKBLOCK;
		}
		else if(role==Role.WAITER)
		{
			permission=RolePermission.WAITER_BREAKBLOCK;
		}

		e.setCancelled(!permission);
	}

//	@EventHandler
	public void onPlayerPlaceBlock(BlockPlaceEvent e)
	{
		Player p=e.getPlayer();
		UUID uuid=p.getUniqueId();
		PlayerData pData=this.pDataManager.getOnlinePlayerData(uuid);
		Role role=pData.getRole();

		boolean permission=false;

		// Role별로 권한 체크
		if(role==Role.MAKER)
		{
			permission=RolePermission.MAKER_PLACEBLOCK;

			// banItem인지 확인

			// 놓인 block 체크
			Block block=e.getBlock();
			Material blockMat=block.getType();
			// mainhand 체크
			ItemStack item=p.getInventory().getItemInMainHand();

			if(this.banItems.containsItem(blockMat)||this.banItems.containsItem(item))
			{
				permission=false;
			}

		}
		else if(role==Role.CHALLENGER)
		{
			permission=RolePermission.CHALLENGER_PLACEBLOCK;
		}
		else if(role==Role.TESTER)
		{
			permission=RolePermission.TESTER_PLACEBLOCK;
		}
		else if(role==Role.VIEWER)
		{
			permission=RolePermission.VIEWER_PLACEBLOCK;
		}
		else if(role==Role.WAITER)
		{
			permission=RolePermission.WAITER_PLACEBLOCK;
		}

		e.setCancelled(!permission);
	}

	@EventHandler
	public void onPlayerPlaceBucket(PlayerBucketEmptyEvent e)
	{
		Player p=e.getPlayer();
		UUID uuid=p.getUniqueId();
		PlayerData pData=this.pDataManager.getOnlinePlayerData(uuid);
		Role role=pData.getRole();

		// Role별로 권한 체크
		if(role==Role.MAKER)
		{
			Material mat=e.getBucket();
			if(this.banItems.containsItem(mat))
			{
				e.setCancelled(true);
			}
		}
		p.sendMessage("sdfsdfsdd");

	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e)
	{
		Player p=e.getPlayer();
		p.sendMessage("welcome to RelayEscape server!");

		// PlayerData 처리
		this.processPlayerData(p);
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e)
	{
		Player p=e.getPlayer();

		if(this.pDataManager.doesMakerExist())
		{
			// Maker가 나갔을 때
			if(this.pDataManager.isMaker(p))
			{
				RelayTime time=this.relayManager.getCurrentTime();

				// Time = WAITING, MAKING, TESTING일떄
				if(time==RelayTime.WAITING||time==RelayTime.MAKING||time==RelayTime.TESTING)
				{
					// msg보내기
					BroadcastTool.sendMessageToEveryone("Maker quit server");

					// reset relay
					this.relayManager.resetRelay();
				}

				// Time = CHALLENGING 일때
				// -> 재접해서 다시 클리어 방지!
				else if(time==RelayTime.CHALLENGING)
				{
					// PlayerDataManager maker = null 처리하지 않고, 다시 들어올때 maker에 있는 player로 maker판별!
					// -> 수행할 동작이 없음
				}
			}
		}

		// PlayerDataManager 처리
		this.pDataManager.saveAndRemovePlayerData(p.getUniqueId());
	}
}
