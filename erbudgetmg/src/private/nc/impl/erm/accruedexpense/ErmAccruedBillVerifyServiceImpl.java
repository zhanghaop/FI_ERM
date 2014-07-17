package nc.impl.erm.accruedexpense;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.erm.accruedexpense.common.ErmAccruedBillConst;
import nc.bs.erm.accruedexpense.common.ErmAccruedBillUtils;
import nc.bs.erm.util.ErLockUtil;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.itf.fi.pub.Currency;
import nc.md.persist.framework.MDPersistenceService;
import nc.pubitf.erm.accruedexpense.IErmAccruedBillVerifyService;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.er.util.UFDoubleTool;
import nc.vo.erm.accruedexpense.AccruedDetailVO;
import nc.vo.erm.accruedexpense.AccruedVO;
import nc.vo.erm.accruedexpense.AccruedVerifyQueryVO;
import nc.vo.erm.accruedexpense.AccruedVerifyVO;
import nc.vo.erm.accruedexpense.AggAccruedBillVO;
import nc.vo.erm.util.VOUtils;
import nc.vo.fi.pub.SqlUtils;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.util.AuditInfoUtil;
import nc.vo.util.BDVersionValidationUtil;

public class ErmAccruedBillVerifyServiceImpl implements IErmAccruedBillVerifyService {

	@SuppressWarnings("unchecked")
	@Override
	public void verifyAccruedVOs(AccruedVerifyVO[] vos, String[] bxdpks) throws BusinessException {
		if (bxdpks == null || bxdpks.length == 0) {
			return;
		}
		BaseDAO dao = new BaseDAO();
		// 查询报销单原核销明细
		Collection<AccruedVerifyVO> delList = dao.retrieveByClause(AccruedVerifyVO.class, SqlUtils.getInStr(
				AccruedVerifyVO.PK_BXD, bxdpks, true));
		if ((vos == null || vos.length == 0) && delList.isEmpty()) {
			return;
		}
		AccruedVerifyVO[] delvos = delList.toArray(new AccruedVerifyVO[delList.size()]);
		// 并发控制，加锁、版本校验
		AggAccruedBillVO[] aggvos = concurrencyCheck(vos, delvos);
		// hash化预提单数据，备用
		Map<String, AccruedVO> parentDataMap = new HashMap<String, AccruedVO>();
		Map<String, AccruedDetailVO> detailDataMap = new HashMap<String, AccruedDetailVO>();
		for (int i = 0; i < aggvos.length; i++) {
			AccruedVO parentVO = aggvos[i].getParentVO();
			parentDataMap.put(parentVO.getPrimaryKey(), parentVO);

			AccruedDetailVO[] childrenVOs = aggvos[i].getChildrenVO();
			if (childrenVOs != null && childrenVOs.length > 0) {
				for (int j = 0; j < childrenVOs.length; j++) {
					detailDataMap.put(childrenVOs[j].getPrimaryKey(), childrenVOs[j]);
				}
			}
		}
		// 计算及校验总核销金额,设置核销信息
		Map<String, AccruedVO> parentUpdateMap = new HashMap<String, AccruedVO>();
		Map<String, AccruedDetailVO> detailUpdateMap = new HashMap<String, AccruedDetailVO>();
		// 计算处理删除的核销明细
		dealDeleteVOs(delvos, parentDataMap, detailDataMap, parentUpdateMap, detailUpdateMap);
		// 计算处理保存的核销明细
		// 核销明细行保存
		dealSaveVOs(vos, parentDataMap, detailDataMap, parentUpdateMap, detailUpdateMap);
		if (delvos != null && delvos.length > 0) {
			// 删除报销单原核销明细
			dao.deleteVOArray(delvos);
		}
		if (vos != null && vos.length > 0) {
			// 保存报销单当前核销明细
			dao.insertVOArray(vos);
		}
		// 更新预提单余额
		dao.updateVOArray(detailUpdateMap.values().toArray(new AccruedDetailVO[0]),
				new String[] { AccruedDetailVO.PREDICT_REST_AMOUNT });
		dao.updateVOArray(parentUpdateMap.values().toArray(new AccruedVO[0]),
				new String[] { AccruedVO.PREDICT_REST_AMOUNT });

	}

