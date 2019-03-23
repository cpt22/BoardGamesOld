package com.cptingle.BoardGames.region;

@Deprecated
public enum RegionPointOld {
	P1,
	P2,
	B1,
	B2,
	SPAWN,
	P1SPAWN,
	P2SPAWN;
	
	@Override
	public String toString() {
		return name().toLowerCase();
	}
}
