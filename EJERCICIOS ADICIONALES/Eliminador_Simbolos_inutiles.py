def obtener_terminales():
    """Obtiene los símbolos terminales del usuario"""
    while True:
        entrada = input("Ingresa los símbolos terminales (separados por coma): ").strip()
        if entrada:
            terminales = [t.strip() for t in entrada.split(',') if t.strip()]
            if terminales:
                return set(terminales)
        print("Por favor, ingresa al menos un símbolo terminal.")


def obtener_no_terminales():
    """Obtiene los símbolos no terminales del usuario"""
    while True:
        entrada = input("Ingresa los símbolos no terminales (separados por coma): ").strip()
        if entrada:
            no_terminales = [nt.strip() for nt in entrada.split(',') if nt.strip()]
            if no_terminales:
                return set(no_terminales)
        print("Por favor, ingresa al menos un símbolo no terminal.")


def obtener_simbolo_inicial(no_terminales):
    """Obtiene el símbolo inicial de la gramática"""
    while True:
        entrada = input("Ingresa el símbolo inicial: ").strip()
        if entrada in no_terminales:
            return entrada
        print(f"El símbolo inicial debe ser uno de los no terminales: {sorted(no_terminales)}")


def parsear_produccion(linea):
    """Parsea una línea de producción y devuelve (lado_izquierdo, [alternativas])"""
    linea = linea.strip()
    if not linea:
        return None, []

    if '→' in linea:
        partes = linea.split('→')
    elif '->' in linea:
        partes = linea.split('->')
    else:
        print(f"Error: La producción '{linea}' no tiene flecha válida (→ o ->)")
        return None, []

    if len(partes) != 2:
        print(f"Error: La producción '{linea}' no tiene formato válido")
        return None, []

    lado_izquierdo = partes[0].strip()
    lado_derecho = partes[1].strip()

    alternativas = []
    for alt in lado_derecho.split('|'):
        alt = alt.strip()
        if alt:
            if alt == 'ε' or alt == 'epsilon':
                simbolos = ['ε']
            else:
                simbolos = [s.strip() for s in alt.split() if s.strip()]
            alternativas.append(simbolos)

    return lado_izquierdo, alternativas


def obtener_producciones(terminales, no_terminales):
    """Obtiene las producciones del usuario"""
    print("\nIngresa las producciones (una por línea).")
    print("Formato: A → α | β (símbolos separados por espacios)")
    print("Usa 'ε' o 'epsilon' para producciones vacías")
    print("Presiona Enter en una línea vacía para terminar.")

    producciones = {}

    while True:
        linea = input("Producción: ").strip()
        if not linea:
            break

        lado_izq, alternativas = parsear_produccion(linea)

        if lado_izq is None:
            continue

        if lado_izq not in no_terminales:
            print(f"Advertencia: '{lado_izq}' no está en los no terminales declarados")

        if lado_izq not in producciones:
            producciones[lado_izq] = []

        producciones[lado_izq].extend(alternativas)

    return producciones


def encontrar_simbolos_generadores(producciones, terminales, no_terminales):
    """
    Encuentra los símbolos que pueden generar cadenas de terminales.
    Implementa el algoritmo V<i> iterativo hasta punto fijo.
    """
    print("\n=== PASO 1: ENCONTRANDO SÍMBOLOS GENERADORES ===")
    
    # V0 = Vt (terminales)
    V_actual = terminales.copy()
    paso = 0
    
    print(f"V{paso} = {sorted(V_actual)} (terminales)")
    
    while True:
        paso += 1
        V_anterior = V_actual.copy()
        
        # Para cada no terminal, verificar si todas sus producciones
        # generan símbolos que ya están en V_actual
        for no_terminal in no_terminales:
            if no_terminal in V_actual:
                continue  # Ya está incluido
                
            if no_terminal in producciones:
                # Verificar si al menos una producción genera símbolos en V_actual
                for produccion in producciones[no_terminal]:
                    if produccion == ['ε']:
                        # Producción vacía siempre genera terminales
                        V_actual.add(no_terminal)
                        break
                    elif all(simbolo in V_actual for simbolo in produccion):
                        # Todos los símbolos de la producción están en V_actual
                        V_actual.add(no_terminal)
                        break
        
        print(f"V{paso} = {sorted(V_actual)}")
        
        # Punto fijo alcanzado
        if V_actual == V_anterior:
            break
    
    simbolos_generadores = V_actual
    print(f"\n✅ Símbolos generadores finales: {sorted(simbolos_generadores)}")
    return simbolos_generadores


