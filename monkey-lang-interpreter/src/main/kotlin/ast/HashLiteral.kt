package ast

import token.Token

data class HashLiteral(val token: Token, val pairs: Map<Expression, Expression>): Expression {
    override fun expressionNode() {
    }

    override fun tokenLiteral(): String {
        return token.literal
    }

    override fun string(): String {
        val pairs = this.pairs.map { it -> "${it.key.string()}:${it.key.string()}" }.joinToString(", ")
        return "{$pairs}"
    }
}