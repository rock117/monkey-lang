package evaluator

import Object_
import ast.*
import `object`.Integer

fun eval(node: Node): Object_? {
    return when (node) {
        is Program -> evalStatements(node.statements)
        is ExpressionStatement -> eval(node.expression!!)
        is IntegerLiteral -> Integer(node.value!!)
        else -> null
    }
}

private fun evalStatements(statements: List<Statement>): Object_? {
    var result: Object_? = null
    for (statement in statements) {
        result = eval(statement)
    }
    return result
}
