package base;

import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/*
    Estructura basica casi lista.
    Faltan constructores de todas las clases (principal, internas, iteradoras).
    Escritos cabeceras metodos de Map, de Object y los especificos de la clase.
    Escritas clases para vistas con sus clases iteradoras.
*/

public class TSB_OAHashtable<K, V> implements Map<K, V>, Cloneable, Serializable
{

    // --------------------------------- inicio metodos de Map

    public int size()
    {
        return 0;
    }

    public boolean isEmpty()
    {
        return true;
    }

    public boolean containsKey(Object key)
    {
        return true;
    }

    public boolean containsValue(Object value)
    {
        return true;
    }

    public V get(Object key)
    {
        return null;
    }

    public V put(K key, V value)
    {
        return null;
    }

    public V remove(Object key)
    {
        return null;
    }

    public void putAll(Map<? extends K,? extends V> m)
    {

    }

    public void clear()
    {

    }

    public Set<K> keySet() // vista
    {
        return null;
    }

    public Set<Map.Entry<K, V>> entrySet() // vista
    {
        return null;
    }

    public Collection<V> values() // vista
    {
        return null;
    }

    // --------------------------------- fin metodos de Map


    // --------------------------------- inicio metodos desde Object

    public boolean equals(Object obj)
    {
        return true;
    }

    public int hashCode()
    {
        return 0;
    }

    protected Object clone() throws CloneNotSupportedException
    {
        return null;
    }

    public String toString()
    {
        return null;
    }

    // --------------------------------- fin metodos desde Object


    // --------------------------------- inicio metodos especificos de la clase

    public boolean contains(Object value)
    {
        return true;
    }

    protected void rehash()
    {

    }

    // --------------------------------- fin metodos especificos de la clase


    // --------------------------------- inicio clases internas

    private class Entry<K, V> implements Map.Entry<K, V>
    {
        public V setValue(V value)
        {
            return null;
        }

        public V getValue()
        {
            return null;
        }

        public K getKey()
        {
            return null;
        }
    }

    private class KeySet extends AbstractSet<K> // vista
    {
        public int size()
        {
            return 0;
        }

        public Iterator<K> iterator()
        {
            return new KeySetIterator();
        }

        private class KeySetIterator implements Iterator<K>
        {
            public boolean hasNext()
            {
                return true;
            }

            public K next()
            {
                return null;
            }

            public void remove()
            {

            }
        }
    }

    private class EntrySet extends AbstractSet<Map.Entry<K, V>> // vista
    {
        public int size()
        {
            return 0;
        }

        public Iterator<Map.Entry<K, V>> iterator()
        {
            return new EntrySetIterator();
        }

        private class EntrySetIterator implements Iterator<Map.Entry<K, V>>
        {
            public boolean hasNext()
            {
                return true;
            }

            public Map.Entry<K, V> next()
            {
                return null;
            }

            public void remove()
            {

            }
        }
    }

    private class ValueCollection extends AbstractCollection<V> // vista
    {
        public int size()
        {
            return 0;
        }

        public Iterator<V> iterator()
        {
            return new ValueCollectionIterator();
        }

        private class ValueCollectionIterator implements Iterator<V>
        {
            public boolean hasNext()
            {
                return true;
            }

            public V next()
            {
                return null;
            }

            public void remove()
            {

            }
        }
    }

    // --------------------------------- fin clases internas

}
