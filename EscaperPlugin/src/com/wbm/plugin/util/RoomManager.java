package com.wbm.plugin.util;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import com.wbm.plugin.data.BlockData;
import com.wbm.plugin.data.Room;
import com.wbm.plugin.data.RoomLocation;
import com.wbm.plugin.util.config.DataMember;
import com.wbm.plugin.util.enums.RoomType;
import com.wbm.plugin.util.general.BroadcastTool;
import com.wbm.plugin.util.general.MathTool;

public class RoomManager implements DataMember
{
	/*
	 * Time		Waiting	Making	Testing	Challenging	Challenging(again)
	 * Room		"empty"	"empty"	"empty"	new title	random
	 * 
	 * Room 크기가 다 일정해야 함!
	 */
	
	// 단순한 Room 데이터들
	Map<String, Room> roomData;
	
	// MainRoom, PracticeRoom
	Map<RoomType, Room> rooms;
	
	private double durationTime;

	public RoomManager()
	{
		this.roomData=new HashMap<>();
		this.rooms = new HashMap<>();

		// register basic rooms
		this.registerBasicRooms();
	}
	
	public void recordMainRoomDurationTime() {
		double secDuration = (int)((System.currentTimeMillis() - this.durationTime) / 1000);
		double minDuration = secDuration / 60;
		
		Room mainRoom = this.rooms.get(RoomType.MAIN);
		mainRoom.addNewAvgDurationTime(minDuration);
	}
	
	public void startMainRoomDurationTime() {
		this.durationTime = System.currentTimeMillis();
	}
	
	public Room getRoom(RoomType roomType) {
		return this.rooms.get(roomType);
	}
	
	public Room getRoomData(String title) {
		return this.roomData.get(title);
	}
	
	public String saveRoomData(RoomType roomType, String makerName) {
		
		// main room
		List<BlockData> blockDatas = this.getRoomBlockDatas(roomType);
		String title = this.getNextTitleWithMaker(makerName);
		
		Room room = new Room(title, makerName, blockDatas, LocalDateTime.now());
		
		// rooms 에 저장
		this.roomData.put(title, room);
		
		return title;
	}
	
	public String getNextTitleWithMaker(String maker) {
		// Room title입력안했을시 maker1, maker2, maker3 ... 순으로 title이 저장됨
		for(int i = 1; i < 10000; i++) {
			String title = maker + i;
			if(! (this.roomData.containsKey(title))) {
				return title;
			}
		}
		
		// 한 사람이 방을 10000개 초과 만들었을떄 ㄷㄷ
		return null;
	}
	
