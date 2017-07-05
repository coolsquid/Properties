package coolsquid.properties.util;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.SetMultimap;

public class ModEventHandler {

	public static final ListMultimap<Block, ItemStack> BLOCK_DROPS = ArrayListMultimap.create();
	public static final SetMultimap<Block, Item> REMOVE_BLOCK_DROPS = HashMultimap.create();

	@SubscribeEvent
	public void addDrops(BlockEvent.HarvestDropsEvent event) {
		event.getDrops()
				.removeIf((stack) -> REMOVE_BLOCK_DROPS.get(event.getState().getBlock()).contains(stack.getItem()));
		for (ItemStack e : BLOCK_DROPS.get(event.getState().getBlock())) {
			event.getDrops().add(e.copy());
		}
	}
}