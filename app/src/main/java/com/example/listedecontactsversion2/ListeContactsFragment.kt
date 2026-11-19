package com.example.listedecontactsversion2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.listedecontactsversion2.databinding.FragmentListeBinding

class ListeContactsFragment : Fragment() {

    private var _b: FragmentListeBinding? = null
    private val b get() = _b!!
    private val vm: ContactsViewModel by activityViewModels()
    private lateinit var adapter: ContactAdapter

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        _b = FragmentListeBinding.inflate(i, c, false)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = ContactAdapter { contact ->
            (requireActivity() as MainActivity)
                .navigateTo(DetailContactFragment.newInstance(contact.id))
        }
        b.recycler.adapter = adapter
        b.recycler.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))

        // Filtrage au fil de la saisie
        b.etSearch.doAfterTextChanged { applyFilter() }
        // Mise à jour automatique après modification d'un contact
        vm.contacts.observe(viewLifecycleOwner) { applyFilter() }
    }

    private fun applyFilter() {
        val q = b.etSearch.text.toString().trim()
        val all = vm.contacts.value.orEmpty()
        val result = if (q.isEmpty()) all else all.filter {
            it.nom.contains(q, ignoreCase = true) ||
                it.prenom.contains(q, ignoreCase = true) ||
                it.telephone.contains(q, ignoreCase = true)
        }
        adapter.submitList(result)
        b.tvEmpty.visibility = if (result.isEmpty()) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }
}
