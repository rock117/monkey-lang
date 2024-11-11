package ast

import token.Token

/**
 * @param token token: if
 */
data class IfExpression(val token: Token, var condition: Expression, var consequence: BlockStatement, var alternative: BlockStatement? = null):Expression {
    override fun expressionNode() {
    }

    override fun tokenLiteral(): String {
        return token.literal
    }

    override fun string(): String {
        val elseStr = alternative?.string() ?: ""
        return "if${condition.string()} $elseStr"
    }

    override fun hashCode(): Int {
        return token.type.hashCode() + condition.hashCode() + consequence.hashCode() + alternative.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if(other is IfExpression) {
            return token.type == other.token.type && condition == other.condition && consequence == other.consequence && alternative == other.alternative
        }
        return false
    }
}