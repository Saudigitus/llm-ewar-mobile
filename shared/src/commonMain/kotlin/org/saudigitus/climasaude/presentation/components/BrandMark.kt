package org.saudigitus.climasaude.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.saudigitus.climasaude.presentation.theme.Sun
import org.saudigitus.climasaude.presentation.theme.Teal
import org.saudigitus.climasaude.presentation.theme.TealDark
import org.saudigitus.climasaude.presentation.theme.TealLight

/**
 * Clima Saúde Community logo: a rain drop with a health cross, the sun, and two arms of the
 * community around it. The paths come from scripts/generate-app-icon.py, which also builds the
 * launcher icons. [inverse] draws a white tile for use on teal backgrounds.
 */
@Composable
fun BrandMark(inverse: Boolean = false, size: Dp = 52.dp) {
    val tile = if (inverse) Brush.linearGradient(listOf(Color.White, Color.White))
    else Brush.linearGradient(listOf(TealDark, TealLight))
    val mark = if (inverse) Teal else Color.White

    Canvas(Modifier.size(size).clip(RoundedCornerShape(size * 0.28f))) {
        drawRect(tile)
        // The artwork lives on a 108-unit canvas; the visible part is the central 72 units.
        scale(this.size.width / VisibleSize, pivot = Offset.Zero) {
            translate(-VisibleOffset, -VisibleOffset) {
                drawPath(SunPath, Sun)
                drawPath(DropPath, mark)
                ArcPaths.forEach {
                    drawPath(it, mark, style = Stroke(width = ArcWidth, cap = StrokeCap.Round))
                }
            }
        }
    }
}

private const val VisibleSize = 72f
private const val VisibleOffset = 18f
private const val ArcWidth = 5f

private fun path(data: String): Path = PathParser().parsePathString(data).toPath()

private val SunPath = path("M59,36 A9,9 0 1,1 77,36 A9,9 0 1,1 59,36 Z")

private val DropPath = path(
    "M54,24 L66.99,46.5 A15,15 0 1,1 41.01,46.5 Z " +
        "M51,48 L57,48 L57,53 L62,53 L62,59 L57,59 L57,64 L51,64 L51,59 L46,59 L46,53 L51,53 Z"
).apply { fillType = PathFillType.EvenOdd }

private val ArcPaths = listOf(
    path("M73.94,63.3 A22,22 0 0,1 57.82,75.67"),
    path("M50.18,75.67 A22,22 0 0,1 34.06,63.3")
)
