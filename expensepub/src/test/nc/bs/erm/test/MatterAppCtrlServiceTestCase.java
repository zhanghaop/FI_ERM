package nc.bs.erm.test;

import java.util.Collection;

import nc.bs.framework.common.NCLocator;
import nc.bs.framework.test.AbstractTestCase;
import nc.md.model.MetaDataException;
import nc.md.persist.framework.IMDPersistenceQueryService;
import nc.md.persist.framework.MDPersistenceService;
import nc.md.util.EnvInit;
import nc.pubitf.erm.matterappctrl.ext.IMatterAppCtrlServiceExt;
import nc.vo.ep.bx.BXVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXMtappCtrlBusiVO;
import nc.vo.erm.matterappctrl.IMtappCtrlBusiVO;
import nc.vo.erm.matterappctrl.MtappCtrlInfoVO;
import nc.vo.erm.util.ErVOUtils;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;

public class MatterAppCtrlServiceTestCase extends AbstractTestCase {
	
	/**
	 * 按申请单行控制回写，测试
	 */
	public void testMtappCtrlByDetail(){
		EnvInit.initClientEnv();
		
		String matterappPK = "1001Z31000000000N3SJ";
		String matterappDPK1 = "1001Z31000000000N3SK";//10
		String matterappDPK2 = "1001Z31000000000N3SL";//30
		
		IMtappCtrlBusiVO[] vos = new JKBXMtappCtrlBusiVO[2];
		
		String bx_no = "264X201307180077";

		IMDPersistenceQueryService qs = MDPersistenceService.lookupPersistenceQueryService();
		try {
			Collection c = qs.queryBillOfVOByCond(BXVO.class, JKBXHeaderVO.DJBH +" = '"+bx_no+"'", false);
			for (Object object : c) {
				BXVO bxvo = (BXVO) object;
				JKBXHeaderVO[] headerVos = ErVOUtils.prepareBxvoItemToHeaderClone(bxvo);
				JKBXHeaderVO parentVO = headerVos[0];
				JKBXMtappCtrlBusiVO vo = new JKBXMtappCtrlBusiVO(parentVO);
//				vo.matterAppDetailPK = matterappDPK1;
				parentVO.setPk_item(matterappPK);
				vo.setDirection(IMtappCtrlBusiVO.Direction_negative);
				vo.setAmount(vo.getAmount().multiply(new UFDouble(-1)));
//				vo.setDirection(IMtappCtrlBusiVO.Direction_positive);
				vo.setDatatype(IMtappCtrlBusiVO.DataType_exe);
				vos[0] = vo;
				
				JKBXHeaderVO parentVO2 = headerVos[1];
				JKBXMtappCtrlBusiVO vo2 = new JKBXMtappCtrlBusiVO(parentVO2);
//				vo2.matterAppDetailPK = matterappDPK2;
				parentVO2.setPk_item(matterappPK);
				vo2.setDirection(IMtappCtrlBusiVO.Direction_negative);
				vo2.setAmount(vo2.getAmount().multiply(new UFDouble(-1)));
//				vo2.setDirection(IMtappCtrlBusiVO.Direction_positive);
				vo2.setDatatype(IMtappCtrlBusiVO.DataType_exe);
				vos[1] = vo2;
			}
		} catch (MetaDataException e1) {
//			e1.printStackTrace();
		}
		
		IMatterAppCtrlServiceExt service = NCLocator.getInstance().lookup(IMatterAppCtrlServiceExt.class);
		try {
			MtappCtrlInfoVO errvo = service.matterappControlByDetail(vos);
			String[] controlinfos = errvo.getControlinfos();
			if(controlinfos != null && controlinfos.length > 0){
				for (int i = 0; i < controlinfos.length; i++) {
					System.out.println(controlinfos[i]);
				}
			}
		} catch (BusinessException e) {
//			e.printStackTrace();
		}
	}
	
}