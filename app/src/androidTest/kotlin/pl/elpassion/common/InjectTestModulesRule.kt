package pl.elpassion.common

import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class InjectTestModulesRule(val stubTestModules: () -> Unit) : TestRule {
    override fun apply(base: Statement, description: Description?) = object : Statement() {
        override fun evaluate() {
            stubTestModules()
            base.evaluate()
        }
    }
}