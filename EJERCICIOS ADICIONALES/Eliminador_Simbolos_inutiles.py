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
    print("\n=== ENCONTRANDO SÍMBOLOS GENERADORES ===")
    
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


def encontrar_simbolos_accesibles(producciones, simbolo_inicial, terminales, no_terminales):
    """
    Encuentra los símbolos accesibles desde el símbolo inicial.
    Implementa el algoritmo Vai iterativo hasta punto fijo.
    """
    print("\n=== ENCONTRANDO SÍMBOLOS ACCESIBLES ===")
    
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


def encontrar_simbolos_utiles(simbolos_generadores, simbolos_accesibles):
    """
    Encuentra los símbolos útiles como intersección de generadores y accesibles.
    """
    print("\n=== ENCONTRANDO SÍMBOLOS ÚTILES ===")
    
    simbolos_utiles = simbolos_generadores.intersection(simbolos_accesibles)
    
    print(f"Símbolos generadores: {sorted(simbolos_generadores)}")
    print(f"Símbolos accesibles: {sorted(simbolos_accesibles)}")
    print(f"Símbolos útiles (intersección): {sorted(simbolos_utiles)}")
    
    return simbolos_utiles


def eliminar_simbolos_inutiles(producciones, simbolos_utiles, terminales, no_terminales):
    """
    Elimina los símbolos inútiles y sus producciones asociadas.
    """
    print("\n=== ELIMINANDO SÍMBOLOS INÚTILES ===")
    
    # Identificar símbolos inútiles
    todos_simbolos = terminales.union(no_terminales)
    simbolos_inutiles = todos_simbolos - simbolos_utiles
    
    if not simbolos_inutiles:
        print("✅ No hay símbolos inútiles para eliminar.")
        return producciones, terminales, no_terminales
    
    print(f"Símbolos inútiles detectados: {sorted(simbolos_inutiles)}")
    
    # Nuevos conjuntos sin símbolos inútiles
    nuevos_terminales = terminales.intersection(simbolos_utiles)
    nuevos_no_terminales = no_terminales.intersection(simbolos_utiles)
    
    # Nuevas producciones
    nuevas_producciones = {}
    
    for no_terminal in producciones:
        if no_terminal in simbolos_inutiles:
            print(f"Eliminando no terminal: {no_terminal}")
            continue
            
        producciones_validas = []
        
        for produccion in producciones[no_terminal]:
            # Verificar si la producción contiene símbolos inútiles
            if produccion == ['ε']:
                producciones_validas.append(produccion)
            elif all(simbolo in simbolos_utiles for simbolo in produccion):
                producciones_validas.append(produccion)
            else:
                simbolos_inutiles_en_prod = [s for s in produccion if s not in simbolos_utiles]
                print(f"Eliminando producción {no_terminal} → {' '.join(produccion)} (contiene símbolos inútiles: {simbolos_inutiles_en_prod})")
        
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


def imprimir_resumen(terminales_orig, no_terminales_orig, terminales_final, no_terminales_final):
    """Imprime un resumen de los cambios realizados"""
    print("\n=== RESUMEN DE CAMBIOS ===")
    
    terminales_eliminados = terminales_orig - terminales_final
    no_terminales_eliminados = no_terminales_orig - no_terminales_final
    
    print(f"Terminales originales: {sorted(terminales_orig)}")
    print(f"Terminales finales: {sorted(terminales_final)}")
    if terminales_eliminados:
        print(f"Terminales eliminados: {sorted(terminales_eliminados)}")
    
    print(f"No terminales originales: {sorted(no_terminales_orig)}")
    print(f"No terminales finales: {sorted(no_terminales_final)}")
    if no_terminales_eliminados:
        print(f"No terminales eliminados: {sorted(no_terminales_eliminados)}")


def main():
    print("=== ELIMINADOR DE SÍMBOLOS INÚTILES EN GRAMÁTICAS LIBRES DE CONTEXTO ===\n")
    
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
    
    # Paso 1: Encontrar símbolos generadores
    simbolos_generadores = encontrar_simbolos_generadores(producciones, terminales, no_terminales)
    
    # Paso 2: Encontrar símbolos accesibles
    simbolos_accesibles = encontrar_simbolos_accesibles(producciones, simbolo_inicial, terminales, no_terminales)
    
    # Paso 3: Encontrar símbolos útiles
    simbolos_utiles = encontrar_simbolos_utiles(simbolos_generadores, simbolos_accesibles)
    
    # Paso 4: Eliminar símbolos inútiles
    producciones_finales, terminales_finales, no_terminales_finales = eliminar_simbolos_inutiles(
        producciones, simbolos_utiles, terminales, no_terminales
    )
    
    # Mostrar resultados
    imprimir_producciones(producciones_finales, "GRAMÁTICA FINAL SIN SÍMBOLOS INÚTILES")
    imprimir_resumen(terminales_orig, no_terminales_orig, terminales_finales, no_terminales_finales)
    
    print("\n🎉 ¡Proceso completado exitosamente!")


if __name__ == "__main__":
    main()