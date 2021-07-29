package com.wbm.plugin.util.data.serial;

import java.io.Serializable;

public interface SerialDataMember extends Serializable
{
	public void installData(Object obj);
	
	public Object getData();
	
	public String getDataMemberName();
}
