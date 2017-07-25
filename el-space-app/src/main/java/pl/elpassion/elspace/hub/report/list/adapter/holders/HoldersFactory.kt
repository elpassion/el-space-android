package pl.elpassion.elspace.hub.report.list.adapter.holders

import android.view.View
import pl.elpassion.elspace.R
import pl.elpassion.elspace.hub.report.list.ReportListController


fun createDayItemViewHolder(controller: ReportListController) =
        R.layout.day_item to { itemView: View -> DayItemViewHolder(itemView, controller) }

fun createDayNotFilledInItemViewHolder(controller: ReportListController) =
        R.layout.day_not_filled_in_item to { itemView: View -> DayNotFilledInItemViewHolder(itemView, controller) }

fun createDayWithDailyReportsItemViewHolder(controller: ReportListController) =
        R.layout.day_with_daily_report_item to { itemView: View -> DayWithDailyReportsItemViewHolder(itemView, controller) }

fun createPaidVacationReportItemViewHolder(controller: ReportListController) =
        R.layout.paid_vacations_report_item to { itemView: View -> PaidVacationReportItemViewHolder(itemView, controller) }

fun createRegularReportItemViewHolder(controller: ReportListController) =
        R.layout.regular_hourly_report_item to { itemView: View -> RegularReportItemViewHolder(itemView, controller) }

fun createWeekendDayItemViewHolder(controller: ReportListController) =
        R.layout.weekend_day_item to { itemView: View -> WeekendDayItemViewHolder(itemView, controller) }

fun createSeparatorItemViewHolder() =
        R.layout.hub_separator to ::SeparatorItemViewHolder

fun createEmptyItemViewHolder() =
        R.layout.empty_adapter_item to ::EmptyItemViewHolder
