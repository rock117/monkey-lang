package `object`

import Object_

class String_(val value: String):Object_ {
    override fun type(): ObjectType {
        return ObjectType.STRING
    }

    override fun inspect(): String {
        return value
    }

    override fun equals(other: Any?): Boolean {
        if(other is String_) {
            return value == other.value
        }
        return false
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }
}