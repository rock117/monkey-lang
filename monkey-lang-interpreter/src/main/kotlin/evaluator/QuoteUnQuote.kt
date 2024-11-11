package evaluator

import Object_
import ast.CallExpression
import ast.Node
import ast.modify
import `object`.Quote

fun quote(node: Node): Object_ {
    val newNode = evalUnquoteCalls(node)
    return Quote(newNode)
}

private fun evalUnquoteCalls(quoted: Node): Node {
    return modify(quoted) { node ->
        if(!isUnquoteCall(node)) {
            return@modify node
        }
        if(node !is CallExpression) {
            return@modify node
        }
        val call = node
        if(call.arguments.size != 1) {
            return@modify node
        }
        node
    }
}

private fun isUnquoteCall(node: Node): Boolean {
    if(node !is CallExpression) {
        return false
    }
    return node.function.tokenLiteral() == "unquote"
}