package com.bablockedarea;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class BABlockedAreaPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(BABlockedAreaPlugin.class);
		RuneLite.main(args);
	}
}
