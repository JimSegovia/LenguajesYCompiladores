def obtener_terminales():
    """Obtiene los símbolos terminales del usuario"""
    while True:
        print("\n--- SÍMBOLOS TERMINALES ---")
        entrada = input("Ingresa los símbolos terminales (separados por coma): ").strip()
        if entrada:
            terminales = [t.strip() for t in entrada.split(',') if t.strip()]
            if terminales:
                return set(terminales)
        print("⚠️  Por favor, ingresa al menos un símbolo terminal.")


def obtener_no_terminales():
    """Obtiene los símbolos no terminales del usuario"""
    while True:
        print("\n--- SÍMBOLOS NO TERMINALES ---")
        entrada = input("Ingresa los símbolos no terminales (separados por coma): ").strip()
        if entrada:
            no_terminales = [nt.strip() for nt in entrada.split(',') if nt.strip()]
            if no_terminales:
                return set(no_terminales)
        print("⚠️  Por favor, ingresa al menos un símbolo no terminal.")


def obtener_simbolo_inicial():
    """Obtiene el símbolo inicial de la gramática"""
    while True:
        print("\n--- SÍMBOLO INICIAL ---")
        entrada = input("Ingresa el símbolo inicial: ").strip()
        if entrada:
            return entrada
        print("⚠️  Por favor, ingresa un símbolo inicial válido.")


def parsear_produccion(linea):
    """Parsea una línea de producción y devuelve (lado_izquierdo, [alternativas])"""
    linea = linea.strip()
    if not linea:
        return None, []

    # Soportar diferentes tipos de flechas
    if '→' in linea:
        partes = linea.split('→')
    elif '->' in linea:
        partes = linea.split('->')
    else:
        print(f"❌ Error: La producción '{linea}' no tiene flecha válida (→ o ->)")
        return None, []

    if len(partes) != 2:
        print(f"❌ Error: La producción '{linea}' no tiene formato válido")
        return None, []

    lado_izquierdo = partes[0].strip()
    lado_derecho = partes[1].strip()

    alternativas = []
    for alt in lado_derecho.split('|'):
        alt = alt.strip()
        if alt:
            # Tratar λ, ε, y epsilon como epsilon
            if alt.lower() in ['λ', 'ε', 'epsilon']:
                simbolos = ['ε']
            else:
                simbolos = [s.strip() for s in alt.split() if s.strip()]
            alternativas.append(simbolos)

    return lado_izquierdo, alternativas


def obtener_producciones(terminales, no_terminales):
    """Obtiene las producciones del usuario"""
    print("\n--- PRODUCCIONES ---")
    print("📝 Ingresa las producciones (una por línea).")
    print("📝 Formato: A → α | β (símbolos separados por espacios)")
    print("📝 Usa λ, ε, o epsilon para producciones vacías")
    print("📝 Presiona Enter en una línea vacía para terminar.")
    print()

    producciones = {}
    linea_num = 1

    while True:
        linea = input(f"Producción {linea_num}: ").strip()
        if not linea:
            break

        lado_izq, alternativas = parsear_produccion(linea)

        if lado_izq is None:
            continue

        if lado_izq not in no_terminales:
            print(f"⚠️  Advertencia: '{lado_izq}' no está en los no terminales declarados")

        if lado_izq not in producciones:
            producciones[lado_izq] = []

        producciones[lado_izq].extend(alternativas)
        print(f"✅ Agregada: {lado_izq} → {' | '.join([' '.join(alt) for alt in alternativas])}")
        linea_num += 1

    return producciones


def generar_nuevo_no_terminal(base, no_terminales_existentes):
    """Genera un nuevo símbolo no terminal único"""
    contador = 1
    while True:
        nuevo = f"{base}{contador}"
        if nuevo not in no_terminales_existentes:
            return nuevo
        contador += 1


def generar_variable_terminal(terminal, variables_existentes):
    """Genera una nueva variable para sustituir un terminal"""
    # Usar la letra mayúscula del terminal
    if terminal.isalpha():
        base = terminal.upper()
    else:
        base = 'T'

    if base not in variables_existentes:
        return base

    contador = 1
    while True:
        nuevo = f"{base}{contador}"
        if nuevo not in variables_existentes:
            return nuevo
        contador += 1


def esta_en_fnc(produccion, terminales, no_terminales):
    """Verifica si una producción está en FNC"""
    if len(produccion) == 1:
        # A → a (terminal) o A → ε
        return produccion[0] in terminales or produccion[0] == 'ε'
    elif len(produccion) == 2:
        # A → BC (dos no terminales)
        return all(simbolo in no_terminales for simbolo in produccion)
    else:
        return False


