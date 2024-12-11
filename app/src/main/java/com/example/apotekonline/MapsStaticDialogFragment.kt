package com.example.apotekonline

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide

class MapsStaticDialogFragment : DialogFragment() {

    private lateinit var staticMapImageView: ImageView
    private lateinit var addressEditText: EditText
    private lateinit var confirmButton: Button

    private var onAddressSelectedListener: ((String) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.dialog_maps_static, container, false)

        staticMapImageView = view.findViewById(R.id.staticMapImageView)
        addressEditText = view.findViewById(R.id.addressEditText)
        confirmButton = view.findViewById(R.id.confirmButton)

        // Tampilkan peta statis (menggunakan Mapbox Static Images API atau lainnya)
        val staticMapUrl =
            "https://maps.googleapis.com/maps/api/staticmap?center=-6.2088,106.8456&zoom=12&size=600x300&markers=-6.2088,106.8456"

        Glide.with(this)
            .load(staticMapUrl)
            .into(staticMapImageView)

        confirmButton.setOnClickListener {
            val address = addressEditText.text.toString()
            if (address.isNotEmpty()) {
                onAddressSelectedListener?.invoke(address)
                dismiss()
            }
        }

        return view
    }

    fun setOnAddressSelectedListener(listener: (String) -> Unit) {
        onAddressSelectedListener = listener
    }
}
