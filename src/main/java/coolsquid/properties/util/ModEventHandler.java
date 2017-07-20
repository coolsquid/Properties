
package coolsquid.properties.util;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.terraingen.BiomeEvent;
import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

public class ModEventHandler {

	public static final ListMultimap<Item, String> ITEM_TOOLTIPS = ArrayListMultimap.create();

	@SubscribeEvent(priority = EventPriority.LOW)
	public void addDrops(BlockEvent.HarvestDropsEvent event) {
		BlockData data = BlockData.getBlockData(event.getState().getBlock());
		if (data.clearDrops) {
			event.getDrops().clear();
			return;
		}
		event.getDrops().removeIf((stack) -> data.dropsToRemove.contains(stack.getItem()));
		for (ItemStack e : data.dropsToAdd) {
			event.getDrops().add(e.copy());
		}
	}

	@SubscribeEvent(priority = EventPriority.LOW)
	public void addDrops(LivingDropsEvent event) {
		EntityData data = EntityData.getEntityDataOrEmpty(event.getEntityLiving().getClass());
		if (data.clearDrops) {
			event.getDrops().clear();
		}
		event.getDrops().removeIf((stack) -> data.dropsToRemove.contains(stack.getItem().getItem()));
		for (ItemStack e : data.dropsToAdd) {
			event.getDrops().add(new EntityItem(event.getEntity().world, event.getEntity().posX, event.getEntity().posY,
					event.getEntity().posZ, e.copy()));
		}
	}

	@SubscribeEvent
	public void onLivingSpawn(EntityJoinWorldEvent event) {
		if (event.getEntity() instanceof EntityLivingBase) {
			EntityLivingBase e = (EntityLivingBase) event.getEntity();
			EntityData data = EntityData.getEntityDataOrEmpty(e.getClass());
			if (data.speed != -1) {
				IAttributeInstance a = e.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
				if (a != null) {
					a.setBaseValue(data.speed);
				}
			}
			if (data.maxHealth != -1) {
				IAttributeInstance a = e.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
				if (a != null) {
					a.setBaseValue(data.maxHealth);
				}
			}
			if (data.followRange != -1) {
				IAttributeInstance a = e.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE);
				if (a != null) {
					a.setBaseValue(data.followRange);
				}
			}
			if (data.knockbackResistance != -1) {
				IAttributeInstance a = e.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE);
				if (a != null) {
					a.setBaseValue(data.knockbackResistance);
				}
			}
			if (data.armor != -1) {
				IAttributeInstance a = e.getEntityAttribute(SharedMonsterAttributes.ARMOR);
				if (a != null) {
					a.setBaseValue(data.armor);
				}
			}
			if (data.armorThoughness != -1) {
				IAttributeInstance a = e.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS);
				if (a != null) {
					a.setBaseValue(data.armorThoughness);
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void tooltips(ItemTooltipEvent event) {
		Item item = event.getItemStack() != null ? event.getItemStack().getItem() : null;
		if (item != null) {
			for (String tooltip : ITEM_TOOLTIPS.get(item)) {
				event.getToolTip().add(tooltip);
			}
		}
	}

	@SubscribeEvent
	public void getBiomeColor(BiomeEvent.BiomeColor event) {
		BiomeData data = BiomeData.getBiomeData(event.getBiome());
		if (event instanceof BiomeEvent.GetFoliageColor && data.foliageColor != -1) {
			event.setNewColor(data.foliageColor);
		}
		if (event instanceof BiomeEvent.GetGrassColor && data.grassColor != -1) {
			event.setNewColor(data.grassColor);
		}
		if (event instanceof BiomeEvent.GetWaterColor && data.waterColor != -1) {
			event.setNewColor(data.waterColor);
		}
	}

	@SubscribeEvent
	public void getVillageBlock(BiomeEvent.GetVillageBlockID event) {
		BiomeData data = BiomeData.getBiomeData(event.getBiome());
		if (data.villageBlock != null) {
			event.setReplacement(data.villageBlock);
			event.setResult(Result.DENY);
		}
	}

	@SubscribeEvent
	public void onGenerate(OreGenEvent.GenerateMinable event) {
		BiomeData data = BiomeData.getBiomeData(event.getWorld().getBiome(event.getPos()));
		if (data.disabledMinables.contains(event.getType())) {
			event.setResult(Result.DENY);
		}
	}

	@SubscribeEvent
	public void onBlockBroken(BlockEvent.BreakEvent event) {
		BlockData data = BlockData.getBlockData(event.getState().getBlock());
		if (!data.breakable) {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void onBlockPlaced(BlockEvent.PlaceEvent event) {
		BlockData data = BlockData.getBlockData(event.getState().getBlock());
		if (!data.placeable) {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void onSourceCreation(BlockEvent.CreateFluidSourceEvent event) {
		BlockData data = BlockData.getBlockData(event.getState().getBlock());
		if (data.infiniteSource == 1) {
			event.setResult(Result.ALLOW);
		} else if (data.infiniteSource == 2) {
			event.setResult(Result.DENY);
		}
	}
}