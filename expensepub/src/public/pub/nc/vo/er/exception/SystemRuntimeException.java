package nc.vo.er.exception;

/**
 * 数据库访问错误、或者程序代码错误产生的异常
 * 避免业务代码中出现不必要的异常处理代码
 *
 */
public class SystemRuntimeException extends RuntimeException {

  private static final long serialVersionUID = 4946018652993178290L;

  /**
   * SystemException 构造子注解。
   */
  public SystemRuntimeException() {
    super();
  }

  /**
   * SystemException 构造子注解。
   */
  public SystemRuntimeException(
      String message) {
    super(message);
  }

  public SystemRuntimeException(
      Exception ex) {
    super(ex);
  }

}