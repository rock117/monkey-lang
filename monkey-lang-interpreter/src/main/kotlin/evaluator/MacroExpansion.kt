package evaluator

import Object_
import ast.*
import `object`.Environment
import `object`.Macro
import `object`.Quote

fun defineMacros(program: Program, env: Environment) {
    val definitions = mutableListOf<Int>()

    program.statements.forEachIndexed { i, statement ->
        if (isMacroDefinition(statement)) {
            addMacro(statement, env)
            definitions.add(i)
        }
    }

    for (i in definitions.size - 1 downTo 0) {
        val definitionIndex = definitions[i]
        program.statements = (program.statements.subList(0, definitionIndex) + program.statements.subList(definitionIndex + 1, program.statements.size)).toMutableList()
    }
}

fun expandMacros(program: Node, env: Environment): Node {
    return modify(program) { node ->
        if(node !is CallExpression) {
            return@modify node
        }
        val callExpression = node
        val macro: Object_? = isMacroCall(callExpression, env)
        if(macro !is Macro) {
            return@modify node
        }
        val args:List<Quote> = quoteArgs(callExpression)
        val evalEnv:Environment = extendMacroEnv(macro, args)
        val quote = eval(macro.body, evalEnv)
        if(quote !is Quote) {
            throw Exception("we only support returing AST-nodes from macros")
        }
        quote.node
    }!!
}

fun extendMacroEnv(macro: Macro, args: List<Quote>): Environment {
    val extended  = Environment.newEnclosingEnvironment(macro.env)
    macro.parameters.forEachIndexed { index, param ->
        extended.set(param.value, args[index])
    }
    return extended
}

fun quoteArgs(exp: CallExpression): List<Quote> {
    val args = mutableListOf<Quote>()
    for(arg in exp.arguments) {
        args.add(Quote(arg))
    }
    return args
}

private fun isMacroCall(exp: CallExpression, env: Environment): Object_? {
    if (exp.function !is Identifier) {
        return null
    }
    val identifier = exp.function
    val macro = env.get(identifier.value) ?: return null
    if (macro !is Macro) {
        return null
    }
    return macro
}


private fun addMacro(statement: Statement, env: Environment) {
    val letStatement = statement as LetStatement
    val macroLiteral = letStatement.value as MacroLiteral
    val macro = Macro(macroLiteral.parameters,macroLiteral.body,  env)
    env.set(letStatement.name.value, macro)
}

private fun isMacroDefinition(node: Statement): Boolean {
    if(node !is LetStatement) {
        return false
    }
    return node.value is MacroLiteral
}