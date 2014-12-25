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
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.pub.pf.CheckStatusCallbackContext;
import nc.bs.pub.pf.ICheckStatusCallback;
import nc.itf.uap.pf.IWorkflowMachine;
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
		// �޸ļ���
		pklockOperate(vo);

		// ��ѯ�޸�ǰ��vo
		IErmAccruedBillQuery qryService = NCLocator.getInstance().lookup(IErmAccruedBillQuery.class);

		AggAccruedBillVO oldvo = null;
		if (vo.getParentVO().getStatus() != VOStatus.NEW) {
			oldvo = qryService.queryBillByPk(vo.getParentVO().getPrimaryKey());
			// ����children��Ϣ����ǰ̨�������ĵ�ֻ�Ǹı��children��
			if (oldvo != null) {
				fillUpChildren(vo, oldvo);
			}
		}

		// ��ȡ���ݺ�
		createBillNo(vo);

		prepareVoValue(vo);

		// �����ݴ�״̬
		vo.getParentVO().setAttributeValue(AccruedVO.BILLSTATUS, ErmAccruedBillConst.BILLSTATUS_TEMPSAVED);

		// ���������Ϣ
		if (vo.getParentVO().getPk_accrued_bill() != null) {
			AuditInfoUtil.updateData(vo.getParentVO());
		} else {
			AuditInfoUtil.addData(vo.getParentVO());
		}
		// ���±���
		vo = getDAO().updateVO(vo);

		// ����
		return vo;
	}

	public AggAccruedBillVO invalidBill(AggAccruedBillVO aggvo)throws BusinessException{
		if (aggvo == null) {
			return null;
		}
		// ɾ������
		pklockOperate(aggvo);
		// �汾У��
		BDVersionValidationUtil.validateVersion(aggvo);

		AccruedBillVOChecker vochecker = new AccruedBillVOChecker();
		vochecker.checkInvalid(aggvo);

		// ����ǰ�¼�����
		fireBeforeInvalidEvent(aggvo);

		//����״̬
		aggvo.getParentVO().setBillstatus(BXStatusConst.DJZT_Invalid);
		// ȡ�������¼���Ϊ�޸�ʱ��
		AuditInfoUtil.updateData(aggvo.getParentVO());
		new BaseDAO().updateVOArray(new AccruedVO[]{aggvo.getParentVO()} , new String[] { MatterAppVO.BILLSTATUS, MatterAppVO.MODIFIER, MatterAppVO.MODIFIEDTIME });

		// ���Ϻ��¼�����
		fireAfterInvalidEvent(aggvo);
		
		// ɾ��������
		NCLocator.getInstance().lookup(IWorkflowMachine.class)
				.deleteCheckFlow(aggvo.getParentVO().getPk_tradetype(), aggvo.getParentVO().getPrimaryKey(), aggvo, InvocationInfoProxy.getInstance().getUserId());
		return aggvo;
	}

	public AggAccruedBillVO insertVO(AggAccruedBillVO aggvo) throws BusinessException {
		prepareVoValue(aggvo);
		new AccruedBillVOChecker().checkBackSave(aggvo);
		AggAccruedBillVO result = null;
		try {
			// ��ȡ���ݺ�
			createBillNo(aggvo);
			// ���������Ϣ
			AuditInfoUtil.addData(aggvo.getParentVO());
			// ����ǰ�¼�����
			fireBeforeInsertEvent(aggvo);
			// ��������
			result = getDAO().insertVO(aggvo);
			// �������¼�����
			fireAfterInsertEvent(aggvo);

			// ��ѯ���ؽ��
			IErmAccruedBillQuery qryService = NCLocator.getInstance().lookup(IErmAccruedBillQuery.class);
			result = qryService.queryBillByPk(aggvo.getParentVO().getPrimaryKey());

		} catch (Exception e) {
			if(aggvo.getParentVO().getBillno() != null){
				returnBillno(new AggAccruedBillVO[] { aggvo });
			}
			ExceptionHandler.handleException(e);
		}

		result.getParentVO().setHasntbcheck(UFBoolean.FALSE);
		// ����
		return result;
	}

	public void deleteVOs(AggAccruedBillVO[] aggvos) throws BusinessException {
		if (aggvos == null || aggvos.length == 0) {
			return;
		}

		retriveItems(aggvos);
		// ɾ������
		pklockOperate(aggvos);
		// �汾У��
		BDVersionValidationUtil.validateVersion(aggvos);
		// ɾ������У��
		// deleteValidate(aggvos);
		// voУ��
		AccruedBillVOChecker vochecker = new AccruedBillVOChecker();
		vochecker.checkDelete(aggvos);
		// ɾ��ǰ�¼�����
		fireBeforeDeleteEvent(aggvos);
		// ɾ������
		getDAO().deleteVOs(aggvos);
		// �޸ĺ��¼�����
		fireAfterDeleteEvent(aggvos);
		// �˻����ݺ�
		returnBillno(aggvos);
		
		// ɾ��������
		NCLocator.getInstance().lookup(IWorkflowMachine.class)
				.deleteCheckFlow(aggvos[0].getParentVO().getPk_tradetype(), aggvos[0].getParentVO().getPrimaryKey(), aggvos[0], InvocationInfoProxy.getInstance().getUserId());
		
		// ��¼ҵ����־
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
		// �޸ļ���
		pklockOperate(aggvo);
		// �汾У��
		BDVersionValidationUtil.validateVersion(aggvo.getParentVO());

		// ��ѯ�޸�ǰ��vo
		IErmAccruedBillQuery qryService = NCLocator.getInstance().lookup(IErmAccruedBillQuery.class);
		AggAccruedBillVO oldaggvo = qryService.queryBillByPk(aggvo.getParentVO().getPrimaryKey());

		// ����children��Ϣ����ǰ̨�������ĵ�ֻ�Ǹı��children��
		fillUpChildren(aggvo, oldaggvo);

		// ���������Ϣ
		AuditInfoUtil.updateData(aggvo.getParentVO());
		// ��ȡ���ݺ�
		createBillNo(aggvo);
		// ��������
		prepareVoValue(aggvo);
		// voУ��
		AccruedBillVOChecker vochecker = new AccruedBillVOChecker();
		vochecker.checkBackUpdateSave(aggvo);
		// �޸�ǰ�¼�����
		fireBeforeUpdateEvent(new AggAccruedBillVO[] { aggvo }, new AggAccruedBillVO[] { oldaggvo });
		// ���±���
		aggvo = getDAO().updateVO(aggvo);
		// �޸ĺ��¼�����
		fireAfterUpdateEvent(new AggAccruedBillVO[] { aggvo }, new AggAccruedBillVO[] { oldaggvo });
		// ��ѯ���ؽ��
		aggvo = qryService.queryBillByPk(aggvo.getParentVO().getPrimaryKey());
		// ��¼ҵ����־
		BusiLogUtil.insertSmartBusiLog(ErmAccruedBillConst.ACCRUED_MD_UPDATE_OPER, new AggAccruedBillVO[] { aggvo },
				new AggAccruedBillVO[] { oldaggvo });

		aggvo.getParentVO().setHasntbcheck(UFBoolean.FALSE);

		return aggvo;
	}

	public AggAccruedBillVO updateVOBillStatus(AggAccruedBillVO updateVO) throws BusinessException {
		// ����
		pklockOperate(updateVO);
		// �汾У��
		BDVersionValidationUtil.validateVersion(updateVO);

		// ���õ���״̬
		int billstatus = ErmAccruedBillUtils.getBillStatus(updateVO.getParentVO().getApprstatus());
		updateVO.getParentVO().setBillstatus(billstatus);

		// ���ݸ��µ����ݿ�
		getDAO().updateAggVOsByHeadFields(new AggAccruedBillVO[] { updateVO }, new String[] { AccruedVO.BILLSTATUS });

		return updateVO;
	}

	/**
	 * �������޸ģ��󱣴����ݣ���̨�������ݣ����=���=Ԥ�����
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
			// �ý���Ԥ��������ԭ�����ݿ��ܴ�portal�˴�������û��������������
			detailvo.setPredict_rest_amount(detailvo.getAmount());
			detailvo.setRest_amount(detailvo.getAmount());
			detailvo.setOrg_rest_amount(detailvo.getOrg_amount());
			detailvo.setGroup_rest_amount(detailvo.getGroup_amount());
			detailvo.setGlobal_rest_amount(detailvo.getGlobal_amount());
		}
	}


	public AggAccruedBillVO unRedbackVO(AggAccruedBillVO aggvo) throws BusinessException {
		// �޸ļ���
		pklockOperate(aggvo);
		// �汾У��
		BDVersionValidationUtil.validateVersion(aggvo.getParentVO());

		//������Ϣ
		fillUpAggAccruedBillVO(aggvo);

		// voУ��
		new AccruedBillVOChecker().checkUnRedback(aggvo);

		// ɾ�����ǰ�¼�����
		fireBeforeUnRedbackEvent(aggvo);

		// ɾ����嵥��
		getDAO().deleteVOs(new AggAccruedBillVO[]{aggvo});

		// ɾ�������¼�����
		fireAfterUnRedbackEvent(aggvo);

		// ��дԭԤ�ᵥ
		unRedbackOldAccruedBill(aggvo);

		return aggvo;
	}

	private void fillUpAggAccruedBillVO(AggAccruedBillVO aggvo) throws BusinessException {
		// ��ѯoldvos
		AggAccruedBillVO[] oldvos = queryOldVOsByVOs(aggvo);
		if (oldvos != null && oldvos.length > 0) {
			// ��������oldvo����ҵ������¼�ǰʹ��
			aggvo.setOldvo(oldvos[0]);
		}
		retriveItems(new AggAccruedBillVO[]{aggvo});
	}

	private void unRedbackOldAccruedBill(AggAccruedBillVO aggvo) throws BusinessException {
		//��ѯԭԤ�ᵥ
		String src_accruedpk = aggvo.getChildrenVO()[0].getSrc_accruedpk();
		AggAccruedBillVO oldvo = NCLocator.getInstance().lookup(IErmAccruedBillQuery.class).queryBillByPk(src_accruedpk);

		AccruedVO parent = oldvo.getParentVO();
		// ɾ����嵥�ݺ�ԭԤ�ᵥ����־�ÿ�
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
		// ��дԭԤ�ᵥ
		getDAO().getBaseDAO().updateVO(oldvo.getParentVO());
		getDAO().getBaseDAO().updateVOArray(oldvo.getChildrenVO());

	}

	public AggAccruedBillVO redbackVO(AggAccruedBillVO aggvo) throws BusinessException {
		// �޸ļ���
		pklockOperate(aggvo);
		// �汾У��
		BDVersionValidationUtil.validateVersion(aggvo.getParentVO());
		// ���˵�û��������
		filtAggAccruedBillVO(aggvo);
		// ���У��
		new AccruedBillVOChecker().checkRedback(aggvo);
		AggAccruedBillVO redbackVO = getRedbackVO(aggvo);

		// ���ǰ�¼�����
		fireBeforeRedbackEvent(redbackVO);

		// �����嵥��
		getDAO().insertVO(redbackVO);

		//�����¼�����
		fireAfterRedbackEvent(redbackVO);

		// ��дԭԤ�ᵥ״̬�ͽ��
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
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("accruedbill_0","02011001-0009")/*@res "�����޿������,���ܽ��к�����"*/);
			}
			if (newChildren.size() != oldChildren.length) {
				aggvo.setChildrenVO(newChildren.toArray(new AccruedDetailVO[newChildren.size()]));
			}
		}else{
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("accruedbill_0","02011001-0010")/*@res "�����ޱ���,���ܽ��к�����"*/);
		}
	}

	private void redbackOldAccruedBill(AggAccruedBillVO aggvo) throws DAOException {
		// ���к���ԭԤ�ᵥ����ֶθ��£�ע���ʱaggvoֻ�ǿ��Խ��к���aggvo,�����ѹ��˵����ɺ����
		//����־-�����
		aggvo.getParentVO().setRedflag(ErmAccruedBillConst.REDFLAG_REDED);
		// ������ԭԤ�ᵥ��ͷ���Ϊ0�����屻��嵽�������ҲΪ0
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
		// �������ݱ�
		getDAO().getBaseDAO().updateVO(aggvo.getParentVO());
		getDAO().getBaseDAO().updateVOArray(aggvo.getChildrenVO());

	}

	private AggAccruedBillVO getRedbackVO(AggAccruedBillVO aggvo) throws BusinessException {
		AggAccruedBillVO redbackVO = (AggAccruedBillVO) aggvo.clone();
		//1.��ղ��ܸ��Ƶ���
		ErmAccruedBillUtils.clearRedbackFieldValue(redbackVO);
		//2.����Ĭ��ֵ
		setDefaultValue4redback(redbackVO,aggvo);
		return redbackVO;
	}

	/**
	 * �������Ĭ��ֵ
	 * @param redbackVO
	 * @throws BusinessException
	 */
	private void setDefaultValue4redback(AggAccruedBillVO redbackVO,AggAccruedBillVO aggvo) throws BusinessException {
		AccruedVO head = redbackVO.getParentVO();
		String userid = BXBsUtil.getBsLoginUser();
//		String pk_psndoc = BXBsUtil.getPk_psndoc(userid);
		// ehp3֮�󣬺��ֵ����ϵľ�������Ϣ�����ֵ����ϴ���
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

		head.setBillstatus(ErmMatterAppConst.BILLSTATUS_APPROVED );//������
		head.setEffectstatus(ErmMatterAppConst.EFFECTSTATUS_VALID );//����Ч
		head.setApprstatus(IBillStatus.CHECKPASS);//����ͨ��

		head.setStatus(VOStatus.NEW);
		createBillNo(redbackVO);
		// ����־-���
		head.setRedflag(ErmAccruedBillConst.REDFLAG_RED);

		for(AccruedDetailVO child : redbackVO.getChildrenVO()){
			// ע�����ĵ��ݱ����еĽ��ӦΪԭԤ�ᵥ�����ĸ���������ֱ��ȡ���ĸ���
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
		// ����ԭ�ҽ��ϼƵ���ͷ
		ErmAccruedBillUtils.sumBodyAmount2Head(redbackVO);

		// �ٸ��ݻ������¼�����֯�����š�ȫ�ֽ��
		ErmAccruedBillUtils.resetHeadAmounts(head);
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
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("accruedbill_0","02011001-0026")/*@res "�����ޱ���,���ܽ��к�����"*/);
			}
		}
		// �ý���Ԥ���������
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

			// ������ϸ
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
	 * ��������
	 *
	 * @param vos
	 * @throws BusinessException
	 */
	private void pklockOperate(AggAccruedBillVO... aggvos) throws BusinessException {
		// ��������
		ErLockUtil.lockAggVOByPk(ErmAccruedBillConst.Accrued_Lock_Key, aggvos);
	}

	/**
	 * �������
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

		// ����
		pklockOperate(aggvos);
		// �汾У��
		BDVersionValidationUtil.validateVersion(aggvos);
		
		//����������Ϣ
		for(AggAccruedBillVO aggVo : aggvos){
			if(aggVo.getParentVO().getApprovetime() == null){
				aggVo.getParentVO().setApprover(INCSystemUserConst.NC_USER_PK);
				aggVo.getParentVO().setApprovetime(AuditInfoUtil.getCurrentTime());
			}
		}
		
		// voУ��
		AccruedBillVOChecker vochecker = new AccruedBillVOChecker();
		vochecker.checkApprove(aggvos);
		for (int i = 0; i < aggvos.length; i++) {
			approveBack(aggvos[i]);
			msgs[i] = new MessageVO(aggvos[i], ActionUtils.AUDIT);
		}

		// ����
		return msgs;
	}

	private void approveBack(AggAccruedBillVO aggvo) throws BusinessException {
		// ���ǰ�¼�����
		fireBeforeApproveEvent(aggvo);

		// ������Ч��Ϣ������״̬
		setAccruedVOAttribute(new AggAccruedBillVO[] { aggvo }, AccruedVO.EFFECTSTATUS,
				ErmAccruedBillConst.EFFECTSTATUS_VALID);

		setAccruedVOAttribute(new AggAccruedBillVO[] { aggvo }, AccruedVO.BILLSTATUS,
				ErmAccruedBillConst.BILLSTATUS_APPROVED);

		// ���±���
		getDAO().updateAggVOsByHeadFields(
				new AggAccruedBillVO[] { aggvo },
				new String[] { AccruedVO.BILLSTATUS, AccruedVO.APPRSTATUS, AccruedVO.EFFECTSTATUS, AccruedVO.APPROVER,
						AccruedVO.APPROVETIME });

		// ��˺��¼�����
		fireAfterApproveEvent(aggvo);

		// �ָ�Ԥ����Ʊ��
		aggvo.getParentVO().setHasntbcheck(UFBoolean.FALSE);
	}

	public MessageVO[] unapproveVOs(AggAccruedBillVO[] aggvos) throws BusinessException {
		if (aggvos == null || aggvos.length == 0) {
			return null;
		}

		retriveItems(aggvos);
		
		MessageVO[] msgs = new MessageVO[aggvos.length];
		// ����
		pklockOperate(aggvos);
		// �汾У��
		BDVersionValidationUtil.validateVersion(aggvos);
		// voУ��
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
		
		// ȡ�����ǰ�¼�����
		fireBeforeUnApproveEvent(aggvo.getOldvo());
		
		//����������Ϣ
		if (aggvo.getParentVO().getApprover() == null 
				|| aggvo.getParentVO().getApprover().equals(INCSystemUserConst.NC_USER_PK)) {
			aggvo.getParentVO().setApprover(null);
			aggvo.getParentVO().setApprovetime(null);
		}
		
		// ���õ���״̬����Ч��Ϣ
		setAccruedVOAttribute(new AggAccruedBillVO[] { aggvo }, AccruedVO.EFFECTSTATUS,
				ErmMatterAppConst.EFFECTSTATUS_NO);

		setAccruedVOAttribute(new AggAccruedBillVO[] { aggvo }, AccruedVO.BILLSTATUS,
				ErmAccruedBillConst.BILLSTATUS_SAVED);


		// ���±���
		getDAO().updateAggVOsByHeadFields(
				new AggAccruedBillVO[] { aggvo },
				new String[] { AccruedVO.APPRSTATUS, AccruedVO.EFFECTSTATUS, AccruedVO.BILLSTATUS,
						AccruedVO.APPROVER, AccruedVO.APPROVETIME });

		// ȡ����˺��¼�����
		fireAfterUnApproveEvent(aggvo);

		//�ָ�Ԥ����Ʊ��
		aggvo.getParentVO().setHasntbcheck(UFBoolean.FALSE);
	}

	public AggAccruedBillVO[] commitVOs(AggAccruedBillVO[] aggvos) throws BusinessException {
		if (aggvos == null || aggvos.length == 0) {
			return null;
		}

		retriveItems(aggvos);
		// ����
		pklockOperate(aggvos);
		// �汾У��
		BDVersionValidationUtil.validateVersion(aggvos);
		// voУ��
		AccruedBillVOChecker vochecker = new AccruedBillVOChecker();
		vochecker.checkCommit(aggvos);

		// �����ύ���״̬
		setAccruedVOAttribute(aggvos, AccruedVO.BILLSTATUS, Integer.valueOf(ErmAccruedBillConst.BILLSTATUS_SAVED));
		setAccruedVOAttribute(aggvos, AccruedVO.APPRSTATUS, Integer.valueOf(IBillStatus.COMMIT));

		// ���±���
		getDAO().updateAggVOsByHeadFields(aggvos, new String[] { AccruedVO.BILLSTATUS, AccruedVO.APPRSTATUS });

		// ����
		return aggvos;
	}

	/**
	 * ����Ԥ�ᵥ��������ֵ
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
		// ����
		pklockOperate(aggvos);
		// �汾У��
		BDVersionValidationUtil.validateVersion(aggvos);
		// voУ��
		AccruedBillVOChecker vochecker = new AccruedBillVOChecker();
		vochecker.checkRecall(aggvos);

		// ���ñ�ͷ�ջ������Ϣ
		setAccruedVOAttribute(aggvos, AccruedVO.BILLSTATUS, Integer.valueOf(ErmAccruedBillConst.BILLSTATUS_SAVED));

		// ���±���
		getDAO().updateAggVOsByHeadFields(aggvos, new String[] { AccruedVO.BILLSTATUS, AccruedVO.APPRSTATUS });
		return aggvos;
	}

	public AccruedVO updatePrintInfo(AccruedVO vo) throws BusinessException {
		// ����
		ErLockUtil.lockVOByPk(ErmAccruedBillConst.Accrued_Lock_Key, vo);
		// �汾У��
		BDVersionValidationUtil.validateVersion(vo);
		// ���´�ӡ��Ϣ
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
	 * �˻����ݺ�
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
	 * �������л�д��������Ϣ״̬
	 */
	@Override
	public void callCheckStatus(CheckStatusCallbackContext cscc) throws BusinessException {
		try {
			AggAccruedBillVO updateVO = (AggAccruedBillVO) cscc.getBillVo();
			// ����
			pklockOperate(updateVO);
			// �汾У��
			BDVersionValidationUtil.validateVersion(updateVO);

			// ��ѯoldvos
			AggAccruedBillVO[] oldvos = queryOldVOsByVOs(updateVO);
			if (oldvos != null && oldvos.length > 0) {
				// ��������oldvo����ҵ������¼�ǰʹ��
				updateVO.setOldvo(oldvos[0]);
			}

			// ���ݸ��µ����ݿ�
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
		// ��ѯ���ؽ��
		IErmAccruedBillQuery qryService = NCLocator.getInstance().lookup(IErmAccruedBillQuery.class);
		return qryService.queryBillByPks(pks);
	}

}
