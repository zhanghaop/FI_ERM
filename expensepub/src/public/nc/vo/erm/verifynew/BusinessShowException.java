/*
 * 创建日期 2005-9-1
 *
 * TODO 要更改此生成的文件的模板，请转至
 * 窗口 － 首选项 － Java － 代码样式 － 代码模板
 */
package nc.vo.erm.verifynew;

import nc.vo.pub.BusinessException;
;

/**
 * @author xuhb 
 *
 * TODO 要更改此生成的类型注释的模板，请转至
 * 窗口 － 首选项 － Java － 代码样式 － 代码模板
 */
public class BusinessShowException extends BusinessException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * BusinessShowException 构造子注解。
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
