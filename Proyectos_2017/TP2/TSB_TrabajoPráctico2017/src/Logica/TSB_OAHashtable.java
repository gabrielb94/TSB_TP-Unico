/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Logica;

import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;

/**
 *
 * @author Emanuel Laurent
 * ULTIMA MODIFICACIÓN 4/11/17 18:01
 * @param <K> el tipo de los objetos que serán usados como clave en la tabla.
 * @param <V> el tipo de los objetos que serán los valores de la tabla.
 */
public class TSB_OAHashtable<K,V> implements Map<K,V>, Cloneable, Serializable 
{
    // la tabla hash: el arreglo que contiene los Objetos contenidos...
    // la tabla esta formada por casillas, cada casilla tiene un estado y a su respectivo Objeto almacenado,
    // en nuestro caso de tipo Entry.
    private Casilla<Map.Entry<K, V>> table[];
    // el tamaño inicial de la tabla (tamaño con el que fue creada)...
    private int initial_capacity;
    // la cantidad de objetos que contiene la tabla...
    private int count;
    // el factor de carga para calcular si hace falta un rehashing...
    private float load_factor; 
     // el tamaño máximo que podrá tener el arreglo de soprte...
    private final static int MAX_SIZE = Integer.MAX_VALUE;
    
    //************************ Atributos privados (para gestionar las vistas).
    private transient Set<K> keySet = null;
    private transient Set<Map.Entry<K,V>> entrySet = null;
    private transient Collection<V> values = null;
    
    // conteo de operaciones de cambio de tamaño (fail-fast iterator).
    protected transient int modCount;
    
    //************************ Constructores.

    /**
     * Crea una tabla vacía, con la capacidad inicial igual a 5 y con factor 
     * de carga igual al 50% de la capacidad. 
     */    
    public TSB_OAHashtable()
    {
        this(5);
    }
    
    /**
     * Crea una tabla vacía, con la capacidad inicial indicada y con el factor 
     * de carga indicado. Si la capacidad inicial indicada por initial_capacity 
     * es menor o igual a 0, la tabla será creada de tamaño 11. El factor de
     * carga se ajustará siempre al 50% de la capacidad.
     * @param initial_capacity la capacidad inicial de la tabla.
     */
    public TSB_OAHashtable(int initial_capacity)
    {
        if(initial_capacity <= 0) { initial_capacity = 11; }
        else
        {
            if(initial_capacity > TSB_OAHashtable.MAX_SIZE) 
            {
                initial_capacity = TSB_OAHashtable.MAX_SIZE;
            }
            else
                if(!esPrimo(initial_capacity))
                    initial_capacity = siguientePrimo(initial_capacity);
        }     
        this.table = new Casilla[initial_capacity];
        for(int i=0; i<table.length; i++)
        {
            table[i] = new Casilla<>();
        }
        this.initial_capacity = initial_capacity;
        this.load_factor = initial_capacity/2f;
        this.count = 0;
        this.modCount = 0;
    }
    
    /**
     * Crea una tabla a partir del contenido del Map especificado.
     * @param t el Map a partir del cual se creará la tabla.
     */     
    public TSB_OAHashtable(Map<? extends K,? extends V> t)
    {
        this(3);
        this.putAll(t);
    }
    
    //************************ Implementación de métodos especificados por Map.
    
    /**
     * Retorna la cantidad de elementos contenidos en la tabla.
     * @return la cantidad de elementos de la tabla.
     */
    @Override
    public int size() 
    {
        return this.count;
    }
    
    /**
     * Determina si la tabla está vacía (no contiene ningún elemento).
     * @return true si la tabla está vacía.
     */
    @Override
    public boolean isEmpty() 
    {
        return (this.count == 0);
    }
    
    /**
     * Determina si la clave key está en la tabla. 
     * @param key la clave a verificar.
     * @return true si la clave está en la tabla.
     * @throws NullPointerException si la clave es null.
     */
    @Override
    public boolean containsKey(Object key) 
    {
        return (this.get((K)key) != null);
    }
    
    /**
     * Determina si alguna clave de la tabla está asociada al objeto value que
     * entra como parámetro. Equivale a contains().
     * @param value el objeto a buscar en la tabla.
     * @return true si alguna clave está asociada efectivamente a ese value.
     */    
    @Override
    public boolean containsValue(Object value)
    {
        return this.contains(value);
    }
    
    /**
     * Retorna el objeto al cual está asociada la clave key en la tabla, o null 
     * si la tabla no contiene ningún objeto asociado a esa clave.
     * @param key la clave que será buscada en la tabla.
     * @return el objeto asociado a la clave especificada (si existe la clave) o 
     *         null (si no existe la clave en esta tabla).
     * @throws NullPointerException si key es null.
     * @throws ClassCastException si la clase de key no es compatible con la 
     *         tabla.
     */
    @Override
    public V get(Object key)
    {
       if(key == null) throw new NullPointerException("get(): parámetro null");
            
       Casilla<Map.Entry<K, V>> c = this.search_for_entry((K)key);
       return (c != null)? c.getEntry().getValue() : null;
    }
    
