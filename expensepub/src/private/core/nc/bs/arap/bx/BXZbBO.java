package nc.bs.arap.bx;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.NamingException;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.er.callouter.FipCallFacade;
import nc.bs.er.pub.BaseDMO;
import nc.bs.er.settle.ErForCmpBO;
import nc.bs.er.util.BXDataPermissionChkUtil;
import nc.bs.er.util.SqlUtils;
import nc.bs.erm.bx.outer.IBxPubActInterface;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Log;
import nc.bs.logging.Logger;
import nc.impl.arap.bx.ArapBXBillPrivateImp;
import nc.itf.arap.prv.IBXBillPrivate;
import nc.itf.arap.pub.ISqdlrKeyword;
import nc.itf.fi.pub.Currency;
import nc.itf.fipub.summary.ISummaryQueryService;
import nc.itf.pim.budget.pub.IBudgetExecute;
import nc.jdbc.framework.crossdb.CrossDBConnection;
import nc.pubitf.arap.payable.IArapPayableBillPubQueryService;
import nc.pubitf.arap.payable.IArapPayableBillPubService;
import nc.pubitf.arap.receivable.IArapReceivableBillPubQueryService;
import nc.pubitf.arap.receivable.IArapReceivableBillPubService;
import nc.pubitf.cmp.settlement.ICmpSettlementPubQueryService;
import nc.pubitf.org.IOrgUnitPubService;
import nc.vo.arap.basebill.BaseBillVO;
import nc.vo.arap.billstatus.ARAPBillStatus;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.arap.bx.util.BXUtil;
import nc.vo.arap.payable.AggPayableBillVO;
import nc.vo.arap.receivable.AggReceivableBillVO;
import nc.vo.cmp.BusiStatus;
import nc.vo.cmp.settlement.SettlementAggVO;
import nc.vo.cmp.settlement.SettlementBodyVO;
import nc.vo.cmp.settlement.SettlementHeadVO;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.BusiTypeVO;
import nc.vo.ep.bx.BxcontrastVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.ep.bx.JsConstrasVO;
import nc.vo.ep.bx.MessageVO;
import nc.vo.ep.bx.SqdlrVO;
import nc.vo.ep.bx.VOFactory;
import nc.vo.ep.dj.DjCondVO;
import nc.vo.er.check.VOChecker;
import nc.vo.er.check.VOStatusChecker;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.er.settle.SettleUtil;
import nc.vo.er.util.StringUtils;
import nc.vo.erm.global.BusiTransVO;
import nc.vo.erm.util.ErVOUtils;
import nc.vo.fip.service.FipMessageVO;
import nc.vo.fip.service.FipRelationInfoVO;
import nc.vo.fipub.billcode.FinanceBillCodeInfo;
import nc.vo.fipub.billcode.FinanceBillCodeUtils;
import nc.vo.fipub.summary.SummaryVO;
import nc.vo.fipub.utils.KeyLock;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pm.budget.pub.BudgetCtlInfoMSG;
import nc.vo.pm.budget.pub.BudgetCtlInfoVO;
import nc.vo.pm.budget.pub.IBudgetExecVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.BusinessRuntimeException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.pf.IPfRetCheckInfo;
import nc.vo.pubapp.pattern.pub.MapList;
import nc.vo.uap.rbac.role.RoleVO;

/**
 * @author twei
 * 
 *         nc.bs.ep.bx.BXZbBO
 * 
 *         借款报销类单据表头业务类
 */
public class BXZbBO {

	private JKBXDAO jkbxDAO;

	public JKBXDAO getJKBXDAO() throws SQLException {
		if (null == jkbxDAO) {
			try {
				jkbxDAO = new JKBXDAO();
			} catch (NamingException e) {
				Log.getInstance(this.getClass()).error(e.getMessage(), e);
				throw new SQLException(e.getMessage());
			}
		}
		return jkbxDAO;
	}

	private static String getBSLoginUser() {
		return InvocationInfoProxy.getInstance().getUserId();
	}

	public Map<String, String> getTsByPrimaryKey(String[] key,
			String tableName, String pkfield) throws BusinessException {
		try {
			return getJKBXDAO().getTsByPrimaryKeys(key, tableName, pkfield);
		} catch (Exception e) {
			throw ExceptionHandler.handleException(this.getClass(), e);

		}
	}

	public MessageVO[] delete(JKBXVO[] vos) throws BusinessException {

		MessageVO[] msgVos = new MessageVO[vos.length];

		compareTs(vos);

		// begin--added by chendya 删除数据权限校验
		List<JKBXVO> bxList = new ArrayList<JKBXVO>();
		List<JKBXVO> jkList = new ArrayList<JKBXVO>();
	
		boolean isNCClient=false;
		for (JKBXVO vo : vos) {
			// 是否期初单据
			boolean isQc = vo.getParentVO().getQcbz().booleanValue();
			// 是否常用单据
			boolean isInit = vo.getParentVO().isInit();
			
			//是否从NC客户端
			 isNCClient=vo.isNCClient();
			
			// 期初常用单据不校验数据权限
			if (isQc || isInit) {
				continue;
			} else {
				if (vo.getParentVO().getDjdl().equals(BXConstans.BX_DJDL)) {
					bxList.add(vo);
				} else if (vo.getParentVO().getDjdl()
						.equals(BXConstans.JK_DJDL)) {
					jkList.add(vo);
				}
			}
		}

		// 验证报销权限
		if(isNCClient){
			BXDataPermissionChkUtil.process(bxList.toArray(new JKBXVO[0]),
					BXConstans.ERMEXPRESOURCECODE, BXConstans.EXPDELOPTCODE,
					getBSLoginUser());
			
			// 验证借款权限
			BXDataPermissionChkUtil.process(jkList.toArray(new JKBXVO[0]),
					BXConstans.ERMLOANRESOURCECODE, BXConstans.LOANDELOPTCODE,
					getBSLoginUser());
		}
		

		
		// --end

		for (int i = 0; i < vos.length; i++) {

			JKBXVO vo = vos[i];
			// begin-- modified by chendya
			// 优化效率问题，不查表体，原来查表体是结算需要业务页签的信息，现在结算改了，不在需要此信息了
			// bxvo = retriveItems(bxvo);
			// --end
			JKBXHeaderVO parentVO = vo.getParentVO();
			String pk_jkbx = parentVO.getPk_jkbx();

			try {
				// 校验单据状态
				VOStatusChecker.checkDeleteStatus(parentVO);

				// 非常用单据单据校验是否关帐
				if(!vo.getParentVO().isInit()){
					VOChecker.checkErmIsCloseAcc(vo);
				}
				// 校验期初单据的反结算操作
				if (parentVO.getQcbz().booleanValue()) {
					checkUnSettle(new String[] { pk_jkbx });
				}

			} catch (BusinessException e) {
				msgVos[i] = new MessageVO(MessageVO.DELETE, vo, e.getMessage(), false);
				continue;
			}

			// 删除业务信息
			getBxBusitemBO(parentVO.getDjlxbm(), parentVO.getDjdl())
					.deleteByBXVOs(new JKBXVO[] { vo });

			msgVos[i] = new MessageVO(MessageVO.DELETE, vo, "", true);

			try {

				beforeActInf(vo, MESSAGE_DELETE);

				// 删除冲借款对照信息
				new ContrastBO().deleteByPK_bxd(new String[] { pk_jkbx });

				// 更新删除标志
				parentVO.setDr(new Integer(1));

				getJKBXDAO().update(new JKBXHeaderVO[] { parentVO },
						new String[] { "dr" });
				
				// begin--added by chendya 暂存单据没有传结算，删除暂存的单据也应该不走结算
				boolean isTempSave = BXStatusConst.DJZT_TempSaved == parentVO.getDjzt().intValue() ? true : false;
				
				// 判断CMP产品是否启用
				boolean isCmpInstalled = isCmpInstall(vo.getParentVO());

				//是否 既无收款也无付款
				boolean notExistsPayOrRecv = (vo.getParentVO().getZfybje()==null || vo.getParentVO().getZfybje().equals(new UFDouble(0)))
				&& (vo.getParentVO().getHkybje()==null || vo.getParentVO().getHkybje().equals(new UFDouble(0)));
				
				if (!isTempSave && !notExistsPayOrRecv && isCmpInstalled) {
					new ErForCmpBO().invokeCmp(vo, parentVO.getDjrq(),BusiStatus.Deleted);
				}

				afterActInf(vo, MESSAGE_DELETE);

				//非常用单据回退单据号，常用单据不生成单据号
				if (!vo.getParentVO().isInit()) {
					returnBillCode(vo);
				}

			} catch (Exception e) {

				throw ExceptionHandler.handleException(e);
			}
		}

		return msgVos;
	}
	
	private static boolean isCmpInstall(JKBXHeaderVO parentVO) throws BusinessException{
		boolean flag = BXUtil.isProductInstalled(parentVO.getPk_group(),
				BXConstans.TM_CMP_FUNCODE);
		if (!flag) {
			return false;
		}
		//组织是否启用了现金管理
		String periord = NCLocator.getInstance().lookup(IOrgUnitPubService.class).getOrgModulePeriodByOrgIDAndModuleID(parentVO.getPk_org(), BXConstans.TM_CMP_FUNCODE);
		if (periord == null) {
			Logger.debug("当前单据所属组织（借款报销单位)未启用现金管理");
			flag = false;
		}
		return flag;
	}

	public IBXBusItemBO getBxBusitemBO(String djlxbm, String djdl)
			throws BusinessException {
		try {
			BusiTypeVO busTypeVO = BXUtil.getBusTypeVO(djlxbm, djdl);
			String busiClass = busTypeVO.getInterfaces().get(
					BusiTypeVO.IBXBusItemBO);
			return (IBXBusItemBO) Class.forName(busiClass).newInstance();
		} catch (Exception e) {
			throw ExceptionHandler.handleException(e);
		}
	}
	
	/**
	 * 特殊处理摘要
	 * 
	 * @param vo
	 */
	private void dealZyName(JKBXVO vo) throws BusinessException {
		String value = vo.getParentVO().getZy();
		if (value != null && value.length() == 20) {
			ISummaryQueryService service = NCLocator.getInstance().lookup(nc.itf.fipub.summary.ISummaryQueryService.class);
			SummaryVO[] summaryVOs = service.querySummaryVOByCondition(SummaryVO.PK_SUMMARY + "='"+ value + "'");
			if (summaryVOs != null && summaryVOs.length > 0) {
				vo.getParentVO().setZy(summaryVOs[0].getSummaryname());
			}
		}
	}
		
