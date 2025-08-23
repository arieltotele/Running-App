package com.example.running_app.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.running_app.R
import com.example.running_app.databinding.FragmentSettingsBinding
import com.example.running_app.ui.viewmodel.UserProfileViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: UserProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.userProfile.observe(viewLifecycleOwner, Observer { userProfile ->
            if (userProfile != null) {
                binding.tietName.setText(userProfile.name)
                binding.tietWeight.setText(userProfile.weight.toString())
            }
        })

        binding.btnApplyChanges.setOnClickListener {
            val name = binding.tietName.text.toString()
            val weight = binding.tietWeight.text.toString().toFloatOrNull()

            if (name.isNotEmpty() && weight != null) {
                viewModel.saveUserProfile(name, weight)
                Snackbar.make(requireView(), "Changes saved successfully",
                    Snackbar.LENGTH_SHORT).show()
            }else{
                Snackbar.make(requireView(), "All fields must be filled",
                    Snackbar.LENGTH_SHORT).show()
            }
        }
    }
}