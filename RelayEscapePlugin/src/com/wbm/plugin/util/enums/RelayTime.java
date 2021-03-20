package com.wbm.plugin.util.enums;

import com.wbm.plugin.util.Setting;

public enum RelayTime
{
//	MAKING(10 * 2),
//	TESTING(5 * 2),
//	CHALLENGING(30),
//	WAITING(5 * 2);
	
    // 단위: 초
    	웨이팅(Setting.WAITING_TIME),
	메이킹(Setting.MAKING_TIME),
	테스팅(Setting.TESTING_TIME),
	챌린징(Setting.CHALLENGING_TIME);
	
	private final int time;
	
	RelayTime(int time) {
		this.time = time;
	}
	
	public int getAmount() {
		return this.time;
	}
	
	public static RelayTime getNextTime(RelayTime time) {
		if(time == RelayTime.메이킹) {
			return RelayTime.테스팅;
		} else if(time == RelayTime.테스팅) {
			return RelayTime.챌린징;
		} else if(time == RelayTime.챌린징) {
			return RelayTime.웨이팅;
		} else // if(time == RelayTime.WAITING) {
			return RelayTime.메이킹;
	}
}