	/**
	 *暂存时，只保存核销明细，不回写预提单
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void tempVerifyAccruedVOs(AccruedVerifyVO[] vos, String[] bxdpks) throws BusinessException {

		if (bxdpks == null || bxdpks.length == 0) {
			return;
		}
		BaseDAO dao = new BaseDAO();
		// 查询报销单原核销明细
		Collection<AccruedVerifyVO> delList = dao.retrieveByClause(AccruedVerifyVO.class, SqlUtils.getInStr(
				AccruedVerifyVO.PK_BXD, bxdpks, true));
		if ((vos == null || vos.length == 0) && delList.isEmpty()) {
			return;
		}
		AccruedVerifyVO[] delvos = delList.toArray(new AccruedVerifyVO[delList.size()]);
		// 并发控制，加锁、版本校验
		AggAccruedBillVO[] aggvos = concurrencyCheck(vos, delvos);
		// hash化预提单数据，备用
		Map<String, AccruedVO> parentDataMap = new HashMap<String, AccruedVO>();
		Map<String, AccruedDetailVO> detailDataMap = new HashMap<String, AccruedDetailVO>();
		for (int i = 0; i < aggvos.length; i++) {
			AccruedVO parentVO = aggvos[i].getParentVO();
			parentDataMap.put(parentVO.getPrimaryKey(), parentVO);

			AccruedDetailVO[] childrenVOs = aggvos[i].getChildrenVO();
			if (childrenVOs != null && childrenVOs.length > 0) {
				for (int j = 0; j < childrenVOs.length; j++) {
					detailDataMap.put(childrenVOs[j].getPrimaryKey(), childrenVOs[j]);
				}
			}
		}
		if (vos != null && vos.length > 0) {
			UFDate currentDate = new UFDate(InvocationInfoProxy.getInstance().getBizDateTime());
			String currentUser = AuditInfoUtil.getCurrentUser();
			for (AccruedVerifyVO verifyVo : vos) {
				String pk_accrued_detail = verifyVo.getPk_accrued_detail();
				AccruedDetailVO detailVo = detailDataMap.get(pk_accrued_detail);
				AccruedVO parentVo = parentDataMap.get(verifyVo.getPk_accrued_bill());
				// 设置核销信息
				verifyVo.setVerify_date(currentDate);
				verifyVo.setVerify_man(currentUser);
				verifyVo.setEffectstatus(BXStatusConst.SXBZ_TEMP);
				if (verifyVo.getVerify_amount().compareTo(detailVo.getRest_amount()) == 0) {// 精度尾差处理
					verifyVo.setOrg_verify_amount(detailVo.getOrg_rest_amount());
					verifyVo.setGroup_verify_amount(detailVo.getGroup_rest_amount());
					verifyVo.setGlobal_verify_amount(detailVo.getGlobal_rest_amount());
				} else {
//					verifyVo.setOrg_verify_amount(verifyVo.getVerify_amount().multiply(detailVo.getOrg_currinfo()));
//					verifyVo.setGroup_verify_amount(verifyVo.getVerify_amount().multiply(detailVo.getGroup_currinfo()));
//					verifyVo.setGlobal_verify_amount(verifyVo.getVerify_amount().multiply(detailVo.getGlobal_currinfo()));
					resetBodyVerifyAmounts(verifyVo, detailVo, parentVo);
				}
			}
		}

		// 核销明细行保存
		if (delvos != null && delvos.length > 0) {
			// 删除报销单原核销明细
			dao.deleteVOArray(delvos);
		}
		if (vos != null && vos.length > 0) {
			// 保存报销单当前核销明细
			dao.insertVOArray(vos);
		}

	}

	/**
	 * 重新计算预提明细页签核销本币金额
	 *
	 * @throws BusinessException
	 */
	public void resetBodyVerifyAmounts(AccruedVerifyVO verifyVo, AccruedDetailVO detailVo, AccruedVO parentVo)
			throws BusinessException {

		if (parentVo == null || parentVo.getPk_currtype() == null || detailVo.getAssume_org() == null) {
			return;
		}
		String pk_group = parentVo.getPk_group();
		String pk_org = detailVo.getAssume_org();
		String pk_currtype = parentVo.getPk_currtype();
		UFDouble amount = verifyVo.getVerify_amount();
		UFDate billdate = parentVo.getBilldate();
		// 获取到汇率
		UFDouble orgRate = detailVo.getOrg_currinfo();
		UFDouble groupRate = detailVo.getGroup_currinfo();
		UFDouble globalRate = detailVo.getGlobal_currinfo();

		// 组织本币金额
		UFDouble orgAmount = Currency.getAmountByOpp(pk_org, pk_currtype, Currency.getOrgLocalCurrPK(pk_org), amount,
				orgRate, billdate);
		verifyVo.setOrg_verify_amount(orgAmount);

		// 集团、全局金额
		UFDouble[] money = Currency.computeGroupGlobalAmount(amount, orgAmount, pk_currtype, billdate, pk_org,
				pk_group, globalRate, groupRate);
		verifyVo.setGroup_verify_amount(money[0]);
		verifyVo.setGlobal_verify_amount(money[1]);

	}

