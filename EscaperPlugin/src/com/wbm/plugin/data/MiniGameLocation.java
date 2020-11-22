package com.wbm.plugin.data;

import org.bukkit.Location;

import com.wbm.plugin.util.Setting;

public class MiniGameLocation
{
	/*
	 * MiniGame 블럭들의 위치를 관리하는 클래스 
	 */
	// WHACK_A_MOLE============
	public static final Location WHACK_A_MOLE_POS1 = Setting.getLoationFromSTDLOC(1, 9, 30);
	public static final Location WHACK_A_MOLE_POS2 = Setting.getLoationFromSTDLOC(4, 9, 27);
	
	// PAINTER==============
	public static final Location PAINTER_POS1 = Setting.getLoationFromSTDLOC(1, 10, 24);
	public static final Location PAINTER_POS2 = Setting.getLoationFromSTDLOC(1, 8, 21);
	
	
//	public static MiniGame getMiniGameWithLocation(Location loc) {
//	}
}
