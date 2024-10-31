package ast

import token.Token

class IntegerLiteral(val token: Token, var value: Int? = null): Expression {
    override fun expressionNode() {
    }

    override fun tokenLiteral(): String {
        return token.literal
    }

    override fun string(): String {
        return token.literal
    }
}