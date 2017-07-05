package coolsquid.properties.util;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.SetMultimap;

public class ModEventHandler {

	public static final ListMultimap<Block, ItemStack> BLOCK_DROPS = ArrayListMultimap.create();
	public static final SetMultimap<Block, Item> REMOVE_BLOCK_DROPS = HashMultimap.create();
	public static final Set<Block> REMOVE_ALL_BLOCK_DROPS = new HashSet<>();

	@SubscribeEvent(priority = EventPriority.LOW)
	public void addDrops(BlockEvent.HarvestDropsEvent event) {
		if (REMOVE_ALL_BLOCK_DROPS.contains(event.getState().getBlock())) {
			event.getDrops().clear();
			return;
		}
		event.getDrops()
				.removeIf((stack) -> REMOVE_BLOCK_DROPS.get(event.getState().getBlock()).contains(stack.getItem()));
		for (ItemStack e : BLOCK_DROPS.get(event.getState().getBlock())) {
			event.getDrops().add(e.copy());
		}
	}
}