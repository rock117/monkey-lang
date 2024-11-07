package `object`

import Object_
import ast.BlockStatement
import ast.Identifier

data class Function(val parameters: MutableList<Identifier>, val body: BlockStatement, val env: Environment): Object_ {
    override fun type(): ObjectType {
        return ObjectType.FUNCTION
    }

    override fun inspect(): String {
        val params = parameters.map { it.string() }.joinToString(", ")
        return "fn($params) {\n${this.body.string()}\n}"
    }
}