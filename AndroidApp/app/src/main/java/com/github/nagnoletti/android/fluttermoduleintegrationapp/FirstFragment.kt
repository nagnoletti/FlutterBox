package com.github.nagnoletti.android.fluttermoduleintegrationapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class FirstFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_first, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<AppCompatButton>(R.id.button_open_flutter_fragment).setOnClickListener {
            // NOTE: Passing Bundle is required for a custom FlutterFragment to work properly with Jetpack navigation.
            // findNavController().navigate(R.id.first_to_custom_flutter, Bundle.EMPTY)

            // Passing custom bundle to let Flutter handle back presses automatically
            findNavController().navigate(
                R.id.first_to_custom_flutter,
                CustomFlutterFragment.bundle()
            )
        }
    }
}