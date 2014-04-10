package nc.bs.erm.costshare;

import java.sql.SQLException;
import java.util.ArrayList;
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
import nc.md.persist.framework.IMDPersistenceQueryService;
import nc.md.persist.framework.MDPersistenceService;
import nc.pubitf.fip.service.IFipMessageService;
import nc.vo.arap.bx.util.ActionUtils;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.arap.transaction.DataValidateException;
import nc.vo.ep.bx.BXHeaderVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.er.util.ErmBillCalUtil;
import nc.vo.erm.common.MessageVO;
import nc.vo.erm.costshare.AggCostShareVO;
import nc.vo.erm.costshare.CShareDetailVO;
import nc.vo.erm.costshare.CostShareVO;
import nc.vo.fip.service.FipMessageVO;
import nc.vo.fip.service.FipRelationInfoVO;
import nc.vo.fipub.billcode.FinanceBillCodeInfo;
import nc.vo.fipub.billcode.FinanceBillCodeUtils;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
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
 * ���ý�ת��ҵ����
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
		// ��������
		insertlockOperate(vo);
		CostShareVO parentVO = (CostShareVO) vo.getParentVO();
		//����У�飬У�鱨�����汾
		try {
			JKBXHeaderVO bxheadvo = parentVO.getBxheadvo();
			if (!bxheadvo.isInit()) {
				BDVersionValidationUtil.validateVersion(bxheadvo);
			}
		} catch (BusinessException e) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0","0upp2012V575-0107"),e);/*"�������ѱ�ȡ����Ч��ɾ��������")*/
		}
		// Ψһ��У��
		insertvalidate(parentVO);
		// voУ��
		CostShareVOChecker vochecker = new CostShareVOChecker();
		vochecker.checkSave(vo);
		try {
			// ��ȡ���ݺ�
			createBillNo(vo);
			// ���������Ϣ
			AuditInfoUtil.addData(parentVO);
			// ����Ĭ��ֵ
			setDefaultValue(vo, true);
			// ����ǰ�¼�����
			fireBeforeInsertEvent(vo);
			// ��������
			result = getDAO().insertVO(vo);
			// �������¼�����
			fireAfterInsertEvent(vo);
		} catch (Exception e) {
			returnBillno(new AggCostShareVO[] { vo });
			ExceptionHandler.handleException(e);
		}
