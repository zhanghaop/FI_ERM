package nc.itf.erm.jkbx.ext;

import java.util.List;

import nc.vo.ep.bx.JKBXVO;
import nc.vo.erm.matterappctrl.IMtappCtrlBusiVO;

/**
 * ����������д���뵥�Ļ�д�ṹ
 * 
 * ����Ԫר��
 * 
 * @author lvhj
 *
 */
public interface IWriteBackPrivateExt {
	/**
	 * �����д�������뵥��ҵ������
	 * 
	 * @param vos
	 * @param eventType
	 * @return
	 */
	public List<IMtappCtrlBusiVO> construstBusiDataForWriteBack(JKBXVO[] vos,String eventType); 
}