def eliminar_simbolos_no_generadores(producciones, simbolos_generadores, terminales, no_terminales):
    """
    Elimina los símbolos no generadores y retorna la gramática resultante.
    """
    print("\n=== ELIMINANDO SÍMBOLOS NO GENERADORES ===")
    
    # Identificar símbolos no generadores
    todos_simbolos = terminales.union(no_terminales)
    simbolos_no_generadores = todos_simbolos - simbolos_generadores
    
    if not simbolos_no_generadores:
        print("✅ No hay símbolos no generadores para eliminar.")
        return producciones, terminales, no_terminales
    
    print(f"Símbolos no generadores detectados: {sorted(simbolos_no_generadores)}")
    
    # Nuevos conjuntos sin símbolos no generadores
    nuevos_terminales = terminales.intersection(simbolos_generadores)
    nuevos_no_terminales = no_terminales.intersection(simbolos_generadores)
    
    # Nuevas producciones
    nuevas_producciones = {}
    
    for no_terminal in producciones:
        if no_terminal in simbolos_no_generadores:
            print(f"Eliminando no terminal: {no_terminal}")
            continue
            
        producciones_validas = []
        
        for produccion in producciones[no_terminal]:
            # Verificar si la producción contiene símbolos no generadores
            if produccion == ['ε']:
                producciones_validas.append(produccion)
            elif all(simbolo in simbolos_generadores for simbolo in produccion):
                producciones_validas.append(produccion)
            else:
                simbolos_no_gen_en_prod = [s for s in produccion if s not in simbolos_generadores]
                print(f"Eliminando producción {no_terminal} → {' '.join(produccion)} (contiene símbolos no generadores: {simbolos_no_gen_en_prod})")
        
        if producciones_validas:
            nuevas_producciones[no_terminal] = producciones_validas
    
    return nuevas_producciones, nuevos_terminales, nuevos_no_terminales


def encontrar_simbolos_accesibles(producciones, simbolo_inicial, terminales, no_terminales):
    """
    Encuentra los símbolos accesibles desde el símbolo inicial.
    Implementa el algoritmo Vai iterativo hasta punto fijo.
    IMPORTANTE: Usa la gramática resultante después del primer paso.
    """
    print("\n=== PASO 2: ENCONTRANDO SÍMBOLOS ACCESIBLES ===")
    print("(Usando la gramática resultante después de eliminar símbolos no generadores)")
    
    # Verificar que el símbolo inicial esté en los no terminales actuales
    if simbolo_inicial not in no_terminales:
        print(f"⚠️  ADVERTENCIA: El símbolo inicial '{simbolo_inicial}' fue eliminado en el paso anterior.")
        print("La gramática no puede generar ninguna cadena.")
        return set()
    
    # V0 = {S} (símbolo inicial)
    V_actual = {simbolo_inicial}
    paso = 0
    
    print(f"V{paso} = {sorted(V_actual)} (símbolo inicial)")
    
    while True:
        paso += 1
        V_anterior = V_actual.copy()
        
        # Para cada símbolo en V_actual, agregar todos los símbolos
        # que aparecen en sus producciones
        simbolos_a_procesar = V_actual.copy()
        
        for simbolo in simbolos_a_procesar:
            if simbolo in producciones:
                for produccion in producciones[simbolo]:
                    for simbolo_prod in produccion:
                        if simbolo_prod != 'ε':
                            V_actual.add(simbolo_prod)
        
        print(f"V{paso} = {sorted(V_actual)}")
        
        # Punto fijo alcanzado
        if V_actual == V_anterior:
            break
    
    simbolos_accesibles = V_actual
    print(f"\n✅ Símbolos accesibles finales: {sorted(simbolos_accesibles)}")
    return simbolos_accesibles


def eliminar_simbolos_inaccesibles(producciones, simbolos_accesibles, terminales, no_terminales):
    """
    Elimina los símbolos inaccesibles y retorna la gramática final.
    """
    print("\n=== ELIMINANDO SÍMBOLOS INACCESIBLES ===")
    
    # Identificar símbolos inaccesibles
    todos_simbolos = terminales.union(no_terminales)
    simbolos_inaccesibles = todos_simbolos - simbolos_accesibles
    
    if not simbolos_inaccesibles:
        print("✅ No hay símbolos inaccesibles para eliminar.")
        return producciones, terminales, no_terminales
    
    print(f"Símbolos inaccesibles detectados: {sorted(simbolos_inaccesibles)}")
    
    # Nuevos conjuntos sin símbolos inaccesibles
    nuevos_terminales = terminales.intersection(simbolos_accesibles)
    nuevos_no_terminales = no_terminales.intersection(simbolos_accesibles)
    
    # Nuevas producciones
    nuevas_producciones = {}
    
    for no_terminal in producciones:
        if no_terminal in simbolos_inaccesibles:
            print(f"Eliminando no terminal: {no_terminal}")
            continue
            
        producciones_validas = []
        
        for produccion in producciones[no_terminal]:
            # Verificar si la producción contiene símbolos inaccesibles
            if produccion == ['ε']:
                producciones_validas.append(produccion)
            elif all(simbolo in simbolos_accesibles for simbolo in produccion):
                producciones_validas.append(produccion)
            else:
                simbolos_inacc_en_prod = [s for s in produccion if s not in simbolos_accesibles]
                print(f"Eliminando producción {no_terminal} → {' '.join(produccion)} (contiene símbolos inaccesibles: {simbolos_inacc_en_prod})")
        
        if producciones_validas:
            nuevas_producciones[no_terminal] = producciones_validas
    
    return nuevas_producciones, nuevos_terminales, nuevos_no_terminales


