package analizador_sintactico_zoolang;

import java.util.List;
import java.util.Stack;

public class Parser {
    private TablaCLR tabla;
    private Stack<String> pila;
    private String[][] producciones;
    private List<Integer> tokens;
    private List<String> entradaOriginal;
    private int pos;

    public Parser() {
        tabla = new TablaCLR();
        pila = new Stack<>();

        producciones = new String[][]{
            {"<S>", "400", "452", "<Declaraciones>", "401", "<BloqueInstr>", "453", "402"},
            {"<Declaraciones>", "<DeclVariable>", "<Declaraciones>"},
            {"<Declaraciones>", "<Clase>", "<Declaraciones>"},
            {"<Declaraciones>", "<FuncionesGlobales>", "<Declaraciones>"},
            {"<Declaraciones>"},
            {"<DeclVariable>", "<TipoPrimitivo>", "<DeclSimple>"},
            {"<DeclVariable>", "420", "<TipoPrimitivo>", "<DeclInicializada>"},
            {"<DeclVariable>", "500", "<DeclObj>"},
            {"<DeclVariable>", "<AsignacionObj>"},
            {"<DeclVariable>", "<AsignacionArreglo>"},
            {"<DeclVariable>", "<AsignacionElemArr>"},
            {"<DeclVariable>", "<AsignacionIdSimple>"},
            {"<DeclVariable>", "<AsignacionIdConArray>"},
            {"<DeclSimple>", "<ListaIds>", "456"},
            {"<DeclSimple>", "<DeclInicializada>"},
            {"<DeclSimple>", "<Arreglos>", "456"},
            {"<DeclInicializada>", "<Inicializar>", "456"},
            {"<DeclInicializada>", "<InicializarArreglos>", "456"},
            {"<DeclObj>", "<ObjSimple>"},
            {"<DeclObj>", "<ObjArreglo>"},
            {"<ObjSimple>", "500", "<IniObjOpcional>", "456"},
            {"<IniObjOpcional>", "506", "418", "500", "450", "<Argumentos>", "451"},
            {"<IniObjOpcional>"},
            {"<ObjArreglo>", "454", "455", "500", "<IniObjArregloOpcional>", "456"},
            {"<IniObjArregloOpcional>", "506", "418", "500", "454", "<ExprAritmeticas>", "455"},
            {"<IniObjArregloOpcional>"},
            {"<AsignacionObj>", "500", "506", "418", "500", "450", "<Argumentos>", "451", "456"},
            {"<AsignacionArreglo>", "500", "506", "418", "500", "454", "<ExprAritmeticas>", "455", "456"},
            {"<AsignacionIdSimple>", "500", "506", "<Valor>", "456"},
            {"<AsignacionIdConArray>", "500", "454", "<ExprAritmeticas>", "455", "506", "<Valor>", "456"},
            {"<AsignacionElemArr>", "500", "454", "<ExprAritmeticas>", "455", "506", "418", "500", "450", "<Argumentos>", "451", "456"},
            {"<ObjDirectoParametro>", "418", "500", "450", "<Argumentos>", "451"},
            {"<ListaIds>", "500", "<ListaIdsCont>"},
            {"<ListaIdsCont>", "458", "<ListaIds>"},
            {"<ListaIdsCont>"},
            {"<Inicializar>", "500", "506", "<Valor>", "<MasDeclInicializadas>"},
            {"<MasDeclInicializadas>", "458", "<Inicializar>"},
            {"<MasDeclInicializadas>"},
            {"<Arreglos>", "500", "454", "<ExprAritmeticas>", "455", "<MasArreglos>"},
            {"<MasArreglos>", "458", "<Arreglos>"},
            {"<MasArreglos>"},
            {"<InicializarArreglos>", "500", "454", "455", "507", "452", "<ListaValores>", "453", "<MasIniArreglos>"},
            {"<MasIniArreglos>", "458", "<InicializarArreglos>"},
            {"<MasIniArreglos>"},
            {"<ListaValores>", "<ListaIds>"},
            {"<ListaValores>", "<ListaString>"},
            {"<ListaValores>", "<ListaNum>"},
            {"<ListaValores>", "<ListaBool>"},
            {"<ListaValores>", "<ListaChar>"},
            {"<ListaString>", "501", "<MasString>"},
            {"<MasString>", "458", "<ListaString>"},
            {"<MasString>"},
            {"<ListaNum>", "<Num>", "<MasNum>"},
            {"<MasNum>", "458", "<ListaNum>"},
            {"<MasNum>"},
            {"<ListaBool>", "<ExpreBooleanos>", "<MasBool>"},
            {"<ExpreBooleanos>", "<Booleanos>"},
            {"<ExpreBooleanos>", "<ExpreLogica>"},
            {"<MasBool>", "458", "<ListaBool>"},
            {"<MasBool>"},
            {"<ListaChar>", "502", "<MasChar>"},
            {"<MasChar>", "458", "<ListaChar>"},
            {"<MasChar>"},
            {"<ListaExpreArit>", "<ExpreArit>", "<MasExpreArit>"},
            {"<MasExpreArit>", "458", "<ListaExpreArit>"},
            {"<MasExpreArit>"},
            {"<Argumentos>", "<Valor>", "<MasArgumentos>"},
            {"<Argumentos>"},
            {"<MasArgumentos>", "458", "<Argumentos>"},
            {"<MasArgumentos>"},
            {"<Valor>", "<ExpreCompletas>"},
            {"<Valor>", "501"},
            {"<Valor>", "502"},
            {"<Valor>", "500", "454", "<ExprAritmeticas>", "455"},
            {"<Num>", "503"},
            {"<Num>", "504"},
            {"<Booleanos>", "433"},
            {"<Booleanos>", "434"},
            {"<TipoPrimitivo>", "411"},
            {"<TipoPrimitivo>", "412"},
            {"<TipoPrimitivo>", "413"},
            {"<TipoPrimitivo>", "415"},
            {"<TipoPrimitivo>", "416"},
            {"<Clase>", "403", "500", "452", "<Miembros>", "453"},
            {"<Miembros>", "<Atributo>", "<Miembros>"},
            {"<Miembros>", "<Metodo>", "<Miembros>"},
            {"<Miembros>", "<Constructor>", "<Miembros>"},
            {"<Miembros>"},
            {"<Constructor>", "419", "500", "450", "<Parametros>", "451", "<BloqueInstr>"},
            {"<Atributo>", "<Acceso>", "<DeclVariable>"},
            {"<Acceso>", "407"},
            {"<Acceso>", "408"},
            {"<Acceso>", "409"},
            {"<Acceso>"},
            {"<Metodo>", "<Acceso>", "<Met>"},
            {"<Met>", "<MetodoConRetorno>"},
            {"<Met>", "<MetodoSinRetorno>"},
            {"<MetodoConRetorno>", "<TipoPrimitivo>", "<MetPrefijo>", "500", "450", "<Parametros>", "451", "452", "<ListaInstr>", "421", "<Valor>", "456", "453"},
            {"<MetodoSinRetorno>", "414", "<MetPrefijo>", "500", "450", "<Parametros>", "451", "<BloqueInstr>"},
            {"<MetPrefijo>", "406"},
            {"<MetPrefijo>", "404"},
            {"<MetPrefijo>", "405"},
            {"<FuncionesGlobales>", "410", "<Func>"},
            {"<Func>", "<FuncConRetorno>"},
            {"<Func>", "<FuncCorpse>"},
            {"<FuncConRetorno>", "<TipoPrimitivo>", "500", "450", "<Parametros>", "451", "452", "<ListaInstr>", "421", "<Valor>", "456", "453"},
            {"<FuncCorpse>", "414", "500", "450", "<Parametros>", "451", "<BloqueInstr>"},
            {"<Parametros>", "<VeriParametros>"},
            {"<Parametros>"},
            {"<VeriParametros>", "<TiposDeParametros>", "<ContParametros>"},
            {"<ContParametros>", "458", "<VeriParametros>"},
            {"<ContParametros>"},
            {"<TiposDeParametros>", "<TipoPrimitivo>", "500", "<ConArray>"},
            {"<TiposDeParametros>", "500", "<ConArray>", "500"},
            {"<TiposDeParametros>", "<ObjDirectoParametro>"},
            {"<ConArray>", "454", "455"},
            {"<ConArray>"},
            {"<BloqueInstr>", "452", "<ListaInstr>", "453"},
            {"<ListaInstr>", "<Instruccion>", "<ListaInstr>"},
            {"<ListaInstr>"},
            {"<Instruccion>", "<DeclVariable>"},
            {"<Instruccion>", "<Llamadas>"},
            {"<Instruccion>", "<Paso>"},
            {"<Instruccion>", "<Asignacion>"},
            {"<Instruccion>", "<ControlFlujo>"},
            {"<Instruccion>", "<Rugg>"},
            {"<Instruccion>", "<Reci>"},
            {"<Instruccion>", "432", "456"},
            {"<Asignacion>", "<Variable>", "506", "<Valor>", "456"},
            {"<Variable>", "417", "460", "500"},
            {"<Paso>", "500", "<MasMasMenosMenos>", "456"},
            {"<MasMasMenosMenos>", "448"},
            {"<MasMasMenosMenos>", "449"},
            {"<Rugg>", "422", "450", "<ExpreParaRugg>", "451", "456"},
            {"<Reci>", "423", "450", "500", "451", "456"},
            {"<ExpreParaRugg>", "<Valor>", "<MasExpreParaRugg>"},
            {"<MasExpreParaRugg>", "461", "<ExpreParaRugg>"},
            {"<MasExpreParaRugg>"},
            {"<ExpreCompletas>", "<Expre>"},
            {"<ExpreCompletas>", "<Llamadas>"},
            {"<Expre>", "<ExprLogica>"},
            {"<Expre>", "<ExprAritmeticas>"},
            {"<Expre>", "<Booleanos>"},
            {"<ExprAritmeticas>", "<Termino>", "<ExprAritmeticas’>"},
            {"<ExprAritmeticas’>", "<OpeSumaResta>", "<Termino>", "<ExprAritmeticas’>"},
            {"<ExprAritmeticas’>"},
            {"<Termino>", "<Factor>", "<Termino’>"},
            {"<Termino’>", "<OpeMulDiv>", "<Factor>", "<Termino’>"},
            {"<Termino’>"},
            {"<Factor>", "450", "<ExprAritmeticas>", "451"},
            {"<Factor>", "<Num>"},
            {"<Factor>", "500"},
            {"<OpeSumaResta>", "444"},
            {"<OpeSumaResta>", "445"},
            {"<OpeMulDiv>", "446"},
            {"<OpeMulDiv>", "447"},
            {"<ExprLogica>", "<CondicionLogica>"},
            {"<CondicionLogica>", "<Condicion>"},
            {"<CondicionLogica>", "<Condicion>", "<OpeLogico>", "<CondicionLogica>"},
            {"<Condicion>", "443", "<UnidadCondicional>"},
            {"<Condicion>", "<UnidadCondicional>"},
            {"<UnidadCondicional>", "<Comparacion>"},
            {"<UnidadCondicional>", "450", "<ExprLogica>", "451"},
            {"<Comparacion>", "<Var1>", "<OpeRelacionalNum>", "<Var1>"},
            {"<Comparacion>", "<Var1>", "<OpeIgualdad>", "<Var1>"},
            {"<Var1>", "<ExprAritmeticas>"},
            {"<Var2>", "<Var1>"},
            {"<Var2>", "501"},
            {"<Var2>", "502"},
            {"<Var2>", "<Booleanos>"},
            {"<OpeRelacionalNum>", "439"},
            {"<OpeRelacionalNum>", "437"},
            {"<OpeRelacionalNum>", "440"},
            {"<OpeRelacionalNum>", "438"},
            {"<OpeIgualdad>", "435"},
            {"<OpeIgualdad>", "436"},
            {"<OpeLogico>", "441"},
            {"<OpeLogico>", "442"},
            {"<Llamadas>", "500", "<FuncOMet>", "456"},
            {"<FuncOMet>", "450", "<Argumentos>", "451"},
            {"<FuncOMet>", "459", "500", "450", "<Argumentos>", "451"},
            {"<ControlFlujo>", "<IfElse>"},
            {"<ControlFlujo>", "<While>"},
            {"<ControlFlujo>", "<DoWhile>"},
            {"<ControlFlujo>", "<For>"},
            {"<ControlFlujo>", "<Switch>"},
            {"<IfElse>", "424", "450", "<Expre>", "451", "<BloqueInstr>", "425", "<IfElseAnidadas>"},
            {"<IfElseAnidadas>", "<IfElse>"},
            {"<IfElseAnidadas>", "<BloqueInstr>"},
            {"<While>", "426", "450", "<Expre>", "451", "<BloqueInstr>"},
            {"<DoWhile>", "428", "<BloqueInstr>", "426", "450", "<Expre>", "451", "456"},
            {"<For>", "427", "450", "<DeclOAsignacion>", "456", "<ExprLogica>", "456", "<IncreDecre>", "451", "<BloqueInstr>"},
            {"<Switch>", "429", "450", "500", "451", "452", "<ListaReacciones>", "453"},
            {"<ListaReacciones>", "<ReaccionBloque>", "<ListaReacciones>"},
            {"<ListaReacciones>", "430", "457", "<BloqueInstr>"},
            {"<ListaReacciones>"},
            {"<ReaccionBloque>", "431", "<definicion>", "457", "<BloqueInstr>"},
            {"<ReaccionBloque>", "431", "<definicion>", "457", "<BloqueInstr>", "432", "456"},
            {"<BloqueInstrHuir>", "<BloqueInstr>", "432", "456"},
            {"<ListaReaccion>", "<ReaccionBloque>", "<ListaReaccion>"},
            {"<ListaReaccion>"},
            {"<definicion>", "503"},
            {"<definicion>", "502"},
            {"<OpInstintoFinal>", "430", "457", "<BloqueInstr>"},
            {"<OpInstintoFinal>"},
            {"<DeclOAsignacion>", "<TipoPrimitivo>", "<Inicializar>"},
            {"<IncreDecre>", "500", "<ExpreOIncreDecre>", "<MasIncreDecre>"},
            {"<ExpreOIncreDecre>", "506", "<ExprAritmeticas>"},
            {"<ExpreOIncreDecre>", "<MasMasMenosMenos>"},
            {"<MasIncreDecre>", "<IncreDecre>"},
            {"<MasIncreDecre>"}
        };
    }
    private static final java.util.Map<String, String> equivalentes = new java.util.HashMap<>();

