package nc.ui.erm.util;

import javax.swing.JComponent;

import nc.funcnode.ui.AbstractFunclet;
import nc.ui.uif2.model.AbstractAppModel;

public class TransTypeUtil {

	public static final String PK_TRANSTYPE = "pk_transtype";
	public static final String TRANSTYPE = "transtype";  
	
	/**
	 * 从AbstractFunclet获取参数pk_transtype
	 * 
	 * @param model
	 * @return
	 * @author: wangyhh@ufida.com.cn
	 */
	public static String getPk_transtype(AbstractAppModel model) {
		JComponent comp = model.getContext().getEntranceUI();
		if (comp instanceof AbstractFunclet) {
			return ((AbstractFunclet) comp).getParameter(PK_TRANSTYPE);
		}
		return null;
	}
	
	
	/**
	 * 从AbstractFunclet获取参数transtype
	 * 
	 * @param model
	 * @return
	 * @author: wangyhh@ufida.com.cn
	 */
	public static String getTranstype(AbstractAppModel model) {
		JComponent comp = model.getContext().getEntranceUI();
		if (comp instanceof AbstractFunclet) {
			return ((AbstractFunclet) comp).getParameter(TRANSTYPE);
		}
		return null;
	}

}
