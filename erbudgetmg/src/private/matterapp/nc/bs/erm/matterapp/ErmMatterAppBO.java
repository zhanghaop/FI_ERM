package nc.bs.erm.matterapp;

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
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.pub.pf.CheckStatusCallbackContext;
import nc.bs.pub.pf.ICheckStatusCallback;
import nc.md.persist.framework.IMDPersistenceQueryService;
import nc.md.persist.framework.MDPersistenceService;
import nc.pubitf.erm.matterapp.IErmMatterAppBillQuery;
import nc.vo.arap.bx.util.ActionUtils;
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
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.trade.pub.IBillStatus;
import nc.vo.uap.rbac.constant.INCSystemUserConst;
import nc.vo.util.AuditInfoUtil;
import nc.vo.util.BDReferenceChecker;
import nc.vo.util.BDVersionValidationUtil;

import org.apache.commons.lang.ArrayUtils;

/**
 * ����������ҵ����
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
		// voУ��
		MatterAppVOChecker vochecker = new MatterAppVOChecker();
		vochecker.checkBackSave(vo);

		try {
			// ��ȡ���ݺ�
			createBillNo(vo);
			// ���������Ϣ
			AuditInfoUtil.addData(vo.getParentVO());
			// ����ǰ�¼�����
			fireBeforeInsertEvent(vo);
			// ��������
			result = getDAO().insertVO(vo);
			// �������¼�����
			fireAfterInsertEvent(vo);
		} catch (Exception e) {
			returnBillno(new AggMatterAppVO[] { vo });
			ExceptionHandler.handleException(e);
		}
		
		result.getParentVO().setHasntbcheck(UFBoolean.FALSE);
		// ����
		return result;
	}

	public AggMatterAppVO updateVO(AggMatterAppVO vo) throws BusinessException {
		// �޸ļ���
		pklockOperate(vo);
		// �汾У��
		BDVersionValidationUtil.validateVersion(vo.getParentVO());

		IErmMatterAppBillQuery qryService = NCLocator.getInstance().lookup(IErmMatterAppBillQuery.class);
		AggMatterAppVO oldvo = qryService.queryBillByPK(vo.getParentVO().getPrimaryKey());
		
		// ����children��Ϣ����ǰ̨�������ĵ�ֻ�Ǹı��children��
		fillUpChildren(vo, oldvo);

		// ���������Ϣ
		AuditInfoUtil.updateData(vo.getParentVO());
		// ��ȡ���ݺ�
		createBillNo(vo);
		//��������
		prepareVoValue(vo);
		// voУ��
		MatterAppVOChecker vochecker = new MatterAppVOChecker();
		vochecker.checkBackSave(vo);
		// �޸�ǰ�¼�����
		fireBeforeUpdateEvent(new AggMatterAppVO[] { vo }, new AggMatterAppVO[] { oldvo });
		// ���±���
		vo = getDAO().updateVO(vo);
		// �޸ĺ��¼�����
		fireAfterUpdateEvent(new AggMatterAppVO[] { vo }, new AggMatterAppVO[] { oldvo });
		// ��¼ҵ����־
		BusiLogUtil.insertSmartBusiLog(ErmMatterAppConst.MAPP_MD_UPDATE_OPER, new AggMatterAppVO[] { vo },
				new AggMatterAppVO[] { oldvo });
		
		vo.getParentVO().setHasntbcheck(UFBoolean.FALSE);
		// ����
		return vo;
	}

	/**
	 * ����children
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
		// ɾ������
		pklockOperate(vos);
		// �汾У��
		BDVersionValidationUtil.validateVersion(vos);
		// ɾ������У��
		deleteValidate(vos);
		// voУ��
		MatterAppVOChecker vochecker = new MatterAppVOChecker();
		vochecker.checkDelete(vos);
		// ɾ��ǰ�¼�����
		fireBeforeDeleteEvent(vos);
		// ɾ������
		getDAO().deleteVOs(vos);
		// �޸ĺ��¼�����
		fireAfterDeleteEvent(vos);
		// �˻����ݺ�
		returnBillno(vos);
		// ��¼ҵ����־
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
		// ����
		pklockOperate(vos);
		// �汾У��
		BDVersionValidationUtil.validateVersion(vos);
		// voУ��
		MatterAppVOChecker vochecker = new MatterAppVOChecker();
		vochecker.checkCommit(vos);

		// �����ύ���״̬
		setAggMatterAppVOAttribute(vos, MatterAppVO.BILLSTATUS, Integer.valueOf(ErmMatterAppConst.BILLSTATUS_COMMITED),
				true);
		setAggMatterAppVOAttribute(vos, MatterAppVO.APPRSTATUS, Integer.valueOf(IBillStatus.COMMIT), false);

		// ���±���
		getDAO().updateAggVOsByFields(vos, new String[] { MatterAppVO.BILLSTATUS, MatterAppVO.APPRSTATUS },
				new String[] { MtAppDetailVO.BILLSTATUS });

		// ����
		return vos;
	}

	public AggMatterAppVO[] recallVOs(AggMatterAppVO[] vos) throws BusinessException {
		if (vos == null || vos.length == 0) {
			return null;
		}

		retriveItems(vos);
		// ����
		pklockOperate(vos);
		// �汾У��
		BDVersionValidationUtil.validateVersion(vos);
		// voУ��
		MatterAppVOChecker vochecker = new MatterAppVOChecker();
		vochecker.checkRecall(vos);

		// �����ջ������Ϣ
		setAggMatterAppVOAttribute(vos, MatterAppVO.BILLSTATUS, Integer.valueOf(ErmMatterAppConst.BILLSTATUS_SAVED),
				true);

		// ���±���
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

		// ����
		pklockOperate(vos);
		// �汾У��
		BDVersionValidationUtil.validateVersion(vos);
		// voУ��
		MatterAppVOChecker vochecker = new MatterAppVOChecker();
		vochecker.checkApprove(vos);
		for (int i = 0; i < vos.length; i++) {
			approveBack(vos[i]);
			msgs[i] = new MessageVO(vos[i], ActionUtils.AUDIT);
		}

		// ����
		return msgs;
	}

	private void approveBack(AggMatterAppVO vo) throws BusinessException {
		// ���ǰ�¼�����
		fireBeforeApproveEvent(vo);

		// ������Ч��Ϣ������״̬
		setAggMatterAppVOAttribute(new AggMatterAppVO[] { vo }, MatterAppVO.EFFECTSTATUS,
				ErmMatterAppConst.EFFECTSTATUS_VALID, true);

		setAggMatterAppVOAttribute(new AggMatterAppVO[] { vo }, MatterAppVO.BILLSTATUS,
				ErmMatterAppConst.BILLSTATUS_APPROVED, true);
		
		setAggMatterAppVOAttribute(new AggMatterAppVO[] { vo }, MatterAppVO.APPROVETIME, vo.getParentVO()
				.getApprovetime(), true);//��������

		// ���±���
		getDAO().updateAggVOsByFields(
				new AggMatterAppVO[] {vo},
				new String[] { MatterAppVO.BILLSTATUS, MatterAppVO.APPRSTATUS, MatterAppVO.EFFECTSTATUS,
						MatterAppVO.APPROVER, MatterAppVO.APPROVETIME },
				new String[] { MtAppDetailVO.BILLSTATUS, MtAppDetailVO.EFFECTSTATUS });

		// ��˺��¼�����
		fireAfterApproveEvent(vo);
		//�ָ�Ԥ����Ʊ��
		vo.getParentVO().setHasntbcheck(UFBoolean.FALSE);
	}

	public MessageVO[] unapproveVOs(AggMatterAppVO[] vos) throws BusinessException {
		if (vos == null || vos.length == 0) {
			return null;
		}

		retriveItems(vos);

		MessageVO[] msgs = new MessageVO[vos.length];
		// ����
		pklockOperate(vos);
		// �汾У��
		BDVersionValidationUtil.validateVersion(vos);
		// voУ��
		MatterAppVOChecker vochecker = new MatterAppVOChecker();
		vochecker.checkunApprove(vos);

		// ��ѯoldvos
		AggMatterAppVO[] oldvos = queryOldVOsByVOs(vos);
		try {
			for (int i = 0; i < vos.length; i++) {
				unApproveBack(vos[i], oldvos[i]);
				msgs[i] = new MessageVO(vos[i], ActionUtils.UNAUDIT);
			}
		} catch (Exception e) {
			throw ExceptionHandler.handleException(e);
		}

		return msgs;
	}

	private void unApproveBack(AggMatterAppVO vo, AggMatterAppVO oldvo) throws BusinessException {
		// �Ƿ���ȡ����Ч����
		MatterAppVO parentvo = (MatterAppVO) vo.getParentVO();

		parentvo.setApprovetime(oldvo.getParentVO().getApprovetime());//����Ԥ�����Ϊ������ڵ������������ڲ��ܶ�
		// ȡ�����ǰ�¼�����
		fireBeforeUnApproveEvent(vo);

		// ���õ���״̬����Ч��Ϣ
		setAggMatterAppVOAttribute(new AggMatterAppVO[] { vo }, MatterAppVO.EFFECTSTATUS,
				ErmMatterAppConst.EFFECTSTATUS_NO, true);

		setAggMatterAppVOAttribute(new AggMatterAppVO[] { vo }, MatterAppVO.BILLSTATUS,
				ErmMatterAppConst.BILLSTATUS_COMMITED, true);

		// ���±���
		getDAO().updateAggVOsByFields(
				new AggMatterAppVO[] { vo },
				new String[] { MatterAppVO.APPRSTATUS, MatterAppVO.EFFECTSTATUS, MatterAppVO.BILLSTATUS,
						MatterAppVO.APPROVER, MatterAppVO.APPROVETIME },
				new String[] { MtAppDetailVO.BILLSTATUS, MtAppDetailVO.EFFECTSTATUS });
		
		// ȡ����˺��¼�����
		fireAfterUnApproveEvent(vo);
		
		//�ָ�Ԥ����Ʊ��
		vo.getParentVO().setHasntbcheck(UFBoolean.FALSE);
	}
	
	/**
	 * �رղ���
	 * @param vos
	 * @param isAuto �Ƿ��Զ��ر�
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
		
		// ����
		pklockOperate(vos);
		// �汾У��
		BDVersionValidationUtil.validateVersion(vos);

		// voУ��
		MatterAppVOChecker vochecker = new MatterAppVOChecker();
		vochecker.checkClose(vos);

		// �ر�ǰ�¼�����
		fireBeforeCloseEvent(isAuto, vos);

		// ����ǵ�ѡ�������ӱ������old������ͬ�������������Ϊ������ȡ���ر�
		boolean isAllClose = isAllClose(vos);

		// ���ñ�ͷ�ر���Ϣ
		UFDate closeDate = new UFDate(InvocationInfoProxy.getInstance().getBizDateTime());
		String closeman = isAuto ? INCSystemUserConst.NC_USER_PK : AuditInfoUtil.getCurrentUser();
		if (isAllClose) {
			for (int i = 0; i < vos.length; i++) {
				vos[i].getParentVO().setAttributeValue(MatterAppVO.CLOSE_STATUS, ErmMatterAppConst.CLOSESTATUS_Y);
				vos[i].getParentVO().setAttributeValue(MatterAppVO.CLOSEMAN, closeman);
				vos[i].getParentVO().setAttributeValue(MatterAppVO.CLOSEDATE, closeDate);
			}
		}
		// �����ӱ�ر���Ϣ
		for (int i = 0; i < vos.length; i++) {
			for (int j = 0; j < vos[i].getChildrenVO().length; j++) {
				if (!(ErmMatterAppConst.CLOSESTATUS_Y == vos[i].getChildrenVO()[j].getClose_status())) {
					vos[i].getChildrenVO()[j].setAttributeValue(MtAppDetailVO.CLOSE_STATUS,
							ErmMatterAppConst.CLOSESTATUS_Y);
					vos[i].getChildrenVO()[j].setAttributeValue(MtAppDetailVO.CLOSEMAN, closeman);
					vos[i].getChildrenVO()[j].setAttributeValue(MtAppDetailVO.CLOSEDATE, closeDate);
				}
			}
		}

		// ���±���
		getDAO().updateAggVOsByFields(vos,
				new String[] { MatterAppVO.CLOSE_STATUS, MatterAppVO.CLOSEMAN, MatterAppVO.CLOSEDATE },
				new String[] { MtAppDetailVO.CLOSE_STATUS, MtAppDetailVO.CLOSEMAN, MtAppDetailVO.CLOSEDATE });
		// �رպ��¼�����
		fireAfterCloseEvent(isAuto, vos);

		// �йر����ǰ̨���������ݲ�ȫ���貹��ȫ����������
		// ����
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

		prepareVoValue(vo);
		// �����ݴ�״̬
		setAggMatterAppVOAttribute(new AggMatterAppVO[] { vo }, MatterAppVO.BILLSTATUS,
				ErmMatterAppConst.BILLSTATUS_TEMPSAVED, true);

		// �޸ļ���
		pklockOperate(vo);
		// ��ȡ���ݺ�
		createBillNo(vo);
		// ���������Ϣ
		if (vo.getParentVO().getPk_mtapp_bill() != null) {
			AuditInfoUtil.updateData(vo.getParentVO());
		} else {
			AuditInfoUtil.addData(vo.getParentVO());
		}
		// ���±���
		vo = getDAO().updateVO(vo);
		// ����
		return vo;
	}

	public MatterAppVO updatePrintInfo(MatterAppVO vo) throws BusinessException {
		// ����
		pklockVO(vo);
		// �汾У��
		BDVersionValidationUtil.validateVersion(vo);

		// ���´�ӡ��Ϣ
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
		
		// ����
		pklockOperate(vos);
		// �汾У��
		BDVersionValidationUtil.validateVersion(vos);

		// voУ��
		MatterAppVOChecker vochecker = new MatterAppVOChecker();
		vochecker.checkunClose(vos);

		// ȡ���ر�ǰ�¼�����
		fireBeforeUnCloseEvent(isAuto, vos);

		// �����ӱ�ر���Ϣ
		for (int i = 0; i < vos.length; i++) {
			boolean openflag = false;
			
			List<String> effectDetails = getEffectOpenDetails(vos[i]);
			
			for (int j = 0; j < vos[i].getChildrenVO().length; j++) {
				MtAppDetailVO mtAppDetailVO = vos[i].getChildrenVO()[j];
				if (effectDetails.contains(mtAppDetailVO.getPk_mtapp_detail())) {//�����Զ��ر���
					mtAppDetailVO.setAttributeValue(MtAppDetailVO.CLOSE_STATUS, ErmMatterAppConst.CLOSESTATUS_N);
					mtAppDetailVO.setAttributeValue(MtAppDetailVO.CLOSEMAN, null);
					mtAppDetailVO.setAttributeValue(MtAppDetailVO.CLOSEDATE, null);
					openflag = true;
				}
			}
			
			if(openflag){
				// ���ñ�ͷ������Ϣ
				vos[i].getParentVO().setAttributeValue(MatterAppVO.CLOSE_STATUS, ErmMatterAppConst.CLOSESTATUS_N);
				vos[i].getParentVO().setAttributeValue(MatterAppVO.CLOSEMAN, null);
				vos[i].getParentVO().setAttributeValue(MatterAppVO.CLOSEDATE, null);
			}
		}

		// ���±���
		getDAO().updateAggVOsByFields(vos,
				new String[] { MatterAppVO.CLOSE_STATUS, MatterAppVO.CLOSEMAN, MatterAppVO.CLOSEDATE },
				new String[] { MtAppDetailVO.CLOSE_STATUS, MtAppDetailVO.CLOSEMAN, MtAppDetailVO.CLOSEDATE });
		// ȡ���رպ��¼�����
		fireAfterUnCloseEvent(isAuto, vos);

		return queryOldVOsByVOs(vos);
	}

	/**
	 * �ж��Ƿ�Ϊ�����رղ���
	 * 
	 * @param vos
	 * @param oldvos
	 * @return
	 * @throws BusinessException
	 */
	private boolean isAllClose(AggMatterAppVO[] vos) throws BusinessException {
		// �б�ҳ��ֻ�������ر�
		if (vos.length == 1) {
			MtAppDetailVO[] newchilds = vos[0].getChildrenVO();
			List<String> newchildpks = new ArrayList<String>();

			if (newchilds == null || newchilds.length == 0) {
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0",
						"0201212-0031")/* @res "û�пɲ����ķ������뵥������" */);
			}

			for (int i = 0; i < newchilds.length; i++) {
				if (newchilds[i].getBillstatus() == null
						|| newchilds[i].getClose_status() == ErmMatterAppConst.CLOSESTATUS_N) {
					newchildpks.add(newchilds[i].getPk_mtapp_detail());
				}
			}

			MtAppDetailVO[] oldchilds = queryOldVOsByVOs(vos)[0].getChildrenVO();
			for (int i = 0; i < oldchilds.length; i++) {
				if ((oldchilds[i].getClose_status() == null || oldchilds[i].getClose_status() == ErmMatterAppConst.CLOSESTATUS_N)
						&& !newchildpks.contains(oldchilds[i].getPk_mtapp_detail())) {
					// �رղ����У����������ֻҪ����һ��δ�رգ��Ǿ���Ϊ������ȡ���ر�
					return false;
				}
			}
		}

		return true;
	}

	private void deleteValidate(AggMatterAppVO[] vos) {
		List<MatterAppVO> headlist = new ArrayList<MatterAppVO>();
		List<MtAppDetailVO> detaillist = new ArrayList<MtAppDetailVO>();
		for (AggMatterAppVO aggvo : vos) {
			headlist.add((MatterAppVO) aggvo.getParentVO());
			CircularlyAccessibleValueObject[] childrenVO = aggvo.getChildrenVO();
			if (childrenVO != null && childrenVO.length > 0) {
				for (int j = 0; j < childrenVO.length; j++) {
					detaillist.add((MtAppDetailVO) childrenVO[j]);
				}
			}
		}
		BDReferenceChecker.getInstance().validate(headlist.toArray(new MatterAppVO[headlist.size()]));
		if (!detaillist.isEmpty()) {
			BDReferenceChecker.getInstance().validate(detaillist.toArray(new MtAppDetailVO[detaillist.size()]));
		}
	}

	/**
	 * ��õ��ݺ�
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
	 * �˻����ݺ�
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
	 * ��������
	 * 
	 * @param vos
	 * @throws BusinessException
	 */
	private void pklockOperate(AggMatterAppVO... vos) throws BusinessException {
		// ��������
		ErLockUtil.lockAggVOByPk("ERM_matterapp", vos);
	}

	/**
	 * �������
	 * 
	 * @param vos
	 * @throws BusinessException
	 */
	private void pklockVO(MatterAppVO... vos) throws BusinessException {
		ErLockUtil.lockVOByPk("ERM_matterapp", vos);
	}

	/**
	 * �ж�Vo�Ƿ�����Ч����
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
	 * �������þۺ�VO��ֵ
	 * 
	 * @param vos
	 *            �ۺ�VO
	 * @param attributeName
	 *            ������
	 * @param attributeValue
	 *            ����ֵ
	 * @param isDetail
	 *            �Ƿ����ൽ�ӱ� true:��
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

	private AggMatterAppVO[] queryOldVOsByVOs(AggMatterAppVO[] vos) throws BusinessException {

		String[] pks = new String[vos.length];
		for (int i = 0; i < vos.length; i++) {
			pks[i] = vos[i].getParentVO().getPrimaryKey();
		}
		IMDPersistenceQueryService service = MDPersistenceService.lookupPersistenceQueryService();

		@SuppressWarnings("unchecked")
		Collection<AggMatterAppVO> c = service.queryBillOfVOByPKs(AggMatterAppVO.class, pks, false);

		return c.toArray(new AggMatterAppVO[c.size()]);
	}

	// ����vo����
	public void prepareVoValue(AggMatterAppVO vo) throws BusinessException {
		MatterAppVO parentVo = (MatterAppVO) vo.getParentVO();
		prepareForNullJe(vo);
		prepareHeader(parentVo);
		prepareChildrenVO(vo);
	}

	private void prepareHeader(MatterAppVO parentVo) {
		parentVo.setBillstatus(MatterAppUtils.getBillStatus(parentVo.getApprstatus()));
		parentVo.setPk_billtype(ErmMatterAppConst.MatterApp_BILLTYPE);
		if (parentVo.getBillmaker() != null) {
			try {
				parentVo.setAuditman(BXBsUtil.getCuserIdByPK_psndoc(parentVo.getBillmaker()));
			} catch (BusinessException e) {
				ExceptionHandler.consume(e);
			}
		}
		// �Զ��ر�ʱ�䣬����Ϊ���
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

				// ���ñ������
				item.setOrg_currinfo(parentVo.getOrg_currinfo());
				item.setGroup_currinfo(parentVo.getGroup_currinfo());
				item.setGlobal_currinfo(parentVo.getGlobal_currinfo());
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
		// ����ͷ��Ϣ���ൽ����
		MatterAppVO parentVo = vo.getParentVO();
		MtAppDetailVO[] children = (MtAppDetailVO[]) vo.getChildrenVO();

		if (children != null) {
			for (int i = 0; i < children.length; i++) {
				if (children[i].getStatus() == VOStatus.DELETED) {
					continue;
				}

				children[i].setPk_currtype(parentVo.getPk_currtype());
				children[i].setPk_billtype(parentVo.getPk_billtype());
				children[i].setPk_tradetype(parentVo.getPk_tradetype());
				children[i].setBilldate(parentVo.getBilldate());
				children[i].setPk_group(parentVo.getPk_group());// ���š���֯
				children[i].setPk_org(parentVo.getPk_org());

				children[i].setPk_currtype(parentVo.getPk_currtype());
				children[i].setOrg_currinfo(parentVo.getOrg_currinfo());// ����
				children[i].setGroup_currinfo(parentVo.getGroup_currinfo());
				children[i].setGlobal_currinfo(parentVo.getGlobal_currinfo());
				children[i].setApply_dept(parentVo.getApply_dept());
				children[i].setBillmaker(parentVo.getBillmaker());
				
				children[i].setBillno(parentVo.getBillno());//���ݱ�ţ�ע����createCode�����޸�ʱ������û������billno
				children[i].setClose_status(ErmMatterAppConst.CLOSESTATUS_N);// �ر�״̬
				children[i].setBillstatus(parentVo.getBillstatus());// ��ͷ����״̬���ൽ����
			}
		}
	}
	
	/**
	 * ��д���뵥������Ϣ
	 */
	@Override
	public void callCheckStatus(CheckStatusCallbackContext cscc) throws BusinessException {
		try {
			AggMatterAppVO updateVO = (AggMatterAppVO) cscc.getBillVo();
			// ����
			pklockOperate(updateVO);
			// �汾У��
			BDVersionValidationUtil.validateVersion(updateVO);

			// ���ݸ��µ����ݿ�
			getDAO().updateAggVOsByFields(new AggMatterAppVO[] { updateVO },
					new String[] { MatterAppVO.APPRSTATUS, MatterAppVO.APPROVER, MatterAppVO.APPROVETIME },
					new String[] {});
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
		}
	}
	
	/**
	 * ����������״̬
	 * 
	 * @param updateVO
	 * @return
	 * @throws BusinessException
	 */
	public AggMatterAppVO updateVOBillStatus(AggMatterAppVO updateVO) throws BusinessException {
		try {
			// ����
			pklockOperate(updateVO);
			// �汾У��
			BDVersionValidationUtil.validateVersion(updateVO);

			// ���õ���״̬
			int billstatus = MatterAppUtils.getBillStatus(updateVO.getParentVO().getApprstatus());
			updateVO.getParentVO().setBillstatus(billstatus);

			// ���ݸ��µ����ݿ�
			getDAO().updateAggVOsByFields(new AggMatterAppVO[] { updateVO }, new String[] { MatterAppVO.BILLSTATUS },
					new String[] {});

			return updateVO;
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private List<String> getEffectOpenDetails(AggMatterAppVO vo) throws BusinessException {
		List<String> result = new ArrayList<String>();

		StringBuffer sql = new StringBuffer();
		sql.append(" orig_amount > isnull(( SELECT sum(p.exe_amount+p.pre_amount) FROM er_mtapp_billpf p ");
		sql
				.append(" WHERE p.pk_mtapp_detail=er_mtapp_detail.pk_mtapp_detail and p.pk_djdl='bx' GROUP BY p.pk_mtapp_detail ),0 ) ");

		String[] pk_maDetails = VOUtils.getAttributeValues(vo.getChildrenVO(), MtAppDetailVO.PK_MTAPP_DETAIL);
		sql.append(" and " + SqlUtil.buildInSql("er_mtapp_detail.pk_mtapp_detail", pk_maDetails));

		Collection<MtAppDetailVO> detailList = MDPersistenceService.lookupPersistenceQueryService()
				.queryBillOfVOByCond(MtAppDetailVO.class, sql.toString(), false);

		if (detailList != null) {
			pk_maDetails = VOUtils.getAttributeValues(detailList.toArray(new MtAppDetailVO[0]),
					MtAppDetailVO.PK_MTAPP_DETAIL);

			for (String pk_maDetail : pk_maDetails) {
				result.add(pk_maDetail);
			}
		}
		return result;
	}

}