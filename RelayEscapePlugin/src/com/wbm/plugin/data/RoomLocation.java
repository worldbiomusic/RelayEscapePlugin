package com.wbm.plugin.data;

import org.bukkit.Location;

import com.wbm.plugin.util.Setting;
import com.wbm.plugin.util.enums.RoomType;
import com.wbm.plugin.util.general.LocationTool;
import com.wbm.plugin.util.general.SpawnLocationTool;

/*
 * 알아둘것!: RoomLocation은 Room의 블럭을 초기화 할 범위, 블럭 break place범위를 나타내는 범위!
 */
public class RoomLocation {
    // MAIN
    public static final Location MAIN_Pos1 = Setting.getLoationFromSTDLOC(10, 4, 10);
    public static final Location MAIN_Pos2 = Setting.getLoationFromSTDLOC(-14, 28, -14);
    public static final Location MAIN_SPAWN = Setting.getLoationFromSTDLOC(-1.5, 4, -1.5, 90, 0);

    // PRACTICE
    public static final Location PRACTICE_Pos1 = Setting.getLoationFromSTDLOC(21, 4, 21);
    public static final Location PRACTICE_Pos2 = Setting.getLoationFromSTDLOC(45, 28, 45);
    public static final Location PRACTICE_SPAWN = Setting.getLoationFromSTDLOC(33.5, 4, 33.5);

    // MINIGAME
    public static final Location MINIGAME_Pos1 = Setting.getLoationFromSTDLOC(10, 0, 21);
    public static final Location MINIGAME_Pos2 = Setting.getLoationFromSTDLOC(-14, 28, 45);
    public static final Location MINIGAME_SPAWN = SpawnLocationTool.LOBBY;

    // FUN
    public static final Location FUN_Pos1 = Setting.getLoationFromSTDLOC(21, 0, 10);
    public static final Location FUN_Pos2 = Setting.getLoationFromSTDLOC(45, 28, -14);
    public static final Location FUN_SPAWN = SpawnLocationTool.LOBBY;

    // ROOM사이즈 (모든 룸 같은 크기)
    public static final int ROOM_SIZE_X = Math.abs((int) (MAIN_Pos1.getX() - MAIN_Pos2.getX()));
    public static final int ROOM_SIZE_Y = Math.abs((int) (MAIN_Pos1.getY() - MAIN_Pos2.getY()));
    public static final int ROOM_SIZE_Z = Math.abs((int) (MAIN_Pos1.getZ() - MAIN_Pos2.getZ()));

    public static int getRoomBlockCount(RoomType roomType) {
	Location pos1 = null, pos2 = null;
	if (roomType == RoomType.MAIN) {
	    pos1 = MAIN_Pos1;
	    pos2 = MAIN_Pos2;
	} else if (roomType == RoomType.PRACTICE) {
	    pos1 = PRACTICE_Pos1;
	    pos2 = PRACTICE_Pos2;
	} else if (roomType == RoomType.MINI_GAME) {
	    pos1 = MINIGAME_Pos1;
	    pos2 = MINIGAME_Pos2;
	} else if (roomType == RoomType.FUN) {
	    pos1 = FUN_Pos1;
	    pos2 = FUN_Pos2;
	}

	return LocationTool.getAreaBlockCount(pos1, pos2);
    }

    public static RoomType getRoomTypeWithLocation(Location loc) {
	if (LocationTool.isIn(MAIN_Pos1, loc, MAIN_Pos2)) {
	    return RoomType.MAIN;
	} else if (LocationTool.isIn(PRACTICE_Pos1, loc, PRACTICE_Pos2)) {
	    return RoomType.PRACTICE;
	} else if (LocationTool.isIn(MINIGAME_Pos1, loc, MINIGAME_Pos2)) {
	    return RoomType.MINI_GAME;
	} else if (LocationTool.isIn(FUN_Pos1, loc, FUN_Pos2)) {
	    return RoomType.FUN;
	}

	return null;
    }

    public static Location getRoomSpawnLocation(RoomType roomType) {
	switch (roomType) {
	case MAIN:
	    return MAIN_SPAWN;
	case PRACTICE:
	    return PRACTICE_SPAWN;
	case MINI_GAME:
	    return MINIGAME_SPAWN;
	case FUN:
	    return FUN_SPAWN;
	default:
	    return null;
	}
    }

}
