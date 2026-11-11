package com.awa2027.rpgquest.domain

/** Dados poliédricos de la arena, en orden de menor a mayor número de caras. */
enum class Die(val sides: Int) {
    D4(4),
    D6(6),
    D8(8),
    D10(10),
    D12(12),
    D20(20)
    ;

    val label: String get() = "d$sides"
}
