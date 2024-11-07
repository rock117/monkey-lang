package ast

import token.Token

/**
 * @param token token.let
 * @param name
 * @param value
 *
 */
data class LetStatement(val token: Token, var name: Identifier, var value: Expression): Statement {
    override fun statementNode() {
    }

    override fun tokenLiteral(): String {
        return this.token.literal
    }

    override fun string(): String {
        val valueStr = if(this.value != null) this.value.string() else ""
        return "${tokenLiteral()} ${name.string()} = $valueStr;"
    }
}

