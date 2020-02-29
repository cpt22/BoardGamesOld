package com.cptingle.BoardGames.games.checkers.components.exceptions;

public class InvalidSquareException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9006214368895107791L;

	public InvalidSquareException() {

	}

	public String toString() {
		return "Invalid square Selected";
	}
}
