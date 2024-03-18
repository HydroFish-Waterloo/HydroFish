package com.hydrofish.app.ui.composables.tabs

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.hydrofish.app.api.ApiClient.historyApiService
import com.hydrofish.app.api.DataResponse
import com.hydrofish.app.api.HydrationEntry
import com.hydrofish.app.ui.theme.HydroFishTheme
import com.hydrofish.app.utils.UserSessionRepository
import com.hydrofish.app.viewmodelfactories.HistoryViewModelFactory
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.core.entry.entryModelOf
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(userSessionRepository: UserSessionRepository, navController: NavHostController) {
    var hydrationData by remember { mutableStateOf(emptyList<HydrationEntry>()) }
    val historyViewModel: HistoryViewModel = viewModel(factory = HistoryViewModelFactory(userSessionRepository))
    val isUserLoggedIn by historyViewModel.isLoggedIn.observeAsState(false)

    //login lock
    if (!isUserLoggedIn) {
        CoverScreen(navController)
        return
    }

    fun fetchHydrationData() {
        val token = userSessionRepository.getToken()
        val headers = mapOf("Authorization" to ("Token " ?: "") + token)
        val call = historyApiService.getHydrationData(headers)

        call.enqueue(object : Callback<DataResponse> {
            override fun onResponse(call: Call<DataResponse>, response: Response<DataResponse>) {
                if (response.isSuccessful) {
                    hydrationData = response.body()?.data ?: emptyList()
                } else {
                    Log.e("HistoryViewModel", "Failed to fetch data: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<DataResponse>, t: Throwable) {
                Log.e("HistoryViewModel", "Error occurred while fetching data", t)
            }
        })
    }

    LaunchedEffect(Unit) {
        fetchHydrationData()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Placeholder text instead of Greeting
            Greeting("History")

            Spacer(modifier = Modifier.height(16.dp))

            val hydrationDataSum = aggregateHydrationDataByDay(hydrationData)

            // Default data for the last three days
            val defaultData = hydrationDataSum.takeLast(3)

            var chartData by remember { mutableStateOf(defaultData) }
            var chartDataSingle by remember { mutableStateOf(defaultData) }

            LaunchedEffect(hydrationDataSum) {
                chartData = defaultData
                chartDataSingle = defaultData
            }

            Row {
                Button(
                    onClick = {
                        chartDataSingle = hydrationDataSum.takeLast(3)
                        chartData = hydrationDataSum.takeLast(3)},
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
                    onClick = {
                        chartDataSingle = hydrationDataSum.takeLast(7)
                        chartData = hydrationDataSum.takeLast(7)},
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
                    onClick = {
                        chartDataSingle = hydrationDataSum.takeLast(30)
                        chartData = hydrationDataSum.takeLast(30)},
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
                //chart = columnChart(),
                chart = lineChart(),
                model = chartEntryModel,
                oldModel = chartEntryModel,
                startAxis = rememberStartAxis(),
                bottomAxis = rememberBottomAxis(),
                isZoomEnabled = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(color = Color.White)
            )

            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.Gray)
            ) {
                items(chartDataSingle) { entry ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "Time: ${entry.date}",
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Text(
                            text = "Hydration: ${entry.hydrationAmount} ml",
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                }
            }

        }
    }
}

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

        if (json != null && json.isJsonArray) {
            val dataArray = json.asJsonArray

            dataArray.forEach { entryElement ->
                val entryObject = entryElement.asJsonObject
                val dateString = entryObject.get("day").asString
                val amount = entryObject.get("total_ml").asInt
                val date = parseDateString(dateString)
                entries.add(HydrationEntry(date, amount))
            }
        } else {
            throw JsonParseException("Invalid JSON format")
        }

        return entries
    }

    fun parseDateString(dateString: String): Date {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH)
        return dateFormat.parse(dateString)
    }
}

//fun readHydrationDataFromJson(context: Context): List<HydrationEntry> {
//    val gson = GsonBuilder()
//        .registerTypeAdapter(object : TypeToken<List<HydrationEntry>>() {}.type, HydrationEntryDeserializer())
//        .create()
//
//    // Get the InputStream to the JSON file
//    val inputStream = context.assets.open("arthur_water_intake_realistic.json")
//
//    // Read the JSON file into a string
//    val jsonString = inputStream.bufferedReader().use { it.readText() }
//
//    // Deserialize the JSON string into a list of HydrationEntry objects
//    return gson.fromJson(jsonString, object : TypeToken<List<HydrationEntry>>() {}.type)
//}

fun aggregateHydrationDataByDay(hydrationData: List<HydrationEntry>): List<HydrationEntry> {
    val aggregatedData = mutableMapOf<String, Int>()
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)

    for (entry in hydrationData) {
        val date = dateFormat.format(entry.date)
        val amount = entry.hydrationAmount
        aggregatedData[date] = aggregatedData.getOrDefault(date, 0) + amount
    }

    return aggregatedData.map { (date, amount) ->
        HydrationEntry(
            date = dateFormat.parse(date)!!,
            hydrationAmount = amount
        )
    }
}
