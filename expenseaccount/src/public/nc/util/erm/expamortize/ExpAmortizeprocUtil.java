package nc.util.erm.expamortize;

import java.util.ArrayList;
import java.util.List;

import nc.vo.erm.expamortize.AggExpamtinfoVO;
import nc.vo.erm.expamortize.ExpamtDetailVO;
import nc.vo.erm.expamortize.ExpamtprocVO;
import nc.vo.pub.lang.UFDouble;

public class ExpAmortizeprocUtil {
	public static List<String> getNotCopyFieldsFromExpamortizeInfoVo() {
		List<String> fields = new ArrayList<String>();
		fields.add(ExpamtprocVO.PK_EXPAMTPROC);
		fields.add(ExpamtprocVO.CREATOR);
		fields.add(ExpamtprocVO.CREATIONTIME);
		fields.add(ExpamtprocVO.MODIFIER);
		fields.add(ExpamtprocVO.MODIFIEDTIME);
		fields.add(ExpamtprocVO.ACCPERIOD);
		fields.add(ExpamtprocVO.AMORTIZE_USER);
		fields.add(ExpamtprocVO.AMORTIZE_DATE);
		return fields;
	}
	
	public static ExpamtprocVO[] getExpamtProcVoFromExpamtinfoVO(AggExpamtinfoVO aggvo, boolean isUnAmt){
		ExpamtDetailVO[] children = (ExpamtDetailVO[])aggvo.getChildrenVO();
		List<ExpamtprocVO> result = new ArrayList<ExpamtprocVO>();
		
		String[] attributes = new ExpamtprocVO().getAttributeNames();
		List<String> notCopyFields = getNotCopyFieldsFromExpamortizeInfoVo();
		
		for(ExpamtDetailVO child : children){
			ExpamtprocVO proc = new ExpamtprocVO();
			for (String attribue : attributes) {
				if(!notCopyFields.contains(attribue)){
					proc.setAttributeValue(attribue, child.getAttributeValue(attribue));
				}
			}
			proc.setPk_expamtinfo(child.getPk_expamtdetail());
			
			if(isUnAmt){
				proc.setCurr_amount(proc.getCurr_amount().multiply(-1));
				proc.setCurr_orgamount(proc.getCurr_orgamount().multiply(-1));
				proc.setCurr_groupamount(proc.getCurr_groupamount() == null ? UFDouble.ZERO_DBL : proc.getCurr_groupamount().multiply(-1));
				proc.setCurr_globalamount(proc.getCurr_globalamount() == null ? UFDouble.ZERO_DBL : proc.getCurr_globalamount().multiply(-1));
			}
			result.add(proc);
		}
		return result.toArray(new ExpamtprocVO[]{});
	}
}
