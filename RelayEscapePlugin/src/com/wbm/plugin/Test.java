package com.wbm.plugin;

import java.awt.Color;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Robot;
import java.awt.event.InputEvent;

public class Test {
    public static void main(String[] args) {
	try {
	    // 1510 25
	    // 1510 125

	    PointerInfo pt;

	    while (true) {
		Robot r = new Robot();
		
		    r.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		    r.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		
		Thread.sleep(10);
	    }

	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

    }
}
