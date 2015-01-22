package nc.arap.mobile.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.erm.util.ErmDjlxCache;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.framework.exception.ComponentException;
import nc.bs.logging.Logger;
import nc.erm.mobile.billaction.JKBXBillSaveAction;
import nc.erm.mobile.pub.template.MobileTemplateUtils;
import nc.erm.mobile.util.RefUtil;
import nc.erm.mobile.util.TranslateJsonToValueObject;
import nc.erm.mobile.util.TranslateValueObjectToJson;
import nc.erm.mobile.view.ComboBoxUtil;
import nc.erm.mobile.view.MobileBillItem;
import nc.itf.arap.prv.IBXBillPrivate;
import nc.itf.arap.pub.IErmBillUIPublic;
import nc.itf.uap.billtemplate.IBillTemplateQry;
import nc.jdbc.framework.processor.ResultSetProcessor;
import nc.pubitf.erm.matterapp.IErmMatterAppBillQuery;
import nc.ui.pub.beans.constenum.DefaultConstEnum;
import nc.ui.pub.bill.IBillItem;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.BXHeaderVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.bill.BillStructVO;
import nc.vo.pub.bill.BillTabVO;
import nc.vo.pub.bill.BillTempletBodyVO;
import nc.vo.pub.bill.BillTempletHeadVO;
import nc.vo.pub.bill.BillTempletVO;
import nc.vo.pub.bill.MetaDataPropertyAdpter;
import nc.vo.pub.templet.translator.BillTranslator;

import org.codehaus.jettison.json.JSONException;

import uap.json.JSONArray;
import uap.json.JSONObject;
public class ErmMobileDefCtrlBO extends AbstractErmMobileCtrlBO{
	private static int panel=9990;
	
	public String addJkbx(Map<String, Object> map, String djlxbm,String userid) throws BusinessException{
		initEvn(userid);
		try{
			String billpk = null;
			TranslateJsonToValueObject trans = new TranslateJsonToValueObject();
			BillTempletVO billTempletVO =  getDefaultTempletStatics(djlxbm); 
			AggregatedValueObject vo = trans.translateMapToAggvo(billTempletVO, map);
			JKBXBillSaveAction saveaction = new JKBXBillSaveAction(PK_ORG);
			if(map.get("pk_jkbx") != null && !"".equals(map.get("pk_jkbx"))){
				// 主键已生成，修改
				billpk = saveaction.updateJkbx(vo,userid); 
				// 附件先删后插
				deleteAttachmentList(billpk, userid); 
				saveAttachment(billpk, map);
			}else{
				//第一次保存
				billpk = saveaction.insertJkbx(vo,djlxbm,userid);
				//保存附件
				saveAttachment(billpk,map);
			}
			return "pk_jkbx"+billpk;
		}catch(BusinessException e){
			String msg = e.getMessage();
			return msg; 
		}
	}
    
    /**
     * 加载默认模板. 创建日期:(01-3-6 11:18:13)
     * 
     * @param strBillType
     *            java.lang.String
     * @throws BusinessException 
     * @throws ComponentException 
     */
    private BillTempletVO getDefaultTempletStatics(String djlxbm) throws BusinessException {
            //查询版本且返回更新数据
    	BillTempletVO cardListVO = findBillTempletDatas(djlxbm);
	        //cacheBillTempletVO(cardListVO, ceKeys[i]);
        if (cardListVO != null ) {
            	BillTranslator.translate(cardListVO);
            	Logger.info("模板加载成功!");
            	return cardListVO; 
        }else{
            throw new BusinessException("未找到可用模板!");
        }
    }
    
