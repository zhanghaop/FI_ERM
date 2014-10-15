package nc.ui.arap.bx.authorize;

import java.awt.Toolkit;


public interface IAuthorizeUIConst {

	/**
	 * BillCardPanelģ��ID �����÷������޴˰�����ʹ��
	 */
	static final String CARD_TEMPLATE_ID = "2011Z31000000000CARD";

	/**
	 * BillListPanelģ��ID �����÷������޴˰�����ʹ��
	 */
	static final String LIST_TEMPLATE_ID = "2011Z31000000000LIST";
	
	/**
	 * ˮƽ�ָ���λ��(��Ļ��ȵ�20%)
	 */
	public static final int H_MINIMINIZED_POSITION = (int)(Toolkit.getDefaultToolkit().getScreenSize().getWidth()*0.2);
	
	/**
	 * ��ֱ�ָ���λ��(��Ļ�߶ȵ�40%)
	 */
	public static final int V_MINIMINIZED_POSITION = (int)(Toolkit.getDefaultToolkit().getScreenSize().getHeight()*0.4);
	
	/**
	 * ��ʼ״̬
	 */
	public static final Integer STATUS_INIT = -1;
	
	/**
	 * ���״̬
	 */
	public static final Integer STATUS_BROWSE = 0;
	
	/**
	 * �༭״̬
	 */
	public static final Integer STATUS_EDIT = 1;
	
}
