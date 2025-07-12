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


def encontrar_variables_epsilon(producciones, no_terminales):
    """
    Encuentra todas las variables no terminales que derivan a epsilon.
    Implementa el algoritmo iterativo hasta punto fijo.
    """
    print("\n=== PASO 1: ENCONTRANDO VARIABLES QUE DERIVAN A EPSILON ===")
    
    # Conjunto inicial: variables con producción directa a epsilon
    epsilon_vars = set()
    
    # Buscar producciones directas a epsilon
    for variable, lista_producciones in producciones.items():
        for produccion in lista_producciones:
            if produccion == ['ε']:
                epsilon_vars.add(variable)
    
    paso = 0
    print(f"Paso {paso}: Variables con producción directa a ε: {sorted(epsilon_vars)}")
    
    # Iterar hasta punto fijo
    while True:
        paso += 1
        epsilon_vars_anterior = epsilon_vars.copy()
        
        # Para cada variable, verificar si puede derivar a epsilon
        for variable in no_terminales:
            if variable in epsilon_vars:
                continue  # Ya está incluida
            
            if variable in producciones:
                for produccion in producciones[variable]:
                    # Verificar si todos los símbolos de la producción están en epsilon_vars
                    if produccion != ['ε'] and all(simbolo in epsilon_vars for simbolo in produccion):
                        epsilon_vars.add(variable)
                        break
        
        if epsilon_vars != epsilon_vars_anterior:
            nuevas_vars = epsilon_vars - epsilon_vars_anterior
            print(f"Paso {paso}: Se agregaron {sorted(nuevas_vars)} → Total: {sorted(epsilon_vars)}")
        else:
            print(f"Paso {paso}: No se agregaron nuevas variables (punto fijo alcanzado)")
            break
    
    print(f"\n✅ Variables que derivan a epsilon: {sorted(epsilon_vars)}")
    return epsilon_vars


def generar_combinaciones_epsilon(produccion, epsilon_vars):
    """
    Genera todas las combinaciones posibles al sustituir variables epsilon por vacío.
    """
    if not produccion or produccion == ['ε']:
        return [produccion]
    
    # Identificar posiciones de variables epsilon
    posiciones_epsilon = []
    for i, simbolo in enumerate(produccion):
        if simbolo in epsilon_vars:
            posiciones_epsilon.append(i)
    
    if not posiciones_epsilon:
        return [produccion]
    
    # Generar todas las combinaciones (2^n donde n es el número de variables epsilon)
    combinaciones = []
    for i in range(2**len(posiciones_epsilon)):
        nueva_produccion = produccion.copy()
        indices_a_eliminar = []
        
        # Determinar qué variables epsilon eliminar
        for j, pos in enumerate(posiciones_epsilon):
            if i & (1 << j):  # Si el j-ésimo bit está activado
                indices_a_eliminar.append(pos)
        
        # Eliminar variables de derecha a izquierda para mantener índices válidos
        for pos in sorted(indices_a_eliminar, reverse=True):
            nueva_produccion.pop(pos)
        
        # Si la producción queda vacía, se convierte en epsilon
        if not nueva_produccion:
            nueva_produccion = ['ε']
        
        combinaciones.append(nueva_produccion)
    
    return combinaciones


def eliminar_producciones_epsilon(producciones, epsilon_vars, simbolo_inicial):
    """
    Elimina las producciones epsilon y genera todas las combinaciones posibles.
    CORREGIDO: Ya no mantiene producciones epsilon excepto para el símbolo inicial.
    """
    print("\n=== PASO 2: ELIMINANDO PRODUCCIONES EPSILON ===")
    
    nuevas_producciones = {}
    
    # Procesar cada variable
    for variable, lista_producciones in producciones.items():
        nuevas_producciones[variable] = []
        
        for produccion in lista_producciones:
            if produccion == ['ε']:
                # CAMBIO CLAVE: Omitir TODAS las producciones epsilon directas
                # Se agregarán solo al símbolo inicial si es necesario en el paso 3
                print(f"  Omitiendo producción epsilon: {variable} → ε")
                continue
            
            # Generar todas las combinaciones para esta producción
            combinaciones = generar_combinaciones_epsilon(produccion, epsilon_vars)
            print(f"  Procesando {variable} → {' '.join(produccion)}")
            
            for combinacion in combinaciones:
                # CAMBIO CLAVE: No agregar producciones epsilon que resulten de las combinaciones
                # Solo agregar si no es epsilon O si es el símbolo inicial
                if combinacion == ['ε']:
                    print(f"    → Omitiendo combinación epsilon resultante")
                    continue
                
                if combinacion not in nuevas_producciones[variable]:
                    nuevas_producciones[variable].append(combinacion)
                    print(f"    → Agregando: {' '.join(combinacion)}")
    
    # Remover variables que quedaron sin producciones
    nuevas_producciones = {var: prods for var, prods in nuevas_producciones.items() if prods}
    
    print("✅ Producciones epsilon eliminadas y combinaciones generadas")
    return nuevas_producciones


