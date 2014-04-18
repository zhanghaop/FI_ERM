package nc.ui.erm.report.comp;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.logging.Logger;
import nc.ui.pub.beans.UIComboBox;
import nc.vo.fipub.report.QueryObjVO;

import org.apache.commons.lang.StringUtils;

public class ErmUIComboBox extends UIComboBox {
    private static final long serialVersionUID = 1L;

    private Map<String, Object> keyvalueMap = new HashMap<String, Object>();

    /**
     * ���ܣ��������ѡ��<br>
     * 
     * @param qryObjVOs  Ҫ��ӵĶ����б�<br>
     * @param showProp Ҫ��ʾ�Ķ�������<br>
     * @param firstNull ��һ���Ƿ�Ϊ��<br>
     */
    public void addItems(List<QueryObjVO> qryObjVOs, String showProp, boolean beFirstNull) {
        // ɾ������ѡ��
        this.removeAllItems();
        keyvalueMap.clear();

        if (qryObjVOs == null || qryObjVOs.size() == 0) {
            return;
        }

        String methodName = "get" + StringUtils.capitalize(showProp);
        Method method = null;
        try {
            method = qryObjVOs.get(0).getClass().getMethod(methodName);
        } catch (SecurityException e) {
            Logger.error(e.getMessage(), e);
        } catch (NoSuchMethodException e) {
            Logger.error(e.getMessage(), e);
        }

        List<String> qryObjList = new ArrayList<String>();
        int k = 0;
        if (beFirstNull) {
            qryObjList.add("");
            keyvalueMap.put("" + k, null);
            k++;
        }

        String dspName = null;
        QueryObjVO queryObjVO = null;
        for (int i = 0; i < qryObjVOs.size(); i++) {
            queryObjVO = qryObjVOs.get(i);
            if (StringUtils.isNotBlank(queryObjVO.getResid())) {
                dspName = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("arap",queryObjVO.getResid());
            } else {
                dspName = null;
            }
            
            if (method != null) {
                try {
                    dspName = StringUtils.isEmpty(dspName) ? (String) method.invoke(queryObjVO) : dspName;
                } catch (IllegalArgumentException e) {
                    Logger.error(e.getMessage(), e);
                    continue;
                } catch (IllegalAccessException e) {
                    Logger.error(e.getMessage(), e);
                    continue;
                } catch (InvocationTargetException e) {
                    Logger.error(e.getMessage(), e);
                    continue;
                }
                qryObjList.add(dspName);
                keyvalueMap.put("" + (i + k), queryObjVO);                
            }
        }

        // ���ѡ��б�
        this.addItems(qryObjList.toArray(new String[0]));
    }

    public QueryObjVO getSelectdItem() {
        String sltIndex = "" + this.getSelectedIndex();
        return (keyvalueMap.get(sltIndex) == null ? null : ((QueryObjVO) keyvalueMap.get(sltIndex)));
    }

}

// /:~
