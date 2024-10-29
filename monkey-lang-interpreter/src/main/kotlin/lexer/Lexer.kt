package lexer

import token.Token
import token.TokenType
private val KEY_WORDS = mapOf("fn" to TokenType.FUNCTION, "let" to TokenType.LET)

class Lexer(private val input: String, var position: Int = 0, var readPosition: Int = 0, var ch: Char = 0 as Char) {

    companion object {
        fun new(input: String): Lexer {
            val lexer = Lexer(input)
            lexer.readChar()
            return lexer
        }
    }

    fun readChar() {
        if(this.readPosition >= this.input.length) {
            this.ch = 0 as Char;
        } else {
            this.ch = this.input[this.readPosition]
        }
        this.position = this.readPosition
        this.readPosition += 1
    }

    fun nextToken(): Token {
        this.skipWhitespace()
        val token = when(this.ch) {
            '=' -> Token(TokenType.ASSIGN, this.ch.toString())
            ';' -> Token(TokenType.SEMICOLON, this.ch.toString())
            '(' -> Token(TokenType.LPAREN, this.ch.toString())
            ')' -> Token(TokenType.RPAREN, this.ch.toString())
            ',' -> Token(TokenType.COMMA, this.ch.toString())
            '+' -> Token(TokenType.PLUS, this.ch.toString())
            '{' -> Token(TokenType.LBRACE, this.ch.toString())
            '}' -> Token(TokenType.RBRACE, this.ch.toString())
            0.toChar() -> Token(TokenType.EOF, "")
            else -> {
                if(isLetter(this.ch)) {
                    val literal = this.readIdentifier()
                    return Token(lookupIdent(literal), literal)
                } else if(isDigit(this.ch)) {
                    return Token(TokenType.INT, this.readNumber())
                } else {
                    return Token(TokenType.ILLEGAL, this.ch.toString())
                }
            }
        }
        this.readChar()
        return token
    }

    fun readIdentifier(): String {
        val position = this.position
        while (isLetter(this.ch)) {
            this.readChar()
        }
        return this.input.substring(position, this.position)
    }

    fun skipWhitespace(){
        while (this.ch == ' ' || this.ch == '\r' || this.ch == '\n' || this.ch == '\t') {
            this.readChar()
        }
    }

    fun readNumber(): String {
        val position = this.position
        while (isDigit(this.ch)) {
            this.readChar()
        }
        return this.input.substring(position, this.position)
    }
}

fun isLetter(ch: Char): Boolean {
    return ch in 'a'..'z' || ch in 'A'..'Z' || ch == '_'
}

fun lookupIdent(ident: String): TokenType {
    return KEY_WORDS[ident] ?: TokenType.IDENT
}

fun isDigit(ch: Char): Boolean {
    return ch in '0' .. '9'
}


// lexer/lexer.go
//package lexer
//
//type Lexer struct {
//    input        string
//            position     int  // 所输入字符串中的当前位置（指向当前字符）
//            readPosition int  // 所输入字符串中的当前读取位置（指向当前字符之后的一个字符）
//            ch           byte // 当前正在查看的字符
//}
//
//func New(input string) *Lexer {
//    l := &Lexer{input: input}
//    return l
//}