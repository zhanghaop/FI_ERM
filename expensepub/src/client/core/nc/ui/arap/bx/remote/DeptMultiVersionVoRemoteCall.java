package nc.ui.arap.bx.remote;

import nc.ui.arap.bx.BXBillMainPanel;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.lang.UFDate;
import nc.vo.vorg.DeptVersionVO;

public class DeptMultiVersionVoRemoteCall extends AbstractMultiVersionVORemoteCall {

	public static String DeptMultiVersionVoCall_ID = "DeptMultiVersionVoCall";
	
	public DeptMultiVersionVoRemoteCall(BXBillMainPanel panel, String date,String pk_org) {
		super(panel, date);
		setPk_org(pk_org);
	}

	public DeptMultiVersionVoRemoteCall(BXBillMainPanel panel, UFDate date,String pk_org) {
		super(panel, date);
		setPk_org(pk_org);
	}

	public DeptMultiVersionVoRemoteCall(BXBillMainPanel panel,String pk_org) {
		super(panel);
		setPk_org(pk_org);
	}
	
	/**
	 * 此构造方法查全集团下的
	 * @param panel
	 */
	public DeptMultiVersionVoRemoteCall(BXBillMainPanel panel) {
		super(panel);
	}

	@Override
	protected String getCacheKey() {
		return DeptMultiVersionVoCall_ID + ","+getPk_group()+ "," + getPk_org() + ","+ getvStartDate();
	}

	@Override
	protected String[] getQryField() {
		return new String[] { DeptVersionVO.PK_VID, DeptVersionVO.PK_DEPT };
	}

	@Override
	protected Class<?> getVoClass() {
		return DeptVersionVO.class;
	}

	@Override
	protected String getWhereCondition() {
		StringBuffer buf = new StringBuffer();
		// 当前集团
		buf.append(DeptVersionVO.PK_GROUP +" = " + " '" + getPk_group() + "' ");
		if(!StringUtil.isEmpty(getPk_org())){
			// 组织
			String[] orgs = new String[]{getPk_org()};
			buf.append(" and " + DeptVersionVO.PK_ORG + " in " + createInSql(orgs, true));
		}
		//截止日期
		buf.append(" and " + DeptVersionVO.VSTARTDATE + " <= " + " '" + getvStartDate() + "' ");
		return buf.toString();
	}
}
