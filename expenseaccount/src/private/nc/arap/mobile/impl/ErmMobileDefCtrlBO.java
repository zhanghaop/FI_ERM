package nc.arap.mobile.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.Map.Entry;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.erm.util.ErmDjlxCache;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.framework.exception.ComponentException;
import nc.bs.logging.Logger;
import nc.bs.pf.pub.PfDataCache;
import nc.erm.mobile.view.ComboBoxUtil;
import nc.erm.mobile.view.MobileBillItem;
import nc.itf.arap.prv.IBXBillPrivate;
import nc.itf.arap.pub.IBXBillPublic;
import nc.itf.arap.pub.IErmBillUIPublic;
import nc.itf.fi.org.IOrgVersionQueryService;
import nc.itf.fi.pub.Currency;
import nc.itf.uap.billtemplate.IBillTemplateQry;
import nc.itf.uap.pf.IPFWorkflowQry;
import nc.jdbc.framework.processor.ResultSetProcessor;
import nc.ui.bd.ref.AbstractRefModel;
import nc.ui.bd.ref.RefPubUtil;
import nc.ui.bd.ref.model.CustBankaccDefaultRefModel;
import nc.ui.pub.beans.constenum.DefaultConstEnum;
import nc.ui.pub.bill.IBillItem;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.bd.ref.RefcolumnVO;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.BXHeaderVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.ValidationException;
import nc.vo.pub.bill.BillStructVO;
import nc.vo.pub.bill.BillTabVO;
import nc.vo.pub.bill.BillTempletBodyVO;
import nc.vo.pub.bill.BillTempletHeadVO;
import nc.vo.pub.bill.BillTempletVO;
import nc.vo.pub.bill.MetaDataPropertyAdpter;
import nc.vo.pub.billtype.BilltypeVO;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.pf.IPfRetCheckInfo;
import nc.vo.pub.templet.translator.BillTranslator;
import nc.vo.vorg.OrgVersionVO;
import uap.json.JSONArray;
import uap.json.JSONObject;
public class ErmMobileDefCtrlBO extends AbstractErmMobileCtrlBO{
	private static int panel=9990;
	private BaseDAO basedao;
	public static final List<String> djlxbmList = new ArrayList<String>();
	static { 
		for(int i=1; i<7; i++){
			djlxbmList.add("264" + Integer.valueOf(i).toString());
		}
	}
	Map<String,Map<String,String>> getBXbilltype(String userid){
		Map<String,Map<String,String>> billtypeMap = new LinkedHashMap<String,Map<String,String>>();
		try {
			initEvn(userid);
			HashMap<String, BilltypeVO> billtypes = PfDataCache.getBilltypes();
			List<BilltypeVO> list = new ArrayList<BilltypeVO>();
			String pk_group = InvocationInfoProxy.getInstance().getGroupId();
			for (BilltypeVO vo : billtypes.values()) {
				if (vo.getSystemcode() != null && vo.getSystemcode().equalsIgnoreCase(BXConstans.ERM_PRODUCT_CODE)) {
					if (vo.getPk_billtypecode().equals(BXConstans.BX_DJLXBM)
							|| vo.getPk_billtypecode().equals(BXConstans.JK_DJLXBM)
							|| vo.getPk_billtypecode().equals("2647") || vo.getPk_billtypecode().equals("264a")) {
						continue;
					}
					// 通过当前集团进行过滤
					if (vo.getPk_group() != null && !vo.getPk_group().equalsIgnoreCase(pk_group)) {
						continue;
					}
					if (BXConstans.BX_DJLXBM.equals(vo.getParentbilltype())) {
						list.add(vo);
					}
				}
			}
			BilltypeVO[] toArray = list.toArray(new BilltypeVO[] {});
			Arrays.sort(toArray, new Comparator<BilltypeVO>() {
				public int compare(BilltypeVO o1, BilltypeVO o2) {
					return o1.getPk_billtypecode().compareTo(o2.getPk_billtypecode());
				}
			});
			for(int i =0; i<toArray.length; i++){
				Map<String,String> map = new HashMap<String,String>();
				map.put("djlxbm", toArray[i].getPk_billtypecode());
				map.put("djlxmc", toArray[i].getBilltypename());
				map.put("nodecode", toArray[i].getNodecode());
				billtypeMap.put("abc"+i, map);
			}
			return billtypeMap;
		}catch (BusinessException e) {
			return billtypeMap;
		}
	}
	
	public String addJkbx(Map<String, Object> map, String djlxbm,String userid) throws BusinessException{
		initEvn(userid);
		try{
			String pk_jkbx = null;
			if(map.get("pk_jkbx") != null && !"".equals(map.get("pk_jkbx"))){
				// 主键已生成，说明是修改后提交，需要先收回单据
				commitCancle(map.get("pk_jkbx").toString());
				pk_jkbx = updateJkbx(map,userid); 
			}else{
				//第一次提交
				pk_jkbx = insertJkbx(map,djlxbm,userid);
			}
			commitJkbx(userid,pk_jkbx);
			return "pk_jkbx"+pk_jkbx;
		}catch(BusinessException e){
			String msg = e.getMessage();
			return msg; 
		}
	}
	
