package com.example.myapplication

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.myapplication.databinding.FragmentFirstBinding

import android.health.connect.datatypes.units.Length

import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem

import android.widget.EditText
import android.widget.TextView

import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.databinding.ContentMainBinding

import java.io.File
import java.io.IOException
import java.io.InputStream



/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {


    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val engButton = view.findViewById<Button>(R.id.engButton)
        val frButton = view.findViewById<Button>(R.id.frButton)
        var selectedButton: Button? = null

        engButton.setOnClickListener {
            if (selectedButton != engButton) {
                selectedButton?.isSelected = false
                engButton.isSelected = true
                selectedButton = engButton
                (activity as MainActivity).setIsButtonPressed(true)
                (activity as MainActivity).generateRandomEnglishWord()
                (activity as MainActivity).clearRecyclerViewData()
            }
        }

        frButton.setOnClickListener {
            if (selectedButton != frButton) {
                selectedButton?.isSelected = false
                frButton.isSelected = true
                selectedButton = frButton
                (activity as MainActivity).setIsButtonPressed(true)
                (activity as MainActivity).generateRandomFrenchWord()
                (activity as MainActivity).clearRecyclerViewData()
            }
        }

//        binding.buttonFirst.setOnClickListener {
//            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
//
//            verifyWordValidity()
//        }
//
//
//
//        binding.inputButton.setOnClickListener {
//           // Toast.makeText(getActivity(),"Text!",Toast.LENGTH_SHORT).show()
//
//            binding.testerText.text = binding.textInput.text
//            verifyWordValidity()
//        }

    }

    fun verifyWordValidity() {
        val mainActivity = activity as? MainActivity

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}