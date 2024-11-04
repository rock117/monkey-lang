package ast

import token.Token

data class FunctionLiteral(val token: Token, val parameters: MutableList<Identifier>, val body: BlockStatement): Expression {
    override fun expressionNode() {

    }

    override fun tokenLiteral(): String {
        return token.literal
    }

    override fun string(): String {
        val params = this.parameters.map { it.string() }.joinToString(", ")
        return "${token.literal}($params) ${body.string()}"
    }
}