	/**
	 * 新增保存
	 * 
	 * @param valuemap
	 * @return
	 * @throws BusinessException
	 */
	@SuppressWarnings("unchecked")
	private String insertJkbx(Map<String, Object> valuemap,String djlxbm,String userid)
			throws BusinessException {
		initEvn(userid);
		String pk_group = InvocationInfoProxy.getInstance().getGroupId();
		
		
//		List<Map<String, Object>> attachment = (List<Map<String, Object>>) valuemap.get("attachment");
//		if(attachment == null || attachment.isEmpty()){
//			return null;
//		}
//		BASE64Decoder decoder = new BASE64Decoder();
//		if(isProductInstalled(InvocationInfoProxy.getInstance().getGroupId(),"70")){
//			String userID = InvocationInfoProxy.getInstance().getUserId();
//			int size = attachment.size();
//			String[] fileNames = new String[size];
//			int[] fileSizes = new int[size];
//			String[] content = new String[size];
//			for(int i=0 ; i<size; i++){
//				Map map = attachment.get(i);
//				fileNames[i] = (String) map.get("name");// 文件的名称
//				fileSizes[i] = Integer.parseInt(map.get("size").toString()); // 文件的大小
//				//file是经过base64编码的
//				String file = (String) map.get("content");
//				content[i] = file;//decoder.decodeBuffer(file).toString();
//			}
//			boolean value = NCLocator.getInstance().lookup(IImagUtil.class)
//					.UploadImag(userID, "1001Z31000000000068K", fileNames, fileSizes, content); 
//		}
		
		
		DjLXVO djlxVO = ErmDjlxCache.getInstance().getDjlxVO(pk_group, djlxbm);
		// 初始化表头数据
		IErmBillUIPublic initservice = NCLocator.getInstance().lookup(IErmBillUIPublic.class);
		JKBXVO jkbxvo = initservice.setBillVOtoUI(djlxVO, "", null);
		JKBXHeaderVO parentVO = jkbxvo.getParentVO();
		//根据数据类型将需要转换的数据进行转换
		Map<String,String> headMap = templetCache.get(InvocationInfoProxy.getInstance().getGroupId() + djlxbm + "head");
		if(headMap == null){
			getBxdTemplate(userid,djlxbm,null,"editcard");
			headMap = templetCache.get(InvocationInfoProxy.getInstance().getGroupId() + djlxbm + "head");
		}
		
		boolean incluedCurr = false;
		for (Entry<String, Object> value_entry : valuemap.entrySet()) {
			String key = value_entry.getKey();
			if("items".equals(key) || "attachment".equals(key)){
				continue;
			}
			String value = getStringValue(value_entry.getValue());
			if(JKBXHeaderVO.DJRQ.equals(key)){
				if(!StringUtil.isEmpty(value)){
					parentVO.setDjrq(new UFDate(value));
				}else{
					parentVO.setDjrq(new UFDate());
				}
			}else if(JKBXHeaderVO.TOTAL.equals(key)){
				if(!StringUtil.isEmpty(value)){
					parentVO.setTotal(new UFDouble(value));
				}
			}else{
				String reftype= headMap.get(key);
				if(reftype != null && reftype.startsWith("COMBO,")){
					if(value!=null && !value.equals("")){
						int intValue = Integer.parseInt(value);
						parentVO.setAttributeValue(key, intValue);
					}
				}else{
					parentVO.setAttributeValue(key, value);
				}
			}
			
			if(key.equals("bzbm")){
				incluedCurr = true;
			}
		}
		parentVO.setPk_jkbx(null);
		parentVO.setStatus(VOStatus.NEW);
		if(!incluedCurr){
			parentVO.setBzbm("1002Z0100000000001K1");// 默认人民币
		}
		parentVO.setBbhl(new UFDouble(1));
		parentVO.setGroupbbhl(UFDouble.ZERO_DBL);
		parentVO.setGlobalbbhl(UFDouble.ZERO_DBL);
		if(parentVO.getPaytarget() == null){
			parentVO.setPaytarget(0);
		}
		parentVO.setDjbh(null);
		// 补充表头组织字段版本信息
		IOrgVersionQueryService orgvservice = NCLocator.getInstance().lookup(IOrgVersionQueryService.class);
		Map<String, OrgVersionVO> orgvmap = orgvservice.getOrgVersionVOsByOrgsAndDate(new String[]{parentVO.getPk_org()}, parentVO.getDjrq());
		OrgVersionVO orgVersionVO = orgvmap.get(parentVO.getPk_org());
		if(orgVersionVO==null){
			Map<String, OrgVersionVO> orgvmap2 = orgvservice.getOrgVersionVOsByOrgsAndDate(new String[]{parentVO.getPk_org()}, new UFDate("2990-01-01"));
			orgVersionVO= orgvmap2.get(parentVO.getPk_org());
		}
		String orgVid = orgVersionVO.getPk_vid();
		parentVO.setDwbm_v(orgVid);
		parentVO.setFydwbm_v(orgVid);
		parentVO.setPk_org_v(orgVid);
		parentVO.setPk_payorg_v(orgVid);
		
		String deptVid = getDept_vid(parentVO.getDeptid(),  parentVO.getDjrq());
		parentVO.setDeptid_v(deptVid);
		parentVO.setFydeptid_v(deptVid);
		
		// 设置表体数据
		List<Map<String, Object>> items = (List<Map<String, Object>>) valuemap.get("items");
		//把表头收支项目同步到表体
		String szxmid = parentVO.getSzxmid();
		if(items != null && !items.isEmpty()){ 
			UFDouble totalAmount = UFDouble.ZERO_DBL;   
			List<BXBusItemVO> itemvos = new ArrayList<BXBusItemVO>();
			for (Map<String, Object> itemvalue : items) {
				String amountvalue = (String) itemvalue.get("amount");
				if(StringUtil.isEmpty(amountvalue)){
					continue;
				}
				BXBusItemVO itemvo = new BXBusItemVO();
				itemvo.setStatus(VOStatus.NEW);
				for (Entry<String, Object> fieldvalues : itemvalue.entrySet()) {
					String key = fieldvalues.getKey();
					Object value = fieldvalues.getValue();
					if("amount".equals(key)){
						value = new UFDouble((String)value);
					}
					itemvo.setAttributeValue(key, value);
				} 
				if(!StringUtil.isEmpty(szxmid) && StringUtil.isEmpty(itemvo.getSzxmid()))
					itemvo.setSzxmid(szxmid);
				itemvo.setYbje(itemvo.getAmount());
				itemvo.setBbje(itemvo.getAmount());
				itemvo.setPaytarget(0);
				itemvo.setReceiver(parentVO.getReceiver());
				itemvos.add(itemvo);
				totalAmount = totalAmount.add(itemvo.getAmount());
			}
			jkbxvo.setBxBusItemVOS(itemvos.toArray(new BXBusItemVO[itemvos.size()]));
			parentVO.setYbje(totalAmount);
			parentVO.setTotal(totalAmount);
//			parentVO.setBbje(totalAmount);
			
			parentVO = resetAmount(parentVO);
			
		}
		// 保存报销单数据
		IBXBillPublic service = NCLocator.getInstance().lookup(IBXBillPublic.class);
		JKBXVO[] result = service.save(new JKBXVO[] { jkbxvo });
		
		String bxpk = result[0].getParentVO().getPrimaryKey();
//		String djbh = result[0].getParentVO().getDjbh();
//		String spzt = result[0].getParentVO().getSpzt().toString();
		//保存附件
		saveAttachment(bxpk,valuemap);
		return bxpk; 
//		return bxpk+","+djbh+","+spzt; 
	}
	
