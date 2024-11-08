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
}