/*
 * �������� 2005-9-21
 * auchor ����
 * TODO Ҫ���Ĵ����ɵ��ļ���ģ�壬��ת��
 * ���� �� ��ѡ�� �� Java �� ������ʽ �� ����ģ��
 * 
 * ���ܣ��ṩѭ��VO��ͳһ�ȽϷ��������ڿ�������
 */
package nc.vo.arap.pub;

import java.util.Comparator;

import nc.bs.logging.Log;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.lang.UFDate;

/**
 * ���ܣ��Ƚϴ�С ���ߣ����� ����ʱ�䣺(2003-5-5 9:59:34) ʹ��˵�����Լ����˿��ܸ���Ȥ�Ľ��� ע�⣺�ִ�Bug
 */
public class VoComparer implements sun.misc.Compare,Comparator {
    private String[] m_sCompareKeys;

    private boolean m_bAscend;

    private int m_iFlag = 1;

    /* ������� */
    private int m_iDaterange = 0;

    /**
     * VoComparer ������ע�⡣
     */
    public VoComparer() {
        super();
    }

    /**
     * doCompare ����ע�⡣
     */
    public int doCompare(Object arg1, Object arg2) {
        if (m_sCompareKeys == null) {
            return 0;
        }
        int iFlag = 0;
        if ((arg1 == null && arg2 == null) || m_sCompareKeys == null
                || m_sCompareKeys.length <= 0) {
            return 0;
        } else if (arg1 == null) {
            return m_iFlag * (-1);
        } else if (arg2 == null) {
            return m_iFlag;
        }
        try {
            CircularlyAccessibleValueObject vo1 = (CircularlyAccessibleValueObject) arg1;
            CircularlyAccessibleValueObject vo2 = (CircularlyAccessibleValueObject) arg2;
            for (int i = 0; i < m_sCompareKeys.length; i++) {
                Object oValue1 = vo1.getAttributeValue(m_sCompareKeys[i]);
                Object oValue2 = vo2.getAttributeValue(m_sCompareKeys[i]);
                if(oValue1==null && oValue2==null){
                	iFlag= 0;
                } else if(oValue1==null){
                	return m_iFlag*-1;
                }else if(oValue2==null){
                	return m_iFlag;
                }else if (oValue1 instanceof String) {
                    iFlag = ((String) oValue1).compareTo((String) oValue2);
                } else if (oValue1 instanceof Integer) {
                    iFlag = ((Integer) oValue1).compareTo((Integer) oValue2);
                } else if (oValue1 instanceof nc.vo.pub.lang.UFDouble) {
                    iFlag = ((nc.vo.pub.lang.UFDouble) oValue1)
                            .compareTo(oValue2);
                } else if (oValue1 instanceof nc.vo.pub.lang.UFDate) {
                    iFlag = UFDate.getDaysBetween(
                            (nc.vo.pub.lang.UFDate) oValue2,
                            (nc.vo.pub.lang.UFDate) oValue1);//compareTo((nc.vo.pub.lang.UFDate)
                                                             // oValue2);
                    if (Math.abs(iFlag) < Math.abs(m_iDaterange)) {
                        iFlag = 0;
                    }
                } else {
                    iFlag = oValue1.toString().compareTo(oValue2.toString());
                }
                if (iFlag != 0) {
                    return m_iFlag * iFlag;
                }
            }
        } catch (Exception e) {
            Log.getInstance(this.getClass()).error(e.getMessage(),e);
            return 0;
        }
        return 0;
    }

    /**
     * a���ܣ� ���ߣ����� ����ʱ�䣺(2003-5-5 11:35:30) ������ <|>����ֵ�� �㷨��
     * 
     * @return int
     */
    public int getDaterange() {
        return m_iDaterange;
    }

    /**
     * a���ܣ� ���ߣ����� ����ʱ�䣺(2003-5-5 10:08:53) ������ <|>����ֵ�� �㷨��
     * 
     * @return boolean
     */
    public boolean isAscend() {
        return m_bAscend;
    }

    /**
     * a���ܣ� ���ߣ����� ����ʱ�䣺(2003-5-5 10:08:53) ������ <|>����ֵ�� �㷨��
     * 
     * @param newAscend
     *            boolean
     */
    public void setAscend(boolean newAscend) {
        m_bAscend = newAscend;
        m_iFlag = (m_bAscend == true ? 1 : -1);
    }

    /**
     * ���ܣ��Ƚϱ�׼ ���ߣ����� ����ʱ�䣺(2003-5-5 10:01:36) ������ <|>����ֵ�� �㷨��
     * 
     * @param comparekeys
     *            java.lang.String[]
     */
    public void setCompareKey(String[] comparekeys) {
        m_sCompareKeys = comparekeys;
    }

    /**
     * a���ܣ� ���ߣ����� ����ʱ�䣺(2003-5-5 11:35:30) ������ <|>����ֵ�� �㷨��
     * 
     * @param newDaterange
     *            int
     */
    public void setDaterange(int newDaterange) {
        m_iDaterange = newDaterange;
    }

	public int compare(Object o1, Object o2) {
		// TODO Auto-generated method stub
		return doCompare(o1,o2);
	}
}

