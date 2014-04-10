package nc.vo.ep.bx;

/**
 * 借款单表头VO（仅支持根据vo classname 查询元数据实体特意扩展）
 * @author chendya
 *
 */
public class JKHeaderVO extends JKBXHeaderVO {
	
	private static final long serialVersionUID = -6568273138082575715L;

	public JKHeaderVO(){
		super();
	}

	@Override
	public String getTableName() {
		if(isInit())
			return "er_jkbx_init";
		return "er_jkzb";
	}
}
