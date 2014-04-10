package nc.ui.arap.bx.print;

import java.util.ArrayList;
import java.util.List;

import nc.ui.pub.print.IMetaDataDataSource;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;

/**
 * 报销单打印模版元数据数据来源(此类暂不使用，报销单据打印多页签时需要自定义取值规则)
 * @deprecated
 * @author chendya
 * 
 */
@Deprecated
public class ErmNewPrintMetaDataDataSource implements IMetaDataDataSource {

	/**
	 * 传入的BX单据VO
	 */
	private JKBXVO[] vos = null;
	
	/**
	 * 是否打印列表
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
