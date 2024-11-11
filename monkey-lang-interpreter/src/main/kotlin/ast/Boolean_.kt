package ast

import `object`.Boolean_
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

    override fun equals(other: Any?): Boolean {
        if(other is Boolean_) {
            return value == other.value
        }
        return false
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }
}