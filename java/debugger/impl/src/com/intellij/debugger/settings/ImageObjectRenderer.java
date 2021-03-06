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
package com.intellij.debugger.settings;

import com.intellij.debugger.engine.DebugProcess;
import com.intellij.debugger.engine.FullValueEvaluatorProvider;
import com.intellij.debugger.engine.evaluation.EvaluateException;
import com.intellij.debugger.engine.evaluation.EvaluationContext;
import com.intellij.debugger.engine.evaluation.EvaluationContextImpl;
import com.intellij.debugger.ui.impl.watch.ValueDescriptorImpl;
import com.intellij.debugger.ui.tree.ValueDescriptor;
import com.intellij.debugger.ui.tree.render.CompoundReferenceRenderer;
import com.intellij.debugger.ui.tree.render.DescriptorLabelListener;
import com.intellij.rt.debugger.ImageSerializer;
import com.intellij.ui.Gray;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.UIUtil;
import com.intellij.xdebugger.frame.XFullValueEvaluator;
import com.sun.jdi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.List;

/**
* Created by Egor on 04.10.2014.
*/
class ImageObjectRenderer extends CompoundReferenceRenderer implements FullValueEvaluatorProvider {
  public ImageObjectRenderer(final NodeRendererSettings rendererSettings) {
    super(rendererSettings, "Image", null, null);
    setClassName("java.awt.Image");
  }

  public String calcLabel(ValueDescriptor descriptor, EvaluationContext evaluationContext, DescriptorLabelListener listener) throws
                                                                                                                             EvaluateException {
    String res = calcToStringLabel(descriptor, evaluationContext, listener);
    if (res != null) {
      return res;
    }
    return super.calcLabel(descriptor, evaluationContext, listener);
  }

  @NotNull
  @Override
  public XFullValueEvaluator getFullValueEvaluator(final EvaluationContextImpl evaluationContext, final ValueDescriptorImpl valueDescriptor) {
    return new CustomPopupFullValueEvaluator(" (Show image)", evaluationContext) {
      @Override
      protected JComponent createComponent() {
        return createIconViewer(getIcon(myEvaluationContext, valueDescriptor.getValue(), "imageToBytes"));
      }
    };
  }

  static JComponent createIconViewer(Icon icon) {
    return new JBScrollPane(new JBLabel(icon){
      private BufferedImage myBackgroundImage = UIUtil.createImage(16, 16, BufferedImage.TYPE_INT_ARGB);
      {
        Graphics2D g = myBackgroundImage.createGraphics();
        g.setColor(new JBColor(Gray._255, Gray._135));
        g.fillRect(0, 0, 17, 17);
        g.setColor(new JBColor(Gray._191, Gray._83));
        g.fillRect(0, 0, 8, 8);
        g.fillRect(8, 8, 8, 8);
      }
      @Override
      public void paint(Graphics g) {
        ((Graphics2D)g).setPaint(new TexturePaint(myBackgroundImage, new Rectangle(0, 0, 16, 16)));
        g.fillRect(0, 0, getWidth()+1, getHeight() + 1);
        super.paint(g);
      }
    });

  }

  @Nullable
  static ImageIcon getIcon(EvaluationContext evaluationContext, Value obj, String methodName) {
    try {
      Value bytes = getImageBytes(evaluationContext, obj, methodName);
      byte[] data = readBytes(bytes);
      if (data != null) {
        return new ImageIcon(data);
      }
    }
    catch (Exception e) {
      return null;
    }
    return null;
  }

  private static Value getImageBytes(EvaluationContext evaluationContext, Value obj, String methodName)
    throws EvaluateException, ClassNotLoadedException, InvalidTypeException {
    DebugProcess process = evaluationContext.getDebugProcess();
    ClassType cls = (ClassType)process.findClass(evaluationContext, ImageSerializer.class.getName(),
                                                               evaluationContext.getClassLoader());

    if (cls != null) {
      List<Method> methods = cls.methodsByName(methodName);
      if (!methods.isEmpty()) {
        return process.invokeMethod(evaluationContext, cls, methods.get(0), Collections.singletonList(obj));
      }
    }
    return null;
  }

  private static byte[] readBytes(Value bytes) {
    if (bytes instanceof ArrayReference) {
      List<Value> values = ((ArrayReference)bytes).getValues();
      byte[] res = new byte[values.size()];
      int idx = 0;
      for (Value value : values) {
        if (value instanceof ByteValue) {
          res[idx++] = ((ByteValue)value).value();
        }
        else {
          return null;
        }
      }
      return res;
    }
    return null;
  }
}
