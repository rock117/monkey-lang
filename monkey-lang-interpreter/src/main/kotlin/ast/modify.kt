package ast

typealias ModifierFunc = (Node) -> Node

fun modify(node: Node, modifier: ModifierFunc) {
    if (node is Program) {

    } else if (node is ExpressionStatement) {

    }
}