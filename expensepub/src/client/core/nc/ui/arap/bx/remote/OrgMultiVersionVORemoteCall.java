package nc.ui.arap.bx.remote;

import nc.ui.arap.bx.BXBillMainPanel;
import nc.vo.pub.lang.UFDate;
import nc.vo.vorg.OrgVersionVO;

/**
 * ��֯��汾VOԶ�̵���
 * 
 * @author chendya
 * 
 */
public class OrgMultiVersionVORemoteCall extends
		AbstractMultiVersionVORemoteCall {

	public static String OrgMultiVersionVORemoteCall_ID = "OrgMultiVersionVORemoteCall";

	public OrgMultiVersionVORemoteCall(BXBillMainPanel panel, String date) {
		super(panel, date);
	}

	public OrgMultiVersionVORemoteCall(BXBillMainPanel panel, UFDate date) {
		super(panel, date);
	}

	public OrgMultiVersionVORemoteCall(BXBillMainPanel panel) {
		super(panel);
	}

	@Override
	protected Class<?> getVoClass() {
		return OrgVersionVO.class;
	}

	@Override
	protected String getWhereCondition() {
		StringBuffer buf = new StringBuffer();
		// ��ǰ����
		buf.append(OrgVersionVO.PK_GROUP +" = " + " '" + getPk_group() + "' ");
		// ��ֹ����
		buf.append(" and " + OrgVersionVO.VSTARTDATE + " <= " + " '"+ getvStartDate() + "' ");
		return buf.toString();
	}

	@Override
	protected String[] getQryField() {
		return new String[] { OrgVersionVO.PK_VID, OrgVersionVO.PK_ORG };
	}

	@Override
	protected String getCacheKey() {
		return OrgMultiVersionVORemoteCall_ID + "," + getPk_group() + "," + getvStartDate();
	}
}
