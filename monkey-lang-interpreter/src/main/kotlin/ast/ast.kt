package ast

interface Node {
    /**
     * just for debug and test
     */
    fun tokenLiteral(): String

    fun string(): String
}

interface Statement: Node {
    fun statementNode()
}

interface Expression: Node {
    fun expressionNode()
}


