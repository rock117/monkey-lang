import lexer.Lexer
import token.TokenType

object repl {
    val PROMPT = ">> "
    fun start(){
        while (true) {
            print(PROMPT)
            val line = readlnOrNull() ?: return
            val lexer = Lexer.new(line)
            var nextToken = lexer.nextToken()
            while (nextToken.type != TokenType.EOF) {
                println(nextToken)
                nextToken = lexer.nextToken()
            }
        }
    }
}