    static {
        equivalentes.put("400", "initHabit");
        equivalentes.put("401", "mainZoo");
        equivalentes.put("402", "finHabit");
        equivalentes.put("403", "classHabit");
        equivalentes.put("404", "acced->");
        equivalentes.put("405", "modif->");
        equivalentes.put("406", "met->");
        equivalentes.put("407", "libre");
        equivalentes.put("408", "encerrado");
        equivalentes.put("409", "protect");
        equivalentes.put("410", "compor");
        equivalentes.put("411", "ent");
        equivalentes.put("412", "ant");
        equivalentes.put("413", "boul");
        equivalentes.put("414", "corpse");
        equivalentes.put("415", "stloro");
        equivalentes.put("416", "char");
        equivalentes.put("417", "self");
        equivalentes.put("418", "NUEVO");
        equivalentes.put("419", "INICIAR");
        equivalentes.put("420", "TORT");
        equivalentes.put("421", "devolver");
        equivalentes.put("422", "rugg");
        equivalentes.put("423", "reci");
        equivalentes.put("424", "cama");
        equivalentes.put("425", "leon");
        equivalentes.put("426", "merodear");
        equivalentes.put("427", "rondar");
        equivalentes.put("428", "me");
        equivalentes.put("429", "instinto");
        equivalentes.put("430", "instintoFinal");
        equivalentes.put("431", "reaccion");
        equivalentes.put("432", "huir");
        equivalentes.put("433", "verdad");
        equivalentes.put("434", "falso");
        equivalentes.put("435", "==");
        equivalentes.put("436", "!=");
        equivalentes.put("437", "<");
        equivalentes.put("438", "<=");
        equivalentes.put("439", ">");
        equivalentes.put("440", ">=");
        equivalentes.put("441", "Y¡");
        equivalentes.put("442", "O¡");
        equivalentes.put("443", "!");
        equivalentes.put("444", "+");
        equivalentes.put("445", "-");
        equivalentes.put("446", "*");
        equivalentes.put("447", "/");
        equivalentes.put("448", "++");
        equivalentes.put("449", "--");
        equivalentes.put("450", "(");
        equivalentes.put("451", ")");
        equivalentes.put("452", "{");
        equivalentes.put("453", "}");
        equivalentes.put("454", "[");
        equivalentes.put("455", "]");
        equivalentes.put("456", ";");
        equivalentes.put("457", ":");
        equivalentes.put("458", ",");
        equivalentes.put("459", ".");
        equivalentes.put("460", "..");
        equivalentes.put("461", "<<");
        equivalentes.put("500", "id");
        equivalentes.put("501", "lit_str");
        equivalentes.put("502", "lit_char");
        equivalentes.put("503", "lit_ent");
        equivalentes.put("504", "lit_decimal");
        equivalentes.put("505", "comentario");
        equivalentes.put("506", "=");
        equivalentes.put("507", "=>");
        equivalentes.put("911", "ERROR");
        equivalentes.put("999", "EOF");
    }

