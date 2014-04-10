package nc.itf.arap.prv;

import java.util.List;

import nc.vo.ep.bx.JKBXVO;
import nc.vo.erm.matterappctrl.IMtappCtrlBusiVO;

public interface IWriteBackPrivate {
	/**
	 * 构造回写费用申请单的业务数据
	 * 
	 * @param vos
	 * @param eventType
	 * @return
	 * @author: wangyhh@ufida.com.cn
	 */
	public List<IMtappCtrlBusiVO> construstBusiDataForWriteBack(JKBXVO[] vos,String eventType); 
}
