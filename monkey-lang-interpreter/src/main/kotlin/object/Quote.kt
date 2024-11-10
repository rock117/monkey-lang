package `object`

import Object_
import ast.Node

/**
 * macro class
 */
data class Quote(val node: Node): Object_ {
    override fun type(): ObjectType {
        return ObjectType.QUOTE
    }

    override fun inspect(): String {
        return "QUOTE(${node.string()})"
    }

    override fun hashCode(): Int {
        return node.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if(other is Quote) {
            return node == other.node
        }
        return false
    }
}