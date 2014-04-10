package nc.ui.erm.billpub.model;

import java.util.HashMap;
import java.util.Map;

import nc.itf.arap.fieldmap.IBillFieldGet;
import nc.ui.arap.bx.print.ERMPrintDigitUtil;
import nc.ui.bd.pub.actions.print.MetaDataSingleSelectDataSource;
import nc.ui.pub.print.IExDataSource;
import nc.ui.uif2.editor.BillForm;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.BxcontrastVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.ep.bx.Traffictool;
import nc.vo.pub.BusinessException;

public class ErmDataSingleSelectDataSource extends MetaDataSingleSelectDataSource implements IExDataSource {

	private static final long serialVersionUID = 1L;
	private BillForm editor = null;
	
	private JKBXVO jkbxvo = null;

	
	@Override
	public Object[] getMDObjects() {
		JKBXVO old = (JKBXVO) getModel().getSelectedData();
		jkbxvo = (JKBXVO) old.clone();
		
		String nodecode = getModel().getContext().getNodeCode();
		BXBusItemVO[] busvos = jkbxvo.getBxBusItemVOS();
		String vehicle = null; // ��ͨ����
		if(busvos!=null && busvos.length!=0){
			for (BXBusItemVO busvo : busvos) {
				if (BXConstans.BXCLFJK_CODE.equals(nodecode)) {// ���÷ѽ�ʱ��defitem4�ǽ�ͨ�����ֶ�
					vehicle = (String) busvo.getDefitem4();
					if (vehicle != null) {
						busvo.setDefitem4(Traffictool.valueOf(Traffictool.class, vehicle).getName());
					}
				} else if (BXConstans.BXTEA_CODE.equals(nodecode) || BXConstans.BXCLFBX_CODE.equals(nodecode)) {// ���÷ѱ�����/��ͨ�ѱ�������defitem5�ǽ�ͨ�����ֶ�
					vehicle = (String) busvo.getDefitem5();
					if (vehicle != null) {
						busvo.setDefitem5(Traffictool.valueOf(Traffictool.class, vehicle).getName());
					}
				}
				
			}
		}
		//������
		Map<String, String[]> fieldsMap = new HashMap<String, String[]>();
		fieldsMap.put(ERMPrintDigitUtil.ErmPrintDigitConst.FIELD_MONEY,new String[] {JKBXHeaderVO.TOTAL,
				JKBXHeaderVO.YBJE,JKBXHeaderVO.HKYBJE,BXBusItemVO.AMOUNT,BxcontrastVO.YBJE,
				BxcontrastVO.CJKYBJE});
		Object[] newobj =new Object[0];
		try {
			newobj= ERMPrintDigitUtil.getDatas(new JKBXVO[]{jkbxvo}, fieldsMap, IBillFieldGet.PK_ORG, JKBXHeaderVO.BZBM);
		} catch (NumberFormatException e) {
			nc.bs.logging.Log.getInstance(this.getClass()).error(e);
		} catch (IllegalArgumentException e) {
			nc.bs.logging.Log.getInstance(this.getClass()).error(e);
		} catch (IllegalAccessException e) {
			nc.bs.logging.Log.getInstance(this.getClass()).error(e);
		} catch (BusinessException e) {
			nc.bs.logging.Log.getInstance(this.getClass()).error(e);
		}
		
		return  newobj ;
	}

	public BillForm getEditor() {
		return editor;
	}

	public void setEditor(BillForm editor) {
		this.editor = editor;
	}
	
	/**
	 * ���ͱ�������ӡ����������ģ���Զ�������Ϊ���գ�����Ԫ���ݱ����ǲ���ʱ��
	 * ��Ҫ����Ĵ���ʽ����������������ӡ������ֵ��
	 */
	@Override
	public Object[] getObjectByExpress(String itemExpress) {
		if(jkbxvo.getChildrenVO() == null || jkbxvo.getChildrenVO().length == 0){
			return null;
		}
		
		if(itemExpress!=null && (itemExpress.startsWith("er_busitem.defitem")||itemExpress.startsWith("jk_busitem.defitem"))) {
			String defitem = itemExpress.split("[.]")[1];
            int rowCount = jkbxvo.getChildrenVO().length;
            Object[] values = new Object[rowCount];
            for(int i=0;i<rowCount;i++){
                Object assid = jkbxvo.getChildrenVO()[i].getAttributeValue(defitem);
                if(assid == null){
                    assid = "";
                }
                values[i] = (Object) assid;
            }
            return values;
        }
		return null;
	}

	@Override
	public int getObjectTypeByExpress(String itemExpress) {
		return IExDataSource.IMAGE_TYPE;
	}
	
}
