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


def esta_en_fng(produccion, terminales, no_terminales):
    """Verifica si una producción está en FNG"""
    if not produccion:
        return False

    # Caso especial: producción épsilon
    if produccion == ['ε']:
        return True

    # Debe empezar con un terminal
    if produccion[0] not in terminales:
        return False

    # El resto deben ser no terminales
    return all(simbolo in no_terminales for simbolo in produccion[1:])


def tiene_recursion_izquierda(producciones):
    """Detecta si la gramática tiene recursión por la izquierda"""
    print("\n🔍 DETECTANDO RECURSIÓN POR LA IZQUIERDA...")

    recursion_directa = {}

    for nt in producciones:
        recursion_directa[nt] = []
        for prod in producciones[nt]:
            if prod and prod[0] == nt:
                recursion_directa[nt].append(prod)
                print(f"   Recursión directa: {nt} → {' '.join(prod)}")

    return recursion_directa


def eliminar_recursion_izquierda_directa(producciones, nt, todas_variables):
    """Elimina la recursión por la izquierda directa de un no terminal"""
    print(f"\n🔄 Eliminando recursión directa en {nt}...")

    # Separar producciones recursivas y no recursivas
    recursivas = []
    no_recursivas = []

    for prod in producciones[nt]:
        if prod and prod[0] == nt:
            recursivas.append(prod[1:])  # Quitar el primer símbolo (A)
        else:
            no_recursivas.append(prod)

    if not recursivas:
        return producciones

    # Crear nueva variable A'
    nueva_var = generar_nuevo_no_terminal(f"{nt}'", todas_variables)
    todas_variables.add(nueva_var)

    # Nuevas producciones para A
    nuevas_prod_A = []
    for prod in no_recursivas:
        if prod == ['ε']:
            nuevas_prod_A.append([nueva_var])
        else:
            nuevas_prod_A.append(prod + [nueva_var])

    # Nuevas producciones para A'
    nuevas_prod_A_prima = []
    for prod in recursivas:
        if not prod:  # Si α es vacío
            nuevas_prod_A_prima.append([nueva_var])
        else:
            nuevas_prod_A_prima.append(prod + [nueva_var])

    nuevas_prod_A_prima.append(['ε'])  # A' → ε

    # Actualizar producciones
    producciones[nt] = nuevas_prod_A
    producciones[nueva_var] = nuevas_prod_A_prima

    print(f"   Creada nueva variable: {nueva_var}")
    print(f"   {nt} → {' | '.join([' '.join(p) for p in nuevas_prod_A])}")
    print(f"   {nueva_var} → {' | '.join([' '.join(p) for p in nuevas_prod_A_prima])}")

    return producciones


def eliminar_recursion_izquierda(producciones, no_terminales):
    """Elimina toda la recursión por la izquierda"""
    print("\n🔄 PASO 1: Eliminando recursión por la izquierda...")

    todas_variables = no_terminales.copy()

    # Eliminar recursión directa
    for nt in list(producciones.keys()):
        if nt in producciones:
            recursion_directa = tiene_recursion_izquierda({nt: producciones[nt]})
            if recursion_directa[nt]:
                producciones = eliminar_recursion_izquierda_directa(producciones, nt, todas_variables)

    # Ordenar no terminales para eliminar recursión indirecta
    no_terminales_ordenados = sorted(producciones.keys())

    # Eliminar recursión indirecta
    print("\n🔄 Eliminando recursión indirecta...")
    for i in range(len(no_terminales_ordenados)):
        Ai = no_terminales_ordenados[i]
        if Ai not in producciones:
            continue

        # Sustituir Ai → Aj β donde j < i
        for j in range(i):
            Aj = no_terminales_ordenados[j]
            if Aj not in producciones:
                continue

            nuevas_producciones = []
            cambios = False

            for prod in producciones[Ai]:
                if prod and prod[0] == Aj:
                    # Sustituir Aj por todas sus producciones
                    for prod_Aj in producciones[Aj]:
                        if prod_Aj == ['ε']:
                            nueva_prod = prod[1:]
                        else:
                            nueva_prod = prod_Aj + prod[1:]

                        if nueva_prod not in nuevas_producciones:
                            nuevas_producciones.append(nueva_prod)
                    cambios = True
                    print(f"   Sustituyendo {Ai} → {Aj} {' '.join(prod[1:])} por producciones de {Aj}")
                else:
                    nuevas_producciones.append(prod)

            if cambios:
                producciones[Ai] = nuevas_producciones

        # Eliminar recursión directa que pudo haberse creado
        recursion_directa = tiene_recursion_izquierda({Ai: producciones[Ai]})
        if recursion_directa[Ai]:
            producciones = eliminar_recursion_izquierda_directa(producciones, Ai, todas_variables)

    return producciones, todas_variables


