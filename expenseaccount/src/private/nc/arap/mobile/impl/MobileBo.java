package nc.arap.mobile.impl;

import nc.bs.erm.accruedexpense.common.AccruedBillQueryCondition;
import nc.bs.erm.matterapp.common.MatterAppQueryCondition;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.itf.arap.prv.IBXBillPrivate;
import nc.itf.erm.accruedexpense.IErmAccruedBillQueryPrivate;
import nc.itf.erm.matterapp.IErmMatterAppBillQueryPrivate;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.pub.BusinessException;
import nc.vo.trade.pub.IBillStatus;

public class MobileBo {
	/**
	 * ��ѯ��������pk
	 * @param flag
	 * @return
	 * @throws BusinessException
	 */
	public String[] getJkBxPksByUser(String flag) throws BusinessException{
		//��ѯ��������pk
		StringBuffer sqlWhere = new StringBuffer();
		sqlWhere.append("djlxbm not in ('2647','264a')");
		if("1".equals(flag)){
			//��ѯ����ɵ���
			sqlWhere.append(" and " + JKBXHeaderVO.SPZT +" in (" + 
					""+IBillStatus.CHECKPASS + "," + IBillStatus.NOPASS + ")");
		}else{
			//��ѯδ��ɵ���
			sqlWhere.append(" and " + JKBXHeaderVO.SPZT +"<>"+IBillStatus.CHECKPASS + " and " + JKBXHeaderVO.SPZT +"<>"+IBillStatus.NOPASS);
		}
		//��ѯ����pk
		String[] pks = NCLocator.getInstance().lookup(IBXBillPrivate.class).queryPKsByWhereForBillNode(sqlWhere.toString(),InvocationInfoProxy.getInstance().getUserId()
				,null);
		return pks;
		
	}
	
	/**
	 * ���뵥������ѯ
	 */
	public String[] queryMaBillPksByWhere(String flag) throws BusinessException {
		MatterAppQueryCondition maCondVo = new MatterAppQueryCondition();
		if("1".equals(flag)){
			//��ѯ����ɵ���
			maCondVo.setWhereSql("apprstatus in (0,1)");
		}else{
			//��ѯδ��ɵ���
			maCondVo.setWhereSql("apprstatus in (2,3,-1)");
		}
		String loginUser = InvocationInfoProxy.getInstance().getUserId();
		//��ѯ�������뵥pk
		String pk_group = InvocationInfoProxy.getInstance().getGroupId();
		maCondVo.setPk_tradetype("2611");
		maCondVo.setNodeCode("201102611");
		maCondVo.setPk_group(pk_group);
		maCondVo.setPk_user(loginUser);
		String[] pks = NCLocator.getInstance().lookup(IErmMatterAppBillQueryPrivate.class).queryBillPksByWhere(maCondVo);
		return pks;
	}
	
	/**
	 * Ԥ�ᵥ������ѯ
	 */
	public String[] queryAccBillPksByWhere(String flag) throws BusinessException {
		AccruedBillQueryCondition condVo = new AccruedBillQueryCondition();
		if("1".equals(flag)){
			//��ѯ����ɵ���
			condVo.setWhereSql("apprstatus in (0,1)");
		}else{
			//��ѯδ��ɵ���
			condVo.setWhereSql("apprstatus in (2,3,-1)");
		}
		String loginUser = InvocationInfoProxy.getInstance().getUserId();
		//��ѯ�������뵥pk
		String pk_group = InvocationInfoProxy.getInstance().getGroupId();
		condVo.setPk_tradetype("2621");
		condVo.setNodeCode("20110ACCMN");
		condVo.setPk_group(pk_group);
		condVo.setPk_user(loginUser);
		String[] pks = NCLocator.getInstance().lookup(IErmAccruedBillQueryPrivate.class).queryBillPksByWhere(condVo);
		return pks;
	}
}
