package evaluator

import Object_
import ast.CallExpression
import ast.IntegerLiteral
import ast.Node
import ast.modify
import `object`.Boolean_
import `object`.Environment
import `object`.Integer
import `object`.Quote
import token.Token
import token.TokenType

fun quote(node: Node, env: Environment): Object_ {
    val newNode = evalUnquoteCalls(node, env)
    return Quote(newNode)
}


private fun evalUnquoteCalls(quoted: Node, env: Environment): Node? {
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
        convertObjectToAstNode(eval(call.arguments[0], env))
    }
}

private fun convertObjectToAstNode(obj: Object_?): Node? {
    if(obj is Integer) {
        val token = Token(TokenType.INT, obj.value.toString())
        return IntegerLiteral(token, obj.value)
    }
    if(obj is Boolean_) {
        val token = if(obj.value) Token(TokenType.TRUE, "true") else Token(TokenType.FALSE, "false")
        return ast.Boolean_(token, obj.value)
    }
    if(obj is Quote) {
        return obj.node
    }
    return null
}

private fun isUnquoteCall(node: Node): Boolean {
    if(node !is CallExpression) {
        return false
    }
    return node.function.tokenLiteral() == "unquote"
}