package nc.bs.erm.expamortize;

import nc.bs.dao.BaseDAO;
import nc.bs.erm.expamortize.util.ExpamtinfoVOChecker;
import nc.bs.erm.util.ErAccperiodUtil;
import nc.bs.erm.util.ErLockUtil;
import nc.bs.erm.util.ErMdpersistUtil;
import nc.md.data.access.NCObject;
import nc.md.persist.framework.MDPersistenceService;
import nc.util.erm.expamortize.ExpamtUtil;
import nc.vo.bd.period2.AccperiodmonthVO;
import nc.vo.erm.expamortize.AggExpamtinfoVO;
import nc.vo.erm.expamortize.ExpamtDetailVO;
import nc.vo.erm.expamortize.ExpamtinfoVO;
import nc.vo.pub.BusinessException;
import nc.vo.util.AuditInfoUtil;
import nc.vo.util.BDVersionValidationUtil;
/**
 * ��̯����ҵ����
 * @author wangled
 *
 */
public class ErmExpamortizeBO {
	
	public void insertVO(AggExpamtinfoVO[] vos) throws BusinessException {
			// ��������
			insertlockOperate(vos);
			// Ψһ��У��,ͨ���ܿ�ģʽ��ʵ�֡�
			// voУ��
			ExpamtinfoVOChecker vochecker = new ExpamtinfoVOChecker();
			vochecker.checkSave(vos);
			// ���������Ϣ
			for(AggExpamtinfoVO vo :vos){
				AuditInfoUtil.addData(vo.getParentVO());
			}
			// ����Ĭ��ֵ
			setDefaultValue(vos, true);
			// ��������
			NCObject[] ncobjs = ErMdpersistUtil.getNCObject(vos);
			MDPersistenceService.lookupPersistenceService().saveBill(ncobjs);
	}
	
	
	public void deleteVOs(AggExpamtinfoVO[] vos) throws BusinessException{
		// ɾ������
		deletelockOperate(vos);
		// voУ��
		ExpamtinfoVOChecker vochecker = new ExpamtinfoVOChecker();
		vochecker.checkDelete(vos);
		// ɾ������
		NCObject[] ncobjs = ErMdpersistUtil.getNCObject(vos);
		MDPersistenceService.lookupPersistenceService().deleteBillFromDB(ncobjs);
	}
	
	
	public ExpamtinfoVO updatePeriod(int period, ExpamtinfoVO vo,String currAccPeriod )throws BusinessException{
		// �޸ļ���
		updatelockOperate(vo);
		// �汾У��
		BDVersionValidationUtil.validateVersion(vo);
		
		//���� �µ���̯����
		int curr_total_period=vo.getTotal_period();
		int new_total_period=curr_total_period+(period-vo.getRes_period());
		vo.setTotal_period(new_total_period);
		//���� �µ�ʣ��̯����
		vo.setRes_period(period);
		
		//�����µĽ�ֹ̯����
		String end_period=ErAccperiodUtil.getAddAccperiodmonth(vo.getPk_org(),
				vo.getStart_period(),vo.getTotal_period()).getYearmth();
		vo.setEnd_period(end_period);
		
		NCObject ncobjs = ErMdpersistUtil.getNCObject(vo);
		String[] filtAttrNames={"res_period","total_period","end_period"};
		MDPersistenceService.lookupPersistenceService().updateBillWithAttrs(new NCObject[]{ncobjs}, filtAttrNames);
		
		
		
		ExpamtinfoVO expamtinfovo=(ExpamtinfoVO) new BaseDAO().retrieveByPK(ExpamtinfoVO.class, vo.getPk_expamtinfo());
		//����̯����������
		ExpamtUtil.addComputePropertys(new ExpamtinfoVO[]{expamtinfovo},currAccPeriod);
		return expamtinfovo;
	}
	public static final String lockmessage = "ERM_expamortize";
	
	/**
	 * ��������
	 * @param vo
	 * @throws BusinessException
	 */
	private void insertlockOperate(AggExpamtinfoVO[] vos) throws BusinessException {
		// ҵ����
		ErLockUtil.lockAggVO(new String[] { ExpamtinfoVO.PK_JKBX,
				ExpamtinfoVO.PK_ORG }, lockmessage,vos);
	}
	/**
	 * ɾ������
	 * @param vos
	 * @throws BusinessException
	 */
	private void deletelockOperate(AggExpamtinfoVO[] vos)
	throws BusinessException {
		// ������
		ErLockUtil.lockAggVOByPk(lockmessage, vos);
	}
	
	/**
	 * �޸ļ���
	 * @throws BusinessException 
	 */
	private void updatelockOperate(ExpamtinfoVO vo) throws BusinessException {
		// ҵ����
		ErLockUtil.lockVO(new String[] { ExpamtinfoVO.PK_JKBX,
				ExpamtinfoVO.PK_ORG }, lockmessage, vo);
		// ������
		ErLockUtil.lockVOByPk(lockmessage, vo);
	}

	/**
	 * ��̯��������Ĭ��ֵ
	 * @param vos
	 * @throws BusinessException 
	 */
	private void setDefaultValue(AggExpamtinfoVO[] vos, boolean isInsert) throws BusinessException {
		for (AggExpamtinfoVO vo : vos) {
			ExpamtinfoVO exvo = ((ExpamtinfoVO) vo.getParentVO());
			// ������̯����ڼ䡢��̯���ڼ���
			String startPeriod = exvo.getStart_period();
			Integer total_period = exvo.getTotal_period();
			exvo.setBillstatus(ExpAmoritizeConst.Billstatus_Init);
			exvo.setStart_period(ErAccperiodUtil.getAccperiodmonthByPk(
					startPeriod).getYearmth());
			AccperiodmonthVO accperiodmonth = ErAccperiodUtil
					.getAddAccperiodmonth(exvo.getPk_org(), exvo
							.getStart_period(), total_period);
			exvo.setEnd_period(accperiodmonth.getYearmth());
			if (vo.getChildrenVO() != null) {
				ExpamtDetailVO[] cr = (ExpamtDetailVO[]) vo.getChildrenVO();
				for (int i = 0; i < cr.length; i++) {
					cr[i].setBx_billno(exvo.getBx_billno());
					cr[i].setPk_jkbx(exvo.getPk_jkbx());
					cr[i].setTotal_period(exvo.getTotal_period());
					cr[i].setRes_period(exvo.getRes_period());
					cr[i].setStart_period(exvo.getStart_period());
					cr[i].setEnd_period(exvo.getEnd_period());
					cr[i].setCreator(exvo.getCreator());
					cr[i].setCreationtime(exvo.getCreationtime());
					cr[i].setModifier(exvo.getModifier());
					cr[i].setModifiedtime(exvo.getModifiedtime());
				}
			}
		}
	}
}
