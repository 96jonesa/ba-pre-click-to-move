package com.bapreclicktomove;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Area;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

class PointsInterfaceBoundaryOverlay extends Overlay
{
	private final Client client;
	private final BAPreClickToMoveConfig config;
	private final BlockedAreaCalculator calculator;

	@Inject
	PointsInterfaceBoundaryOverlay(Client client, BAPreClickToMoveConfig config)
	{
		this.client = client;
		this.config = config;
		this.calculator = new BlockedAreaCalculator(client, id -> id == InterfaceID.BARBASSAULT_WAVECOMPLETE);
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_WIDGETS);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!config.showPointsInterfaceBoundary() || client.getWidget(InterfaceID.BARBASSAULT_WAVECOMPLETE, 0) == null)
		{
			return null;
		}

		Area blocked = calculator.compute();
		if (blocked.isEmpty())
		{
			return null;
		}

		graphics.setColor(config.fillColor());
		graphics.fill(blocked);
		graphics.setColor(config.outlineColor());
		graphics.setStroke(new BasicStroke(config.outlineWidth()));
		graphics.draw(blocked);
		return null;
	}
}
