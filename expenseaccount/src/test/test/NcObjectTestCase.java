package test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.test.AbstractTestCase;
import nc.md.data.access.NCObject;
import nc.md.model.IBusinessEntity;
import nc.md.util.EnvInit;
import nc.uap.pf.metadata.PfMetadataTools;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.pub.BusinessException;

public class NcObjectTestCase extends AbstractTestCase {

	public void _testObj(){
		EnvInit.initClientEnv();
		InvocationInfoProxy.getInstance().setGroupId("0001Z3100000000008I3");
		// ��������ҵ�񵥾���Ϣ
		IBusinessEntity destBE;
		try {
			destBE = PfMetadataTools.queryMetaOfBilltype("2641");
			NCObject busiobj = NCObject.newInstance(destBE,null);
			
			busiobj.setAttributeValue(JKBXHeaderVO.APPROVER, "xxxxx");
			busiobj.setAttributeValue(BXConstans.ER_BUSITEM+"."+BXBusItemVO.PK_ITEM, "xxxxx");
			busiobj.setAttributeValue(BXConstans.ER_BUSITEM+"."+BXBusItemVO.SRCBILLTYPE, new String[]{"1","2"});
			
			System.out.println(busiobj.getAttributeValue(JKBXHeaderVO.APPROVER));
			System.out.println(busiobj.getAttributeValue(BXConstans.ER_BUSITEM+"."+BXBusItemVO.PK_ITEM));
			System.out.println(busiobj.getAttributeValue(BXConstans.ER_BUSITEM+"."+BXBusItemVO.SRCBILLTYPE));
			
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void testMenu(){
		EnvInit.initClientEnv();
		// 61ԭϵͳ���˵���
		String[] menuItems61 = new String[] { "2011", "201100", "20110002",
				"20110004", "20110006", "20110008", "20110010", "20110012",
				"20110014", "20110016", "20110018", "20110024", "201101",
				"201102", "20110202", "20110204", "201103", "20110301",
				"20110302", "20110303", "20110304", "20110305", "20110306",
				"20110307", "20110308", "20110309", "201106", "20110602",
				"2011060202", "2011060206", "201109", "20110903", "2011090301",
				"20110904", "2011090401", "20110905", "2011090501", "20110906",
				"2011090601", "20110907", "2011090701", "2011090701", "201111",
				"20111101", "2011110101" };
		List<String> menuItem61List = Arrays.asList(menuItems61);
		// ���61�˵���Ŀ�����Ӧ63��
		Map<String, String> codemap = new HashMap<String, String>();
		codemap.put("201100", "201100");
		codemap.put("2011", "2011"); // ��������-���ù���
		codemap.put("201103", "20110202");
		codemap.put("20110602", "201104");
		codemap.put("201109", "201105");
		codemap.put("20110904", "20110501");
		codemap.put("20110903", "20110502");
		codemap.put("20110907", "20110503");
		codemap.put("20110906", "20110504");
		codemap.put("20110905", "20110505");
		codemap.put("201111", "201106");
		codemap.put("20111101", "20110601");
		codemap.put("201102", "201102");// ���ݴ���-��������
		codemap.put("201106", "201102");// �ۺϴ���-��������
		
		String code = "@@@@_20110310".split("_")[1];
		if(menuItem61List.contains(code)){
			// ϵͳԤ�õĲ˵����룬����
			System.out.println("ϵͳԤ�õĲ˵����룬����");
		}
		// ����Զ���˵���Ŀ�ĸ����룬��ȥ����λ
		String parentcode = code.substring(0, code.length() - 2);
		String parent63 = codemap.get(parentcode);
		if(parent63 == null){
			// ��Ǳ��ݲ˵���Ŀ��ɾ��
			System.out.println("61������63���˵��������󣬲˵�����--------"+code);
		}
		System.out.println("���ײ˵�����--------"+parent63);
	}
}
