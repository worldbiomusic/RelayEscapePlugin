package com.wbm.plugin.util;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import com.wbm.plugin.data.BlockData;
import com.wbm.plugin.data.Room;
import com.wbm.plugin.util.config.DataMember;
import com.wbm.plugin.util.general.BroadcastTool;

public class RoomManager implements DataMember
{
	/*
	 * Time		Waiting	Making	Testing	Challenging	Challenging(again)
	 * Room		"empty"	"empty"	"empty"	new title	random
	 * 
	 */
	Room mainRoom;

	// key: room.name / value: room
	Map<String, Room> rooms;

	public RoomManager()
	{
		this.rooms=new HashMap<>();

		// server main room loation 범위 설정
		World world=Bukkit.getWorld("world");
		Location loc1=new Location(world, 1, 4, 1);
		Location loc2=new Location(world, 10, 50, 10);
		Room.setMainRoomSpace(loc1, loc2);

		// register basic rooms
		this.registerBasicRooms();
	}
	
	public void clearMainRoom() {
		long secDuration = (System.currentTimeMillis() - (long)this.mainRoom.getAvgDurationTime()) / 1000;
		double newDuration = secDuration / 60;
		
		int challengingCount = this.mainRoom.getChallengingCount();
		double avgDuration = this.mainRoom.getAvgDurationTime();
		double totalDuration = (challengingCount-1) * avgDuration + newDuration;
		double totalAvgDuration = totalDuration / challengingCount;   
		
		this.mainRoom.setAvgDurationTime(totalAvgDuration);
	}
	
	public void recordRoomDuration() {
		this.mainRoom.setAvgDurationTime(System.currentTimeMillis());
	}
	
	public Room getMainRoom() {
		return this.mainRoom;
	}
	
	public Room getRoom(String title) {
		return this.rooms.get(title);
	}
	
	public String saveRoomData(String makerName) {
		// TODO: RoomType으로 구별해서 지역별로 data만들기 (argu에 RoomType roomType 추가하기)
		
		// main room
		List<BlockData> blockDatas = this.getRoomBlockDatas();
		String title = this.getNextTitleWithMaker(makerName);
		
		Room room = new Room(title, makerName, blockDatas, LocalDateTime.now());
		
		// rooms 에 저장
		this.rooms.put(title, room);
		
		return title;
	}
	
	public String getNextTitleWithMaker(String maker) {
		// Room title입력안했을시 maker1, maker2, maker3 ... 순으로 title이 저장됨
		for(int i = 1; i < 1000; i++) {
			String title = maker + i;
			if(! (this.rooms.containsKey(title))) {
				return title;
			}
		}
		
		// 한 사람이 방을 1000개 초과 만들었을떄 ㄷㄷ
		return null;
	}
	
	// TODO: RoomType으로 구별해서 지역별로 data만들기 (argu에 RoomType roomType 추가하기)
	public List<BlockData> getRoomBlockDatas() {
		
		// Main room
		Location loc1=Room.mainRoomLoc1;
		Location loc2=Room.mainRoomLoc2;
		
		
		int dx=(int)loc2.getX()-(int)loc1.getX();
		int dy=(int)loc2.getY()-(int)loc1.getY();
		int dz=(int)loc2.getZ()-(int)loc1.getZ();
		
		List<BlockData> blocks = new ArrayList<>();
		for(int z=0; z<=dz; z++)
		{
			for(int y=0; y<=dy; y++)
			{
				for(int x=0; x<=dx; x++)
				{
					Location loc=loc1.clone();
					loc.add(x, y, z);
					Block b = loc.getBlock();

					Material mat = b.getType();
					@SuppressWarnings("deprecation")
					Byte data = b.getData();
					BlockData blockData = new BlockData(mat, data);
					
					// add to blockData list
					blocks.add(blockData);
				}
			}
		}
		
		return blocks;
	}

