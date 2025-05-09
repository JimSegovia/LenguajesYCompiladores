package al_zoolang.lexer;

import java_cup.runtime.Symbol;

%%

%class Lexer
%unicode
%line
%column
%cup
%public

%{
    private Symbol symbol(int type) {
        return new Symbol(type, yyline, yycolumn);
    }
    
    private Symbol symbol(int type, Object value) {
        return new Symbol(type, yyline, yycolumn, value);
    }
%}

LineTerminator = \r|\n|\r\n
WhiteSpace = {LineTerminator} | [ \t\f]

/* Identificadores */
Letra = [a-zA-Z_]
Digito = [0-9]
ID_GLOBAL = [a-z_][a-zA-Z0-9_\-]*
ID_CLASE = [A-Z][a-zA-Z0-9_\-]*

/* Literales */
LIT_STRING = \"([a-zA-Z0-9_\-])*\"
LIT_CHAR = \'([a-zA-Z0-9])\'
LIT_ENT = {Digito}+
LIT_REAL = {Digito}+\.{Digito}+

/* Comentarios */
COMENTARIO = \/\*([^*]|[\r\n]|(\*+([^*/]|[\r\n])))*\*+\/

%%

<YYINITIAL> {
    /* Palabras reservadas */
    "initHabit"     { return symbol(Tokens.INIT_HABIT); }
    "mainZoo"       { return symbol(Tokens.MAIN_ZOO); }
    "finHabit"      { return symbol(Tokens.FIN_HABIT); }
    "ClassHabit"    { return symbol(Tokens.CLASS_HABIT); }
    "acced->"       { return symbol(Tokens.ACCED); }
    "modif->"       { return symbol(Tokens.MODIF); }
    "met->"         { return symbol(Tokens.MET); }
    "libre"         { return symbol(Tokens.LIBRE); }
    "encerrado"     { return symbol(Tokens.ENCERRADO); }
    "protect"       { return symbol(Tokens.PROTECT); }
    "compor"        { return symbol(Tokens.COMPOR); }
    "ent"           { return symbol(Tokens.ENT); }
    "ant"           { return symbol(Tokens.ANT); }
    "boul"          { return symbol(Tokens.BOUL); }
    "corpse"        { return symbol(Tokens.CORPSE); }
    "stloro"        { return symbol(Tokens.STLORO); }
    "TORT"          { return symbol(Tokens.TORT); }
    "devolver"      { return symbol(Tokens.DEVOLVER); }
    "rugg"          { return symbol(Tokens.RUGG); }
    "reci"          { return symbol(Tokens.RECI); }
    "cama"          { return symbol(Tokens.CAMA); }
    "leon"          { return symbol(Tokens.LEON); }
    "merodear"      { return symbol(Tokens.MERODEAR); }
    "rondar"        { return symbol(Tokens.RONDAR); }
    "me"            { return symbol(Tokens.ME); }
    "instinto"      { return symbol(Tokens.INSTINTO); }
    "instintoFinal" { return symbol(Tokens.INSTINTO_FINAL); }
    "reaccion"      { return symbol(Tokens.REACCION); }
    "huir"          { return symbol(Tokens.HUIR); }
    "verdad"        { return symbol(Tokens.VERDAD); }
    "falso"         { return symbol(Tokens.FALSO); }
    "nulo"          { return symbol(Tokens.NULO); }
    
    /* Operadores */
    "=="            { return symbol(Tokens.OP_IGUAL); }
    "!="            { return symbol(Tokens.OP_DIF); }
    "<"             { return symbol(Tokens.OP_MENOR); }
    "<="            { return symbol(Tokens.OP_MENOR_IGUAL); }
    ">"             { return symbol(Tokens.OP_MAYOR); }
    ">="            { return symbol(Tokens.OP_MAYOR_IGUAL); }
    "y¡"            { return symbol(Tokens.OP_Y); }
    "o¡"            { return symbol(Tokens.OP_O); }
    "!"             { return symbol(Tokens.OP_NO); }
    "+"             { return symbol(Tokens.OP_SUMA); }
    "++"            { return symbol(Tokens.OP_INCREMENTO); }
    "-"             { return symbol(Tokens.OP_RESTA); }
    "--"            { return symbol(Tokens.OP_DECREMENTO); }
    "*"             { return symbol(Tokens.OP_MULT); }
    "/"             { return symbol(Tokens.OP_DIV); }
    "="             { return symbol(Tokens.OP_ASIGN); }
    "=>"            { return symbol(Tokens.OP_ASIGN_ARR); }
    
    /* Símbolos */
    "("             { return symbol(Tokens.PAR_ABIERTO); }
    ")"             { return symbol(Tokens.PAR_CERRADO); }
    "{"             { return symbol(Tokens.LLAVE_ABIERTA); }
    "}"             { return symbol(Tokens.LLAVE_CERRADA); }
    "["             { return symbol(Tokens.CORCH_ABIERTO); }
    "]"             { return symbol(Tokens.CORCH_CERRADO); }
    ";"             { return symbol(Tokens.PUNTO_COMA); }
    ":"             { return symbol(Tokens.DOS_PUNTOS); }
    ","             { return symbol(Tokens.COMA); }
    "."             { return symbol(Tokens.PUNTO); }
    ".."            { return symbol(Tokens.DOBLE_PUNTO); }
    
    /* Literales e identificadores */
    {LIT_STRING}    { return symbol(Tokens.LIT_STRING, yytext()); }
    {LIT_CHAR}      { return symbol(Tokens.LIT_CHAR, yytext().charAt(1)); }
    {LIT_ENT}       { return symbol(Tokens.LIT_ENT, Integer.parseInt(yytext())); }
    {LIT_REAL}      { return symbol(Tokens.LIT_REAL, Double.parseDouble(yytext())); }
    {ID_GLOBAL}     { return symbol(Tokens.ID_GLOBAL, yytext()); }
    {ID_CLASE}      { return symbol(Tokens.ID_CLASE, yytext()); }
    
    /* Comentarios y espacios */
    {COMENTARIO}    { /* Ignorar */ }
    {WhiteSpace}    { /* Ignorar */ }
}

/* Manejo de errores */
[^] { System.err.println("Error léxico: '" + yytext() + "' en línea " + (yyline+1) + ", columna " + (yycolumn+1)); 
      return symbol(Tokens.ERROR); }