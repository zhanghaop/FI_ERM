package nc.bs.erm.util;

import java.util.ArrayList;
import java.util.List;

import nc.bd.accperiod.InvalidAccperiodExcetion;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.itf.arap.prv.IBXBillPrivate;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.ep.bx.Paytarget;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.pf.IPfRetCheckInfo;
/**
 * 
 * @author wangled
 *
 */
public class ErmBillPubUtil {
	
	public static JKBXVO[] getWriteBackBillVO(JKBXVO[] bills, UFDate current, String userid) throws BusinessException {
		List<JKBXVO> ret = new ArrayList<JKBXVO>();
		for (JKBXVO bill : bills) {
			try {
				JKBXVO writeBackBillVO = getWriteBackBillVO(bill, current, userid);
				ret.add(writeBackBillVO);
			} catch (InvalidAccperiodExcetion e) {
				throw ExceptionHandler.createException(e.getMessage());
			}
		}
		return ret.toArray(new JKBXVO[0]);
	}
	/**
	 * 生成最终的借款报销单红冲单据
	 * @param bill
	 * @param current
	 * @param userid
	 * @return
	 * @throws InvalidAccperiodExcetion
	 */
	private static JKBXVO getWriteBackBillVO(JKBXVO bill, UFDate current,
			String userid) throws InvalidAccperiodExcetion{
		//表体的金额字段
		String[] itemMnyKeys = { BXBusItemVO.AMOUNT,BXBusItemVO.YJYE, BXBusItemVO.YBYE,
				BXBusItemVO.YBJE, BXBusItemVO.BBYE, BXBusItemVO.BBJE,
				BXBusItemVO.CJKYBJE, BXBusItemVO.CJKBBJE, BXBusItemVO.ZFYBJE,
				BXBusItemVO.ZFBBJE, BXBusItemVO.HKYBJE, BXBusItemVO.HKBBJE,
				BXBusItemVO.HKYBJE,BXBusItemVO.HKBBJE,BXBusItemVO.FYYBJE,
				BXBusItemVO.FYBBJE,BXBusItemVO.GLOBALBBYE,BXBusItemVO.GLOBALBBJE,
				BXBusItemVO.GLOBALCJKBBJE,BXBusItemVO.GLOBALHKBBJE,BXBusItemVO.GLOBALZFBBJE,
				BXBusItemVO.GROUPBBJE,BXBusItemVO.GROUPBBYE,BXBusItemVO.GROUPCJKBBJE,BXBusItemVO.GROUPHKBBJE,
				BXBusItemVO.GROUPZFBBJE};
		
		String[] clearKeys = new String[] { JKBXHeaderVO.PJH,JKBXHeaderVO.PAYDATE, JKBXHeaderVO.PAYMAN, JKBXHeaderVO.PAYFLAG, JKBXHeaderVO.DJBH};
		JKBXVO vo = (JKBXVO) bill.clone();
		JKBXHeaderVO parent = (JKBXHeaderVO) vo.getParentVO();
		BXBusItemVO[] children = (BXBusItemVO[]) vo.getChildrenVO();
		
		UFDouble NEGATIVE = new UFDouble("-1");//需要与金额字段相乘
		
		String[] result;
		String pk_psndoc=null;
		try {
			result = NCLocator.getInstance().lookup(
					IBXBillPrivate.class).queryPsnidAndDeptid(userid,parent.getPk_group());
			if(result[0] == null || StringUtil.isEmpty(result[0])){
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011ermpub0316_0",
						"02011ermpub0316-0000")/* * * @res*
						 * "当前用户未关联人员，请联系管理人员为此用户指定身份"*/);
			}
			pk_psndoc = result[0];
		} catch (BusinessException e) {
			ExceptionHandler.consume(e);
		}
		
		// 清空表头项
		for (String key : clearKeys) {
			parent.setAttributeValue(key, null);
		}
		parent.setQcbz(UFBoolean.FALSE);
		parent.setCreator(userid);
		parent.setOperator(userid);
		parent.setJsr(pk_psndoc);
		parent.setJkbxr(pk_psndoc);
		parent.setReceiver(pk_psndoc);
		parent.setApprover(pk_psndoc);
		parent.setPrimaryKey(null);
		long bizDateTime = InvocationInfoProxy.getInstance().getBizDateTime();
		parent.setShrq(new UFDateTime(bizDateTime));
		parent.setDjrq(current);
		parent.setJsrq(current);
		parent.setPaydate(current);
		parent.setShrq_show(current);
		parent.setDjzt(BXStatusConst.DJZT_Saved);
		parent.setSxbz(BXStatusConst.SXBZ_NO);
		parent.setSpzt(IPfRetCheckInfo.NOSTATE);
		parent.setPayflag(BXStatusConst.PAYFLAG_None);
		parent.setIsreded(UFBoolean.TRUE);
		parent.setTs(null);
		parent.setGroupbbhl(parent.getGroupbbhl() == null ? UFDouble.ZERO_DBL : parent.getGroupbbhl());
		parent.setGlobalbbhl(parent.getGlobalbbhl() == null ? UFDouble.ZERO_DBL : parent.getGlobalbbhl());
		
		for (int i = 0; i < children.length; i++) {
			children[i].setPk_jkbx(null);
			children[i].setPrimaryKey(null);
			children[i].setStatus(VOStatus.NEW);
			for(String itemkey : itemMnyKeys){//表体的金额字段
				UFDouble ufDouble = (UFDouble)children[i].getAttributeValue(itemkey);
				UFDouble multiply = ufDouble==null ?UFDouble.ZERO_DBL.multiply(NEGATIVE):ufDouble.multiply(NEGATIVE);
				children[i].setAttributeValue(itemkey, multiply);
			}
		}
		//将表体的金额字段合计到表头
		for (int i = 0; i < children.length; i++) {
			for(String itemkey : itemMnyKeys){//表体的金额字段
				UFDouble itemValue = (UFDouble)children[i].getAttributeValue(itemkey);
				if(itemkey.equals(BXBusItemVO.AMOUNT)){
					parent.setTotal(itemValue);
				}
				if(itemValue!=UFDouble.ZERO_DBL){
					parent.setAttributeValue(itemkey, itemValue);
				}else{
					parent.setAttributeValue(itemkey, UFDouble.ZERO_DBL);
				}
			}
		}
		/**
		 * 设置表头的支付对象信息
		 */
		for (int i = 0; i < children.length; i++) {
				if(children[i].getPaytarget().intValue()==Paytarget.EMPLOYEE){
					parent.setPaytarget(Paytarget.EMPLOYEE);
					parent.setReceiver(children[i].getReceiver());
					parent.setSkyhzh(children[i].getSkyhzh());
				}else if(children[i].getPaytarget().intValue()==Paytarget.HBBM){
					parent.setPaytarget(Paytarget.HBBM);
					parent.setHbbm(children[i].getHbbm());
					parent.setCustaccount(children[i].getCustaccount());
				}else if(children[i].getPaytarget().intValue()==Paytarget.CUSTOMER){
					parent.setPaytarget(Paytarget.CUSTOMER);
					parent.setCustomer(children[i].getCustomer());
					parent.setCustaccount(children[i].getCustaccount());
				}
		}
		
		return vo;
	}
}
