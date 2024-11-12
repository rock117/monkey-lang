package ast

import token.Token

data class MacroLiteral(val token: Token, val parameters: MutableList<Identifier>, val body: BlockStatement): Expression {
    override fun expressionNode() {
    }

    override fun tokenLiteral(): String {
        return token.literal
    }

    override fun string(): String {
        val params = parameters.joinToString(", ") { it.string() }
        return "${tokenLiteral()}($params)${body.string()}"
    }

    override fun hashCode(): Int {
        return token.type.hashCode() + parameters.hashCode() + body.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if(other is MacroLiteral) {
            return token.type == other.token.type && parameters == other.parameters && body == other.body
        }
        return false
    }
}