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
 * 待摊费用业务类
 * @author wangled
 *
 */
public class ErmExpamortizeBO {
	
	public void insertVO(AggExpamtinfoVO[] vos) throws BusinessException {
			// 新增加锁
			insertlockOperate(vos);
			// 唯一性校验,通过管控模式来实现。
			// vo校验
			ExpamtinfoVOChecker vochecker = new ExpamtinfoVOChecker();
			vochecker.checkSave(vos);
			// 设置审计信息
			for(AggExpamtinfoVO vo :vos){
				AuditInfoUtil.addData(vo.getParentVO());
			}
			// 设置默认值
			setDefaultValue(vos, true);
			// 新增保存
			NCObject[] ncobjs = ErMdpersistUtil.getNCObject(vos);
			MDPersistenceService.lookupPersistenceService().saveBill(ncobjs);
	}
	
	
	public void deleteVOs(AggExpamtinfoVO[] vos) throws BusinessException{
		// 删除加锁
		deletelockOperate(vos);
		// vo校验
		ExpamtinfoVOChecker vochecker = new ExpamtinfoVOChecker();
		vochecker.checkDelete(vos);
		// 删除单据
		NCObject[] ncobjs = ErMdpersistUtil.getNCObject(vos);
		MDPersistenceService.lookupPersistenceService().deleteBillFromDB(ncobjs);
	}
	
	
	public ExpamtinfoVO updatePeriod(int period, ExpamtinfoVO vo,String currAccPeriod )throws BusinessException{
		// 修改加锁
		updatelockOperate(vo);
		// 版本校验
		BDVersionValidationUtil.validateVersion(vo);
		
		//设置 新的总摊销期
		int curr_total_period=vo.getTotal_period();
		int new_total_period=curr_total_period+(period-vo.getRes_period());
		vo.setTotal_period(new_total_period);
		//设置 新的剩余摊销期
		vo.setRes_period(period);
		
		//设置新的截止摊销期
		String end_period=ErAccperiodUtil.getAddAccperiodmonth(vo.getPk_org(),
				vo.getStart_period(),vo.getTotal_period()).getYearmth();
		vo.setEnd_period(end_period);
		
		NCObject ncobjs = ErMdpersistUtil.getNCObject(vo);
		String[] filtAttrNames={"res_period","total_period","end_period"};
		MDPersistenceService.lookupPersistenceService().updateBillWithAttrs(new NCObject[]{ncobjs}, filtAttrNames);
		
		
		
		ExpamtinfoVO expamtinfovo=(ExpamtinfoVO) new BaseDAO().retrieveByPK(ExpamtinfoVO.class, vo.getPk_expamtinfo());
		//补充摊销计算属性
		ExpamtUtil.addComputePropertys(new ExpamtinfoVO[]{expamtinfovo},currAccPeriod);
		return expamtinfovo;
	}
	public static final String lockmessage = "ERM_expamortize";
	
	/**
	 * 新增加锁
	 * @param vo
	 * @throws BusinessException
	 */
	private void insertlockOperate(AggExpamtinfoVO[] vos) throws BusinessException {
		// 业务锁
		ErLockUtil.lockAggVO(new String[] { ExpamtinfoVO.PK_JKBX,
				ExpamtinfoVO.PK_ORG }, lockmessage,vos);
	}
	/**
	 * 删除加锁
	 * @param vos
	 * @throws BusinessException
	 */
	private void deletelockOperate(AggExpamtinfoVO[] vos)
	throws BusinessException {
		// 主键锁
		ErLockUtil.lockAggVOByPk(lockmessage, vos);
	}
	
	/**
	 * 修改加锁
	 * @throws BusinessException 
	 */
	private void updatelockOperate(ExpamtinfoVO vo) throws BusinessException {
		// 业务锁
		ErLockUtil.lockVO(new String[] { ExpamtinfoVO.PK_JKBX,
				ExpamtinfoVO.PK_ORG }, lockmessage, vo);
		// 主键锁
		ErLockUtil.lockVOByPk(lockmessage, vo);
	}

	/**
	 * 待摊费用设置默认值
	 * @param vos
	 * @throws BusinessException 
	 */
	private void setDefaultValue(AggExpamtinfoVO[] vos, boolean isInsert) throws BusinessException {
		for (AggExpamtinfoVO vo : vos) {
			ExpamtinfoVO exvo = ((ExpamtinfoVO) vo.getParentVO());
			// 根据起摊会计期间、总摊销期计算
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
