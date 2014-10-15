package nc.vo.ep.bx;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import nc.vo.arap.engine.IConfigVO;

public class ReimRuleDefVO implements Serializable, IConfigVO ,Cloneable{

	private static final long serialVersionUID = 1L;
	
	public static String key="reimruledef";
	
	private String id; //主键，单据类型编码+公司编码

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	List<ReimRuleDef> reimRuleDefList=new ArrayList<ReimRuleDef>();

	public List<ReimRuleDef> getReimRuleDefList() {
		return reimRuleDefList;
	}
	public void setReimRuleDefList(List<ReimRuleDef> reimRuleDefList) {
		this.reimRuleDefList = reimRuleDefList;
	}
	
	@Override
	public Object clone (){
		ReimRuleDefVO ret;
		try {
			ret = (ReimRuleDefVO)super.clone();
			ret.setId(this.id);
			ret.setReimRuleDefList(reimRuleDefList);
		} catch (CloneNotSupportedException e) {
			  throw new RuntimeException("clone not supported!");
		}
		return ret;
	}
}
