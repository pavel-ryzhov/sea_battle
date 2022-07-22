package com.example.sea_battle.presentation.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import com.example.sea_battle.MainActivity
import com.example.sea_battle.databinding.DialogConfirmActionBinding

class ConfirmActionDialog(
    private val contextActivity: Context,
    private val text: String,
    private val onConfirmed: () -> Unit = {},
    private val onCanceled: () -> Unit = {},
    private val onNothingChosen: () -> Unit = {}
) : Dialog(contextActivity) {
    private lateinit var binding: DialogConfirmActionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = DialogConfirmActionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            textView.text = text
            buttonOk.setOnClickListener {
                dismiss()
                onConfirmed()
            }
            buttonCancel.setOnClickListener {
                dismiss()
                onCanceled()
            }
        }
    }

    override fun onStop() {
        onNothingChosen()
        (contextActivity as MainActivity).hideSystemUI()
        super.onStop()
    }
}