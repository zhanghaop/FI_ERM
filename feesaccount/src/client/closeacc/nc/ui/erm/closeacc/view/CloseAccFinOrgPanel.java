package nc.ui.erm.closeacc.view;

import java.awt.Dimension;
import java.util.Vector;

import nc.bd.accperiod.AccperiodParamAccessor;
import nc.bd.accperiod.InvalidAccperiodExcetion;
import nc.bs.logging.Logger;
import nc.desktop.ui.WorkbenchEnvironment;
import nc.pubitf.accperiod.AccountCalendar;
import nc.ui.bd.pub.BDOrgPanel;
import nc.ui.bd.ref.AbstractRefModel;
import nc.ui.bd.ref.model.AccperiodYearRefModel;
import nc.ui.erm.closeacc.model.CloseAccManageModel;
import nc.ui.org.closeaccbook.CloseAccModelServicer;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.ValueChangedEvent;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.uif2.model.AbstractUIAppModel;
import nc.vo.bd.period.AccperiodVO;
import nc.vo.fipub.utils.ArrayUtil;
import nc.vo.pub.lang.UFDate;

import org.apache.commons.collections.CollectionUtils;
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
	
	public void initUI() {
		super.initUI();
		getRefPane().setPreferredSize(new Dimension(250, 22));
		try {
            if (getRefPane().getRefPK() != null) {
                getData(getRefPane().getRefPK());
            }
        } catch (Throwable e) {
            ShowStatusBarMsgUtil.showStatusBarMsg(e.getMessage(),
                    getModel().getContext());
        }
	}

	public void valueChanged(ValueChangedEvent event) {
		try {
			String pk_org = getRefPane().getRefPK();
			if (pk_org != null) {
				getData(pk_org);
			}
		} catch (Exception e) {
			exceptionHandler.handlerExeption(e);
		}
	}

	public void getData(String pk_org) {
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
            refPane = new UIRefPane() {

                @Override
                public void showModel() {
                    // 初始化AccperiodAccessor的AccperiodVO缓存
                    // 初始化第一个单位的pk_accperiodscheme
                    AbstractRefModel refModel = getRefPane().getRefModel(); 
                    @SuppressWarnings("rawtypes")
                    Vector data = refModel.getData();
                    if (!CollectionUtils.isEmpty(data)) {
                        String pkFieldCode = refModel.getPkFieldCode();
                        Object[] pks = getRefPane().getRefModel().getValues(pkFieldCode, data);
                        if (!ArrayUtil.isArrayIsNull(pks)) {
                            AccountCalendar calendar = AccountCalendar.getInstanceByPk_org((String)pks[0]);
                            calendar.getYearVO(); //初始化AccperiodAccessor的AccperiodVO缓存
                        }
                    }
                    // end 初始化
                    super.showModel();
                }
                
            };
            refPane.setPreferredSize(new Dimension(200, 20));
            if (StringUtils.isNotBlank(getRefNodeName())) {
                refPane.setRefNodeName(getRefNodeName());
                refPane.getRefModel().setPk_group(
                        getModel().getContext().getPk_group());
                String[] orgpks = getModel().getContext().getFuncInfo()
                        .getFuncPermissionPkorgs();
                refPane.getRefModel().setFilterPks(orgpks);
            } else {
                refPane.setRefModel(getRefModel());
            }
            refPane.addValueChangedListener(this);
            refPane.setButtonFireEvent(true);
        }
        return refPane;
    }

    @Override
    protected void initDefaultOrg() {
        AbstractUIAppModel model = getModel();  
        if (model.getContext().getPk_org() != null) {
            getRefPane().setPK(model.getContext().getPk_org());
        } else {
            getData(null);
        }
    }

}
