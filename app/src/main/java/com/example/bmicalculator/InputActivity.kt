package com.example.bmicalculator

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.pow

class InputActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val startHeight = intent.getIntExtra("HEIGHT", 170)
        val startWeight = intent.getIntExtra("WEIGHT", 65)
        val startAge = intent.getIntExtra("AGE", 27)
        val startGender = intent.getStringExtra("GENDER")

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Black
                ) {
                    InputScreen(
                        startHeight,
                        startWeight,
                        startAge,
                        startGender,
                        onBack = { h, w, a, g ->
                            val i = Intent(this, WelcomeActivity::class.java)
                            i.putExtra("HEIGHT", h)
                            i.putExtra("WEIGHT", w)
                            i.putExtra("AGE", a)
                            i.putExtra("GENDER", g)
                            startActivity(i)
                            finish()
                        },
                        onCalculate = { h, w, a, g ->
                            val bmi = w / (h / 100.0).pow(2)
                            val i = Intent(this, ResultActivity::class.java)
                            i.putExtra("BMI_VALUE", bmi)
                            i.putExtra("HEIGHT", h)
                            i.putExtra("WEIGHT", w)
                            i.putExtra("AGE", a)
                            i.putExtra("GENDER", g)
                            startActivity(i)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun InputScreen(
    startHeight: Int,
    startWeight: Int,
    startAge: Int,
    startGender: String?,
    onBack: (Int, Int, Int, String?) -> Unit,
    onCalculate: (Int, Int, Int, String?) -> Unit
) {
    var selectedGender by remember { mutableStateOf(startGender) }

    var heightText by remember { mutableStateOf(startHeight.toString()) }
    var weightText by remember { mutableStateOf(startWeight.toString()) }
    var ageText by remember { mutableStateOf(startAge.toString()) }

    val height = heightText.toIntOrNull()?.coerceIn(100, 250) ?: startHeight
    val weight = weightText.toIntOrNull()?.coerceIn(1, 200) ?: startWeight
    val age = ageText.toIntOrNull()?.coerceIn(1, 100) ?: startAge

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 76.dp) // space for bottom button
        ) {

            IconButton(onClick = { onBack(height, weight, age, selectedGender) }) {
                Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.White)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                GenderCard(
                    modifier = Modifier.weight(1f),
                    text = "MALE",
                    icon = Icons.Filled.Person,
                    selected = selectedGender == "MALE"
                ) { selectedGender = "MALE" }

                GenderCard(
                    modifier = Modifier.weight(1f),
                    text = "FEMALE",
                    icon = Icons.Outlined.Person,
                    selected = selectedGender == "FEMALE"
                ) { selectedGender = "FEMALE" }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1C1C))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Height", color = Color.White)

                    BasicTextField(
                        value = heightText,
                        onValueChange = { input ->
                            if (input.isEmpty()) heightText = ""
                            else if (input.all { it.isDigit() } && input.length <= 3 && input.toInt() <= 250)
                                heightText = input
                        },
                        modifier = Modifier.width(90.dp),
                        textStyle = TextStyle(
                            color = Color.White,
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    )

                    Slider(
                        value = height.toFloat(),
                        onValueChange = { heightText = it.toInt().toString() },
                        valueRange = 100f..250f,
                        colors = SliderDefaults.colors(
                            activeTrackColor = Color(0xFF7CFC00),
                            thumbColor = Color(0xFF7CFC00)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                CounterCard(
                    modifier = Modifier.weight(1f),
                    title = "Weight",
                    valueText = weightText,
                    unit = "kg",
                    max = 200,
                    onChange = { weightText = it }
                )

                CounterCard(
                    modifier = Modifier.weight(1f),
                    title = "Age",
                    valueText = ageText,
                    unit = "year",
                    max = 100,
                    onChange = { ageText = it }
                )
            }
        }

        // ✅ CALCULATE FIXED TO BOTTOM
        Button(
            onClick = { onCalculate(height, weight, age, selectedGender) },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(56.dp)
                .padding(bottom = 8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF7CFC00)
            )
        ) {
            Text("CALCULATE", color = Color.Black, fontWeight = FontWeight.Bold)
        }
    }
}

/* ---------- Gender Card ---------- */

@Composable
fun GenderCard(
    modifier: Modifier,
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(120.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) Color(0xFF7CFC00) else Color(0xFF1C1C1C)
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                icon,
                contentDescription = text,
                tint = if (selected) Color.Black else Color.White,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = text,
                fontWeight = FontWeight.Bold,
                color = if (selected) Color.Black else Color.White
            )
        }
    }
}

/* ---------- Counter Card (ONLY COLOR CHANGED) ---------- */

@Composable
fun CounterCard(
    modifier: Modifier,
    title: String,
    valueText: String,
    unit: String,
    max: Int,
    onChange: (String) -> Unit
) {
    val value = valueText.toIntOrNull() ?: 0

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1C1C))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, color = Color.White)

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Button(
                    onClick = { onChange((value - 1).coerceAtLeast(1).toString()) },
                    modifier = Modifier.size(42.dp),
                    contentPadding = PaddingValues(0.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF7CFC00)
                    )
                ) {
                    Text("-", color = Color.Black, fontWeight = FontWeight.Bold)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    BasicTextField(
                        value = valueText,
                        onValueChange = { input ->
                            if (input.isEmpty()) {
                                onChange("")
                                return@BasicTextField
                            }
                            if (!input.all { it.isDigit() }) return@BasicTextField
                            if (input.length > 3) return@BasicTextField
                            if (input.toInt() <= max) onChange(input)
                        },
                        modifier = Modifier.width(48.dp),
                        textStyle = TextStyle(
                            color = Color.White,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    )
                    Text(unit, color = Color.Gray)
                }

                Button(
                    onClick = { onChange((value + 1).coerceAtMost(max).toString()) },
                    modifier = Modifier.size(42.dp),
                    contentPadding = PaddingValues(0.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF7CFC00)
                    )
                ) {
                    Text("+", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}


