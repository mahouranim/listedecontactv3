package com.example.listedecontactsversion2

data class Contact(
    val id: Int,
    val nom: String,
    val prenom: String,
    val telephone: String,
    val email: String,
    val photoUri: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null
) {
    /** Vrai si les deux coordonnées existent et sont dans les bornes. */
    fun hasValidPosition(): Boolean {
        val la = latitude ?: return false
        val lo = longitude ?: return false
        return la in -90.0..90.0 && lo in -180.0..180.0
    }
}
