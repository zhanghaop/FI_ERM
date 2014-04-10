package nc.bs.erm.expenseaccount;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.logging.Logger;
import nc.vo.erm.expenseaccount.ExpenseAccountVO;
import nc.vo.erm.expenseaccount.ExpenseBalVO;
import nc.vo.fip.pub.SqlTools;
import nc.vo.fipub.freevalue.util.MD5;
import nc.vo.fipub.utils.ArrayUtil;
import nc.vo.fipub.utils.KeyLock;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;

/**
 * 费用帐余额表维护业务类
 *
 * @author lvhj
 *
 */
public class ExpenseAccountBalanceBO {

	public static final MD5 _MD5 = new MD5();
	/**
	 * 构造MD5的费用帐维度
	 */
	private static final String[] DimFields = new String[] { ExpenseBalVO.ACCYEAR, ExpenseBalVO.ACCMONTH,
			ExpenseBalVO.BILLDATE, ExpenseBalVO.BILLSTATUS, ExpenseBalVO.SRC_BILLTYPE, ExpenseBalVO.SRC_TRADETYPE,
			ExpenseBalVO.ASSUME_ORG, ExpenseBalVO.ASSUME_DEPT, ExpenseBalVO.PK_PCORG, ExpenseBalVO.PK_RESACOSTCENTER,
			ExpenseBalVO.PK_IOBSCLASS, ExpenseBalVO.PK_PROJECT, ExpenseBalVO.PK_WBS, ExpenseBalVO.PK_CHECKELE,
			ExpenseBalVO.PK_SUPPLIER, ExpenseBalVO.PK_CUSTOMER, ExpenseBalVO.DEFITEM30, ExpenseBalVO.DEFITEM29,
			ExpenseBalVO.DEFITEM28, ExpenseBalVO.DEFITEM27, ExpenseBalVO.DEFITEM26, ExpenseBalVO.DEFITEM25,
			ExpenseBalVO.DEFITEM24, ExpenseBalVO.DEFITEM23, ExpenseBalVO.DEFITEM22, ExpenseBalVO.DEFITEM21,
			ExpenseBalVO.DEFITEM20, ExpenseBalVO.DEFITEM19, ExpenseBalVO.DEFITEM18, ExpenseBalVO.DEFITEM17,
			ExpenseBalVO.DEFITEM16, ExpenseBalVO.DEFITEM15, ExpenseBalVO.DEFITEM14, ExpenseBalVO.DEFITEM13,
			ExpenseBalVO.DEFITEM12, ExpenseBalVO.DEFITEM11, ExpenseBalVO.DEFITEM10, ExpenseBalVO.DEFITEM9,
			ExpenseBalVO.DEFITEM8, ExpenseBalVO.DEFITEM7, ExpenseBalVO.DEFITEM6, ExpenseBalVO.DEFITEM5,
			ExpenseBalVO.DEFITEM4, ExpenseBalVO.DEFITEM3, ExpenseBalVO.DEFITEM2, ExpenseBalVO.DEFITEM1,
			ExpenseBalVO.PK_ORG, ExpenseBalVO.PK_GROUP, ExpenseBalVO.BX_ORG, ExpenseBalVO.BX_GROUP,
			ExpenseBalVO.BX_FIORG, ExpenseBalVO.BX_CASHPROJ, ExpenseBalVO.BX_DWBM, ExpenseBalVO.BX_DEPTID,
			ExpenseBalVO.BX_JSFS, ExpenseBalVO.BX_CASHITEM, ExpenseBalVO.BX_JKBXR, ExpenseBalVO.PK_CURRTYPE,
			ExpenseBalVO.ISWRITEOFF, ExpenseBalVO.PAYMAN, ExpenseBalVO.PAYDATE, ExpenseBalVO.PAYFLAG,
			ExpenseBalVO.PK_PAYORG };

	private ExpenseAccountDAO dao;

	private ExpenseAccountDAO getDAO() {
		if (dao == null) {
			dao = new ExpenseAccountDAO();
		}
		return dao;
	}

