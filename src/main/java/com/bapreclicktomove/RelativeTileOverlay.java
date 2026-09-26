package com.bapreclicktomove;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.util.List;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Player;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

/**
 * Outlines the tile at a configured offset from the player while in the Barbarian Assault lobby. It is drawn above
 * widgets so interfaces such as the wave-complete one don't hide it.
 */
class RelativeTileOverlay extends Overlay
{
	/**
	 * The waiting rooms in the lobby below the Barbarian Outpost, indexed by wave (index 0 is wave 1). Players are
	 * placed in the room for the next wave, so the room identifies the wave. Corners (north-west, south-east) were
	 * measured in game.
	 */
	static final List<WorldArea> WAVE_LOBBIES = List.of(
		room(2576, 5298, 2583, 5291),
		room(2584, 5298, 2591, 5291),
		room(2595, 5298, 2602, 5291),
		room(2603, 5298, 2610, 5291),
		room(2576, 5288, 2583, 5281),
		room(2584, 5288, 2591, 5281),
		room(2595, 5288, 2602, 5281),
		room(2603, 5288, 2610, 5281),
		room(2576, 5278, 2583, 5271),
		room(2584, 5278, 2591, 5271)
	);

	static final int NOT_IN_LOBBY = 0;

	private static final Color TRANSPARENT = new Color(0, 0, 0, 0);

	private final Client client;
	private final BAPreClickToMoveConfig config;

	@Inject
	RelativeTileOverlay(Client client, BAPreClickToMoveConfig config)
	{
		this.client = client;
		this.config = config;
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_WIDGETS);
		setPriority(PRIORITY_HIGHEST);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!config.showRelativeTile())
		{
			return null;
		}

		Player player = client.getLocalPlayer();
		if (player == null)
		{
			return null;
		}

		// The server-side tile, not the interpolated position mid-walk, so the outline steps with the player.
		WorldPoint playerTile = player.getWorldLocation();
		int wave = lobbyWave(playerTile);
		if (wave == NOT_IN_LOBBY)
		{
			return null;
		}

		WorldPoint target;
		if (wave == 10 && config.wave10Offset())
		{
			target = playerTile
				.dx(config.wave10EastWest().offset(config.wave10EastWestTiles()))
				.dy(config.wave10NorthSouth().offset(config.wave10NorthSouthTiles()));
		}
		else
		{
			target = playerTile
				.dx(config.eastWest().offset(config.eastWestTiles()))
				.dy(config.northSouth().offset(config.northSouthTiles()));
		}
		LocalPoint tile = LocalPoint.fromWorld(player.getWorldView(), target);
		if (tile == null)
		{
			return null;
		}

		Polygon poly = Perspective.getCanvasTilePoly(client, tile);
		if (poly != null)
		{
			OverlayUtil.renderPolygon(graphics, poly, config.tileColor(), TRANSPARENT, new BasicStroke(2));
		}
		return null;
	}

	/**
	 * @return the wave whose waiting room {@code tile} is in, or {@link #NOT_IN_LOBBY}
	 */
	static int lobbyWave(WorldPoint tile)
	{
		for (int i = 0; i < WAVE_LOBBIES.size(); i++)
		{
			if (WAVE_LOBBIES.get(i).contains(tile))
			{
				return i + 1;
			}
		}
		return NOT_IN_LOBBY;
	}

	/**
	 * A plane-0 room given by its inclusive north-west and south-east corner tiles.
	 */
	private static WorldArea room(int westX, int northY, int eastX, int southY)
	{
		return new WorldArea(westX, southY, eastX - westX + 1, northY - southY + 1, 0);
	}
}
