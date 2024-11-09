package ast

import token.Token

/**
 * @param token token '['
 * @param name such as a in a[123]
 * @param index such as 123 in a[123] or index in a[index]
 */
data class IndexExpression(val token: Token, val left: Expression, val index: Expression): Expression {
    override fun expressionNode() {
    }

    override fun tokenLiteral(): String {
       return token.literal
    }

    override fun string(): String {
        return "(${left.string()}[${index.string()}])"
    }
}