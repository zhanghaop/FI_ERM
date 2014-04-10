package nc.vo.ep.bx;

import nc.vo.pub.IVOMeta;
import nc.vo.pubapp.pattern.model.meta.entity.vo.VOMetaFactory;


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

	@nc.vo.annotation.MDEntityInfo(beanFullclassName =  "nc.vo.ep.bx.BXHeaderVO" )
	public IVOMeta getMetaData() {
		IVOMeta meta = VOMetaFactory.getInstance().getVOMeta("erm.bxzb");
		return meta;
	}
}
