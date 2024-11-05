package ast

import token.Token

data class PrefixExpression(val token: Token, val operator: String, var right: Expression? = null): Expression {
    override fun expressionNode() {
    }

    override fun tokenLiteral(): String {
        return "(${operator}${right?.string()})"
    }

    override fun string(): String {
        return token.literal
    }
}