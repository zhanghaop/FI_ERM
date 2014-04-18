package nc.bs.erm.eventlistener;


import org.apache.commons.lang.ArrayUtils;

import nc.bs.businessevent.IBusinessEvent;
import nc.bs.businessevent.IBusinessListener;
import nc.bs.framework.common.NCLocator;
import nc.bs.pub.para.IParaEventType;
import nc.bs.pub.para.ParaEvent;
import nc.pubitf.erm.matterapp.IErmMatterAppBillQuery;
import nc.vo.arap.bx.util.BXParamConstant;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.erm.util.VOUtils;
import nc.vo.pub.BusinessException;
import nc.vo.pub.para.SysInitVO;

/**
 * 费用管理参数修改监听
 * 借款和报销单全部为生效状态才可以更改参数
 * <b>Date:</b>2012-11-22<br>
 * @author：wangyhh@ufida.com.cn
 * @version $Revision$
 */
public class ErmParamModifyBusinessListener implements IBusinessListener {

	@Override
	public void doAction(IBusinessEvent event) throws BusinessException {
		if ((event instanceof ParaEvent) && event.getSourceID().equals(IParaEventType.SOURCEID)) {
			SysInitVO vo = ((ParaEvent) event).getSysinitVO();
			if (!vo.getInitcode().equals(BXParamConstant.PARAM_MTAPP_CTRL)) {
				return;
			}

			//根据组织查询费用申请单
			String condition = " pk_org='" + vo.getPk_org() + "' ";
			AggMatterAppVO[] mtappVos = NCLocator.getInstance().lookup(IErmMatterAppBillQuery.class).queryBillByWhere(condition);
			if (ArrayUtils.isEmpty(mtappVos)) {
				//该组织没有关联费用申请单
				return;
			}
			String[] mtappPks = VOUtils.getAttributeValues(mtappVos, MatterAppVO.PK_MTAPP_BILL);

			//业务单据全部生效才可以更改
			if (EventListenerUtil.isExistUnEffectBill(mtappPks)) {
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0094")/*@res "借款和报销单全部为生效状态才可以更改参数"*/);
			}
		}
	}

}