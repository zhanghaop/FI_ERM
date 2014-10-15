package nc.vo.er.exception;

/**
 * 异常统一处理类
 * */
import java.sql.SQLException;

import nc.bs.logging.Log;
import nc.bs.logging.Logger;
import nc.vo.pub.BusinessException;
import nc.vo.pub.BusinessRuntimeException;

public class ExceptionHandler {

	/** 处理异常并且封装异常 */
	@SuppressWarnings("rawtypes")
	public static BusinessException handleException(Class cl, Exception e) throws BusinessException {
		Log.getInstance(cl).error(e.getMessage(), e);
		return handle(e);
	}
	/**处理异常并且封装异常*/
	public static BusinessException handleException(Exception e) throws BusinessException {
		Logger.error(e.getMessage(), e);
		return handle(e);
	}
	
	/**处理异常并且封装异常*/
	public static BusinessException handleExceptionRuntime(Exception e){
		Logger.error(e.getMessage(), e);
		return handleRuntimeException(e);
	}

	private static BusinessException handleRuntimeException(Exception ex) {
		if (ex instanceof SQLException) {
			throw new BusinessRuntimeException(ex.getMessage(), ex);
		} else if (ex instanceof BusinessRuntimeException) {
			// 如果ex已经是FIBusinessRuntimeException，则直接抛出。
			throw (BusinessRuntimeException) ex;
		} else if (ex instanceof BusinessException) {
			// 如果ex已经是FIBusinessException则直接抛出。
			throw new BusinessRuntimeException(ex.getMessage(), ex);
		} else {
			throw new BusinessRuntimeException(ex.getMessage(), ex);
		}
	}
	/** 创建异常 */
	public static BusinessException createException(String sMsg, Throwable cause) {
		return cause == null ? new BusinessException(sMsg) : new BusinessException(sMsg, cause);
	}

	/** 创建异常 */
	public static BusinessException createException(String sMsg) {
		return new BusinessException(sMsg);
	}

	/** 创建再抛出异常 */
	public static BusinessException cteateandthrowException(String sMsg) throws BusinessException {
		BusinessException e = new BusinessException(sMsg);
		throw e;
	}

	/** 创建异常 */
	public static BusinessException createException(Throwable cause) {
		return new BusinessException(cause.getMessage(),cause);
	}

	/** 以debug方式输出日志 */
	public static void debug(Object info) {
		Logger.debug(info);
	}

	/** 输出异常并且不再抛出 */
	public static void consume(Throwable throwable) {
		Logger.error(throwable.getMessage(), throwable);
	}

	/** 以error方式输出日志。注意：info不能是异常。如果是异常需要调用consume */
	public static void error(Object info) {
		Logger.error(info);
	}

	private static BusinessException handle(Exception ex) throws BusinessException {
		if (ex instanceof SQLException) {
			throw new BusinessRuntimeException(ex.getMessage(), ex);
		} else if (ex instanceof BusinessRuntimeException) {
			// 如果ex已经是FIBusinessRuntimeException，则直接抛出。
			throw (BusinessRuntimeException) ex;
		} else if (ex instanceof BusinessException) {
			// 如果ex已经是FIBusinessException则直接抛出。
			throw (BusinessException) ex;
		} else {
			throw new BusinessException(ex.getMessage(), ex);
		}
	}
}