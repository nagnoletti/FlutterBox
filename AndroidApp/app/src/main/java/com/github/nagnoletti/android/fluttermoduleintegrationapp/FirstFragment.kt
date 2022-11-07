package com.github.nagnoletti.android.fluttermoduleintegrationapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.github.nagnoletti.android.fluttermoduleintegrationapp.R

class FirstFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_first, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val openFlutterFragmentButton =
            view.findViewById<AppCompatButton>(R.id.button_open_flutter_fragment)
        openFlutterFragmentButton.setOnClickListener {
            // NOTE: navigating to a FlutterFragment requires nav arguments (Bundle.EMPTY)
            // otherwise the SDK fails retrieving arguments from fragment's getArguments().
            // findNavController().navigate(R.id.first_to_custom_flutter, Bundle.EMPTY)
            findNavController().navigate(
                R.id.first_to_custom_flutter,
                CustomFlutterFragment.bundle()
            )
        }
    }
}