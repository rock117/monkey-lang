package ast

import token.Token

/**
 * @param token token.ident
 * @param value
 */
data class Identifier(val token: Token, val value: String): Expression {
    override fun expressionNode() {
    }

    override fun tokenLiteral(): String {
        return this.token.literal
    }

    override fun string(): String {
        return value
    }

    override fun hashCode(): Int {
        return token.hashCode() + value.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if(other is Identifier) {
            return token == other.token && value == other.value
        }
        return false
    }
}