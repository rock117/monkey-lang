package ast

import token.Token

data class ArrayLiteral(val token: Token, val elements: List<Expression>): Expression {
    override fun expressionNode() {

    }

    override fun tokenLiteral(): String {
        return token.literal
    }

    override fun string(): String {
        val elements = this.elements.map { it.string() }.joinToString(", ")
        return "[$elements]"
    }
}