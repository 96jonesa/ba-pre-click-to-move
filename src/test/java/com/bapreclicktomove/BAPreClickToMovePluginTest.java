package com.bapreclicktomove;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class BAPreClickToMovePluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(BAPreClickToMovePlugin.class);
		RuneLite.main(args);
	}
}
