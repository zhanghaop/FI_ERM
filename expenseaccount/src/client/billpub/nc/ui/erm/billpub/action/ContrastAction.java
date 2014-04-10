package nc.ui.erm.billpub.action;

import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import nc.bs.erm.util.action.ErmActionConst;
import nc.bs.logging.Logger;
import nc.ui.er.util.BXUiUtil;
import nc.ui.erm.billpub.model.ErmBillBillManageModel;
import nc.ui.erm.billpub.view.ContrastDialog;
import nc.ui.erm.billpub.view.ErmBillBillForm;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillModel;
import nc.ui.pub.bill.IBillItem;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.UIState;
import nc.ui.uif2.editor.BillForm;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BxUIControlUtil;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.BxcontrastVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.ep.bx.JKHeaderVO;
import nc.vo.erm.util.VOUtils;
import nc.vo.pub.BusinessException;
import nc.vo.pub.bill.BillTabVO;
import nc.vo.pub.lang.UFDouble;
/**
 * 
 * @author wangled
 * @version V6.3
 * @since V6.3 创建时间：2013-1-18 上午09:47:24
 */
public class ContrastAction extends NCAction {
	
	private static final long serialVersionUID = 1L;
	private BillManageModel model;
	private BillForm editor;
	private ContrastDialog contrastDialog;
	protected String qrySql = null;// 查询借款单的过滤条件
	
	public ContrastAction(){
		super();
		setCode(ErmActionConst.CONTRAST);
		setBtnName(ErmActionConst.getContrastBame());
	}
		
	@Override
	public void doAction(ActionEvent e) throws Exception {
		JKBXVO vo= ((ErmBillBillForm)editor).getHelper().getJKBXVO(editor);
		if (vo == null) {
			return;
		}
 
		ContrastDialog dialog = getContrastDialog(vo,getModel().getContext().getPk_org());

		dialog.showModal();

		if (dialog.getResult() == UIDialog.ID_OK) {

			List<BxcontrastVO> contrastsData = dialog.getContrastData();

			doContrastToUI(getEditor().getBillCardPanel(),vo, contrastsData, ((ErmBillBillForm)editor));
			
			((ErmBillBillForm)editor).setContrast(true);
		}
	}
	
	public JKHeaderVO[] getSelectedJkVos(JKBXVO vo) throws BusinessException {
		if(contrastDialog == null){
			return getContrastDialog(vo, getModel().getContext().getPk_org()).getSelectedJKVOs();
		}else{
			return contrastDialog.getSelectedJKVOs();
		}

	}
	
	/**
	 * @param card 报销单据卡片面板
	 * @param vo   报销VO
	 * @param contrastsData 冲销集合
	 * @throws BusinessException
	 */
	public static void doContrastToUI(BillCardPanel card,JKBXVO vo, List<BxcontrastVO> contrastsData,ErmBillBillForm editor) throws BusinessException {
		String[] headFields=new String[]{JKBXHeaderVO.CJKYBJE,JKBXHeaderVO.CJKBBJE,JKBXHeaderVO.HKYBJE,JKBXHeaderVO.HKBBJE
				,JKBXHeaderVO.GROUPCJKBBJE,JKBXHeaderVO.GLOBALCJKBBJE,JKBXHeaderVO.GROUPHKBBJE,JKBXHeaderVO.GLOBALHKBBJE};
		
		String[] changeHeadFields = new String[]{JKBXHeaderVO.ZFYBJE,JKBXHeaderVO.ZFBBJE,JKBXHeaderVO.GROUPZFBBJE,JKBXHeaderVO.GLOBALZFBBJE};
		
		BillModel billModel = card.getBillModel(BXConstans.CONST_PAGE);
		if(contrastsData==null || contrastsData.size()==0 ){
			//取消借款单的冲销
			for(String field:headFields){
				setHeadValue(card,field,null);
			}
			setHeadValue(card,JKBXHeaderVO.ZFYBJE, getHeadValue(card,JKBXHeaderVO.YBJE));
			setHeadValue(card,JKBXHeaderVO.ZFBBJE, getHeadValue(card,JKBXHeaderVO.BBJE));
			setHeadValue(card,JKBXHeaderVO.GROUPZFBBJE, getHeadValue(card,JKBXHeaderVO.GROUPBBJE));
			setHeadValue(card,JKBXHeaderVO.GLOBALZFBBJE, getHeadValue(card,JKBXHeaderVO.GLOBALBBJE));
			
			//需要将表体的所有业务页签都重新设置
			BillTabVO[] billTabVOs = card.getBillData().getBillTabVOs(IBillItem.BODY);
			for (BillTabVO billTabVO : billTabVOs) {
				String metaDataPath = billTabVO.getMetadatapath();
				if(!BXConstans.ER_BUSITEM.equals(metaDataPath) &&  metaDataPath != null){
					continue;
				}
				String tabcode = billTabVO.getTabcode();
				if(card.getBillModel(tabcode)!=null){
					int rowCount = card.getRowCount(tabcode);
					for(int i=0;i<rowCount;i++){
						for(String field:headFields){
							card.setBodyValueAt(UFDouble.ZERO_DBL, i, field,tabcode);
						}
						card.setBodyValueAt(card.getBillModel(tabcode).getValueAt(i,JKBXHeaderVO.YBJE), i, JKBXHeaderVO.ZFYBJE,tabcode);
						card.setBodyValueAt(card.getBillModel(tabcode).getValueAt(i,JKBXHeaderVO.BBJE), i, JKBXHeaderVO.ZFBBJE,tabcode);
						card.setBodyValueAt(card.getBillModel(tabcode).getValueAt(i,JKBXHeaderVO.GROUPBBJE), i, JKBXHeaderVO.GROUPZFBBJE,tabcode);
						card.setBodyValueAt(card.getBillModel(tabcode).getValueAt(i,JKBXHeaderVO.GLOBALBBJE), i, JKBXHeaderVO.GLOBALZFBBJE,tabcode);
						// 用于报销单冲借款保存后，再修改去除掉冲借款信息时，业务页签的信息传不到后台，猜测原因这块没有更改行状态
						if(card.getBillModel(tabcode).getRowState(i)!=BillModel.ADD)
						{
							//不是新增行状态时，才设置为修改
							card.getBillModel(tabcode).setRowState(i, BillModel.MODIFICATION);
						}
							
					}
				}
			}		
			//删除冲销页签中的
			if(billModel!=null){
				billModel.clearBodyData();
				if(BXConstans.BILLTYPECODE_RETURNBILL.equals(card.getHeadItem(JKBXHeaderVO.DJLXBM).getValueObject().toString())){
					card.getBillModel(BXConstans.BUS_PAGE).clearBodyData();
				}
			}
		} else {
			new BxUIControlUtil().doContrast(vo, contrastsData);
			vo.setContrastVO(contrastsData.toArray(new BxcontrastVO[] {}));

			setValueToEditor(card, vo, contrastsData, headFields, changeHeadFields, billModel);
		}
	}

