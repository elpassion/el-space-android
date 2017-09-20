package pl.elpassion.elspace.hub.report.list

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.kotlintest.matchers.shouldBe
import io.reactivex.subjects.PublishSubject
import pl.elpassion.elspace.common.TreeSpec
import pl.elpassion.elspace.common.assertOnFirstElement
import pl.elpassion.elspace.common.extensions.getTimeFrom
import pl.elpassion.elspace.hub.report.list.adapter.Empty
import pl.elpassion.elspace.hub.report.list.service.ReportsListAdaptersService
import java.util.*

class ReportListModelTest : TreeSpec() {

    private val reportListAdaptersSubject = PublishSubject.create<List<AdapterItem>>()
    private val service = mock<ReportsListAdaptersService>().apply {
        whenever(createReportsListAdapters(any())).thenReturn(reportListAdaptersSubject)
    }
    private var currentDay: Calendar = getTimeFrom(2016, Calendar.OCTOBER, 4)
    private val currentDayProvider = { currentDay }

    private val model = ReportListModel(service, currentDayProvider)
    private val states = model.states
    private val events = model.events

    init {
        "Model should " {
            "start with predefined ui state" > {
                states
                        .test()
                        .assertOnFirstElement { it shouldBe ReportListModel.getGetStartState(currentDay) }
            }
            "on create " {
                before { events.accept(ReportList.Event.OnCreate) }
                "propagate list of adapters returned from service" > {
                    val reportListAdapters = listOf(Empty, Empty)
                    reportListAdaptersSubject.onNext(reportListAdapters)
                    states.test()
                            .assertOnFirstElement { it.adapterItems shouldBe reportListAdapters }
                }
                "call service for report list adapters" > {
                    verify(service).createReportsListAdapters(yearMonth = getTimeFrom(year = 2016, month = Calendar.JUNE, day = 1).toYearMonth())
                }
                "show loader" > {
                    states
                            .test()
                            .assertOnFirstElement {
                                it.isLoaderVisible shouldBe true
                            }
                }
            }
            "on change to " {
                "next month, change yearMonth to correct one" > {
                    events.accept(ReportList.Event.OnNextMonth)
                    states.test()
                            .assertOnFirstElement { it.yearMonth shouldBe yearMonthFrom(2016, Calendar.NOVEMBER) }
                }
                "previous month, change yearMonth to correct one" > {
                    events.accept(ReportList.Event.OnPreviousMonth)
                    states.test()
                            .assertOnFirstElement { it.yearMonth shouldBe yearMonthFrom(2016, Calendar.SEPTEMBER) }
                }
                "previous month, change yearMonth to correct one" > {
                    currentDay = getTimeFrom(2014, Calendar.JANUARY, 1)
                    events.accept(ReportList.Event.OnChangeToCurrentDay)
                    states.test()
                            .assertOnFirstElement { it.yearMonth shouldBe yearMonthFrom(2014, Calendar.JANUARY) }
                }
            }
        }
    }

    private fun yearMonthFrom(year: Int, month: Int) = getTimeFrom(year, month, 1).toYearMonth()
}
