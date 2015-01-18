package nc.erm.mobile.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nc.bs.dao.BaseDAO;
import nc.bs.erm.accruedexpense.common.AccruedBillQueryCondition;
import nc.bs.erm.matterapp.common.MatterAppQueryCondition;
import nc.bs.erm.util.CacheUtil;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.itf.arap.prv.IBXBillPrivate;
import nc.itf.erm.accruedexpense.IErmAccruedBillQueryPrivate;
import nc.itf.erm.matterapp.IErmMatterAppBillQueryPrivate;
import nc.itf.uap.rbac.IUserManageQuery;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.processor.ResultSetProcessor;
import nc.pubitf.erm.accruedexpense.IErmAccruedBillQuery;
import nc.pubitf.erm.matterapp.IErmMatterAppBillQuery;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.erm.accruedexpense.AccruedVO;
import nc.vo.erm.accruedexpense.AggAccruedBillVO;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.fi.pub.SqlUtils;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.pf.BillStatusEnum;
import nc.vo.sm.UserVO;
import nc.vo.util.AuditInfoUtil;

public class AuditListUtil {
	
	public String[] queryApprovedWFBillPksByCondition(String pk_user, String[] tradeTypes, boolean isApproved)
			throws BusinessException {
		StringBuffer sqlBuf = new StringBuffer(
				"select distinct billid from pub_workflownote where checkman = ?  and  actiontype = ? ");

		SQLParameter sqlparams = new SQLParameter();
		if (pk_user == null) {// 用户
			pk_user = AuditInfoUtil.getCurrentUser();
		}
		sqlparams.addParam(pk_user);
		sqlparams.addParam("Z");//actiontype为Z时，表示为审批

		if (isApproved) {//是否已审批
			sqlBuf.append(" and approvestatus = 1 ");
		} else {//待我审批
			sqlBuf.append(" and ischeck = 'N' ");
		}

		if (tradeTypes != null && tradeTypes.length > 0) {//交易类型
			sqlBuf.append(" and " + SqlUtils.getInStr("pk_billtype", tradeTypes, true));
		}

		@SuppressWarnings("unchecked")
		List<String> result = (List<String>) new BaseDAO().executeQuery(sqlBuf.toString(), sqlparams,
				new ResultSetProcessor() {
					private static final long serialVersionUID = 1L;

					@Override
					public Object handleResultSet(ResultSet rs) throws SQLException {
						List<String> result = new ArrayList<String>();
						while (rs.next()) {
							result.add(rs.getString(1));
						}
						return result;
					}

				});

		if (result != null && result.size() > 0) {
			return result.toArray(new String[] {});
		}

		return null;
	}
	
