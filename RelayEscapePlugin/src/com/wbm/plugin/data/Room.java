package com.wbm.plugin.data;

import java.io.Serializable;
// start 
// end 21
import java.time.LocalDateTime;

public class Room implements Serializable
{
	private static final long serialVersionUID=1L;

	// 서버 main룸 위치
//	public static Location mainRoomLoc1;
//	public static Location mainRoomLoc2;
	
	// room member 
	private String title;
	private String maker;
	private int challengingCount;
	private int clearCount;
	private LocalDateTime birth;
	private int voted;
	// sec 단위
	// avgDurationTime = 
	// ((avgDurationTime * clearCount) + new DurationTime ) / challengingCount + 1
	private double avgDurationTime;
	
	public Room(String title
	, String maker
	, LocalDateTime birth) 
	{
		this.title = title;
		this.maker = maker;
		this.birth = birth;
		this.challengingCount = 0;
		this.clearCount = 0;
		this.voted = 0;
		this.avgDurationTime = 0;
	}
	
	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title=title;
	}


	public String getMaker()
	{
		return maker;
	}

	public void setMaker(String maker)
	{
		this.maker=maker;
	}

	public int getChallengingCount()
	{
		return challengingCount;
	}

	public void setChallengingCount(int challengingCount)
	{
		this.challengingCount=challengingCount;
	}
	
	public void addChallengingCount(int challengingCount) {
		this.challengingCount += challengingCount;
	}
	
	public void subChallengingCount(int challengingCount) {
		this.challengingCount -= challengingCount;
	}

	public int getClearCount()
	{
		return clearCount;
	}

	public void setClearCount(int clearCount)
	{
		this.clearCount=clearCount;
	}
	
	public void addClearCount(int clearCount) {
		this.clearCount += clearCount;
	}
	
	public void subClearCount(int clearCount) {
		this.clearCount -= clearCount;
	}

	public int getVoted()
	{
		return voted;
	}

	public void setVoted(int voted)
	{
		this.voted=voted;
	}
	
	public void addVoted(int voted) {
		this.voted += voted;
	}
	
	public void subVoted(int voted) {
		this.voted -= voted;
	}
	public LocalDateTime getBirth()
	{
		return birth;
	}
	public void setBirth(LocalDateTime birth)
	{
		this.birth=birth;
	}
	public static long getSerialversionuid()
	{
		return serialVersionUID;
	}

	public double getAvgDurationTime()
	{
		return avgDurationTime;
	}

	public void setAvgDurationTime(double avgDurationTime)
	{
		this.avgDurationTime=avgDurationTime;
	}
	
	public void addNewAvgDurationTime(double amount) {
		double allDurationTime = this.avgDurationTime * (this.clearCount-1);
		allDurationTime += amount;
		allDurationTime /= this.clearCount;
		
		// 소수점 반올림
		this.avgDurationTime = Math.round(allDurationTime);
	}

	@Override
	public String toString()
	{
		return "Room \n[title="+title+", \nmaker="+maker+
				", \nchallengingCount="+challengingCount
				+", \nclearCount="+clearCount+", \nbirth="+
				birth+", \nvoted="+voted+ ", \navgDurationTime="+avgDurationTime+"]";
	}
	
	
	
}
