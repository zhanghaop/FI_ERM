/*
 * �������� 2005-9-1
 *
 * TODO Ҫ���Ĵ����ɵ��ļ���ģ�壬��ת��
 * ���� �� ��ѡ�� �� Java �� ������ʽ �� ����ģ��
 */
package nc.vo.erm.verifynew;

import nc.vo.pub.BusinessException;
;

/**
 * @author xuhb 
 *
 * TODO Ҫ���Ĵ����ɵ�����ע�͵�ģ�壬��ת��
 * ���� �� ��ѡ�� �� Java �� ������ʽ �� ����ģ��
 */
public class BusinessShowException extends BusinessException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * BusinessShowException ������ע�⡣
     * @param s java.lang.String
     */
    public BusinessShowException(String s) {
    	super(s);
    }
    /**
     * @since  nc3.1
     */
    public BusinessShowException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @since  nc3.1
     */
    public BusinessShowException(Throwable cause) {
        super(cause);
    }

}
