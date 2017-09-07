package pl.elpassion.elspace.hub.report.list

import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.FreeSpec
import pl.elpassion.elspace.common.extensions.daysForCurrentMonth
import pl.elpassion.elspace.common.extensions.getFullMonthName
import pl.elpassion.elspace.common.extensions.getTimeFrom

class CalendarToYearMonthConverterTest : FreeSpec({

    "Should correctly convert " - {

        "year" {
            getTimeFrom(2016, 1, 1).toYearMonth().run {
                year shouldBe 2016
            }
        }

        "month " {
            getTimeFrom(2016, 11, 1).toYearMonth().run {
                month.index shouldBe 11
            }
        }

        "month name " {
            getTimeFrom(2016, 11, 21).run {
                this.toYearMonth().month.monthName shouldBe this.getFullMonthName()
            }
        }

        "days in month" {
            getTimeFrom(2016, 11, 21).run {
                this.toYearMonth().month.daysInMonth shouldBe this.daysForCurrentMonth()
            }
        }
    }

})