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
        val json = "[{\"day\":\"2024-03-17T10:30:00Z\",\"total_ml\":500}]"
        val typeOfT: Type? = null
        val context: JsonDeserializationContext? = null

        val result = deserializer.deserialize(JsonParser.parseString(json), typeOfT, context)

        assertEquals(1, result.size)
        assertEquals(500, result[0].hydrationAmount)
    }

    @Test(expected = JsonParseException::class)
    fun `deserialize invalid JSON format`() {
        val invalidJson = "{\"day\":\"2024-03-17T10:30:00Z\",\"total_ml\":\"invalid\"}"
        val typeOfT: Type? = null
        val context: JsonDeserializationContext? = null

        deserializer.deserialize(JsonParser.parseString(invalidJson), typeOfT, context)
    }

    @Test(expected = JsonParseException::class)
    fun `deserialize invalid date format`() {
        val invalidJson = "{\"day\":\"invalid\",\"total_ml\":500}"
        val typeOfT: Type? = null
        val context: JsonDeserializationContext? = null

        deserializer.deserialize(JsonParser.parseString(invalidJson), typeOfT, context)
    }

    @Test(expected = JsonParseException::class)
    fun `deserialize invalid hydration amount format`() {
        val invalidJson = "{\"day\":\"2024-03-17T10:30:00Z\",\"total_ml\":\"invalid\"}"
        val typeOfT: Type? = null
        val context: JsonDeserializationContext? = null

        deserializer.deserialize(JsonParser.parseString(invalidJson), typeOfT, context)
    }

    @Test(expected = JsonParseException::class)
    fun `parse invalid date string`() {
        val invalidDateString = "invalid"
        deserializer.parseDateString(invalidDateString)
    }

    @Test(expected = JsonParseException::class)
    fun `deserialize empty JSON array`() {
        val emptyJson = "[]"
        val typeOfT: Type? = null
        val context: JsonDeserializationContext? = null

        deserializer.deserialize(JsonParser.parseString(emptyJson), typeOfT, context)
    }

    @Test(expected = JsonParseException::class)
    fun `deserialize null JSON`() {
        val nullJson = "null"
        val typeOfT: Type? = null
        val context: JsonDeserializationContext? = null

        deserializer.deserialize(JsonParser.parseString(nullJson), typeOfT, context)
    }

    @Test(expected = JsonParseException::class)
    fun `deserialize missing fields in JSON`() {
        val json = "[{\"day\":\"2024-03-17T10:30:00Z\"}]"
        val typeOfT: Type? = null
        val context: JsonDeserializationContext? = null

        deserializer.deserialize(JsonParser.parseString(json), typeOfT, context)
    }

    @Test
    fun `parse valid date string`() {

        val dateString = "2024-03-17T10:30:00Z"
        val result = deserializer.parseDateString(dateString)
        val expectedDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH).parse(dateString)
        assertEquals(expectedDate, result)
    }

}
