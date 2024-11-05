package `object`

import Object_

class Null: Object_ {
    override fun type(): ObjectType {
        return ObjectType.NULL
    }

    override fun inspect(): String {
        return "null"
    }

    override fun toString(): String {
        return inspect()
    }

    override fun hashCode(): Int {
        return toString().hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if(other === null) return false
        return other is Null
    }
}