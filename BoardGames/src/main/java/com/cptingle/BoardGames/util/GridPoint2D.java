package com.cptingle.BoardGames.util;

import java.io.Serializable;

public class GridPoint2D implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1944843907428590761L;

	/**
	 * X Location
	 */
	private int x;

	/**
	 * Z Location
	 */
	private int z;

	/**
	 * Constructor
	 */
	public GridPoint2D(int x, int z) {
		this.x = x;
		this.z = z;
	}

	/**
	 * Point Setters
	 */
	public void setX(int x) {
		this.x = x;
	}

	public void setZ(int z) {
		this.z = z;
	}

	public void setLocation(int x, int z) {
		this.x = x;
		this.z = z;
	}

	/**
	 * Point getters
	 */
	public int getX() {
		return x;
	}

	public int getZ() {
		return z;
	}

	public int X() {
		return x;
	}

	public int Z() {
		return z;
	}

	/**
	 * Other Methods
	 */
	public void flip() {
		int tempX = x;
		x = z;
		z = tempX;
	}

	public GridPoint2D translate(int tx, int ty) {
		return new GridPoint2D(x + tx, z + ty);
	}

	/**
	 * Equals
	 */
	public boolean equals(Object obj) {
		if (obj instanceof GridPoint2D) {
			GridPoint2D other = (GridPoint2D) obj;
			if (other.getX() == this.x && other.getZ() == this.z) {
				return true;
			}
		}
		return false;
	}

	public int hashCode() {
		return Integer.parseInt("" + Math.abs(x) + "" + Math.abs(z));
	}

	public String toString() {
		return "X: " + X() + "   Z:" + Z();
	}

}
