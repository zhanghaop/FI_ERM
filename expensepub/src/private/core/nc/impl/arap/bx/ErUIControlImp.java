package nc.impl.arap.bx;

import java.util.List;

import nc.bs.framework.common.NCLocator;
import nc.itf.arap.prv.IBXBillPrivate;
import nc.itf.arap.pub.IBxUIControl;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.er.util.StringUtils;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;

/**
 * @author twei
 *
 * nc.impl.arap.bx.ErUIControlImp
 */
public class ErUIControlImp implements IBxUIControl{

	/**
	 * @param head
	 * @param cxrq 冲销日期，在前台取登陆日期
	 * @param sampQuery
	 * @return
	 * @throws BusinessException
	 */
	public List<JKBXHeaderVO> getJKD(JKBXHeaderVO head,UFDate cxrq,String sampQuery) throws BusinessException {

		if(head.getJkbxr()==null && head.getReceiver()==null){
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000179")/*@res "请录入报销人,再进行冲借款操作!"*/);
		}

		if(head.getYbje()==null){
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000180")/*@res "请录入报销金额,再进行冲借款操作!"*/);
		}

		if(head.getBzbm()==null){
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000390")/*@res "请录入报销币种,再进行冲借款操作!"*/);
		}

		String jkbxr=StringUtils.isNullWithTrim(head.getReceiver())?head.getJkbxr():head.getReceiver();
		String pkbxd=head.getPk_jkbx();
		String bzbm= head.getBzbm();

		UFBoolean ischeck = head.getIscheck()==null?UFBoolean.FALSE:head.getIscheck();

		String sql = "" ;

		if(StringUtils.isNullWithTrim(sampQuery)){
			sql = "  where (zb.yjye>0 and zb.dr=0 and zb.djzt=3 and zb.ischeck='"+ischeck+"' and zb.bzbm='"+bzbm+"' and zb.jkbxr='"
			+ jkbxr + "' and zb.djrq<='"+cxrq+"') ";

			if(!StringUtils.isNullWithTrim(pkbxd)){
				sql+=" or  ( zb.pk_jkbx in (select pk_jkd from er_bxcontrast where pk_bxd='"+pkbxd+"' and sxbz=0 ))" ;
			}
		}else{
			sql = "  where zb.yjye>0 and zb.dr=0 and zb.djzt=3 and zb.djrq<='"+cxrq+"' and zb.ischeck='"+ischeck+"' and zb.bzbm='"+bzbm+"' and "+sampQuery ;
		}

		IBXBillPrivate bxpri=((IBXBillPrivate) NCLocator.getInstance().lookup(IBXBillPrivate.class.getName()));

		List<JKBXHeaderVO> bxvos = bxpri.queryHeadersByWhereSql(sql, BXConstans.JK_DJDL);

		for(JKBXHeaderVO vo:bxvos){
			vo.setYbye(vo.getYjye());
		}

		return bxvos;
	}

}