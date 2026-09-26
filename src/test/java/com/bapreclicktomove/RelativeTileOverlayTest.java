package com.bapreclicktomove;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import net.runelite.api.coords.WorldPoint;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class RelativeTileOverlayTest
{
	@Nested
	class TestLobbyWave
	{
		/** Wave, then the north-west and south-east corners, as measured in game. */
		private final int[][] rooms = {
			{1, 2576, 5298, 2583, 5291},
			{2, 2584, 5298, 2591, 5291},
			{3, 2595, 5298, 2602, 5291},
			{4, 2603, 5298, 2610, 5291},
			{5, 2576, 5288, 2583, 5281},
			{6, 2584, 5288, 2591, 5281},
			{7, 2595, 5288, 2602, 5281},
			{8, 2603, 5288, 2610, 5281},
			{9, 2576, 5278, 2583, 5271},
			{10, 2584, 5278, 2591, 5271},
		};

		private int wave(int x, int y)
		{
			return RelativeTileOverlay.lobbyWave(new WorldPoint(x, y, 0));
		}

		@Test
		void allFourCornersOfEveryRoomAreThatWave()
		{
			for (int[] r : rooms)
			{
				int w = r[0], west = r[1], north = r[2], east = r[3], south = r[4];
				assertEquals(w, wave(west, north), "wave " + w + " NW");
				assertEquals(w, wave(east, south), "wave " + w + " SE");
				assertEquals(w, wave(west, south), "wave " + w + " SW");
				assertEquals(w, wave(east, north), "wave " + w + " NE");
			}
		}

		@Test
		void tilesJustOutsideEveryRoomAreNotThatWave()
		{
			// Neighbouring rooms can share an edge, so outside a room may be another room, but never the same one.
			for (int[] r : rooms)
			{
				int w = r[0], west = r[1], north = r[2], east = r[3], south = r[4];
				assertNotEquals(w, wave(west - 1, north), "wave " + w + " west of NW");
				assertNotEquals(w, wave(west, north + 1), "wave " + w + " north of NW");
				assertNotEquals(w, wave(east + 1, south), "wave " + w + " east of SE");
				assertNotEquals(w, wave(east, south - 1), "wave " + w + " south of SE");
			}
		}

		@Test
		void adjacentRoomsMeetWithoutOverlap()
		{
			assertEquals(1, wave(2583, 5295));
			assertEquals(2, wave(2584, 5295));
			assertEquals(9, wave(2583, 5275));
			assertEquals(10, wave(2584, 5275));
		}

		@Test
		void corridorBetweenRoomsIsNotALobby()
		{
			assertEquals(RelativeTileOverlay.NOT_IN_LOBBY, wave(2593, 5295));
			assertEquals(RelativeTileOverlay.NOT_IN_LOBBY, wave(2580, 5290));
		}

		@Test
		void otherPlanesAreNotALobby()
		{
			assertEquals(RelativeTileOverlay.NOT_IN_LOBBY, RelativeTileOverlay.lobbyWave(new WorldPoint(2576, 5298, 1)));
		}

		@Test
		void roomListMatchesTheMeasuredTable()
		{
			assertEquals(rooms.length, RelativeTileOverlay.WAVE_LOBBIES.size());
		}
	}
}
