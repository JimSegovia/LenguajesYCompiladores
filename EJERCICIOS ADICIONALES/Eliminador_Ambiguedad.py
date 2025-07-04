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
    print("-" * 50)

    producciones = {}
    contador = 1

    while True:
        linea = input(f"Producción {contador} (Enter vacío para terminar): ").strip()
        if not linea:
            if contador == 1:
                print("No se ingresó ninguna producción.")
                continue
            else:
                break

        lado_izq, alternativas = parsear_produccion(linea)

        if lado_izq is None:
            continue

        if lado_izq not in no_terminales:
            print(f"Advertencia: '{lado_izq}' no está en los no terminales declarados")

        if lado_izq not in producciones:
            producciones[lado_izq] = []

        producciones[lado_izq].extend(alternativas)
        contador += 1

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


def encontrar_prefijo_comun(producciones_lista):
    """Encuentra el prefijo común más largo entre producciones"""
    if len(producciones_lista) < 2:
        return []

    # Comparar símbolo por símbolo
    prefijo = []
    min_len = min(len(prod) for prod in producciones_lista)

    for i in range(min_len):
        simbolo = producciones_lista[0][i]
        if all(prod[i] == simbolo for prod in producciones_lista):
            prefijo.append(simbolo)
        else:
            break

    return prefijo


def detectar_prefijos_comunes(producciones):
    """Detecta no terminales con prefijos comunes en sus producciones"""
    ambiguedades = {}

    for no_terminal in producciones:
        prods = producciones[no_terminal]
        if len(prods) < 2:
            continue

        # Agrupar producciones por primer símbolo
        grupos = {}
        for prod in prods:
            if prod:  # Solo producciones no vacías
                primer = prod[0]
                if primer not in grupos:
                    grupos[primer] = []
                grupos[primer].append(prod)

        # Verificar si hay grupos con más de una producción
        for primer_simbolo, grupo in grupos.items():
            if len(grupo) > 1:
                prefijo = encontrar_prefijo_comun(grupo)
                if prefijo:
                    if no_terminal not in ambiguedades:
                        ambiguedades[no_terminal] = []
                    ambiguedades[no_terminal].append({
                        'prefijo': prefijo,
                        'producciones': grupo
                    })

    return ambiguedades


def aplicar_factorizacion_izquierda(no_terminal, info_ambiguedad, producciones, no_terminales):
    """Aplica factorización por la izquierda para eliminar prefijos comunes"""
    print(f"\n--- Aplicando factorización por la izquierda en {no_terminal} ---")

    nuevas_producciones = {}
    # Copiar producciones existentes
    for nt in producciones:
        if nt != no_terminal:
            nuevas_producciones[nt] = producciones[nt].copy()

    producciones_restantes = producciones[no_terminal].copy()
    nuevas_producciones[no_terminal] = []

    for ambiguedad in info_ambiguedad:
        prefijo = ambiguedad['prefijo']
        prods_con_prefijo = ambiguedad['producciones']

        print(f"Prefijo común encontrado: {' '.join(prefijo)}")

        # Crear nuevo no terminal
        nuevo_nt = generar_nuevo_no_terminal(no_terminal, no_terminales)
        no_terminales.add(nuevo_nt)
        print(f"Nuevo no terminal creado: {nuevo_nt}")

        # Crear producción factorizada: A → αA'
        produccion_factorizada = prefijo + [nuevo_nt]
        nuevas_producciones[no_terminal].append(produccion_factorizada)
        print(f"Nueva producción: {no_terminal} → {' '.join(produccion_factorizada)}")

        # Crear producciones para el nuevo no terminal: A' → β₁ | β₂ | ...
        nuevas_producciones[nuevo_nt] = []
        for prod in prods_con_prefijo:
            # Quitar el prefijo común
            beta = prod[len(prefijo):]
            if not beta:  # Si no queda nada después del prefijo
                beta = ['ε']
            nuevas_producciones[nuevo_nt].append(beta)
            print(f"Nueva producción: {nuevo_nt} → {' '.join(beta)}")

        # Remover las producciones originales de la lista restante
        for prod in prods_con_prefijo:
            if prod in producciones_restantes:
                producciones_restantes.remove(prod)

    # Agregar producciones que no tenían prefijos comunes
    nuevas_producciones[no_terminal].extend(producciones_restantes)

    return nuevas_producciones, no_terminales


