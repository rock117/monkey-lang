package `object`

import Object_

class Builtin(val fn: (List<Object_?>) -> Object_): Object_ {
    override fun type(): ObjectType {
       return ObjectType.BUILTIN
    }

    override fun inspect(): String {
        return "builtin function"
    }
}

