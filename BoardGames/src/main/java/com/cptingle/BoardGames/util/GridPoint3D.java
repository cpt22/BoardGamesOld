package com.cptingle.BoardGames.util;

import java.io.Serializable;

public class GridPoint3D implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1944843907428590761L;

	/**
	 * X Location
	 */
	private int x;
	
	/**
	 * Y Location
	 */
	private int y;
	
	/**
	 * Z Location
	 */
	private int z;
	
	/**
	 * Constructor
	 */
	public GridPoint3D(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	/**
	 * Point Setters
	 */
	public void setX(int x) {
		this.x = x;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
	public void setZ(int z) {
		this.z = z;
	}
	
	public void setLocation(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	
	/**
	 * Point getters
	 */
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getZ() {
		return z;
	}
	
	/**
	 * Other Methods
	 */
	@Deprecated
	public void flip() {
		int tempX = x;
		x = y;
		y = tempX;
	}
	
	public GridPoint3D translate(int dx, int dy, int dz) {
		return new GridPoint3D(x + dx, y + dy, z + dz);
	}
	
	/**
	 * Equals
	 */
	public boolean equals(Object obj) {
		if (obj instanceof GridPoint3D) {
			GridPoint3D other = (GridPoint3D) obj;
			if (other.getX() == this.x && other.getY() == this.y && other.getZ() == this.z) {
				return true;
			}
		}
		return false;
	}
	
	public int hashCode() {
		return Integer.parseInt("" + x + "" + y + "" + z);
	}

}
