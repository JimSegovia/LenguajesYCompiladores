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
            {"<ExpreParaRugg>", "<Valor>", "<MasExpreParaRugg>", "456"},
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
            return String.valueOf(token); // Token válido
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
        pila.clear();
        pos = 0;
        pila.push("0");

        while (true) {
            String estado = pila.peek();
            String simbolo = lexico();
            // Si el lexico devolvió el token de error léxico, detener análisis
            if (simbolo.equals("911")) {
                return false;
            }
            String accion = tabla.accion(estado, simbolo);
            
            String pilaActual = pilaToString();
            String entradaActual = entradaRestante();
            String accionTexto;

            // VALIDACIÓN CRÍTICA
            if (accion == null) {
                vista.agregarFila(pilaActual, entradaActual, "Error: acción inválida [" + simbolo + "]");
                return false;
            }

            if (accion.equals("Aceptar")) {
                vista.agregarFila(pilaActual, entradaActual, "Aceptar");
                return true;
            }
                   
            if (accion.equals("Aceptar")) {
            vista.agregarFila(pilaActual, entradaActual, "Aceptar");
            return true;    
            }
            
            if (accion.startsWith("d")) {              
                accionTexto = "Desplazar: " + simbolo + " -> estado " + accion.substring(1);
                vista.agregarFila(pilaActual, entradaActual, accionTexto);

                pila.push(simbolo);
                pila.push(accion.substring(1));
                pos++;

            }  else if (accion.startsWith("r")) {
                int numProd = Integer.parseInt(accion.substring(1));
                String[] produccion = producciones[numProd - 1 ];
                String lhs = produccion[0]; // Lado izquierdo
                int rhsLen = produccion.length - 1; // Cantidad de símbolos en RHS

                // Mostrar acción de reducción
                StringBuilder reduccion = new StringBuilder("Reducir: " + lhs + " -> ");
                for (int i = 1; i < produccion.length; i++) {
                    reduccion.append(produccion[i]).append(" ");
                }
                reduccion.append("por lookahead: ").append(simbolo);
                vista.agregarFila(pilaActual, entradaActual, reduccion.toString());

                // Paso 1: Desapilar 2*rhsLen elementos (símbolos y estados)
                for (int i = 0; i < rhsLen; i++) {
                    pila.pop(); // Símbolo
                    pila.pop(); // Estado
                }
                // Paso 2: Obtener el estado actual (después de desapilar)
                String estadoAnterior = pila.peek();
                // Paso 3: Empujar el lhs (no terminal) y buscar el nuevo estado
                pila.push(lhs);
                String gotoEstado = tabla.accion(estadoAnterior, lhs); // Buscar en la tabla
                if (gotoEstado == null) {
                    vista.agregarFila(pilaToString(), entradaRestante(), 
                        "Error: No hay transición GOTO para " + lhs + " desde estado " + estadoAnterior);
                    return false;
                }
                pila.push(gotoEstado); // Empujar el nuevo estado

            } else {      
            vista.agregarFila(pilaActual, entradaActual, "Error: acción inválida [" + simbolo + "]");
            return false;
            } 
        }  
    }
    
    private String pilaToString() {
        StringBuilder sb = new StringBuilder();
        for (String s : pila) {
            sb.append(s);
        }
        return sb.toString();
    }
}
