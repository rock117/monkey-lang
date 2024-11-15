package ast

enum class Operator {
    `+`, `-`, `*`, Div, `=`, `==`, `!=`, `!`, LT, GT;

    override fun toString(): String {
        return when (this) {
            Div -> "/"
            LT -> "<"
            GT -> ">"
            else -> this.name
        }
    }
}

