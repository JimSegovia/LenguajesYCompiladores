package analizador_léxico_zoolang;

public enum Tokens {
    // Palabras Reservadas (400–434)
    
    // Inicio y estructura de hábitats
    INIT_HABIT(400),        // "initHabit"
    MAIN_ZOOP(401),         // "mainZoo"
    FIN_HABIT(402),         // "finHabit"

    // Definición de clases y métodos
    CLASS_HABIT(403),       // "ClassHabit"
    ACCEDER(404),           // "acced->"
    MODIFICAR(405),         // "modif->"
    METODO(406),            // "met->"

    // Estados y acciones
    LIBRE(407),             // "libre"
    ENCERRADO(408),         // "encerrado"
    PROTECT(409),           // "protect"
    COMPOR(410),            // "compor"

    // Tipos y valores
    ENT(411),               // "ent"
    ANT(412),               // "ant"
    BOUL(413),              // "boul"
    CORPSE(414),            // "corpse"
    STLORO(415),            // "stloro"
    CHAR(416),              // "char"

    // Referencias y objetos
    SELF(417),              // "self"
    NUEVO(418),             // "NUEVO"
    INICIAR(419),           // "INICIAR"

    // Acciones de métodos o funciones
    TORT(420),              // "TORT"
    DEVOLVER(421),          // "devolver"
    RUGIR(422),             // "rugg"
    RECI(423),              // "reci"
    CAMA(424),              // "cama"

    // Animales y comportamientos
    LEON(425),              // "leon"
    MERODEAR(426),          // "merodear"
    RONDAR(427),            // "rondar"
    ME(428),                // "me"
    INSTINTO(429),          // "instinto"
    INSTINTO_FINAL(430),    // "instintoFinal"
    REACCION(431),          // "reaccion"
    HUIR(432),              // "huir"

    // Booleanos
    VERDAD(433),            // "verdad"
    FALSO(434),             // "falso"


    // Operadores y Símbolos (435–461)
    // Comparación
    IGUAL_IGUAL(435),       // "=="
    DIFERENTE(436),         // "!="

    // Relacionales
    MENOR(437),             // "<"
    MENOR_IGUAL(438),       // "<="
    MAYOR(439),             // ">"
    MAYOR_IGUAL(440),       // ">="

    // Lógicos
    AND(441),               // "y¡"
    OR(442),                // "o¡"
    NOT(443),               // "!"

    // Aritméticos
    SUMA(444),              // "+"
    RESTA(445),             // "-"
    MULTIPLICACION(446),    // "*"
    DIVISION(447),          // "/"
    INCREMENTO(448),        // "++"
    DECREMENTO(449),        // "--"

    // Paréntesis, llaves y corchetes
    PARENTESIS_IZQ(450),    // "("
    PARENTESIS_DER(451),    // ")"
    LLAVE_IZQ(452),         // "{"
    LLAVE_DER(453),         // "}"
    CORCHETE_IZQ(454),      // "["
    CORCHETE_DER(455),      // "]"

    // Separadores y otros símbolos
    PUNTO_COMA(456),        // ";"
    DOS_PUNTOS(457),        // ":"
    COMA(458),              // ","
    PUNTO(459),             // "."
    PUNTO_PUNTO(460),       // ".."

    // Concatenación
    CONCATENACION(461),     // "<<"

    // Identificadores y Literales (500–911)
    // Identificadores
    ID(500),            // id = [a-zA-Z0-9_\-]* (común)

    // Literales
    LIT_STRING(501),        // “cadena”
    LIT_CHAR(502),          // ‘c’ o ‘9’
    LIT_ENT(503),           // número entero
    LIT_REAL(504),          // número decimal

    // Comentario en bloque
    COMENTARIO(505),        // /* comentario */

    // Asignación
    ASIGNACION(506),            // "="
    ASIGNACION_ESPECIAL(507),   // "=>"

    // Error
    ERROR(911),

    // Fin de archivo
    EOF(999);

    
    private final int tokenNumber;
    Tokens(int tokenNumber) {
        this.tokenNumber = tokenNumber;
    }
    public int getTokenNumber() {
        return tokenNumber;
    }
}