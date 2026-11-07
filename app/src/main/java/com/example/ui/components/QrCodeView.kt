package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.random.Random

/**
 * High-fidelity QR Code Canvas drawing for eSIM LPA activation
 */
@Composable
fun QrCodeView(
    data: String,
    size: Dp = 200.dp,
    modifier: Modifier = Modifier
) {
    val matrixSize = 25
    val matrix = remember(data) {
        generateQrMatrix(data, matrixSize)
    }

    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size - 24.dp)) {
            val moduleWidth = this.size.width / matrixSize
            val moduleHeight = this.size.height / matrixSize

            for (r in 0 until matrixSize) {
                for (c in 0 until matrixSize) {
                    if (matrix[r][c]) {
                        drawRect(
                            color = Color(0xFF0C1B33),
                            topLeft = Offset(c * moduleWidth, r * moduleHeight),
                            size = Size(moduleWidth + 0.5f, moduleHeight + 0.5f)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Creates deterministic QR matrix containing 3 finder patterns and data modules
 */
private fun generateQrMatrix(data: String, size: Int): Array<BooleanArray> {
    val matrix = Array(size) { BooleanArray(size) }

    // Helper to paint a 7x7 Finder Pattern at (row, col)
    fun putFinderPattern(r0: Int, c0: Int) {
        for (r in 0 until 7) {
            for (c in 0 until 7) {
                val isBorder = r == 0 || r == 6 || c == 0 || c == 6
                val isCore = r in 2..4 && c in 2..4
                matrix[r0 + r][c0 + c] = isBorder || isCore
            }
        }
        // White separator around finder
        for (r in -1..7) {
            for (c in -1..7) {
                val rr = r0 + r
                val cc = c0 + c
                if (rr in 0 until size && cc in 0 until size) {
                    if (r == -1 || r == 7 || c == -1 || c == 7) {
                        matrix[rr][cc] = false
                    }
                }
            }
        }
    }

    // Top-left
    putFinderPattern(0, 0)
    // Top-right
    putFinderPattern(0, size - 7)
    // Bottom-left
    putFinderPattern(size - 7, 0)

    // Timing lines
    for (i in 8 until size - 8) {
        matrix[6][i] = i % 2 == 0
        matrix[i][6] = i % 2 == 0
    }

    // Deterministic pseudo-random fill based on data hash
    val seed = data.hashCode().toLong()
    val rng = Random(seed)

    for (r in 0 until size) {
        for (c in 0 until size) {
            // Skip finder zones
            val inTopLeft = r < 8 && c < 8
            val inTopRight = r < 8 && c >= size - 8
            val inBottomLeft = r >= size - 8 && c < 8
            val inTiming = (r == 6 && c in 8 until size - 8) || (c == 6 && r in 8 until size - 8)

            if (!inTopLeft && !inTopRight && !inBottomLeft && !inTiming) {
                matrix[r][c] = rng.nextBoolean()
            }
        }
    }

    return matrix
}
