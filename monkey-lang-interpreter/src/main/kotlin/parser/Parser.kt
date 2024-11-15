package parser

import ast.*
import extension.toOperator
import lexer.Lexer
import token.Token
import token.TokenType

private val precedences = mapOf(
    TokenType.EQ to OpPrecedence.EQUALS,
    TokenType.NOT_EQ to OpPrecedence.EQUALS,
    TokenType.LT to OpPrecedence.LESSGREATER,
    TokenType.GT to OpPrecedence.LESSGREATER,
    TokenType.PLUS to OpPrecedence.SUM,

    TokenType.MINUS to OpPrecedence.SUM,
    TokenType.SLASH to OpPrecedence.PRODUCT,
    TokenType.ASTERISK to OpPrecedence.PRODUCT,
    TokenType.LPAREN to OpPrecedence.CALL,
    TokenType.LBRACKET to OpPrecedence.INDEX,
    )

class Parser(val lexer: Lexer,
             var curToken: Token,
             var peekToken: Token,
             val erros: MutableList<String> = mutableListOf(),
             val prefixParseFns: MutableMap<TokenType, () -> Expression?> = mutableMapOf(),
             val infixParseFns: MutableMap<TokenType, (Expression?) -> Expression?> = mutableMapOf()
    ) {
    companion object {
        fun new(lexer: Lexer): Parser {
            var tokens = initTokens(lexer)
            val parser = Parser(lexer, tokens.first, tokens.second)
            parser.registerPrefixFn(TokenType.IDENT, parser::parseIdentifier)
            parser.registerPrefixFn(TokenType.INT, parser::parseIntegerLiteral)
            parser.registerPrefixFn(TokenType.BANG, parser::parsePrefixExpression)
            parser.registerPrefixFn(TokenType.MINUS, parser::parsePrefixExpression)
            parser.registerPrefixFn(TokenType.TRUE, parser::parseBoolean)
            parser.registerPrefixFn(TokenType.FALSE, parser::parseBoolean)
            parser.registerPrefixFn(TokenType.LPAREN, parser::parseGroupedExpression)
            parser.registerPrefixFn(TokenType.IF, parser::parseIfExpression)
            parser.registerPrefixFn(TokenType.FUNCTION, parser::parseFunctionLiteral)
            parser.registerPrefixFn(TokenType.STRING, parser::parseStringLiteral)
            parser.registerPrefixFn(TokenType.LBRACKET, parser::parseArrayLiteral)
            parser.registerPrefixFn(TokenType.LBRACE, parser::parseHashLiteral)
            parser.registerPrefixFn(TokenType.MACRO, parser::parseMacroLiteral)


            parser.registerInfixFn(TokenType.PLUS, parser::parseInfixExpression)
            parser.registerInfixFn(TokenType.MINUS, parser::parseInfixExpression)
            parser.registerInfixFn(TokenType.SLASH, parser::parseInfixExpression)
            parser.registerInfixFn(TokenType.ASTERISK, parser::parseInfixExpression)
            parser.registerInfixFn(TokenType.EQ, parser::parseInfixExpression)
            parser.registerInfixFn(TokenType.NOT_EQ, parser::parseInfixExpression)
            parser.registerInfixFn(TokenType.LT, parser::parseInfixExpression)
            parser.registerInfixFn(TokenType.GT, parser::parseInfixExpression)
            parser.registerInfixFn(TokenType.LPAREN, parser::parseCallExpression)
            parser.registerInfixFn(TokenType.LBRACKET, parser::parseIndexExpression)
            return parser
        }
        private fun initTokens(lexer: Lexer): Pair<Token, Token> {
            return Pair(lexer.nextToken(), lexer.nextToken())
        }
    }

    private fun parseMacroLiteral(): Expression? {
        val token = this.curToken
        if(!expectPeek(TokenType.LPAREN)) {
            return null
        }
        val params = parseFunctionParameters()
        if(!expectPeek(TokenType.LBRACE)) {
            return null
        }
        val body = parseBlockStatement()
        return MacroLiteral(token, params, body)
    }

    private fun parseIndexExpression(left: Expression?): Expression? {
        val token = this.curToken
        this.nextToken()
        val index = this.parseExpression(OpPrecedence.LOWEST)!!
        if(!this.expectPeek(TokenType.RBRACKET)) {
            return null
        }
        return IndexExpression(token, left!!, index)
    }

    private fun parseArrayLiteral(): Expression? {
        return ArrayLiteral(this.curToken, this.parseExpressionList(TokenType.RBRACKET))
    }

    private fun parseHashLiteral(): Expression? {
        val token = this.curToken
        val pairs = mutableMapOf<Expression, Expression>()
        while (!this.peekTokenIs(TokenType.RBRACE)) {
            this.nextToken()
            val key = this.parseExpression(OpPrecedence.LOWEST)!!
            if(!this.expectPeek(TokenType.COLON)) {
                return null
            }
            this.nextToken()
            val value = this.parseExpression(OpPrecedence.LOWEST)!!
            pairs[key] = value
            if(!this.peekTokenIs(TokenType.RBRACE) && !this.expectPeek(TokenType.COMMA)) {
                return null
            }
        }
        if(!this.expectPeek(TokenType.RBRACE)) {
            return null
        }
        return HashLiteral(token, pairs)
    }

    private fun parseExpressionList(end: TokenType): MutableList<Expression> {
        val expressions = mutableListOf<Expression>()
        if(this.peekTokenIs(end)) {
            this.nextToken()
            return expressions
        }
        this.nextToken()
        expressions.add(this.parseExpression(OpPrecedence.LOWEST)!!)
        while (this.peekTokenIs(TokenType.COMMA)) {
            this.nextToken()
            this.nextToken()
            expressions.add(this.parseExpression(OpPrecedence.LOWEST)!!)
        }
        if(!this.expectPeek(end)) {
            // TODO return nil
            return expressions
        }
        return expressions
    }

    private fun parseStringLiteral(): Expression? {
        return StringLiteral(this.curToken, this.curToken.literal)
    }

    private fun parseCallExpression(function: Expression?): Expression? {
        return CallExpression(this.curToken, function!!, this.parseCallArguments())
    }

    private fun parseCallArguments(): MutableList<Expression> {
        return this.parseExpressionList(TokenType.RPAREN).toMutableList()
    }

    private fun parseFunctionLiteral(): Expression? {
        val token = this.curToken
        if(!this.expectPeek(TokenType.LPAREN)) {
            return null
        }
        val params  = this.parseFunctionParameters()
        if(!this.expectPeek(TokenType.LBRACE)) {
            return null
        }
        val body = this.parseBlockStatement()
        return FunctionLiteral(token, params, body)
    }

    private fun parseFunctionParameters(): MutableList<Identifier> {
        val identifiers = mutableListOf<Identifier>()
        if(this.peekTokenIs(TokenType.RPAREN)) {
            this.nextToken()
            return identifiers
        }
        this.nextToken()
        identifiers.add(Identifier(this.curToken, this.curToken.literal))
        while (this.peekTokenIs(TokenType.COMMA)) {
            this.nextToken()
            this.nextToken()
            identifiers.add(Identifier(this.curToken, this.curToken.literal))
        }
        if(!this.expectPeek(TokenType.RPAREN)) {
           // return null //TODO
            return identifiers
        }
        return identifiers
    }

    private fun parseIfExpression(): Expression? {
        val token = this.curToken
        if(!this.expectPeek(TokenType.LPAREN)) {
            return null
        }
        this.nextToken()
        val condition = this.parseExpression(OpPrecedence.LOWEST)!!
        if(!this.expectPeek(TokenType.RPAREN)) {
            return null
        }
        if(!this.expectPeek(TokenType.LBRACE)) {
            return null
        }
        val consequence: BlockStatement = this.parseBlockStatement()
        var alternative: BlockStatement? = null
        if(this.peekTokenIs(TokenType.ELSE)) {
            this.nextToken()
            if(!this.peekTokenIs(TokenType.LBRACE)) {
                return null
            }
            alternative = this.parseBlockStatement()
        }
        return IfExpression(token, condition, consequence, alternative)
    }

    private fun parseBlockStatement(): BlockStatement {
        val block = BlockStatement(this.curToken)
        this.nextToken()
        while (!this.curTokenIs(TokenType.RBRACE) && !this.curTokenIs(TokenType.EOF)) {
            val stmt = this.parseStatement()
            if(stmt != null) {
                block.statements.add(stmt)
            }
            this.nextToken()
        }
        return block
    }

    private fun parseGroupedExpression(): Expression? {
        this.nextToken()
        val exp = this.parseExpression(OpPrecedence.LOWEST)
        if(!this.expectPeek(TokenType.RPAREN)) {
            return null
        }
        return exp
    }

    fun parseProgram(): Program {
        val program = Program(mutableListOf())
        while (this.curToken.type != TokenType.EOF) {
            val stmt = this.parseStatement()
            if(stmt != null) {
                program.statements.add(stmt)
            }
            this.nextToken()
        }
        return program
    }
  
    private fun nextToken() {
        this.curToken = this.peekToken
        this.peekToken = this.lexer.nextToken()
    }

    private fun parseBoolean(): Expression? {
        return Boolean_(this.curToken, this.curTokenIs(TokenType.TRUE))
    }

    private fun parseInfixExpression(left: Expression?): Expression? {
        val precedence = this.curPrecedence()
        val token = this.curToken
        this.nextToken()
        val right = this.parseExpression(precedence)
        return InfixExpression(token, left!!, token.literal.toOperator(),  right!!)
    }

    private fun parsePrefixExpression(): Expression? {
        val expression = PrefixExpression(this.curToken, this.curToken.literal.toOperator())
        this.nextToken()
        expression.right = this.parseExpression(OpPrecedence.PREFIX)
        return expression
    }

    private fun parseStatement(): Statement? {
        return when(this.curToken.type) {
            TokenType.LET -> this.parseLetStatement()
            TokenType.RETURN -> this.parseReturnStatement()
            else -> this.parseExpressionStatement()
        }
    }

    private fun parseExpressionStatement(): ExpressionStatement {
        val stmt = ExpressionStatement(this.curToken, this.parseExpression(OpPrecedence.LOWEST))
        if(this.peekTokenIs(TokenType.SEMICOLON)) {
            this.nextToken()
        }
        return stmt
    }

    private fun parseExpression(precedence: OpPrecedence): Expression? {
        val prefix = this.prefixParseFns[this.curToken.type] ?: return null
        if(prefix == null) {
            this.noPrefixParseFnError(this.curToken.type)
            return null
        }
        var leftExp = prefix.invoke()
        var go = !this.peekTokenIs(TokenType.SEMICOLON) && precedence < this.peekPrecedence()
        println("go: $go, precedence: ${precedence}, this.peekPrecedence(): ${this.peekPrecedence()}")
        while (go) {
            val infix = this.infixParseFns[this.peekToken!!.type] ?: return null
            this.nextToken()
            leftExp = infix.invoke(leftExp)
            go = !this.peekTokenIs(TokenType.SEMICOLON) && precedence < this.peekPrecedence()
            println("go: $go, precedence: ${precedence}, this.peekPrecedence(): ${this.peekPrecedence()}")

        }
        return leftExp
    }

    private fun parseReturnStatement(): ReturnStatement {
        val stmt = ReturnStatement(this.curToken, null)
        this.nextToken()
        stmt.returnValue = this.parseExpression(OpPrecedence.LOWEST)
        if(this.peekTokenIs(TokenType.SEMICOLON)) {
            this.nextToken()
        }
        return stmt
    }

    private fun parseLetStatement(): LetStatement? {
        val token = this.curToken
        if(!this.expectPeek(TokenType.IDENT)) {
            return null
        }
        val name = Identifier(this.curToken, this.curToken.literal)
        if(!this.expectPeek(TokenType.ASSIGN)) {
            return null
        }
        this.nextToken()
        val value = this.parseExpression(OpPrecedence.LOWEST)
        if (this.peekTokenIs(TokenType.SEMICOLON)) {
            this.nextToken()
        }
        return LetStatement(token, name, value!!)
    }

    private fun curTokenIs(t: TokenType): Boolean {
        return this.curToken.type == t
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

    private fun registerPrefixFn(tokenType: TokenType, prefixFn: () -> Expression?) {
        this.prefixParseFns[tokenType] = prefixFn
    }

    private fun registerInfixFn(tokenType: TokenType, infixFn: (Expression?) -> Expression?) {
        this.infixParseFns[tokenType] = infixFn
    }

    private fun parseIdentifier(): Identifier {
        return Identifier(this.curToken, this.curToken.literal)
    }

    private fun parseIntegerLiteral(): Expression {
        val token = this.curToken
        val value = this.curToken.literal.toIntOrNull()
        if(value == null) {
            this.erros.add("could not parse ${this.curToken.literal} as integer")
        }
        return IntegerLiteral(token, value)
    }

    private fun noPrefixParseFnError(t: TokenType) {
        this.erros.add("no prefix parse function for $t found")
    }

    private fun peekPrecedence(): OpPrecedence {
        return precedences[this.peekToken.type] ?: OpPrecedence.LOWEST
    }

    private fun curPrecedence(): OpPrecedence {
        return precedences[this.curToken.type] ?: OpPrecedence.LOWEST
    }
}