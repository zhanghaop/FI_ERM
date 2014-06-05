package nc.bs.erm.eventlistener;

import java.util.ArrayList;
import java.util.List;

import nc.bs.businessevent.IBusinessEvent;
import nc.bs.businessevent.IBusinessListener;
import nc.bs.erm.annotation.ErmBusinessDef;
import nc.bs.erm.event.ErmBusinessEvent;
import nc.bs.erm.event.ErmBusinessEvent.ErmCommonUserObj;
import nc.bs.erm.event.ErmEventType;
import nc.bs.framework.common.NCLocator;
import nc.itf.arap.prv.IWriteBackPrivate;
import nc.pubitf.erm.matterappctrl.IMatterAppCtrlService;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.ep.bx.JKBXMtappCtrlBusiVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.erm.matterappctrl.IMtappCtrlBusiVO;
import nc.vo.erm.matterappctrl.MtappCtrlInfoVO;
import nc.vo.fipub.annotation.Business;
import nc.vo.fipub.annotation.BusinessType;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;

import org.apache.commons.lang.ArrayUtils;

/**
 * 回写费用申请单业务插件 <b>Date:</b>2012-11-19<br>
 * 
 * @author：wangyhh@ufida.com.cn
 * @version $Revision$
 */
public class ErmWriteBackMAListener implements IBusinessListener {

	@Override
	@Business(business=ErmBusinessDef.MATTAPP_CTROL,subBusiness="", description = "借款报销单回写费用申请单插件" /*-=notranslate=-*/,type=BusinessType.CORE)
	public void doAction(IBusinessEvent event) throws BusinessException {

		if (event instanceof ErmBusinessEvent) {
			ErmBusinessEvent erEvent = (ErmBusinessEvent) event;
			String eventType = erEvent.getEventType();
			if ((!ErmEventType.TYPE_INSERT_AFTER.equalsIgnoreCase(eventType)) && 
					(!ErmEventType.TYPE_UPDATE_AFTER.equalsIgnoreCase(eventType)) && 
					(!ErmEventType.TYPE_DELETE_BEFORE.equalsIgnoreCase(eventType)) && 
					(!ErmEventType.TYPE_UNSIGN_BEFORE.equalsIgnoreCase(eventType)) && 
					(!ErmEventType.TYPE_SIGN_BEFORE.equalsIgnoreCase(eventType))   &&
					(!ErmEventType.TYPE_INVALID_BEFORE.equalsIgnoreCase(eventType)) 
					) {
				// 新增后,修改前,删除前,生效前，取消生效前,作废后
				return;
			}

			JKBXVO[] vos = (JKBXVO[]) ((ErmCommonUserObj) erEvent.getUserObject()).getNewObjects();
			if(ArrayUtils.isEmpty(vos)){
				return;
			}

			// 封装业务数据
			List<IMtappCtrlBusiVO> mtBusiVoList = NCLocator.getInstance().lookup(IWriteBackPrivate.class).construstBusiDataForWriteBack(vos, eventType);
			if (mtBusiVoList.isEmpty()) {
				return;
			}
			
			List<IMtappCtrlBusiVO> jkrowctrllist = new ArrayList<IMtappCtrlBusiVO>();// 借款单按行回写集合
			List<IMtappCtrlBusiVO> jkmactrllist = new ArrayList<IMtappCtrlBusiVO>();// 借款单按整单调剂回写集合
			List<IMtappCtrlBusiVO> mactrllist = new ArrayList<IMtappCtrlBusiVO>();// 报销单按规则回写集合
			for (int i = 0; i < mtBusiVoList.size(); i++) {
				JKBXMtappCtrlBusiVO busivo =  (JKBXMtappCtrlBusiVO) mtBusiVoList.get(i);
				if(BXConstans.JK_DJLXBM.equals(busivo.getBillType())&&StringUtil.isEmpty(busivo.getSrcBusidetailPK())){
					if(StringUtil.isEmpty(busivo.getMatterAppDetailPK())){
						// 借款单整单控制回写申请单。
						jkmactrllist.add(busivo);
					}else{
						// 借款单按行控制回写申请单。
						jkrowctrllist.add(busivo);
					}
				}else{
					mactrllist.add(busivo);
				}
			}
			IMatterAppCtrlService mactrlservice = NCLocator.getInstance().lookup(IMatterAppCtrlService.class);
			
			if(!jkmactrllist.isEmpty()){
				// 借款单整单控制回写申请单
				MtappCtrlInfoVO errMsgVo = mactrlservice.matterappControlByAllAdjust(jkmactrllist.toArray(new IMtappCtrlBusiVO[0]));
				if (errMsgVo.getControlinfos() != null) {
					throw new BusinessException(getErrMsg(errMsgVo));
				}
			}
			if(!jkrowctrllist.isEmpty()){
				// 借款单按行控制回写申请单
				MtappCtrlInfoVO errMsgVo = mactrlservice.matterappControlByDetail(jkrowctrllist.toArray(new IMtappCtrlBusiVO[0]));
				if (errMsgVo.getControlinfos() != null) {
					throw new BusinessException(getErrMsg(errMsgVo));
				}
			}
			
			if(!mactrllist.isEmpty()){
				MtappCtrlInfoVO errMsgVo = mactrlservice.matterappControl(mtBusiVoList.toArray(new IMtappCtrlBusiVO[0]));
				if (errMsgVo.getControlinfos() != null) {
					throw new BusinessException(getErrMsg(errMsgVo));
				}
			}

		}

	}

	/**
	 * 构造错误信息
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
