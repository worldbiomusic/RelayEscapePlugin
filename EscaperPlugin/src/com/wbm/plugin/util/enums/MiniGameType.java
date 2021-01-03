package com.wbm.plugin.util.enums;

import org.bukkit.Location;

import com.wbm.plugin.util.Setting;

public enum MiniGameType {
    /*
     * MiniGame에 대한 Enum과 roomLocation을 가지고 있음
     */
    // Solo
    FIND_THE_RED(new Location(Setting.world,-74, 4, 115, 0, 0), 5, 30, 30),
    PAINTER(new Location(Setting.world,-83, 4, 115, 0, 0), 5, 30, 30),
    COPY_BLOCK(new Location(Setting.world,0, 0, 0), 5, 30, 30),
    JUMP_MAP(new Location(Setting.world,-36, 4, 225), 5, 10, 60 * 3),
    
    // Cooperative
    FIND_THE_YELLOW(new Location(Setting.world,-74, 8, 115, 0, 0), 5, 60, 30),
    // Battle
    FIND_THE_BLUE(new Location(Setting.world,-74, 12, 115, 0, 0), 5, 30, 30),
    BATTLE_TOWN(new Location(Setting.world, 8, 5, 153, 90, 0), 5, 30, 60);

    private Location spawnLoc;
    private int fee;
    private int waitingTime;
    private int timeLimit;

    MiniGameType(Location roomLoc, int fee, int waitingTime, int timeLimit) {
	this.spawnLoc = roomLoc;
	this.fee = fee;
	this.waitingTime = waitingTime;
	this.timeLimit = timeLimit;

    }

    public Location getSpawnLocation() {
	return this.spawnLoc;
    }

    public int getFee() {
	return this.fee;
    }

    public int getWaitingTime() {
	return this.waitingTime;
    }

    public int getTimeLimit() {
	return this.timeLimit;
    }

}
