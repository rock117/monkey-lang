package `object`

import Object_

data class String_(val value: String):Object_ {
    override fun type(): ObjectType {
        return ObjectType.STRING
    }

    override fun inspect(): String {
        return value
    }
}