package sample;

import java.io.Serializable;
import java.util.*;



public class TSB_OAHashtable<K, V> implements Map<K, V>, Cloneable, Serializable
{
    // --------------------------------- inicio atributos

    private int count;
    private float load_factor;
    private int initial_capacity;
    private final int max_size = Integer.MAX_VALUE;
    private int modCount;

    private Casilla[] tabla;

    private transient Set<K> keySet = null;
    private transient Set<Map.Entry<K,V>> entrySet = null;
    private transient Collection<V> values = null;

    // --------------------------------- fin atributos



    // --------------------------------- inicio constructores

    public TSB_OAHashtable()
    {
        this(11);
    }

    public TSB_OAHashtable(int capacity)
    {
        if(capacity <= 0) capacity = 11;
        if(capacity > max_size) capacity = max_size;

        tabla = new Casilla[capacity];

        for(int i=0; i<tabla.length; i++)
        {
            tabla[i] = new Casilla();
        }

        initial_capacity = capacity;
        count = 0;
        modCount = 0;
        load_factor = 0.5f;
    }

    public TSB_OAHashtable(Map<? extends K,? extends V> t)
    {
        this(11);
        this.putAll(t);
    }

    // --------------------------------- fin constructores



    // --------------------------------- inicio metodos de Map

    public int size()
    {
        return count;
    }

    public boolean isEmpty()
    {
        return (count == 0);
    }

    public boolean containsKey(Object key)
    {
        Casilla cas;

        for(int i=0; i<tabla.length; i++)
        {
            cas = tabla[i];

            if(cas.getEstado() == 1 && cas.getPar().getKey().equals(key))
            {
                return true;
            }
        }

        return false;
    }

    public boolean containsValue(Object value)
    {
        Casilla cas;

        for(int i=0; i<tabla.length; i++)
        {
            cas = tabla[i];

            if(cas.getEstado() == 1 && cas.getPar().getValue().equals(value))
            {
                return true;
            }
        }

        return false;
    }

    public V get(Object key)
    {
        Casilla cas;

        for(int i=0; i<tabla.length; i++)
        {
            cas = tabla[i];

            if(cas.getEstado() == 1 && cas.getPar().getKey().equals(key))
            {
                return (V) cas.getPar().getValue();
            }
        }

        return null;
    }

    public V put(K key, V value)
    {
        if(key == null || value == null) throw new NullPointerException("put(): parámetro null");

        Casilla cas;
        V old = null;

        for(int i=0; i<tabla.length; i++)
        {
            cas = tabla[i];

            if(cas.getEstado() == 1 && cas.getPar().getKey().equals(key))
            {
                old = (V) cas.getPar().getValue();
                cas.getPar().setValue(value);
                return old;
            }
        }

        Map.Entry<K, V> entry = new Entry<>(key, value);
        int indice = h(key);
        count++;
        insertarEntry(indice, entry);

        return old;
    }

    public V remove(Object key)
    {
        if(key == null) throw new NullPointerException("remove(): parámetro null");

        V old = null;

        Casilla cas;

        for(int i=0; i<tabla.length; i++)
        {
            cas = tabla[i];

            if(cas.getEstado() == 1 && cas.getPar().getKey().equals(key))
            {
                cas.setEstado(2);
                cas.setPar(null);
                count--;
            }
        }

        return old;
    }

    public void putAll(Map<? extends K,? extends V> m)
    {
        for(Map.Entry<? extends K, ? extends V> e : m.entrySet())
        {
            put(e.getKey(), e.getValue());
        }
    }

    public void clear()
    {
        tabla = new Casilla[11];

        for(int i=0; i<tabla.length; i++)
        {
            tabla[i] = new Casilla();
        }

        count = 0;
        modCount++;
    }

    public Set<K> keySet()
    {
        if(keySet == null)
        {
            keySet = new KeySet();
        }
        return keySet;
    }

    public Set<Map.Entry<K, V>> entrySet()
    {
        if(entrySet == null)
        {
            entrySet = new EntrySet();
        }
        return entrySet;
    }

    public Collection<V> values()
    {
        if(values==null)
        {
            values = new ValueCollection();
        }
        return values;
    }

    // --------------------------------- fin metodos de Map



    // ---------------------------------------------------  inicio privados

    private int h(int k)
    {
        return h(k, tabla.length);
    }

    private int h(K key)
    {
        return h(key.hashCode(), tabla.length);
    }

    private int h(K key, int t)
    {
        return h(key.hashCode(), t);
    }

    private int h(int k, int t)
    {
        if(k < 0) k *= -1;
        return k % t;
    }

