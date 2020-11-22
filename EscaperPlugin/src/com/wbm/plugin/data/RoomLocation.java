package com.wbm.plugin.data;

import org.bukkit.Location;

import com.wbm.plugin.util.Setting;
import com.wbm.plugin.util.enums.RoomType;
import com.wbm.plugin.util.general.LocationTool;
import com.wbm.plugin.util.general.MathTool;

/*
 * 알아둘것!: RoomLocation은 Room의 블럭을 초기화 할 범위, 블럭 break place범위를 나타내는 범위!
 */
public class RoomLocation
{
	// main
	public static final Location MAIN_Pos1 = Setting.getLoationFromSTDLOC(1, 4, 1);
	public static final Location MAIN_Pos2 =Setting.getLoationFromSTDLOC(10, 50, 10);
	
	// practice
	public static final Location PRACTICE_Pos1 = Setting.getLoationFromSTDLOC(21, 4, 21);
	public static final Location PRACTICE_Pos2 = Setting.getLoationFromSTDLOC(30, 50, 30);
	
	// minigame
	public static final Location MINIGAME_Pos1 = Setting.getLoationFromSTDLOC(1, 4, 21);
	public static final Location MINIGAME_Pos2 = Setting.getLoationFromSTDLOC(10, 4, 30);
	
	public static int getRoomBlockCount(RoomType roomType) {
		Location pos1 = null, pos2 = null;
		if(roomType == RoomType.MAIN) {
			pos1 = MAIN_Pos1;
			pos2 = MAIN_Pos2;
		} else if(roomType == RoomType.PRACTICE) {
			pos1 = PRACTICE_Pos1;
			pos2 = PRACTICE_Pos2;
		} 
		
		int dx = MathTool.getDiff((int)pos1.getX(), (int)pos2.getX());
		int dy = MathTool.getDiff((int)pos1.getY(), (int)pos2.getY());
		int dz = MathTool.getDiff((int)pos1.getZ(), (int)pos2.getZ());
		
		// +1하는 이유: 만약 (1,1) ~ (3,3) 면적의 블럭을 지정하면 총 9개의 블럭을 가리키는것인데
		// 위에서 dx, dy, dz를 구할때 차이를 구하므로 3-1 = 2 즉 2칸만을 의미하게 되서 +1을 해줌
		return (dx+1) * (dy+1) * (dz+1);
	}
	
	public static RoomType getRoomTypeWithLocation(Location loc) {
		if(LocationTool.isIn(MAIN_Pos1, loc, MAIN_Pos2)) {
			return RoomType.MAIN;
		} else if(LocationTool.isIn(PRACTICE_Pos1, loc, PRACTICE_Pos2)) {
			return RoomType.PRACTICE;
		} else if(LocationTool.isIn(MINIGAME_Pos1, loc, MINIGAME_Pos2)) {
			return RoomType.MINI_GAME;
		}
		
		return null;
	}
}
