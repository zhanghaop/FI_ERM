package nc.vo.er.exception;

/**
 * ���ݿ���ʴ��󡢻��߳���������������쳣
 * ����ҵ������г��ֲ���Ҫ���쳣�������
 *
 */
public class SystemRuntimeException extends RuntimeException {

  private static final long serialVersionUID = 4946018652993178290L;

  /**
   * SystemException ������ע�⡣
   */
  public SystemRuntimeException() {
    super();
  }

  /**
   * SystemException ������ע�⡣
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