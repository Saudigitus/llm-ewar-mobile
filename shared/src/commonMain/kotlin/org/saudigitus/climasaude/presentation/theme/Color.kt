package org.saudigitus.climasaude.presentation.theme

import androidx.compose.ui.graphics.Color
import org.saudigitus.climasaude.domain.model.RiskLevel

internal val Ink = Color(0xFF173D3B)
internal val Teal = Color(0xFF087C70)
internal val TealDark = Color(0xFF0D5B58)
internal val TealLight = Color(0xFF138E7B)
internal val Sun = Color(0xFFF4B942)
internal val OnTealMuted = Color(0xFFDCF2EA)
internal val TealSelection = Color(0xFFE3F2ED)
internal val Canvas = Color(0xFFF3F7F4)
internal val Muted = Color(0xFF5C716D)
internal val Outline = Color(0xFFCFDDD8)
internal val Danger = Color(0xFF9F3D32)
internal val DangerContainer = Color(0xFFFBEAE7)

fun riskColor(level: RiskLevel): Color = when (level) {
    RiskLevel.GREEN -> Color(0xFF237850)
    RiskLevel.YELLOW -> Color(0xFF986B12)
    RiskLevel.RED -> Color(0xFFBA4035)
}
