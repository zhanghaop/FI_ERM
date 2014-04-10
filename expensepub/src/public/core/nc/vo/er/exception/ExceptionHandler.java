package nc.vo.er.exception;

/**
 * �쳣ͳһ������
 * */
import java.sql.SQLException;

import nc.bs.logging.Log;
import nc.bs.logging.Logger;
import nc.vo.pub.BusinessException;
import nc.vo.pub.BusinessRuntimeException;

public class ExceptionHandler {

	/** �����쳣���ҷ�װ�쳣 */
	public static BusinessException handleException(Class cl, Exception e) throws BusinessException {
		Log.getInstance(cl).error(e.getMessage(), e);
		return handle(e);
	}
	/**�����쳣���ҷ�װ�쳣*/
	public static BusinessException handleException(Exception e) throws BusinessException {
		Logger.error(e.getMessage(), e);
		return handle(e);
	}
	
	/**�����쳣���ҷ�װ�쳣*/
	public static BusinessException handleExceptionRuntime(Exception e){
		Logger.error(e.getMessage(), e);
		return handleRuntimeException(e);
	}

	private static BusinessException handleRuntimeException(Exception ex) {
		if (ex instanceof SQLException) {
			throw new BusinessRuntimeException(ex.getMessage(), ex);
		} else if (ex instanceof BusinessRuntimeException) {
			// ���ex�Ѿ���FIBusinessRuntimeException����ֱ���׳���
			throw (BusinessRuntimeException) ex;
		} else if (ex instanceof BusinessException) {
			// ���ex�Ѿ���FIBusinessException��ֱ���׳���
			throw new BusinessRuntimeException(ex.getMessage(), ex);
		} else {
			throw new BusinessRuntimeException(ex.getMessage(), ex);
		}
	}
	/** �����쳣 */
	public static BusinessException createException(String sMsg, Throwable cause) {
		return cause == null ? new BusinessException(sMsg) : new BusinessException(sMsg, cause);
	}

	/** �����쳣 */
	public static BusinessException createException(String sMsg) {
		return new BusinessException(sMsg);
	}

	/** �������׳��쳣 */
	public static BusinessException cteateandthrowException(String sMsg) throws BusinessException {
		BusinessException e = new BusinessException(sMsg);
		throw e;
	}

	/** �����쳣 */
	public static BusinessException createException(Throwable cause) {
		return new BusinessException(cause.getMessage(),cause);
	}

	/** ��debug��ʽ�����־ */
	public static void debug(Object info) {
		Logger.debug(info);
	}

	/** ����쳣���Ҳ����׳� */
	public static void consume(Throwable throwable) {
		Logger.error(throwable.getMessage(), throwable);
	}

	/** ��error��ʽ�����־��ע�⣺info�������쳣��������쳣��Ҫ����consume */
	public static void error(Object info) {
		Logger.error(info);
	}

	private static BusinessException handle(Exception ex) throws BusinessException {
		if (ex instanceof SQLException) {
			throw new BusinessRuntimeException(ex.getMessage(), ex);
		} else if (ex instanceof BusinessRuntimeException) {
			// ���ex�Ѿ���FIBusinessRuntimeException����ֱ���׳���
			throw (BusinessRuntimeException) ex;
		} else if (ex instanceof BusinessException) {
			// ���ex�Ѿ���FIBusinessException��ֱ���׳���
			throw (BusinessException) ex;
		} else {
			throw new BusinessException(ex.getMessage(), ex);
		}
	}
}