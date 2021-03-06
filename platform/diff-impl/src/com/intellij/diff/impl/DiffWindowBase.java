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
package com.intellij.diff.impl;

import com.intellij.diff.DiffDialogHints;
import com.intellij.diff.util.DiffUserDataKeys;
import com.intellij.diff.util.DiffUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.WindowWrapper;
import com.intellij.openapi.ui.WindowWrapperBuilder;
import com.intellij.openapi.util.Disposer;
import com.intellij.util.ImageLoader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class DiffWindowBase {
  @Nullable protected final Project myProject;
  @NotNull protected final DiffDialogHints myHints;

  private DiffRequestProcessor myProcessor;
  private WindowWrapper myWrapper;

  public DiffWindowBase(@Nullable Project project, @NotNull DiffDialogHints hints) {
    myProject = project;
    myHints = hints;
  }

  protected void init() {
    if (myWrapper != null) return;

    myProcessor = createProcessor();

    String dialogGroupKey = myProcessor.getContextUserData(DiffUserDataKeys.DIALOG_GROUP_KEY);
    if (dialogGroupKey == null) dialogGroupKey = "DiffContextDialog";

    myWrapper = new WindowWrapperBuilder(DiffUtil.getWindowMode(myHints), myProcessor.getComponent())
      .setProject(myProject)
      .setParent(myHints.getParent())
      .setDimensionServiceKey(dialogGroupKey)
      .setOnShowCallback(new Runnable() {
        @Override
        public void run() {
          myProcessor.updateRequest();
          myProcessor.requestFocus(); // TODO: not needed for modal dialogs. Make a flag in WindowWrapperBuilder ?
        }
      })
      .build();
    myProcessor.init();
    myWrapper.setImage(ImageLoader.loadFromResource("/diff/Diff.png"));
    Disposer.register(myWrapper, myProcessor);
  }

  public void show() {
    init();
    myWrapper.show();
  }

  @NotNull
  protected abstract DiffRequestProcessor createProcessor();

  //
  // Delegate
  //

  protected void setWindowTitle(@NotNull String title) {
    myWrapper.setTitle(title);
  }

  protected void onAfterNavigate() {
    DiffUtil.closeWindow(myWrapper.getWindow(), true, true);
  }

  //
  // Getters
  //

  protected WindowWrapper getWrapper() {
    return myWrapper;
  }

  protected DiffRequestProcessor getProcessor() {
    return myProcessor;
  }
}
