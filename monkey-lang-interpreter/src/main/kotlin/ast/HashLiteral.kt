package ast

import token.Token

data class HashLiteral(val token: Token, val pairs: MutableMap<Expression, Expression>): Expression {
    override fun expressionNode() {
    }

    override fun tokenLiteral(): String {
        return token.literal
    }

    override fun string(): String {
        val pairs = this.pairs.map { it -> "${it.key.string()}:${it.key.string()}" }.joinToString(", ")
        return "{$pairs}"
    }

    override fun hashCode(): Int {
        return token.type.hashCode() + pairs.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if(other is HashLiteral) {
            return token.type == other.token.type && pairs == other.pairs
        }
        return false
    }
}