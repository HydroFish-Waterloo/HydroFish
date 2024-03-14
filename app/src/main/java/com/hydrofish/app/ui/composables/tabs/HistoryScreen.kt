package com.hydrofish.app.ui.composables.tabs

import android.content.Context
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
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
import androidx.compose.runtime.LaunchedEffect
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
import androidx.navigation.NavHostController
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
import com.hydrofish.app.utils.UserSessionRepository
import com.hydrofish.app.viewmodelfactories.SettingsViewModelFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface ApiService {
    //@Headers("Content-Type:application/json")
    //@Headers()
    @GET("hydrofish/getwaterhistory/") // Replace "endpoint" with your API endpoint
    // fun getHydrationData(token: String?): Call<List<HydrationEntry>> // Change the return type as per your response model
    fun getHydrationData(@Header("Authorization") token: String?): Call<List<HydrationEntry>>
}

//fun checkUserLoggedIn(context: Context): Boolean {
//    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
//    return sharedPreferences.getBoolean("isLoggedIn", false)
//}
//
//
//class MainActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        val isLoggedIn = checkUserLoggedIn(applicationContext) // Implement this function to check the user's login status
//        setContent {
//            HydroFishTheme {
//                HistoryScreen(isLoggedIn)
//            }
//        }
//    }
//}

@Composable
fun HistoryScreen(userSessionRepository: UserSessionRepository, token: String?, navController: NavHostController) {
    var hydrationData by remember { mutableStateOf(emptyList<HydrationEntry>()) }
    //var hydrationDataSum by remember { mutableStateOf(emptyList<HydrationEntry>()) }

    val settingsViewModel: SettingsViewModel = viewModel(factory = SettingsViewModelFactory(userSessionRepository))
    val isUserLoggedIn by settingsViewModel.isLoggedIn.observeAsState(false)

    //login lock
    if (!isUserLoggedIn) {
        // If the user is not logged in, display a login screen or redirect to the login activity
        // You can replace the placeholder composable with your actual login screen
        LoginScreen(navController)
        return
    }

    fun fetchHydrationData() {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8000/") // Replace with your API base URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)
        val call = apiService.getHydrationData(token)

        call.enqueue(object : Callback<List<HydrationEntry>> {
            override fun onResponse(call: Call<List<HydrationEntry>>, response: Response<List<HydrationEntry>>) {
                if (response.isSuccessful) {
                    hydrationData = response.body() ?: emptyList()
                } else {
                    Log.e("MainActivity", "Failed to login: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<HydrationEntry>>, t: Throwable) {
                Log.e("MainActivity", "Error occurred while logging in", t)
            }
        })
    }

    LaunchedEffect(Unit) {
        fetchHydrationData()
    }

    val hydrationDataSum = aggregateHydrationDataByDay(hydrationData)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        // Extract hydration data from historyData
        //val hydrationData = readHydrationDataFromJson(LocalContext.current)
        //val hydrationDataSum = aggregateHydrationDataByDay(hydrationData)
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

            var chartData by remember { mutableStateOf(hydrationDataSum) }
            var chartDataSingle by remember { mutableStateOf(hydrationData) }

            Row {
                Button(
                    onClick = { chartData = hydrationDataSum.takeLast(3) },
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
                    onClick = { chartData = hydrationDataSum.takeLast(7) },
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
                    onClick = { chartData = hydrationDataSum.takeLast(30) },
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
                items(chartDataSingle) { entry ->
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
data class HydrationSumEntry(val date: Date, val hydrationAmount: Int)

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

    // Convert aggregated data back to list of HydrationEntry objects
    return aggregatedData.map { (date, amount) ->
        HydrationEntry(
            date = dateFormat.parse(date), // Parse date string back to Date object
            hydrationAmount = amount
        )
    }
}


