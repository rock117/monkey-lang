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
    fun testParsingHashLiteral() {
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
        val input = """ {"one": 0 + 1, "two": 10 - 8, "three": 15 / 5} """
        val parser = Parser.new(Lexer.new(input))
        val program = parser.parseProgram()
        val expStatement = program.statements[0] as ExpressionStatement
        val literal = expStatement.expression as HashLiteral
        Assert.assertEquals(literal.pairs.size, 3)
    }
}
// parser/parser_test.go
//
//func TestParsingHashLiteralsWithExpressions(t *testing.T) {
//    input := `{"one": 0 + 1, "two": 10 - 8, "three": 15 / 5}`
//
//    l := lexer.New(input)
//    p := New(l)
//    program := p.ParseProgram()
//    checkParserErrors(t, p)
//
//    stmt := program.Statements[0].(*ast.ExpressionStatement)
//    hash, ok := stmt.Expression.(*ast.HashLiteral)
//    if !ok {
//        t.Fatalf("exp is not ast.HashLiteral. got=%T", stmt.Expression)
//    }
//
//    if len(hash.Pairs) != 3 {
//        t.Errorf("hash.Pairs has wrong length. got=%d", len(hash.Pairs))
//    }
//
//    tests := map[string]func(ast.Expression){
//        "one": func(e ast.Expression) {
//        testInfixExpression(t, e, 0, "+", 1)
//    },
//        "two": func(e ast.Expression) {
//        testInfixExpression(t, e, 10, "-", 8)
//    },
//        "three": func(e ast.Expression) {
//        testInfixExpression(t, e, 15, "/", 5)
//    },
//    }
//
//    for key, value := range hash.Pairs {
//        literal, ok := key.(*ast.StringLiteral)
//        if !ok {
//            t.Errorf("key is not ast.StringLiteral. got=%T", key)
//            continue
//        }
//
//        testFunc, ok := tests[literal.String()]
//        if !ok {
//            t.Errorf("No test function for key %q found", literal.String())
//            continue
//        }
//
//        testFunc(value)
//    }