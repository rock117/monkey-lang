package ast

import org.testng.Assert
import org.testng.annotations.Test
import token.Token
import token.TokenType
import kotlin.time.measureTime

class ModifyTest {
    @Test
    fun testModify() {
        val one: () -> Expression = { -> IntegerLiteral(token = Token(TokenType.INT, "1"), 1) }
        val two: () -> Expression = { -> IntegerLiteral(token = Token(TokenType.INT, "2"), 2) }
        val turnOneInTwo: (Node) -> Node = { node ->
            if(node is IntegerLiteral) {
                if(node.value == 1) {
                    node.value = 2
                }
            } else {
                node
            }
            node
        }

        val tests = mapOf(
            one() to two(),
            Program(mutableListOf(ExpressionStatement(Token(TokenType.EOF, ""), one()))) to  Program(mutableListOf(ExpressionStatement(Token(TokenType.EOF, ""), two()))),
            InfixExpression(Token(TokenType.EOF, ""), one(), Operator.`+`, two()) to InfixExpression(Token(TokenType.EOF, ""), two(), Operator.`+`, two()),
            InfixExpression(Token(TokenType.EOF, ""), two(), Operator.`+`, one()) to InfixExpression(Token(TokenType.EOF, ""), two(), Operator.`+`, two()),
            PrefixExpression(Token(TokenType.EOF, ""), Operator.`-`, one()) to  PrefixExpression(Token(TokenType.EOF, ""), Operator.`-`, two()),
            IndexExpression(Token(TokenType.EOF, ""), one(), one()) to  IndexExpression(Token(TokenType.EOF, ""), two(), two()),
            IfExpression(Token(TokenType.IF, "if"), one(), BlockStatement(Token(TokenType.LBRACE, "{"), mutableListOf(ExpressionStatement(Token(TokenType.EOF, ""), one()))), BlockStatement(Token(TokenType.LBRACE, "{"), mutableListOf(ExpressionStatement(Token(TokenType.EOF, ""), one())))) to
            IfExpression(Token(TokenType.IF, "if"), two(), BlockStatement(Token(TokenType.LBRACE, "{"), mutableListOf(ExpressionStatement(Token(TokenType.EOF, ""), two()))), BlockStatement(Token(TokenType.LBRACE, "{"), mutableListOf(ExpressionStatement(Token(TokenType.EOF, ""), two())))),
            ReturnStatement(Token(TokenType.RETURN, "return"), one()) to ReturnStatement(Token(TokenType.RETURN, "return"), two()),
            LetStatement(Token(TokenType.LET, "let"), Identifier(Token(TokenType.IDENT, "id"), "id"), one()) to  LetStatement(Token(TokenType.LET, "let"), Identifier(Token(TokenType.IDENT, "id"), "id"), two()),
            FunctionLiteral(Token(TokenType.FUNCTION, "fn"), mutableListOf<Identifier>(), BlockStatement(Token(TokenType.LBRACE, "{"), mutableListOf(ExpressionStatement(Token(TokenType.EOF, ""), one() )))) to
            FunctionLiteral(Token(TokenType.FUNCTION, "fn"), mutableListOf<Identifier>(), BlockStatement(Token(TokenType.LBRACE, "{"), mutableListOf(ExpressionStatement(Token(TokenType.EOF, ""), two() )))),
            ArrayLiteral(Token(TokenType.LBRACKET, "["), mutableListOf(one())) to  ArrayLiteral(Token(TokenType.LBRACKET,"["), mutableListOf(two())),
          // TODO  HashLiteral(Token(TokenType.LBRACE, "{"), mutableMapOf(one() to one()) ) to  HashLiteral(Token(TokenType.LBRACE, "{"), mutableMapOf(two() to two()) )
        )
        for(tt in tests) {
            println("test case: ${tt.key.string()} == ${tt.value.string()}")
            val modified = modify(tt.key, turnOneInTwo)
            if(modified != tt.value) {
                println("!!! ${modified} != \n!!! ${tt.value}")
            }
            Assert.assertEquals(modified, tt.value )
        }
    }


}


// ast/modify_test.go
//
//func TestModify(t *testing.T) {
//// [...]
//
//    tests := []struct {
//        input    Node
//                expected Node
//    }{
//// [...]
//        {
//            &InfixExpression{Left: one(), Operator: "+", Right: two()},
//            &InfixExpression{Left: two(), Operator: "+", Right: two()},
//        },
//        {
//            &InfixExpression{Left: two(), Operator: "+", Right: one()},
//            &InfixExpression{Left: two(), Operator: "+", Right: two()},
//        },
//    }
//
//// [...]
//}