def convertir_a_fng(producciones, terminales, no_terminales):
    """Convierte producciones que no están en FNG"""
    print("\n🔄 PASO 2: Convirtiendo a Forma Normal de Greibach...")

    todas_variables = no_terminales.copy()
    cambios = True

    while cambios:
        cambios = False

        for nt in list(producciones.keys()):
            if nt not in producciones:
                continue

            nuevas_producciones = []

            for prod in producciones[nt]:
                if prod == ['ε']:
                    # Mantener producciones épsilon
                    nuevas_producciones.append(prod)
                elif not prod:
                    continue
                elif prod[0] in terminales:
                    # Ya está en FNG
                    nuevas_producciones.append(prod)
                elif prod[0] in producciones:
                    # Sustituir el no terminal inicial por sus producciones
                    for prod_sustituir in producciones[prod[0]]:
                        if prod_sustituir == ['ε']:
                            nueva_prod = prod[1:]
                        else:
                            nueva_prod = prod_sustituir + prod[1:]

                        if nueva_prod and nueva_prod not in nuevas_producciones:
                            nuevas_producciones.append(nueva_prod)
                    cambios = True
                    print(f"   Sustituyendo {nt} → {prod[0]} {' '.join(prod[1:])} por producciones de {prod[0]}")
                else:
                    nuevas_producciones.append(prod)

            producciones[nt] = nuevas_producciones

    return producciones, todas_variables


def manejar_terminales_intermedios(producciones, terminales, todas_variables):
    """Maneja terminales que aparecen en posiciones intermedias"""
    print("\n🔄 PASO 3: Manejando terminales intermedios...")

    nuevas_producciones = {}
    variables_terminales = {}

    for nt in producciones:
        nuevas_producciones[nt] = []

        for prod in producciones[nt]:
            if prod == ['ε'] or len(prod) <= 1:
                nuevas_producciones[nt].append(prod)
                continue

            # Verificar si hay terminales después de la primera posición
            tiene_terminales_intermedios = False
            for i in range(1, len(prod)):
                if prod[i] in terminales:
                    tiene_terminales_intermedios = True
                    break

            if not tiene_terminales_intermedios:
                nuevas_producciones[nt].append(prod)
                continue

            # Crear variables para terminales intermedios
            nueva_prod = [prod[0]]  # Mantener el primer terminal

            for i in range(1, len(prod)):
                if prod[i] in terminales:
                    if prod[i] not in variables_terminales:
                        nueva_var = generar_nuevo_no_terminal(f"T_{prod[i]}", todas_variables)
                        variables_terminales[prod[i]] = nueva_var
                        todas_variables.add(nueva_var)
                        print(f"   Creando variable para terminal: {nueva_var} → {prod[i]}")

                    nueva_prod.append(variables_terminales[prod[i]])
                else:
                    nueva_prod.append(prod[i])

            nuevas_producciones[nt].append(nueva_prod)
            if nueva_prod != prod:
                print(f"   Transformando: {nt} → {' '.join(prod)} ⇒ {nt} → {' '.join(nueva_prod)}")

    # Agregar producciones para las nuevas variables terminales
    for terminal, variable in variables_terminales.items():
        nuevas_producciones[variable] = [[terminal]]

    return nuevas_producciones, todas_variables


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


