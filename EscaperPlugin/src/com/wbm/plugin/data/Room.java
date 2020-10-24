package com.wbm.plugin.data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

public class Room implements Serializable
{
	private static final long serialVersionUID=1L;

	// 서버 main룸 위치
//	public static Location mainRoomLoc1;
//	public static Location mainRoomLoc2;
	
	// room member 
	private String title;
	private String maker;
//	Room의 List<Block> 이 null이면 모두 Block.Air로 처리
//	blocks안에 있는 Block들의 위치정보는 사용하면 안됨 (정해진 Room에 순서대로 block들이 정해지는것이기 때문)
	private List<BlockData> blocks;
	private int challengingCount;
	private int clearCount;
	private LocalDateTime birth;
	private int voted;
	// min 단위
	// avgDurationTime = 
	// ((avgDurationTime * clearCount) + new DurationTime )/ challengingCount + 1
	private double avgDurationTime;
	
	public Room(String title
	, String maker
	, List<BlockData> blocks
	, LocalDateTime birth) 
	{
		this.title = title;
		this.maker = maker;
		this.blocks = blocks;
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

	public List<BlockData> getBlocks()
	{
		return blocks;
	}
	public void setBlocks(List<BlockData> blocks)
	{
		this.blocks=blocks;
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
		
		this.avgDurationTime = allDurationTime;
	}

	@Override
	public String toString()
	{
		return "Room \n[title="+title+", \nmaker="+maker+", \nchallengingCount="+challengingCount
				+", \nclearCount="+clearCount+", \nbirth="+birth+", \nvoted="+voted+ ", \navgDurationTime="+avgDurationTime+"]";
	}
	
	
	
}
