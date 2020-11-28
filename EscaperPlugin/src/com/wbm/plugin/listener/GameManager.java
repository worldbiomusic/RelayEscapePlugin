package com.wbm.plugin.listener;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.wbm.plugin.data.PlayerData;
import com.wbm.plugin.data.Room;
import com.wbm.plugin.data.RoomLocation;
import com.wbm.plugin.util.MiniGameManager;
import com.wbm.plugin.util.PlayerDataManager;
import com.wbm.plugin.util.RelayManager;
import com.wbm.plugin.util.RoomManager;
import com.wbm.plugin.util.enums.RelayTime;
import com.wbm.plugin.util.enums.Role;
import com.wbm.plugin.util.enums.RoomType;
import com.wbm.plugin.util.general.BanItemTool;
import com.wbm.plugin.util.general.BroadcastTool;
import com.wbm.plugin.util.general.InventoryTool;
import com.wbm.plugin.util.general.PlayerTool;
import com.wbm.plugin.util.general.SpawnLocationTool;
import com.wbm.plugin.util.general.shop.ShopGoods;

public class GameManager implements Listener
{
	/*
	 * class 설명:
	 * 어디서인지(Room), 언제인지(RelayTime), 역할이 누구인지(Role)이 3단계를 거치고
	 * 실행되야 하는 리스너들(RelayManager에 검사 메소드 있음)
	 */
	PlayerDataManager pDataManager;
	RoomManager roomManager;
	RelayManager relayManager;
	BanItemTool banItems;
	MiniGameManager miniGameManager;

