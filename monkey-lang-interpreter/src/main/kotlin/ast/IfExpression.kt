package ast

import token.Token

/**
 * @param token token: if
 */
data class IfExpression(val token: Token, val condition: Expression, val consequence: BlockStatement, val alternative: BlockStatement? = null):Expression {
    override fun expressionNode() {
    }

    override fun tokenLiteral(): String {
        return token.literal
    }

    override fun string(): String {
        val elseStr = alternative?.string() ?: ""
        return "if${condition.string()} $elseStr"
    }
}