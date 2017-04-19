/*
 * Copyright (C) 2017 Protium - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium.api.utils;

public class Pair < L, R > {

	private final L left;
	private final R right;

	public Pair(L left, R right) {
		this.left = left;
		this.right = right;
	}

	public L getLeft( ) {
		return left;
	}

	public R getRight( ) {
		return right;
	}

	@Override
	public int hashCode( ) {
		return left.hashCode() ^ right.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Pair)) return false;
		Pair pairo = (Pair) o;
		return this.left.equals(pairo.getLeft()) &&
			this.right.equals(pairo.getRight());
	}

	public String toString( ) {
		return "{ " + left.toString() + ", " + right.toString() + " }";
	}
}