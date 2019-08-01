package com.markus1002.endteleporters;

import java.nio.file.Path;
import java.util.List;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.google.common.collect.Lists;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

public class Config
{
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    
    public static ForgeConfigSpec CONFIG;

    public static ConfigValue<List<? extends String>> TELEPORTER_BLOCKS;
	public static ConfigValue<List<? extends String>> TELEPORTABLE_ENTITIES_BLACKLIST;
    public static ForgeConfigSpec.IntValue MAX_TELEPORTER_RANGE;
    public static ForgeConfigSpec.IntValue MIN_TELEPORTER_BLOCKS;
    public static ForgeConfigSpec.IntValue RANGE_PER_BLOCK;
	
    static
    {
    	TELEPORTER_BLOCKS = BUILDER.comment("Blocks that can be used to construct a teleporter and expand it's range")
    			.defineList("teleporterBlocks", Lists.newArrayList("minecraft:purpur_block", "minecraft:purpur_pillar"), e -> e instanceof String);
    	TELEPORTABLE_ENTITIES_BLACKLIST = BUILDER.comment("Entities that can not be teleported using a teleporter. By default used for block-like entities and entities from the End")
    			.defineList("teleportableEntitiesBlacklist", Lists.newArrayList("minecraft:area_effect_cloud", "minecraft:leash_knot", "minecraft:painting", "minecraft:item_frame", "minecraft:evocation_fangs", "minecraft:ender_crystal", "minecraft:elder_guardian", "minecraft:ghast", "minecraft:enderman", "minecraft:ender_dragon", "minecraft:wither", "minecraft:shulker", "minecraft:endermite", "minecraft:lightning_bolt")
    					, e -> e instanceof String);
    	MAX_TELEPORTER_RANGE = BUILDER.comment("The maximum distance (in blocks) that can be traveled using a teleporter").defineInRange("maxTeleporterRange", 3000, 1, 10000);
    	MIN_TELEPORTER_BLOCKS = BUILDER.comment("The minimum amount of purpur blocks required to activate a teleporter").defineInRange("minTeleporterBlocks", 9, 1, 98);
    	RANGE_PER_BLOCK = BUILDER.comment("The amount of range added per purpur block").defineInRange("rangePerBlock", 40, 1, 1000);
        
        CONFIG = BUILDER.build();
    }
	
    public static void loadConfig(Path path)
    {
        final CommentedFileConfig configData = CommentedFileConfig.builder(path)
                .sync()
                .autosave()
                .writingMode(WritingMode.REPLACE)
                .build();

        configData.load();
        CONFIG.setConfig(configData);
    }
}