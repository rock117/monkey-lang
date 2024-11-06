package evaluator

import Object_
import ast.*
import ast.Boolean_
import `object`.*
import `object`.Boolean_.Companion.TRUE
import `object`.Boolean_.Companion.FALSE

fun eval(node: Node?): Object_? {
    return when (node) {
        is Program -> evalProgram(node)
        is ExpressionStatement -> eval(node.expression)
        is IntegerLiteral -> Integer(node.value!!)
        is Boolean_ -> if (node.value) TRUE else FALSE
        is PrefixExpression -> {
            val right = eval(node.right)
            if(isError(right)) right else evalPrefixExpression(node.operator, right)
        }
        is InfixExpression -> {
            val left = eval(node.left)
            if(isError(left)) {
                return left
            }
            val right = eval(node.right)
            if(isError(right)) {
                return right
            }
            evalInfixExpression(node.operator, left, right)
        }
        is BlockStatement -> evalBlockStatement(node)
        is IfExpression -> evalIfExpression(node)
        is ReturnStatement -> {
            val value = eval(node.returnValue)
            if(isError(value)) value else ReturnValue(value!!)
        }
        else -> null
    }
}

private fun evalBlockStatement(node: BlockStatement): Object_? {
    var result: Object_? = null
    for (statement in node.statements) {
        result = eval(statement)
        if(result is ReturnValue || result is Error_) {
            return result
        }
    }
    return result
}

private fun evalProgram(program: Program): Object_? {
    var result: Object_? = null
    for (statement in program.statements) {
        result = eval(statement)
        if(result is ReturnValue) {
            return result.value
        } else if(result is Error_) {
            return result
        }
    }
    return result
}

private fun evalIfExpression(node: IfExpression): Object_? {
    val condition = eval(node.condition)
    if(isError(condition)) {
        return condition
    }
    return if(isTruety(condition!!)) {
        eval(node.consequence)
    } else if(node.alternative != null) {
        eval(node.alternative!!)
    } else {
        Null
    }
}

private fun isTruety(obj: Object_): Boolean {
    return when(obj) {
        Null -> false
        TRUE -> true
        FALSE -> false
        else -> true
    }
}

private fun evalInfixExpression(operator: String, left: Object_?, right: Object_?): Object_? {
    return if (left?.type() == ObjectType.INTEGER && right?.type() == ObjectType.INTEGER) {
        evalIntegerInfixExpression(operator, left as Integer, right as Integer)
    } else if (left?.type() !=  right?.type() ) {
        newError("type mismatch: ${left?.type()} $operator ${right?.type()}")
    }  else if (operator == "==") {
        `object`.Boolean_(left == right)
    } else if (operator == "!=") {
        `object`.Boolean_(left != right)
    }  else {
        newError("unknown operator: ${left?.type()} $operator ${right?.type()}")
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
        else -> newError("unknown operator: ${left?.type()} $operator ${right?.type()}")
    }
}

private fun evalStatements(statements: List<Statement>): Object_? {
    var result: Object_? = null
    for (statement in statements) {
        result = eval(statement)
        if(result is ReturnValue) {
            return result.value
        }
    }
    return result
}


private fun evalPrefixExpression(operator: String, right: Object_?): Object_ {
    return when (operator) {
        "!" -> evalBangOperatorExpression(right)
        "-" -> evalMinusPrefixOperatorExpression(right)
        else -> newError("unknown operator: $operator ${right?.type()}")
    }
}

fun evalMinusPrefixOperatorExpression(right: Object_?): Object_ {
    if (right?.type() != ObjectType.INTEGER) {
        return newError("unknown operator: -${right?.type()}")
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

private fun newError(msg: String): Error_ {
    return Error_(msg)
}

private fun isError(obj: Object_?): Boolean {
    return  obj is Error_
}