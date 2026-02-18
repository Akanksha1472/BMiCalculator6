package com.example.bmicalculator

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite

class WelcomeActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ RECEIVE ALL SAVED VALUES (INCLUDING GENDER)
        val height = intent.getIntExtra("HEIGHT", 170)
        val weight = intent.getIntExtra("WEIGHT", 65)
        val age = intent.getIntExtra("AGE", 27)
        val gender = intent.getStringExtra("GENDER")

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Black
                ) {
                    WelcomeScreen {
                        val i = Intent(this, InputActivity::class.java)
                        i.putExtra("HEIGHT", height)
                        i.putExtra("WEIGHT", weight)
                        i.putExtra("AGE", age)
                        i.putExtra("GENDER", gender) // ✅ THIS WAS MISSING
                        startActivity(i)
                    }
                }
            }
        }
    }
}

@Composable
fun WelcomeScreen(onStartClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {

        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
                tint = Color(0xFF7CFC00),
                modifier = Modifier.size(64.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "BMI Calculator",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Button(
            onClick = onStartClick,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(24.dp)
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF7CFC00)
            )
        ) {
            Text(
                text = "START",
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
        }
    }
}



