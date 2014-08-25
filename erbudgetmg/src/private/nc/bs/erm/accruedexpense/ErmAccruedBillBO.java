package nc.bs.erm.accruedexpense;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import nc.bs.arap.bx.BusiLogUtil;
import nc.bs.businessevent.EventDispatcher;
import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.er.util.BXBsUtil;
import nc.bs.erm.accruedexpense.check.AccruedBillVOChecker;
import nc.bs.erm.accruedexpense.common.ErmAccruedBillConst;
import nc.bs.erm.accruedexpense.common.ErmAccruedBillUtils;
import nc.bs.erm.event.ErmBusinessEvent;
import nc.bs.erm.event.ErmEventType;
import nc.bs.erm.matterapp.common.ErmMatterAppConst;
import nc.bs.erm.util.ErLockUtil;
import nc.bs.framework.common.NCLocator;
import nc.bs.pub.pf.CheckStatusCallbackContext;
import nc.bs.pub.pf.ICheckStatusCallback;
import nc.pubitf.erm.accruedexpense.IErmAccruedBillQuery;
import nc.vo.arap.bx.util.ActionUtils;
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

		// 删除前事件处理
		fireBeforeDeleteEvent(aggvo);

		//作废状态
		aggvo.getParentVO().setBillstatus(ErmAccruedBillConst.BILLSTATUS_INVALID);
		// 取服务器事件作为修改时间
		AuditInfoUtil.updateData(aggvo.getParentVO());
		new BaseDAO().updateVOArray(new AccruedVO[]{aggvo.getParentVO()} , new String[] { MatterAppVO.BILLSTATUS, MatterAppVO.MODIFIER, MatterAppVO.MODIFIEDTIME });

		// 修改后事件处理
		fireAfterDeleteEvent(aggvo);

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
	}

	private void unRedbackOldAccruedBill(AggAccruedBillVO aggvo) throws BusinessException {
		//查询原预提单
		String src_accruedpk = aggvo.getChildrenVO()[0].getSrc_accruedpk();
		AggAccruedBillVO oldvo = NCLocator.getInstance().lookup(IErmAccruedBillQuery.class).queryBillByPk(src_accruedpk);

		AccruedVO parent = oldvo.getParentVO();
		// 删除红冲单据后，原预提单红冲标志置空
		parent.setRedflag(ErmAccruedBillConst.REDFLAG_NO);
		parent.setRest_amount(parent.getAmount().sub(parent.getVerify_amount()));
		parent.setOrg_rest_amount(parent.getOrg_amount().sub(parent.getOrg_verify_amount()));
		parent.setGroup_rest_amount(parent.getGroup_amount().sub(parent.getGroup_verify_amount()));
		parent.setGlobal_rest_amount(parent.getGlobal_amount().sub(parent.getGlobal_verify_amount()));
		parent.setPredict_rest_amount(parent.getRest_amount());

		for(AccruedDetailVO child : oldvo.getChildrenVO()){
			child.setRest_amount(child.getAmount().sub(child.getVerify_amount()));
			child.setOrg_rest_amount(child.getOrg_amount().sub(child.getOrg_verify_amount()));
			child.setGroup_rest_amount(child.getGroup_amount().sub(child.getGroup_verify_amount()));
			child.setGlobal_rest_amount(child.getGlobal_amount().sub(child.getGlobal_verify_amount()));
			child.setPredict_rest_amount(child.getRest_amount());
		}
		// 回写原预提单
		getDAO().getBaseDAO().updateVO(oldvo.getParentVO());
		getDAO().getBaseDAO().updateVOArray(oldvo.getChildrenVO());

	}

	public AggAccruedBillVO redbackVO(AggAccruedBillVO aggvo) throws BusinessException {
		// 修改加锁
		pklockOperate(aggvo);
		// 版本校验
		BDVersionValidationUtil.validateVersion(aggvo.getParentVO());
		// 过滤掉没有余额的行
		filtAggAccruedBillVO(aggvo);
		// 红冲校验
		new AccruedBillVOChecker().checkRedback(aggvo);
		AggAccruedBillVO redbackVO = getRedbackVO(aggvo);

		// 红冲前事件处理
		fireBeforeRedbackEvent(redbackVO);

		// 插入红冲单据
		getDAO().insertVO(redbackVO);

		//红冲后事件处理
		fireAfterRedbackEvent(redbackVO);

		// 回写原预提单状态和金额
		redbackOldAccruedBill(aggvo);

		redbackVO.getParentVO().setHasntbcheck(UFBoolean.FALSE);

		return redbackVO;
	}

	private void filtAggAccruedBillVO(AggAccruedBillVO aggvo) throws BusinessException{
		AccruedDetailVO[] oldChildren = aggvo.getChildrenVO();
		List<AccruedDetailVO> newChildren = new ArrayList<AccruedDetailVO>();
		if (oldChildren != null && oldChildren.length > 0) {
			for (AccruedDetailVO child : oldChildren) {
				if (child.getRest_amount().compareTo(UFDouble.ZERO_DBL) > 0) {
					newChildren.add(child);
				}
			}
			if (newChildren.size() == 0) {
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("accruedbill_0","02011001-0009")/*@res "单据无可用余额,不能进行红冲操作"*/);
			}
			if (newChildren.size() != oldChildren.length) {
				aggvo.setChildrenVO(newChildren.toArray(new AccruedDetailVO[newChildren.size()]));
			}
		}else{
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("accruedbill_0","02011001-0010")/*@res "单据无表体,不能进行红冲操作"*/);
		}
	}

	private void redbackOldAccruedBill(AggAccruedBillVO aggvo) throws DAOException {
		// 进行红冲的原预提单相关字段更新，注意此时aggvo只是可以进行红冲的aggvo,表体已过滤掉不可红冲行
		//红冲标志-被红冲
		aggvo.getParentVO().setRedflag(ErmAccruedBillConst.REDFLAG_REDED);
		// 红冲完后，原预提单表头余额为0，表体被红冲到的行余额也为0
		aggvo.getParentVO().setRest_amount(UFDouble.ZERO_DBL);
		aggvo.getParentVO().setOrg_rest_amount(UFDouble.ZERO_DBL);
		aggvo.getParentVO().setGroup_rest_amount(UFDouble.ZERO_DBL);
		aggvo.getParentVO().setGlobal_rest_amount(UFDouble.ZERO_DBL);
		aggvo.getParentVO().setPredict_rest_amount(UFDouble.ZERO_DBL);

		for (AccruedDetailVO child : aggvo.getChildrenVO()) {
			child.setRest_amount(UFDouble.ZERO_DBL);
			child.setOrg_rest_amount(UFDouble.ZERO_DBL);
			child.setGroup_rest_amount(UFDouble.ZERO_DBL);
			child.setGlobal_rest_amount(UFDouble.ZERO_DBL);
			child.setPredict_rest_amount(UFDouble.ZERO_DBL);
		}
		// 更新数据表
		getDAO().getBaseDAO().updateVO(aggvo.getParentVO());
		getDAO().getBaseDAO().updateVOArray(aggvo.getChildrenVO());

	}

	private AggAccruedBillVO getRedbackVO(AggAccruedBillVO aggvo) throws BusinessException {
		AggAccruedBillVO redbackVO = (AggAccruedBillVO) aggvo.clone();
		//1.清空不能复制的项
		ErmAccruedBillUtils.clearRedbackFieldValue(redbackVO);
		//2.设置默认值
		setDefaultValue4redback(redbackVO,aggvo);
		return redbackVO;
	}

	/**
	 * 红冲设置默认值
	 * @param redbackVO
	 * @throws BusinessException
	 */
	private void setDefaultValue4redback(AggAccruedBillVO redbackVO,AggAccruedBillVO aggvo) throws BusinessException {
		AccruedVO head = redbackVO.getParentVO();
		String userid = BXBsUtil.getBsLoginUser();
//		String pk_psndoc = BXBsUtil.getPk_psndoc(userid);
		// ehp3之后，红字单据上的经办人信息从蓝字单据上带出
//		head.setOperator(pk_psndoc);
//		head.setOperator_dept(BXBsUtil.getPsnPk_dept(pk_psndoc));
//		head.setOperator_org(BXBsUtil.getPsnPk_org(pk_psndoc));

		head.setRest_amount(UFDouble.ZERO_DBL);
		head.setOrg_rest_amount(UFDouble.ZERO_DBL);
		head.setGroup_rest_amount(UFDouble.ZERO_DBL);
		head.setGlobal_rest_amount(UFDouble.ZERO_DBL);
		head.setOrg_verify_amount(UFDouble.ZERO_DBL);
		head.setGroup_verify_amount(UFDouble.ZERO_DBL);
		head.setGlobal_verify_amount(UFDouble.ZERO_DBL);
		head.setVerify_amount(UFDouble.ZERO_DBL);
		head.setPredict_rest_amount(UFDouble.ZERO_DBL);

		UFDateTime currTime = BXBsUtil.getBsLoginDate();
		head.setCreationtime(currTime);
		head.setApprovetime(currTime);
		head.setBilldate(currTime.getDate());

		head.setCreator(userid);
		head.setApprover(userid);

		head.setBillstatus(ErmMatterAppConst.BILLSTATUS_APPROVED );//已审批
		head.setEffectstatus(ErmMatterAppConst.EFFECTSTATUS_VALID );//已生效
		head.setApprstatus(IBillStatus.CHECKPASS);//审批通过

		head.setStatus(VOStatus.NEW);
		createBillNo(redbackVO);
		// 红冲标志-红冲
		head.setRedflag(ErmAccruedBillConst.REDFLAG_RED);

		for(AccruedDetailVO child : redbackVO.getChildrenVO()){
			// 注意红冲后的单据表体行的金额应为原预提单行余额的负数，而非直接取金额的负数
			child.setAmount(new UFDouble("-1").multiply(child.getRest_amount()));
			child.setOrg_amount(new UFDouble("-1").multiply(child.getOrg_rest_amount()));
			child.setGroup_amount(new UFDouble("-1").multiply(child.getGroup_rest_amount()));
			child.setGlobal_amount(new UFDouble("-1").multiply(child.getGlobal_rest_amount()));
			child.setRest_amount(UFDouble.ZERO_DBL);
			child.setOrg_rest_amount(UFDouble.ZERO_DBL);
			child.setGroup_rest_amount(UFDouble.ZERO_DBL);
			child.setGlobal_rest_amount(UFDouble.ZERO_DBL);
			child.setVerify_amount(UFDouble.ZERO_DBL);
			child.setOrg_verify_amount(UFDouble.ZERO_DBL);
			child.setGroup_verify_amount(UFDouble.ZERO_DBL);
			child.setGlobal_verify_amount(UFDouble.ZERO_DBL);
			child.setPredict_rest_amount(UFDouble.ZERO_DBL);

			child.setSrctype(aggvo.getParentVO().getPk_billtype());
			child.setSrc_accruedpk(aggvo.getParentVO().getPk_accrued_bill());
			child.setStatus(VOStatus.NEW);
		}
		// 表体原币金额合计到表头
		ErmAccruedBillUtils.sumBodyAmount2Head(redbackVO);

		// 再根据汇率重新计算组织、集团、全局金额
		ErmAccruedBillUtils.resetHeadAmounts(head);
	}

	private void prepareHeader(AccruedVO parentVo) throws BusinessException {
		if (parentVo.getApprstatus() != IBillStatus.FREE) {
			parentVo.setBillstatus(ErmAccruedBillUtils.getBillStatus(parentVo.getApprstatus()));
		}

		parentVo.setPk_billtype(ErmAccruedBillConst.AccruedBill_Billtype);
		if (parentVo.getOperator() != null) {
			String auditUser = BXBsUtil.getCuserIdByPK_psndoc(parentVo.getOperator());
			if(auditUser != null){
				parentVo.setAuditman(auditUser);
			}else {
				throw new BusinessException("经办人未关联用户！");
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

}