//		// ��¼ҵ����־
//		ErmBusiLogUtils.insertSmartBusiLogs(new AggCostShareVO[]{vo}, null, IErmCostShareConst.CS_MD_INSERT_OPER);
		// ����
		return result;
	}

	public AggCostShareVO tempInsertVO(AggCostShareVO vo) throws BusinessException {
		// ��������
		insertlockOperate(vo);
		// Ψһ��У��
		insertvalidate((CostShareVO) vo.getParentVO());
		// voУ��
		CostShareVOChecker vochecker = new CostShareVOChecker();
		vochecker.checkSave(vo);
		try{
			// ��ȡ���ݺ�
			createBillNo(vo);
			// ���������Ϣ
			AuditInfoUtil.addData(vo.getParentVO());
			// ����Ĭ��ֵ
			setDefaultValue(vo, true);
			// ����ǰ�¼�����
			fireBeforeTempInsertEvent(vo);
			// ��������
			vo = getDAO().insertVO(vo);
			// �������¼�����
			fireAfterTempInsertEvent(vo);
		}catch (Exception e) {
			 returnBillno(new AggCostShareVO[]{vo});
			 ExceptionHandler.handleException(e);
		}
//		// ��¼ҵ����־
//		ErmBusiLogUtils.insertSmartBusiLogs(new AggCostShareVO[]{vo}, null, IErmCostShareConst.CS_MD_INSERT_OPER);
		// ����
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
			Logger.error("��ѯ��������ЧУ�飬��ѯʧ��",e);
			throw new BusinessException(e);
		}
		if(count == null || count.intValue() == 0){
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0095")/*@res "�������ѱ�ȡ����Ч��ɾ��������"*/);
		}
	}

	private void insertvalidate(CostShareVO vo) throws BusinessException {
		onlyValidate(vo);
		if(IErmCostShareConst.CostShare_Bill_SCRTYPE_SEL == vo.getSrc_type()){
			unInureValidate(vo);
		}
	}

	private void onlyValidate(CostShareVO vo) throws BusinessException {
		// Ψһ��У�飬��Ӧ�����������ظ�

		String sql = "select top 1 1 from " + vo.getTableName() + " where " + CostShareVO.SRC_ID +" = ? ";

		SQLParameter sqlParameter = new SQLParameter();
		sqlParameter.addParam(vo.getSrc_id());

		Integer count = null;
		try {
			count = (Integer)new BaseDAO().executeQuery(sql, sqlParameter, new ColumnProcessor());
		} catch (DAOException e) {
			Logger.error("���ý�ת��Ψһ��У�飬��ѯʧ��",e);
			throw new BusinessException(e);
		}
		if(count == null || count.intValue() == 0){
			return ;
		}
		throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0096")/*@res "�����ظ���������ѡ������"*/);
	}

	public AggCostShareVO updateVO(AggCostShareVO vo) throws BusinessException {
		// �޸ļ���
		updatelockOperate(vo);
		// �汾У��
		CircularlyAccessibleValueObject parentVO = vo.getParentVO();
		BDVersionValidationUtil.validateVersion(parentVO);
		// voУ��
		CostShareVOChecker vochecker = new CostShareVOChecker();
		vochecker.checkSave(vo);
		// ����Ĭ��ֵ
		setDefaultValue(vo, false);
		// ���������Ϣ
		AuditInfoUtil.updateData(parentVO);
		// ��ѯ�޸�ǰ��vo
		IMDPersistenceQueryService qryservice = MDPersistenceService.lookupPersistenceQueryService();
		AggCostShareVO oldvo = qryservice.queryBillOfVOByPK(AggCostShareVO.class, parentVO.getPrimaryKey(), false);
		// �޸�ǰ�¼�����
		fireBeforeUpdateEvent(vo,oldvo);
		// ���±���
		vo = getDAO().updateVO(vo);
		// �޸ĺ��¼�����
		fireAfterUpdateEvent(vo,oldvo);
		// ��¼ҵ����־
		BusiLogUtil.insertSmartBusiLog(IErmCostShareConst.CS_MD_UPDATE_OPER,new AggCostShareVO[]{vo}, new AggCostShareVO[]{oldvo});
		// ����
		return vo;
	}
	
	/**
	 * ɾ�����ý�ת������������
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	public MessageVO[] deleteVOs(AggCostShareVO[] vos) throws BusinessException {
		if (vos == null || vos.length == 0) {
			return null;
		}
		//��¼��־��������Ҫ������ʾ�����Բ�֧����������
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
		// ɾ������
		deletelockOperate(vos);
		// �汾У��
		BDVersionValidationUtil.validateVersion(vos);
		// ɾ������У��
		deleteValidate(vos);
		// voУ��
		CostShareVOChecker vochecker = new CostShareVOChecker();
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
		//��¼��־��������Ҫ������ʾ�����Բ�֧����������
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
		// ����
		updatelockOperate(vos);
		// �汾У��
		BDVersionValidationUtil.validateVersion(vos);
		// voУ��
		CostShareVOChecker vochecker = new CostShareVOChecker();
		vochecker.checkApprove(vos,buDate);
		// ���ǰ�¼�����
		fireBeforeApproveEvent(vos);
		// �������״̬
		CostShareVO[] parentvos = new CostShareVO[vos.length];
		String[] pks = new String[vos.length];
		//�ӱ���
		List<CShareDetailVO> childrenVOS = new ArrayList<CShareDetailVO>();
		// ������Ч��Ϣ������ˡ�����������ڲ�������������
		for (int i = 0; i < parentvos.length; i++) {
			CostShareVO parentvo = (CostShareVO) vos[i].getParentVO();
			parentvo.setEffectstate(IErmCostShareConst.CostShare_Bill_Effectstate_Y);
			parentvo.setBillstatus(BXStatusConst.DJZT_Sign);
			parentvo.setApprover(InvocationInfoProxy.getInstance()
					.getUserId());
			parentvo.setApprovedate(buDate);
			parentvos[i] = parentvo;
			pks[i] = parentvo.getPrimaryKey();

			//ͬ������������
			CShareDetailVO[] childrenvo = (CShareDetailVO[]) vos[i].getChildrenVO();
			for (int j = 0; j < childrenvo.length; j++) {
				childrenvo[j].setBillstatus(parentvo.getBillstatus());
				childrenVOS.add(childrenvo[j]);
			}
		}
//		@SuppressWarnings("unchecked")
//		Collection<AggCostShareVO> oldvos = MDPersistenceService.lookupPersistenceQueryService().queryBillOfVOByPKs(AggCostShareVO.class, pks, false);
		// ����������
		getDAO().updateParentVOs(
				parentvos,
				new String[] { CostShareVO.PK_COSTSHARE, CostShareVO.APPROVER,
						CostShareVO.APPROVEDATE, CostShareVO.EFFECTSTATE,
						CostShareVO.BILLSTATUS });

		getDAO().updateChildrenVOs(childrenVOS.toArray(new CShareDetailVO[0]), new String[] {CostShareVO.BILLSTATUS });


		// ��˺��¼�����
		fireAfterApproveEvent(vos);
		// ������Ϣ�����ƽ̨������ƾ֤
		sendMessageToFip(vos, FipMessageVO.MESSAGETYPE_ADD);
		qryCshareVo(vos, parentvos);

//		// ��¼ҵ����־
//		ErmBusiLogUtils.insertSmartBusiLogs(vos, (AggCostShareVO[]) oldvos.toArray(new AggCostShareVO[0]), IErmCostShareConst.CS_MD_APPROVE_OPER);
	}


	public MessageVO[] unapproveVOs(AggCostShareVO[] vos)
			throws BusinessException {
		if (vos == null || vos.length == 0) {
			return null;
		}
		//��¼��־��������Ҫ������ʾ�����Բ�֧����������
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
		// ����
		updatelockOperate(vos);
		// �汾У��
		BDVersionValidationUtil.validateVersion(vos);
		// voУ��
		CostShareVOChecker vochecker = new CostShareVOChecker();
		vochecker.checkunApprove(vos);
		// ȡ�����ǰ�¼�����
		fireBeforeUnApproveEvent(vos);
		// �������״̬
		CostShareVO[] parentvos = new CostShareVO[vos.length];
		String[] pks = new String[vos.length];
		//�ӱ�����
		List<CShareDetailVO> childrenVOS = new ArrayList<CShareDetailVO>();
		// ���õ���״̬����Ч��Ϣ���������ˡ��������
		for (int i = 0; i < parentvos.length; i++) {
			CostShareVO parentvo = (CostShareVO) vos[i].getParentVO();
			parentvos[i] = parentvo;
			parentvo.setEffectstate(IErmCostShareConst.CostShare_Bill_Effectstate_N);
			parentvo.setBillstatus(BXStatusConst.DJZT_Saved);
			parentvo.setApprover(null);
			parentvo.setApprovedate(null);
			pks[i] = parentvo.getPrimaryKey();
			//ͬ������������
			CShareDetailVO[] childrenvo = (CShareDetailVO[]) vos[i].getChildrenVO();
			for (int j = 0; j < childrenvo.length; j++) {
				childrenvo[j].setBillstatus(parentvo.getBillstatus());
				childrenVOS.add(childrenvo[j]);
			}

		}
//		@SuppressWarnings("unchecked")
//		Collection<AggCostShareVO> oldvos = MDPersistenceService.lookupPersistenceQueryService().queryBillOfVOByPKs(AggCostShareVO.class, pks, false);
//		AggCostShareVO[] arraycsVO = oldvos.toArray(new AggCostShareVO[0]);
		// ���±���
		getDAO().updateParentVOs(
				parentvos,
				new String[] { CostShareVO.PK_COSTSHARE, CostShareVO.APPROVER,
						CostShareVO.APPROVEDATE, CostShareVO.EFFECTSTATE,
						CostShareVO.BILLSTATUS });


		getDAO().updateChildrenVOs(childrenVOS.toArray(new CShareDetailVO[0]), new String[] {CostShareVO.BILLSTATUS });

		// ȡ����˺��¼�����
		fireAfterUnApproveEvent(vos);
		// ������Ϣ�����ƽ̨��ɾ��ƾ֤
		sendMessageToFip(vos, FipMessageVO.MESSAGETYPE_DEL);
		// ����
		qryCshareVo(vos, parentvos);
//		// ��¼ҵ����־
//		ErmBusiLogUtils.insertSmartBusiLogs(vos, arraycsVO, IErmCostShareConst.CS_MD_UNAPPROVE_OPER);
	}
	/**
	 * ��ѯ��������������
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
	 * ������Ϣ�����ƽ̨
	 *
	 * @param vos
	 * @throws BusinessException
	 */
	private void sendMessageToFip(AggCostShareVO[] vos, int messageType)
			throws BusinessException {
		// ��װ��Ϣ
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
		// ���͵����ƽ̨
		NCLocator.getInstance().lookup(IFipMessageService.class)
				.sendMessages(messageList.toArray(new FipMessageVO[0]));
	}

	/**
	 * ��õ��ݺ�
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
	 * �˻����ݺ�
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
	 * ��������
	 *
	 * @param vo
	 * @throws BusinessException
	 */
	private void insertlockOperate(AggCostShareVO vo) throws BusinessException {
		// ҵ����
		ErLockUtil.lockVO(new String[] { CostShareVO.SRC_ID,
				CostShareVO.PK_ORG }, lockmessage, vo.getParentVO());
	}

	/**
	 * ���¼���
	 *
	 * @param vo
	 * @throws BusinessException
	 */
	private void updatelockOperate(AggCostShareVO... vos)
			throws BusinessException {
		// ҵ����
		ErLockUtil.lockAggVO(new String[] { CostShareVO.SRC_ID,
				CostShareVO.PK_ORG }, lockmessage, vos);
		// ������
		ErLockUtil.lockAggVOByPk(lockmessage, vos);

	}

	/**
	 * ɾ������
	 *
	 * @param vos
	 * @throws BusinessException
	 */
	private void deletelockOperate(AggCostShareVO[] vos)
			throws BusinessException {
		// ������
		ErLockUtil.lockAggVOByPk(lockmessage, vos);
	}

	/**
	 * ���ý�ת����Ĭ��ֵ
	 *
	 * @param vos
	 */
	private void setDefaultValue(AggCostShareVO vo, boolean isInsert) {
		CostShareVO csvo = ((CostShareVO) vo.getParentVO());
		csvo.setEffectstate(IErmCostShareConst.CostShare_Bill_Effectstate_N);
		// ��������
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
	/**
	 * ��ǰ̨��������������ٺ�̨�����ˡ�
	 * @param csvo
	 * @param cr
	 * @param i
	 */
	private void setRateAmount(CostShareVO csvo, CShareDetailVO[] cr, int i) {
		// ������屾�ҽ��
		UFDouble hl = UFDouble.ZERO_DBL;
		UFDouble grouphl = UFDouble.ZERO_DBL;
		UFDouble globalhl = UFDouble.ZERO_DBL;
		try {
			String localCurry = Currency.getLocalCurrPK(cr[i].getAssume_org());
			UFDouble[] rates = ErmBillCalUtil.getRate(csvo.getBzbm(), cr[i].getAssume_org(),
					csvo.getPk_group(), csvo.getBilldate(), localCurry);
			 hl = rates[0];
			 grouphl = rates[1];
			 globalhl = rates[2];
			
//			ratevalue = Currency.getRate(cr[i].getAssume_org(), cr[i]
//					.getBzbm(), localCurry, csvo.getBilldate());
		} catch (BusinessException e) {
			Logger.error(e.getMessage(), e);
		}
		cr[i].setBbhl(hl);
		// ���ҽ��=ԭ��*����
		cr[i].setBbje(cr[i].getAssume_amount().multiply(
				hl, UFDouble.ROUND_HALF_UP));
		
		cr[i].setGroupbbhl(grouphl);
		// ���Ž��=ԭ��*����
		cr[i].setGroupbbje(cr[i].getAssume_amount().multiply(
				grouphl, UFDouble.ROUND_HALF_UP));
		
		cr[i].setGlobalbbhl(globalhl);
		// ȫ�ֽ��=ԭ��*����
		cr[i].setGlobalbbje(cr[i].getAssume_amount().multiply(
				globalhl, UFDouble.ROUND_HALF_UP));
	}
	
//	private void setGroupRateAmount(CostShareVO csvo, CShareDetailVO[] cr, int i) {
//		// ������弯�Ž��
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
//		// ���Ž��=ԭ��*����
//		cr[i].setGroupbbje(cr[i].getAssume_amount().multiply(
//				ratevalue, UFDouble.ROUND_HALF_UP));
//	}
//
//	private void setGloalRateAmount(CostShareVO csvo, CShareDetailVO[] cr, int i) {
//		// �������ȫ�ֽ��
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
//		// ȫ�ֽ��=ԭ��*����
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