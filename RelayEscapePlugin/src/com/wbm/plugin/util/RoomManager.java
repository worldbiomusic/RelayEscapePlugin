package com.wbm.plugin.util;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.wbm.plugin.data.BlockData;
import com.wbm.plugin.data.Room;
import com.wbm.plugin.data.RoomLocation;
import com.wbm.plugin.data.RoomLocker;
import com.wbm.plugin.util.config.DataMember;
import com.wbm.plugin.util.enums.RoomType;
import com.wbm.plugin.util.general.BroadcastTool;
import com.wbm.plugin.util.general.MathTool;

public class RoomManager implements DataMember {
    /*
     * 룸 크기: 10 * 50 * 10 = 5000
     * 
     * [AIR를 null로 저장했을때]
     * 
     * 룸 개수: 51개 크기: 255KB
     * 
     * 룸 1개당 크기: 5KB
     * 
     * null(AIR)저장 크기 = 0.001KB
     * 
     * 일반블럭 저장크기 = 0.012KB
     * 
     * 12배 차이남
     * 
     * ※더욱 데이터 아끼는 방법 HashMap<Location, BlockData>으로 저장 (fill 하기 전에 모두 AIR로 변경후에)
     */

    // 단순한 Room 데이터들
    // title, room
    Map<String, Room> roomData;

    // 현재 roomType에 맞는 실제 room data
    Map<RoomType, Room> rooms;

    private double durationTime;

    public RoomManager() {
	this.roomData = new HashMap<>();
	this.rooms = new HashMap<>();

	// register basic rooms
	this.registerBasicRooms();
    }

    public void recordMainRoomDurationTime() {
	double secDuration = (int) ((System.currentTimeMillis() - this.durationTime) / 1000);
	double minDuration = secDuration / 60;

	Room mainRoom = this.rooms.get(RoomType.MAIN);
	// room avgDurationTime 업데이트
	mainRoom.addNewAvgDurationTime(minDuration);
    }

    public void startMainRoomDurationTime() {
	this.durationTime = System.currentTimeMillis();
    }

    public Room getRoom(RoomType roomType) {
	/*
	 * 현재 RoomType의 Room을 반환
	 */
	return this.rooms.get(roomType);
    }

    public Room getRoomData(String title) {
	return this.roomData.get(title);
    }

    public String saveRoomData(RoomType roomType, String maker, String roomTitle) {

	// room data 가져오기
	List<BlockData> blockDatas = this.getRoomBlockDatas(roomType);

	// room 객체 생성
	Room room = new Room(roomTitle, maker, blockDatas, LocalDateTime.now());

	// rooms 에 저장
	this.roomData.put(roomTitle, room);

	return roomTitle;
    }

    public boolean updateRoom(String title, Room room) {
	if(this.isExistRoomTitle(title)) {
	    this.roomData.put(title, room);
	    return true;
	} else {
	    return false;
	}
    }

    public String getNextTitleWithMakerName(String maker) {
	// Room title입력안했을시 maker1, maker2, maker3 ... 순으로 title이 저장됨
	for (int i = 1; i < 100000000; i++) {
	    String title = maker + i;
	    if (!(this.roomData.containsKey(title))) {
		return title;
	    }
	}

	// 한 사람이 방을 10000개 초과 만들었을떄 ㄷㄷ
	return null;
    }

