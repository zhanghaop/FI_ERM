package nc.ui.er.djlx;

import java.util.Hashtable;

import nc.ui.pub.beans.ValueChangedListener;
import nc.vo.er.djlx.BillTypeVO;
import nc.vo.pub.BusinessException;

public interface IDjlxModel {

	public abstract void addDataChangeListener(ValueChangedListener vl);

	public abstract void removeDataChangeListener(ValueChangedListener vl);

	public abstract Hashtable getDjdlCache();

	/**
	 * 得到单据大类的单据类型
	 * */
	public abstract BillTypeVO[] getBilltypesbyDjdl(String djdl);

	public abstract String[] getDjdlName();

	public abstract String[] getDjdlCode();
	
	public abstract String getbm()throws BusinessException;
	
	public void insertbm(String bm);

}