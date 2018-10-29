package main.java.desarrollo.tsb.maven.main;

public class Utilities {
	public static int siguientePrimo(int n) {
        if ( n % 2 == 0) n++;
        n += 2;
        for ( ; !esPrimo(n); n += 2);
        return n;
    }

    public static boolean esPrimo(int n) {
        if (n % 2 == 0) return false;
        for (int i = 3; i < Math.sqrt(n); i += 2) {
            if (n % i == 0) {
                return false;
            }
        }
        return true;
    }

}
