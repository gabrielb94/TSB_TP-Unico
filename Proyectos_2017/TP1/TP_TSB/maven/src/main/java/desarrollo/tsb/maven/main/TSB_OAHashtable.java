package main.java.desarrollo.tsb.maven.main;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

@SuppressWarnings("unchecked")
public class TSB_OAHashtable<K, V> implements Map<K, V>, Serializable {

	private enum estadoCasillero {
        cerrado,
        tumba
    }
    private Entry[] objetos;
    private int count = 0;
    private int INITIAL_CAPACITY;
    private static final double LOAD_FACTOR = 0.75;

    private KeySet keys;
    private EntrySet entries;
    private ValueCollection values;

    public TSB_OAHashtable(int tamano) {
        if (!Utilities.esPrimo(tamano)) {
            tamano = Utilities.siguientePrimo(tamano);
        }
        objetos = (Entry[]) Array.newInstance(Entry.class, tamano);
        INITIAL_CAPACITY = tamano;
        keys = new KeySet();
        entries = new EntrySet();
        values = new ValueCollection();
    }

    public int size() {
        return count;
    }

    public boolean isEmpty() {
        return count == 0;
    }

    public boolean containsKey(Object key) {
        if (key == null) {
            throw new NullPointerException("Key a buscar es nula");
        }
        return (this.get(key) != null);
    }

    public boolean containsValue(Object value) {
        if (value == null) {
            throw new NullPointerException("Valor a buscar es nulo");
        }
        for (Entry objeto : objetos) {
            if (objeto == null) continue;
            if (objeto.getValue() == value) {
                return true;
            }
        }
        return false;
    }

    public V get(Object key) {
        if (key == null) {
            throw new NullPointerException("Key a buscar es nula");
        }
        V value = null;
        for (int x = hashear(key); objetos[x %= objetos.length] != null; x++) {
            if (objetos[x].getEstado() == estadoCasillero.tumba) {
                break;
            }
            if (objetos[x].getKey().equals(key)) {
                value = objetos[x].getValue();
            }
        }
        return value;
    }

    public V put(K key, V value) {
        if (key == null || value == null) {
            throw new IllegalArgumentException("Error en los argumentos");
        }
        if (count >= (objetos.length * LOAD_FACTOR)) {
            rehash();
        }

        if (containsKey(key)) {
            for (int x = hashear(key); objetos[x %= objetos.length] != null; x++) {
                if (objetos[x].getEstado() == estadoCasillero.cerrado && objetos[x].getKey().equals(key)) {
                    V viejo = objetos[x].getValue();
                    values.remove(viejo);
                    objetos[x].setValue(value);
                    values.add(value);
                    return viejo;
                }
            }
        } else {
            for (int x = hashear(key); ; x++) {
                x %= objetos.length;
                if (objetos[x] == null || objetos[x].getEstado() == estadoCasillero.tumba) {
                    Entry e = new Entry(key, value);
                    keys.add(key);
                    values.add(value);
                    entries.add(e);
                    objetos[x] = e;
                    count++;
                    break;
                }
            }
        }
        return null;
    }

    public V remove(Object key) {
        if (key == null) {
            throw new NullPointerException("La key a remover es nula");
        }
        if (containsKey(key)) {
            for (int x = hashear(key); objetos[x] != null; x++) {
                x %= objetos.length;
                if (objetos[x].getEstado() == estadoCasillero.cerrado && objetos[x].getKey() == key) {
                    V viejo = objetos[x].getValue();
                    values.remove(viejo);
                    keys.remove(key);
                    entries.remove(objetos[x]);
                    objetos[x].setEstado(estadoCasillero.tumba);
                    count--;
                    return viejo;
                }
            }
        }
        return null;
    }

    public void putAll(Map<? extends K, ? extends V> m) {
        for (Map.Entry<? extends K, ? extends V> e : m.entrySet()) {
            this.put(e.getKey(), e.getValue());
        }
    }

    public void clear() {
        count = 0;
        objetos = (Entry[]) Array.newInstance(Entry.class, INITIAL_CAPACITY);
        keys = new KeySet();
        entries = new EntrySet();
        values = new ValueCollection();
    }

    public Set<K> keySet() {
        return keys;
    }

    public Collection<V> values() {
        return values;
    }

    public Set<Map.Entry<K, V>> entrySet() {
        return entries;
    }

