/**
 * @(#)ArapBillTypeImpl.java	V5.0 2005-11-9
 *
 * Copyright 1988-2005 UFIDA, Inc. All Rights Reserved.
 *
 * This software is the proprietary information of UFSoft, Inc.
 * Use is subject to license terms.
 *
 */

package nc.impl.er.djlx;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Vector;

import javax.naming.NamingException;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.er.callouter.FipCallFacade;
import nc.bs.er.djlx.DjLXBO;
import nc.bs.er.djlx.DjLXDMO;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.logging.Log;
import nc.bs.pf.pub.PfDataCache;
import nc.bs.pub.SystemException;
import nc.itf.er.prv.IArapBillTypePrivate;
import nc.itf.er.pub.IArapBillTypePublic;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.er.djlx.BillTypeVO;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.BusinessRuntimeException;
import nc.vo.pub.billtype.BilltypeVO;
import nc.vo.pub.pftemplate.SystemplateBaseVO;
import nc.vo.pub.pftemplate.SystemplateVO;
import nc.vo.pub.template.TemplateLayer;

/**
 * <p>
 * 类的主要说明。类设计的目标，完成什么样的功能。
 * </p>
 * <p>
 * <Strong>主要的类使用：</Strong>
 * <ul>
 * <li>如何使用该类</li>
 * <li>是否线程安全</li>
 * <li>并发性要求</li>
 * <li>使用约束</li>
 * <li>其他</li>
 * </ul>
 * </p>
 * <p>
 * <Strong>已知的BUG：</Strong>
 * <ul>
 * <li></li>
 * </ul>
 * </p>
 * 
 * <p>
 * <strong>修改历史：</strong>
 * <ul>
 * <li>
 * <ul>
 * <li><strong>修改人:</strong>st</li>
 * <li><strong>修改日期：</strong>2005-11-9</li>
 * <li><strong>修改内容：<strong></li>
 * </ul>
 * </li>
 * <li> </li>
 * </ul>
 * </p>
 * 
 * @author st
 * @version V5.0
 * @since V3.1
 */

public class ArapBillTypeImpl implements IArapBillTypePrivate, IArapBillTypePublic {

	/**
	 * 
	 */
	public ArapBillTypeImpl() {
		super();
		//
	}

	/**
	 * @see nc.itf.er.prv.IArapBillTypePrivate#updateBillType(nc.vo.er.djlx.DjLXVO, nc.vo.er.djlx.DjlxtempletVO[])
	 */
	public BillTypeVO updateBillType(BillTypeVO billtypevo) throws BusinessException {
		//
		DjLXVO djlx = (DjLXVO) billtypevo.getParentVO();
		// DjlxtempletVO[] templetvos = (DjlxtempletVO[])billtypevo.getChildrenVO();
		checkUnique(djlx);
		BaseDAO dao = new BaseDAO();
		dao.updateVO(djlx);
		// dao.updateVOArray(templetvos);
		return billtypevo;
		// new DjLXBO().update(djlx,templetvos);
	}

	/**
	 * @see nc.itf.er.prv.IArapBillTypePrivate#checkBillTypeUnique(nc.vo.er.djlx.DjLXVO)
	 */
	public boolean checkBillTypeUnique(DjLXVO billtypeVo) throws BusinessException {
		//
		return checkUnique(billtypeVo);
	}

	/**
	 * @see nc.itf.er.prv.IArapBillTypePrivate#deleteCorpsBillType(nc.vo.er.djlx.DjLXVO, java.lang.String[])
	 */
	public Hashtable deleteCorpsBillType(BillTypeVO billtypeVo, String[] pk_corps) throws BusinessException {
		//
		return new DjLXBO().deleteCorps(billtypeVo, pk_corps);
	}

	/**
	 * @see nc.itf.er.prv.IArapBillTypePrivate#insertBillType2Corps(nc.vo.er.djlx.DjLXVO, nc.vo.er.djlx.DjlxtempletVO[], java.lang.String[])
	 */
	public Hashtable insertBillType2Corps(BillTypeVO billtypevo, String[] pk_corps) throws BusinessException {
		DjLXVO djlx = (DjLXVO) billtypevo.getParentVO();
		// DjlxtempletVO[] templetVos = (DjlxtempletVO[]) billtypevo.getChildrenVO();
		try {
			;
			Hashtable corpcode = checkDjLX(djlx, pk_corps);
			for (int i = 0; i < pk_corps.length; i++) {
				if (corpcode.get(pk_corps[i]) != null)
					continue;
				djlx.setDwbm(pk_corps[i]);
				insertDjlx(djlx);
			}
			return corpcode;
		} catch (BusinessException e) {
			Log.getInstance(this.getClass()).error(e.getMessage());
			throw e;
		}
	}

