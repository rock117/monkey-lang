package parser

import ast.ExpressionStatement
import ast.Identifier
import ast.Program
import lexer.Lexer
import org.testng.Assert
import org.testng.annotations.Test

class ParserTest {

    @Test
    fun testIdentifierExpression() {
        val input = "foobar"
        val lexer = Lexer.new(input)
        val parser = Parser.new(lexer)
        val program = parser.parseProgram()
        val statements = program.statements
        Assert.assertEquals(1, statements.size, "statements size not 1")
        Assert.assertTrue(statements[0] is ExpressionStatement, "program.statements[0] is not a ExpressionStatement")
        val stmt: ExpressionStatement = statements[0] as ExpressionStatement
        val ident = stmt.expression as Identifier
        Assert.assertEquals(input, ident.value)
    }

    @Test
    fun testParsingPrefixExpression() {
        val input = "5+3;"
        val parser = Parser.new(Lexer.new(input))
        val program = parser.parseProgram()
        val statements = program.statements
        statements.forEach { println(it) }
    }
}