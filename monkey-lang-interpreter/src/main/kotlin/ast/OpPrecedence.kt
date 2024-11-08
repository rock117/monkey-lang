package ast

enum class OpPrecedence {
    LOWEST,
    EQUALS,
    LESSGREATER, // > or <
    SUM,
    PRODUCT,
    PREFIX, // -x or !x
    CALL, // myFunction(x)
    INDEX
}