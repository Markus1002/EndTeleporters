package com.markus1002.endteleporters;

import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.markus1002.endteleporters.block.EnderFrameBlock;
import com.markus1002.endteleporters.block.EnderFrameTileEntity;
import com.markus1002.endteleporters.util.ModSoundEvents;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("endteleporters")
public class EndTeleporters
{
	public static final String MOD_ID = "endteleporters";
	
    private static final Logger LOGGER = LogManager.getLogger();
    
    public static final Block ENDER_FRAME = new EnderFrameBlock(Block.Properties.create(Material.ROCK).hardnessAndResistance(22.5F, 600.0F).sound(SoundType.GLASS).lightValue(7)).setRegistryName(location("ender_frame"));

    public static final TileEntityType<?> ENDER_FRAME_TILE = TileEntityType.Builder.create(EnderFrameTileEntity::new, ENDER_FRAME).build(null).setRegistryName(location("ender_frame"));

    
    public EndTeleporters()
    {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    private void setup(final FMLCommonSetupEvent event)
    {
        ModLoadingContext.get().registerConfig(Type.COMMON, Config.CONFIG);
        Config.loadConfig(Paths.get("config", MOD_ID + ".toml"));
    }
    
    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents
    {
        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> event)
        {
        	event.getRegistry().register(ENDER_FRAME);
        }
        
        @SubscribeEvent
        public static void onItemsRegistry(final RegistryEvent.Register<Item> event)
        {
        	event.getRegistry().register(new BlockItem(ENDER_FRAME, new Item.Properties().group(ItemGroup.TRANSPORTATION)).setRegistryName(ENDER_FRAME.getRegistryName()));
        }
        
        @SubscribeEvent
        public static void onTileEntityRegistry(final RegistryEvent.Register<TileEntityType<?>> event) {

            event.getRegistry().register(ENDER_FRAME_TILE);

        }
        
        @SubscribeEvent
        public static void onSoundEventsRegistry(final RegistryEvent.Register<SoundEvent> event)
        {
        	event.getRegistry().registerAll(ModSoundEvents.BLOCK_ENDER_FRAME_TELEPORT, ModSoundEvents.BLOCK_ENDER_FRAME_TELEPORT_FAIL, ModSoundEvents.BLOCK_ENDER_FRAME_ACTIVATE, ModSoundEvents.BLOCK_ENDER_FRAME_DEACTIVATE);
        }
    }
    
	public static ResourceLocation location(String name)
	{
		return new ResourceLocation(MOD_ID, name);
	}
}