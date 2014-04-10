package nc.ui.erm.closeacc.model;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.pubitf.erm.closeacc.IErmCloseAccBookQryService;
import nc.ui.erm.closeacc.view.CloseaccDesInfoPanel;
import nc.ui.uif2.IExceptionHandler;
import nc.ui.uif2.model.IAppModelDataManager;
import nc.ui.uif2.model.IQueryAndRefreshManager;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.org.CloseAccBookVO;
import nc.vo.pub.BusinessException;

/**
 *
 * @author wangled
 *
 */
public class CloseAccDataManager implements IQueryAndRefreshManager,
        IAppModelDataManager {
    private IExceptionHandler exceptionHandler;
    private CloseAccManageModel model;
    private CloseaccDesInfoPanel rightView;

    @Override
    public void initModelBySqlWhere(String sqlWhere) {

    }

    @Override
    public void refresh() {
        initData(getModel());
        // 同时刷新提示面板
        setViewDesinfo(getModel().getPk_org());
    }

    private void initData(CloseAccManageModel model) {
        CloseAccBookVO[] vos = null;
        try {
            vos = getService().queryCloseAccBookVoByPk(
                    getModel().getPk_accperiod(),
                    getModel().getPk_org());
        } catch (BusinessException e) {
            getExceptionHandler().handlerExeption(e);
        }
        getModel().initModel(vos);
    }

    @Override
    public void initModel() {
        String pk_org = getModel().getContext().getPk_org();
        String pkAccperiod = ((CloseAccManageModel) getModel()).getPk_accperiod();
        if (!StringUtil.isEmpty(pk_org)) {
            // 初始化提示框
            // 查询最小未结账和最小已结账信息
            setViewDesinfo(pk_org);
            // 初始化列表
            if (!StringUtil.isEmpty(pkAccperiod)) {
                try {
                    getModel().setPk_accperiod(pkAccperiod);
                    getModel().setModuleId(BXConstans.ERM_MODULEID);
                    getModel().setPk_org(pk_org);
                    CloseAccBookVO[] vos = getService().queryCloseAccBookVoByPk(pkAccperiod, pk_org);
                    getModel().initModel(vos);
                } catch (BusinessException e) {
                    getExceptionHandler().handlerExeption(e);
                }
            }
        }
    }

    private static final String initVal = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0004")/*@res "无"*/;

    private void setViewDesinfo(String pk_org) {
        Map<String, List<String>> maxMinMap;
        try {
            maxMinMap = getErmCloseAccBookQryService().getMaxEndedAndMinNotEndedAccByOrg(new String[] { pk_org });
            List<String> maxMinlist = maxMinMap.get(pk_org);
            String minPeriod = initVal;
            String maxPeriod = initVal;
            if (StringUtils.isNotEmpty(maxMinlist.get(0)))
                maxPeriod = maxMinlist.get(0);
            if (StringUtils.isNotEmpty(maxMinlist.get(1)))
                minPeriod = maxMinlist.get(1);
            getModel().setMinNotAcc(minPeriod);
            getModel().setMaxAcc(maxPeriod);
            getRightView().setDesinfo(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0005")/*@res "可结账期间:"*/ + minPeriod + nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0006")/*@res "\n可取消结账期间:"*/ + maxPeriod);
        } catch (BusinessException e) {
            Logger.error(e.getMessage(), e);
            return;
        }
    }

    public IErmCloseAccBookQryService getService() {
        return NCLocator.getInstance().lookup(IErmCloseAccBookQryService.class);
    }

  private IErmCloseAccBookQryService getErmCloseAccBookQryService() {
      return NCLocator.getInstance().lookup(IErmCloseAccBookQryService.class);
  }

    public CloseaccDesInfoPanel getRightView() {
        return rightView;
    }

    public void setRightView(CloseaccDesInfoPanel rightView) {
        this.rightView = rightView;
    }

    public IExceptionHandler getExceptionHandler() {
        return exceptionHandler;
    }

    public void setExceptionHandler(IExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    public CloseAccManageModel getModel() {
        return model;
    }

    public void setModel(CloseAccManageModel model) {
        this.model = model;
    }

}