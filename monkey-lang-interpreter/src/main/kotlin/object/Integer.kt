package `object`

import Object_

data class Integer(val value: Int): Object_ {
    override fun type(): ObjectType {
        return ObjectType.INTEGER
    }

    override fun inspect(): String {
        return "$value"
    }

    override fun equals(other: Any?): Boolean {
        if(other is Integer) {
            return value == other.value
        }
        return false
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }
}