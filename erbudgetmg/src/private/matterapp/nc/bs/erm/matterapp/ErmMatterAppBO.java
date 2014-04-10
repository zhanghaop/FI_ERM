package nc.bs.erm.matterapp;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import nc.bs.arap.bx.BusiLogUtil;
import nc.bs.businessevent.EventDispatcher;
import nc.bs.er.util.BXBsUtil;
import nc.bs.er.util.SqlUtil;
import nc.bs.erm.event.ErmBusinessEvent;
import nc.bs.erm.event.ErmEventType;
import nc.bs.erm.matterapp.check.MatterAppVOChecker;
import nc.bs.erm.matterapp.common.ErmMatterAppConst;
import nc.bs.erm.matterapp.common.MatterAppUtils;
import nc.bs.erm.util.ErLockUtil;
import nc.bs.erm.util.ErmDjlxCache;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.pub.pf.CheckStatusCallbackContext;
import nc.bs.pub.pf.ICheckStatusCallback;
import nc.itf.fi.pub.Currency;
import nc.md.persist.framework.MDPersistenceService;
import nc.pubitf.erm.matterapp.IErmMatterAppBillQuery;
import nc.vo.arap.bx.util.ActionUtils;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.erm.common.MessageVO;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.erm.matterapp.MtAppDetailVO;
import nc.vo.erm.util.VOUtils;
import nc.vo.fipub.billcode.FinanceBillCodeInfo;
import nc.vo.fipub.billcode.FinanceBillCodeUtils;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.trade.pub.IBillStatus;
import nc.vo.uap.rbac.constant.INCSystemUserConst;
import nc.vo.util.AuditInfoUtil;
import nc.vo.util.BDVersionValidationUtil;

import org.apache.commons.lang.ArrayUtils;

/**
 * 事项审批单业务类
 * 
 * @author lvhj
 * 
 */
public class ErmMatterAppBO implements ICheckStatusCallback{

	private ErmMatterAppDAO dao;

	private ErmMatterAppDAO getDAO() {
		if (dao == null) {
			dao = new ErmMatterAppDAO();
		}
		return dao;
	}

	public AggMatterAppVO insertVO(AggMatterAppVO vo) throws BusinessException {
		AggMatterAppVO result = null;
		prepareVoValue(vo);
		// vo校验
		MatterAppVOChecker vochecker = new MatterAppVOChecker();
		vochecker.checkBackSave(vo);

		try {
			// 获取单据号
			createBillNo(vo);
			// 设置审计信息
			AuditInfoUtil.addData(vo.getParentVO());
			// 新增前事件处理
			fireBeforeInsertEvent(vo);
			// 新增保存
			result = getDAO().insertVO(vo);
			// 新增后事件处理
			fireAfterInsertEvent(vo);
			
			// 查询返回结果
			IErmMatterAppBillQuery qryService = NCLocator.getInstance().lookup(IErmMatterAppBillQuery.class);
			result = qryService.queryBillByPK(vo.getParentVO().getPrimaryKey());
			
		} catch (Exception e) {
			returnBillno(new AggMatterAppVO[] { vo });
			ExceptionHandler.handleException(e);
		}
		
		result.getParentVO().setHasntbcheck(UFBoolean.FALSE);
		// 返回
		return result;
	}

	public AggMatterAppVO updateVO(AggMatterAppVO vo) throws BusinessException {
		// 修改加锁
		pklockOperate(vo);
		// 版本校验
		BDVersionValidationUtil.validateVersion(vo.getParentVO());

		// 查询修改前的vo
		IErmMatterAppBillQuery qryService = NCLocator.getInstance().lookup(IErmMatterAppBillQuery.class);
		AggMatterAppVO oldvo = qryService.queryBillByPK(vo.getParentVO().getPrimaryKey());
		

		// 补齐children信息（因前台传过来的的只是改变的children）
		fillUpChildren(vo, oldvo);

		// 设置审计信息
		AuditInfoUtil.updateData(vo.getParentVO());
		// 获取单据号
		createBillNo(vo);
		//补齐数据
		prepareVoValue(vo);
		// vo校验
		MatterAppVOChecker vochecker = new MatterAppVOChecker();
		vochecker.checkBackSave(vo);
		// 修改前事件处理
		fireBeforeUpdateEvent(new AggMatterAppVO[] { vo }, new AggMatterAppVO[] { oldvo });
		// 更新保存
		vo = getDAO().updateVO(vo);
		// 修改后事件处理
		fireAfterUpdateEvent(new AggMatterAppVO[] { vo }, new AggMatterAppVO[] { oldvo });
		// 查询返回结果
		vo = qryService.queryBillByPK(vo.getParentVO().getPrimaryKey());
		// 记录业务日志
		BusiLogUtil.insertSmartBusiLog(ErmMatterAppConst.MAPP_MD_UPDATE_OPER, new AggMatterAppVO[] { vo },
				new AggMatterAppVO[] { oldvo });
		
		vo.getParentVO().setHasntbcheck(UFBoolean.FALSE);
		// 返回
		return vo;
	}

	/**
	 * 补充children
	 * 
	 * @param vo
	 * @param oldvo
	 */
	private void fillUpChildren(AggMatterAppVO vo, AggMatterAppVO oldvo) {
		List<MtAppDetailVO> result = new ArrayList<MtAppDetailVO>();

		List<String> pkList = new ArrayList<String>();

		MtAppDetailVO[] changedChildren = vo.getChildrenVO();
		MtAppDetailVO[] oldChildren = oldvo.getChildrenVO();
		
		if(changedChildren != null){
			for (int i = 0; i < changedChildren.length; i++) {
				if (changedChildren[i].getStatus() != VOStatus.NEW) {
					pkList.add(changedChildren[i].getPk_mtapp_detail());
				}
				result.add(changedChildren[i]);
			}
		}
		
		if(oldChildren != null){
			for (int i = 0; i < oldChildren.length; i++) {
				if (!pkList.contains(oldChildren[i].getPk_mtapp_detail())) {
					oldChildren[i].setStatus(VOStatus.UPDATED);
					result.add(oldChildren[i]);
				}
			}
		}
		
		Collections.sort(result, new Comparator<MtAppDetailVO>() {
			@Override
			public int compare(MtAppDetailVO item1, MtAppDetailVO item2) {
				if (item1.getRowno() == null && item2.getRowno() == null) {
					return 0;
				} else if (item1.getRowno() != null && item2.getRowno() == null) {
					return -1;
				} else if (item1.getRowno() == null && item2.getRowno() != null) {
					return 1;
				}
				return item1.getRowno().compareTo(item2.getRowno());
			}
		});

		vo.setChildrenVO(result.toArray(new MtAppDetailVO[] {}));
	}