	public List<Map<String, String>> getAuditBillListByUser(String flag, boolean isApproved)
			throws BusinessException {
		List<Map<String, String>> maplist = new ArrayList<Map<String, String>>();
		String userid = InvocationInfoProxy.getInstance().getUserId();
		String groupid = InvocationInfoProxy.getInstance().getGroupId();
		//待查询的字段
		String[] queryFields = new String[] { JKBXHeaderVO.PK_JKBX,
				JKBXHeaderVO.YBJE,JKBXHeaderVO.TOTAL,
				JKBXHeaderVO.DJRQ, JKBXHeaderVO.DJBH, JKBXHeaderVO.ZY, JKBXHeaderVO.JKBXR,JKBXHeaderVO.DJLXBM,
				JKBXHeaderVO.DJDL,JKBXHeaderVO.SPZT};
		
		if(BillTypeUtil.BX.equals(flag) || BillTypeUtil.JK.equals(flag)){
			  String sqlWhere = "QCBZ='N' and DR ='0'";
			  //待查询的单据类型编码
			  String[] djlxbmarray = BillTypeUtil.getBillTypeArray(userid,flag);
			  sqlWhere += " and " + SqlUtils.getInStr(JKBXHeaderVO.DJLXBM, djlxbmarray, false);
			  String[] billPks = queryApprovedWFBillPksByCondition(userid, djlxbmarray, isApproved);
			  if (billPks != null && billPks.length > 0) {
				  sqlWhere += " and spzt in (2,3) and " + SqlUtils.getInStr(JKBXHeaderVO.PK_JKBX, billPks, false);
			  } else {
				  sqlWhere += " and 1=0 ";
			  }
			  String[] bxpks = NCLocator.getInstance().lookup(IBXBillPrivate.class)
				.queryPKsByWhereForBillManageNode(sqlWhere, groupid, userid);
			  
		      if(bxpks != null && bxpks.length > 0){
		    	  //根据主键查询表头信息
		    	  List<JKBXHeaderVO> list = null;
		    	  if(BillTypeUtil.BX.equals(flag)){
			    	  list = NCLocator.getInstance().lookup(IBXBillPrivate.class)
			    	  	.queryHeadersByPrimaryKeys(bxpks, BXConstans.BX_DJDL);
		    	  }else{
		    		  list = NCLocator.getInstance().lookup(IBXBillPrivate.class)
		  		    	  	.queryHeadersByPrimaryKeys(bxpks, BXConstans.JK_DJDL);
		    	  }
		    	  
		    	  if (list != null && !list.isEmpty()) {
		    		  Set<String> userSet = new HashSet<String>();
		    		  for(int i=0;i<list.size();i++){
		    			  userSet.add(list.get(i).getCreator());//getJkbxr()
		    		  }
	//	    		  PsndocVO[] docvos = NCLocator.getInstance().lookup(IPsndocQueryService.class).queryPsndocByPks(userSet.toArray(new String[0]));
		    		  UserVO[] uservo = NCLocator.getInstance().lookup(IUserManageQuery.class).findUserByIDs(userSet.toArray(new String[0]));
		    		  Map<String,String> userMap = new HashMap<String,String>();
		    		  for(int i=0;i<uservo.length;i++){
		    			  userMap.put(uservo[i].getCuserid(), uservo[i].getUser_name());
		    		  }
		    		  Map<String,String> djlxMap = new HashMap<String,String>();
					  DjLXVO[] vos = CacheUtil.getValueFromCacheByWherePart(DjLXVO.class, 
							"pk_group = '"+InvocationInfoProxy.getInstance().getGroupId()+"' and djdl in('jk','bx')");
					  for(int i=0;i<vos.length;i++){
						  String djlxmc = vos[i].getDjlxmc();
	//					  djlxmc = djlxmc.substring(0, djlxmc.length()-3);
						  djlxMap.put(vos[i].getDjlxbm(), djlxmc);
					  }
		  			  for (JKBXHeaderVO vo : list) {
		  				Map<String, String> fieldvalueMap = new HashMap<String, String>();
		  				for (int i = 0; i < queryFields.length; i++) {
		  					String field = queryFields[i];
		  					String attributeValue = vo.getAttributeValue(field) == null ? ""
		  							: vo.getAttributeValue(field).toString();
		  					fieldvalueMap.put(field, attributeValue);
		  					if(JKBXHeaderVO.DJRQ.equals(field)){
		  						fieldvalueMap.put(field, new UFDate(attributeValue).toLocalString());
		  					}else if(JKBXHeaderVO.SPZT.equals(field)){
		  						String spztshow = getSpztShow(vo.getSpzt());
		  						fieldvalueMap.put("spztshow", spztshow);
		  					}else if(JKBXHeaderVO.TOTAL.equals(field)){
								UFDouble total = new UFDouble(attributeValue);
								String value = NumberFormatUtil.formatDouble(total);
								fieldvalueMap.put(field, value);
							}else if(JKBXHeaderVO.JKBXR.equals(field)){
		  						fieldvalueMap.put(JKBXHeaderVO.JKBXR, userMap.get(vo.getAttributeValue(JKBXHeaderVO.CREATOR)));
		  					}else if(JKBXHeaderVO.DJLXBM.equals(field)){
			  					fieldvalueMap.put("djlxmc", djlxMap.get(vo.getDjlxbm()));
		  					}if(JKBXHeaderVO.ZY.equals(field) && attributeValue.length()>10){
								attributeValue = attributeValue.substring(0,5)+"...";
								fieldvalueMap.put(field, attributeValue);
							}
		  					
		  				}
		  				//图标				
						fieldvalueMap.put("iconsrc", BillTypeUtil.getIcon(fieldvalueMap.get("djlxbm")));
		  				maplist.add(fieldvalueMap);
		  			}
		  		} 		
		  	}
		}else if(BillTypeUtil.AC.equals(flag)){
			//待查询的字段
			String[] matterappFields = new String[] { MatterAppVO.PK_MTAPP_BILL,
					MatterAppVO.ORIG_AMOUNT,MatterAppVO.ORG_AMOUNT,
					MatterAppVO.BILLDATE, MatterAppVO.BILLNO, MatterAppVO.REASON, MatterAppVO.BILLMAKER,MatterAppVO.PK_TRADETYPE,
					MatterAppVO.DJDL,MatterAppVO.APPRSTATUS};
	      MatterAppQueryCondition condVo = new MatterAppQueryCondition();
	      String condition = "(billstatus = 1)";
	      initQueryCondition(groupid, userid,condition, condVo,isApproved);
	      String[] pks = getQueryService().queryBillPksByWhere(condVo);//[1001AA10000000000KUP]
	      AggMatterAppVO[] mattervos = NCLocator.getInstance().lookup(IErmMatterAppBillQuery.class)
	    		  .queryBillByPKs(pks);
	      if(mattervos != null && mattervos.length>0){
	    	  Set<String> userSet = new HashSet<String>();
    		  for(int i=0;i<mattervos.length;i++){
    			  userSet.add(mattervos[i].getParentVO().getCreator());
    		  }
//	    		  PsndocVO[] docvos = NCLocator.getInstance().lookup(IPsndocQueryService.class).queryPsndocByPks(userSet.toArray(new String[0]));
    		  UserVO[] uservo = NCLocator.getInstance().lookup(IUserManageQuery.class).findUserByIDs(userSet.toArray(new String[0]));
    		  Map<String,String> userMap = new HashMap<String,String>();
    		  if(uservo != null){
	    		  for(int i=0;i<uservo.length;i++){
	    			  if(uservo[i] != null)
	    				  userMap.put(uservo[i].getCuserid(), uservo[i].getUser_name());
	    		  }
    		  }
    		  Map<String,String> djlxMap = new HashMap<String,String>();
			  DjLXVO[] vos = CacheUtil.getValueFromCacheByWherePart(DjLXVO.class, 
					"pk_group = '" + groupid + "' and djdl in('ma')");
			  for(int i=0;i<vos.length;i++){
				  String djlxmc = vos[i].getDjlxmc();
//					  djlxmc = djlxmc.substring(0, djlxmc.length()-3);
				  djlxMap.put(vos[i].getDjlxbm(), djlxmc);
			  }
	    	  for(int voindex = 0;voindex < mattervos.length;voindex++){
	    		  AggMatterAppVO mattervo = mattervos[voindex];
	    		  MatterAppVO vo = mattervo.getParentVO();
	    		  Map<String, String> fieldvalueMap = new HashMap<String, String>();
	  				for (int i = 0; i < matterappFields.length; i++) {
	  					String field = matterappFields[i];
	  					String attributeValue = vo.getAttributeValue(field) == null ? ""
	  							: vo.getAttributeValue(field).toString();
	  					fieldvalueMap.put(queryFields[i], attributeValue);
	  					if(MatterAppVO.BILLDATE.equals(field)){
	  						fieldvalueMap.put(queryFields[i], new UFDate(attributeValue).toLocalString());
	  					}else if(MatterAppVO.APPRSTATUS.equals(field)){
	  						String spztshow = getSpztShow(vo.getApprstatus());
	  						fieldvalueMap.put("spztshow", spztshow);
	  					}else if(MatterAppVO.ORG_AMOUNT.equals(field)){
							UFDouble total = new UFDouble(attributeValue);
							String value = NumberFormatUtil.formatDouble(total);
							fieldvalueMap.put(queryFields[i], value);
						}else if(MatterAppVO.BILLMAKER.equals(field)){
	  						fieldvalueMap.put(queryFields[i], userMap.get(vo.getAttributeValue(JKBXHeaderVO.CREATOR)));
	  					}else if(MatterAppVO.PK_TRADETYPE.equals(field)){
		  					fieldvalueMap.put("djlxmc", djlxMap.get(vo.getPk_tradetype()));
	  					}if(MatterAppVO.REASON.equals(field) && attributeValue.length()>10){
							attributeValue = attributeValue.substring(0,5)+"...";
							fieldvalueMap.put(queryFields[i], attributeValue);
						}
	  					
	  				}
	  				//图标				
					fieldvalueMap.put("iconsrc", BillTypeUtil.getIcon(fieldvalueMap.get("djlxbm")));
	  				maplist.add(fieldvalueMap);
	    	  }
	      }
		}else if(BillTypeUtil.MA.equals(flag)){
			//待查询的字段
			String[] accruedFields = new String[] { AccruedVO.PK_ACCRUED_BILL,
					AccruedVO.AMOUNT,AccruedVO.GLOBAL_AMOUNT,
					AccruedVO.BILLDATE, AccruedVO.BILLNO, AccruedVO.REASON, AccruedVO.CREATOR,AccruedVO.PK_TRADETYPE,
					AccruedVO.PK_BILLTYPE,AccruedVO.APPRSTATUS};
			AccruedBillQueryCondition condVo = new AccruedBillQueryCondition();
		    String condition = "(billstatus = 1)";
		      initQueryCondition(groupid, userid,condition, condVo,isApproved);//[1001AA10000000000LMO]
		      String[] pks = NCLocator.getInstance().lookup(IErmAccruedBillQueryPrivate.class).queryBillPksByWhere(condVo);//[1001AA10000000000KUP]
		      AggAccruedBillVO[] accruedvos = NCLocator.getInstance().lookup(IErmAccruedBillQuery.class)
		    		  .queryBillByPks(pks,false);
		      if(accruedvos != null && accruedvos.length > 0){
		    	  Set<String> userSet = new HashSet<String>();
	    		  for(int i=0;i<accruedvos.length;i++){
	    			  userSet.add(accruedvos[i].getParentVO().getCreator());
	    		  }
//		    		  PsndocVO[] docvos = NCLocator.getInstance().lookup(IPsndocQueryService.class).queryPsndocByPks(userSet.toArray(new String[0]));
	    		  UserVO[] uservo = NCLocator.getInstance().lookup(IUserManageQuery.class).findUserByIDs(userSet.toArray(new String[0]));
	    		  Map<String,String> userMap = new HashMap<String,String>();
	    		  if(uservo != null){
		    		  for(int i=0;i<uservo.length;i++){
		    			  if(uservo[i] != null)
		    				  userMap.put(uservo[i].getCuserid(), uservo[i].getUser_name());
		    		  }
	    		  }
	    		  Map<String,String> djlxMap = new HashMap<String,String>();
				  DjLXVO[] vos = CacheUtil.getValueFromCacheByWherePart(DjLXVO.class, 
						"pk_group = '" + groupid + "' and djdl in('ac')");
				  for(int i=0;i<vos.length;i++){
					  String djlxmc = vos[i].getDjlxmc();
//						  djlxmc = djlxmc.substring(0, djlxmc.length()-3);
					  djlxMap.put(vos[i].getDjlxbm(), djlxmc);
				  }
		    	  for(int voindex = 0;voindex < accruedvos.length;voindex++){
		    		  AggAccruedBillVO mattervo = accruedvos[voindex];
		    		  AccruedVO vo = mattervo.getParentVO();
		    		  Map<String, String> fieldvalueMap = new HashMap<String, String>();
		  				for (int i = 0; i < accruedFields.length; i++) {
		  					String field = accruedFields[i];
		  					String attributeValue = vo.getAttributeValue(field) == null ? ""
		  							: vo.getAttributeValue(field).toString();
		  					fieldvalueMap.put(queryFields[i], attributeValue);
		  					if(AccruedVO.BILLDATE.equals(field)){
		  						fieldvalueMap.put(queryFields[i], new UFDate(attributeValue).toLocalString());
		  					}else if(AccruedVO.APPRSTATUS.equals(field)){
		  						String spztshow = getSpztShow(vo.getApprstatus());
		  						fieldvalueMap.put("spztshow", spztshow);
		  					}else if(AccruedVO.ORG_AMOUNT.equals(field)){
								UFDouble total = new UFDouble(attributeValue);
								String value = NumberFormatUtil.formatDouble(total);
								fieldvalueMap.put(queryFields[i], value);
							}else if(AccruedVO.CREATOR.equals(field)){
		  						fieldvalueMap.put(queryFields[i], userMap.get(vo.getAttributeValue(JKBXHeaderVO.CREATOR)));
		  					}else if(AccruedVO.PK_TRADETYPE.equals(field)){
			  					fieldvalueMap.put("djlxmc", djlxMap.get(vo.getPk_tradetype()));
		  					}if(AccruedVO.REASON.equals(field) && attributeValue.length()>10){
								attributeValue = attributeValue.substring(0,5)+"...";
								fieldvalueMap.put(queryFields[i], attributeValue);
							}
		  					
		  				}
		  				//图标				
						fieldvalueMap.put("iconsrc", BillTypeUtil.getIcon(fieldvalueMap.get("djlxbm")));
		  				maplist.add(fieldvalueMap);
		    	  }
		      }
		}
	    return maplist;
	}
	
