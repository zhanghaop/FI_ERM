package nc.bs.erm.test;

import java.util.ArrayList;
import java.util.List;

import nc.bs.framework.test.AbstractTestCase;
import nc.vo.jcom.lang.StringUtil;

public class ErmTestcase extends AbstractTestCase {
	
	public void _test(){
		
		String code = "@@@@_201100";
		code = code.split("_")[1];
		
		System.out.println("code:"+code);
		System.out.println("parentcode:"+code.substring(0, code.length() - 2));
		System.out.println("newcode:"+"20110302"+Integer.toHexString((Integer.parseInt("a0",16)+1)));
		
		List<String> errCodes = new ArrayList<String>();
		errCodes.add("201101");
		errCodes.add("201102");
		errCodes.add("2011034");
		System.out.println("errCodes:"+StringUtil.toString(errCodes, ",", "'", "'"));
		
	}
	
	public void testUpdate(){
//		借用IErmGroupPredataService来测试ErmNewInstallAdjust中的方法doUpdateMenu
//		EnvInit.initClientEnv();
//		IErmGroupPredataService service = NCLocator.getInstance().lookup(IErmGroupPredataService.class);
//		try {
//			service.initGroupData(null);
//		} catch (BusinessException e) {
//			e.printStackTrace();
//		}
	}

}
