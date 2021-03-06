/*
 * Copyright 2000-2015 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jetbrains.python.commandInterface.commandsWithArgs;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * For many commands we know nothing about arguments but their help text.
 * This strategy is for this case
 *
 * @author Ilya.Kazakevich
 */
public final class UnknownArgumentsInfo implements ArgumentsInfo {
  /**
   * Argument help text
   */
  @NotNull
  private final String myHelp;

  /**
   * @param allArgumentsHelpText argument help text
   */
  public UnknownArgumentsInfo(@NotNull final String allArgumentsHelpText) {
    myHelp = allArgumentsHelpText;
  }

  @Nullable
  @Override
  public Argument getArgument(final int argumentPosition) {
    return new Argument(myHelp); // We can't say argument does not exist.
  }

  @NotNull
  @Override
  public ArgumentsValuesValidationInfo validateArgumentValues(@NotNull final List<String> argumentValuesToCheck) {
    return ArgumentsValuesValidationInfo.NO_ERROR; // Actually, we have no idea
  }
}
