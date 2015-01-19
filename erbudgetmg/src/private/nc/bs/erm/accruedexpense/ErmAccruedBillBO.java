package nc.bs.erm.accruedexpense;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nc.bs.arap.bx.BusiLogUtil;
import nc.bs.businessevent.EventDispatcher;
import nc.bs.dao.BaseDAO;
import nc.bs.er.util.BXBsUtil;
import nc.bs.erm.accruedexpense.check.AccruedBillVOChecker;
import nc.bs.erm.accruedexpense.common.ErmAccruedBillConst;
import nc.bs.erm.accruedexpense.common.ErmAccruedBillUtils;
import nc.bs.erm.event.ErmBusinessEvent;
import nc.bs.erm.event.ErmEventType;
import nc.bs.erm.matterapp.common.ErmMatterAppConst;
import nc.bs.erm.util.ErLockUtil;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.pub.pf.CheckStatusCallbackContext;
import nc.bs.pub.pf.ICheckStatusCallback;
import nc.itf.uap.pf.IWorkflowMachine;
import nc.jdbc.framework.exception.DbException;
import nc.pubitf.erm.accruedexpense.IErmAccruedBillQuery;
import nc.vo.arap.bx.util.ActionUtils;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.erm.accruedexpense.AccruedDetailVO;
import nc.vo.erm.accruedexpense.AccruedVO;
import nc.vo.erm.accruedexpense.AccruedVerifyVO;
import nc.vo.erm.accruedexpense.AggAccruedBillVO;
import nc.vo.erm.common.MessageVO;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.fipub.billcode.FinanceBillCodeInfo;
import nc.vo.fipub.billcode.FinanceBillCodeUtils;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.trade.pub.IBillStatus;
import nc.vo.uap.rbac.constant.INCSystemUserConst;
import nc.vo.util.AuditInfoUtil;
import nc.vo.util.BDVersionValidationUtil;

import org.apache.commons.lang.ArrayUtils;

public class ErmAccruedBillBO implements ICheckStatusCallback {

	private ErmAccruedBillDAO dao;

	public ErmAccruedBillDAO getDAO() {
		if (dao == null) {
			dao = new ErmAccruedBillDAO();
		}
		return dao;
	}

	public AggAccruedBillVO tempSave(AggAccruedBillVO vo) throws BusinessException {
		// 修改加锁
		pklockOperate(vo);

		// 查询修改前的vo
		IErmAccruedBillQuery qryService = NCLocator.getInstance().lookup(IErmAccruedBillQuery.class);

		AggAccruedBillVO oldvo = null;
		if (vo.getParentVO().getStatus() != VOStatus.NEW) {
			oldvo = qryService.queryBillByPk(vo.getParentVO().getPrimaryKey());
			// 补齐children信息（因前台传过来的的只是改变的children）
			if (oldvo != null) {
				fillUpChildren(vo, oldvo);
			}
		}

		// 获取单据号
		createBillNo(vo);

		prepareVoValue(vo);

		// 设置暂存状态
		vo.getParentVO().setAttributeValue(AccruedVO.BILLSTATUS, ErmAccruedBillConst.BILLSTATUS_TEMPSAVED);

		// 设置审计信息
		if (vo.getParentVO().getPk_accrued_bill() != null) {
			AuditInfoUtil.updateData(vo.getParentVO());
		} else {
			AuditInfoUtil.addData(vo.getParentVO());
		}
		// 更新保存
		vo = getDAO().updateVO(vo);

		// 返回
		return vo;
	}

	public AggAccruedBillVO invalidBill(AggAccruedBillVO aggvo)throws BusinessException{
		if (aggvo == null) {
			return null;
		}
		// 删除加锁
		pklockOperate(aggvo);
		// 版本校验
		BDVersionValidationUtil.validateVersion(aggvo);

		AccruedBillVOChecker vochecker = new AccruedBillVOChecker();
		vochecker.checkInvalid(aggvo);

		// 作废前事件处理
		fireBeforeInvalidEvent(aggvo);

		//作废状态
		aggvo.getParentVO().setBillstatus(BXStatusConst.DJZT_Invalid);
		// 取服务器事件作为修改时间
		AuditInfoUtil.updateData(aggvo.getParentVO());
		new BaseDAO().updateVOArray(new AccruedVO[]{aggvo.getParentVO()} , new String[] { MatterAppVO.BILLSTATUS, MatterAppVO.MODIFIER, MatterAppVO.MODIFIEDTIME });

		// 作废后事件处理
		fireAfterInvalidEvent(aggvo);
		
		// 删除审批流
		NCLocator.getInstance().lookup(IWorkflowMachine.class)
				.deleteCheckFlow(aggvo.getParentVO().getPk_tradetype(), aggvo.getParentVO().getPrimaryKey(), aggvo, InvocationInfoProxy.getInstance().getUserId());
		return aggvo;
	}

