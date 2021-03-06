package org.scalacoin.script.splice

import org.scalacoin.script.constant.ScriptOperation

/**
 * Created by chris on 1/22/16.
 */
sealed trait SpliceOperation extends ScriptOperation


case object OP_CAT extends SpliceOperation {
  override def opCode =	126
}

case object OP_SUBSTR extends SpliceOperation {
  override def opCode = 127
}

case object OP_LEFT extends SpliceOperation {
  override def opCode = 128
}

case object OP_RIGHT extends SpliceOperation {
  override def opCode = 129
}

case object OP_SIZE extends SpliceOperation {
  override def opCode = 130
}