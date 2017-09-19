package pl.elpassion.elspace.hub.report.list

import io.kotlintest.matchers.shouldBe
import pl.elpassion.elspace.common.TreeSpec
import pl.elpassion.elspace.common.assertOnFirstElement
import pl.elpassion.elspace.common.extensions.getTimeFrom
import java.util.*

class ReportListDateModelTest : TreeSpec() {

    private val startDate = getTimeFrom(2016, Calendar.OCTOBER, 1)

    init {
        "should " {
            "send provided date on start" > {
                ReportListDateModel(startDate).run {
                    dates.test().assertOnFirstElement {
                        it.year shouldBe 2016
                        it.month.index shouldBe Calendar.OCTOBER
                    }
                }
            }
            "change date provided on start on Next month event" > {
                ReportListDateModel(startDate).run {
                    events.accept(ReportList.Date.Event.OnNextMonth)
                    dates.test().assertOnFirstElement {
                        it.year shouldBe 2016
                        it.month.index shouldBe Calendar.NOVEMBER
                    }
                }
            }
            "change date provided on start on Previous month event" > {
                ReportListDateModel(startDate).run {
                    events.accept(ReportList.Date.Event.OnPreviousMonth)
                    dates.test().assertOnFirstElement {
                        it.year shouldBe 2016
                        it.month.index shouldBe Calendar.SEPTEMBER
                    }
                }
            }
            "change date provided on start on Change month event" > {
                ReportListDateModel(startDate).run {
                    events.accept(ReportList.Date.Event.OnChangeToDate(getTimeFrom(2015, Calendar.JUNE, 1)))
                    dates.test().assertOnFirstElement {
                        it.year shouldBe 2015
                        it.month.index shouldBe Calendar.JUNE
                    }
                }
            }
            "return last date" > {
                ReportListDateModel(startDate).run {
                    lastDate.run {
                        year shouldBe 2016
                        month.index shouldBe Calendar.OCTOBER
                    }
                }
            }
        }
    }
}