    /**
     * Asocia el valor (value) especificado, con la clave (key) especificada en
     * esta tabla. Si la tabla contenía previamente un valor asociado para la 
     * clave, entonces el valor anterior es reemplazado por el nuevo (y en este 
     * caso el tamaño de la tabla no cambia). 
     * @param key la clave del objeto que se quiere agregar a la tabla.
     * @param value el objeto que se quiere agregar a la tabla.
     * @return el objeto anteriormente asociado a la clave si la clave ya 
     *         estaba asociada con alguno, o null si la clave no estaba antes 
     *         asociada a ningún objeto.
     * @throws NullPointerException si key es null o value es null.
     */
    @Override
    public V put(K key, V value)
    {
        if(key == null || value == null) throw new NullPointerException("put(): parámetro null");
       
        int idMadre = this.h(key), idTumba = -1, i = idMadre, j = 1;
        boolean flag = false;
        Map.Entry<K, V> x;
        V old = null;
        while(table[i].getEstado() != 0)
        {
            if(table[i].getEstado() == 3) //si la casilla es de tipo tumba y no guarde ninguna tumba signfica que es la 1ra y la guardo.
            {
                if(idTumba == -1)
                    idTumba = i;
            }
            else
            {
                x = table[i].getEntry();
                if(key.equals(x.getKey())) //si ya existe el objeto, reemplazo su valor por el nuevo.
                {
                    old = x.getValue();
                    x.setValue(value);
                    flag = true;
                    break;
                }
            }
            i = (int) (idMadre + Math.pow(j, 2)); //exploracion cuadratica
            if(i > table.length -1)
                i = i - table.length;
            j++;
        }
        if(flag == false)
        {      
            Map.Entry<K, V> entry = new Entry<>(key, value);
            if(idTumba != -1)
                this.table[idTumba].setEntry(entry);
            else
                this.table[i].setEntry(entry);
            this.table[i].setEstado(1);
            this.count++;
            this.modCount++;
            if(this.count >= this.load_factor) this.rehash();
        }
        return old;
    }
    
    /**
     * Elimina de la tabla la clave key (y su correspondiente valor asociado).  
     * El método no hace nada si la clave no está en la tabla. 
     * @param key la clave a eliminar.
     * @return El objeto al cual la clave estaba asociada, o null si la clave no
     *         estaba en la tabla.
     * @throws NullPointerException - if the key is null.
     */
    @Override
    public V remove(Object key)
    {
        if(key == null) throw new NullPointerException("remove(): parámetro null");
        
        V old = null;
        Casilla<Map.Entry<K, V>> c = this.search_for_entry((K)key);
        if(c != null)
        {
            Map.Entry<K, V> x = c.getEntry();
            old = x.getValue();
            c.setEntry(null);
            c.setEstado(3);
            this.count--;
            this.modCount++; 
        } 
        return old;        
    }
    
    /**
     * Copia en esta tabla, todos los objetos contenidos en el map especificado.
     * Los nuevos objetos reemplazarán a los que ya existan en la tabla 
     * asociados a las mismas claves (si se repitiese alguna).
     * @param m el map cuyos objetos serán copiados en esta tabla. 
     * @throws NullPointerException si m es null.
     */
    @Override
    public void putAll(Map<? extends K, ? extends V> m) 
    {
        for(Map.Entry<? extends K, ? extends V> e : m.entrySet())
        {
            put(e.getKey(), e.getValue());
        }
    }
    
    /**
     * Elimina todo el contenido de la tabla, de forma de dejarla vacía. En esta
     * implementación además, la tabla vuelve a tener el tamaño que
     * inicialmente tuvo al ser creado el objeto.
     */
    @Override
    public void clear() 
    {
        this.table = new Casilla[this.initial_capacity];
        for(int i=0; i<table.length; i++)
        {
            table[i] = new Casilla<>();
        }
        this.count = 0;
        this.modCount++;
    }
    