	public void deleteVOs(AggMatterAppVO[] vos) throws BusinessException {
		if (vos == null || vos.length == 0) {
			return;
		}

		retriveItems(vos);
		// 删除加锁
		pklockOperate(vos);
		// 版本校验
		BDVersionValidationUtil.validateVersion(vos);
		// 删除引用校验
		deleteValidate(vos);
		// vo校验
		MatterAppVOChecker vochecker = new MatterAppVOChecker();
		vochecker.checkDelete(vos);
		// 删除前事件处理
		fireBeforeDeleteEvent(vos);
		// 删除单据
		getDAO().deleteVOs(vos);
		// 修改后事件处理
		fireAfterDeleteEvent(vos);
		// 退还单据号
		returnBillno(vos);
		// 记录业务日志
		for (AggMatterAppVO aggMatterAppVO : vos) {
			aggMatterAppVO.getParentVO().setStatus(VOStatus.DELETED);
			MtAppDetailVO[] childrenVO = aggMatterAppVO.getChildrenVO();
			if (!ArrayUtils.isEmpty(childrenVO)) {
				for (MtAppDetailVO mtAppDetailVO : childrenVO) {
					mtAppDetailVO.setStatus(VOStatus.DELETED);
				}
			}
		}
		BusiLogUtil.insertSmartBusiLog(ErmMatterAppConst.MAPP_MD_DELETE_OPER, vos, null);

	}

	public AggMatterAppVO[] commitVOs(AggMatterAppVO[] vos) throws BusinessException {
		if (vos == null || vos.length == 0) {
			return null;
		}

		retriveItems(vos);
		// 加锁
		pklockOperate(vos);
		// 版本校验
		BDVersionValidationUtil.validateVersion(vos);
		// vo校验
		MatterAppVOChecker vochecker = new MatterAppVOChecker();
		vochecker.checkCommit(vos);

		// 设置提交相关状态
		setAggMatterAppVOAttribute(vos, MatterAppVO.BILLSTATUS, Integer.valueOf(ErmMatterAppConst.BILLSTATUS_COMMITED),
				true);
		setAggMatterAppVOAttribute(vos, MatterAppVO.APPRSTATUS, Integer.valueOf(IBillStatus.COMMIT), false);

		// 更新保存
		getDAO().updateAggVOsByFields(vos, new String[] { MatterAppVO.BILLSTATUS, MatterAppVO.APPRSTATUS },
				new String[] { MtAppDetailVO.BILLSTATUS });

		// 返回
		return vos;
	}

	public AggMatterAppVO[] recallVOs(AggMatterAppVO[] vos) throws BusinessException {
		if (vos == null || vos.length == 0) {
			return null;
		}

		retriveItems(vos);
		// 加锁
		pklockOperate(vos);
		// 版本校验
		BDVersionValidationUtil.validateVersion(vos);
		// vo校验
		MatterAppVOChecker vochecker = new MatterAppVOChecker();
		vochecker.checkRecall(vos);

		// 设置收回相关信息
		setAggMatterAppVOAttribute(vos, MatterAppVO.BILLSTATUS, Integer.valueOf(ErmMatterAppConst.BILLSTATUS_SAVED),
				true);

		// 更新保存
		getDAO().updateAggVOsByFields(vos, new String[] { MatterAppVO.BILLSTATUS, MatterAppVO.APPRSTATUS },
				new String[] { MtAppDetailVO.BILLSTATUS });
		return vos;
	}

	public MessageVO[] approveVOs(AggMatterAppVO[] vos) throws BusinessException {
		if (vos == null || vos.length == 0) {
			return null;
		}

		retriveItems(vos);

		MessageVO[] msgs = new MessageVO[vos.length];

		// 加锁
		pklockOperate(vos);
		// 版本校验
		BDVersionValidationUtil.validateVersion(vos);
		// vo校验
		MatterAppVOChecker vochecker = new MatterAppVOChecker();
		vochecker.checkApprove(vos);
		for (int i = 0; i < vos.length; i++) {
			approveBack(vos[i]);
			msgs[i] = new MessageVO(vos[i], ActionUtils.AUDIT);
		}

		// 返回
		return msgs;
	}

	private void approveBack(AggMatterAppVO vo) throws BusinessException {
		// 审核前事件处理
		fireBeforeApproveEvent(vo);

		// 设置生效信息，单据状态
		setAggMatterAppVOAttribute(new AggMatterAppVO[] { vo }, MatterAppVO.EFFECTSTATUS,
				ErmMatterAppConst.EFFECTSTATUS_VALID, true);

		setAggMatterAppVOAttribute(new AggMatterAppVO[] { vo }, MatterAppVO.BILLSTATUS,
				ErmMatterAppConst.BILLSTATUS_APPROVED, true);
		
		setAggMatterAppVOAttribute(new AggMatterAppVO[] { vo }, MatterAppVO.APPROVETIME, vo.getParentVO()
				.getApprovetime(), true);//审批日期

		// 更新保存
		getDAO().updateAggVOsByFields(
				new AggMatterAppVO[] {vo},
				new String[] { MatterAppVO.BILLSTATUS, MatterAppVO.APPRSTATUS, MatterAppVO.EFFECTSTATUS,
						MatterAppVO.APPROVER, MatterAppVO.APPROVETIME },
				new String[] { MtAppDetailVO.BILLSTATUS, MtAppDetailVO.EFFECTSTATUS });

		// 审核后事件处理
		fireAfterApproveEvent(vo);
		//恢复预算控制标记
		vo.getParentVO().setHasntbcheck(UFBoolean.FALSE);
	}

	public MessageVO[] unapproveVOs(AggMatterAppVO[] vos) throws BusinessException {
		if (vos == null || vos.length == 0) {
			return null;
		}

		retriveItems(vos);

		MessageVO[] msgs = new MessageVO[vos.length];
		// 加锁
		pklockOperate(vos);
		// 版本校验
		BDVersionValidationUtil.validateVersion(vos);
		// vo校验
		MatterAppVOChecker vochecker = new MatterAppVOChecker();
		vochecker.checkunApprove(vos);

		try {
			for (int i = 0; i < vos.length; i++) {
				unApproveBack(vos[i]);
				msgs[i] = new MessageVO(vos[i], ActionUtils.UNAUDIT);
			}
		} catch (Exception e) {
			throw ExceptionHandler.handleException(e);
		}

		return msgs;
	}

