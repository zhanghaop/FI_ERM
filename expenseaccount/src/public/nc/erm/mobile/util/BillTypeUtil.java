package nc.erm.mobile.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import nc.bs.erm.common.ErmBillConst;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.pf.pub.PfDataCache;
import nc.pubitf.rbac.IFunctionPermissionPubService;
import nc.vo.am.proxy.AMProxy;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.pub.BusinessException;
import nc.vo.pub.billtype.BilltypeVO;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class BillTypeUtil {
	public static String getIcon(String djlxbm){
		String iconsrc;
		if("2641".equals(djlxbm)){
			iconsrc = "clf.png";
		}else if("2642".equals(djlxbm)){
			iconsrc = "jtf.png";
		}else if("2643".equals(djlxbm)){
			iconsrc = "txf.png";
		}else if("2644".equals(djlxbm)){
			iconsrc = "lpf.png";
		}else if("2645".equals(djlxbm)){
			iconsrc = "zdf.png";
		}else if("2646".equals(djlxbm)){
			iconsrc = "hyf.png";
		}else if("2611".equals(djlxbm)){
			iconsrc = "matterapp1080.png";
		}else if("2631".equals(djlxbm) || "2632".equals(djlxbm)){
			iconsrc = "loan1280.png";
		}else if("2621".equals(djlxbm)){
			iconsrc = "accured1280.png";
		}else{
			iconsrc = "bx.png";
		}
		return iconsrc;
	}
	public static String JK = "jk";
	public static String BX = "bx";
	public static String MA = "ma";
	public static String AC = "ac";
	public static String getBXbilltype(String userid,String flag){
		JSONObject jsonObj = new JSONObject();
		try {
			String groupid = InvocationInfoProxy.getInstance().getGroupId();
		    //得到用户有权限的功能节点
			String[] stra = NCLocator.getInstance().lookup(IFunctionPermissionPubService.class)
			.getUserPermissionFuncNode(userid, groupid);
			List<String> nodecodelist = Arrays.asList(stra);
			HashMap<String, BilltypeVO> billtypes = PfDataCache.getBilltypes();
			List<BilltypeVO> list = new ArrayList<BilltypeVO>();
			String pk_group = InvocationInfoProxy.getInstance().getGroupId();
			for (BilltypeVO vo : billtypes.values()) {
				if (vo.getSystemcode() != null && vo.getSystemcode().equalsIgnoreCase(BXConstans.ERM_PRODUCT_CODE)) {
					// 通过当前集团进行过滤
					if (vo.getPk_group() != null && !vo.getPk_group().equalsIgnoreCase(pk_group)) {
						continue;
					}
					//判断当前用户是否对此节点有权限
					if(!nodecodelist.contains(vo.getNodecode())){
						continue;
					}
					if(BX.equals(flag)){
						//报销类单据
						if (vo.getPk_billtypecode().equals("2647") || vo.getPk_billtypecode().equals("264a")) {
							continue;
						}
						if (BXConstans.BX_DJLXBM.equals(vo.getParentbilltype())) {
							list.add(vo);
						}
					}else if(JK.equals(flag)){
						//借款类单据
						if (BXConstans.JK_DJLXBM.equals(vo.getParentbilltype())) {
							list.add(vo);
						}
					}else if(MA.equals(flag)){
						//预提类单据
						if (ErmBillConst.AccruedBill_Billtype.equals(vo.getParentbilltype())) {
							list.add(vo);
						}
					}else if(AC.equals(flag)){
						//费用申请类单据
						if (ErmBillConst.MatterApp_BILLTYPE.equals(vo.getParentbilltype()) ) {
							list.add(vo);
						}
					}else{
						//清除缓存时用，返回所有单据类型
						//报销类单据
						if (vo.getPk_billtypecode().equals("2647") || vo.getPk_billtypecode().equals("264a")) {
							continue;
						}
						if (BXConstans.BX_DJLXBM.equals(vo.getParentbilltype()) || BXConstans.JK_DJLXBM.equals(vo.getParentbilltype())) {
							list.add(vo);
						}
						//预提类单据
						if (ErmBillConst.AccruedBill_Billtype.equals(vo.getParentbilltype())) {
							list.add(vo);
						}
						//费用申请类单据
						if (ErmBillConst.MatterApp_BILLTYPE.equals(vo.getParentbilltype()) ) {
							list.add(vo);
						}
					}
				}
			}
			BilltypeVO[] toArray = list.toArray(new BilltypeVO[] {});
			Arrays.sort(toArray, new Comparator<BilltypeVO>() {
				public int compare(BilltypeVO o1, BilltypeVO o2) {
					return o1.getPk_billtypecode().compareTo(o2.getPk_billtypecode());
				}
			});
			JSONArray funcodearray = new JSONArray();
			for(int i =0; i<toArray.length; i++){
				JSONObject o = new JSONObject();
				o.put("djlxbm", toArray[i].getPk_billtypecode());
				o.put("djlxmc", toArray[i].getBilltypename());
				o.put("nodecode", toArray[i].getNodecode());
				o.put("iconsrc", getIcon(toArray[i].getPk_billtypecode()));
				funcodearray.put(o);
			}
			jsonObj.put("jkbxheadList", funcodearray);
			return jsonObj.toString();
		}catch (BusinessException e) {
			return jsonObj.toString();
		} catch (JSONException e) {
			return jsonObj.toString();
		}
	}
	
	public static String[] getBillTypeArray(String userid,String flag) throws BusinessException{
		String groupid = InvocationInfoProxy.getInstance().getGroupId();
	    //得到用户有权限的功能节点
		String[] stra = AMProxy.lookup(IFunctionPermissionPubService.class)
		.getUserPermissionFuncNode(userid, groupid);
		List<String> nodecodelist = Arrays.asList(stra);
		HashMap<String, BilltypeVO> billtypes = PfDataCache.getBilltypes();
		List<BilltypeVO> list = new ArrayList<BilltypeVO>();
		String pk_group = InvocationInfoProxy.getInstance().getGroupId();
		for (BilltypeVO vo : billtypes.values()) {
			if (vo.getSystemcode() != null && vo.getSystemcode().equalsIgnoreCase(BXConstans.ERM_PRODUCT_CODE)) {
				// 通过当前集团进行过滤
				if (vo.getPk_group() != null && !vo.getPk_group().equalsIgnoreCase(pk_group)) {
					continue;
				}
				//判断当前用户是否对此节点有权限
				if(!nodecodelist.contains(vo.getNodecode())){
					continue;
				}
				if(BX.equals(flag)){
					//报销类单据
					if (vo.getPk_billtypecode().equals("2647") || vo.getPk_billtypecode().equals("264a")) {
						continue;
					}
					if (BXConstans.BX_DJLXBM.equals(vo.getParentbilltype())) {
						list.add(vo);
					}
				}else if(JK.equals(flag)){
					//借款类单据
					if (BXConstans.JK_DJLXBM.equals(vo.getParentbilltype())) {
						list.add(vo);
					}
				}else if(MA.equals(flag)){
					//预提类单据
					if (ErmBillConst.AccruedBill_Billtype.equals(vo.getParentbilltype())) {
						list.add(vo);
					}
				}else if(AC.equals(flag)){
					//费用申请类单据
					if (ErmBillConst.MatterApp_BILLTYPE.equals(vo.getParentbilltype()) ) {
						list.add(vo);
					}
				}else{
					//清除缓存时用，返回所有单据类型
					//报销类单据
					if (vo.getPk_billtypecode().equals("2647") || vo.getPk_billtypecode().equals("264a")) {
						continue;
					}
					if (BXConstans.BX_DJLXBM.equals(vo.getParentbilltype()) || BXConstans.JK_DJLXBM.equals(vo.getParentbilltype())) {
						list.add(vo);
					}
					//预提类单据
					if (ErmBillConst.AccruedBill_Billtype.equals(vo.getParentbilltype())) {
						list.add(vo);
					}
					//费用申请类单据
					if (ErmBillConst.MatterApp_BILLTYPE.equals(vo.getParentbilltype()) ) {
						list.add(vo);
					}
				}
			}
		}
		BilltypeVO[] toArray = list.toArray(new BilltypeVO[] {});
		Arrays.sort(toArray, new Comparator<BilltypeVO>() {
			public int compare(BilltypeVO o1, BilltypeVO o2) {
				return o1.getPk_billtypecode().compareTo(o2.getPk_billtypecode());
			}
		});
		String[] djlxbmarray = new String[toArray.length];
		for(int i =0; i<toArray.length; i++){
			djlxbmarray[i] = toArray[i].getPk_billtypecode();
		}
		return djlxbmarray;
	}
}
