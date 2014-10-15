package nc.vo.arap.bx.util;

import nc.vo.ml.NCLangRes4VoTransl;

/**
 * @author twei
 *
 * nc.vo.ep.bx.BXUtils
 *
 * VO工具类
 */
public class BXVOUtils {
	
	public static boolean simpleEquals(Object o1,Object o2){
		if(o1==null && o2==null)
			return true;
		if(o1==null || o2==null){
			return false;
		}
		return o1.equals(o2);
	}

	/**
	 * @param djzt
	 * @return 单据状态名称
	 */
	public static String getDjztmc(Integer djzt){

		if(djzt==null)
			return "";
	    int zt = djzt.intValue();
		if(zt==BXStatusConst.DJZT_Saved){
	        return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("200602","UPP200602-v50-000168");
	    }else if(zt==BXStatusConst.DJZT_Verified){
	        return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("200602","UPP200602-v50-000169");
	    }else if(zt==BXStatusConst.DJZT_Sign){
	        return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000271")/*@res "已签字"*/;
	    }else if(zt==BXStatusConst.DJZT_TempSaved){
	        return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("200602","UPP200602-v50-000172");
	    }else
	        return "";
	}

	/**
	 * @param sxbz
	 * @return 生效标志名称
	 */
	public static String getSxbzmc(Integer sxbz){

		if(sxbz==null)
			return "";
	    int zt = sxbz.intValue();
        if(zt==BXStatusConst.SXBZ_VALID)
            return NCLangRes4VoTransl.getNCLangRes().getStrByID("2006","UPP2006-v51-000299")/*@res "已生效"*/;
        else if(zt==BXStatusConst.SXBZ_NO)
        	return NCLangRes4VoTransl.getNCLangRes().getStrByID("2006","UPP2006-v51-000300")/*@res "未生效"*/;
        return "";

	}

}