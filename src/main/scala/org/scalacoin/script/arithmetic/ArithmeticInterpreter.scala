package org.scalacoin.script.arithmetic

import org.scalacoin.script.control.{ControlOperationsInterpreter, OP_VERIFY}
import org.scalacoin.script.error.{ScriptErrorUnknownError, ScriptErrorInvalidStackOperation}
import org.scalacoin.script.{ScriptProgram}
import org.scalacoin.script.constant._
import org.scalacoin.util.BitcoinSUtil

/**
 * Created by chris on 1/25/16.
 */
trait ArithmeticInterpreter extends ControlOperationsInterpreter {


  /**
   * a is added to b
   * @param program
   * @return
   */
  def opAdd(program : ScriptProgram) : ScriptProgram = {
    require(program.script.headOption.isDefined && program.script.head == OP_ADD, "Script top must be OP_ADD")
    performBinaryArithmeticOperation(program, (x,y) => x + y)
  }

  /**
   * Increments the stack top by 1
   * @param program
   * @return
   */
  def op1Add(program : ScriptProgram) : ScriptProgram = {
    require(program.script.headOption.isDefined && program.script.head == OP_1ADD, "Script top must be OP_1ADD")
    performUnaryArithmeticOperation(program, x => x + ScriptNumberFactory.one)
  }

  /**
   * Decrements the stack top by 1
   * @param program
   * @return
   */
  def op1Sub(program : ScriptProgram) : ScriptProgram = {
    require(program.script.headOption.isDefined && program.script.head == OP_1SUB, "Script top must be OP_1SUB")
    performUnaryArithmeticOperation(program, x => x - ScriptNumberFactory.one )
  }


  /**
   * b is subtracted from a.
   * @param program
   * @return
   */
  def opSub(program : ScriptProgram) : ScriptProgram = {
    require(program.script.headOption.isDefined && program.script.head == OP_SUB, "Script top must be OP_SUB")
    performBinaryArithmeticOperation(program, (x,y) => y - x)
  }

  /**
   * Takes the absolute value of the stack top
   * @param program
   * @return
   */
  def opAbs(program : ScriptProgram) : ScriptProgram = {
    require(program.script.headOption.isDefined && program.script.head == OP_ABS, "Script top must be OP_ABS")
    performUnaryArithmeticOperation(program, x => ScriptNumberFactory.fromNumber(x.num.abs))
  }

  /**
   * Negates the stack top
   * @param program
   * @return
   */
  def opNegate(program : ScriptProgram) : ScriptProgram = {
    require(program.script.headOption.isDefined && program.script.head == OP_NEGATE, "Script top must be OP_NEGATE")
    performUnaryArithmeticOperation(program, x => x -)
  }

  /**
   * If the input is 0 or 1, it is flipped. Otherwise the output will be 0.
   * @param program
   * @return
   */
  def opNot(program : ScriptProgram) : ScriptProgram = {
    require(program.script.headOption.isDefined && program.script.head == OP_NOT, "Script top must be OP_NOT")
    //TODO: this needs to be modified to have an exhaustive type check
    performUnaryArithmeticOperation(program, x => if (program.stackTopIsFalse) OP_TRUE else OP_FALSE)
  }

  /**
   * Returns 0 if the input is 0. 1 otherwise.
   * @param program
   * @return
   */
  def op0NotEqual(program : ScriptProgram) : ScriptProgram = {
    require(program.script.headOption.isDefined && program.script.head == OP_0NOTEQUAL, "Script top must be OP_0NOTEQUAL")
    performUnaryArithmeticOperation(program, x => if(x.num == 0) OP_FALSE else OP_TRUE)
  }


  /**
   * 	If both a and b are not 0, the output is 1. Otherwise 0.
   * @param program
   * @return
   */
  def opBoolAnd(program : ScriptProgram) : ScriptProgram = {
    require(program.script.headOption.isDefined && program.script.head == OP_BOOLAND, "Script top must be OP_BOOLAND")
    performBinaryBooleanOperation(program,(x,y) => {
      val xIsFalse = (x == ScriptNumberFactory.zero || x == OP_0)
      val yIsFalse = (y == ScriptNumberFactory.zero || y == OP_0)
      if (xIsFalse || yIsFalse) false else true
    })

  }

