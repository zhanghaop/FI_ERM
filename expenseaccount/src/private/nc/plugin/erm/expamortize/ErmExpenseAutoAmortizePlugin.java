package nc.plugin.erm.expamortize;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.erm.util.ErAccperiodUtil;
import nc.bs.framework.common.NCLocator;
import nc.bs.framework.core.service.TimeService;
import nc.bs.pub.pa.PreAlertObject;
import nc.bs.pub.taskcenter.BgWorkingContext;
import nc.bs.pub.taskcenter.IBackgroundWorkPlugin;
import nc.itf.org.IOrgUnitQryService;
import nc.pubitf.erm.expamortize.IExpAmortize;
import nc.pubitf.erm.expamortize.IExpAmortizeinfoQuery;
import nc.vo.bd.period2.AccperiodmonthVO;
import nc.vo.erm.common.MessageVO;
import nc.vo.erm.expamortize.ExpamtinfoVO;
import nc.vo.org.OrgVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;

/**
 * �Զ���̯̯����̨������
 *
 * @author lvhj
 *
 */
public class ErmExpenseAutoAmortizePlugin implements IBackgroundWorkPlugin {

	@Override
	public PreAlertObject executeTask(BgWorkingContext bgwc)
			throws BusinessException {
		// ��ѯȫ��δ̯����ɵ�̯����Ϣ
		IExpAmortizeinfoQuery qryService = NCLocator.getInstance().lookup(
				IExpAmortizeinfoQuery.class);
		ExpamtinfoVO[] vos = qryService.queryAllAmtingVOs();
		if (vos == null || vos.length == 0) {
			return null;
		}
		// ������֯�����̯��Ϣ
		Map<String, List<ExpamtinfoVO>> map = new HashMap<String, List<ExpamtinfoVO>>();
		for (int i = 0; i < vos.length; i++) {
			String pk_org = vos[i].getPk_org();
			List<ExpamtinfoVO> list = map.get(pk_org);
			if (list == null) {
				list = new ArrayList<ExpamtinfoVO>();
				map.put(pk_org, list);
			}
			list.add(vos[i]);
		}
		// ������֯+������ʱ��ת��Ϊ ��֯ + ����ڼ�
		IOrgUnitQryService orgservice = NCLocator.getInstance().lookup(
				IOrgUnitQryService.class);
		OrgVO[] orgvos = orgservice
				.getOrgs(map.keySet().toArray(new String[0]));
		if (orgvos == null || orgvos.length == 0) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0103")/*@res "�����Զ���̯̯������ʧ�ܣ��޷���ѯ����֯"*/);
		}
		IExpAmortize service = NCLocator.getInstance().lookup(
				IExpAmortize.class);
		UFDate currDate = TimeService.getInstance().getUFDateTime().getDate();

		StringBuffer result = new StringBuffer();
		for (int i = 0; i < orgvos.length; i++) {
			String pk_org = orgvos[i].getPrimaryKey();

			AccperiodmonthVO monthVo = ErAccperiodUtil.getAccperiodmonthByUFDate(pk_org, currDate);
			// ��������̯��
			ExpamtinfoVO[] orgExpInfoVOs = map.get(pk_org).toArray(
							new ExpamtinfoVO[0]);
			MessageVO[] resvos  = service.amortize(pk_org, monthVo.getYearmth(), orgExpInfoVOs);
			if(resvos != null && resvos.length > 0){
				for (int j = 0; j < resvos.length; j++) {
					result.append(resvos[j].toString());
					result.append("\n");
				}
			}
		}
		bgwc.setLogStr(result.toString());
		return null;
	}

}