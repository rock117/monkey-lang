package ast

import token.Token

/**
 * @param token return token
 * @param returnValue
 */
class ReturnStatement(val token: Token, var returnValue: Expression?): Statement {
    override fun statementNode() {
    }

    override fun tokenLiteral(): String {
        return this.token.literal
    }
}