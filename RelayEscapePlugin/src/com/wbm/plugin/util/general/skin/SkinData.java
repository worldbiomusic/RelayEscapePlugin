package com.wbm.plugin.util.general.skin;

import java.io.Serializable;

public class SkinData implements Serializable
{
	private static final long serialVersionUID=1L;
	String name;
	String texture;
	String signature;

	public SkinData(String name, String texture, String signature)
	{
		this.name = name;
		this.texture=texture;
		this.signature=signature;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name=name;
	}

	public String getTexture()
	{
		return texture;
	}

	public void setTexture(String texture)
	{
		this.texture=texture;
	}

	public String getSignature()
	{
		return signature;
	}

	public void setSignature(String signature)
	{
		this.signature=signature;
	}
	
	@Override
	public String toString()
	{
		return "name: " + this.name + "\ntexture: " + this.texture
				+ "\nsignature: " + this.signature;
	}
}
