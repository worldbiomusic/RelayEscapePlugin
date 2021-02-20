package com.wbm.plugin.util.general;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import com.wbm.plugin.util.Setting;

public class BlockTool {
	public static void setBlockWithMaterial(Location pos1, Location pos2, List<Material> mats) {

		int pos1X = (int) pos1.getX();
		int pos2X = (int) pos2.getX();
		int pos1Y = (int) pos1.getY();
		int pos2Y = (int) pos2.getY();
		int pos1Z = (int) pos1.getZ();
		int pos2Z = (int) pos2.getZ();

		// get difference
		int dx = MathTool.getDiff(pos1X, pos2X);
		int dy = MathTool.getDiff(pos1Y, pos2Y);
		int dz = MathTool.getDiff(pos1Z, pos2Z);

		// get smaller x, y, z
		int smallX = Math.min(pos1X, pos2X);
		int smallY = Math.min(pos1Y, pos2Y);
		int smallZ = Math.min(pos1Z, pos2Z);

		int index = 0;
		/*
		 * for문에서 <=dx인 이유: 만약 (1,1) ~ (3,3) 면적의 블럭을 지정하면 총 9개의 블럭을 가리키는것인데 위에서 dx, dy,
		 * dz를 구할때 차이를 구하므로 3-1 = 2 즉 2칸만을 의미하게 되서 <=을 해줘서 3칸을 채우게 함
		 */
		for (int y = 0; y <= dy; y++) {
			for (int z = 0; z <= dz; z++) {
				for (int x = 0; x <= dx; x++) {
					Location loc = new Location(Setting.world, smallX, smallY, smallZ);
					loc.add(x, y, z);

					Material mat = mats.get(index);
					// set type
					loc.getBlock().setType(mat);

					index++;
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	public static void setBlockWithItemStack(Location pos1, Location pos2, List<ItemStack> items) {
		int pos1X = (int) pos1.getX();
		int pos2X = (int) pos2.getX();
		int pos1Y = (int) pos1.getY();
		int pos2Y = (int) pos2.getY();
		int pos1Z = (int) pos1.getZ();
		int pos2Z = (int) pos2.getZ();

		// get difference
		int dx = MathTool.getDiff(pos1X, pos2X);
		int dy = MathTool.getDiff(pos1Y, pos2Y);
		int dz = MathTool.getDiff(pos1Z, pos2Z);

		// get smaller x, y, z
		int smallX = Math.min(pos1X, pos2X);
		int smallY = Math.min(pos1Y, pos2Y);
		int smallZ = Math.min(pos1Z, pos2Z);

		int index = 0;
		/*
		 * for문에서 <=dx인 이유: 만약 (1,1) ~ (3,3) 면적의 블럭을 지정하면 총 9개의 블럭을 가리키는것인데 위에서 dx, dy,
		 * dz를 구할때 차이를 구하므로 3-1 = 2 즉 2칸만을 의미하게 되서 <=을 해줘서 3칸을 채우게 함
		 */
		for (int y = 0; y <= dy; y++) {
			for (int z = 0; z <= dz; z++) {
				for (int x = 0; x <= dx; x++) {
					Location loc = new Location(Setting.world, smallX, smallY, smallZ);
					loc.add(x, y, z);

					ItemStack item = items.get(index);
					Material mat = item.getType();
					Byte data = item.getData().getData();
					// set type
					loc.getBlock().setType(mat);
					// set data
					loc.getBlock().setData(data);

					index++;
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	public static boolean isAllSameBlock(Location pos1, Location pos2) {
		// 전체와 비교할 기준 ItemStack 가져오기
		ItemStack STDItemStack = ItemStackTool.item(pos1.getBlock().getType(), pos1.getBlock().getData());
		return isAllSameBlockWithItemStack(pos1, pos2, STDItemStack);
	}

	@SuppressWarnings("deprecation")
	public static boolean isAllSameBlockWithItemStack(Location pos1, Location pos2, ItemStack targetItem) {
		// 전체와 비교할 ItemStack 가져오기
		int pos1X = (int) pos1.getX();
		int pos2X = (int) pos2.getX();
		int pos1Y = (int) pos1.getY();
		int pos2Y = (int) pos2.getY();
		int pos1Z = (int) pos1.getZ();
		int pos2Z = (int) pos2.getZ();

		// get difference
		int dx = MathTool.getDiff(pos1X, pos2X);
		int dy = MathTool.getDiff(pos1Y, pos2Y);
		int dz = MathTool.getDiff(pos1Z, pos2Z);

		// get smaller x, y, z
		int smallX = Math.min(pos1X, pos2X);
		int smallY = Math.min(pos1Y, pos2Y);
		int smallZ = Math.min(pos1Z, pos2Z);

		/*
		 * for문에서 <=dx인 이유: 만약 (1,1) ~ (3,3) 면적의 블럭을 지정하면 총 9개의 블럭을 가리키는것인데 위에서 dx, dy,
		 * dz를 구할때 차이를 구하므로 3-1 = 2 즉 2칸만을 의미하게 되서 <=을 해줘서 3칸을 채우게 함
		 */
		for (int y = 0; y <= dy; y++) {
			for (int z = 0; z <= dz; z++) {
				for (int x = 0; x <= dx; x++) {
					Location loc = new Location(Setting.world, smallX, smallY, smallZ);
					loc.add(x, y, z);

					// 비교
					Block locB = loc.getBlock();
					ItemStack locItemStack = ItemStackTool.item(locB.getType(), locB.getData());

					if (!(ItemStackTool.isSameWithMaterialNData(targetItem, locItemStack))) {
						// 하나라도 같지 않으면 false
						return false;
					}
				}
			}
		}

		// 모두 같으면 true
		return true;
	}
}
