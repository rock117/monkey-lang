package ast

typealias ModifierFunc = (Node) -> Node?

fun modify(node: Node, modifier: ModifierFunc): Node? {
    if (node is Program) {
        for (statement in node.statements) {
            modify(statement, modifier)
        }

        node.statements.forEachIndexed { index, statement ->
            node.statements[index] = modify(statement, modifier) as Statement
        }
    } else if (node is ExpressionStatement) {
        node.expression = modify(node.expression!!, modifier) as Expression
    } else if (node is InfixExpression) {
        node.left = modify(node.left, modifier) as Expression
        node.right = modify(node.right, modifier) as Expression
    } else if (node is PrefixExpression) {
        node.right = modify(node.right!!, modifier) as Expression
    } else if (node is IndexExpression) {
        node.left = modify(node.left, modifier) as Expression
        node.index = modify(node.index, modifier) as Expression
    } else if (node is IfExpression) {
        node.condition = modify(node.condition, modifier) as Expression
        node.consequence = modify(node.consequence, modifier) as BlockStatement
        if(node.alternative != null) {
            node.alternative = modify(node.alternative!!, modifier) as BlockStatement
        }
    } else if (node is BlockStatement) {
        node.statements.forEachIndexed { index, statement ->
            node.statements[index] = modify(statement!!, modifier) as Statement
        }
    } else if (node is ReturnStatement) {
        node.returnValue = modify(node.returnValue!!, modifier) as Expression
    }  else if (node is LetStatement) {
        node.value = modify(node.value, modifier) as Expression
    } else if (node is FunctionLiteral) {
        node.parameters.forEachIndexed { index, parameter ->
            node.parameters[index] = modify(parameter, modifier) as Identifier
        }
        node.body = modify(node.body, modifier) as BlockStatement
    } else if (node is ArrayLiteral) {
        node.elements.forEachIndexed { index, e ->
            node.elements[index] = modify(e, modifier) as Expression
        }
    } else if (node is HashLiteral) {
        node.pairs.forEach { (key, value) ->
            val newKey = modify(key, modifier) as Expression
            val newValue = modify(value, modifier) as Expression
            node.pairs[newKey] = newValue
            node.pairs.remove(key)
        }
    }
    return modifier(node)
}