package ast

import token.Token

data class InfixExpression(val token: Token, val left: Expression, val operator: String, val right: Expression): Expression {
    override fun expressionNode() {

    }

    override fun tokenLiteral(): String {
        return token.literal
    }

    override fun string(): String {
        return "(${this.left.string()} $operator ${this.right.string()})"
    }
}