	private void unApproveBack(AggMatterAppVO vo) throws BusinessException {
//		// 是否是取消生效动作
//		MatterAppVO parentvo = (MatterAppVO) vo.getParentVO();
//
//		parentvo.setApprovetime(oldvo.getParentVO().getApprovetime());//用于预算控制为审核日期的情况，审核日期不能丢
		// 取消审核前事件处理
		fireBeforeUnApproveEvent(vo.getOldvo());

		// 设置单据状态、生效信息
		setAggMatterAppVOAttribute(new AggMatterAppVO[] { vo }, MatterAppVO.EFFECTSTATUS,
				ErmMatterAppConst.EFFECTSTATUS_NO, true);

		setAggMatterAppVOAttribute(new AggMatterAppVO[] { vo }, MatterAppVO.BILLSTATUS,
				ErmMatterAppConst.BILLSTATUS_COMMITED, true);

		// 清空审核人、审核日期
//		parentvo.setApprover(null);
//		parentvo.setApprovetime(null);

		// 更新保存
		getDAO().updateAggVOsByFields(
				new AggMatterAppVO[] { vo },
				new String[] { MatterAppVO.APPRSTATUS, MatterAppVO.EFFECTSTATUS, MatterAppVO.BILLSTATUS,
						MatterAppVO.APPROVER, MatterAppVO.APPROVETIME },
				new String[] { MtAppDetailVO.BILLSTATUS, MtAppDetailVO.EFFECTSTATUS });
		
		// 取消审核后事件处理
		fireAfterUnApproveEvent(vo);
		
		//恢复预算控制标记
		vo.getParentVO().setHasntbcheck(UFBoolean.FALSE);
	}
	
	/**
	 * 关闭操作
	 * @param vos
	 * @param isAuto 是否自动关闭
	 * @return
	 * @throws BusinessException
	 */
	public AggMatterAppVO[] closeVOs(AggMatterAppVO[] vos, boolean isAuto) throws BusinessException {
		if (vos == null || vos.length == 0) {
			return null;
		}

		retriveItems(vos);
		
		if(!isAuto){
			filterAutoCloseLine(vos);
		}
		
		// 加锁
		pklockOperate(vos);
		// 版本校验
		BDVersionValidationUtil.validateVersion(vos);

		// vo校验
		MatterAppVOChecker vochecker = new MatterAppVOChecker();
		vochecker.checkClose(vos);

		// 关闭前事件处理
		fireBeforeCloseEvent(isAuto, vos);

		// 设置表头关闭信息
		UFDate closeDate = new UFDate(InvocationInfoProxy.getInstance().getBizDateTime());
		String closeman = isAuto ? INCSystemUserConst.NC_USER_PK : AuditInfoUtil.getCurrentUser();
		
		// 设置子表关闭信息
		for (int i = 0; i < vos.length; i++) {
			// 如果是单选，并且子表个数与old个数不同，其它情况都认为是整单取消关闭
			AggMatterAppVO aggMatterAppVO = vos[i];
			boolean isAllClose = isAllClose(aggMatterAppVO);
			
			if (isAllClose) {
				aggMatterAppVO.getParentVO().setAttributeValue(MatterAppVO.CLOSE_STATUS, ErmMatterAppConst.CLOSESTATUS_Y);
				aggMatterAppVO.getParentVO().setAttributeValue(MatterAppVO.CLOSEMAN, closeman);
				aggMatterAppVO.getParentVO().setAttributeValue(MatterAppVO.CLOSEDATE, closeDate);
			}
			
			for (int j = 0; j < aggMatterAppVO.getChildrenVO().length; j++) {
				if (!(ErmMatterAppConst.CLOSESTATUS_Y == aggMatterAppVO.getChildrenVO()[j].getClose_status())) {
					aggMatterAppVO.getChildrenVO()[j].setAttributeValue(MtAppDetailVO.CLOSE_STATUS,
							ErmMatterAppConst.CLOSESTATUS_Y);
					aggMatterAppVO.getChildrenVO()[j].setAttributeValue(MtAppDetailVO.CLOSEMAN, closeman);
					aggMatterAppVO.getChildrenVO()[j].setAttributeValue(MtAppDetailVO.CLOSEDATE, closeDate);
				}
			}
		}

		// 更新保存
		getDAO().updateAggVOsByFields(vos,
				new String[] { MatterAppVO.CLOSE_STATUS, MatterAppVO.CLOSEMAN, MatterAppVO.CLOSEDATE },
				new String[] { MtAppDetailVO.CLOSE_STATUS, MtAppDetailVO.CLOSEMAN, MtAppDetailVO.CLOSEDATE });
		// 关闭后事件处理
		fireAfterCloseEvent(isAuto, vos);

		// 行关闭造成前台传来的数据不全，需补齐全部表体数据
		// 返回
		return queryOldVOsByVOs(vos);
	}

	private void filterAutoCloseLine(AggMatterAppVO[] vos) {
		if (vos != null) {
			for (AggMatterAppVO aggVo : vos) {
				if (aggVo.getChildrenVO() != null) {
					List<MtAppDetailVO> childrenVos = new ArrayList<MtAppDetailVO>();

					for (MtAppDetailVO detail : aggVo.getChildrenVO()) {
						if (!INCSystemUserConst.NC_USER_PK.equals(detail.getCloseman())) {
							childrenVos.add(detail);
						}
					}
					aggVo.setChildrenVO(childrenVos.toArray(new MtAppDetailVO[] {}));
				}
			}
		}
	}

	public AggMatterAppVO tempSave(AggMatterAppVO vo) throws BusinessException {
		// 修改加锁
		pklockOperate(vo);
		
		// 查询修改前的vo
		IErmMatterAppBillQuery qryService = NCLocator.getInstance().lookup(IErmMatterAppBillQuery.class);
		
		AggMatterAppVO oldvo = null;
		if (vo.getParentVO().getStatus() != VOStatus.NEW) {
			oldvo = qryService.queryBillByPK(vo.getParentVO().getPrimaryKey());
			// 补齐children信息（因前台传过来的的只是改变的children）
			if (oldvo != null) {
				fillUpChildren(vo, oldvo);
			}
		}
		
		// 获取单据号
		createBillNo(vo);
		
		prepareVoValue(vo);
		
		// 设置暂存状态
		setAggMatterAppVOAttribute(new AggMatterAppVO[] { vo }, MatterAppVO.BILLSTATUS,
				ErmMatterAppConst.BILLSTATUS_TEMPSAVED, true);
		
		// 设置审计信息
		if (vo.getParentVO().getPk_mtapp_bill() != null) {
			AuditInfoUtil.updateData(vo.getParentVO());
		} else {
			AuditInfoUtil.addData(vo.getParentVO());
		}
		// 更新保存
		vo = getDAO().updateVO(vo);
		
		// 新增后事件处理
		if (oldvo == null) {
			fireAfterTempSaveEvent(new AggMatterAppVO[] { vo }, null);
		} else {
			fireAfterTempSaveEvent(new AggMatterAppVO[] { vo }, new AggMatterAppVO[] { oldvo });
		}
		// 返回
		return vo;
	}
	