def manejar_simbolo_inicial_epsilon(producciones, simbolo_inicial, epsilon_vars, no_terminales):
    """
    Si el símbolo inicial deriva a epsilon, crea un nuevo símbolo inicial.
    CORREGIDO: S' → S | ε (no copia todas las producciones de S)
    """
    print("\n=== PASO 3: MANEJO DEL SÍMBOLO INICIAL ===")
    
    if simbolo_inicial not in epsilon_vars:
        print(f"El símbolo inicial '{simbolo_inicial}' no deriva a epsilon. No se requiere cambio.")
        return producciones, no_terminales, simbolo_inicial
    
    print(f"El símbolo inicial '{simbolo_inicial}' deriva a epsilon.")
    
    # Crear nuevo símbolo inicial
    nuevo_simbolo = simbolo_inicial + "'"
    while nuevo_simbolo in no_terminales:
        nuevo_simbolo += "'"
    
    print(f"Creando nuevo símbolo inicial: {nuevo_simbolo}")
    
    # Agregar nuevo símbolo inicial a no terminales
    nuevos_no_terminales = no_terminales.copy()
    nuevos_no_terminales.add(nuevo_simbolo)
    
    # Crear producciones para el nuevo símbolo inicial
    nuevas_producciones = producciones.copy()
    
    # CAMBIO CLAVE: El nuevo símbolo inicial solo tiene S' → S | ε
    nuevas_producciones[nuevo_simbolo] = [[simbolo_inicial], ['ε']]
    
    print(f"✅ Nuevo símbolo inicial creado: {nuevo_simbolo} → {simbolo_inicial} | ε")
    
    return nuevas_producciones, nuevos_no_terminales, nuevo_simbolo


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


def imprimir_resumen_cambios(producciones_orig, producciones_final, 
                           simbolo_inicial_orig, simbolo_inicial_final,
                           epsilon_vars):
    """Imprime un resumen de los cambios realizados"""
    print("\n=== RESUMEN DE CAMBIOS ===")
    
    print(f"📊 VARIABLES QUE DERIVAN A EPSILON: {sorted(epsilon_vars)}")
    
    print(f"\n📊 SÍMBOLO INICIAL:")
    print(f"  Original: {simbolo_inicial_orig}")
    print(f"  Final: {simbolo_inicial_final}")
    
    if simbolo_inicial_orig != simbolo_inicial_final:
        print(f"  ✅ Se creó nuevo símbolo inicial porque {simbolo_inicial_orig} deriva a ε")
    
    print(f"\n📊 PRODUCCIONES MODIFICADAS:")
    for variable in sorted(set(producciones_orig.keys()) | set(producciones_final.keys())):
        orig = producciones_orig.get(variable, [])
        final = producciones_final.get(variable, [])
        
        if orig != final:
            print(f"  {variable}:")
            if orig:
                orig_str = ' | '.join([' '.join(p) if p != ['ε'] else 'ε' for p in orig])
                print(f"    Original: {orig_str}")
            if final:
                final_str = ' | '.join([' '.join(p) if p != ['ε'] else 'ε' for p in final])
                print(f"    Final:    {final_str}")
            print()


def main():
    print("=== ELIMINADOR DE PRODUCCIONES VACÍAS EN GRAMÁTICAS LIBRES DE CONTEXTO ===")
    print("Algoritmo en tres pasos:")
    print("1) Encontrar variables que derivan a epsilon")
    print("2) Eliminar producciones epsilon y generar combinaciones")
    print("3) Manejar símbolo inicial si deriva a epsilon\n")
    
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
    producciones_orig = {}
    for var, prods in producciones.items():
        producciones_orig[var] = [p.copy() for p in prods]
    simbolo_inicial_orig = simbolo_inicial
    
    imprimir_producciones(producciones, "GRAMÁTICA ORIGINAL")
    
    # PASO 1: Encontrar variables que derivan a epsilon
    print("\n" + "="*70)
    epsilon_vars = encontrar_variables_epsilon(producciones, no_terminales)
    
    # PASO 2: Eliminar producciones epsilon y generar combinaciones
    print("\n" + "="*70)
    producciones_sin_epsilon = eliminar_producciones_epsilon(producciones, epsilon_vars, simbolo_inicial)
    
    # PASO 3: Manejar símbolo inicial si deriva a epsilon
    print("\n" + "="*70)
    producciones_finales, no_terminales_finales, simbolo_inicial_final = manejar_simbolo_inicial_epsilon(
        producciones_sin_epsilon, simbolo_inicial, epsilon_vars, no_terminales
    )
    
    # Mostrar resultados finales
    print("\n" + "="*70)
    imprimir_producciones(producciones_finales, "GRAMÁTICA FINAL (Sin producciones epsilon)")
    
    print(f"\n📊 GRAMÁTICA RESULTANTE:")
    print(f"Terminales: {sorted(terminales)}")
    print(f"No terminales: {sorted(no_terminales_finales)}")
    print(f"Símbolo inicial: {simbolo_inicial_final}")
    
    imprimir_resumen_cambios(producciones_orig, producciones_finales,
                           simbolo_inicial_orig, simbolo_inicial_final,
                           epsilon_vars)
    
    print("\n🎉 ¡Eliminación de producciones epsilon completada exitosamente!")
    print("La gramática resultante es equivalente pero sin producciones vacías.")


if __name__ == "__main__":
    main()