package org.jetbrains.rpc;

import com.intellij.util.BooleanFunction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jsonProtocol.Request;

import static org.jetbrains.rpc.CommandProcessor.LOG;

public abstract class MessageWriter implements BooleanFunction<Request> {
  @Override
  public boolean fun(@NotNull Request message) {
    CharSequence content = message.toJson();
    if (isDebugLoggingEnabled()) {
      LOG.debug("OUT: " + content.toString());
    }
    return write(content);
  }

  protected boolean isDebugLoggingEnabled() {
    return LOG.isDebugEnabled();
  }

  protected abstract boolean write(@NotNull CharSequence content);
}