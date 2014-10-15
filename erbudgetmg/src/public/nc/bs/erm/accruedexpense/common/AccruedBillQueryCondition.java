package nc.bs.erm.accruedexpense.common;

public class AccruedBillQueryCondition implements java.io.Serializable{
	
	private static final long serialVersionUID = 1L;

	/**
	 * ��ѯ�Ի������ɲ�ѯ����
	 */
	private String whereSql;
	
	/**
	 * �ڵ����
	 */
	private String nodeCode;
	
	/**
	 * ��������
	 */
	private String pk_tradetype;
	
	/**
	 * �û�
	 */
	private String pk_user;
	
	/**
	 * ��Ա
	 */
	private String pk_psn;
	
	/**
	 * ��֯
	 */
	private String pk_org;
	
	/**
	 * ����
	 */
	private String pk_group;
	/**
	 * ������
	 */
	boolean user_approving = false;
	
	/**
	 * ������
	 */
	boolean user_approved = false;

	public String getWhereSql() {
		return whereSql;
	}

	public void setWhereSql(String whereSql) {
		this.whereSql = whereSql;
	}

	public String getNodeCode() {
		return nodeCode;
	}

	public void setNodeCode(String nodeCode) {
		this.nodeCode = nodeCode;
	}

	public String getPk_tradetype() {
		return pk_tradetype;
	}

	public void setPk_tradetype(String pk_tradetype) {
		this.pk_tradetype = pk_tradetype;
	}

	public String getPk_user() {
		return pk_user;
	}

	public void setPk_user(String pk_user) {
		this.pk_user = pk_user;
	}

	public String getPk_psn() {
		return pk_psn;
	}

	public void setPk_psn(String pk_psn) {
		this.pk_psn = pk_psn;
	}

	public String getPk_org() {
		return pk_org;
	}

	public void setPk_org(String pk_org) {
		this.pk_org = pk_org;
	}

	public String getPk_group() {
		return pk_group;
	}

	public void setPk_group(String pk_group) {
		this.pk_group = pk_group;
	}


	public boolean isUser_approving() {
		return user_approving;
	}

	public void setUser_approving(boolean user_approving) {
		this.user_approving = user_approving;
	}

	public boolean isUser_approved() {
		return user_approved;
	}

	public void setUser_approved(boolean user_approved) {
		this.user_approved = user_approved;
	}
}