    /**
     * Retorna un Set (conjunto) a modo de vista de todas las claves (key)
     * contenidas en la tabla. El conjunto está respaldado por la tabla, por lo 
     * que los cambios realizados en la tabla serán reflejados en el conjunto, y
     * viceversa. Si la tabla es modificada mientras un iterador está actuando 
     * sobre el conjunto vista, el resultado de la iteración será indefinido 
     * (salvo que la modificación sea realizada por la operación remove() propia
     * del iterador, o por la operación setValue() realizada sobre una entrada 
     * de la tabla que haya sido retornada por el iterador). El conjunto vista 
     * provee métodos para eliminar elementos, y esos métodos a su vez 
     * eliminan el correspondiente par (key, value) de la tabla (a través de las
     * operaciones Iterator.remove(), Set.remove(), removeAll(), retainAll() 
     * y clear()). El conjunto vista no soporta las operaciones add() y 
     * addAll() (si se las invoca, se lanzará una UnsuportedOperationException).
     * @return un conjunto (un Set) a modo de vista de todas las claves
     *         mapeadas en la tabla.
     */
    @Override
    public Set<K> keySet() 
    {
        if(keySet == null) 
        {  
            // keySet = Collections.synchronizedSet(new KeySet());
            keySet = new KeySet();
        }
        return keySet;  
    }
    
    /**
     * Retorna una Collection (colección) a modo de vista de todos los valores
     * (values) contenidos en la tabla. La colección está respaldada por la 
     * tabla, por lo que los cambios realizados en la tabla serán reflejados en 
     * la colección, y viceversa. Si la tabla es modificada mientras un iterador 
     * está actuando sobre la colección vista, el resultado de la iteración será 
     * indefinido (salvo que la modificación sea realizada por la operación 
     * remove() propia del iterador, o por la operación setValue() realizada 
     * sobre una entrada de la tabla que haya sido retornada por el iterador). 
     * La colección vista provee métodos para eliminar elementos, y esos métodos 
     * a su vez eliminan el correspondiente par (key, value) de la tabla (a 
     * través de las operaciones Iterator.remove(), Collection.remove(), 
     * removeAll(), removeAll(), retainAll() y clear()). La colección vista no 
     * soporta las operaciones add() y addAll() (si se las invoca, se lanzará 
     * una UnsuportedOperationException).
     * @return una colección (un Collection) a modo de vista de todas los 
     *         valores mapeados en la tabla.
     */
    @Override
    public Collection<V> values() 
    {
        if(values==null)
        {
            // values = Collections.synchronizedCollection(new ValueCollection());
            values = new ValueCollection();
        }
        return values;    
    }
    
    /**
     * Retorna un Set (conjunto) a modo de vista de todos los pares (key, value)
     * contenidos en la tabla. El conjunto está respaldado por la tabla, por lo 
     * que los cambios realizados en la tabla serán reflejados en el conjunto, y
     * viceversa. Si la tabla es modificada mientras un iterador está actuando 
     * sobre el conjunto vista, el resultado de la iteración será indefinido 
     * (salvo que la modificación sea realizada por la operación remove() propia
     * del iterador, o por la operación setValue() realizada sobre una entrada 
     * de la tabla que haya sido retornada por el iterador). El conjunto vista 
     * provee métodos para eliminar elementos, y esos métodos a su vez 
     * eliminan el correspondiente par (key, value) de la tabla (a través de las
     * operaciones Iterator.remove(), Set.remove(), removeAll(), retainAll() 
     * and clear()). El conjunto vista no soporta las operaciones add() y 
     * addAll() (si se las invoca, se lanzará una UnsuportedOperationException).
     * @return un conjunto (un Set) a modo de vista de todos los objetos 
     *         mapeados en la tabla.
     */
    @Override
    public Set<Map.Entry<K, V>> entrySet() 
    {
        if(entrySet == null) 
        {
            // entrySet = Collections.synchronizedSet(new EntrySet());
            entrySet = new EntrySet();
        }
        return entrySet;
    }
    
    //************************ Redefinición de métodos heredados desde Object.
    
    /**
     * Retorna una copia superficial de la tabla. Las listas de desborde o 
     * buckets que conforman la tabla se clonan ellas mismas, pero no se clonan 
     * los objetos que esas listas contienen: en cada bucket de la tabla se 
     * almacenan las direcciones de los mismos objetos que contiene la original. 
     * @return una copia superficial de la tabla.
     * @throws java.lang.CloneNotSupportedException si la clase no implementa la
     *         interface Cloneable.    
     */ 
    @Override
    protected Object clone() throws CloneNotSupportedException 
    {
        TSB_OAHashtable<K, V> t = (TSB_OAHashtable<K, V>)super.clone();
        t.table = new Casilla[table.length];
        System.arraycopy(this.table, 0, t.table, 0, table.length);
        t.keySet = null;
        t.entrySet = null;
        t.values = null;
        t.modCount = 0;
        return t;
    }

    /**
     * Determina si esta tabla es igual al objeto espeficicado.
     * @param obj el objeto a comparar con esta tabla.
     * @return true si los objetos son iguales.
     */
    @Override
    public boolean equals(Object obj) 
    {
        if(!(obj instanceof Map)) { return false; }
        
        Map<K, V> t = (Map<K, V>) obj;
        if(t.size() != this.size()) { return false; }

        try 
        {
            Iterator<Map.Entry<K,V>> i = this.entrySet().iterator();
            while(i.hasNext()) 
            {
                Map.Entry<K, V> e = i.next();
                K key = e.getKey();
                V value = e.getValue();
                if(t.get(key) == null) { return false; }
                else 
                {
                    if(!value.equals(t.get(key))) { return false; }
                }
            }
        } 
        
        catch (ClassCastException | NullPointerException e) 
        {
            return false;
        }

        return true;    
    }

