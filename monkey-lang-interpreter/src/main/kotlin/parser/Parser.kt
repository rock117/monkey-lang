package parser

import ast.*
import lexer.Lexer
import token.Token
import token.TokenType

class Parser(val lexer: Lexer,
             var curToken: Token?,
             var peekToken: Token?,
             val erros: MutableList<String> = mutableListOf(),
             val prefixParseFns: MutableMap<TokenType, () -> Expression> = mutableMapOf(),
             val infixParseFns: MutableMap<TokenType, (Expression) -> Expression> = mutableMapOf()
    ) {
    companion object {
        fun new(lexer: Lexer): Parser {
            val parser = Parser(lexer, null, null)
            parser.registerPrefixFn(TokenType.IDENT, parser::parseIdentifier)
            parser.registerPrefixFn(TokenType.INT, parser::parseIntegerLiteral)
            parser.registerPrefixFn(TokenType.BANG, parser::parsePrefixExpression)
            parser.registerPrefixFn(TokenType.MINUS, parser::parsePrefixExpression)

            parser.nextToken()
            parser.nextToken()
            return parser
        }
    }

    private fun parsePrefixExpression(): Expression {
        val expression = PrefixExpression(this.curToken!!, this.curToken!!.literal)
        this.nextToken()
        expression.right = this.parseExpression(OpPrecedence.PREFIX)
        return expression
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
            else -> this.parseExpressionStatement()
        }
    }

    private fun parseExpressionStatement(): ExpressionStatement {
        val stmt = ExpressionStatement(this.curToken!!, this.parseExpression(OpPrecedence.LOWEST))
        if(this.peekTokenIs(TokenType.SEMICOLON)) {
            this.nextToken()
        }
        return stmt
    }

    private fun parseExpression(lowest: OpPrecedence): Expression? {
        val prefix = this.prefixParseFns[this.curToken!!.type] ?: return null
        if(prefix == null) {
            this.noPrefixParseFnError(this.curToken!!.type)
            return null
        }
        return prefix?.invoke()
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

    fun registerPrefixFn(tokenType: TokenType, prefixFn: () -> Expression) {
        this.prefixParseFns[tokenType] = prefixFn
    }

    fun registerInfixFn(tokenType: TokenType, infixFn: (Expression) -> Expression) {
        this.infixParseFns[tokenType] = infixFn
    }

    fun parseIdentifier(): Identifier {
        return Identifier(this.curToken!!, this.curToken!!.literal)
    }

    fun parseIntegerLiteral(): Expression {
        val literal = IntegerLiteral(this.curToken!!)
        val value = this.curToken?.literal?.toIntOrNull()
        if(value == null) {
            this.erros.add("could not parse ${this.curToken?.literal} as integer")
        }
        literal.value = value
        return literal
    }

    fun noPrefixParseFnError(t: TokenType) {
        this.erros.add("no prefix parse function for $t found")
    }
}