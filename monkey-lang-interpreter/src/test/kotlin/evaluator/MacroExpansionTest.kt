package evaluator

import Object_
import ast.Program
import lexer.Lexer
import `object`.Environment
import org.testng.Assert
import org.testng.annotations.Test
import parser.Parser

class MacroExpansionTest {
    @Test
    fun testMacroExpansion() {
        val tests = mapOf(
            """ let infixExpression = macro() { quote(1 + 2); };  infixExpression(); """ to "(1 + 2)",
            " let reverse = macro(a, b) { quote(unquote(b) - unquote(a)); };  reverse(2 + 2, 10 - 5); " to "(10 - 5) - (2 + 2)",
        )

        for (test in tests) {
            val expected = parseProgram(test.value) // TODO
            val program = parseProgram(test.key)
            val env = Environment()
            defineMacros(program, env)
            val expand = expandMacros(program, env)
            Assert.assertEquals(expand, expected)
        }
    }


    fun parseProgram(input: String): Program {
        val parser = Parser.new(Lexer.new(input))
        val program = parser.parseProgram()
        return program
    }
    fun testEval(input: String): Object_? {
        val parser = Parser.new(Lexer.new(input))
        val program = parser.parseProgram()
        return eval(program, Environment())
    }
}


// evaluator/macro_expansion_test.go
//
//func TestExpandMacros(t *testing.T) {
//    tests := []struct {
//        input    string
//                expected string
//    }{
//        {
//            `
//            let infixExpression = macro() { quote(1 + 2); };
//
//            infixExpression();
//            `,
//            `(1 + 2)`,
//        },
//        {
//            `
//            let reverse = macro(a, b) { quote(unquote(b) - unquote(a)); };
//
//            reverse(2 + 2, 10 - 5);
//            `,
//            `(10 - 5) - (2 + 2)`,
//        },
//    }
//
//    for _, tt := range tests {
//        expected := testParseProgram(tt.expected)
//        program := testParseProgram(tt.input)
//
//        env := object.NewEnvironment()
//        DefineMacros(program, env)
//        expanded := ExpandMacros(program, env)
//
//        if expanded.String() != expected.String() {
//            t.Errorf("not equal. want=%q, got=%q",
//                expected.String(), expanded.String())
//        }
//    }
//}