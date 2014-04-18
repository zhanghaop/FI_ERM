package nc.vo.erm.multiversion;

import java.util.HashMap;
import java.util.Map;

import nc.itf.erm.multiversion.IMultiversionVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.org.OrgVO;
import nc.vo.vorg.OrgVersionVO;

/**
 * 报销管理多版本VO封装
 * @author chendya
 *
 */
public class ErmOrgMultiVersionVO implements IMultiversionVO {

	@Override
	public Map<String, String> getFieldMap() {
		Map<String, String> map = new HashMap<String, String>();
		map.put(JKBXHeaderVO.PK_ORG, JKBXHeaderVO.PK_ORG_V);
		map.put(JKBXHeaderVO.FYDWBM, JKBXHeaderVO.FYDWBM_V);
		map.put(JKBXHeaderVO.DWBM, JKBXHeaderVO.DWBM_V);
		return map;
	}
	
	@Override
	public Class<?> getVOClass() {
		return OrgVO.class;
	}

	@Override
	public Class<?> getVersionVOClass() {
		return OrgVersionVO.class;
	}

	@Override
	public String[] getTableNames() {
		return new String[] { OrgVO.getDefaultTableName(),
				OrgVersionVO.getDefaultTableName() };
	}

	@Override
	public String getOidField() {
		return OrgVO.PK_ORG;
	}

	@Override
	public String getVidField() {
		return OrgVersionVO.PK_VID;
	}
	
	@Override
	public String getVstartdateField() {
		return OrgVersionVO.VSTARTDATE;
	}
}
