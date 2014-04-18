package nc.vo.er.exception;


import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import nc.vo.pub.BusinessException;

/**
 * @author 钟鸣
 * 
 * 将后台的异常堆栈传递到前台，而不至于造成后台的异常直接传递到前台后，由于前台
 * 没有相关的异常类，而导致反序列化失败
 *
 * 2007-6-29 下午04:00:41
 */
public class TransferException extends BusinessException {
  private static final long serialVersionUID = -1175141188691689620L;

  private final String stackTrace;

  public TransferException(
      Throwable exception) {
    super(exception.getMessage());
    this.stackTrace = this.getStackTrace(exception);
  }

  public String getInnerStackTrace() {
    return this.stackTrace;
  }

  private String getStackTrace(Throwable ex) {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw, true);
    ex.printStackTrace(pw);
    return sw.getBuffer().toString();
  }

  @Override
  public void printStackTrace() {
    if (this.stackTrace != null) {
      this.printStackTrace(System.err);
    }
    else {
      super.printStackTrace();
    }
  }

  @Override
  public void printStackTrace(PrintStream s) {
    if (this.stackTrace != null) {
      synchronized (s) {
        s.print(this.stackTrace);
      }
    }
    else {
      super.printStackTrace(s);
    }
  }

  @Override
  public void printStackTrace(PrintWriter s) {
    if (this.stackTrace != null) {
      synchronized (s) {
        s.print(this.stackTrace);
      }
    }
    else {
      super.printStackTrace(s);
    }
  }

}