	/**
	 * 正式保存，进行业务校验，并调用需要的接口
	 * 
	 * @param vos
	 *            要保存单据的聚合VO数组
	 */
	public JKBXVO[] save(JKBXVO[] vos) throws BusinessException {
		VOChecker checker = new VOChecker();
		for (JKBXVO vo : vos) {
			
			//特殊处理摘要
			dealZyName(vo);
			
			// 保存校验
			checker.checkkSaveBackground(vo);

			// 检查单据是否关帐
			VOChecker.checkErmIsCloseAcc(vo);

			// added by Chen deyin(chendya@ufida.com.cn)
			if (vo.getParentVO().getQcbz() != null && vo.getParentVO().getQcbz().booleanValue()) {
				//期初单据保存后自动审核通过
				vo.getParentVO().setSpzt(IPfRetCheckInfo.PASSING);
			}else{
				//保存后自动提交
				vo.getParentVO().setSpzt(IPfRetCheckInfo.COMMIT);
			}
			
			// 去服务器事件作为创建时间
			vo.getParentVO().setCreationtime(
					(new UFDateTime(InvocationInfoProxy.getInstance()
							.getBizDateTime())));
		}

		beforeActInf(vos, MESSAGE_SAVE);

		// 保存单据
		JKBXVO[] bxvos;
		try {
			bxvos = getJKBXDAO().save(vos);
		} catch (Exception e) {
			for (JKBXVO bxvo : vos) {
				returnBillCode(bxvo);
			}
			throw ExceptionHandler.handleException(e);
		}

		try {
			// 调用现金流平台结算
			ErForCmpBO erBO = new ErForCmpBO();

			for (JKBXVO vo : vos) {
				
				BusiStatus billStatus = SettleUtil.getBillStatus(vo.getParentVO(), false);
				
				//是否安装结算
				boolean isInstallCmp = BXUtil.isProductInstalled(vo.getParentVO().getPk_group(),BXConstans.TM_CMP_FUNCODE);
				
				//是否 既无收款也无付款
				boolean notExistsPayOrRecv = (vo.getParentVO().getZfybje()==null || vo.getParentVO().getZfybje().equals(new UFDouble(0)))
				&& (vo.getParentVO().getHkybje()==null || vo.getParentVO().getHkybje().equals(new UFDouble(0)));
				
				if (isInstallCmp && !notExistsPayOrRecv) {
					erBO.invokeCmp(vo, vo.getParentVO().getDjrq(), billStatus);
				}
			}

			afterActInf(vos, MESSAGE_SAVE);

		} catch (Exception e) {
			for (JKBXVO bxvo : vos) {
				returnBillCode(bxvo);
			}
			throw ExceptionHandler.handleException(e);
		}

		return bxvos;
	}

