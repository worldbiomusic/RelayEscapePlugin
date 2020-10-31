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
	
	public static RelayTime getNextTime(RelayTime time) {
		if(time == RelayTime.MAKING) {
			return RelayTime.TESTING;
		} else if(time == RelayTime.TESTING) {
			return RelayTime.CHALLENGING;
		} else if(time == RelayTime.CHALLENGING) {
			return RelayTime.WAITING;
		} else // if(time == RelayTime.WAITING) {
			return RelayTime.MAKING;
	}
}