	public AggAccruedBillVO insertVO(AggAccruedBillVO aggvo) throws BusinessException {
		prepareVoValue(aggvo);
		new AccruedBillVOChecker().checkBackSave(aggvo);
		AggAccruedBillVO result = null;
		try {
			// 获取单据号
			createBillNo(aggvo);
			// 设置审计信息
			AuditInfoUtil.addData(aggvo.getParentVO());
			// 新增前事件处理
			fireBeforeInsertEvent(aggvo);
			// 新增保存
			result = getDAO().insertVO(aggvo);
			// 新增后事件处理
			fireAfterInsertEvent(aggvo);

			// 查询返回结果
			IErmAccruedBillQuery qryService = NCLocator.getInstance().lookup(IErmAccruedBillQuery.class);
			result = qryService.queryBillByPk(aggvo.getParentVO().getPrimaryKey());

		} catch (Exception e) {
			if(aggvo.getParentVO().getBillno() != null){
				returnBillno(new AggAccruedBillVO[] { aggvo });
			}
			ExceptionHandler.handleException(e);
		}

		result.getParentVO().setHasntbcheck(UFBoolean.FALSE);
		// 返回
		return result;
	}

	public void deleteVOs(AggAccruedBillVO[] aggvos) throws BusinessException {
		if (aggvos == null || aggvos.length == 0) {
			return;
		}

		retriveItems(aggvos);
		// 删除加锁
		pklockOperate(aggvos);
		// 版本校验
		BDVersionValidationUtil.validateVersion(aggvos);
		// 删除引用校验
		// deleteValidate(aggvos);
		// vo校验
		AccruedBillVOChecker vochecker = new AccruedBillVOChecker();
		vochecker.checkDelete(aggvos);
		// 删除前事件处理
		fireBeforeDeleteEvent(aggvos);
		// 删除单据
		getDAO().deleteVOs(aggvos);
		// 修改后事件处理
		fireAfterDeleteEvent(aggvos);
		// 退还单据号
		returnBillno(aggvos);
		
		// 删除审批流
		NCLocator.getInstance().lookup(IWorkflowMachine.class)
				.deleteCheckFlow(aggvos[0].getParentVO().getPk_tradetype(), aggvos[0].getParentVO().getPrimaryKey(), aggvos[0], InvocationInfoProxy.getInstance().getUserId());
		
		// 记录业务日志
		for (AggAccruedBillVO aggvo : aggvos) {
			aggvo.getParentVO().setStatus(VOStatus.DELETED);
			AccruedDetailVO[] childrenVO = aggvo.getChildrenVO();
			if (!ArrayUtils.isEmpty(childrenVO)) {
				for (AccruedDetailVO child : childrenVO) {
					child.setStatus(VOStatus.DELETED);
				}
			}
		}
		BusiLogUtil.insertSmartBusiLog(ErmAccruedBillConst.ACCRUED_MD_DELETE_OPER, aggvos, null);

	}

	public AggAccruedBillVO updateVO(AggAccruedBillVO aggvo) throws BusinessException {
		// 修改加锁
		pklockOperate(aggvo);
		// 版本校验
		BDVersionValidationUtil.validateVersion(aggvo.getParentVO());

		// 查询修改前的vo
		IErmAccruedBillQuery qryService = NCLocator.getInstance().lookup(IErmAccruedBillQuery.class);
		AggAccruedBillVO oldaggvo = qryService.queryBillByPk(aggvo.getParentVO().getPrimaryKey());

		// 补齐children信息（因前台传过来的的只是改变的children）
		fillUpChildren(aggvo, oldaggvo);

		// 设置审计信息
		AuditInfoUtil.updateData(aggvo.getParentVO());
		// 获取单据号
		createBillNo(aggvo);
		// 补齐数据
		prepareVoValue(aggvo);
		// vo校验
		AccruedBillVOChecker vochecker = new AccruedBillVOChecker();
		vochecker.checkBackUpdateSave(aggvo);
		// 修改前事件处理
		fireBeforeUpdateEvent(new AggAccruedBillVO[] { aggvo }, new AggAccruedBillVO[] { oldaggvo });
		// 更新保存
		aggvo = getDAO().updateVO(aggvo);
		// 修改后事件处理
		fireAfterUpdateEvent(new AggAccruedBillVO[] { aggvo }, new AggAccruedBillVO[] { oldaggvo });
		// 查询返回结果
		aggvo = qryService.queryBillByPk(aggvo.getParentVO().getPrimaryKey());
		// 记录业务日志
		BusiLogUtil.insertSmartBusiLog(ErmAccruedBillConst.ACCRUED_MD_UPDATE_OPER, new AggAccruedBillVO[] { aggvo },
				new AggAccruedBillVO[] { oldaggvo });

		aggvo.getParentVO().setHasntbcheck(UFBoolean.FALSE);

		return aggvo;
	}

	public AggAccruedBillVO updateVOBillStatus(AggAccruedBillVO updateVO) throws BusinessException {
		// 加锁
		pklockOperate(updateVO);
		// 版本校验
		BDVersionValidationUtil.validateVersion(updateVO);

		// 设置单据状态
		int billstatus = ErmAccruedBillUtils.getBillStatus(updateVO.getParentVO().getApprstatus());
		updateVO.getParentVO().setBillstatus(billstatus);

		// 数据更新到数据库
		getDAO().updateAggVOsByHeadFields(new AggAccruedBillVO[] { updateVO }, new String[] { AccruedVO.BILLSTATUS });

		return updateVO;
	}

	/**
	 * 新增，修改，后保存数据，后台补齐数据：金额=余额=预计余额
	 * @param aggvo
	 * @throws BusinessException
	 */
	private void prepareVoValue(AggAccruedBillVO aggvo) throws BusinessException {
		prepareHeader(aggvo.getParentVO());
		prepareChildrenVO(aggvo);
	}

