package nc.bs.erm.matterapp.eventlistener;

import nc.bs.businessevent.IBusinessEvent;
import nc.bs.businessevent.IBusinessListener;
import nc.bs.businessevent.bd.BDCommonEvent;
import nc.bs.businessevent.bd.BDCommonEvent.BDCommonUserObj;
import nc.bs.erm.matterapp.common.ErmMatterAppConst;
import nc.bs.erm.util.ErAccperiodUtil;
import nc.bs.framework.common.NCLocator;
import nc.itf.erm.matterapp.IErmMatterAppBillQueryPrivate;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.bd.period2.AccperiodmonthVO;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.org.CloseAccBookVO;
import nc.vo.pub.BusinessException;

/**
 * ����ǰУ��������뵥�Ƿ���Ч
 *
 * @author wangled
 *
 */
public class ErmMatterAppJsControlListener implements IBusinessListener {
	@Override
	public void doAction(IBusinessEvent event) throws BusinessException {
		BDCommonEvent erEvent = (BDCommonEvent) event;
		BDCommonUserObj obj = (BDCommonUserObj) erEvent.getUserObject();
		Object vos[] = (Object[]) obj.getNewObjects();
		CloseAccBookVO vo=((CloseAccBookVO)vos[0]);

		// ֻУ����ù���ģ��
		if (!BXConstans.ERM_MODULEID.equals(vo.getModuleid())) {
			return;
		}
        String pk_org = vo.getPk_org();
        String pk_accperiodmonth = vo.getPk_accperiodmonth();
        AccperiodmonthVO accperiodVO = ErAccperiodUtil
        .getAccperiodmonthByPk(pk_accperiodmonth);
        String begindate = accperiodVO.getBegindate().toString();
        String enddate = accperiodVO.getEnddate().toString();
        // ��ѯ�������뵥
        StringBuffer msg = getMtapp(pk_org, begindate, enddate);
		if (msg != null && msg.length()> 0 ) {
			throw new BusinessException(msg.toString());
		}
	}

	private StringBuffer getMtapp(String pk_org, String begindate, String enddate)
			throws BusinessException {
		StringBuffer msg = new StringBuffer();
		MatterAppVO[] vos = getSerivce().getMtappByMthPk(pk_org, begindate,
				enddate);
		if (vos != null) {
			for (MatterAppVO vo : vos) {
				if (vo.getBillstatus() != ErmMatterAppConst.EFFECTSTATUS_VALID) {
					msg.append(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0","0201212-0029")/*@res "��δ��Ч�ķ������뵥"*/ + vo.getBillno()
							+ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0","0201212-0030")/*@res "�����Խ���!\n"*/);
				}
			}
		}
		return msg;
	}

	private IErmMatterAppBillQueryPrivate getSerivce() {
		return NCLocator.getInstance().lookup(
				IErmMatterAppBillQueryPrivate.class);
	}

}