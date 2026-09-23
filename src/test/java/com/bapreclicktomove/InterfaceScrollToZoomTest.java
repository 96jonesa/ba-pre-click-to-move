package com.bapreclicktomove;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.awt.Canvas;
import java.awt.Rectangle;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Area;
import net.runelite.api.Client;
import net.runelite.api.Point;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class InterfaceScrollToZoomTest
{
	private static final Rectangle SCROLL_BLOCKED = new Rectangle(100, 100, 50, 50);

	private Client client;
	private BAPreClickToMoveConfig config;
	private InterfaceScrollToZoom zoom;

	@BeforeEach
	void setUp()
	{
		client = mock(Client.class);
		config = mock(BAPreClickToMoveConfig.class);
		ClientThread clientThread = mock(ClientThread.class);
		doAnswer(inv ->
		{
			((Runnable) inv.getArgument(0)).run();
			return null;
		}).when(clientThread).invoke(any(Runnable.class));

		BlockedAreaCalculator calculator = mock(BlockedAreaCalculator.class);
		when(calculator.compute()).thenReturn(new BlockedAreaCalculator.Result(new Area(), new Area(SCROLL_BLOCKED)));

		// Defaults: feature on, interface open, game's scroll-to-zoom on, menu closed, mouse inside.
		when(config.scrollToZoom()).thenReturn(true);
		when(client.getWidget(InterfaceID.BARBASSAULT_WAVECOMPLETE, 0)).thenReturn(mock(Widget.class));
		when(client.getVarbitValue(VarbitID.CAMERA_ZOOM_MOUSE_DISABLED)).thenReturn(0);
		mouseAt(120, 120);

		zoom = new InterfaceScrollToZoom(client, clientThread, config, calculator);
	}

	private void mouseAt(int x, int y)
	{
		when(client.getMouseCanvasPosition()).thenReturn(new Point(x, y));
	}

	private static MouseWheelEvent wheel(int rotation)
	{
		return new MouseWheelEvent(new Canvas(), MouseWheelEvent.MOUSE_WHEEL, 0, 0, 0, 0, 0, false,
			MouseWheelEvent.WHEEL_UNIT_SCROLL, 3, rotation);
	}

	@Nested
	class TestMouseWheelMoved
	{
		@Test
		void zoomsWithWheelRotationInsideScrollBlockedArea()
		{
			zoom.mouseWheelMoved(wheel(-1));

			verify(client).runScript(39, -1);
		}

		@Test
		void doesNothingOutsideScrollBlockedArea()
		{
			mouseAt(10, 10);

			zoom.mouseWheelMoved(wheel(1));

			verify(client, never()).runScript(anyInt(), anyInt());
		}

		@Test
		void doesNothingWhenDisabled()
		{
			when(config.scrollToZoom()).thenReturn(false);

			zoom.mouseWheelMoved(wheel(1));

			verify(client, never()).runScript(anyInt(), anyInt());
		}

		@Test
		void doesNothingWhenInterfaceClosed()
		{
			when(client.getWidget(InterfaceID.BARBASSAULT_WAVECOMPLETE, 0)).thenReturn(null);

			zoom.mouseWheelMoved(wheel(1));

			verify(client, never()).runScript(anyInt(), anyInt());
		}

		@Test
		void doesNothingWhenGameScrollToZoomIsOff()
		{
			when(client.getVarbitValue(VarbitID.CAMERA_ZOOM_MOUSE_DISABLED)).thenReturn(1);

			zoom.mouseWheelMoved(wheel(1));

			verify(client, never()).runScript(anyInt(), anyInt());
		}

		@Test
		void doesNothingWhenMenuOpen()
		{
			when(client.isMenuOpen()).thenReturn(true);

			zoom.mouseWheelMoved(wheel(1));

			verify(client, never()).runScript(anyInt(), anyInt());
		}

		@Test
		void doesNothingForZeroRotation()
		{
			zoom.mouseWheelMoved(wheel(0));

			verify(client, never()).runScript(anyInt(), anyInt());
		}
	}
}
