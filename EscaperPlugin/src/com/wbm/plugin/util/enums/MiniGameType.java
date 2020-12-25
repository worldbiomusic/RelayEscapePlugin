package com.wbm.plugin.util.enums;

import org.bukkit.Location;

import com.wbm.plugin.util.Setting;

public enum MiniGameType {
    /*
     * MiniGame에 대한 Enum과 roomLocation을 가지고 있음
     */
    // Solo
    FIND_THE_RED(Setting.getLoationFromSTDLOC(9, 8, 29, 90, 0), 5, 30),
    PAINTER(Setting.getLoationFromSTDLOC(9, 8, 23, 90, 0), 5, 30),
    COPY_BLOCK(Setting.getLoationFromSTDLOC(0, 0, 0), 5, 30),
    // Cooperative
    FIND_THE_YELLOW(Setting.getLoationFromSTDLOC(9, 12, 29, 90, 0), 5, 30),
    // Battle
    FIND_THE_BLUE(Setting.getLoationFromSTDLOC(9, 16, 29, 90, 0), 5, 30),
    BATTLE_TOWN(new Location(Setting.world, 8,5,153, 90, 0), 5, 60);

    private Location roomLoc;
    private int fee;
    private int timeLimit;

    MiniGameType(Location roomLoc, int fee, int timeLimit) {
	this.roomLoc = roomLoc;
	this.fee = fee;
	this.timeLimit = timeLimit;
	
    }

    public Location getRoomLocation() {
	return this.roomLoc;
    }

    public int getFee() {
	return this.fee;
    }

    public int getTimeLimit() {
	return this.timeLimit;
    }
}
