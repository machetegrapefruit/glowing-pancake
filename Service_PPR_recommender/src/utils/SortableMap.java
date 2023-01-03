package utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class SortableMap<K extends Comparable<K>, V extends Comparable<V>> implements Map<K, V>, Iterable<Entry<K, V>> {

	private Map<K, V> map;
	private SortType sortType = SortType.INCREASING;
	
	public SortableMap() {
		map = new LinkedHashMap<K, V>();
	}
	
	public void setSortType(SortType sortType) {
		this.sortType = sortType;
	}

	@Override
	public int size() {
		return map.size();
	}

	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return map.containsValue(value);
	}

	@Override
	public void clear() {
		map.clear();
	}


	@Override
	public Iterator<Entry<K, V>> iterator() {
		return new SortingIterator(sortType);
	}

	@Override
	public V get(Object key) {
		return map.get(key);
	}

	@Override
	public V put(K key, V value) {
		return map.put(key, value);
	}

	@Override
	public V remove(Object key) {
		return map.remove(key);
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		map.putAll(m);
	}

	@Override
	public Set<K> keySet() {
		return map.keySet();
	}

	@Override
	public Collection<V> values() {
		return map.values();
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		return map.entrySet();
	}

	private class SortingIterator implements Iterator<Entry<K, V>> {

		List<K> sortedKeyList;
		Iterator<K> sortedKeyListIterator;
		Map<K, V> sortedMap;
		Iterator<Entry<K, V>> sortedMapIterator;
		SortType sortType;
		
		private SortingIterator(SortType sortType) {
			this.sortType = sortType;
			
			sortedKeyList = new ArrayList<K>();
			Set<K> keys = map.keySet();
			sortedKeyList.addAll(keys);
			
			sortedKeyList.sort(new Comparator<K>() {
				
				@Override
				public int compare(K o1, K o2) {
					int result;
					if (SortingIterator.this.sortType.equals(SortType.INCREASING)) {
						result = map.get(o1).compareTo(map.get(o2));
					} else { // if sortType.equals(SortType.DECREASING)
						result = -1 * map.get(o1).compareTo(map.get(o2));
					}
					return result;
				}
				
			});
			
			sortedKeyListIterator = sortedKeyList.iterator();
			sortedMap = new LinkedHashMap<K, V>();
			
			for (Iterator<K> iterator = sortedKeyListIterator; iterator.hasNext();) {
				K key = iterator.next();
				sortedMap.put(key, map.get(key));
			}
			
			sortedMapIterator = sortedMap.entrySet().iterator();
		}
		
		@Override
		public boolean hasNext() {
			return sortedMapIterator.hasNext();
		}

		@Override
		public Entry<K, V> next() {
			return sortedMapIterator.next();
		}
		
	}
	
	public static enum SortType {
		INCREASING,
		DECREASING,
	}
}