	/**
	 * 暂存后事件监听类:分新增暂存和修改暂存
	 * @param vos
	 * @throws BusinessException
	 */
	private void fireAfterTempSaveEvent(AggMatterAppVO[] vos, AggMatterAppVO[] oldvos) throws BusinessException {
		if(oldvos == null){
			EventDispatcher.fireEvent(new ErmBusinessEvent(ErmMatterAppConst.MatterApp_MDID,
					ErmEventType.TYPE_TEMPSAVE_AFTER, vos));
		}else{
			EventDispatcher.fireEvent(new ErmBusinessEvent(ErmMatterAppConst.MatterApp_MDID,
					ErmEventType.TYPE_TEMPUPDATE_AFTER, vos,oldvos));
		}
	}

	public MatterAppVO updatePrintInfo(MatterAppVO vo) throws BusinessException {
		// 加锁
		pklockVO(vo);
		// 版本校验
		BDVersionValidationUtil.validateVersion(vo);

		// 更新打印信息
		getDAO().updateVOsByFields(new MatterAppVO[] { vo },
				new String[] { MatterAppVO.PRINTER, MatterAppVO.PRINTDATE });

		return vo;
	}

	public AggMatterAppVO[] openVOs(AggMatterAppVO[] vos, boolean isAuto) throws BusinessException {
		if (vos == null || vos.length == 0) {
			return null;
		}
		
		retriveItems(vos);
		
		if(!isAuto){
			filterAutoCloseLine(vos);
		}
		
		// 加锁
		pklockOperate(vos);
		// 版本校验
		BDVersionValidationUtil.validateVersion(vos);

		// vo校验
		MatterAppVOChecker vochecker = new MatterAppVOChecker();
		vochecker.checkunClose(vos);

		// 取消关闭前事件处理
		fireBeforeUnCloseEvent(isAuto, vos);

		// 设置子表关闭信息
		for (int i = 0; i < vos.length; i++) {
			boolean openflag = false;
			
			List<String> effectDetails = getEffectOpenDetails(vos[i]);
			if(effectDetails.isEmpty()){
				continue;
			}
			
			for (int j = 0; j < vos[i].getChildrenVO().length; j++) {
				MtAppDetailVO mtAppDetailVO = vos[i].getChildrenVO()[j];
				if (effectDetails.contains(mtAppDetailVO.getPk_mtapp_detail())) {//过滤自动关闭行
					mtAppDetailVO.setAttributeValue(MtAppDetailVO.CLOSE_STATUS, ErmMatterAppConst.CLOSESTATUS_N);
					mtAppDetailVO.setAttributeValue(MtAppDetailVO.CLOSEMAN, null);
					mtAppDetailVO.setAttributeValue(MtAppDetailVO.CLOSEDATE, null);
					openflag = true;
				}
			}
			
			if(openflag){
				// 设置表头开启信息
				vos[i].getParentVO().setAttributeValue(MatterAppVO.CLOSE_STATUS, ErmMatterAppConst.CLOSESTATUS_N);
				vos[i].getParentVO().setAttributeValue(MatterAppVO.CLOSEMAN, null);
				vos[i].getParentVO().setAttributeValue(MatterAppVO.CLOSEDATE, null);
			}
		}

		// 更新保存
		getDAO().updateAggVOsByFields(vos,
				new String[] { MatterAppVO.CLOSE_STATUS, MatterAppVO.CLOSEMAN, MatterAppVO.CLOSEDATE },
				new String[] { MtAppDetailVO.CLOSE_STATUS, MtAppDetailVO.CLOSEMAN, MtAppDetailVO.CLOSEDATE });
		// 取消关闭后事件处理
		fireAfterUnCloseEvent(isAuto, vos);

		return queryOldVOsByVOs(vos);
	}

	@SuppressWarnings("unchecked")
	private List<String> getEffectOpenDetails(AggMatterAppVO vo) throws BusinessException {
		List<String> result = new ArrayList<String>();
		if(vo.getChildrenVO() == null || vo.getChildrenVO().length == 0){
			return result;
		}

		StringBuffer sql = new StringBuffer();
		sql.append(" orig_amount > isnull(( SELECT sum(p.exe_amount) FROM er_mtapp_billpf p ");
		sql
				.append(" WHERE p.pk_mtapp_detail=er_mtapp_detail.pk_mtapp_detail and p.pk_djdl='bx' GROUP BY p.pk_mtapp_detail ),0 ) ");

		String[] pk_maDetails = VOUtils.getAttributeValues(vo.getChildrenVO(), MtAppDetailVO.PK_MTAPP_DETAIL);
		sql.append(" and " + SqlUtil.buildInSql("er_mtapp_detail.pk_mtapp_detail", pk_maDetails));

		Collection<MtAppDetailVO> detailList = MDPersistenceService.lookupPersistenceQueryService()
				.queryBillOfVOByCond(MtAppDetailVO.class, sql.toString(), false);

		if (detailList != null && !detailList.isEmpty()) {
			pk_maDetails = VOUtils.getAttributeValues(detailList.toArray(new MtAppDetailVO[0]),
					MtAppDetailVO.PK_MTAPP_DETAIL);

			for (String pk_maDetail : pk_maDetails) {
				result.add(pk_maDetail);
			}
		}
		return result;
	}

