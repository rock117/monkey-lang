package evaluator

import Object_
import lexer.Lexer
import `object`.*
import org.testng.Assert
import org.testng.annotations.Test
import parser.Parser

class EvaluatorTest {

    @Test
    fun testEvalIntegerExpression() {
        val tests = listOf("5", "10")
        for (test in tests) {
            val obj = testEval(test)
            Assert.assertNotNull(obj)
            val integer = obj as Integer
            Assert.assertEquals(test.toInt(), integer.value)
        }

        var obj = testEval("1+2")
        Assert.assertNotNull(obj)
        var integer = obj as Integer
        Assert.assertEquals(3, integer.value)

        obj = testEval("1+2*3")
        Assert.assertNotNull(obj)
        integer = obj as Integer
        Assert.assertEquals(7, integer.value)

        obj = testEval("(1+2)*3")
        Assert.assertNotNull(obj)
        integer = obj as Integer
        Assert.assertEquals(9, integer.value)
    }


    @Test
    fun testEvalBooleanExpression() {
        val tests = listOf("true", "false")
        for (test in tests) {
            val obj = testEval(test)
            Assert.assertNotNull(obj)
            val integer = obj as Boolean_
            Assert.assertEquals("true".equals(test), integer.value)
        }

        var obj = testEval("1+1 == 2")
        Assert.assertNotNull(obj)
        var b = obj as Boolean_
        Assert.assertEquals(true, b.value)

        obj = testEval("1+1*3 == 4")
        Assert.assertNotNull(obj)
        b = obj as Boolean_
        Assert.assertEquals(true, b.value)

        obj = testEval("1+1*3 > 2")
        Assert.assertNotNull(obj)
        b = obj as Boolean_
        Assert.assertEquals(true, b.value)
    }

    @Test
    fun testIfElseExpression() {
        val tests = listOf(
            Pair("if(true) {10}", 10),
            Pair("if(false) {10}", null),
            Pair("if(1) {10}", 10),
            Pair("if(1<2) {10}", 10),
            Pair("if(1<2) {10} else {20}", 10),
            Pair("if(1>2) {10} else {20}", 20),
        )

        for (test in tests) {
            val obj = testEval(test.first)
            val integer = if(obj is Null) null else (obj as Integer).value
            Assert.assertEquals(integer, test.second,  "${test.first} != ${test.second}")
        }
    }

    @Test
    fun testReturnStatement() {
        val tests = listOf(
            Pair("return 10;", 10),
            Pair("return 10; 9;", 10),
            Pair("9; return 10; 20;", 10),
            Pair("""
                 if (10 > 1) {
                   if (10 > 1) {
                     return 10;
                   }
                   return 1;
                 }
            """.trimIndent(), 10)
        )

        for (test in tests) {
            val obj = testEval(test.first)
            val integer = obj as Integer
            Assert.assertEquals(integer.value, test.second,  "${test.first} != ${test.second}")
        }
    }


    @Test
    fun testErrorHandling() {
        val tests = listOf(
            "5+true;",
            "5+true;5",
            "-true",
            "true+false",
            "if(10) {true+false;}"
        )
        for (test in tests) {
            val obj = testEval(test)
            val err = obj as Error_?
            Assert.assertNotNull(err)
        }
    }

    @Test
    fun testLetStatement() {
        val tests = listOf(
            Pair("let a = 5; a;", 5),
            Pair("let a = 5*5; a;", 25),
            Pair("let a = 5; let b = a; let c = a + b + 5; c;", 15),
        )
        for (test in tests) {
            val obj = testEval(test.first)
            val n = obj as Integer
            Assert.assertEquals(n.value, test.second)
        }
    }

    @Test
    fun testFunctionApplication() {
        val tests = listOf(
            Pair("let identity = fn(x) { x; }; identity(5);", 5),
            Pair("let identity = fn(x) { return x; }; identity(5);", 5),
            Pair("let double = fn(x) { x * 2; }; double(5);", 10),
            Pair("let add = fn(x, y) { x + y; }; add(5, 5);", 10),
            Pair("let add = fn(x, y) { x + y; }; add(5 + 5, add(5, 5));", 20),
            Pair("fn(x) { x; }(5)", 5),
        )
        for (test in tests) {
            println("test: ${test.first}")
            val obj = testEval(test.first)
            val n = obj as Integer
            Assert.assertEquals(n.value, test.second)
        }
    }

    fun testEval(input: String): Object_? {
        val parser = Parser.new(Lexer.new(input))
        val program = parser.parseProgram()
        return eval(program, Environment())
    }
}


// evaluator/evaluator_test.go

//func TestFunctionApplication(t *testing.T) {
//    tests := []struct {
//        input    string
//                expected int64
//    }{
//        {"let identity = fn(x) { x; }; identity(5);", 5},
//        {"let identity = fn(x) { return x; }; identity(5);", 5},
//        {"let double = fn(x) { x * 2; }; double(5);", 10},
//        {"let add = fn(x, y) { x + y; }; add(5, 5);", 10},
//        {"let add = fn(x, y) { x + y; }; add(5 + 5, add(5, 5));", 20},
//        {"fn(x) { x; }(5)", 5},
//    }
//
//    for _, tt := range tests {
//        testIntegerObject(t, testEval(tt.input), tt.expected)
//    }
//}