	private void registerBasicRooms()
	{
		// empty room
		List<BlockData> emptyBlocks=new ArrayList<>();
		for(int i=0; i<(10*10*47); i++)
		{
			emptyBlocks.add(new BlockData(Material.AIR, 0));
		}
		if(!(this.rooms.containsKey("empty")))
		{
			Room emptyRoom=new Room("empty", "wbm", emptyBlocks, LocalDateTime.of(2020, 11, 1, 0, 0));
			this.rooms.put("empty", emptyRoom);
		}

		// base room
		if(!(this.rooms.containsKey("base")))
		{
			List<BlockData> baseBlocks=new ArrayList<>();
			baseBlocks.add(new BlockData(Material.GLOWSTONE, 0));
			for(int i=0; i<(10*10*47); i++)
			{
				baseBlocks.add(new BlockData(Material.AIR, 0));
			}

			Room baseRoom=new Room("base", "wbm", baseBlocks, LocalDateTime.of(2020, 11, 1, 0, 0));
			this.rooms.put("base", baseRoom);
		}

	}

	public void setMainRoom(Room room)
//	setRoom()으로 바꾸고, RoomType으로 결정할 수 있게 하기
	{
		// main room
		this.mainRoom = room;
		this.fillSpace(Room.mainRoomLoc1, Room.mainRoomLoc2, room.getBlocks());
	}

	@SuppressWarnings("deprecation")
	void fillSpace(Location loc1, Location loc2, List<BlockData> blocks)
	{
		int dx=(int)loc2.getX()-(int)loc1.getX();
		int dy=(int)loc2.getY()-(int)loc1.getY();
		int dz=(int)loc2.getZ()-(int)loc1.getZ();

		int index=0;
		for(int z=0; z<=dz; z++)
		{
			for(int y=0; y<=dy; y++)
			{
				for(int x=0; x<=dx; x++)
				{
					Location loc=loc1.clone();
					loc.add(x, y, z);

					BlockData blockData = blocks.get(index);
					Material mat = blockData.getMaterial();
					Byte data = blockData.getData();
					// set type
					loc.getBlock().setType(mat);
					// set data
					loc.getBlock().setData(data);
					
					index++;
				}
			}
		}

	}

	void fillSpaceWithMaterial(Location loc1, Location loc2, List<Material> materials)
	{
		int dx=(int)loc2.getX()-(int)loc1.getX();
		int dy=(int)loc2.getY()-(int)loc1.getY();
		int dz=(int)loc2.getZ()-(int)loc1.getZ();

//		Bukkit.getServer().broadcastMessage("dx: " + dx);
//		Bukkit.getServer().broadcastMessage("dz: " + dz);

		int index=0;
		for(int z=0; z<=dz; z++)
		{
			for(int y=0; y<=dy; y++)
			{
				for(int x=0; x<=dx; x++)
				{
					Location loc=loc1.clone();
					loc.add(x, y, z);

					loc.getBlock().setType(materials.get(index));
					index++;
				}
			}
		}

	}

	public void setMainRoomEmpty()
	{
		this.setMainRoom(this.getRoom("empty"));
	}

	public void setMainRoomToBaseRoom()
	{
		// Maker뽑기위해 일단 이전맵을 돌려야 하는 메서드지만 아직 기반이없어서
		// base room으로 가운에 glowstone하나 설치해서 Maker 한사람 구하기가 목적
		
		// rooms에 저장되있는것중 아무거나 하나 선택 (empty 제외)

		// TODO: 임시로 base room을 랜덤으로 설정
		Set<String> keys = this.rooms.keySet();
		String[] titles = new String[keys.size()];
		keys.toArray(titles);
		
		int r = (int) (Math.random() * titles.length);
		String title = titles[r];
		
		// empty이면 core가 없어 진행이 불가능이므로 base로 교체
		if(title.equals("empty")) {
			title = "base";
		}
		
		Room room = this.getRoom(title);
		
		this.setMainRoom(room);
		
		BroadcastTool.printConsoleMessage("base room: " + title);
		BroadcastTool.printConsoleMessage("room count : " + titles.length);
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public void installData(Object obj)
	{
		this.rooms=(Map<String, Room>)obj;

		// register basic rooms
		this.registerBasicRooms();
	}

	@Override
	public Object getData()
	{
		return this.rooms;
	}

	@Override
	public String getDataMemberName()
	{
		// TODO Auto-generated method stub
		return "room";
	}
}
