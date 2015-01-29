package nc.erm.mobile.eventhandler;

import nc.vo.pub.BusinessException;

/**
 * 
 * @author gaotn
 *
 */

public abstract class AbstractEditeventListener {
	
	public static final int ALL_ROW = -99;
	public static final int HEAD_ROW = -1;
	
	protected Object getBodyValueAt(JsonVoTransform vo,int row,String key) {
		return vo.getBodyValueAt(row, key);
	}
	
	protected Object getBodyValueAt(JsonVoTransform vo,String tab,int row,String key) {
		return vo.getBodyValueAt(tab, row, key);
	}
	protected Object getHeadValue(JsonVoTransform vo, String key) {
		return vo.getHeadValue(key);
	}
	
	//public abstract void process(JsonVoTransform jsonVoTransform) throws BusinessException;

}
