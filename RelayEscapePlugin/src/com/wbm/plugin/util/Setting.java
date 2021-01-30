package com.wbm.plugin.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class Setting {
    /*
     * 서버의 모든 설정 관리하는 클래스 위치, 이름(템, 지역 등) 등등
     */
    public static final boolean DEBUG = false;

    public static final World world = Bukkit.getWorld("world");
    public static final Location STDLOC = new Location(Setting.world, 0, 0, 0);

    // 기준 위치 시스템 STDLOC 기준
    /*
     * [LIST]============ RoomLocker RoomLocation MiniGame MiniGameLocation
     * SpawnLocationTool StageManager에 registerStage호출하는 곳(ex. Main)
     */

    /*
     * 기준좌표: (0, 0, 0) = A
     * 
     * main room: (1, 4, 1) ~ (10, 50, 10) ex.(A+(1, 4, 1)) ~ (A+(10, 50, 10))
     * 
     * practice room: (21, 4, 21) ~ (30, 50, 30)
     * 
     * 
     * locker(main room): (9, 5, 11) (10, 5, 11) (11, 5, 11) (11, 5, 10) (11, 5, 9)
     * 
     * spawn: (9.5, 4, 5.5)
     * 
     * respawn: (9.5, 4, 5.5)
     * 
     * lobby: (16, 4, 16)
     */

    public static final String DISCORD_CH_SERVER_CHAT = "server-chat";

    public static final int MAIN_ROOM_CLEAR_TOKEN = Bukkit.getOnlinePlayers().size() * 1;
    public static final int PRACTICE_ROOM_CLEAR_TOKEN = Bukkit.getOnlinePlayers().size() * 1;

    public static final int MinimunMakingTime = 60;
    
    public static final int DATA_SAVE_DELAY = 20 * 60 * 60;

    public static Location getAbsoluteLocation(Location loc) {
	return loc;
    }

    public static Location getAbsoluteLocation(double x, double y, double z) {
	return new Location(Setting.world, x, y, z);
    }

    public static Location getAbsoluteLocation(double x, double y, double z, float yaw, float pitch) {
	return new Location(Setting.world, x, y, z, yaw, pitch);
    }

    public static Location getLoationFromSTDLOC(Location loc) {
	return loc.clone().add(Setting.STDLOC);
    }

    public static Location getLoationFromSTDLOC(double x, double y, double z) {
	return new Location(Setting.world, x, y, z).clone().add(Setting.STDLOC);
    }

    public static Location getLoationFromSTDLOC(double x, double y, double z, float yaw, float pitch) {
	return new Location(Setting.world, x, y, z, yaw, pitch).clone().add(Setting.STDLOC);
    }

    public static final String CoolDown_Subject_CHAT = "CHAT";
    public static final String CoolDown_Subject_CMD_ROOM = "CMD_ROOM";

    // RelayTime time amount
    public static final int WAITING_TIME = 30;
    public static final int MAKING_TIME = 60 * 5;
    public static final int TESTING_TIME = 60 * 10;
    public static final int CHALLENGING_TIME = 60 * 10;

}

//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
