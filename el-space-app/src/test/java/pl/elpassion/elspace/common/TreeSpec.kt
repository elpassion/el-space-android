package pl.elpassion.elspace.common

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.Spec
import io.kotlintest.TestCase
import io.kotlintest.TestSuite
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
abstract class TreeSpec : Spec() {

    infix operator fun String.invoke(build: TreeTestSuiteBuilder.() -> Unit) {
        val suite = TreeTestSuiteBuilder(TestSuite(sanitizeSpecName(this)), testCaseConfig = defaultTestCaseConfig)
        suite.build()
        rootTestSuite.addNestedSuite(suite.testSuite)
    }

    infix operator fun String.compareTo(assert: () -> Unit)  = 0.also {
        rootTestSuite.addTestCase(TestCase(
                suite = rootTestSuite,
                name = this,
                test = assert,
                config = defaultTestCaseConfig
        ))
    }

}

fun sanitizeSpecName(name: String) = name.replace("(", " ").replace(")", " ")
