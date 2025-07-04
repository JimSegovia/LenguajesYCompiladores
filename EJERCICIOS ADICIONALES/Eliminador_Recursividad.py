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
            simbolos = [s.strip() for s in alt.split() if s.strip()]
            alternativas.append(simbolos)

    return lado_izquierdo, alternativas


def obtener_producciones(terminales, no_terminales):
    """Obtiene las producciones del usuario"""
    print("\nIngresa las producciones (una por línea).")
    print("Formato: A → α | β (símbolos separados por espacios)")
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


def generar_nuevo_no_terminal(base, no_terminales_existentes):
    """Genera un nuevo símbolo no terminal único"""
    contador = 1
    while True:
        nuevo = f"{base}'"
        if contador > 1:
            nuevo = f"{base}" + "'" * contador
        if nuevo not in no_terminales_existentes:
            return nuevo
        contador += 1


def detectar_recursividad_indirecta(producciones, no_terminales):
    """Detecta si hay recursividad indirecta usando análisis de ciclos"""
    # Crear grafo de dependencias
    grafo = {nt: set() for nt in no_terminales}

    for no_terminal in producciones:
        for produccion in producciones[no_terminal]:
            if produccion and produccion[0] in no_terminales:
                # Si la producción comienza con un no terminal
                grafo[no_terminal].add(produccion[0])

    # Detectar ciclos usando DFS
    def tiene_ciclo(nodo, visitados, pila_recursion):
        if nodo in pila_recursion:
            return True
        if nodo in visitados:
            return False

        visitados.add(nodo)
        pila_recursion.add(nodo)

        for vecino in grafo[nodo]:
            if tiene_ciclo(vecino, visitados, pila_recursion):
                return True

        pila_recursion.remove(nodo)
        return False

    visitados = set()
    for nodo in no_terminales:
        if nodo not in visitados:
            if tiene_ciclo(nodo, visitados, set()):
                return True

    return False


def encontrar_orden_eliminacion(producciones, no_terminales):
    """Encuentra un orden para eliminar recursividad que evite ciclos"""
    # Ordenar no terminales por orden lexicográfico como base
    orden = sorted(no_terminales)

    # Crear grafo de dependencias inmediatas
    dependencias = {nt: set() for nt in no_terminales}

    for no_terminal in producciones:
        for produccion in producciones[no_terminal]:
            if produccion and produccion[0] in no_terminales:
                dependencias[no_terminal].add(produccion[0])

    return orden, dependencias


def eliminar_recursividad_indirecta(producciones, no_terminales):
    """Elimina recursividad indirecta sustituyendo producciones"""
    print("\n--- Eliminando recursividad indirecta ---")

    orden, dependencias = encontrar_orden_eliminacion(producciones, no_terminales)
    nuevas_producciones = {nt: producciones.get(nt, []).copy() for nt in no_terminales}

    # Procesar no terminales en orden
    for i, Ai in enumerate(orden):
        # Para cada Aj donde j < i
        for j in range(i):
            Aj = orden[j]

            # Si Ai → Aj α, reemplazar con las producciones de Aj
            producciones_modificadas = []
            cambios = False

            for produccion in nuevas_producciones[Ai]:
                if produccion and produccion[0] == Aj:
                    # Reemplazar Aj con sus producciones
                    alpha = produccion[1:]  # Resto después de Aj

                    # Para cada producción de Aj
                    for prod_Aj in nuevas_producciones[Aj]:
                        nueva_prod = prod_Aj + alpha
                        producciones_modificadas.append(nueva_prod)

                    cambios = True
                    print(f"Sustituyendo en {Ai}: {Aj} {' '.join(alpha)} -> expansiones de {Aj}")
                else:
                    producciones_modificadas.append(produccion)

            if cambios:
                nuevas_producciones[Ai] = producciones_modificadas

    return nuevas_producciones


def tiene_recursividad_directa(no_terminal, producciones):
    """Verifica si un no terminal tiene recursividad directa por la izquierda"""
    if no_terminal not in producciones:
        return False

    for produccion in producciones[no_terminal]:
        if produccion and produccion[0] == no_terminal:
            return True
    return False