	public void initQueryCondition(String groupid,String userid,String condition, AccruedBillQueryCondition condVo,boolean isApproved) {
		condVo.setWhereSql(condition);
		condVo.setPk_tradetype("2621");
		condVo.setNodeCode("20110ACCMN");
		condVo.setPk_group(groupid);//000133100000000002BX
		condVo.setPk_user(userid);//100112100000000003EN
		if (isApproved) {
			condVo.setUser_approved(true);
		}else{
			condVo.setUser_approving(true);
		}
	}
	
	public void initQueryCondition(String groupid,String userid,String condition, MatterAppQueryCondition condVo,boolean isApproved) {
		condVo.setWhereSql(condition);
		condVo.setPk_tradetype("2611");
		condVo.setNodeCode("20110MTAMN");
		condVo.setPk_group(groupid);//000133100000000002BX
		condVo.setPk_user(userid);//100112100000000003EN
		if (isApproved) {
			condVo.setUser_approved(true);
		}else{
			condVo.setUser_approving(true);
		}
	}
	
	private IErmMatterAppBillQueryPrivate queryService;
	private IErmMatterAppBillQueryPrivate getQueryService() {
		if (queryService == null) {
			queryService = NCLocator.getInstance().lookup(IErmMatterAppBillQueryPrivate.class);
		}
		return queryService;
	}
	
	public String getSpztShow(Integer spzt) {
		String value = "";
		switch (spzt) {
		case -1:
			value = "待提交";
			break;
		case 0: 
			value = BillStatusEnum.NOPASS.getName();
			break;
		case 1:
			value = BillStatusEnum.APPROVED.getName();
			break;
		case 2:
			value = BillStatusEnum.APPROVING.getName();
			break;
		case 3:
			value = "已提交";
		default:
			break;
		}
		return value;
	}
}
