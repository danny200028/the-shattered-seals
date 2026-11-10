package com.shatteredseals;

import com.shatteredseals.world.ShatteredBiomeRegion;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import terrablender.api.Regions;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import java.util.Map;
import java.util.List;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredItem;
import terrablender.api.SurfaceRuleManager;

@Mod(ShatteredSeals.MOD_ID)
public final class ShatteredSeals {
    public static final String MOD_ID = "shatteredseals";
    public static final String VERSION = "1.0.61";
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT, MOD_ID);
    public static final DeferredHolder<SoundEvent, SoundEvent> BOSS_THEME = SOUNDS.register("music.boss",
        () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(MOD_ID, "music.boss")));
    public static final DeferredHolder<SoundEvent, SoundEvent> SANCTUARY_THEME = SOUNDS.register("music.sanctuary",
        () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(MOD_ID, "music.sanctuary")));
    public static final DeferredHolder<SoundEvent, SoundEvent> EXPLORE_THEME = SOUNDS.register("music.explore",
        () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(MOD_ID, "music.explore")));
    public static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS =
        DeferredRegister.create(Registries.ARMOR_MATERIAL, MOD_ID);
    // Four armour points, three toughness and a little knockback resistance,
    // rendered as a translucent gold-and-amethyst open crown rather than a helmet.
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> ARCHIVIST_MATERIAL =
        ARMOR_MATERIALS.register("archivist", () -> new ArmorMaterial(
            Map.of(ArmorItem.Type.HELMET, 4, ArmorItem.Type.CHESTPLATE, 0,
                ArmorItem.Type.LEGGINGS, 0, ArmorItem.Type.BOOTS, 0,
                ArmorItem.Type.BODY, 0),
            24, SoundEvents.ARMOR_EQUIP_GOLD,
            () -> Ingredient.of(Items.GOLD_INGOT),
            List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(MOD_ID, "archivists_crown"))),
            3.0F, 0.1F));
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MOD_ID);
    public static final DeferredItem<Item> SANCTUARY_KEYSTONE =
        ITEMS.registerSimpleItem("sanctuary_keystone", new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> MASK_SMUGGLER_KING =
        ITEMS.registerSimpleItem("boss_mask_smuggler_king", new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> MASK_CINDER_WARDEN =
        ITEMS.registerSimpleItem("boss_mask_cinder_warden", new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> MASK_HOLLOW_KNIGHT =
        ITEMS.registerSimpleItem("boss_mask_hollow_knight", new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> MASK_ARCANE_SEER =
        ITEMS.registerSimpleItem("boss_mask_arcane_seer", new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> MASK_SIXFOLD_GUARDIAN =
        ITEMS.registerSimpleItem("boss_mask_sixfold_guardian", new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> MASK_HOLLOW_KING =
        ITEMS.registerSimpleItem("boss_mask_hollow_king", new Item.Properties().stacksTo(1));
    public static final DeferredItem<ArmorItem> ARCHIVISTS_CROWN =
        ITEMS.register("archivists_crown", () -> new ArmorItem(
            ARCHIVIST_MATERIAL, ArmorItem.Type.HELMET,
            new Item.Properties().durability(ArmorItem.Type.HELMET.getDurability(37)).fireResistant()));
    private static final System.Logger LOGGER = System.getLogger("The Shattered Seals");

    public ShatteredSeals(IEventBus modBus) {
        ARMOR_MATERIALS.register(modBus);
        ITEMS.register(modBus);
        SOUNDS.register(modBus);
        modBus.addListener(this::commonSetup);
        LOGGER.log(System.Logger.Level.INFO,
            "The Shattered Seals progression and GUI initialized ({0})", VERSION);
    }

    /** The six first-victory trophy models, also worn by the storyline bosses. */
    public static Item bossMask(int chapter) {
        return switch (chapter) {
            case 0 -> MASK_SMUGGLER_KING.get();
            case 1 -> MASK_CINDER_WARDEN.get();
            case 2 -> MASK_HOLLOW_KNIGHT.get();
            case 3 -> MASK_ARCANE_SEER.get();
            case 4 -> MASK_SIXFOLD_GUARDIAN.get();
            default -> MASK_HOLLOW_KING.get();
        };
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            Regions.register(new ShatteredBiomeRegion());
            SurfaceRuleManager.addSurfaceRules(
                SurfaceRuleManager.RuleCategory.OVERWORLD, MOD_ID, ShatteredBiomeRegion.surfaceRules());
            LOGGER.log(System.Logger.Level.INFO,
                "Six rare Shattered Seals biomes registered for Overworld generation.");
        });
    }
}
