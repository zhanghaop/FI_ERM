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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import javax.naming.NamingException;

import nc.bs.bd.cache.CacheProxy;
import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.er.djlx.DjLXBO;
import nc.bs.er.djlx.DjLXDMO;
import nc.bs.erm.common.ErmBillConst;
import nc.bs.erm.util.CacheUtil;
import nc.bs.erm.util.ErUtil;
import nc.bs.logging.Log;
import nc.bs.pf.pub.PfDataCache;
import nc.bs.pub.SystemException;
import nc.bs.pub.pf.PfUtilTools;
import nc.bs.trade.billsource.IBillDataFinder;
import nc.bs.trade.billsource.IBillFinder;
import nc.impl.pubapp.linkquery.BillTypeSetBillFinder;
import nc.itf.er.prv.IArapBillTypePrivate;
import nc.itf.er.pub.IArapBillTypePublic;
import nc.jdbc.framework.processor.BaseProcessor;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.er.djlx.BillTypeVO;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.erm.costshare.CostShareVO;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.fi.pub.SqlUtils;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.BusinessRuntimeException;
import nc.vo.pub.billtype.BilltypeVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.pftemplate.SystemplateBaseVO;
import nc.vo.pub.pftemplate.SystemplateVO;
import nc.vo.sm.funcreg.ParamRegVO;
import nc.vo.trade.billsource.LightBillVO;

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
 * @author st
 * @version V5.0
 * @since V3.1
 */

