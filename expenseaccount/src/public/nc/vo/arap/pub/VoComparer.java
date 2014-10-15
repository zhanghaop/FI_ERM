/*
 * 创建日期 2005-9-21
 * auchor 宋涛
 * TODO 要更改此生成的文件的模板，请转至
 * 窗口 － 首选项 － Java － 代码样式 － 代码模板
 * 
 * 功能：提供循环VO的统一比较方法，用于快速排序
 */
package nc.vo.arap.pub;

import java.util.Comparator;

import nc.bs.logging.Log;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.lang.UFDate;

/**
 * 功能：比较大小 作者：宋涛 创建时间：(2003-5-5 9:59:34) 使用说明：以及别人可能感兴趣的介绍 注意：现存Bug
 */
public class VoComparer implements sun.misc.Compare,Comparator {
    private String[] m_sCompareKeys;

    private boolean m_bAscend;

    private int m_iFlag = 1;

    /* 日期误差 */
    private int m_iDaterange = 0;

    /**
     * VoComparer 构造子注解。
     */
    public VoComparer() {
        super();
    }

    /**
     * doCompare 方法注解。
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
     * a功能： 作者：宋涛 创建时间：(2003-5-5 11:35:30) 参数： <|>返回值： 算法：
     * 
     * @return int
     */
    public int getDaterange() {
        return m_iDaterange;
    }

    /**
     * a功能： 作者：宋涛 创建时间：(2003-5-5 10:08:53) 参数： <|>返回值： 算法：
     * 
     * @return boolean
     */
    public boolean isAscend() {
        return m_bAscend;
    }

    /**
     * a功能： 作者：宋涛 创建时间：(2003-5-5 10:08:53) 参数： <|>返回值： 算法：
     * 
     * @param newAscend
     *            boolean
     */
    public void setAscend(boolean newAscend) {
        m_bAscend = newAscend;
        m_iFlag = (m_bAscend == true ? 1 : -1);
    }

    /**
     * 功能：比较标准 作者：宋涛 创建时间：(2003-5-5 10:01:36) 参数： <|>返回值： 算法：
     * 
     * @param comparekeys
     *            java.lang.String[]
     */
    public void setCompareKey(String[] comparekeys) {
        m_sCompareKeys = comparekeys;
    }

    /**
     * a功能： 作者：宋涛 创建时间：(2003-5-5 11:35:30) 参数： <|>返回值： 算法：
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