def encontrar_simbolos_anulables(producciones):
    """Encuentra todos los símbolos que pueden derivar épsilon"""
    anulables = set()

    # Buscar producciones directas a épsilon
    for nt in producciones:
        for prod in producciones[nt]:
            if prod == ['ε']:
                anulables.add(nt)

    # Propagar anulabilidad
    cambios = True
    while cambios:
        cambios = False
        for nt in producciones:
            if nt not in anulables:
                for prod in producciones[nt]:
                    if prod != ['ε'] and len(prod) > 0 and all(simbolo in anulables for simbolo in prod):
                        anulables.add(nt)
                        cambios = True
                        break

    return anulables


def generar_combinaciones_sin_anulables(produccion, anulables):
    """Genera todas las combinaciones posibles eliminando símbolos anulables"""
    if not produccion:
        return [[]]

    if produccion[0] in anulables:
        # El símbolo es anulable: incluirlo o no incluirlo
        con_simbolo = [[produccion[0]] + resto for resto in
                       generar_combinaciones_sin_anulables(produccion[1:], anulables)]
        sin_simbolo = generar_combinaciones_sin_anulables(produccion[1:], anulables)
        return con_simbolo + sin_simbolo
    else:
        # El símbolo no es anulable: incluirlo obligatoriamente
        return [[produccion[0]] + resto for resto in
                generar_combinaciones_sin_anulables(produccion[1:], anulables)]


def eliminar_producciones_epsilon(producciones, simbolo_inicial):
    """Elimina producciones épsilon manteniendo el lenguaje"""
    print("\n🔄 PASO 1: Eliminando producciones épsilon...")

    anulables = encontrar_simbolos_anulables(producciones)

    if anulables:
        print(f"📋 Símbolos anulables: {sorted(anulables)}")
    else:
        print("📋 No se encontraron símbolos anulables")
        return producciones

    nuevas_producciones = {}

    for nt in producciones:
        nuevas_producciones[nt] = []

        for prod in producciones[nt]:
            if prod == ['ε']:
                # Solo mantener S → ε si S es el símbolo inicial
                if nt == simbolo_inicial:
                    nuevas_producciones[nt].append(prod)
                    print(f"   Manteniendo: {nt} → ε (símbolo inicial)")
                else:
                    print(f"   Eliminando: {nt} → ε")
                continue

            # Generar todas las combinaciones eliminando símbolos anulables
            combinaciones = generar_combinaciones_sin_anulables(prod, anulables)

            for comb in combinaciones:
                if comb and comb not in nuevas_producciones[nt]:
                    nuevas_producciones[nt].append(comb)
                    if comb != prod:
                        print(f"   Agregando: {nt} → {' '.join(comb)}")

    # Limpiar producciones vacías
    nuevas_producciones = {nt: prods for nt, prods in nuevas_producciones.items() if prods}

    return nuevas_producciones


def eliminar_producciones_unitarias(producciones, no_terminales):
    """Elimina producciones unitarias A → B donde B es no terminal"""
    print("\n🔄 PASO 2: Eliminando producciones unitarias...")

    # Encontrar producciones unitarias
    unitarias = {}
    for nt in producciones:
        unitarias[nt] = set()
        unitarias[nt].add(nt)  # Reflexiva

        for prod in producciones[nt]:
            if len(prod) == 1 and prod[0] in no_terminales:
                unitarias[nt].add(prod[0])
                print(f"   Encontrada: {nt} → {prod[0]}")

    # Calcular cierre transitivo
    cambios = True
    while cambios:
        cambios = False
        for nt in unitarias:
            for intermedio in list(unitarias[nt]):
                if intermedio in unitarias:
                    for destino in unitarias[intermedio]:
                        if destino not in unitarias[nt]:
                            unitarias[nt].add(destino)
                            cambios = True

    # Crear nuevas producciones
    nuevas_producciones = {}

    for nt in producciones:
        nuevas_producciones[nt] = []

        # Para cada símbolo alcanzable desde nt
        for objetivo in unitarias[nt]:
            if objetivo in producciones:
                for prod in producciones[objetivo]:
                    # Solo agregar producciones no unitarias
                    if not (len(prod) == 1 and prod[0] in no_terminales):
                        if prod not in nuevas_producciones[nt]:
                            nuevas_producciones[nt].append(prod)
                            if objetivo != nt:
                                print(f"   Agregando: {nt} → {' '.join(prod)} (de {objetivo})")

    return nuevas_producciones


