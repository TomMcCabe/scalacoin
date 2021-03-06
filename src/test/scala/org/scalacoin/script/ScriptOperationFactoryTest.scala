package org.scalacoin.script

import org.scalacoin.script.arithmetic.OP_1ADD
import org.scalacoin.script.bitwise.OP_EQUAL
import org.scalacoin.script.constant._
import org.scalacoin.script.control.OP_IF
import org.scalacoin.script.crypto.OP_RIPEMD160
import org.scalacoin.script.locktime.OP_CHECKLOCKTIMEVERIFY
import org.scalacoin.script.splice.OP_SUBSTR
import org.scalacoin.script.stack.OP_TOALTSTACK
import org.scalacoin.util.{BitcoinSUtil}
import org.scalatest.{FlatSpec, MustMatchers}

/**
 * Created by chris on 1/9/16.
 */
class ScriptOperationFactoryTest extends FlatSpec with MustMatchers {

  "ScriptOperationFactory" must "match operations with their byte representation" in {
    ScriptOperationFactory.fromByte(0x00) must be (Some(OP_0))
    ScriptOperationFactory.fromByte(0x51) must be (Some(OP_1))

    ScriptOperationFactory.fromByte(0x63) must be (Some(OP_IF))
    ScriptOperationFactory.fromByte(0x6b) must be (Some(OP_TOALTSTACK))

    ScriptOperationFactory.fromByte(135.toByte) must be (Some(OP_EQUAL))

    ScriptOperationFactory.fromByte(139.toByte) must be (Some(OP_1ADD))

    ScriptOperationFactory.fromByte(166.toByte) must be (Some(OP_RIPEMD160))
    ScriptOperationFactory.fromByte(177.toByte) must be (Some(OP_CHECKLOCKTIMEVERIFY))

  }

  it must "find OP_4 from it's byte representation" in {
    val byteRepresentation = BitcoinSUtil.decodeHex("54").head
    ScriptOperationFactory.fromByte(byteRepresentation) must be (Some(OP_4))
  }

  it must "find a byte to push onto stack from its byte representation" in {
    val result = ScriptOperationFactory.fromByte(2.toByte)
    result.isDefined must be (true)
    result.get must be (BytesToPushOntoStackImpl(2))
  }

  it must "find a script number from its hex representation" in {
    val result = ScriptOperationFactory.fromHex("02")
    result.isDefined must be (true)
    result.get must be (BytesToPushOntoStackImpl(2))
  }

  it must "find undefined op codes"in {
    val result = ScriptOperationFactory.fromHex("ba")
    result.isDefined must be (true)
  }

  it must "find a splice operation from it's hex representation" in {
    val spliceOperation = ScriptOperationFactory.fromHex("7F")
    spliceOperation.isDefined must be (true)
    spliceOperation.get must be (OP_SUBSTR)
  }

  it must "find OP_1NEGATE from its hex representation" in {
    val negateOperation = ScriptOperationFactory.fromHex("4f")
    negateOperation.isDefined must be (true)
    negateOperation.get must be (OP_1NEGATE)
  }

}
