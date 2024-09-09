package com.example.imagien

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView

/**
 * A Composable View that displays a resizable video player using ExoPlayer.
 *
 * @param player The ExoPlayer instance to use for playback.
 * @param onDrag A lambda function that is called when the player is dragged horizontally.
 * The function receives the drag amount as a Float value.
 * @param onClick called when the player is clicked.
 */
@UnstableApi
@Composable
fun ResizablePlayer(
    player: ExoPlayer,
    modifier: Modifier = Modifier,
    onDrag: (Float) -> Unit,
    onClick: () -> Unit
) {

    var genericError by remember { mutableStateOf(false) }

    // Listen for player events
    LaunchedEffect(player) {
        player.addListener(object : Player.Listener {

            // There is a possibility to handle different errors, but I tried to keep it sample
            override fun onPlayerError(error: PlaybackException) {
                genericError = true
            }

            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                if (playbackState == Player.STATE_READY) {
                    genericError = false
                }
            }
        })
    }

    AndroidView(
        modifier = modifier
            .pointerInput(Unit) {
                detectHorizontalDragGestures { change, dragAmount ->
                    change.consume()
                    onDrag(dragAmount / size.width)
                }
            }
            .clickable {
                onClick()
            },
        factory = { context ->
            PlayerView(context).apply {
                this.player = player
                player.repeatMode = Player.REPEAT_MODE_ALL
                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
                useController = false
            }
        }
    )

    // Show error message if there is an error
    if (genericError) {
        Text(
            modifier = Modifier.fillMaxSize(),
            text = "Error loading video, \nplease check your internet connection!",
            textAlign = TextAlign.Center,
            color = Color.Red
        )
    }
}