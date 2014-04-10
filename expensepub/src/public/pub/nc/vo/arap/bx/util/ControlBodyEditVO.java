package nc.vo.arap.bx.util;

import java.util.List;

public class ControlBodyEditVO extends BodyEditVO{
	public static int TIP_SHOW = 1;
	public static int TIP_CONTROL = 2;
	
	//1��ʾ��2����
	private int tip;
	//��ʽ�������
	protected java.lang.String FormulaRule;
	//�洢������׼ά���ֶ��е��ݶ�Ӧ��Ϊ���������ڵ��ݱ���������Щ��ʱ��Ҫ�������׼�ȽϵĲ���
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
