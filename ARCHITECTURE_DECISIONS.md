# Decisiones de arquitectura — RPG Quest & Dice Tracker (Unidad 1)

Responde con tus palabras y con ejemplos de tu propio código. Respuestas genéricas que podrían aplicar a cualquier proyecto reciben poco puntaje. Extensión sugerida: de 3 a 8 líneas por pregunta.

Puntaje (15 puntos, revisión manual): preguntas 1 a 4, 10 puntos; tu regla en la sección 6 de `AGENTS.md` (pregunta 5), 2; declaración de uso de IA (pregunta 6), 1; y el checklist `SECURITY_AND_QUALITY.md`, 2.

## 1. Qué estado va en la UI y cuál en el ViewModel

Clasifica al menos cuatro datos de tu app (por ejemplo: el grupo de dados, el modo de tirada, el historial, si la pantalla de estadísticas está desplazada, si un diálogo está abierto) en `remember`, `rememberSaveable` o ViewModel. Justifica cada uno con lo que pasa al girar el teléfono y al salir de la pantalla.

**Tu respuesta:**

> 

## 2. Inmutabilidad y `StateFlow`

¿Por qué `DicePool` y los estados de pantalla son inmutables? Explica qué pasaría si `add` modificara un `MutableMap` interno y devolviera la misma instancia, relacionándolo con cómo `StateFlow` compara el valor nuevo con el anterior.

**Tu respuesta:**

> 

## 3. Inyección con Koin

¿Por qué los ViewModels reciben `DiceRoller` y los repositorios como interfaces por constructor en lugar de crearlos adentro? ¿Qué cambiaría en la app si `RollHistoryRepository` se declarara con `factory` en lugar de `single`?

**Tu respuesta:**

> 

## 4. Tus tests propios

Para cada test de `student/MyScenariosTest.kt`: ¿qué error plausible del reto atrapa? Describe un cambio concreto en `app/src/main/` (un mutante) que lo haría fallar.

**Tu respuesta:**

> 

## 5. Tu regla en AGENTS.md

Copia la regla que agregaste a `AGENTS.md`. Describe qué generó el asistente de IA antes de tener esa regla y qué cambió después (si no usaste asistente, explica qué error evitaría la regla).

**Tu respuesta:**

> 

## 6. Declaración de uso de IA

Completa con transparencia; declarar el uso no resta puntos, ocultarlo sí.

- Herramientas usadas (o "ninguna"):
- Partes del trabajo en las que las usaste:
- Algo que el asistente propuso y aceptaste después de verificarlo:
- Algo que el asistente propuso y corregiste o rechazaste, y por qué:
