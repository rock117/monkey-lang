package evaluator

import Object_
import lexer.Lexer
import `object`.Environment
import `object`.Quote
import org.testng.Assert
import org.testng.annotations.Test
import parser.Parser

class QuoteUnQuoteTest {

    @Test
    fun testQuoteUnQuote(){
        val tests = mapOf(
            "quote(unquote(4))" to "4",
            "quote(unquote(4 + 4))" to "8",
            "quote(8 + unquote(4 + 4))" to "(8 + 8)",
            "quote(unquote(4 + 4) + 8)" to "(8 + 8)"
        )

        for (test in tests) {
            val obj = testEval(test.key)
            val i = obj as Quote
            println("${test.key} == ${test.value}")
            Assert.assertEquals(i?.node?.string(), test.value)
        }
    }


    fun testEval(input: String): Object_? {
        val parser = Parser.new(Lexer.new(input))
        val program = parser.parseProgram()
        return eval(program, Environment())
    }
}

// evaluator/quote_unquote_test.go
//
//func TestQuoteUnquote(t *testing.T) {
//    tests := []struct {
//        input    string
//                expected string
//    }{
//        {
//                `quote(unquote(4))`,
//                `4`,
//        },
//        {
//                `quote(unquote(4 + 4))`,
//                `8`,
//        },
//        {
//                `quote(8 + unquote(4 + 4))`,
//                `(8 + 8)`,
//        },
//        {
//                `quote(unquote(4 + 4) + 8)`,
//                `(8 + 8)`,
//        },
//    }
//
//    for _, tt := range tests {
//        evaluated := testEval(tt.input)
//        quote, ok := evaluated.(*object.Quote)
//        if !ok {
//            t.Fatalf("expected *object.Quote. got=%T (%+v)",
//                evaluated, evaluated)
//        }
//
//        if quote.Node == nil {
//            t.Fatalf("quote.Node is nil")
//        }
//
//        if quote.Node.String() != tt.expected {
//            t.Errorf("not equal. got=%q, want=%q",
//                quote.Node.String(), tt.expected)
//        }
//    }
//}