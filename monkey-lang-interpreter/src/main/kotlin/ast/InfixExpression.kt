package ast

import token.Token

data class InfixExpression(val token: Token, var left: Expression, val operator: Operator, var right: Expression): Expression {
    override fun expressionNode() {

    }

    override fun tokenLiteral(): String {
        return token.literal
    }

    override fun string(): String {
        return "(${this.left.string()} $operator ${this.right.string()})"
    }

    override fun hashCode(): Int {
        return left.hashCode() + operator.hashCode() + right.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if(other is InfixExpression) {
            return left == other.left && operator == other.operator && right == other.right
        }
        return false
    }
}