package nc.ui.erm.accruedexpense.budgetinit;

import java.awt.Container;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import nc.bs.erm.accruedexpense.common.AccFlowCheckInfo;
import nc.bs.erm.util.ErUtil;
import nc.ui.pf.pub.PFClientBizRetObj;
import nc.ui.pub.pf.IPFClientBizProcess;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.erm.accruedexpense.AggAccruedBillVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.BusinessRuntimeException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.pf.PfClientBizProcessContext;
import nc.vo.tb.control.NtbCtlInfoVO;
import nc.vo.wfengine.core.data.BasicType;
import nc.vo.wfengine.core.data.DataField;

public class ErmAccPfClientBizProcess implements IPFClientBizProcess {

	private String budgetControl_id = "isBudgetControl";
	private String budgetControl_name = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0","02011002-0054")/*@res "是否控制预算"*/;
	
	@SuppressWarnings("unchecked")
	public PFClientBizRetObj execute(Container parent, PfClientBizProcessContext context) {
		PFClientBizRetObj result = new PFClientBizRetObj();

		List argsList = context.getArgsList();
		if (argsList == null || argsList.size() == 0)
			return result;

		Object billvo = context.getBillvo();

		for (Iterator iterator = argsList.iterator(); iterator.hasNext();) {
			DataField df = (DataField) iterator.next();
			Object value = df.getInitialValue();
			if (value == null)
				continue;
			
			StringBuffer controlMsg = new StringBuffer();
			if (budgetControl_id.equals(df.getName())) {//预算控制
				if (UFBoolean.valueOf(value.toString()).booleanValue()) {
					NtbCtlInfoVO tpcontrolvo = null;
					try {
						tpcontrolvo = AccFlowCheckInfo.doNtbCheck((AggAccruedBillVO)billvo );
					} catch (BusinessException e) {
						throw new BusinessRuntimeException(e.getMessage(), e);
					}
					
					
					if(tpcontrolvo != null){
						if(tpcontrolvo.isControl()){
							result.setShowPass(false);//批准按钮置灰
						}
						String controlInfo = getYsControlInfo(tpcontrolvo);
						if(controlInfo != null && !controlInfo.trim().equals("")){
							controlMsg.append("\n" + controlInfo);
						}
					}
				}
			}
			result.setHintMessage(controlMsg.toString());
		}
		
		if (StringUtils.isEmpty(result.getHintMessage() == null ? null : result.getHintMessage().trim())) {
			return null;
		}
		return result;
	}

	private String getYsControlInfo(NtbCtlInfoVO tpcontrolvo) {
		if(tpcontrolvo == null){
			return null;
		}
		
		StringBuffer controlMsg = new StringBuffer();
		String[] seminfos;
		if ((tpcontrolvo != null) && tpcontrolvo.isControl()) { // 控制
			seminfos = tpcontrolvo.getControlInfos();
			for (int j = 0; j < seminfos.length; j++) {
				controlMsg.append("\n" + seminfos[j]); // 预警信息
			}
		} else if ((tpcontrolvo != null) && tpcontrolvo.isAlarm()) { // 预警
			seminfos = tpcontrolvo.getAlarmInfos();
			for (int j = 0; j < seminfos.length; j++) {
				controlMsg.append("\n" + seminfos[j]); // 预警信息
			}
		} else if ((tpcontrolvo != null)
				&& tpcontrolvo.isMayBeControl()) { // 柔性
			seminfos = tpcontrolvo.getFlexibleControlInfos();
			for (int j = 0; j < seminfos.length; j++) {
				controlMsg.append("\n" + seminfos[j]); // 预警信息
			}
		}
		
		return controlMsg.toString();
	}

	public DataField[] getApplicationArgs() {
		List<DataField> dataFieldList = new ArrayList<DataField>();
		//判断预算产品是否启用
		boolean istbbused = ErUtil.isProductTbbInstalled(BXConstans.TBB_FUNCODE);
		if(istbbused){
			dataFieldList.add(new DataField(budgetControl_id, budgetControl_name, BasicType.BOOLEAN));
		}
		
		
		return dataFieldList.toArray(new DataField[0]);
	}
}
