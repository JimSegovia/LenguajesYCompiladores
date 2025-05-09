package al_zoolang.lexer;

public class sym {
    // Palabras reservadas (400-431)
    public static final int INIT_HABIT = 400;
    public static final int MAIN_ZOO = 401;
    public static final int FIN_HABIT = 402;
    public static final int CLASS_HABIT = 403;
    public static final int ACCED = 404;
    public static final int MODIF = 405;
    public static final int MET = 406;
    public static final int LIBRE = 407;
    public static final int ENCERRADO = 408;
    public static final int PROTECT = 409;
    public static final int COMPOR = 410;
    public static final int ENT = 411;
    public static final int ANT = 412;
    public static final int BOUL = 413;
    public static final int CORPSE = 414;
    public static final int STLORO = 415;
    public static final int TORT = 416;
    public static final int DEVOLVER = 417;
    public static final int RUGG = 418;
    public static final int RECI = 419;
    public static final int CAMA = 420;
    public static final int LEON = 421;
    public static final int MERODEAR = 422;
    public static final int RONDAR = 423;
    public static final int ME = 424;
    public static final int INSTINTO = 425;
    public static final int INSTINTO_FINAL = 426;
    public static final int REACCION = 427;
    public static final int HUIR = 428;
    public static final int VERDAD = 429;
    public static final int FALSO = 430;
    public static final int NULO = 504;
    
    // Operadores y símbolos (431-456)
    public static final int OP_IGUAL = 431;
    public static final int OP_DIF = 432;
    public static final int OP_MENOR = 433;
    public static final int OP_MENOR_IGUAL = 434;
    public static final int OP_MAYOR = 435;
    public static final int OP_MAYOR_IGUAL = 436;
    public static final int OP_Y = 437;
    public static final int OP_O = 438;
    public static final int OP_NO = 439;
    public static final int OP_SUMA = 440;
    public static final int OP_INCREMENTO = 444;
    public static final int OP_RESTA = 441;
    public static final int OP_DECREMENTO = 445;
    public static final int OP_MULT = 442;
    public static final int OP_DIV = 443;
    public static final int OP_ASIGN = 509;
    public static final int OP_ASIGN_ARR = 510;
    
    public static final int PAR_ABIERTO = 446;
    public static final int PAR_CERRADO = 447;
    public static final int LLAVE_ABIERTA = 448;
    public static final int LLAVE_CERRADA = 449;
    public static final int CORCH_ABIERTO = 450;
    public static final int CORCH_CERRADO = 451;
    public static final int PUNTO_COMA = 452;
    public static final int DOS_PUNTOS = 453;
    public static final int COMA = 454;
    public static final int PUNTO = 455;
    public static final int DOBLE_PUNTO = 456;
    
    // Identificadores y literales (500-507)
    public static final int ID_GLOBAL = 500;
    public static final int ID = 501;
    public static final int ID_CLASE = 502;
    public static final int LIT_STRING = 503;
    public static final int LIT_CHAR = 505;
    public static final int LIT_ENT = 506;
    public static final int LIT_REAL = 507;
    
    // Error
    public static final int ERROR = 911;
    public static final int EOF = -1;
}