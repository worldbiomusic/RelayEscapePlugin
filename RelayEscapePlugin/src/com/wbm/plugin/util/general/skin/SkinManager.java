package com.wbm.plugin.util.general.skin;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wbm.plugin.util.data.serial.SerialDataMember;
import com.wbm.plugin.util.general.BroadcastTool;

public class SkinManager implements SerialDataMember
{
	Map<String, SkinData> skinData;
	
	public SkinManager() {
		this.skinData = new HashMap<String, SkinData>();
	}
	
	public boolean doesExist(String name) {
		return this.skinData.containsKey(name);
	}
	
	public void addPlayerSkinData(String name) {
		SkinData skin = this.getSkinFromMojangAPI(name);
		this.skinData.put(name, skin);
	}
	
	public void removePlayerSkinData(String name) {
		if(this.doesExist(name)) {
			this.skinData.remove(name);
		}
	}
	
	public SkinData getPlayerSkinData(String name) {
			return this.skinData.get(name);
	}
	
	private SkinData getSkinFromMojangAPI(String name)
	{
		try
		{
			URL url=new URL("https://api.mojang.com/users/profiles/minecraft/"+name);
			InputStreamReader reader=new InputStreamReader(url.openStream());
			String uuid=new JsonParser().parse(reader).getAsJsonObject().get("id").getAsString();

			URL url2=new URL("https://sessionserver.mojang.com/session/minecraft/profile/"+uuid+"?unsigned=false");
			InputStreamReader reader2=new InputStreamReader(url2.openStream());

			JsonObject property=new JsonParser().parse(reader2).getAsJsonObject().get("properties").getAsJsonArray()
					.get(0).getAsJsonObject();

			String texture=property.get("value").getAsString();
			String signature=property.get("signature").getAsString();

			return new SkinData(name, texture, signature);
		}
		catch(Exception e)
		{
			/*
			 * 예외1: 플레이어가 없을때 예외2: 모장사이트에 너무 많은 request할때
			 */
			e.printStackTrace();
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void installData(Object obj)
	{
		this.skinData = (Map<String, SkinData>)obj;
		
		BroadcastTool.debug("=============SKIN DATA================");
		for(String name : this.skinData.keySet()) {
			BroadcastTool.debug(name);
		}
	}

	@Override
	public Object getData()
	{
		return this.skinData;
	}

	@Override
	public String getDataMemberName()
	{
		return "skinData";
	}
	
	
}
