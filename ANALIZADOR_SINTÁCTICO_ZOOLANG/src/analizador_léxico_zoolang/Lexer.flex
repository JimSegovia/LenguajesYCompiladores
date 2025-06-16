package analizador_léxico_zoolang;
import static analizador_léxico_zoolang.Tokens.*;

%%
%class Lexer
%public
%unicode
%type Tokens  // Cambiado de LexerToken a Tokens
%line
%column
%{
    private  String lexema;  // Variable pública para almacenar el lexema
    public String getLexema() {
        return lexema;
    }
    
    // Métodos para acceder a posición
    public int getLinea() { return yyline; }
    public int getColumna() { return yycolumn; }

%}

/* ------------------- Expresiones ------------------- */
espacio     = [ \t\r\n]+
id      = [a-zA-Z][a-zA-Z0-9_\-]*
lit_str  = \"([^\"]|\\.)*\"
lit_char    = \'([^\']|\\.)\'
lit_ent     = [0-9]+
lit_decimal    = [0-9]+\.[0-9]+ 

/* ------------------ Palabras Clave ------------------ */
%%
{espacio}                   { /* Ignorar espacios */ }
"/*"([^*]|\*+[^*/])*"*/"    {lexema = yytext(); return COMENTARIO;}

"initHabit"         { lexema = yytext(); return INIT_HABIT; }
"mainZoo"           { lexema = yytext(); return MAIN_ZOOP; }
"finHabit"          { lexema = yytext(); return FIN_HABIT; }
"ClassHabit"        { lexema = yytext(); return CLASS_HABIT; }
"acced->"           { lexema = yytext(); return ACCEDER; }
"modif->"           { lexema = yytext(); return MODIFICAR; }
"met->"             { lexema = yytext(); return METODO; }
"libre"             { lexema = yytext(); return LIBRE; }
"encerrado"         { lexema = yytext(); return ENCERRADO; }
"protect"           { lexema = yytext(); return PROTECT; }
"compor"            { lexema = yytext(); return COMPOR; }
"ent"               { lexema = yytext(); return ENT; }
"ant"               { lexema = yytext(); return ANT; }
"boul"              { lexema = yytext(); return BOUL; }
"corpse"            { lexema = yytext(); return CORPSE; }
"stloro"            { lexema = yytext(); return STLORO; }
"char"              { lexema = yytext(); return CHAR; }
"self"              { lexema = yytext(); return SELF; }
"NUEVO"             { lexema = yytext(); return NUEVO; }
"INICIAR"           { lexema = yytext(); return INICIAR; }
"TORT"              { lexema = yytext(); return TORT; }
"devolver"          { lexema = yytext(); return DEVOLVER; }
"rugg"              { lexema = yytext(); return RUGIR; }
"reci"              { lexema = yytext(); return RECI; }
"cama"              { lexema = yytext(); return CAMA; }
"leon"              { lexema = yytext(); return LEON; }
"merodear"          { lexema = yytext(); return MERODEAR; }
"rondar"            { lexema = yytext(); return RONDAR; }
"me"                { lexema = yytext(); return ME; }
"instinto"          { lexema = yytext(); return INSTINTO; }
"instintoFinal"     { lexema = yytext(); return INSTINTO_FINAL; }
"reaccion"          { lexema = yytext(); return REACCION; }
"huir"              { lexema = yytext(); return HUIR; }
"verdad"            { lexema = yytext(); return VERDAD; }
"falso"             { lexema = yytext(); return FALSO; }

/* ------------------ Literales ------------------ */
{lit_str}         { lexema = yytext(); return LIT_STRING; }
{lit_char}           { lexema = yytext(); return LIT_CHAR; }
{lit_ent}            { lexema = yytext(); return LIT_ENT; }
{lit_decimal}           { lexema = yytext(); return LIT_REAL; }

/* ------------------ Identificadores ------------------ */
{id}             { lexema = yytext(); return ID; }

/* ------------------ Operadores y Símbolos ------------------ */
"=="                { lexema = yytext(); return IGUAL_IGUAL; }
"!="                { lexema = yytext(); return DIFERENTE; }
"<="                { lexema = yytext(); return MENOR_IGUAL; }
"<"                 { lexema = yytext(); return MENOR; }
">="                { lexema = yytext(); return MAYOR_IGUAL; }
">"                 { lexema = yytext(); return MAYOR; }
"y¡"                { lexema = yytext(); return AND; }
"o¡"                { lexema = yytext(); return OR; }
"!"                 { lexema = yytext(); return NOT; }
"+"                 { lexema = yytext(); return SUMA; }
"-"                 { lexema = yytext(); return RESTA; }
"*"                 { lexema = yytext(); return MULTIPLICACION; }
"/"                 { lexema = yytext(); return DIVISION; }
"++"                { lexema = yytext(); return INCREMENTO; }
"--"                { lexema = yytext(); return DECREMENTO; }
"("                 { lexema = yytext(); return PARENTESIS_IZQ; }
")"                 { lexema = yytext(); return PARENTESIS_DER; }
"{"                 { lexema = yytext(); return LLAVE_IZQ; }
"}"                 { lexema = yytext(); return LLAVE_DER; }
"["                 { lexema = yytext(); return CORCHETE_IZQ; }
"]"                 { lexema = yytext(); return CORCHETE_DER; }
";"                 { lexema = yytext(); return PUNTO_COMA; }
":"                 { lexema = yytext(); return DOS_PUNTOS; }
","                 { lexema = yytext(); return COMA; }
".."                { lexema = yytext(); return PUNTO_PUNTO; }
"."                 { lexema = yytext(); return PUNTO; }
"="                 { lexema = yytext(); return ASIGNACION; }
"=>"                { lexema = yytext(); return ASIGNACION_ESPECIAL; }
"<<"                { lexema = yytext(); return CONCATENACION; }

/* ------------------ Fin de Archivo ------------------ */
<<EOF>>             { return EOF; }
/* ------------------ Error ------------------ */
.                   { lexema = yytext(); return ERROR; }