package nc.vo.er.djlx;

import org.apache.commons.lang.StringUtils;

import nc.bs.logging.Log;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.CircularlyAccessibleValueObject;

/**
 * �˴���������˵���� �������ڣ�(2003-9-17 13:33:55)
 * 
 * @author����ǿ
 */
public class BillTypeVO extends AggregatedValueObject implements Cloneable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4129859872464524169L;
	private boolean m_isDirty = false;
	private DjLXVO parent = null;

	// private DjlxtempletVO[] children = null;
	/**
	 * BillTypeVO ������ע�⡣
	 */
	public BillTypeVO() {
		super();
	}

	/**
	 * ��¡һ����ȫ��ͬ��VO����(ǰ�㸴��)��
	 * 
	 * �������ڣ�(2001-3-7 11:34:51)
	 * 
	 * @return nc.vo.pub.ValueObject
	 */
	public Object clone() {

		Object o = null;
		try {
			o = super.clone();
		} catch (CloneNotSupportedException e) {

			Log.getInstance(this.getClass()).error(e.getMessage(), e);
		}

		return o;
	}

	/**
	 * getChildrenVO ����ע�⡣
	 */
	public CircularlyAccessibleValueObject[] getChildrenVO() {
		return null;
	}

	/**
	 * getParentVO ����ע�⡣
	 */
	public CircularlyAccessibleValueObject getParentVO() {
		return parent;
	}

	/**
	 * �Ƿ�Ϊ������ �������ڣ�(2001-8-8 12:44:04)
	 * 
	 * @since V1.00
	 * @return boolean
	 */
	public boolean isDirty() {
		return m_isDirty;
	}

	/**
	 * �����Ƿ�Ϊ������ �������ڣ�(2001-8-8 12:43:23)
	 * 
	 * @since V1.00
	 * @param isDirty
	 *            boolean
	 */
	public void setDirty(boolean isDirty) {
		m_isDirty = isDirty;
	}

	/**
	 * @see nc.vo.pub.AggregatedValueObject#setChildrenVO(nc.vo.pub.CircularlyAccessibleValueObject[])
	 */
	public void setChildrenVO(CircularlyAccessibleValueObject[] children) {
		//
		// this.children = (DjlxtempletVO[]) children;
	}

	/**
	 * @see nc.vo.pub.AggregatedValueObject#setParentVO(nc.vo.pub.CircularlyAccessibleValueObject)
	 */
	public void setParentVO(CircularlyAccessibleValueObject parent) {
		//
		this.parent = (DjLXVO) parent;
	}

	public String toString() {
		if (parent != null) {
			return parent.getDjlxjc();
		}
		return StringUtils.EMPTY;
	}

	public String getDjlxoid() {
		return parent.getDjlxoid();
	}

	public String getDjdl() {
		return parent.getDjdl();
	}

	public String getDjlxmc() {
		return parent.getDjlxmc();
	}

	public String getDjlxbm() {
		return parent.getDjlxbm();
	}
}
