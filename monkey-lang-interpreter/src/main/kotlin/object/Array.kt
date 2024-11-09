package `object`

import Object_

data class Array(val elements: List<Object_?>): Object_ {
    override fun type(): ObjectType {
        return ObjectType.ARRAY
    }

    override fun inspect(): String {
        val values = elements.joinToString(", ") { it?.inspect().toString() }
        return "[$values]"
    }

    override fun equals(other: Any?): Boolean {
        if(other is Array) {
            return elements == other.elements
        }
        return false
    }

    override fun hashCode(): Int {
        return elements.hashCode()
    }
}