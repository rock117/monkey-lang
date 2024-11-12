package `object`

import Object_
import ast.BlockStatement
import ast.Identifier

data class Macro(val parameters: MutableList<Identifier>, val body: BlockStatement, val env: Environment): Object_ {
    override fun type(): ObjectType {
        return ObjectType.FUNCTION
    }

    override fun inspect(): String {
        val params = parameters.map { it.string() }.joinToString(", ")
        return "macro($params) {\n${this.body.string()}\n}"
    }

    override fun equals(other: Any?): Boolean {
        if(other is Macro) {
            return parameters == other.parameters && body == other.body && env == other.env
        }
        return false
    }

    override fun hashCode(): Int {
        return parameters.hashCode() + body.hashCode() + env.hashCode()
    }
}