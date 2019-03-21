package com.cptingle.BoardGames.region;

public enum RegionPoint {
	P1,
	P2,
	B1,
	B2,
	P1SPAWN,
	P2SPAWN;
	
	@Override
	public String toString() {
		return name().toLowerCase();
	}
}
