/*
 * To change this license header, choose License Headers in Project Properties. To change this template file, choose
 * Tools | Templates and open the template in the editor.
 */
package it.scrs.miner.models;

public class Pairs<K, V> {

	private K value1;
	private V value2;

	public Pairs(K value1, V value2) {
		this.value1 = value1;
		this.value2 = value2;
	}

	public Pairs() {

	}

	/**
	 * @return the value1
	 */
	public K getValue1() {

		return value1;
	}

	/**
	 * @param value1
	 *            the value1 to set
	 */
	public void setValue1(K value1) {

		this.value1 = value1;
	}

	/**
	 * @return the value2
	 */
	public V getValue2() {

		return value2;
	}

	/**
	 * @param value2
	 *            the value2 to set
	 */
	public void setValue2(V value2) {

		this.value2 = value2;
	}

	public Boolean isValue1(K value1) {
		if (this.value1.equals(value1))
			return Boolean.TRUE;
		return Boolean.FALSE;
	}

}
