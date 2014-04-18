package nc.bs.erm.costshare;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import nc.bs.arap.bx.BusiLogUtil;
import nc.bs.businessevent.EventDispatcher;
import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.er.util.SqlUtils;
import nc.bs.erm.costshare.util.CostShareMapProcessor;
import nc.bs.erm.costshare.util.CostShareVOChecker;
import nc.bs.erm.event.ErmBusinessEvent;
import nc.bs.erm.event.ErmEventType;
import nc.bs.erm.util.ErLockUtil;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.itf.fi.pub.Currency;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.md.model.MetaDataException;
import nc.pubitf.erm.costshare.IErmCostShareBillQuery;
import nc.pubitf.fip.service.IFipMessageService;
import nc.vo.arap.bx.util.ActionUtils;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.arap.transaction.DataValidateException;
import nc.vo.ep.bx.BXHeaderVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.MtappfUtil;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.erm.common.MessageVO;
import nc.vo.erm.costshare.AggCostShareVO;
import nc.vo.erm.costshare.CShareDetailVO;
import nc.vo.erm.costshare.CostShareVO;
import nc.vo.erm.matterappctrl.MtapppfVO;
import nc.vo.erm.util.VOUtils;
import nc.vo.fip.service.FipMessageVO;
import nc.vo.fip.service.FipRelationInfoVO;
import nc.vo.fipub.billcode.FinanceBillCodeInfo;
import nc.vo.fipub.billcode.FinanceBillCodeUtils;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.SuperVO;
import nc.vo.pub.VOStatus;
import nc.vo.pub.ValidationException;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.util.AuditInfoUtil;
import nc.vo.util.BDReferenceChecker;
import nc.vo.util.BDVersionValidationUtil;

import org.apache.commons.lang.ArrayUtils;

/**
 * 费用结转单业务类
 *
 * @author lvhj
 *
 */
public class ErmCostShareBO {

	private ErmCostShareDAO dao;

	private ErmCostShareDAO getDAO() {
		if (dao == null) {
			dao = new ErmCostShareDAO();
		}
		return dao;
	}

