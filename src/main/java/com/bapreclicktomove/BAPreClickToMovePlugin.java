package com.bapreclicktomove;

import com.google.inject.Provides;
import javax.inject.Inject;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.input.MouseManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(
	name = "BA Pre-Click to Move",
	description = "Barbarian Assault lobby helpers: outline the interface area that blocks scene clicks, zoom while scrolling over it, and outline a tile relative to the player",
	tags = {"barbarian", "assault", "ba", "minigame", "interface", "overlay", "zoom"}
)
public class BAPreClickToMovePlugin extends Plugin
{
	@Inject
	private OverlayManager overlayManager;

	@Inject
	private PointsInterfaceBoundaryOverlay overlay;

	@Inject
	private RelativeTileOverlay relativeTileOverlay;

	@Inject
	private MouseManager mouseManager;

	@Inject
	private InterfaceScrollToZoom interfaceScrollToZoom;

	@Override
	protected void startUp()
	{
		overlayManager.add(overlay);
		overlayManager.add(relativeTileOverlay);
		mouseManager.registerMouseWheelListener(interfaceScrollToZoom);
	}

	@Override
	protected void shutDown()
	{
		overlayManager.remove(overlay);
		overlayManager.remove(relativeTileOverlay);
		mouseManager.unregisterMouseWheelListener(interfaceScrollToZoom);
	}

	@Provides
	BAPreClickToMoveConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(BAPreClickToMoveConfig.class);
	}
}
