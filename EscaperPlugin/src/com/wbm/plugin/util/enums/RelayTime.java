package com.wbm.plugin.util.enums;

import com.wbm.plugin.util.Setting;

public enum RelayTime
{
//	MAKING(10 * 2),
//	TESTING(5 * 2),
//	CHALLENGING(30),
//	WAITING(5 * 2);
	
    // 단위: 초
    	WAITING(Setting.WAITING_TIME),
	MAKING(Setting.MAKING_TIME),
	TESTING(Setting.TESTING_TIME),
	CHALLENGING(Setting.CHALLENGING_TIME);
	
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
