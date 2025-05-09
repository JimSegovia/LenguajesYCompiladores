package codigo;

public class LexerToken {
    private final Tokens token;
    private final String lexema;
    private final int linea;
    private final int columna;

    public LexerToken(Tokens token, String lexema, int linea, int columna) {
        this.token = token;
        this.lexema = lexema;
        this.linea = linea;
        this.columna = columna;
    }

    // Getters
    public Tokens getToken() { return token; }
    public String getLexema() { return lexema; }
    public int getLinea() { return linea; }
    public int getColumna() { return columna; }
}