    private void insertarEntry(int indice, Map.Entry<K, V> entry)
    {
        Casilla cas = tabla[indice];

        if(cas.getEstado() != 1)
        {
            cas.setEstado(1);
            cas.setPar(entry);
            if(averageLength() >= load_factor) rehash();
        }
        else
        {
            int est;
            int j = 1;
            int indMadre = indice;

            while(true)
            {
                indice = indMadre + j*j;

                while(indice >= tabla.length)
                {
                    indice = indice - tabla.length;
                }

                cas = tabla[indice];
                est = cas.getEstado();

                if(est == 0 || est == 2)
                {
                    cas.setEstado(1);
                    cas.setPar(entry);
                    if(averageLength() >= load_factor) rehash();
                    return;
                }

                j++;
            }
        }
    }

    private float averageLength()
    {
        float cou = count;
        float len = tabla.length;
        return cou / len;
    }

    private static boolean esPrimo(int num)
    {
        boolean esPrimo = true;
        double raiz;
        raiz = Math.sqrt(num);
        for(int i = 2; i <= raiz; i++)
        {
            if(num%i == 0)
            {
                esPrimo = false;
                break;
            }
        }
        return esPrimo;
    }

    private static final int siguientePrimo (int n)
    {
        if ( n % 2 == 0) n++;
        for ( ; !esPrimo(n); n+=2 );
        return n;
    }

    // --------------------------------------------------- fin privados



    // --------------------------------- inicio metodos desde Object

    public boolean equals(Object obj)
    {
        if(!(obj instanceof Map)) return false;

        Map<K, V> t = (Map<K, V>) obj;

        if(t.size() != this.size()) return false;

        try
        {
            Iterator<Map.Entry<K,V>> i = this.entrySet().iterator();

            while(i.hasNext())
            {
                Map.Entry<K, V> e = i.next();
                K key = e.getKey();
                V value = e.getValue();
                if(t.get(key) == null)
                {
                    return false;
                }
                else
                {
                    if(!value.equals(t.get(key))) return false;
                }
            }
        }
        catch (ClassCastException | NullPointerException e)
        {
            return false;
        }

        return true;
    }

    public int hashCode()
    {
        if(this.isEmpty()) return 0;

        int hc = 0;
        for(Map.Entry<K, V> entry : this.entrySet())
        {
            hc += entry.hashCode();
        }

        return hc;
    }

    protected Object clone() throws CloneNotSupportedException
    {
        TSB_OAHashtable<K, V> clon = (TSB_OAHashtable<K, V>) super.clone();

        clon.tabla = new Casilla[this.tabla.length];

        System.arraycopy(this.tabla, 0, clon.tabla, 0, tabla.length);
        clon.keySet = null;
        clon.entrySet = null;
        clon.values = null;
        clon.modCount = 0;
        return clon;
    }

    public String toString()
    {
        String str = "";
        Casilla cas;

        for(int i=0; i<tabla.length; i++)
        {
            cas = tabla[i];

            if(cas.getEstado() == 1)
            {
                K clave = (K) cas.getPar().getKey();
                V valor = (V) cas.getPar().getValue();
                str = str + "Indice: " + i + "   -   Par: (" + clave + ", " + valor + ")\n";
            }
        }

        return str;
    }

    // --------------------------------- fin metodos desde Object



    // --------------------------------- inicio metodos especificos de la clase

    public boolean contains(Object value)
    {
        Casilla cas;

        for(int i=0; i<tabla.length; i++)
        {
            cas = tabla[i];

            if(cas.getEstado() == 1 && cas.getPar().getValue().equals(value))
            {
                return true;
            }
        }

        return false;
    }

    protected void rehash()
    {
        int old_length = this.tabla.length;

        int new_length = old_length * 2 + 1;

        if (new_length > max_size)
        {
            new_length = max_size;
        }
        else
        {
            if (!esPrimo(new_length))
            {
                new_length = siguientePrimo(new_length);
            }
        }

        Casilla[] temp = new Casilla[new_length];

        for (int i = 0; i < temp.length; i++)
        {
            temp[i] = new Casilla();
        }

        Casilla cas, casTemp;

        for(int i = 0; i < tabla.length; i++)
        {
            cas = tabla[i];

            if(cas.getEstado() == 1)
            {
                int indice = h((K) cas.getPar().getKey(), temp.length);
                int indMad = indice;

                casTemp = temp[indice];

                int j = 1;

                while (casTemp.getEstado() != 0)
                {
                    indice = indMad + j * j;

                    while (indice >= temp.length)
                    {
                        indice = indice - temp.length;
                    }

                    casTemp = temp[indice];

                    j++;
                }

                casTemp.setEstado(1);
                casTemp.setPar(cas.getPar());
            }

        }

        this.tabla = temp;
        this.modCount++;
    }

    // --------------------------------- fin metodos especificos de la clase



    // --------------------------------- inicio clases internas

    private class Casilla<K, V> implements Serializable
    {
        private int estado; // 0 ABIERTA, 1 CERRADA, 2 TUMBA
        private Map.Entry<K, V> par;

