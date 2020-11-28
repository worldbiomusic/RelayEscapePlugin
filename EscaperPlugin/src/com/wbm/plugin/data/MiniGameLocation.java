package com.wbm.plugin.data;

import org.bukkit.Location;

import com.wbm.plugin.util.Setting;
import com.wbm.plugin.util.enums.MiniGameType;
import com.wbm.plugin.util.general.LocationTool;

public class MiniGameLocation
{
	/*
	 * MiniGame 블럭들의 위치를 관리하는 클래스 
	 */
	// WHACK_A_MOLE============
	public static final Location FIND_THE_RED_POS1 = Setting.getLoationFromSTDLOC(1, 10, 30);
	public static final Location FIND_THE_RED_POS2 = Setting.getLoationFromSTDLOC(4, 10, 27);
	
	// PAINTER==============
	public static final Location PAINTER_POS1 = Setting.getLoationFromSTDLOC(1, 10, 24);
	public static final Location PAINTER_POS2 = Setting.getLoationFromSTDLOC(1, 8, 21);
	
	
	public static MiniGameType getMiniGameWithLocation(Location loc) {
		if(LocationTool.isIn(FIND_THE_RED_POS1, loc, FIND_THE_RED_POS2)) {
			return MiniGameType.FIND_THE_RED;
		} else { // if(LocationTool.isIn(PAINTER_POS1, loc, PAINTER_POS2)) {
			return MiniGameType.PAINTER;
		}
	}
	
	public static int getGameBlockCount(MiniGameType gameType) {
		if(gameType == MiniGameType.FIND_THE_RED) {
			return LocationTool.getAreaBlockCount(FIND_THE_RED_POS1, FIND_THE_RED_POS2);
		} else { // if(gameType == MiniGameType.FIND_THE_RED) {
			return LocationTool.getAreaBlockCount(PAINTER_POS1, PAINTER_POS2);
		} 
	}
}




