def imprimir_producciones(producciones, titulo="PRODUCCIONES"):
    """Imprime las producciones en formato legible"""
    print(f"\n=== {titulo} ===")
    if not producciones:
        print("No hay producciones.")
        return
        
    for no_terminal in sorted(producciones.keys()):
        alternativas = []
        for prod in producciones[no_terminal]:
            if prod == ['ε']:
                alternativas.append('ε')
            else:
                alternativas.append(' '.join(prod))
        
        print(f"{no_terminal} → {' | '.join(alternativas)}")


def imprimir_resumen_completo(terminales_orig, no_terminales_orig, 
                             terminales_paso1, no_terminales_paso1,
                             terminales_final, no_terminales_final):
    """Imprime un resumen completo de todos los cambios realizados"""
    print("\n=== RESUMEN COMPLETO DE CAMBIOS ===")
    
    print("📊 TERMINALES:")
    print(f"  Originales: {sorted(terminales_orig)}")
    print(f"  Después del Paso 1: {sorted(terminales_paso1)}")
    print(f"  Finales: {sorted(terminales_final)}")
    
    terminales_elim_paso1 = terminales_orig - terminales_paso1
    terminales_elim_paso2 = terminales_paso1 - terminales_final
    
    if terminales_elim_paso1:
        print(f"  ❌ Eliminados en Paso 1: {sorted(terminales_elim_paso1)}")
    if terminales_elim_paso2:
        print(f"  ❌ Eliminados en Paso 2: {sorted(terminales_elim_paso2)}")
    
    print("\n📊 NO TERMINALES:")
    print(f"  Originales: {sorted(no_terminales_orig)}")
    print(f"  Después del Paso 1: {sorted(no_terminales_paso1)}")
    print(f"  Finales: {sorted(no_terminales_final)}")
    
    no_terminales_elim_paso1 = no_terminales_orig - no_terminales_paso1
    no_terminales_elim_paso2 = no_terminales_paso1 - no_terminales_final
    
    if no_terminales_elim_paso1:
        print(f"  ❌ Eliminados en Paso 1: {sorted(no_terminales_elim_paso1)}")
    if no_terminales_elim_paso2:
        print(f"  ❌ Eliminados en Paso 2: {sorted(no_terminales_elim_paso2)}")


def main():
    print("=== ELIMINADOR DE SÍMBOLOS INÚTILES EN GRAMÁTICAS LIBRES DE CONTEXTO ===")
    print("Algoritmo de dos pasos: 1) Eliminar no generadores, 2) Eliminar inaccesibles\n")
    
    # Obtener entrada del usuario
    terminales = obtener_terminales()
    print(f"Terminales: {sorted(terminales)}")
    
    no_terminales = obtener_no_terminales()
    print(f"No terminales: {sorted(no_terminales)}")
    
    simbolo_inicial = obtener_simbolo_inicial(no_terminales)
    print(f"Símbolo inicial: {simbolo_inicial}")
    
    producciones = obtener_producciones(terminales, no_terminales)
    
    if not producciones:
        print("No se ingresaron producciones válidas.")
        return
    
    # Guardar originales para el resumen
    terminales_orig = terminales.copy()
    no_terminales_orig = no_terminales.copy()
    
    imprimir_producciones(producciones, "GRAMÁTICA ORIGINAL")
    
    # PASO 1: Encontrar y eliminar símbolos no generadores
    print("\n" + "="*70)
    simbolos_generadores = encontrar_simbolos_generadores(producciones, terminales, no_terminales)
    
    producciones_paso1, terminales_paso1, no_terminales_paso1 = eliminar_simbolos_no_generadores(
        producciones, simbolos_generadores, terminales, no_terminales
    )
    
    imprimir_producciones(producciones_paso1, "GRAMÁTICA DESPUÉS DEL PASO 1 (Sin símbolos no generadores)")
    
    # PASO 2: Encontrar y eliminar símbolos inaccesibles
    # IMPORTANTE: Usar la gramática resultante del paso 1
    print("\n" + "="*70)
    simbolos_accesibles = encontrar_simbolos_accesibles(
        producciones_paso1, simbolo_inicial, terminales_paso1, no_terminales_paso1
    )
    
    producciones_finales, terminales_finales, no_terminales_finales = eliminar_simbolos_inaccesibles(
        producciones_paso1, simbolos_accesibles, terminales_paso1, no_terminales_paso1
    )
    
    # Mostrar resultados finales
    print("\n" + "="*70)
    imprimir_producciones(producciones_finales, "GRAMÁTICA FINAL (Sin símbolos inútiles)")
    
    imprimir_resumen_completo(terminales_orig, no_terminales_orig,
                             terminales_paso1, no_terminales_paso1,
                             terminales_finales, no_terminales_finales)
    
    print("\n🎉 ¡Proceso completado exitosamente!")
    print("La gramática resultante contiene únicamente símbolos útiles.")


if __name__ == "__main__":
    main()