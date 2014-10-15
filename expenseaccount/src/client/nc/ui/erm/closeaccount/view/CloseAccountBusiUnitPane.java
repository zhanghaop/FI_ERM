package nc.ui.erm.closeaccount.view;

import nc.bs.uif2.BusinessExceptionAdapter;
import nc.ui.org.closeaccbook.OrgCloseManageModel;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.pub.BusinessException;
import nc.vo.sm.funcreg.ModuleVO;

/**
 * 业务单元参照面板
 *
 *
 */
@SuppressWarnings("serial")
public class CloseAccountBusiUnitPane extends nc.ui.org.closeaccbook.CloseAccBookBusiUnitPane {
    
//    private static Object objSync = new Object();
    
	@Override
    public void init() {
        super.init();
//        String pk_user = getModel().getContext().getPk_loginUser();
//        String pk_group = getModel().getContext().getPk_group();
//        try {
//            String pkOrg = OrgSettingAccessor.getDefaultOrgUnit(pk_user, pk_group);
//            getRefPane().setPK(pkOrg);
//            getRefPane().setPreferredSize(new Dimension(250, 22));
//            if (!StringUtil.isEmpty(pkOrg)) {
//                Thread thread = new Thread() {
//
//                    @Override
//                    public void run() {
//                        try {
//                            synchronized(objSync) {
//                                objSync.wait(200);
//                            };
//                        } catch (InterruptedException e) {
//                            Logger.error(e.getMessage(), e);
//                        }
//                        setCenterUI();
//                    }
//                    
//                };
//                thread.start();
//            }
//        } catch (Exception e) {
//            Logger.error(e.getMessage(), e);
//        }
    }

    // 刷新按钮也要调用
	public void setCenterUI() {
		try {
			String pk_org = getRefPane().getRefPK();
			((OrgCloseManageModel) getModel()).setPk_org(pk_org);
			ModuleVO[] moduleVos = new ModuleVO[0];
			if (pk_org != null)
				moduleVos = getClientServicer().queryCloseAccModuleVOs(pk_org);

			moduleVos = filter(moduleVos);

			if (moduleVos != null){
				getCenterPane().resetUI(moduleVos);
			}

		} catch (BusinessException e) {
			throw new BusinessExceptionAdapter(e);
		}
	}

	/*
	 * 只保留报销模块
	 */
	private ModuleVO[] filter(ModuleVO[] moduleVos) {
		for (int i = 0; i < moduleVos.length; i++) {
			if (BXConstans.ERM_MODULEID.equals(moduleVos[i].getModuleid())) {
				return new ModuleVO[] { moduleVos[i] };
			}
		}
		return null;
	}

}