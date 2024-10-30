package parser

import ast.*
import lexer.Lexer
import token.Token
import token.TokenType
import kotlin.time.measureTime

class Parser(val lexer: Lexer, var curToken: Token?, var peekToken: Token?, val erros: MutableList<String> = mutableListOf()) {
    companion object {
        fun new(lexer: Lexer): Parser {
            val parser = Parser(lexer, null, null)
            parser.nextToken()
            parser.nextToken()
            return parser
        }
    }

    fun nextToken() {
        this.curToken = this.peekToken
        this.peekToken = this.lexer.nextToken()
    }

    fun parseProgram(): Program {
        val program = Program(mutableListOf())
        while (this.curToken?.type != TokenType.EOF) {
            val stmt = this.parseStatement()
            if(stmt != null) {
                program.statements.add(stmt)
            }
            this.nextToken()
        }
        return program
    }

    private fun parseStatement(): Statement? {
        return when(this.curToken?.type) {
            TokenType.LET -> this.parseLetStatement()
            TokenType.RETURN -> this.parseReturnStatement()
            else -> null
        }
    }

    private fun parseReturnStatement(): ReturnStatement {
        val stmt = ReturnStatement(this.curToken!!, null)
        this.nextToken()
        // TODO skip expression
        while (!this.curTokenIs(TokenType.SEMICOLON)) {
            this.nextToken()
        }
        return stmt
    }

    private fun parseLetStatement(): LetStatement? {
        val stmt = LetStatement(this.curToken!!, null, null)
        if(!this.expectPeek(TokenType.IDENT)) {
            return null
        }
        stmt.name = Identifier(this.curToken!!, this.curToken!!.literal)
        if(!this.expectPeek(TokenType.ASSIGN)) {
            return null
        }

        // TODO skip handle expression
        while (this.curTokenIs(TokenType.SEMICOLON)) {
            this.nextToken()
        }
        return stmt
    }

    private fun curTokenIs(t: TokenType): Boolean {
        return this.curToken?.type == t
    }

    private fun peekTokenIs(t: TokenType): Boolean {
        return this.peekToken?.type == t
    }


    private fun expectPeek(t: TokenType): Boolean {
        if(this.peekTokenIs(t)) {
            this.nextToken()
            return true
        } else {
            this.peekError(t)
            return false
        }
    }

    private fun peekError(t: TokenType) {
        val msg = "expected next token to be $t, got ${peekToken?.type} instead"
        this.erros.add(msg)
    }
}