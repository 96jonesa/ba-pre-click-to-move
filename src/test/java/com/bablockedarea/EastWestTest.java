package com.bablockedarea;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class EastWestTest
{
	@Nested
	class TestOffset
	{
		@Test
		void eastIsPositive()
		{
			assertEquals(3, EastWest.EAST.offset(3));
		}

		@Test
		void westIsNegative()
		{
			assertEquals(-3, EastWest.WEST.offset(3));
		}

		@Test
		void zeroTilesIsNoOffset()
		{
			assertEquals(0, EastWest.WEST.offset(0));
		}
	}
}