    public void rehash() {
        count = 0;
        Entry[] arrayViejo = objetos;
        objetos = (Entry[]) Array.newInstance(Entry.class, Utilities.siguientePrimo(objetos.length) * 2);
        for (Entry objeto : arrayViejo) {
            if (objeto == null || objeto.getEstado() == estadoCasillero.tumba) continue;
            put(objeto.key, objeto.value);
        }
    }

    public boolean contains(int value) {
        return containsValue(value);
    }

    //Funciones agregadas por nosotros
    private int hashear(Object o) {
        // 0x7fffffff obliga que el hashcode sea positivo
        if (o == null) {
            throw new NullPointerException();
        }
        return (o.hashCode() & 0x7fffffff) % objetos.length;
    }

    //Clase para representar los pares de objetos.
    private final class Entry implements Map.Entry<K, V>, Serializable {

        private K key;
        private V value;
        private estadoCasillero estado;

        public Entry(K key, V value) {
            if (key == null || value == null) {
                throw new IllegalArgumentException("Los argumentos de la entry son invalidos.");
            }
            this.key = key;
            this.value = value;
            this.estado = estadoCasillero.cerrado;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }

        public V setValue(V value) {
            if (value == null) {
                throw new IllegalArgumentException("Value entry es nulo.");
            }
            V old = this.value;
            this.value = value;
            return old;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 61 * hash + Objects.hashCode(this.key);
            hash = 61 * hash + Objects.hashCode(this.value);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) { return false; }
            if (this.getClass() != obj.getClass()) { return false; }

            Entry other = (Entry) obj;
            return Objects.equals(this.value, other.value) && Objects.equals(this.key, other.key);
        }

        @Override
        public String toString() {
            return "(Key: " + this.key + ", Value: " + this.value + ")";
        }

        public estadoCasillero getEstado() {
            return estado;
        }

        public void setEstado(estadoCasillero estado) {
            this.estado = estado;
        }
    }

    private final class KeySet extends AbstractSet<K> implements Serializable {

        private HashSet keys = new HashSet<K>();

        public Iterator<K> iterator() {
            return keys.iterator();
        }

        public int size() {
            return count;
        }

        public boolean contains(Object o) {
            return containsKey(o);
        }

        public boolean add(Object o) {
            return o != null && keys.add(o);
        }

        public boolean remove(Object o) {
            return o != null && keys.remove(o);
        }

    }

    private final class ValueCollection extends AbstractCollection<V> implements Serializable {

        private LinkedList values = new LinkedList<V>();

        public Iterator<V> iterator() {
            return values.iterator();
        }

        public int size() {
            return count;
        }

        public boolean contains(Object o) {
            return containsValue(o);
        }

        public boolean add(Object o) {
            return o != null && values.add(o);
        }

        public boolean remove(Object o) {
            return o != null && values.remove(o);
        }

    }

    private final class EntrySet extends AbstractSet<Map.Entry<K, V>> implements Serializable {

        private HashSet entries = new HashSet<Map.Entry<K, V>>();

        public Iterator<Map.Entry<K, V>> iterator() {
            return entries.iterator();
        }

        public int size() {
            return count;
        }

        public boolean add(Map.Entry<K, V> o) {
            return o != null && entries.add(o);
        }

        public boolean remove(Map.Entry<K, V> o) {
            return o != null && entries.remove(o);
        }

    }


	public V getOrDefault(Object key, V defaultValue) {
		// TODO Auto-generated method stub
		return null;
	}

	public void forEach(BiConsumer<? super K, ? super V> action) {
		// TODO Auto-generated method stub
		
	}

	public void replaceAll(
			BiFunction<? super K, ? super V, ? extends V> function) {
		// TODO Auto-generated method stub
		
	}


	public V putIfAbsent(K key, V value) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean remove(Object key, Object value) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean replace(K key, V oldValue, V newValue) {
		// TODO Auto-generated method stub
		return false;
	}

	public V replace(K key, V value) {
		// TODO Auto-generated method stub
		return null;
	}

	public V computeIfAbsent(K key,
			Function<? super K, ? extends V> mappingFunction) {
		// TODO Auto-generated method stub
		return null;
	}

	public V computeIfPresent(K key,
			BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
		// TODO Auto-generated method stub
		return null;
	}

	public V compute(K key,
			BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
		// TODO Auto-generated method stub
		return null;
	}

	public V merge(K key, V value,
			BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
		// TODO Auto-generated method stub
		return null;
	}

}
