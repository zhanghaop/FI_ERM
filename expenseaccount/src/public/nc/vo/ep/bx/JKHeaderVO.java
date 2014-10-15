package nc.vo.ep.bx;

import nc.vo.pub.IVOMeta;
import nc.vo.pubapp.pattern.model.meta.entity.vo.VOMetaFactory;

/**
 * ����ͷVO����֧�ָ���vo classname ��ѯԪ����ʵ��������չ��
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
	
	@nc.vo.annotation.MDEntityInfo(beanFullclassName = "nc.vo.ep.bx.JKHeaderVO")
	public IVOMeta getMetaData() {
		IVOMeta meta = VOMetaFactory.getInstance().getVOMeta("erm.jkzb");
		return meta;
	}
}
