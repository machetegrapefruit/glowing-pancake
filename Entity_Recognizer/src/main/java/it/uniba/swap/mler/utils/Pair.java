package it.uniba.swap.mler.utils;

import java.util.HashMap;
import java.util.Map;

public class Pair<K, V> {
	private K v1;
	private V v2;
	
	public Pair(K v1, V v2) {
		this.v1 = v1;
		this.v2 = v2;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Pair<?, ?>) {
			return ((Pair<?, ?>) o).v1.equals(this.v1) && ((Pair<?, ?>) o).v2.equals(this.v2);
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return this.v1.hashCode() + this.v2.hashCode();
	}
	
	public K getFirst() {
		return this.v1;
	}
	
	public V getSecond() {
		return this.v2;
	}
	
	public static void main(String[] args) {
		Pair<String, String> x = new Pair<>("a", "b");
		Pair<String, String> y = new Pair<>("a", "b");
		Map<Pair<String, String>, String> map = new HashMap<>();
		map.put(x, "c");
		System.out.println(x.equals(y));
		System.out.println(map.containsKey(y));
	}

}
