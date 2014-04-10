package nc.bs.erm.event;

/**
 * 事件类型常量
 * 
 * @author lvhj
 *
 */
public class ErmEventType {

	 public static final String TYPE_INSERT_BEFORE = "ERM1001"; //新增前
	 public static final String TYPE_INSERT_AFTER  = "ERM1002"; //新增后
	 
	 public static final String TYPE_UPDATE_BEFORE = "ERM1003"; //修改前
	 public static final String TYPE_UPDATE_AFTER  = "ERM1004"; //修改后
	 
	 public static final String TYPE_DELETE_BEFORE = "ERM1005"; //删除前
	 public static final String TYPE_DELETE_AFTER  = "ERM1006"; //删除后
	 
	 public static final String TYPE_USED_BEFORE = "ERM1007"; //启用前
	 public static final String TYPE_USED_AFTER = "ERM1008"; //启用后
	 
	 public static final String TYPE_UNUSED_BEFORE = "ERM1009"; //反启用前
	 public static final String TYPE_UNUSED_AFTER = "ERM1010"; //反启用后
	 
	 public static final String TYPE_APPROVE_BEFORE = "ERM1011"; //审批前
	 public static final String TYPE_APPROVE_AFTER = "ERM1012"; //审批后
	 
	 public static final String TYPE_UNAPPROVE_BEFORE = "ERM1013"; //取消审批前
	 public static final String TYPE_UNAPPROVE_AFTER = "ERM1014"; //取消审批后
	 
	 public static final String TYPE_COMMIT_BEFORE = "ERM1015"; //提交前
	 public static final String TYPE_COMMIT_AFTER = "ERM1016"; //提交后
	 
	 public static final String TYPE_RECALL_BEFORE = "ERM1017"; //收回前
	 public static final String TYPE_RECALL_AFTER = "ERM1018"; //收回后
	 
	 public static final String TYPE_CLOSE_BEFORE = "ERM1019"; //关闭前
	 public static final String TYPE_CLOSE_AFTER = "ERM1020"; //关闭后
	 
	 public static final String TYPE_UNCLOSE_BEFORE = "ERM1021"; //取消关闭前
	 public static final String TYPE_UNCLOSE_AFTER = "ERM1022"; //取消关闭后
	 
	 
	 public static final String TYPE_SIGN_BEFORE = "ERM1023"; //生效前
	 public static final String TYPE_SIGN_AFTER = "ERM1024"; //生效后
	 
	 public static final String TYPE_UNSIGN_BEFORE = "ERM1025"; //取消生效前
	 public static final String TYPE_UNSIGN_AFTER = "ERM1026"; //取消生效后
	 
	 public static final String TYPE_WRITEOFF_BEFORE = "ERM1027"; //审批前
	 public static final String TYPE_WRITEOFF_AFTER = "ERM1028"; //审批后
	 
	 public static final String TYPE_UNWRITEOFF_BEFORE = "ERM1029"; //取消审批前
	 public static final String TYPE_UNWRITEOFF_AFTER = "ERM1030"; //取消审批后
	 
	 public static final String TYPE_TEMPSAVE_BEFORE = "ERM1031"; //暂存前
	 public static final String TYPE_TEMPSAVE_AFTER = "ERM1032"; //暂存后
	 
	 public static final String TYPE_AMORTIZE_BEFORE = "ERM1033"; //摊销前
	 public static final String TYPE_AMORTIZE_AFTER = "ERM1034"; //摊销后
	 
	 public static final String TYPE_CLOSEACC_BEFORE = "ERM1035"; //结账前
	 public static final String TYPE_CLOSEACC_AFTER = "ERM1036"; //结账后
	 
	 public static final String TYPE_UNCLOSEACC_BEFORE = "ERM1037"; //取消结账前
	 public static final String TYPE_UNCLOSEACC_AFTER = "ERM1038"; //取消结账前
	 
	 public static final String TYPE_TEMPUPDATE_BEFORE = "ERM1039"; //暂存修改前
	 public static final String TYPE_TEMPUPDATE_AFTER = "ERM1040"; //暂存修改后
	 
	 public static final String TYPE_MTAPPWB_AFTER = "ERM1041";//冲费用申请单生效(write back)
	 
	 
	 public static final String TYPE_REDBACK_BEFORE = "ERM2001"; //红冲前
	 public static final String TYPE_REDBACK_AFTER = "ERM2002"; //红冲后
	 
	 public static final String TYPE_UNREDBACK_BEFORE = "ERM2003"; //删除红冲前
	 public static final String TYPE_UNREDBACK_AFTER = "ERM2004"; //删除红冲后
}
