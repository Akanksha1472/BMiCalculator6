package com.example.bmicalculator

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Screen that displays the kid's complete rewards history.
 *
 * Sequence diagram role: "App retrieves and displays the rewards history
 * for the kid." – loads rewards from [RewardsManager.getRewardsHistory],
 * which in turn reads from the [RewardsRepository] (local storage or Firebase).
 */
class RewardsActivity : ComponentActivity() {

    private lateinit var rewardsManager: RewardsManager

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { /* no-op */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        rewardsManager = RewardsManager(
            repository = LocalRewardsRepository(this),
            notificationHelper = NotificationHelper(this)
        )

        // Request notification permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Black
                ) {
                    RewardsScreen(rewardsManager = rewardsManager)
                }
            }
        }
    }
}

@Composable
fun RewardsScreen(rewardsManager: RewardsManager) {
    var rewards by remember { mutableStateOf<List<Reward>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        rewardsManager.getRewardsHistory(
            onSuccess = { list ->
                rewards = list
                isLoading = false
            },
            onError = { e ->
                errorMessage = "Unable to load rewards: ${e.message}"
                isLoading = false
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text(
            text = "My Rewards 🏆",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(16.dp))

        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF7CFC00))
                }
            }
            errorMessage != null -> {
                // Error handling: Firebase or storage unreachable
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF3A0000))
                ) {
                    Text(
                        text = errorMessage ?: "Unknown error",
                        color = Color.Red,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            rewards.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "No rewards yet. Complete a BMI check to earn your first reward!",
                        color = Color.Gray,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            else -> {
                val totalPoints = rewards.sumOf { it.points }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1C1C))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Total Points", color = Color.Gray, fontSize = 12.sp)
                            Text(
                                text = "$totalPoints pts",
                                color = Color(0xFF7CFC00),
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Rewards", color = Color.Gray, fontSize = 12.sp)
                            Text(
                                text = "${rewards.size}",
                                color = Color.White,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(rewards) { reward ->
                        RewardCard(reward = reward)
                    }
                }
            }
        }
    }
}

private val rewardDateFormat = SimpleDateFormat("MMM d, yyyy HH:mm", Locale.getDefault())

@Composable
fun RewardCard(reward: Reward) {
    val dateString = rewardDateFormat.format(Date(reward.timestamp))

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1C1C))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = reward.title,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = reward.description,
                    color = Color.LightGray,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = dateString,
                    color = Color.Gray,
                    fontSize = 11.sp
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "+${reward.points}",
                    color = Color(0xFF7CFC00),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(text = "pts", color = Color.Gray, fontSize = 11.sp)
            }
        }
    }
}
