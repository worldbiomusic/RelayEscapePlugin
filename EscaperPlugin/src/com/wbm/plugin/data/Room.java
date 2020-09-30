package com.wbm.plugin.data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;

public class Room implements Serializable
{
	private static final long serialVersionUID=1L;

	// 서버 main룸 위치
	public static Location mainRoomLoc1;
	public static Location mainRoomLoc2;
	
	// room member 
	private String title;
	private String maker;
//	Room의 List<Block> 이 null이면 모두 Block.Air로 처리
//	blocks안에 있는 Block들의 위치정보는 사용하면 안됨 (정해진 Room에 순서대로 block들이 정해지는것이기 때문)
	private List<BlockData> blocks;
	private int challegeCount;
	private int clearCount;
	private LocalDateTime birth;
	
	public Room(String title
	, String maker
	, List<BlockData> blocks
	, int challegeCount
	, int clearCount
	, LocalDateTime birth) 
	{
		this.title = title;
		this.maker = maker;
		this.blocks = blocks;
		this.challegeCount = challegeCount;
		this.clearCount = clearCount;
	}
	
	public static void setMainRoomSpace(Location loc1, Location loc2) {
		Room.mainRoomLoc1 = loc1;
		Room.mainRoomLoc2 = loc2;
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
	public int getChallegeCount()
	{
		return challegeCount;
	}
	public void setChallegeCount(int challegeCount)
	{
		this.challegeCount=challegeCount;
	}
	public int getClearCount()
	{
		return clearCount;
	}
	public void setClearCount(int clearCount)
	{
		this.clearCount=clearCount;
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
	
	
	@Override
	public String toString()
	{
		return "Room [title="+title+", maker="+maker
				+", blocks="+blocks+", challegeCount="
				+challegeCount+", clearCount="
				+clearCount+", birth="+birth+"]";
	}
}
