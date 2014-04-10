package nc.ui.arap.bx.actions;

import java.util.List;

import nc.ui.arap.bx.BXBillCardPanel;
import nc.ui.arap.bx.ContrastDialog;
import nc.ui.pub.beans.UIDialog;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BxUIControlUtil;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.BxcontrastVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.ValidationException;
import nc.vo.pub.lang.UFDouble;

/**
 * @author twei
 *
 * ����Action
 *
 * nc.ui.arap.bx.actions.ContrastAction
 */
public class ContrastAction extends BXDefaultAction {

	/**
	 * @throws BusinessException
	 *
	 * 1. ��ʼ������Ի���
	 * 2. ȡ��ѡ����Ϣ,�ڽ����ϼ�¼����Ϣ��ˢ�½���
	 */
	public void contrast() throws BusinessException {

		JKBXVO vo=null;

		vo = getBillValueVO();

		if (vo == null) {
			return;
		}

		if(vo.getChildrenVO()==null){

		}
		if(vo.getParentVO().getPrimaryKey()!=null){
			String primaryKey = vo.getParentVO().getPrimaryKey();
			if(getVoCache().getVOByPk(primaryKey)!=null){
				vo.setContrastVO(getVoCache().getVOByPk(primaryKey).getContrastVO());
			}
		}

//modified by chendya �ݲ�У��
//		UFDate djrq = vo.getParentVO().getDjrq();
//		if(djrq.afterDate(BXUiUtil.getSysdate())){
//			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000410")/*@res "�������ĵ������ڲ������ڵ�ǰ��������!"*/);
//		}
//--end		

		ContrastDialog dialog = getMainPanel().getContrastDialog(vo,getPk_org());

		dialog.showModal();

		if (dialog.getResult() == UIDialog.ID_OK) {

			List<BxcontrastVO> contrastsData = dialog.getContrastData();

			doContrastToUI(getBxBillCardPanel(),vo, contrastsData, true);
			
			//�������£����ݱ�ͷ��Ϣ����ҵ���� add by chenshuai
			if(BXConstans.BILLTYPECODE_RETURNBILL.equals(vo.getParentVO().getDjlxbm())){
				doHkBusitem(vo);
			}
		}
	}
	
	//
	private void doHkBusitem(JKBXVO vo1) throws ValidationException {
		JKBXVO vo = getBillValueVO();
		
		if(vo == null || vo.getParentVO() == null){
			return;
		}
		
		final JKBXHeaderVO headVo = vo.getParentVO();
		final BXBusItemVO[] busitemVos = vo.getChildrenVO();
		
		BXBusItemVO tempBusitemVo = null;
		
		if(busitemVos != null && busitemVos.length > 0){
			tempBusitemVo = busitemVos[0];
		}else{
			tempBusitemVo = new BXBusItemVO();
		}
		
		if(headVo.getCjkybje().equals(new UFDouble(0))){
			vo.setChildrenVO(null);
			getBxBillCardPanel().setBillValueVO(vo);
			return;
		}
		
		tempBusitemVo.setTablecode(BXConstans.BUS_PAGE);
		tempBusitemVo.setYbje(headVo.getYbje());
		tempBusitemVo.setBbje(headVo.getBbje());
		tempBusitemVo.setHkybje(headVo.getHkybje());
		tempBusitemVo.setHkbbje(headVo.getHkbbje());
		tempBusitemVo.setZfybje(headVo.getZfybje());
		tempBusitemVo.setZfbbje(headVo.getZfbbje());
		tempBusitemVo.setCjkybje(headVo.getCjkybje());
		tempBusitemVo.setCjkbbje(headVo.getCjkbbje());
		tempBusitemVo.setGroupbbye(headVo.getGroupbbje());//����
		tempBusitemVo.setGrouphkbbje(headVo.getGrouphkbbje());
		tempBusitemVo.setGroupzfbbje(headVo.getGroupzfbbje());
		tempBusitemVo.setGroupcjkbbje(headVo.getGroupcjkbbje());
		tempBusitemVo.setGlobalbbje(headVo.getGlobalbbje());//ȫ��
		tempBusitemVo.setGlobalhkbbje(headVo.getGlobalhkbbje());
		tempBusitemVo.setGlobalzfbbje(headVo.getGlobalzfbbje());
		tempBusitemVo.setGlobalcjkbbje(headVo.getGlobalcjkbbje());
		
		vo.setChildrenVO(new BXBusItemVO[]{tempBusitemVo});
		
		getBxBillCardPanel().setBillValueVO(vo);
	}
	
