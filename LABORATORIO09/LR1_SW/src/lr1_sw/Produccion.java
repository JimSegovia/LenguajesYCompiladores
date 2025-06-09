package lr1_sw;

import java.util.List;

public class Produccion {
    public String izq;
    public List<String> der;

    public Produccion(String izq, List<String> der) {
        this.izq = izq;
        this.der = der;
    }

    @Override
    public String toString() {
        return izq + " -> " + String.join(" ", der);
    }
}
