package `object`

import Object_

data class ReturnValue(val value: Object_): Object_ {
    override fun type(): ObjectType {
        return ObjectType.RETURN
    }

    override fun inspect(): String {
        return value.inspect()
    }
}