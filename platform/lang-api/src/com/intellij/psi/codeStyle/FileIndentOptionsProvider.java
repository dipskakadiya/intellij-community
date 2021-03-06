/*
 * Copyright 2000-2014 JetBrains s.r.o.
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
package com.intellij.psi.codeStyle;

import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author Rustam Vishnyakov
 */
public abstract class FileIndentOptionsProvider {

  public final static ExtensionPointName<FileIndentOptionsProvider> EP_NAME = ExtensionPointName.create("com.intellij.fileIndentOptionsProvider");
  /**
   * Retrieves indent options for PSI file.
   * @param settings Code style settings for which indent options are calculated.
   * @param file The file to retrieve options for.
   * @return Indent options or <code>null</code> if the provider can't retrieve them.
   */
  @Nullable
  public abstract CommonCodeStyleSettings.IndentOptions getIndentOptions(@NotNull CodeStyleSettings settings, @NotNull PsiFile file);

  /**
   * Tells if the provider can be used when a complete file is reformatted.
   * @return True by default
   */
  public boolean useOnFullReformat() {
    return true;
  }

  /**
   * @return A name which will be used to notify a user on overwritten indent options. If the option is <code>null</code>, no notification
   * will be shown.
   */
  @Nullable
  public String getDisplayName() {
    return null;
  }

  /**
   * @return The icon to be displayed in a notification message, can be <coe>null</coe> (no icon).
   */
  @Nullable
  public Icon getIcon() {
    return null;
  }

  /**
   * @return <code>True</code> if the provider can be disabled (default is <code>false</code>).
   */
  public boolean canBeDisabled() {
    return false;
  }

  /**
   * Disables the provider.
   * @param project The project to disable the provider for.
   */
  public void disable(@NotNull Project project) {
  }

  /**
   * Tells if there should not be any notification for this specific file.
   * @param file  The file to check.
   * @return <code>true</code> if the file can be silently accepted without a warning.
   */
  public boolean isAcceptedWithoutWarning(@SuppressWarnings("UnusedParameters") @NotNull VirtualFile file) {
    return false;
  }

  /**
   * Sets the file as accepted by end user.
   * @param file The file to be accepted. A particular implementation of <code>FileIndentOptionsProvider</code> may ignore this parameter
   *             and set a global acceptance flag so that no notification will be shown anymore.
   */
  public void setAccepted(@SuppressWarnings("UnusedParameters") @NotNull VirtualFile file) {}
}
