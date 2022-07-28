package com.example.sea_battle.presentation.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import android.widget.Toast
import com.example.sea_battle.R
import com.example.sea_battle.data.services.client.ClientServiceImpl
import com.example.sea_battle.databinding.DialogJoinPrivateGameBinding
import com.example.sea_battle.entities.Host
import com.example.sea_battle.navigation.Navigator
import com.example.sea_battle.presentation.choose_game.ChooseGameViewModel
import com.example.sea_battle.presentation.start_game.StartGameFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class JoinPrivateGameDialog(context: Context, val navigator: Navigator, val viewModel: ChooseGameViewModel, val host: Host) : Dialog(context) {

    private lateinit var binding: DialogJoinPrivateGameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = DialogJoinPrivateGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            buttonCancel.setOnClickListener {
                dismiss()
            }
            buttonOk.setOnClickListener {

                    if (host.password == editTextPassword.text.toString()) {
                        hide()
                        CoroutineScope(Dispatchers.IO + Job()).launch(Dispatchers.IO) {
                            if (viewModel.notifyClientJoinedGame(host)) {
                                navigator.openFragment(
                                    StartGameFragment().also { it.setHost(host) },
                                    Bundle()
                                )
                                viewModel.interrupt()
                            }
                        }
                    } else {
                        Toast.makeText(
                            context,
                            context.resources.getString(R.string.wrong_password),
                            Toast.LENGTH_LONG
                        ).show()
                    }
            }
        }
    }
}