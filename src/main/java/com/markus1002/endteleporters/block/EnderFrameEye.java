package com.markus1002.endteleporters.block;

import net.minecraft.util.IStringSerializable;

public enum EnderFrameEye implements IStringSerializable
{
	NONE("none"),
	DOWN("down"),
	UP("up"),
	NORTH("north"),
	SOUTH("south"),
	WEST("west"),
	EAST("east");

	private final String name;

	private EnderFrameEye(String name)
	{
		this.name = name;
	}

	public String toString()
	{
		return this.name;
	}

	public String getName()
	{
		return this.name;
	}
}