def eliminar_recursividad_directa(no_terminal, producciones, no_terminales):
    """Elimina la recursividad directa por la izquierda de un no terminal específico"""
    if not tiene_recursividad_directa(no_terminal, producciones):
        return producciones, no_terminales

    print(f"\n--- Eliminando recursividad directa en {no_terminal} ---")

    # Separar producciones recursivas y no recursivas
    recursivas = []  # A → Aα
    no_recursivas = []  # A → β

    for produccion in producciones[no_terminal]:
        if produccion and produccion[0] == no_terminal:
            # Es recursiva, α = resto de la producción
            alpha = produccion[1:]
            recursivas.append(alpha)
            print(f"Producción recursiva: {no_terminal} → {no_terminal} {' '.join(alpha)}")
        else:
            # No es recursiva, β = toda la producción
            no_recursivas.append(produccion)
            print(f"Producción no recursiva: {no_terminal} → {' '.join(produccion)}")

    # Si no hay producciones no recursivas, es un error
    if not no_recursivas:
        print(f"Error: {no_terminal} solo tiene producciones recursivas")
        return producciones, no_terminales

    # Generar nuevo no terminal A'
    nuevo_no_terminal = generar_nuevo_no_terminal(no_terminal, no_terminales)
    no_terminales.add(nuevo_no_terminal)
    print(f"Creando nuevo no terminal: {nuevo_no_terminal}")

    # Crear nuevas producciones
    nuevas_producciones = {}

    # Copiar producciones existentes (excepto la que estamos modificando)
    for nt in producciones:
        if nt != no_terminal:
            nuevas_producciones[nt] = producciones[nt]

    # A → βA' para cada β no recursiva
    nuevas_producciones[no_terminal] = []
    for beta in no_recursivas:
        if beta == ['ε']:
            nueva_prod = [nuevo_no_terminal]
        else:
            nueva_prod = beta + [nuevo_no_terminal]
        nuevas_producciones[no_terminal].append(nueva_prod)
        print(f"Nueva producción: {no_terminal} → {' '.join(nueva_prod)}")

    # A' → αA' | ε para cada α recursiva
    nuevas_producciones[nuevo_no_terminal] = []
    for alpha in recursivas:
        if alpha:  # Si α no es vacío
            nueva_prod = alpha + [nuevo_no_terminal]
        else:  # Si α es vacío
            nueva_prod = [nuevo_no_terminal]
        nuevas_producciones[nuevo_no_terminal].append(nueva_prod)
        print(f"Nueva producción: {nuevo_no_terminal} → {' '.join(nueva_prod)}")

    # Agregar producción A' → ε
    nuevas_producciones[nuevo_no_terminal].append(['ε'])
    print(f"Nueva producción: {nuevo_no_terminal} → ε")

    return nuevas_producciones, no_terminales


def imprimir_producciones(producciones, titulo="PRODUCCIONES"):
    """Imprime las producciones en formato legible"""
    print(f"\n=== {titulo} ===")
    for no_terminal in sorted(producciones.keys()):
        alternativas = []
        for prod in producciones[no_terminal]:
            if prod == ['ε']:
                alternativas.append('ε')
            else:
                alternativas.append(' '.join(prod))

        print(f"{no_terminal} → {' | '.join(alternativas)}")


def main():
    print("=== ELIMINADOR DE RECURSIVIDAD POR LA IZQUIERDA (ROBUSTO) ===\n")

    # Obtener símbolos terminales
    terminales = obtener_terminales()
    print(f"Terminales: {sorted(terminales)}")

    # Obtener símbolos no terminales
    no_terminales = obtener_no_terminales()
    print(f"No terminales: {sorted(no_terminales)}")

    # Obtener producciones
    producciones = obtener_producciones(terminales, no_terminales)

    if not producciones:
        print("No se ingresaron producciones válidas.")
        return

    imprimir_producciones(producciones, "GRAMÁTICA ORIGINAL")

    # Detectar recursividad indirecta
    if detectar_recursividad_indirecta(producciones, no_terminales):
        print("\n⚠️  RECURSIVIDAD INDIRECTA DETECTADA")
        print("Procediendo a eliminar recursividad indirecta primero...")

        # Eliminar recursividad indirecta
        producciones = eliminar_recursividad_indirecta(producciones, no_terminales)
        imprimir_producciones(producciones, "DESPUÉS DE ELIMINAR RECURSIVIDAD INDIRECTA")
    else:
        print("\n✅ No se detectó recursividad indirecta")

    # Crear copia de no_terminales para modificar
    no_terminales_modificados = no_terminales.copy()

    # Eliminar recursividad directa
    print("\n=== ELIMINANDO RECURSIVIDAD DIRECTA ===")
    recursividad_encontrada = False

    for no_terminal in sorted(no_terminales):
        if tiene_recursividad_directa(no_terminal, producciones):
            recursividad_encontrada = True
            producciones, no_terminales_modificados = eliminar_recursividad_directa(
                no_terminal, producciones, no_terminales_modificados
            )

    if not recursividad_encontrada:
        print("No se encontró recursividad directa por la izquierda.")

    imprimir_producciones(producciones, "GRAMÁTICA FINAL SIN RECURSIVIDAD")

    print(f"\nNuevos no terminales: {sorted(no_terminales_modificados)}")
    print(f"Terminales: {sorted(terminales)}")


if __name__ == "__main__":
    main()