package com.bp.dinodata

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHost
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.createGraph
import androidx.navigation.navArgument
import com.bp.dinodata.presentation.DetailGenusScreen
import com.bp.dinodata.presentation.ListGenusScreen
import com.bp.dinodata.presentation.MyNavigation
import com.bp.dinodata.presentation.Screens
import com.bp.dinodata.theme.DinoDataTheme
import com.bp.dinodata.use_cases.TextToSpeechUseCases
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity: AppCompatActivity() {

    @Inject
    lateinit var textToSpeechUseCases: TextToSpeechUseCases

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DinoDataTheme {
                MyNavigation()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        textToSpeechUseCases.close()
    }
}