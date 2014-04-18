package nc.impl.erm.costshare.ext;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import nc.bs.dao.BaseDAO;
import nc.bs.erm.util.ErAccperiodUtil;
import nc.itf.erm.costshare.ext.IErmCsMonthManageServiceExt;
import nc.itf.fi.pub.Currency;
import nc.vo.bd.period2.AccperiodmonthVO;
import nc.vo.erm.costshare.AggCostShareVO;
import nc.vo.erm.costshare.CShareDetailVO;
import nc.vo.erm.costshare.CostShareVO;
import nc.vo.erm.costshare.ext.CShareMonthVO;
import nc.vo.fi.pub.SqlUtils;
import nc.vo.fipub.utils.VOUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;

/**
 * 分期均摊数据维护实现类
 * 
 * 合生元专用
 * 
 * @author lvhj
 * 
 */
public class ErmCsMonthManageServiceExtImpl implements IErmCsMonthManageServiceExt {

	@Override
	public void generateMonthVos(AggCostShareVO[] vos, AggCostShareVO[] oldvos)
			throws BusinessException {
		BaseDAO dao = new BaseDAO();
		if (oldvos != null && oldvos.length > 0) {
			// 先处理待删除的结转单，对应的分期记录
			String[] parentpks = VOUtil.getAttributeValues(oldvos, null);
			dao.deleteByClause(CShareMonthVO.class, SqlUtils.getInStr(
					CShareMonthVO.PK_COSTSHARE, parentpks, true));
		}

		if (vos != null && vos.length > 0) {
			List<CShareMonthVO> list = new ArrayList<CShareMonthVO>();
			for (int i = 0; i < vos.length; i++) {
				CostShareVO headvo = (CostShareVO) vos[i].getParentVO();

				String beginMonthpk = headvo.getDefitem1();
				String endMonthpk = headvo.getDefitem2();

				AccperiodmonthVO beginMonth = ErAccperiodUtil
						.getAccperiodmonthByPk(beginMonthpk);
				AccperiodmonthVO endMonth = ErAccperiodUtil
						.getAccperiodmonthByPk(endMonthpk);

				AccperiodmonthVO tempMonth = beginMonth;
				// 分期计算，各个期间占用预算的日期按照期间的起始日期占用
				List<UFDate> dates = new ArrayList<UFDate>();
				int count = 0;
				while (tempMonth.getYearmth().compareTo(endMonth.getYearmth()) <= 0) {
					++count;
					dates.add(tempMonth.getBegindate());
					tempMonth = ErAccperiodUtil.getAddedAccperiodmonth(
							tempMonth, 2);
				}
				int ybDecimalDigit = Currency.getCurrDigit(headvo.getBzbm());// 原币精度
				int orgBbDecimalDigit = Currency.getCurrDigit(Currency.getOrgLocalCurrPK(headvo.getPk_org()));// 组织本币精度（一张申请单不存在组织本币不同情况）
				int groupByDecimalDigit = Currency.getCurrDigit(Currency.getGroupCurrpk(headvo.getPk_group()));// 集团本币精度
				int globalByDecimalDigit = Currency.getCurrDigit(Currency.getGlobalCurrPk(null));
				
				// 按照申请明细行，计算生成分期均摊信息
				List<CShareMonthVO> detaillist = generate(vos[i].getChildrenVO(), count,dates,new int[]{ybDecimalDigit,orgBbDecimalDigit,groupByDecimalDigit,globalByDecimalDigit});
				if(!detaillist.isEmpty()){
					vos[i].setTableVO(CShareMonthVO.getDefaultTableName(), detaillist.toArray(new CShareMonthVO[0]));
					list.addAll(detaillist);
				}
			}
			// 保存分期均摊信息
			dao.insertVOList(list);
		}
	}

	private List<CShareMonthVO> generate(CircularlyAccessibleValueObject[] dtailvos,
			int count, List<UFDate> dates, int[] decimalDigits) throws BusinessException {
		
		List<CShareMonthVO> list = new ArrayList<CShareMonthVO>();
		
		for (int j = 0; j < dtailvos.length; j++) {
			if (dtailvos[j].getStatus() == VOStatus.DELETED) {
				continue;
			}
			CShareDetailVO detailvo = (CShareDetailVO) dtailvos[j];
			
			// 分期均摊金额，原币、组织本币、集团本币、全局本币，各自处理均摊、尾差
			UFDouble orig_amount = detailvo.getAssume_amount();
			UFDouble divMoney = orig_amount.div(count);
			divMoney = divMoney.setScale(decimalDigits[0],BigDecimal.ROUND_UP);

			UFDouble org_amount = detailvo.getBbje();
			UFDouble orgdivMoney = org_amount.div(count);
			orgdivMoney = orgdivMoney.setScale(decimalDigits[1],BigDecimal.ROUND_UP);

			UFDouble group_amount = detailvo.getGroupbbje();
			UFDouble groupdivMoney = group_amount.div(count);
			groupdivMoney = groupdivMoney.setScale(decimalDigits[2],BigDecimal.ROUND_UP);

			UFDouble global_amount = detailvo.getGlobalbbje();
			UFDouble globaldivMoney = global_amount.div(count);
			globaldivMoney = globaldivMoney.setScale(decimalDigits[3],BigDecimal.ROUND_UP);

			for (int j2 = 0; j2 < count; j2++) {
				// 生成MtappMonthExtVO
				CShareMonthVO vo = new CShareMonthVO();
				list.add(vo);
				vo.setStatus(VOStatus.NEW);
				vo.setPk_costshare(detailvo.getPk_costshare());
				vo.setPk_cshare_detail(detailvo.getPrimaryKey());
				vo.setPk_org(detailvo.getPk_org());
				vo.setPk_group(detailvo.getPk_group());
				vo.setBilldate(dates.get(j2));
				vo.setAssume_org(detailvo.getAssume_org());
				vo.setPk_pcorg(detailvo.getPk_pcorg());
				if (j2 == count - 1) {
					// 最后一个期间，保留尾差
					setAmount(vo, new UFDouble[] { orig_amount, org_amount,
							group_amount, global_amount });
				} else {
					setAmount(vo, new UFDouble[] { divMoney, orgdivMoney,
							groupdivMoney, globaldivMoney });
					orig_amount = orig_amount.sub(divMoney);
					org_amount = org_amount.sub(orgdivMoney);
					group_amount = group_amount.sub(groupdivMoney);
					global_amount = global_amount.sub(globaldivMoney);
				}
			}
		}
		return list;
	}

	private void setAmount(CShareMonthVO vo, UFDouble[] amounts) {
		vo.setOrig_amount(amounts[0]);
		vo.setOrg_amount(amounts[1]);
		vo.setGroup_amount(amounts[2]);
		vo.setGlobal_amount(amounts[3]);
	}

}
