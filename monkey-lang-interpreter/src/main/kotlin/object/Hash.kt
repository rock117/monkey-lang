package `object`

import Object_

data class Hash(val pairs: Map<Object_, Object_>): Object_ {
    override fun type(): ObjectType {
        return ObjectType.HASH
    }

    override fun inspect(): String {
        val pairs = this.pairs.map { "${it.key.inspect()}: ${it.value.inspect()}" }.joinToString(", ")
        return "{$pairs}"
    }

    override fun hashCode(): Int {
        return pairs.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if(other is Hash) {
            return pairs == other.pairs
        }
        return false
    }

    operator fun get(index: Object_): Object_? {
        return pairs[index]
    }
}