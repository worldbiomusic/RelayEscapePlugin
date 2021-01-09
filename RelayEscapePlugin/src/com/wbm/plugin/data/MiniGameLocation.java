package com.wbm.plugin.data;

public enum MiniGameLocation {
//    /*
//     * MiniGame 에서 event에 반응할 범위
//     */
//
//    // FIND_THE_RED============
//    FIND_THE_RED_LOCATION(MiniGameType.FIND_THE_RED, Setting.getAbsoluteLocation(-73, 5, 120),
//	    Setting.getAbsoluteLocation(-76, 5, 123)),
//
//    // PAINTER==============
//    PAINTER_LOCATION(MiniGameType.PAINTER, Setting.getAbsoluteLocation(-82, 4, 123),
//	    Setting.getAbsoluteLocation(-85, 6, 123)),
//
//    COPY_BLOCK_LOCATION(MiniGameType.COPY_BLOCK, Setting.getAbsoluteLocation(-82, 4, 138),
//	    Setting.getAbsoluteLocation(-85, 6, 138)),
//
//    // FIT_TOOL============
//    FIT_TOOL_LOCATION(MiniGameType.FIT_TOOL, Setting.getAbsoluteLocation(-73, 5, 135),
//	    Setting.getAbsoluteLocation(-76, 5, 138)),
//    // FIND_THE_YELLOW============
//    FIND_THE_YELLOW_LOCATION(MiniGameType.FIND_THE_YELLOW, Setting.getAbsoluteLocation(-73, 9, 120),
//	    Setting.getAbsoluteLocation(-76, 9, 123)),
//    // FIND_THE_BLUE============
//    FIND_THE_BLUE_LOCATION(MiniGameType.FIND_THE_BLUE, Setting.getAbsoluteLocation(-73, 13, 120),
//	    Setting.getAbsoluteLocation(-73, 13, 120)),
//
//    // BATTLE_TOWN============
//    BATTLE_TOWN_LOCATION(MiniGameType.BATTLE_TOWN, Setting.getAbsoluteLocation(-36, 4, 203),
//	    Setting.getAbsoluteLocation(55, 26, 98)),
//    // JUMP_MAP============
//    JUMP_MAP_LOCATION(MiniGameType.JUMP_MAP, Setting.getAbsoluteLocation(-37, 31, 224),
//	    Setting.getAbsoluteLocation(35, 3, 303));
//
//    MiniGameType gameType;
//    Location pos1, pos2;
//
//    private MiniGameLocation(MiniGameType gameType, Location pos1, Location pos2) {
//	this.gameType = gameType;
//	this.pos1 = pos1;
//	this.pos2 = pos2;
//    }
//
//    public MiniGameType getGameType() {
//	return this.gameType;
//    }
//
//    public Location getPos1() {
//	return this.pos1;
//    }
//
//    public Location getPos2() {
//	return this.pos2;
//    }
//
//    public int getGameBlockCount() {
//	return LocationTool.getAreaBlockCount(this.pos1, this.pos2);
//    }
//
//    public static MiniGameType getMiniGameWithLocation(Location loc) {
//	for (MiniGameLocation gameLocs : MiniGameLocation.values()) {
//	    if (LocationTool.isIn(gameLocs.pos1, loc, gameLocs.pos2)) {
//		System.out.println("ISIN OK! " + gameLocs.gameType.name());
//		return gameLocs.gameType;
//	    }
//	}
//	return null;
//    }
}
