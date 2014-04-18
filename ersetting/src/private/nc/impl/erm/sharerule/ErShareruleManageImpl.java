package nc.impl.erm.sharerule;

import nc.bs.bd.cache.CacheProxy;
import nc.bs.erm.sharerule.ShareruleConst;
import nc.bs.erm.sharerule.SharerulePubUtil;
import nc.bs.logging.Logger;
import nc.bs.uif2.validation.IValidationService;
import nc.bs.uif2.validation.ValidationFrameworkUtil;
import nc.md.model.MetaDataException;
import nc.md.persist.framework.IMDPersistenceQueryService;
import nc.md.persist.framework.IMDPersistenceService;
import nc.md.persist.framework.MDPersistenceService;
import nc.pubitf.erm.sharerule.IErShareruleManage;
import nc.vo.erm.sharerule.AggshareruleVO;
import nc.vo.erm.sharerule.ShareruleVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;
import nc.vo.util.AuditInfoUtil;
import nc.vo.util.BDPKLockUtil;
import nc.vo.util.BDUniqueRuleValidate;
import nc.vo.util.BDVersionValidationUtil;
import nc.vo.util.bizlock.BizlockDataUtil;

public class ErShareruleManageImpl implements IErShareruleManage {

	@Override
	public AggshareruleVO insertVO(AggshareruleVO vo) throws BusinessException {
		if (vo == null)
			return vo;

		ShareruleVO parentvo = (ShareruleVO) vo.getParentVO();
		// 新增时的加锁操作
		insertlockOperate(parentvo);
		// 逻辑校验
		insertValidateVO(vo);

		saveValidate(vo);

		// 设置审计信息
		setInsertAuditInfo(parentvo);

		// 库操作
		String pk = dbInsertVO(vo);

		// 重新检索出插入的VO
		vo = retrieveVO(pk);

		// 通知更新缓存
		notifyVersionChangeWhenDataInserted();

		return vo;
	}

	/**
	 * 保存业务规则校验
	 *
	 * @param vo
	 */
	private void saveValidate(AggshareruleVO vo) throws BusinessException {
		// 分摊方式为比例情况,若合计值不是100抛出异常
		ShareruleVO parentvo = (ShareruleVO) vo.getParentVO();
		if (parentvo.getRule_type().equals(ShareruleConst.SRuletype_Ratio)) {
			UFDouble totalRatio = SharerulePubUtil.getTotalRatio(vo);
			if ((totalRatio.compareTo(new UFDouble(100)) != 0)) {
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201100_0","0201100-0021")/*@res "表体分摊比例合计不为100%"*/);
			}
		}
	}

	@Override
	public AggshareruleVO updateVO(AggshareruleVO vo) throws BusinessException {
		if (vo == null)
			return vo;
		ShareruleVO parentvo = (ShareruleVO) vo.getParentVO();
		// 更新时的加锁操作
		updatelockOperate(parentvo);

		// 校验版本
		BDVersionValidationUtil.validateSuperVO(parentvo);

		// 业务校验逻辑
		updateValidateVO(vo);

		saveValidate(vo);

		// 设置审计信息
		setUpdateAuditInfo(parentvo);

		// 库操作
		dbUpdateVO(vo);

		// 更新缓存
		notifyVersionChangeWhenDataUpdated(parentvo);

		// 重新检索出新数据
		vo = retrieveVO(parentvo.getPrimaryKey());

		return vo;
	}

	@Override
	public void deleteVO(AggshareruleVO vo) throws BusinessException {
		if (vo == null)
			return;
		ShareruleVO parentvo = (ShareruleVO) vo.getParentVO();
		// 删除时的加锁操作
		deletelockOperate(parentvo);

		// 校验版本
		BDVersionValidationUtil.validateObject(vo);

		// 删除前引用对象校验
		deleteValidateVO(parentvo);

		// 缓存通知
		notifyVersionChangeWhenDataDeleted(parentvo);

		// 库操作
		dbDeleteVO(vo);

	}

	private void insertlockOperate(ShareruleVO vo) throws BusinessException {
		BizlockDataUtil.lockDataByBizlock(vo);
	}

	private void insertValidateVO(AggshareruleVO vo) throws BusinessException {
		IValidationService validateService = ValidationFrameworkUtil.createValidationService(new BDUniqueRuleValidate());
		validateService.validate(vo.getParentVO());
	}

	private void setInsertAuditInfo(ShareruleVO vo) {
		AuditInfoUtil.addData(vo);
	}

	private String dbInsertVO(AggshareruleVO vo) throws BusinessException {
		IMDPersistenceService service = MDPersistenceService.lookupPersistenceService();
		return service.saveBill(vo);
	}

	private AggshareruleVO retrieveVO(String pk) throws BusinessException {
		IMDPersistenceQueryService qryService = MDPersistenceService.lookupPersistenceQueryService();
		try {
			return qryService.queryBillOfVOByPK(AggshareruleVO.class, pk, false);
		} catch (MetaDataException e) {
			Logger.error(e.getMessage());
			throw new BusinessException(e);
		}
	}

	private void notifyVersionChangeWhenDataInserted() {
		CacheProxy.fireDataInserted(ShareruleVO.getDefaultTableName());
	}

	private void updatelockOperate(ShareruleVO vo) throws BusinessException {
		BDPKLockUtil.lockSuperVO(vo);
		BizlockDataUtil.lockDataByBizlock(vo);
	}

	private void updateValidateVO(AggshareruleVO vo) throws BusinessException {
		IValidationService validateService = ValidationFrameworkUtil.createValidationService(new BDUniqueRuleValidate());
		validateService.validate(vo.getParentVO());
	}

	private void setUpdateAuditInfo(ShareruleVO vo) {
		AuditInfoUtil.updateData(vo);
	}

	private String dbUpdateVO(AggshareruleVO vo) throws BusinessException {
		IMDPersistenceService service = MDPersistenceService.lookupPersistenceService();
		return service.saveBillWithRealDelete(vo);
	}

	protected void notifyVersionChangeWhenDataUpdated(ShareruleVO vo) throws BusinessException {
		CacheProxy.fireDataUpdated(ShareruleVO.getDefaultTableName(), vo.getPrimaryKey());
	}

	protected void deletelockOperate(ShareruleVO vo) throws BusinessException {
		BDPKLockUtil.lockSuperVO(vo);
	}

	protected void deleteValidateVO(ShareruleVO vo) throws BusinessException {
		IValidationService validateService = ValidationFrameworkUtil.createValidationService(new BDUniqueRuleValidate());
		validateService.validate(vo);
	}

	protected void notifyVersionChangeWhenDataDeleted(ShareruleVO vo) throws BusinessException {
		CacheProxy.fireDataDeleted(ShareruleVO.getDefaultTableName(), vo.getPrimaryKey());
	}

	protected void dbDeleteVO(AggshareruleVO vo) throws BusinessException {
		IMDPersistenceService service = MDPersistenceService.lookupPersistenceService();
		service.deleteBillFromDB(vo);
	}

}