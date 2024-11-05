package parser

import ast.*
import lexer.Lexer
import org.testng.Assert
import org.testng.annotations.Test
import token.TokenType

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

    @Test
    fun testParseLetStatement() {
        val input = "let a = 5"
        val parser = Parser.new(Lexer.new(input))
        val program = parser.parseProgram()
        val statements = program.statements
        Assert.assertEquals(1, statements.size)
        val letStmt = statements.first() as LetStatement
        Assert.assertEquals("a", letStmt.name!!.value)
        Assert.assertEquals("5", letStmt.value!!.tokenLiteral())
    }

    @Test
    fun testReturnStatement() {
        val input = "return 5"
        val parser = Parser.new(Lexer.new(input))
        val program = parser.parseProgram()
        val statements = program.statements
        Assert.assertEquals(1, statements.size)
        val returnStmt = statements.first() as ReturnStatement
        Assert.assertEquals("5", returnStmt.returnValue?.tokenLiteral())
    }

    @Test
    fun testParseInfixExpression() {
        val input = "1+2"
        val parser = Parser.new(Lexer.new(input))
        val program = parser.parseProgram()
        val statements = program.statements
        Assert.assertEquals(1, statements.size)
        val expStmt = statements.first() as ExpressionStatement
        val infixExp = expStmt.expression as InfixExpression
        Assert.assertEquals("1", infixExp.left?.tokenLiteral())
        Assert.assertEquals("+", infixExp.operator)
        Assert.assertEquals("2", infixExp.right?.tokenLiteral())
    }

    @Test
    fun testParseExpressionStatement() {
        val input = "1"
        val parser = Parser.new(Lexer.new(input))
        val program = parser.parseProgram()
        val statements = program.statements
        Assert.assertEquals(1, statements.size)
        val expStmt = statements.first() as ExpressionStatement
        val intLiteral = expStmt.expression as IntegerLiteral
        Assert.assertEquals(TokenType.INT, intLiteral.token.type)
        Assert.assertEquals(1, intLiteral.value)
    }

    fun testParseBlockStatement() {

    }

    fun testIfBlockStatement() {

    }

    fun testFunctionLiteral() {

    }

    fun testCallExpression() {

    }


    @Test
    fun testParseGroupExpression() {
         // TODO
    }
}