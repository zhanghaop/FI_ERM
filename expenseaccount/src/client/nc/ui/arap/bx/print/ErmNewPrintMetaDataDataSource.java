package nc.ui.arap.bx.print;

import java.util.ArrayList;
import java.util.List;

import nc.ui.pub.print.IMetaDataDataSource;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;

/**
 * ��������ӡģ��Ԫ����������Դ(�����ݲ�ʹ�ã��������ݴ�ӡ��ҳǩʱ��Ҫ�Զ���ȡֵ����)
 * @deprecated
 * @author chendya
 * 
 */
@Deprecated
public class ErmNewPrintMetaDataDataSource implements IMetaDataDataSource {

	/**
	 * �����BX����VO
	 */
	private JKBXVO[] vos = null;
	
	/**
	 * �Ƿ��ӡ�б�
	 */
	private final boolean isPrintList;

	public ErmNewPrintMetaDataDataSource(JKBXVO[] vos,boolean isPrintList) {
		this.vos = vos;
		this.isPrintList = isPrintList;
	}

	protected JKBXVO[] getVos() {
		return vos;
	}

	@Override
	public Object[] getMDObjects() {
		if(isPrintList){
			List<JKBXHeaderVO> listHeadVO = new ArrayList<JKBXHeaderVO>();
			JKBXVO[] selBxvos = getVos();
			for (int i = 0; i < selBxvos.length; i++) {
				listHeadVO.add(selBxvos[i].getParentVO());
			}
			return listHeadVO.toArray(new JKBXHeaderVO[0]);
		}
		return getVos();
	}

	@Override
	public String[] getAllDataItemExpress() {
		return null;
	}

	@Override
	public String[] getAllDataItemNames() {

		return null;
	}

	@Override
	public String[] getDependentItemExpressByExpress(String arg0) {

		return new String[]{"ttt","ttt2"};
	}

	@Override
	public String[] getItemValuesByExpress(String arg0) {

		return null;
	}

	@Override
	public String getModuleName() {

		return null;
	}

	@Override
	public boolean isNumber(String arg0) {

		return false;
	}
}