  /**
   * If a or b is not 0, the output is 1. Otherwise 0.
   * @param program
   * @return
   */
  def opBoolOr(program : ScriptProgram) : ScriptProgram = {
    require(program.script.headOption.isDefined && program.script.head == OP_BOOLOR, "Script top must be OP_BOOLOR")
    performBinaryBooleanOperation(program, (x,y) => {
      if (x == y && (x == ScriptNumberFactory.zero || x == OP_0)) false else true
    })
  }

  /**
   * Returns 1 if the numbers are equal, 0 otherwise.
   * @param program
   * @return
   */
  def opNumEqual(program : ScriptProgram) : ScriptProgram = {
    require(program.script.headOption.isDefined && program.script.head == OP_NUMEQUAL, "Script top must be OP_NUMEQUAL")
    performBinaryBooleanOperation(program,(x,y) => x.numEqual(y))
  }


  /**
   * Same as OP_NUMEQUAL, but runs OP_VERIFY afterward.
   * @param program
   * @return
   */
  def opNumEqualVerify(program : ScriptProgram) : ScriptProgram = {
    require(program.script.headOption.isDefined && program.script.head == OP_NUMEQUALVERIFY,
      "Script top must be OP_NUMEQUALVERIFY")
    if (program.stack.size < 2) {
      logger.error("OP_NUMEQUALVERIFY requires two stack elements")
      ScriptProgram(program,ScriptErrorInvalidStackOperation)
    } else {
      val numEqualProgram = ScriptProgram(program, program.stack, OP_NUMEQUAL :: program.script.tail)
      val numEqualResult = opNumEqual(numEqualProgram)
      val verifyProgram = ScriptProgram(numEqualResult, numEqualResult.stack, OP_VERIFY :: numEqualResult.script)
      val verifyResult = opVerify(verifyProgram)
      verifyResult
    }

  }


  /**
   * 	Returns 1 if the numbers are not equal, 0 otherwise.
   * @param program
   * @return
   */
  def opNumNotEqual(program : ScriptProgram) : ScriptProgram = {
    require(program.script.headOption.isDefined && program.script.head == OP_NUMNOTEQUAL,
      "Script top must be OP_NUMNOTEQUAL")
    performBinaryBooleanOperation(program, (x,y) => {
      x.num != y.num
    })
  }


  /**
   * 	Returns 1 if a is less than b, 0 otherwise.
   * @param program
   * @return
   */
  def opLessThan(program : ScriptProgram) : ScriptProgram = {
    require(program.script.headOption.isDefined && program.script.head == OP_LESSTHAN,
      "Script top must be OP_LESSTHAN")
    performBinaryBooleanOperation(program, (x,y) => y < x)
  }


  /**
   * 	Returns 1 if a is greater than b, 0 otherwise.
   * @param program
   * @return
   */
  def opGreaterThan(program : ScriptProgram) : ScriptProgram = {
    require(program.script.headOption.isDefined && program.script.head == OP_GREATERTHAN,
      "Script top must be OP_GREATERTHAN")
    performBinaryBooleanOperation(program, (x,y) => y > x)
  }

  /**
   * Returns 1 if a is less than or equal to b, 0 otherwise.
   * @param program
   * @return
   */
  def opLessThanOrEqual(program : ScriptProgram) : ScriptProgram = {
    require(program.script.headOption.isDefined && program.script.head == OP_LESSTHANOREQUAL,
      "Script top must be OP_LESSTHANOREQUAL")
    performBinaryBooleanOperation(program, (x,y) => y <= x)
  }

  /**
   *	Returns 1 if a is greater than or equal to b, 0 otherwise.
   * @param program
   * @return
   */
  def opGreaterThanOrEqual(program : ScriptProgram) : ScriptProgram = {
    require(program.script.headOption.isDefined && program.script.head == OP_GREATERTHANOREQUAL,
      "Script top must be OP_GREATERTHANOREQUAL")
    performBinaryBooleanOperation(program, (x,y) => y >= x)
  }


