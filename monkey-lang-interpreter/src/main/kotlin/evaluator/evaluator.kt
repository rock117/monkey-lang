package evaluator

import Object_
import ast.*
import ast.Boolean_
import `object`.*
import `object`.Array
import `object`.Boolean_.Companion.TRUE
import `object`.Boolean_.Companion.FALSE
import `object`.Function
import `object`.BuiltinFns

val builtins = mapOf(
    "len" to BuiltinFns.len(),
    "first" to BuiltinFns.first(),
    "last" to BuiltinFns.last(),
    "rest" to BuiltinFns.rest(),
    "push" to BuiltinFns.push(),
    "puts" to BuiltinFns.put()
)

fun eval(node: Node?, env: Environment): Object_? {
    return when (node) {
        is Program -> evalProgram(node, env)
        is ExpressionStatement -> eval(node.expression, env)
        is IntegerLiteral -> Integer(node.value!!)
        is Boolean_ -> if (node.value) TRUE else FALSE
        is PrefixExpression -> {
            val right = eval(node.right, env)
            if (isError(right)) right else evalPrefixExpression(node.operator, right)
        }

        is InfixExpression -> {
            val left = eval(node.left, env)
            if (isError(left)) {
                return left
            }
            val right = eval(node.right, env)
            if (isError(right)) {
                return right
            }
            evalInfixExpression(node.operator, left, right)
        }

        is BlockStatement -> evalBlockStatement(node, env)
        is IfExpression -> evalIfExpression(node, env)
        is ReturnStatement -> {
            val value = eval(node.returnValue, env)
            if (isError(value)) value else ReturnValue(value!!)
        }

        is LetStatement -> {
            val value = eval(node.value, env)!!
            if (isError(value)) {
                return value
            }
            env.set(node?.name?.value!!, value)
            value
        }

        is Identifier -> evalIdentifier(node, env)
        is FunctionLiteral -> Function(parameters = node.parameters, body = node.body, env)
        is CallExpression -> {
            if(node.function.tokenLiteral() == "quote") {
                return quote(node.arguments[0], env)
            }
            val function = eval(node.function, env)
            if (isError(function)) {
                return function
            }
            val args = evalExpressions(node.arguments, env)
            if (args.size == 1 && isError(args[0])) {
                return args[0]
            }
            return applyFunction(function, args)
        }

        is StringLiteral -> String_(node.value)
        is ArrayLiteral -> {
            val elements = evalExpressions(node.elements, env)
            if(elements.size == 1 && isError(elements[0])) {
                elements[0]
            } else {
                Array(elements)
            }
        }
        is HashLiteral -> evalHashLiteral(node, env)
        is IndexExpression -> {
            val left = eval(node.left, env)
            if(isError(left)) {
                left
            } else {
                val index = eval(node.index, env)
                if(isError(index)) {
                    index
                } else {
                    evalIndexExpression(left, index)
                }
            }
        }
        else -> null
    }
}

private fun quote(expression: Expression): Object_ {
    return Quote(expression)
}

private fun evalHashLiteral(node: HashLiteral, env: Environment): Object_? {
    val pairs = mutableMapOf<Object_, Object_>()
    for(kv in node.pairs){
        val keyNode = kv.key
        val valueNode = kv.value
        val key = eval(keyNode, env)
        if(isError(key)){
            return key
        }
        val value = eval(valueNode, env)
        if(isError(value)){
            return value
        }
        pairs[key!!] = value!!
    }
    return Hash(pairs)
}

private fun evalIndexExpression(left: Object_?, index: Object_?): Object_? {
    return if(left is Array && index is Integer) {
        evalArrayIndexExpression(left, index)
    } else if(left is Hash) {
        evalHashIndexExpression(left, index)
    } else {
        newError("index operator not supported: ${left?.type()}")
    }
}

fun evalHashIndexExpression(hash: Hash, index: Object_?): Object_ {
    if (index == null) {
        return Null
    }
    return hash[index!!] ?: Null
}

private fun evalArrayIndexExpression(arrayObj: Array, index: Integer): Object_? {
    val idx = index.value
    val max = arrayObj.elements.size - 1
    return if(idx < 0 || idx > max) Null else arrayObj.elements[idx]
}

private fun applyFunction(function: Object_?, args: List<Object_?>): Object_? {
    return when (function?.type()) {
        ObjectType.FUNCTION -> {
            val extendedEnv = extendFunctionEnv(function as Function, args)
            val evaluated = eval(function.body, extendedEnv)
            unwrapReturnValue(evaluated)
        }
        ObjectType.BUILTIN -> (function as Builtin).fn(args)
        else -> newError("not a function: ${function?.type()} ")
    }
}

