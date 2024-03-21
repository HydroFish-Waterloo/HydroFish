import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonParseException
import com.google.gson.JsonParser
import com.hydrofish.app.ui.composables.tabs.HydrationEntryDeserializer
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
        // Add more assertions based on your expected behavior
    }

    @Test(expected = JsonParseException::class)
    fun `deserialize invalid JSON format`() {
        // Given
        val invalidJson = "{\"day\":\"2024-03-17T10:30:00Z\",\"total_ml\":\"invalid\"}"
        val typeOfT: Type? = null
        val context: JsonDeserializationContext? = null

        // When
        deserializer.deserialize(JsonParser.parseString(invalidJson), typeOfT, context)

        // Then (exception should be thrown)
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

    // Add more test cases as needed for edge cases, boundary values, etc.
}
