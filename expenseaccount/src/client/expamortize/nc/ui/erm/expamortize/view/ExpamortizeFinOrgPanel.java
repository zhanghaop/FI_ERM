package nc.ui.erm.expamortize.view;

import java.awt.Dimension;

import nc.bd.accperiod.InvalidAccperiodExcetion;
import nc.bs.erm.util.ErAccperiodUtil;
import nc.itf.org.IOrgConst;
import nc.ui.bd.pub.BDOrgPanel;
import nc.ui.bd.ref.model.AccPeriodDefaultRefModel;
import nc.ui.er.util.BXUiUtil;
import nc.ui.erm.expamortize.model.ExpamorizeManageModel;
import nc.ui.org.ref.FinanceOrgDefaultRefTreeModel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.ValueChangedEvent;
import nc.vo.bd.period2.AccperiodmonthVO;
import nc.vo.org.util.OrgTypeManager;

import org.apache.commons.lang.StringUtils;
/**
 * 
 * @author wangled
 *
 */
public class ExpamortizeFinOrgPanel extends BDOrgPanel{
	private static final long serialVersionUID = 7956520045068494804L;
	private ExpamortizePeriodPanel topperiodpane=null;
	private UIRefPane refPane = null;
	
	public ExpamortizePeriodPanel getTopperiodpane() {
		return topperiodpane;
	}
	public void setTopperiodpane(ExpamortizePeriodPanel topperiodpane) {
		this.topperiodpane = topperiodpane;
	}
	
	public void initUI() {
		super.initUI();
		getRefPane().setPreferredSize(new Dimension(122,22));
		if(getRefPane().getRefPK() != null){
			getData(getRefPane().getRefPK());
		}
	}
	
	public void valueChanged(ValueChangedEvent event) {
		try {
			String pk_org = getRefPane().getRefPK();
			if (pk_org != null) {
				getData(pk_org);
				getDataManager().initModel();
			}
		} catch (Exception e) {
			exceptionHandler.handlerExeption(e);
		}
	}
	
	public void getData(String pk_org ){
		AccperiodmonthVO accperiodmonthVO;
		try {
			accperiodmonthVO = ErAccperiodUtil.getAccperiodmonthByUFDate(pk_org, BXUiUtil.getBusiDate());
			//需要设置一下会计期间方案，否则取默认的会计期间
			((AccPeriodDefaultRefModel)getTopperiodpane()
			        .getRefPane().getRefModel()).setDefaultpk_accperiodscheme(accperiodmonthVO.getPk_accperiodscheme());
			getTopperiodpane().getRefPane().setPK(accperiodmonthVO.getPk_accperiodmonth());
			((ExpamorizeManageModel)getModel()).setPeriod(accperiodmonthVO.getYearmth());
			getModel().getContext().setPk_org(pk_org);
		} catch (InvalidAccperiodExcetion e) {
			exceptionHandler.handlerExeption(e);
		}
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
                FinanceOrgDefaultRefTreeModel refModel = new FinanceOrgDefaultRefTreeModel()
                {
                    @Override
                    public String getTableName() {
                        return getOrgRefTableName();
                    }
                };
                refModel.setHiddenFieldCode(new String[] {"pk_financeorg", "pk_fatherorg", "pk_corp", "pk_group", "enablestate" , "pk_accperiodscheme"});
                refPane.setRefModel(refModel);
            }
            refPane.getRefModel().setPk_group(getModel().getContext().getPk_group());
            String[] orgpks = getModel().getContext().getPkorgs();
            refPane.getRefModel().setFilterPks(orgpks);
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
    
    /**
	 * 初始化默认组织
	 */
	protected void initDefaultOrg() {
		if (getModel().getContext().getPk_org() != null) {
			refPane.setPK(getModel().getContext().getPk_org());
		}
	}
}
