package nc.ui.erm.report.model;

import nc.ui.bd.ref.AbstractRefModel;
import nc.vo.ml.NCLangRes4VoTransl;

public class ErmRefModel extends AbstractRefModel {
    
    public void reset() {
        setRefNodeName("参照(所有)");/*-=notranslate=-*/
        
        setFieldCode(new String[] { "code", "name", "modulename" });
        setFieldName(new String[] { 
                NCLangRes4VoTransl.getNCLangRes().getStrByID("common", "UC000-0003279") /* @res "编码" */,
                NCLangRes4VoTransl.getNCLangRes().getStrByID("common", "UC000-0001155") /* @res "名称" */,
                NCLangRes4VoTransl.getNCLangRes().getStrByID("common", "2UC000-000358") /* @res "所属模块" */
                    });
        setHiddenFieldCode(new String[] { "pk_refinfo", "refclass", "metadatatypename" });
        setPkFieldCode("pk_refinfo");
        setRefCodeField("code");
        setRefNameField("name");
        setTableName("bd_refinfo");
        setMnecode(new String[] { "name" });

        //显示全部数据 不增加启用过滤条件开关
        setAddEnableStateWherePart(false);
        
//        setResourceID(IOrgResourceCodeConst.DEPT);
        
//        setFilterRefNodeName(new String[] {"业务单元"});/*-=notranslate=-*/
        
        //按显示顺序、编码排序
        setOrderPart(" moduleName, code, name ");
        
        resetFieldName();
        
    }
    
}