def detectar_ambiguedad_operadores(producciones, terminales):
    """Detecta ambigüedad por precedencia de operadores"""
    operadores_binarios = []

    for no_terminal in producciones:
        for prod in producciones[no_terminal]:
            # Buscar patrones como E → E op E
            if len(prod) == 3 and prod[0] == no_terminal and prod[2] == no_terminal:
                if prod[1] in terminales:
                    operadores_binarios.append(prod[1])

    return list(set(operadores_binarios))


def obtener_precedencia_operadores(operadores):
    """Obtiene la precedencia de operadores del usuario"""
    if not operadores:
        return {}, {}

    print(f"\nOperadores detectados: {', '.join(operadores)}")
    print("Configura la precedencia y asociatividad:")
    print("Precedencia: número más alto = mayor precedencia")
    print("Asociatividad: 'left' (izquierda) o 'right' (derecha)")

    precedencia = {}
    asociatividad = {}

    for op in operadores:
        while True:
            try:
                prec = int(input(f"Precedencia de '{op}': "))
                precedencia[op] = prec
                break
            except ValueError:
                print("Ingresa un número entero.")

        while True:
            asoc = input(f"Asociatividad de '{op}' (left/right): ").strip().lower()
            if asoc in ['left', 'right']:
                asociatividad[op] = asoc
                break
            print("Ingresa 'left' o 'right'.")

    return precedencia, asociatividad


