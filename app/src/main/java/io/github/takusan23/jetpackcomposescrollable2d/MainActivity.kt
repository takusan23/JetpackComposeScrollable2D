package io.github.takusan23.jetpackcomposescrollable2d

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.rememberScrollable2DState
import androidx.compose.foundation.gestures.scrollable2D
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.takusan23.jetpackcomposescrollable2d.ui.theme.JetpackComposeScrollable2DTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JetpackComposeScrollable2DTheme {
                MainScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainScreen() {

    val offset = remember { mutableStateOf(Offset.Zero) }
    val size = remember { mutableStateOf(IntSize.Zero) }

    Scaffold(
        topBar = { TopAppBar(title = { Text(text = stringResource(R.string.app_name)) }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier

                .fillMaxSize()
                .padding(paddingValues)

                .clipToBounds()
                .scrollable2D(state = rememberScrollable2DState { delta ->
                    // maxOf でサイズに収める
                    // これをしないと見えないスクロール（スクロールしても UI がなかなか反映されない）が起きる
                    val newX = (offset.value.x + delta.x).toInt().coerceIn(-size.value.width..0)
                    val newY = (offset.value.y + delta.y).toInt().coerceIn(-size.value.height..0)
                    offset.value = Offset(newX.toFloat(), newY.toFloat())
                    // TODO 今回は面倒なのでネストスクロールを考慮していません。
                    // TODO 本来は利用した分だけ return するべきです
                    delta
                })
                .layout { measurable, constraints ->
                    // ここを infinity にすると左端に寄ってくれる
                    val childConstraints = constraints.copy(
                        maxHeight = Constraints.Infinity,
                        maxWidth = Constraints.Infinity,
                    )
                    // この辺は全部 Scroll.kt のパクリ
                    val placeable = measurable.measure(childConstraints)
                    val width = placeable.width.coerceAtMost(constraints.maxWidth)
                    val height = placeable.height.coerceAtMost(constraints.maxHeight)
                    val scrollHeight = placeable.height - height
                    val scrollWidth = placeable.width - width
                    size.value = IntSize(scrollWidth, scrollHeight)
                    layout(width, height) {
                        val scrollX = offset.value.x.toInt().coerceIn(-scrollWidth..0)
                        val scrollY = offset.value.y.toInt().coerceIn(-scrollHeight..0)
                        val xOffset = scrollX
                        val yOffset = scrollY
                        withMotionFrameOfReferencePlacement {
                            placeable.placeRelativeWithLayer(xOffset, yOffset)
                        }
                    }
                }


        ) {
            for (i in 0..10) {
                Row {
                    for (j in 0..10) {
                        NumberSquare(number = i * 10 + j)
                    }
                }
            }
        }
    }
}

@Composable
private fun NumberSquare(
    modifier: Modifier = Modifier,
    number: Int
) {
    Box(
        modifier = modifier
            .border(1.dp, Color.Black)
            .size(100.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = number.toString(),
            fontSize = 20.sp
        )
    }
}