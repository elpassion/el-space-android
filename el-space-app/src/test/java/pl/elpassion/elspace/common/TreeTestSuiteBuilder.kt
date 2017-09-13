package pl.elpassion.elspace.common

import io.kotlintest.TestCase
import io.kotlintest.TestCaseConfig
import io.kotlintest.TestSuite

data class TreeTestSuiteBuilder(val testSuite: TestSuite,
                                val parent: TreeTestSuiteBuilder? = null,
                                val testCaseConfig: TestCaseConfig) {

    private var before: (() -> Unit) = {}

    fun before(block: () -> Unit) {
        before = block
    }

    infix operator fun String.invoke(apply: TreeTestSuiteBuilder.() -> Unit) {
        val nested = TreeTestSuiteBuilder(TestSuite(sanitizeSpecName(this)), this@TreeTestSuiteBuilder, testCaseConfig)
        testSuite.addNestedSuite(nested.testSuite)
        nested.apply()
    }

    infix operator fun String.compareTo(test: () -> Unit) = 0.also {
        val tc = TestCase(
                suite = testSuite,
                name = sanitizeSpecName(this),
                test = {
                    executeUpstreamBefore()
                    test()
                },
                config = testCaseConfig)
        testSuite.addTestCase(tc)
    }

    private fun executeUpstreamBefore() {
        parent?.executeUpstreamBefore()
        before.invoke()
    }
}