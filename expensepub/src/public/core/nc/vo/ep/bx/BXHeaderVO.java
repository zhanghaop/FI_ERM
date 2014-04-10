package nc.vo.ep.bx;

public class BXHeaderVO extends JKBXHeaderVO {

	private static final long serialVersionUID = -5743459427569286178L;

	public BXHeaderVO(){
		super();
	}

	@Override
	public String getTableName() {
		if(isInit())
			return "er_jkbx_init";
		return "er_bxzb";
	}
}
