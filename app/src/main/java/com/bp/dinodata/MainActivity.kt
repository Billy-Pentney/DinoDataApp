package com.bp.dinodata

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.bp.dinodata.presentation.MyNavigation
import com.bp.dinodata.theme.DinoDataTheme
import com.bp.dinodata.use_cases.AudioPronunciationUseCases
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity: AppCompatActivity() {

    @Inject
    lateinit var textToSpeechUseCases: AudioPronunciationUseCases

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