package com.nosteven;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("nosteven")
public interface NoStevenConfig extends Config
{
	@ConfigItem(
		position = 0,
		keyName = "strictMode",
		name = "Strict Mode",
		description = "Be case-sensitive when considering potential Steven names"
	)
	default boolean strictMode()
	{
		return false;
	}

	@ConfigItem(
		position = 1,
		keyName = "hideInteractors",
		name = "Hide Interactors",
		description = "Hide NPCs interacting with Stevens in the same way that Stevens are hidden"
	)
	default boolean hideInteractors()
	{
		return true;
	}
}