package nc.ui.erm.closeacc.view;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JLabel;
import javax.swing.JPanel;

import nc.bd.accperiod.AccperiodParamAccessor;
import nc.bs.logging.Logger;
import nc.ui.bd.ref.AbstractRefModel;
import nc.ui.bd.ref.model.AccPeriodDefaultRefModel;
import nc.ui.erm.closeacc.model.CloseAccManageModel;
import nc.ui.pub.beans.UIComboBox;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.ValueChangedEvent;
import nc.ui.pub.beans.ValueChangedListener;
import nc.ui.pub.beans.constenum.DefaultConstEnum;
import nc.ui.uif2.components.RefPanel;
import nc.ui.uif2.model.IAppModelDataManager;
/**
 * 
 * @author wangled
 *
 */
@SuppressWarnings("serial")
public class CloseAccPanel extends UIPanel{
	private CloseAccManageModel model;
	private IAppModelDataManager modelmanager;
	
    private String getNameYjz() {
    	return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UPP2011-000932")/*@res "可取消结账"*/;
    }
    
    private String getNameWjz() {
        return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UPP2011-000933")/*@res "可结账"*/;
    }

	public void  initUI(){
//	    add(new JLabel("会计期间方案"));
//	    add(getAccPeriodSchemePane());
		this.add(getToporgpane());
        //this.add(getTopperiodpane());
		add(new JLabel(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UPP2011-000931")/*@res "状态"*/));
        add(getUIComboBox());
        getUIComboBox().addItemListener(new ComboBoxItemListener());
//        UIButton queryBtn = new UIButton("查询");
//        queryBtn.addActionListener(new ActionListener() {
//        	
//            @Override
//            public void actionPerformed(ActionEvent e) {
//            	String[] orgPks = ((CloseAccFinOrgPanel)getToporgpane()).getRefPane().getRefPKs();
//            	String pkAccperiod = ((CloseAccPeriodPanel) getTopperiodpane()).getRefPane().getRefPK();
//            	((CloseAccManageModel)getModel()).setPk_accperiodmonth(pkAccperiod);
//                if(orgPks !=null || pkAccperiod !=null){
//                	getModelmanager().initModel();
//                }
//            }
//            
//        });
//        add(queryBtn);
		this.setLayout(new FlowLayout(FlowLayout.LEFT));
	}
	
	private class ComboBoxItemListener implements ItemListener{

		@Override
		public void itemStateChanged(ItemEvent e) {
			getModelmanager().initModel();
		}
		
	}
	
	private UIComboBox status;
	public UIComboBox getUIComboBox() {
		if(status == null){
			status = new UIComboBox();
			status.addItems(new DefaultConstEnum[] {
					new DefaultConstEnum("wjz", getNameWjz()),
					new DefaultConstEnum("yjz", getNameYjz())});
			status.setFocusable(false);//设置值后，离开焦点
		}
		return status;
	}
	
	private UIRefPane accPeriodSchemePane;
	
	public JPanel getAccPeriodSchemePane() {
	    if (accPeriodSchemePane == null) {
	        accPeriodSchemePane = new UIRefPane();
	        accPeriodSchemePane.setRefNodeName("会计期间方案"/* -=notranslate=- */);
	        accPeriodSchemePane.setPreferredSize(new Dimension(150, 20));
	        String defaultSchemePk = AccperiodParamAccessor.getInstance().getDefaultSchemePk();//"0001Z000000000000001";
	        accPeriodSchemePane.setPK(defaultSchemePk);
	        accPeriodSchemePane.addValueChangedListener(new ValueChangedListener() {

                @Override
                public void valueChanged(ValueChangedEvent event) {
                    CloseAccFinOrgPanel orgRefPane = (CloseAccFinOrgPanel)getToporgpane();
                    RefPanel refPane = (RefPanel)getTopperiodpane();
                    AccPeriodDefaultRefModel refModel = (AccPeriodDefaultRefModel)refPane.getRefModel();
                    String[] vals = null;
                    if (event.getNewValue() != null && event.getNewValue().getClass().isArray()) {
                        vals = (String[])event.getNewValue();   
                    } else {
                        vals = new String[] {event.getNewValue().toString()};
                    }
                    if (vals != null && vals.length > 0) {
                        refModel.setDefaultpk_accperiodscheme(vals[0]);
                        AbstractRefModel orgRefModel = orgRefPane.getRefPane().getRefModel();
                        orgRefModel.addWherePart(" and pk_accperiodscheme = '" + vals[0] + "' ", false);
                    } else {
                        refModel.setDefaultpk_accperiodscheme(null);
                    }
                    Logger.error(refModel.getDefaultpk_accperiodscheme());
                }
	            
	        });
            accPeriodSchemePane.setValueObjFireValueChangeEvent(defaultSchemePk);
	    }
	    return accPeriodSchemePane;
	}
	
	private JPanel toporgpane;
	private JPanel topperiodpane;
	
	public JPanel getToporgpane() {
		return toporgpane;
	}
	public void setToporgpane(JPanel toporgpane) {
		this.toporgpane = toporgpane;
	}
	public JPanel getTopperiodpane() {
		return topperiodpane;
	}
	public void setTopperiodpane(JPanel topperiodpane) {
		this.topperiodpane = topperiodpane;
	}

	public CloseAccManageModel getModel() {
		return model;
	}

	public void setModel(CloseAccManageModel model) {
		this.model = model;
	}

	public IAppModelDataManager getModelmanager() {
		return modelmanager;
	}

	public void setModelmanager(IAppModelDataManager modelmanager) {
		this.modelmanager = modelmanager;
	}
	
	
	
	
}
