package nc.ui.erm.billquery.action;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nc.bs.framework.common.NCLocator;
import nc.itf.arap.pub.IBXBillPublic;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.arap.bx.util.ActionUtils;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.erm.common.MessageVO;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
/**
 * 借款报销单作废按钮
 * @author wangled
 *
 */
public class InvalidAction extends NCAction {

	private static final long serialVersionUID = 1L;
	private BillManageModel model;
	
	
	public InvalidAction() {
		setCode("Invalid");
		this.setBtnName(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0175")/*@res "作废"*/);
	}
	
	@Override
	public void doAction(ActionEvent arg0) throws Exception {
		Object[] vos = (Object[]) getModel().getSelectedOperaDatas();
		if (vos==null || vos.length < 1) {
			throw new BusinessException(nc.ui.ml.NCLangRes.getInstance().getStrByID("201107_0","0201107-0176")/* @res "没有可作废的单据,操作失败"*/);
		}
		
		JKBXVO[] jkbxVos = Arrays.asList(vos).toArray(new JKBXVO[0]);
		
		MessageVO[] msgs = new MessageVO[jkbxVos.length];
		
		MessageVO[] returnMsgs = invalidOneByOne(jkbxVos);
		
		List<AggregatedValueObject> successVos = ErUiUtil.combineMsgs(msgs, returnMsgs);
		
		//更新界面数据
		getModel().directlyUpdate(successVos.toArray(new AggregatedValueObject[] {}));
		
		//界面提示
		ErUiUtil.showBatchResults(getModel().getContext(), returnMsgs);
		
//		boolean existSuccess = true;
//		boolean exist = false;
//		
//		JKBXVO[] jkbxVos = Arrays.asList(vos).toArray(new JKBXVO[0]);
//		StringBuffer msg = new StringBuffer();
//		List<JKBXVO> dealVo = new ArrayList<JKBXVO>();
//		
//		for (int i = 0; i < jkbxVos.length; i++) {
//			if ((BXStatusConst.DJZT_Saved != ((JKBXVO) jkbxVos[i]).getParentVO().getDjzt().intValue())) {
//				existSuccess = false;
//				msg.append(nc.ui.ml.NCLangRes.getInstance().getStrByID("201107_0",
//						"0201107-0166") + jkbxVos[i].getParentVO().getDjbh() + " "+nc.ui.ml.NCLangRes.getInstance().getStrByID("201107_0",
//						"0201107-0177")).append("\r\n");
//						continue;
//			}
//			dealVo.add(jkbxVos[i]);
//		}
//		
//		if (dealVo != null && dealVo.size() != 0) {
//			try{
//				
//				List<JKBXVO> returnVo = NCLocator.getInstance().lookup(IBXBillPrivate.class).dealInvalid(dealVo);
//				
//				exist = true;//有成功的
//				//
//				getModel().directlyUpdate(returnVo.toArray(new AggregatedValueObject[returnVo.size()]));
//			}catch(BusinessException e){
//				existSuccess = false;
//				msg.append(e.getMessage());
//			}
//		}
//		
//		
//		if (existSuccess) {
//			ShowStatusBarMsgUtil.showStatusBarMsg(nc.ui.ml.NCLangRes.getInstance().getStrByID("201107_0", "0201107-0178"),getModel().getContext());
//		} else if(!existSuccess && exist){
//			ShowStatusBarMsgUtil.showErrorMsg(nc.ui.ml.NCLangRes.getInstance().getStrByID("201107_0", "0201107-0179"), msg.toString(),
//					getModel().getContext());
//		} else if(!existSuccess && !exist){
//			ShowStatusBarMsgUtil.showErrorMsg(nc.ui.ml.NCLangRes.getInstance().getStrByID("201107_0", "0201107-0180"), msg.toString(),
//					getModel().getContext());
//		}
	}
	
	private MessageVO[] invalidOneByOne(JKBXVO[] vos) throws Exception {
		List<MessageVO> resultList = new ArrayList<MessageVO>();
		for (JKBXVO aggVo : vos) {
			MessageVO msgReturn = invalidSingle(aggVo);
			resultList.add(msgReturn);
		}
		return resultList.toArray(new MessageVO[] {});
	}

	private MessageVO invalidSingle(JKBXVO jkbxvo) throws Exception {
		
		MessageVO result = null;
		try {
			JKBXVO returnVo = NCLocator.getInstance().lookup(IBXBillPublic.class).invalidBill(jkbxvo);
			result = new MessageVO(returnVo, ActionUtils.INVALID);
		} catch (Exception e) {
			ExceptionHandler.consume(e);
			String errMsg = e.getMessage();
			result = new MessageVO(jkbxvo, ActionUtils.INVALID, false, errMsg);
		}
		return result;
	}

	/**
	 * 保存未生效的单据
	 */
	@Override
	protected boolean isActionEnable() {
		boolean inenable = false;
		Object[] vos = (Object[]) getModel().getSelectedOperaDatas();
		if (vos != null && vos.length != 0) {
			for (int i = 0; i < vos.length; i++) {
				if ((BXStatusConst.DJZT_Saved == ((JKBXVO) vos[i]).getParentVO().getDjzt().intValue())) {
					inenable = true;
					break;
				}
			}
		}
		return inenable;
	}
	public BillManageModel getModel() {
		return model;
	}

	public void setModel(BillManageModel model) {
		this.model = model;
		this.model.addAppEventListener(this);
	}
}
