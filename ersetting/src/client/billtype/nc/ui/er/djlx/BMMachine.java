package nc.ui.er.djlx;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import nc.bs.logging.Log;
import nc.vo.er.djlx.BillTypeVO;
import nc.vo.pub.BusinessException;
public class BMMachine {
	/* 保存所有可以使用的编码 */
	Vector<String> BMCleanlily = new Vector<String>();
	Vector number=null;
	Vector chars=null;
	private BillTypeVO[] billtypes = null;
/**
 * BMMachine 构造子注解。
 * 在这里将所有可以使用的编码保存，
 */
	public BMMachine(){
		super();
		this.number=this.numberChange();
		this.chars=this.charChange();
	}
/**
 * 此处插入方法说明。
 * 创建日期：(2002-4-29 14:58:11)
 */
public void BMDirty(String dirtyBm) {
	if(BMCleanlily.contains(dirtyBm)){
		BMCleanlily.removeElement(dirtyBm);
	}
	Log.getInstance(this.getClass()).debug(dirtyBm);
}
/**
 * 此处插入方法说明。
 * 创建日期：(2002-4-29 14:58:11)
 */
public String  getBM()throws BusinessException {
	String aim ;
	if(BMCleanlily.isEmpty()){
		throw new BusinessException(nc.ui.ml.NCLangRes.getInstance().getStrByID("20060101","UPP20060101-000002")/*@res "已经没有了可分配的单据编码，新增将取消"*/);
	}
	aim = BMCleanlily.elementAt(0).toString();
	return aim;
}
/**
 * 此处插入方法说明。
 * 创建日期：(2002-4-29 14:58:11)
 */
public void insertBM(String bm) {
	BMCleanlily.addElement(bm);
}

private Vector numberChange()
{
	Vector<String> result=new Vector<String>();
	for(int i=0;i<=9;i++)
	{
		result.add(i+"");
	}
	return result;
}

private Vector charChange()
{
	Vector<String> result=new Vector<String>();
	for(int i=0;i<26;i++)
	{
		result.add(new String(new Character((char)('A'+i)).toString()));
	}
	return result;
}

public void init()
{
	for(int i=0;i<this.chars.size();i++)
	{
		for(int j=0;j<this.number.size();j++)
		{
			BMCleanlily.addElement("23"+this.chars.get(i).toString()+this.number.get(j).toString());
		}
		for(int j=0;j<this.chars.size();j++)
		{
			BMCleanlily.addElement("23"+this.chars.get(i).toString()+this.chars.get(j).toString());
		}
	}
	for(int i=0;i<this.number.size();i++)
	{
		for(int j=0;j<this.number.size();j++)
		{
			BMCleanlily.addElement("24"+this.number.get(i).toString()+this.number.get(j).toString());
		}
		for(int j=0;j<this.chars.size();j++)
		{
			BMCleanlily.addElement("24"+this.number.get(i).toString()+this.chars.get(j).toString());
		}
	}
	for(int i=0;i<this.chars.size();i++)
	{
		for(int j=0;j<this.number.size();j++)
		{
			BMCleanlily.addElement("24"+this.chars.get(i).toString()+this.number.get(j).toString());
		}
		for(int j=0;j<this.chars.size();j++)
		{
			BMCleanlily.addElement("24"+this.chars.get(i).toString()+this.chars.get(j).toString());
		}
	}
	for(int i=0;i<this.number.size();i++)
	{
		for(int j=0;j<this.number.size();j++)
		{
			BMCleanlily.addElement("25"+this.number.get(i).toString()+this.number.get(j).toString());
		}
		for(int j=0;j<this.chars.size();j++)
		{
			BMCleanlily.addElement("25"+this.number.get(i).toString()+this.chars.get(j).toString());
		}
	}
	for(int i=0;i<this.chars.size();i++)
	{
		for(int j=0;j<this.number.size();j++)
		{
			BMCleanlily.addElement("25"+this.chars.get(i).toString()+this.number.get(j).toString());
		}
		for(int j=0;j<this.chars.size();j++)
		{
			BMCleanlily.addElement("25"+this.chars.get(i).toString()+this.chars.get(j).toString());
		}
	}
	try
	{

		BillTypeVO[] btvos = getBilltypes();
		String[] exist=new String[btvos.length];
		for(int i=0;i<exist.length;i++)
		{
			exist[i]=btvos[i].getDjlxbm();
		}
		List list=Arrays.asList(exist);
		this.BMCleanlily.removeAll(list);
	}
	catch(Exception e)
	{
		Log.getInstance(this.getClass()).error(e.getMessage(),e);
	}

}
private BillTypeVO[] getBilltypes() {
	return billtypes;
}
public void setBilltypes(BillTypeVO[] billtypes) {
	this.billtypes = billtypes;
}
}