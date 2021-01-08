package com.wbm.plugin.util.general;

import java.util.List;

import org.bukkit.Location;

public class Rotationer {
    public static enum Direction {
	CLOCK, CLOCK_REVERSE;
    }

    double delay;
    String title;
    List<Location> locs;
    Direction dir;

    public Rotationer(String title, double delay, List<Location> locs, Direction dir) {
	this.title = title;
	this.delay = delay;
	this.locs = locs;
	this.dir = dir;
    }

}
