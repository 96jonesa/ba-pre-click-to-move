package com.bapreclicktomove;

import java.awt.Color;
import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Range;

@ConfigGroup(BAPreClickToMoveConfig.GROUP)
public interface BAPreClickToMoveConfig extends Config
{
	String GROUP = "bapreclicktomove";

	@ConfigSection(
		name = "Points interface boundary",
		description = "Outline of the part of the wave-complete interface that blocks clicks on the scene",
		position = 0
	)
	String pointsInterfaceBoundarySection = "pointsInterfaceBoundary";

	@ConfigSection(
		name = "Relative tile",
		description = "Outline of a tile at a fixed offset from the player, while in the lobby",
		position = 1
	)
	String relativeTileSection = "relativeTile";

	@ConfigItem(
		keyName = "showPointsInterfaceBoundary",
		name = "Show points interface boundary",
		description = "Outline the part of the wave-complete interface that blocks clicks on the scene behind it",
		section = pointsInterfaceBoundarySection,
		position = 0
	)
	default boolean showPointsInterfaceBoundary()
	{
		return false;
	}

	@ConfigItem(
		keyName = "scrollToZoom",
		name = "Scroll to zoom over interface",
		description = "Zoom the camera when scrolling over the part of the wave-complete interface that blocks the scene."
			+ " Needs the game's scroll-to-zoom setting on",
		section = pointsInterfaceBoundarySection,
		position = 1
	)
	default boolean scrollToZoom()
	{
		return false;
	}

	@Alpha
	@ConfigItem(
		keyName = "outlineColor",
		name = "Outline color",
		description = "Color of the outline drawn along the points interface boundary",
		section = pointsInterfaceBoundarySection,
		position = 2
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
		section = pointsInterfaceBoundarySection,
		position = 3
	)
	default int outlineWidth()
	{
		return 2;
	}

	@Alpha
	@ConfigItem(
		keyName = "fillColor",
		name = "Fill color",
		description = "Color used to fill the area inside the points interface boundary (fully transparent by default)",
		section = pointsInterfaceBoundarySection,
		position = 4
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

	@ConfigItem(
		keyName = "wave10Offset",
		name = "Different offset on wave 10",
		description = "In the lobby before wave 10, outline the tile at the wave 10 offset below instead."
			+ " The dispensers are placed differently relative to the spawns on wave 10",
		section = relativeTileSection,
		position = 6
	)
	default boolean wave10Offset()
	{
		return false;
	}

	@Range(min = 0)
	@ConfigItem(
		keyName = "wave10NorthSouthTiles",
		name = "Wave 10 tiles north/south",
		description = "How many tiles north or south of the player the tile is before wave 10",
		section = relativeTileSection,
		position = 7
	)
	default int wave10NorthSouthTiles()
	{
		return 0;
	}

	@ConfigItem(
		keyName = "wave10NorthSouth",
		name = "Wave 10 North/South",
		description = "Whether the tile is north or south of the player before wave 10",
		section = relativeTileSection,
		position = 8
	)
	default NorthSouth wave10NorthSouth()
	{
		return NorthSouth.NORTH;
	}

	@Range(min = 0)
	@ConfigItem(
		keyName = "wave10EastWestTiles",
		name = "Wave 10 tiles east/west",
		description = "How many tiles east or west of the player the tile is before wave 10",
		section = relativeTileSection,
		position = 9
	)
	default int wave10EastWestTiles()
	{
		return 0;
	}

	@ConfigItem(
		keyName = "wave10EastWest",
		name = "Wave 10 East/West",
		description = "Whether the tile is east or west of the player before wave 10",
		section = relativeTileSection,
		position = 10
	)
	default EastWest wave10EastWest()
	{
		return EastWest.EAST;
	}
}