	/**
	 * 同步余额表
	 *
	 * @param vos
	 * @throws BusinessException
	 */
	@SuppressWarnings({ "unchecked" })
	public void synchExpenseBal(ExpenseAccountVO[] vos,
			ExpenseAccountVO[] deletevos) throws BusinessException {

		if (ArrayUtil.isArrayIsNull((Object[]) vos)
				&& ArrayUtil.isArrayIsNull((Object[]) deletevos)) {
			return;
		}
		//MD5 分组明细账，且进行汇总
		Map<String, ExpenseBalVO> map = getMD5Map(vos, deletevos);
		// 加锁
		lockbalance(map.keySet());
		// 查询已经存在的余额信息
		BaseDAO basedao = new BaseDAO();
		Collection<ExpenseBalVO> c = null;
		try {
			c = basedao.retrieveByClause(
					ExpenseBalVO.class,
					SqlTools.getInStr(ExpenseBalVO.MD5,
							map.keySet().toArray(new String[0]), true));
		} catch (DAOException e) {
			Logger.error("根据MD5查询费用余额表出错");
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0024")/*@res "根据MD5查询费用余额表出错"*/, e);
		} catch (BusinessException e) {
			Logger.error("根据MD5查询费用余额表出错");
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0024")/*@res "根据MD5查询费用余额表出错"*/, e);
		}

		List<ExpenseBalVO> updateList = new ArrayList<ExpenseBalVO>();

		if (c != null && !c.isEmpty()) {
			for (ExpenseBalVO vo : c) {
				String md5 = vo.getMd5();
				ExpenseBalVO mapvo = map.get(md5);
				vo.setAssume_amount(getAmount(mapvo.getAssume_amount(),
						vo.getAssume_amount(), true));
				vo.setOrg_amount(getAmount(mapvo.getOrg_amount(),
						vo.getOrg_amount(), true));
				vo.setGroup_amount(getAmount(mapvo.getGroup_amount(),
						vo.getGroup_amount(), true));
				vo.setGlobal_amount(getAmount(mapvo.getGlobal_amount(),
						vo.getGlobal_amount(), true));
				updateList.add(vo);
				map.remove(md5);
			}
		}
		List<ExpenseBalVO> insertList = new ArrayList<ExpenseBalVO>();
		for (Entry<String, ExpenseBalVO> tempmap : map.entrySet()) {
			ExpenseBalVO value = tempmap.getValue();
			insertList.add(value);
		}
		// 新增余额信息
		if (!insertList.isEmpty()) {
			getDAO().insertAccountBalanceVO(
					insertList.toArray(new ExpenseBalVO[insertList.size()]));
		}
		// 更新余额信息
		if (!updateList.isEmpty()) {
			getDAO().updateAccountBalanceVO(
					updateList.toArray(new ExpenseBalVO[updateList.size()]),
					new String[] { ExpenseBalVO.ASSUME_AMOUNT,
							ExpenseBalVO.ORG_AMOUNT, ExpenseBalVO.GROUP_AMOUNT,
							ExpenseBalVO.GLOBAL_AMOUNT });
		}

	}

	private void lockbalance(Set<String> keySet) throws BusinessException {
		List<String> lockPKs = new ArrayList<String>(keySet.size());
		for (String md5 : keySet) {
			lockPKs.add("Er_expensebal_"+md5);
		}
		KeyLock.dynamicLockWithException(lockPKs);
	}

	/**
	 * 构造MD5，且根据MD5进行分组
	 *
	 * @param vos
	 * @param deletevos
	 * @return
	 */
	private Map<String, ExpenseBalVO> getMD5Map(ExpenseAccountVO[] vos,
			ExpenseAccountVO[] deletevos) {
		Map<String, ExpenseBalVO> map = new HashMap<String, ExpenseBalVO>();

		if (!ArrayUtil.isArrayIsNull((Object[]) vos)) {
			computeAmount(vos, map, true);
		}
		if (!ArrayUtil.isArrayIsNull((Object[]) deletevos)) {
			computeAmount(deletevos, map, false);
		}
		return map;
	}

	private void computeAmount(ExpenseAccountVO[] vos,
			Map<String, ExpenseBalVO> map, boolean isplus) {
		for (int i = 0; i < vos.length; i++) {
			ExpenseAccountVO vo = vos[i];
			String md5 = getMd5ByVO(vo);
			ExpenseBalVO mapvo = map.get(md5);
			if (mapvo == null) {
				mapvo = new ExpenseBalVO();
				mapvo.setMd5(md5);
				copyAccout2Bal(vo, mapvo);
				map.put(md5, mapvo);
			}
			mapvo.setAssume_amount(getAmount(mapvo.getAssume_amount(),
					vo.getAssume_amount(), isplus));
			mapvo.setOrg_amount(getAmount(mapvo.getOrg_amount(),
					vo.getOrg_amount(), isplus));
			mapvo.setGroup_amount(getAmount(mapvo.getGroup_amount(),
					vo.getGroup_amount(), isplus));
			mapvo.setGlobal_amount(getAmount(mapvo.getGlobal_amount(),
					vo.getGlobal_amount(), isplus));
		}
	}

	/**
	 * 费用帐信息copy到余额表
	 *
	 * @param vo
	 * @param balvo
	 */
	private void copyAccout2Bal(ExpenseAccountVO vo, ExpenseBalVO balvo) {
		for (int i = 0; i < DimFields.length; i++) {
			balvo.setAttributeValue(DimFields[i],
					vo.getAttributeValue(DimFields[i]));
		}
	}

	/**
	 * 构造MD5
	 *
	 * @param o
	 * @return
	 */
	private String getMd5ByVO(ExpenseAccountVO vo) {
		StringBuffer bf = new StringBuffer();
		for (int i = 0; i < DimFields.length; i++) {
			Object attributeValue = vo.getAttributeValue(DimFields[i]);
			if (attributeValue == null) {
				continue;
			}
			bf.append(DimFields[i]).append(":").append(attributeValue)
					.append(";");
		}
		return _MD5.getMD5ofStr(bf.toString());
	}

	private UFDouble getAmount(UFDouble u1, UFDouble u2, boolean isplus) {
		if (isplus) {
			return addUFDouble(u1, u2);
		} else {
			return subUFDouble(u1, u2);
		}
	}

	private UFDouble addUFDouble(UFDouble u1, UFDouble u2) {
		u1 = u1 == null ? UFDouble.ZERO_DBL : u1;
		u2 = u2 == null ? UFDouble.ZERO_DBL : u2;
		return u1.add(u2);
	}

	private UFDouble subUFDouble(UFDouble u1, UFDouble u2) {
		u1 = u1 == null ? UFDouble.ZERO_DBL : u1;
		u2 = u2 == null ? UFDouble.ZERO_DBL : u2;
		return u1.sub(u2);
	}
}