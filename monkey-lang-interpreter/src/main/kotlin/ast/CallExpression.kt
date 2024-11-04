package ast

import token.Token

/**
 * @param token token '('
 * @param function identifier or function literal
 */
data class CallExpression(val token: Token, val function: Expression, val arguments: MutableList<Expression>): Expression {
    override fun expressionNode() {
    }

    override fun tokenLiteral(): String {
        return token.literal
    }

    override fun string(): String {
        val args = arguments.map { it.string() }.joinToString(", ")
        return "${function.string()}($args)"
    }
}