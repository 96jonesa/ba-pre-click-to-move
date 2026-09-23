package com.bablockedarea;

import java.awt.Rectangle;
import java.awt.geom.Area;
import java.util.function.IntPredicate;
import net.runelite.api.Client;
import net.runelite.api.WidgetNode;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetConfigNode;
import net.runelite.api.widgets.WidgetModalMode;
import net.runelite.api.widgets.WidgetType;

/**
 * Computes the part of the viewport in which widgets stop the mouse from interacting with the scene.
 * <p>
 * This mirrors the client's widget mouse-input pass. The client walks the widget tree depth first, adding the
 * scene's menu entries when it reaches the viewport widget (content type 1337). A widget processed later that
 * contains the mouse then resets the menu to just "Cancel", which throws those scene entries away. Two things do
 * that reset:
 * <ul>
 *     <li>an if3 widget with {@code noClickThrough}, over its bounds clipped to its ancestors';</li>
 *     <li>a layer mounting an interface with modal mode {@link WidgetModalMode#MODAL_NOCLICKTHROUGH}, over the
 *     layer's clipped bounds.</li>
 * </ul>
 * Widgets with ops or listeners do not block: they add their own entries above the scene's but leave them in place.
 */
class BlockedAreaCalculator
{
	private static final int CONTENT_TYPE_VIEWPORT = 1337;
	private static final int CONTENT_TYPE_1338 = 1338;

	private final Client client;
	private final IntPredicate countsInterface;

	private Rectangle viewport;
	private Area blocked;

	/**
	 * @param countsInterface which interface (group) ids may contribute to the blocked area; blockers belonging to
	 *                        other interfaces still block the scene, but are left out of the result
	 */
	BlockedAreaCalculator(Client client, IntPredicate countsInterface)
	{
		this.client = client;
		this.countsInterface = countsInterface;
	}

	/**
	 * @return the blocked part of the viewport, or an empty area if nothing blocks it or there is no viewport
	 */
	Area compute()
	{
		viewport = null;
		blocked = new Area();

		Widget[] roots = client.getWidgetRoots();
		if (roots != null)
		{
			visit(roots, new Rectangle(0, 0, client.getCanvasWidth(), client.getCanvasHeight()));
		}

		if (viewport == null)
		{
			return new Area();
		}
		blocked.intersect(new Area(viewport));
		return blocked;
	}

	private void visit(Widget[] widgets, Rectangle parentClip)
	{
		for (Widget w : widgets)
		{
			if (w != null)
			{
				visit(w, parentClip);
			}
		}
	}

	private void visit(Widget w, Rectangle parentClip)
	{
		if (!isProcessed(w))
		{
			return;
		}

		Rectangle clip = parentClip.intersection(w.getBounds());
		if (clip.isEmpty() && w.isIf3())
		{
			return;
		}

		if (w.getContentType() == CONTENT_TYPE_VIEWPORT)
		{
			viewport = clip;
		}
		else if (w.isIf3() && w.getNoClickThrough())
		{
			block(clip, w.getId());
		}

		Widget[] children = w.getStaticChildren();
		if (children != null)
		{
			visit(children, clip);
		}
		children = w.getDynamicChildren();
		if (children != null)
		{
			visit(children, clip);
		}

		WidgetNode node = client.getComponentTable().get(w.getId());
		if (node != null)
		{
			if (node.getModalMode() == WidgetModalMode.MODAL_NOCLICKTHROUGH)
			{
				block(clip, node.getId() << 16);
			}
			children = w.getNestedChildren();
			if (children != null)
			{
				visit(children, clip);
			}
		}
	}

	private void block(Rectangle clip, int componentId)
	{
		// Blockers processed before the viewport only discard entries that come before the scene's.
		if (viewport != null && !clip.isEmpty() && countsInterface.test(componentId >>> 16))
		{
			blocked.add(new Area(clip));
		}
	}

	/**
	 * The client's filter for which widgets take part in mouse input at all. A widget that fails it is skipped along
	 * with its whole subtree, so e.g. a plain if3 sprite with {@code noClickThrough} set does not block.
	 */
	private boolean isProcessed(Widget w)
	{
		if (!w.isIf3())
		{
			return true;
		}
		if (w.isSelfHidden())
		{
			return false;
		}

		int type = w.getType();
		if (type == WidgetType.LAYER || type == 11 || type == WidgetType.INPUT_FIELD
			|| w.hasListener() || w.getContentType() == CONTENT_TYPE_1338)
		{
			return true;
		}

		WidgetConfigNode config = client.getWidgetConfig(w);
		if (config != null)
		{
			return config.getClickMask() != 0 || config.getOpMask() != 0;
		}
		return w.getClickMask() != 0;
	}
}
