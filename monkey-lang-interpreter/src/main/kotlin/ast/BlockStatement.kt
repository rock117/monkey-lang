package ast

import token.Token

/**
 * @param token token: '{'
 */
data class BlockStatement(val token: Token, val statements: MutableList<Statement> = mutableListOf()): Statement {
    override fun statementNode() {
    }

    override fun tokenLiteral(): String {
        return token.literal
    }

    override fun string(): String {
        return statements.joinToString("")
    }
}