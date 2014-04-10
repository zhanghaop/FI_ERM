package nc.bs.erm.util;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import nc.md.model.IAttribute;
import nc.md.model.context.MDNode;
import nc.ui.bd.ref.AbstractRefModel;
import nc.ui.md.MDTreeNode;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.UIRefTextFiledRefFormatter;
import nc.ui.pub.beans.UITextField;
import nc.ui.pub.beans.UITree;
import nc.ui.pub.beans.textfield.formatter.DefaultTextFiledFormatterFactory;

public class MDPropertyRefPane extends UIRefPane {

	private static final long serialVersionUID = 1L;
	private MDPropertyDialog dialog = null;
	private String dialogName;
	private String entityid;
	private String bizmodelStyle;//业务场景
	private static final List<String> fieldList = new ArrayList<String>();
    
    static {
        fieldList.add("code");
        fieldList.add("name");
    }
    
    public MDPropertyRefPane(String dialogName, String entityid,String style) {
    	this(dialogName,entityid);
    	this.setBizmodelStyle(style);
    }
    
	public MDPropertyRefPane(String dialogName, String entityid) {
		super();
		this.setEntityid(entityid);
		this.setDialogName(dialogName);
		this.setBizmodelStyle("erm");
	}
	
	private AbstractRefModel newRefModel() {
	    AbstractRefModel refModel = new AbstractRefModel() {
            
            public int getFieldIndex(String field) {
                return fieldList.indexOf(field);
            }
            
            @SuppressWarnings({ "unchecked", "rawtypes" })
            @Override
            public Vector getData() {
                UITree mdTree = getDialog().getEntityTree();
                Enumeration enumera = ((MDTreeNode)mdTree.getModel().getRoot()).children();
                Vector vec = new Vector();
                while (enumera.hasMoreElements()) {
                    MDTreeNode treeNode = (MDTreeNode)enumera.nextElement();
                    MDNode node = (MDNode)treeNode.getUserObject();
                    IAttribute att = node.getAttribute();
                    Vector row = new Vector();
                    row.add(att.getName());
                    row.add(att.getDisplayName());
                    vec.add(row);
                }
                return vec;
            }
            
            @Override
            public String getPkFieldCode() {
                return fieldList.get(0);
            }

            @Override
            public String getRefNameField() {
                return fieldList.get(1);
            }

            @Override
            public String getRefCodeField() {
                return fieldList.get(0);
            }
            
            @Override
            public String[] getBlurFields() {
                return fieldList.toArray(new String[fieldList.size()]);
            }

            @SuppressWarnings({ "rawtypes" })
            public void setSelectedData(Vector vecSelectedData) {
                if (m_vecSelectedData == null && vecSelectedData == null) {
                    return;
                }
                m_vecSelectedData = vecSelectedData;
                setInitData(vecSelectedData);
            }

        };
        return refModel;
	}
	
	@Override
    public AbstractRefModel getRefModel() {
	    AbstractRefModel refModel = super.getRefModel();
	    if (refModel == null) {
	        refModel = newRefModel();
	        refModel.addChangeListener(null);
	        setRefModel(refModel);
	    }
	    return refModel;
    }

    public MDPropertyDialog getDialog() {
		if (dialog == null) {
				dialog = new MDPropertyDialog(this, getDialogName(), getEntityid(),getBizmodelStyle());
		}
		return dialog;
	}
	
	public void setDialog(MDPropertyDialog dialog) {
		this.dialog = dialog;
	}

    public void onButtonClicked() {
		//设值
		if (getDialog().showModal() == UIDialog.ID_OK) {
            String showCode = getRefShowCode(); 
            UITextField field = getUITextField();
            field.setValue(showCode);
            getRefModel().setSelectedData(getSelectedData());
		}
	}
	
	@Override
    public String getRefShowName() {
        StringBuffer sText = new StringBuffer() ;
        Map<String, String> map = getDialog().getSelecteddatas();
        if ( map != null) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                if (entry.getValue() != null) {
                    sText.append(entry.getValue()+",");
                }
            }
        }
        if (sText.length() > 0) {
            sText.deleteCharAt(sText.length() - 1);
        }
        return sText.toString();
    }

    private static UITextField textField = null;

    public UITextField getUITextField() {
        if (textField != super.getUITextField()) {
            textField = super.getUITextField();
            textField.setTextFiledFormatterFactory(new DefaultTextFiledFormatterFactory(
                            new UIRefTextFiledRefFormatter(this, true),
                            new UIRefTextFiledRefFormatter(this), null));
        }
        return textField;
    }
    
    public void removeTextFiledFormatter() {
//        getUITextField().setTextFiledFormatterFactory(null);
    }
    
    @Override
    public String getRefShowCode() {
        StringBuffer sValue = new StringBuffer();
        Map<String, String> map = getDialog().getSelecteddatas();
        if (map != null) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                sValue.append(entry.getKey() + ",");
            }
        }
        if (sValue.length() > 0) {
            sValue.deleteCharAt(sValue.length() - 1);
        }
        return sValue.toString();
    }
    
   
    @SuppressWarnings({ "unchecked", "rawtypes" })
	private Vector getSelectedData() {
        String showCode = getRefShowCode();
        String showName = getRefShowName();
        String[] codeArray = showCode.split(",");
        String[] nameArray = showName.split(",");
        Vector vecSelectedData = new Vector();
        for (int nPos = 0; nPos < codeArray.length; nPos++) {
            Vector row = new Vector();
            row.add(codeArray[nPos]);
            row.add(nameArray[nPos]);
            vecSelectedData.add(row);
        }
        return vecSelectedData;
    }
  
	@SuppressWarnings("rawtypes")
    private void setInitData(Vector vector) {
        Map<String, String> map = new LinkedHashMap<String, String>();
        if (vector != null) {
//            String[] codeArray = ((String)((Vector)vector.get(0)).get(0)).split(",");
//            String[] nameArray = ((String)((Vector)vector.get(0)).get(1)).split(",");
//            for (int nPos = 0; nPos < codeArray.length; nPos++) {
//                map.put(codeArray[nPos], nameArray[nPos]);
//            }
            for (Object row : vector) {
                Vector rowa = (Vector) row;
                map.put((String)rowa.get(0), (String)rowa.get(1));
            }
        }
        getDialog().setSelecteddatas(map);
        setValueObjFireValueChangeEvent(getRefShowCode());
    }
    
    public String getDialogName() {
		return dialogName;
	}

	public void setDialogName(String dialogName) {
		this.dialogName = dialogName;
	}

	public String getEntityid() {
		return entityid;
	}

	public void setEntityid(String entityid) {
		this.entityid = entityid;
	}

	public String getBizmodelStyle() {
		return bizmodelStyle;
	}

	public void setBizmodelStyle(String bizmodelStyle) {
		this.bizmodelStyle = bizmodelStyle;
	}

}
