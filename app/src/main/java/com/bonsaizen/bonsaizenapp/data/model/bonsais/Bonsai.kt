package com.bonsaizen.bonsaizenapp.data.model.bonsais

data class Bonsai(
    val id: String = "",
    val nombre: String = "",
    val fechaAdquisicion: String = "",
    val fechaUltimoTransplante: String = "",
    val fechaProximoTransplante: String = "",
    val imagenes: List<String> = emptyList() // URLs de Firebase Storage
)
