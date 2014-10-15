package nc.bs.er.util;

import java.util.List;

import nc.bs.framework.common.NCLocator;
import nc.itf.arap.prv.IBXBillPrivate;
import nc.itf.uap.rbac.IUserManageQuery;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.pub.compiler.PfParameterVO;
import nc.vo.pub.pf.plugin.IPfMsgCustomReceiver;
import nc.vo.pub.pf.plugin.ReceiverVO;
import nc.vo.sm.UserVO;

public class OperatorReceiver implements IPfMsgCustomReceiver {
	private String lrrCode = "lrr";
	private String lrrName = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UC000-0001925")/*@res "录入人"*/;
	private String jkbxrCode = "jkbxr";
	private String jkbxrName = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0","02011002-0157")/*@res "借款报销人"*/;

	public ReceiverVO[] createReceivers() {
		ReceiverVO lrr = new ReceiverVO();
		lrr.setName(lrrName);
		lrr.setCode(lrrCode);
		ReceiverVO dlr = new ReceiverVO();
		dlr.setName(jkbxrName);
		dlr.setCode(jkbxrCode);
		return new ReceiverVO[]{lrr,dlr};
	}

	public UserVO[] queryUsers(ReceiverVO receiverVO, PfParameterVO paravo) {
		if (receiverVO.getCode().equals(lrrCode)) {
			return queryOperator(paravo);
		}
		if(receiverVO.getCode().equals(jkbxrCode)){
			return queryOperatorByPsn(paravo);
		}
		return new UserVO[]{};
	}

	private UserVO[] queryOperatorByPsn(PfParameterVO paravo) {
		try {
			String billid = paravo.m_billId;
			IBXBillPrivate bXBillPrivate = (IBXBillPrivate)NCLocator.getInstance().lookup(IBXBillPrivate.class.getName());
			List<JKBXHeaderVO> list = bXBillPrivate.queryHeadersByPrimaryKeys(new String[]{billid}, null);
			if(list!=null&&list.size()>0){
				String bxrid = list.get(0).getJkbxr(); //报销人
				IUserManageQuery userManageQuery = ((IUserManageQuery) NCLocator.getInstance().lookup(IUserManageQuery.class.getName()));
				UserVO user = userManageQuery.queryUserVOByPsnDocID(bxrid);
				return new UserVO[]{user};
			}
		} catch (Exception e) {
			ExceptionHandler.handleExceptionRuntime(e);
		}
		return new UserVO[]{};
	}

	private UserVO[] queryOperator(PfParameterVO paravo) {
		try {
			String billid = paravo.m_billId;
			IBXBillPrivate bXBillPrivate = (IBXBillPrivate)NCLocator.getInstance().lookup(IBXBillPrivate.class.getName());
			List<JKBXHeaderVO> list = bXBillPrivate.queryHeadersByPrimaryKeys(new String[]{billid}, null);
			if(list!=null&&list.size()>0){
				String lrrid = list.get(0).getOperator(); //录入人
				IUserManageQuery userManageQuery = ((IUserManageQuery) NCLocator.getInstance().lookup(IUserManageQuery.class.getName()));
				UserVO user = userManageQuery.getUser(lrrid);
				return new UserVO[]{user};
			}
		} catch (Exception e) {
			ExceptionHandler.handleExceptionRuntime(e);
		}
		return new UserVO[]{};
	}

}