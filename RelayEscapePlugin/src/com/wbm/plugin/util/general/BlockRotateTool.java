package com.wbm.plugin.util.general;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.wbm.plugin.Main;

public class BlockRotateTool {
    static Map<String, Rotationer> rotates = new HashMap<>();

    public static void registerRotationer(Rotationer r) {
	rotates.put(r.title, r);
    }

    public static void startRotating() {
	for (Rotationer r : rotates.values()) {
	    new BukkitRunnable() {

		@SuppressWarnings("deprecation")
		@Override
		public void run() {

		    List<ItemStack> items = new ArrayList<>();

		    // 다음 ITemStack 저장
		    if (r.dir == Rotationer.Direction.CLOCK) { // 시계방향 회전
			for (int i = 0; i < r.locs.size(); i++) {
			    int nextIndex = (i + 1) % r.locs.size();
			    Block b = r.locs.get(nextIndex).getBlock();
			    ItemStack item = ItemStackTool.item(b.getType(), b.getData());
			    items.add(item);
			}
		    } else if (r.dir == Rotationer.Direction.CLOCK_REVERSE) { // 반-시계방향 회전
			for (int i = 0; i < r.locs.size(); i++) {
//			    int preIndex = (i - 1) % r.locs.size();
			    int preIndex = (i - 1);
			    if (preIndex == -1) {
				preIndex = r.locs.size() - 1;
			    }

			    Block b = r.locs.get(preIndex).getBlock();
			    ItemStack item = ItemStackTool.item(b.getType(), b.getData());
			    items.add(item);
			}
		    }

		    // 다음 ItemStack을 블럭으로 불러오기
		    for (int i = 0; i < r.locs.size(); i++) {
			ItemStack item = items.get(i);
			r.locs.get(i).getBlock().setType(item.getType());
			r.locs.get(i).getBlock().setData(item.getData().getData());
		    }

		}
	    }.runTaskTimer(Main.getInstance(), 1, (long) r.delay);
	    
	    
	    
	    
	    
//	    Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), new Runnable() {
//		@SuppressWarnings("deprecation")
//		@Override
//		public void run() {
//		    List<ItemStack> items = new ArrayList<>();
//
//		    // 다음 ITemStack 저장
//		    if (r.dir == Rotationer.Direction.CLOCK) { // 시계방향 회전
//			for (int i = 0; i < r.locs.size(); i++) {
//			    int nextIndex = (i + 1) % r.locs.size();
//			    Block b = r.locs.get(nextIndex).getBlock();
//			    ItemStack item = ItemStackTool.item(b.getType(), b.getData());
//			    items.add(item);
//			}
//		    } else if (r.dir == Rotationer.Direction.CLOCK_REVERSE) { // 반-시계방향 회전
//			for (int i = 0; i < r.locs.size(); i++) {
////			    int preIndex = (i - 1) % r.locs.size();
//			    int preIndex = (i - 1);
//			    if (preIndex == -1) {
//				preIndex = r.locs.size() - 1;
//			    }
//
//			    Block b = r.locs.get(preIndex).getBlock();
//			    ItemStack item = ItemStackTool.item(b.getType(), b.getData());
//			    items.add(item);
//			}
//		    }
//
//		    // 다음 ItemStack을 블럭으로 불러오기
//		    for (int i = 0; i < r.locs.size(); i++) {
//			ItemStack item = items.get(i);
//			r.locs.get(i).getBlock().setType(item.getType());
//			r.locs.get(i).getBlock().setData(item.getData().getData());
//		    }
//		}
//	    }, 0, (long) r.delay);
//	}
	}
    }

}
