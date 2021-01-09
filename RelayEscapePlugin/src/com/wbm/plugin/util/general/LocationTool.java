package com.wbm.plugin.util.general;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;

import com.wbm.plugin.util.Setting;

public class LocationTool {

    // 경계포함
    public static boolean isIn(Location pos1, Location target, Location pos2) {
	double pos1X = pos1.getX();
	double pos1Y = pos1.getY();
	double pos1Z = pos1.getZ();

	double pos2X = pos2.getX();
	double pos2Y = pos2.getY();
	double pos2Z = pos2.getZ();

	/*
	 * 마크에서 위치검사할때 1 ~ 5이면 1.0 ~ 5.999까지 되야 하기 때문에
	 * 
	 * LocationTool만 큰수에 1를 더해줘서 -0.001빼줘서 해당 수의 가장 큰값까지 검사
	 */
	double smallXPos = Math.min(pos1X, pos2X);
	double smallYPos = Math.min(pos1Y, pos2Y);
	double smallZPos = Math.min(pos1Z, pos2Z);

	double bigXPos = Math.floor((Math.max(pos1X, pos2X) + 1)) - 0.001;
	double bigYPos = Math.floor((Math.max(pos1Y, pos2Y) + 1)) - 0.001;
	double bigZPos = Math.floor((Math.max(pos1Z, pos2Z) + 1)) - 0.001;

	double targetX = target.getX();
	double targetY = target.getY();
	double targetZ = target.getZ();

	if (MathTool.isIn(smallXPos, targetX, bigXPos) && MathTool.isIn(smallYPos, targetY, bigYPos)
		&& MathTool.isIn(smallZPos, targetZ, bigZPos)) {
	    return true;
	}

	return false;
    }

    // 경계포함 x
    public static boolean isBetween(Location pos1, Location target, Location pos2) {
	double pos1X = pos1.getX();
	double pos1Y = pos1.getY();
	double pos1Z = pos1.getZ();

	double pos2X = pos2.getX();
	double pos2Y = pos2.getY();
	double pos2Z = pos2.getZ();

	double targetX = target.getX();
	double targetY = target.getY();
	double targetZ = target.getZ();

	BroadcastTool.printConsoleMessage(pos1X + " " + targetX + " " + pos2X);
	BroadcastTool.printConsoleMessage(pos1Y + " " + targetY + " " + pos2Y);
	BroadcastTool.printConsoleMessage(pos1Z + " " + targetZ + " " + pos2Z);

	if (MathTool.isBetween(pos1X, targetX, pos2X) && MathTool.isBetween(pos1Y, targetY, pos2Y)
		&& MathTool.isBetween(pos1Z, targetZ, pos2Z)) {
	    return true;
	}

	return false;
    }

    public static int getAreaBlockCount(Location loc1, Location loc2) {
	int dx = MathTool.getDiff((int) loc1.getX(), (int) loc2.getX());
	int dy = MathTool.getDiff((int) loc1.getY(), (int) loc2.getY());
	int dz = MathTool.getDiff((int) loc1.getZ(), (int) loc2.getZ());

	// +1하는 이유: 만약 (1,1) ~ (3,3) 면적의 블럭을 지정하면 총 9개의 블럭을 가리키는것인데
	// 위에서 dx, dy, dz를 구할때 차이를 구하므로 3-1 = 2 즉 2칸만을 의미하게 되서 +1을 해줌
	return (dx + 1) * (dy + 1) * (dz + 1);
    }
    
    public static void letEntityOnGround(Entity e) {
	Location eLoc = e.getLocation();
	int  y = (int) eLoc.getY();
	
	Location loc = Setting.getAbsoluteLocation(eLoc.getX(), y, eLoc.getZ());
	for(int i = 0; i <= y; i++) {
	    if(loc.clone().subtract(0,i,0).getBlock().getType() != Material.AIR) {
		TeleportTool.tp(e, loc);
		System.out.println("LOC: "+ loc);
		break;
	    }
	}
    }
}












