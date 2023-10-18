package me.dio.copa.catar.features

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import dagger.hilt.android.AndroidEntryPoint
import me.dio.copa.catar.extensions.observe
import me.dio.copa.catar.notification.scheduler.extensions.NotificationMatcherWorker
import me.dio.copa.catar.ui.theme.Copa2022Theme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observeActions()
        setContent {
            Copa2022Theme {
                val state by viewModel.state.collectAsState()
                MainScreen(matches = state.matches, viewModel::toggleNotification)
            }
        }
    }

    private fun observeActions() {
        viewModel.action.observe(this) { action ->
            when (action) {
                is MainUiAction.MatchesNotFound -> showErrorDialog("Nenhuma correspondência encontrada.")
                MainUiAction.Unexpected -> {
                    logError("Ocorreu um erro inesperado.")
                    showErrorDialog("Ocorreu um erro inesperado. Tente novamente mais tarde.")
                }
                is MainUiAction.DisableNotification ->
                    NotificationMatcherWorker.cancel(applicationContext, action.match)
                is MainUiAction.EnableNotification ->
                    NotificationMatcherWorker.start(applicationContext, action.match)
            }
        }
    }

    private fun showErrorDialog(s: String) {

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Erro")
        builder.setMessage(s)
        builder.setPositiveButton("OK") { _, _ -> }
        val dialog = builder.create()
        dialog.show()
            
    }

    private fun logError(errorMessage: String) {

        Log.e("AppError", errorMessage)
    }


}
