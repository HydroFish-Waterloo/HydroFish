import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonParseException
import com.google.gson.JsonParser
import com.hydrofish.app.api.HydrationEntry
import com.hydrofish.app.ui.composables.tabs.HydrationEntryDeserializer
import com.hydrofish.app.ui.composables.tabs.aggregateHydrationDataByDay
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*

class HydrationEntryDeserializerTest {

    private lateinit var deserializer: HydrationEntryDeserializer

    @Before
    fun setUp() {
        deserializer = HydrationEntryDeserializer()
    }

    @Test
    fun `deserialize valid JSON array`() {
        // Given
        val json = "[{\"day\":\"2024-03-17T10:30:00Z\",\"total_ml\":500}]"
        val typeOfT: Type? = null
        val context: JsonDeserializationContext? = null

        // When
        val result = deserializer.deserialize(JsonParser.parseString(json), typeOfT, context)

        // Then
        assertEquals(1, result.size)
        assertEquals(500, result[0].hydrationAmount)
    }

    @Test(expected = JsonParseException::class)
    fun `deserialize invalid JSON format`() {
        // Given
        val invalidJson = "{\"day\":\"2024-03-17T10:30:00Z\",\"total_ml\":\"invalid\"}"
        val typeOfT: Type? = null
        val context: JsonDeserializationContext? = null

        // When
        deserializer.deserialize(JsonParser.parseString(invalidJson), typeOfT, context)
    }

    @Test
    fun `parse valid date string`() {
        // Given
        val dateString = "2024-03-17T10:30:00Z"

        // When
        val result = deserializer.parseDateString(dateString)

        // Then
        val expectedDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH).parse(dateString)
        assertEquals(expectedDate, result)
    }
}

class HydrationDataAggregatorTest {

    @Test
    fun `test aggregateHydrationDataByDay`() {
        // Prepare test data
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        val date1 = dateFormat.parse("2022-01-01")!!
        val date2 = dateFormat.parse("2022-01-02")!!
        val date3 = dateFormat.parse("2022-01-01")!!
        val entries = listOf(
            HydrationEntry(date1, 200),
            HydrationEntry(date2, 300),
            HydrationEntry(date3, 150)
        )

        // Call the function to be tested
        val result = aggregateHydrationDataByDay(entries)

        // Verify the result
        assert(result.size == 2) // We expect two entries for two different days
        assert(result[0].date == date1 && result[0].hydrationAmount == 350) // Date1 is aggregated with 200 + 150 = 350
        assert(result[1].date == date2 && result[1].hydrationAmount == 300) // Date2 is unique and remains 300
    }
}