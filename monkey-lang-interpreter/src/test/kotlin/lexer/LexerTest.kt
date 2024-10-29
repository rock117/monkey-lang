package lexer

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
        for(tokenType in tests) {
            val tok = lexer.nextToken()
            println()
        }
    }
}


// lexer/lexer_test.go
//
//func TestNextToken(t *testing.T) {
//    input :=`let five = 5;
//    let ten = 10;
//
//    let add = fn(x, y) {
//        x + y;
//    };
//
//    let result = add(five, ten);
//    `
//
//    tests := []struct {
//        expectedType    token.TokenType
//                expectedLiteral string
//    }{
//         TokenType.LET, "let",
//         TokenType.IDENT, "five",
//         TokenType.ASSIGN, "=",
//         TokenType.INT, "5",
//         TokenType.SEMICOLON, ";",
//         TokenType.LET, "let",
//         TokenType.IDENT, "ten",
//         TokenType.ASSIGN, "=",
//         TokenType.INT, "10",
//         TokenType.SEMICOLON, ";",
//         TokenType.LET, "let",
//         TokenType.IDENT, "add",
//         TokenType.ASSIGN, "=",
//         TokenType.FUNCTION, "fn",
//         TokenType.LPAREN, "(",
//         TokenType.IDENT, "x",
//         TokenType.COMMA, ",",
//         TokenType.IDENT, "y",
//         TokenType.RPAREN, ")",
//         TokenType.LBRACE, "{",
//         TokenType.IDENT, "x",
//         TokenType.PLUS, "+",
//         TokenType.IDENT, "y",
//         TokenType.SEMICOLON, ";",
//         TokenType.RBRACE, "}",
//         TokenType.SEMICOLON, ";",
//         TokenType.LET, "let",
//         TokenType.IDENT, "result",
//         TokenType.ASSIGN, "=",
//         TokenType.IDENT, "add",
//         TokenType.LPAREN, "(",
//         TokenType.IDENT, "five",
//         TokenType.COMMA, ",",
//         TokenType.IDENT, "ten",
//         TokenType.RPAREN, ")",
//         TokenType.SEMICOLON, ";",
//         TokenType.EOF, "",
//    }
//// [...]
//}