  /**
   * Returns the smaller of a and b.
   * @param program
   * @return
   */
  def opMin(program : ScriptProgram) : ScriptProgram = {
    require(program.script.headOption.isDefined && program.script.head == OP_MIN,
      "Script top must be OP_MIN")

    if (program.stack.size < 2) {
      logger.error("OP_MIN requires at least two stack elements")
      ScriptProgram(program,ScriptErrorInvalidStackOperation)
    } else {
      val b = program.stack.head
      val a = program.stack.tail.head
      val isLessThanOrEqual = (a,b) match {
        case (x : ScriptNumberOperation, y : ScriptNumber) => x.num <= y.num
        case (x : ScriptNumber, y : ScriptNumberOperation) => x.num <= y.num
        case (x,y) => x.toLong <= y.toLong
      }

      val newStackTop = if (isLessThanOrEqual) a else b
      ScriptProgram(program, newStackTop :: program.stack.tail.tail, program.script.tail)
    }

  }


  /**
   * Returns the larger of a and b.
   * @param program
   * @return
   */
  def opMax(program : ScriptProgram) : ScriptProgram = {
    require(program.script.headOption.isDefined && program.script.head == OP_MAX,
      "Script top must be OP_MAX")
    if (program.stack.size < 2) {
      logger.error("OP_MAX requires at least two stack elements")
      ScriptProgram(program,ScriptErrorInvalidStackOperation)
    } else {
      val b = program.stack.head
      val a = program.stack.tail.head

      val isGreaterThanOrEqual = (a,b) match {
        case (x : ScriptNumberOperation, y : ScriptNumber) => x.num >= y.num
        case (x : ScriptNumber, y : ScriptNumberOperation) => x.num >= y.num
        case (x,y) => x.toLong >= y.toLong
      }

      val newStackTop = if (isGreaterThanOrEqual) a else b
      ScriptProgram(program, newStackTop :: program.stack.tail.tail, program.script.tail)
    }


  }


  /**
   * Returns 1 if x is within the specified range (left-inclusive), 0 otherwise.
   * @param program
   * @return
   */
  def opWithin(program : ScriptProgram) : ScriptProgram = {
    require(program.script.headOption.isDefined && program.script.head == OP_WITHIN,
      "Script top must be OP_WITHIN")
    if (program.stack.size < 3) {
      logger.error("OP_WITHIN requires at least 3 elements on the stack")
      ScriptProgram(program,ScriptErrorInvalidStackOperation)
    } else {
      val c = program.stack.head
      val b = program.stack.tail.head
      val a = program.stack.tail.tail.head

      val isWithinRange = (a,b,c) match {
        case (x : ScriptNumber, y : ScriptNumber, z : ScriptNumber) => x >= y && x < z
        case (x,y,z) => x.toLong >= y.toLong && x.toLong < z.toLong
      }

      val newStackTop = if (isWithinRange) OP_TRUE else OP_FALSE
      ScriptProgram(program, newStackTop :: program.stack.tail.tail.tail, program.script.tail)
    }

  }


  /**
   * This function checks if a number is <= 4 bytes in size
   * We cannot perform arithmetic operations on bitcoin numbers that are larger than 4 bytes.
   * https://github.com/bitcoin/bitcoin/blob/a6a860796a44a2805a58391a009ba22752f64e32/src/script/script.h#L214-L239
   * @param scriptNumber the script number to be checked
   * @return if the number is larger than 4 bytes
   */
  private def checkBitcoinIntByteSize(scriptNumber : ScriptNumber) : Boolean = scriptNumber.bytes.size <= 4


