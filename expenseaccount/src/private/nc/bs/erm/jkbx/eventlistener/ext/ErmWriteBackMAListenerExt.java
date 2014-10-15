package nc.bs.erm.jkbx.eventlistener.ext;

import java.util.List;

import nc.bs.businessevent.IBusinessEvent;
import nc.bs.businessevent.IBusinessListener;
import nc.bs.erm.annotation.ErmBusinessDef;
import nc.bs.erm.event.ErmBusinessEvent;
import nc.bs.erm.event.ErmBusinessEvent.ErmCommonUserObj;
import nc.bs.erm.event.ErmEventType;
import nc.bs.framework.common.NCLocator;
import nc.itf.erm.jkbx.ext.IWriteBackPrivateExt;
import nc.pubitf.erm.matterappctrl.ext.IMatterAppCtrlServiceExt;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.erm.matterappctrl.IMtappCtrlBusiVO;
import nc.vo.erm.matterappctrl.MtappCtrlInfoVO;
import nc.vo.fipub.annotation.Business;
import nc.vo.fipub.annotation.BusinessType;
import nc.vo.pub.BusinessException;

import org.apache.commons.lang.ArrayUtils;

/**
 * ������������д�������뵥ҵ����
 * 
 * ����Ԫר��
 * 
 * @author lvhj
 */
public class ErmWriteBackMAListenerExt implements IBusinessListener {

	@Override
	@Business(business=ErmBusinessDef.MATTAPP_CTROL,subBusiness="", description = "��������д�������뵥���" /*-=notranslate=-*/,type=BusinessType.CORE)
	public void doAction(IBusinessEvent event) throws BusinessException {

		if (event instanceof ErmBusinessEvent) {
			ErmBusinessEvent erEvent = (ErmBusinessEvent) event;
			String eventType = erEvent.getEventType();
			if ((!ErmEventType.TYPE_INSERT_AFTER.equalsIgnoreCase(eventType)) && 
					(!ErmEventType.TYPE_UPDATE_AFTER.equalsIgnoreCase(eventType)) && 
					(!ErmEventType.TYPE_DELETE_BEFORE.equalsIgnoreCase(eventType)) && 
					(!ErmEventType.TYPE_UNSIGN_BEFORE.equalsIgnoreCase(eventType)) && 
					(!ErmEventType.TYPE_SIGN_BEFORE.equalsIgnoreCase(eventType))) {
				// ������,�޸�ǰ,ɾ��ǰ,��Чǰ��ȡ����Чǰ
				return;
			}

			JKBXVO[] vos = (JKBXVO[]) ((ErmCommonUserObj) erEvent.getUserObject()).getNewObjects();
			if(ArrayUtils.isEmpty(vos)){
				return;
			}

			// ��װҵ������
			List<IMtappCtrlBusiVO> mtBusiVoList = NCLocator.getInstance().lookup(IWriteBackPrivateExt.class).construstBusiDataForWriteBack(vos, eventType);
			if (mtBusiVoList.isEmpty()) {
				return;
			}

			// ������д���뵥
			MtappCtrlInfoVO errMsgVo = NCLocator.getInstance().lookup(IMatterAppCtrlServiceExt.class).matterappControlByRatio(mtBusiVoList.toArray(new IMtappCtrlBusiVO[0]));
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