    /**
     * Retorna un hash code para la tabla completa.
     * @return un hash code para la tabla.
     */
    @Override
    public int hashCode() 
    {
        if(this.isEmpty()) {return 0;}
        
        int hc = 0;
        for(Map.Entry<K, V> entry : this.entrySet())
        {
            hc += entry.hashCode();
        }
        
        return hc;
    }
    
    /**
     * Devuelve el contenido de la tabla en forma de String. Sólo por razones de
     * didáctica, se hace referencia explícita en esa cadena al contenido de 
     * cada una de las casillas de la tabla.
     * @return una cadena con el contenido completo de la tabla.
     */
    @Override
    public String toString() 
    {
        StringBuilder cad = new StringBuilder("");
        for(int i = 0; i < this.table.length; i++)
        {
            cad.append("\nCasilla ").append(i).append(":\n\t").append(this.table[i].toString());   
        }
        return cad.toString();
    }
    
    //************************ Métodos específicos de la clase.

    /**
     * Determina si alguna clave de la tabla está asociada al objeto value que
     * entra como parámetro. Equivale a containsValue().
     * @param value el objeto a buscar en la tabla.
     * @return true si alguna clave está asociada efectivamente a ese value.
     */
    public boolean contains(Object value)
    {
        if(value == null) return false;
        
        for(Casilla<Map.Entry<K, V>> c : this.table)
        {
            if(c.getEstado() == 1)
            {
                Map.Entry<K, V> entry = c.getEntry();
                if(value.equals(entry.getValue()))
                    return true;
            }            
        }
        return false;
    }
    
    /**
     * Incrementa el tamaño de la tabla y reorganiza su contenido. Se invoca 
     * automaticamente cuando se detecta que la cantidad de elementos en la 
     * tabla supera a cierto el valor critico dado por (load_factor). Si el
     * valor de load_factor es 5,5, esto implica que el límite antes de invocar 
     * rehash es de 6 elementos en tabla.
     */
    protected void rehash()
    {
        int old_length = this.table.length;
        
        // nuevo tamaño: doble del anterior, más uno para llevarlo a impar...
        int new_length = old_length * 2 + 1;
        
        // no permitir que la tabla tenga un tamaño mayor al límite máximo...
        // ... para evitar overflow y/o desborde de índices...
        if(new_length > TSB_OAHashtable.MAX_SIZE) 
        { 
            new_length = TSB_OAHashtable.MAX_SIZE;
        }
        else
        {
            if(!esPrimo(new_length))
                new_length = siguientePrimo(new_length);
        }

        // crear el nuevo arreglo con new_length casillas vacías...
        Casilla<Map.Entry<K, V>> temp[] = new Casilla[new_length];
        for(int j = 0; j < temp.length; j++) { temp[j] = new Casilla<>(); }
        
        // notificación fail-fast iterator... la tabla cambió su estructura...
        this.modCount++;  
       
        // recorrer el viejo arreglo y redistribuir los objetos que tenia...
        for(int i = 0; i < this.table.length; i++)
        {
           // entrar en la casilla numero i, y obtener su entry...
           Casilla<Map.Entry<K, V>> c = this.table[i];
           if(c.getEstado() == 1)
           {
               // obtener un objeto de la vieja lista...
               Map.Entry<K, V> x = c.getEntry();
               
               // obtener su nuevo valor de dispersión para el nuevo arreglo...
               K key = x.getKey();
               int idMadre = this.h(key, temp.length);
               int y = idMadre, j = 1;
               
               // insertarlo en el nuevo arreglo, en la casilla numero "y", pero fijandome que 
               // no esté ya usada por otro Objeto...
               while(temp[y].getEstado() != 0)
                {
                    y = (int) (idMadre + Math.pow(j, 2)); //exploracion cuadratica
                    if(y > temp.length -1)
                        y = y - temp.length;
                    j++;
                }
               temp[y].setEntry(x);
               temp[y].setEstado(c.getEstado());
           }
        }
       
        // cambiar la referencia table para que apunte a temp y actualizamos el load_factor..
        this.table = temp;
        this.load_factor = temp.length/2f; 
    }
    
    //************************ Métodos privados.
    
    /*
     * Función hash. Toma una clave entera k y calcula y retorna un índice 
     * válido para esa clave para entrar en la tabla.     
     */
    private int h(int k)
    {
        return h(k, this.table.length);
    }
    