  /**
   * Performs the given arithmetic operation on the stack head
   * @param program the program whose stack top is used as an argument for the arithmetic operation
   * @param op the arithmetic ooperation that needs to be executed on the number, for instance incrementing by 1
   * @return the program with the result from performing the arithmetic operation pushed onto the top of the stack
   */
  private def performUnaryArithmeticOperation(program : ScriptProgram, op : ScriptNumber => ScriptNumber) : ScriptProgram = {
    program.stack.headOption match {
      case None =>
        logger.error("We need one stack element for performing a unary arithmetic operation")
        ScriptProgram(program,ScriptErrorInvalidStackOperation)
      case Some(s : ScriptNumber) =>
        if (checkBitcoinIntByteSize(s)) {
          val newScriptNumber = op(s)
          ScriptProgram(program, newScriptNumber :: program.stack.tail, program.script.tail)
        }
        else {
          logger.error("Cannot perform arithmetic operation on a number larger than 4 bytes, here is the number: " + s)
          //pretty sure that an error is thrown inside of CScriptNum which in turn is caught by interpreter.cpp here
          //https://github.com/bitcoin/bitcoin/blob/master/src/script/interpreter.cpp#L999-L1002
          ScriptProgram(program,ScriptErrorUnknownError)
        }
      case Some(s : ScriptConstant) =>
        val interpretedNumber = ScriptNumberFactory.fromNumber(BitcoinSUtil.hexToLong(s.hex))
        val newProgram = ScriptProgram(program, interpretedNumber ::  program.stack.tail, ScriptProgram.Stack)
        performUnaryArithmeticOperation(newProgram, op)
      case Some(s : ScriptToken) =>
        //pretty sure that an error is thrown inside of CScriptNum which in turn is caught by interpreter.cpp here
        //https://github.com/bitcoin/bitcoin/blob/master/src/script/interpreter.cpp#L999-L1002
        logger.error("Stack top must be a script number to perform an arithmetic operation")
        ScriptProgram(program,ScriptErrorUnknownError)
    }
  }

  /**
   * Performs the given arithmetic operation on the top two stack items
   * @param program the program whose stack top is used as an argument for the arithmetic operation
   * @param op the arithmetic ooperation that needs to be executed on the number, for instance incrementing by 1
   * @return the program with the result from performing the arithmetic operation pushed onto the top of the stack
   */
  private def performBinaryArithmeticOperation(program : ScriptProgram, op : (ScriptNumber, ScriptNumber) => ScriptNumber) : ScriptProgram = {
    if (program.stack.size < 2) {
      logger.error("We must have two elements to perform a binary arithmetic operation")
      ScriptProgram(program,ScriptErrorInvalidStackOperation)
    } else {
      (program.stack.head, program.stack.tail.head) match {
        case (x : ScriptNumber, y : ScriptNumber) =>
          if (checkBitcoinIntByteSize(x) && checkBitcoinIntByteSize(y)) {
            val newStackTop = op(x,y)
            ScriptProgram(program,newStackTop :: program.stack.tail.tail,program.script.tail)
          }
          else {
            //pretty sure that an error is thrown inside of CScriptNum which in turn is caught by interpreter.cpp here
            //https://github.com/bitcoin/bitcoin/blob/master/src/script/interpreter.cpp#L999-L1002
            logger.error("Cannot perform arithmetic operation on a number larger than 4 bytes, one of these two numbers is larger than 4 bytes: " + x + " " + y)
            ScriptProgram(program,ScriptErrorUnknownError)
          }
        case (x : ScriptConstant, y : ScriptNumber) =>
          //interpret x as a number
          val interpretedNumber = ScriptNumberFactory.fromNumber(BitcoinSUtil.hexToLong(x.hex))
          val newProgram = ScriptProgram(program, interpretedNumber ::  program.stack.tail, ScriptProgram.Stack)
          performBinaryArithmeticOperation(newProgram, op)
        case (x : ScriptNumber, y : ScriptConstant) =>
          //interpret y as a number
          val interpretedNumber = ScriptNumberFactory.fromNumber(BitcoinSUtil.hexToLong(y.hex))
          val newProgram = ScriptProgram(program, x :: interpretedNumber ::  program.stack.tail, ScriptProgram.Stack)
          performBinaryArithmeticOperation(newProgram, op)
        case (x : ScriptConstant, y : ScriptConstant) =>
          //interpret x and y as a number
          val interpretedNumberX = ScriptNumberFactory.fromNumber(BitcoinSUtil.hexToLong(x.hex))
          val interpretedNumberY = ScriptNumberFactory.fromNumber(BitcoinSUtil.hexToLong(y.hex))
          val newProgram = ScriptProgram(program, interpretedNumberX :: interpretedNumberY ::  program.stack.tail.tail, ScriptProgram.Stack)
          performBinaryArithmeticOperation(newProgram, op)
        case (x : ScriptToken, y : ScriptToken) =>
          //pretty sure that an error is thrown inside of CScriptNum which in turn is caught by interpreter.cpp here
          //https://github.com/bitcoin/bitcoin/blob/master/src/script/interpreter.cpp#L999-L1002
          logger.error("The top two stack items must be script numbers to perform an arithmetic operation")
          ScriptProgram(program,ScriptErrorUnknownError)
      }
    }


  }