	private void prepareChildrenVO(AggAccruedBillVO aggvo) {
		if(aggvo == null || aggvo.getChildrenVO() == null || aggvo.getChildrenVO().length <= 0){
			return;
		}
		for (AccruedDetailVO detailvo : aggvo.getChildrenVO()) {
			// 用金额补齐预计余额和余额（原因：数据可能从portal端传过来，没有做数据联动）
			detailvo.setPredict_rest_amount(detailvo.getAmount());
			detailvo.setRest_amount(detailvo.getAmount());
			detailvo.setOrg_rest_amount(detailvo.getOrg_amount());
			detailvo.setGroup_rest_amount(detailvo.getGroup_amount());
			detailvo.setGlobal_rest_amount(detailvo.getGlobal_amount());
		}
	}


	public AggAccruedBillVO unRedbackVO(AggAccruedBillVO aggvo) throws BusinessException {
		// 修改加锁
		pklockOperate(aggvo);
		// 版本校验
		BDVersionValidationUtil.validateVersion(aggvo.getParentVO());

		//补齐信息
		fillUpAggAccruedBillVO(aggvo);

		// vo校验
		new AccruedBillVOChecker().checkUnRedback(aggvo);

		// 删除红冲前事件处理
		fireBeforeUnRedbackEvent(aggvo);

		// 删除红冲单据
		getDAO().deleteVOs(new AggAccruedBillVO[]{aggvo});

		// 删除红冲后事件处理
		fireAfterUnRedbackEvent(aggvo);

		// 回写原预提单
		unRedbackOldAccruedBill(aggvo);

		return aggvo;
	}

	private void fillUpAggAccruedBillVO(AggAccruedBillVO aggvo) throws BusinessException {
		// 查询oldvos
		AggAccruedBillVO[] oldvos = queryOldVOsByVOs(aggvo);
		if (oldvos != null && oldvos.length > 0) {
			// 保留下来oldvo，供业务处理的事件前使用
			aggvo.setOldvo(oldvos[0]);
		}
		retriveItems(new AggAccruedBillVO[]{aggvo});
	}

	private void unRedbackOldAccruedBill(AggAccruedBillVO redVO) throws BusinessException {
		if(redVO == null || redVO.getChildrenVO() == null || redVO.getChildrenVO().length == 0){
			return;
		}
		Map<String,UFDouble[]> hcDetailMap = new HashMap<String,UFDouble[]>();
		for(AccruedDetailVO detailvo : redVO.getChildrenVO()){
			UFDouble[] money = new UFDouble[]{detailvo.getAmount(),detailvo.getOrg_amount(),detailvo.getGroup_amount(),detailvo.getGlobal_amount()};
			hcDetailMap.put(detailvo.getSrc_detailpk(), money);
		}
		// 查询原预提单
		String src_accruedpk = null;
		if(redVO.getChildrenVO() != null && redVO.getChildrenVO().length > 0){
			src_accruedpk = redVO.getChildrenVO()[0].getSrc_accruedpk();
		}else if(redVO.getOldvo() != null && redVO.getChildrenVO() != null && redVO.getChildrenVO().length > 0){
			src_accruedpk = redVO.getOldvo().getChildrenVO()[0].getSrc_accruedpk();
		}else {
			return;
		}
		AggAccruedBillVO oldvo = NCLocator.getInstance().lookup(IErmAccruedBillQuery.class)
				.queryBillByPk(src_accruedpk);

		AccruedVO parent = oldvo.getParentVO();
		// 删除红冲单据后，原预提单红冲标志置空
		parent.setRedflag(ErmAccruedBillConst.REDFLAG_NO);
		// aggvo:红冲单据，金额为负数，所以这里回写为减:原预提单余额+负的红冲金额
		parent.setRest_amount(parent.getRest_amount().sub(redVO.getParentVO().getAmount()));
		parent.setOrg_rest_amount(parent.getOrg_rest_amount().sub(redVO.getParentVO().getOrg_verify_amount()));
		parent.setGroup_rest_amount(parent.getGroup_rest_amount().sub(redVO.getParentVO().getGroup_verify_amount()));
		parent.setGlobal_rest_amount(parent.getGlobal_rest_amount().sub(redVO.getParentVO().getGlobal_verify_amount()));
		parent.setPredict_rest_amount(parent.getPredict_rest_amount().sub(redVO.getParentVO().getAmount()));
		//篮子预提单上的红冲金额，在删除红冲单回写时，是减去相应红冲的金额，红冲金额记录负数，则为add
		parent.setRed_amount(parent.getRed_amount().add(redVO.getParentVO().getAmount()));
		for (AccruedDetailVO child : oldvo.getChildrenVO()) {
			if(hcDetailMap.get(child.getPk_accrued_detail()) != null){
				child.setRest_amount(child.getRest_amount().sub(hcDetailMap.get(child.getPk_accrued_detail())[0]));
				child.setOrg_rest_amount(child.getOrg_rest_amount().sub(hcDetailMap.get(child.getPk_accrued_detail())[1]));
				child.setGroup_rest_amount(child.getGroup_rest_amount().sub(hcDetailMap.get(child.getPk_accrued_detail())[2]));
				child.setGlobal_rest_amount(child.getGlobal_rest_amount().sub(hcDetailMap.get(child.getPk_accrued_detail())[3]));
				child.setPredict_rest_amount(child.getPredict_rest_amount().sub(hcDetailMap.get(child.getPk_accrued_detail())[0]));
				child.setRed_amount(child.getRed_amount().sub(hcDetailMap.get(child.getPk_accrued_detail())[0]));
			}
		}
		
		// 回写原预提单
		getDAO().getBaseDAO().updateVO(oldvo.getParentVO());
		getDAO().getBaseDAO().updateVOArray(oldvo.getChildrenVO());

	}

