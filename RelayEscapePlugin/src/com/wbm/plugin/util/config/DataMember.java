package com.wbm.plugin.util.config;

public interface DataMember
{
	public void installData(Object obj);
	
	public Object getData();
	
	public String getDataMemberName();
}
