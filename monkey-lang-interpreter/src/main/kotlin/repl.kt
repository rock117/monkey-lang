import lexer.Lexer
import parser.Parser

object repl {
    val PROMPT = ">> "
    fun start(){
        while (true) {
            print(PROMPT)
            val line = readlnOrNull() ?: return
            val parser = Parser.new(Lexer.new(line))
            val program = parser.parseProgram()
            if(parser.erros.isNotEmpty()) {
                printParserErrors(parser.erros)
                continue
            }

            println(program.string())
            println()
        }
    }

    private fun printParserErrors(erros: List<String>) {
        erros.forEach {
            println("\t$it")
        }
    }

}


private val  MONKEY_FACE = """            __,__
.--.  .-"     "-.  .--.
/ .. \/  .-. .-.  \/ .. \
| |  '|  /   Y   \  |'  | |
| \   \  \ 0 | 0 /  /   / |
\ '- ,\.-"""""""-./, -' /
''-' /_   ^ ^   _\ '-''
|  \._   _./  |
\   \ '~' /   /
'._ '-=-' _.'
'-----'
"""