	public GameManager(PlayerDataManager pDataManager, 
			RoomManager roomManager, 
			RelayManager relayManager,
			MiniGameManager miniGameManager)
	{
		this.pDataManager=pDataManager;
		this.roomManager=roomManager;
		this.relayManager=relayManager;
		this.miniGameManager = miniGameManager;

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
			pData=this.pDataManager.getPlayerData(uuid);

			// maker가 남아있는경우는 Maker가 ChallengingTime일떄 나간경우임!
			// -> role을 유지해서 viewer로 겜모를바꿔서 자신이 만든룸을 clear못하게 만들어야 함
			if(this.pDataManager.doesMakerExist())
			{
//				Player maker=this.pDataManager.getMaker();
				// 들어온사람이 전에 나간 Maker였을때
				if(this.pDataManager.isMaker(p))
				{
					BroadcastTool.sendMessage(p, "you are Viewer in your room(structure)");
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
			TODOListWhenplayerJoinServer(p);
		}
	}
	
	public void TODOListWhenplayerJoinServer(Player p) {
		this.processPlayerData(p);
		p.teleport(SpawnLocationTool.joinLocation);
		InventoryTool.clearPlayerInv(p);
		// 기본 굿즈 제공
		this.giveBasicGoods(p);
	}
	
	void giveBasicGoods(Player p) {
		PlayerData pData = this.pDataManager.getPlayerData(p.getUniqueId());
		if(!pData.doesHaveGoods(ShopGoods.CHEST)) {
			pData.addGoods(ShopGoods.CHEST);
		}
	}

	@EventHandler
	public void onPlayerBreakBlock(BlockBreakEvent e)
	{
		// 모든 break event는 여기를 거쳐서 처리됨
		Block b=e.getBlock();

//		// 일단 cancel
		e.setCancelled(true);

		// Main Room 체크
		if(RoomLocation.getRoomTypeWithLocation(b.getLocation())==RoomType.MAIN)
		{
			this.onTesterAndChallengerBreakCore(e);
			this.onPlayerBreakBlockInMainRoom(e);
		} 
		else if(RoomLocation.getRoomTypeWithLocation(b.getLocation())==RoomType.PRACTICE)
		{
			this.onPlayerBreakBlockInPracticeRoom(e);
		} else if(RoomLocation.getRoomTypeWithLocation(b.getLocation())==RoomType.MINI_GAME) {
			this.onPlayerBreakBlockInMiniGameRoom(e);
		}

	}
	
	private void onPlayerBreakBlockInMiniGameRoom(BlockBreakEvent e) {
		Player p = e.getPlayer();
		PlayerData pData = this.pDataManager.getPlayerData(p.getUniqueId());
		Role role = pData.getRole();
		RelayTime time = this.relayManager.getCurrentTime();
		
		if(time == RelayTime.MAKING || time == RelayTime.TESTING) {
			if(role == Role.WAITER) {
				this.miniGameManager.processBlockEvent(e);
			}
		}
	}

	private void onPlayerBreakBlockInPracticeRoom(BlockBreakEvent e)
	{
		Player p = e.getPlayer();
		PlayerData pData = this.pDataManager.getPlayerData(p.getUniqueId());
		Role role = pData.getRole();
		RelayTime time = this.relayManager.getCurrentTime();
		
		if(time == RelayTime.MAKING || time == RelayTime.TESTING) {
			if(role == Role.WAITER) {
				Block b = e.getBlock();
				
				// core 부수면 token: 인원수 / 3 지급
				if(b.getType() == Material.GLOWSTONE) {
					int token = Bukkit.getOnlinePlayers().size() / 3;
					BroadcastTool.sendMessage(p, "you clear practice room");
					BroadcastTool.sendMessage(p, "token + " + token);
					
					// block 사라지게 (1회용)
					b.setType(Material.AIR);
				}
			}
		}
	}
	@EventHandler
	public void onPlayerPlaceBlock(BlockPlaceEvent e)
	{
		// 모든 place event는 여기를 거쳐서 처리됨
		Block b=e.getBlock();

		// 일단 cancel
		e.setCancelled(true);

		// Main Room 체크
		if(RoomLocation.getRoomTypeWithLocation(b.getLocation())==RoomType.MAIN)
		{
			this.onPlayerPlaceBlockInMainRoom(e);
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
		PlayerData pData=this.pDataManager.getPlayerData(pUuid);
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
				BroadcastTool.sendMessageToEveryone("core is broken!");

				// 2. 클리어한 maker는 pDataManager의 maker로 등록
				this.pDataManager.registerMaker(p);

				// 3. main room clearCount +1, time측정 후 초기화
				this.roomManager.getRoom(RoomType.MAIN).addClearCount(1);
				this.roomManager.recordMainRoomDurationTime();
				this.roomManager.setRoomEmpty(RoomType.MAIN);

				// 4.next relay 시작
				this.relayManager.startNextTime();

				// 5.player token +, clearCount +1
				int token = PlayerTool.onlinePlayersCount() / 2;
				pData.addToken(token);
				pData.addClearCount(1);
			}
			// Time: Testing / Role: Tester
			else if(role==Role.TESTER)
			{
				// 1.save room, set main room
				String title = this.relayManager.getRoomTitle();
				this.roomManager.saveRoomData(RoomType.MAIN, p, title);
				Room mainRoom=this.roomManager.getRoomData(title);
				this.roomManager.setRoom(RoomType.MAIN, mainRoom);

				// 2.next relay 시작
				this.relayManager.startNextTime();
			}
		}
	}


//	@EventHandler
	public void onPlayerBreakBlockInMainRoom(BlockBreakEvent e)
	{
		Block core=e.getBlock();
		RelayTime time=this.relayManager.getCurrentTime();
		
		// making time
		if(time==RelayTime.MAKING)
		{
			Player p=e.getPlayer();
			// maker
			if(this.pDataManager.isMaker(p))
			{
				// 기본적으로 부술 수 있게
				e.setCancelled(false);
				
				// core검사
				if(core.getType()==Material.GLOWSTONE)
				{
					BroadcastTool.sendMessage(p, "core is broken");
					this.relayManager.setCorePlaced(false);
				}
			}
		}
	}
	