	/**
	 * @see nc.itf.er.prv.IArapBillTypePrivate#queryBillType(java.lang.String)
	 */
	public BillTypeVO[] queryBillType(String pk_corp) throws BusinessException {

		BillTypeVO[] djLXs = null;
		Vector<BillTypeVO> vec = new Vector<BillTypeVO>();
		DjLXVO[] headers = null;
		BaseDAO dao = new BaseDAO();
		Collection<DjLXVO> cl = dao.retrieveByClause(DjLXVO.class, "dwbm='" + pk_corp + "'");
		headers = new DjLXVO[cl.size()];
		headers = cl.toArray(headers);
		if (headers == null || headers.length == 0)
			return djLXs;

		for (int i = 0; i < headers.length; i++) {
			BillTypeVO atype = new BillTypeVO();
			atype.setParentVO(headers[i]);
			vec.addElement(atype);
		}
		if (vec.size() > 0) {
			djLXs = new BillTypeVO[vec.size()];
			vec.copyInto(djLXs);

		}
		return djLXs;
	}

	/**
	 * @see nc.itf.er.prv.IArapBillTypePrivate#queryBillTypeByBillTypeCode(java.lang.String, java.lang.String)
	 */
	public BillTypeVO[] queryBillTypeByBillTypeCode(String billtypeCode, String pk_group) throws BusinessException {
		String sWhere = "";
		if (billtypeCode != null) {
			sWhere += "and djlxbm='" + billtypeCode + "' ";
		}
		if (pk_group != null)
			sWhere += "and pk_group='" + pk_group + "' ";
		if (sWhere.length() > 1) {
			sWhere = sWhere.substring(3);
		}
		BillTypeVO[] djLXs = null;
		Vector<BillTypeVO> vec = new Vector<BillTypeVO>();
		DjLXVO[] headers = null;
		BaseDAO dao = new BaseDAO();
		Collection<DjLXVO> cl = dao.retrieveByClause(DjLXVO.class, sWhere);
		headers = new DjLXVO[cl.size()];
		headers = cl.toArray(headers);
		if (headers == null || headers.length == 0)
			return djLXs;

		for (int i = 0; i < headers.length; i++) {
			BillTypeVO atype = new BillTypeVO();
			atype.setParentVO(headers[i]);

			vec.addElement(atype);
		}
		if (vec.size() > 0) {
			djLXs = new BillTypeVO[vec.size()];
			vec.copyInto(djLXs);

		}
		return djLXs;
	}

	/**
	 * @see nc.itf.er.prv.IArapBillTypePrivate#queryByJC(nc.vo.er.djlx.DjLXVO)
	 */
	public DjLXVO queryByJC(DjLXVO vo) throws BusinessException {
		String sWhere = "dwbm='" + vo.getDwbm() + "' and djlxjc='" + vo.getDjlxjc() + "' and dr=0 ";
		BaseDAO dao = new BaseDAO();
		Collection cl = dao.retrieveByClause(DjLXVO.class, sWhere);
		DjLXVO[] djlxs =  (DjLXVO[]) changeCollection2Array(cl, DjLXVO.class);
		if (djlxs != null) {
			return djlxs[0];
		} else {
			return null;
		}
	}

	public DjLXVO[] queryByWhereStr(String where) throws BusinessException {
		return new DjLXBO().queryByWhereStr(where);
	}

	/**
	 * @see nc.itf.er.prv.IArapBillTypePrivate#queryBillTypesForTreeNode(nc.vo.er.djlx.DjLXVO)
	 */
	public DjLXVO[] queryBillTypesForTreeNode(DjLXVO billtypeVo) throws BusinessException {
		//
		return new DjLXBO().queryForTreeNode(billtypeVo);
	}

