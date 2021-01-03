package com.wbm.plugin.data;

import org.bukkit.Location;

import com.wbm.plugin.util.Setting;
import com.wbm.plugin.util.enums.MiniGameType;
import com.wbm.plugin.util.general.LocationTool;

public class MiniGameLocation {
    /*
     * MiniGame 에서 상호작용되는 블럭들만의 위치를 관리하는 클래스
     * 
     * 또는
     * 
     * MiniGame에서 플레이어가 상호작용하는 공간 관리하는 클래스
     */
    // FIND_THE_RED============
    public static final Location FIND_THE_RED_POS1 = new Location(Setting.world, -73, 5, 120);
    public static final Location FIND_THE_RED_POS2 = new Location(Setting.world, -76, 5, 123);

    // PAINTER==============
    public static final Location PAINTER_POS1 = new Location(Setting.world, -82, 4, 123);
    public static final Location PAINTER_POS2 = new Location(Setting.world, -85, 6, 123);

    // FIND_THE_YELLOW============
    public static final Location FIND_THE_YELLOW_POS1 = new Location(Setting.world, -73, 9, 120);
    public static final Location FIND_THE_YELLOW_POS2 = new Location(Setting.world, -76, 9, 123);

    // FIND_THE_BLUE============
    public static final Location FIND_THE_BLUE_POS1 = new Location(Setting.world, -73, 13, 120);
    public static final Location FIND_THE_BLUE_POS2 = new Location(Setting.world, -73, 13, 123);

    // BATTLE_TOWN============
    public static final Location BATTLE_TOWN_POS1 = new Location(Setting.world, -36, 4, 203);
    public static final Location BATTLE_TOWN_POS2 = new Location(Setting.world, 55, 26, 98);

    // JUMP_MAP============
    public static final Location JUMP_MAP_POS1 = new Location(Setting.world, -37, 31, 224);
    public static final Location JUMP_MAP_POS2 = new Location(Setting.world, 35, 3, 303);

    public static MiniGameType getMiniGameWithLocation(Location loc) {
	if (LocationTool.isIn(FIND_THE_RED_POS1, loc, FIND_THE_RED_POS2)) {
	    return MiniGameType.FIND_THE_RED;
	} else if (LocationTool.isIn(PAINTER_POS1, loc, PAINTER_POS2)) {
	    return MiniGameType.PAINTER;
	} else if (LocationTool.isIn(FIND_THE_YELLOW_POS1, loc, FIND_THE_YELLOW_POS2)) {
	    return MiniGameType.FIND_THE_YELLOW;
	} else if(LocationTool.isIn(FIND_THE_BLUE_POS1, loc, FIND_THE_BLUE_POS2)) {
	    return MiniGameType.FIND_THE_BLUE;
	} else if(LocationTool.isIn(BATTLE_TOWN_POS1, loc, BATTLE_TOWN_POS2)) {
	    return MiniGameType.BATTLE_TOWN;
	}  else if(LocationTool.isIn(JUMP_MAP_POS1, loc, JUMP_MAP_POS2)) {
	    return MiniGameType.JUMP_MAP;
	} else {
	    return null;
	}
    }

    public static int getGameBlockCount(MiniGameType gameType) {
	if (gameType == MiniGameType.FIND_THE_RED) {
	    return LocationTool.getAreaBlockCount(FIND_THE_RED_POS1, FIND_THE_RED_POS2);
	} else if (gameType == MiniGameType.PAINTER) {
	    return LocationTool.getAreaBlockCount(PAINTER_POS1, PAINTER_POS2);
	} else if (gameType == MiniGameType.FIND_THE_YELLOW) {
	    return LocationTool.getAreaBlockCount(FIND_THE_YELLOW_POS1, FIND_THE_YELLOW_POS2);
	} else { // if(gameType == MiniGameType.FIND_THE_BLUE) {
	    return LocationTool.getAreaBlockCount(FIND_THE_BLUE_POS1, FIND_THE_BLUE_POS2);
	}
    }
}