	/**
	 * 设置主表，业务行，冲销行；业务行，冲销行包含新增，修改和删除的行
	 * 
	 * @param card
	 * @param vo
	 * @param contrastsData
	 * @param headFields
	 * @param changeHeadFields
	 * @param billModel
	 * @throws BusinessException
	 * @author: wangyhh@ufida.com.cn
	 */
	private static void setValueToEditor(BillCardPanel card, JKBXVO vo, List<BxcontrastVO> contrastsData, String[] headFields, String[] changeHeadFields, BillModel billModel) throws BusinessException {
		// 设置主表，业务行，冲销行；业务行，冲销行包含新增，修改和删除的行
		// 主表
		for (String key : headFields) {
			setHeadValue(card, key, vo.getParentVO().getAttributeValue(key));
		}
		for (String key : changeHeadFields) {
			setHeadValue(card, key, vo.getParentVO().getAttributeValue(key));
		}
		
		// 分页签设置业务行
		Map<String, List<BXBusItemVO>> tableCode2VOMap = VOUtils.changeCollection2MapList(Arrays.asList( vo.getChildrenVO()), new String[]{BXBusItemVO.TABLECODE});
		if(tableCode2VOMap!=null){
			BillTabVO[] billTabVOs = card.getBillData().getBillTabVOs(IBillItem.BODY);
			for (BillTabVO billTabVO : billTabVOs) {
				String metaDataPath = billTabVO.getMetadatapath();
				if(!BXConstans.ER_BUSITEM.equals(metaDataPath) &&  metaDataPath != null){
					continue;
				}
				List<BXBusItemVO> list = tableCode2VOMap.get(billTabVO.getTabcode());
				if(list != null){
					for (int i = 0; i < list.size(); i++) {
						for (String field : headFields) {
							card.setBodyValueAt(list.get(i).getAttributeValue(field), i, field, billTabVO.getTabcode());
						}
						for (String field : changeHeadFields) {
							card.setBodyValueAt(list.get(i).getAttributeValue(field), i, field, billTabVO.getTabcode());
						}
						
						BillModel billModel2 = card.getBillModel(billTabVO.getTabcode());
						int rowState = billModel2.getRowState(i);
						if(BillModel.ADD != rowState && BillModel.DELETE != rowState){
							billModel2.setRowState(i, BillModel.MODIFICATION);
						}
					}
				}
			}
		}
		
		billModel.setBodyDataVO(contrastsData.toArray(new BxcontrastVO[0]));
		billModel.loadLoadRelationItemValue();
		
		try {
			BXUiUtil.resetDecimal(card,vo.getParentVO().getPk_org(),vo.getParentVO().getBzbm());
		} catch (Exception e) {
			Logger.error(e);
			throw new BusinessException(e);
		}
	}
	
	/**
	 * 
	 * 初始化冲借款对话框
	 */
	public ContrastDialog getContrastDialog(JKBXVO vo, String pk_corp) throws BusinessException {
		if (contrastDialog == null) {
			contrastDialog = new ContrastDialog(getEditor(), BXConstans.BXMNG_NODECODE, pk_corp, vo);
		}
		BxcontrastVO[] oldcontrastvos = null;
		if(getModel().getUiState() == UIState.EDIT){
			JKBXVO selectedData = (JKBXVO) getModel().getSelectedData();
			if(selectedData != null){
				oldcontrastvos = selectedData.getContrastVO();
			}
		}
		contrastDialog.initData(vo,oldcontrastvos,qrySql);
		return contrastDialog;
	}
	
	protected static Object getHeadValue(BillCardPanel card, String key) {
		return card.getHeadItem(key).getValueObject();
	}
	
	protected static void setHeadValue(BillCardPanel card, String key, Object value) {
		if (card.getHeadItem(key) != null) {
			card.getHeadItem(key).setValue(value);
		}
	}
	
    @Override
    protected boolean isActionEnable()
    {
        
        if (BXConstans.JK_DJDL.equals(((ErmBillBillManageModel)getModel()).getCurrentDjLXVO().getDjdl()))
        {
            return false;
        }
        return true;
    }
	public BillManageModel getModel() {
		return model;
	}
	public void setModel(BillManageModel model) {
		this.model = model;
		this.model.addAppEventListener(this);
	}
	public BillForm getEditor() {
		return editor;
	}
	public void setEditor(BillForm editor) {
		this.editor = editor;
	}
	
	
}
