package code

import java.nio.ByteBuffer
import java.nio.ByteOrder

typealias Instructions = ByteArray

typealias OpCode = Byte

typealias Result<D, E> = Pair<D?, E?>

enum class OpCodeType(val byte: OpCode) {
    OpConstant(0);

    companion object {
        fun fromByte(byte: Byte): OpCodeType? {
            return values().find { it.byte == byte }
        }
    }
}

class Definition(val name: String, val operandWidths: Array<Int>)

private val definitions = mutableMapOf(
    OpCodeType.OpConstant to Definition(OpCodeType.OpConstant.name, arrayOf(2))
)

fun lookUp(op: OpCodeType): Result<Definition, String> {
    val def = definitions[op] ?: return Result(null, "opcode $op undefined")
    return Result(def, null)
}

fun lookUp(op: Byte): Result<Definition, String> {
    val opType = OpCodeType.fromByte(op) ?: return Result(null, "opcode $op undefined")
    val def = definitions[opType] ?: return Result(null, "opcode $op undefined")
    return Result(def, null)
}

fun make(op: OpCodeType, vararg operands: Int): Instructions {
    val (def, _) = lookUp(op)
    if (def == null) {
        return ByteArray(0)
    }

    val instructionLen = 1 + def.operandWidths.sum()
    val instructions = ByteArray(instructionLen)
    instructions[0] = op.byte
    var offset = 1
    operands.forEachIndexed { i, value ->
        val width = def.operandWidths[i]
        if (width == 2) {
            putUint16BigEndian(value, instructions, offset)
        }
        offset += width
    }
    return ByteArray(0)
}

fun string(ins: Instructions): String {
    var i = 0
    while (i < ins.size) {
        val (def, err) = lookUp(ins[i])
        if (err != null) {
            println("ERROR: $err")
            continue
        }
        val (operands, read) = readOperands(def!!, ins.slice(i + 1..<ins.size).toByteArray())
        println(String.format("%04d %s", i, fmtInstruction(ins, def, operands)))
        i += 1 + read
    }
    return ""
}

fun readOperands(def: Definition, ins: Instructions): Pair<IntArray, Int> {
    val operands = IntArray(def.operandWidths.size)
    var offset = 0
    def.operandWidths.forEachIndexed { i, width ->
        if (width == 2) {
            operands[i] = readUint16(ins.slice(offset..<ins.size).toByteArray())
        }
        offset += 2
    }
    return Pair(operands, offset)
}

fun fmtInstruction(ins: Instructions, def: Definition, operands: IntArray): String {
    TODO()
}

private fun putUint16BigEndian(value: Int, bytes: ByteArray, offset: Int = 0) {
    ByteBuffer.wrap(bytes, offset, 2).order(java.nio.ByteOrder.BIG_ENDIAN).putShort(value.toShort())
}

private fun readUint16(ins: Instructions): Int {
    val buffer = ByteBuffer.wrap(ins)
    buffer.order(ByteOrder.BIG_ENDIAN)
    return buffer.short.toInt()
}