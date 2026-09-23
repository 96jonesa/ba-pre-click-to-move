package com.bablockedarea;

import java.awt.Color;
import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;

@ConfigGroup(BABlockedAreaConfig.GROUP)
public interface BABlockedAreaConfig extends Config
{
	String GROUP = "bablockedarea";

	@Alpha
	@ConfigItem(
		keyName = "outlineColor",
		name = "Outline color",
		description = "Color of the outline drawn around the blocked area",
		position = 0
	)
	default Color outlineColor()
	{
		return Color.RED;
	}

	@Range(min = 1, max = 5)
	@ConfigItem(
		keyName = "outlineWidth",
		name = "Outline width",
		description = "Width of the outline, in pixels",
		position = 1
	)
	default int outlineWidth()
	{
		return 2;
	}

	@Alpha
	@ConfigItem(
		keyName = "fillColor",
		name = "Fill color",
		description = "Color used to fill the blocked area (fully transparent by default)",
		position = 2
	)
	default Color fillColor()
	{
		return new Color(255, 0, 0, 0);
	}
}
