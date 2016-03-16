package org.scalacoin.protocol.transaction

import org.scalacoin.marshallers.transaction.{RawTransactionParser, RawTransactionInputParser}
import org.scalacoin.util.Factory

/**
 * Created by chris on 2/21/16.
 */
trait TransactionFactory extends Factory[Transaction] { this : Transaction =>


  /**
   * Updates a transaction outputs
   * @param updatedOutputs
   * @return
   */
  def factory(updatedOutputs : UpdateTransactionOutputs) : Transaction = {
    TransactionImpl(version,inputs,updatedOutputs.outputs,lockTime)
  }

  /**
   * Updates a transaction's inputs
   * @param updatedInputs
   * @return
   */
  def factory(updatedInputs : UpdateTransactionInputs) : Transaction = {
    TransactionImpl(version,updatedInputs.inputs,outputs,lockTime)
  }


  /**
   * Removes the inputs of the transactions
   * @return
   */
  def emptyInputs : Transaction = TransactionImpl(version,Seq(),outputs,lockTime)

  /**
   * Removes the outputs of the transactions
   * @return
   */
  def emptyOutputs : Transaction = TransactionImpl(version,inputs,Seq(),lockTime)

  def empty : Transaction = TransactionImpl(TransactionConstants.version,Seq(),Seq(),TransactionConstants.lockTime)

  /**
   * Creates a transaction object from a sequence of bytes
   * @param bytes
   * @return
   */
  def factory(bytes : Seq[Byte]) : Transaction = fromBytes(bytes)

  /**
   * Creates a transction object from its hexadecimal representation
   * @param hex
   * @return
   */
  def factory(hex : String) : Transaction = fromHex(hex)


  def factory(bytes : Array[Byte]) : Transaction = fromBytes(bytes.toSeq)

  def fromBytes(bytes : Seq[Byte]) : Transaction = RawTransactionParser.read(bytes)

}

sealed trait TransactionFactoryHelper
case class UpdateTransactionOutputs(outputs : Seq[TransactionOutput]) extends TransactionFactoryHelper
case class UpdateTransactionInputs(inputs : Seq[TransactionInput]) extends TransactionFactoryHelper