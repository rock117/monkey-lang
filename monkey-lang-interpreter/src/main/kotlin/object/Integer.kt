package `object`

import Object_

data class Integer(val value: Int): Object_ {
    override fun type(): ObjectType {
        return ObjectType.INTEGER
    }

    override fun inspect(): String {
        return "$value"
    }
}