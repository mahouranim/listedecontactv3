package com.example.listedecontactsversion2

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.listedecontactsversion2.databinding.FragmentDetailBinding
import java.io.File

class DetailContactFragment : Fragment() {

    private var _b: FragmentDetailBinding? = null
    private val b get() = _b!!
    private val vm: ContactsViewModel by activityViewModels()

    private var contact: Contact? = null
    private var photoUri: String? = null     // photo en cours d'édition (non enregistrée)
    private var cameraUri: Uri? = null

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {              // null = annulation : on ne fait rien
                photoUri = uri.toString()
                b.imgPhoto.showContactPhoto(photoUri)
            }
        }

    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { ok ->
            if (ok && cameraUri != null) {
                photoUri = cameraUri.toString()
                b.imgPhoto.showContactPhoto(photoUri)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        savedInstanceState?.let {
            photoUri = it.getString(KEY_PHOTO)
            cameraUri = it.getString(KEY_CAM)?.let(Uri::parse)
        }
    }

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        _b = FragmentDetailBinding.inflate(i, c, false)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val c = vm.getById(requireArguments().getInt(ARG_ID))
        if (c == null) {
            toast(R.string.err_contact)
            parentFragmentManager.popBackStack()
            return
        }
        contact = c

        // Après rotation, les EditText se restaurent seuls : on ne les écrase pas.
        if (savedInstanceState == null) {
            photoUri = c.photoUri
            b.etNom.setText(c.nom)
            b.etPrenom.setText(c.prenom)
            b.etTel.setText(c.telephone)
            b.etEmail.setText(c.email)
            b.etLat.setText(c.latitude?.toString().orEmpty())
            b.etLon.setText(c.longitude?.toString().orEmpty())
        }
        b.imgPhoto.showContactPhoto(photoUri)

        b.btnAppeler.setOnClickListener { appeler() }
        b.btnEmail.setOnClickListener { envoyerEmail() }
        b.btnGalerie.setOnClickListener { galleryLauncher.launch("image/*") }
        b.btnCamera.setOnClickListener { prendrePhoto() }
        b.btnCarte.setOnClickListener {
            (requireActivity() as MainActivity).navigateTo(CarteContactFragment.newInstance(c.id))
        }
        b.btnSave.setOnClickListener { enregistrer() }
        b.btnCancel.setOnClickListener { parentFragmentManager.popBackStack() }
    }

    // ---------- Actions ----------

    private fun appeler() {
        val tel = b.etTel.text.toString().trim()
        if (tel.isEmpty()) return toast(R.string.err_tel_vide)
        safeStart(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$tel")))
    }

    private fun envoyerEmail() {
        val mail = b.etEmail.text.toString().trim()
        if (mail.isEmpty()) return toast(R.string.err_email_vide)
        safeStart(Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$mail")))
    }

    private fun prendrePhoto() {
        try {
            val ctx = requireContext()
            val dir = File(ctx.cacheDir, "photos").apply { mkdirs() }
            val file = File(dir, "contact_${System.currentTimeMillis()}.jpg")
            cameraUri = FileProvider.getUriForFile(ctx, "${ctx.packageName}.fileprovider", file)
            cameraLauncher.launch(cameraUri!!)
        } catch (e: ActivityNotFoundException) {
            toast(R.string.err_app_absente)
        }
    }

    private fun safeStart(intent: Intent) {
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            toast(R.string.err_app_absente)
        }
    }

    // ---------- Validation + enregistrement ----------

    private fun enregistrer() {
        val old = contact ?: return
        val nom = b.etNom.text.toString().trim()
        val prenom = b.etPrenom.text.toString().trim()
        val tel = b.etTel.text.toString().trim()
        val mail = b.etEmail.text.toString().trim()
        val latTxt = b.etLat.text.toString().trim().replace(',', '.')
        val lonTxt = b.etLon.text.toString().trim().replace(',', '.')

        var ok = true
        ok = requireNotEmpty(b.etNom, nom) && ok
        ok = requireNotEmpty(b.etPrenom, prenom) && ok
        ok = requireNotEmpty(b.etTel, tel) && ok
        if (mail.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
            b.etEmail.error = getString(R.string.err_email); ok = false
        }

        var lat: Double? = null
        var lon: Double? = null
        if (latTxt.isNotEmpty() || lonTxt.isNotEmpty()) {
            lat = latTxt.toDoubleOrNull()?.takeIf { it in -90.0..90.0 }
            lon = lonTxt.toDoubleOrNull()?.takeIf { it in -180.0..180.0 }
            if (latTxt.isEmpty() && lonTxt.isNotEmpty()) {
                b.etLat.error = getString(R.string.err_coord_paire); ok = false
            } else if (lat == null) {
                b.etLat.error = getString(R.string.err_lat); ok = false
            }
            if (lonTxt.isEmpty() && latTxt.isNotEmpty()) {
                b.etLon.error = getString(R.string.err_coord_paire); ok = false
            } else if (lon == null) {
                b.etLon.error = getString(R.string.err_lon); ok = false
            }
        }
        if (!ok) return

        vm.update(old.copy(
            nom = nom, prenom = prenom, telephone = tel, email = mail,
            photoUri = photoUri, latitude = lat, longitude = lon
        ))
        toast(R.string.msg_enregistre)
        parentFragmentManager.popBackStack()   // la liste se met à jour via le ViewModel
    }

    private fun requireNotEmpty(et: EditText, value: String): Boolean {
        if (value.isEmpty()) { et.error = getString(R.string.err_obligatoire); return false }
        return true
    }

    private fun toast(res: Int) = Toast.makeText(requireContext(), res, Toast.LENGTH_SHORT).show()

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_PHOTO, photoUri)
        outState.putString(KEY_CAM, cameraUri?.toString())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }

    companion object {
        private const val ARG_ID = "contact_id"
        private const val KEY_PHOTO = "photo"
        private const val KEY_CAM = "cam"
        fun newInstance(id: Int) = DetailContactFragment().apply {
            arguments = bundleOf(ARG_ID to id)    // on passe l'ID, pas la position
        }
    }
}
