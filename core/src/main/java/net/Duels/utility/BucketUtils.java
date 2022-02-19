package net.Duels.utility;

import java.util.List;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class BucketUtils {
	
	public static boolean generates(Block from, Block to) {
		return to.isLiquid() && generates(from.getType(), to.getType());
	}

	public static boolean generates(Material from, Block to) {
		return to.isLiquid() && generates(from, to.getType());
	}

	public static boolean generates(Material from, Material to) {
		if (from == XMaterial.WATER.parseMaterial()) {
			from = XMaterial.WATER.parseMaterial();
		} else if (from == XMaterial.LAVA.parseMaterial()) {
			from = XMaterial.LAVA.parseMaterial();
		}
		if (to == XMaterial.WATER.parseMaterial()) {
			to = XMaterial.WATER.parseMaterial();
		} else if (to == XMaterial.LAVA.parseMaterial()) {
			to = XMaterial.LAVA.parseMaterial();
		}
		return from != to;
	}

	public static void trackWater(Block block, List<Block> blocks) {
		if (!blocks.contains(block)) {
			blocks.add(block);
		}
		for (int x = -1; x <= 1; ++x) {
			for (int y = -1; y <= 1; ++y) {
				for (int z = -1; z <= 1; ++z) {
					Block targetBlock = block.getLocation().add(x, y, z).getBlock();
					if ((targetBlock.getType() == XMaterial.WATER.parseMaterial()
							|| targetBlock.getType() == XMaterial.WATER.parseMaterial())
							&& !blocks.contains(targetBlock)) {
						blocks.add(targetBlock);
						trackWater(targetBlock, blocks);
					}
				}
			}
		}
	}

	public static void trackLava(Block block, List<Block> blocks) {
		if (!blocks.contains(block)) {
			blocks.add(block);
		}
		for (int x = -1; x <= 1; ++x) {
			for (int y = -1; y <= 1; ++y) {
				for (int z = -1; z <= 1; ++z) {
					Block targetBlock = block.getLocation().add(x, y, z).getBlock();
					if ((targetBlock.getType() == XMaterial.LAVA.parseMaterial()
							|| targetBlock.getType() == XMaterial.LAVA.parseMaterial())
							&& !blocks.contains(targetBlock)) {
						blocks.add(targetBlock);
						trackLava(targetBlock, blocks);
					}
				}
			}
		}
	}
	
}
