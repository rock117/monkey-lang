package token

enum class TokenType(value: String) {
    ILLEGAL("ILLEGAL"),
    EOF("EOF"),
    IDENT("IDENT"), // add, foobar, x, y, ...
    INT("INT"), // 1343456
    ASSIGN("="),
    PLUS("+"),

    COMMA(","),
    SEMICOLON(";"),
    LPAREN("("),
    RPAREN(")"),
    LBRACE("{"),
    RBRACE("}"),

    FUNCTION("FUNCTION"),
    LET("LET")
}