	/**
	 * 计算处理保存的核销明细
	 *
	 * @param vos
	 * @param parentDataMap
	 * @param detailDataMap
	 * @param parentUpdateMap
	 * @param detailUpdateMap
	 * @throws BusinessException
	 */
	private void dealSaveVOs(AccruedVerifyVO[] vos, Map<String, AccruedVO> parentDataMap,
			Map<String, AccruedDetailVO> detailDataMap, Map<String, AccruedVO> parentUpdateMap,
			Map<String, AccruedDetailVO> detailUpdateMap) throws BusinessException {
		if (vos != null && vos.length > 0) {
			UFDate currentDate = new UFDate(InvocationInfoProxy.getInstance().getBizDateTime());
			String currentUser = AuditInfoUtil.getCurrentUser();
			for (int i = 0; i < vos.length; i++) {
				AccruedVerifyVO accruedVerifyVO = vos[i];
				String pk_accrued_detail = accruedVerifyVO.getPk_accrued_detail();
				AccruedDetailVO detailVO = detailDataMap.get(pk_accrued_detail);
				String pk_accrued_bill = accruedVerifyVO.getPk_accrued_bill();
				AccruedVO parentVO = parentDataMap.get(pk_accrued_bill);
				// 核销预提，减预提单预计余额
				detailVO.setPredict_rest_amount(detailVO.getPredict_rest_amount().sub(
						accruedVerifyVO.getVerify_amount()));
				parentVO.setPredict_rest_amount(parentVO.getPredict_rest_amount().sub(
						accruedVerifyVO.getVerify_amount()));
				// 检查金额合法性
				checkRestAmount(detailVO, AccruedVO.PREDICT_REST_AMOUNT);
				detailUpdateMap.put(pk_accrued_detail, detailVO);
				parentUpdateMap.put(pk_accrued_bill, parentVO);
				// 设置核销信息
				accruedVerifyVO.setVerify_date(currentDate);
				accruedVerifyVO.setVerify_man(currentUser);
				accruedVerifyVO.setEffectstatus(BXStatusConst.SXBZ_NO);
				if (accruedVerifyVO.getVerify_amount().compareTo(detailVO.getRest_amount()) == 0) {// 处理核销时，精度尾差问题
					accruedVerifyVO.setOrg_verify_amount(detailVO.getOrg_rest_amount());
					accruedVerifyVO.setGroup_verify_amount(detailVO.getGroup_rest_amount());
					accruedVerifyVO.setGlobal_verify_amount(detailVO.getGlobal_rest_amount());
				} else {
//					accruedVerifyVO.setOrg_verify_amount(accruedVerifyVO.getVerify_amount().multiply(
//							detailVO.getOrg_currinfo()));
//					accruedVerifyVO.setGroup_verify_amount(accruedVerifyVO.getVerify_amount().multiply(
//							detailVO.getGroup_currinfo()));
//					accruedVerifyVO.setGlobal_verify_amount(accruedVerifyVO.getVerify_amount().multiply(
//							detailVO.getGlobal_currinfo()));
					resetBodyVerifyAmounts(accruedVerifyVO, detailVO, parentVO);
				}
			}
		}
	}

	/**
	 * 计算处理删除的核销明细
	 *
	 * @param delvos
	 * @param parentDataMap
	 * @param detailDataMap
	 * @param parentUpdateMap
	 * @param detailUpdateMap
	 * @throws BusinessException
	 */
	private void dealDeleteVOs(AccruedVerifyVO[] delvos, Map<String, AccruedVO> parentDataMap,
			Map<String, AccruedDetailVO> detailDataMap, Map<String, AccruedVO> parentUpdateMap,
			Map<String, AccruedDetailVO> detailUpdateMap) throws BusinessException {
		if (delvos != null && delvos.length > 0) {
			for (int i = 0; i < delvos.length; i++) {
				AccruedVerifyVO accruedVerifyVO = delvos[i];
				String pk_accrued_detail = accruedVerifyVO.getPk_accrued_detail();
				AccruedDetailVO detailVO = detailDataMap.get(pk_accrued_detail);
				String pk_accrued_bill = accruedVerifyVO.getPk_accrued_bill();
				AccruedVO parentVO = parentDataMap.get(pk_accrued_bill);
				// 取消核销预提，加预提单预计余额
				if (accruedVerifyVO.getEffectstatus() == null
						|| accruedVerifyVO.getEffectstatus() != BXStatusConst.SXBZ_TEMP) {

					detailVO.setPredict_rest_amount(detailVO.getPredict_rest_amount().add(
							accruedVerifyVO.getVerify_amount()));
					parentVO.setPredict_rest_amount(parentVO.getPredict_rest_amount().add(
							accruedVerifyVO.getVerify_amount()));
				}
				// 检查金额合法性
				checkRestAmount(detailVO, AccruedVO.PREDICT_REST_AMOUNT);
				detailUpdateMap.put(pk_accrued_detail, detailVO);
				parentUpdateMap.put(pk_accrued_bill, parentVO);
			}
		}
	}

	@Override
	public void effectAccruedVerifyVOs(JKBXVO[] bxvos) throws BusinessException {
		dealEffectVerifyVOs(bxvos, true);
	}

	@Override
	public void uneffectAccruedVerifyVOs(JKBXVO[] bxvos) throws BusinessException {
		dealEffectVerifyVOs(bxvos, false);
	}