    private vista.VistaAnalizadorSintáctico vista;

    public void setTokens(List<Integer> tokens) {
        this.tokens = tokens;
    }
    public void setEntradaOriginal(List<String> entradaOriginal) {
        this.entradaOriginal = entradaOriginal;
    }
    private String lexico() {
    while (pos < tokens.size()) {
        int token = tokens.get(pos);
        if (token == 505) {
            // Ignorar comentario 
            pos++;
            continue;
        } else if (token == 911) {
            // Token de error léxico
            vista.agregarFila(pilaToString(), entradaRestante(), "Error léxico: símbolo no reconocido");
            return "911";
        } else {
            //return String.valueOf(token); // Retornar token valido
            //REEEMPLAZO PARA MOSTRAR EL TOKEN
            return equivalentes.getOrDefault(token, String.valueOf(token)); // realizar remplazo al imprimir 

        }
    }
    return null; // $ (fin de entrada real)
    }
    private String entradaRestante() {
        StringBuilder sb = new StringBuilder();
        for (int i = pos; i < entradaOriginal.size(); i++) {
            sb.append(entradaOriginal.get(i)).append(" ");
        }
        return sb.toString().trim();
    }
    
    public void setVista(vista.VistaAnalizadorSintáctico vista) {
        this.vista = vista;
    }

