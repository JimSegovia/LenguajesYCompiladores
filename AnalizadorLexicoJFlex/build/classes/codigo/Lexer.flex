package codigo;
import static codigo.Tokens.*;

%%
%class Lexer
%unicode
%type Tokens
%line
%column
%{
    public String lexeme;
%}

/* ------------------- Expresiones ------------------- */
espacio     = [ \t\r\n]+
LineTerminator = \r|\n|\r\n
ID_VAR      = [a-z_][a-zA-Z0-9_\-]*
ID_CLASE    = [A-Z][a-zA-Z0-9_\-]*
LIT_STRING  = \"([^\"]|\\.)*\"
LIT_CHAR    = \'([^\']|\\.)\'
LIT_ENT     = [0-9]+
LIT_REAL    = [0-9]+\.[0-9]

/* ------------------ Palabras Clave ------------------ */
%%
{espacio}                   { /* Ignorar espacios */ }
\/\/[^\n]*                  { /* Comentario de una línea */ }
\/\*([^*]|\*+[^*/])*\*+\/   { /* Comentario de bloque */ }

"initHabit"         { lexeme = yytext(); return INIT_HABIT; }
"mainZoo"           { lexeme = yytext(); return MAIN_ZOOP; }
"finHabit"          { lexeme = yytext(); return FIN_HABIT; }
"ClassHabit"        { lexeme = yytext(); return CLASS_HABIT; }
"acced->"           { lexeme = yytext(); return ACCEDER; }
"modif->"           { lexeme = yytext(); return MODIFICAR; }
"met->"             { lexeme = yytext(); return METODO; }
"char"              { lexeme = yytext(); return CHAR; }
"libre"             { lexeme = yytext(); return LIBRE; }
"encerrado"         { lexeme = yytext(); return ENCERRADO; }
"protect"           { lexeme = yytext(); return PROTECT; }
"compor"            { lexeme = yytext(); return COMPOR; }
"ent"               { lexeme = yytext(); return ENT; }
"ant"               { lexeme = yytext(); return ANT; }
"boul"              { lexeme = yytext(); return BOUL; }
"corpse"            { lexeme = yytext(); return CORPSE; }
"stloro"            { lexeme = yytext(); return STLORO; }
"TORT"              { lexeme = yytext(); return TORT; }
"devolver"          { lexeme = yytext(); return DEVOLVER; }
"cama"              { lexeme = yytext(); return CAMA; }
"leon"              { lexeme = yytext(); return LEON; }
"merodear"          { lexeme = yytext(); return MERODEAR; }
"rondar"            { lexeme = yytext(); return RONDAR; }
"instinto"          { lexeme = yytext(); return INSTINTO; }
"instintoFinal"     { lexeme = yytext(); return INSTINTO_FINAL; }
"reaccion"          { lexeme = yytext(); return REACCION; }
"huir"              { lexeme = yytext(); return HUIR; }
"verdad"            { lexeme = yytext(); return VERDAD; }
"falso"             { lexeme = yytext(); return FALSO; }
"nulo"              { lexeme = yytext(); return LIT_NULO; }

/* ------------------ Literales ------------------ */
{LIT_STRING}         { lexeme = yytext(); return LIT_STRING; }
{LIT_CHAR}           { lexeme = yytext(); return LIT_CHAR; }
{LIT_ENT}            { lexeme = yytext(); return LIT_ENT; }
{LIT_REAL}           { lexeme = yytext(); return LIT_REAL; }

/* ------------------ Identificadores ------------------ */
{ID_CLASE}           { lexeme = yytext(); return ID_CLASE; }
{ID_VAR}             { lexeme = yytext(); return ID_VAR; }

/* ------------------ Operadores y Símbolos ------------------ */
"=="                { lexeme = yytext(); return IGUAL_IGUAL; }
"!="                { lexeme = yytext(); return DIFERENTE; }
"<="                { lexeme = yytext(); return MENOR_IGUAL; }
"<"                 { lexeme = yytext(); return MENOR; }
">="                { lexeme = yytext(); return MAYOR_IGUAL; }
">"                 { lexeme = yytext(); return MAYOR; }
"y¡"                { lexeme = yytext(); return AND; }
"o¡"                { lexeme = yytext(); return OR; }
"!"                 { lexeme = yytext(); return NOT; }
"+"                 { lexeme = yytext(); return SUMA; }
"-"                 { lexeme = yytext(); return RESTA; }
"*"                 { lexeme = yytext(); return MULTIPLICACION; }
"/"                 { lexeme = yytext(); return DIVISION; }
"++"                { lexeme = yytext(); return INCREMENTO; }
"--"                { lexeme = yytext(); return DECREMENTO; }
"("                 { lexeme = yytext(); return PARENTESIS_IZQ; }
")"                 { lexeme = yytext(); return PARENTESIS_DER; }
"{"                 { lexeme = yytext(); return LLAVE_IZQ; }
"}"                 { lexeme = yytext(); return LLAVE_DER; }
"["                 { lexeme = yytext(); return CORCHETE_IZQ; }
"]"                 { lexeme = yytext(); return CORCHETE_DER; }
";"                 { lexeme = yytext(); return PUNTO_COMA; }
":"                 { lexeme = yytext(); return DOS_PUNTOS; }
","                 { lexeme = yytext(); return COMA; }
".."                { lexeme = yytext(); return PUNTO_PUNTO; }
"."                 { lexeme = yytext(); return PUNTO; }
"="                 { lexeme = yytext(); return ASIGNACION; }
"=>"                { lexeme = yytext(); return ASIGNACION_ESPECIAL; }

/* ------------------ Error ------------------ */
.                   { return ERROR; }

