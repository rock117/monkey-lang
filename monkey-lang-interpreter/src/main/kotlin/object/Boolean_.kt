package `object`

import Object_


data class Boolean_(val value: Boolean) : Object_ {
    override fun type(): ObjectType {
        return ObjectType.BOOLEAN
    }

    override fun inspect(): String {
        return "$value"
    }

    companion object {
        val TRUE = Boolean_(true)
        val FALSE = Boolean_(false)
    }

    override fun equals(other: Any?): Boolean {
        if(other is Boolean_) {
            return value == other.value
        }
        return false
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }
}

