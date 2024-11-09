package `object`

import Object_

data class ReturnValue(val value: Object_): Object_ {
    override fun type(): ObjectType {
        return ObjectType.RETURN
    }

    override fun inspect(): String {
        return value.inspect()
    }

    override fun equals(other: Any?): Boolean {
        if(other is ReturnValue) {
            return value == other.value
        }
        return false
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }
}