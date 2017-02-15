package pl.elpassion.elspace.hub.report.datechooser

import android.support.v4.app.FragmentManager
import com.philliphsu.bottomsheetpickers.date.BottomSheetDatePickerDialog
import pl.elpassion.elspace.common.extensions.getCurrentTimeCalendar
import pl.elpassion.elspace.common.extensions.getDateString
import java.util.*

private val DATE_DIALOG_TAG = "date_dialog"

fun showDateDialog(supportFragmentManager: FragmentManager, dateListener: (String) -> Unit) {
    val now = getCurrentTimeCalendar()
    val dateDialog = BottomSheetDatePickerDialog.newInstance(
            { _, year, monthOfYear, dayOfMonth ->
                dateListener(getDateString(year, monthOfYear + 1, dayOfMonth))
            },
            now.get(Calendar.YEAR),
            now.get(Calendar.MONTH),
            now.get(Calendar.DAY_OF_MONTH))
    dateDialog.show(supportFragmentManager, DATE_DIALOG_TAG)
}
