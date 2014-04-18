package nc.vo.arap.bx.util;

import java.util.List;

public class ControlBodyEditVO extends BodyEditVO{
	public static int TIP_SHOW = 1;
	public static int TIP_CONTROL = 2;
	
	//1提示，2控制
	private int tip;
	//公式定义规则
	protected java.lang.String FormulaRule;
	//存储报销标准维度字段有单据对应项为表体的项，当在单据表体输入这些项时需要触发与标准比较的操作
	protected List<BodyEditVO> dimlist;
	
	public int getTip() {
		return tip;
	}
	
	public void setTip(int tip) {
		this.tip = tip;
	}
	
	public java.lang.String getFormulaRule() {
		return FormulaRule;
	}

	public void setFormulaRule(java.lang.String formulaRule) {
		FormulaRule = formulaRule;
	}

	public List<BodyEditVO> getDimlist() {
		return dimlist;
	}

	public void setDimlist(List<BodyEditVO> dimlist) {
		this.dimlist = dimlist;
	}

	public String toString()
    {
        return (new StringBuilder()).append(getValue()).append(getRow())
               .append(getPos()).append(getItemkey()).append(getTablecode())
               .append(tip).append(FormulaRule).toString();
    }
}
