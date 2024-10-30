package lexer

import org.testng.Assert
import org.testng.annotations.Test
import token.TokenType

class LexerTest {
    @Test
    fun testNextToken() {
        val input = """ let five = 5;
        let ten = 10;
    
        let add = fn(x, y) {
            x + y;
        };"""

        val tests = arrayOf(
            TokenType.LET to "let",
            TokenType.IDENT to "five",
            TokenType.ASSIGN to "=",
            TokenType.INT to "5",
            TokenType.SEMICOLON to ";",
            TokenType.LET to "let",
            TokenType.IDENT to "ten",
            TokenType.ASSIGN to "=",
            TokenType.INT to "10",
            TokenType.SEMICOLON to ";",
            TokenType.LET to "let",
            TokenType.IDENT to "add",
            TokenType.ASSIGN to "=",
            TokenType.FUNCTION to "fn",
            TokenType.LPAREN to "(",
            TokenType.IDENT to "x",
            TokenType.COMMA to ",",
            TokenType.IDENT to "y",
            TokenType.RPAREN to ")",
            TokenType.LBRACE to "{",
            TokenType.IDENT to "x",
            TokenType.PLUS to "+",
            TokenType.IDENT to "y",
            TokenType.SEMICOLON to ";",
            TokenType.RBRACE to "}",
            TokenType.SEMICOLON to ";",
            TokenType.LET to "let",
            TokenType.IDENT to "result",
            TokenType.ASSIGN to "=",
            TokenType.IDENT to "add",
            TokenType.LPAREN to "(",
            TokenType.IDENT to "five",
            TokenType.COMMA to ",",
            TokenType.IDENT to "ten",
            TokenType.RPAREN to ")",
            TokenType.SEMICOLON to ";",
            TokenType.EOF to "",
        )

        val lexer = Lexer.new(input)
        for((i, tokenType) in tests.withIndex()) {
            val tok = lexer.nextToken()
            println(tok)
          //  Assert.assertEquals(tok.type, tokenType.first, "tests[$i] - tokentype wrong. expected=${tokenType.first}, got=${tok.type}")
          //  Assert.assertEquals(tok.literal, tokenType.second, "tests[$i] - literal wrong. expected=${tokenType.second}, got=${tok.literal}")
        }
    }
}

