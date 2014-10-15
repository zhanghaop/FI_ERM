package nc.bs.erm.eventlistener;

import java.util.ArrayList;
import java.util.List;

import nc.bs.businessevent.IBusinessEvent;
import nc.bs.businessevent.IBusinessListener;
import nc.bs.erm.event.ErmBusinessEvent;
import nc.bs.erm.event.ErmEventType;
import nc.bs.erm.event.ErmBusinessEvent.ErmCommonUserObj;
import nc.bs.framework.common.NCLocator;
import nc.itf.erm.bx.IBxExpAmortizeSetting;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.ep.bx.JKVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;

/**
 * 待摊业务插件
 * 
 * @author 
 * @wangled
 */
public class ErmExpamortizeControlListener implements IBusinessListener {

	@Override
	public void doAction(IBusinessEvent event) throws BusinessException {
		ErmBusinessEvent erEvent = (ErmBusinessEvent) event;
		String eventType = erEvent.getEventType();

		ErmCommonUserObj obj = (ErmCommonUserObj) erEvent.getUserObject();
		JKBXVO[] vos = (JKBXVO[]) obj.getNewObjects();
		
		if (vos.length > 0 && vos[0] instanceof JKVO) {
			return;
		}
		
		if(ErmEventType.TYPE_INSERT_AFTER.equalsIgnoreCase(eventType)){//新增
			doInsertAct(vos);
		}else if(ErmEventType.TYPE_UPDATE_AFTER.equalsIgnoreCase(eventType)){//修改
			doUpdateAct(vos);
		}else if(ErmEventType.TYPE_SIGN_AFTER.equals(eventType)){//生效
			doSignAct(vos);
		}else if(ErmEventType.TYPE_UNSIGN_AFTER.equals(eventType)){//反生效
			doUnSignAct(vos);
		}
	}

	private void doUnSignAct(JKBXVO[] vos) throws BusinessException {
		List<JKBXVO> voList = getExpamtList(vos);
		if(voList.size() > 0){
			getBxExpAmortizeSettingService().expAmortizeUnApprove(voList.toArray(new JKBXVO[]{}));
		}
	}

	private void doSignAct(JKBXVO[] vos) throws BusinessException {
		List<JKBXVO> voList = getExpamtList(vos);
		if(voList.size() > 0){
			getBxExpAmortizeSettingService().expAmortizeApprove(voList.toArray(new JKBXVO[]{}));
		}
	}

	private void doUpdateAct(JKBXVO[] vos) throws BusinessException {
		List<JKBXVO> amortizeVoList = new ArrayList<JKBXVO>();
		List<JKBXVO> unAmortizeVoList = new ArrayList<JKBXVO>();
		for(JKBXVO vo : vos){
			if(vo.getBxoldvo().getParentVO().getIsexpamt().equals(vo.getParentVO().getIsexpamt())){
				continue;
			}else{
				if(isExpamt(vo)){
					amortizeVoList.add(vo);
				} else{
					unAmortizeVoList.add(vo);
				}
			}
		}
		
		if(amortizeVoList.size() > 0){
			getBxExpAmortizeSettingService().expAmortizeSet(amortizeVoList.toArray(new JKBXVO[]{}));
		}
		
		if(unAmortizeVoList.size() > 0){
			getBxExpAmortizeSettingService().expAmortizeUnSet(unAmortizeVoList.toArray(new JKBXVO[]{}));
		}
	}

	private void doInsertAct(JKBXVO[] vos) throws BusinessException {
		List<JKBXVO> voList = getExpamtList(vos);
		if(voList.size() > 0){
			getBxExpAmortizeSettingService().expAmortizeSet(voList.toArray(new JKBXVO[]{}));
		}
	}
	
	/**
	 * 获取待摊信息集合
	 * @param vos
	 * @return
	 * @throws BusinessException 
	 */
	private List<JKBXVO> getExpamtList(JKBXVO[] vos) throws BusinessException {
		List<JKBXVO> voList = new ArrayList<JKBXVO>();
		//判断是否待摊
		for (JKBXVO vo : vos){
			if(isExpamt(vo)){
				voList.add(vo);
			}
		}
		return voList;
	}

	private IBxExpAmortizeSetting getBxExpAmortizeSettingService() {
		return NCLocator.getInstance().lookup(IBxExpAmortizeSetting.class);
	}
	
	/**
	 * 判断报销VO是否待摊
	 * @param vo
	 * @return
	 */
	private boolean isExpamt(JKBXVO vo) {
		return vo.getParentVO().getIsexpamt().equals(UFBoolean.TRUE);
	}


}