	public AggAccruedBillVO redbackVO(AggAccruedBillVO redbackVO) throws BusinessException {
		

		prepareVoValue(redbackVO);
		new AccruedBillVOChecker().checkBackSave(redbackVO);
		AggAccruedBillVO result = null;
		try {
			// 获取单据号
			createBillNo(redbackVO);
			// 设置审计信息
			AuditInfoUtil.addData(redbackVO.getParentVO());
			// 红冲前事件处理
			fireBeforeRedbackEvent(redbackVO);

			// 插入红冲单据
			getDAO().insertVO(redbackVO);

			//红冲后事件处理
			fireAfterRedbackEvent(redbackVO);
			// 回写原预提单状态和金额
			writebackOldAccruedBill(redbackVO);

			// 查询返回结果
			IErmAccruedBillQuery qryService = NCLocator.getInstance().lookup(IErmAccruedBillQuery.class);
			result = qryService.queryBillByPk(redbackVO.getParentVO().getPrimaryKey());

		} catch (Exception e) {
			if(redbackVO.getParentVO().getBillno() != null){
				returnBillno(new AggAccruedBillVO[] { redbackVO });
			}
			ExceptionHandler.handleException(e);
		}

		result.getParentVO().setHasntbcheck(UFBoolean.FALSE);
		// 返回
		return result;
	}


	private void writebackOldAccruedBill(AggAccruedBillVO redbackVO) throws BusinessException {
		Set<String> pkList = new HashSet<String>();
		// key:原预提单的主键+表体主键  value:红冲金额
		Map<String,UFDouble> redMap = new HashMap<String,UFDouble>();
		for (AccruedDetailVO redDetailvo : redbackVO.getChildrenVO()) {
			pkList.add(redDetailvo.getSrc_accruedpk());
			redMap.put(redDetailvo.getSrc_accruedpk()+redDetailvo.getSrc_detailpk(), redDetailvo.getAmount());
		}
		AggAccruedBillVO[] oldAggvos = NCLocator.getInstance().lookup(IErmAccruedBillQuery.class)
				.queryBillByPks(pkList.toArray(new String[pkList.size()]));
		// 给原预提单加主键锁
		lockByPK(pkList);
		
		Set<AccruedDetailVO> detailSet = new HashSet<AccruedDetailVO>();
		Set<AccruedVO> headSet = new HashSet<AccruedVO>();
		for(AggAccruedBillVO oldvo : oldAggvos){
			for(AccruedDetailVO oldDetailvo : oldvo.getChildrenVO()){
				String key = oldDetailvo.getPk_accrued_bill() + oldDetailvo.getPk_accrued_detail();
				if(redMap.containsKey(key)){
					
					if(redMap.get(key).multiply(new UFDouble("-1")).compareTo(oldDetailvo.getPredict_rest_amount()) > 0){
						throw new BusinessException("按行红冲金额已超出回写金额，请改小金额重新红冲");
					}
					
					oldDetailvo.setPredict_rest_amount(oldDetailvo.getPredict_rest_amount().add(redMap.get(key)));
					oldDetailvo.setRest_amount(oldDetailvo.getRest_amount().add(redMap.get(key)));
					//累加红冲金额记录在蓝字预提单上
					oldDetailvo.setRed_amount(oldDetailvo.getRed_amount().add(new UFDouble("-1").multiply(redMap.get(key))));
					ErmAccruedBillUtils.resetBodyAmount(oldDetailvo, oldvo.getParentVO());
					detailSet.add(oldDetailvo);
					headSet.add(oldvo.getParentVO());
					redMap.remove(key);
				}
			}
			// 表体原币金额合计到表头
			ErmAccruedBillUtils.sumBodyAmount2Head(oldvo);

			// 再根据汇率重新计算组织、集团、全局金额
			ErmAccruedBillUtils.resetHeadAmounts(oldvo.getParentVO());
			
		}
		
		if(!redMap.isEmpty()){
			throw new BusinessException("红冲表体行，存在非原预提单的行，请删除");
		}
		// 更新数据表
		getDAO().getBaseDAO().updateVOArray(headSet.toArray(new AccruedVO[headSet.size()]));
		getDAO().getBaseDAO().updateVOArray(detailSet.toArray(new AccruedDetailVO[detailSet.size()]));
	}

	/**
	 * 加主键锁
	 * @param pkList
	 * @throws BusinessException
	 */
	private void lockByPK(Set<String> pkList) throws BusinessException{
		ErLockUtil.lockByPk(ErmAccruedBillConst.Accrued_Lock_Key, pkList);
	}


