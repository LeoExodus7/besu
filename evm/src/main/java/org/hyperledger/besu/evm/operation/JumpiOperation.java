/*
 * Copyright ConsenSys AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package org.hyperledger.besu.evm.operation;

import org.hyperledger.besu.evm.Code;
import org.hyperledger.besu.evm.EVM;
import org.hyperledger.besu.evm.frame.ExceptionalHaltReason;
import org.hyperledger.besu.evm.frame.MessageFrame;
import org.hyperledger.besu.evm.gascalculator.GasCalculator;

import java.util.Optional;

import org.apache.tuweni.bytes.Bytes;

public class JumpiOperation extends AbstractFixedCostOperation {

  private final OperationResult invalidJumpResponse;
  private final OperationResult jumpResponse;

  public JumpiOperation(final GasCalculator gasCalculator) {
    super(0x57, "JUMPI", 2, 0, 1, gasCalculator, gasCalculator.getHighTierGasCost());
    invalidJumpResponse =
        new Operation.OperationResult(
            Optional.of(gasCost), Optional.of(ExceptionalHaltReason.INVALID_JUMP_DESTINATION));
    jumpResponse = new OperationResult(Optional.of(gasCost), Optional.empty(), 0);
  }

  @Override
  public OperationResult executeFixedCostOperation(final MessageFrame frame, final EVM evm) {
    final Bytes dest = frame.popStackItem().trimLeadingZeros();
    final Bytes condition = frame.popStackItem();

    // If condition is zero (false), no jump is will be performed. Therefore skip the test.
    if (condition.isZero()) {
      return successResponse;
    } else {
      final int jumpDestination;
      try {
        jumpDestination = dest.toInt();
      } catch (RuntimeException re) {
        return invalidJumpResponse;
      }
      final Code code = frame.getCode();
      if (!evm.isValidJumpDestination(jumpDestination, code)) {
        return invalidJumpResponse;
      }
      frame.setPC(jumpDestination);
      return jumpResponse;
    }
  }
}
