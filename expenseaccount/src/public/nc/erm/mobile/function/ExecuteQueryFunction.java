package nc.erm.mobile.function;

import java.util.HashMap;

import nc.arap.mobile.itf.IWebPubService;
import nc.bs.framework.common.NCLocator;


public class ExecuteQueryFunction implements InterfaceFunction {
	  private String CODE = InterfaceFunction.TOKEN + "executeQuery" + InterfaceFunction.TOKEN;
	  
	  @Override
	  public String getCode() {
		  return this.CODE;
	  }
	  
	  @Override
	  public FunctionResultVO value(Object object) throws Exception{
		  String sql = (String)object;
		  IWebPubService iWebPubService = (IWebPubService) (NCLocator.getInstance()
					.lookup(IWebPubService.class));
		  HashMap<String,String> resultMap = iWebPubService.executeQuery(sql);
		  FunctionResultVO functionResultVO = new FunctionResultVO();
		  functionResultVO.setRefPK(resultMap.get("value"));
		  functionResultVO.setRefCode(resultMap.get("code"));
		  functionResultVO.setRefName(resultMap.get("name"));
		  return functionResultVO;
	  }

}