package org.scalacoin.script.reserved

import org.scalacoin.script.constant.ScriptOperation

/**
 * Created by chris on 1/22/16.
 */

/**
 * Reserved words
 * Any opcode not assigned is also reserved. Using an unassigned opcode makes the transaction invalid.
 * https://en.bitcoin.it/wiki/Script#Reserved_words
 */
sealed trait ReservedOperation extends ScriptOperation

/**
 * Transaction is invalid unless occuring in an unexecuted OP_IF branch
 */
case object OP_RESERVED extends ReservedOperation {
  override def opCode = 80
}

/**
 * Transaction is invalid unless occuring in an unexecuted OP_IF branch
 */
case object OP_VER extends ReservedOperation {
  override def opCode = 98
}

/**
 * Transaction is invalid even when occuring in an unexecuted OP_IF branch
 */
case object OP_VERIF extends ReservedOperation {
  override def opCode = 101
}

/**
 * 	Transaction is invalid even when occuring in an unexecuted OP_IF branch
 */
case object OP_VERNOTIF extends ReservedOperation {
  override def opCode = 102
}

/**
 * Transaction is invalid unless occuring in an unexecuted OP_IF branch
 */
case object OP_RESERVED1 extends ReservedOperation {
  override def opCode = 137
}

/**
 * Transaction is invalid unless occuring in an unexecuted OP_IF branch
 */
case object OP_RESERVED2 extends  ReservedOperation {
  override def opCode = 138
}

case object OP_NOP1 extends ReservedOperation {
  override def opCode = 176
}

case object OP_NOP3 extends ReservedOperation {
  override def opCode = 178
}
case object OP_NOP4 extends ReservedOperation {
  override def opCode = 179
}
case object OP_NOP5 extends ReservedOperation {
  override def opCode = 180
}
case object OP_NOP6 extends ReservedOperation {
  override def opCode = 181
}
case object OP_NOP7 extends ReservedOperation {
  override def opCode = 182
}
case object OP_NOP8 extends ReservedOperation {
  override def opCode = 183
}
case object OP_NOP9 extends ReservedOperation {
  override def opCode = 184
}
case object OP_NOP10 extends ReservedOperation {
  override def opCode = 185
}

case class UndefinedOP_NOP(opCode : Int) extends ReservedOperation

