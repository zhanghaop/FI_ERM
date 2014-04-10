package nc.itf.arap.pub;

public interface ISqdlrKeyword {
	

	/**
	 * 关键字-业务员
	 */
	public static final String KEYWORD_BUSIUSER = "busiuser";
	
	/**
	 * 关键字-是否代理所有人员
	 */
	public static final String KEYWORD_ISALL = "isall";
	
	/**
	 * 关键字-代理部门
	 */
	public static final String KEYWORD_PK_DEPTDOC = "pk_deptdoc";
	
	/**
	 * 关键字-是否代理本部门
	 */
	public static final String KEYWORD_ISSAMEDEPT = "issamedept";
	
	/**
	 * 个人授权节点类型(用的同一张表,根据type字段区分)
	 */
	public static final int TYPE_SINGLE_AUTH_NODE= 1;

	/**
	 * 授权代理设置节点类型
	 */
	public static final int TYPE_AUTH_AGENT_NODE_TYPE = 0;

}
