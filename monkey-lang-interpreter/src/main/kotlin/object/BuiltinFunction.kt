package `object`

import Object_
import evaluator.newError

class Builtin(val fn: (List<Object_?>) -> Object_): Object_ {
    override fun type(): ObjectType {
        return ObjectType.BUILTIN
    }

    override fun inspect(): String {
        return "builtin function"
    }
}

object BuiltinFns {

    fun len(): Builtin {
        return Builtin { args: List<Object_?> ->
            if(args.size != 1) {
                newError("wrong member of arguments, got=${args.size}, want=1")
            } else {
                val type = args[0]?.type()
                when(type) {
                    ObjectType.ARRAY -> Integer((args[0] as Array).elements.size)
                    ObjectType.STRING -> Integer((args[0] as String_).value.length)
                    else -> newError("argument to `len` not supported got ${args[0]?.type()}")
                }
            }
        }
    }

    fun first(): Builtin {
        return Builtin { args: List<Object_?> ->
            if(args.size != 1) {
                return@Builtin newError("wrong member of arguments, got=${args.size}, want=1")
            }
            val type = args[0]?.type()
            if (type != ObjectType.ARRAY) {
                return@Builtin newError("argument to `first` must be ARRAY, got $type ")
            }
            val arr = args[0] as Array
            if(arr.elements.isNotEmpty()) {
                return@Builtin arr.elements[0]!!
            }
            return@Builtin Null
        }
    }

    fun last(): Builtin {
        return Builtin { args: List<Object_?> ->
            if(args.size != 1) {
                return@Builtin newError("wrong member of arguments, got=${args.size}, want=1")
            }
            val type = args[0]?.type()
            if (type != ObjectType.ARRAY) {
                return@Builtin newError("argument to `last` must be ARRAY, got $type ")
            }
            val arr = args[0] as Array
            if(arr.elements.isNotEmpty()) {
                return@Builtin arr.elements[arr.elements.size - 1]!!
            }
            return@Builtin Null
        }
    }

    fun rest(): Builtin {
        return Builtin { args: List<Object_?> ->
            if(args.size != 1) {
                return@Builtin newError("wrong member of arguments, got=${args.size}, want=1")
            }
            val type = args[0]?.type()
            if (type != ObjectType.ARRAY) {
                return@Builtin newError("argument to `rest` must be ARRAY, got $type ")
            }
            val arr = args[0] as Array

            if(arr.elements.isNotEmpty()) {
                return@Builtin Array(arr.elements.drop(1))
            }
            return@Builtin Null
        }
    }

    fun push(): Builtin {
        return Builtin { args: List<Object_?> ->
            if(args.size != 2) {
                return@Builtin newError("wrong member of arguments, got=${args.size}, want=2")
            }
            val type = args[0]?.type()
            if (type != ObjectType.ARRAY) {
                return@Builtin newError("argument to `push` must be ARRAY, got $type ")
            }
            val arr = args[0] as Array
            val e = args[1]!!
            var newElements = listOf<Object_?>()
            newElements = newElements + arr.elements
            newElements = newElements + e
            return@Builtin Array(newElements)
        }
    }

}