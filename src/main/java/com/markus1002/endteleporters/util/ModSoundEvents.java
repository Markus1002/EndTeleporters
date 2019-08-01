package com.markus1002.endteleporters.util;

import com.markus1002.endteleporters.EndTeleporters;

import net.minecraft.util.SoundEvent;

public class ModSoundEvents
{
    public static final SoundEvent BLOCK_ENDER_FRAME_TELEPORT = new SoundEvent(EndTeleporters.location("block.ender_frame.teleport")).setRegistryName(EndTeleporters.location("block.ender_frame.teleport"));
    public static final SoundEvent BLOCK_ENDER_FRAME_TELEPORT_FAIL = new SoundEvent(EndTeleporters.location("block.ender_frame.teleport_fail")).setRegistryName(EndTeleporters.location("block.ender_frame.teleport_fail"));
    public static final SoundEvent BLOCK_ENDER_FRAME_ACTIVATE = new SoundEvent(EndTeleporters.location("block.ender_frame.activate")).setRegistryName(EndTeleporters.location("block.ender_frame.activate"));
    public static final SoundEvent BLOCK_ENDER_FRAME_DEACTIVATE = new SoundEvent(EndTeleporters.location("block.ender_frame.deactivate")).setRegistryName(EndTeleporters.location("block.ender_frame.deactivate"));
}