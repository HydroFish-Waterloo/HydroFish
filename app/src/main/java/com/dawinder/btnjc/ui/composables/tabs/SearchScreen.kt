package com.dawinder.btnjc.ui.composables.tabs

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dawinder.btnjc.ui.theme.BottomTabNavigationJetpackComposeTheme
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.core.entry.entryModelOf

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BottomTabNavigationJetpackComposeTheme {
                SearchScreen()
            }
        }
    }
}

@Composable
fun SearchScreen() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        // Sample data for the list
        val hydrationData = listOf(
            HydrationEntry("2022-01-01", 200),
            HydrationEntry("2022-01-02", 150),
            HydrationEntry("2022-01-03", 300),
            HydrationEntry("2022-01-04", 100),
            HydrationEntry("2022-01-05", 200),
            HydrationEntry("2022-01-06", 150),
            HydrationEntry("2022-01-07", 300),
            HydrationEntry("2022-01-08", 100),
            HydrationEntry("2022-01-09", 200),
            HydrationEntry("2022-01-10", 150),
            HydrationEntry("2022-01-11", 300),
            HydrationEntry("2022-01-12", 100)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Placeholder text instead of Greeting
            Greeting("Search Screen")

            Spacer(modifier = Modifier.height(16.dp))

            // Button to switch between different data sets
            var chartData by remember { mutableStateOf(hydrationData) }

            Row {
                Button(onClick = { chartData = hydrationData.takeLast(3) }) {
                    Text("Last 3 Days")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { chartData = hydrationData.takeLast(7) }) {
                    Text("Last 7 Days")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { chartData = hydrationData.takeLast(30) }) {
                    Text("Last Month")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Chart (with placeholder data)
            val hydrationAmounts = chartData.map { it.hydrationAmount }
            val chartEntryModel = entryModelOf(*hydrationAmounts.toTypedArray())
            Chart(
                chart = columnChart(),
                model = chartEntryModel,
                startAxis = rememberStartAxis(),
                bottomAxis = rememberBottomAxis(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(color = Color.White)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // List (with placeholder data)
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.Gray)
            ) {
                items(chartData) { entry ->
                    HydrationListItem(entry)
                }
            }
        }
    }
}


@Composable
fun HydrationListItem(entry: HydrationEntry) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            modifier = Modifier
                .size(24.dp)
                .padding(end = 8.dp)
        )
        Text(text = "Date: ${entry.date}")

        Spacer(modifier = Modifier.width(16.dp))

        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            modifier = Modifier
                .size(24.dp)
                .padding(end = 8.dp)
        )
        Text(text = "Hydration: ${entry.hydrationAmount} ml")
    }
}

data class HydrationEntry(val date: String, val hydrationAmount: Int)

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BottomTabNavigationJetpackComposeTheme {
        Greeting("Android")
    }
}
