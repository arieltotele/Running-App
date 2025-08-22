package com.example.running_app.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.running_app.R
import com.example.running_app.databinding.FragmentSetupBinding
import com.example.running_app.ui.MainActivity
import com.example.running_app.ui.viewmodels.UserProfileViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SetupFragment : Fragment() {
    private var _binding: FragmentSetupBinding? = null
    private val binding get() = _binding!!

    private val viewModel: UserProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSetupBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.userProfile.observe(viewLifecycleOwner){
            if(!it.isFirstAppOpen){
                val navOptions = NavOptions.Builder()
                    .setPopUpTo(R.id.setupFragment, true)
                    .build()
                val action = SetupFragmentDirections.toRunFragment()
                findNavController().navigate(action, navOptions)
            }
        }

        binding.tvContinue.setOnClickListener {
            if (checkFieldsForEmptyValues()){
                Snackbar.make(requireView(), "All fields must be filled",
                    Snackbar.LENGTH_SHORT).show()
            }else{
                val name = binding.tietName.text.toString()
                val weight = binding.etWeight.text.toString().toFloat()
                viewModel.saveUserProfile(name, weight)
                viewModel.setAppOpened()
            }

        }
    }

    private fun checkFieldsForEmptyValues(): Boolean {
        val name = binding.tietName.text.toString()
        val weight = binding.etWeight.text.toString()

        return (name.isEmpty() || weight.isEmpty())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