        public Casilla()
        {
            estado = 0;
            par = null;
        }

        public int getEstado()
        {
            return estado;
        }

        public void setEstado(int est)
        {
            estado = est;
        }

        public Map.Entry<K, V> getPar()
        {
            return par;
        }

        public void setPar(Map.Entry<K, V> p)
        {
            par = p;
        }

        public String toString()
        {
            return "Estado: " + estado + " - Par: " + par;
        }
    }

    private class Entry<K, V> implements Map.Entry<K, V>, Serializable
    {
        private K key;
        private V value;

        public Entry(K k, V v)
        {
            if(k == null || v == null)
            {
                throw new IllegalArgumentException("Entry(): parámetro null...");
            }
            key = k;
            value = v;
        }

        public V setValue(V v)
        {
            if(value == null)
            {
                throw new IllegalArgumentException("setValue(): parámetro null...");
            }

            V old = value;
            value = v;
            return old;
        }

        public V getValue()
        {
            return value;
        }

        public K getKey()
        {
            return key;
        }

        public String toString()
        {
            return "(" + key.toString() + ", " + value.toString() + ")";
        }

        public int hashCode()
        {
            int hash = 7;
            hash = 61 * hash + Objects.hashCode(this.key);
            hash = 61 * hash + Objects.hashCode(this.value);
            return hash;
        }

        public boolean equals(Object obj)
        {
            if (this == obj) { return true; }
            if (obj == null) { return false; }
            if (this.getClass() != obj.getClass()) { return false; }

            final Entry other = (Entry) obj;
            if (!Objects.equals(this.key, other.key)) { return false; }
            if (!Objects.equals(this.value, other.value)) { return false; }
            return true;
        }
    }

    private class KeySet extends AbstractSet<K>
    {
        public int size()
        {
            return TSB_OAHashtable.this.count;
        }

        public boolean contains(Object o)
        {
            return TSB_OAHashtable.this.containsKey(o);
        }

        public boolean remove(Object o)
        {
            return (TSB_OAHashtable.this.remove(o) != null);
        }

        public void clear()
        {
            TSB_OAHashtable.this.clear();
        }

        public Iterator<K> iterator()
        {
            return new KeySetIterator();
        }

        private class KeySetIterator implements Iterator<K>
        {
            private int casillaActual;
            private int casillaAnterior;
            private int contador;
            private boolean next_ok;
            private int exp_modCount;

            public KeySetIterator()
            {
                casillaActual = -1;
                casillaAnterior = -1;
                contador = 0;
                next_ok = false;
                exp_modCount = TSB_OAHashtable.this.modCount;
            }

            public boolean hasNext()
            {
                if(TSB_OAHashtable.this.isEmpty()) return false;
                if(casillaActual >= TSB_OAHashtable.this.tabla.length - 1) return false;
                if(contador >= TSB_OAHashtable.this.count) return false;

                return true;
            }

            public K next()
            {
                if(TSB_OAHashtable.this.modCount != exp_modCount)
                {
                    throw new ConcurrentModificationException("next(): modificación inesperada de tabla...");
                }

                if(!hasNext())
                {
                    throw new NoSuchElementException("next(): no existe el elemento pedido...");
                }

                casillaAnterior = casillaActual;

                casillaActual++;

                while(TSB_OAHashtable.this.tabla[casillaActual].getEstado() != 1)
                {
                    casillaActual++;
                }

                contador++;
                next_ok = true;

                return (K) TSB_OAHashtable.this.tabla[casillaActual].getPar().getKey();
            }

            public void remove()
            {
                if(!next_ok)
                {
                    throw new IllegalStateException("remove(): debe invocar a next() antes de remove()...");
                }

                Casilla<K, V> cas = TSB_OAHashtable.this.tabla[casillaActual];
                TSB_OAHashtable.this.remove(cas.getPar().getKey());

                TSB_OAHashtable.this.count--;

                if(casillaAnterior != casillaActual)
                {
                    casillaActual = casillaAnterior;
                    contador --;
                }

                next_ok = false;

                TSB_OAHashtable.this.modCount++;
                exp_modCount++;
            }
        }
    }

    private class EntrySet extends AbstractSet<Map.Entry<K, V>>
    {
        public int size()
        {
            return TSB_OAHashtable.this.count;
        }

        public boolean contains(Object o)
        {
            if(o == null) return false;
            if(!(o instanceof Entry)) return false;

            Map.Entry<K, V> obj = (Map.Entry<K, V>) o;

            int indice = h(obj.getKey());

            if(tabla[indice].getPar().equals(obj))
            {
                return true;
            }
            else
            {
                return false;
            }
        }