	/**
	 * @see nc.itf.er.pub.IArapBillTypePublic#queryAllBillTypeByCorp(java.lang.String)
	 */
	public DjLXVO[] queryAllBillTypeByCorp(String pk_corp) throws BusinessException {
		//

		return new DjLXBO().queryAll(pk_corp);
	}

	/**
	 * @see nc.itf.er.pub.IArapBillTypePublic#deleteBillType(nc.vo.er.djlx.DjLXVO)
	 */
	public void deleteBillType(BillTypeVO billtypevo) throws BusinessException {
		DjLXVO vo = (DjLXVO) billtypevo.getParentVO();
		BaseDAO dao = new BaseDAO();
		dao.deleteByPK(DjLXVO.class, vo.getDjlxoid());
		String strBm = vo.getDjlxbm();
		new FipCallFacade().deleteBill(strBm);
	}

	/**
	 * *方法说明: *参数: *返回值: ***注意点*** *@author：屈淑轩 *创建日期：(2001-12-19 13:15:21)
	 */
	private boolean isRefered(DjLXVO vo) {
		BaseDAO dao = new BaseDAO();
		try {
			Collection cl = dao.retrieveByClause(DjLXVO.class, "dwbm <>'GLOBLE00000000000000' and djlxbm ='" + vo.getDjlxbm() + "'");
			if (cl.size() > 0) {
				return true;
			} else {
				return false;
			}

		} catch (DAOException e) {
			throw new BusinessRuntimeException(e.getMessage(),e);
		}
	}

	/**
	 * @see nc.itf.er.pub.IArapBillTypePublic#insertBillType(nc.vo.er.djlx.DjLXVO, nc.vo.er.djlx.DjlxtempletVO[])
	 */
	public BillTypeVO insertBillType(BillTypeVO billtypevo) throws BusinessException {
		DjLXVO djlx = (DjLXVO) billtypevo.getParentVO();
		// DjlxtempletVO[] templetvos = (DjlxtempletVO[])billtypevo.getChildrenVO();
		Vector<String> parentID = new Vector<String>();
		Vector<String> childID = new Vector<String>();
  
		
		parentID.addElement(BXConstans.BILLTYPECODE_CLFJK);
		parentID.addElement(BXConstans.BILLTYPECODE_CLFBX);
		childID.addElement(BXConstans.JK_DJDL);
		childID.addElement(BXConstans.BX_DJDL);
 
		try {
			checkUnique(djlx);
			djlx.setDr(0);
			insertDjlx(djlx);
			/** 传递 */
			String strDestID = djlx.getDjlxbm();
			String djdl = djlx.getDjdl();
			int index = childID.indexOf(djdl);
			String strSourceID = parentID.elementAt(index).toString();
			String djmc = djlx.getDjlxjc();
			//FIXME
			BilltypeVO srcBillTypeVo = PfDataCache.getBillTypeInfo(strSourceID);
//			.findBillTypeByKey(strSourceID);
//			BilltypeVO srcBillTypeVo = null;
			BilltypeVO desBillTypeVo = (BilltypeVO) srcBillTypeVo.clone();
			desBillTypeVo.setPk_billtypecode(strDestID);
			desBillTypeVo.setBilltypename(djmc);
			
			/*拷贝生成自定义交易类型单据
			 * @parame strDestID
			 * 		   strSourceID
			 */
			copybill(strDestID, strSourceID);
			
		}  catch (BusinessException e) {
			Log.getInstance(this.getClass()).error(e.getMessage(), e);
			throw e;
		}
		return billtypevo;
	}
	
	@SuppressWarnings("unchecked")
	private void copybill(String strDestID, String strSourceID) throws DAOException {
		BaseDAO baseDAO =  new BaseDAO();
		Collection<SystemplateVO> sourcevos = baseDAO.retrieveByClause(SystemplateVO.class, " pk_corp='"+InvocationInfoProxy.getInstance().getGroupId()+"' and  funnode='" +BXConstans.BXMNG_NODECODE +"' and nodekey='"+ strSourceID+"'");
		//单据管理
		for(SystemplateVO sysvo :sourcevos){
			sysvo.setNodekey(strDestID);
			baseDAO.insertVO(sysvo);
		}
				
		//期初单据
		for(SystemplateVO sysvo :sourcevos){
			if(strSourceID.startsWith("263")){
				sysvo.setFunnode(BXConstans.BXLR_QCCODE);
				baseDAO.insertVO(sysvo);
			}	
		}
		//常用单据
		for(SystemplateVO sysvo :sourcevos){
			sysvo.setFunnode(BXConstans.BXINIT_NODECODE);
			baseDAO.insertVO(sysvo);
		}
		//常用单据业务单元节点
		for(SystemplateVO sysvo :sourcevos){
			sysvo.setFunnode(BXConstans.BXINIT_NODECODE_U);
			baseDAO.insertVO(sysvo);
		}
		//常用单据集团节点
		for(SystemplateVO sysvo :sourcevos){
			sysvo.setFunnode(BXConstans.BXINIT_NODECODE_G);
			baseDAO.insertVO(sysvo);
		}
		
		//单据查询节点
		for(SystemplateVO sysvo :sourcevos){
			sysvo.setFunnode(BXConstans.BXBILL_QUERY);
			baseDAO.insertVO(sysvo);
		}
	}

