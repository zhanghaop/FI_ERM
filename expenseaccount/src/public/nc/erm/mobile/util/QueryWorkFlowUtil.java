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
	 * ����vo��ѯ������/�������������ظ�������ҳ��
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
		 //���巵��ֵmap
		 JSONObject resultJson = new JSONObject();
//		 if(aggvo == null)
//			 return resultJson;
//		 BaseBillVO parent = (BaseBillVO) aggvo.getParentVO();
		 billType = "D1";
		 String billId = openBillId;
 		 //Ĭ����������
 		 int m_iWorkflowtype = WorkflowTypeEnum.Workflow.getIntValue();
 		 
		  //����ѯ���ֶ�
		  String[] queryFields = new String[] {"actiontype","checknote","checkname",
					"checkname", "dealdate", "sendername","senddate","checkman","approveresult"};//"messagenote",
		  //��ת�����ֶ�
		  String[] fromFields = new String[] {"actiontype","checknote","checkname",
					"checkname", "dealdate", "checkname","dealdate","checkman","approveresult"};
		  //ת������ֶ�
		  String[] toFields = new String[] {"actiontype","checknote","checkname",
					"checkname", "dealdate", "sendername","senddate","checkman","approveresult"};
			
		  // �ҵ�WFTask���������̶��弰�丸���̶���
	      ProcessRouteRes processRoute = null;
	      IWorkflowDefine wfDefine = (IWorkflowDefine) NCLocator
					.getInstance().lookup(IWorkflowDefine.class.getName());
	      processRoute = wfDefine.queryProcessRoute(billId,
	    		  billType, null, m_iWorkflowtype);
	      if (processRoute == null || processRoute.getXpdlString() == null){
	          // WARN::˵���õ��ݾ�û������ʵ��,ֱ�Ӳ鿴�õ��ݵ�״̬
//	    	  JSONObject fieldvalueJson = new JSONObject();
//	  		  if(parent.getApprovestatus().equals(IBillStatus.CHECKPASS))
//	  				fieldvalueJson.put("flowstatus","����ͨ��");
//	  		  else
//	  				fieldvalueJson.put("flowstatus","���ύ");
		      resultJson.put("workflowstatus", "3");
	          return resultJson;
	      }

	      // ��ȡ������״̬
	      String  m_iMainFlowStatus = String.valueOf(processRoute.getProcStatus());
	      // ����һ����ʱ��
//	      XpdlPackage pkg = new XpdlPackage("unknown", "unknown", null);
//	      pkg.getExtendedAttributes().put(XPDLNames.MADE_BY, "UFW");
//	      String def_xpdl = null;
//	      ProcessRouteRes currentRoute = processRoute;
//	      if (currentRoute.getXpdlString() != null)
//	         def_xpdl = currentRoute.getXpdlString().toString();
//	      WorkflowProcess wp = null;
//	      try {
//	         // ǰ̨����XML��Ϊ����
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
//	      // ��ǰ�����еĻ
//	      HashSet hsRunningActs = new HashSet();
//	      for (int i = 0; i < allActInstances.length; i++) {
//		      //�ų������ϵ�������
//		      if(allActInstances[i].getStatus()==WfTaskOrInstanceStatus.Inefficient.getIntValue())
//		            continue;
//		         startedActivityDefIds[i] = allActInstances[i].getActivityID();
//		         if (allActInstances[i].getStatus() == WfTaskOrInstanceStatus.Started.getIntValue())
//		            hsRunningActs.add(startedActivityDefIds[i]);
//	      }
//	      if (startedActivityDefIds == null || startedActivityDefIds.length == 0)
//	         return resultJson;

	      //��ȡ������Ϣ
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
			  //���Ƶ��п���������ֻ
			  //�Ƶ��
				  workFlowArray.put(getApproveFieldMap(m_iMainFlowStatus,queryFields,fromFields,noteVOs[j],true));
			  //�����
				  workFlowArray.put(getApproveFieldMap(m_iMainFlowStatus,queryFields,toFields,noteVOs[j],false));
			  }
			  else{
				  //����ֻ�����һ��
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
							attributeValue="�ύ";
							fieldvalueMap.put("checktype", String.valueOf(3));
						}
						else{
							if(vo.getApprovestatus().intValue()==1)
								attributeValue="ͨ��";
							else if(vo.getApprovestatus().intValue()==0)
								attributeValue="������";
							else if(vo.getApprovestatus().intValue()==4)
								attributeValue="����";
							fieldvalueMap.put("checktype", vo.getApprovestatus().toString());
						}
					}
//					//�����˵ļ���绰����
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
		long nd = 1000*24*60*60;//һ��ĺ�����
		long nh = 1000*60*60;//һСʱ�ĺ�����
		//long nm = 1000*60;//һ���ӵĺ�����
		//long ns = 1000;//һ���ӵĺ�����
		long diff;
		//�������ʱ��ĺ���ʱ�����
		try {
			diff = sd.parse(endTime).getTime() - sd.parse(startTime).getTime();
			long day = diff/nd;//����������
			long hour = diff%nd/nh;//��������Сʱ
//			long min = diff%nd%nh/nm;//�������ٷ���
//			long sec = diff%nd%nh%nm/ns;//����������
			if(day>0)
				return day+"��"+hour+"Сʱ";
			else
				return hour+"Сʱ";
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
	 //Ĭ����������
	 int m_iWorkflowtype = WorkflowTypeEnum.Approveflow.getIntValue();
	 // �ҵ�WFTask���������̶��弰�丸���̶���
    ProcessRouteRes processRoute = null;
    IWorkflowDefine wfDefine = (IWorkflowDefine) NCLocator
			.getInstance().lookup(IWorkflowDefine.class.getName());
    processRoute = wfDefine.queryProcessRoute(billId,
  		  billType, null, m_iWorkflowtype);
    if (processRoute == null || processRoute.getXpdlString() == null){
		// WARN::˵���õ��ݾ�û������ʵ��,ֱ�Ӳ鿴�õ��ݵ�״̬
    	resultJson.put("success", "true");
        return resultJson;
    }

    // ����һ����ʱ��
    XpdlPackage pkg = new XpdlPackage("unknown", "unknown", null);
    pkg.getExtendedAttributes().put(XPDLNames.MADE_BY, "UFW");
    String def_xpdl = null;
    ProcessRouteRes currentRoute = processRoute;
    if (currentRoute.getXpdlString() != null)
       def_xpdl = currentRoute.getXpdlString().toString();
    WorkflowProcess wp = null;
    try {
       // ǰ̨����XML��Ϊ����
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
    // ��ǰ�����еĻ
    HashSet hsRunningActs = new HashSet();
    for (int i = 0; i < allActInstances.length; i++) {
	      //�ų������ϵ�������
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
	    //�������б�
	    List participants = wp.getParticipants();
	    //��б�
	    List applications = wp.getApplications();
		//��������ͼ
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