	private void dealEffectVerifyVOs(JKBXVO[] bxvos, boolean isEffect) throws DAOException, BusinessException {
		if (bxvos == null || bxvos.length == 0) {
			return;
		}
		BaseDAO dao = new BaseDAO();
		// 查询报销单核销明细
		Map<String, JKBXVO> bxmap = VOUtils.changeCollection2Map(Arrays.asList(bxvos));
		@SuppressWarnings("unchecked")
		List<AccruedVerifyVO> verifyList = (List<AccruedVerifyVO>) dao.retrieveByClause(AccruedVerifyVO.class, SqlUtils
				.getInStr(AccruedVerifyVO.PK_BXD, bxmap.keySet().toArray(new String[0]), true));
		if (verifyList == null || verifyList.size() == 0) {
			return;
		}
		Map<String, List<AccruedVerifyVO>> verifyMap = VOUtils.changeCollection2MapList(verifyList,
				new String[] { AccruedVerifyVO.PK_BXD });
		AccruedVerifyVO[] vos = verifyList.toArray(new AccruedVerifyVO[verifyList.size()]);
		// 获得相关预提单PKs
		Set<String> headPks = new HashSet<String>();
		for (Entry<String, List<AccruedVerifyVO>> verifyEntry : verifyMap.entrySet()) {
			JKBXVO bxvo = bxmap.get(verifyEntry.getKey());
			UFDate shrq = isEffect ? bxvo.getParentVO().getShrq().getDate() : null;
			List<AccruedVerifyVO> bxVerifyList = verifyEntry.getValue();
			for (AccruedVerifyVO accruedVerifyVO : bxVerifyList) {
				String pk_accrued_bill = accruedVerifyVO.getPk_accrued_bill();
				headPks.add(pk_accrued_bill);
				// 设置核销明细生效状态、生效日期
				if (isEffect) {
					accruedVerifyVO.setEffectstatus(BXStatusConst.SXBZ_VALID);
					accruedVerifyVO.setEffectdate(shrq);
				} else {
					accruedVerifyVO.setEffectstatus(BXStatusConst.SXBZ_NO);
					accruedVerifyVO.setEffectdate(null);
				}
			}
			bxvo.setAccruedVerifyVO(bxVerifyList.toArray(new AccruedVerifyVO[bxVerifyList.size()]));
		}
		// 预提单加锁
		ErLockUtil.lockByPk(ErmAccruedBillConst.Accrued_Lock_Key, headPks);
		// 查询预提单
		String[] billPks = headPks.toArray(new String[0]);
		@SuppressWarnings("unchecked")
		Collection<AggAccruedBillVO> aggvos = MDPersistenceService.lookupPersistenceQueryService().queryBillOfVOByPKs(
				AggAccruedBillVO.class, billPks, false);
		// hash化预提单数据，备用
		Map<String, AccruedVO> parentDataMap = new HashMap<String, AccruedVO>();
		Map<String, AccruedDetailVO> detailDataMap = new HashMap<String, AccruedDetailVO>();
		for (AggAccruedBillVO aggvo : aggvos) {
			AccruedVO parentVO = aggvo.getParentVO();
			parentDataMap.put(parentVO.getPrimaryKey(), parentVO);

			AccruedDetailVO[] childrenVOs = aggvo.getChildrenVO();
			if (childrenVOs != null && childrenVOs.length > 0) {
				for (int j = 0; j < childrenVOs.length; j++) {
					detailDataMap.put(childrenVOs[j].getPrimaryKey(), childrenVOs[j]);
				}
			}
		}
		// 计算及校验总核销金额,设置核销信息
		Map<String, AccruedVO> parentUpdateMap = new HashMap<String, AccruedVO>();
		Map<String, AccruedDetailVO> detailUpdateMap = new HashMap<String, AccruedDetailVO>();
		// 计算处理生效/取消生效的核销明细
		dealEffectVOs(isEffect, vos, parentDataMap, detailDataMap, parentUpdateMap, detailUpdateMap);
		// 更新核销明细的生效信息
		dao.updateVOArray(vos, new String[] { AccruedVerifyVO.EFFECTSTATUS, AccruedVerifyVO.EFFECTDATE });
		// 更新预提单余额
		dao.updateVOArray(detailUpdateMap.values().toArray(new AccruedDetailVO[0]), new String[] {
				AccruedDetailVO.REST_AMOUNT, AccruedDetailVO.ORG_REST_AMOUNT, AccruedDetailVO.GROUP_REST_AMOUNT,
				AccruedDetailVO.GLOBAL_REST_AMOUNT, AccruedDetailVO.VERIFY_AMOUNT, AccruedDetailVO.ORG_VERIFY_AMOUNT,
				AccruedDetailVO.GROUP_VERIFY_AMOUNT, AccruedDetailVO.GLOBAL_VERIFY_AMOUNT });
		dao.updateVOArray(parentUpdateMap.values().toArray(new AccruedVO[0]), new String[] { AccruedVO.REST_AMOUNT,
				AccruedVO.ORG_REST_AMOUNT, AccruedVO.GROUP_REST_AMOUNT, AccruedVO.GLOBAL_REST_AMOUNT,
				AccruedVO.VERIFY_AMOUNT, AccruedVO.ORG_VERIFY_AMOUNT, AccruedVO.GROUP_VERIFY_AMOUNT,
				AccruedVO.GLOBAL_VERIFY_AMOUNT });
	}