        public boolean remove(Object o)
        {
            if(o == null) throw new NullPointerException("remove(): parámetro null");
            if(!(o instanceof Entry)) return false;

            Map.Entry<K, V> obj = (Map.Entry<K, V>) o;

            return TSB_OAHashtable.this.remove(obj.getKey()) != null;
        }

        public void clear()
        {
            TSB_OAHashtable.this.clear();
        }

        public Iterator<Map.Entry<K, V>> iterator()
        {
            return new EntrySetIterator();
        }

        private class EntrySetIterator implements Iterator<Map.Entry<K, V>>
        {
            private int casillaActual;
            private int casillaAnterior;
            private int contador;
            private boolean next_ok;
            private int exp_modCount;

            public EntrySetIterator()
            {
                casillaActual = -1;
                casillaAnterior = -1;
                contador = 0;
                next_ok = false;
                exp_modCount = TSB_OAHashtable.this.modCount;
            }

            public boolean hasNext()
            {
                if(TSB_OAHashtable.this.isEmpty()) return false;
                if(casillaActual >= TSB_OAHashtable.this.tabla.length - 1) return false;
                if(contador >= TSB_OAHashtable.this.count) return false;

                return true;
            }

            public Map.Entry<K, V> next()
            {
                if(TSB_OAHashtable.this.modCount != exp_modCount)
                {
                    throw new ConcurrentModificationException("next(): modificación inesperada de tabla...");
                }

                if(!hasNext())
                {
                    throw new NoSuchElementException("next(): no existe el elemento pedido...");
                }

                casillaAnterior = casillaActual;

                casillaActual++;

                while(TSB_OAHashtable.this.tabla[casillaActual].getEstado() != 1)
                {
                    casillaActual++;
                }

                contador++;
                next_ok = true;

                return (Map.Entry<K, V>) TSB_OAHashtable.this.tabla[casillaActual].getPar();
            }

            public void remove()
            {
                if(!next_ok)
                {
                    throw new IllegalStateException("remove(): debe invocar a next() antes de remove()...");
                }

                Casilla<K, V> cas = TSB_OAHashtable.this.tabla[casillaActual];
                TSB_OAHashtable.this.remove(cas.getPar().getKey());

                TSB_OAHashtable.this.count--;

                if(casillaAnterior != casillaActual)
                {
                    casillaActual = casillaAnterior;
                    contador --;
                }

                next_ok = false;

                TSB_OAHashtable.this.modCount++;
                exp_modCount++;
            }
        }
    }

    private class ValueCollection extends AbstractCollection<V>
    {
        public int size()
        {
            return TSB_OAHashtable.this.count;
        }

        public boolean contains(Object o)
        {
            return TSB_OAHashtable.this.containsValue(o);
        }

        public void clear()
        {
            TSB_OAHashtable.this.clear();
        }

        public Iterator<V> iterator()
        {
            return new ValueCollectionIterator();
        }

        private class ValueCollectionIterator implements Iterator<V>
        {
            private int casillaActual;
            private int casillaAnterior;
            private int contador;
            private boolean next_ok;
            private int exp_modCount;

            public ValueCollectionIterator()
            {
                casillaActual = -1;
                casillaAnterior = -1;
                contador = 0;
                next_ok = false;
                exp_modCount = TSB_OAHashtable.this.modCount;
            }

            public boolean hasNext()
            {
                if(TSB_OAHashtable.this.isEmpty()) return false;
                if(casillaActual >= TSB_OAHashtable.this.tabla.length - 1) return false;
                if(contador >= TSB_OAHashtable.this.count) return false;

                return true;
            }

            public V next()
            {
                if(TSB_OAHashtable.this.modCount != exp_modCount)
                {
                    throw new ConcurrentModificationException("next(): modificación inesperada de tabla...");
                }

                if(!hasNext())
                {
                    throw new NoSuchElementException("next(): no existe el elemento pedido...");
                }

                casillaAnterior = casillaActual;

                casillaActual++;

                while(TSB_OAHashtable.this.tabla[casillaActual].getEstado() != 1)
                {
                    casillaActual++;
                }

                contador++;
                next_ok = true;

                return (V) TSB_OAHashtable.this.tabla[casillaActual].getPar().getValue();
            }

            public void remove()
            {
                if(!next_ok)
                {
                    throw new IllegalStateException("remove(): debe invocar a next() antes de remove()...");
                }

                Casilla<K, V> cas = TSB_OAHashtable.this.tabla[casillaActual];
                TSB_OAHashtable.this.remove(cas.getPar().getKey());

                TSB_OAHashtable.this.count--;

                if(casillaAnterior != casillaActual)
                {
                    casillaActual = casillaAnterior;
                    contador --;
                }

                next_ok = false;

                TSB_OAHashtable.this.modCount++;
                exp_modCount++;
            }
        }
    }

    // --------------------------------- fin clases internas
}