	private void prepareHeader(AccruedVO parentVo) throws BusinessException {
//		if (parentVo.getApprstatus() != IBillStatus.FREE) {
//			parentVo.setBillstatus(ErmAccruedBillUtils.getBillStatus(parentVo.getApprstatus()));
//		}
		parentVo.setPk_billtype(ErmAccruedBillConst.AccruedBill_Billtype);
		if (parentVo.getOperator() != null) {
			String auditUser = BXBsUtil.getCuserIdByPK_psndoc(parentVo.getOperator());
			if(auditUser != null){
				parentVo.setAuditman(auditUser);
			}else {
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("accruedbill_0","02011001-0026")/*@res "单据无表体,不能进行红冲操作"*/);
			}
		}
		// 用金额补齐预计余额和余额
		parentVo.setPredict_rest_amount(parentVo.getAmount());
		parentVo.setRest_amount(parentVo.getAmount());
		parentVo.setOrg_rest_amount(parentVo.getOrg_amount());
		parentVo.setGroup_rest_amount(parentVo.getGroup_amount());
		parentVo.setGlobal_rest_amount(parentVo.getGlobal_amount());
	}

	private void fillUpChildren(AggAccruedBillVO aggvo, AggAccruedBillVO oldvo) {
		List<AccruedDetailVO> resultChild = new ArrayList<AccruedDetailVO>();

		List<String> pkList = new ArrayList<String>();

		AccruedDetailVO[] changedChildren = aggvo.getChildrenVO();
		AccruedDetailVO[] oldChildren = oldvo.getChildrenVO();

		if (changedChildren != null) {
			for (int i = 0; i < changedChildren.length; i++) {
				if (changedChildren[i].getStatus() != VOStatus.NEW) {
					pkList.add(changedChildren[i].getPk_accrued_detail());
				}
				resultChild.add(changedChildren[i]);
			}
		}

		if (oldChildren != null) {
			for (int i = 0; i < oldChildren.length; i++) {
				if (!pkList.contains(oldChildren[i].getPk_accrued_detail())) {
					oldChildren[i].setStatus(VOStatus.UPDATED);
					resultChild.add(oldChildren[i]);
				}
			}
		}

		Collections.sort(resultChild, new Comparator<AccruedDetailVO>() {
			@Override
			public int compare(AccruedDetailVO item1, AccruedDetailVO item2) {
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

		aggvo.setChildrenVO(resultChild.toArray(new AccruedDetailVO[] {}));
	}

	private void retriveItems(AggAccruedBillVO[] aggvos) throws BusinessException {
		List<String> pkList = new LinkedList<String>();
		for (AggAccruedBillVO aggvo : aggvos) {
			if ((aggvo.getChildrenVO() == null || aggvo.getChildrenVO().length == 0)) {
				pkList.add(aggvo.getParentVO().getPk_accrued_bill());
			}
		}
		if (!pkList.isEmpty()) {
			Map<String, List<AccruedDetailVO>> detailMap = getDAO().queryAccruedDetailsByHeadPks(
					pkList.toArray(new String[pkList.size()]));

			// 核销明细
			Map<String, List<AccruedVerifyVO>> verifyMap = getDAO().queryAccruedVerifiesByHeadPks(
					pkList.toArray(new String[pkList.size()]));

			for (AggAccruedBillVO aggvo : aggvos) {
				if ((aggvo.getChildrenVO() == null || aggvo.getChildrenVO().length == 0)) {
					List<AccruedDetailVO> list = detailMap.get(aggvo.getParentVO().getPk_accrued_bill());
					if (list == null) {
						continue;
					}
					aggvo.setChildrenVO(list.toArray(new AccruedDetailVO[list.size()]));
				}

				if (aggvo.getAccruedVerifyVO() == null || aggvo.getAccruedVerifyVO().length == 0) {
					List<AccruedVerifyVO> list = verifyMap.get(aggvo.getParentVO().getPk_accrued_bill());
					if (list == null) {
						continue;
					}
					aggvo.setAccruedVerifyVO(list.toArray(new AccruedVerifyVO[list.size()]));
				}
			}

		}
	}

	/**
	 * 加主键锁
	 *
	 * @param vos
	 * @throws BusinessException
	 */
	private void pklockOperate(AggAccruedBillVO... aggvos) throws BusinessException {
		// 加主键锁
		ErLockUtil.lockAggVOByPk(ErmAccruedBillConst.Accrued_Lock_Key, aggvos);
	}

	/**
	 * 主表加锁
	 *
	 * @param vos
	 * @throws BusinessException
	 */
	@SuppressWarnings("unused")
	private void pklock2headVOs(AccruedVO... headvos) throws BusinessException {
		ErLockUtil.lockVOByPk(ErmAccruedBillConst.Accrued_Lock_Key, headvos);
	}

	public MessageVO[] approveVOS(AggAccruedBillVO[] aggvos) throws BusinessException {
		if (aggvos == null || aggvos.length == 0) {
			return null;
		}

		retriveItems(aggvos);

		MessageVO[] msgs = new MessageVO[aggvos.length];

		// 加锁
		pklockOperate(aggvos);
		// 版本校验
		BDVersionValidationUtil.validateVersion(aggvos);
		
		//补充审批信息
		for(AggAccruedBillVO aggVo : aggvos){
			if(aggVo.getParentVO().getApprovetime() == null){
				aggVo.getParentVO().setApprover(INCSystemUserConst.NC_USER_PK);
				aggVo.getParentVO().setApprovetime(AuditInfoUtil.getCurrentTime());
			}
		}
		
		// vo校验
		AccruedBillVOChecker vochecker = new AccruedBillVOChecker();
		vochecker.checkApprove(aggvos);
		for (int i = 0; i < aggvos.length; i++) {
			approveBack(aggvos[i]);
			msgs[i] = new MessageVO(aggvos[i], ActionUtils.AUDIT);
		}

		// 返回
		return msgs;
	}

	private void approveBack(AggAccruedBillVO aggvo) throws BusinessException {
		// 审核前事件处理
		fireBeforeApproveEvent(aggvo);

		// 设置生效信息，单据状态
		setAccruedVOAttribute(new AggAccruedBillVO[] { aggvo }, AccruedVO.EFFECTSTATUS,
				ErmAccruedBillConst.EFFECTSTATUS_VALID);

		setAccruedVOAttribute(new AggAccruedBillVO[] { aggvo }, AccruedVO.BILLSTATUS,
				ErmAccruedBillConst.BILLSTATUS_APPROVED);

		// 更新保存
		getDAO().updateAggVOsByHeadFields(
				new AggAccruedBillVO[] { aggvo },
				new String[] { AccruedVO.BILLSTATUS, AccruedVO.APPRSTATUS, AccruedVO.EFFECTSTATUS, AccruedVO.APPROVER,
						AccruedVO.APPROVETIME });

		// 审核后事件处理
		fireAfterApproveEvent(aggvo);

		// 恢复预算控制标记
		aggvo.getParentVO().setHasntbcheck(UFBoolean.FALSE);
	}

	public MessageVO[] unapproveVOs(AggAccruedBillVO[] aggvos) throws BusinessException {
		if (aggvos == null || aggvos.length == 0) {
			return null;
		}

		retriveItems(aggvos);
		
		MessageVO[] msgs = new MessageVO[aggvos.length];
		// 加锁
		pklockOperate(aggvos);
		// 版本校验
		BDVersionValidationUtil.validateVersion(aggvos);
		// vo校验
		AccruedBillVOChecker vochecker = new AccruedBillVOChecker();
		vochecker.checkunApprove(aggvos);

		try {
			for (int i = 0; i < aggvos.length; i++) {
				unApproveBack(aggvos[i]);
				msgs[i] = new MessageVO(aggvos[i], ActionUtils.UNAUDIT);
			}
		} catch (Exception e) {
			throw ExceptionHandler.handleException(e);
		}

		return msgs;
	}

	private void unApproveBack(AggAccruedBillVO aggvo) throws BusinessException {
		
		// 取消审核前事件处理
		fireBeforeUnApproveEvent(aggvo.getOldvo());
		
		//补充审批信息
		if (aggvo.getParentVO().getApprover() == null 
				|| aggvo.getParentVO().getApprover().equals(INCSystemUserConst.NC_USER_PK)) {
			aggvo.getParentVO().setApprover(null);
			aggvo.getParentVO().setApprovetime(null);
		}
		
		// 设置单据状态、生效信息
		setAccruedVOAttribute(new AggAccruedBillVO[] { aggvo }, AccruedVO.EFFECTSTATUS,
				ErmMatterAppConst.EFFECTSTATUS_NO);

		setAccruedVOAttribute(new AggAccruedBillVO[] { aggvo }, AccruedVO.BILLSTATUS,
				ErmAccruedBillConst.BILLSTATUS_SAVED);


		// 更新保存
		getDAO().updateAggVOsByHeadFields(
				new AggAccruedBillVO[] { aggvo },
				new String[] { AccruedVO.APPRSTATUS, AccruedVO.EFFECTSTATUS, AccruedVO.BILLSTATUS,
						AccruedVO.APPROVER, AccruedVO.APPROVETIME });

		// 取消审核后事件处理
		fireAfterUnApproveEvent(aggvo);

		//恢复预算控制标记
		aggvo.getParentVO().setHasntbcheck(UFBoolean.FALSE);
	}

	public AggAccruedBillVO[] commitVOs(AggAccruedBillVO[] aggvos) throws BusinessException {
		if (aggvos == null || aggvos.length == 0) {
			return null;
		}

		retriveItems(aggvos);
		// 加锁
		pklockOperate(aggvos);
		// 版本校验
		BDVersionValidationUtil.validateVersion(aggvos);
		// vo校验
		AccruedBillVOChecker vochecker = new AccruedBillVOChecker();
		vochecker.checkCommit(aggvos);

		// 设置提交相关状态
		setAccruedVOAttribute(aggvos, AccruedVO.BILLSTATUS, Integer.valueOf(ErmAccruedBillConst.BILLSTATUS_SAVED));
		setAccruedVOAttribute(aggvos, AccruedVO.APPRSTATUS, Integer.valueOf(IBillStatus.COMMIT));

		// 更新保存
		getDAO().updateAggVOsByHeadFields(aggvos, new String[] { AccruedVO.BILLSTATUS, AccruedVO.APPRSTATUS });

		// 返回
		return aggvos;
	}

	/**
	 * 设置预提单主表属性值
	 *
	 * @param vos
	 * @param attributeName
	 * @param attributeValue
	 * @param isDetail
	 */
	private void setAccruedVOAttribute(AggAccruedBillVO[] vos, String attributeName, Object attributeValue) {
		if (vos != null && vos.length > 0) {
			for (AggAccruedBillVO aggVO : vos) {
				aggVO.getParentVO().setAttributeValue(attributeName, attributeValue);
			}
		}
	}

	public AggAccruedBillVO[] recallVOs(AggAccruedBillVO[] aggvos) throws BusinessException {
		if (aggvos == null || aggvos.length == 0) {
			return null;
		}

		retriveItems(aggvos);
		// 加锁
		pklockOperate(aggvos);
		// 版本校验
		BDVersionValidationUtil.validateVersion(aggvos);
		// vo校验
		AccruedBillVOChecker vochecker = new AccruedBillVOChecker();
		vochecker.checkRecall(aggvos);

		// 设置表头收回相关信息
		setAccruedVOAttribute(aggvos, AccruedVO.BILLSTATUS, Integer.valueOf(ErmAccruedBillConst.BILLSTATUS_SAVED));

		// 更新保存
		getDAO().updateAggVOsByHeadFields(aggvos, new String[] { AccruedVO.BILLSTATUS, AccruedVO.APPRSTATUS });
		return aggvos;
	}

	public AccruedVO updatePrintInfo(AccruedVO vo) throws BusinessException {
		// 加锁
		ErLockUtil.lockVOByPk(ErmAccruedBillConst.Accrued_Lock_Key, vo);
		// 版本校验
		BDVersionValidationUtil.validateVersion(vo);
		// 更新打印信息
		getDAO().updateVOsByFields(new AccruedVO[] { vo }, new String[] { AccruedVO.PRINTER, AccruedVO.PRINTDATE });
		return vo;
	}

	private void createBillNo(AggAccruedBillVO aggvo) throws BusinessException {
		FinanceBillCodeInfo info = new FinanceBillCodeInfo(AccruedVO.PK_BILLTYPE, AccruedVO.BILLNO, AccruedVO.PK_GROUP,
				AccruedVO.PK_ORG, AccruedVO.getDefaultTableName());
		FinanceBillCodeUtils util = new FinanceBillCodeUtils(info);
		util.createBillCode(new AggregatedValueObject[] { aggvo });
	}

	/**
	 * 退换单据号
	 *
	 * @param aggAccruedBillVOs
	 */
	private void returnBillno(AggAccruedBillVO[] aggvos) {
		FinanceBillCodeInfo info = new FinanceBillCodeInfo(AccruedVO.PK_BILLTYPE, AccruedVO.BILLNO, AccruedVO.PK_GROUP,
				AccruedVO.PK_ORG, AccruedVO.getDefaultTableName());
		FinanceBillCodeUtils util = new FinanceBillCodeUtils(info);
		util.returnBillCode(aggvos);
	}

	/**
	 * 审批流中回写审批流信息状态
	 */
	@Override
	public void callCheckStatus(CheckStatusCallbackContext cscc) throws BusinessException {
		try {
			AggAccruedBillVO updateVO = (AggAccruedBillVO) cscc.getBillVo();
			// 加锁
			pklockOperate(updateVO);
			// 版本校验
			BDVersionValidationUtil.validateVersion(updateVO);

			// 查询oldvos
			AggAccruedBillVO[] oldvos = queryOldVOsByVOs(updateVO);
			if (oldvos != null && oldvos.length > 0) {
				// 保留下来oldvo，供业务处理的事件前使用
				updateVO.setOldvo(oldvos[0]);
			}

			// 数据更新到数据库
			getDAO().updateAggVOsByHeadFields(new AggAccruedBillVO[] { updateVO },
					new String[] { AccruedVO.APPRSTATUS, AccruedVO.APPROVER, AccruedVO.APPROVETIME });
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
		}
	}

	protected void fireBeforeInsertEvent(AggAccruedBillVO... aggvos) throws BusinessException {
		List<AggAccruedBillVO> listVOs = new ArrayList<AggAccruedBillVO>();
		for (AggAccruedBillVO vo : aggvos) {
			if (isEffectVo(vo)) {
				listVOs.add(vo);
			}
		}

		EventDispatcher.fireEvent(new ErmBusinessEvent(ErmAccruedBillConst.AccruedBill_MDID,
				ErmEventType.TYPE_INSERT_BEFORE, listVOs.toArray(new AggAccruedBillVO[] {})));
	}

	protected void fireAfterInsertEvent(AggAccruedBillVO... aggvos) throws BusinessException {
		List<AggAccruedBillVO> listVOs = new ArrayList<AggAccruedBillVO>();
		for (AggAccruedBillVO vo : aggvos) {
			if (isEffectVo(vo)) {
				listVOs.add(vo);
			}
		}
		EventDispatcher.fireEvent(new ErmBusinessEvent(ErmAccruedBillConst.AccruedBill_MDID,
				ErmEventType.TYPE_INSERT_AFTER, listVOs.toArray(new AggAccruedBillVO[listVOs.size()])));
	}

	protected void fireAfterUpdateEvent(AggAccruedBillVO[] aggvos, AggAccruedBillVO[] oldaggvos)
			throws BusinessException {
		for (AggAccruedBillVO aggvo : aggvos) {
			if (!isEffectVo(aggvo)) {
				return;
			}
		}
		EventDispatcher.fireEvent(new ErmBusinessEvent(ErmAccruedBillConst.AccruedBill_MDID,
				ErmEventType.TYPE_UPDATE_AFTER, aggvos, oldaggvos));
	}

	protected void fireBeforeUpdateEvent(AggAccruedBillVO[] aggvos, AggAccruedBillVO[] oldaggvos)
			throws BusinessException {
		for (AggAccruedBillVO aggvo : aggvos) {
			if (!isEffectVo(aggvo)) {
				return;
			}
		}
		EventDispatcher.fireEvent(new ErmBusinessEvent(ErmAccruedBillConst.AccruedBill_MDID,
				ErmEventType.TYPE_UPDATE_BEFORE, aggvos, oldaggvos));
	}

	protected void fireBeforeDeleteEvent(AggAccruedBillVO... aggvos) throws BusinessException {
		EventDispatcher.fireEvent(new ErmBusinessEvent(ErmAccruedBillConst.AccruedBill_MDID,
				ErmEventType.TYPE_DELETE_BEFORE, aggvos));
	}

	protected void fireAfterDeleteEvent(AggAccruedBillVO... aggvos) throws BusinessException {
		EventDispatcher.fireEvent(new ErmBusinessEvent(ErmAccruedBillConst.AccruedBill_MDID,
				ErmEventType.TYPE_DELETE_AFTER, aggvos));
	}
	
	protected void fireBeforeInvalidEvent(AggAccruedBillVO... aggvos) throws BusinessException {
		EventDispatcher.fireEvent(new ErmBusinessEvent(ErmAccruedBillConst.AccruedBill_MDID,
				ErmEventType.TYPE_INVALID_BEFORE, aggvos));
	}

	protected void fireAfterInvalidEvent(AggAccruedBillVO... aggvos) throws BusinessException {
		EventDispatcher.fireEvent(new ErmBusinessEvent(ErmAccruedBillConst.AccruedBill_MDID,
				ErmEventType.TYPE_INVALID_AFTER, aggvos));
	}

	protected void fireBeforeApproveEvent(AggAccruedBillVO... aggvos) throws BusinessException {
		EventDispatcher.fireEvent(new ErmBusinessEvent(ErmAccruedBillConst.AccruedBill_MDID,
				ErmEventType.TYPE_SIGN_BEFORE, aggvos));
	}

	protected void fireAfterApproveEvent(AggAccruedBillVO... aggvos) throws BusinessException {
		EventDispatcher.fireEvent(new ErmBusinessEvent(ErmAccruedBillConst.AccruedBill_MDID,
				ErmEventType.TYPE_SIGN_AFTER, aggvos));
	}

	protected void fireBeforeRedbackEvent(AggAccruedBillVO... aggvos) throws BusinessException {
		EventDispatcher.fireEvent(new ErmBusinessEvent(ErmAccruedBillConst.AccruedBill_MDID,
				ErmEventType.TYPE_REDBACK_BEFORE, aggvos));
	}

	protected void fireAfterRedbackEvent(AggAccruedBillVO... aggvos) throws BusinessException {
		EventDispatcher.fireEvent(new ErmBusinessEvent(ErmAccruedBillConst.AccruedBill_MDID,
				ErmEventType.TYPE_REDBACK_AFTER, aggvos));
	}

	protected void fireBeforeUnRedbackEvent(AggAccruedBillVO... aggvos) throws BusinessException {
		EventDispatcher.fireEvent(new ErmBusinessEvent(ErmAccruedBillConst.AccruedBill_MDID,
				ErmEventType.TYPE_UNREDBACK_BEFORE, aggvos));
	}

	protected void fireAfterUnRedbackEvent(AggAccruedBillVO... aggvos) throws BusinessException {
		EventDispatcher.fireEvent(new ErmBusinessEvent(ErmAccruedBillConst.AccruedBill_MDID,
				ErmEventType.TYPE_UNREDBACK_AFTER, aggvos));
	}

	protected void fireBeforeUnApproveEvent(AggAccruedBillVO... aggvos) throws BusinessException {
		EventDispatcher.fireEvent(new ErmBusinessEvent(ErmAccruedBillConst.AccruedBill_MDID,
				ErmEventType.TYPE_UNSIGN_BEFORE, aggvos));
	}

	protected void fireAfterUnApproveEvent(AggAccruedBillVO... aggvos) throws BusinessException {
		EventDispatcher.fireEvent(new ErmBusinessEvent(ErmAccruedBillConst.AccruedBill_MDID,
				ErmEventType.TYPE_UNSIGN_AFTER, aggvos));
	}

	private boolean isEffectVo(AggAccruedBillVO aggvo) {
		if (aggvo.getParentVO().getBillstatus().equals(ErmAccruedBillConst.BILLSTATUS_TEMPSAVED)) {
			return false;
		}
		return true;
	}

	private AggAccruedBillVO[] queryOldVOsByVOs(AggAccruedBillVO... vos) throws BusinessException {
		String[] pks = new String[vos.length];
		for (int i = 0; i < vos.length; i++) {
			pks[i] = vos[i].getParentVO().getPrimaryKey();
		}
		// 查询返回结果
		IErmAccruedBillQuery qryService = NCLocator.getInstance().lookup(IErmAccruedBillQuery.class);
		return qryService.queryBillByPks(pks);
	}

	public Map<String, UFDateTime> getTsMapByPK(List<String> key, String tableName, String pk_field) throws DbException {
		return getDAO().getTsMap(key, tableName, pk_field);
	}


}
