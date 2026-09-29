package com.zynpath.game

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zynpath.game.core.audio.ZynpathAudioManager
import com.zynpath.game.core.cosmetics.model.EquippedCosmetics
import com.zynpath.game.core.cosmetics.repository.CosmeticsRepository
import com.zynpath.game.core.designsystem.theme.ClassicMidnightPalette
import com.zynpath.game.core.designsystem.theme.ZynpathTheme
import com.zynpath.game.feature.navigation.ZynpathNavGraph
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var cosmeticsRepository: CosmeticsRepository

    @Inject
    lateinit var audioManager: ZynpathAudioManager

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Zynpath)
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(audioManager)
        enableEdgeToEdge()
        setContent {
            val activePalette by cosmeticsRepository.activePaletteFlow.collectAsStateWithLifecycle(initialValue = ClassicMidnightPalette)
            val equippedCosmetics by cosmeticsRepository.equippedCosmeticsFlow.collectAsStateWithLifecycle(initialValue = EquippedCosmetics.default())

            ZynpathTheme(
                palette = activePalette,
                pathEffectId = equippedCosmetics.pathEffectId
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = activePalette.backgroundDark
                ) {
                    ZynpathNavGraph()
                }
            }
        }
    }
}
