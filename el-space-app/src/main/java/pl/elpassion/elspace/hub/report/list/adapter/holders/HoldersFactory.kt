package pl.elpassion.elspace.hub.report.list.adapter.holders

import android.view.View
import pl.elpassion.elspace.R
import pl.elpassion.elspace.hub.report.list.OnDayClick
import pl.elpassion.elspace.hub.report.list.OnReportClick


fun createDayItemViewHolder(onDayClick: OnDayClick) =
        R.layout.day_item to { itemView: View -> DayItemViewHolder(itemView, onDayClick) }

fun createDayNotFilledInItemViewHolder(onDayClick: OnDayClick) =
        R.layout.day_not_filled_in_item to { itemView: View -> DayNotFilledInItemViewHolder(itemView, onDayClick) }

fun createDayWithDailyReportsItemViewHolder(onReportClick: OnReportClick) =
        R.layout.day_with_daily_report_item to { itemView: View -> DayWithDailyReportsItemViewHolder(itemView, onReportClick) }

fun createPaidVacationReportItemViewHolder(onReportClick: OnReportClick) =
        R.layout.paid_vacations_report_item to { itemView: View -> PaidVacationReportItemViewHolder(itemView, onReportClick) }

fun createRegularReportItemViewHolder(onReportClick: OnReportClick) =
        R.layout.regular_hourly_report_item to { itemView: View -> RegularReportItemViewHolder(itemView, onReportClick) }

fun createWeekendDayItemViewHolder(onDayClick: OnDayClick) =
        R.layout.weekend_day_item to { itemView: View -> WeekendDayItemViewHolder(itemView, onDayClick) }

fun createSeparatorItemViewHolder() =
        R.layout.hub_separator to ::SeparatorItemViewHolder

fun createEmptyItemViewHolder() =
        R.layout.empty_adapter_item to ::EmptyItemViewHolder
