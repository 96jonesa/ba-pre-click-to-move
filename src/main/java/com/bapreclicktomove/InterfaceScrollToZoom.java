package com.bapreclicktomove;

import java.awt.event.MouseWheelEvent;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.Client;
import net.runelite.api.Point;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.input.MouseWheelListener;

/**
 * Zooms the camera when the mouse wheel is scrolled over the part of the wave-complete interface that stops scroll
 * events from reaching the viewport, as scrolling over the scene would.
 */
@Singleton
class InterfaceScrollToZoom implements MouseWheelListener
{
	/**
	 * The script the viewport's scroll-wheel listener runs to zoom, taking the wheel rotation. Same arguments as
	 * the game passes when scrolling over the scene.
	 */
	private static final int ZOOM_SCRIPT_ID = 39;

	private final Client client;
	private final ClientThread clientThread;
	private final BAPreClickToMoveConfig config;
	private final BlockedAreaCalculator calculator;

	@Inject
	InterfaceScrollToZoom(Client client, ClientThread clientThread, BAPreClickToMoveConfig config)
	{
		this(client, clientThread, config,
			new BlockedAreaCalculator(client, id -> id == InterfaceID.BARBASSAULT_WAVECOMPLETE));
	}

	InterfaceScrollToZoom(Client client, ClientThread clientThread, BAPreClickToMoveConfig config,
		BlockedAreaCalculator calculator)
	{
		this.client = client;
		this.clientThread = clientThread;
		this.config = config;
		this.calculator = calculator;
	}

	@Override
	public MouseWheelEvent mouseWheelMoved(MouseWheelEvent event)
	{
		int rotation = event.getWheelRotation();
		if (rotation != 0 && config.scrollToZoom())
		{
			clientThread.invoke(() -> zoom(rotation));
		}
		return event;
	}

	private void zoom(int rotation)
	{
		if (client.getWidget(InterfaceID.BARBASSAULT_WAVECOMPLETE, 0) == null
			|| client.isMenuOpen()
			|| client.getVarbitValue(VarbitID.CAMERA_ZOOM_MOUSE_DISABLED) != 0)
		{
			return;
		}

		Point mouse = client.getMouseCanvasPosition();
		if (calculator.compute().getScrollBlocked().contains(mouse.getX(), mouse.getY()))
		{
			client.runScript(ZOOM_SCRIPT_ID, rotation);
		}
	}
}
