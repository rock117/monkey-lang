package evaluator

import Object_
import ast.*
import `object`.Integer
import `object`.Null
import `object`.Boolean_.Companion.TRUE
import `object`.Boolean_.Companion.FALSE
import `object`.ObjectType

fun eval(node: Node?): Object_? {
    return when (node) {
        is Program -> evalStatements(node.statements)
        is ExpressionStatement -> eval(node.expression!!)
        is IntegerLiteral -> Integer(node.value!!)
        is Boolean_ -> if (node.value) TRUE else FALSE
        is PrefixExpression -> evalPrefixExpression(node.operator, eval(node.right))
        is InfixExpression -> evalInfixExpression(node.operator, eval(node.left), eval(node.right))
        else -> null
    }
}

private fun evalInfixExpression(operator: String, left: Object_?, right: Object_?): Object_? {
    return if (left?.type() == ObjectType.INTEGER && right?.type() == ObjectType.INTEGER) {
        evalIntegerInfixExpression(operator, left as Integer, right as Integer)
    } else if (operator == "==") {
        `object`.Boolean_(left == right)
    } else if (operator == "!=") {
        `object`.Boolean_(left != right)
    }  else {
        Null
    }
}

fun evalIntegerInfixExpression(operator: String, left: Integer, right: Integer): Object_ {
    return when (operator) {
        "+" -> Integer(left.value + right.value)
        "-" -> Integer(left.value - right.value)
        "*" -> Integer(left.value * right.value)
        "/" -> Integer(left.value / right.value)

        "<" -> `object`.Boolean_(left.value < right.value)
        ">" -> `object`.Boolean_(left.value > right.value)
        "==" -> `object`.Boolean_(left.value == right.value)
        "!=" -> `object`.Boolean_(left.value != right.value)
        else -> Null
    }
}

private fun evalStatements(statements: List<Statement>): Object_? {
    var result: Object_? = null
    for (statement in statements) {
        result = eval(statement)
    }
    return result
}


private fun evalPrefixExpression(operator: String, right: Object_?): Object_ {
    return when (operator) {
        "!" -> evalBangOperatorExpression(right)
        "-" -> evalMinusPrefixOperatorExpression(right)
        else -> Null
    }
}

fun evalMinusPrefixOperatorExpression(right: Object_?): Object_ {
    if (right?.type() != ObjectType.INTEGER) {
        return Null
    }
    val value = (right as Integer).value
    return Integer(-value)
}

private fun evalBangOperatorExpression(right: Object_?): Object_ {
    return when (right) {
        TRUE -> FALSE
        FALSE -> TRUE
        Null -> TRUE
        else -> FALSE
    }
}