	public void copyDefused(BilltypeVO source,BilltypeVO target ) throws DAOException {
	}

	private void insertDjlx(DjLXVO djlx) throws DAOException, BusinessException {
		BaseDAO dao = new BaseDAO();
		dao.insertVO(djlx);
	}

	/**
	 * @see nc.itf.er.pub.IArapBillTypePublic#getDjlxvoByDjlxbm(java.lang.String, java.lang.String)
	 */
	public DjLXVO getDjlxvoByDjlxbm(String billTypeCode, String pk_group) throws BusinessException {
		BillTypeVO[] billtypes = queryBillTypeByBillTypeCode(billTypeCode, pk_group);
		if (billtypes == null || billtypes[0] == null) {
			throw new BusinessException(nc.bs.ml.NCLangResOnserver.getInstance().getStrByID("2006", "UPP2006-000390")/* @res "当前公司没有分配该单据类型！" */);
		}
		return (DjLXVO) billtypes[0].getParentVO();
	}

	/**
	 * @see nc.itf.er.prv.IArapBillTypePrivate#getBillTypesByWhere(java.lang.String)
	 */
	public DjLXVO[] getBillTypesByWhere(String condition) throws BusinessException {
		BaseDAO dao = new BaseDAO();
		if (condition != null && (condition.startsWith("where"))) {
			condition = condition.substring(5);
		}
		Collection cl = dao.retrieveByClause(DjLXVO.class, condition);
		return  (DjLXVO[]) changeCollection2Array(cl, DjLXVO.class);
	}

	/**
	 * 此处插入方法说明。 创建日期：(2001-8-29 10:29:48)
	 * 
	 * @user
	 * @return boolean
	 * @param vo
	 *            nc.vo.arap.djlx.DjLXVO
	 * @exception java.sql.SQLException
	 *                异常说明。
	 */
	private boolean checkUnique(DjLXVO vo) throws BusinessException {
		DjLXDMO dmo;
		try {
			dmo = new DjLXDMO();
			return dmo.checkUnique(vo);
		} catch (SystemException e) {
			Log.getInstance(this.getClass()).error(e);
			throw new BusinessRuntimeException(e.getMessage());
		} catch (NamingException e) {
			Log.getInstance(this.getClass()).error(e);
			throw new BusinessRuntimeException(e.getMessage());
		}
	}

	// 检查要插入的单据类型该公司是否已经存在 2004-8-3 xhb
	private Hashtable checkDjLX(DjLXVO djlx, String[] corps) throws BusinessException {
		Hashtable<String, String> corpcode = new Hashtable<String, String>();

		BillTypeVO[] vos = queryBillTypeByBillTypeCode(djlx.getDjlxbm(), null);
		Hashtable<String, BillTypeVO> hash = new Hashtable<String, BillTypeVO>();

		if (vos != null) {
			for (int i = 0; i < vos.length; i++) {
				hash.put(((DjLXVO) vos[i].getParentVO()).getDwbm(), vos[i]);
			}
			for (int i = 0; i < corps.length; i++) {
				if (hash.get(corps[i]) != null) {
					corpcode.put(corps[i], corps[i]);
				}
			}
		}

		return corpcode;
	}
	
	private Object changeCollection2Array(Collection cl, Class sClass) {
		if (cl.isEmpty()) {
			return null;
		}
		Object[] o = (Object[]) Array.newInstance(sClass, cl.size());
		o = cl.toArray(o);
		return o;

	}

}