def convertir_terminales_a_variables(producciones, terminales, no_terminales):
    """Convierte terminales en producciones mixtas a nuevas variables"""
    print("\n🔄 PASO 3: Convirtiendo terminales a variables...")

    variables_terminales = {}
    todas_variables = no_terminales.copy()
    nuevas_producciones = {}

    for nt in producciones:
        nuevas_producciones[nt] = []

        for prod in producciones[nt]:
            if len(prod) >= 2:  # Producción mixta o larga
                nueva_prod = []

                for simbolo in prod:
                    if simbolo in terminales:
                        # Crear variable para el terminal
                        if simbolo not in variables_terminales:
                            nueva_var = generar_variable_terminal(simbolo, todas_variables)
                            variables_terminales[simbolo] = nueva_var
                            todas_variables.add(nueva_var)
                            print(f"   Creando: {nueva_var} → {simbolo}")

                        nueva_prod.append(variables_terminales[simbolo])
                    else:
                        nueva_prod.append(simbolo)

                nuevas_producciones[nt].append(nueva_prod)
                if nueva_prod != prod:
                    print(f"   Transformando: {nt} → {' '.join(prod)} ⇒ {nt} → {' '.join(nueva_prod)}")
            else:
                # Producción de longitud 1
                nuevas_producciones[nt].append(prod)

    return nuevas_producciones, variables_terminales, todas_variables


def descomponer_producciones_largas(producciones, todas_variables):
    """Descompone producciones de longitud > 2"""
    print("\n🔄 PASO 4: Descomponiendo producciones largas...")

    producciones_finales = {}

    for nt in producciones:
        producciones_finales[nt] = []

        for prod in producciones[nt]:
            if len(prod) > 2:
                print(f"   Descomponiendo: {nt} → {' '.join(prod)}")

                # A → X1 X2 X3 ... Xn se convierte en:
                # A → X1 R1, R1 → X2 R2, ..., Rn-2 → Xn-1 Xn

                var_actual = nt

                for i in range(len(prod) - 2):
                    if i == 0:
                        # Primera producción: A → X1 R1
                        nueva_var = generar_nuevo_no_terminal('R', todas_variables)
                        todas_variables.add(nueva_var)
                        producciones_finales[nt].append([prod[0], nueva_var])
                        print(f"      {nt} → {prod[0]} {nueva_var}")
                        var_actual = nueva_var
                    else:
                        # Producciones intermedias: Ri → Xi+1 Ri+1
                        siguiente_var = generar_nuevo_no_terminal('R', todas_variables)
                        todas_variables.add(siguiente_var)

                        if var_actual not in producciones_finales:
                            producciones_finales[var_actual] = []

                        producciones_finales[var_actual].append([prod[i], siguiente_var])
                        print(f"      {var_actual} → {prod[i]} {siguiente_var}")
                        var_actual = siguiente_var

                # Última producción: Rn-2 → Xn-1 Xn
                if var_actual not in producciones_finales:
                    producciones_finales[var_actual] = []

                producciones_finales[var_actual].append([prod[-2], prod[-1]])
                print(f"      {var_actual} → {prod[-2]} {prod[-1]}")

            else:
                # Producción de longitud <= 2
                producciones_finales[nt].append(prod)

    return producciones_finales


def agregar_producciones_terminales(producciones, variables_terminales):
    """Agrega producciones X → a para variables terminales"""
    print("\n🔄 PASO 5: Agregando producciones terminales...")

    for terminal, variable in variables_terminales.items():
        if variable not in producciones:
            producciones[variable] = []

        if [terminal] not in producciones[variable]:
            producciones[variable].append([terminal])
            print(f"   Agregando: {variable} → {terminal}")

    return producciones


def imprimir_producciones(producciones, titulo="PRODUCCIONES"):
    """Imprime las producciones de forma organizada"""
    print(f"\n{'=' * 60}")
    print(f"📋 {titulo}")
    print(f"{'=' * 60}")

    if not producciones:
        print("(No hay producciones)")
        return

    for nt in sorted(producciones.keys()):
        if producciones[nt]:
            alternativas = [' '.join(prod) for prod in producciones[nt]]
            print(f"{nt} → {' | '.join(alternativas)}")


