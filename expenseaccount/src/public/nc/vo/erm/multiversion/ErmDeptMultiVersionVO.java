package nc.vo.erm.multiversion;

import java.util.HashMap;
import java.util.Map;

import nc.itf.erm.multiversion.IMultiversionVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.org.DeptVO;
import nc.vo.vorg.DeptVersionVO;

/**
 * 报销管理多版本VO封装
 * @author chendya
 *
 */
public class ErmDeptMultiVersionVO implements IMultiversionVO {

	@Override
	public Map<String, String> getFieldMap() {
		Map<String, String> map = new HashMap<String, String>();
		map.put(JKBXHeaderVO.DEPTID, JKBXHeaderVO.DEPTID_V);
		map.put(JKBXHeaderVO.FYDEPTID, JKBXHeaderVO.FYDEPTID_V);
		return map;
	}
	
	@Override
	public Class<?> getVOClass() {
		return DeptVO.class;
	}

	@Override
	public Class<?> getVersionVOClass() {
		return DeptVersionVO.class;
	}

	@Override
	public String[] getTableNames() {
		return new String[] { DeptVO.getDefaultTableName(),
				DeptVersionVO.getDefaultTableName() };
	}

	@Override
	public String getOidField() {
		return DeptVO.PK_ORG;
	}

	@Override
	public String getVidField() {
		return DeptVersionVO.PK_VID;
	}
	
	@Override
	public String getVstartdateField() {
		return DeptVersionVO.VSTARTDATE;
	}
}
