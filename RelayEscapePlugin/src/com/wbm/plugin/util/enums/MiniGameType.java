package com.wbm.plugin.util.enums;

import org.bukkit.Location;

import com.wbm.plugin.util.Setting;
import com.wbm.plugin.util.general.LocationTool;

public enum MiniGameType {
    /*
     * MiniGame에 대한 Enum과 roomLocation을 가지고 있음
     */
    // Solo
    FIND_THE_RED(Setting.getAbsoluteLocation(-73, 5, 120), Setting.getAbsoluteLocation(-76, 5, 123),
	    new Location(Setting.world, -74, 4, 115, 0, 0), 5, 10, 30, 1),
    PAINTER(Setting.getAbsoluteLocation(-82, 4, 123), Setting.getAbsoluteLocation(-85, 6, 123),
	    new Location(Setting.world, -83, 4, 115, 0, 0), 5, 10, 30, 1),
    COPY_BLOCK(Setting.getAbsoluteLocation(-73, 4, 138), Setting.getAbsoluteLocation(-76, 6, 138),
	    new Location(Setting.world, -74, 4, 130), 5, 10, 30, 1),
    JUMP_MAP(Setting.getAbsoluteLocation(-37, 31, 224), Setting.getAbsoluteLocation(35, 3, 303),
	    new Location(Setting.world, -36, 4, 225), 5, 10, 60 * 3, 1),
    FIT_TOOL(Setting.getAbsoluteLocation(-82, 4, 138), Setting.getAbsoluteLocation(-85, 6, 138),
	    new Location(Setting.world, -83, 4, 130), 5, 10, 60, 1),

    // Cooperative
    FIND_THE_YELLOW(Setting.getAbsoluteLocation(-88, 5, 120), Setting.getAbsoluteLocation(-91, 5, 123),
	    new Location(Setting.world, -89, 4, 115, 0, 0), 5, 60, 30, 4),
    // Battle
    FIND_THE_BLUE(Setting.getAbsoluteLocation(-103, 5, 120), Setting.getAbsoluteLocation(-106, 5, 123),
	    new Location(Setting.world, -104, 4, 115, 0, 0), 5, 30, 30, 4),
    BATTLE_TOWN(Setting.getAbsoluteLocation(-37, 0, 98), Setting.getAbsoluteLocation(55, 26, 203),
	    new Location(Setting.world, 8, 5, 153, 90, 0), 5, 30, 180, 10),
    MAKE_HOLE(Setting.getAbsoluteLocation(-36, 4, 203), Setting.getAbsoluteLocation(55, 26, 98),
	    new Location(Setting.world, 8, 5, 153, 90, 0), 5, 30, 180, 10),
    CRITICAL(Setting.getAbsoluteLocation(-112, 4, 114), Setting.getAbsoluteLocation(-115, 6, 122),
	    new Location(Setting.world, -113, 4, 115, 90, 0), 5, 30, 180, 4),
    BANG(Setting.getAbsoluteLocation(-103, 10, 114), Setting.getAbsoluteLocation(-106, 8, 123),
	    new Location(Setting.world, -104, 8, 115, 0, 0), 5, 30, 60, 4);

    // pos1, pos2는 이벤트에 반응하는 위치 area를 나타냄
    private Location pos1, pos2;
    // roomLoc은 tp할 지점
    private Location roomLoc;
    private int fee;
    // 게임 시작전 대기시간
    private int waitingTime;
    // 게임 지속 시간
    private int timeLimit;
    // 최대 입장 인원수
    private int maxPlayerCount;

    private MiniGameType(Location pos1, Location pos2, Location roomLoc, int fee, int waitingTime, int timeLimit,
	    int maxPlayerCount) {
	this.pos1 = pos1;
	this.pos2 = pos2;
	this.roomLoc = roomLoc;
	this.fee = fee;
	this.waitingTime = waitingTime;
	this.timeLimit = timeLimit;
	this.maxPlayerCount = maxPlayerCount;
    }

    public Location getSpawnLocation() {
	return this.roomLoc;
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

    public int getGameBlockCount() {
	return LocationTool.getAreaBlockCount(this.pos1, this.pos2);
    }

    public Location getGamePos1() {
	return this.pos1;
    }

    public Location getGamePos2() {
	return this.pos2;
    }

    public int getMaxPlayerCount() {
	return this.maxPlayerCount;
    }

    public static MiniGameType getMiniGameWithLocation(Location loc) {
	for (MiniGameType game : MiniGameType.values()) {
	    if (LocationTool.isIn(game.pos1, loc, game.pos2)) {
		return game;
	    }
	}
	return null;
    }
}
