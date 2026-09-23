package com.bapreclicktomove;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum NorthSouth
{
	NORTH("North", 1),
	SOUTH("South", -1);

	private final String name;
	private final int sign;

	/**
	 * @return the signed y offset, in tiles, of moving {@code tiles} tiles in this direction
	 */
	int offset(int tiles)
	{
		return sign * tiles;
	}

	@Override
	public String toString()
	{
		return name;
	}
}
