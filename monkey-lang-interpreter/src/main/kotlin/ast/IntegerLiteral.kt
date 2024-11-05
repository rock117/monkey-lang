package ast

import token.Token

data class IntegerLiteral(val token: Token, val value: Int?): Expression {
    override fun expressionNode() {
    }

    override fun tokenLiteral(): String {
        return token.literal
    }

    override fun string(): String {
        return token.literal
    }
}