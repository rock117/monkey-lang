import lexer.Lexer
import parser.Parser

import java.io.File
import java.net.URL

object repl {
    val MONKEY_FACE = readResourceFile("MONKEY_FACE.mk")
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
        println(MONKEY_FACE)
        println("Woops! We ran into some monkey business here!")
        println(" parser errors:")
        erros.forEach {
            println("\t$it")
        }
    }

}

fun readResourceFile(resourceName: String): String {
    val classLoader = Thread.currentThread().contextClassLoader
    val resourceUrl: URL = classLoader.getResource(resourceName)!!
    val file = File(resourceUrl.toURI())
    return file.readText(Charsets.UTF_8)
}