	/**
	 * 计算处理生效/取消生效的核销明细
	 *
	 * @param isEffect
	 * @param vos
	 * @param parentDataMap
	 * @param detailDataMap
	 * @param parentUpdateMap
	 * @param detailUpdateMap
	 * @throws BusinessException
	 */
	private void dealEffectVOs(boolean isEffect, AccruedVerifyVO[] vos, Map<String, AccruedVO> parentDataMap,
			Map<String, AccruedDetailVO> detailDataMap, Map<String, AccruedVO> parentUpdateMap,
			Map<String, AccruedDetailVO> detailUpdateMap) throws BusinessException {
		for (int i = 0; i < vos.length; i++) {
			AccruedVerifyVO accruedVerifyVO = vos[i];
			String pk_accrued_bill = accruedVerifyVO.getPk_accrued_bill();
			AccruedVO parentVO = parentDataMap.get(pk_accrued_bill);
			String pk_accrued_detail = accruedVerifyVO.getPk_accrued_detail();
			AccruedDetailVO detailVO = detailDataMap.get(pk_accrued_detail);
			// 核销预提生效，加核销金额、减预提单余额;取消生效减核销金额、增加余额;
			if (isEffect) {
				detailVO.setVerify_amount(UFDoubleTool.sum(detailVO.getVerify_amount(), accruedVerifyVO
						.getVerify_amount()));
				detailVO.setOrg_verify_amount(UFDoubleTool.sum(detailVO.getOrg_verify_amount(), accruedVerifyVO
						.getOrg_verify_amount()));
				detailVO.setGroup_verify_amount(UFDoubleTool.sum(detailVO.getGroup_verify_amount(), accruedVerifyVO
						.getGroup_verify_amount()));
				detailVO.setGlobal_verify_amount(UFDoubleTool.sum(detailVO.getGlobal_verify_amount(), accruedVerifyVO
						.getGlobal_verify_amount()));

				parentVO.setVerify_amount(UFDoubleTool.sum(parentVO.getVerify_amount(), accruedVerifyVO
						.getVerify_amount()));
//				parentVO.setOrg_verify_amount(UFDoubleTool.sum(parentVO.getOrg_verify_amount(), accruedVerifyVO
//						.getOrg_verify_amount()));
//				parentVO.setGroup_verify_amount(UFDoubleTool.sum(parentVO.getGroup_verify_amount(), accruedVerifyVO
//						.getGroup_verify_amount()));
//				parentVO.setGlobal_verify_amount(UFDoubleTool.sum(parentVO.getGlobal_verify_amount(), accruedVerifyVO
//						.getGlobal_verify_amount()));

				detailVO.setRest_amount(detailVO.getRest_amount().sub(accruedVerifyVO.getVerify_amount()));
				detailVO.setOrg_rest_amount(detailVO.getOrg_rest_amount().sub(accruedVerifyVO.getOrg_verify_amount()));
				detailVO.setGroup_rest_amount(detailVO.getGroup_rest_amount().sub(
						accruedVerifyVO.getGroup_verify_amount()));
				detailVO.setGlobal_rest_amount(detailVO.getGlobal_rest_amount().sub(
						accruedVerifyVO.getGlobal_verify_amount()));

				parentVO.setRest_amount(parentVO.getRest_amount().sub(accruedVerifyVO.getVerify_amount()));
//				parentVO.setOrg_rest_amount(parentVO.getOrg_rest_amount().sub(accruedVerifyVO.getOrg_verify_amount()));
//				parentVO.setGroup_rest_amount(parentVO.getGroup_rest_amount().sub(
//						accruedVerifyVO.getGroup_verify_amount()));
//				parentVO.setGlobal_rest_amount(parentVO.getGlobal_rest_amount().sub(
//						accruedVerifyVO.getGlobal_verify_amount()));
			} else {
				detailVO.setVerify_amount(UFDoubleTool.sub(detailVO.getVerify_amount(), accruedVerifyVO
						.getVerify_amount()));
				detailVO.setOrg_verify_amount(UFDoubleTool.sub(detailVO.getOrg_verify_amount(), accruedVerifyVO
						.getOrg_verify_amount()));
				detailVO.setGroup_verify_amount(UFDoubleTool.sub(detailVO.getGroup_verify_amount(), accruedVerifyVO
						.getGroup_verify_amount()));
				detailVO.setGlobal_verify_amount(UFDoubleTool.sub(detailVO.getGlobal_verify_amount(), accruedVerifyVO
						.getGlobal_verify_amount()));

				parentVO.setVerify_amount(UFDoubleTool.sub(parentVO.getVerify_amount(), accruedVerifyVO
						.getVerify_amount()));
//				parentVO.setOrg_verify_amount(UFDoubleTool.sub(parentVO.getOrg_verify_amount(), accruedVerifyVO
//						.getOrg_verify_amount()));
//				parentVO.setGroup_verify_amount(UFDoubleTool.sub(parentVO.getGroup_verify_amount(), accruedVerifyVO
//						.getGroup_verify_amount()));
//				parentVO.setGlobal_verify_amount(UFDoubleTool.sub(parentVO.getGlobal_verify_amount(), accruedVerifyVO
//						.getGlobal_verify_amount()));

				detailVO.setRest_amount(detailVO.getRest_amount().add(accruedVerifyVO.getVerify_amount()));
				detailVO.setOrg_rest_amount(detailVO.getOrg_rest_amount().add(accruedVerifyVO.getOrg_verify_amount()));
				detailVO.setGroup_rest_amount(detailVO.getGroup_rest_amount().add(
						accruedVerifyVO.getGroup_verify_amount()));
				detailVO.setGlobal_rest_amount(detailVO.getGlobal_rest_amount().add(
						accruedVerifyVO.getGlobal_verify_amount()));

				parentVO.setRest_amount(parentVO.getRest_amount().add(accruedVerifyVO.getVerify_amount()));
//				parentVO.setOrg_rest_amount(parentVO.getOrg_rest_amount().add(accruedVerifyVO.getOrg_verify_amount()));
//				parentVO.setGroup_rest_amount(parentVO.getGroup_rest_amount().add(
//						accruedVerifyVO.getGroup_verify_amount()));
//				parentVO.setGlobal_rest_amount(parentVO.getGlobal_rest_amount().add(
//						accruedVerifyVO.getGlobal_verify_amount()));
			}
			// 前面设置完表头原币金额后，最后根据表头汇率重新计算金额
			ErmAccruedBillUtils.resetHeadAmounts(parentVO);

			// 检查金额合法性
			checkRestAmount(detailVO, AccruedVO.REST_AMOUNT);

			detailUpdateMap.put(pk_accrued_detail, detailVO);
			parentUpdateMap.put(pk_accrued_bill, parentVO);
		}
	}




