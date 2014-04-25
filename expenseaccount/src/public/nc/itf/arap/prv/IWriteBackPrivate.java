package nc.itf.arap.prv;

import java.util.List;

import nc.vo.ep.bx.JKBXVO;
import nc.vo.erm.costshare.AggCostShareVO;
import nc.vo.erm.matterappctrl.IMtappCtrlBusiVO;

public interface IWriteBackPrivate {
	/**
	 * �����д�������뵥��ҵ������
	 * 
	 * @param vos
	 * @param eventType
	 * @return
	 * @author: wangyhh@ufida.com.cn
	 */
	public List<IMtappCtrlBusiVO> construstBusiDataForWriteBack(JKBXVO[] vos,String eventType); 
	/**
	 * ������ý�ת����д�������뵥��ҵ������
	 * 
	 * @param vos
	 * @param eventType
	 * @return
	 */
	public List<IMtappCtrlBusiVO> construstCostshareDataForWriteBack(AggCostShareVO[] vos,String eventType); 
}