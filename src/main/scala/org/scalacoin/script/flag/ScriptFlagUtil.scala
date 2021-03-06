package org.scalacoin.script.flag

/**
 * Created by chris on 4/6/16.
 */
trait ScriptFlagUtil {

  /**
   * Checks if the strict encoding is required in the set of flags
   * given to us
   * @param flags
   * @return
   */
  def requiresStrictDerEncoding(flags : Seq[ScriptFlag]) : Boolean = {
    flags.contains(ScriptVerifyDerSig) || flags.contains(ScriptVerifyStrictEnc)
  }

  /**
   * Checks if the script flag for checklocktimeverify is enabled
   * @param flags
   * @return
   */
  def checkLockTimeVerifyEnabled(flags : Seq[ScriptFlag]) : Boolean = {
    flags.contains(ScriptVerifyCheckLocktimeVerify)
  }

  /**
   * Checks to see if the script flag is set to discourage NOPs that are not in use
   * NOPs are used by soft forks to repurpose NOPs to actual functionality such as checklocktimeverify
   * See BIP65 for an example of this
   * @param flags
   * @return
   */
  def discourageUpgradableNOPs(flags : Seq[ScriptFlag]) : Boolean = {
    flags.contains(ScriptVerifyDiscourageUpgradableNOPs)
  }
}


object ScriptFlagUtil extends ScriptFlagUtil
