package com.example.listedecontactsversion2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.listedecontactsversion2.databinding.FragmentCarteBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class CarteContactFragment : Fragment(), OnMapReadyCallback {

    private var _b: FragmentCarteBinding? = null
    private val b get() = _b!!
    private val vm: ContactsViewModel by activityViewModels()
    private var contact: Contact? = null

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        _b = FragmentCarteBinding.inflate(i, c, false)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        b.btnRetour.setOnClickListener { parentFragmentManager.popBackStack() }

        contact = vm.getById(requireArguments().getInt(ARG_ID))
        val c = contact
        when {
            c == null -> showMessage(R.string.err_contact)
            c.latitude == null || c.longitude == null -> showMessage(R.string.msg_pas_coord)
            !c.hasValidPosition() -> showMessage(R.string.msg_coord_invalides)
            else -> {
                val map = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
                map.getMapAsync(this)
            }
        }
    }

    private fun showMessage(res: Int) {
        b.tvMessage.setText(res)
        b.tvMessage.visibility = View.VISIBLE
        b.map.visibility = View.GONE
    }

    override fun onMapReady(googleMap: GoogleMap) {
        val c = contact ?: return
        val pos = LatLng(c.latitude!!, c.longitude!!)
        googleMap.addMarker(MarkerOptions().position(pos).title("${c.nom} ${c.prenom}"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 14f))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }

    companion object {
        private const val ARG_ID = "contact_id"
        fun newInstance(id: Int) = CarteContactFragment().apply {
            arguments = bundleOf(ARG_ID to id)
        }
    }
}
