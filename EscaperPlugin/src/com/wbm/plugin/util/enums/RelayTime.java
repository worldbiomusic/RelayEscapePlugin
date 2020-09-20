package com.wbm.plugin.util.enums;

public enum RelayTime
{
	MAKING(10 * 2),
	TESTING(5 * 2),
	CHALLENGING(30),
	WAITING(5 * 2);
	
	private int time;
	
	RelayTime(int time) {
		this.time = time;
	}
	
	public int getAmount() {
		return this.time;
	}
}
