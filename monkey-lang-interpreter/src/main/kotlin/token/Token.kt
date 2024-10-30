package token

data class Token(val type: TokenType, val literal: String) {
    constructor(type: TokenType,   literal: Char) : this(type, literal.toString())
}