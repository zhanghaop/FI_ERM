package nc.erm.mobile.billaction;

import nc.erm.mobile.util.NumberFormatUtil;
import nc.vo.pub.lang.UFDouble;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;



public class DelLineAction {
	public String delLine(String userid, String ctx1, String itemno, String djlxbm){
		double total = 0;
		JSONObject ctx;
		try {
			ctx = new JSONObject(ctx1);
			JSONArray itemlist = (JSONArray) ctx.get("itemlist");
			JSONArray newitemlist = new JSONArray();
			for(int i=0; i<itemlist.length(); i++){
				JSONObject item = (JSONObject) itemlist.get(i);
				if(itemno.equals(item.get("itemno"))){
					continue;
				}
				newitemlist.put(item);
			}
			for(int i = 0; i < newitemlist.length(); i++){
				JSONObject item = (JSONObject) newitemlist.get(i);
				if(i == 0){
					item.put("ListViewSelector", "1");
				}
				item.put("itemno", i);
				total = total + Float.parseFloat((String) item.get("amount"));
			}
			ctx.put("itemlist", newitemlist);
			ctx.put("itemnum", newitemlist.length());
			ctx.put("total", total);
			ctx.put("total_name", NumberFormatUtil.formatDouble(new UFDouble(total)));
			return ctx.toString();
		} catch (JSONException e) {
			return null;
		}
	}
	/**
	 * 
	 * 方法说明：表体行改变后重新设置金额
	 * 
	 * @param e
	 * @see
	 * @since V6.0
	 */
	public void resetJeAfterModifyRow() {
//		if (!editor.getBillCardPanel().getCurrentBodyTableCode().equals(BXConstans.CSHARE_PAGE)) {
//			editor.getHelper().calculateFinitemAndHeadTotal(editor);
//			try {
//				// eventUtil.setHeadYFB();
//				eventUtil.resetHeadYFB();
//				// editor.getEventHandle().resetBodyFinYFB();
//			} catch (BusinessException e) {
//				ExceptionHandler.handleExceptionRuntime(e);
//			}
//		} else {
//			// 费用调整单情况，按照分摊页签进行合计
//			try {
//				ErmForCShareUiUtil.calculateHeadTotal(getBillCardPanel());
//			} catch (BusinessException e) {
//				ExceptionHandler.handleExceptionRuntime(e);
//			}
//		}
	}

}