def verificar_fng(producciones, terminales, no_terminales, simbolo_inicial):
    """Verifica que todas las producciones estén en FNG"""
    print("\n🔍 VERIFICANDO FORMA NORMAL DE GREIBACH...")

    todas_correctas = True

    for nt in producciones:
        for prod in producciones[nt]:
            if prod == ['ε'] and nt != simbolo_inicial:
                print(f"❌ ERROR: {nt} → ε (solo el símbolo inicial puede tener ε)")
                todas_correctas = False
            elif not esta_en_fng(prod, terminales, no_terminales) and prod != ['ε']:
                print(f"❌ ERROR: {nt} → {' '.join(prod)} no está en FNG")
                todas_correctas = False
            else:
                if prod == ['ε']:
                    print(f"✅ {nt} → ε (símbolo inicial)")
                else:
                    print(f"✅ {nt} → {' '.join(prod)} (terminal + no terminales)")

    if todas_correctas:
        print("\n🎉 ¡TODAS LAS PRODUCCIONES ESTÁN EN FNG!")
    else:
        print("\n❌ HAY ERRORES EN LA CONVERSIÓN")

    return todas_correctas


def convertir_a_fng_completo(producciones, terminales, no_terminales, simbolo_inicial):
    """Convierte una gramática a Forma Normal de Greibach"""
    print("\n" + "=" * 60)
    print("🚀 INICIANDO CONVERSIÓN A FORMA NORMAL DE GREIBACH")
    print("=" * 60)

    # Verificar recursión por la izquierda
    recursion = tiene_recursion_izquierda(producciones)
    tiene_recursion = any(recursion.values())

    if tiene_recursion:
        # Paso 1: Eliminar recursión por la izquierda
        producciones, todas_variables = eliminar_recursion_izquierda(producciones, no_terminales)
        imprimir_producciones(producciones, "DESPUÉS DE ELIMINAR RECURSIÓN IZQUIERDA")
    else:
        print("✅ No se detectó recursión por la izquierda")
        todas_variables = no_terminales.copy()

    # Paso 2: Convertir a FNG
    producciones, todas_variables = convertir_a_fng(producciones, terminales, todas_variables)
    imprimir_producciones(producciones, "DESPUÉS DE SUSTITUIR NO TERMINALES")

    # Paso 3: Manejar terminales intermedios
    producciones, todas_variables = manejar_terminales_intermedios(producciones, terminales, todas_variables)
    imprimir_producciones(producciones, "DESPUÉS DE MANEJAR TERMINALES INTERMEDIOS")

    return producciones, todas_variables


def main():
    print("🎯 CONVERTIDOR A FORMA NORMAL DE GREIBACH")
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

    # Convertir a FNG
    producciones_fng, no_terminales_finales = convertir_a_fng_completo(
        producciones, terminales, no_terminales, simbolo_inicial
    )

    # Mostrar resultado final
    print("\n" + "=" * 60)
    print("🎉 RESULTADO FINAL")
    print("=" * 60)
    imprimir_producciones(producciones_fng, "GRAMÁTICA EN FORMA NORMAL DE GREIBACH")

    # Mostrar vocabulario final
    print(f"\n📚 VOCABULARIO FINAL:")
    print(f"   • Terminales: {sorted(terminales)}")
    print(f"   • No terminales: {sorted(no_terminales_finales)}")
    print(f"   • Símbolo inicial: {simbolo_inicial}")

    # Verificar FNG
    verificar_fng(producciones_fng, terminales, no_terminales_finales, simbolo_inicial)


if __name__ == "__main__":
    main()