package com.bablockedarea;

import java.awt.Color;
import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Range;

@ConfigGroup(BABlockedAreaConfig.GROUP)
public interface BABlockedAreaConfig extends Config
{
	String GROUP = "bablockedarea";

	@ConfigSection(
		name = "Blocked area",
		description = "Outline of the part of the wave-complete interface that blocks clicks on the scene",
		position = 0
	)
	String blockedAreaSection = "blockedArea";

	@ConfigSection(
		name = "Relative tile",
		description = "Outline of a tile at a fixed offset from the player, while in the lobby",
		position = 1
	)
	String relativeTileSection = "relativeTile";

	@ConfigItem(
		keyName = "showBlockedArea",
		name = "Show blocked area",
		description = "Outline the part of the wave-complete interface that blocks clicks on the scene behind it",
		section = blockedAreaSection,
		position = 0
	)
	default boolean showBlockedArea()
	{
		return false;
	}

	@Alpha
	@ConfigItem(
		keyName = "outlineColor",
		name = "Outline color",
		description = "Color of the outline drawn around the blocked area",
		section = blockedAreaSection,
		position = 1
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
		section = blockedAreaSection,
		position = 2
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
		section = blockedAreaSection,
		position = 3
	)
	default Color fillColor()
	{
		return new Color(255, 0, 0, 0);
	}

	@ConfigItem(
		keyName = "showRelativeTile",
		name = "Show relative tile",
		description = "In the Barbarian Assault lobby, outline the tile at the offset below from the player",
		section = relativeTileSection,
		position = 0
	)
	default boolean showRelativeTile()
	{
		return false;
	}

	@Range(min = 0)
	@ConfigItem(
		keyName = "northSouthTiles",
		name = "Tiles north/south",
		description = "How many tiles north or south of the player the tile is",
		section = relativeTileSection,
		position = 1
	)
	default int northSouthTiles()
	{
		return 0;
	}

	@ConfigItem(
		keyName = "northSouth",
		name = "North/South",
		description = "Whether the tile is north or south of the player",
		section = relativeTileSection,
		position = 2
	)
	default NorthSouth northSouth()
	{
		return NorthSouth.NORTH;
	}

	@Range(min = 0)
	@ConfigItem(
		keyName = "eastWestTiles",
		name = "Tiles east/west",
		description = "How many tiles east or west of the player the tile is",
		section = relativeTileSection,
		position = 3
	)
	default int eastWestTiles()
	{
		return 0;
	}

	@ConfigItem(
		keyName = "eastWest",
		name = "East/West",
		description = "Whether the tile is east or west of the player",
		section = relativeTileSection,
		position = 4
	)
	default EastWest eastWest()
	{
		return EastWest.EAST;
	}

	@Alpha
	@ConfigItem(
		keyName = "tileColor",
		name = "Tile color",
		description = "Color of the relative tile's outline",
		section = relativeTileSection,
		position = 5
	)
	default Color tileColor()
	{
		return Color.CYAN;
	}
}