	public BillTempletVO findBillTempletDatas(String djlxbm)
		throws BusinessException, ComponentException {
		String pk_billtemplet = MobileTemplateUtils.getTemplatePK(djlxbm);
		if(pk_billtemplet == null){
			return null;
		}
		String pk_group = InvocationInfoProxy.getInstance().getGroupId();
			return getIBillTemplateQry().findTempletData(pk_billtemplet,pk_group);
	}
    private IBillTemplateQry iBillTemplateQry = null;
    private IBillTemplateQry getIBillTemplateQry()
    		throws ComponentException {
		if (iBillTemplateQry == null)
		    iBillTemplateQry = (IBillTemplateQry) NCLocator.getInstance().lookup(IBillTemplateQry.class.getName());
		return iBillTemplateQry;
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
	
	//拿到单据context
	public String getBxdTemplate(String userid,
			String djlxbm, String nodecode,String flag) throws BusinessException {
		panel = 9990;
		initEvn(userid);
		JSONObject jsonObj = new JSONObject();

        BillTempletVO billTempletVO =  getDefaultTempletStatics(djlxbm ); 
        billTempletVO.setParentToBody();
    	//编辑界面点击添加按钮时需要表体页签列表
        //表体页签列表
        JSONArray tablist = getTableCodes(billTempletVO.getHeadVO());
        jsonObj.put("tablist", tablist);
        //表头要加载字段，编辑项
		BillTempletBodyVO[] bodyVO = billTempletVO.getBodyVO();
		jsonObj.put("dsl", getheaddsl(flag,bodyVO,djlxbm));
        jsonObj.put("ts", billTempletVO.getHeadVO().getTs().toString());
		//显示界面需要加载表头和表体，因为要根据表体的dsl的reftype确定pk所对应的名称
//        if(flag.equals("editcard")){
//        	for(int i=0;i<tablist.length();i++){
//        		JSONObject item = (JSONObject) tablist.get(i);
//        		String tablecode = item.getString("tablecode");
//        		jsonObj.put(tablecode+"dsl", getbodydsl(bodyVO,tablecode,flag,djlxbm));
//        	}
//        }
		return jsonObj.toString();
	}
	
	//得到表体页签列表
	private JSONArray getTableCodes(BillTempletHeadVO headVO){
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
				for (int i = 0; i < btvos.length; i++) {
					pos = btvos[i].getPos().intValue();
					tableCode = btvos[i].getTabcode();
					tableName = btvos[i].getTabname();
					if (pos == IBillItem.BODY && tableCode != null) {
						JSONObject jsonObj = new JSONObject();
						jsonObj.put("tablecode", tableCode);
						jsonObj.put("tablename", tableName);
						jsonarray.put(jsonObj);
					}
				}
			}
		}
		return jsonarray;
		
	}
	