	// TODO: RoomType으로 구별해서 지역별로 data만들기 (argu에 RoomType roomType 추가하기)
	public List<BlockData> getRoomBlockDatas(RoomType roomType) {
		
		// Main room
		Location pos1= null, pos2 = null;
		if(roomType == RoomType.MAIN) {
			pos1 = RoomLocation.mainPos1;
			pos2 = RoomLocation.mainPos2;
		} else if(roomType == RoomType.PRACTICE) {
			pos1 = RoomLocation.practicePos1;
			pos2 = RoomLocation.practicePos2;
		}
		int pos1X = (int)pos1.getX(); int pos2X = (int)pos2.getX();
		int pos1Y = (int)pos1.getY(); int pos2Y = (int)pos2.getY();
		int pos1Z = (int)pos1.getZ(); int pos2Z = (int)pos2.getZ();
		
		// get difference
		int dx=MathTool.getDiff(pos1X, pos2X);
		int dy=MathTool.getDiff(pos1Y, pos2Y);
		int dz=MathTool.getDiff(pos1Z, pos2Z);
				
		// get smaller x, y, z
		int smallX = MathTool.getSmaller(pos1X, pos2X);
		int smallY = MathTool.getSmaller(pos1Y, pos2Y);
		int smallZ = MathTool.getSmaller(pos1Z, pos2Z);
		
		List<BlockData> blocks = new ArrayList<>();
		for(int z=0; z<=dz; z++)
		{
			for(int y=0; y<=dy; y++)
			{
				for(int x=0; x<=dx; x++)
				{
					Location loc=new Location(Bukkit.getWorld("world"), smallX, smallY, smallZ);;
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
		int mainRoomBlockCount = RoomLocation.getRoomBlockCount(RoomType.MAIN);
		
		// empty room
		if(!(this.roomData.containsKey("empty")))
		{
			// make room
			List<BlockData> emptyBlocks=new ArrayList<>();
			for(int i=0; i< mainRoomBlockCount; i++)
			{
				emptyBlocks.add(new BlockData(Material.AIR, 0));
			}
			
			// put room
			Room emptyRoom=new Room("empty", "wbm", emptyBlocks, LocalDateTime.of(2020, 11, 1, 0, 0));
			this.roomData.put("empty", emptyRoom);
		}

		// base room
		if(!(this.roomData.containsKey("base")))
		{
			// make room
			List<BlockData> baseBlocks=new ArrayList<>();
			baseBlocks.add(new BlockData(Material.GLOWSTONE, 0));
			for(int i=0; i<mainRoomBlockCount; i++)
			{
				baseBlocks.add(new BlockData(Material.AIR, 0));
			}

			// put room
			Room baseRoom=new Room("base", "wbm", baseBlocks, LocalDateTime.of(2020, 11, 1, 0, 0));
			this.roomData.put("base", baseRoom);
		}

	}

	public void setRoom(RoomType roomType, Room room)
//	setRoom()으로 바꾸고, RoomType으로 결정할 수 있게 하기
	{
		this.rooms.put(roomType, room);
		this.fillSpace(roomType, room.getBlocks());
		
		// room state 알림
		BroadcastTool.sendMessageToEveryone("Main room: " + room.getTitle());
		BroadcastTool.sendMessageToEveryone("roomData count : " + this.roomData.size());
	}

	@SuppressWarnings("deprecation")
	void fillSpace(RoomType roomType, List<BlockData> blocks)
	{
		Location pos1 = null, pos2 = null;
		
		if(roomType == RoomType.MAIN ) {
			pos1 = RoomLocation.mainPos1;
			pos2 = RoomLocation.mainPos2;
		} else if(roomType == RoomType.PRACTICE) {
			pos1 = RoomLocation.practicePos1;
			pos2 = RoomLocation.practicePos2;
		} else {
			// wrong RoomType
			BroadcastTool.debug(" bug!!!!!!!!!!!!!!!!!");
			return;
		}
		
		int pos1X = (int)pos1.getX(); int pos2X = (int)pos2.getX();
		int pos1Y = (int)pos1.getY(); int pos2Y = (int)pos2.getY();
		int pos1Z = (int)pos1.getZ(); int pos2Z = (int)pos2.getZ();
		
		// get difference
		int dx=MathTool.getDiff(pos1X, pos2X);
		int dy=MathTool.getDiff(pos1Y, pos2Y);
		int dz=MathTool.getDiff(pos1Z, pos2Z);
		
		// get smaller x, y, z
		int smallX = MathTool.getSmaller(pos1X, pos2X);
		int smallY = MathTool.getSmaller(pos1Y, pos2Y);
		int smallZ = MathTool.getSmaller(pos1Z, pos2Z);

		int index=0;
		/*
		 * for문에서 <=dx인 이유: 만약 (1,1) ~ (3,3) 면적의 블럭을 지정하면 총 9개의 블럭을 가리키는것인데
		 * 위에서 dx, dy, dz를 구할때 차이를 구하므로 3-1 = 2 즉 2칸만을 의미하게 되서 <=을 해줘서 3칸을 채우게 함
		 */
		for(int z=0; z<=dz; z++)
		{
			for(int y=0; y<=dy; y++)
			{
				for(int x=0; x<=dx; x++)
				{
					Location loc= new Location(Bukkit.getWorld("world"), smallX, smallY, smallZ);
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

//	void fillSpaceWithMaterial(Location loc1, Location loc2, List<Material> materials)
//	{
//		int dx=(int)loc2.getX()-(int)loc1.getX();
//		int dy=(int)loc2.getY()-(int)loc1.getY();
//		int dz=(int)loc2.getZ()-(int)loc1.getZ();
//
//		int index=0;
//		for(int z=0; z<=dz; z++)
//		{
//			for(int y=0; y<=dy; y++)
//			{
//				for(int x=0; x<=dx; x++)
//				{
//					Location loc=loc1.clone();
//					loc.add(x, y, z);
//
//					loc.getBlock().setType(materials.get(index));
//					index++;
//				}
//			}
//		}
//
//	}

	public void setRoomEmpty(RoomType roomType)
	{
		this.setRoom(roomType, this.getRoomData("empty"));
	}
	
	public Room getRandomRoomData() {
		Room randomRoom = this.roomData.get("base");
		
		int r = (int)(Math.random() * this.roomData.size());
		int i = 0;
		for(Room room : this.roomData.values()) {
			if(i >= r) {
				if(room.getTitle().equals("empty")) {
					// empty면 진행이 안되기 때문에 뺌
					break;
				}
				randomRoom = room;
				break;
			}
			i++;
		}
		return randomRoom;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void installData(Object obj)
	{
		this.roomData=(Map<String, Room>)obj;

		// register basic rooms
		this.registerBasicRooms();
		
		// print all room
		for(Room room : this.roomData.values()) {
			BroadcastTool.debug(room.toString());
		}
			
	}

	@Override
	public Object getData()
	{
		return this.roomData;
	}

	@Override
	public String getDataMemberName()
	{
		// TODO Auto-generated method stub
		return "room";
	}
}
