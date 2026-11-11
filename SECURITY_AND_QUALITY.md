# Checklist de seguridad y calidad — RPG Quest & Dice Tracker (Unidad 1)

Marca cada casilla solo si la verificaste en tu código y escribe la evidencia (archivo y función, comando o test). Una casilla marcada sin evidencia no cuenta.

- [ ] **Estado inmutable.** Ningún estado de pantalla ni `DicePool` usa `var`, `MutableList` o `MutableMap` públicos; cada cambio crea un valor nuevo con `copy`. Evidencia:
- [ ] **Flujos de solo lectura.** Todo `MutableStateFlow` es privado y la UI recibe `StateFlow`. Evidencia:
- [ ] **Flujo unidireccional.** Las pantallas sin estado (`XxxContent`) reciben el estado y lambdas de eventos; no conocen al ViewModel. Evidencia:
- [ ] **Sin bloqueos.** No hay `Thread.sleep` ni `runBlocking` en `app/src/main/`; la animación usa `delay` en `viewModelScope`. Evidencia:
- [ ] **Aleatoriedad inyectada.** Solo `RandomDiceRoller` usa `Random`; el resto recibe `DiceRoller`, y los tests usan `SequenceDiceRoller`. Evidencia:
- [ ] **Límites del contrato.** Máximo 10 dados, modificador de -10 a +10 en el ViewModel y en `resolveRoll`, total mínimo 1. Evidencia:
- [ ] **Navegación type-safe.** Las rutas son clases `@Serializable` y ningún destino arma rutas con texto. Evidencia:
- [ ] **Listas con identidad.** Cada `LazyColumn` usa `key` estable (id de misión, id de tirada o total). Evidencia:
- [ ] **Sin secretos ni datos personales.** No hay credenciales ni datos personales en el código; la app no hace llamadas de red. Evidencia:
- [ ] **Calidad.** `./gradlew check` en verde (ktlint, lint de Android y tests), `./gradlew -PwarningsAsErrors=true ktlintCheck lintDebug assembleDebug` sin advertencias y ningún archivo oficial modificado. Evidencia:
