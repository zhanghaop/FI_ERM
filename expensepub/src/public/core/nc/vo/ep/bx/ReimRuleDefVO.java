package nc.vo.ep.bx;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import nc.vo.arap.engine.IConfigVO;

public class ReimRuleDefVO implements Serializable, IConfigVO {

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
	@SuppressWarnings("unchecked")
	public Object clone (){
		ReimRuleDefVO ret=new ReimRuleDefVO();
		ret.setId(this.id);
		ret.setReimRuleDefList(reimRuleDefList);
		return ret;
	}
	

}
