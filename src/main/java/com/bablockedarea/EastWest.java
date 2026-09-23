package com.bablockedarea;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum EastWest
{
	EAST("East", 1),
	WEST("West", -1);

	private final String name;
	private final int sign;

	/**
	 * @return the signed x offset, in tiles, of moving {@code tiles} tiles in this direction
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
