package com.wbm.plugin.util.enums;

import org.bukkit.Location;

import com.wbm.plugin.util.Setting;

public enum MiniGameType
{
	/*
	 * MiniGame에 대한 Enum과 roomLocation을 가지고 있음
	 */
	FIND_THE_RED(Setting.getLoationFromSTDLOC(9, 8, 29, 90, 0)),
	PAINTER(Setting.getLoationFromSTDLOC(9, 8, 23, 90, 0)),
	COPY_BLOCK(Setting.getLoationFromSTDLOC(0, 0, 0));
	
	private Location roomLoc;
	
	MiniGameType(Location roomLoc) {
		this.roomLoc = roomLoc;
	}
	
	public Location getRoomLocation() {
		return this.roomLoc;
	}
	
	public static Location getRoomLocation(MiniGameType game) {
		return game.roomLoc;
	}
}
