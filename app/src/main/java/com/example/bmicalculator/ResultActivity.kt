package com.example.bmicalculator

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class ResultActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bmi = intent.getDoubleExtra("BMI_VALUE", 0.0)

        // Sequence diagram step: task (BMI calculation) is complete →
        // send to RewardsManager which calculates and persists the reward,
        // then fires a notification to the kid (and optionally the parent).
        // Guard with savedInstanceState == null so the reward is only created
        // once per actual BMI submission, not again on screen rotations.
        val (category, _) = getBmiResult(bmi)
        val notificationHelper = NotificationHelper(this)
        val rewardsManager = RewardsManager(
            repository = LocalRewardsRepository(this),
            notificationHelper = notificationHelper
        )
        if (savedInstanceState == null) {
            rewardsManager.onTaskCompleted(
                bmiCategory = category,
                onParentNotify = { reward ->
                    notificationHelper.sendParentNotification(reward)
                }
            )
        }

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Black
                ) {
                    ResultScreen(
                        bmi = bmi,
                        onRecalculate = {
                            finish()
                        },
                        onViewRewards = {
                            startActivity(Intent(this, RewardsActivity::class.java))
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ResultScreen(
    bmi: Double,
    onRecalculate: () -> Unit,
    onViewRewards: () -> Unit = {}
) {
    val (category, advice) = getBmiResult(bmi)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {

        // Scrollable content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 128.dp) // space for fixed buttons
        ) {

            Text(
                text = "Result",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1C1C))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Your BMI is", color = Color.Gray)

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = String.format("%.1f", bmi),
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = category,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF7CFC00)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = category,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Diet and Nutrition",
                fontWeight = FontWeight.Bold,
                color = Color(0xFF7CFC00)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = advice,
                color = Color.LightGray,
                lineHeight = 20.sp
            )
        }

        // Fixed bottom buttons
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            Button(
                onClick = onViewRewards,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1C1C1C)
                )
            ) {
                Text(
                    text = "View My Rewards 🏆",
                    color = Color(0xFF7CFC00),
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onRecalculate,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF7CFC00)
                )
            ) {
                Text(
                    text = "Re-Calculate",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/* ---------- BMI LOGIC ---------- */

fun getBmiResult(bmi: Double): Pair<String, String> {
    return when {
        bmi < 18.5 -> {
            "Underweight" to """
Diet and Nutrition

Eat More Calories:
• Consume high-calorie foods like nuts, avocados, and healthy oils.
• Increase protein sizes during meals.

Choose Nutrient-Rich Foods:
• Focus on foods rich in protein (lean meats, fish, eggs, legumes).
• Include complex carbohydrates (whole grains, sweet potatoes).

Eat plenty of fruits and vegetables for overall health.
""".trimIndent()
        }

        bmi < 25 -> {
            "Normal" to """
Diet and Nutrition

Maintain a Balanced Diet:
• Eat a variety of fruits, vegetables, whole grains, and lean proteins.
• Maintain regular meal timings.

Stay Hydrated:
• Drink plenty of water throughout the day.

Continue regular physical activity to stay healthy.
""".trimIndent()
        }

        bmi < 30 -> {
            "Overweight" to """
Diet and Nutrition

Control Portion Sizes:
• Reduce high-calorie and sugary foods.
• Focus on portion control.

Increase Fiber Intake:
• Eat more fruits, vegetables, and whole grains.

Engage in regular physical activity to manage weight.
""".trimIndent()
        }

        else -> {
            "Obese" to """
Diet and Nutrition

Adopt a Low-Calorie Diet:
• Avoid processed foods and sugary drinks.
• Choose lean proteins and vegetables.

Increase Physical Activity:
• Include daily exercise like walking or cycling.

Consult a healthcare professional for personalized guidance.
""".trimIndent()
        }
    }
}
