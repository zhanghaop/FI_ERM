package nc.ui.erm.billpub.btnstatus;

import java.util.Arrays;

import nc.ui.uif2.editor.BillForm;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.pub.pf.IPfRetCheckInfo;

public class BxApproveBtnStatusListener
{
    private BillManageModel model;
    private BillForm editor;
    
    public boolean approveButtonStatus() {
        // 审核按钮是否可用
        boolean isApproveBtnEnable = false;

        // 列表界面
        if (!getEditor().isShowing())
        {
            Object[] object = (Object[]) getModel().getSelectedOperaDatas();
            if (object == null)
            {
                return false;
            }
            JKBXVO[] vos = Arrays.asList(object).toArray(new JKBXVO[0]);
            //对于多选的话 的处理 
            Boolean[] isApproveBtnEnables = new Boolean[vos.length];
            for (int i = 0; i < vos.length; i++)
            {
                // 和卡片界面相同处理
                isApproveBtnEnable = isApproveBtnEnable(vos[i]);
                //保存是否取消审批按钮的数据
                isApproveBtnEnables[i] = isApproveBtnEnable;

            }
            for (int j = 0; j < isApproveBtnEnables.length; j++)
            {
                if (isApproveBtnEnables[j] == true)
                {
                    isApproveBtnEnable = true;
                    break;
                }
                else
                {
                    isApproveBtnEnable = false;
                }
            }
        } else {
                // 卡片界面
                JKBXVO vo= (JKBXVO) getModel().getSelectedData();
                isApproveBtnEnable = isApproveBtnEnable(vo) ;
        }
        
        return isApproveBtnEnable;
    }
    
    public boolean unApproveButtonStatus() {
        // 反审核按钮是否可用
        boolean isUnApproveBtnEnable = false;
        
        // 列表界面
        if (!getEditor().isShowing())
        {
            Object[] object = (Object[]) getModel().getSelectedOperaDatas();
            if (object == null)
            {
                return false;
            }
            JKBXVO[] vos = Arrays.asList(object).toArray(new JKBXVO[0]);
            //对于多选的话 的处理 
            Boolean[] isUnApproveBtnEnables = new Boolean[vos.length];
            for (int i = 0; i < vos.length; i++)
            {
                // 和卡片界面相同处理
                isUnApproveBtnEnable = isUnApproveBtnEnable(vos[i]);
                //保存是否取消审批按钮的数据
                isUnApproveBtnEnables[i] = isUnApproveBtnEnable;

            }
            for (int j = 0; j < isUnApproveBtnEnables.length; j++)
            {
                if (isUnApproveBtnEnables[j] == true)
                {
                    isUnApproveBtnEnable = true;
                    break;
                }
                else
                {
                    isUnApproveBtnEnable = false;
                }
            }
        } else {
                // 卡片界面
                JKBXVO vo= (JKBXVO) getModel().getSelectedData();
                isUnApproveBtnEnable = isUnApproveBtnEnable(vo);
        }
        
        return isUnApproveBtnEnable;
    }

    /**
     * 审批按钮是否可用
     * 
     * @param vo
     * @return
     */
	private boolean isApproveBtnEnable(JKBXVO vo) {
		if (vo == null) {
			return false;
		}
		if(vo.getParentVO().getDjzt().intValue() == BXStatusConst.DJZT_Invalid){
			return false;
		}
		// 审批不通过， 审核按钮不可用
		if (vo.getParentVO().getSpzt() != null && vo.getParentVO().getSpzt() == IPfRetCheckInfo.NOPASS) {
			return false;
		}
		// 单据已经审核通过了，审核按钮不可用
		if (vo.getParentVO().getDjzt() > BXStatusConst.DJZT_Saved
				|| vo.getParentVO().getDjzt() == BXStatusConst.DJZT_TempSaved) {
			return false;
		}

		if (vo.getParentVO().getSpzt() != null && (vo.getParentVO().getSpzt() == IPfRetCheckInfo.GOINGON
				|| vo.getParentVO().getSpzt() == IPfRetCheckInfo.COMMIT)) {
			return true;
		}
		return false;
	}
    /**
     * 反审核按钮是否可用
     * 
     * @param vo
     * @return
     */
    private boolean isUnApproveBtnEnable(JKBXVO vo) {
        
        if(vo==null){
            return false;
        }
        if(vo.getParentVO().getDjzt().intValue() == BXStatusConst.DJZT_Invalid){
        	return false;
        }

        //审批中、审批结束， 反审核按钮可用
		if (vo.getParentVO().getSpzt() != null
				&& (vo.getParentVO().getSpzt() == IPfRetCheckInfo.GOINGON 
				|| vo.getParentVO().getSpzt() == IPfRetCheckInfo.NOPASS)
				|| vo.getParentVO().getSpzt() == IPfRetCheckInfo.PASSING) {
			return true;
        }
        
//        //单据尚未审核，反审核不可用
//        if (vo.getParentVO().getDjzt() < BXStatusConst.DJZT_Verified) {
//            return false;
//        }
        
        // 单据已经审核通过了，反审核按钮可用
        if (vo.getParentVO().getDjzt() > BXStatusConst.DJZT_Saved ) {
            return true;
        }
        
        return true;
    }

    public BillManageModel getModel()
    {
        return model;
    }

    public void setModel(BillManageModel model)
    {
        this.model = model;
    }

    public BillForm getEditor()
    {
        return editor;
    }

    public void setEditor(BillForm editor)
    {
        this.editor = editor;
    }
}
