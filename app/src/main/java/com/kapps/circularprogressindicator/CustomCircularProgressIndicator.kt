package com.kapps.circularprogressindicator

import android.animation.PropertyValuesHolder
import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kapps.circularprogressindicator.ui.theme.*
import kotlin.math.*

@Composable
fun CustomCircularProgressIndicator(
    modifier: Modifier = Modifier,
    initialValue:Int,
    primaryColor: Color,
    secondaryColor: Color,
    minValue: Int = 0,
    maxValue: Int = 100,
    circleRadius:Float,
    onPositionChange:(Int)->Unit
) {

    var circleCenter by remember {
        mutableStateOf(Offset.Zero)
    }

    var changeAngle by remember {
        mutableStateOf(0f)
    }

    var dragStartedAngle by remember {
        mutableStateOf(0f)
    }

    var oldCenterValue by remember {
        mutableStateOf(initialValue)
    }

    var positionValue by remember {
        mutableStateOf(initialValue)
    }

    Box(
        modifier = modifier
    ){
        oldCenterValue = initialValue
        positionValue = initialValue
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(true) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            dragStartedAngle = -atan2(
                                x = circleCenter.y - offset.y,
                                y = circleCenter.x - offset.x
                            ) * (180f / PI).toFloat()
                            dragStartedAngle = (dragStartedAngle + 180f).mod(360f)
                        },
                        onDragEnd = {
                            oldCenterValue = positionValue
                            onPositionChange(positionValue)
                        }
                    ) { change, _ ->
                        var touchAngle = -atan2(
                            x = circleCenter.y - change.position.y,
                            y = circleCenter.x - change.position.x
                        ) * (180f / PI).toFloat()
                        touchAngle = (touchAngle + 180f).mod(360f)

                        changeAngle = touchAngle - oldCenterValue * 360f / maxValue

                        if (dragStartedAngle in
                            (oldCenterValue.toFloat() * 360 / maxValue - (360 / maxValue * 5))
                            ..
                            (oldCenterValue.toFloat() * 360 / maxValue + (360 / maxValue * 5))
                        ) {
                            val dragOverlapFrom100 = (positionValue == maxValue &&
                                    (oldCenterValue + (changeAngle / (360f / maxValue.toFloat()))).roundToInt() in (minValue..maxValue - 5))
                            val dragOverLapFrom0 = (positionValue == minValue &&
                                    (oldCenterValue + (changeAngle / (360f / maxValue.toFloat()))).roundToInt() in (minValue + 5..maxValue))

                            if (!dragOverLapFrom0 && !dragOverlapFrom100) {
                                positionValue =
                                    (oldCenterValue + (changeAngle / (360f / maxValue.toFloat()))).roundToInt()
                            }
                        }

                    }
                }
        ){

            val width = size.width
            val height = size.height
            val circleThickness = width/30f
            circleCenter = Offset(x = width/2f, y = height/2f)

            drawCircle(
                brush = Brush.radialGradient(
                    listOf(
                        primaryColor.copy(0.45f),
                        secondaryColor.copy(0.15f)
                    )
                ),
                radius = circleRadius,
                center = circleCenter
            )
            drawCircle(
                style = Stroke(
                    width = circleThickness
                ),
                color = secondaryColor,
                radius = circleRadius,
                center = circleCenter
            )

            drawArc(
                color = primaryColor,
                startAngle = 90f,
                sweepAngle = (360f/maxValue) * positionValue.toFloat(),
                style = Stroke(
                    width = circleThickness,
                    cap = StrokeCap.Round
                ),
                useCenter = false,
                size = Size(
                    width = circleRadius * 2f,
                    height = circleRadius * 2f
                ),
                topLeft = Offset(
                    (size.width - circleRadius * 2f)/2f,
                    (size.height - circleRadius * 2f)/2f
                )
            )

            val outerRadius = circleRadius + circleThickness / 2f
            val gap = 15f
            for(i in minValue .. maxValue){
                val color = if(i < positionValue) primaryColor else primaryColor.copy(alpha = 0.3f)
                val angleInDegrees = i * 360f/maxValue.toFloat()
                val angleInRad = angleInDegrees * PI /180f + PI.toFloat()/2f

                val yGapAdjustment = cos(angleInDegrees * PI.toFloat()/180f) * gap
                val xGapAdjustment = -sin(angleInDegrees * PI.toFloat()/180f) * gap

                val start = Offset (
                    x = outerRadius * cos(angleInRad).toFloat()+circleCenter.x + xGapAdjustment,
                    y = outerRadius * sin(angleInRad).toFloat()+circleCenter.y + yGapAdjustment
                )
                val end = Offset(
                    x = outerRadius * cos(angleInRad).toFloat() + circleCenter.x + xGapAdjustment,
                    y = outerRadius * sin(angleInRad).toFloat() + circleThickness + circleCenter.y + yGapAdjustment
                )
                rotate(
                    degrees = angleInDegrees,
                    pivot = start
                ){
                    drawLine(
                        color = color,
                        start = start,
                        end = end,
                        strokeWidth = 1.dp.toPx(),
                    )
                }

            }

            drawContext.canvas.nativeCanvas.apply {
                drawIntoCanvas {
                    drawText(
                        "$positionValue %",
                        circleCenter.x,
                        circleCenter.y + 45.dp.toPx()/3f,
                        Paint().apply {
                            textSize = 38.dp.toPx()
                            textAlign = Paint.Align.CENTER
                            color = white.toArgb()
                            isFakeBoldText = true
                        }

                    )
                }
            }
        }
    }



}

@Preview(showBackground = true)
@Composable
fun CircularPositionComponentPreview() {
    CircularProgressIndicatorTheme{
        CustomCircularProgressIndicator(
            modifier = Modifier
                .size(250.dp)
                .background(darkGray),
            initialValue  = 67,
            primaryColor = orange,
            secondaryColor = blueGray,
            circleRadius = 230f,
            onPositionChange = {}
        )
    }
}