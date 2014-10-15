package nc.bs.arap.bx;

import nc.vo.arap.bx.util.BXConstans;
import nc.vo.erm.mapping.Er_bxzbVOMeta;
import nc.vo.erm.mapping.Er_jkzbVOMeta;
import nc.vo.erm.mapping.IBXArapMappingMeta;

/**
 * @author twei
 *
 * nc.bs.arap.bx.BXVOUtil
 * 
 * 报销单据VO后台工具类
 */
public class BXVOUtil {
	private static IBXArapMappingMeta bxmeta= new Er_bxzbVOMeta();
	private static IBXArapMappingMeta jkmeta= new Er_jkzbVOMeta();
	public static IBXArapMappingMeta getMetaData(String djdl) {
		if (djdl != null && djdl.equals(BXConstans.JK_DJDL))
				return jkmeta;
		return bxmeta;
	}
}