	/**
	 * 并发控制，加锁、版本校验
	 *
	 * @param vos
	 * @param delvos
	 * @return
	 * @throws BusinessException
	 */
	private AggAccruedBillVO[] concurrencyCheck(AccruedVerifyVO[] vos, AccruedVerifyVO[] delvos)
			throws BusinessException {
		// 遍历预提核销明细，按预提单pk分组
		Set<String> headPks = new HashSet<String>();
		HashMap<String, String> newTsMap = new HashMap<String, String>();
		if (delvos != null && delvos.length > 0) {
			// 待删除的不需要与预提单校验版本
			for (AccruedVerifyVO accruedVerifyVO : delvos) {
				String pk_accrued_bill = accruedVerifyVO.getPk_accrued_bill();
				headPks.add(pk_accrued_bill);
			}
		}
		if (vos != null && vos.length > 0) {
			for (int i = 0; i < vos.length; i++) {
				AccruedVerifyVO accruedVerifyVO = vos[i];
				String pk_accrued_bill = accruedVerifyVO.getPk_accrued_bill();
				String tempts = newTsMap.get(pk_accrued_bill);
				UFDateTime ts = accruedVerifyVO.getTs();
				if (ts == null) {
					throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("accruedbill_0","02011001-0011")/*@res "预提核销明细时间戳无效，请检查"*/);
				}
				if (tempts == null) {
					newTsMap.put(pk_accrued_bill, ts.toString());
				} else {
					if (tempts != null && !tempts.equals(ts.toString())) {
						throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("accruedbill_0","02011001-0012")/*@res "预提核销明细时间戳不同，不可同时核销相同预提单"*/);
					}
				}
				headPks.add(pk_accrued_bill);
			}
		}

		// 加锁
		ErLockUtil.lockByPk(ErmAccruedBillConst.Accrued_Lock_Key, headPks);
		// 查询预提单
		HashMap<String, String> oldTsMap = new HashMap<String, String>();
		String[] billPks = headPks.toArray(new String[0]);
		@SuppressWarnings("unchecked")
		Collection<AggAccruedBillVO> aggvos = MDPersistenceService.lookupPersistenceQueryService().queryBillOfVOByPKs(
				AggAccruedBillVO.class, billPks, false);
		for (AggAccruedBillVO aggvo : aggvos) {
			AccruedVO accruedVO = aggvo.getParentVO();
			String pk = accruedVO.getPrimaryKey();
			oldTsMap.put(pk, accruedVO.getTs().toString());
		}
		// 数据版本校验
		compareTs(newTsMap, oldTsMap);

		// 返回预提单业务数据
		return aggvos.toArray(new AggAccruedBillVO[aggvos.size()]);
	}

	private void compareTs(HashMap<String, String> newTsMap, HashMap<String, String> oldTsMap) throws BusinessException {
		// 进行比较
		Iterator<String> ite = newTsMap.keySet().iterator();
		while (ite.hasNext()) {
			String key = (String) ite.next();
			String newTs = newTsMap.get(key);
			String oldTs = oldTsMap.get(key);
			if (oldTs == null) {
				throw new BusinessException(BDVersionValidationUtil.getDelInfo());
			}
			if (!oldTs.equals(newTs)) {
				throw new BusinessException(BDVersionValidationUtil.getUpdateInfo());
			}
		}
	}

	private void checkRestAmount(AccruedDetailVO detailVO, String amountField) throws BusinessException {
		UFDouble rest_amount = (UFDouble) detailVO.getAttributeValue(amountField);
		if (rest_amount.compareTo(UFDouble.ZERO_DBL) < 0) {// 预计余额小于0的情况下
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("accruedbill_0","02011001-0013")/*@res "预提单余额不足，请重新核销预提!"*/);
		}
		if (rest_amount.compareTo(detailVO.getAmount()) > 0) {// 预计余额大于预提单金额情况
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("accruedbill_0","02011001-0014")/*@res "保存核销预提金额失败，核销预提的金额超出预提单的余额！"*/);
		}
	}

	@Override
	public AggAccruedBillVO[] queryAggAccruedBillVOsByWhere(AccruedVerifyQueryVO queryvo) throws BusinessException {
		// 基础查询条件：预提单生效+预计余额大于0+同组织+同币种
		String sqlwhere = AccruedVO.EFFECTSTATUS + " = " + BXStatusConst.SXBZ_VALID + " and "
				+ AccruedVO.PREDICT_REST_AMOUNT + "> 0 and " + AccruedVO.PK_ORG + " = '" + queryvo.getPk_org()
				+ "' and " + AccruedVO.PK_CURRTYPE + " = '" + queryvo.getPk_currtype() + "'";

		if (queryvo.getWhere() == null) {
			sqlwhere += " and " + AccruedVO.OPERATOR + " = '" + queryvo.getPk_bxr() + "'";
		} else {
			sqlwhere += " and " + queryvo.getWhere();
		}
		AggAccruedBillVO[] aggvos = new ErmAccruedBillQueryImpl().queryBillByWhere(sqlwhere);

		// 合并、补充已经核销的明细
		aggvos = combianVerifyVOs(queryvo, aggvos);
		// 过滤预提单表体无预计余额的行
		filtAccruedDetailVOs(aggvos,queryvo);
		return aggvos;
	}

	private void filtAccruedDetailVOs(AggAccruedBillVO[] aggvos, AccruedVerifyQueryVO queryvo) {
		if (aggvos == null || aggvos.length == 0) {
			return;
		}
		List<String> oldDetailPks = new ArrayList<String>();
		AccruedVerifyVO[] verifyvos = queryvo.getVerifyvos();
		if (verifyvos != null && verifyvos.length > 0) {
			for (AccruedVerifyVO verifyvo : verifyvos) {
				if (!oldDetailPks.contains(verifyvo.getPk_accrued_detail())) {
					oldDetailPks.add(verifyvo.getPk_accrued_detail());
				}
			}
		}
		for (AggAccruedBillVO vo : aggvos) {
			List<AccruedDetailVO> children = new ArrayList<AccruedDetailVO>();
			AccruedDetailVO[] detailvos = vo.getChildrenVO();
			if (detailvos != null && detailvos.length > 0) {
				for (AccruedDetailVO child : detailvos) {
					//注意核销明细中有的明细行不能被剔除掉，原因暂存-》修改已经无预计余额的单据时，应该能正常修改，不能剔除，否则前台在界面展现时会报错
					if (child.getPredict_rest_amount() != null
							&& (child.getPredict_rest_amount().compareTo(UFDouble.ZERO_DBL) > 0 || oldDetailPks
									.contains(child.getPk_accrued_detail()))) {
						children.add(child);
					}
				}
			}
			vo.setChildrenVO(children.toArray(new AccruedDetailVO[children.size()]));
		}
	}

	/**
	 * 合并、补充已经核销的明细
	 *
	 * @param queryvo
	 * @param aggvos
	 * @return
	 * @throws BusinessException
	 */
	private AggAccruedBillVO[] combianVerifyVOs(AccruedVerifyQueryVO queryvo, AggAccruedBillVO[] aggvos)
			throws BusinessException {
		// hash化预提单数据，备用
		Map<String, AccruedVO> parentDataMap = new HashMap<String, AccruedVO>();
		Map<String, AccruedDetailVO> detailDataMap = new HashMap<String, AccruedDetailVO>();
		for (int i = 0; i < aggvos.length; i++) {
			AccruedVO parentVO = aggvos[i].getParentVO();
			parentDataMap.put(parentVO.getPrimaryKey(), parentVO);
			// 清空核销金额，核销前台使用
			clearVerifyAmount(parentVO);
			AccruedDetailVO[] childrenVOs = aggvos[i].getChildrenVO();
			if (childrenVOs != null && childrenVOs.length > 0) {
				for (int j = 0; j < childrenVOs.length; j++) {
					detailDataMap.put(childrenVOs[j].getPrimaryKey(), childrenVOs[j]);
					// 清空核销金额，核销前台使用
					clearVerifyAmount(childrenVOs[j]);
				}
			}
		}
		Map<String,AccruedVerifyVO> oldverifyMap = new HashMap<String,AccruedVerifyVO>();
		if(queryvo.getOldverifyvos() != null && queryvo.getOldverifyvos().length > 0){
			for(AccruedVerifyVO old : queryvo.getOldverifyvos()){
				oldverifyMap.put(old.getPk_accrued_detail(),old);
			}
		}
		AccruedVerifyVO[] verifyvos = queryvo.getVerifyvos();
		if (verifyvos != null && verifyvos.length > 0) {
			// 过滤出，已经被核销，但是未查询出来的预提单；且设置预提单的预计余额=余额+被当前核销记录核销的金额
			List<String> querylist = new ArrayList<String>();
			Map<String, List<AccruedVerifyVO>> queryVerifyMap = new HashMap<String, List<AccruedVerifyVO>>();
			for (int i = 0; i < verifyvos.length; i++) {
				AccruedVerifyVO accruedVerifyVO = verifyvos[i];
				String pk_accrued_bill = accruedVerifyVO.getPk_accrued_bill();
				AccruedVO headvo = parentDataMap.get(pk_accrued_bill);
				String pk_accrued_detail = accruedVerifyVO.getPk_accrued_detail();
				AccruedDetailVO detailvo = detailDataMap.get(pk_accrued_detail);
				if (headvo == null ) {
					querylist.add(pk_accrued_bill);
					List<AccruedVerifyVO> queryVerifyList = queryVerifyMap.get(pk_accrued_bill);
					if (queryVerifyList == null) {
						queryVerifyList = new ArrayList<AccruedVerifyVO>();
						queryVerifyMap.put(pk_accrued_bill, queryVerifyList);
					}
					queryVerifyList.add(accruedVerifyVO);
				} else {
						// 设置预提单的预计余额=预计余额+被当前核销记录核销的金额
						AccruedVerifyVO oldverify = oldverifyMap.get(accruedVerifyVO.getPk_accrued_detail());
						if (oldverify != null && oldverify.getEffectstatus() != BXStatusConst.SXBZ_TEMP) {
							detailvo.setPredict_rest_amount(detailvo.getPredict_rest_amount().add(
									oldverify.getVerify_amount()));
							headvo.setPredict_rest_amount(headvo.getPredict_rest_amount().add(
									oldverify.getVerify_amount()));
						}
				}
			}
			// 补充查询，已经被核销不在查询记录中的数据
			aggvos = queryAddedVerifyInfo(aggvos, querylist, queryVerifyMap,oldverifyMap);
		}
		return aggvos;
	}

	private void clearVerifyAmount(AccruedVO parentVO) {
		parentVO.setVerify_amount(UFDouble.ZERO_DBL);
		parentVO.setOrg_verify_amount(UFDouble.ZERO_DBL);
		parentVO.setGroup_verify_amount(UFDouble.ZERO_DBL);
		parentVO.setGlobal_verify_amount(UFDouble.ZERO_DBL);

	}

	private void clearVerifyAmount(AccruedDetailVO detailVO) {
		detailVO.setVerify_amount(UFDouble.ZERO_DBL);
		detailVO.setOrg_verify_amount(UFDouble.ZERO_DBL);
		detailVO.setGroup_verify_amount(UFDouble.ZERO_DBL);
		detailVO.setGlobal_verify_amount(UFDouble.ZERO_DBL);
	}

	/**
	 * 补充查询，已经被核销不在查询记录中的数据
	 *
	 * @param aggvos
	 * @param querylist
	 * @param queryVerifyMap
	 * @param oldverifyMap
	 * @return
	 * @throws BusinessException
	 */
	private AggAccruedBillVO[] queryAddedVerifyInfo(AggAccruedBillVO[] aggvos, List<String> querylist,
			Map<String, List<AccruedVerifyVO>> queryVerifyMap, Map<String, AccruedVerifyVO> oldverifyMap) throws BusinessException {
		if (!querylist.isEmpty()) {
			AggAccruedBillVO[] verifyaggvos = new ErmAccruedBillQueryImpl().queryBillByPks(querylist
					.toArray(new String[querylist.size()]));
			// 设置补充查询的预提单预计余额
			for (int i = 0; i < verifyaggvos.length; i++) {
				AggAccruedBillVO aggvo = verifyaggvos[i];
				AccruedVO parentVO = aggvo.getParentVO();
				// 清空核销金额，核销前台使用
				clearVerifyAmount(parentVO);

				List<AccruedVerifyVO> verifyList = queryVerifyMap.get(parentVO.getPrimaryKey());
				// 被冲销过的预提单，明细行不可能为空
				Map<String, AccruedDetailVO> vdetailmap = new HashMap<String, AccruedDetailVO>();
				AccruedDetailVO[] childrenVO = aggvo.getChildrenVO();
				for (int j = 0; j < childrenVO.length; j++) {
					AccruedDetailVO detailvo = childrenVO[j];
					vdetailmap.put(detailvo.getPrimaryKey(), detailvo);
					// 清空核销金额，核销前台使用
					clearVerifyAmount(detailvo);
				}

				for (AccruedVerifyVO accruedVerifyVO : verifyList) {
					AccruedDetailVO dvo = vdetailmap.get(accruedVerifyVO.getPk_accrued_detail());
					AccruedVerifyVO oldverify = oldverifyMap.get(accruedVerifyVO.getPk_accrued_detail());
					if (oldverify != null && (oldverify.getEffectstatus() != null
							&& oldverify.getEffectstatus() != BXStatusConst.SXBZ_TEMP)) {
						dvo.setPredict_rest_amount(dvo.getPredict_rest_amount().add(oldverify.getVerify_amount()));
						parentVO.setPredict_rest_amount(parentVO.getPredict_rest_amount().add(
								oldverify.getVerify_amount()));
					}
				}
			}
			if (aggvos == null || aggvos.length == 0) {
				aggvos = verifyaggvos;
			} else {
				// 合并两个数组
				AggAccruedBillVO[] combinvos = new AggAccruedBillVO[aggvos.length + verifyaggvos.length];
				System.arraycopy(aggvos, 0, combinvos, 0, aggvos.length);
				System.arraycopy(verifyaggvos, 0, combinvos, aggvos.length, verifyaggvos.length);
				aggvos = combinvos;
			}
		}
		return aggvos;
	}

	/**
	 * 是否存在核销预提未生效或者暂存的单据
	 */
	@Override
	public boolean isExistAccruedVerifyEffectStatusNo(String pkAccruedBill) throws BusinessException {
		String sql = AccruedVerifyVO.PK_ACCRUED_BILL + "= '" + pkAccruedBill + " ' and (" + AccruedVerifyVO.EFFECTSTATUS
				+ "=" + ErmAccruedBillConst.EFFECTSTATUS_NO + " or " + AccruedVerifyVO.EFFECTSTATUS + "="
				+ ErmAccruedBillConst.EFFECTSTATUS_Temp + ") and dr=0";
		Collection<?> vos = MDPersistenceService.lookupPersistenceQueryService().queryBillOfVOByCond(
				AccruedVerifyVO.class, sql, true);

		if (vos != null && vos.size() > 0) {
			return true;
		}
		return false;
	}

}