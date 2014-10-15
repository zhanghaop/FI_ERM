package nc.vo.arap.bx.util;

import java.util.Vector;

import nc.vo.er.djlx.DjLXVO;
import nc.vo.er.pub.PubConstData;
import nc.vo.er.pub.QryCondArrayVO;
import nc.vo.er.pub.QryCondVO;
import nc.vo.er.util.StringUtils;

/**
 * @author twei
 *
 * nc.vo.arap.bx.util.BXQueryUtil
 * 
 * 报销查询工具类
 * 
 * 1. 补充默认条件
 * 2. 处理逻辑条件
 */
public class BXQueryUtil implements PubConstData{
	
	public static String getDjdlFromBm(String djlxbms , DjLXVO[] vos) {
		String djdl="";
		if(StringUtils.isNullWithTrim(djlxbms))
			return djdl;
		
		String[] djlxs = djlxbms.split(",");
		
		for(String dj:djlxs){
			if(dj.startsWith("('")){
				dj=dj.substring(2);
			}
			if(dj.startsWith("'")){
				dj=dj.substring(1);
			}
			if(dj.endsWith("')")){
				dj=dj.substring(0,dj.length()-2);
			}
			if(dj.endsWith("'")){
				dj=dj.substring(0,dj.length()-1);
			}
			
			DjLXVO djlxVO = getDjlxVO(vos,dj);
			
			if(djlxVO==null)
				continue;
			
			if(StringUtils.isNullWithTrim(djdl)){
				djdl=djlxVO.getDjdl();
			}else if(!djlxVO.getDjdl().equals(djdl)){
				return "";
			}
		}
		
		return djdl;
	}

	private static DjLXVO getDjlxVO(DjLXVO[] vos, String dj) {
		if(vos==null)
			return null;
		for(DjLXVO vo:vos){
			if(vo.getDjlxbm().equals(dj))
				return vo;
		}
		return null;
	}

	public static final String PZZT = "pzzt";
	public static final String XSPZ = "xspz";
	public static final String APPEND = "append";
	public static final String DJLXBM = "zb.djlxbm";

	public final static Integer Voucher_All = Integer.valueOf(-1);
	public final static Integer Voucher_NotCreated = Integer.valueOf(0);
	public final static Integer Voucher_Created = Integer.valueOf(1);
	public final static Integer Voucher_Singed = Integer.valueOf(2);
	
	public static QryCondArrayVO[] getValueCondVO(boolean isQc) {
		Vector<Object> v = new Vector<Object>();

		Object oTmp = null;

		oTmp = getCondIncludeDr();
		if (oTmp != null) {
			v.add(oTmp);
		}
		oTmp = getCondQcbz(isQc);
		if (oTmp != null) {
			v.add(oTmp);
		}
//		oTmp = getCorpConds();
//		if (oTmp != null) {
//			v.add(oTmp);
//		}

		if (v.size() == 0)
			return null;
		QryCondArrayVO[] voResults = new QryCondArrayVO[v.size()];
		v.copyInto(voResults);
		return voResults;
	}
	


	private static QryCondArrayVO getCondIncludeDr() {
		Vector<QryCondVO> v = new Vector<QryCondVO>();
		QryCondVO voTmp = null;
		voTmp = new QryCondVO();
		voTmp.setFldorigin("zb");
		voTmp.setQryfld("dr");
		voTmp.setFldtype(Integer.valueOf(INTEGER));
		voTmp.setBoolopr("=");
		voTmp.setValue("0");
		v.addElement(voTmp);

		QryCondVO[] tmp = new QryCondVO[v.size()];
		v.copyInto(tmp);
		QryCondArrayVO re = new QryCondArrayVO();
		re.setLogicAnd(false);
		re.setItems(tmp);
		return re;
	}
	
	private static QryCondArrayVO getCondQcbz(boolean isQc) {
        Vector<QryCondVO> v = new Vector<QryCondVO>();
        QryCondVO voTmp = new QryCondVO();
        voTmp.setFldorigin("zb");
        voTmp.setQryfld("qcbz");
        voTmp.setFldtype(Integer.valueOf(STRING));
        voTmp.setBoolopr("=");
        voTmp.setValue(isQc?"Y":"N");
        v.addElement(voTmp);

        QryCondVO[] tmp = new QryCondVO[v.size()];
        v.copyInto(tmp);
        QryCondArrayVO re = new QryCondArrayVO();
        re.setLogicAnd(true);
        re.setItems(tmp);
        return re;
    }

	public static Integer[] splitQueryConditons(String value) {
		value=value.replace(')', ' ');
		value=value.replace('(', ' ');
		value=value.replace('\'', ' ');
		String[] values = value.split(",");
		Integer[] Ivalues = new Integer[values.length];
		for (int j = 0; j < values.length; j++) {
			Ivalues[j]=new Integer(values[j].trim());
		}
		return Ivalues;
	}
}