    public boolean sintactico() {
    // Reinicia la pila y la posición del token actual
    pila.clear();
    pos = 0;
    pila.push("0");

    // Bucle principal del analizador sintáctico
    while (true) {
        String estado = pila.peek();         // Obtiene el estado actual de la pila
        String simbolo = lexico();           // Lee el siguiente símbolo/token de entrada

        if (simbolo.equals("911")) {
            return false; // Detiene si hay error léxico
        }

        String accion = tabla.accion(estado, simbolo); // Consulta la acción desde la tabla LR

        String pilaActual = pilaToString();         // Estado actual de la pila (para mostrar)
        String entradaActual = entradaRestante();   // Entrada restante (para mostrar)
        String accionTexto;

        if (accion == null) {
            vista.agregarFila(pilaActual, entradaActual, "Error: acción inválida [" + simbolo + "]");
            return false;
        }

        if (accion.equals("Aceptar")) {
            vista.agregarFila(pilaActual, entradaActual, "Aceptar");
            return true;
        }

        if (accion.startsWith("d")) {
            // Usa el diccionario 'equivalentes' para mostrar nombre legible del token
            String simboloNombre = equivalentes.getOrDefault(simbolo, simbolo);
            accionTexto = "Desplazar: " + simboloNombre + " -> estado " + accion.substring(1);
            
            // REEMPLAZO: si deseas mostrar el número del token directamente (sin equivalente), usa esta línea:
            // accionTexto = "Desplazar: " + simbolo + " -> estado " + accion.substring(1);
            
            vista.agregarFila(pilaActual, entradaActual, accionTexto);

            pila.push(simbolo);               // Apila el símbolo
            pila.push(accion.substring(1));   // Apila el nuevo estado
            pos++;                            // Avanza al siguiente token
        }

        else if (accion.startsWith("r")) {
            int numProd = Integer.parseInt(accion.substring(1)); // Número de producción
            String[] produccion = producciones[numProd - 1];     // Producción a aplicar
            String lhs = produccion[0];                          // Lado izquierdo
            int rhsLen = produccion.length - 1;                  // Cantidad de símbolos en RHS

            // Construye mensaje de reducción con equivalentes legibles
            StringBuilder reduccion = new StringBuilder("Reducir: " + lhs + " -> ");
            for (int i = 1; i < produccion.length; i++) {
                String cuerpo = produccion[i];
                reduccion.append(equivalentes.getOrDefault(cuerpo, cuerpo)).append(" ");

                // REEMPLAZO: si deseas mostrar directamente el código simbólico sin equivalente:
                // reduccion.append(produccion[i]).append(" ");
            }

            reduccion.append("por lookahead: ").append(equivalentes.getOrDefault(simbolo, simbolo));

            // REEMPLAZO: si deseas mostrar directamente el token sin equivalente:
            // reduccion.append("por lookahead: ").append(simbolo);

            vista.agregarFila(pilaActual, entradaActual, reduccion.toString());

            // Elimina 2 * RHS elementos de la pila (símbolo + estado por cada uno)
            for (int i = 0; i < rhsLen; i++) {
                pila.pop();
                pila.pop();
            }

            // Transición GOTO
            String estadoAnterior = pila.peek();
            pila.push(lhs);
            String gotoEstado = tabla.accion(estadoAnterior, lhs);
            if (gotoEstado == null) {
                vista.agregarFila(pilaToString(), entradaRestante(),
                    "Error: No hay transición GOTO para " + lhs + " desde estado " + estadoAnterior);
                return false;
            }
            pila.push(gotoEstado); // Apila el nuevo estado después del GOTO
        }

        else {
            // Acción no válida (ni shift, ni reduce, ni aceptar)
            vista.agregarFila(pilaActual, entradaActual, "Error: acción inválida [" + simbolo + "]");
            return false;
        }
    }
}

    
    private String pilaToString() {
        StringBuilder sb = new StringBuilder();
        for (String s : pila) {
            //sb.append(s);
            if (equivalentes.containsKey(s)) {
            sb.append(equivalentes.get(s)).append(" ");
        } else {
            sb.append(s).append(" ");
        }
        }
        return sb.toString();
    }
}
