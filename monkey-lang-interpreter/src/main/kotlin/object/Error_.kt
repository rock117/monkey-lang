package `object`

import Object_

data class Error_(val message: String): Object_ {
    override fun type(): ObjectType {
        return ObjectType.ERROR
    }

    override fun inspect(): String {
        return "ERROR: $message"
    }

    override fun equals(other: Any?): Boolean {
        if(other is Error_) {
            return message == other.message
        }
        return false
    }

    override fun hashCode(): Int {
        return message.hashCode()
    }
}