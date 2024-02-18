
import android.content.Context
import android.content.SharedPreferences
import com.hydrofish.app.ui.composables.tabs.timeStringToSeconds
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.mockito.ArgumentMatchers
import org.mockito.Mockito

@RunWith(Parameterized::class)
class TimeConversionTest(private val input: String, private val expected: Int?) {

    class SharedPreferencesTest {

        private lateinit var sharedPreferences: SharedPreferences
        private lateinit var context: Context
        private lateinit var editor: SharedPreferences.Editor

        @Before
        fun setUp() {
            // Initialize mocks
            context = Mockito.mock(Context::class.java)
            sharedPreferences = Mockito.mock(SharedPreferences::class.java)
            editor = Mockito.mock(SharedPreferences.Editor::class.java)

            Mockito.`when`(
                context.getSharedPreferences(
                    ArgumentMatchers.anyString(),
                    ArgumentMatchers.anyInt()
                )
            ).thenReturn(sharedPreferences)
            Mockito.`when`(sharedPreferences.edit()).thenReturn(editor)
            Mockito.`when`(
                editor.putString(
                    ArgumentMatchers.anyString(),
                    ArgumentMatchers.anyString()
                )
            ).thenReturn(editor)
            Mockito.`when`(editor.putInt(ArgumentMatchers.anyString(), ArgumentMatchers.anyInt())).thenReturn(editor)
            Mockito.`when`(editor.apply()).then {}
        }

        @Test
        fun testSharedPreferencesWriteString() {
            val key = "test_key"
            val value = "test_value"

            sharedPreferences.edit().putString(key, value).apply()

            Mockito.verify(editor).putString(key, value)
            Mockito.verify(editor).apply()
        }

        @Test
        fun testSharedPreferencesReadString() {
            val key = "test_key"
            val defaultValue = "default"
            Mockito.`when`(sharedPreferences.getString(key, defaultValue)).thenReturn("test_value")

            val actualValue = sharedPreferences.getString(key, defaultValue)

            assertEquals("test_value", actualValue)
        }

        @Test
        fun testSharedPreferencesWriteInt() {
            val key = "test_key_int"
            val value = 42

            sharedPreferences.edit().putInt(key, value).apply()

            Mockito.verify(editor).putInt(key, value)
            Mockito.verify(editor).apply()
        }

        @Test
        fun testSharedPreferencesReadInt() {
            val key = "test_key_int"
            val defaultValue = 0
            Mockito.`when`(sharedPreferences.getInt(key, defaultValue)).thenReturn(42)

            val actualValue = sharedPreferences.getInt(key, defaultValue)

            assertEquals(42, actualValue)
        }
    }

    // Inside HydroFishViewModelTest.kt or a new test file in the test folder

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{index}: timeStringToSeconds(\"{0}\") should be {1}")
        fun data(): Collection<Array<Any?>> {
            return listOf(
                arrayOf("12:34", 12 * 3600 + 34 * 60),
                arrayOf("00:00", 0),
                arrayOf("23:59", 23 * 3600 + 59 * 60),
                arrayOf("24:00", null),
                arrayOf("00:60", null),
                arrayOf("abc", null)
            )
        }
    }

    @Test
    fun timeStringToSeconds_ValidAndInvalidTimes_CorrectlyConvertedOrThrowsException() {
        if (expected != null) {
            assertEquals(expected, timeStringToSeconds(input))
        } else {
            assertThrows(IllegalArgumentException::class.java) {
                timeStringToSeconds(input)
            }
        }
    }
}