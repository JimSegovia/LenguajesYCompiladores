package analizador_léxico_zoolang;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class Analizador_Léxico {
    private List<Integer> tokens;
    private List<String> lexemas;
    
    public Analizador_Léxico() {
        tokens = new ArrayList<>();
        lexemas = new ArrayList<>();
    }
    
    public void analizar(String entrada)
    {
        try {
            Lexer lexer = new Lexer(new StringReader(entrada));
            Tokens token;

            while ((token = lexer.yylex()) != Tokens.EOF) {
                int codigo = token.getTokenNumber();     // Asume que Tokens tiene este método
                String lexema = lexer.getLexema();       // Asume que Lexer tiene este método

                tokens.add(codigo);
                lexemas.add(lexema);
            }
        } catch (Exception ex) {
            System.err.println("Error al analizar: " + ex.getMessage());
        }
        
        tokens.add(36); // Token para '$'
        lexemas.add("$");
        
        //AQUI PROBAMOS EN LA CONSOLA SI SE REGISTRA LOS TOKENS Y LA ENTRADA ORIGINAL
        System.out.println("TOKENS: " + tokens);
        System.out.println("ENTRADA ORIGINAL: " + lexemas);
    }
    
    public List<Integer> getTokens() {
        return tokens;
    }

    public List<String> getEntradaOriginal() {
        return lexemas;
    }
}
