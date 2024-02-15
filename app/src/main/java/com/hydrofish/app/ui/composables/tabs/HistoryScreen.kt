package com.hydrofish.app.ui.composables.tabs

import android.content.Context
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.hydrofish.app.ui.theme.HydroFishTheme
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.core.entry.entryModelOf
import java.lang.reflect.Type
import com.google.gson.JsonDeserializer
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HydroFishTheme {
                HistoryScreen()
            }
        }
    }
}

@Composable
fun HistoryScreen() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        // Extract hydration data from historyData
        val hydrationData = readHydrationDataFromJson(LocalContext.current)
        // Sample data for the list
//        val hydrationData = listOf(
//            HydrationEntry("2022-01-01", 200),
//            HydrationEntry("2022-01-02", 150),
//            HydrationEntry("2022-01-03", 300),
//            HydrationEntry("2022-01-04", 100),
//            HydrationEntry("2022-01-05", 200),
//            HydrationEntry("2022-01-06", 150),
//            HydrationEntry("2022-01-07", 300),
//            HydrationEntry("2022-01-08", 100),
//            HydrationEntry("2022-01-09", 200),
//            HydrationEntry("2022-01-10", 150),
//            HydrationEntry("2022-01-11", 300),
//            HydrationEntry("2022-01-12", 100)
//        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Placeholder text instead of Greeting
            Greeting("History")
            //Greeting(hydrationData_1.toString())

            Spacer(modifier = Modifier.height(16.dp))

            // Button to switch between different data sets
            var chartData by remember { mutableStateOf(hydrationData) }

            Row {
                Button(
                    onClick = { chartData = hydrationData.takeLast(3) },
                    modifier = Modifier
                        .weight(1.2f)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Last 3 Days",
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { chartData = hydrationData.takeLast(7) },
                    modifier = Modifier
                        .weight(1.2f)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Last 7 Days",
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { chartData = hydrationData.takeLast(30) },
                    modifier = Modifier
                        .weight(1.2f)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Last Month",
                        textAlign = TextAlign.Center
                    )
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
    }
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
        Text(text = "Hydration: ${entry.hydrationAmount} ml")
    }
}


data class HydrationEntry(val date: Date, val hydrationAmount: Int)

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "$name",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    HydroFishTheme {
        Greeting("Android")
    }
}
class HydrationEntryDeserializer : JsonDeserializer<List<HydrationEntry>> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): List<HydrationEntry> {
        val entries = mutableListOf<HydrationEntry>()

        if (json != null && json.isJsonObject) {
            val jsonObject = json.asJsonObject

            val historyObject = jsonObject.getAsJsonObject("history")

            historyObject.entrySet().forEach { (dateString, amountElement) ->
                val amount = amountElement.asInt
                val date = parseDateString(dateString)
                entries.add(HydrationEntry(date, amount))
            }
        } else {
            throw JsonParseException("Invalid JSON format")
        }

        return entries
    }

    private fun parseDateString(dateString: String): Date {
        val dateFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy Z", Locale.ENGLISH)
        return dateFormat.parse(dateString)
    }
}

fun readHydrationDataFromJson(context: Context): List<HydrationEntry> {
    val gson = GsonBuilder()
        .registerTypeAdapter(object : TypeToken<List<HydrationEntry>>() {}.type, HydrationEntryDeserializer())
        .create()

    // Get the InputStream to the JSON file
    val inputStream = context.assets.open("arthur_water_intake_realistic.json")

    // Read the JSON file into a string
    val jsonString = inputStream.bufferedReader().use { it.readText() }

    // Deserialize the JSON string into a list of HydrationEntry objects
    return gson.fromJson(jsonString, object : TypeToken<List<HydrationEntry>>() {}.type)
}