private fun unwrapReturnValue(obj: Object_?): Object_? {
    return if (obj is ReturnValue) obj.value else obj
}

private fun extendFunctionEnv(fn: Function, args: List<Object_?>): Environment {
    val env = Environment.newEnclosingEnvironment(fn.env)
    fn.parameters.forEachIndexed { index, param ->
        env.set(param.value, args[index]!!)
    }
    return env
}

private fun evalExpressions(exps: List<Expression>, env: Environment): List<Object_?> {
    val result = mutableListOf<Object_?>()
    for (exp in exps) {
        val evaluated = eval(exp, env)
        if (isError(evaluated)) {
            return listOf(evaluated)
        }
        result.add(evaluated)
    }
    return result
}

private fun evalIdentifier(node: Identifier, env: Environment): Object_ {
    val value = env.get(node.value)
    if (value != null) {
        return value
    }
    val builtin = builtins[node.value]
    if (builtin == null) {
        return newError("Identifier not found: ${node.value}")
    } else {
        return builtin
    }
}

private fun evalBlockStatement(node: BlockStatement, env: Environment): Object_? {
    var result: Object_? = null
    for (statement in node.statements) {
        result = eval(statement, env)
        if (result is ReturnValue || result is Error_) {
            return result
        }
    }
    return result
}

private fun evalProgram(program: Program, env: Environment): Object_? {
    var result: Object_? = null
    for (statement in program.statements) {
        result = eval(statement, env)
        if (result is ReturnValue) {
            return result.value
        } else if (result is Error_) {
            return result
        }
    }
    return result
}

private fun evalIfExpression(node: IfExpression, env: Environment): Object_? {
    val condition = eval(node.condition, env)
    if (isError(condition)) {
        return condition
    }
    return if (isTruety(condition!!)) {
        eval(node.consequence, env)
    } else if (node.alternative != null) {
        eval(node.alternative!!, env)
    } else {
        Null
    }
}

private fun isTruety(obj: Object_): Boolean {
    return when (obj) {
        Null -> false
        TRUE -> true
        FALSE -> false
        else -> true
    }
}

private fun evalInfixExpression(operator: Operator, left: Object_?, right: Object_?): Object_? {
    return if (left?.type() == ObjectType.INTEGER && right?.type() == ObjectType.INTEGER) {
        evalIntegerInfixExpression(operator, left as Integer, right as Integer)
    } else if (left?.type() == ObjectType.STRING && right?.type() == ObjectType.STRING) {
        evalStringInfixExpression(operator, left as String_, right as String_)
    } else if (left?.type() != right?.type()) {
        newError("type mismatch: ${left?.type()} $operator ${right?.type()}")
    } else if (operator == Operator.`==`) {
        `object`.Boolean_(left == right)
    } else if (operator == Operator.`!=`) {
        `object`.Boolean_(left != right)
    } else {
        newError("unknown operator: ${left?.type()} $operator ${right?.type()}")
    }
}

private fun evalStringInfixExpression(operator: Operator, left: String_, right: String_): Object_ {
    return if (operator != Operator.`+`) {
        newError("unknow operator: ${left.type()} $operator ${right.type()}")
    } else {
        String_(left.value + right.value)
    }
}

fun evalIntegerInfixExpression(operator: Operator, left: Integer, right: Integer): Object_ {
    return when (operator) {
        Operator.`+` -> Integer(left.value + right.value)
        Operator.`-` -> Integer(left.value - right.value)
        Operator.`*` -> Integer(left.value * right.value)
        Operator.Div -> Integer(left.value / right.value)

        Operator.LT -> `object`.Boolean_(left.value < right.value)
        Operator.GT -> `object`.Boolean_(left.value > right.value)
        Operator.`==` -> `object`.Boolean_(left.value == right.value)
        Operator.`!=` -> `object`.Boolean_(left.value != right.value)
        else -> newError("unknown operator: ${left?.type()} $operator ${right?.type()}")
    }
}

private fun evalPrefixExpression(operator: Operator, right: Object_?): Object_ {
    return when (operator) {
        Operator.`!` -> evalBangOperatorExpression(right)
        Operator.`-` -> evalMinusPrefixOperatorExpression(right)
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

fun newError(msg: String): Error_ {
    return Error_(msg)
}

private fun isError(obj: Object_?): Boolean {
    return obj is Error_
}