    /*
     * Función hash. Toma un objeto key que representa una clave y calcula y 
     * retorna un índice válido para esa clave para entrar en la tabla.     
     */
    private int h(K key)
    {
        return h(key.hashCode(), this.table.length);
    }
    
    /*
     * Función hash. Toma un objeto key que representa una clave y un tamaño de 
     * tabla t, y calcula y retorna un índice válido para esa clave dado ese
     * tamaño.     
     */
    private int h(K key, int t)
    {
        return h(key.hashCode(), t);
    }
    
    /*
     * Función hash. Toma una clave entera k y un tamaño de tabla t, y calcula y 
     * retorna un índice válido para esa clave dado ese tamaño.     
     */
    private int h(int k, int t)
    {
        if(k < 0) k *= -1;
        return k % t;        
    }
    
    /*
     * Busca en la tabla un objeto Entry cuya clave coincida con key.
     * Si lo encuentra, retorna el objeto Casilla donde se encuentra ese Entry
     * Si no lo encuentra, retorna null.
     */
    private Casilla<Map.Entry<K, V>> search_for_entry(K key)
    {
        int idMadre = this.h(key.hashCode()), i = idMadre, j = 1;
        Map.Entry<K, V> x;
        while(table[i].getEstado() != 0)
        {
            x = table[i].getEntry();
            if(key.equals(x.getKey()))
            {
                return table[i];
            }
            i = (int) (idMadre + Math.pow(j, 2)); //exploracion cuadratica
            if(i > table.length -1)
                i = i - table.length;
            j++;
        }
        return null;
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
    
    //************************ Clases Internas.
    
    /*
     * Clase interna que representa una casilla de la tabla hash:
     * cada casilla posee un estado y almacena a su respectivo objeto Map.Entry<K, V>.
     * Son instancias de esta clase las que realmente se guardan en 
     * en cada uno de los objetos del arreglo table que se usan como soporte de 
     * la tabla.
     */
    private class Casilla<E> implements Serializable
    {
        int estado; //0 = abierta, 1 = cerrada, 2 = tumba
        E entry;
        
        public Casilla()
        {
            this.estado = 0;
        }

        public int getEstado()
        {
            return estado;
        }

        public E getEntry() 
        {
            return entry;
        }

        public void setEstado(int estado) 
        {
            this.estado = estado;
        }

        public void setEntry(E entry) 
        {
            this.entry = entry;
        }
        
        public boolean isEmpty()
        {
            return(this.estado != 1);
        }

        @Override
        public String toString()
        {
            if(this.estado != 1)
                return "{Estado = " + estado + '}';
            else
                return "{Estado = " + estado + ", " + entry.toString() + '}';
        } 
    }
    
    /*
     * Clase interna que representa los pares de objetos que se almacenan en la
     * tabla hash: son instancias de esta clase las que realmente se guardan en 
     * en cada uno de los objetos Casilla que se usan como soporte de 
     * la tabla. Lanzará una IllegalArgumentException si alguno de los dos 
     * parámetros es null.
     */
    private class Entry<K, V> implements Map.Entry<K, V>, Serializable
    {
        private K key;
        private V value;
        
        public Entry(K key, V value) 
        {
            if(key == null || value == null)
            {
                throw new IllegalArgumentException("Entry(): parámetro null...");
            }
            this.key = key;
            this.value = value;
        }
        
        @Override
        public K getKey() 
        {
            return key;
        }

        @Override
        public V getValue() 
        {
            return value;
        }

        @Override
        public V setValue(V value) 
        {
            if(value == null) 
            {
                throw new IllegalArgumentException("setValue(): parámetro null...");
            }
                
            V old = this.value;
            this.value = value;
            return old;
        }
       
        @Override
        public int hashCode() 
        {
            int hash = 7;
            hash = 61 * hash + Objects.hashCode(this.key);
            hash = 61 * hash + Objects.hashCode(this.value);            
            return hash;
        }

        @Override
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
        
        @Override
        public String toString()
        {
            return "(" + key.toString() + ", " + value.toString() + ")";
        }
    }
    
    /*
     * Clase interna que representa una vista de todos los PARES mapeados en la
     * tabla: si la vista cambia, cambia también la tabla que le da respaldo, y
     * viceversa. La vista es stateless: no mantiene estado alguno (es decir, no 
     * contiene datos ella misma, sino que accede y gestiona directamente datos
     * de otra fuente), por lo que no tiene atributos y sus métodos gestionan en
     * forma directa el contenido de la tabla. Están soportados los metodos para
     * eliminar un objeto (remove()), eliminar todo el contenido (clear) y la  
     * creación de un Iterator (que incluye el método Iterator.remove()).
     */ 
    private class EntrySet extends AbstractSet<Map.Entry<K, V>>
    {

        @Override
        public Iterator<Map.Entry<K, V>> iterator() 
        {
            return new EntrySetIterator();
        }

        /*
         * Verifica si esta vista (y por lo tanto la tabla) contiene al par 
         * que entra como parámetro (que debe ser de la clase Entry).
         */
        @Override
        public boolean contains(Object o) 
        {
            if(o == null) { return false; } 
            if(!(o instanceof Entry)) { return false; }
            
            Map.Entry<K, V> entry = (Map.Entry<K,V>)o;

            return TSB_OAHashtable.this.contains(entry);
        }

        /*
         * Elimina de esta vista (y por lo tanto de la tabla) al par que entra
         * como parámetro (y que debe ser de tipo Entry).
         */
        @Override
        public boolean remove(Object o) 
        {
            if(o == null) { throw new NullPointerException("remove(): parámetro null");}
            if(!(o instanceof Entry)) { return false; }

            Map.Entry<K, V> entry = (Map.Entry<K, V>) o;
            K key = entry.getKey();
            
            return TSB_OAHashtable.this.remove(key) != null;
        }

        @Override
        public int size() 
        {
            return TSB_OAHashtable.this.count;
        }

        @Override
        public void clear() 
        {
            TSB_OAHashtable.this.clear();
        }
        
        private class EntrySetIterator implements Iterator<Map.Entry<K, V>>
        {
            // índice de la casilla actualmente recorrida...
            private int current_cell;
            
            // índice de la casilla anterior (si se requiere en remove())...
            private int last_cell;
                        
            // contador de la cantidad de elementos que vamos recorriendo...
            private int count;
                        
            // flag para controlar si remove() está bien invocado...
            private boolean next_ok;
            
            // el valor que debería tener el modCount de la tabla completa...
            private int expected_modCount;
            
            /*
             * Crea un iterador comenzando fuera de la tabla. Activa el 
             * mecanismo fail-fast.
             */
            public EntrySetIterator()
            {
                current_cell = -1; 
                last_cell = -1;
                count = 0;
                next_ok = false;
                expected_modCount = TSB_OAHashtable.this.modCount;
            }

            /*
             * Determina si hay al menos un elemento en la tabla que no haya 
             * sido retornado por next(). 
             */
            @Override
            public boolean hasNext() 
            {
                // variable auxiliar t para simplificar accesos...
                Casilla<Map.Entry<K, V>> t[] = TSB_OAHashtable.this.table;

                if(TSB_OAHashtable.this.isEmpty()) { return false; }
                if(current_cell >= t.length) { return false; }
                
                // tabla lista?...
                if(count >= TSB_OAHashtable.this.size()) { return false; }
                   
                return true;
            }

            /*
             * Retorna el siguiente elemento disponible en la tabla.
             */
            @Override
            public Map.Entry<K, V> next()
            {
                // control: fail-fast iterator...
                if(TSB_OAHashtable.this.modCount != expected_modCount)
                {    
                    throw new ConcurrentModificationException("next(): modificación inesperada de tabla...");
                }
                
                if(!hasNext()) 
                {
                    throw new NoSuchElementException("next(): no existe el elemento pedido...");
                }
                
                // variable auxiliar t para simplificar accesos...
                Casilla<Map.Entry<K, V>> t[] = TSB_OAHashtable.this.table;
               
                // ...recordar el índice de la casilla que se va a abandonar..
                last_cell = current_cell;
                
                current_cell++;      
                // buscar la siguiente casilla no vacía, que DEBE existir, ya 
                // que se hasNext() retornó true...
                while(t[current_cell].isEmpty())
                {
                    current_cell++;
                }
                    
                // actualizar la referencia cell con el núevo índice...
                Casilla<Map.Entry<K, V>> c = t[current_cell];
                
                // avisar que next() fue invocado con éxito...
                next_ok = true;
                count ++;
                
                // y retornar el elemento alcanzado...
                return c.getEntry();
            }
            
            /*
             * Remueve el elemento actual de la tabla, dejando el iterador en la
             * posición anterior al que fue removido. El elemento removido es el
             * que fue retornado la última vez que se invocó a next(). El método
             * sólo puede ser invocado una vez por cada invocación a next().
             */
            @Override
            public void remove()
            {
                if(!next_ok) 
                { 
                    throw new IllegalStateException("remove(): debe invocar a next() antes de remove()..."); 
                }
                
                // eliminar el objeto que retornó next() la última vez...
                Casilla<Map.Entry<K, V>> c = TSB_OAHashtable.this.table[current_cell];
                V garbage = TSB_OAHashtable.this.remove(c.getEntry().getKey());

                // quedar apuntando al anterior al que se retornó...                
                if(last_cell != current_cell) 
                {
                    current_cell = last_cell;
                    count --;
                }

                // avisar que el remove() válido para next() ya se activó...
                next_ok = false;

                // fail_fast iterator: todo en orden...
                expected_modCount++;
            }     
        }
    }    
    
    /*
     * Clase interna que representa una vista de todos los VALORES mapeados en 
     * la tabla: si la vista cambia, cambia también la tabla que le da respaldo, 
     * y viceversa. La vista es stateless: no mantiene estado alguno (es decir, 
     * no contiene datos ella misma, sino que accede y gestiona directamente los
     * de otra fuente), por lo que no tiene atributos y sus métodos gestionan en
     * forma directa el contenido de la tabla. Están soportados los metodos para
     * eliminar un objeto (remove()), eliminar todo el contenido (clear) y la  
     * creación de un Iterator (que incluye el método Iterator.remove()).
     */ 
    private class ValueCollection extends AbstractCollection<V>
    {
        @Override
        public Iterator<V> iterator() 
        {
            return new ValueCollectionIterator();
        }
        
        @Override
        public int size() 
        {
            return TSB_OAHashtable.this.count;
        }
        
        @Override
        public boolean contains(Object o) 
        {
            return TSB_OAHashtable.this.containsValue(o);
        }
        
        @Override
        public void clear() 
        {
            TSB_OAHashtable.this.clear();
        }
        
        private class ValueCollectionIterator implements Iterator<V>
        {
            // índice de la casilla actualmente recorrida...
            private int current_cell;
            
            // índice de la casilla anterior (si se requiere en remove())...
            private int last_cell;
                        
            // contador de la cantidad de elementos que vamos recorriendo...
            private int count;
                        
            // flag para controlar si remove() está bien invocado...
            private boolean next_ok;
            
            // el valor que debería tener el modCount de la tabla completa...
            private int expected_modCount;
            
            /*
             * Crea un iterador comenzando fuera de la tabla. Activa el 
             * mecanismo fail-fast.
             */
            public ValueCollectionIterator()
            {
                current_cell = -1; 
                last_cell = -1;
                count = 0;
                next_ok = false;
                expected_modCount = TSB_OAHashtable.this.modCount;
            }

            /*
             * Determina si hay al menos un elemento en la tabla que no haya 
             * sido retornado por next(). 
             */
            @Override
            public boolean hasNext() 
            {
                // variable auxiliar t para simplificar accesos...
                Casilla<Map.Entry<K, V>> t[] = TSB_OAHashtable.this.table;

                if(TSB_OAHashtable.this.isEmpty()) { return false; }
                if(current_cell >= t.length) { return false; }
                
                // tabla lista?...
                if(count >= TSB_OAHashtable.this.size()) { return false; }
                   
                return true;
            }

            /*
             * Retorna el siguiente elemento disponible en la tabla.
             */
            @Override
            public V next() 
            {
                // control: fail-fast iterator...
                if(TSB_OAHashtable.this.modCount != expected_modCount)
                {    
                    throw new ConcurrentModificationException("next(): modificación inesperada de tabla...");
                }
                
                if(!hasNext()) 
                {
                    throw new NoSuchElementException("next(): no existe el elemento pedido...");
                }
                
                // variable auxiliar t para simplificar accesos...
                Casilla<Map.Entry<K, V>> t[] = TSB_OAHashtable.this.table;
               
                // ...recordar el índice de la casilla que se va a abandonar..
                last_cell = current_cell;
                
                current_cell++; 
                
                // buscar la siguiente casilla no vacía, que DEBE existir, ya 
                // que se hasNext() retornó true...
                while(t[current_cell].isEmpty())
                {
                    current_cell++;
                }
                    
                // actualizar la referencia cell con el núevo índice...
                Casilla<Map.Entry<K, V>> c = t[current_cell];
                
                // avisar que next() fue invocado con éxito...
                next_ok = true;
                count ++;
                
                // y retornar la clave del elemento alcanzado...
                V value = c.getEntry().getValue();
                return value;
            }
            
            /*
             * Remueve el elemento actual de la tabla, dejando el iterador en la
             * posición anterior al que fue removido. El elemento removido es el
             * que fue retornado la última vez que se invocó a next(). El método
             * sólo puede ser invocado una vez por cada invocación a next().
             */
            @Override
            public void remove() 
            {
                if(!next_ok) 
                { 
                    throw new IllegalStateException("remove(): debe invocar a next() antes de remove()..."); 
                }
                
                // eliminar el objeto que retornó next() la última vez...
                Casilla<Map.Entry<K, V>> c = TSB_OAHashtable.this.table[current_cell];
                V garbage = TSB_OAHashtable.this.remove(c.getEntry().getKey());

                // quedar apuntando al anterior al que se retornó...                
                if(last_cell != current_cell) 
                {
                    current_cell = last_cell;
                    count --;
                }

                // avisar que el remove() válido para next() ya se activó...
                next_ok = false;

                // fail_fast iterator: todo en orden...
                expected_modCount++;
            }     
        }
    }
    
    /*
     * Clase interna que representa una vista de todas los Claves mapeadas en la
     * tabla: si la vista cambia, cambia también la tabla que le da respaldo, y
     * viceversa. La vista es stateless: no mantiene estado alguno (es decir, no 
     * contiene datos ella misma, sino que accede y gestiona directamente datos
     * de otra fuente), por lo que no tiene atributos y sus métodos gestionan en
     * forma directa el contenido de la tabla. Están soportados los metodos para
     * eliminar un objeto (remove()), eliminar todo el contenido (clear) y la  
     * creación de un Iterator (que incluye el método Iterator.remove()).
     */
    private class KeySet extends AbstractSet<K>
    {
        @Override
        public Iterator<K> iterator() 
        {
            return new KeySetIterator();
        }
        
        @Override
        public int size() 
        {
            return TSB_OAHashtable.this.count;
        }
        
        @Override
        public boolean contains(Object o) 
        {
            return TSB_OAHashtable.this.containsKey(o);
        }
        
        @Override
        public boolean remove(Object o) 
        {
            return (TSB_OAHashtable.this.remove(o) != null);
        }
        
        @Override
        public void clear() 
        {
            TSB_OAHashtable.this.clear();
        }
        
        private class KeySetIterator implements Iterator<K>
        {
            // índice de la casilla actualmente recorrida...
            private int current_cell;
            
            // índice de la casilla anterior (si se requiere en remove())...
            private int last_cell;
                        
            // contador de la cantidad de elementos que vamos recorriendo...
            private int count;
                        
            // flag para controlar si remove() está bien invocado...
            private boolean next_ok;
            
            // el valor que debería tener el modCount de la tabla completa...
            private int expected_modCount;
            
            /*
             * Crea un iterador comenzando fuera de la tabla. Activa el 
             * mecanismo fail-fast.
             */
            public KeySetIterator()
            {
                current_cell = -1; 
                last_cell = -1;
                count = 0;
                next_ok = false;
                expected_modCount = TSB_OAHashtable.this.modCount;
            }

            /*
             * Determina si hay al menos un elemento en la tabla que no haya 
             * sido retornado por next(). 
             */
            @Override
            public boolean hasNext() 
            {
                // variable auxiliar t para simplificar accesos...
                Casilla<Map.Entry<K, V>> t[] = TSB_OAHashtable.this.table;

                if(TSB_OAHashtable.this.isEmpty()) { return false; }
                if(current_cell >= t.length) { return false; }
                
                // tabla lista?...
                if(count >= TSB_OAHashtable.this.size()) { return false; }
                
                return true;
            }

            /*
             * Retorna el siguiente elemento disponible en la tabla.
             */
            @Override
            public K next() 
            {
                // control: fail-fast iterator...
                if(TSB_OAHashtable.this.modCount != expected_modCount)
                {    
                    throw new ConcurrentModificationException("next(): modificación inesperada de tabla...");
                }
                
                if(!hasNext()) 
                {
                    throw new NoSuchElementException("next(): no existe el elemento pedido...");
                }
                
                // variable auxiliar t para simplificar accesos...
                Casilla<Map.Entry<K, V>> t[] = TSB_OAHashtable.this.table;
               
                // ...recordar el índice de la casilla que se va a abandonar..
                last_cell = current_cell;
                
                current_cell++; 
                
                // buscar la siguiente casilla no vacía, que DEBE existir, ya 
                // que se hasNext() retornó true...
                while(t[current_cell].isEmpty())
                {
                    current_cell++;
                }
                    
                // actualizar la referencia cell con el núevo índice...
                Casilla<Map.Entry<K, V>> c = t[current_cell];
                
                // avisar que next() fue invocado con éxito...
                next_ok = true;
                count ++;
                
                // y retornar la clave del elemento alcanzado...
                K key = c.getEntry().getKey();
                return key;
            }
            
            /*
             * Remueve el elemento actual de la tabla, dejando el iterador en la
             * posición anterior al que fue removido. El elemento removido es el
             * que fue retornado la última vez que se invocó a next(). El método
             * sólo puede ser invocado una vez por cada invocación a next().
             */
            @Override
            public void remove() 
            {
                if(!next_ok) 
                { 
                    throw new IllegalStateException("remove(): debe invocar a next() antes de remove()..."); 
                }
                
                // eliminar el objeto que retornó next() la última vez...
                Casilla<Map.Entry<K, V>> c = TSB_OAHashtable.this.table[current_cell];
                V garbage = TSB_OAHashtable.this.remove(c.getEntry().getKey());

                // quedar apuntando al anterior al que se retornó...                
                if(last_cell != current_cell) 
                {
                    current_cell = last_cell;
                    count --;
                }

                // avisar que el remove() válido para next() ya se activó...
                next_ok = false;

                // fail_fast iterator: todo en orden...
                expected_modCount++;
            }     
        }
    }
}

