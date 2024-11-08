package ast

import token.Token

data class StringLiteral(val token: Token, val value: String): Expression {
    override fun expressionNode() {
    }

    override fun tokenLiteral(): String {
        return token.literal
    }

    override fun string(): String {
        return token.literal
    }
}