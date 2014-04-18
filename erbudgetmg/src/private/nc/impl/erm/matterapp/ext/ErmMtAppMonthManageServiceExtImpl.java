package nc.impl.erm.matterapp.ext;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import nc.bs.dao.BaseDAO;
import nc.bs.erm.matterapp.ext.ErmMatterAppConstExt;
import nc.bs.erm.util.ErAccperiodUtil;
import nc.itf.erm.matterapp.ext.IErmMtAppMonthManageServiceExt;
import nc.itf.fi.pub.Currency;
import nc.vo.bd.period2.AccperiodmonthVO;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.erm.matterapp.MtAppDetailVO;
import nc.vo.erm.matterapp.ext.MtappMonthExtVO;
import nc.vo.fi.pub.SqlUtils;
import nc.vo.fipub.utils.VOUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;

/**
 * 分期均摊数据维护实现类
 * 
 * @author lvhj
 * 
 */
public class ErmMtAppMonthManageServiceExtImpl implements
		IErmMtAppMonthManageServiceExt {

	@Override
	public void generateMonthVos(AggMatterAppVO[] vos, AggMatterAppVO[] oldvos)
			throws BusinessException {
		BaseDAO dao = new BaseDAO();
		if (oldvos != null && oldvos.length > 0) {
			// 先处理待删除的申请单，对应的分期记录
			String[] mtappPks = VOUtil.getAttributeValues(oldvos, null);
			dao.deleteByClause(MtappMonthExtVO.class, SqlUtils.getInStr(
					MtappMonthExtVO.PK_MTAPP_BILL, mtappPks, true));
		}

		if (vos != null && vos.length > 0) {
			List<MtappMonthExtVO> list = new ArrayList<MtappMonthExtVO>();
			for (int i = 0; i < vos.length; i++) {
				MatterAppVO headvo = (MatterAppVO) vos[i].getParentVO();

				String beginMonthpk = (String) headvo.getAttributeValue(ErmMatterAppConstExt.STARTPERIOD_FIELD);
				String endMonthpk = (String) headvo.getAttributeValue(ErmMatterAppConstExt.ENDPERIOD_FIELD);

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
				int ybDecimalDigit = Currency.getCurrDigit(headvo.getPk_currtype());// 原币精度
				int orgBbDecimalDigit = Currency.getCurrDigit(Currency.getOrgLocalCurrPK(headvo.getPk_org()));// 组织本币精度（一张申请单不存在组织本币不同情况）
				int groupByDecimalDigit = Currency.getCurrDigit(Currency.getGroupCurrpk(headvo.getPk_group()));// 集团本币精度
				int globalByDecimalDigit = Currency.getCurrDigit(Currency.getGlobalCurrPk(null));
				
				// 按照申请明细行，计算生成分期均摊信息
				List<MtappMonthExtVO> monthlist = generate(vos[i].getChildrenVO(), count,dates,new int[]{ybDecimalDigit,orgBbDecimalDigit,groupByDecimalDigit,globalByDecimalDigit});
				if(!monthlist.isEmpty()){
					list.addAll(monthlist);
					vos[i].setTableVO(MtappMonthExtVO.getDefaultTableName(), monthlist.toArray(new MtappMonthExtVO[0]));
				}
			}
			// 保存分期均摊信息
			dao.insertVOList(list);
		}
	}

	/**
	 * 包装计算分期均摊记录
	 * 
	 * @param dtailvos
	 * @param count
	 * @param dates
	 * @param decimalDigits
	 * @return
	 * @throws BusinessException
	 */
	private List<MtappMonthExtVO> generate(MtAppDetailVO[] dtailvos,
			int count, List<UFDate> dates, int[] decimalDigits) throws BusinessException {
		
		List<MtappMonthExtVO> list = new ArrayList<MtappMonthExtVO>();
		
		for (int j = 0; j < dtailvos.length; j++) {
			if (dtailvos[j].getStatus() == VOStatus.DELETED) {
				continue;
			}
			MtAppDetailVO detailvo = dtailvos[j];
			
			// 分期均摊金额，原币、组织本币、集团本币、全局本币，各自处理均摊、尾差
			UFDouble orig_amount = detailvo.getOrig_amount();
			UFDouble divMoney = orig_amount.div(count);
			divMoney = divMoney.setScale(decimalDigits[0],BigDecimal.ROUND_UP);

			UFDouble org_amount = detailvo.getOrg_amount();
			UFDouble orgdivMoney = org_amount.div(count);
			orgdivMoney = orgdivMoney.setScale(decimalDigits[1],BigDecimal.ROUND_UP);

			UFDouble group_amount = detailvo.getGroup_amount();
			UFDouble groupdivMoney = group_amount.div(count);
			groupdivMoney = groupdivMoney.setScale(decimalDigits[2],BigDecimal.ROUND_UP);

			UFDouble global_amount = detailvo.getGlobal_amount();
			UFDouble globaldivMoney = global_amount.div(count);
			globaldivMoney = globaldivMoney.setScale(decimalDigits[3],BigDecimal.ROUND_UP);

			for (int j2 = 0; j2 < count; j2++) {
				// 生成MtappMonthExtVO
				MtappMonthExtVO vo = new MtappMonthExtVO();
				list.add(vo);
				vo.setStatus(VOStatus.NEW);
				vo.setPk_mtapp_bill(detailvo.getPk_mtapp_bill());
				vo.setPk_mtapp_detail(detailvo.getPrimaryKey());
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

	private void setAmount(MtappMonthExtVO vo, UFDouble[] amounts) {
		vo.setOrig_amount(amounts[0]);
		vo.setOrg_amount(amounts[1]);
		vo.setGroup_amount(amounts[2]);
		vo.setGlobal_amount(amounts[3]);
	}

}