def eliminar_ambiguedad_operadores(producciones, operadores, precedencia, asociatividad, no_terminales):
    """Elimina ambigüedad por precedencia creando niveles jerárquicos"""
    print("\n--- Eliminando ambigüedad por precedencia de operadores ---")

    # Ordenar operadores por precedencia (menor a mayor)
    ops_ordenados = sorted(operadores, key=lambda x: precedencia[x])

    # Crear niveles de precedencia
    niveles = {}
    for i, op in enumerate(ops_ordenados):
        niveles[op] = i

    # Generar nuevos no terminales para cada nivel
    no_terminal_base = None
    for nt in producciones:
        # Encontrar el no terminal que tiene las producciones ambiguas
        for prod in producciones[nt]:
            if len(prod) == 3 and prod[0] == nt and prod[2] == nt and prod[1] in operadores:
                no_terminal_base = nt
                break
        if no_terminal_base:
            break

    if not no_terminal_base:
        return producciones, no_terminales

    # Crear nombres para los niveles
    nombres_niveles = []
    for i in range(len(ops_ordenados)):
        if i == 0:
            nombres_niveles.append(no_terminal_base)
        else:
            nuevo_nombre = f"{no_terminal_base}{i}"
            nombres_niveles.append(nuevo_nombre)
            no_terminales.add(nuevo_nombre)

    # Crear nuevo no terminal para factores
    factor_nt = f"{no_terminal_base}_factor"
    no_terminales.add(factor_nt)
    nombres_niveles.append(factor_nt)

    nuevas_producciones = {}

    # Copiar producciones que no son del no terminal base
    for nt in producciones:
        if nt != no_terminal_base:
            nuevas_producciones[nt] = producciones[nt].copy()

    # Crear producciones para cada nivel
    for i, op in enumerate(ops_ordenados):
        nivel_actual = nombres_niveles[i]
        nivel_siguiente = nombres_niveles[i + 1]

        if nivel_actual not in nuevas_producciones:
            nuevas_producciones[nivel_actual] = []

        # Crear producciones según asociatividad
        if asociatividad[op] == 'left':
            # Asociatividad izquierda: A → A op B | B
            nuevas_producciones[nivel_actual].append([nivel_actual, op, nivel_siguiente])
        else:
            # Asociatividad derecha: A → B op A | B
            nuevas_producciones[nivel_actual].append([nivel_siguiente, op, nivel_actual])

        # Agregar producción de paso al siguiente nivel
        nuevas_producciones[nivel_actual].append([nivel_siguiente])

        print(f"Nivel {i} ({op}): {nivel_actual} → {nivel_actual} {op} {nivel_siguiente} | {nivel_siguiente}")

    # Crear producciones para el factor (último nivel)
    nuevas_producciones[factor_nt] = []

    # Buscar producciones originales que no eran operadores binarios
    for prod in producciones[no_terminal_base]:
        if not (len(prod) == 3 and prod[0] == no_terminal_base and prod[2] == no_terminal_base and prod[
            1] in operadores):
            # Reemplazar referencias al no terminal base con factor
            nueva_prod = []
            for simbolo in prod:
                if simbolo == no_terminal_base:
                    nueva_prod.append(nombres_niveles[0])  # Referencia al nivel más alto
                else:
                    nueva_prod.append(simbolo)
            nuevas_producciones[factor_nt].append(nueva_prod)

    # Si no hay producciones base, crear una producción por defecto
    if not nuevas_producciones[factor_nt]:
        nuevas_producciones[factor_nt].append(['id'])  # Producción por defecto

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
    print("=== ELIMINADOR DE AMBIGÜEDAD EN GRAMÁTICAS ===\n")

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

    # Crear copia de no_terminales para modificar
    no_terminales_modificados = no_terminales.copy()

    # 1. Detectar y eliminar prefijos comunes
    print("\n=== ANALIZANDO PREFIJOS COMUNES ===")
    ambiguedades_prefijos = detectar_prefijos_comunes(producciones)

    if ambiguedades_prefijos:
        print("⚠️  AMBIGÜEDAD POR PREFIJOS COMUNES DETECTADA")
        for nt, info in ambiguedades_prefijos.items():
            print(f"No terminal {nt} tiene prefijos comunes:")
            for amb in info:
                print(f"  Prefijo: {' '.join(amb['prefijo'])}")
                print(f"  Producciones: {[' '.join(p) for p in amb['producciones']]}")

        # Aplicar factorización por la izquierda
        for nt, info in ambiguedades_prefijos.items():
            producciones, no_terminales_modificados = aplicar_factorizacion_izquierda(
                nt, info, producciones, no_terminales_modificados
            )

        imprimir_producciones(producciones, "DESPUÉS DE FACTORIZACIÓN POR LA IZQUIERDA")
    else:
        print("✅ No se detectaron prefijos comunes")

    # 2. Detectar y eliminar ambigüedad por operadores
    print("\n=== ANALIZANDO AMBIGÜEDAD POR OPERADORES ===")
    operadores = detectar_ambiguedad_operadores(producciones, terminales)

    if operadores:
        print("⚠️  AMBIGÜEDAD POR OPERADORES DETECTADA")
        precedencia, asociatividad = obtener_precedencia_operadores(operadores)

        producciones, no_terminales_modificados = eliminar_ambiguedad_operadores(
            producciones, operadores, precedencia, asociatividad, no_terminales_modificados
        )

        imprimir_producciones(producciones, "DESPUÉS DE ELIMINAR AMBIGÜEDAD POR OPERADORES")
    else:
        print("✅ No se detectó ambigüedad por operadores")

    # Resultado final
    imprimir_producciones(producciones, "GRAMÁTICA FINAL SIN AMBIGÜEDAD")

    print(f"\nNuevos no terminales: {sorted(no_terminales_modificados)}")
    print(f"Terminales: {sorted(terminales)}")

    print("\n=== RESUMEN ===")
    print(f"✅ Prefijos comunes: {'Eliminados' if ambiguedades_prefijos else 'No detectados'}")
    print(f"✅ Ambigüedad por operadores: {'Eliminada' if operadores else 'No detectada'}")
    print("✅ Gramática transformada correctamente")


if __name__ == "__main__":
    main()