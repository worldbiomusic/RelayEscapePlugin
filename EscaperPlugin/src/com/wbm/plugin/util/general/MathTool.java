package com.wbm.plugin.util.general;

public class MathTool
{
//	public static isBetween(int a, int b,int c) {
//		
//	}
	
	// 경계포함
	public static boolean isIn(double a, double target, double b) {
		double big = a, small = b;
		
		if(a < b) {
			big = b;
			small = a;
		}
		
		if(small <= target && target <= big) {
			return true;
		}
		
		return false;
	}
	
	// 경계포함 x
	public static boolean isBetween(double a, double target, double b) {
		double big = a, small = b;
		
		if(a < b) {
			big = b;
			small = a;
		}
		
		if(small < target && target < big) {
			return true;
		}
		
		return false;
	}
	
	public static int getDiff(int a, int b) {
		return Math.abs(a - b);
	}
	
	public static int getSmaller(int a, int b) {
		if(a < b) {
			return a;
		} else {
			return b;
		}
	}
	
	public static int getBigger(int a, int b) {
		if(a > b) {
			return a;
		} else {
			return b;
		}
	}
}























