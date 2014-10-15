package nc.vo.ep.bx;

import java.io.Serializable;
import java.util.List;

import nc.vo.arap.engine.IConfigVO;


public class BilltypeRuleVO implements Serializable, IConfigVO ,Cloneable {

	private static final long serialVersionUID = -1;

	public static String key = "billtyperule";
	
	public static String MACTRLBILL = "mactrlbill";// �������뵥���ƶ����id
	public static String FIELDCONTRAST_SHARESTATE = "fieldcontrast_sharestate";// ά�ȶ��շ�̯�����id
	public static String BILLCONTRAST_SRC = "billcontrast_src";// ���ݶ�����Դ�����id
	public static String BILLCONTRAST_DES = "billcontrast_des";// ���ݶ���Ŀ������id


	
	public BilltypeRuleVO() {
	}

	private String id; // ����

	/**
	 * ���ƶ����ϼ�
	 */
	private List<String> parentitems;
	/**
	 * ��Ҫ�ų��Ŀ��ƶ���
	 */
	private List<String> excludeitems;
	/**
	 * ���ƶ���
	 */
	private List<String> items;


	public List<String> getItems() {
		return items;
	}

	public void setItems(List<String> items) {
		this.items = items;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}


	@Override
	public Object clone() {
		BilltypeRuleVO ret;
		try {
			ret = (BilltypeRuleVO) super.clone();
			ret.setId(this.id);
			ret.setItems(this.items);
		} catch (CloneNotSupportedException e) {
			  throw new RuntimeException("clone not supported!");
		}
		return ret;
	}

	public List<String> getParentitems() {
		return parentitems;
	}

	public void setParentitems(List<String> parentitems) {
		this.parentitems = parentitems;
	}

	public List<String> getExcludeitems() {
		return excludeitems;
	}

	public void setExcludeitems(List<String> excludeitems) {
		this.excludeitems = excludeitems;
	}
	
}
