package ast

import token.Token

data class Boolean_(val token: Token, val value: Boolean): Expression {
    override fun expressionNode() {
    }

    override fun tokenLiteral(): String {
        return token.literal
    }

    override fun string(): String {
        return token.literal
    }
}