package nc.erm.mobile.view;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;

import nc.erm.mobile.environment.ErmTemplateQueryUtil;
import nc.erm.mobile.util.JsonItem;
import nc.ui.pub.beans.constenum.DefaultConstEnum;
import nc.ui.pub.bill.IBillItem;
import nc.vo.pub.BusinessException;
import nc.vo.pub.bill.BillStructVO;
import nc.vo.pub.bill.BillTabVO;
import nc.vo.pub.bill.BillTempletBodyVO;
import nc.vo.pub.bill.BillTempletHeadVO;
import nc.vo.pub.bill.BillTempletVO;
import nc.vo.pub.bill.MetaDataPropertyAdpter;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class MobileTemplateFactory {
	BillTempletVO billTempletVO;
	JSONObject formula;
	StringBuffer notnull;
	StringBuffer order;
	public MobileTemplateFactory(String djlxbm) {
		try {
			billTempletVO =  ErmTemplateQueryUtil.getDefaultTempletStatics(djlxbm);
			formula = new  JSONObject();
			notnull = new StringBuffer();
			order = new StringBuffer();
		} catch (BusinessException e) {
			ExceptionUtils.wrappBusinessException("模板转换为移动端数据异常：" + e.getMessage());
		}
	}
	public String getheadcarddsl(String flag){
		JSONObject jsonObj = new JSONObject();
		//表头要加载字段，编辑项
        BillTempletBodyVO[] bodyVO = billTempletVO.getBodyVO();
        try {
        	//编辑界面点击添加按钮时需要表体页签列表  表体页签列表
			jsonObj.put("tablist", getTableCodes(billTempletVO.getHeadVO()));
			jsonObj.put("dsl", getheaddsl(flag,bodyVO));
			jsonObj.put("formula", formula.toString());
			jsonObj.put("notnull", notnull.toString());
			jsonObj.put("ts", billTempletVO.getHeadVO().getTs().toString());
        } catch (Exception e) {
		}
		return jsonObj.toString();
	}
	public String getbodycarddsl(String flag, String tablecode){
		JSONObject jsonObj = new JSONObject();
		//表头要加载字段，编辑项
        BillTempletBodyVO[] bodyVO = billTempletVO.getBodyVO();
        try {
        	//编辑界面点击添加按钮时需要表体页签列表  表体页签列表
        	jsonObj.put("dsl", getbodydsl(bodyVO,tablecode,flag));
			jsonObj.put(tablecode + "_order", order.toString());
			jsonObj.put(tablecode + "_formula", formula.toString());
			jsonObj.put(tablecode + "_notnull", notnull.toString());
			jsonObj.put("ts", "\""+billTempletVO.getHeadVO().getTs().toString()+"\"");
        } catch (Exception e) {
		}
		return jsonObj.toString();
	}
	boolean isHeadShow(BillTempletBodyVO bVO){
		return ((bVO.getPos().intValue() == IBillItem.HEAD || bVO.getPos().intValue() == IBillItem.TAIL) && bVO.getShowflag().booleanValue());
	}
	boolean isHeadListShow(BillTempletBodyVO bVO){
		return ((bVO.getPos().intValue() == IBillItem.HEAD || bVO.getPos().intValue() == IBillItem.TAIL) && bVO.getListshowflag().booleanValue());
	}
	boolean isItemShow(BillTempletBodyVO bVO,String tablecode){
		return ((bVO.getPos().intValue() == IBillItem.BODY && bVO.getTable_code().equals(tablecode) && bVO.getShowflag().booleanValue()));
	}
	private int panel = 9990;
	public String getheaddsl(String flag,BillTempletBodyVO[] bodyVO){
		StringBuffer div = new StringBuffer();
		div.append("<div id=\"viewPage99\"  layout=\"vbox\" width=\"fill\" height=\"wrap\">");
		div.append("<div id=\"viewPage" + panel
				+ "\"  layout=\"vbox\" width=\"fill\" height=\"wrap\" padding-left=\"15\">");
		
		int itemnum = 0;
		boolean hide = false;
		if(flag.equals("addcard")){
			if (bodyVO != null) {
				initBodyVOs(bodyVO);
				for (int i = 0; i < bodyVO.length; i++) {
					BillTempletBodyVO bVO = bodyVO[i];
					if(isHeadShow(bVO)){
						//记录当前有几行表头
						itemnum++;
						JsonItem item = new JsonItem(bVO, bVO.getCardflag());
						if(itemnum > 3 && hide == false){
							//超过3行时隐藏剩下的div
							div.append("<div id=\"foldpanel\" layout=\"vbox\" valign=\"center\" width=\"fill\" height=\"wrap\"  color=\"#000000\" >");//display=\"none\"
							hide = true;
						}
						div.append(buildOnePanel("head.",item,flag));
						panel++;
						div.append("<div id=\"viewPage" + panel
								+ "\"  layout=\"vbox\" width=\"fill\" height=\"1\" background=\"#c7c7c7\" />");//margin-left=\"15\"
						
						//记录表头公式
						if(item.getEditFormulas() != null){
							String[] formulars = item.getEditFormulas();
							StringBuffer formularstr = new StringBuffer();
							for(int j=0;j<formulars.length;j++){
								formularstr.append(formulars[j]+";");
							}
							try {
								formula.put(item.getKey(), formularstr);
							} catch (JSONException e) {
								
							}
						}
						
						//获取表头必输项
						if(item.isNull()){
							notnull.append(item.getKey()+",");
						}
					}
				}
				//private boolean isContrast = false; // 是否进行了冲借款操作
				//private boolean isVerifyAccrued = false; // 是否进行了核销预提操作
				JsonItem item = new JsonItem();
				item.setDataType(IBillItem.BOOLEAN);
				item.setKey("isContrast");
				item.setName("冲借款");
				item.setEdit(true);
				item.setNull(false);
				div.append(buildOnePanel("head.",item,flag));
				panel++;
				div.append("<div id=\"viewPage" + panel
						+ "\"  layout=\"vbox\" width=\"fill\" height=\"1\" background=\"#c7c7c7\" />");//margin-left=\"15\"
				item.setKey("isVerifyAccrued");
				item.setName("核销预提");
				item.setEdit(true);
				div.append(buildOnePanel("head.",item,flag));
				panel++;
				div.append("<div id=\"viewPage" + panel
						+ "\"  layout=\"vbox\" width=\"fill\" height=\"1\" background=\"#c7c7c7\" />");//margin-left=\"15\"
				
			}
		}
		else if(flag.equals("editcard")){
			if (bodyVO != null) {
				initBodyVOs(bodyVO);
				for (int i = 0; i < bodyVO.length; i++) {
					BillTempletBodyVO bVO = bodyVO[i];
					if(isHeadListShow(bVO)){
						//记录当前有几行表头
						itemnum++;
						JsonItem item = new JsonItem(bVO, bVO.getCardflag());
//						if(itemnum > 3 && hide == false){
//							//超过3行时隐藏剩下的div
//							div.append("<div id=\"foldpanel\" layout=\"vbox\" valign=\"center\" width=\"fill\" height=\"wrap\"  color=\"#000000\" >");//display=\"none\"
//							hide = true;
//						}
						div.append(buildOnePanel("head.",item,flag));
						panel++;
						div.append("<div id=\"viewPage" + panel
								+ "\"  layout=\"vbox\" width=\"fill\" height=\"1\" background=\"#c7c7c7\" />");//padding-left=\"15\" margin-left=\"15\"
						
					}
				}
			}
		}
		div.append("</div>");
		//将最后一条不封顶的线隐藏
//		panel++;
//		div.replace(div.lastIndexOf("<div id=\"viewPage"), div.lastIndexOf("</div>"), "");
//		try{
//		     BufferedWriter writer = new BufferedWriter(new FileWriter(new File("c:\\Result.txt")));
//		     writer.write(div.toString());
//		     writer.close();
//		}catch(Exception e){
//
//	    }
		//如果有隐藏的标签，需要加/div和展开全部panel
		if(hide && flag.equals("addcard")){
			div.append("</div>");
			div.append("<div id=\"unfold_button_panel"
					+ "\"  layout=\"hbox\" valign=\"center\" onclick=\"unfold_panel()\" width=\"fill\" height=\"44\"  padding-right=\"15\" color=\"#000000\" >");
			panel++;
			div.append("<label id=\"label" + panel +"\" height=\"fill\" weight=\"1\" />");
			div.append("<label id=\"unfold_button"
			+ "\" hAlign=\"center\" onclick=\"unfold_panel()\" height=\"fill\" color=\"#167ef8\" font-size=\"19\" "
			+ " width=\"wrap\" margin-left=\"10\" margin-right=\"10\" font-family=\"default\" vAlign=\"center\" value = \"收起\"/>");
			div.append("<image id=\"unfold_image"
					+ "\" scaletype=\"fitcenter\" onclick=\"unfold_panel()\" src=\"arrowup.png\" height=\"8\" width=\"12\" />");
			div.append("</div>");
		}else{
			panel++;
			div.replace(div.lastIndexOf("<div id=\"viewPage"), div.lastIndexOf("</div>"), "");
		}
		//最下面封顶的那条线
		String linedown = "<div id=\"viewPage" + panel
				+ "\"  layout=\"vbox\" width=\"fill\" height=\"1\" background=\"#c7c7c7\" />";
		div.append(linedown);
		div.append("</div>");
		return div.toString();
	}
	
	//获取表头公式
	public String getheadformula(String flag,BillTempletBodyVO[] bodyVO) throws JSONException{
		if(flag.equals("addcard")){
			JSONObject formulajson = new  JSONObject();
			if (bodyVO != null) {
				initBodyVOs(bodyVO);
				for (int i = 0; i < bodyVO.length; i++) {
					BillTempletBodyVO bVO = bodyVO[i];
					if(bVO.getPos().intValue() == IBillItem.HEAD || bVO.getPos().intValue() == IBillItem.TAIL){
						JsonItem item = new JsonItem(bVO, bVO.getCardflag());
						if(item.getEditFormulas() != null){
							String[] formulars = item.getEditFormulas();
							StringBuffer formularstr = new StringBuffer();
							for(int j=0;j<formulars.length;j++){
								formularstr.append(formulars[j]+";");
							}
							formulajson.put(item.getKey(), formularstr);
						}
						
					}
				}
			}
			return formulajson.toString();
		}
		return null;
	}
	
	//根据item生成div，加横线
		private String buildOnePanel(String prefix,JsonItem item,String flag){
			StringBuffer div = new StringBuffer(); 
			panel++;
			div.append("<div id=\"viewPage" + panel
					+ "\"  layout=\"hbox\" valign=\"center\" width=\"fill\" height=\"44\"  padding-right=\"15\" color=\"#000000\" >");
			div.append("<label id=\"" + item.getKey() 
					+ "_key\" height=\"fill\" "
					+"font-size=\"16\" width=\"100\" font-family=\"default\" value=\"" +//margin-left=\"15\"
					item.getName() + "\" ");
			if(flag.equals("addcard")){
				if(item.isEdit()){
					if(item.isNull())
						div.append(" color=\"#ff0000\" ");
					else
						div.append(" color=\"#6F6F6F\" ");
				}else{
					if(item.isNull())
						div.append(" color=\"#ff0000\" ");
					else
						div.append(" color=\"#CFCFCF\" ");
				}
				div.append("/>");
				String input = translateEditItem(prefix, item);
				div.append(input);
				if(input.startsWith("<label")){
					//参照
					panel++;
					div.append("<image id=\"image" + panel
					+ "\" scaletype=\"fitcenter\" src=\"arrowbig_nc.png\" height=\"12\" width=\"8\" />");//margin-right=\"15\"
					panel++;
					div.append("<label id=\"label" + panel 
					+ "\" height=\"fill\" color=\"#6F6F6F\" "
					+"font-size=\"16\" width=\"0\" font-family=\"default\" bindfield=\"" + prefix +
					item.getKey() + "\" display=\"none\" />");
				}
			}
			else{
				div.append(" color=\"#6F6F6F\" />");
				div.append(translateShowItem(prefix, item));
			}
			div.append("</div>"); 
			return div.toString();
		}
		
	/**
	 * 初始化模板数据. 创建日期:(01-2-23 15:05:07)
	 */
	public static String getDefaultTableCode(int pos) {
		switch (pos) {
		case 0:
			return "main";
			// return BillUtil.getDefaultTableName(HEAD);
		case 1:
			// return BillUtil.getDefaultTableName(BODY);
			return "table";
		case 2:
			// return BillUtil.getDefaultTableName(TAIL);
			return "tail";
		}
		return null;
	}
	
	public static String getDefaultTableName(int pos) {
		switch (pos) {
		case 0:
			return "主表";
			// return BillData.DEFAULT_HEAD_TABBEDCODE;
		case 1:
			return "子表";

			// return BillData.DEFAULT_BODY_TABLECODE;
		case 2:
			return "主表";
			// return BillData.DEFAULT_TAIL_TABBEDCODE;
		}
		return null;
	}
	
	/**
	 * 初始化模板数据. 创建日期:(01-2-23 15:05:07)
	 */
	private void initBodyVOs(BillTempletBodyVO[] bodys) {
		if (bodys == null || bodys.length == 0)
			return;
		String code;
		int pos;
		for (int i = 0; i < bodys.length; i++) {
			if ((code = bodys[i].getTableCode()) == null
					|| code.trim().length() == 0) {
				bodys[i]
						.setTableCode(getDefaultTableCode(pos = bodys[i].getPos()
										.intValue()));
				bodys[i].setTableName(getDefaultTableName(pos));
			}
		}

		// 模板VO排序 by pos table_code showorder
//		BillUtil.sortBodyVOsByProps(bodys, new String[] { "pos", "table_code",
//				"showorder" });
	}
	
	//得到表体页签列表
	public JSONArray getTableCodes(BillTempletHeadVO headVO){
		JSONArray jsonarray = new JSONArray();
		if (headVO != null) {
			BillStructVO btVO = headVO.getStructvo();
			if(btVO == null)
			    return jsonarray;
			BillTabVO[] btvos = btVO.getBillTabVOs();
			if (btvos != null) {
				int pos;
				String tableCode;
				String tableName;
				String classname;
				for (int i = 0; i < btvos.length; i++) {
					pos = btvos[i].getPos().intValue();
					tableCode = btvos[i].getTabcode();
					tableName = btvos[i].getTabname();
					classname = btvos[i].getMetadataclass();
					if (pos == IBillItem.BODY && tableCode != null) {
						JSONObject jsonObj = new JSONObject();
						try {
							jsonObj.put("tablecode", tableCode);
							jsonObj.put("tablename", tableName);
							jsonObj.put("classname", classname);
						} catch (JSONException e) {
						}
						jsonarray.put(jsonObj);
					}
				}
			}
		}
		return jsonarray;
		
	}
	
	//得到表体动态dsl
	public String getbodydsl(BillTempletBodyVO[] bodyVO,String tablecode,String flag){
//		return this.getheaddsl(flag, bodyVO, djlxbm);
		StringBuffer div = new StringBuffer();
		div.append("<div id=\"viewPage999\"  layout=\"vbox\" width=\"fill\" height=\"wrap\">");
		div.append("<div id=\"viewPage" + panel
				+ "\"  layout=\"vbox\" width=\"fill\" height=\"wrap\" padding-left=\"15\">");
				
		if(flag.equals("addcard")){
			if (bodyVO != null) {
				initBodyVOs(bodyVO);
				for (int i = 0; i < bodyVO.length; i++) {
					BillTempletBodyVO bVO = bodyVO[i];
					if(bVO.getPos().intValue() == IBillItem.BODY && bVO.getTable_code().equals(tablecode) && bVO.getShowflag().booleanValue()==true){
								JsonItem item = new JsonItem(bVO, bVO.getCardflag());
								div.append(buildOnePanel("item.",item,flag));
								panel++;
								div.append("<div id=\"viewPage" + panel
										+ "\"  layout=\"hbox\" valign=\"center\" width=\"fill\" height=\"1\" background=\"#c7c7c7\" />");//padding-left=\"15\"
						
								//得到表体动态dsl的字段显示顺序
								if(!"amount".equals(item.getKey())){
									int dataType = item.getDataType();
									if(dataType == IBillItem.UFREF || dataType == IBillItem.USERDEF || dataType == IBillItem.COMBO){
										order.append(item.getKey() + "_name").append(",");
									}else{
										order.append(item.getKey()).append(",");
									}
								}
								
								//获取表体公式
								if(item.getEditFormulas() != null){
									String[] formulars = item.getEditFormulas();
									StringBuffer formularstr = new StringBuffer();
									for(int j=0;j<formulars.length;j++){
										formularstr.append(formulars[j]+";");
									}
									try {
										formula.put(item.getKey(), formularstr);
									} catch (JSONException e) {
									}
								}
								
								//获取表体必输项
								if(bVO.getNullflag()){
									notnull.append(item.getKey()+",");
								}
					}
				}
				if(order.length()>0)
					order.substring(0,order.length()-1).toString();
			}
		}
		else if(flag.equals("editcard")){
			if (bodyVO != null) {
				initBodyVOs(bodyVO);
				for (int i = 0; i < bodyVO.length; i++) {
					BillTempletBodyVO bVO = bodyVO[i];
					if(bVO.getPos().intValue() == IBillItem.BODY && bVO.getTable_code().equals(tablecode) && bVO.getListshowflag().booleanValue()==true){
								JsonItem item = new JsonItem(bVO, bVO.getCardflag());
								div.append(buildOnePanel("item.",item,flag));
								panel++;
								div.append("<div id=\"viewPage" + panel
										+ "\"  layout=\"hbox\" valign=\"center\" width=\"fill\" height=\"1\" padding-left=\"15\" background=\"#c7c7c7\" />");
						
					}
				}
			}
		}
		div.append("</div>");
		panel++;
		div.replace(div.lastIndexOf("<div id=\"viewPage"), div.lastIndexOf("</div>"), "");
		String linedown = "<div id=\"viewPage" + panel
				+ "\"  layout=\"vbox\" width=\"fill\" height=\"1\" background=\"#c7c7c7\" />";
		div.append(linedown);
		div.append("</div>");
		try{
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File("c:\\Result.txt")));
		     writer.write(div.toString());
		     writer.close();
		}catch(Exception e){
	
	   }
		return div.toString();
	}
		
	//得到表体动态dsl的字段显示顺序
	public String getbodyorder(BillTempletBodyVO[] bodyVO,String tablecode,String flag){
		
		if(flag.equals("addcard")){
			StringBuffer addorder = new StringBuffer();
			if (bodyVO != null) {
				for (int i = 0; i < bodyVO.length; i++) {
					BillTempletBodyVO bVO = bodyVO[i];
					if(bVO.getPos().intValue() == IBillItem.BODY && bVO.getTable_code().equals(tablecode) && bVO.getShowflag().booleanValue()==true){
						MobileBillItem item = new MobileBillItem(bVO, bVO.getCardflag());
						if(!"amount".equals(item.getKey())){
							int dataType = item.getDataType();
							if(dataType == IBillItem.UFREF || dataType == IBillItem.USERDEF || dataType == IBillItem.COMBO){
								addorder.append(item.getKey() + "_name").append(",");
							}else{
								addorder.append(item.getKey()).append(",");
							}
						}
					}
				}
			} 
			if(addorder.length()>0)
				return addorder.substring(0,addorder.length()-1).toString();
		}else if(flag.equals("editcard")){
			StringBuffer editorder = new StringBuffer();
			if (bodyVO != null) {
				for (int i = 0; i < bodyVO.length; i++) {
					BillTempletBodyVO bVO = bodyVO[i];
					if(bVO.getPos().intValue() == IBillItem.BODY && bVO.getTable_code().equals(tablecode) && bVO.getListshowflag().booleanValue()==true){
						MobileBillItem item = new MobileBillItem(bVO, bVO.getCardflag());
						if(!"amount".equals(item.getKey())){
							int dataType = item.getDataType();
							if(dataType == IBillItem.UFREF || dataType == IBillItem.USERDEF || dataType == IBillItem.COMBO){
								editorder.append(item.getKey() + "_name").append(",");
							}else{
								editorder.append(item.getKey()).append(",");
							}
						}
					}
				} 
			}
			if(editorder.length()>0){
				String orderStr = editorder.substring(0,editorder.length()-1).toString();
				return orderStr;
			}
		}
		return "";
	}
		
	//获取表体公式
	public String getbodyformula(BillTempletBodyVO[] bodyVO,String tablecode) throws JSONException{
		JSONObject formulajson = new  JSONObject();
		if (bodyVO != null) {
			initBodyVOs(bodyVO);
			for (int i = 0; i < bodyVO.length; i++) {
				BillTempletBodyVO bVO = bodyVO[i];
				if(bVO.getPos().intValue() == IBillItem.BODY && bVO.getTable_code().equals(tablecode) && bVO.getShowflag().booleanValue()==true){
							JsonItem item = new JsonItem(bVO, bVO.getCardflag());
							if(item.getEditFormulas() != null){
								String[] formulars = item.getEditFormulas();
								StringBuffer formularstr = new StringBuffer();
								for(int j=0;j<formulars.length;j++){
									formularstr.append(formulars[j]+";");
								}
								formulajson.put(item.getKey(), formularstr);
							}
					
				}
			}
		}
		return formulajson.toString();
	}
	
	//获取表体必输项
	public String getbodynotnull(BillTempletBodyVO[] bodyVO,String tablecode) throws JSONException{
		StringBuffer notnullstr = new StringBuffer();
		if (bodyVO != null) {
			initBodyVOs(bodyVO);

			for (int i = 0; i < bodyVO.length; i++) {
				BillTempletBodyVO bVO = bodyVO[i];
				if(bVO.getPos().intValue() == IBillItem.BODY && bVO.getTable_code().equals(tablecode) && bVO.getShowflag().booleanValue()==true ){
							JsonItem item = new JsonItem(bVO, bVO.getNullflag());
							if(bVO.getNullflag()){
								notnullstr.append(item.getKey()+",");
							}
					
				}
			}
		}
		return notnullstr.toString();
	}
	
	//查看字段dsl生成
	public String translateShowItem(String prefix,JsonItem item){
		StringBuffer input = new StringBuffer();
		int dataType = item.getDataType();
		if("zy".equals(item.getKey()) || "zy2".equals(item.getKey())){
			dataType = IBillItem.STRING ;
		}
		switch (dataType) {
			case IBillItem.STRING:
				input.append("<label id=\"" + item.getKey() 
						+ "\"  height=\"44\"  color=\"#000000\" halign=\"right\" "
						+ "font-size=\"16\" width=\"fill\" font-family=\"default\" ");
				input.append(" bindfield=\"" + prefix + item.getKey() + "_name\"");
				//字符串直接赋值
//				if(item.getDefaultValue()!=null && !item.getDefaultValue().equals(""))
//					input.append(" value=\"" + item.getDefaultValue() + "\"");
				input.append("/>"); 
				break;
			case IBillItem.DATE:
			case IBillItem.DATETIME:
				input.append("<label id=\"" + item.getKey() 
						+ "\"  height=\"44\"  color=\"#000000\" halign=\"right\" "
						+ "font-size=\"16\" width=\"fill\" font-family=\"default\" "
						+ " bindfield=\"" + prefix + item.getKey() + "_name\"");
				//日期直接赋值
//				if(item.getDefaultValue()!=null && !item.getDefaultValue().equals(""))
//					input.append(" value=\"" + item.getDefaultValue() + "\"");
				input.append("/>"); 
				break;
			case IBillItem.DECIMAL:
			case IBillItem.MONEY: 
				input.append("<label id=\"" + item.getKey() 
						+ "\"  height=\"44\"  color=\"#000000\" halign=\"right\" "
						+ "font-size=\"16\" width=\"fill\" font-family=\"default\" ");
				input.append(" bindfield=\"" + prefix + item.getKey() + "_name\"");
				//金额直接赋值
//				if(item.getDefaultValue()!=null && !item.getDefaultValue().equals(""))
//					input.append(" value=\"" + item.getDefaultValue() + "\"");
				input.append("/>"); 
				break;
			case IBillItem.INTEGER:
				input.append("<label id=\"" + item.getKey() 
						+ "\"  height=\"44\"  color=\"#000000\" halign=\"right\" "
						+ "font-size=\"16\" width=\"fill\" font-family=\"default\" ");
				input.append(" bindfield=\"" + prefix + item.getKey() + "_name\"");
				//金额直接赋值
//				if(item.getDefaultValue()!=null && !item.getDefaultValue().equals(""))
//					input.append(" value=\"" + item.getDefaultValue() + "\"");
				input.append("/>"); 
				break;
			default:
				//参照或下拉
				input.append("<label id=\"" + item.getKey() 
						+ "\"  height=\"44\"  color=\"#000000\" halign=\"right\" "
						+ "font-size=\"16\" width=\"fill\" font-family=\"default\" ");
				input.append(" bindfield=\"" + prefix + item.getKey() + "_name\"");
				//参照赋默认值
//				if(item.getDefaultValue()!=null && !item.getDefaultValue().equals(""))
//					input.append(" value=\"" + item.getDefaultValue() + "\"");
				input.append("/>"); 
				break;  
		}
		return input.toString();
	}  
	
	//编辑字段dsl生成
	public String translateEditItem(String prefix,JsonItem item){
		StringBuffer input = new StringBuffer();
		int dataType = item.getDataType();
		if("zy".equals(item.getKey()) || "zy2".equals(item.getKey())){
			dataType = IBillItem.STRING ;
		}
		switch (dataType) {
			case IBillItem.STRING:
				input.append("<input id=\"" + item.getKey() 
				+ "\" maxlength=\"256\" placeholder=\"可空\" type=\"text\""
				+ " height=\"44\"  color=\"#000000\" onchange=\"onInputChange()\" onfocus=\"onfocus()\""//onfocus-key=\"
				+ " font-size=\"16\" width=\"fill\" font-family=\"default\" ");//padding-left=\"12\"
				if(!item.isEdit())
					input.append(" readonly=\"true\"");
				input.append(" bindfield=\"" + prefix + item.getKey() + "\"");
				//字符串直接赋值
//				if(item.getDefaultValue()!=null && !item.getDefaultValue().equals(""))
//					input.append(" value=\"" + item.getDefaultValue() + "\"");
				input.append("/>"); 
				break;
			case IBillItem.DATE:
			case IBillItem.DATETIME:
				input.append("<input id=\"" + item.getKey() 
				+ "\" format=\"yyyy-MM-dd\" placeholder=\"可空\" type=\"date\""
				+ " height=\"44\" color=\"#000000\" onchange=\"onInputChange()\" onfocus=\"onfocus()\""
				+ " font-size=\"16\" width=\"fill\" font-family=\"default\" "
				+ " bindfield=\"" + prefix + item.getKey() + "\"");
				if(!item.isEdit())
					input.append(" readonly=\"true\"");
				//日期直接赋值
//				if(item.getDefaultValue()!=null && !item.getDefaultValue().equals(""))
//					input.append(" value=\"" + item.getDefaultValue() + "\"");
				input.append("/>"); 
				break;
			case IBillItem.DECIMAL:
			case IBillItem.MONEY: 
				input.append( "<input id=\"" + item.getKey() 
				+ "\" min=\"-9.99999999E8\" precision=\"2\" max=\"9.99999999E8\" roundValue=\"5\" type=\"number\" roundType=\"value\" "
				+ " height=\"44\" color=\"#000000\" background=\"#ffffff\" onchange=\"onInputChange()\" onfocus=\"onfocus()\""
				+ " font-size=\"16\" width=\"fill\" padding-left=\"12\" font-family=\"default\" halign=\"LEFT\" ");
				if(!item.isEdit())
					input.append(" readonly=\"true\"");
				input.append(" bindfield=\"" + prefix + item.getKey() + "\"");
				//金额直接赋值
//				if(item.getDefaultValue()!=null && !item.getDefaultValue().equals(""))
//					input.append(" value=\"" + item.getDefaultValue() + "\"");
				input.append("/>"); 
				break;
			case IBillItem.INTEGER:
				input.append( "<input id=\"" + item.getKey() 
				+ "\" min=\"-9.99999999E8\" max=\"9.99999999E8\" roundValue=\"5\" type=\"number\" roundType=\"value\" "
				+ " height=\"44\" color=\"#000000\" background=\"#ffffff\" onchange=\"onInputChange()\" onfocus=\"onfocus()\""
				+ " font-size=\"16\" width=\"fill\" padding-left=\"12\" font-family=\"default\" halign=\"LEFT\" ");
				input.append(" bindfield=\"" + prefix + item.getKey() + "\"");
				if(!item.isEdit())
					input.append(" readonly=\"true\"");
				//金额直接赋值
//				if(item.getDefaultValue()!=null && !item.getDefaultValue().equals(""))
//					input.append(" value=\"" + item.getDefaultValue() + "\"");
				input.append("/>"); 
				break;
			case IBillItem.BOOLEAN:
				input.append( "<input id=\"" + item.getKey() 
				+ "\" type=\"checkbox\" check-on-image=\"checkbox_select\" check-off-image=\"checkbox_noselect\" "
				+ " height=\"22\" color=\"#000000\"  onchange=\"onInputChange()\" onfocus=\"onfocus()\""
				+ " font-size=\"16\" width=\"22\" padding-left=\"12\" font-family=\"default\" halign=\"LEFT\" ");
				input.append(" bindfield=\"" + prefix + item.getKey() + "\"");
//				input.append(" value=\"false\"");
				if(item.isEdit() == false)
					input.append(" readonly=\"true\"");
				//金额直接赋值
//				if(item.getDefaultValue()!=null && !item.getDefaultValue().equals(""))
//					input.append(" value=\"" + item.getDefaultValue() + "\"");
				input.append("/>"); 
				break;
			default:
				String reftype = item.getRefType();
				//下拉类型和参照类型分别需要对reftype做处理
				if(dataType == IBillItem.COMBO){
//					JSONObject jsonObj = new JSONObject();
					boolean isFromMeta = reftype
			                   .startsWith(MetaDataPropertyAdpter.COMBOBOXMETADATATOKEN);
					String reftype1 = reftype.replaceFirst(MetaDataPropertyAdpter.COMBOBOXMETADATATOKEN,"");
					List<DefaultConstEnum> combodata = ComboBoxUtil.getInitData(reftype1, isFromMeta);
					StringBuffer typestr = new StringBuffer();
					for(int i=0;i<combodata.size();i++){
						DefaultConstEnum enumvalue = combodata.get(i);
						if(i != 0)
							typestr.append(",");
						typestr.append(enumvalue.getName()).append("=").append(enumvalue.getValue());
					}
					reftype = "COMBO," + item.getName() + ":" +typestr.toString();	
				}else{
					if(item.getRefType() == null)
						reftype = "UFREF,没有参照";
					else if(item.getRefType()!=null && item.getRefType().contains(","))
						reftype = "UFREF," + item.getRefType().split(",")[0];
				}
				input.append("<label id=\"" + item.getKey() //open_reflist
				+ "\" height=\"44\" color=\"#000000\" "
				+"font-size=\"16\" weight=\"1\" padding-left=\"12\" onclick =\"openReference()\" font-family=\"default\" " 
		 		+ " onclick-reftype=\"" + reftype
				+ "\" onclick-mapping=\"{'" + prefix + item.getKey() + "':'pk_ref','" + prefix + item.getKey() + "_name':'refname'}\"");
				input.append(" bindfield=\"" + prefix + item.getKey() + "_name\"");
				if(!item.isEdit())
					input.append(" disabled=\"disabled\"");
				//参照赋默认值
//				if(item.getDefaultValue()!=null && !item.getDefaultValue().equals(""))
//					input.append(" value=\"" + item.getDefaultValue() + "\"");
				input.append("/>"); 
				break;  
		}
		return input.toString();
	}  
}
