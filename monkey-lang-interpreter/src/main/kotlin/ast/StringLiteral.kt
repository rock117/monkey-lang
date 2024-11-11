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

    override fun hashCode(): Int {
        return value.hashCode() + token.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if(other is StringLiteral) {
            return token == other.token && value == other.value
        }
        return false
    }
}