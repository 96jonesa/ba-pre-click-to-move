package com.bablockedarea;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class NorthSouthTest
{
	@Nested
	class TestOffset
	{
		@Test
		void northIsPositive()
		{
			assertEquals(3, NorthSouth.NORTH.offset(3));
		}

		@Test
		void southIsNegative()
		{
			assertEquals(-3, NorthSouth.SOUTH.offset(3));
		}

		@Test
		void zeroTilesIsNoOffset()
		{
			assertEquals(0, NorthSouth.SOUTH.offset(0));
		}
	}
}