	public AggCostShareVO insertVO(AggCostShareVO vo) throws BusinessException {
		AggCostShareVO result = null;
		// 新增加锁
		insertlockOperate(vo);
		CostShareVO parentVO = (CostShareVO) vo.getParentVO();
		//拉单校验，校验报销单版本
		try {
			JKBXHeaderVO bxheadvo = parentVO.getBxheadvo();
			if (!bxheadvo.isInit()) {
				BDVersionValidationUtil.validateVersion(bxheadvo);
			}
		} catch (BusinessException e) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0","0upp2012V575-0107"),e);/*"报销单已被取消生效或删除，请检查")*/
		}
		// 唯一性校验
		insertvalidate(parentVO);
		// vo校验
		CostShareVOChecker vochecker = new CostShareVOChecker();
		vochecker.checkSave(vo);
		try {
			// 获取单据号
			createBillNo(vo);
			// 设置审计信息
			AuditInfoUtil.addData(parentVO);
			// 设置默认值
			setDefaultValue(vo, true);
			// 新增前事件处理
			fireBeforeInsertEvent(vo);
			// 新增保存
			result = getDAO().insertVO(vo);
			// 新增后事件处理
			fireAfterInsertEvent(vo);
		} catch (Exception e) {
			returnBillno(new AggCostShareVO[] { vo });
			ExceptionHandler.handleException(e);
		}
		// 查询返回结果
		IErmCostShareBillQuery qryservice = NCLocator.getInstance().lookup(IErmCostShareBillQuery.class);
		result = qryservice.queryBillByPK(vo.getParentVO().getPrimaryKey());
//		// 记录业务日志
//		ErmBusiLogUtils.insertSmartBusiLogs(new AggCostShareVO[]{vo}, null, IErmCostShareConst.CS_MD_INSERT_OPER);
		// 返回
		return result;
	}

	public AggCostShareVO tempInsertVO(AggCostShareVO vo) throws BusinessException {
		// 新增加锁
		insertlockOperate(vo);
		// 唯一性校验
		insertvalidate((CostShareVO) vo.getParentVO());
		// vo校验
		CostShareVOChecker vochecker = new CostShareVOChecker();
		vochecker.checkSave(vo);
		try{
			// 获取单据号
			createBillNo(vo);
			// 设置审计信息
			AuditInfoUtil.addData(vo.getParentVO());
			// 设置默认值
			setDefaultValue(vo, true);
			// 新增前事件处理
			fireBeforeTempInsertEvent(vo);
			// 新增保存
			vo = getDAO().insertVO(vo);
			// 新增后事件处理
			fireAfterTempInsertEvent(vo);
		}catch (Exception e) {
			 returnBillno(new AggCostShareVO[]{vo});
			 ExceptionHandler.handleException(e);
		}
//		// 记录业务日志
//		ErmBusiLogUtils.insertSmartBusiLogs(new AggCostShareVO[]{vo}, null, IErmCostShareConst.CS_MD_INSERT_OPER);
		// 返回
		return vo;
	}
	public AggCostShareVO tempUpdateVO(AggCostShareVO vo) throws BusinessException {
		return vo;
	}

	private void unInureValidate(CostShareVO vo) throws BusinessException {
		String sql = "select top 1 1 from " +
					BXConstans.BX_TABLENAME + " where " + BXHeaderVO.PK_JKBX +" = ? "+" and " +BXHeaderVO.SXBZ+	"="+BXStatusConst.SXBZ_VALID;
		SQLParameter sqlParameter = new SQLParameter();
		sqlParameter.addParam(vo.getSrc_id());

		Integer count = null;
		try {
			count = (Integer)new BaseDAO().executeQuery(sql, sqlParameter, new ColumnProcessor());
		} catch (DAOException e) {
			Logger.error("查询报销反生效校验，查询失败",e);
			throw new BusinessException(e);
		}
		if(count == null || count.intValue() == 0){
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0095")/*@res "报销单已被取消生效或删除，请检查"*/);
		}
	}

	private void insertvalidate(CostShareVO vo) throws BusinessException {
		onlyValidate(vo);
		if(IErmCostShareConst.CostShare_Bill_SCRTYPE_SEL == vo.getSrc_type()){
			unInureValidate(vo);
		}
	}

	private void onlyValidate(CostShareVO vo) throws BusinessException {
		// 唯一性校验，对应报销单不可重复

		String sql = "select top 1 1 from " + vo.getTableName() + " where " + CostShareVO.SRC_ID +" = ? ";

		SQLParameter sqlParameter = new SQLParameter();
		sqlParameter.addParam(vo.getSrc_id());

		Integer count = null;
		try {
			count = (Integer)new BaseDAO().executeQuery(sql, sqlParameter, new ColumnProcessor());
		} catch (DAOException e) {
			Logger.error("费用结转单唯一性校验，查询失败",e);
			throw new BusinessException(e);
		}
		if(count == null || count.intValue() == 0){
			return ;
		}
		throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0096")/*@res "拉单重复，请重新选择报销单"*/);
	}

	public AggCostShareVO updateVO(AggCostShareVO vo) throws BusinessException {
		// 修改加锁
		updatelockOperate(vo);
		// 版本校验
		CircularlyAccessibleValueObject parentVO = vo.getParentVO();
		BDVersionValidationUtil.validateVersion(parentVO);
		// vo校验
		CostShareVOChecker vochecker = new CostShareVOChecker();
		vochecker.checkSave(vo);
		// 设置默认值
		setDefaultValue(vo, false);
		// 设置审计信息
		AuditInfoUtil.updateData(parentVO);
		// 查询修改前的vo
		IErmCostShareBillQuery qryservice = NCLocator.getInstance().lookup(IErmCostShareBillQuery.class);
		AggCostShareVO oldvo = null;
		if(vo.getOldvo() != null){
			oldvo = vo.getOldvo();
		}else{
			oldvo = qryservice.queryBillByPK(parentVO.getPrimaryKey());
		}
		// 补充查询事前分摊拉单执行情况
		fillUpMapf(oldvo);
		// 修改前事件处理
		fireBeforeUpdateEvent(vo,oldvo);
		// 更新保存
		vo = getDAO().updateVO(vo);
		// 修改后事件处理
		fireAfterUpdateEvent(vo,oldvo);
		// 记录业务日志
		BusiLogUtil.insertSmartBusiLog(IErmCostShareConst.CS_MD_UPDATE_OPER,new AggCostShareVO[]{vo}, new AggCostShareVO[]{oldvo});
		// 查询返回结果
		vo = qryservice.queryBillByPK(parentVO.getPrimaryKey());
		// 返回
		return vo;
	}
	
	/**
	 * 补充事前分摊情况申请执行记录
	 * 
	 * @param aggvo
	 * @throws BusinessException
	 */
	private void fillUpMapf(AggCostShareVO... aggvos) throws BusinessException {
		if(aggvos.length == 1){
			MtapppfVO[] pfVos = MtappfUtil.getMaPfVosByCsVo(aggvos[0]);
			aggvos[0].setMaPfVos(pfVos);
		}else{
			MtapppfVO[] pfVos = MtappfUtil.getMaPfVosByCsVo(aggvos);
			if(pfVos != null && pfVos.length > 0){
				Map<String, List<SuperVO>> bxPfMap = VOUtils.changeCollectionToMapList(Arrays.asList(pfVos), MtapppfVO.BUSI_PK);
				for (int i = 0; i < aggvos.length; i++) {
					String bxPK = ((CostShareVO)aggvos[i].getParentVO()).getSrc_id();
					aggvos[i].setMaPfVos(bxPfMap.get(bxPK)==null?null:bxPfMap.get(bxPK).toArray(new MtapppfVO[0]));
				}
			}
		}
		
	}

	/**
	 * 删除费用结转单（假批量）
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	public MessageVO[] deleteVOs(AggCostShareVO[] vos) throws BusinessException {
		if (vos == null || vos.length == 0) {
			return null;
		}
		//记录日志，由于需要单个提示，所以不支持批量操作
		MessageVO[] cm = new MessageVO[vos.length];
		for (int i = 0; i < vos.length; i++) {
			cm[i] = new MessageVO(vos[i],ActionUtils.DELETE,true,"");
			try {
				doDeleteVOs(new AggCostShareVO[] {vos[i]});
			} catch (Exception e) {
				ExceptionHandler.handleException(e);
			}
		}
		return cm;
	}

	public void doDeleteVOs(AggCostShareVO[] vos) throws BusinessException,
			DataValidateException, ValidationException {
		// 删除加锁
		deletelockOperate(vos);
		// 版本校验
		BDVersionValidationUtil.validateVersion(vos);
		// 删除引用校验
		deleteValidate(vos);
		// vo校验
		CostShareVOChecker vochecker = new CostShareVOChecker();
		vochecker.checkDelete(vos);
		// 补充事前分摊拉单执行记录
		fillUpMapf(vos);
		// 删除前事件处理
		fireBeforeDeleteEvent(vos);
		// 删除单据
		getDAO().deleteVOs(vos);
		// 修改后事件处理
		fireAfterDeleteEvent(vos);
		// 退还单据号
		returnBillno(vos);
		// 记录业务日志
		for (AggCostShareVO aggCostShareVO : vos) {
			CircularlyAccessibleValueObject[] childrenVO = aggCostShareVO.getChildrenVO();
			if (!ArrayUtils.isEmpty(childrenVO)) {
				for (CircularlyAccessibleValueObject detailVo : childrenVO) {
					detailVo.setStatus(VOStatus.DELETED);
				}
			}
		}
		BusiLogUtil.insertSmartBusiLog(IErmCostShareConst.CS_MD_DELETE_OPER,vos, null);
	}


	public MessageVO[] approveVOs(AggCostShareVO[] vos,UFDate buDate)
	throws BusinessException {
		if (vos == null || vos.length == 0) {
			return null;
		}
		//记录日志，由于需要单个提示，所以不支持批量操作
		MessageVO[] cm = new MessageVO[vos.length];
		for (int i = 0; i < vos.length; i++) {
			cm[i] = new MessageVO(vos[i],ActionUtils.CONFIRM,true,"");
			try {
				doApproveVOs(new AggCostShareVO[] {vos[i]},buDate);
			} catch (Exception e) {
				ExceptionHandler.handleException(e);
			}
		}
		return cm;
	}

	public void doApproveVOs(AggCostShareVO[] vos,UFDate buDate) throws BusinessException,
			DataValidateException, MetaDataException, DAOException {
		// 加锁
		updatelockOperate(vos);
		// 版本校验
		BDVersionValidationUtil.validateVersion(vos);
		// vo校验
		CostShareVOChecker vochecker = new CostShareVOChecker();
		vochecker.checkApprove(vos,buDate);
		// 补充事前分摊拉单执行记录
		fillUpMapf(vos);
		// 审核前事件处理
		fireBeforeApproveEvent(vos);
		// 设置审核状态
		CostShareVO[] parentvos = new CostShareVO[vos.length];
		String[] pks = new String[vos.length];
		//子表集合
		List<CShareDetailVO> childrenVOS = new ArrayList<CShareDetailVO>();
		// 设置生效信息，审核人、审核日期请在参数中设置完整
		for (int i = 0; i < parentvos.length; i++) {
			CostShareVO parentvo = (CostShareVO) vos[i].getParentVO();
			parentvo.setEffectstate(IErmCostShareConst.CostShare_Bill_Effectstate_Y);
			parentvo.setBillstatus(BXStatusConst.DJZT_Sign);
			parentvo.setApprover(InvocationInfoProxy.getInstance()
					.getUserId());
			parentvo.setApprovedate(buDate);
			parentvos[i] = parentvo;
			pks[i] = parentvo.getPrimaryKey();

			//同步至表体数据
			CShareDetailVO[] childrenvo = (CShareDetailVO[]) vos[i].getChildrenVO();
			for (int j = 0; j < childrenvo.length; j++) {
				childrenvo[j].setBillstatus(parentvo.getBillstatus());
				childrenVOS.add(childrenvo[j]);
			}
		}
//		@SuppressWarnings("unchecked")
//		Collection<AggCostShareVO> oldvos = MDPersistenceService.lookupPersistenceQueryService().queryBillOfVOByPKs(AggCostShareVO.class, pks, false);
		// 更新主表保存
		getDAO().updateParentVOs(
				parentvos,
				new String[] { CostShareVO.PK_COSTSHARE, CostShareVO.APPROVER,
						CostShareVO.APPROVEDATE, CostShareVO.EFFECTSTATE,
						CostShareVO.BILLSTATUS });

		getDAO().updateChildrenVOs(childrenVOS.toArray(new CShareDetailVO[0]), new String[] {CostShareVO.BILLSTATUS });


		// 审核后事件处理
		fireAfterApproveEvent(vos);
		// 发送消息到会计平台，生成凭证
		sendMessageToFip(vos, FipMessageVO.MESSAGETYPE_ADD);
		qryCshareVo(vos, parentvos);

//		// 记录业务日志
//		ErmBusiLogUtils.insertSmartBusiLogs(vos, (AggCostShareVO[]) oldvos.toArray(new AggCostShareVO[0]), IErmCostShareConst.CS_MD_APPROVE_OPER);
	}


	public MessageVO[] unapproveVOs(AggCostShareVO[] vos)
			throws BusinessException {
		if (vos == null || vos.length == 0) {
			return null;
		}
		//记录日志，由于需要单个提示，所以不支持批量操作
		MessageVO[] cm = new MessageVO[vos.length];
		for (int i = 0; i < vos.length; i++) {
			cm[i] = new MessageVO(vos[i],ActionUtils.UNCONFIRM,true,"");
			try {
				doUnapproveVOs(new AggCostShareVO[] {vos[i]});
			} catch (Exception e) {
				ExceptionHandler.handleException(e);
			}
		}
		return cm;
	}




	public void doUnapproveVOs(AggCostShareVO[] vos) throws BusinessException,
			DataValidateException, ValidationException, MetaDataException,
			DAOException {
		// 加锁
		updatelockOperate(vos);
		// 版本校验
		BDVersionValidationUtil.validateVersion(vos);
		// vo校验
		CostShareVOChecker vochecker = new CostShareVOChecker();
		vochecker.checkunApprove(vos);
		// 补充事前分摊拉单执行记录
		fillUpMapf(vos);
		// 取消审核前事件处理
		fireBeforeUnApproveEvent(vos);
		// 设置审核状态
		CostShareVO[] parentvos = new CostShareVO[vos.length];
		String[] pks = new String[vos.length];
		//子表数据
		List<CShareDetailVO> childrenVOS = new ArrayList<CShareDetailVO>();
		// 设置单据状态、生效信息，清空审核人、审核日期
		for (int i = 0; i < parentvos.length; i++) {
			CostShareVO parentvo = (CostShareVO) vos[i].getParentVO();
			parentvos[i] = parentvo;
			parentvo.setEffectstate(IErmCostShareConst.CostShare_Bill_Effectstate_N);
			parentvo.setBillstatus(BXStatusConst.DJZT_Saved);
			parentvo.setApprover(null);
			parentvo.setApprovedate(null);
			pks[i] = parentvo.getPrimaryKey();
			//同步至表体数据
			CShareDetailVO[] childrenvo = (CShareDetailVO[]) vos[i].getChildrenVO();
			for (int j = 0; j < childrenvo.length; j++) {
				childrenvo[j].setBillstatus(parentvo.getBillstatus());
				childrenVOS.add(childrenvo[j]);
			}

		}
//		@SuppressWarnings("unchecked")
//		Collection<AggCostShareVO> oldvos = MDPersistenceService.lookupPersistenceQueryService().queryBillOfVOByPKs(AggCostShareVO.class, pks, false);
//		AggCostShareVO[] arraycsVO = oldvos.toArray(new AggCostShareVO[0]);
		// 更新保存
		getDAO().updateParentVOs(
				parentvos,
				new String[] { CostShareVO.PK_COSTSHARE, CostShareVO.APPROVER,
						CostShareVO.APPROVEDATE, CostShareVO.EFFECTSTATE,
						CostShareVO.BILLSTATUS });


		getDAO().updateChildrenVOs(childrenVOS.toArray(new CShareDetailVO[0]), new String[] {CostShareVO.BILLSTATUS });

		// 取消审核后事件处理
		fireAfterUnApproveEvent(vos);
		// 发送消息到会计平台，删除凭证
		sendMessageToFip(vos, FipMessageVO.MESSAGETYPE_DEL);
		// 返回
		qryCshareVo(vos, parentvos);
//		// 记录业务日志
//		ErmBusiLogUtils.insertSmartBusiLogs(vos, arraycsVO, IErmCostShareConst.CS_MD_UNAPPROVE_OPER);
	}
	/**
	 * 查询并设置最新数据
	 *
	 * @param vos
	 * @param parentvos
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	private void qryCshareVo(AggCostShareVO[] vos, CostShareVO[] parentvos)
	throws DAOException {
		String csTableName = ((CostShareVO) vos[0].getParentVO())
		.getTableName();
		StringBuffer sbf = new StringBuffer();
		sbf.append("select ").append(CostShareVO.PK_COSTSHARE).append(",")
		.append("TS").append(" from ").append(csTableName);
		try {
			sbf.append(" where ").append(
					SqlUtils.getInStr(CostShareVO.PK_COSTSHARE, parentvos,
							CostShareVO.PK_COSTSHARE));
		} catch (SQLException e) {
			Logger.error(e.getMessage(), e);
		}
		Map<String, String> tsMap = (Map<String, String>) new BaseDAO()
		.executeQuery(sbf.toString(), new CostShareMapProcessor(
				CostShareVO.PK_COSTSHARE, "TS"));
		for (int i = 0; i < parentvos.length; i++) {
			CostShareVO parentvo = (CostShareVO) vos[i].getParentVO();
			parentvo.setTs(new UFDateTime(tsMap.get(parentvo.getPrimaryKey())));
		}
	}

	/**
	 * 发送消息到会计平台
	 *
	 * @param vos
	 * @throws BusinessException
	 */
	private void sendMessageToFip(AggCostShareVO[] vos, int messageType)
			throws BusinessException {
		// 包装消息
		List<FipMessageVO> messageList = new ArrayList<FipMessageVO>();
		for (int i = 0; i < vos.length; i++) {
			AggCostShareVO aggvo = vos[i];
			CostShareVO vo = (CostShareVO) aggvo.getParentVO();

			FipRelationInfoVO reVO = new FipRelationInfoVO();
			reVO.setPk_group(vo.getPk_group());
			reVO.setPk_org(vo.getPk_org());
			reVO.setRelationID(vo.getPrimaryKey());

			reVO.setPk_system(BXConstans.ERM_PRODUCT_CODE_Lower);
			reVO.setBusidate(vo.getApprovedate());
			reVO.setPk_billtype(vo.getPk_tradetype());

			reVO.setPk_operator(vo.getBillmaker());

			reVO.setFreedef1(vo.getBillno());
			reVO.setFreedef2(vo.getZy());
			UFDouble total = vo.getYbje();
			total = total.setScale(Currency.getCurrDigit(vo.getBzbm()), UFDouble.ROUND_HALF_UP);
			reVO.setFreedef3(String.valueOf(total));

			FipMessageVO messageVO = new FipMessageVO();
			messageVO.setBillVO(aggvo);
			messageVO.setMessagetype(messageType);
			messageVO.setMessageinfo(reVO);
			messageList.add(messageVO);
		}
		// 发送到会计平台
		NCLocator.getInstance().lookup(IFipMessageService.class)
				.sendMessages(messageList.toArray(new FipMessageVO[0]));
	}

	/**
	 * 获得单据号
	 *
	 * @param aggvo
	 * @throws BusinessException
	 */
	private void createBillNo(AggCostShareVO aggvo) throws BusinessException {
		CostShareVO parentvo = (CostShareVO) aggvo.getParentVO();
		parentvo.setDjdl(IErmCostShareConst.COSTSHARE_DJDL);
		FinanceBillCodeInfo info = new FinanceBillCodeInfo(
				CostShareVO.DJDL, CostShareVO.BILLNO,
				CostShareVO.PK_GROUP, CostShareVO.PK_ORG,
				parentvo.getTableName());
		FinanceBillCodeUtils util = new FinanceBillCodeUtils(info);
		util.createBillCode(new AggregatedValueObject[] { aggvo });
	}

	/**
	 * 退还单据号
	 *
	 * @param vos
	 * @throws BusinessException
	 */
	private void returnBillno(AggCostShareVO[] vos) throws BusinessException {
		CostShareVO parentvo = (CostShareVO) vos[0].getParentVO();
		parentvo.setDjdl(IErmCostShareConst.COSTSHARE_DJDL);
		FinanceBillCodeInfo info = new FinanceBillCodeInfo(
				CostShareVO.DJDL, CostShareVO.BILLNO,
				CostShareVO.PK_GROUP, CostShareVO.PK_ORG,
				parentvo.getTableName());
		FinanceBillCodeUtils util = new FinanceBillCodeUtils(info);
		util.returnBillCode(vos);
	}

	public static final String lockmessage = "ERM_costshare";

	/**
	 * 新增加锁
	 *
	 * @param vo
	 * @throws BusinessException
	 */
	private void insertlockOperate(AggCostShareVO vo) throws BusinessException {
		// 业务锁
		ErLockUtil.lockVO(new String[] { CostShareVO.SRC_ID,
				CostShareVO.PK_ORG }, lockmessage, vo.getParentVO());
	}

	/**
	 * 更新加锁
	 *
	 * @param vo
	 * @throws BusinessException
	 */
	private void updatelockOperate(AggCostShareVO... vos)
			throws BusinessException {
		// 业务锁
		ErLockUtil.lockAggVO(new String[] { CostShareVO.SRC_ID,
				CostShareVO.PK_ORG }, lockmessage, vos);
		// 主键锁
		ErLockUtil.lockAggVOByPk(lockmessage, vos);

	}

	/**
	 * 删除加锁
	 *
	 * @param vos
	 * @throws BusinessException
	 */
	private void deletelockOperate(AggCostShareVO[] vos)
			throws BusinessException {
		// 主键锁
		ErLockUtil.lockAggVOByPk(lockmessage, vos);
	}

	/**
	 * 费用结转设置默认值
	 *
	 * @param vos
	 */
	private void setDefaultValue(AggCostShareVO vo, boolean isInsert) {
		CostShareVO csvo = ((CostShareVO) vo.getParentVO());
		csvo.setEffectstate(IErmCostShareConst.CostShare_Bill_Effectstate_N);
		// 汇率折算
		if (vo.getChildrenVO() != null) {
			CShareDetailVO[] cr = (CShareDetailVO[]) vo.getChildrenVO();
			for (int i = 0; i < cr.length; i++) {
				cr[i].setBillstatus(csvo.getBillstatus());
				cr[i].setBzbm(csvo.getBzbm());
				cr[i].setSrc_type(csvo.getSrc_type());
				cr[i].setSrc_id(csvo.getSrc_id());
				cr[i].setBillno(csvo.getBillno());
				cr[i].setPk_billtype(csvo.getPk_billtype());
				cr[i].setPk_org(csvo.getPk_org());
				cr[i].setPk_group(csvo.getPk_group());
				cr[i].setPk_jkbx(csvo.getSrc_id());
				cr[i].setPk_tradetype(csvo.getPk_tradetype());
				//setRateAmount(csvo, cr, i);
			}
		}
		
	}
