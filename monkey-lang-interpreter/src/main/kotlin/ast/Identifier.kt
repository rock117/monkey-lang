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

}