	/**
	 * @param card �������ݿ�Ƭ���
	 * @param vo   ����VO
	 * @param contrastsData ��������
	 * @param isChange �Ƿ��г������� ���ڵ��������ť��ȷ��Ϊtrue�������ط���ֵ����ȡBxBillCardPanel �е�isContrast()����ֵ��
	 * @throws BusinessException
	 */
	public static void doContrastToUI(BXBillCardPanel card,JKBXVO vo, List<BxcontrastVO> contrastsData, boolean isChange) throws BusinessException {
		if(contrastsData==null || contrastsData.size()==0 ){
			//ȡ�����ĳ���
			String[] clearFields=new String[]{JKBXHeaderVO.CJKYBJE,JKBXHeaderVO.CJKBBJE,JKBXHeaderVO.HKYBJE,JKBXHeaderVO.HKBBJE
					,JKBXHeaderVO.GROUPCJKBBJE,JKBXHeaderVO.GLOBALCJKBBJE,JKBXHeaderVO.GROUPHKBBJE,JKBXHeaderVO.GLOBALHKBBJE};
			for(String field:clearFields){
				setHeadValue(card,field,null);
			}
			setHeadValue(card,JKBXHeaderVO.ZFYBJE, getHeadValue(card,JKBXHeaderVO.YBJE));
			setHeadValue(card,JKBXHeaderVO.ZFBBJE, getHeadValue(card,JKBXHeaderVO.BBJE));
			setHeadValue(card,JKBXHeaderVO.GROUPZFBBJE, getHeadValue(card,JKBXHeaderVO.GROUPBBJE));
			setHeadValue(card,JKBXHeaderVO.GLOBALZFBBJE, getHeadValue(card,JKBXHeaderVO.GLOBALBBJE));
			
			if(card.getBillModel(BXConstans.BUS_PAGE)!=null){
				int rowCount = card.getRowCount(BXConstans.BUS_PAGE);
				for(int i=0;i<rowCount;i++){
					for(String field:clearFields){
						card.setBodyValueAt(null, i, field,BXConstans.BUS_PAGE);
					}
					card.setBodyValueAt(card.getBillModel(BXConstans.BUS_PAGE).getValueAt(i,JKBXHeaderVO.YBJE), i, JKBXHeaderVO.ZFYBJE,BXConstans.BUS_PAGE);
					card.setBodyValueAt(card.getBillModel(BXConstans.BUS_PAGE).getValueAt(i,JKBXHeaderVO.BBJE), i, JKBXHeaderVO.ZFBBJE,BXConstans.BUS_PAGE);
					card.setBodyValueAt(card.getBillModel(BXConstans.BUS_PAGE).getValueAt(i,JKBXHeaderVO.GROUPBBJE), i, JKBXHeaderVO.GROUPZFBBJE,BXConstans.BUS_PAGE);
					card.setBodyValueAt(card.getBillModel(BXConstans.BUS_PAGE).getValueAt(i,JKBXHeaderVO.GLOBALBBJE), i, JKBXHeaderVO.GLOBALZFBBJE,BXConstans.BUS_PAGE);
				}
			}
			
			//ɾ������ҳǩ�е�
			if(card.getBillModel(BXConstans.CONST_PAGE)!=null){
				int rowCount = card.getRowCount(BXConstans.CONST_PAGE);
				
				int[] rows = new int[rowCount];
				for(int i=0;i<rowCount;i++){
					rows[i] = i;
				}
				card.getBillModel(BXConstans.CONST_PAGE).delLine(rows);
			}
		}else{
			new BxUIControlUtil().doContrast(vo, contrastsData);
			
			//add by chenshuai ���ó���VO
			vo.setContrastVO(contrastsData.toArray(new BxcontrastVO[]{}));
			card.setBillValueVO(vo);
		}
		
		card.setContrast(isChange);
		card.setContrasts(contrastsData);
	}
}