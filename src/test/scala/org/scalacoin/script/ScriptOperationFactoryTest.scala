package org.scalacoin.script

import org.scalacoin.script.arithmetic.OP_1ADD
import org.scalacoin.script.bitwise.OP_EQUAL
import org.scalacoin.script.constant.{OP_1, OP_TRUE, OP_0}
import org.scalacoin.script.control.OP_IF
import org.scalacoin.script.crypto.OP_RIPEMD160
import org.scalacoin.script.locktime.OP_CHECKLOCKTIMEVERIFY
import org.scalacoin.script.stack.OP_TOALTSTACK
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

}