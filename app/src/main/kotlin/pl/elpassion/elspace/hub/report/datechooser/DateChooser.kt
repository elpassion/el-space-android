package pl.elpassion.elspace.hub.report.datechooser

import android.support.v4.app.FragmentManager
import com.philliphsu.bottomsheetpickers.date.BottomSheetDatePickerDialog
import pl.elpassion.elspace.common.extensions.getCurrentTimeCalendar
import pl.elpassion.elspace.common.extensions.getDateString
import java.util.*

private val DATE_DIALOG_TAG = "date_dialog"

fun showDateDialog(supportFragmentManager: FragmentManager, dateListener: (String) -> Unit) {
    val calendar = getCurrentTimeCalendar()
    val dateDialog = createDateDialog(calendar, dateListener)
    dateDialog.show(supportFragmentManager, DATE_DIALOG_TAG)
}

private fun createDateDialog(calendar: Calendar, dateListener: (String) -> Unit) =
        with(calendar) {
            BottomSheetDatePickerDialog.newInstance(
                    { _, year, monthOfYear, dayOfMonth ->
                        dateListener(getDateString(year, monthOfYear + 1, dayOfMonth))
                    },
                    get(Calendar.YEAR),
                    get(Calendar.MONTH),
                    get(Calendar.DAY_OF_MONTH))
        }