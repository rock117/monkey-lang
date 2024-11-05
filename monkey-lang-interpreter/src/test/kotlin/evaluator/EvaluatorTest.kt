package evaluator

import Object_
import lexer.Lexer
import `object`.Boolean_
import `object`.Integer
import org.testng.Assert
import org.testng.annotations.Test
import parser.Parser

class EvaluatorTest {

    @Test
    fun testEvalIntegerExpression() {
        val tests = listOf("5", "10")
        for(test in tests) {
            val obj = testEval(test)
            Assert.assertNotNull(obj)
            val integer = obj as Integer
            Assert.assertEquals(test.toInt(), integer.value)
        }
    }

    @Test
    fun testEvalBooleanExpression() {
        val tests = listOf("true", "false")
        for(test in tests) {
            val obj = testEval(test)
            Assert.assertNotNull(obj)
            val integer = obj as Boolean_
            Assert.assertEquals("true".equals(test), integer.value)
        }
    }

    fun testEval(input: String): Object_? {
        val parser = Parser.new(Lexer.new(input))
        val program = parser.parseProgram()
        return eval(program)
    }
}