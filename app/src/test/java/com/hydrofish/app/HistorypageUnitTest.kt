import org.junit.Assert.assertEquals
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.*

fun parseDateString(dateString: String): Date {
    // Remove the timezone offset (+0000) from the date string
    val dateStringWithoutOffset = dateString.substring(0, dateString.lastIndexOf(" "))

    val dateFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy", Locale.ENGLISH)
    return dateFormat.parse(dateStringWithoutOffset)
}
class MainActivityTest {

    @Test
    fun parseDateStringTest1() {
        // Mock input date string
        val dateString = "Fri Feb 18 15:00:00 2022 -0500"

        // Expected output date
        val expectedDate = Calendar.getInstance().apply {
            set(Calendar.YEAR, 2022)
            set(Calendar.MONTH, Calendar.FEBRUARY)
            set(Calendar.DAY_OF_MONTH, 18)
            set(Calendar.HOUR_OF_DAY, 15)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        // Call the function
        val actualDate = parseDateString(dateString)

        // Assert
        assertEquals(expectedDate, actualDate)
    }

    @Test
    fun parseDateStringTest2() {
        // Mock input date string
        val dateString = "Fri Feb 22 7:00:01 2024 -0500"

        // Expected output date
        val expectedDate = Calendar.getInstance().apply {
            set(Calendar.YEAR, 2024)
            set(Calendar.MONTH, Calendar.FEBRUARY)
            set(Calendar.DAY_OF_MONTH, 22)
            set(Calendar.HOUR_OF_DAY, 7)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 1)
            set(Calendar.MILLISECOND, 0)
        }.time

        // Call the function
        val actualDate = parseDateString(dateString)

        // Assert
        assertEquals(expectedDate, actualDate)
    }
}
