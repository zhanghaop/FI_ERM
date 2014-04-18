package nc.ui.erm.closeacc.view;

import java.awt.Dimension;

import nc.bd.accperiod.AccperiodParamAccessor;
import nc.bd.accperiod.InvalidAccperiodExcetion;
import nc.bs.logging.Logger;
import nc.desktop.ui.WorkbenchEnvironment;
import nc.itf.org.IOrgConst;
import nc.pubitf.accperiod.AccountCalendar;
import nc.ui.bd.pub.BDOrgPanel;
import nc.ui.bd.ref.model.AccperiodYearRefModel;
import nc.ui.erm.closeacc.model.CloseAccManageModel;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.org.closeaccbook.CloseAccModelServicer;
import nc.ui.org.ref.FinanceOrgDefaultRefTreeModel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.ValueChangedEvent;
import nc.vo.bd.period.AccperiodVO;
import nc.vo.org.util.OrgTypeManager;
import nc.vo.pub.lang.UFDate;

import org.apache.commons.lang.StringUtils;

/**
 * 
 * @author wangled
 * 
 */
@SuppressWarnings("serial")
public class CloseAccFinOrgPanel extends BDOrgPanel {
	private CloseAccPeriodPanel topperiodpane = null;
	private UIRefPane refPane = null;
	
	public CloseAccFinOrgPanel() {
        super();
    }

    public void initUI() {
		super.initUI();
		getRefPane().getUITextField().setFocusable(false);//设置组织后，离开焦点
		getRefPane().setPreferredSize(new Dimension(360, 22));
	}

	public void valueChanged(ValueChangedEvent event) {
		try {
			String[] orgPks = getRefPane().getRefPKs();if(orgPks !=null){
                	getDataManager().initModel();
                }
		} catch (Exception e) {
			exceptionHandler.handlerExeption(e);
		}
	}

	@SuppressWarnings("unused")
	@Deprecated
	private void getData(String pk_org) {
	    String pkAccScheme = AccperiodParamAccessor.getInstance().getAccperiodschemePkByPk_org(pk_org);
        //设置会计年度的过滤条件
        ((AccperiodYearRefModel) getTopperiodpane().getRefModel())
                .setPk_accperiodscheme(pkAccScheme);
        UFDate curDate = WorkbenchEnvironment.getInstance().getBusiDate();
	    AccountCalendar calendar = AccountCalendar.getInstanceByPk_org(pk_org);
        AccperiodVO accperiodVO = null;
		try {
            calendar.setDate(curDate);
            accperiodVO = calendar.getYearVO();
        } catch (InvalidAccperiodExcetion e) {
            Logger.error(e.getMessage(), e);
        }
		if (accperiodVO != null) {
		    getTopperiodpane().getRefPane().setPK(accperiodVO.getPk_accperiod());
		    ((CloseAccManageModel) getModel()).setPk_accperiod(accperiodVO.getPk_accperiod());
		    getModel().getContext().setPk_org(pk_org);
		    //将组织的启用期间查出
		    getDataManager().initModel();
		} else {
		    getTopperiodpane().getRefPane().setPK(null);
		}

	}

	public CloseAccPeriodPanel getTopperiodpane() {
		return topperiodpane;
	}

	public void setTopperiodpane(CloseAccPeriodPanel topperiodpane) {
		this.topperiodpane = topperiodpane;
	}
	
	public CloseAccModelServicer getSerivce() {
		return new  CloseAccModelServicer() ;
	}

    @Override
    public UIRefPane getRefPane() {
        if (refPane == null) {
            refPane = new UIRefPane() ;
            refPane.setPreferredSize(new Dimension(200, 20));
            if (StringUtils.isNotBlank(getRefNodeName())) {
                refPane.setRefNodeName(getRefNodeName());
                refPane.getRefModel().setTableName(getOrgRefTableName());
            } else {
                FinanceOrgDefaultRefTreeModel refModel = new FinanceOrgDefaultRefTreeModel() {
                    @Override
                    public String getTableName() {
                        return getOrgRefTableName();
                    }
                    
                };
                refModel.setHiddenFieldCode(new String[] {"pk_financeorg", "pk_fatherorg", "pk_corp", "pk_group", "enablestate" , "pk_accperiodscheme"});
                refPane.setRefModel(refModel);
            }
            refPane.getRefModel().setPk_group(
                    getModel().getContext().getPk_group());
            String[] orgpks = getModel().getContext().getPkorgs();
            refPane.getRefModel().setFilterPks(orgpks);
            refPane.setMultiSelectedEnabled(true);
            refPane.setMultiOrgSelected(true);
            refPane.addValueChangedListener(this);
            refPane.setButtonFireEvent(true);
        }
        return refPane;
    }

    private String getOrgRefTableName() {
        String financeorg_fieldname = OrgTypeManager.getInstance().getOrgTypeByID(IOrgConst.FINANCEORGTYPE).getFieldname();
        StringBuffer sb = new StringBuffer();
        sb.append("SELECT org_orgs.pk_accperiodscheme, org_corp.code, org_corp.name, org_corp.name2, org_corp.name3, org_corp.name4, org_corp.name5, org_corp.name6, org_corp.pk_corp AS pk_financeorg, org_corp.pk_fatherorg, org_corp.pk_corp, org_corp.pk_group, org_financeorg.enablestate " +
                "FROM org_corp LEFT JOIN org_financeorg ON org_corp.pk_corp = org_financeorg.pk_financeorg " +
                "left join org_orgs on org_corp.pk_corp = org_orgs.pk_org WHERE org_orgs." + financeorg_fieldname + " = 'Y' ");
                
        sb.append(" UNION ");
        
        sb.append("SELECT pk_accperiodscheme, org_financeorg.code , org_financeorg.name , org_financeorg.name2 , org_financeorg.name3  , org_financeorg.name4, org_financeorg.name5 , org_financeorg.name6, org_financeorg.pk_financeorg, org_orgs.pk_corp as pk_fatherorg, org_orgs.pk_corp, org_financeorg.pk_group, org_financeorg.enablestate " +
                "FROM org_financeorg LEFT JOIN org_orgs ON org_financeorg.pk_financeorg = org_orgs.pk_org  " +
                "WHERE " + financeorg_fieldname + " = 'Y' AND org_financeorg.pk_financeorg <> org_orgs.pk_corp");
        

        return "(" + sb.toString() + ") org_financeorg_temp";
        
    }
    
    @Override
    protected void initDefaultOrg() {
//        AbstractUIAppModel model = getModel();
//        String defOrgPk = ErUiUtil.getDefaultPsnOrg();
        String defOrgPk = ErUiUtil.getDefaultOrgUnit();//取个性化中心的值
        getRefPane().setPK(defOrgPk);
//        if (model.getContext().getPk_org() != null) {
//            getRefPane().setPK(model.getContext().getPk_org());
//        } else {
//            getData(null);
//        }
    }

}