//	private static Map<String,Map<String,String>> templetCache = new HashMap<String,Map<String,String>>();
	private String getheaddsl(String flag,BillTempletBodyVO[] bodyVO,String djlxbm){
		StringBuffer div = new StringBuffer();
		div.append("<div id=\"viewPage99\"  layout=\"vbox\" width=\"fill\" height=\"wrap\">");
		div.append("<div id=\"viewPage" + panel
				+ "\"  layout=\"vbox\" width=\"fill\" height=\"wrap\" padding-left=\"15\">");
		
		if(flag.equals("addcard")){
			if (bodyVO != null) {
				initBodyVOs(bodyVO);
				for (int i = 0; i < bodyVO.length; i++) {
					BillTempletBodyVO bVO = bodyVO[i];
					if(bVO.getPos().intValue() == IBillItem.HEAD && bVO.getShowflag().booleanValue()==true){
								MobileBillItem item = new MobileBillItem(bVO, bVO.getCardflag());
								if(item.getKey().equals(BXHeaderVO.DJBH))
									continue;
								div.append(builddsl("head.",item,flag));
								panel++;
								div.append("<div id=\"viewPage" + panel
										+ "\"  layout=\"vbox\" width=\"fill\" height=\"1\" background=\"#c7c7c7\" />");//margin-left=\"15\"
						
					}
				}
			}
		}
		else if(flag.equals("editcard")){
			Map<String,String> head = new HashMap<String,String>();
			head.put(JKBXHeaderVO.PK_JKBX,null);
			head.put(JKBXHeaderVO.DJLXBM,null);
			head.put(JKBXHeaderVO.DJDL,null);
			head.put(JKBXHeaderVO.TOTAL,null);
			head.put(JKBXHeaderVO.SPZT,null);
			head.put(JKBXHeaderVO.DJBH,null);
			if (bodyVO != null) {
				initBodyVOs(bodyVO);
				for (int i = 0; i < bodyVO.length; i++) {
					BillTempletBodyVO bVO = bodyVO[i];
					if(bVO.getPos().intValue() == IBillItem.HEAD && bVO.getListshowflag().booleanValue()==true){
								MobileBillItem item = new MobileBillItem(bVO, bVO.getCardflag());
								int dataType = item.getDataType();
								if(dataType == IBillItem.UFREF || dataType == IBillItem.USERDEF){
									head.put(item.getKey(), "UFREF,"+item.getRefType());
								}else if(dataType == IBillItem.COMBO){
									head.put(item.getKey(), "COMBO,"+item.getRefType());
								}else{
									head.put(item.getKey(), null);
								}
								div.append(builddsl("head.",item,flag));
								panel++;
								div.append("<div id=\"viewPage" + panel
										+ "\"  layout=\"vbox\" width=\"fill\" height=\"1\" background=\"#c7c7c7\" />");//padding-left=\"15\" margin-left=\"15\"
						
					}
				}
			}
//			templetCache.put(InvocationInfoProxy.getInstance().getGroupId() + djlxbm + "head", head);
		}
		div.append("</div>");
		panel++;
		div.replace(div.lastIndexOf("<div id=\"viewPage"), div.lastIndexOf("</div>"), "");
		String linedown = "<div id=\"viewPage" + panel
				+ "\"  layout=\"vbox\" width=\"fill\" height=\"1\" background=\"#c7c7c7\" />";
		div.append(linedown);
		div.append("</div>");
		return div.toString();
	}
	
	//根据item生成div
	private String builddsl(String prefix,MobileBillItem item,String flag){
		StringBuffer div = new StringBuffer(); 
		panel++;
		div.append("<div id=\"viewPage" + panel
				+ "\"  layout=\"hbox\" valign=\"center\" width=\"fill\" height=\"44\"  padding-right=\"15\" color=\"#000000\" >");
		div.append("<label id=\"label" + panel 
			+ "\" height=\"fill\" color=\"#6F6F6F\" "
			+"font-size=\"16\" width=\"100\" font-family=\"default\" value=\"" +//margin-left=\"15\"
			item.getName() + "\" />");
		String input = translateItem(prefix,item,flag);
		div.append(input);
		if(flag.equals("addcard") && input.startsWith("<label")){
			panel++;
			div.append("<image id=\"image" + panel
			+ "\" scaletype=\"fitcenter\" src=\"arrowbig_nc.png\" height=\"12\" width=\"8\" />");//margin-right=\"15\"
			panel++;
			div.append("<label id=\"label" + panel 
			+ "\" height=\"fill\" color=\"#6F6F6F\" "
			+"font-size=\"16\" width=\"0\" font-family=\"default\" bindfield=\"head." +
			item.getKey() + "\" display=\"none\" />");
		}
		div.append("</div>"); 
		return div.toString();
	}
	private String translateItem(String prefix,MobileBillItem item,String flag){
		StringBuffer input = new StringBuffer();
		int dataType = item.getDataType();
		if("zy".equals(item.getKey()) || "zy2".equals(item.getKey())){
			dataType = IBillItem.STRING ;
		}
		switch (dataType) {
			case IBillItem.STRING:
				panel++;
				if(flag.equals("addcard"))
					input.append("<input id=\"textbox" + panel 
					+ "\" maxlength=\"256\" placeholder=\"可空\" type=\"text\""
					+ " height=\"44\"  color=\"#000000\" "
					+ "font-size=\"16\" width=\"fill\" font-family=\"default\" ");//padding-left=\"12\"
				else if(flag.equals("editcard"))
//					input.append("<label id=\"label" + panel 
//							+ "\" height=\"44\"  color=\"#000000\" halign=\"right\" "
//							+ "font-size=\"16\" width=\"fill\" padding-right=\"15\" font-family=\"default\" ");
					input.append("<input id=\"textbox" + panel 
							+ "\" readonly =\"true\" maxlength=\"256\" type=\"text\" height=\"44\"  color=\"#000000\" halign=\"right\" "
							+ "font-size=\"16\" width=\"fill\" font-family=\"default\" ");
				input.append(" bindfield=\"" + prefix + item.getKey() + "\"");
				//字符串直接赋值
				if(item.getDefaultValue()!=null && !item.getDefaultValue().equals(""))
					input.append(" value=\"" + item.getDefaultValue() + "\"");
				input.append("/>"); 
				break;
			case IBillItem.DATE:
			case IBillItem.DATETIME:
				panel++;
				if(flag.equals("addcard"))
					input.append("<input id=\"dateinput" + panel 
					+ "\" format=\"yyyy-MM-dd\" placeholder=\"可空\" type=\"date\""
					+ " height=\"44\" weight=\"1\" color=\"#000000\" "
					+ " font-size=\"16\" width=\"fill\" font-family=\"default\" "
					+ " bindfield=\"" + prefix + item.getKey() + "\"");
				else if(flag.equals("editcard"))
					input.append("<input id=\"dateinput" + panel 
							+ "\" readonly =\"true\" format=\"yyyy-MM-dd\" type=\"date\" height=\"44\"  color=\"#000000\" halign=\"right\" "
							+ "font-size=\"16\" width=\"fill\" font-family=\"default\" "
							+ " bindfield=\"" + prefix + item.getKey() + "_name\"");
				//日期直接赋值
				if(item.getDefaultValue()!=null && !item.getDefaultValue().equals(""))
					input.append(" value=\"" + item.getDefaultValue() + "\"");
				input.append("/>"); 
				break;
			case IBillItem.DECIMAL:
			case IBillItem.MONEY: 
				panel++;
				if(flag.equals("addcard"))
					input.append( "<input id=\"number" + panel 
					+ "\" min=\"-9.99999999E8\" precision=\"2\" max=\"9.99999999E8\" roundValue=\"5\" type=\"number\" roundType=\"value\" "
					+ " height=\"44\" color=\"#000000\" background=\"#ffffff\" "
					+ "font-size=\"16\" width=\"fill\" padding-left=\"12\" font-family=\"default\" halign=\"LEFT\" ");
				else if(flag.equals("editcard"))
//					input.append("<input id=\"number" + panel 
//							+ "\" readonly =\"true\" min=\"-9.99999999E8\" precision=\"2\" max=\"9.99999999E8\" roundValue=\"5\" " 
//							+ "type=\"number\" roundType=\"value\" height=\"44\"  color=\"#000000\" background=\"#ffffff\" halign=\"right\" "
//							+ "font-size=\"16\" width=\"fill\" padding-right=\"15\" font-family=\"default\" ");
					input.append("<input id=\"textbox" + panel 
							+ "\" readonly =\"true\" maxlength=\"256\" type=\"text\" height=\"44\"  color=\"#000000\" halign=\"right\" "
							+ "font-size=\"16\" width=\"fill\" font-family=\"default\" ");
				input.append(" bindfield=\"" + prefix + item.getKey() + "\"");
				//金额直接赋值
				if(item.getDefaultValue()!=null && !item.getDefaultValue().equals(""))
					input.append(" value=\"" + item.getDefaultValue() + "\"");
				input.append("/>"); 
				break;
			case IBillItem.INTEGER:
				panel++;
				if(flag.equals("addcard"))
					input.append( "<input id=\"number" + panel 
					+ "\" min=\"-9.99999999E8\" max=\"9.99999999E8\" roundValue=\"5\" type=\"number\" roundType=\"value\" "
					+ " height=\"44\" color=\"#000000\" background=\"#ffffff\" "
					+ "font-size=\"16\" width=\"fill\" padding-left=\"12\" font-family=\"default\" halign=\"LEFT\" ");
				else if(flag.equals("editcard"))
//					input.append("<input id=\"number" + panel 
//							+ "\" readonly =\"true\" min=\"-9.99999999E8\" precision=\"2\" max=\"9.99999999E8\" roundValue=\"5\" " 
//							+ "type=\"number\" roundType=\"value\" height=\"44\"  color=\"#000000\" background=\"#ffffff\" halign=\"right\" "
//							+ "font-size=\"16\" width=\"fill\" padding-right=\"15\" font-family=\"default\" ");
					input.append("<input id=\"textbox" + panel 
							+ "\" readonly =\"true\" maxlength=\"256\" type=\"text\" height=\"44\"  color=\"#000000\" halign=\"right\" "
							+ "font-size=\"16\" width=\"fill\" font-family=\"default\" ");
				input.append(" bindfield=\"" + prefix + item.getKey() + "\"");
				//金额直接赋值
				if(item.getDefaultValue()!=null && !item.getDefaultValue().equals(""))
					input.append(" value=\"" + item.getDefaultValue() + "\"");
				input.append("/>"); 
				break;
			default:
				panel++;
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
				if(flag.equals("addcard"))
					input.append("<label id=\"label" + panel 
					+ "\" height=\"44\" color=\"#000000\" "
					+"font-size=\"16\" weight=\"1\" padding-left=\"12\" onclick =\"open_reflist\" font-family=\"default\" " 
			 		+ " onclick-reftype=\"" + reftype
					+ "\" onclick-mapping=\"{'" + prefix + item.getKey() + "':'pk_ref','" + prefix + item.getKey() + "_name':'refname'}\"");
				else if(flag.equals("editcard"))
//					input.append("<label id=\"label" + panel
//							+ "\" height=\"44\"  color=\"#000000\" halign=\"right\" "
//							+ "font-size=\"16\" width=\"fill\" padding-right=\"15\" font-family=\"default\" ");
					input.append("<input id=\"textbox" + panel 
							+ "\" readonly =\"true\" maxlength=\"256\" type=\"text\" height=\"44\"  color=\"#000000\" halign=\"right\" "
							+ "font-size=\"16\" width=\"fill\" font-family=\"default\" ");
				input.append(" bindfield=\"" + prefix + item.getKey() + "_name\"");
				//参照赋默认值
				if(item.getDefaultValue()!=null && !item.getDefaultValue().equals(""))
					input.append(" value=\"" + item.getDefaultValue() + "\"");
				input.append("/>"); 
				break;  
		}
		return input.toString();
	}  

	public String getRefList(String userid, String reftype,Map<String, Object> map) throws BusinessException {
		initEvn(userid);
		return RefUtil.getRefList(userid, reftype, map);
	}
	
	//得到表体动态dsl
	private String getbodydsl(BillTempletBodyVO[] bodyVO,String tablecode,String flag,String djlxbm){
		StringBuffer div = new StringBuffer();
//		String[] fieldset ={"hbbm","amount","defitem1","defitem2","defitem3","szxmid"};
		div.append("<div id=\"viewPage999\"  layout=\"vbox\" width=\"fill\" height=\"wrap\">");
		div.append("<div id=\"viewPage" + panel
				+ "\"  layout=\"vbox\" width=\"fill\" height=\"wrap\" padding-left=\"15\">");
				
		if(flag.equals("addcard")){
			if (bodyVO != null) {
				initBodyVOs(bodyVO);
				for (int i = 0; i < bodyVO.length; i++) {
					BillTempletBodyVO bVO = bodyVO[i];
					if(bVO.getPos().intValue() == IBillItem.BODY && bVO.getTable_code().equals(tablecode) && bVO.getShowflag().booleanValue()==true){
	//					for(int j=0;j<fieldset.length;j++){
	//						if(fieldset[j].equals(bVO.getItemkey())){
								MobileBillItem item = new MobileBillItem(bVO, bVO.getCardflag());
								div.append(builddsl("item.",item,flag));
								panel++;
								div.append("<div id=\"viewPage" + panel
										+ "\"  layout=\"hbox\" valign=\"center\" width=\"fill\" height=\"1\" background=\"#c7c7c7\" />");//padding-left=\"15\"
						
	//						}
	//					}
					}
				}
			}
		}
		else if(flag.equals("editcard")){
			Map<String,String> body = new HashMap<String,String>();
			body.put(BXBusItemVO.AMOUNT,null);
			body.put(BXBusItemVO.TABLECODE,null);
			if (bodyVO != null) {
				initBodyVOs(bodyVO);
				for (int i = 0; i < bodyVO.length; i++) {
					BillTempletBodyVO bVO = bodyVO[i];
					if(bVO.getPos().intValue() == IBillItem.BODY && bVO.getTable_code().equals(tablecode) && bVO.getListshowflag().booleanValue()==true){
								MobileBillItem item = new MobileBillItem(bVO, bVO.getCardflag());
								int dataType = item.getDataType();
								if(dataType == IBillItem.UFREF || dataType == IBillItem.USERDEF){
									body.put(item.getKey(),"UFREF,"+item.getRefType());
								}else if(dataType == IBillItem.COMBO){
									body.put(item.getKey(), "COMBO,"+item.getRefType());
								}else{
									body.put(item.getKey(), null);
								}
								div.append(builddsl("item.",item,flag));
								panel++;
								div.append("<div id=\"viewPage" + panel
										+ "\"  layout=\"hbox\" valign=\"center\" width=\"fill\" height=\"1\" padding-left=\"15\" background=\"#c7c7c7\" />");
						
					}
				}
			}
//			templetCache.put(InvocationInfoProxy.getInstance().getGroupId() + djlxbm + tablecode + "body", body);
		}
		div.append("</div>");
		panel++;
		div.replace(div.lastIndexOf("<div id=\"viewPage"), div.lastIndexOf("</div>"), "");
		String linedown = "<div id=\"viewPage" + panel
				+ "\"  layout=\"vbox\" width=\"fill\" height=\"1\" background=\"#c7c7c7\" />";
		div.append(linedown);
		div.append("</div>");
		return div.toString();
	}
	
	//得到表体动态dsl的字段显示顺序
	private String getbodyorder(BillTempletBodyVO[] bodyVO,String tablecode,String flag,String djlxbm){
		
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
	
	public String getItemDslFile(String userid, String djlxbm, String nodecode,
			String tablecode, String flag) throws BusinessException {
		initEvn(userid);
        BillTempletVO billTempletVO =  getDefaultTempletStatics(djlxbm);
        billTempletVO.setParentToBody();
		BillTempletBodyVO[] bodyVO = billTempletVO.getBodyVO();
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("dsl", getbodydsl(bodyVO,tablecode,flag,djlxbm));
		jsonObj.put(tablecode + "_order", getbodyorder(bodyVO,tablecode,flag,djlxbm));
		jsonObj.put("ts", "\""+billTempletVO.getHeadVO().getTs().toString()+"\"");
		return jsonObj.toString();
	}
	 private class ListResultSetProcessor
     implements ResultSetProcessor
     {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public Object handleResultSet(ResultSet rs) throws SQLException {
			List<String> topPks = new ArrayList<String>();
			while(rs.next()){
				topPks.add(rs.getString(1));
			}
	        return topPks;
	     }
     }

	public String validateTs(String userid, String djlxbm, String nodecode,
			String tsflag) throws BusinessException {
		initEvn(userid);
		if(tsflag.equals("head")){
			return MobileTemplateUtils.getTemplateTS(djlxbm);
		}
		else
			return null;
	}
	
	
	public String getJkbxCard(String pk_jkbx,String userid,String djlxbm,String djlxmc,String getbillflag) throws BusinessException {
		initEvn(userid);
		org.codehaus.jettison.json.JSONObject retJson = new org.codehaus.jettison.json.JSONObject();
		AggregatedValueObject billvo = null;
		if(StringUtil.isEmpty(pk_jkbx)){
			//新增单据  带出默认值
			String pk_group = InvocationInfoProxy.getInstance().getGroupId();
			DjLXVO djlxVO = ErmDjlxCache.getInstance().getDjlxVO(pk_group, djlxbm);
			// 初始化表头数据
			if(djlxbm.startsWith("263") || djlxbm.startsWith("264")){
				//借款报销单初始化
				IErmBillUIPublic initservice = NCLocator.getInstance().lookup(IErmBillUIPublic.class);
				billvo = initservice.setBillVOtoUI(djlxVO, "", null);
			}else if(djlxbm.startsWith("261")){
				//费用申请单初始化
				IErmMatterAppBillQuery initservice = NCLocator.getInstance().lookup(IErmMatterAppBillQuery.class);
				billvo = initservice.getAddInitAggMatterVo(djlxVO, "", null);
			}
		}else{
			List<JKBXVO> vos = NCLocator.getInstance()
					.lookup(IBXBillPrivate.class).queryVOsByPrimaryKeysForNewNode(
							new String[]{pk_jkbx}, null,false,null);
			billvo = vos.get(0);
		}
		BillTempletVO billTempletVO =  getDefaultTempletStatics(djlxbm);
		TranslateValueObjectToJson trans = new TranslateValueObjectToJson();
		try {
			retJson = trans.transValueObjectToJSON(billTempletVO,billvo);
//			retJson.put("djbh", jkbxvo.getParentVO().getDjbh());
			retJson.put("total", ((org.codehaus.jettison.json.JSONObject)retJson.get("head")).get("total"));
			retJson.put("total_name", ((org.codehaus.jettison.json.JSONObject)retJson.get("head")).get("total_name"));
			retJson.put("pk_jkbx", billvo.getParentVO().getPrimaryKey());
			retJson.put("djlxbm", djlxbm);
			retJson.put("djlxmc", djlxmc);
			retJson.put("userid", userid);
		} catch (Exception e) {
			Logger.debug(e.getMessage());
		}
		
		// 获取附件个数
		try {
			String fileNum = getFileNum(pk_jkbx, userid);
			if(fileNum != null){
				retJson.put("attachnum", fileNum);
			}else{
				retJson.put("attachnum", "0");
			}
		} catch (JSONException e) {
			Logger.debug(e.getMessage());
		}
		return retJson.toString();
	}

	public String getAttachFile(String pk_jkbx, String userid) throws BusinessException {
		org.codehaus.jettison.json.JSONObject retJson = new org.codehaus.jettison.json.JSONObject();
		// 获取附件列表
		try {
			org.codehaus.jettison.json.JSONArray attatchmapList = getFileList(pk_jkbx, userid);
			retJson.put("contentlist", attatchmapList);
		} catch (JSONException e) {
			Logger.debug(e.getMessage());
		}
		return retJson.toString();
	}
	
//	private static Map<String,Map<String,String>> refPkName = new HashMap<String,Map<String,String>>();
//	private String resetRefName(String key,String refval,String reftype){
//		if(key.endsWith("_name")){
//			return null;
//		}
//		if(reftype.startsWith("UFREF,")){
//			reftype = reftype.substring(6);
//			if(reftype != null && reftype.contains(","))
//				reftype = reftype.split(",")[0];
//			if (RefPubUtil.isSpecialRef(reftype)) {
//				return null;
//			}
//			if(refPkName.get(reftype) == null){
//				AbstractRefModel refModel = RefPubUtil.getRefModel(reftype);
//				String pkFieldCode = refModel.getPkFieldCode();
//				RefcolumnVO[] RefcolumnVOs = RefPubUtil.getColumnSequences(refModel);
//				Vector vDataAll = refModel.getRefData();
//				Map<String,String> map = new HashMap<String,String>();
//				for(int i=0;i<vDataAll.size();i++){
//					Vector aa = (Vector) vDataAll.get(i);
//					String pk = null;
//					String value = null;
//					for(int j=0;j<RefcolumnVOs.length;j++){
//						if(RefcolumnVOs[j].getFieldname().equals("name")){
//							value = (String) aa.get(j);
//						}
//						else if(RefcolumnVOs[j].getFieldname().equals(pkFieldCode)){
//							pk = (String) aa.get(j);
//						}
//					}
//					map.put(pk, value);
//				}
//				if(map.size() > 0)
//					refPkName.put(reftype, map);
//				else
//					return null;
//			}
//		}else if(reftype.startsWith("COMBO,")){
//			//如果是下拉，则按照下拉取值
//			reftype = reftype.substring(6);
//			if(refPkName.get(reftype) == null){
//				if (reftype != null
//						&& (reftype = reftype.trim()).length() > 0) {
//					boolean isFromMeta = reftype
//							.startsWith(MetaDataPropertyAdpter.COMBOBOXMETADATATOKEN);
//					String reftype1 = reftype.replaceFirst(MetaDataPropertyAdpter.COMBOBOXMETADATATOKEN,"");
//					List<DefaultConstEnum> combodata = ComboBoxUtil.getInitData(reftype1, isFromMeta);
//					Map<String,String> map = new HashMap<String,String>();
//					for(int i=0;i<combodata.size();i++){
//						map.put(combodata.get(i).getValue().toString(), combodata.get(i).getName());
//					}
//					if(map.size() > 0)
//						refPkName.put(reftype, map);
//					else
//						return null;
//				}
//			}
//		}
//		return refPkName.get(reftype).get(refval);
//	}
}

