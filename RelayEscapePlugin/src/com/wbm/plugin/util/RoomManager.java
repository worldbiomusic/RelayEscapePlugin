package com.wbm.plugin.util;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.wbm.plugin.data.PlayerData;
import com.wbm.plugin.data.Room;
import com.wbm.plugin.data.RoomLocation;
import com.wbm.plugin.util.config.DataMember;
import com.wbm.plugin.util.enums.RoomType;
import com.wbm.plugin.util.general.BroadcastTool;

public class RoomManager implements DataMember {
	/*
	 * WorldEdit API 사용해서 schematic "파일"로 룸 데이터 관리
	 * 
	 * 디렉토리: dataFolder()/roomData/
	 */

	WorldEditAPIController worldeditAPI;

	// 단순한 Room 데이터들
	// title, room
	Map<String, Room> roomData;


	// 현재 roomType에 맞는 실제 room data
	Map<RoomType, Room> rooms;

	private double durationStartTime;

	public RoomManager(WorldEditAPIController worldeditAPI) {
		this.worldeditAPI = worldeditAPI;
		this.roomData = new HashMap<>();
		this.rooms = new HashMap<>();

		// register basic rooms
		this.registerBasicRooms();
	}

	public void recordMainRoomDurationTime() {
		int secDuration = Math.round((float) ((System.currentTimeMillis() - this.durationStartTime) / 1000));
		System.out.println("secDuration: " + secDuration);
		Room mainRoom = this.rooms.get(RoomType.MAIN);
		// room avgDurationTime 업데이트
		mainRoom.addNewAvgDurationTime(secDuration);
	}

	public void startMainRoomDurationTime() {
		this.durationStartTime = System.currentTimeMillis();
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

		// room을 shematic file로 저장
		this.saveRoomToSchematicFile(roomType, roomTitle);

		// room 객체 생성
		Room room = new Room(roomTitle, maker, LocalDateTime.now());

		// rooms 에 저장
		this.roomData.put(roomTitle, room);

		return roomTitle;
	}

	private void saveRoomToSchematicFile(RoomType roomType, String roomTitle) {
		Location minPos = null, maxPos = null;

		if (roomType == RoomType.MAIN) {
			minPos = RoomLocation.MAIN_Pos1;
			maxPos = RoomLocation.MAIN_Pos2;
		} else if (roomType == RoomType.PRACTICE) {
			minPos = RoomLocation.PRACTICE_Pos1;
			maxPos = RoomLocation.PRACTICE_Pos2;
		} else {
			return;
		}

		// schematic file로 저장
		this.worldeditAPI.copy(minPos, maxPos);
		this.worldeditAPI.save(roomTitle + ".schem");
	}

	public boolean updateRoom(String title, Room room) {
		if (this.isExistRoomTitle(title)) {
			this.roomData.put(title, room);
			return true;
		} else {
			return false;
		}
	}

	public String getNextTitleWithMakerName(String maker) {
		// Room title입력안했을시 maker1, maker2, maker3 ... 순으로 title이 저장됨
		for (int i = 1; i < 100000000; i++) {
			String title = maker + "_" + i;
			if (!(this.roomData.containsKey(title))) {
				return title;
			}
		}

		// 한 사람이 방을 100000000개 초과 만들었을떄
		BroadcastTool.reportBug("한 사람이 방을 100000000개 초과 만들었을떄의 버그");
		return null;
	}

	private void registerBasicRooms() {
		int mainRoomBlockCount = RoomLocation.getRoomBlockCount(RoomType.MAIN);

		// empty room
		if (!(this.roomData.containsKey("empty"))) {
			// put room
			Room emptyRoom = new Room("empty", "worldbiomusic", LocalDateTime.of(2020, 11, 1, 0, 0));
			this.roomData.put("empty", emptyRoom);
		}

		// base room
		if (!(this.roomData.containsKey("base"))) {
			// put room
			Room baseRoom = new Room("base", "worldbiomusic", LocalDateTime.of(2020, 11, 1, 0, 0));
			this.roomData.put("base", baseRoom);
		}
	}

	public void setRoom(RoomType roomType, Room room) {
		this.rooms.put(roomType, room);
		this.fillSpace(roomType, room.getTitle());
	}

	public void fillSpace(RoomType roomType, String schematicTitle) {
		System.out.println("TITLE: " + schematicTitle);
		Location minPos = null;

		if (roomType == RoomType.MAIN) {
			minPos = RoomLocation.MAIN_Pos1;
		} else if (roomType == RoomType.PRACTICE) {
			minPos = RoomLocation.PRACTICE_Pos1;
		} else {
			return;
		}

		this.worldeditAPI.load(schematicTitle + ".schem");
		this.worldeditAPI.paste(minPos);
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

	public void plusTokenToRoomMaker(PlayerDataManager pDataManager, Room room, int token) {
		String maker = room.getMaker();
		PlayerData roomMakerPData = pDataManager.getPlayerData(maker);
		roomMakerPData.plusToken(token);
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
