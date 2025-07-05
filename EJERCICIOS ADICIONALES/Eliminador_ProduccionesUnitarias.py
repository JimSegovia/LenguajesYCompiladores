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


def es_produccion_unitaria(produccion, no_terminales):
    """Determina si una producción es unitaria (A → B donde B es no terminal)"""
    return len(produccion) == 1 and produccion[0] in no_terminales


def detectar_producciones_unitarias(producciones, no_terminales):
    """Detecta todas las producciones unitarias en la gramática"""
    unitarias = {}

    for no_terminal in producciones:
        unitarias[no_terminal] = []
        for produccion in producciones[no_terminal]:
            if es_produccion_unitaria(produccion, no_terminales):
                unitarias[no_terminal].append(produccion[0])

    # Eliminar entradas vacías
    unitarias = {k: v for k, v in unitarias.items() if v}

    return unitarias


def calcular_variables_accesibles(A, producciones, no_terminales):
    """
    Calcula el conjunto VA = {B ∈ Vn / A →* B}
    Variables accesibles desde A por derivaciones unitarias
    """
    # Paso 1: Inicializar V0 = {A}
    V_anterior = {A}
    i = 0

    print(f"Calculando variables accesibles desde {A}:")
    print(f"V{i} = {V_anterior}")

    while True:
        i += 1
        # Paso 2: Vi = Vi-1 ∪ {C ∈ Vn / B → C ∈ P ∧ B ∈ Vi-1}
        V_actual = V_anterior.copy()

        for B in V_anterior:
            if B in producciones:
                for produccion in producciones[B]:
                    if es_produccion_unitaria(produccion, no_terminales):
                        C = produccion[0]
                        V_actual.add(C)

        print(f"V{i} = {V_actual}")

        # Paso 3: Si Vi = Vi-1, entonces VA = Vi
        if V_actual == V_anterior:
            break

        V_anterior = V_actual

    print(f"VA para {A} = {V_actual}")
    return V_actual


def eliminar_producciones_unitarias(producciones, no_terminales):
    """Elimina las producciones unitarias de la gramática"""
    print("\n=== ELIMINANDO PRODUCCIONES UNITARIAS ===")

    # Detectar producciones unitarias
    unitarias = detectar_producciones_unitarias(producciones, no_terminales)

    if not unitarias:
        print("✅ No se encontraron producciones unitarias.")
        return producciones

    print("\n📋 Producciones unitarias encontradas:")
    for nt, unidades in unitarias.items():
        for unidad in unidades:
            print(f"  {nt} → {unidad}")

    # Paso 1: Calcular VA para cada A ∈ Vn
    variables_accesibles = {}

    for A in no_terminales:
        print(f"\n--- Calculando variables accesibles para {A} ---")
        variables_accesibles[A] = calcular_variables_accesibles(A, producciones, no_terminales)

    # Paso 2: Construir P' - nuevas producciones
    print("\n--- Construyendo nuevas producciones ---")
    nuevas_producciones = {}

    for A in no_terminales:
        nuevas_producciones[A] = []

        # Para cada B ∈ VA
        for B in variables_accesibles[A]:
            if B in producciones:
                # Para cada producción B → α que NO es unitaria
                for produccion in producciones[B]:
                    if not es_produccion_unitaria(produccion, no_terminales):
                        # Añadir A → α a P'
                        if produccion not in nuevas_producciones[A]:
                            nuevas_producciones[A].append(produccion)
                            print(f"  Añadiendo: {A} → {' '.join(produccion)} (desde {B})")

    # Eliminar entradas vacías
    nuevas_producciones = {k: v for k, v in nuevas_producciones.items() if v}

    return nuevas_producciones


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
    print("=== ELIMINADOR DE PRODUCCIONES UNITARIAS ===\n")
    print("Una producción unitaria tiene la forma A → B donde B es un no terminal.")
    print("Ejemplo: E → T, T → F son producciones unitarias.\n")

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

    # Eliminar producciones unitarias
    nuevas_producciones = eliminar_producciones_unitarias(producciones, no_terminales)

    if nuevas_producciones != producciones:
        imprimir_producciones(nuevas_producciones, "GRAMÁTICA SIN PRODUCCIONES UNITARIAS")

        print("\n📊 RESUMEN DE CAMBIOS:")
        print("Producciones unitarias eliminadas:")
        unitarias = detectar_producciones_unitarias(producciones, no_terminales)
        for nt, unidades in unitarias.items():
            for unidad in unidades:
                print(f"  ❌ {nt} → {unidad}")

        print("\nNuevas producciones añadidas:")
        for nt in sorted(nuevas_producciones.keys()):
            for prod in nuevas_producciones[nt]:
                prod_str = ' '.join(prod)
                # Verificar si es una producción nueva
                if nt not in producciones or prod not in producciones[nt]:
                    print(f"  ✅ {nt} → {prod_str}")
    else:
        print("✅ La gramática no tenía producciones unitarias para eliminar.")


if __name__ == "__main__":
    main()