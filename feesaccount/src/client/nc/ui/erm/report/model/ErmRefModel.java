package nc.ui.erm.report.model;

import nc.ui.bd.ref.AbstractRefModel;
import nc.vo.ml.NCLangRes4VoTransl;

public class ErmRefModel extends AbstractRefModel {
    
    public void reset() {
        setRefNodeName("����(����)");/*-=notranslate=-*/
        
        setFieldCode(new String[] { "code", "name", "modulename" });
        setFieldName(new String[] { 
                NCLangRes4VoTransl.getNCLangRes().getStrByID("common", "UC000-0003279") /* @res "����" */,
                NCLangRes4VoTransl.getNCLangRes().getStrByID("common", "UC000-0001155") /* @res "����" */,
                NCLangRes4VoTransl.getNCLangRes().getStrByID("common", "2UC000-000358") /* @res "����ģ��" */
                    });
        setHiddenFieldCode(new String[] { "pk_refinfo", "refclass", "metadatatypename" });
        setPkFieldCode("pk_refinfo");
        setRefCodeField("code");
        setRefNameField("name");
        setTableName("bd_refinfo");
        setMnecode(new String[] { "name" });

        //��ʾȫ������ ���������ù�����������
        setAddEnableStateWherePart(false);
        
//        setResourceID(IOrgResourceCodeConst.DEPT);
        
//        setFilterRefNodeName(new String[] {"ҵ��Ԫ"});/*-=notranslate=-*/
        
        //����ʾ˳�򡢱�������
        setOrderPart(" moduleName, code, name ");
        
        resetFieldName();
        
    }
    
}
