package coolsquid.properties.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.google.common.base.Preconditions;

public class EntityData {

	private static final Map<Class<? extends EntityLivingBase>, EntityData> ENTITY_DATA = new HashMap<>();
	private static final EntityData EMPTY = new EntityData();

	public List<ItemStack> dropsToAdd;
	public Set<Item> dropsToRemove;
	public boolean clearDrops = false;

	public double speed = -1;
	public double maxHealth = -1;
	public double followRange = -1;
	public double knockbackResistance = -1;
	public double armor = -1;
	public double armorThoughness = -1;

	public EntityData() {
		this(new ArrayList<>(), new HashSet<>());
	}

	public EntityData(List<ItemStack> dropsToAdd, Set<Item> dropsToRemove) {
		this.dropsToAdd = Preconditions.checkNotNull(dropsToAdd);
		this.dropsToRemove = Preconditions.checkNotNull(dropsToRemove);
	}

	@Nonnull
	public static EntityData getEntityData(Class<? extends EntityLivingBase> type) {
		EntityData data = ENTITY_DATA.get(type);
		if (data == null) {
			data = new EntityData();
			ENTITY_DATA.put(type, data);
		}
		return data;
	}

	@Nullable
	public static EntityData getEntityDataOrEmpty(Class<? extends EntityLivingBase> type) {
		EntityData data = ENTITY_DATA.get(type);
		return data == null ? EMPTY : data;
	}
}