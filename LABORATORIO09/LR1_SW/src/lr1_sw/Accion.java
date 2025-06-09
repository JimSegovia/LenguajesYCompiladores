package lr1_sw;

public class Accion {
    public String tipo; // "shift", "reduce", "accept"
    public int valor;

    public Accion(String tipo, int valor) {
        this.tipo = tipo;
        this.valor = valor;
    }
}
