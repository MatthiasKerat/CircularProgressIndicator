package com.kapps.circularprogressindicator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kapps.circularprogressindicator.ui.theme.CircularProgressIndicatorTheme
import com.kapps.circularprogressindicator.ui.theme.blueGray
import com.kapps.circularprogressindicator.ui.theme.darkGray
import com.kapps.circularprogressindicator.ui.theme.orange

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CircularProgressIndicatorTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(darkGray),
                    contentAlignment = Alignment.Center
                ){
                    CustomCircularProgressIndicator(
                        initialValue = 20,
                        primaryColor = orange,
                        secondaryColor = blueGray,
                        circleRadius = 300f,
                        onPositionChange = {
                            //Do some logic here
                            //Execute some action, update viewmodel, etc..
                        }
                    )
                }
            }
        }
    }
}