	// MakekingTime에서 Maker가 core를 설치했는지 확인 (최대 1개만 설치 가능)
	// priority HIGH 로 높여서 마지막에 검사하게
//	@EventHandler(priority=EventPriority.HIGH)
	public void onPlayerPlaceBlockInMainRoom(BlockPlaceEvent e)
	{
		Block core=e.getBlock();
		
			RelayTime time=this.relayManager.getCurrentTime();
			// making time
			if(time==RelayTime.MAKING)
			{
				Player p=e.getPlayer();
				// maker
				if(this.pDataManager.isMaker(p))
				{
					// 기본적으로 설치할수있게
					e.setCancelled(false);
					
					// core관련 if문
					// 이미 설치되어 있을때
					if(core.getType()==Material.GLOWSTONE)
					{
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
//	public void onPlayerBreakBlockInMainRoom(BlockBreakEvent e)
//	{
//		// 생각해보니까 Maker일때만 break할때  체크해주면 됨(다른때는  다 그냥 cancel true로 !
//		Player p=e.getPlayer();
//		UUID uuid=p.getUniqueId();
//		PlayerData pData=this.pDataManager.getOnlinePlayerData(uuid);
//		Role role=pData.getRole();
//
//		boolean permission=false;
//
//		// Role별로 권한 체크
//		if(role==Role.MAKER)
//		{
//			permission=RolePermission.MAKER_BREAKBLOCK;
//		}
//		else if(role==Role.CHALLENGER)
//		{
//			permission=RolePermission.CHALLENGER_BREAKBLOCK;
//		}
//		else if(role==Role.TESTER)
//		{
//			permission=RolePermission.TESTER_BREAKBLOCK;
//		}
//		else if(role==Role.VIEWER)
//		{
//			permission=RolePermission.VIEWER_BREAKBLOCK;
//		}
//		else if(role==Role.WAITER)
//		{
//			permission=RolePermission.WAITER_BREAKBLOCK;
//		}
//
//		e.setCancelled(!permission);
//	}

//	@EventHandler
//	public void onPlayerPlaceBlockInMainRoom(BlockPlaceEvent e)
//	{
//		// 생각해보니까 Maker일때만 break할때  체크해주면 됨(다른때는  다 그냥 cancel true로 !
//		Player p=e.getPlayer();
//		UUID uuid=p.getUniqueId();
//		PlayerData pData=this.pDataManager.getOnlinePlayerData(uuid);
//		Role role=pData.getRole();
//
//		boolean permission=false;
//
//		// Role별로 권한 체크
//		if(role==Role.MAKER)
//		{
//			permission=RolePermission.MAKER_PLACEBLOCK;
//		}
//		else if(role==Role.CHALLENGER)
//		{
//			permission=RolePermission.CHALLENGER_PLACEBLOCK;
//		}
//		else if(role==Role.TESTER)
//		{
//			permission=RolePermission.TESTER_PLACEBLOCK;
//		}
//		else if(role==Role.VIEWER)
//		{
//			permission=RolePermission.VIEWER_PLACEBLOCK;
//		}
//		else if(role==Role.WAITER)
//		{
//			permission=RolePermission.WAITER_PLACEBLOCK;
//		}
//
//		e.setCancelled(!permission);
//	}

	@EventHandler
	public void onPlayerPlaceBucket(PlayerBucketEmptyEvent e)
	{
		Player p=e.getPlayer();
		UUID uuid=p.getUniqueId();
		PlayerData pData=this.pDataManager.getPlayerData(uuid);
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

	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e)
	{
		Player p=e.getPlayer();
		BroadcastTool.sendMessage(p, "welcome to RelayEscape server!");

		// PlayerData 처리
		this.TODOListWhenplayerJoinServer(p);
	}
	
	@EventHandler
	public void onMakerQuit(PlayerQuitEvent e)
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
					// -> join event에서 막아주기 때문에수행할 동작이 없음
				}
			}
		}

		// PlayerDataManager 처리
		// 구조 바꿔서 할 필요 없어짐
//		this.pDataManager.saveAndRemovePlayerData(p.getUniqueId());
	}
	
	
}
























