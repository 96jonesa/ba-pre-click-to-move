package com.bapreclicktomove;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import net.runelite.api.coords.WorldPoint;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class RelativeTileOverlayTest
{
	@Nested
	class TestIsWave10Lobby
	{
		// Corners as measured in game.
		private static final int WEST = 2584;
		private static final int EAST = 2591;
		private static final int SOUTH = 5271;
		private static final int NORTH = 5278;

		@Test
		void containsAllFourCorners()
		{
			assertTrue(RelativeTileOverlay.isWave10Lobby(new WorldPoint(WEST, NORTH, 0)));
			assertTrue(RelativeTileOverlay.isWave10Lobby(new WorldPoint(EAST, SOUTH, 0)));
			assertTrue(RelativeTileOverlay.isWave10Lobby(new WorldPoint(WEST, SOUTH, 0)));
			assertTrue(RelativeTileOverlay.isWave10Lobby(new WorldPoint(EAST, NORTH, 0)));
		}

		@Test
		void excludesTilesJustOutsideEachEdge()
		{
			assertFalse(RelativeTileOverlay.isWave10Lobby(new WorldPoint(WEST - 1, NORTH, 0)));
			assertFalse(RelativeTileOverlay.isWave10Lobby(new WorldPoint(WEST, NORTH + 1, 0)));
			assertFalse(RelativeTileOverlay.isWave10Lobby(new WorldPoint(EAST + 1, SOUTH, 0)));
			assertFalse(RelativeTileOverlay.isWave10Lobby(new WorldPoint(EAST, SOUTH - 1, 0)));
		}

		@Test
		void excludesOtherPlanes()
		{
			assertFalse(RelativeTileOverlay.isWave10Lobby(new WorldPoint(WEST, NORTH, 1)));
		}

		@Test
		void isInsideTheLobbyRegion()
		{
			assertTrue(RelativeTileOverlay.WAVE_10_LOBBY.toWorldPoint().getRegionID() == RelativeTileOverlay.LOBBY_REGION_ID);
		}
	}
}