    @SuppressWarnings("deprecation")
    public List<BlockData> getRoomBlockDatas(RoomType roomType) {
	/*
	 * Material.AIR는 BlockData = null로 저장 (데이터 크기 줄이기)
	 */
	// Main room
	Location pos1 = null, pos2 = null;
	if (roomType == RoomType.MAIN) {
	    pos1 = RoomLocation.MAIN_Pos1;
	    pos2 = RoomLocation.MAIN_Pos2;
	} else if (roomType == RoomType.PRACTICE) {
	    pos1 = RoomLocation.PRACTICE_Pos1;
	    pos2 = RoomLocation.PRACTICE_Pos2;
	}

	int pos1X = (int) pos1.getX();
	int pos2X = (int) pos2.getX();
	int pos1Y = (int) pos1.getY();
	int pos2Y = (int) pos2.getY();
	int pos1Z = (int) pos1.getZ();
	int pos2Z = (int) pos2.getZ();

	// get difference
	int dx = MathTool.getDiff(pos1X, pos2X);
	int dy = MathTool.getDiff(pos1Y, pos2Y);
	int dz = MathTool.getDiff(pos1Z, pos2Z);

	// get smaller x, y, z
	int smallX = Math.min(pos1X, pos2X);
	int smallY = Math.min(pos1Y, pos2Y);
	int smallZ = Math.min(pos1Z, pos2Z);

	List<BlockData> blocks = new ArrayList<>();
	for (int z = 0; z <= dz; z++) {
	    for (int y = 0; y <= dy; y++) {
		for (int x = 0; x <= dx; x++) {
		    Location loc = new Location(Setting.world, smallX, smallY, smallZ);
		    loc.add(x, y, z);
		    Block b = loc.getBlock();

		    Material mat = b.getType();
		    Byte data = b.getData();

		    BlockData blockData = new BlockData(mat, data);

		    // AIR일때 null로 저장(데이터 크기 줄일 수 있음)
		    if (mat == Material.AIR) {
			blockData = null;
		    }

		    // add to blockData list
		    blocks.add(blockData);
		}
	    }
	}

	return blocks;
    }

    private void registerBasicRooms() {
	int mainRoomBlockCount = RoomLocation.getRoomBlockCount(RoomType.MAIN);

	// empty room
	if (!(this.roomData.containsKey("empty"))) {
	    // make room
	    List<BlockData> emptyBlocks = new ArrayList<>();
	    for (int i = 0; i < mainRoomBlockCount; i++) {
		// Material.AIR
		emptyBlocks.add(null);
	    }

	    // put room
	    Room emptyRoom = new Room("empty", "?", emptyBlocks, LocalDateTime.of(2020, 11, 1, 0, 0));
	    this.roomData.put("empty", emptyRoom);
	}

	// base room
	if (!(this.roomData.containsKey("base"))) {
	    // make room
	    List<BlockData> baseBlocks = new ArrayList<>();
	    for (int i = 0; i < mainRoomBlockCount; i++) {
		// Material.AIR
		baseBlocks.add(null);
	    }

	    // set core block
	    baseBlocks.set(0, new BlockData(Material.GLOWSTONE, 0));

	    // put room
	    Room baseRoom = new Room("base", "?", baseBlocks, LocalDateTime.of(2020, 11, 1, 0, 0));
	    this.roomData.put("base", baseRoom);
	}

    }

    public void setRoom(RoomType roomType, Room room)
//	setRoom()으로 바꾸고, RoomType으로 결정할 수 있게 하기
    {
	this.rooms.put(roomType, room);
	this.fillSpace(roomType, room.getBlocks());
    }

    @SuppressWarnings("deprecation")
    void fillSpace(RoomType roomType, List<BlockData> blocks) {
	/*
	 * BlockData == null 인것은 Material.AIR로 변경 (데이터 크기 줄이기)
	 */
	Location pos1 = null, pos2 = null;

	if (roomType == RoomType.MAIN) {
	    pos1 = RoomLocation.MAIN_Pos1;
	    pos2 = RoomLocation.MAIN_Pos2;
	} else if (roomType == RoomType.PRACTICE) {
	    pos1 = RoomLocation.PRACTICE_Pos1;
	    pos2 = RoomLocation.PRACTICE_Pos2;
	} else {
	    // wrong RoomType
	    BroadcastTool.debug(" bug!!!!!!!!!!!!!!!!!");
	    return;
	}

	int pos1X = (int) pos1.getX();
	int pos2X = (int) pos2.getX();
	int pos1Y = (int) pos1.getY();
	int pos2Y = (int) pos2.getY();
	int pos1Z = (int) pos1.getZ();
	int pos2Z = (int) pos2.getZ();

	// get difference
	int dx = MathTool.getDiff(pos1X, pos2X);
	int dy = MathTool.getDiff(pos1Y, pos2Y);
	int dz = MathTool.getDiff(pos1Z, pos2Z);

	// get smaller x, y, z
	int smallX = Math.min(pos1X, pos2X);
	int smallY = Math.min(pos1Y, pos2Y);
	int smallZ = Math.min(pos1Z, pos2Z);

	int index = 0;
	/*
	 * for문에서 <=dx인 이유: 만약 (1,1) ~ (3,3) 면적의 블럭을 지정하면 총 9개의 블럭을 가리키는것인데 위에서 dx, dy,
	 * dz를 구할때 차이를 구하므로 3-1 = 2 즉 2칸만을 의미하게 되서 <=을 해줘서 3칸을 채우게 함
	 */
	for (int z = 0; z <= dz; z++) {
	    for (int y = 0; y <= dy; y++) {
		for (int x = 0; x <= dx; x++) {
		    Location loc = new Location(Setting.world, smallX, smallY, smallZ);
		    loc.add(x, y, z);

		    BlockData blockData = blocks.get(index);
		    Material mat;
		    Byte data;

		    // blockData == null일때 AIR로 변경해서 채우기
		    if (blockData == null) {
			mat = Material.AIR;
			data = 0;
		    } else {
			mat = blockData.getMaterial();
			data = blockData.getData();
		    }

		    // set type
		    loc.getBlock().setType(mat);
		    // set data
		    loc.getBlock().setData(data);

		    index++;
		}
	    }
	}
    }

