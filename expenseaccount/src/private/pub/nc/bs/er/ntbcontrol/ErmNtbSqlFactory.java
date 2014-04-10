package nc.bs.er.ntbcontrol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nc.bs.erm.annotation.ErmBusinessDef;
import nc.bs.erm.costshare.IErmCostShareConst;
import nc.bs.erm.expamortize.ExpAmoritizeConst;
import nc.bs.erm.matterapp.common.ErmMatterAppConst;
import nc.bs.pf.pub.PfDataCache;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.erm.control.TokenTools;
import nc.vo.fipub.annotation.Business;
import nc.vo.fipub.annotation.BusinessType;
import nc.vo.pub.BusinessException;
import nc.vo.pub.billtype.BilltypeVO;
import nc.vo.tb.obj.NtbParamVO;

/**
 * ����Ԥ���ѯsql����������
 * @author chenshuaia
 * 
 */
public class ErmNtbSqlFactory {
	/**
	 * Ԥ�����
	 */
	private NtbParamVO ntbParam;

	private static volatile ErmNtbSqlFactory factory;

	/**
	 * ���ݲ�ѯsql����map
	 */
	private static Map<String, AbstractErmNtbSqlStrategy> strategyMap = new HashMap<String, AbstractErmNtbSqlStrategy>();

	private ErmNtbSqlFactory() {
		strategyMap.put(BXConstans.BX_DJLXBM, new BXErmNtbSqlStrategy());// ����
		strategyMap.put(BXConstans.JK_DJLXBM, new JKErmNtbSqlStrategy());// ���
		strategyMap.put(IErmCostShareConst.COSTSHARE_BILLTYPE, new CSErmNtbSqlStrategy());// ��ת
		strategyMap.put(ErmMatterAppConst.MatterApp_BILLTYPE, new MAErmNtbSqlStrategy());// ����
		strategyMap.put(ExpAmoritizeConst.Expamoritize_BILLTYPE, new ATErmNtbSqlStrategy());// ̯��
	}

	public static ErmNtbSqlFactory getInstance(NtbParamVO param) {// ����ģʽ
		ErmNtbSqlFactory factory = getFactory();
		factory.setNtbParam(param);
		return factory;
	}

	@Business(business = ErmBusinessDef.TBB_CTROL, subBusiness = "", description = "��ѯ����������Ԥ��ȡ��sql����" /*-=notranslate=-*/, type = BusinessType.NORMAL)
	public String[] getSqls() throws BusinessException {
		List<String> sqlList = new ArrayList<String>();
		Set<String> typeSet = getAllParentBillTyps();
		try {
			if (typeSet.contains(BXConstans.BX_DJLXBM)) {// ������
				sqlList.addAll(strategyMap.get(BXConstans.BX_DJLXBM).getSqls(getNtbParam()));
			}
			if (typeSet.contains(BXConstans.JK_DJLXBM)) {// ��
				sqlList.addAll(strategyMap.get(BXConstans.JK_DJLXBM).getSqls(getNtbParam()));
			}
			if (typeSet.contains(IErmCostShareConst.COSTSHARE_BILLTYPE)) {// ��ת��
				sqlList.addAll(strategyMap.get(IErmCostShareConst.COSTSHARE_BILLTYPE).getSqls(getNtbParam()));
			}
			if (typeSet.contains(ErmMatterAppConst.MatterApp_BILLTYPE)) {// ���뵥
				sqlList.addAll(strategyMap.get(ErmMatterAppConst.MatterApp_BILLTYPE).getSqls(getNtbParam()));
			}
			if (typeSet.contains(ExpAmoritizeConst.Expamoritize_BILLTYPE)) {// ̯����Ϣ
				sqlList.addAll(strategyMap.get(ExpAmoritizeConst.Expamoritize_BILLTYPE).getSqls(getNtbParam()));
			}
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
		}
		return sqlList.toArray(new String[] {});
	}

	@Business(business = ErmBusinessDef.TBB_CTROL, subBusiness = "", description = "��ѯ��������Ԥ��ȡ����ϸsql����" /*-=notranslate=-*/, type = BusinessType.NORMAL)
	public String[] getDetailSqls() throws BusinessException {
		List<String> sqlList = new ArrayList<String>();
		Set<String> typeSet = getAllParentBillTyps();
		try {
			if (typeSet.contains(BXConstans.BX_DJLXBM)) {// ������
				sqlList.addAll(strategyMap.get(BXConstans.BX_DJLXBM).getDetailSqls(getNtbParam()));
			}
			if (typeSet.contains(BXConstans.JK_DJLXBM)) {// ��
				sqlList.addAll(strategyMap.get(BXConstans.JK_DJLXBM).getDetailSqls(getNtbParam()));
			}
			if (typeSet.contains(IErmCostShareConst.COSTSHARE_BILLTYPE)) {// ��ת��
				sqlList.addAll(strategyMap.get(IErmCostShareConst.COSTSHARE_BILLTYPE).getDetailSqls(getNtbParam()));
			}
			if (typeSet.contains(ErmMatterAppConst.MatterApp_BILLTYPE)) {// ���뵥
				sqlList.addAll(strategyMap.get(ErmMatterAppConst.MatterApp_BILLTYPE).getDetailSqls(getNtbParam()));
			}
			if (typeSet.contains(ExpAmoritizeConst.Expamoritize_BILLTYPE)) {// ̯����Ϣ
				sqlList.addAll(strategyMap.get(ExpAmoritizeConst.Expamoritize_BILLTYPE).getDetailSqls(getNtbParam()));
			}
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
		}
		return sqlList.toArray(new String[] {});
	}

	public NtbParamVO getNtbParam() {
		return ntbParam;
	}

	public void setNtbParam(NtbParamVO ntbParam) {
		this.ntbParam = ntbParam;
	}

	/**
	 * ��ò�ѯ��ȫ����������
	 * 
	 * @return
	 */
	public Set<String> getAllParentBillTyps() {
		Set<String> set = new HashSet<String>();

		String billType = getNtbParam().getBill_type();
		TokenTools token = null;
		if (billType.indexOf("#") != -1) {
			token = new TokenTools(billType, "#", false);
		} else {
			token = new TokenTools(billType, ",", false);
		}

		String[] billTypes = token.getStringArray();

		if (billTypes != null && billTypes.length > 0) {
			String parentBilltype = null;
			for (int i = 0; i < billTypes.length; i++) {
				BilltypeVO vo = PfDataCache.getBillType(billTypes[i]);
				if (vo == null) {
					continue;
				}
				if (vo.getIstransaction() != null || vo.getIstransaction().booleanValue()) {
					// ��ý������͵�
					parentBilltype = vo.getParentbilltype();
				} else {
					parentBilltype = vo.getPk_billtypecode();
				}

				if (parentBilltype == null) {// ����޸��൥�ݣ����ý�������
					parentBilltype = billTypes[i];
				}
				set.add(parentBilltype);
			}
		}
		return set;
	}

	private static ErmNtbSqlFactory getFactory() {
		if (factory == null) {
			factory = new ErmNtbSqlFactory();
		}
		return factory;
	}
}
