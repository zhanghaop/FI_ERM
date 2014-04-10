package nc.ui.arap.bx.authorize;

import java.awt.Toolkit;


public interface IAuthorizeUIConst {

	/**
	 * BillCardPanel模版ID 特殊用法，仅限此包名内使用
	 */
	static final String CARD_TEMPLATE_ID = "2011Z31000000000CARD";

	/**
	 * BillListPanel模版ID 特殊用法，仅限此包名内使用
	 */
	static final String LIST_TEMPLATE_ID = "2011Z31000000000LIST";
	
	/**
	 * 水平分割栏位置(屏幕宽度的20%)
	 */
	public static final int H_MINIMINIZED_POSITION = (int)(Toolkit.getDefaultToolkit().getScreenSize().getWidth()*0.2);
	
	/**
	 * 垂直分割栏位置(屏幕高度的40%)
	 */
	public static final int V_MINIMINIZED_POSITION = (int)(Toolkit.getDefaultToolkit().getScreenSize().getHeight()*0.4);
	
	/**
	 * 初始状态
	 */
	public static final Integer STATUS_INIT = -1;
	
	/**
	 * 浏览状态
	 */
	public static final Integer STATUS_BROWSE = 0;
	
	/**
	 * 编辑状态
	 */
	public static final Integer STATUS_EDIT = 1;
	
}
