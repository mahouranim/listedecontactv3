package com.example.listedecontactsversion2

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * ViewModel partagé (portée Activity) : source unique des contacts en mémoire.
 * Choisi plutôt que Fragment Result API car la liste OBSERVE les données :
 * elle se met à jour toute seule, survit à la rotation et évite de gérer
 * des résultats ponctuels entre fragments.
 */
class ContactsViewModel : ViewModel() {

    private val _contacts = MutableLiveData(demo())
    val contacts: LiveData<List<Contact>> = _contacts

    fun getById(id: Int): Contact? = _contacts.value.orEmpty().firstOrNull { it.id == id }

    fun update(contact: Contact) {
        _contacts.value = _contacts.value.orEmpty().map { if (it.id == contact.id) contact else it }
    }

    private fun demo() = listOf(
        Contact(1, "Ben Ali", "Ahmed", "22111222", "ahmed@gmail.com", null, 36.8065, 10.1815),
        Contact(2, "Trabelsi", "Sarra", "55123456", "sarra@gmail.com", null, 34.7406, 10.7603),
        Contact(3, "Mansouri", "Mohamed", "99123456", "mohamed@gmail.com"),
        Contact(4, "Jaziri", "Amira", "20123456", "amira@gmail.com", null, 35.8256, 10.6084)
    )
}
