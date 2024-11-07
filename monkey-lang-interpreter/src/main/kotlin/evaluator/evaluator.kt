package evaluator

import Object_
import ast.*
import ast.Boolean_
import `object`.*
import `object`.Boolean_.Companion.TRUE
import `object`.Boolean_.Companion.FALSE
import `object`.Function

fun eval(node: Node?, env: Environment): Object_? {
    return when (node) {
        is Program -> evalProgram(node, env)
        is ExpressionStatement -> eval(node.expression, env)
        is IntegerLiteral -> Integer(node.value!!)
        is Boolean_ -> if (node.value) TRUE else FALSE
        is PrefixExpression -> {
            val right = eval(node.right, env)
            if(isError(right)) right else evalPrefixExpression(node.operator, right)
        }
        is InfixExpression -> {
            val left = eval(node.left, env)
            if(isError(left)) {
                return left
            }
            val right = eval(node.right, env)
            if(isError(right)) {
                return right
            }
            evalInfixExpression(node.operator, left, right)
        }
        is BlockStatement -> evalBlockStatement(node, env)
        is IfExpression -> evalIfExpression(node, env)
        is ReturnStatement -> {
            val value = eval(node.returnValue, env)
            if(isError(value)) value else ReturnValue(value!!)
        }
        is LetStatement -> {
            val value = eval(node.value, env)!!
            if(isError(value)) {
                return value
            }
            env.set(node?.name?.value!!, value)
            value
        }
        is Identifier -> evalIdentifier(node, env)
        is FunctionLiteral -> Function(parameters = node.parameters, body = node.body, env)
        is CallExpression -> {
            val function = eval(node.function, env)
            if(isError(function)) {
                return function
            }
            val args = evalExpressions(node.arguments, env)
            if(args.size == 1 && isError(args[0])) {
                return args[0]
            }
            return applyFunction(function, args)
        }
        else -> null
    }
}

fun applyFunction(function: Object_?, args: List<Object_?>): Object_? {
    if(function !is Function) {
        return newError("not a function: ${function?.type()}")
    }
    val extendedEnv = extendFunctionEnv(function, args)
    val evaluated = eval(function.body, extendedEnv)
    return unwrapReturnValue(evaluated)
}

fun unwrapReturnValue(obj: Object_?): Object_? {
    return if (obj is ReturnValue) obj.value else obj
}

fun extendFunctionEnv(fn: Function, args: List<Object_?>): Environment {
    val env = Environment.newEnclosingEnvironment(fn.env)
    fn.parameters.forEachIndexed { index, param ->
        env.set(param.value, args[index]!!)
    }
    return env
}

private fun evalExpressions(exps: List<Expression>, env: Environment): List<Object_?> {
    val result = mutableListOf<Object_?>()
    for(exp in exps) {
        val evaluated = eval(exp, env)
        if(isError(evaluated)) {
            return listOf(evaluated)
        }
        result.add(evaluated)
    }
    return result
}

private fun evalIdentifier(node: Identifier, env: Environment): Object_ {
    val value = env.get(node.value)
    if(value == null) {
        return newError("Identifier not found: ${node.value}")
    } else  {
        return value
    }
}

private fun evalBlockStatement(node: BlockStatement, env: Environment): Object_? {
    var result: Object_? = null
    for (statement in node.statements) {
        result = eval(statement, env)
        if(result is ReturnValue || result is Error_) {
            return result
        }
    }
    return result
}

private fun evalProgram(program: Program, env: Environment): Object_? {
    var result: Object_? = null
    for (statement in program.statements) {
        result = eval(statement, env)
        if(result is ReturnValue) {
            return result.value
        } else if(result is Error_) {
            return result
        }
    }
    return result
}

private fun evalIfExpression(node: IfExpression, env: Environment): Object_? {
    val condition = eval(node.condition, env)
    if(isError(condition)) {
        return condition
    }
    return if(isTruety(condition!!)) {
        eval(node.consequence, env)
    } else if(node.alternative != null) {
        eval(node.alternative!!, env)
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

private fun evalStatements(statements: List<Statement>, env: Environment): Object_? {
    var result: Object_? = null
    for (statement in statements) {
        result = eval(statement, env)
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