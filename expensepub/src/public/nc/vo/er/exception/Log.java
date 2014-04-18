package nc.vo.er.exception;


import nc.bs.logging.Logger;

/**
 * 日志操作类
 * @author 钟鸣
 *
 * 2007-6-29 下午03:42:15
 */
public class Log {
  private static Log instance = new Log();

  private Log() {
    //缺省构造方法
  }

  public static Log getInstance() {
    return Log.instance;
  }

  public void error(Throwable ex) {
    if (ex instanceof TransferException) {
      TransferException exception = (TransferException) ex;
      Logger.error(exception.getInnerStackTrace());
    }
    else {
      Logger.error(ex.getMessage(), ex);
    }
  }

  public void info(String message) {
    Logger.info(message);
  }

  public void info(Object obj) {
    Logger.error(obj);
  }

  public void debug(String message) {
    Logger.debug(message);
  }
}
