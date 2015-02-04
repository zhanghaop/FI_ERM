package nc.erm.mobile.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import nc.bs.framework.common.NCLocator;
import nc.bs.wfengine.engine.ActivityInstance;
import nc.itf.uap.pf.IPFWorkflowQry;
import nc.itf.uap.pf.IWorkflowDefine;
import nc.vo.pub.BusinessException;
import nc.vo.pub.workflownote.WorkflownoteVO;
import nc.vo.pub.workflowqry.FlowAdminVO;
import nc.vo.uap.wfmonitor.ProcessRouteRes;
import nc.vo.wfengine.core.XpdlPackage;
import nc.vo.wfengine.core.activity.Activity;
import nc.vo.wfengine.core.activity.Implementation;
import nc.vo.wfengine.core.parser.UfXPDLParser;
import nc.vo.wfengine.core.parser.XPDLNames;
import nc.vo.wfengine.core.parser.XPDLParserException;
import nc.vo.wfengine.core.participant.BasicParticipantEx;
import nc.vo.wfengine.core.workflow.WorkflowProcess;
import nc.vo.wfengine.definition.WorkflowTypeEnum;
import nc.vo.wfengine.pub.WfTaskOrInstanceStatus;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class QueryWorkFlowUtil {
	/**
	 * 根据vo查询审批流/工作流，并返回给轻量级页面
	 * @param aggvo
	 * @return
	 * @throws BusinessException
	 * @throws JSONException
	 */
	public JSONObject queryWorkFlow(String openBillId,String billType) throws BusinessException, JSONException {
//		IArapPayableBillPubQueryService service = (IArapPayableBillPubQueryService) NCLocatorFactory
//				.getInstance().getFiwebNCLocator()
//				.lookup(IArapPayableBillPubQueryService.class);
//		String[] keys = { openBillId };
//		AggPayableBillVO[] fi_read = service.findBillByPrimaryKey(keys);
//		AggPayableBillVO aggvo = fi_read[0];
//		
		 //定义返回值map
		 JSONObject resultJson = new JSONObject();
//		 if(aggvo == null)
//			 return resultJson;
//		 BaseBillVO parent = (BaseBillVO) aggvo.getParentVO();
		 billType = "D1";
		 String billId = openBillId;
 		 //默认走审批流
 		 int m_iWorkflowtype = WorkflowTypeEnum.Workflow.getIntValue();
 		 
		  //待查询的字段
		  String[] queryFields = new String[] {"actiontype","checknote","checkname",
					"checkname", "dealdate", "sendername","senddate","checkman","approveresult"};//"messagenote",
		  //待转换的字段
		  String[] fromFields = new String[] {"actiontype","checknote","checkname",
					"checkname", "dealdate", "checkname","dealdate","checkman","approveresult"};
		  //转换后的字段
		  String[] toFields = new String[] {"actiontype","checknote","checkname",
					"checkname", "dealdate", "sendername","senddate","checkman","approveresult"};
			
		  // 找到WFTask所属的流程定义及其父流程定义
	      ProcessRouteRes processRoute = null;
	      IWorkflowDefine wfDefine = (IWorkflowDefine) NCLocator
					.getInstance().lookup(IWorkflowDefine.class.getName());
	      processRoute = wfDefine.queryProcessRoute(billId,
	    		  billType, null, m_iWorkflowtype);
	      if (processRoute == null || processRoute.getXpdlString() == null){
	          // WARN::说明该单据就没有流程实例,直接查看该单据的状态
//	    	  JSONObject fieldvalueJson = new JSONObject();
//	  		  if(parent.getApprovestatus().equals(IBillStatus.CHECKPASS))
//	  				fieldvalueJson.put("flowstatus","审批通过");
//	  		  else
//	  				fieldvalueJson.put("flowstatus","已提交");
		      resultJson.put("workflowstatus", "3");
	          return resultJson;
	      }

	      // 获取主流程状态
	      String  m_iMainFlowStatus = String.valueOf(processRoute.getProcStatus());
	      // 构造一个临时包
//	      XpdlPackage pkg = new XpdlPackage("unknown", "unknown", null);
//	      pkg.getExtendedAttributes().put(XPDLNames.MADE_BY, "UFW");
//	      String def_xpdl = null;
//	      ProcessRouteRes currentRoute = processRoute;
//	      if (currentRoute.getXpdlString() != null)
//	         def_xpdl = currentRoute.getXpdlString().toString();
//	      WorkflowProcess wp = null;
//	      try {
//	         // 前台解析XML串为对象
//	         wp = UfXPDLParser.getInstance().parseProcess(def_xpdl);
//	      } catch (XPDLParserException e) {
//	         Logger.error(e.getMessage(), e);
//	         return resultJson;
//	      }
//	      wp.setPackage(pkg);
//	      List<BasicTransitionEx> transition = wp.getTransitions();
//	      Map<String,String> transitionMap = new LinkedHashMap<String, String>();
//	      for(int i=0;i<transition.size();i++){
//	    	  transitionMap.put(transition.get(i).getTo(), transition.get(i).getFrom());
//	      }
//	      ActivityInstance[] allActInstances = currentRoute.getActivityInstance();
//	      String[] startedActivityDefIds = new String[allActInstances.length];
//	      // 当前正运行的活动
//	      HashSet hsRunningActs = new HashSet();
//	      for (int i = 0; i < allActInstances.length; i++) {
//		      //排除掉作废的子流程
//		      if(allActInstances[i].getStatus()==WfTaskOrInstanceStatus.Inefficient.getIntValue())
//		            continue;
//		         startedActivityDefIds[i] = allActInstances[i].getActivityID();
//		         if (allActInstances[i].getStatus() == WfTaskOrInstanceStatus.Started.getIntValue())
//		            hsRunningActs.add(startedActivityDefIds[i]);
//	      }
//	      if (startedActivityDefIds == null || startedActivityDefIds.length == 0)
//	         return resultJson;

	      //获取具体信息
	      FlowAdminVO adminVO = NCLocator
		    		.getInstance().lookup(IPFWorkflowQry.class)
			.queryWorkitemForAdmin(billType, billId, m_iWorkflowtype);
	      WorkflownoteVO[] noteVOs = adminVO.getWorkflowNotes();
		  if (noteVOs == null || noteVOs.length == 0) {
				return resultJson;
		  }
		  JSONArray workFlowArray = new JSONArray();
		  for(int j=0;j<noteVOs.length;j++){
//			  String id = noteVOs[j].getActivityID();
//			  String fromid = transitionMap.get(id);
			  if(noteVOs[j].getMessagenote().contains("commitBill") || noteVOs[j].getMessagenote().contains("startworkflow")){
			  //从制单中可以提出两种活动
			  //制单活动
				  workFlowArray.put(getApproveFieldMap(m_iMainFlowStatus,queryFields,fromFields,noteVOs[j],true));
			  //审批活动
				  workFlowArray.put(getApproveFieldMap(m_iMainFlowStatus,queryFields,toFields,noteVOs[j],false));
			  }
			  else{
				  //其余只能提出一种
				  workFlowArray.put(getApproveFieldMap(m_iMainFlowStatus,queryFields,toFields,noteVOs[j],false));
			  }
		  }
		  resultJson.put("workflowstatus", m_iMainFlowStatus);
		  resultJson.put("workflow", workFlowArray);
	      return resultJson;
	}
	
	private JSONObject getApproveFieldMap(String m_iMainFlowStatus,String[] queryFields,String[] targetFields,WorkflownoteVO vo,boolean isBillMake){
		JSONObject fieldvalueMap = new JSONObject();
		try {
//			fieldvalueMap.put("flowstatus",m_iMainFlowStatus);
//			Map<String,String> psndocMap = getTelnoByUserid();
			for (int k = 0; k < queryFields.length; k++) {
			    String field = queryFields[k];
					String attributeValue = vo.getAttributeValue(field) == null ? ""
							: vo.getAttributeValue(field).toString();
					if(field.equals("actiontype")){
						if(isBillMake){
							attributeValue="提交";
							fieldvalueMap.put("checktype", String.valueOf(3));
						}
						else{
							if(vo.getApprovestatus().intValue()==1)
								attributeValue="通过";
							else if(vo.getApprovestatus().intValue()==0)
								attributeValue="待审批";
							else if(vo.getApprovestatus().intValue()==4)
								attributeValue="作废";
							fieldvalueMap.put("checktype", vo.getApprovestatus().toString());
						}
					}
//					//审批人的加入电话号码
//					if(field.equals("checkman")){
//						attributeValue = vo.getAttributeValue(targetFields[k]) == null ? ""
//								: vo.getAttributeValue(targetFields[k]).toString();
//						String telno = psndocMap.get(attributeValue);
//						fieldvalueMap.put("telno", telno);
//					}
					
					fieldvalueMap.put(targetFields[k], attributeValue);
					
					if(vo.getApprovestatus().intValue()==0 && field.equals("dealdate")){
						fieldvalueMap.put("dealdate", dateDiff(vo.getSenddate().toLocalString(),new Date().toLocaleString()));
					}
			 }
		}catch(Exception e){
			
		}
		 return fieldvalueMap;
	}
	public String dateDiff(String startTime, String endTime) {
		SimpleDateFormat sd = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
		long nd = 1000*24*60*60;//一天的毫秒数
		long nh = 1000*60*60;//一小时的毫秒数
		//long nm = 1000*60;//一分钟的毫秒数
		//long ns = 1000;//一秒钟的毫秒数
		long diff;
		//获得两个时间的毫秒时间差异
		try {
			diff = sd.parse(endTime).getTime() - sd.parse(startTime).getTime();
			long day = diff/nd;//计算差多少天
			long hour = diff%nd/nh;//计算差多少小时
//			long min = diff%nd%nh/nm;//计算差多少分钟
//			long sec = diff%nd%nh%nm/ns;//计算差多少秒
			if(day>0)
				return day+"天"+hour+"小时";
			else
				return hour+"小时";
		} catch (ParseException e) {
			return "";
		}
	}
//	private Map<String,String> getTelnoByUserid() {
////		String sql = "select mobile from bd_psndoc where pk_psndoc in (select pk_psndoc from sm_user where cuserid='" + userid + "') ";
//		String sql = "select cuserid,mobile from bd_psndoc,sm_user where bd_psndoc.pk_psndoc = sm_user.pk_psndoc ";
//		
//		BaseDAO dao = new BaseDAO();
//		final Map<String,String> psndocMap = new HashMap<String, String>();
//		try {
//			dao.executeQuery(sql, new BaseProcessor(){
//				private static final long serialVersionUID = 1L;
//
//				@Override
//				public Object processResultSet(ResultSet resultset)
//						throws SQLException {
//					 while (resultset.next()) {
//						String mobile = (String) resultset.getObject(2);
//						String cuserid = (String) resultset.getObject(1);
//						psndocMap.put(cuserid, mobile);
//					}
//					return null;
//				}
//				
//			});
//		} catch (DAOException e) {
//			e.printStackTrace();
//		}
//		
//		return psndocMap;
//	}
public static JSONObject getWorkFlowGraph(String billType,String billId) throws BusinessException, JSONException{
	 JSONObject resultJson = new JSONObject();
	 //默认走审批流
	 int m_iWorkflowtype = WorkflowTypeEnum.Approveflow.getIntValue();
	 // 找到WFTask所属的流程定义及其父流程定义
    ProcessRouteRes processRoute = null;
    IWorkflowDefine wfDefine = (IWorkflowDefine) NCLocator
			.getInstance().lookup(IWorkflowDefine.class.getName());
    processRoute = wfDefine.queryProcessRoute(billId,
  		  billType, null, m_iWorkflowtype);
    if (processRoute == null || processRoute.getXpdlString() == null){
		// WARN::说明该单据就没有流程实例,直接查看该单据的状态
    	resultJson.put("success", "true");
        return resultJson;
    }

    // 构造一个临时包
    XpdlPackage pkg = new XpdlPackage("unknown", "unknown", null);
    pkg.getExtendedAttributes().put(XPDLNames.MADE_BY, "UFW");
    String def_xpdl = null;
    ProcessRouteRes currentRoute = processRoute;
    if (currentRoute.getXpdlString() != null)
       def_xpdl = currentRoute.getXpdlString().toString();
    WorkflowProcess wp = null;
    try {
       // 前台解析XML串为对象
       wp = UfXPDLParser.getInstance().parseProcess(def_xpdl);
    } catch (XPDLParserException e) {
    	resultJson.put("success", "false");
    	resultJson.put("failmessage", e.getMessage());
        return resultJson;
    }
    wp.setPackage(pkg);
//    List<BasicTransitionEx> transition = wp.getTransitions();
//    Map<String,String> transitionMap = new LinkedHashMap<String, String>();
//    for(int i=0;i<transition.size();i++){
//  	  transitionMap.put(transition.get(i).getTo(), transition.get(i).getFrom());
//    }
    ActivityInstance[] allActInstances = currentRoute.getActivityInstance();
    
    List<String> startedActivityDefIds = new ArrayList<String>();
    // 当前正运行的活动
    HashSet hsRunningActs = new HashSet();
    for (int i = 0; i < allActInstances.length; i++) {
	      //排除掉作废的子流程
	      if(allActInstances[i].getStatus() == WfTaskOrInstanceStatus.Inefficient.getIntValue())
	            continue;
	      startedActivityDefIds.add(allActInstances[i].getActivityID());
	      if (allActInstances[i].getStatus() == WfTaskOrInstanceStatus.Started.getIntValue())
	            hsRunningActs.add(startedActivityDefIds.get(i));
    }
    if (startedActivityDefIds == null || startedActivityDefIds.size() == 0)
       return resultJson;
    
    try{
    	resultJson.put("success", "true");
	    resultJson.put("nodename", wp.getMultiLangName().getText());
	    //参与者列表
	    List participants = wp.getParticipants();
	    //活动列表
	    List applications = wp.getApplications();
		//工作流程图
	    List activities = wp.getActivities();
	    Iterator it = activities.iterator();
	    Object mayBeActivity;
	    JSONArray jsonarray = new JSONArray();
	    while (it.hasNext()) {
	      mayBeActivity = it.next();
	      if (mayBeActivity instanceof Activity) {
	    	    Activity activity = (Activity) mayBeActivity;
	    	    JSONObject tasknode = new JSONObject();
	    	    String id = activity.getId();
	    	    Implementation imp = activity.getImplementation();
	    	    if(startedActivityDefIds.contains(id)){
	    	    	tasknode.put("status", "1");
	    	    }
	    	    String performer = activity.getPerformer();
	    	    for(Object participant:participants){
	    	    	if (participant instanceof BasicParticipantEx) {
	    	    		BasicParticipantEx p = (BasicParticipantEx) participant;
	    	    		if(p.getId().equals(performer)){
	    	    			tasknode.put("participant", p.getMultiLangName());
	    	    			tasknode.put("participanttype", p.getParticipantType().toString());
	    	    		}
	    	    	}
	    	    }
	    	    tasknode.put("refname", activity.getMultiLangName().getText());
	    		tasknode.put("pk_ref", id);
	    		jsonarray.put(tasknode);
	      }
	    }
		resultJson.put("reflist", jsonarray);
    }catch (JSONException e) {
		return resultJson;
	}
	return resultJson;
}
	
}
