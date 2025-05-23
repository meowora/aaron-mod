package net.azureaaron.mod.mixins;

import java.util.Base64;
import java.util.Collection;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.properties.Property;

import net.azureaaron.mod.Main;
import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.features.Dragons;
import net.azureaaron.mod.utils.Cache;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PlayerHeadItem;
import net.minecraft.world.World;

@Mixin(EnderDragonEntity.class)
public class EnderDragonEntityMixin extends MobEntity implements Monster {
	@Unique
	private static final String POWER_TEX = "http://textures.minecraft.net/texture/c20ef06dd60499766ac8ce15d2bea41d2813fe55718864b52dc41cbaae1ea913";
	@Unique
	private static final String FLAME_TEX = "http://textures.minecraft.net/texture/aace6bb3aa4ccac031168202f6d4532597bcac6351059abd9d10b28610493aeb";
	@Unique
	private static final String APEX_TEX = "http://textures.minecraft.net/texture/816f0073c58703d8d41e55e0a3abb042b73f8c105bc41c2f02ffe33f0383cf0a";
	@Unique
	private static final String ICE_TEX = "http://textures.minecraft.net/texture/e4e71671db5f69d2c46a0d72766b249c1236d726782c00a0e22668df5772d4b9";
	@Unique
	private static final String SOUL_TEX = "http://textures.minecraft.net/texture/cad8cc982786fb4d40b0b6e64a41f0d9736f9c26affb898f4a7faea88ccf8997";

	@Unique
	private long lastM7DragTickTime = 0L;
	@Unique
	private Dragons m7DragonType = null;

	protected EnderDragonEntityMixin(EntityType<? extends MobEntity> entityType, World world) {
		super(entityType, world);
	}

	@Unique
	private void tickM7DragonColour() {
		if (this.m7DragonType == null && this.lastM7DragTickTime + 250L < System.currentTimeMillis()) {
			this.lastM7DragTickTime = System.currentTimeMillis();
			ClientWorld world = MinecraftClient.getInstance().world;

			try {
				if (world != null) {
					List<ArmorStandEntity> armourStands = world.getEntitiesByClass(ArmorStandEntity.class, this.calculateBoundingBox(), e -> e.getEquippedStack(EquipmentSlot.HEAD).getItem() instanceof PlayerHeadItem);

					for (ArmorStandEntity armourStand : armourStands) {
		    			ItemStack head = armourStand.getEquippedStack(EquipmentSlot.HEAD);
		    			ProfileComponent profile = head.get(DataComponentTypes.PROFILE);

		    			if (profile != null && !head.isEmpty()) {
		    				Collection<Property> properties = profile.properties().get("textures");

		    				for (Property property : properties) {
		    					String value = property.value();
		    					JsonObject texObj = JsonParser.parseString(new String(Base64.getDecoder().decode(value))).getAsJsonObject();
		    					String tex = texObj.get("textures").getAsJsonObject().get("SKIN").getAsJsonObject().get("url").getAsString();

		    					Dragons dragon = this.m7DragonType = switch (tex) {
		    						case POWER_TEX -> Dragons.POWER;
		    						case FLAME_TEX -> Dragons.FLAME;
		    						case APEX_TEX -> Dragons.APEX;
		    						case ICE_TEX -> Dragons.ICE;
		    						case SOUL_TEX -> Dragons.SOUL;

		    						default -> null;
		    					};

		    					if (dragon != null) return;
		    				}
		    			}
					}
				}
			} catch (Exception e) {
				Main.LOGGER.error("[Aaron's Mod] Encountered an error while determining the dragon's colour! ", e);
			}
		}
	}

	@Override
	public int getTeamColorValue() {
		if (Cache.inM7Phase5) {
			this.tickM7DragonColour();

			if (this.m7DragonType != null) return this.m7DragonType.colour;
		}

		return super.getTeamColorValue();
	}

	@Override
	public boolean isGlowing() {
		return (Cache.inM7Phase5 && AaronModConfigManager.get().skyblock.m7.glowingDragons) ? true : super.isGlowing();
	}
}
