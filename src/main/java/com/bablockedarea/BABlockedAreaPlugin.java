package com.bablockedarea;

import com.google.inject.Provides;
import javax.inject.Inject;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(
	name = "BA Blocked Area",
	description = "Outlines the part of the Barbarian Assault wave-complete interface that blocks clicks on the scene behind it",
	tags = {"barbarian", "assault", "ba", "minigame", "interface", "overlay"}
)
public class BABlockedAreaPlugin extends Plugin
{
	@Inject
	private OverlayManager overlayManager;

	@Inject
	private BABlockedAreaOverlay overlay;

	@Override
	protected void startUp()
	{
		overlayManager.add(overlay);
	}

	@Override
	protected void shutDown()
	{
		overlayManager.remove(overlay);
	}

	@Provides
	BABlockedAreaConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(BABlockedAreaConfig.class);
	}
}
