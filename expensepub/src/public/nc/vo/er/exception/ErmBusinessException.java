package nc.vo.er.exception;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import nc.vo.pub.BusinessException;

/**
 * 业务异常处理。
 */
public class ErmBusinessException extends BusinessException {
  private static final long serialVersionUID = -5506475841265255052L;

  private final String location;

  private final String stackTrace;

  public ErmBusinessException(
      String message) {
    super(message);
    this.location = null;
    this.stackTrace = null;
  }

  public ErmBusinessException(
      String message, String location) {
    super(message);
    this.location = location;
    this.stackTrace = null;
  }

  public ErmBusinessException(
      String message, String location, Throwable ex) {
    super(message);
    this.location = location;
    this.stackTrace = this.getStackTrace(ex);
  }

  public ErmBusinessException(
      String message, Throwable ex) {
    super(message);
    this.stackTrace = this.getStackTrace(ex);
    this.location = null;
  }

  public String getLocation() {
    return this.location;
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
