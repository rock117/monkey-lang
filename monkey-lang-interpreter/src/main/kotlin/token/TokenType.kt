package token

enum class TokenType(val value: String) {
    ILLEGAL("ILLEGAL"),
    EOF("EOF"),
    IDENT("IDENT"), // add, foobar, x, y, ...
    INT("INT"), // 1343456

    ASSIGN("="),
    PLUS("+"),
    MINUS("-"),
    BANG("!"),
    ASTERISK("*"),
    SLASH("/"),
    LT("<"),
    GT(">"),

    COMMA(","),
    SEMICOLON(";"),
    LPAREN("("),
    RPAREN(")"),
    LBRACE("{"),
    RBRACE("}"),

    FUNCTION("FUNCTION"),
    LET("LET"),
    TRUE("TRUE"),
    FALSE("FALSE"),
    IF("IF"),
    ELSE("ELSE"),
    RETURN("RETURN"),

    EQ("=="),
    NOT_EQ("!="),
    STRING("STRING");

    override fun toString(): String {
        return this.value
    }
}
