package com.motionsyncx

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.motionsyncx.motion_sync.MotionSyncX
import com.motionsyncx.motion_sync.utils.MotionSyncAlignment
import com.motionsyncx.motion_sync.utils.MotionSyncLayout
import com.motionsyncx.ui.theme.MotionSyncXTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MotionSyncXTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MaterialXMotionSyncDemo()
                }
            }
        }
    }
}

@Composable
private fun MaterialXMotionSyncDemo() {
    val shapesList: List<@Composable () -> Unit> = listOf(
        {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(colorResource(id = R.color.purple_500))
            )
        },
        {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(colorResource(id = R.color.purple_700))
            )
        },
        {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(colorResource(id = R.color.purple_500))
            )
        },
    )

    MotionSyncX(
        composableList = shapesList,
        size = 80.dp,
        layoutDirection = MotionSyncLayout.COLUMN,
        alignmentOption = MotionSyncAlignment.CENTER,
        spacing = 20.dp,
        speedMultiplier = 1f
    )
}

@Preview(showBackground = true)
@Composable
private fun MaterialXMotionSyncPreview() {
    MotionSyncXTheme {
        MaterialXMotionSyncDemo()
    }
}