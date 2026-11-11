# AGENTS.md — Reglas para asistentes de IA en RPG Quest & Dice Tracker (Unidad 1)

Este archivo le da contexto permanente a cualquier asistente de código que trabaje en el repositorio. Es una base: como parte de tu entrega debes ampliarlo con al menos una regla propia (ver `ARCHITECTURE_DECISIONS.md`, pregunta 5).

## 1. Entorno y comandos

- Proyecto Android con Gradle (wrapper incluido), Kotlin, Jetpack Compose, Material 3, Navigation Compose type-safe y Koin 4. JDK 21.
- Comandos: `./gradlew testDebugUnitTest`, `./gradlew check` (ktlint, lint de Android y tests), `./gradlew ktlintFormat`, `./gradlew assembleDebug`.
- Las versiones viven en `gradle/libs.versions.toml`. No agregues dependencias ni cambies versiones o archivos de Gradle sin que la persona lo pida.

## 2. Capas

- `domain/`: modelos inmutables y funciones puras. No importa nada de Android, Compose ni Koin.
- `data/`: interfaces (`DiceRoller`, `QuestRepository`, `RollHistoryRepository`) y sus implementaciones. Los ViewModels dependen de las interfaces, nunca de las clases concretas.
- `ui/`: cada pantalla tiene una función con estado (`XxxScreen`, obtiene el ViewModel con `koinViewModel`) y una sin estado (`XxxContent`, recibe el estado y lambdas de eventos, con `@Preview`).
- `di/AppModule.kt`: el único lugar donde se crean las implementaciones.

## 3. Estado y flujo unidireccional

- El estado de pantalla es una `data class` inmutable con `val` y listas o mapas de solo lectura. Se actualiza con `copy` dentro de `_uiState.update { }`.
- `MutableStateFlow` siempre es privado; se expone como `StateFlow` con `asStateFlow()` o `stateIn`.
- El estado baja y los eventos suben: la UI llama `onEvent(...)` con una `sealed interface` de eventos y nunca modifica el estado directamente.
- Lo derivado (filtros, notación, estadísticas) se calcula a partir del estado; no se guarda duplicado.
- En Compose: nunca `mutableStateOf` sin `remember`; estado de UI que debe sobrevivir a la rotación con `rememberSaveable`; estado de la pantalla en el ViewModel. `LazyColumn` siempre con `key` estable.

## 4. Corrutinas y aleatoriedad

- Prohibido `Thread.sleep` y `runBlocking` en código de la app: se usa `delay` dentro de `viewModelScope.launch`.
- La aleatoriedad entra solo por `DiceRoller`. Prohibido `Random`, `Math.random` o `(1..n).random()` en ViewModels o en `domain/`.

## 5. Pruebas

- No modifiques `TestDoubles.kt`, `DemoTest.kt`, `WorkshopTest.kt`, `ChallengeTest.kt`, los archivos de Gradle ni `.github/workflows/ci.yml`.
- Los tests propios viven en `app/src/test/kotlin/com/awa2027/rpgquest/student/`, son JVM puros (sin emulador, red ni archivos) y usan `SequenceDiceRoller`, `runTest` y Turbine.
- Ningún código de `app/src/main/` puede detectar si corre bajo pruebas.

## 6. Reglas propias del alumno

(Agrega aquí tus reglas y explica su efecto en `ARCHITECTURE_DECISIONS.md`.)