//	/**
//	 * 有前台来计算出来，不再后台处理了。
//	 * @param csvo
//	 * @param cr
//	 * @param i
//	 */
//	private void setRateAmount(CostShareVO csvo, CShareDetailVO[] cr, int i) {
//		// 计算表体本币金额
//		UFDouble hl = UFDouble.ZERO_DBL;
//		UFDouble grouphl = UFDouble.ZERO_DBL;
//		UFDouble globalhl = UFDouble.ZERO_DBL;
//		try {
//			String localCurry = Currency.getLocalCurrPK(cr[i].getAssume_org());
//			UFDouble[] rates = ErmBillCalUtil.getRate(csvo.getBzbm(), cr[i].getAssume_org(),
//					csvo.getPk_group(), csvo.getBilldate(), localCurry);
//			 hl = rates[0];
//			 grouphl = rates[1];
//			 globalhl = rates[2];
//			
////			ratevalue = Currency.getRate(cr[i].getAssume_org(), cr[i]
////					.getBzbm(), localCurry, csvo.getBilldate());
//		} catch (BusinessException e) {
//			Logger.error(e.getMessage(), e);
//		}
//		cr[i].setBbhl(hl);
//		// 本币金额=原币*汇率
//		cr[i].setBbje(cr[i].getAssume_amount().multiply(
//				hl, UFDouble.ROUND_HALF_UP));
//		
//		cr[i].setGroupbbhl(grouphl);
//		// 集团金额=原币*汇率
//		cr[i].setGroupbbje(cr[i].getAssume_amount().multiply(
//				grouphl, UFDouble.ROUND_HALF_UP));
//		
//		cr[i].setGlobalbbhl(globalhl);
//		// 全局金额=原币*汇率
//		cr[i].setGlobalbbje(cr[i].getAssume_amount().multiply(
//				globalhl, UFDouble.ROUND_HALF_UP));
//	}
	
