package com.wbm.plugin;

import java.time.LocalDateTime;

public class Test
{
	public static void main(String[] args)
	{
		LocalDateTime today = LocalDateTime.now();
		
		System.out.println(today.getMonth());
		System.out.println(today.getMonthValue());
		System.out.println(today.getDayOfMonth());
		System.out.println(today.getHour());
		System.out.println(today.getMinute());
		System.out.println(today.getSecond());
	}
}
