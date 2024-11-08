package lexer

import token.Token
import token.TokenType
private val KEY_WORDS = mapOf(
    "fn" to TokenType.FUNCTION,
    "let" to TokenType.LET,
    "true" to TokenType.TRUE,
    "false" to TokenType.FALSE,
    "if" to TokenType.IF,
    "else" to TokenType.ELSE,
    "return" to TokenType.RETURN,
    )

/**
 * parse input string into token stream
 */
class Lexer(private val input: String, var position: Int = 0, var readPosition: Int = 0, var ch: Char = 0.toChar()) {

    companion object {
        fun new(input: String): Lexer {
            val lexer = Lexer(input)
            lexer.readChar()
            return lexer
        }
    }

    fun readChar() {
        if(this.readPosition >= this.input.length) {
            this.ch = 0.toChar();
        } else {
            this.ch = this.input[this.readPosition]
        }
        this.position = this.readPosition
        this.readPosition += 1
    }

    fun peekChar(): Char {
       return if(this.readPosition >= this.input.length) 0.toChar() else this.input[this.readPosition]
    }

    fun nextToken(): Token {
        this.skipWhitespace()
        val token = when(this.ch) {
            '=' -> {
                if(this.peekChar() == '=') {
                    val ch = this.ch
                    this.readChar()
                    val literal = ch + this.ch.toString()
                    Token(TokenType.EQ, literal)
                } else {
                    Token(TokenType.ASSIGN, this.ch)
                }
            }
            ';' -> Token(TokenType.SEMICOLON, this.ch)
            '(' -> Token(TokenType.LPAREN, this.ch)
            ')' -> Token(TokenType.RPAREN, this.ch)
            ',' -> Token(TokenType.COMMA, this.ch)

            '+' -> Token(TokenType.PLUS, this.ch)
            '-' -> Token(TokenType.MINUS, this.ch)
            '*' -> Token(TokenType.ASTERISK, this.ch)
            '/' -> Token(TokenType.SLASH, this.ch)
            '!' -> {
                if(this.peekChar() == '=') {
                    val ch = this.ch
                    this.readChar()
                    val literal = ch + this.ch.toString()
                    Token(TokenType.NOT_EQ, literal)
                } else {
                    Token(TokenType.BANG, this.ch)
                }
            }
            '<' -> Token(TokenType.LT, this.ch)
            '>' -> Token(TokenType.GT, this.ch)
            '{' -> Token(TokenType.LBRACE, this.ch)
            '}' -> Token(TokenType.RBRACE, this.ch)
            '[' -> Token(TokenType.LBRACKET, this.ch)
            ']' -> Token(TokenType.RBRACKET, this.ch)
            '"' -> Token(TokenType.STRING, this.readString())
            0.toChar() -> Token(TokenType.EOF, "")
            else -> {
                if(isLetter(this.ch)) {
                    val literal = this.readIdentifier()
                    return Token(lookupIdent(literal), literal)
                } else if(isDigit(this.ch)) {
                    return Token(TokenType.INT, this.readNumber())
                } else {
                    return Token(TokenType.ILLEGAL, this.ch)
                }
            }
        }
        this.readChar()
        return token
    }

    private fun readString(): String {
        val position = this.position + 1
        while (true) {
            this.readChar()
            if(this.ch == '"' || this.ch == 0.toChar()) {
                break
            }
        }
        return this.input.substring(position, this.position)
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