public class ArapBillTypeImpl implements IArapBillTypePrivate,
		IArapBillTypePublic {

	public ArapBillTypeImpl() {
		super();
	}

	/**
	 * @see nc.itf.er.prv.IArapBillTypePrivate#updateBillType(nc.vo.er.djlx.DjLXVO,
	 *      nc.vo.er.djlx.DjlxtempletVO[])
	 */
	public BillTypeVO updateBillType(BillTypeVO billtypevo)
			throws BusinessException {
		//
		DjLXVO djlx = (DjLXVO) billtypevo.getParentVO();
		
		try {
			checkUpdate(djlx);
			checkMatypeNotNull(djlx);
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
		}
		
		checkUnique(djlx);
		BaseDAO dao = new BaseDAO();
		dao.updateVO(djlx);
		CacheProxy.fireDataUpdated(djlx.getTableName());
		return billtypevo;
	}

	@SuppressWarnings("unchecked")
	private void checkUpdate(DjLXVO billtypevo) throws Exception {
		String djlxbm = billtypevo.getDjlxbm();// 校验申请单
		if (billtypevo != null && djlxbm.startsWith("261")) {
			DjLXVO[] djlxVos = CacheUtil.getValueFromCacheByWherePart(DjLXVO.class, "djlxbm = '" + djlxbm
					+ "' and pk_group = '" + billtypevo.getPk_group() + "'");

			if (djlxVos != null && djlxVos.length > 0) {
				Integer newMatype = billtypevo.getMatype() == null ? Integer.valueOf(0) : billtypevo.getMatype();
				Integer oldMatype = djlxVos[0].getMatype() == null ? Integer.valueOf(0) : djlxVos[0].getMatype();

				if (!newMatype.equals(oldMatype)) {
					BaseDAO baseDao = new BaseDAO();

					List<String> result = (List<String>) baseDao.executeQuery(
							" select pk_mtapp_bill from er_mtapp_bill where pk_tradetype = '" + djlxbm
									+ "' and billstatus = 3 ", new BaseProcessor() {
								private static final long serialVersionUID = -4702438982530122614L;

								@Override
								public Object processResultSet(ResultSet rs) throws SQLException {
									List<String> tempResult = new ArrayList<String>();
									while (rs.next()) {
										tempResult.add(rs.getString("pk_mtapp_bill"));
									}
									return tempResult;
								}
							});
					
					IBillFinder billFinder = (IBillFinder) PfUtilTools.findBizImplOfBilltype(djlxbm, BillTypeSetBillFinder.class
							.getName());

					IBillDataFinder dataFinder = billFinder.createBillDataFinder(djlxbm);
					String[] types = dataFinder.getForwardBillTypes(djlxbm);//下游单据类型
					
					if(types != null && types.length > 0 && result != null && result.size() > 0){
						for (String type : types) {
							LightBillVO[] lightVos = ErUtil.queryForwardBills(djlxbm, new String[]{type}, result.toArray(new String[0]));

							if (lightVos != null && lightVos.length > 0) {
								//TODO 提多语
								throw new BusinessException("该申请单交易类型的单据已被下游单据拉单，不可修改费用申请类型");
							}
						}
					}
				}
			}
		}
	}

	/**
	 * @see nc.itf.er.prv.IArapBillTypePrivate#checkBillTypeUnique(nc.vo.er.djlx.DjLXVO)
	 */
	public boolean checkBillTypeUnique(DjLXVO billtypeVo)
			throws BusinessException {
		//
		return checkUnique(billtypeVo);
	}

	/**
	 * @see nc.itf.er.prv.IArapBillTypePrivate#deleteCorpsBillType(nc.vo.er.djlx.DjLXVO,
	 *      java.lang.String[])
	 */
	public Hashtable deleteCorpsBillType(BillTypeVO billtypeVo,
			String[] pk_corps) throws BusinessException {
		//
		return new DjLXBO().deleteCorps(billtypeVo, pk_corps);
	}

	/**
	 * @see nc.itf.er.prv.IArapBillTypePrivate#insertBillType2Corps(nc.vo.er.djlx.DjLXVO,
	 *      nc.vo.er.djlx.DjlxtempletVO[], java.lang.String[])
	 */
	public Hashtable insertBillType2Corps(BillTypeVO billtypevo,
			String[] pk_corps) throws BusinessException {
		DjLXVO djlx = (DjLXVO) billtypevo.getParentVO();
		// DjlxtempletVO[] templetVos = (DjlxtempletVO[])
		// billtypevo.getChildrenVO();
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
		Collection<DjLXVO> cl = dao.retrieveByClause(DjLXVO.class, "dwbm='"
				+ pk_corp + "'");
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

	public BillTypeVO[] queryBillTypeByBillTypeCode(String billtypeCode,
			String pk_group) throws BusinessException {
		String sWhere = "";
		if (billtypeCode != null) {
			sWhere += "and djlxbm='" + billtypeCode + "' ";
		}
		if (pk_group != null)
			sWhere += "and pk_group='" + pk_group + "' ";
		if (sWhere.length() > 1) {
			sWhere = sWhere.substring(3);
		}
		Vector<BillTypeVO> vec = new Vector<BillTypeVO>();
		DjLXVO[] headers = null;
		BaseDAO dao = new BaseDAO();
		@SuppressWarnings("unchecked")
		Collection<DjLXVO> cl = dao.retrieveByClause(DjLXVO.class, sWhere);

		headers = cl.toArray(new DjLXVO[] {});
		if (headers == null || headers.length == 0)
			return null;

		for (int i = 0; i < headers.length; i++) {
			BillTypeVO atype = new BillTypeVO();
			atype.setParentVO(headers[i]);
			vec.addElement(atype);
		}

		return vec.toArray(new BillTypeVO[] {});
	}

	/**
	 * @see nc.itf.er.prv.IArapBillTypePrivate#queryByJC(nc.vo.er.djlx.DjLXVO)
	 */
	public DjLXVO queryByJC(DjLXVO vo) throws BusinessException {
		String sWhere = "dwbm='" + vo.getDwbm() + "' and djlxjc='"
				+ vo.getDjlxjc() + "' and dr=0 ";
		BaseDAO dao = new BaseDAO();
		Collection cl = dao.retrieveByClause(DjLXVO.class, sWhere);
		DjLXVO[] djlxs = (DjLXVO[]) changeCollection2Array(cl, DjLXVO.class);
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
	public DjLXVO[] queryBillTypesForTreeNode(DjLXVO billtypeVo)
			throws BusinessException {
		//
		return new DjLXBO().queryForTreeNode(billtypeVo);
	}

	/**
	 * @see nc.itf.er.pub.IArapBillTypePublic#queryAllBillTypeByCorp(java.lang.String)
	 */
	public DjLXVO[] queryAllBillTypeByCorp(String pk_corp)
			throws BusinessException {
		//

		return new DjLXBO().queryAll(pk_corp);
	}


	/**
	 * @see nc.itf.er.pub.IArapBillTypePublic#deleteBillType(nc.vo.er.djlx.DjLXVO)
	 */
	public void deleteBillType(BillTypeVO billtypevo) throws BusinessException {
		DjLXVO djlx = (DjLXVO) billtypevo.getParentVO();
		deleteDjlxVO(djlx);
		// 删除分配的模板
		String[] funcs = bxFuncs;
		if(BXConstans.JK_DJDL.equals(djlx.getDjdl())){
			funcs = jkFuncs;
		}
		String sql = SqlUtils.getInStr("funnode", funcs, false)+" and pk_corp='"+djlx.getPk_group()+"' and nodekey='"+djlx.getDjlxbm()+"'";
		BaseDAO dao = new BaseDAO();
		dao.deleteByClause(SystemplateVO.class, sql);
		
	}

	private void deleteDjlxVO(DjLXVO vo) throws BusinessException,
			DAOException {
		if (isRefered(vo)) {

			String djlxjc = vo.getDjlxjc();
			/* @res "单据{0}被引用,不能删除。" */
			throw new nc.vo.pub.BusinessException(nc.bs.ml.NCLangResOnserver
					.getInstance().getStrByID("20060101", "UPP20060101-000000",
							null, new String[] { djlxjc }));
		} else {
			BaseDAO dao = new BaseDAO();
			dao.deleteByPK(DjLXVO.class, vo.getDjlxoid());
			
			CacheProxy.fireDataDeletedBatch(vo.getTableName(), new String[]{vo.getDjlxoid()});
		}
	}

	/**
	 * *方法说明: *参数: *返回值: ***注意点*** *@author：屈淑轩 *创建日期：(2001-12-19 13:15:21)
	 * @throws DAOException 
	 */
	private boolean isRefered(DjLXVO vo) throws DAOException {
		BaseDAO dao = new BaseDAO();
		// 1、交易类型没有发布成功能节点
		@SuppressWarnings("unchecked")
		Collection<ParamRegVO> c = dao.retrieveByClause(
				ParamRegVO.class,"PARAMNAME = 'transtype' and PARAMVALUE='"+vo.getDjlxbm()+"'",
				"PARENTID");
		if(c != null && c.size() > 0){
			return true;
		}
		// 2、交易类型下没有业务数据
		String djdl = vo.getDjdl();
		if(ErmBillConst.MatterApp_DJDL.equals(djdl)){
			String checkSql = "select count(1) from "+MatterAppVO.getDefaultTableName()+" where "+MatterAppVO.PK_TRADETYPE+" = '"+vo.getDjlxbm()+"'";
			int column = (Integer) dao.executeQuery(checkSql, new ColumnProcessor());
			if(column > 0){
				return true;
			}
		}else if(ErmBillConst.CostShare_DJDL.equals(djdl)){
			String checkSql = "select count(1) from "+CostShareVO.getDefaultTableName()+" where "+CostShareVO.PK_TRADETYPE+" = '"+vo.getDjlxbm()+"'";
			int column = (Integer) dao.executeQuery(checkSql, new ColumnProcessor());
			if(column > 0){
				return true;
			}
		}else if(BXConstans.JK_DJDL.equals(djdl)){
			String checkSql = "select count(1) from er_jkbx_init where "+JKBXHeaderVO.DJLXBM+" = '"+vo.getDjlxbm()+"'";
			int column = (Integer) dao.executeQuery(checkSql, new ColumnProcessor());
			if(column > 0){
				return true;
			}
			checkSql = "select count(1) from er_jkzb where "+JKBXHeaderVO.DJLXBM+" = '"+vo.getDjlxbm()+"'";
			column = (Integer) dao.executeQuery(checkSql, new ColumnProcessor());
			if(column > 0){
				return true;
			}
		}else if(BXConstans.BX_DJDL.equals(djdl)){
			String checkSql = "select count(1) from er_jkbx_init where "+JKBXHeaderVO.DJLXBM+" = '"+vo.getDjlxbm()+"'";
			int column = (Integer) dao.executeQuery(checkSql, new ColumnProcessor());
			if(column > 0){
				return true;
			}
			checkSql = "select count(1) from er_bxzb where "+JKBXHeaderVO.DJLXBM+" = '"+vo.getDjlxbm()+"'";
			column = (Integer) dao.executeQuery(checkSql, new ColumnProcessor());
			if(column > 0){
				return true;
			}
		}
		return false;
	}

	/**
	 * @see nc.itf.er.pub.IArapBillTypePublic#insertBillType(nc.vo.er.djlx.DjLXVO,
	 *      nc.vo.er.djlx.DjlxtempletVO[])
	 */
	public BillTypeVO insertBillType(BillTypeVO billtypevo)
			throws BusinessException {
		DjLXVO djlx = (DjLXVO) billtypevo.getParentVO();
		// DjlxtempletVO[] templetvos =
		// (DjlxtempletVO[])billtypevo.getChildrenVO();
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
			// FIXME
			BilltypeVO srcBillTypeVo = PfDataCache.getBillTypeInfo(strSourceID);
			// .findBillTypeByKey(strSourceID);
			// BilltypeVO srcBillTypeVo = null;
			BilltypeVO desBillTypeVo = (BilltypeVO) srcBillTypeVo.clone();
			desBillTypeVo.setPk_billtypecode(strDestID);
			desBillTypeVo.setBilltypename(djmc);

			/*
			 * 拷贝生成自定义交易类型单据
			 * 
			 * @parame strDestID strSourceID
			 */
			copybill(strDestID, strSourceID,djlx.getPk_group());

		} catch (BusinessException e) {
			Log.getInstance(this.getClass()).error(e.getMessage(), e);
			throw e;
		}
		return billtypevo;
	}
	/**
	 * 借款交易类型相关的节点编号
	 */
	static final String[] jkFuncs = new String[]{BXConstans.BXLR_QCCODE,BXConstans.BXMNG_NODECODE,
		BXConstans.BXINIT_NODECODE_U,BXConstans.BXINIT_NODECODE_G,BXConstans.BXBILL_QUERY};
	/**
	 * 报销交易类型相关的节点编号
	 */
	static final String[] bxFuncs = new String[]{BXConstans.BXMNG_NODECODE,
		BXConstans.BXINIT_NODECODE_U,BXConstans.BXINIT_NODECODE_G,BXConstans.BXBILL_QUERY};

	@SuppressWarnings("unchecked")
	private void copybill(String strDestID, String strSourceID,String pk_group)
			throws DAOException {
		BaseDAO baseDAO = new BaseDAO();
		Collection<SystemplateBaseVO> sourcevos = baseDAO.retrieveByClause(
				SystemplateBaseVO.class, " funnode='"
						+ BXConstans.BXMNG_NODECODE + "' and nodekey='"
						+ strSourceID + "'");
		// 待新增的模板分配信息
		List<SystemplateVO> list = new ArrayList<SystemplateVO>();
		String[] funcs = bxFuncs;
		if (strSourceID.startsWith("263")) {
			funcs = jkFuncs;
		}
		// 单据管理、期初单据、常用单据、单据查询节点
		for (SystemplateBaseVO sysvo : sourcevos) {
			for (int i = 0; i < funcs.length; i++) {
				SystemplateVO systemplateVO = sysvo.toSystemplateVO();
				systemplateVO.setPk_corp(pk_group);// 设置集团pk
				systemplateVO.setTemplateflag(UFBoolean.TRUE);// 非系统默认模板
				systemplateVO.setSysflag(1);
				systemplateVO.setPrimaryKey(null);
				systemplateVO.setFunnode(funcs[i]);
				systemplateVO.setNodekey(strDestID);
				list.add(systemplateVO);
			}
		}
		
		baseDAO.insertVOList(list);
	}

	public void copyDefused(BilltypeVO source, BilltypeVO target)
			throws DAOException {
	}

	private void insertDjlx(DjLXVO djlx) throws DAOException, BusinessException {
		checkMatypeNotNull(djlx);
		BaseDAO dao = new BaseDAO();
		dao.insertVO(djlx);
		CacheProxy.fireDataInserted(djlx.getTableName());
	}

	/**
	 * @see nc.itf.er.pub.IArapBillTypePublic#getDjlxvoByDjlxbm(java.lang.String,
	 *      java.lang.String)
	 */
	public DjLXVO getDjlxvoByDjlxbm(String billTypeCode, String pk_group)
			throws BusinessException {
		BillTypeVO[] billtypes = queryBillTypeByBillTypeCode(billTypeCode,
				pk_group);
		if (billtypes == null || billtypes.length == 0) {
			throw new BusinessException(nc.bs.ml.NCLangResOnserver
					.getInstance().getStrByID("2006", "UPP2006-000390")/*
																		 * @res
																		 * "当前公司没有分配该单据类型！"
																		 */);
		}
		return (DjLXVO) billtypes[0].getParentVO();
	}

	/**
	 * @see nc.itf.er.prv.IArapBillTypePrivate#getBillTypesByWhere(java.lang.String)
	 */
	public DjLXVO[] getBillTypesByWhere(String condition)
			throws BusinessException {
		BaseDAO dao = new BaseDAO();
		if (condition != null && (condition.startsWith("where"))) {
			condition = condition.substring(5);
		}
		Collection cl = dao.retrieveByClause(DjLXVO.class, condition);
		return (DjLXVO[]) changeCollection2Array(cl, DjLXVO.class);
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

	private void checkMatypeNotNull(DjLXVO vo) throws BusinessException{
		if(vo != null && ErmBillConst.MatterApp_DJDL.equals(vo.getDjdl())){
			if(vo.getMatype() == null){
				//TODO 提多语
				throw new BusinessException("新增费用申请单交易类型，【费用申请类型】不能为空");
			}
		}
		
	}
	
	// 检查要插入的单据类型该公司是否已经存在 2004-8-3 xhb
	private Hashtable checkDjLX(DjLXVO djlx, String[] corps)
			throws BusinessException {
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

	@Override
	public void insertBillType(DjLXVO djlx) throws BusinessException {
		// 新增er_djlx
		insertDjlx(djlx);
		// 新增分配单据模版
		String sql = getSysbaseQrysql(djlx.getDjdl(),null);
		if(StringUtil.isEmpty(sql)){
			return ;
		}
		BaseDAO baseDAO = new BaseDAO();
		@SuppressWarnings("unchecked")
		List<SystemplateBaseVO> sysbasevos = (List<SystemplateBaseVO>) baseDAO
				.retrieveByClause(SystemplateBaseVO.class, sql);
		// copy基础默认模板
		List<SystemplateVO> systemplateVOs = new ArrayList<SystemplateVO>();
		for (SystemplateBaseVO vo : sysbasevos) {
			SystemplateVO systemplateVO = vo.toSystemplateVO();
			systemplateVO.setPk_corp(djlx.getPk_group());// 设置集团pk
			systemplateVO.setTemplateflag(UFBoolean.TRUE);// 非集团的系统默认模板
			systemplateVO.setSysflag(1);
			systemplateVO.setPrimaryKey(null);
			systemplateVO.setNodekey(djlx.getDjlxbm());
			systemplateVOs.add(systemplateVO);
		}
		if(!systemplateVOs.isEmpty()){
			baseDAO.insertVOList(systemplateVOs);
		}
	}

	@Override
	public void deleteBillType(DjLXVO djlx) throws BusinessException {
		BaseDAO baseDAO = new BaseDAO();
		// 删除er_djlx
		deleteDjlxVO(djlx);
		
		// 删除分配的模板
		String funnode = ErmBillConst.MatterApp_FUNCODE;
		if(ErmBillConst.CostShare_DJDL.equals(djlx.getDjdl())){
			funnode = ErmBillConst.CostShare_FUNCODE;
		}
		
		String sql = "funnode = '"+funnode+"' and pk_corp='"+djlx.getPk_group()+"' and nodekey='"+djlx.getDjlxbm()+"'";
		baseDAO.deleteByClause(SystemplateVO.class, sql);
	}

	private String getSysbaseQrysql(String djdl, String tradetype) {
		String sql = null;
		if (ErmBillConst.MatterApp_DJDL.equals(djdl)) {
			// 费用申请单
			sql = " funnode='"
					+ ErmBillConst.MatterApp_FUNCODE
					+ "' and nodekey='"
					+ (StringUtil.isEmpty(tradetype) ? ErmBillConst.MatterApp_base_tradeType
							: tradetype) + "'";
		} else if (ErmBillConst.CostShare_DJDL.equals(djdl)) {
			// 费用结转单
			sql = " funnode='" + ErmBillConst.CostShare_FUNCODE
					+ "' and nodekey='" + (StringUtil.isEmpty(tradetype) ? ErmBillConst.CostShare_base_tradeType:tradetype)
					+ "'";
		} 
		return sql;
	}

}
