package com.example.imagien

import android.media.MediaCodec
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.OptIn
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.dash.DashMediaSource
import com.example.imagien.ui.theme.ImagienTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ImagienTheme {
                val videoUris = listOf(
                    Uri.parse("https://dash.akamaized.net/digitalprimates/fraunhofer/480p_video/heaac_2_0_with_video/Sintel/sintel_480p_heaac2_0.mpd"),
                    Uri.parse("https://dash.akamaized.net/dash264/TestCases/2c/qualcomm/1/MultiResMPEG2.mpd"),
                    Uri.parse("https://bitmovin-a.akamaihd.net/content/MI201109210084_1/mpds/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.mpd")
                )
                ResizableRow(videoUris)
            }
        }
    }

}

@OptIn(UnstableApi::class)
@Composable
fun ResizableRow(videoUris: List<Uri>) {

    //Initial videos weights
    var weight1 by remember { mutableFloatStateOf(0.7f) }
    var weight2 by remember { mutableFloatStateOf(0.2f) }
    var weight3 by remember { mutableFloatStateOf(0.1f) }

    val maxWeight = 0.8f
    val minWeight = 0.1f

    // Animate the weights
    val animatedWeight1 by animateFloatAsState(targetValue = weight1, label = "")
    val animatedWeight2 by animateFloatAsState(targetValue = weight2, label = "")
    val animatedWeight3 by animateFloatAsState(targetValue = weight3, label = "")

    val context = LocalContext.current
    val exoPlayer1 = remember(context) {
        ExoPlayer.Builder(context)
            .setVideoScalingMode(MediaCodec.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING).build()
    }

    val exoPlayer2 = remember(context) {
        ExoPlayer.Builder(context)
            .setVideoScalingMode(MediaCodec.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING).build()
    }

    val exoPlayer3 = remember(context) {
        ExoPlayer.Builder(context)
            .setVideoScalingMode(MediaCodec.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING).build()
    }

    // Dispose of the players when the composable is disposed
    DisposableEffect(context) {
        onDispose {
            exoPlayer1.release()
            exoPlayer2.release()
            exoPlayer3.release()
        }
    }

    Row(
        Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(Color.White)
    ) {

        // Player view 1
        ResizablePlayer(modifier = Modifier
            .weight(animatedWeight1)
            .fillMaxHeight(), player = exoPlayer1, onDrag = { delta ->
            weight1 = (weight1 + delta).coerceIn(minWeight, maxWeight)
            weight2 = (weight2 - delta / 2).coerceIn(minWeight, maxWeight)
            weight3 = (weight3 - delta / 2).coerceIn(minWeight, maxWeight)
        }, onClick = {
            weight1 = maxWeight
            weight2 = minWeight
            weight3 = minWeight
        })

        // Player view 2
        ResizablePlayer(modifier = Modifier
            .weight(animatedWeight2)
            .fillMaxHeight()
            .padding(start = 6.dp, end = 6.dp), player = exoPlayer2,
            onDrag = { delta ->
                weight2 = (weight2 + delta).coerceIn(minWeight, maxWeight)
                weight1 = (weight1 - delta / 2).coerceIn(minWeight, maxWeight)
                weight3 = (weight3 - delta / 2).coerceIn(minWeight, maxWeight)
            }, onClick = {
                weight2 = maxWeight
                weight1 = minWeight
                weight3 = minWeight
            })

        // Player view 3
        ResizablePlayer(modifier = Modifier
            .weight(animatedWeight3)
            .fillMaxHeight(), player = exoPlayer3, onDrag = { delta ->
            weight3 = (weight3 - delta).coerceIn(minWeight, maxWeight)
            weight1 = (weight1 + delta / 2).coerceIn(minWeight, maxWeight)
            weight2 = (weight2 + delta / 2).coerceIn(minWeight, maxWeight)
        }, onClick = {
            weight3 = maxWeight
            weight1 = minWeight
            weight2 = minWeight
        })
    }

    LaunchedEffect(videoUris) {
        val dataSourceFactory = DefaultHttpDataSource.Factory()
        val mediaItem1 = MediaItem.fromUri(videoUris[0])
        val mediaItem2 = MediaItem.fromUri(videoUris[1])
        val mediaItem3 = MediaItem.fromUri(videoUris[2])

        val mediaSource1 = DashMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem1)
        val mediaSource2 = DashMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem2)
        val mediaSource3 = DashMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem3)

        exoPlayer1.setMediaSource(mediaSource1)
        exoPlayer2.setMediaSource(mediaSource2)
        exoPlayer3.setMediaSource(mediaSource3)

        exoPlayer1.prepare()
        exoPlayer2.prepare()
        exoPlayer3.prepare()

        exoPlayer1.playWhenReady = true
        exoPlayer2.playWhenReady = true
        exoPlayer3.playWhenReady = true
    }
}





