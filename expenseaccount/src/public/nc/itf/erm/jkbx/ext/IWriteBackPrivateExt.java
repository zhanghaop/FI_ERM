package nc.itf.erm.jkbx.ext;

import java.util.List;

import nc.vo.ep.bx.JKBXVO;
import nc.vo.erm.matterappctrl.IMtappCtrlBusiVO;

/**
 * 构造整单回写申请单的回写结构
 * 
 * 合生元专用
 * 
 * @author lvhj
 *
 */
public interface IWriteBackPrivateExt {
	/**
	 * 构造回写费用申请单的业务数据
	 * 
	 * @param vos
	 * @param eventType
	 * @return
	 */
	public List<IMtappCtrlBusiVO> construstBusiDataForWriteBack(JKBXVO[] vos,String eventType); 
}
