package ast

class Program(val statements: MutableList<Statement>): Node {
    override fun tokenLiteral(): String {
        if(this.statements.isNotEmpty()) {
            return this.statements[0].tokenLiteral()
        } else {
            return ""
        }
    }

    override fun string(): String {
        return this.statements.joinToString("") { it.string() }
    }

}