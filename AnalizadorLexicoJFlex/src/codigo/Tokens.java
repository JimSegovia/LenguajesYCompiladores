package codigo;

public enum Tokens {
    // Palabras Reservadas (400-431)
    INIT_HABIT(400), MAIN_ZOOP(401), FIN_HABIT(402),
    CLASS_HABIT(403), ACCEDER(404), MODIFICAR(405),
    METODO(406), LIBRE(407), ENCERRADO(408), PROTECT(409),
    COMPOR(410), ENT(411), ANT(412), BOUL(413), CORPSE(414),
    STLORO(415), TORT(416), DEVOLVER(417), CAMA(420),
    LEON(421), MERODEAR(422), RONDAR(423), INSTINTO(425),
    INSTINTO_FINAL(426), REACCION(427), HUIR(428),
    VERDAD(429), FALSO(430), LIT_NULO(504),CHAR(231),

    // Operadores y Símbolos (431-456)
    IGUAL_IGUAL(431), DIFERENTE(432), MENOR(433),
    MENOR_IGUAL(434), MAYOR(435), MAYOR_IGUAL(436),
    AND(437), OR(438), NOT(439), SUMA(440), RESTA(441),
    MULTIPLICACION(442), DIVISION(443), INCREMENTO(444),
    DECREMENTO(445), PARENTESIS_IZQ(446), PARENTESIS_DER(447),
    LLAVE_IZQ(448), LLAVE_DER(449), CORCHETE_IZQ(450),
    CORCHETE_DER(451), PUNTO_COMA(452), DOS_PUNTOS(453),
    COMA(454), PUNTO(455), PUNTO_PUNTO(456), ASIGNACION(509),
    ASIGNACION_ESPECIAL(510),

    // Identificadores y Literales (500-510)
    ID_VAR(500), ID_CLASE(502), LIT_STRING(503),
    LIT_CHAR(505), LIT_ENT(506), LIT_REAL(507),

    // Error
    ERROR(911);

    private final int tokenNumber;
    Tokens(int tokenNumber) {
        this.tokenNumber = tokenNumber;
    }
    public int getTokenNumber() {
        return tokenNumber;
    }
}