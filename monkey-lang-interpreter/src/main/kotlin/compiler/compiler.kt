package compiler

import Object_
import ast.Node
import code.Instructions

class Compiler(val instructions: Instructions, val constants: Array<Object_>) {

    fun compile(node: Node) {

    }

    fun bytecode(): Bytecode {
        return Bytecode(instructions, constants)
    }
}

class Bytecode(val instructions: Instructions, val constants: Array<Object_>) {

}