//	private void setGroupRateAmount(CostShareVO csvo, CShareDetailVO[] cr, int i) {
//		// 计算表体集团金额
//		UFDouble ratevalue = UFDouble.ZERO_DBL;
//		try {
//			String groupCurry = Currency.getGroupCurrpk(csvo
//					.getPk_org());
//			ratevalue = Currency.getRate(cr[i].getAssume_org(), cr[i]
//			                                                       .getBzbm(), groupCurry, csvo.getBilldate());
//		} catch (BusinessException e) {
//			Logger.error(e.getMessage(), e);
//		}
//		cr[i].setGroupbbhl(ratevalue);
//		// 集团金额=原币*汇率
//		cr[i].setGroupbbje(cr[i].getAssume_amount().multiply(
//				ratevalue, UFDouble.ROUND_HALF_UP));
//	}
//
//	private void setGloalRateAmount(CostShareVO csvo, CShareDetailVO[] cr, int i) {
//		// 计算表体全局金额
//		UFDouble ratevalue = UFDouble.ZERO_DBL;
//		try {
//			String localCurry = Currency.getGlobalCurrPk(csvo
//					.getPk_org());
//			ratevalue = Currency.getRate(cr[i].getAssume_org(), cr[i]
//			                                                       .getBzbm(), localCurry, csvo.getBilldate());
//		} catch (BusinessException e) {
//			Logger.error(e.getMessage(), e);
//		}
//
//		cr[i].setGlobalbbhl(ratevalue);
//		// 全局金额=原币*汇率
//		cr[i].setGlobalbbje(cr[i].getAssume_amount().multiply(
//				ratevalue, UFDouble.ROUND_HALF_UP));
//	}


	protected void fireBeforeInsertEvent(AggCostShareVO... vos)
			throws BusinessException {
		EventDispatcher.fireEvent(new ErmBusinessEvent(
				IErmCostShareConst.COSTSHARE_MDID,
				ErmEventType.TYPE_INSERT_BEFORE, vos));
	}

	protected void fireAfterInsertEvent(AggCostShareVO... vos)
			throws BusinessException {
		EventDispatcher.fireEvent(new ErmBusinessEvent(
				IErmCostShareConst.COSTSHARE_MDID,
				ErmEventType.TYPE_INSERT_AFTER, vos));
	}

	protected void fireBeforeTempInsertEvent(AggCostShareVO... vos)
	throws BusinessException {
		EventDispatcher.fireEvent(new ErmBusinessEvent(
				IErmCostShareConst.COSTSHARE_MDID,
				ErmEventType.TYPE_TEMPSAVE_BEFORE, vos));
	}

	protected void fireAfterTempInsertEvent(AggCostShareVO... vos)
	throws BusinessException {
		EventDispatcher.fireEvent(new ErmBusinessEvent(
				IErmCostShareConst.COSTSHARE_MDID,
				ErmEventType.TYPE_TEMPSAVE_AFTER, vos));
	}

	protected void fireBeforeUpdateEvent(AggCostShareVO vo,AggCostShareVO oldvo)
			throws BusinessException {
		EventDispatcher.fireEvent(new ErmBusinessEvent(
				IErmCostShareConst.COSTSHARE_MDID,
				ErmEventType.TYPE_UPDATE_BEFORE,new AggCostShareVO[]{vo},new AggCostShareVO[]{oldvo}));
	}

	protected void fireAfterUpdateEvent(AggCostShareVO vo,AggCostShareVO oldvo)
			throws BusinessException {
		EventDispatcher.fireEvent(new ErmBusinessEvent(
				IErmCostShareConst.COSTSHARE_MDID,
				ErmEventType.TYPE_UPDATE_AFTER, new AggCostShareVO[]{vo},new AggCostShareVO[]{oldvo}));
	}

	protected void fireBeforeDeleteEvent(AggCostShareVO... vos)
			throws BusinessException {
		EventDispatcher.fireEvent(new ErmBusinessEvent(
				IErmCostShareConst.COSTSHARE_MDID,
				ErmEventType.TYPE_DELETE_BEFORE, vos));
	}

	protected void fireAfterDeleteEvent(AggCostShareVO... vos)
			throws BusinessException {
		EventDispatcher.fireEvent(new ErmBusinessEvent(
				IErmCostShareConst.COSTSHARE_MDID,
				ErmEventType.TYPE_DELETE_AFTER, vos));
	}

	protected void fireBeforeApproveEvent(AggCostShareVO... vos)
			throws BusinessException {
		EventDispatcher.fireEvent(new ErmBusinessEvent(
				IErmCostShareConst.COSTSHARE_MDID,
				ErmEventType.TYPE_APPROVE_BEFORE, vos));
	}

	protected void fireAfterApproveEvent(AggCostShareVO... vos)
			throws BusinessException {
		EventDispatcher.fireEvent(new ErmBusinessEvent(
				IErmCostShareConst.COSTSHARE_MDID,
				ErmEventType.TYPE_APPROVE_AFTER, vos));
	}

	protected void fireBeforeUnApproveEvent(AggCostShareVO... vos)
			throws BusinessException {
		EventDispatcher.fireEvent(new ErmBusinessEvent(
				IErmCostShareConst.COSTSHARE_MDID,
				ErmEventType.TYPE_UNAPPROVE_BEFORE, vos));
	}

	protected void fireAfterUnApproveEvent(AggCostShareVO... vos)
			throws BusinessException {
		EventDispatcher.fireEvent(new ErmBusinessEvent(
				IErmCostShareConst.COSTSHARE_MDID,
				ErmEventType.TYPE_UNAPPROVE_AFTER, vos));
	}

	private void deleteValidate(AggCostShareVO[] vos) {
		List<CostShareVO> headlist = new ArrayList<CostShareVO>();
		List<CShareDetailVO> detaillist = new ArrayList<CShareDetailVO>();
		for (AggCostShareVO aggvo : vos) {
			headlist.add((CostShareVO) aggvo.getParentVO());
			CircularlyAccessibleValueObject[] childrenVO = aggvo.getChildrenVO();
			if(childrenVO != null && childrenVO.length > 0){
				for (int j = 0; j < childrenVO.length; j++) {
					detaillist.add((CShareDetailVO) childrenVO[j]);
				}
			}
		}
		BDReferenceChecker.getInstance().validate(headlist.toArray(new CostShareVO[headlist.size()]));
		if(!detaillist.isEmpty()){
			BDReferenceChecker.getInstance().validate(detaillist.toArray(new CShareDetailVO[detaillist.size()]));
		}
	}

}