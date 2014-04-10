package nc.bs.erm.costshare.actimpl;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import nc.bs.businessevent.IBusinessEvent;
import nc.bs.businessevent.IBusinessListener;
import nc.bs.erm.costshare.IErmCostShareConst;
import nc.bs.erm.event.ErmBusinessEvent;
import nc.bs.erm.event.ErmBusinessEvent.ErmCommonUserObj;
import nc.bs.framework.common.NCLocator;
import nc.itf.arap.prv.IWriteBackPrivate;
import nc.pubitf.erm.matterappctrl.IMatterAppCtrlService;
import nc.vo.erm.costshare.AggCostShareVO;
import nc.vo.erm.costshare.CostShareVO;
import nc.vo.erm.matterappctrl.IMtappCtrlBusiVO;
import nc.vo.erm.matterappctrl.MtappCtrlInfoVO;
import nc.vo.erm.util.VOUtils;
import nc.vo.pub.BusinessException;

/**
 * ���ý�ת������-��д���뵥ҵ��ʵ�ֲ��
 * 
 * @author lvhj
 * 
 */
public class ErmCostshareMaControlListener implements IBusinessListener {

	@Override
	public void doAction(IBusinessEvent event) throws BusinessException {

		ErmBusinessEvent erevent = (ErmBusinessEvent) event;
		String eventType = erevent.getEventType();

		ErmCommonUserObj obj = (ErmCommonUserObj) erevent.getUserObject();
		AggCostShareVO[] vos = (AggCostShareVO[]) obj.getNewObjects();
		Integer src_type = (Integer) vos[0].getParentVO().getAttributeValue(
				CostShareVO.SRC_TYPE);
		boolean isSrcType_Self = src_type.intValue() == IErmCostShareConst.CostShare_Bill_SCRTYPE_SEL;
		
		if(isSrcType_Self){
			// ֻ����������ǰ��̯��������
			return ;
		}
		
		AggCostShareVO[] oldvos = (AggCostShareVO[]) obj.getOldObjects();
		if(oldvos != null && oldvos.length >0){
			// �޸��������oldvo�����newvo��ʹ��
			Map<String, AggCostShareVO> oldvomap = VOUtils.changeCollection2Map(Arrays.asList(oldvos));
			for (int i = 0; i < vos.length; i++) {
				vos[i].setOldvo(oldvomap.get(vos[i].getParentVO().getPrimaryKey()));
			}
		}
		
		// ��װҵ������
		List<IMtappCtrlBusiVO> mtBusiVoList = NCLocator.getInstance().lookup(IWriteBackPrivate.class).construstCostshareDataForWriteBack(vos, eventType);
		// ��д���뵥
		if(!mtBusiVoList.isEmpty()){
			IMatterAppCtrlService mactrlservice = NCLocator.getInstance().lookup(IMatterAppCtrlService.class);
			MtappCtrlInfoVO errMsgVo = mactrlservice.matterappControl(mtBusiVoList.toArray(new IMtappCtrlBusiVO[0]));
			if (errMsgVo.getControlinfos() != null) {
				throw new BusinessException(getErrMsg(errMsgVo));
			}
		}
	}
	
	/**
	 * ���������Ϣ
	 * 
	 * @param errMsgVo
	 * @return
	 * @author: wangyhh@ufida.com.cn
	 */
	private String getErrMsg(MtappCtrlInfoVO errMsgVo) {
		StringBuffer buf = new StringBuffer();
		String[] controlinfos = errMsgVo.getControlinfos();
		for (String msg : controlinfos) {
			buf.append(msg);
		}
		return buf.toString();
	}
	
	
	
}