    @SuppressWarnings("deprecation")
    void fillBlocks(List<Location> locs, ItemStack item) {
	for (Location loc : locs) {
	    loc.getBlock().setType(item.getType());
	    loc.getBlock().setData(item.getData().getData());
	    loc.getBlock().getState().update();
	}
    }

    public void setRoomEmpty(RoomType roomType) {
	this.setRoom(roomType, this.getRoomData("empty"));
    }

    public Room getRandomRoomData() {
	Room randomRoom = this.roomData.get("base");

	int r = (int) (Math.random() * this.roomData.size());
	int i = 0;
	for (Room room : this.roomData.values()) {
	    if (i >= r) {
		if (room.getTitle().equals("empty")) {
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

    public void lockRoom(RoomType roomType) {
	// lock room with some blocks
	if (roomType == RoomType.MAIN) {
	    this.fillBlocks(RoomLocker.mainLocker, RoomLocker.mainLockerItem);
	}
    }

    public void unlockRoom(RoomType roomType) {
	if (roomType == RoomType.MAIN) {
	    this.fillBlocks(RoomLocker.mainLocker, RoomLocker.air);
	}
    }

    public List<Room> getOwnRooms(String maker) {
	List<Room> rooms = new ArrayList<>();
	for (Room room : this.roomData.values()) {
	    if (room.getMaker().equals(maker)) {
		rooms.add(room);
	    }
	}
	return rooms;
    }

    public void printRoomList(Player p) {
	/*
	 * room info 출력 예. Data: 2020/12/22 10:11 Title: wbm's room
	 */
	List<Room> rooms = this.getOwnRooms(p.getName());

	BroadcastTool.sendMessage(p, "=====[Room List]=====");
	for (Room room : rooms) {
	    LocalDateTime b = room.getBirth();
	    String date = String.format("%d/%d/%d-%dH:%dM", b.getYear(), b.getMonthValue(), b.getDayOfMonth(),
		    b.getHour(), b.getMinute());
	    String roomInfo = String.format(ChatColor.BLUE + "Date" + ChatColor.WHITE + ": %s       " + ChatColor.RED
		    + "Title" + ChatColor.WHITE + ": %s", date, room.getTitle());

	    BroadcastTool.sendMessage(p, roomInfo);
	}
    }

    public int getAllRoomCount() {
	return this.roomData.size();
    }

    public boolean isExistRoomTitle(String title) {
	return this.roomData.containsKey(title);
    }

    public Room removeRoom(String title) {
	return this.roomData.remove(title);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void installData(Object obj) {
	this.roomData = (Map<String, Room>) obj;

//	 register basic rooms
	this.registerBasicRooms();

	BroadcastTool.debug("============ROOM DATA===============");
	// print all room
	for (Room room : this.roomData.values()) {
	    BroadcastTool.debug(room.toString());
	}

    }

    @Override
    public Object getData() {
	return this.roomData;
    }

    @Override
    public String getDataMemberName() {
	// TODO Auto-generated method stub
	return "room";
    }
}
