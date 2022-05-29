package com.example.sea_battle.presentation.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import com.example.sea_battle.databinding.DialogInfoBinding

class InfoDialog(context: Context, private val text: String, private val onHide: () -> Unit = {}) : Dialog(context) {

    private lateinit var binding: DialogInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = DialogInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            buttonOk.setOnClickListener {
                dismiss()
            }
            textView.text = text
        }
    }

    override fun onStop() {
        super.onStop()
        onHide()
    }
}