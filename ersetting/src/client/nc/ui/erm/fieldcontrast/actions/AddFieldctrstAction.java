package nc.ui.erm.fieldcontrast.actions;

import java.awt.event.ActionEvent;

import nc.bs.erm.cache.ErmBillFieldContrastCache;
import nc.bs.erm.common.ErmBillConst;
import nc.ui.erm.fieldcontrast.view.FieldBilRefPanel;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.uif2.actions.batch.BatchAddLineAction;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.erm.fieldcontrast.FieldcontrastVO;

/**
 * �ֶζ���������ť
 * @author luolch
 *
 */
public class AddFieldctrstAction extends BatchAddLineAction {

	private static final long serialVersionUID = 1L;
	private FieldBilRefPanel refPanel;

	@Override
	protected void setDefaultData(Object obj) {
		if (obj != null) {
			FieldcontrastVO vo = (FieldcontrastVO) obj;
			Integer app_scene = ErmBillFieldContrastCache.FieldContrast_SCENE_MatterAppCtrlField;
			if(FieldBilRefPanel.SHARESTATE == refPanel.getBcombobox().getSelectedIndex()) {
				app_scene = ErmBillFieldContrastCache.FieldContrast_SCENE_SHARERULEField;
				vo.setDes_billtype("~");
				vo.setApp_scene(app_scene);
				Object refValue = getRefPanel().getDjlxRef().getValueObj();
				if(!((String[])refValue)[0].contains(ErmBillConst.MatterApp_PREFIX)){
					vo.setSrc_busitype(BXConstans.CSHARE_PAGE);
				}else{
					vo.setSrc_busitype(ErmBillConst.MatterApp_MDCODE_DETAIL);
				}
			}
			vo.setPk_org(getModel().getContext().getPk_org());
			vo.setPk_group(getModel().getContext().getPk_group());
		}
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {
		if(valiDataAdd()){
			super.doAction(e);
		}
	}
	/**
	 * ����ǰУ��
	 * @return
	 */
	private boolean valiDataAdd()  {
		getRefPanel().getDjlxRef().stopEditing();
		Object refValue = getRefPanel().getDjlxRef().getValueObj();
		if (refValue == null) {
			ShowStatusBarMsgUtil.showErrorMsg( nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201100_0","0201100-0005")/*@res "��ʾ"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201100_0","0201100-0006")/*@res "����ѡ����ƶ���"*/,getModel().getContext());
			return false;
		}
		return true;
	}

	public FieldBilRefPanel getRefPanel() {
		return refPanel;
	}

	public void setRefPanel(FieldBilRefPanel refPanel) {
		this.refPanel = refPanel;
	}

}