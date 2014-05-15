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
        // ��˰�ť�Ƿ����
        boolean isApproveBtnEnable = false;

        // �б����
        if (!getEditor().isShowing())
        {
            Object[] object = (Object[]) getModel().getSelectedOperaDatas();
            if (object == null)
            {
                return false;
            }
            JKBXVO[] vos = Arrays.asList(object).toArray(new JKBXVO[0]);
            //���ڶ�ѡ�Ļ� �Ĵ��� 
            Boolean[] isApproveBtnEnables = new Boolean[vos.length];
            for (int i = 0; i < vos.length; i++)
            {
                // �Ϳ�Ƭ������ͬ����
                isApproveBtnEnable = isApproveBtnEnable(vos[i]);
                //�����Ƿ�ȡ��������ť������
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
                // ��Ƭ����
                JKBXVO vo= (JKBXVO) getModel().getSelectedData();
                isApproveBtnEnable = isApproveBtnEnable(vo) ;
        }
        
        return isApproveBtnEnable;
    }
    
    public boolean unApproveButtonStatus() {
        // ����˰�ť�Ƿ����
        boolean isUnApproveBtnEnable = false;
        
        // �б����
        if (!getEditor().isShowing())
        {
            Object[] object = (Object[]) getModel().getSelectedOperaDatas();
            if (object == null)
            {
                return false;
            }
            JKBXVO[] vos = Arrays.asList(object).toArray(new JKBXVO[0]);
            //���ڶ�ѡ�Ļ� �Ĵ��� 
            Boolean[] isUnApproveBtnEnables = new Boolean[vos.length];
            for (int i = 0; i < vos.length; i++)
            {
                // �Ϳ�Ƭ������ͬ����
                isUnApproveBtnEnable = isUnApproveBtnEnable(vos[i]);
                //�����Ƿ�ȡ��������ť������
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
                // ��Ƭ����
                JKBXVO vo= (JKBXVO) getModel().getSelectedData();
                isUnApproveBtnEnable = isUnApproveBtnEnable(vo);
        }
        
        return isUnApproveBtnEnable;
    }

    /**
     * ������ť�Ƿ����
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
		// ������ͨ���� ��˰�ť������
		if (vo.getParentVO().getSpzt() != null && vo.getParentVO().getSpzt() == IPfRetCheckInfo.NOPASS) {
			return false;
		}
		// �����Ѿ����ͨ���ˣ���˰�ť������
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
     * ����˰�ť�Ƿ����
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

        //�����У� ����˰�ť����
		if (vo.getParentVO().getSpzt() != null
				&& (vo.getParentVO().getSpzt() == IPfRetCheckInfo.GOINGON || vo
						.getParentVO().getSpzt() == IPfRetCheckInfo.NOPASS)) {
			return true;
        }
        
        //������δ��ˣ�����˲�����
        if (vo.getParentVO().getDjzt() < BXStatusConst.DJZT_Verified) {
            return false;
        }
        
        // �����Ѿ����ͨ���ˣ�����˰�ť����
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
