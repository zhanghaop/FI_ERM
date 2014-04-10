package nc.impl.erm.termendtransact;

import nc.bs.framework.common.NCLocator;
import nc.itf.erm.prv.IErmParamService;
import nc.itf.erm.termendtransact.ICloseAccountService;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.er.paramsetting.ParamVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;

public class CloseAccountServiceImpl implements ICloseAccountService {

	/**
	 * 根据pk_org、节点编号更新结账信息
	 * @param nodeCode
	 * @param pk_org
	 * @param period
	 * @throws Exception
	 */
	public void updateCloseAccountInfo(String nodeCode ,String pk_org ,String year,String month) throws Exception {

		String sParamCode = this.getParamCode(nodeCode);
		ParamVO paramVO = this.getParaService().queryParamByCode(pk_org, sParamCode, null);
		String period = getPeriod(year,month);
    	/**
    	* 如果不存在该参数，新建一个参数VO
    	*/
    	if(paramVO == null)
    	{
    		paramVO = new ParamVO();
    		paramVO.setPk_org(pk_org);
    		paramVO.setParam_code(sParamCode);
    		paramVO.setParam_name(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0","02011002-0092")/*@res "应收应付结账日期"*/);
        	paramVO.setParam_value(period);
        	this.getParaService().insertParams(new ParamVO[]{paramVO});
    	} else {
        	paramVO.setParam_value(period);
        	this.getParaService().updateParams(new ParamVO[]{paramVO});
    	}
	}

	/**
	 * 根据财务组织pk、结点编号查询某个期间是否结帐
	 * @param nodeCode
	 * @return 结帐：true。未结帐：false
	 * @throws BusinessException
	 */
	public boolean isAccountClosed(String nodeCode ,String pk_org ,String year,String month) throws BusinessException {

		String sParamCode = this.getParamCode(nodeCode);

		/**
		 * 如果参数编码为空，终止查询
		 */
		if(sParamCode == null) {
			return true;
		}

		String period = getPeriod(year,month);

		/**
		 * 如果查询出的结果包含period，说明已经结帐
		 */
		ParamVO paramVO = this.getParaService().queryParamByCode(pk_org, sParamCode, null);
		if (null != paramVO && null != paramVO.getParam_value() ) {
			UFDate a = new UFDate(period+"-01");
			UFDate b = new UFDate(paramVO.getParam_value().toString().trim()+"-01");
			if (!a.after(b))
				return false;
		}
		return true;
	}

	public String[] getCloseAccountInfo (String nodeCode ,String pk_org) throws BusinessException {
		String sParamCode = this.getParamCode(nodeCode);
		ParamVO paramVO = this.getParaService().queryParamByCode(pk_org, sParamCode, null);
		if (null != paramVO ) {
			return this.getResult(paramVO.getParam_value());
		}

		return new String[]{"",""};
	}

	private String[] getResult( String period) {
		String[] res = null;
		if(period!=null) {
			res = new String[]{period.substring(0,4), period.substring(5,7)};
		} else {
			res = new String[]{"",""};
		}
		return res;
	}

	private String getPeriod(String year,String month) {
		if(year == null || month == null) {
			return null;
		}if(month.length() <2) {
			month = "0" + month;
		}
		return year + "-" + month;
	}

	/**
	 * 获得ErmParamService
	 * @return
	 */
	private IErmParamService getParaService()
	{
		return NCLocator.getInstance().lookup(IErmParamService.class);
	}


	/**
	 * 取得操作模块的结点，根据结点代码返回参数编码
	 * @return 参数编码值
	 */
	private  String getParamCode(String nodeCode){
		String paramCode = null;
		if(BXConstans.ERM_MODULEID.equals(nodeCode)){
			paramCode = BXConstans.PARAM_CLSACC_ERM;
		}
		return paramCode;
	};
}