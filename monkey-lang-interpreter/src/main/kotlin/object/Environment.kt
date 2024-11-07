package `object`

import Object_

class Environment {
    private val store: MutableMap<String, Object_> = mutableMapOf()
    private var outer: Environment? = null

    constructor() {}

    companion object {
        fun newEnclosingEnvironment(outer: Environment): Environment {
            val env = Environment()
            env.outer = outer
            return env
        }
    }

    fun set(key: String, value: Object_) {
        store[key] = value
    }

    fun get(key: String): Object_? {
        val v = store[key]
        if (v == null && outer != null) {
            return outer!!.get(key)
        } else {
            return v
        }
    }

}