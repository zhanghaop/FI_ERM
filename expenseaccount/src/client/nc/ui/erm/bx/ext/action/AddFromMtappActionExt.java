package nc.ui.erm.bx.ext.action;

import java.awt.event.ActionEvent;

import nc.bs.erm.ext.common.ErmConstExt;
import nc.ui.erm.billpub.action.AddFromMtappAction;
import nc.ui.erm.billpub.model.ErmBillBillManageModel;
import nc.ui.erm.billpub.view.MatterSourceRefDlg;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.erm.matterapp.MatterAppConvResVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;

/**
 * ����������Ӧ����չ
 * 
 * ����Ԫר��
 * 
 * @author lvhj
 *
 */
public class AddFromMtappActionExt  extends AddFromMtappAction{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private MatterSourceRefDlgExt maSourceDlgExt;
	
	@Override
	public void doAction(ActionEvent e) throws Exception {
		// �����̵渶����������������
		String selectBillTypeCode = ((ErmBillBillManageModel) getModel()).getSelectBillTypeCode();
		if(ErmConstExt.Distributor_BX_Tradetype.equals(selectBillTypeCode)){
			throw new BusinessException("�����̵渶��������֧�ֲ��շ������뵥����");
		}
		// ������������
		super.doAction(e);
	}
	
	/* (non-Javadoc)
	 * @see nc.ui.erm.billpub.action.AddFromMtappAction#getMaSourceDlg()
	 */
	protected MatterSourceRefDlg getMaSourceDlg() {
		// �������ڣ��滻Ϊ�ӱ�ѡ��Ĵ���
		if(maSourceDlgExt == null){
			maSourceDlgExt = new MatterSourceRefDlgExt(getModel().getContext());
		}
		return maSourceDlgExt;
	}
	
	protected MatterAppConvResVO convertAggMattappVO(String pk_org, AggMatterAppVO retvo) throws BusinessException {

		MatterAppConvResVO convertBusiVO = super.convertAggMattappVO(pk_org,retvo);
		// ������ת����̯ҳǩ
		JKBXVO jkbxvo = (JKBXVO) convertBusiVO.getBusiobj();
		JKBXHeaderVO parentVO = jkbxvo.getParentVO();
		parentVO.setIscostshare(UFBoolean.FALSE);
		jkbxvo.setcShareDetailVo(null);
		
		return convertBusiVO;
	}
	
}
