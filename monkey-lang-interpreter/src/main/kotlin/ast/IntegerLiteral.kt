package ast

import token.Token

data class IntegerLiteral(val token: Token, var value: Int?): Expression {
    override fun expressionNode() {
    }

    override fun tokenLiteral(): String {
        return token.literal
    }

    override fun string(): String {
        return token.literal
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if(other is IntegerLiteral) {
            return value == other.value
        }
        return false
    }
}