package nc.itf.er.prv;

import java.util.Hashtable;

import nc.vo.er.djlx.BillTypeVO;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.pub.BusinessException;

public interface IArapBillTypePrivate {

	BillTypeVO updateBillType(BillTypeVO billtypevo) throws BusinessException;

	boolean checkBillTypeUnique(DjLXVO billtypeVo) throws BusinessException;

	Hashtable deleteCorpsBillType(BillTypeVO billtypeVo, String[] pk_corps) throws BusinessException;

	Hashtable insertBillType2Corps(BillTypeVO billtypevo, String[] pk_corps) throws BusinessException;

	BillTypeVO[] queryBillType(String pk_corp) throws BusinessException;

	BillTypeVO[] queryBillTypeByBillTypeCode(String billtypeCode, String pk_corp) throws BusinessException;

	DjLXVO queryByJC(DjLXVO billtypevo) throws BusinessException;

	DjLXVO[] queryBillTypesForTreeNode(DjLXVO billtypeVo) throws BusinessException;

	DjLXVO[] getBillTypesByWhere(String condition) throws BusinessException;
	
	/**
	 * 新增交易类型
	 * 
	 * 新增er_djlx，新增出对应的模板分配信息
	 * 
	 * @param djlx
	 * @throws BusinessException
	 */
	public void insertBillType(DjLXVO djlx) throws BusinessException;
	/**
	 * 删除交易类型
	 * 
	 * 删除er_djlx，删除对应的模板分配信息
	 * 
	 * @param djlx
	 * @throws BusinessException
	 */
	public void deleteBillType(DjLXVO djlx) throws BusinessException;
}
