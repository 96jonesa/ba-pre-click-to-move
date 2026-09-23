package com.bapreclicktomove;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Player;
import net.runelite.api.coords.LocalPoint;
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
	/** The underground lobby with the waiting rooms, below the Barbarian Outpost. */
	static final int LOBBY_REGION_ID = 10322;

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
		if (playerTile.getRegionID() != LOBBY_REGION_ID)
		{
			return null;
		}

		WorldPoint target = playerTile
			.dx(config.eastWest().offset(config.eastWestTiles()))
			.dy(config.northSouth().offset(config.northSouthTiles()));
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
}