  /**
   * Compares two script numbers with the given boolean operation
   * @param program the program whose two top stack elements are used for the comparison
   * @param op the operation which compares the two script numbers
   * @return the program with either ScriptFalse or ScriptTrue on the stack top
   */
  private def performBinaryBooleanOperation(program : ScriptProgram, op : (ScriptNumber, ScriptNumber) => Boolean) : ScriptProgram = {

    if (program.stack.size < 2) {
      logger.error("We need two stack elements for a binary boolean operation")
      ScriptProgram(program,ScriptErrorInvalidStackOperation)
    } else {
      (program.stack.head, program.stack.tail.head) match {
        case (x : ScriptNumber, y : ScriptNumber) =>
          if (checkBitcoinIntByteSize(x) && checkBitcoinIntByteSize(y)) {
            val newStackTop = if(op(x,y)) OP_TRUE else OP_FALSE
            ScriptProgram(program,newStackTop :: program.stack.tail.tail,program.script.tail)
          }
          else {
            //pretty sure that an error is thrown inside of CScriptNum which in turn is caught by interpreter.cpp here
            //https://github.com/bitcoin/bitcoin/blob/master/src/script/interpreter.cpp#L999-L1002
            logger.error("Cannot perform boolean operation on a number larger than 4 bytes, one of these two numbers is larger than 4 bytes: " + x + " " + y)
            ScriptProgram(program,ScriptErrorUnknownError)
          }
        case (x : ScriptConstant, y : ScriptNumber) =>
          //interpret x as a number
          val interpretedNumber = ScriptNumberFactory.fromNumber(BitcoinSUtil.hexToLong(x.hex))
          val newProgram = ScriptProgram(program, interpretedNumber ::  program.stack.tail, ScriptProgram.Stack)
          performBinaryBooleanOperation(newProgram, op)
        case (x : ScriptNumber, y : ScriptConstant) =>
          //interpret y as a number
          val interpretedNumber = ScriptNumberFactory.fromNumber(BitcoinSUtil.hexToLong(y.hex))
          val newProgram = ScriptProgram(program, x :: interpretedNumber ::  program.stack.tail, ScriptProgram.Stack)
          performBinaryBooleanOperation(newProgram, op)
        case (x : ScriptConstant, y : ScriptConstant) =>
          //interpret x and y as a number
          val interpretedNumberX = ScriptNumberFactory.fromNumber(BitcoinSUtil.hexToLong(x.hex))
          val interpretedNumberY = ScriptNumberFactory.fromNumber(BitcoinSUtil.hexToLong(y.hex))
          val newProgram = ScriptProgram(program, interpretedNumberX :: interpretedNumberY ::  program.stack.tail.tail, ScriptProgram.Stack)
          performBinaryBooleanOperation(newProgram, op)
        case (x : ScriptToken, y : ScriptToken) =>
          //pretty sure that an error is thrown inside of CScriptNum which in turn is caught by interpreter.cpp here
          //https://github.com/bitcoin/bitcoin/blob/master/src/script/interpreter.cpp#L999-L1002
          logger.error("The top two stack items must be script numbers to perform an arithmetic operation")
          ScriptProgram(program,ScriptErrorUnknownError)
      }
    }


  }
}
