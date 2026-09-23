package com.bablockedarea;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.awt.Rectangle;
import java.awt.geom.Area;
import net.runelite.api.Client;
import net.runelite.api.HashTable;
import net.runelite.api.WidgetNode;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetModalMode;
import net.runelite.api.widgets.WidgetType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class BlockedAreaCalculatorTest
{
	private static final int TOPLEVEL = 161;
	private static final int TARGET = 497;
	private static final int OTHER = 300;

	private Client client;
	private HashTable<WidgetNode> componentTable;
	private BlockedAreaCalculator calculator;

	@BeforeEach
	@SuppressWarnings("unchecked")
	void setUp()
	{
		client = mock(Client.class);
		componentTable = mock(HashTable.class);
		when(client.getComponentTable()).thenReturn(componentTable);
		when(client.getCanvasWidth()).thenReturn(800);
		when(client.getCanvasHeight()).thenReturn(600);
		calculator = new BlockedAreaCalculator(client, id -> id == TARGET);
	}

	private static Widget widget(int group, int child, Rectangle bounds)
	{
		Widget w = mock(Widget.class);
		when(w.getId()).thenReturn(group << 16 | child);
		when(w.getBounds()).thenReturn(bounds);
		return w;
	}

	/** An if3 layer, which the client always processes. */
	private static Widget layer(int group, int child, Rectangle bounds, Widget... children)
	{
		Widget w = widget(group, child, bounds);
		when(w.isIf3()).thenReturn(true);
		when(w.getType()).thenReturn(WidgetType.LAYER);
		when(w.getStaticChildren()).thenReturn(children);
		return w;
	}

	private static Widget viewport(Rectangle bounds)
	{
		Widget w = layer(TOPLEVEL, 1, bounds);
		when(w.getContentType()).thenReturn(1337);
		return w;
	}

	private static Widget noClickThrough(Widget w)
	{
		when(w.getNoClickThrough()).thenReturn(true);
		return w;
	}

	private void mount(Widget layer, int group, int modalMode, Widget... roots)
	{
		WidgetNode node = mock(WidgetNode.class);
		when(node.getId()).thenReturn(group);
		when(node.getModalMode()).thenReturn(modalMode);
		when(componentTable.get((long) layer.getId())).thenReturn(node);
		when(layer.getNestedChildren()).thenReturn(roots);
	}

	private void roots(Widget... roots)
	{
		when(client.getWidgetRoots()).thenReturn(roots);
	}

	private static Area area(Rectangle... rects)
	{
		Area a = new Area();
		for (Rectangle r : rects)
		{
			a.add(new Area(r));
		}
		return a;
	}

	/** {@link Area} only overrides {@code equals(Area)}, so compare shapes explicitly. */
	private static void assertArea(Area actual, Rectangle... expected)
	{
		Area want = area(expected);
		assertTrue(want.equals(actual), () -> "expected " + want.getBounds() + " but was " + actual.getBounds());
	}

	@Nested
	class TestCompute
	{
		private final Rectangle view = new Rectangle(0, 0, 500, 400);

		@Test
		void noClickThroughAfterViewportBlocks()
		{
			Rectangle box = new Rectangle(100, 100, 50, 50);
			roots(viewport(view), noClickThrough(layer(TARGET, 3, box)));

			assertArea(calculator.compute(), box);
		}

		@Test
		void noClickThroughBeforeViewportDoesNotBlock()
		{
			roots(noClickThrough(layer(TARGET, 3, new Rectangle(100, 100, 50, 50))), viewport(view));

			assertTrue(calculator.compute().isEmpty());
		}

		@Test
		void noViewportMeansNothingBlocked()
		{
			roots(noClickThrough(layer(TARGET, 3, new Rectangle(100, 100, 50, 50))));

			assertTrue(calculator.compute().isEmpty());
		}

		@Test
		void blockerIsClippedToAncestorsAndViewport()
		{
			// The child spills out of its parent on the left and out of the viewport at the bottom.
			Widget child = noClickThrough(layer(TARGET, 4, new Rectangle(50, 350, 200, 100)));
			Widget parent = layer(TARGET, 3, new Rectangle(100, 0, 400, 600), child);
			roots(viewport(view), parent);

			assertArea(calculator.compute(), new Rectangle(100, 350, 150, 50));
		}

		@Test
		void modalMountBlocksLayerBounds()
		{
			Rectangle slot = new Rectangle(20, 20, 300, 200);
			Widget mountLayer = layer(TOPLEVEL, 5, slot);
			mount(mountLayer, TARGET, WidgetModalMode.MODAL_NOCLICKTHROUGH, layer(TARGET, 0, slot));
			roots(viewport(view), mountLayer);

			assertArea(calculator.compute(), slot);
		}

		@Test
		void nonModalMountDoesNotBlock()
		{
			Rectangle slot = new Rectangle(20, 20, 300, 200);
			Widget mountLayer = layer(TOPLEVEL, 5, slot);
			mount(mountLayer, TARGET, WidgetModalMode.MODAL_CLICKTHROUGH, layer(TARGET, 0, slot));
			roots(viewport(view), mountLayer);

			assertTrue(calculator.compute().isEmpty());
		}

		@Test
		void blockersInsideNestedInterfaceBlock()
		{
			Rectangle box = new Rectangle(40, 40, 30, 30);
			Widget mountLayer = layer(TOPLEVEL, 5, new Rectangle(20, 20, 300, 200));
			mount(mountLayer, TARGET, WidgetModalMode.MODAL_CLICKTHROUGH,
				layer(TARGET, 0, new Rectangle(20, 20, 300, 200), noClickThrough(layer(TARGET, 7, box))));
			roots(viewport(view), mountLayer);

			assertArea(calculator.compute(), box);
		}

		@Test
		void otherInterfacesAreLeftOut()
		{
			roots(viewport(view), noClickThrough(layer(OTHER, 3, new Rectangle(100, 100, 50, 50))));

			assertTrue(calculator.compute().isEmpty());
		}

		@Test
		void unionOfSeveralBlockers()
		{
			Rectangle a = new Rectangle(0, 0, 50, 50);
			Rectangle b = new Rectangle(25, 25, 50, 50);
			roots(viewport(view), noClickThrough(layer(TARGET, 3, a)), noClickThrough(layer(TARGET, 4, b)));

			assertArea(calculator.compute(), a, b);
		}

		@Test
		void if3GraphicWithoutListenerOrMaskIsSkipped()
		{
			Widget sprite = noClickThrough(widget(TARGET, 3, new Rectangle(100, 100, 50, 50)));
			when(sprite.isIf3()).thenReturn(true);
			when(sprite.getType()).thenReturn(WidgetType.GRAPHIC);
			roots(viewport(view), sprite);

			assertTrue(calculator.compute().isEmpty());
		}

		@Test
		void if3GraphicWithListenerBlocks()
		{
			Rectangle box = new Rectangle(100, 100, 50, 50);
			Widget sprite = noClickThrough(widget(TARGET, 3, box));
			when(sprite.isIf3()).thenReturn(true);
			when(sprite.getType()).thenReturn(WidgetType.GRAPHIC);
			when(sprite.hasListener()).thenReturn(true);
			roots(viewport(view), sprite);

			assertArea(calculator.compute(), box);
		}

		@Test
		void selfHiddenSubtreeIsSkipped()
		{
			Widget child = noClickThrough(layer(TARGET, 4, new Rectangle(100, 100, 50, 50)));
			Widget parent = layer(TARGET, 3, new Rectangle(0, 0, 500, 400), child);
			when(parent.isSelfHidden()).thenReturn(true);
			roots(viewport(view), parent);

			assertTrue(calculator.compute().isEmpty());
		}

		@Test
		void listenerWithoutNoClickThroughDoesNotBlock()
		{
			Widget button = layer(TARGET, 3, new Rectangle(100, 100, 50, 50));
			when(button.hasListener()).thenReturn(true);
			when(button.getActions()).thenReturn(new String[]{"Close"});
			roots(viewport(view), button);

			assertTrue(calculator.compute().isEmpty());
		}
	}
}
