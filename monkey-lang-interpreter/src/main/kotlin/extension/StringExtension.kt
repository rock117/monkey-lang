package extension

import ast.Operator


fun String.toOperator(): Operator {
    return Operator.`+`
}