	private void checkForInitBill(JKBXVO[] vos) throws BusinessException {
		for (JKBXVO vo : vos) {
			if (vo.getParentVO().isInit()) {
				JKBXHeaderVO header = vo.getParentVO();
				String djlxbm = header.getDjlxbm();
				String pk_org = header.getPk_org();
				String pk_group = header.getPk_group();
				String pk = "ER_JKBXINIT" + djlxbm + pk_org;
				String user = KeyLock.dynamicLock(pk);

				if (user != null) {
					throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl
							.getNCLangRes().getStrByID("expensepub_0",
									"02011002-0060")/*
													 * @res
													 * "并发异常，保存常用单据失败，该单据类型的常用单据在当前单位已经存在！"
													 */);
				}

				DjCondVO condVO = new DjCondVO();
				UFBoolean isGroup = vo.getParentVO().getIsinitgroup();
				if (isGroup.booleanValue()) {
					condVO.defWhereSQL = " zb.djlxbm='" + djlxbm
							+ "' and zb.dr=0 and zb.isinitgroup='" + isGroup
							+ "'";
					condVO.pk_group = new String[] { pk_group };
				} else {
					condVO.defWhereSQL = " zb.djlxbm='" + djlxbm
							+ "' and zb.dr=0 and zb.isinitgroup='" + isGroup
							+ "'";
					condVO.pk_org = new String[] { pk_org };
				}
				condVO.isInit = true;
				condVO.isCHz = false;
				List<JKBXVO> svos = NCLocator.getInstance().lookup(
						IBXBillPrivate.class).queryVOs(0, 1, condVO);

				if (svos != null && svos.size() > 0) {
					throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl
							.getNCLangRes().getStrByID("expensepub_0",
									"02011002-0060")/*
													 * @res
													 * "并发异常，保存常用单据失败，该单据类型的常用单据在当前单位已经存在！"
													 */);
				}
			}
		}
	}

	/**
	 * 暂存业务处理，不进行任何业务校验，不调用其他模块接口
	 * 
	 * @param vos
	 *            要暂存单据的聚合VO数组
	 */
	public JKBXVO[] tempSave(JKBXVO[] vos) throws BusinessException {

		// 常用单据的并发校验
		checkForInitBill(vos);

		for (JKBXVO vo : vos) {
			new VOChecker().checkkSaveBackground(vo);
			
			//特殊处理摘要
			dealZyName(vo);
		}

		beforeActInf(vos, MESSAGE_SAVE);

		JKBXVO[] bxvos;
		try {
			bxvos = getJKBXDAO().save(vos);
		} catch (SQLException e) {
			throw ExceptionHandler.handleException(e);
		}
		
		afterActInf(vos, MESSAGE_SAVE);

		return bxvos;
	}

	public JKBXVO[] update(JKBXVO[] vos) throws BusinessException {
		//校验时间戳
		compareTs(vos);
		
		boolean isNCClient=false;
		
		for (JKBXVO vo : vos) {
			
			//特殊处理摘要
			dealZyName(vo);
			new VOChecker().checkkSaveBackground(vo);
			// 常用单据单据,暂存态单据不校验关帐
			boolean isChkCloseAcc = true;
			JKBXHeaderVO parentVO = vo.getParentVO();
			if (parentVO != null&& (parentVO.isInit() || BXStatusConst.DJZT_TempSaved == parentVO.getDjzt())) {
				isChkCloseAcc = false;
			}
			if(isChkCloseAcc){
				VOChecker.checkErmIsCloseAcc(vo);
			}
			if (vo.getParentVO().getDjzt() != BXStatusConst.DJZT_TempSaved) {
				if (vo.getParentVO().getQcbz() != null&& vo.getParentVO().getQcbz().booleanValue()) {
					vo.getParentVO().setSpzt(IPfRetCheckInfo.PASSING);
				} else {
					vo.getParentVO().setSpzt(IPfRetCheckInfo.COMMIT);
				}
			}
			
			// 取服务器事件作为修改时间
			vo.getParentVO().setModifiedtime(new UFDateTime(InvocationInfoProxy.getInstance().getBizDateTime()));
		}

		// begin--added by chendya 修改数据权限校验
		List<JKBXVO> bxList = new ArrayList<JKBXVO>();
		List<JKBXVO> jkList = new ArrayList<JKBXVO>();
		for (JKBXVO vo : vos) {
			// 是否期初单据
			boolean isQc = vo.getParentVO().getQcbz().booleanValue();
			// 是否常用单据
			boolean isInit = vo.getParentVO().isInit();
			
			//是否从NC客户端
			isNCClient=vo.isNCClient();
			 
			// 期初常用单据不校验数据权限
			if (isQc || isInit) {
				continue;
			} else {
				if (vo.getParentVO().getDjdl().equals(BXConstans.BX_DJDL)) {
					bxList.add(vo);
				} else if (vo.getParentVO().getDjdl()
						.equals(BXConstans.JK_DJDL)) {
					jkList.add(vo);
				}
			}
		}
		if(isNCClient){
			// 验证报销权限
			BXDataPermissionChkUtil.process(bxList.toArray(new JKBXVO[0]),
					BXConstans.ERMEXPRESOURCECODE, BXConstans.EXPEDITCODE,
					getBSLoginUser());

			// 验证借款权限
			BXDataPermissionChkUtil.process(jkList.toArray(new JKBXVO[0]),
					BXConstans.ERMLOANRESOURCECODE, BXConstans.LOANEDITCODE,
					getBSLoginUser());
		}
		
		// --end

		beforeActInf(vos, MESSAGE_UPDATE);

		JKBXVO[] bxvos;

		try {
			bxvos = getJKBXDAO().update(vos);
		} catch (SQLException e) {
			for (JKBXVO bxvo : vos) {
				returnBillCode(bxvo);
			}
			throw ExceptionHandler.handleException(e);
		}
		try {
			
			// 调用现金流平台结算
			ErForCmpBO erBO = new ErForCmpBO();
			
			for (JKBXVO vo : vos) {
				
				//当前结算信息状态
				BusiStatus billStatus = SettleUtil.getBillStatus(vo.getParentVO(), false);
				
				//当前结算信息状态
				BusiStatus oldBillStatus = SettleUtil.getBillStatus(vo.getBxoldvo().getParentVO(), false);
				
				//CMP产品是否启用
				boolean isInstallCmp = BXUtil.isProductInstalled(vo.getParentVO().getPk_group(), BXConstans.TM_CMP_FUNCODE);
				
				//是否暂存单据修改
				boolean isTmpSave = BXStatusConst.DJZT_TempSaved==vo.getParentVO().getDjzt();
				
				//是否不存在收付款
				boolean notExistsPayOrRecv = (vo.getParentVO().getZfybje()==null || vo.getParentVO().getZfybje().equals(new UFDouble(0)))
										&& (vo.getParentVO().getHkybje()==null || vo.getParentVO().getHkybje().equals(new UFDouble(0)));
				
				boolean isToSettle = false;
				if(oldBillStatus == BusiStatus.Save && billStatus==BusiStatus.Deleted){
					isToSettle = true;
				}else{
					isToSettle = !notExistsPayOrRecv;
				}
				
				if (isInstallCmp && !isTmpSave && isToSettle) {
					erBO.invokeCmp(vo, vo.getParentVO().getDjrq(), billStatus);
				}
			}

			afterActInf(vos, MESSAGE_UPDATE);

		} catch (Exception e) {
			for (JKBXVO bxvo : vos) {
				returnBillCode(bxvo);
			}
			throw ExceptionHandler.handleException(e);
		}

		return bxvos;
	}

	public MessageVO[] unAudit(JKBXVO[] vos) throws BusinessException {

		if (vos == null || vos.length < 1)
			return null;

		MessageVO[] msgs = new MessageVO[vos.length];

		compareTs(vos);

		try {
			for (int i = 0; i < vos.length; i++) {

				try {

					unAuditBack(vos[i]);

					msgs[i] = new MessageVO(MessageVO.UNAUDIT, vos[i], "", true);

				} catch (BusinessException e) {
					msgs[i] = new MessageVO(MessageVO.UNAUDIT, vos[i], e
							.getMessage(), false);
					throw ExceptionHandler.handleException(e);
				}
			}
		} catch (Exception e) {
			throw ExceptionHandler.handleException(e);
		}

		return msgs;

	}

	private void unAuditBack(JKBXVO bxvo) throws BusinessException {
		//查表体信息
		bxvo = retriveItems(bxvo);
		JKBXHeaderVO headerVO = bxvo.getParentVO();
		VOStatusChecker.checkUnAuditStatus(headerVO);

		// 判断CMP产品是否启用
		boolean isCmpInstalled = isCmpInstall(headerVO);
		BusiStatus billStatus = SettleUtil.getBillStatus(headerVO, false,BusiStatus.Save);
		if (billStatus.equals(BusiStatus.Deleted)) { 
			
			// 没有结算信息的单据直接反生效
			unSettle(new JKBXVO[] { bxvo });
			
			//删除凭证
			effectToFip(bxvo, MESSAGE_UNSETTLE);
		} else {
			UFDate shrq = headerVO.getShrq() == null ? null : headerVO.getShrq().getDate();
			if (!isCmpInstalled) {
				unSettle(new JKBXVO[] { bxvo });
				//删除凭证
				effectToFip(bxvo, MESSAGE_UNSETTLE);
			} else {
				//安装了结算的反审核
				new ErForCmpBO().invokeCmp(bxvo, shrq, billStatus);
			}
		}

		headerVO.setSxbz(BXStatusConst.SXBZ_NO);
		headerVO.setDjzt(new Integer(BXStatusConst.DJZT_Saved));

		// begin--清空结算人，结算日期
		if (headerVO.getJsr() != null) {
			headerVO.setJsr(null);
		}
		if (headerVO.getJsrq() != null) {
			headerVO.setJsrq(null);
		}
		// --end

		try {

			getJKBXDAO().update(new JKBXHeaderVO[] { headerVO },
					new String[] { JKBXHeaderVO.DJZT, JKBXHeaderVO.SXBZ ,JKBXHeaderVO.SPZT});

			afterActInf(bxvo, MESSAGE_UNAUDIT);

		} catch (Exception e) {
			throw ExceptionHandler.handleException(e);
		}
	}

	private JKBXVO retriveItems(JKBXVO bxvo) throws BusinessException {

		if ((bxvo.getChildrenVO() == null || bxvo.getChildrenVO().length == 0)) {
			JKBXVO resultBxvo = new ArapBXBillPrivateImp().retriveItems(bxvo.getParentVO());
			bxvo.setChildrenVO(resultBxvo.getChildrenVO());
		}

		return bxvo;
	}

	public MessageVO[] audit(JKBXVO[] vos) throws BusinessException {

		if (vos == null || vos.length < 1)
			return null;

		MessageVO[] msgs = new MessageVO[vos.length];

		compareTs(vos);

		try {
			for (int i = 0; i < vos.length; i++) {

				auditBack(vos[i]);

				msgs[i] = new MessageVO(MessageVO.AUDIT, vos[i], "", true);

			}
		} catch (Exception e) {
			throw ExceptionHandler.handleException(e);
		}

		return msgs;
	}

	/**
	 * 审核，单据状态转为已审核或已签字状态
	 * 
	 * @param bxvo
	 * @throws BusinessException
	 * @throws SQLException
	 */
	private void auditBack(JKBXVO bxvo) throws BusinessException, SQLException {

		bxvo = retriveItems(bxvo);

		JKBXHeaderVO headerVO = bxvo.getParentVO();

		VOStatusChecker.checkAuditStatus(headerVO, headerVO.getShrq());
		headerVO.setSpzt(IPfRetCheckInfo.PASSING);

		beforeActInf(bxvo, MESSAGE_AUDIT);

		// 单据是否自动签字(受参数控制)
		boolean isAutoSign = SettleUtil.isAutoSign(headerVO);

		// 判断CMP产品是否启用
		boolean isCmpInstalled = isCmpInstall(headerVO);

		// modified by chendya
		if (headerVO.getSpzt() == IPfRetCheckInfo.PASSING) {
			// 审批通过
			headerVO.setDjzt(new Integer(BXStatusConst.DJZT_Verified));
		}
		if (isAutoSign) {
			// 1.状态置为已审核，结算单据
			headerVO.setJsr(headerVO.getApprover());
			headerVO.setJsrq(headerVO.getShrq().getDate());

			getJKBXDAO().update(
					new JKBXHeaderVO[] { headerVO },
					new String[] { JKBXHeaderVO.SPZT, JKBXHeaderVO.DJZT, JKBXHeaderVO.SXBZ, JKBXHeaderVO.APPROVER, JKBXHeaderVO.SHRQ });

			// 是否有结算信息(根据支付，还款金额判断)
			BusiStatus billStatus = SettleUtil.getBillStatus(headerVO, false, BusiStatus.Audit);

			// 没有结算信息的单据直接签字生效
			if (billStatus.equals(BusiStatus.Deleted)) {
				settle(headerVO.getApprover(), headerVO.getShrq().getDate(),bxvo);
				
				//传会计平台
				effectToFip(bxvo,MESSAGE_SETTLE);
			} else {
				if (isCmpInstalled) {
					new ErForCmpBO().invokeCmp(bxvo, headerVO.getShrq().getDate(), billStatus);
				} else {
					
					//未装结算情况
					settle(headerVO.getApprover(),headerVO.getShrq().getDate(), bxvo);
					
					//传会计平台
					effectToFip(bxvo,MESSAGE_SETTLE);
				}
			}
			// 自动签字
			headerVO.setDjzt(new Integer(BXStatusConst.DJZT_Sign));
			// 生效标志
			headerVO.setSxbz(new Integer(BXStatusConst.SXBZ_VALID));
		} else {
			BusiStatus billStatus = SettleUtil.getBillStatus(headerVO, false,BusiStatus.Audit);
			// 没有结算信息的单据直接签字生效
			if (billStatus.equals(BusiStatus.Deleted)) {
				settle(headerVO.getApprover(), headerVO.getShrq().getDate(),bxvo);
				//传会计平台
				effectToFip(bxvo,MESSAGE_SETTLE);
			} else {
				if (isCmpInstalled) {
					new ErForCmpBO().invokeCmp(bxvo, headerVO.getShrq().getDate(), billStatus);
				} else {
					settle(headerVO.getApprover(),headerVO.getShrq().getDate(), bxvo);
					//传会计平台
					effectToFip(bxvo,MESSAGE_SETTLE);
				}
			}
		}
		try {

			getJKBXDAO().update(new JKBXHeaderVO[] { headerVO },new String[] { JKBXHeaderVO.SPZT, 
					JKBXHeaderVO.DJZT,JKBXHeaderVO.SXBZ, JKBXHeaderVO.APPROVER,JKBXHeaderVO.SHRQ });

			afterActInf(bxvo, MESSAGE_AUDIT);

		} catch (Exception e) {

			throw ExceptionHandler.handleException(e);
		}

	}

	/**
	 * 
	 * @param vos
	 * @param isCmpUnEffectiveCall
	 *            是否结算反生效调用
	 * @return
	 * @throws BusinessException
	 */
	public JKBXVO[] unSettle(JKBXVO[] vos, boolean isCmpUnEffectiveCall)
			throws BusinessException {

		compareTs(vos);

		try {
			String[] keys = new String[vos.length];
			JKBXHeaderVO[] headers = new JKBXHeaderVO[vos.length];

			beforeActInf(vos, MESSAGE_UNSETTLE);

			for (int i = 0; i < vos.length; i++) {
				JKBXHeaderVO parentVO = vos[i].getParentVO();
				keys[i] = parentVO.getPk_jkbx();

				vos[i].setBxoldvo((JKBXVO) vos[i].clone());
				headers[i] = parentVO;
				headers[i].setJsrq(null);
				headers[i].setJsr(null);
				headers[i].setDjzt(BXStatusConst.DJZT_Verified);
				headers[i].setSxbz(BXStatusConst.SXBZ_NO);

				// 补充信息
				addContrastInfo(vos[i]);
				// 处理票据报销和写帐
				// dealPjAndBankInfo(vos[i], true);
			}

			String inStr = SqlUtils.getInStr("er_jsconstras.pk_bxd", keys);

			String sqlJS = " er_jsconstras  where " + inStr;

			// 校验反签字信息
			checkUnSettle(keys);

			Collection<JsConstrasVO> jsContrasVOs = getJKBXDAO()
					.queryJsContrastByWhereSql(sqlJS);

			if (jsContrasVOs != null) {

				// 处理生成的收付往来单据，分应收应付单独处理0表示应收 1表示应付
				List<String> vouchid1 = new ArrayList<String>();
				List<String> vouchid0 = new ArrayList<String>();
				JsConstrasVO jsconvo = null;
				for (Iterator<JsConstrasVO> iter = jsContrasVOs.iterator(); iter
						.hasNext();) {
					jsconvo = iter.next();

					String vouchid = jsconvo.getPk_jsd();
					Integer billflag = jsconvo.getBillflag();
					if (billflag.intValue() == 1) {
						vouchid1.add(vouchid);
					} else {
						vouchid0.add(vouchid);
					}
				}

				if (vouchid1 != null && vouchid1.size() != 0) {
					// 通过对应的应付单的pk找到对应的聚合VO，之后删除对应的应付单
					IArapPayableBillPubService apBillService = (IArapPayableBillPubService) NCLocator.getInstance().lookup(IArapPayableBillPubService.class.getName());
					IArapPayableBillPubQueryService apQryService = (IArapPayableBillPubQueryService) NCLocator.getInstance().lookup(IArapPayableBillPubQueryService.class.getName());
					AggPayableBillVO[] payablevo = apQryService.findBillByPrimaryKey(vouchid1.toArray(new String[] {}));
					if (payablevo != null) {
						for (AggPayableBillVO ss : payablevo) {
							BaseBillVO svo = (BaseBillVO) ss.getParentVO();
							if (svo.getBillstatus().intValue() != ARAPBillStatus.TEMPSAVE.VALUE.intValue()
									&& svo.getBillstatus().intValue() != ARAPBillStatus.SAVE.VALUE
											.intValue()) {
								throw new BusinessRuntimeException(
										nc.vo.ml.NCLangRes4VoTransl
												.getNCLangRes().getStrByID(
														"expensepub_0",
														"02011002-0061")/*
																		 * @res
																		 * "下游往来单据已经不是暂存或保存态， 不能进行反生效操作"
																		 */);
							}
						}
						apBillService.delete(payablevo);
					}
				}
				if (vouchid0 != null && vouchid0.size() != 0) {
					// 通过对应的应收单的pk找到对应的聚合VO，之后删除对应的应收单
					IArapReceivableBillPubService billBo0 = (IArapReceivableBillPubService) NCLocator
							.getInstance().lookup(
									IArapReceivableBillPubService.class
											.getName());
					IArapReceivableBillPubQueryService billquery0 = (IArapReceivableBillPubQueryService) NCLocator
							.getInstance().lookup(
									IArapReceivableBillPubQueryService.class
											.getName());
					AggReceivableBillVO[] recvo = billquery0
							.findBillByPrimaryKey(vouchid0
									.toArray(new String[] {}));
					if (recvo != null) {
						for (AggReceivableBillVO ss : recvo) {
							BaseBillVO svo = (BaseBillVO) ss.getParentVO();
							if (svo.getBillstatus().intValue() != ARAPBillStatus.TEMPSAVE.VALUE
									.intValue()
									&& svo.getBillstatus().intValue() != ARAPBillStatus.SAVE.VALUE
											.intValue()) {
								throw new BusinessRuntimeException(
										nc.vo.ml.NCLangRes4VoTransl
												.getNCLangRes().getStrByID(
														"expensepub_0",
														"02011002-0061")/*
																		 * @res
																		 * "下游往来单据已经不是暂存或保存态， 不能进行反生效操作"
																		 */);
							}
						}
						billBo0.delete(recvo);
					}
				}
			}
			getJKBXDAO().delete(jsContrasVOs.toArray(new JsConstrasVO[] {}));
			updateHeaders(headers, new String[] { JKBXHeaderVO.JSR,JKBXHeaderVO.JSRQ, JKBXHeaderVO.DJZT, JKBXHeaderVO.SXBZ });

			afterActInf(vos, MESSAGE_UNSETTLE);

			return vos;

		} catch (Exception e) {
			throw ExceptionHandler.handleException(e);
		}
	}

	/**
	 * @param head
	 * @throws BusinessException
	 * 
	 *             后台进行反结算操作
	 */
	public void unSettleBack(JKBXHeaderVO head) throws BusinessException {

		VOStatusChecker.checkUnSettleStatus(head);

		JKBXVO bxvo = VOFactory.createVO(head);

		head.setJsr(null);
		head.setJsrq(null);
		head.setDjzt(BXStatusConst.DJZT_Verified);
		head.setSxbz(BXStatusConst.SXBZ_NO);
		unSettle(new JKBXVO[] { bxvo });

	}

	/**
	 * 此方法处理生效，借款控制，预算控制，传收付
	 * @param jsr
	 * @param jsrq
	 * @param vo
	 * @throws BusinessException
	 * 
	 */
	public void settle(String jsr, UFDate jsrq, JKBXVO vo) throws BusinessException {

		// 校验ts
		compareTs(new JKBXVO[] { vo });

		JKBXHeaderVO head = vo.getParentVO();

		VOStatusChecker.checkSettleStatus(head, jsrq);

		// 补充信息
		addContrastInfo(vo);

		// begin--added by chendya 下列代码块一定放在预算控制之前，
		// 避免预算控制设置控制规则中如果设置控制日期为“生效日期”时查不到控制方案
		{
			head.setJsr(jsr);
			head.setJsrq(jsrq);
			head.setDjzt(BXStatusConst.DJZT_Sign);
			head.setSxbz(BXStatusConst.SXBZ_VALID);
		}
		// --end

		// 处理前插件动作(预算控制)
		beforeActInf(new JKBXVO[] { vo }, MESSAGE_SETTLE);

		// 更新vo信息
		updateHeaders(new JKBXHeaderVO[] { head }, new String[] { JKBXHeaderVO.JSR,
				JKBXHeaderVO.JSRQ, JKBXHeaderVO.DJZT, JKBXHeaderVO.SXBZ });

		// 处理后插件动作
		afterActInf(new JKBXVO[] { vo }, MESSAGE_SETTLE);

		//传收付
		transferArap(vo);
	}

	/**
	 * 
	 * 传收付 往来单据
	 */
	private void transferArap(JKBXVO vo) throws BusinessException {
		// 判断是否安装应收应付产品，否则不进行转往来操作
		boolean isARused = BXUtil.isProductInstalled(vo.getParentVO()
				.getPk_group(), BXConstans.FI_AR_FUNCODE);
		boolean isAPused = BXUtil.isProductInstalled(vo.getParentVO()
				.getPk_group(), BXConstans.FI_AP_FUNCODE);

		// 进行转收付操作
		if (isARused && isAPused) {
			new ErPFUtil().doTransferArap(vo);
		}
	}

	/**
	 * @param vo
	 * @throws BusinessException
	 * 
	 *             增加冲借款的信息和对应的借款单的信息
	 */
	private void addContrastInfo(JKBXVO vo) throws BusinessException {
		if (vo.getParentVO().getDjdl().equals(BXConstans.BX_DJDL)) {
			BxcontrastVO[] contrast = vo.getContrastVO();
			if (vo.getContrastVO() == null || vo.getContrastVO().length == 0) {
				// 补充冲销信息
				Collection<BxcontrastVO> contrasts = queryContrasts(vo.getParentVO());
				contrast = contrasts.toArray(new BxcontrastVO[] {});
				vo.setContrastVO(contrast);
			}

			// 补充对应的借款单信息
			List<String> jkdKeys = new ArrayList<String>();
			if (contrast != null && contrast.length != 0) {
				for (BxcontrastVO contr : contrast) {
					jkdKeys.add(contr.getPk_jkd());
				}
			}
			List<JKBXHeaderVO> jkds = new BXZbBO().queryHeadersByPrimaryKeys(jkdKeys.toArray(new String[] {}), BXConstans.JK_DJDL);
			Map<String, JKBXHeaderVO> jkdMap = new HashMap<String, JKBXHeaderVO>();
			if (jkds != null) {
				for (JKBXHeaderVO jkd : jkds) {
					jkdMap.put(jkd.getPrimaryKey(), jkd);
				}
			}
			vo.setJkdMap(jkdMap);

		}
	}

	/**
	 * 回退单据号
	 * 
	 * @param bxvo
	 * @throws BusinessException
	 */

	public void returnBillCode(JKBXVO bxvo) throws BusinessException {
		//调用fippub回退单据号的公共方法
		FinanceBillCodeUtils utils = new FinanceBillCodeUtils(new FinanceBillCodeInfo(JKBXHeaderVO.DJDL, JKBXHeaderVO.DJBH, JKBXHeaderVO.PK_GROUP,JKBXHeaderVO.PK_ORG, bxvo.getParentVO().getTableName()));
		utils.returnBillCode(new AggregatedValueObject[]{bxvo});
		
	}

	/**
	 * @param headers
	 * @throws DAOException
	 * @throws SQLException
	 * 
	 *             由于DAO不能同时更新借款单和报销单，这里需要分开处理.
	 */
	public void updateHeaders(JKBXHeaderVO[] headers, String[] fields)
			throws BusinessException {
		Map<String, List<JKBXHeaderVO>> voMap = splitJkbx(headers);
		Collection<List<JKBXHeaderVO>> values = voMap.values();
		for (Iterator<List<JKBXHeaderVO>> iter = values.iterator(); iter
				.hasNext();) {
			List<JKBXHeaderVO> lvos = iter.next();
			try {
				getJKBXDAO().update(lvos.toArray(new JKBXHeaderVO[] {}), fields);
			} catch (Exception e) {
				throw ExceptionHandler.handleException(e);
			}
		}
	}

	/**
	 * @param headers
	 * @return 按单据大类分离VO数组
	 */
	private Map<String, List<JKBXHeaderVO>> splitJkbx(JKBXHeaderVO[] headers) {
		Map<String, List<JKBXHeaderVO>> map = new HashMap<String, List<JKBXHeaderVO>>();
		for (int i = 0; i < headers.length; i++) {
			JKBXHeaderVO headerVO = headers[i];
			String djdl = headerVO.getDjdl();
			if (map.containsKey(djdl)) {
				map.get(djdl).add(headerVO);
			} else {
				List<JKBXHeaderVO> list = new ArrayList<JKBXHeaderVO>();
				list.add(headerVO);
				map.put(djdl, list);
			}
		}
		return map;
	}

	/**
	 * @param keys
	 *            //所有单据主键
	 * @param jkdKeys
	 *            //借款单据主键
	 * @throws SQLException
	 * @throws DAOException
	 * @throws BusinessException
	 */
	private void checkUnSettle(String[] keys) throws BusinessException {

		try {
			if (keys != null) {// 校验借款单是否已经进行了冲销

				String sqlJK = SqlUtils.getInStr(BxcontrastVO.PK_JKD, keys);

				Collection<BxcontrastVO> jkContrasVOs = getJKBXDAO()
						.retrieveContrastByClause(sqlJK);

				if (jkContrasVOs != null && jkContrasVOs.size() > 0) {
					throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl
							.getNCLangRes()
							.getStrByID("2011", "UPP2011-000384")/*
																 * @res
																 * "借款单已进行了冲销操作,无法进行反签字!"
																 */);
				}
			}
		} catch (Exception e) {
			throw ExceptionHandler.handleException(e);
		}
	}

	public static String MESSAGE_SAVE = "bx-save";

	public static String MESSAGE_UPDATE = "bx-update";

	public static String MESSAGE_AUDIT = "bx-audit";

	public static String MESSAGE_UNAUDIT = "bx-unaudit";

	public static String MESSAGE_DELETE = "bx-delete";

	public static String MESSAGE_SETTLE = "bx-settle";

	public static String MESSAGE_UNSETTLE = "bx-unsettle";

	public static int MESSAGE_NOTSEND = -1;

	/**
	 * @ 与收付以及会计平台交互 @
	 * */

	private void sendMessage(JKBXVO vo, int message) throws BusinessException {
		try {
			JKBXHeaderVO headVO = VOFactory.createHeadVO(vo.getParentVO().getDjdl());
			headVO = vo.getParentVO();
			
			AggregatedValueObject object = createJkbxToFIPVO(vo, message);
			
			sendMessageToFip(headVO, vo, object, message);
		} catch (BusinessException e) {

			ExceptionHandler.handleException(e);

		}
	}

	public static AggregatedValueObject createJkbxToFIPVO(JKBXVO vo, int message)
			throws BusinessException {
		AggregatedValueObject object = null;
		
		JKBXVO bxvo = ErVOUtils.prepareBxvoHeaderToItemClone(vo);

		// begin--added by chendya 如果有结算，取结算的单据项目设置结算信息
		// 判断CMP产品是否启用
		boolean isCmpInstalled = isCmpInstall(vo.getParentVO());
		if (isCmpInstalled) {
			ICmpSettlementPubQueryService queryService = NCLocator.getInstance().lookup(ICmpSettlementPubQueryService.class);
			nc.vo.cmp.settlement.SettlementAggVO[] settleAggVOs =  queryService.queryBillsBySourceBillID(new String[] { bxvo.getParentVO().getPk_jkbx() });
			if (settleAggVOs != null && settleAggVOs.length > 0) {
				// 有结算信息
				bxvo = splitVobySettinfo(bxvo, settleAggVOs[0]);
			}
		}
		// end

		if (message != FipMessageVO.MESSAGETYPE_DEL) {
			object = new FipUtil().addOtherInfo(bxvo);
		}
		return object;
	}

	/**
	 * 如果结算信息,且结算拆行支付，则将当前单据表体也拆行，因为拆行后进行支付，每一次支付都需要生成凭证，
	 * 如果结算拆航支付，当前单据不拆行，则生成凭证的金额就会不正确。
	 * 
	 * @author chendya
	 * @param vo
	 * @param settleVO
	 * @return
	 */
	public static JKBXVO splitVobySettinfo(JKBXVO vo, SettlementAggVO settle) {
		SettlementBodyVO[] settitemvos = (SettlementBodyVO[]) settle.getChildrenVO();
		BXBusItemVO[] itemvos = vo.getChildrenVO();
		MapList<String, SettlementBodyVO> tables = new MapList<String, SettlementBodyVO>();
		for (SettlementBodyVO item : settitemvos) {
			tables.put(item.getPk_billdetail(), item);
		}
		List<SettlementBodyVO> list = null;
		List<BXBusItemVO> splits = new ArrayList<BXBusItemVO>();
		for (BXBusItemVO item : itemvos) {
			list = tables.get(item.getPrimaryKey());
			if (list != null) {
				// 有结算信息，根据结算信息拆行
				for (SettlementBodyVO bodyVO : list) {
					BXBusItemVO itemVO = copySettleValue((BXBusItemVO) item.clone(), bodyVO);
					itemVO.setSettleBodyVO(bodyVO);
					splits.add(itemVO);
				}
			} else {
				//没有结算信息，直接加入
				splits.add(item);
			}
		}
		JKBXVO clone = (JKBXVO) vo.clone();
		clone.getParentVO().setSettleHeadVO((SettlementHeadVO) settle.getParentVO());
		clone.setChildrenVO(splits.toArray(new BXBusItemVO[0]));
		clone.setSettlevo(settle);
		clone.setContrastVO(vo.getContrastVO());
		return clone;
	}

	/**
	 * 拆行后,从结算信息VO拷贝值
	 * 
	 * @author chendya
	 * @param clone
	 * @param settle
	 * @return
	 */
	private static BXBusItemVO copySettleValue(BXBusItemVO clone, SuperVO settle) {
		String[] attributeNames = settle.getAttributeNames();
		for (int i = 0; i < attributeNames.length; i++) {
			clone.setAttributeValue(BXBusItemVO.SETTLE_BODY_PREFIX
					+ attributeNames[i], settle
					.getAttributeValue(attributeNames[i]));
		}
		clone.setSettleBodyVO(settle);
		return clone;
	}

	/**
	 * 传入会计平台的数据： 集团，组织，来源系统，业务日期，单据PK，单据类型 自定义项：报销管理在会计平台需要展示的项目
	 * 
	 * @param message
	 * */
	private void sendMessageToFip(JKBXHeaderVO headVO, JKBXVO bxvo, Object object,
			int message) throws BusinessException {

		FipRelationInfoVO reVO = new FipRelationInfoVO();
		// 具体设置信息应用会计平台
		reVO.setPk_group(headVO.getPk_group());
		reVO.setPk_org(headVO.getPk_org());
		reVO.setRelationID(headVO.getPk());
		reVO.setPk_system(BXConstans.ERM_PRODUCT_CODE_Lower);
		reVO.setBusidate(headVO.getJsrq() == null ? new UFDate() : headVO
				.getJsrq());
		reVO.setPk_billtype(headVO.getDjlxbm());
		reVO.setPk_operator(headVO.getOperator());
		reVO.setFreedef1(headVO.getDjbh());
		reVO.setFreedef2(headVO.getZy());
		UFDouble total = headVO.getTotal();

		// added by chendya 设置金额字段的精度
		total = total.setScale(Currency.getCurrDigit(headVO.getBzbm()),UFDouble.ROUND_HALF_UP);
		reVO.setFreedef3(String.valueOf(total));

		FipMessageVO messageVO = new FipMessageVO();
		messageVO.setBillVO(object);
		messageVO.setMessagetype(message);
		messageVO.setMessageinfo(reVO);
		try {
			new FipCallFacade().sendMessage(messageVO);
		} catch (BusinessException e) {
			ExceptionHandler.handleException(e);

		}
	}
	
	void beforeActInf(JKBXVO dJZB, String message) throws BusinessException {

		beforeActInf(new JKBXVO[] { dJZB }, message);
	}

	void afterActInf(JKBXVO dJZB, String message) throws BusinessException {

		afterActInf(new JKBXVO[] { dJZB }, message);
	}

	private BusiTransVO[] getBusiTransVO(String message)
			throws BusinessException {
		List<BusiTransVO> list = new ArrayList<BusiTransVO>();
		if (message.equals(MESSAGE_AUDIT)) {
			BusiTransVO vo = new BusiTransVO();
			vo.setInfClass(nc.bs.framework.core.util.ObjectCreator.newInstance(
					BXConstans.ERM_PRODUCT_CODE_Lower,
					"nc.impl.arap.bx.ArapBXVerifyEffImp"));
			list.add(vo);
		}
		if (message.equals(MESSAGE_UNAUDIT)) {
			BusiTransVO vo = new BusiTransVO();
			vo.setInfClass(nc.bs.framework.core.util.ObjectCreator.newInstance(
					BXConstans.ERM_PRODUCT_CODE_Lower,
					"nc.impl.arap.bx.ArapBXUnVerifyEffImp"));
			list.add(vo);
		}
		if (message.equals(MESSAGE_SAVE)) {
			BusiTransVO vo1 = new BusiTransVO();
			vo1.setInfClass(nc.bs.framework.core.util.ObjectCreator
					.newInstance(BXConstans.ERM_PRODUCT_CODE_Lower,
							"nc.impl.arap.bx.BXSaveJkControlEffImp"));
			list.add(vo1);
			BusiTransVO vo2 = new BusiTransVO();
			vo2.setInfClass(nc.bs.framework.core.util.ObjectCreator
					.newInstance(BXConstans.ERM_PRODUCT_CODE_Lower,
							"nc.impl.arap.bx.BXSaveYsControlEffImp"));
			list.add(vo2);
		}
		if (message.equals(MESSAGE_UPDATE)) {
			BusiTransVO vo1 = new BusiTransVO();
			vo1.setInfClass(nc.bs.framework.core.util.ObjectCreator
					.newInstance(BXConstans.ERM_PRODUCT_CODE_Lower,
							"nc.impl.arap.bx.BXUpdateYsControlEffImp"));
			list.add(vo1);
			BusiTransVO vo2 = new BusiTransVO();
			vo2.setInfClass(nc.bs.framework.core.util.ObjectCreator
					.newInstance(BXConstans.ERM_PRODUCT_CODE_Lower,
							"nc.impl.arap.bx.BXUpdateJkControlEffImp"));
			list.add(vo2);
		}
		if (message.equals(MESSAGE_DELETE)) {
			BusiTransVO vo1 = new BusiTransVO();
			vo1.setInfClass(nc.bs.framework.core.util.ObjectCreator
					.newInstance(BXConstans.ERM_PRODUCT_CODE_Lower,
							"nc.impl.arap.bx.BXDeleteYsControlEffImp"));
			list.add(vo1);
		}
		if (message.equals(MESSAGE_SETTLE)) {
			BusiTransVO vo1 = new BusiTransVO();
			vo1.setInfClass(nc.bs.framework.core.util.ObjectCreator
					.newInstance(BXConstans.ERM_PRODUCT_CODE_Lower,
							"nc.impl.arap.bx.BXSettleYsControlEffImp"));
			list.add(vo1);
			BusiTransVO vo2 = new BusiTransVO();
			vo2.setInfClass(nc.bs.framework.core.util.ObjectCreator
					.newInstance(BXConstans.ERM_PRODUCT_CODE_Lower,
							"nc.impl.arap.bx.BXSettleJkControlEffImp"));
			list.add(vo2);
		}
		if (message.equals(MESSAGE_UNSETTLE)) {
			BusiTransVO vo = new BusiTransVO();
			vo.setInfClass(nc.bs.framework.core.util.ObjectCreator.newInstance(
					BXConstans.ERM_PRODUCT_CODE_Lower,
					"nc.impl.arap.bx.BXUnSettleCjkControlEffImp"));
			list.add(vo);
			BusiTransVO vo1 = new BusiTransVO();
			vo1.setInfClass(nc.bs.framework.core.util.ObjectCreator
					.newInstance(BXConstans.ERM_PRODUCT_CODE_Lower,
							"nc.impl.arap.bx.BXUnSettleYsControlEffImp"));
			list.add(vo1);
		}

		return list.toArray(new BusiTransVO[] {});
	}

	/**
	 * 动作后事件处理
	 * @param vos
	 * @param message
	 * @throws BusinessException
	 */
	private void afterActInf(JKBXVO[] vos, String message)
			throws BusinessException {
		List<JKBXVO> listVOs = new ArrayList<JKBXVO>();
		for (JKBXVO vo : vos) {
			if (!vo.getParentVO().isNoOtherEffectItf()) {
				listVOs.add(vo);
			}
		}
		BusiTransVO[] busitransvos = getBusiTransVO(message);
		if (busitransvos != null) {
			for (int i = 0; i < busitransvos.length; i++) {
				try {
					Log.getInstance(this.getClass()).info(
							"afterActInf" + busitransvos[i].getInfClass()
									+ "end:" + System.currentTimeMillis()
									/ 1000);

					if (busitransvos[i].getInfClass() != null)
						((IBxPubActInterface) busitransvos[i].getInfClass())
								.afterEffectAct(listVOs.toArray(new JKBXVO[] {}));

					Log.getInstance(this.getClass()).info(
							"afterActInf" + busitransvos[i].getInfClass()
									+ "start:" + System.currentTimeMillis()
									/ 1000);

				} catch (ClassCastException e) {
					Log.getInstance(this.getClass()).error(e);
				} catch (BusinessException e) {
					throw ExceptionHandler.handleException(e);
				} catch (Throwable e) {
					Log.getInstance(this.getClass()).error(e.getMessage(), e);
					String strerr = busitransvos[i].getNote() + "\n"
							+ e.getMessage();
					throw new BusinessException(strerr, e);
				}
			}
		}
		
		//v6.1新增项目预算控制
		projectBudgetCtrl(vos,message);
		
		// 生成报销业务日志
		for (JKBXVO bxvo : vos) {
			if(message.equals(MESSAGE_UPDATE) || message.equals(MESSAGE_DELETE)){
				ErmBusiLogUtils.insertSmartBusiLogs(message.equals(MESSAGE_UPDATE)?true:false, bxvo);
			}
		}
	}
	
	/**
	 * 
	 * v6.1新增 项目预算执行VO实现
	 * 
	 * @author chendya
	 */

	private static List<IBudgetExecVO> getBudgetExecVOs(final JKBXVO vo) {
		List<IBudgetExecVO> resultVOs = new ArrayList<IBudgetExecVO>();

		for (final BXBusItemVO busItemVO : vo.getChildrenVO()) {
			resultVOs.add(new IBudgetExecVO() {
				public String getPk_wbs_exec() {
					return null;
				}

				public String getPk_wbs() {
					return vo.getParentVO().getProjecttask();
				}

				public String getPk_project() {
					return vo.getParentVO().getJobid();
				}

				public String getPk_factor() {
					return vo.getParentVO().getPk_checkele();
				}

				public String getPk_currtype() {
					return vo.getParentVO().getBzbm();
				}

				public UFDouble getNmoney() {
					return busItemVO.getYbje();
				}

				public String getBill_type() {
					final String djdl = vo.getParentVO().getDjdl();
					if (BXConstans.JK_DJDL.equals(djdl)) {
						return BXConstans.JK_DJLXBM;
					}
					return BXConstans.BX_DJLXBM;
				}

				public String getBill_transitype() {
					return vo.getParentVO().getDjlxbm();
				}

				public String getBill_id() {
					return vo.getParentVO().getPk_jkbx();
				}

				public String getBill_code() {
					return vo.getParentVO().getDjbh();
				}

				public String getBill_bid() {
					return null;
				}

				public Map getUserDefMap() {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put(JKBXHeaderVO.SZXMID, busItemVO.getSzxmid());
					map.put(BXBusItemVO.PK_REIMTYPE, busItemVO.getPk_reimtype());
					return map;
				}
			});
		}

		return resultVOs;
	}
	
	private static IBudgetExecVO[] getBudgetExecVOs(JKBXVO[] vos) {
		List<IBudgetExecVO> voList = new ArrayList<IBudgetExecVO>();
		for (JKBXVO vo : vos) {
			if(vo==null||vo.getParentVO()==null){
				continue;
			}
			//没有项目任务
			if(StringUtil.isEmpty(vo.getParentVO().getJobid())){
				continue;
			}
			
			voList.addAll(getBudgetExecVOs(vo));
		}
		return (IBudgetExecVO[]) voList.toArray(new IBudgetExecVO[0]);
	}
	
	
	
	private static JKBXVO[] getOldVOArray(JKBXVO[] vos) {
		List<JKBXVO> oldVOList = new ArrayList<JKBXVO>();
		for (JKBXVO vo : vos) {
			oldVOList.add(vo.getBxoldvo());
		}
		return oldVOList.toArray(new JKBXVO[0]);
	}
	
	/**
	 * v6.1 新增报销管理支持项目预算控制
	 * @author chendya
	 */
	private void projectBudgetCtrl(JKBXVO[] vos, String message) throws BusinessException {
		//项目管理是否安装
		boolean installed = BXUtil.isProductInstalled(vos[0].getParentVO().getPk_group(), BXConstans.PM_MODULEID);
		if (!installed) {
			return;
		}
		List<String> PROJECT_CTRL_MESSAGE = Arrays.asList(new String[] { MESSAGE_SAVE,
				MESSAGE_DELETE, MESSAGE_UPDATE, MESSAGE_SETTLE, MESSAGE_UNSETTLE });
		if (!PROJECT_CTRL_MESSAGE.contains(message)) {
			return;
		}

		// 当前项目预算执行vo
		IBudgetExecVO[] currBudgetExecVOs = getBudgetExecVOs(vos);
		if (currBudgetExecVOs.length == 0) {
			// 没有需要执行项目预算的单据
			return;
		}
		// 修改前项目预算执行vo
		IBudgetExecVO[] oldBudgetExecVOs = getBudgetExecVOs(getOldVOArray(vos));

		// 反生效或删除操作时old vo为空
		if (MESSAGE_DELETE.equals(message) || MESSAGE_UNSETTLE.equals(message)) {
			oldBudgetExecVOs = null;
		}

		// 调用项目预算接口执行项目预算
		BudgetCtlInfoMSG retMsg = NCLocator.getInstance().lookup(IBudgetExecute.class)
				.executeBudgetWithCheck(currBudgetExecVOs, oldBudgetExecVOs, getBudgetCtrlPoint(message), getBudgetOperTypeENum(message), getBudgetCtrlMap(message));
		if (retMsg != null && retMsg.getDetailList() != null) {
			List<BudgetCtlInfoVO> ctrlInfos = retMsg.getDetailList();
			StringBuffer buffer = new StringBuffer();
			for (BudgetCtlInfoVO info : ctrlInfos) {
				if (info != null) {
					buffer.append(info.toString());
				}
			}
			Log.getInstance(getClass()).debug(buffer.toString());
		}
	}
	
	
	
	private int getBudgetCtrlPoint(final String message) {
		if (MESSAGE_SAVE.equals(message) || MESSAGE_UPDATE.equals(message)|| MESSAGE_DELETE.equals(message)) {
			return nc.vo.pmbd.budgetctrl.BudgetCtrlPoint.save_control;
		}
		else if (MESSAGE_AUDIT.equals(message) || MESSAGE_UNAUDIT.equals(message)) {
			return nc.vo.pmbd.budgetctrl.BudgetCtrlPoint.check_control;
		}
		return nc.vo.pmbd.budgetctrl.BudgetCtrlPoint.effext_control;
	}

	private nc.itf.pim.budget.pub.BudgetOperTypeENum getBudgetOperTypeENum(String message) {
		if (MESSAGE_SAVE.equals(message)||MESSAGE_SETTLE.equals(message)) {
			return nc.itf.pim.budget.pub.BudgetOperTypeENum.OPER_ADD_NEW;
		} else if (MESSAGE_DELETE.equals(message)||MESSAGE_UNSETTLE.equals(message)) {
			return nc.itf.pim.budget.pub.BudgetOperTypeENum.OPER_CANCEL;
		}
		return nc.itf.pim.budget.pub.BudgetOperTypeENum.OPER_UPDATE;
	}

	private Map<Integer, Boolean> getBudgetCtrlMap(final String message) {
		if (MESSAGE_SAVE.equals(message) || MESSAGE_UPDATE.equals(message)|| MESSAGE_DELETE.equals(message)) {
			return nc.vo.pmbd.budgetctrl.BudgetCtrlMapConst.getBudget_map_save();
		}
		return nc.vo.pmbd.budgetctrl.BudgetCtrlMapConst.getBudget_map_approve();
	}
	
	
	/**
	 * 单据生效传会计平台
	 * 
	 * @throws BusinessException
	 */
	private void effectToFip(JKBXVO bxvo, String message) throws BusinessException {
		if (getDapMessage(bxvo.getParentVO(), message) != MESSAGE_NOTSEND) {
			sendMessage(bxvo, getDapMessage(bxvo.getParentVO(), message));
		}
	}
	
	/**
	 * 单据生效传会计平台
	 * 
	 * @throws BusinessException
	 */
	public void effectToFip(List<JKBXVO> listVOs, String message) throws BusinessException {
		// 发送会计平台
		for (Iterator<JKBXVO> iter = listVOs.iterator(); iter.hasNext();) {
			effectToFip(iter.next(), message);
		}
	}

	/**
	 * Action触发之前的动作
	 * 
	 * @param vos
	 * @param message
	 * @throws BusinessException
	 */
	private void beforeActInf(JKBXVO[] vos, String message) throws BusinessException {
		List<JKBXVO> listVOs = new ArrayList<JKBXVO>();
		for (JKBXVO vo : vos) {
			if (!vo.getParentVO().isNoOtherEffectItf()) {
				listVOs.add(vo);
			}
		}
		BusiTransVO[] busitransvos = getBusiTransVO(message);

		if (busitransvos != null) {
			for (int i = 0; i < busitransvos.length; i++) {
				try {
					Log.getInstance(this.getClass()).info(
							"beforeEffectAct" + busitransvos[i].getInfClass() + "start:"
									+ System.currentTimeMillis());

					if (busitransvos[i].getInfClass() != null)
						((IBxPubActInterface) busitransvos[i].getInfClass())
								.beforeEffectAct(listVOs.toArray(new JKBXVO[] {}));

					Log.getInstance(this.getClass()).info(
							"beforeEffectAct" + busitransvos[i].getInfClass() + "end:"
									+ System.currentTimeMillis());
				} catch (ClassCastException e) {
				} catch (BusinessException e) {
					throw ExceptionHandler.handleException(e);
				} catch (Exception e) {
					Log.getInstance(this.getClass()).error(e.getMessage(), e);
					String strerr = busitransvos[i].getNote() + "\n" + e.getMessage();
					throw new BusinessException(strerr, e);
				}
			}
		}
	}

	// 判断签字时候传会计平台
	private int getDapMessage(JKBXHeaderVO head, String message) {
		int msg = MESSAGE_NOTSEND;

		if (message.equals(MESSAGE_SETTLE)) {
			msg = FipMessageVO.MESSAGETYPE_ADD;
		} else if (message.equals(MESSAGE_UNSETTLE)) {
			msg = FipMessageVO.MESSAGETYPE_DEL;
		} else if (head.getQcbz().equals("Y") && message.equals(MESSAGE_SAVE)) {
			msg = FipMessageVO.MESSAGETYPE_ADD;
		} else if (head.getQcbz().equals("Y") && message.equals(MESSAGE_DELETE)) {
			msg = FipMessageVO.MESSAGETYPE_DEL;
		}
		return msg;
	}

	public List<JKBXHeaderVO> queryHeaders(Integer start, Integer count,
			DjCondVO condVO) throws BusinessException {
		try {
			return getJKBXDAO().queryHeaders(start, count, condVO);
		} catch (Exception e) {
			throw ExceptionHandler.handleException(e);
		}
	}

	public List<JKBXHeaderVO> queryHeadersByWhereSql(String sql, String djdl)
			throws BusinessException {
		try {
			return getJKBXDAO().queryHeadersByWhereSql(sql, djdl);
		} catch (Exception e) {
			throw ExceptionHandler.handleException(e);
		}
	}

	private static void lockDJ(String pk) throws BusinessException {
		String lock = KeyLock.dynamicLock(pk);
		if (lock != null) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl
					.getNCLangRes().getStrByID("2011", "UPP2011-000359"));
		}
	}

	public void compareTs(JKBXHeaderVO[] vos) throws BusinessException {
		Hashtable<String, String> ts = new Hashtable<String, String>();
		Hashtable<String, String> ts2 = new Hashtable<String, String>();
		Hashtable<String, String> ts3 = new Hashtable<String, String>();
		for (JKBXHeaderVO vo : vos) {
			if (vo.isInit()) {
				ts3.put(vo.getPk_jkbx(), vo.getTs().toString());
			} else if (vo.getDjdl().equals(BXConstans.BX_DJDL)) {
				ts.put(vo.getPk_jkbx(), vo.getTs().toString());
			} else {
				ts2.put(vo.getPk_jkbx(), vo.getTs().toString());
			}
		}
		if (ts.size() != 0)
			compareTS(ts, "er_bxzb", JKBXHeaderVO.PK_JKBX);
		if (ts2.size() != 0)
			compareTS(ts2, "er_jkzb", JKBXHeaderVO.PK_JKBX);
		if (ts3.size() != 0)
			compareTS(ts3, "er_jkbx_init", JKBXHeaderVO.PK_JKBX);
	}

	public void compareTs(JKBXVO[] vos) throws BusinessException {
		Hashtable<String, String> ts = new Hashtable<String, String>();
		Hashtable<String, String> ts2 = new Hashtable<String, String>();
		Hashtable<String, String> ts3 = new Hashtable<String, String>();

		for (JKBXVO vo : vos) {
			UFDateTime ts4 = vo.getParentVO().getTs();
			String pk_jkbx = vo.getParentVO().getPk_jkbx();

			if (ts4 == null || pk_jkbx == null)
				continue;

			if (vo.getParentVO().isInit()) {
				ts3.put(pk_jkbx, ts4.toString());
			} else if (vo.getParentVO().getDjdl().equals(BXConstans.BX_DJDL)) {
				ts.put(pk_jkbx, ts4.toString());
			} else {
				ts2.put(pk_jkbx, ts4.toString());
			}
		}

		if (ts.size() != 0)
			compareTS(ts, "er_bxzb", JKBXHeaderVO.PK_JKBX);
		if (ts2.size() != 0)
			compareTS(ts2, "er_jkzb", JKBXHeaderVO.PK_JKBX);
		if (ts3.size() != 0)
			compareTS(ts3, "er_jkbx_init", JKBXHeaderVO.PK_JKBX);
	}

	@SuppressWarnings("unchecked")
	public static void compareTS(Hashtable<String, String> ts,
			String tableName, String pkField) throws BusinessException {

		String tname = null;
		Connection con = null;
		PreparedStatement stat = null;
		try {
			BaseDMO dmo = new BaseDMO();
			con = dmo.getConnection();

			nc.bs.mw.sqltrans.TempTable tmptab = new nc.bs.mw.sqltrans.TempTable();

			tname = tmptab.createTempTable(con, "ErmTsTemp",
					"pk char(20),ts1 char(19)", "pk");

			String sql = "insert into " + tname + " (pk,ts1) values(?,?)";
			((CrossDBConnection) con).setAddTimeStamp(false);
			stat = con.prepareStatement(sql);
			Set key = ts.keySet();
			Iterator it = key.iterator();
			while (it.hasNext()) {
				String pk = (String) it.next();
				String t = ts.get(pk);
				stat.setString(1, pk);
				stat.setString(2, t);
				stat.addBatch();

				lockDJ(pk);
			}
			stat.executeBatch();

			String sql_n = "select count(temp.pk) from " + tname
					+ " temp inner join " + tableName + " tab on temp.pk=tab."
					+ pkField + " where temp.ts1=tab.ts";
			stat = con.prepareStatement(sql_n);
			ResultSet rs = stat.executeQuery();
			int result = 0;
			if (rs.next())
				result = rs.getInt(1);
			if (result != ts.size())
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl
						.getNCLangRes().getStrByID("2011", "UPP2011-000359")/*
																			 * @res
																			 * "并发异常，数据已经更新，请重新查询数据后操作"
																			 */);

			String sql_ts = " update " + tableName + " set dr=dr where "
					+ pkField + " in (select pk from " + tname + ") ";
			stat = con.prepareStatement(sql_ts);
			int count = stat.executeUpdate();
			if (count != ts.size())
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl
						.getNCLangRes().getStrByID("2011", "UPP2011-000359")/*
																			 * @res
																			 * "并发异常，数据已经更新，请重新查询数据后操作"
																			 */);

		} catch (Exception e) {
			throw ExceptionHandler.handleException(e);
		} finally {
			if (con != null)
				try {
					con.close();
				} catch (SQLException e) {
					// ...
				}
		}
	}

	public Collection<JsConstrasVO> queryJsContrasts(JKBXHeaderVO header)
			throws BusinessException {

		String pk_jkbx = header.getPk_jkbx();

		Collection<JsConstrasVO> vos = null;
		try {
			vos = getJKBXDAO().retrieveJsContrastByClause(
					JsConstrasVO.PK_BXD + "='" + pk_jkbx + "'");
		} catch (Exception e) {
			throw ExceptionHandler.handleException(e);
		}
		return vos;
	}

	public Collection<BxcontrastVO> queryContrasts(JKBXHeaderVO parentVO)
			throws BusinessException {

		String pk_jkbx = parentVO.getPk_jkbx();
		String key = null;

		if (parentVO.getDjdl().equals(BXConstans.BX_DJDL)) {
			key = BxcontrastVO.PK_BXD;
		} else {
			key = BxcontrastVO.PK_JKD;
		}

		Collection<BxcontrastVO> vos = null;
		try {
			vos = getJKBXDAO().retrieveContrastByClause(
					key + "='" + pk_jkbx + "'");
		} catch (Exception e) {
			throw ExceptionHandler.handleException(e);
		}

		return vos;

	}

	public Collection<BxcontrastVO> queryContrasts(JKBXHeaderVO[] parentVO,
			String djdl) throws BusinessException {

		String key = null;

		if (djdl.equals(BXConstans.BX_DJDL)) {
			key = BxcontrastVO.PK_BXD;
		} else {
			key = BxcontrastVO.PK_JKD;
		}

		Collection<BxcontrastVO> vos = null;
		try {
			vos = getJKBXDAO().retrieveContrastByClause(
					SqlUtils.getInStr(key, parentVO, JKBXHeaderVO.PK_JKBX));
		} catch (Exception e) {
			throw ExceptionHandler.handleException(e);
		}

		return vos;

	}

	public List<JKBXHeaderVO> queryHeadersByPrimaryKeys(String[] keys, String djdl)
			throws BusinessException {
		List<JKBXHeaderVO> headVos = null;
		try {

			String inStr = SqlUtils.getInStr(JKBXHeaderVO.PK_JKBX, keys);

			headVos = getJKBXDAO().queryHeadersByWhereSql(" where zb.dr=0 and " + inStr, djdl);
			
		} catch (Exception e) {
			throw ExceptionHandler.handleException(e);
		}
		return headVos;
	}

	public int querySize(DjCondVO condVO) throws BusinessException {
		int querySize = -99;
		try {
			querySize = getJKBXDAO().querySize(condVO);
		} catch (Exception e) {
			throw ExceptionHandler.handleException(e);
		}
		return querySize;
	}

	public void updateQzzt(JKBXVO[] vos) throws BusinessException {
		JKBXHeaderVO[] heads = new JKBXHeaderVO[vos.length];
		for (int i = 0; i < heads.length; i++) {
			heads[i] = vos[i].getParentVO();
		}
		try {
			getJKBXDAO().update(heads, new String[] { JKBXHeaderVO.QZZT });
		} catch (Exception e) {
			throw ExceptionHandler.handleException(e);
		}
	}

	public Collection<BxcontrastVO> queryJkContrast(JKBXVO[] selBxvos,
			boolean isBatch) throws BusinessException {
		Collection<BxcontrastVO> jkContrasVOs = null;
		try {
			String[] bxdKeys = new String[selBxvos.length];
			for (int i = 0; i < bxdKeys.length; i++) {
				bxdKeys[i] = selBxvos[i].getParentVO().getPk_jkbx();
			}
			String sqlJK = SqlUtils.getInStr(BxcontrastVO.PK_BXD, bxdKeys);
			jkContrasVOs = getJKBXDAO().retrieveContrastByClause(sqlJK);

			if (isBatch) {
				List<String> cjk_pc = new ArrayList<String>();
				for (BxcontrastVO vo : jkContrasVOs) {
					if (!StringUtils.isNullWithTrim(vo.getPk_pc())) {
						cjk_pc.add(vo.getPk_pc());
					}
				}

				Collection<BxcontrastVO> batchVos = getJKBXDAO()
						.retrieveContrastByClause(
								SqlUtils.getInStr(BxcontrastVO.PK_PC, cjk_pc
										.toArray(new String[] {})));

				jkContrasVOs.addAll(batchVos);
			}

		} catch (Exception e) {
			throw ExceptionHandler.handleException(e);
		}
		return jkContrasVOs;

	}

	public void saveSqdlrs(List<String> roles, SqdlrVO[] sqdlrVOs)
			throws BusinessException {
		BaseDAO dao = new BaseDAO();
		List<SqdlrVO> insertVos = new ArrayList<SqdlrVO>();
		List<SqdlrVO> updateVos = new ArrayList<SqdlrVO>();
		for (SqdlrVO sqdlrVO : sqdlrVOs) {
			for (String role : roles) {
				if (sqdlrVO.getPk_authorize() != null) {
					if (!role.equals(sqdlrVO.getPk_roler())) {
						addNewSqdlrVO(insertVos, sqdlrVO, role);
					} else {
						updateVos.add(sqdlrVO);
					}
				} else {
					addNewSqdlrVO(insertVos, sqdlrVO, role);
				}

			}
		}
		try {
			dao.insertVOArray(insertVos.toArray(new SqdlrVO[insertVos.size()]));
			dao.updateVOArray(updateVos.toArray(new SqdlrVO[0]));
		} catch (DAOException e) {
			throw ExceptionHandler.handleException(e);
		}
	}

	private void addNewSqdlrVO(List<SqdlrVO> insertVos, SqdlrVO sqdlrVO,
			String role) throws BusinessException {
		if (!isAsigned(role, sqdlrVO.getPk_user(),
				(sqdlrVO.getKeyword() != null ? sqdlrVO.getKeyword()
						: ISqdlrKeyword.KEYWORD_BUSIUSER))) {
			SqdlrVO o = new SqdlrVO();
			o.setPk_roler(role);
			o.setPk_org(sqdlrVO.getPk_org());
			o.setPk_user(sqdlrVO.getPk_user());
			o.setKeyword(sqdlrVO.getKeyword() != null ? sqdlrVO.getKeyword()
					: ISqdlrKeyword.KEYWORD_BUSIUSER);
			o.setType(0);
			insertVos.add(o);
		}
	}

	@SuppressWarnings("unchecked")
	public boolean isAsigned(String pk_role, String user, String keyword)
			throws BusinessException {
		BaseDAO dao = new BaseDAO();
		String condition = " pk_roler = '" + pk_role + "' and pk_user = '"
				+ user + "' and keyword = '" + keyword + "'";
		Collection c = null;
		try {
			c = dao.retrieveByClause(SqdlrVO.class, condition);
		} catch (DAOException e) {
			throw ExceptionHandler.handleException(e);
		}
		if (c != null && c.size() > 0)
			return true;
		return false;
	}

	@SuppressWarnings("unchecked")
	public boolean isAsigned(String pk_role, String user)
			throws BusinessException {
		BaseDAO dao = new BaseDAO();
		String condition = " pk_roler = '" + pk_role + "' and pk_user = '"
				+ user + "'";
		Collection c = null;
		try {
			c = dao.retrieveByClause(SqdlrVO.class, condition);
		} catch (DAOException e) {
			throw ExceptionHandler.handleException(e);
		}
		if (c != null && c.size() > 0)
			return true;
		return false;
	}

	@SuppressWarnings("unchecked")
	public Map<String, List<SqdlrVO>> querySqdlr(String[] pk_roles,
			String... ywy_corps) throws BusinessException {
		BaseDAO dao = new BaseDAO();
		StringBuffer condition = null;
		try {
			condition = new StringBuffer(SqlUtils
					.getInStr("pk_roler", pk_roles));
			if (ywy_corps != null && ywy_corps.length > 0) {
				condition.append(" and ");
				condition.append(SqlUtils.getInStr("pk_corp", ywy_corps));
			}
			condition.append(" and type=0 ");
			Collection c = null;
			c = dao.retrieveByClause(SqdlrVO.class, condition.toString());
			Map<String, List<SqdlrVO>> vos = new HashMap<String, List<SqdlrVO>>();
			if (c != null) {
				for (Object o : c) {
					if (vos.get(((SqdlrVO) o).getPk_roler()) == null) {
						vos.put(((SqdlrVO) o).getPk_roler(),
								new ArrayList<SqdlrVO>());
					}
					vos.get(((SqdlrVO) o).getPk_roler()).add((SqdlrVO) o);
				}
			}
			return vos;
		} catch (Exception e1) {
			throw ExceptionHandler.handleException(e1);
		}

	}

	public void delSqdlrs(List<String> roles, SqdlrVO[] sqdlrVOs)
			throws BusinessException {
		BaseDAO dao = new BaseDAO();
		try {
			StringBuffer condition = new StringBuffer();
			for (String role : roles) {
				for (SqdlrVO sqdlrVO : sqdlrVOs) {
					condition.append(" pk_roler = '" + role
							+ "' and pk_user = '" + sqdlrVO.getPk_user() + "'");
					dao.deleteByClause(SqdlrVO.class, condition.toString());
					condition = new StringBuffer();
				}
			}

		} catch (DAOException e) {
			throw ExceptionHandler.handleException(e);
		}

	}

	public Collection<JsConstrasVO> queryJsContrastsByJsd(String pk_jsd)
			throws BusinessException {

		Collection<JsConstrasVO> vos;
		try {
			vos = getJKBXDAO().retrieveJsContrastByClause(
					JsConstrasVO.PK_JSD + "='" + pk_jsd + "'");
		} catch (Exception e) {
			throw ExceptionHandler.handleException(e);
		}

		return vos;
	}

	public void unSettleFromArap(JKBXVO[] vos) throws BusinessException {
		unSettle(vos, false);
	}

	public JKBXVO[] unSettle(JKBXVO[] vos) throws BusinessException {
		return unSettle(vos, true);
	}

	public List<SqdlrVO> querySqdlr(String pk_user, String user_corp,
			String ywy_corp) throws BusinessException {
		nc.pubitf.rbac.IRolePubService service = NCLocator.getInstance()
				.lookup(nc.pubitf.rbac.IRolePubService.class);
		try {
			RoleVO[] roleVOs = service.queryRoleByUserID(pk_user, user_corp);
			List<String> roles = new ArrayList<String>();
			for (RoleVO roleVO : roleVOs) {
				roles.add(roleVO.getPk_role());
			}
			Map<String, List<SqdlrVO>> sqdlrMap = querySqdlr(roles
					.toArray(new String[roles.size()]), ywy_corp);
			List<SqdlrVO> result = new ArrayList<SqdlrVO>();
			for (String role : sqdlrMap.keySet()) {
				result.addAll(sqdlrMap.get(role));
			}
			return result;
		} catch (BusinessException e) {
			throw ExceptionHandler.handleException(e);
		}
	}

	public void savedefSqdlrs(List<String> roles, Map<String, String[]> defMap)
			throws BusinessException {
		List<SqdlrVO> vos = new ArrayList<SqdlrVO>();
		for (String key : defMap.keySet()) {
			if (defMap.get(key) != null) {
				deldefSqdlrs(roles, key, defMap.get(key));
				for (String def : defMap.get(key)) {
					SqdlrVO vo = new SqdlrVO();
					vo.setKeyword(key);
					vo.setPk_user(def);
					vo.setType(0);
					vos.add(vo);
				}
			} else {
				deldefSqdlrs(roles, key, new String[] {});
			}

		}
		saveSqdlrs(roles, vos.toArray(new SqdlrVO[vos.size()]));

	}

	private void deldefSqdlrs(List<String> roles, String key, String[] pk_defs)
			throws BusinessException {
		BaseDAO dao = new BaseDAO();
		List<String> curDefls = new ArrayList<String>();
		for (String pk_def : pk_defs) {
			curDefls.add(pk_def);
		}
		for (String role : roles) {
			SqdlrVO[] sqdlrVOs = getAssigned(role, key);
			if (sqdlrVOs == null)
				break;
			for (SqdlrVO o : sqdlrVOs) {
				if (!curDefls.contains(o.getPk_user())) {
					try {
						dao.deleteVO(o);
					} catch (DAOException e) {
						throw ExceptionHandler.handleException(e);
					}
				}
			}
		}
	}

	private SqdlrVO[] getAssigned(String pk_role, String keyword) {
		BaseDAO dao = new BaseDAO();
		String condition = " pk_roler = '" + pk_role + "' and keyword = '"
				+ keyword + "'";
		Collection<?> c = null;
		try {
			c = dao.retrieveByClause(SqdlrVO.class, condition);
		} catch (DAOException e) {
			nc.bs.logging.Log.getInstance(this.getClass()).error(e);
			;
		}
		if (c != null && c.size() > 0)
			return c.toArray(new SqdlrVO[c.size()]);
		return null;
	}

	public JKBXHeaderVO updateHeader(JKBXHeaderVO header, String[] fields)
			throws BusinessException {
		compareTs(new JKBXHeaderVO[] { header });
		updateHeaders(new JKBXHeaderVO[] { header }, fields);
		return header;
	}

}