	private JKBXHeaderVO resetAmount(JKBXHeaderVO parentVO){
		
		//集团
		String pk_group = InvocationInfoProxy.getInstance().getGroupId();
		// 原币币种pk
		String pk_currtype = parentVO.getBzbm();
		UFDate djrq = parentVO.getDjrq();
		UFDouble ybje = parentVO.getYbje();

		// 汇率(本币，集团本币，全局本币汇率)
		UFDouble orgRate = new UFDouble(1);
		
		UFDouble groupRate = new UFDouble(1);
		UFDouble globalRate = new UFDouble(1);
		try {
			orgRate = Currency.getRate(PK_ORG, pk_currtype, djrq);
			groupRate = Currency.getGroupRate(PK_ORG, pk_group, pk_currtype, djrq);
			globalRate = Currency.getGlobalRate(PK_ORG, pk_currtype, djrq);
		} catch (BusinessException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		

		parentVO.setBbhl(orgRate);
		parentVO.setGroupbbhl(groupRate);
		parentVO.setGlobalbbhl(globalRate);
		
		try {
			// 组织本币金额
			UFDouble[] bbje = Currency.computeYFB(PK_ORG,
					Currency.Change_YBCurr, pk_currtype, ybje, null, null, null, orgRate,new UFDate());
			parentVO.setYbye(ybje);
			parentVO.setBbje(bbje[2]);
			parentVO.setBbye(bbje[2]);

			// 集团、全局金额
			UFDouble[] money = Currency.computeGroupGlobalAmount(bbje[0], bbje[2],
					pk_currtype, new UFDate(), PK_ORG, pk_group,groupRate, globalRate);
			parentVO.setGroupbbje(money[0]);
			parentVO.setGroupbbye(money[0]);
			parentVO.setGlobalbbje(money[1]);
			parentVO.setGlobalbbye(money[1]);
		} catch (Exception e) {
			ExceptionHandler.consume(e);
		}
		return parentVO;
	}
    
	private String updateJkbx(Map<String, Object> valuemap,String userid) throws BusinessException {
		initEvn(userid);
		String headpk = (String) valuemap.get("pk_jkbx");
		// 查询当前借款报销单
		List<JKBXVO> vos = NCLocator.getInstance().
		  lookup(IBXBillPrivate.class).queryVOsByPrimaryKeys(new String[]{headpk}, null);
		if(vos == null || vos.isEmpty()){
			throw new BusinessException("单据已被删除，请检查");
		}
		JKBXVO jkbxvo = vos.get(0);
		JKBXHeaderVO parentVO = jkbxvo.getParentVO();
		// 审批状态控制
		Integer spzt = parentVO.getSpzt();

		if (spzt != null && (spzt.equals(IPfRetCheckInfo.GOINGON) || spzt.equals(IPfRetCheckInfo.COMMIT))) {
			String userId = InvocationInfoProxy.getInstance().getUserId();
			String billId = headpk;
			String billType = parentVO.getDjlxbm();
			try {
				if (((IPFWorkflowQry) NCLocator.getInstance().lookup(IPFWorkflowQry.class.getName()))
						.isApproveFlowStartup(billId, billType)) {// 启动了审批流后
					if(spzt.equals(IPfRetCheckInfo.COMMIT) && userId.equals(jkbxvo.getParentVO().getCreator())){
						throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0","0201212-0093")/*@res "请收回单据再修改！"*/);
					}
					
					if (!NCLocator.getInstance().lookup(IPFWorkflowQry.class).isCheckman(billId,
									billType, userId)) {
						throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0","0201212-0092")/*@res "请取消审批再修改！"*/);
					}
				}else{
					throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0","0201212-0093")/*@res "请收回单据再修改！"*/);
				}
			} catch (ValidationException ex) {
				ExceptionHandler.handleException(ex);
			} catch (BusinessException e) {
				ExceptionHandler.consume(e);
			}
		}
		//根据数据类型将需要转换的数据进行转换
		Map<String,String> headMap = templetCache.get(InvocationInfoProxy.getInstance().getGroupId() + (String) valuemap.get("djlxbm") + "head");
		if(headMap == null){
			getBxdTemplate(userid,(String) valuemap.get("djlxbm"),null,"editcard");
			headMap = templetCache.get(InvocationInfoProxy.getInstance().getGroupId() + (String) valuemap.get("djlxbm") + "head");
		}
		parentVO.setStatus(VOStatus.UPDATED);
		parentVO.setBzbm("1002Z0100000000001K1");// 默认人民币
		parentVO.setBbhl(new UFDouble(1));
		parentVO.setPaytarget(0);
		for (Entry<String, Object> value_entry : valuemap.entrySet()) {
			String key = value_entry.getKey();
			if("items".equals(key)){
				continue;
			}
			String value = getStringValue(value_entry.getValue());
			if(JKBXHeaderVO.DJRQ.equals(key)){
				if(!StringUtil.isEmpty(value)){
					parentVO.setDjrq(new UFDate(value));
				}
			}else if(JKBXHeaderVO.TOTAL.equals(key)){
				if(!StringUtil.isEmpty(value)){
					parentVO.setTotal(new UFDouble(value));
				}
			}else{
				String reftype= headMap.get(key);
				if(reftype != null && reftype.startsWith("COMBO,")){
					if(value!=null && !value.equals("")){
						int intValue = Integer.parseInt(value);
						parentVO.setAttributeValue(key, intValue);
					}
				}else{
					parentVO.setAttributeValue(key, value);
				}
			}
		}
		// 业务行先删后插，重新合计表头
		List<BXBusItemVO> itemlist = new ArrayList<BXBusItemVO>();
		BXBusItemVO[] olditems = jkbxvo.getBxBusItemVOS();
		if(olditems != null && olditems.length > 0){
			for (int i = 0; i < olditems.length; i++) {
				olditems[i].setStatus(VOStatus.DELETED);
				itemlist.add(olditems[i]);
			}
		}
		UFDouble totalAmount = UFDouble.ZERO_DBL;
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> items = (List<Map<String, Object>>) valuemap.get("items");
		if(items != null && !items.isEmpty()){
			//把表头收支项目同步到表体
			String szxmid = parentVO.getSzxmid();
			for (Map<String, Object> itemvalue : items) {
				String amountvalue = (String) itemvalue.get("amount");
				if(StringUtil.isEmpty(amountvalue)){
					continue;
				}
				BXBusItemVO itemvo = new BXBusItemVO();
				itemvo.setStatus(VOStatus.NEW);
				for (Entry<String, Object> fieldvalues : itemvalue.entrySet()) {
					String key = fieldvalues.getKey();
					Object value = fieldvalues.getValue();
					if("amount".equals(key)){
						value = new UFDouble((String)value);
					}
					itemvo.setAttributeValue(key, value);
					//收支项目字段同步到表体行
					if(!StringUtil.isEmpty(szxmid) && StringUtil.isEmpty(itemvo.getSzxmid())){
						itemvo.setSzxmid(szxmid);
					}
				} 
				
				itemvo.setYbje(itemvo.getAmount()); 
				itemvo.setBbje(itemvo.getAmount());
				itemvo.setPaytarget(0);
				itemvo.setReceiver(parentVO.getReceiver());
				itemvo.setPrimaryKey(null);
				itemlist.add(itemvo);
				totalAmount = totalAmount.add(itemvo.getAmount());
			}
		}
		jkbxvo.setBxBusItemVOS(itemlist.toArray(new BXBusItemVO[itemlist.size()]));
		parentVO.setYbje(totalAmount);
		parentVO.setTotal(totalAmount);
		parentVO.setBbje(totalAmount);
		// 附件先删后插
		deleteAttachmentList(headpk, parentVO.getOperator()); 
		saveAttachment(headpk, valuemap);
		
		// 更新单据
		NCLocator.getInstance().lookup(IBXBillPublic.class).update(new JKBXVO[]{jkbxvo});
		return headpk;
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
		String pk_billtemplet = getTemplatePK(djlxbm);
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
        if(flag.equals("editcard")){
        	for(int i=0;i<tablist.length();i++){
        		JSONObject item = (JSONObject) tablist.get(i);
        		String tablecode = item.getString("tablecode");
        		jsonObj.put(tablecode+"dsl", getbodydsl(bodyVO,tablecode,flag,djlxbm));
        	}
        }
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
	
	private static Map<String,Map<String,String>> templetCache = new HashMap<String,Map<String,String>>();
	private String getheaddsl(String flag,BillTempletBodyVO[] bodyVO,String djlxbm){
		StringBuffer div = new StringBuffer();
		div.append("<div id=\"viewPage999\"  layout=\"vbox\" width=\"fill\" height=\"wrap\">");
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
			templetCache.put(InvocationInfoProxy.getInstance().getGroupId() + djlxbm + "head", head);
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
					+ " font-size=\"16\" width=\"fill\" font-family=\"default\" ");
				else if(flag.equals("editcard"))
					input.append("<input id=\"dateinput" + panel 
							+ "\" readonly =\"true\" format=\"yyyy-MM-dd\" type=\"date\" height=\"44\"  color=\"#000000\" halign=\"right\" "
							+ "font-size=\"16\" width=\"fill\" font-family=\"default\" ");
				input.append(" bindfield=\"" + prefix + item.getKey() + "\"");
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
					reftype = "COMBO," + item.getName() + "," + reftype;
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

	public String getRefList(String userid, String reftype) throws BusinessException {
		initEvn(userid);
 		JSONObject jsonObj = new JSONObject();
		if(reftype.startsWith("COMBO,")){
			reftype = reftype.substring(6);
			int index = reftype.indexOf(",");
			String name = reftype.substring(0, index);
			reftype = reftype.substring(index+1);
			if (reftype != null
					&& (reftype = reftype.trim()).length() > 0) {
				boolean isFromMeta = reftype
						.startsWith(MetaDataPropertyAdpter.COMBOBOXMETADATATOKEN);
				String reftype1 = reftype.replaceFirst(MetaDataPropertyAdpter.COMBOBOXMETADATATOKEN,"");
				List<DefaultConstEnum> combodata = ComboBoxUtil.getInitData(reftype1, isFromMeta);
				JSONArray jsonarray = new JSONArray();
				for(int i=0;i<combodata.size();i++){
					DefaultConstEnum enumvalue = combodata.get(i);
					JSONObject data = new JSONObject();
					data.put("refname", enumvalue.getName());
					data.put("pk_ref", enumvalue.getValue());
					jsonarray.put(data);
				}
				JSONObject none = new JSONObject();
				none.put("refname", "无");
				none.put("pk_ref", "");
				jsonarray.put(none);
				jsonObj.put("reflist", jsonarray);
				jsonObj.put("nodename", name);
				return jsonObj.toString();		
			}
		}else{
			if(reftype.startsWith("UFREF,"))
				reftype = reftype.substring(6);
			//特殊参照返回空
			if (RefPubUtil.isSpecialRef(reftype)) {
				return jsonObj.toString();
			}
			AbstractRefModel refModel = RefPubUtil.getRefModel(reftype);
			String pkFieldCode = refModel.getPkFieldCode();
			RefcolumnVO[] RefcolumnVOs = RefPubUtil.getColumnSequences(refModel);
			if(reftype.equals("客商银行账户")){
				((CustBankaccDefaultRefModel) refModel).setPk_cust("10041110000000000Q8Q");
			}
			Vector vDataAll = refModel.getRefData();
			JSONArray jsonarray = new JSONArray();
			for(int i=0;i<vDataAll.size();i++){
				Vector aa = (Vector) vDataAll.get(i);
				JSONObject data = new JSONObject();
				for(int j=0;j<RefcolumnVOs.length;j++){
					String field = RefcolumnVOs[j].getFieldname();
					Object value = (Object) aa.get(j);
					if(reftype.equals("客商银行账户")){
						if(field.equals("accname")){
							data.put("refname", value);
						}
					}
					if(field.equals("name")){
						data.put("refname", value);
					}
					else if(pkFieldCode.equals(field)){
						data.put("pk_ref", value);
					}
				}
				jsonarray.put(data);
			}
			JSONObject none = new JSONObject();
			none.put("refname", "无");
			none.put("pk_ref", "");
			jsonarray.put(none);
			jsonObj.put("reflist", jsonarray);
			jsonObj.put("nodename", reftype);
		}
		return jsonObj.toString();
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
			templetCache.put(InvocationInfoProxy.getInstance().getGroupId() + djlxbm + tablecode + "body", body);
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
	
	private static Map<String,String> orderCache = new HashMap<String,String>();
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
				orderCache.put(InvocationInfoProxy.getInstance().getGroupId() + djlxbm  + tablecode + "_order",orderStr);
				return orderStr;
			}else{
				orderCache.put(InvocationInfoProxy.getInstance().getGroupId() + djlxbm  + tablecode + "_order","");
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
	 @SuppressWarnings("unchecked")
	private String getTemplatePK(String djlxbm) throws BusinessException {
		String pk_corp = InvocationInfoProxy.getInstance().getGroupId();
		ListResultSetProcessor processor = new ListResultSetProcessor();
		String xtdjlx = "2641,2642,2643,2644,2645,2646";
		try {
			if(!xtdjlx.contains(djlxbm)){
				//自定义交易类型，首先查找当前集团当前交易类型的模板，命名必须为djlxbm_W
				String sql = "select PK_BILLTEMPLET from PUB_BILLTEMPLET where pk_corp = '" 
				+ pk_corp + "' and BILL_TEMPLETNAME = '" + djlxbm + "_W'";
				List<String> ts = (List<String>)getBasedao().executeQuery(sql, processor);
				if (isEmpty(ts)){
					//为空则查找系统预置的差旅费报销单的移动模板
					sql = "select PK_BILLTEMPLET from PUB_BILLTEMPLET where PK_BILLTYPECODE = '2641_W' and pk_corp = '@@@@'";
					ts = (List<String>)getBasedao().executeQuery(sql, processor);
				}
				if (isEmpty(ts)){
					return null; 
				} 
				return ts.get(0);
			}else{
				//预制单据类型，首先查找当前集团当前交易类型的模板，命名必须为djlxbm_W
				String sql = "select PK_BILLTEMPLET from PUB_BILLTEMPLET where PK_BILLTYPECODE = '" + djlxbm 
				+ "_W' and pk_corp = '" + pk_corp + "' and BILL_TEMPLETNAME = '" + djlxbm + "_W'";
				List<String> ts = (List<String>)getBasedao().executeQuery(sql, processor);
				if (isEmpty(ts)){
					//为空则查找系统预置的当前交易类型的
					sql = "select PK_BILLTEMPLET from PUB_BILLTEMPLET where PK_BILLTYPECODE = '" + djlxbm 
					+ "_W' and pk_corp = '@@@@'";
					ts = (List<String>)getBasedao().executeQuery(sql, processor);
					if (isEmpty(ts)){
						//还为空则查找系统预置的差旅费报销单的模板模板
						sql = "select PK_BILLTEMPLET from PUB_BILLTEMPLET where PK_BILLTYPECODE = '2641_W' and pk_corp = '@@@@'";
						ts = (List<String>)getBasedao().executeQuery(sql, processor);
					}
				}
				if (isEmpty(ts)){
					return null; 
				} 
				return ts.get(0);
			}
		} catch (DAOException e) {
			return null;
		}
	}
	
	private BaseDAO getBasedao() {
		if (basedao == null) {
			basedao = new BaseDAO();
		}
		return basedao;
	}
	private <T> boolean isEmpty(Collection<T> c) {
		return c == null || c.size() == 0;
	}

	public String validateTs(String userid, String djlxbm, String nodecode,
			String tsflag) throws BusinessException {
		initEvn(userid);
		String pk_corp = InvocationInfoProxy.getInstance().getGroupId();
		ListResultSetProcessor processor = new ListResultSetProcessor();
		String xtdjlx = "2641,2642,2643,2644,2645,2646";
		if(tsflag.equals("head")){
			try {
				if(!xtdjlx.contains(djlxbm)){
					//自定义交易类型，首先查找当前集团当前交易类型的模板，命名必须为djlxbm_W
					String sql = "select ts from PUB_BILLTEMPLET where pk_corp = '" 
					+ pk_corp + "' and BILL_TEMPLETNAME = '" + djlxbm + "_W'";
					List<String> ts = (List<String>)getBasedao().executeQuery(sql, processor);
					if (isEmpty(ts)){
						//为空则查找系统预置的差旅费报销单的移动模板
						sql = "select ts from PUB_BILLTEMPLET where PK_BILLTYPECODE = '2641_W' and pk_corp = '@@@@'";
						ts = (List<String>)getBasedao().executeQuery(sql, processor);
					}
					if (isEmpty(ts)){
						return null; 
					} 
					return ts.get(0);
				}else{
					//预制单据类型，首先查找当前集团当前交易类型的模板，命名必须为djlxbm_W
					String sql = "select ts from PUB_BILLTEMPLET where PK_BILLTYPECODE = '" + djlxbm 
					+ "_W' and pk_corp = '" + pk_corp + "' and BILL_TEMPLETNAME = '" + djlxbm + "_W'";
					List<String> ts = (List<String>)getBasedao().executeQuery(sql, processor);
					if (isEmpty(ts)){
						//为空则查找系统预置的当前交易类型的
						sql = "select ts from PUB_BILLTEMPLET where PK_BILLTYPECODE = '" + djlxbm 
						+ "_W' and pk_corp = '@@@@'";
						ts = (List<String>)getBasedao().executeQuery(sql, processor);
						if (isEmpty(ts)){
							//还为空则查找系统预置的差旅费报销单的模板模板
							sql = "select ts from PUB_BILLTEMPLET where PK_BILLTYPECODE = '2641_W' and pk_corp = '@@@@'";
							ts = (List<String>)getBasedao().executeQuery(sql, processor);
						}
					}
					if (isEmpty(ts)){
						return null; 
					} 
					return ts.get(0);
				}
			} catch (DAOException e) {
				return null;
			}
		}
		else
			return null;
	}
	
	
	@SuppressWarnings("unchecked")
	Map<String,Object> getJkbxCard(String pk_jkbx,String userid,String djlxbm) throws BusinessException {
		initEvn(userid);
		
		//若没有pk，则返回空
		BaseDAO dao = new BaseDAO();
		Map<String, Object> resultmap = new HashMap<String, Object>();
		if(StringUtil.isEmpty(pk_jkbx)){
			return resultmap;
		}
		templetCache.clear();
		//取表头数据，只需取出queryFields标明的字段即可
		Map headMap = templetCache.get(InvocationInfoProxy.getInstance().getGroupId() + djlxbm + "head");
		if(headMap == null){
			getBxdTemplate(userid,djlxbm,null,"editcard");
			headMap = templetCache.get(InvocationInfoProxy.getInstance().getGroupId() + djlxbm + "head");
		}
		String[] queryFields = (String[]) headMap.keySet().toArray(new String[0]);
		BXHeaderVO bxheadvo = (BXHeaderVO) dao.retrieveByPK(BXHeaderVO.class, pk_jkbx, queryFields);
		if(bxheadvo == null)
			return resultmap;
		Map<String, Object> headvo = new HashMap<String,Object>();
		for (int i = 0; i < queryFields.length; i++) {
			String queryField = queryFields[i];
			String value = ErmMobileCtrlBO.getStringValue(bxheadvo.getAttributeValue(queryField));
			headvo.put(queryField, value);
			if(JKBXHeaderVO.DJRQ.equals(queryField)){
				headvo.put(queryField, new UFDate(value).toLocalString());
			}else if(JKBXHeaderVO.SPZT.equals(queryField)){
				String spztshow = ErmMobileCtrlBO.getSpztShow(bxheadvo.getSpzt());
				headvo.put("spztshow", spztshow);
			}
			
			//根据queryFields把是参照的部分取出来名字
			if(headMap.get(queryField) != null){
				String refname = resetRefName(queryField,value,headMap.get(queryField).toString());
				if(refname != null)
					headvo.put(queryField + "_name", refname);
			}
			 
		}
		resultmap.put("head", headvo);
		
		//取表体数据，只需取出itemQueryFields标明的字段即可
		IBXBillPrivate service = NCLocator.getInstance().lookup(IBXBillPrivate.class);
		BXBusItemVO[] items = service.queryItems(bxheadvo);
		List<Map<String, Object>> itemResultmapList = new ArrayList<Map<String,Object>>();
		if(items != null && items.length > 0){
			for (int i = 0; i < items.length; i++) {
				BXBusItemVO item = items[i];
				String tablecode = item.getTablecode();
				
				Map bodyMap = templetCache.get(InvocationInfoProxy.getInstance().getGroupId() + djlxbm  + tablecode + "body");
				String orderStr = orderCache.get(InvocationInfoProxy.getInstance().getGroupId() + djlxbm  + tablecode + "_order");
				if(bodyMap == null || orderStr == null){
					getItemDslFile(userid, djlxbm, null, tablecode, "editcard");
					bodyMap = templetCache.get(InvocationInfoProxy.getInstance().getGroupId() + djlxbm  + tablecode + "body");
					orderStr = orderCache.get(InvocationInfoProxy.getInstance().getGroupId() + djlxbm  + tablecode + "_order");
				}
				 
				if(orderStr != null && !"".equals(orderStr)){
					resultmap.put(tablecode + "_order", orderStr);
				} 
				
				String[] itemQueryFields = (String[])bodyMap.keySet().toArray(new String[0]); 
				Map<String, Object> itemResultmap = new HashMap<String, Object>();
				for (int j = 0; j < itemQueryFields.length;j++) {
					String queryField = itemQueryFields[j];
					String attrvalue = ErmMobileCtrlBO.getStringValue(item.getAttributeValue(queryField));
					if(BXBusItemVO.PK_REIMTYPE.equals(queryField)){
						// 转换报销类型为name
//						String attrname = reimtypemap.get(attrvalue) == null?"":reimtypemap.get(attrvalue).get(ReimTypeVO.NAME);
//						itemResultmap.put("reimname", attrname);
					}
					if(bodyMap.get(queryField) != null){
						String refname = resetRefName(queryField,attrvalue,bodyMap.get(queryField).toString());
						if(refname != null)
							itemResultmap.put(queryField + "_name", refname);
					}
					itemResultmap.put(queryField, attrvalue);
				}
				itemResultmapList.add(itemResultmap);
			}
			resultmap.put("items", itemResultmapList);
		}
		
		// 获取附件列表
		List<Map<String, String>> attatchmapList = getFileList(pk_jkbx, userid);
		resultmap.put("attachment", attatchmapList);
		//resultmap.put("ts", ts);
		return resultmap;
	}
	private static Map<String,Map<String,String>> refPkName = new HashMap<String,Map<String,String>>();
	private String resetRefName(String key,String refval,String reftype){
		if(key.endsWith("_name")){
			return null;
		}
		if(reftype.startsWith("UFREF,")){
			reftype = reftype.substring(6);
			if(reftype != null && reftype.contains(","))
				reftype = reftype.split(",")[0];
			if (RefPubUtil.isSpecialRef(reftype)) {
				return null;
			}
			if(refPkName.get(reftype) == null){
				AbstractRefModel refModel = RefPubUtil.getRefModel(reftype);
				String pkFieldCode = refModel.getPkFieldCode();
				RefcolumnVO[] RefcolumnVOs = RefPubUtil.getColumnSequences(refModel);
				Vector vDataAll = refModel.getRefData();
				Map<String,String> map = new HashMap<String,String>();
				for(int i=0;i<vDataAll.size();i++){
					Vector aa = (Vector) vDataAll.get(i);
					String pk = null;
					String value = null;
					for(int j=0;j<RefcolumnVOs.length;j++){
						if(RefcolumnVOs[j].getFieldname().equals("name")){
							value = (String) aa.get(j);
						}
						else if(RefcolumnVOs[j].getFieldname().equals(pkFieldCode)){
							pk = (String) aa.get(j);
						}
					}
					map.put(pk, value);
				}
				if(map.size() > 0)
					refPkName.put(reftype, map);
				else
					return null;
			}
		}else if(reftype.startsWith("COMBO,")){
			//如果是下拉，则按照下拉取值
			reftype = reftype.substring(6);
			if(refPkName.get(reftype) == null){
				if (reftype != null
						&& (reftype = reftype.trim()).length() > 0) {
					boolean isFromMeta = reftype
							.startsWith(MetaDataPropertyAdpter.COMBOBOXMETADATATOKEN);
					String reftype1 = reftype.replaceFirst(MetaDataPropertyAdpter.COMBOBOXMETADATATOKEN,"");
					List<DefaultConstEnum> combodata = ComboBoxUtil.getInitData(reftype1, isFromMeta);
					Map<String,String> map = new HashMap<String,String>();
					for(int i=0;i<combodata.size();i++){
						map.put(combodata.get(i).getValue().toString(), combodata.get(i).getName());
					}
					if(map.size() > 0)
						refPkName.put(reftype, map);
					else
						return null;
				}
			}
		}
		return refPkName.get(reftype).get(refval);
	}
}

