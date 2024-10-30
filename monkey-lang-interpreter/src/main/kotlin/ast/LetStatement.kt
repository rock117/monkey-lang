package ast

import token.Token

/**
 * @param token token.let
 * @param name
 * @param value
 *
 */
class LetStatement(val token: Token, var name: Identifier?, var value: Expression?): Statement {
    override fun statementNode() {
    }

    override fun tokenLiteral(): String {
        return this.token.literal
    }
}