	/**
	 * 判断是否为整单关闭操作
	 * 
	 * @param vos
	 * @param oldvos
	 * @return
	 * @throws BusinessException
	 */
	private boolean isAllClose(AggMatterAppVO vo) throws BusinessException {
		// 列表页面只有整单关闭
		MtAppDetailVO[] newchilds = vo.getChildrenVO();
		List<String> newchildpks = new ArrayList<String>();

		if (newchilds == null || newchilds.length == 0) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0",
					"0201212-0031")/* @res "没有可操作的费用申请单行数据" */);
		}

		for (int i = 0; i < newchilds.length; i++) {
			if (newchilds[i].getBillstatus() == null
					|| newchilds[i].getClose_status() == ErmMatterAppConst.CLOSESTATUS_N) {
				newchildpks.add(newchilds[i].getPk_mtapp_detail());
			}
		}

		MtAppDetailVO[] oldchilds = queryOldVOsByVOs(vo)[0].getChildrenVO();
		for (int i = 0; i < oldchilds.length; i++) {
			if ((oldchilds[i].getClose_status() == null
					|| oldchilds[i].getClose_status() == ErmMatterAppConst.CLOSESTATUS_N) && !newchildpks
							.contains(oldchilds[i].getPk_mtapp_detail())) {
				// 关闭操作中，如果表体里只要还有一个未关闭，那就认为非整单取消关闭
				return false;
			}
		}

		return true;
	}

	private void deleteValidate(AggMatterAppVO[] vos) {
//		List<MatterAppVO> headlist = new ArrayList<MatterAppVO>();
//		List<MtAppDetailVO> detaillist = new ArrayList<MtAppDetailVO>();
//		for (AggMatterAppVO aggvo : vos) {
//			headlist.add((MatterAppVO) aggvo.getParentVO());
//			CircularlyAccessibleValueObject[] childrenVO = aggvo.getChildrenVO();
//			if (childrenVO != null && childrenVO.length > 0) {
//				for (int j = 0; j < childrenVO.length; j++) {
//					detaillist.add((MtAppDetailVO) childrenVO[j]);
//				}
//			}
//		}
//		BDReferenceChecker.getInstance().validate(headlist.toArray(new MatterAppVO[headlist.size()]));
//		if (!detaillist.isEmpty()) {
//			BDReferenceChecker.getInstance().validate(detaillist.toArray(new MtAppDetailVO[detaillist.size()]));
//		}
	}

	/**
	 * 获得单据号
	 * 
	 * @param aggvo
	 * @throws BusinessException
	 */
	private void createBillNo(AggMatterAppVO aggvo) throws BusinessException {
		MatterAppVO parentvo = (MatterAppVO) aggvo.getParentVO();
		FinanceBillCodeInfo info = new FinanceBillCodeInfo(MatterAppVO.DJDL, MatterAppVO.BILLNO, MatterAppVO.PK_GROUP,
				MatterAppVO.PK_ORG, parentvo.getTableName());
		FinanceBillCodeUtils util = new FinanceBillCodeUtils(info);
		util.createBillCode(new AggregatedValueObject[] { aggvo });
	}

	/**
	 * 退还单据号
	 * 
	 * @param vos
	 * @throws BusinessException
	 */
	private void returnBillno(AggMatterAppVO[] vos) throws BusinessException {
		MatterAppVO parentvo = (MatterAppVO) vos[0].getParentVO();
		FinanceBillCodeInfo info = new FinanceBillCodeInfo(MatterAppVO.DJDL, MatterAppVO.BILLNO, MatterAppVO.PK_GROUP,
				MatterAppVO.PK_ORG, parentvo.getTableName());
		FinanceBillCodeUtils util = new FinanceBillCodeUtils(info);
		util.returnBillCode(vos);
	}

	protected void fireBeforeInsertEvent(AggMatterAppVO... vos) throws BusinessException {
		List<AggMatterAppVO> listVOs = new ArrayList<AggMatterAppVO>();
		for (AggMatterAppVO vo : vos) {
			if (isEffectVo(vo)) {
				listVOs.add(vo);
			}
		}

		EventDispatcher.fireEvent(new ErmBusinessEvent(ErmMatterAppConst.MatterApp_MDID,
				ErmEventType.TYPE_INSERT_BEFORE, listVOs.toArray(new AggMatterAppVO[] {})));
	}

	protected void fireAfterInsertEvent(AggMatterAppVO... vos) throws BusinessException {
		List<AggMatterAppVO> listVOs = new ArrayList<AggMatterAppVO>();
		for (AggMatterAppVO vo : vos) {
			if (isEffectVo(vo)) {
				listVOs.add(vo);
			}
		}

		EventDispatcher.fireEvent(new ErmBusinessEvent(ErmMatterAppConst.MatterApp_MDID,
				ErmEventType.TYPE_INSERT_AFTER, listVOs.toArray(new AggMatterAppVO[] {})));
	}

	protected void fireBeforeUpdateEvent(AggMatterAppVO[] vos, AggMatterAppVO[] oldvos) throws BusinessException {
		for (AggMatterAppVO aggMatterAppVO : vos) {
			if (!isEffectVo(aggMatterAppVO)) {
				return;
			}
		}
		EventDispatcher.fireEvent(new ErmBusinessEvent(ErmMatterAppConst.MatterApp_MDID,
				ErmEventType.TYPE_UPDATE_BEFORE, vos, oldvos));
	}

	public void fireMtAppWBEvent(AggMatterAppVO[] vos, AggMatterAppVO[] oldvos) throws BusinessException {
		for (AggMatterAppVO aggMatterAppVO : vos) {
			if (!isEffectVo(aggMatterAppVO)) {
				return;
			}
		}
		EventDispatcher.fireEvent(new ErmBusinessEvent(ErmMatterAppConst.MatterApp_MDID,
				ErmEventType.TYPE_MTAPPWB_AFTER, vos, oldvos));
	}

	protected void fireAfterUpdateEvent(AggMatterAppVO[] vos, AggMatterAppVO[] oldvos) throws BusinessException {
		for (AggMatterAppVO aggMatterAppVO : vos) {
			if (!isEffectVo(aggMatterAppVO)) {
				return;
			}
		}
		EventDispatcher.fireEvent(new ErmBusinessEvent(ErmMatterAppConst.MatterApp_MDID,
				ErmEventType.TYPE_UPDATE_AFTER, vos, oldvos));
	}

	protected void fireBeforeDeleteEvent(AggMatterAppVO... vos) throws BusinessException {
		EventDispatcher.fireEvent(new ErmBusinessEvent(ErmMatterAppConst.MatterApp_MDID,
				ErmEventType.TYPE_DELETE_BEFORE, vos));
	}

	protected void fireAfterDeleteEvent(AggMatterAppVO... vos) throws BusinessException {
		EventDispatcher.fireEvent(new ErmBusinessEvent(ErmMatterAppConst.MatterApp_MDID,
				ErmEventType.TYPE_DELETE_AFTER, vos));
	}

	protected void fireBeforeApproveEvent(AggMatterAppVO... vos) throws BusinessException {
		EventDispatcher.fireEvent(new ErmBusinessEvent(ErmMatterAppConst.MatterApp_MDID, ErmEventType.TYPE_SIGN_BEFORE,
				vos));
	}

	protected void fireAfterApproveEvent(AggMatterAppVO... vos) throws BusinessException {
		EventDispatcher.fireEvent(new ErmBusinessEvent(ErmMatterAppConst.MatterApp_MDID, ErmEventType.TYPE_SIGN_AFTER,
				vos));
	}

	protected void fireBeforeUnApproveEvent( AggMatterAppVO... vos) throws BusinessException {
		EventDispatcher.fireEvent(new ErmBusinessEvent(ErmMatterAppConst.MatterApp_MDID, ErmEventType.TYPE_UNSIGN_BEFORE , vos));
	}

	protected void fireAfterUnApproveEvent(AggMatterAppVO... vos) throws BusinessException {
		EventDispatcher.fireEvent(new ErmBusinessEvent(ErmMatterAppConst.MatterApp_MDID,
				ErmEventType.TYPE_UNSIGN_AFTER, vos));
	}

	protected void fireBeforeCloseEvent(boolean isAuto, AggMatterAppVO... vos) throws BusinessException {
		ErmBusinessEvent event = new ErmBusinessEvent(ErmMatterAppConst.MatterApp_MDID, ErmEventType.TYPE_CLOSE_BEFORE,
				vos);
		event.setUserDefineObjs(isAuto);
		EventDispatcher.fireEvent(event);
	}

	protected void fireAfterCloseEvent(boolean isAuto,AggMatterAppVO... vos) throws BusinessException {
		ErmBusinessEvent event = new ErmBusinessEvent(ErmMatterAppConst.MatterApp_MDID, ErmEventType.TYPE_CLOSE_AFTER,
				vos);
		event.setUserDefineObjs(isAuto);
		EventDispatcher.fireEvent(event);
	}

	protected void fireBeforeUnCloseEvent(boolean isAuto, AggMatterAppVO... vos) throws BusinessException {
		ErmBusinessEvent event = new ErmBusinessEvent(ErmMatterAppConst.MatterApp_MDID,
				ErmEventType.TYPE_UNCLOSE_BEFORE, vos);
		event.setUserDefineObjs(isAuto);
		EventDispatcher.fireEvent(event);
	}

	protected void fireAfterUnCloseEvent(boolean isAuto, AggMatterAppVO... vos) throws BusinessException {
		ErmBusinessEvent event = new ErmBusinessEvent(ErmMatterAppConst.MatterApp_MDID,
				ErmEventType.TYPE_UNCLOSE_AFTER, vos);
		event.setUserDefineObjs(isAuto);
		EventDispatcher.fireEvent(event);
	}

	/**
	 * 加主键锁
	 * 
	 * @param vos
	 * @throws BusinessException
	 */
	private void pklockOperate(AggMatterAppVO... vos) throws BusinessException {
		// 加主键锁
		ErLockUtil.lockAggVOByPk("ERM_matterapp", vos);
	}

	/**
	 * 主表加锁
	 * 
	 * @param vos
	 * @throws BusinessException
	 */
	private void pklockVO(MatterAppVO... vos) throws BusinessException {
		ErLockUtil.lockVOByPk("ERM_matterapp", vos);
	}

	/**
	 * 判断Vo是否是有效单据
	 * 
	 * @param vo
	 * @return
	 */
	private boolean isEffectVo(AggMatterAppVO vo) {
		MatterAppVO parentVo = (MatterAppVO) vo.getParentVO();
		if (parentVo.getBillstatus().equals(ErmMatterAppConst.BILLSTATUS_TEMPSAVED)) {
			return false;
		}
		return true;
	}

	/**
	 * 批量设置聚合VO中值
	 * 
	 * @param vos
	 *            聚合VO
	 * @param attributeName
	 *            属性名
	 * @param attributeValue
	 *            属性值
	 * @param isDetail
	 *            是否冗余到子表 true:是
	 */
	private void setAggMatterAppVOAttribute(AggMatterAppVO[] vos, String attributeName, Object attributeValue,
			boolean isDetail) {
		if (vos != null && vos.length > 0) {
			for (AggMatterAppVO aggVO : vos) {
				aggVO.getParentVO().setAttributeValue(attributeName, attributeValue);
				if (isDetail) {
					if(aggVO.getChildrenVO() != null){
						for (MtAppDetailVO detail : aggVO.getChildrenVO()) {
							detail.setAttributeValue(attributeName, attributeValue);
						}
					}
				}
			}
		}
	}

	private AggMatterAppVO[] queryOldVOsByVOs(AggMatterAppVO... vos) throws BusinessException {

		String[] pks = new String[vos.length];
		for (int i = 0; i < vos.length; i++) {
			pks[i] = vos[i].getParentVO().getPrimaryKey();
		}
		// 查询返回结果
		IErmMatterAppBillQuery qryService = NCLocator.getInstance().lookup(IErmMatterAppBillQuery.class);

		return qryService.queryBillByPKs(pks);
	}

	// 补充vo数据
	public void prepareVoValue(AggMatterAppVO vo) throws BusinessException {
		MatterAppVO parentVo = (MatterAppVO) vo.getParentVO();
		prepareForNullJe(vo);
		prepareHeader(parentVo);
		prepareChildrenVO(vo);
		// 根据费用申请单交易类型允许报销百分比，计算允许报销最大金额
		prepareHeadMaxAmount(parentVo);
		
		if(parentVo.getBillstatus() != ErmMatterAppConst.BILLSTATUS_TEMPSAVED){
			//如果没有计算分摊比例，或比例合计不为100，这里给与比例重算
			prepareChildRaido(vo.getChildrenVO(), parentVo);
			
			// 根据明细行占比，计算各个行允许报销最大金额
			prepareChildMaxAmont(vo.getChildrenVO(), parentVo);
		}
	}
	
	//补充分摊比例
	private void prepareChildRaido(MtAppDetailVO[] childrenVO, MatterAppVO parentVo) {
		if(childrenVO == null){
			return;
		}
		
		int rows = childrenVO.length;
		if (rows == 0) {
			return;
		}
		
		UFDouble sumShareRadio = UFDouble.ZERO_DBL;
		for (int row = 0; row < rows; row++) {
			if(childrenVO[row].getShare_ratio() != null  && 
					childrenVO[row].getStatus() != VOStatus.DELETED){
				sumShareRadio = childrenVO[row].getShare_ratio().add(sumShareRadio);
			}
		}
		
		if(sumShareRadio.compareTo(new UFDouble(100)) != 0){//合计金额不为100，则进行重算
			UFDouble ori_amount = parentVo.getOrig_amount();
			ori_amount = ori_amount == null ? UFDouble.ZERO_DBL : ori_amount;

			UFDouble uf100 = new UFDouble(100);
			UFDouble differ_ratio = new UFDouble(100).setScale(2, UFDouble.ROUND_HALF_UP);// 占比尾差

			int lastRow = 0;// 最后有效行
			for (int row = 0; row < rows; row++) {
				// 计算各行占比
				if(childrenVO[row].getStatus() == VOStatus.DELETED){
					continue;
				}
				UFDouble rowAmount = childrenVO[row].getOrig_amount();
				rowAmount = rowAmount == null ? UFDouble.ZERO_DBL : rowAmount;

				if (rowAmount.compareTo(UFDouble.ZERO_DBL) == 0) {
					childrenVO[row].setShare_ratio(UFDouble.ZERO_DBL);
				} else {
					UFDouble shareRatio = (ori_amount.compareTo(UFDouble.ZERO_DBL) == 0 ? UFDouble.ZERO_DBL : rowAmount
							.div(ori_amount)).multiply(uf100);
					shareRatio = shareRatio.setScale(2, UFDouble.ROUND_HALF_UP);
					childrenVO[row].setShare_ratio(shareRatio);

					differ_ratio = differ_ratio.sub(shareRatio);
					lastRow = row;
				}
			}
			
			if (lastRow > 0 && differ_ratio.compareTo(UFDouble.ZERO_DBL) != 0) {
				if (differ_ratio.compareTo(UFDouble.ZERO_DBL) > 0) {
					UFDouble lastrow_ratio = childrenVO[lastRow].getShare_ratio();
					childrenVO[lastRow].setShare_ratio(differ_ratio.add(lastrow_ratio));
				} else {// 合计值大于100时，差额为负数
					for (int row = lastRow; row > 0; row--) {
						UFDouble oriAmount = childrenVO[row].getOrig_amount();
						if (oriAmount == null || oriAmount.compareTo(UFDouble.ZERO_DBL) == 0
								|| childrenVO[row].getStatus()== VOStatus.DELETED) {
							continue;
						}
						
						UFDouble lastrow_ratio = childrenVO[row].getShare_ratio();
						if (lastrow_ratio.compareTo(differ_ratio.abs()) >= 0) {
							childrenVO[row].setShare_ratio(differ_ratio.add(lastrow_ratio));
							break;
						} else {
							childrenVO[row].setShare_ratio(UFDouble.ZERO_DBL);
							differ_ratio = lastrow_ratio.add(differ_ratio);
						}
					}
				}
			}
		}
		
	}

	private void prepareChildMaxAmont(MtAppDetailVO[] childrenVO, MatterAppVO parentVo) {
		if(childrenVO != null && childrenVO.length > 0){
			UFDouble max_amount = parentVo.getMax_amount();
			UFDouble max_amount_sum = UFDouble.ZERO_DBL;
			boolean amount_recompute = false;// 是否需要重算允许报销金额
			for (int i = 0; i < childrenVO.length; i++) {
				if(childrenVO[i].getStatus() == VOStatus.DELETED){//删除行过滤掉
					continue;
				}
				UFDouble detail_max_amount = childrenVO[i].getMax_amount();
				if(detail_max_amount==null){
					amount_recompute = true;
				}
				max_amount_sum = max_amount_sum.add(detail_max_amount==null?UFDouble.ZERO_DBL:detail_max_amount);
			}
			
			if(amount_recompute || max_amount_sum.compareTo(max_amount) != 0){
				// 金额重算
				amount_recompute = true;
			}else{
				// 不需要重算任何数据
				return ;
			}
			
			UFDouble diffAmount = max_amount.sub(max_amount_sum);// 差额

			for (int row = childrenVO.length - 1 ; row >= 0; row--) {
				if(childrenVO[row].getStatus() == VOStatus.DELETED){//删除行过滤掉
					continue;
				}
				
				UFDouble rowMaxAmount = childrenVO[row].getMax_amount();
				rowMaxAmount = rowMaxAmount == null ? UFDouble.ZERO_DBL : rowMaxAmount;
				UFDouble rowAmount = childrenVO[row].getOrig_amount();

				if (rowAmount != null && rowAmount.compareTo(UFDouble.ZERO_DBL) != 0) {
					if (diffAmount.compareTo(UFDouble.ZERO_DBL) > 0) {
						childrenVO[row].setMax_amount(rowMaxAmount.add(diffAmount));
						break;
					} else {
						UFDouble tempDiffAmount = rowMaxAmount.sub(rowAmount);
						if (diffAmount.abs().compareTo(tempDiffAmount) <= 0) {
							childrenVO[row].setMax_amount(rowMaxAmount.add(diffAmount));
							break;
						} else {
							childrenVO[row].setMax_amount(rowAmount);
							diffAmount = diffAmount.add(tempDiffAmount);
						}
					}
				}
			}
		}
	}

	private void prepareHeadMaxAmount(MatterAppVO parentVo)
			throws BusinessException {
		// 未计算最大允许报销金额情况，在后台进行补充
		if(parentVo.getMax_amount() ==  null){
			DjLXVO djlx = ErmDjlxCache.getInstance().getDjlxVO(parentVo.getPk_group(), parentVo.getPk_tradetype());
			UFDouble bx_percentage = djlx.getBx_percentage();
			UFDouble multi_amount = bx_percentage == null? UFDouble.ONE_DBL:bx_percentage.div(100);
			UFDouble max_amount = parentVo.getOrig_amount().multiply(multi_amount);
			int ybDecimalDigit = Currency.getCurrDigit(parentVo.getPk_currtype());// 原币精度
			max_amount = max_amount.setScale(ybDecimalDigit,BigDecimal.ROUND_UP);
			parentVo.setMax_amount(max_amount);
		}
	}

	private void prepareHeader(MatterAppVO parentVo) {
		if(parentVo.getApprstatus() != IBillStatus.FREE){
			parentVo.setBillstatus(MatterAppUtils.getBillStatus(parentVo.getApprstatus()));
		}
		
		parentVo.setPk_billtype(ErmMatterAppConst.MatterApp_BILLTYPE);
		if (parentVo.getBillmaker() != null) {
			try {
				parentVo.setAuditman(BXBsUtil.getCuserIdByPK_psndoc(parentVo.getBillmaker()));
			} catch (BusinessException e) {
				ExceptionHandler.consume(e);
			}
		}
		// 自动关闭时间，设置为最后
		if (parentVo.getAutoclosedate() != null) {
			parentVo.setAutoclosedate(parentVo.getAutoclosedate().asEnd());
		}
	}

	private void prepareForNullJe(AggMatterAppVO vo) {
		MatterAppVO parentVo = vo.getParentVO();
		MtAppDetailVO[] childrenVos = vo.getChildrenVO();

		String[] jeField = AggMatterAppVO.getHeadAmounts();
		String[] bodyJeField = AggMatterAppVO.getBodyAmounts();
		for (String field : jeField) {
			if (parentVo.getAttributeValue(field) == null) {
				parentVo.setAttributeValue(field, UFDouble.ZERO_DBL);
			}
		}

		if (childrenVos != null) {
			for (MtAppDetailVO item : childrenVos) {
				for (String field : bodyJeField) {
					if (item.getAttributeValue(field) == null) {
						item.setAttributeValue(field, UFDouble.ZERO_DBL);
					}
				}
			}
		}
	}

	private void retriveItems(AggMatterAppVO[] aggVos) throws BusinessException {
		List<String> pkList = new LinkedList<String>();
		for (AggMatterAppVO aggVo : aggVos) {
			if ((aggVo.getChildrenVO() == null || aggVo.getChildrenVO().length == 0)) {
				pkList.add(aggVo.getParentVO().getPk_mtapp_bill());
			}
		}
		if (!pkList.isEmpty()) {
			Map<String, List<MtAppDetailVO>> map = getDAO().queryMtDetailsByPks(
					pkList.toArray(new String[pkList.size()]));

			for (AggMatterAppVO aggVo : aggVos) {
				if ((aggVo.getChildrenVO() == null || aggVo.getChildrenVO().length == 0)) {
					List<MtAppDetailVO> list = map.get(aggVo.getParentVO().getPk_mtapp_bill());
					if (list == null)
						continue;
					aggVo.setChildrenVO(list.toArray(new MtAppDetailVO[list.size()]));
				}
			}
		}
	}

	private void prepareChildrenVO(AggMatterAppVO vo) {
		// 将表头信息冗余到表体
		MatterAppVO parentVo = vo.getParentVO();
		MtAppDetailVO[] children = (MtAppDetailVO[]) vo.getChildrenVO();

		
		if (children != null && children.length > 0) {
			UFBoolean iscostshare = UFBoolean.FALSE;// 是否分摊，判断条件为明细行费用承担单位和费用承担部门是否相同
			
			String assumeorg = children[0].getAssume_org()==null?"~":children[0].getAssume_org();
			String assumedept = children[0].getAssume_dept()==null?"~":children[0].getAssume_dept();
			
			for (int i = 0; i < children.length; i++) {
				if (children[i].getStatus() == VOStatus.DELETED) {
					continue;
				}
				
				if(!iscostshare.booleanValue()){
					String org  = children[i].getAssume_org()==null?"~":children[i].getAssume_org();
					String dept  = children[i].getAssume_dept()==null?"~":children[i].getAssume_dept();
					if(!(assumeorg.equals(org)&&assumedept.equals(dept))){
						iscostshare = UFBoolean.TRUE;
					}
				}

				children[i].setPk_currtype(parentVo.getPk_currtype());
				children[i].setPk_billtype(parentVo.getPk_billtype());
				children[i].setPk_tradetype(parentVo.getPk_tradetype());
				children[i].setBilldate(parentVo.getBilldate());
				children[i].setPk_group(parentVo.getPk_group());// 集团、组织
				children[i].setPk_org(parentVo.getPk_org());

				children[i].setPk_currtype(parentVo.getPk_currtype());
				
				children[i].setApply_dept(parentVo.getApply_dept());
				children[i].setBillmaker(parentVo.getBillmaker());
				
				children[i].setBillno(parentVo.getBillno());//单据编号，注：在createCode中在修改时，可能没有设置billno
				children[i].setClose_status(ErmMatterAppConst.CLOSESTATUS_N);// 关闭状态
				children[i].setBillstatus(parentVo.getBillstatus());// 表头单据状态冗余到表体
				children[i].setEffectstatus(ErmMatterAppConst.EFFECTSTATUS_NO);
			}
			// 设置申请单是否分摊
			parentVo.setIscostshare(iscostshare);
		}
		}
	
	/**
	 * 回写申请单审批信息
	 */
	@Override
	public void callCheckStatus(CheckStatusCallbackContext cscc) throws BusinessException {
		try {
			AggMatterAppVO updateVO = (AggMatterAppVO) cscc.getBillVo();
			// 加锁
			pklockOperate(updateVO);
			// 版本校验
			BDVersionValidationUtil.validateVersion(updateVO);

			// 查询oldvos
			AggMatterAppVO[] oldvos = queryOldVOsByVOs(updateVO);
			if(oldvos != null && oldvos.length > 0){
				// 保留下来oldvo，供业务处理的事件前使用
				updateVO.setOldvo(oldvos[0]);
			}
			
			// 数据更新到数据库
			getDAO().updateAggVOsByFields(new AggMatterAppVO[] { updateVO },
					new String[] { MatterAppVO.APPRSTATUS, MatterAppVO.APPROVER, MatterAppVO.APPROVETIME },
					new String[] {});
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
		}
	}
	
	/**
	 * 更新审批单状态
	 * 
	 * @param updateVO
	 * @return
	 * @throws BusinessException
	 */
	public AggMatterAppVO updateVOBillStatus(AggMatterAppVO updateVO) throws BusinessException {
		try {
			// 加锁
			pklockOperate(updateVO);
			// 版本校验
			BDVersionValidationUtil.validateVersion(updateVO);

			// 设置单据状态
			int billstatus = MatterAppUtils.getBillStatus(updateVO.getParentVO().getApprstatus());
			updateVO.getParentVO().setBillstatus(billstatus);

			// 数据更新到数据库
			getDAO().updateAggVOsByFields(new AggMatterAppVO[] { updateVO }, new String[] { MatterAppVO.BILLSTATUS },
					new String[] {});

			return updateVO;
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
		}
		return null;
	}
}