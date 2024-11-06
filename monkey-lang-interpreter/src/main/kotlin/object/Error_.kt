package `object`

import Object_

data class Error_(val message: String): Object_ {
    override fun type(): ObjectType {
        return ObjectType.ERROR
    }

    override fun inspect(): String {
        return "ERROR: $message"
    }
}