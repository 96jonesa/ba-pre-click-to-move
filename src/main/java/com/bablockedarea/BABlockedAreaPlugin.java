package com.bablockedarea;

import com.google.inject.Provides;
import javax.inject.Inject;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(
	name = "BA Blocked Area",
	description = "Barbarian Assault lobby helpers: outline the interface area that blocks scene clicks, and a tile relative to the player",
	tags = {"barbarian", "assault", "ba", "minigame", "interface", "overlay"}
)
public class BABlockedAreaPlugin extends Plugin
{
	@Inject
	private OverlayManager overlayManager;

	@Inject
	private BABlockedAreaOverlay overlay;

	@Inject
	private RelativeTileOverlay relativeTileOverlay;

	@Override
	protected void startUp()
	{
		overlayManager.add(overlay);
		overlayManager.add(relativeTileOverlay);
	}

	@Override
	protected void shutDown()
	{
		overlayManager.remove(overlay);
		overlayManager.remove(relativeTileOverlay);
	}

	@Provides
	BABlockedAreaConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(BABlockedAreaConfig.class);
	}
}
