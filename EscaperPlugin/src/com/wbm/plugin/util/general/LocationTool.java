package com.wbm.plugin.util.general;

import org.bukkit.Location;

public class LocationTool
{
	// 경계포함
	public static boolean isIn(Location pos1, Location target, Location pos2) {
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
		
		
		if(MathTool.isIn(pos1X, targetX, pos2X) 
				&& MathTool.isIn(pos1Y, targetY, pos2Y)
				&& MathTool.isIn(pos1Z, targetZ, pos2Z)) {
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
		
		
		if(MathTool.isBetween(pos1X, targetX, pos2X) 
				&& MathTool.isBetween(pos1Y, targetY, pos2Y)
				&& MathTool.isBetween(pos1Z, targetZ, pos2Z)) {
			return true;
		}
		
		return false;
	}
}
