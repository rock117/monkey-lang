package ast

import token.Token

/**
 * expression statement, such as `x + 10;`
 * @param token token x
 * @param expression
 */
data class ExpressionStatement(val token: Token, var expression: Expression? = null): Statement {
    override fun statementNode() {
    }

    override fun tokenLiteral(): String {
        return this.token.literal
    }

    override fun string(): String {
        return if(this.expression != null) this.expression!!.string() else ""
    }
}