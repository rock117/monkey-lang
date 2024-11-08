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

    @Test
    fun testIfExpression() {
        val input = "if(x < y) { x }"
        val parser = Parser.new(Lexer.new(input))
        val program = parser.parseProgram()
        val statements = program.statements
        Assert.assertEquals(statements.size, 1, "more than 1 statements")
        val expStmt = statements[0] as ExpressionStatement
        Assert.assertNotNull(expStmt)
    }

    @Test
    fun testFunctionLiteralParsing() {
        val input = "fn(x, y) { x + y;}"
        val parser = Parser.new(Lexer.new(input))
        val program = parser.parseProgram()
        val statements = program.statements
        Assert.assertEquals(statements.size, 1, "more than 1 statements")
    }

    @Test
    fun testCallExpressionParsing() {
        val input = "let add = fn(x, y) { x + y;}; add(1, 2);"
        val parser = Parser.new(Lexer.new(input))
        val program = parser.parseProgram()
        val statements = program.statements
        Assert.assertEquals(statements.size, 2, "more than 1 statements")
    }

    @Test
    fun testParsingArrayLiteral() {
        val input = "[1, 2 * 2, 3 + 3]"
        val parser = Parser.new(Lexer.new(input))
        val program = parser.parseProgram()
        val expStatement = program.statements[0] as ExpressionStatement
        val arrayLiteral = expStatement.expression as ArrayLiteral
        Assert.assertEquals(arrayLiteral.elements.size, 3)
        println("array is: ${arrayLiteral.string()}")
    }

    @Test
    fun testParsingIndexExpression() {
        val input = "[1, 2 * 2, 3 + 3]"
        val parser = Parser.new(Lexer.new(input))
        val program = parser.parseProgram()
        val expStatement = program.statements[0] as ExpressionStatement
        val arrayLiteral = expStatement.expression as ArrayLiteral
        Assert.assertEquals(arrayLiteral.elements.size, 3)
        println("array is: ${arrayLiteral.string()}")
    }

    @Test
    fun testParseGroupExpression() {
         // TODO
    }
}

//
//// parser/parser_test.go
//
//func TestParsingArrayLiterals(t *testing.T) {
//    input := "[1, 2 * 2, 3 + 3]"
//
//    l := lexer.New(input)
//    p := New(l)
//    program := p.ParseProgram()
//    checkParserErrors(t, p)
//
//    stmt, ok := program.Statements[0].(*ast.ExpressionStatement)
//    array, ok := stmt.Expression.(*ast.ArrayLiteral)
//    if !ok {
//        t.Fatalf("exp not ast.ArrayLiteral. got=%T", stmt.Expression)
//    }
//
//    if len(array.Elements) != 3 {
//        t.Fatalf("len(array.Elements) not 3. got=%d", len(array.Elements))
//    }
//
//    testIntegerLiteral(t, array.Elements[0], 1)
//    testInfixExpression(t, array.Elements[1], 2, "*", 2)
//    testInfixExpression(t, array.Elements[2], 3, "+", 3)
//}