def verificar_fnc(producciones, terminales, no_terminales):
    """Verifica que todas las producciones estén en FNC"""
    print("\n🔍 VERIFICANDO FORMA NORMAL DE CHOMSKY...")

    todas_correctas = True

    for nt in producciones:
        for prod in producciones[nt]:
            if not esta_en_fnc(prod, terminales, no_terminales):
                print(f"❌ ERROR: {nt} → {' '.join(prod)} no está en FNC")
                todas_correctas = False
            else:
                tipo = ""
                if len(prod) == 1:
                    if prod[0] in terminales:
                        tipo = "(terminal)"
                    elif prod[0] == 'ε':
                        tipo = "(épsilon)"
                elif len(prod) == 2:
                    tipo = "(dos no terminales)"

                print(f"✅ {nt} → {' '.join(prod)} {tipo}")

    if todas_correctas:
        print("\n🎉 ¡TODAS LAS PRODUCCIONES ESTÁN EN FNC!")
    else:
        print("\n❌ HAY ERRORES EN LA CONVERSIÓN")

    return todas_correctas


def convertir_a_fnc(producciones, terminales, no_terminales, simbolo_inicial):
    """Convierte una gramática a Forma Normal de Chomsky"""
    print("\n" + "=" * 60)
    print("🚀 INICIANDO CONVERSIÓN A FORMA NORMAL DE CHOMSKY")
    print("=" * 60)

    # Paso 1: Eliminar producciones épsilon
    producciones = eliminar_producciones_epsilon(producciones, simbolo_inicial)
    imprimir_producciones(producciones, "DESPUÉS DE ELIMINAR ÉPSILON")

    # Actualizar conjunto de no terminales
    no_terminales_actualizados = set(producciones.keys())

    # Paso 2: Eliminar producciones unitarias
    producciones = eliminar_producciones_unitarias(producciones, no_terminales_actualizados)
    imprimir_producciones(producciones, "DESPUÉS DE ELIMINAR UNITARIAS")

    # Paso 3: Convertir terminales a variables
    producciones, variables_terminales, todas_variables = convertir_terminales_a_variables(
        producciones, terminales, no_terminales_actualizados
    )
    imprimir_producciones(producciones, "DESPUÉS DE CONVERTIR TERMINALES")

    # Paso 4: Descomponer producciones largas
    producciones = descomponer_producciones_largas(producciones, todas_variables)
    imprimir_producciones(producciones, "DESPUÉS DE DESCOMPONER LARGAS")

    # Paso 5: Agregar producciones terminales
    producciones = agregar_producciones_terminales(producciones, variables_terminales)

    return producciones, todas_variables


def main():
    print("🎯 CONVERTIDOR A FORMA NORMAL DE CHOMSKY")
    print("=" * 50)

    # Obtener componentes de la gramática
    terminales = obtener_terminales()
    print(f"✅ Terminales: {sorted(terminales)}")

    no_terminales = obtener_no_terminales()
    print(f"✅ No terminales: {sorted(no_terminales)}")

    simbolo_inicial = obtener_simbolo_inicial()
    print(f"✅ Símbolo inicial: {simbolo_inicial}")

    # Verificar que el símbolo inicial esté en los no terminales
    if simbolo_inicial not in no_terminales:
        print(f"⚠️  Advertencia: El símbolo inicial '{simbolo_inicial}' no está en los no terminales")
        no_terminales.add(simbolo_inicial)

    # Obtener producciones
    producciones = obtener_producciones(terminales, no_terminales)

    if not producciones:
        print("❌ No se ingresaron producciones válidas.")
        return

    # Mostrar gramática original
    imprimir_producciones(producciones, "GRAMÁTICA ORIGINAL")

    # Convertir a FNC
    producciones_fnc, no_terminales_finales = convertir_a_fnc(
        producciones, terminales, no_terminales, simbolo_inicial
    )

    # Mostrar resultado final
    print("\n" + "=" * 60)
    print("🎉 RESULTADO FINAL")
    print("=" * 60)
    imprimir_producciones(producciones_fnc, "GRAMÁTICA EN FORMA NORMAL DE CHOMSKY")

    # Mostrar vocabulario final
    print(f"\n📚 VOCABULARIO FINAL:")
    print(f"   • Terminales: {sorted(terminales)}")
    print(f"   • No terminales: {sorted(no_terminales_finales)}")
    print(f"   • Símbolo inicial: {simbolo_inicial}")

    # Verificar FNC
    verificar_fnc(producciones_fnc, terminales, no_terminales_finales)


if __name__ == "__main__":
    main()