package nc.arap.mobile.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import nc.bs.arap.util.BillOrgVUtils;
import nc.bs.erm.util.ErUtil;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Log;
import nc.bs.pf.pub.PfDataCache;
import nc.bs.pub.filesystem.IFileSystemService;
import nc.bs.pub.filesystem.IQueryFolderTreeNodeService;
import nc.bs.pub.filesystem.UploadFileIsExistException;
import nc.itf.arap.prv.IBXBillPrivate;
import nc.itf.bd.psn.psndoc.IPsndocQueryService;
import nc.itf.org.IOrgVersionQryService;
import nc.itf.uap.rbac.IUserManageQuery;
import nc.vo.arap.bx.util.BXConstans;
import nc.pubitf.para.SysInitQuery;
import nc.vo.arap.bx.util.BXParamConstant;
import nc.vo.bd.psn.PsndocVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.pub.BusinessException;
import nc.vo.pub.billtype.BilltypeVO;
import nc.vo.pub.filesystem.NCFileNode;
import nc.vo.pub.filesystem.NCFileVO;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.pf.workflow.IPFActionName;
import nc.vo.sm.UserVO;
import nc.vo.trade.pub.IBillStatus;
import nc.vo.vorg.DeptVersionVO;

import org.apache.commons.lang.ArrayUtils;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;


public abstract class AbstractErmMobileCtrlBO {
	//将当前用户可用的单据类型缓存，查找我的未完成 待审批时用
	private String[] djlxbm = null;
	public String[] getDjlxbmArray(){
		if(djlxbm == null){
			HashMap<String, BilltypeVO> billtypes = PfDataCache.getBilltypes();
			List<String> list = new ArrayList<String>();
			String pk_group = InvocationInfoProxy.getInstance().getGroupId();
			for (BilltypeVO vo : billtypes.values()) {
				if (vo.getSystemcode() != null && vo.getSystemcode().equalsIgnoreCase(BXConstans.ERM_PRODUCT_CODE)) {
					if (vo.getPk_billtypecode().equals(BXConstans.BX_DJLXBM)
							|| vo.getPk_billtypecode().equals(BXConstans.JK_DJLXBM)
							|| vo.getPk_billtypecode().equals("2647")) {
						continue;
					}
					// 通过当前集团进行过滤
					if (vo.getPk_group() != null && !vo.getPk_group().equalsIgnoreCase(pk_group)) {
						continue;
					}
					if (BXConstans.BX_DJLXBM.equals(vo.getParentbilltype())) {
						list.add(vo.getPk_billtypecode());
					}
				}
			}
			djlxbm=list.toArray(new String[0]);
		}
		return djlxbm;
	}
	
	public static String getStringValue(Object value){
		return value == null? "":value.toString();
	}
	protected static String PK_ORG;
	public void initEvn(String userid) throws BusinessException{
		UserVO uservo = NCLocator.getInstance().lookup(IUserManageQuery.class).getUser(userid);
		InvocationInfoProxy.getInstance().setUserId(userid);
		InvocationInfoProxy.getInstance().setGroupId(uservo == null?null:uservo.getPk_group());
		PsndocVO[] docvos = NCLocator.getInstance().lookup(IPsndocQueryService.class).queryPsndocByPks(new String[]{uservo.getPk_psndoc()});
		if(docvos!=null && docvos.length>0)
			PK_ORG = docvos[0].getPk_org();
	}
	@SuppressWarnings("unchecked")
	protected void saveAttachment(String bxpk, Map valuemap) throws BusinessException{
		List<Map<String, Object>> attachment = (List<Map<String, Object>>) valuemap.get("attachment");
		if(attachment == null || attachment.isEmpty()){
			return ;
		}
		BASE64Decoder decoder = new BASE64Decoder();
		for(int i=0 ; i<attachment.size(); i++){
			Map map = attachment.get(i);
	//		String file = (String) map.get("attachment");
			String file = (String) map.get("content");
			if(file == null){
				continue;
			}
			InputStream in = null;
			try {
				in = new ByteArrayInputStream(decoder.decodeBuffer(file));
			} catch (IOException e3) {
				ExceptionHandler.handleException(e3);
			}
			String parentPath = bxpk;
			
			IFileSystemService service = NCLocator.getInstance().lookup(IFileSystemService.class);
			
			String filename = (String) map.get("name");// 文件的名称
			String size = (String) map.get("size").toString(); // 文件的大小
			long length = Long.parseLong(size);
			
			try {
				
	//			NCFileNode node = 
					service.createNewFileNodeWithStream(parentPath, filename, InvocationInfoProxy.getInstance().getUserId(), in, length);
				
			} catch (UploadFileIsExistException e1) {
	
		         ExceptionHandler.handleException(e1);
	
		      } catch (Exception e) {
	
		         try {
	
		             String fullPath = filename.replace('\\', '/');
	
		             if (parentPath != null && parentPath.trim().length() > 0) {
	
		                parentPath = parentPath.replace('\\', '/');
	
		                if (!parentPath.endsWith("/")) {
	
		                    parentPath += "/";
	
		                }
	
		                fullPath = parentPath + filename;
	
		             }
	
		             service.deleteNCFileNode(fullPath);
	
		         } catch (Exception e2) {
		        	 ExceptionHandler.handleException(e2);
		         }
		         ExceptionHandler.handleException(e);
	
		      }
	
	
		}
	
	}

	/**
	 * 收回提交的单据
	 * 
	 * @throws BusinessException
	 */
	protected String commitCancle(String pk_jkbx) throws BusinessException{
		// 根据pk查询单据
		List<JKBXVO> vos = null;
		try {
			vos = NCLocator.getInstance().
			    lookup(IBXBillPrivate.class).queryVOsByPrimaryKeys(new String[]{pk_jkbx}, null);
		} catch (BusinessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	    if(vos == null || vos.isEmpty()){
	        throw new BusinessException("单据已被删除，请检查");
	    }
		JKBXVO jkbxvo = vos.get(0);
		initEvn(jkbxvo.getParentVO().getOperator());
		if(jkbxvo.getParentVO().getSpzt().equals(IBillStatus.FREE))
			return pk_jkbx;
		//收回提交
		String actionType = getActionCode(IPFActionName.UNSAVE,PK_ORG);
		  PfUtilPrivate.runAction(null, actionType, jkbxvo.getParentVO().getDjlxbm(), jkbxvo, null,
					null, null, null);
	//	PfUtilPrivate.runAction(null, IPFActionName.UNSAVE
	//		 	+ InvocationInfoProxy.getInstance().getUserId(), jkbxvo.getParentVO().getDjlxbm(), jkbxvo, null,
	//			null, null, null); 
		return pk_jkbx;
	}
	
	/**
	 * 重构获取多版本部门id,供前台调用
	 * @param pk_dept
	 * @param billdate
	 * @return
	 * @throws BusinessException
	 */
	protected String getDept_vid(String pk_dept,UFDate billdate) throws BusinessException {
		Map<String, DeptVersionVO[]> versionMap=new HashMap<String, DeptVersionVO[]>();
		IOrgVersionQryService query = NCLocator.getInstance().lookup(IOrgVersionQryService.class);
		return getDept_vid(query, versionMap, pk_dept, billdate);
	}

	protected String getDept_vid(IOrgVersionQryService query,
			Map<String, DeptVersionVO[]> versionMap, String pk_org,
			UFDate billdate) throws BusinessException {
	
		if(billdate==null){
			billdate=new UFDate();
		}
	
		String pk_org_v = null;
	
		DeptVersionVO[] versions = versionMap.get(pk_org);
		if (ArrayUtils.isEmpty(versions)) {
			versions = query.queryDeptVersionVOSByOID(pk_org);
			versionMap.put(pk_org, versions == null ? new DeptVersionVO[0]: versions);
		}
		if(ArrayUtils.isEmpty(versions)){
			Log.getInstance(BillOrgVUtils.class).error(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2006pub_0","02006pub-0582")/*@res "~~~~~~~~~~@@@@~~~~~~~~~开始设置部门版本号"*/+pk_org, BillOrgVUtils.class, "setDept_v");
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2006pub_0","02006pub-0583")/*@res "查询部门版本错误，该部门无可用版本!"*/);
		}
	
		for(DeptVersionVO vso:versions){
			if(vso.getEnablestate()!=2){ //是否可用
				continue;
			}
			if((vso.getVstartdate().compareTo(billdate)<=0 &&  vso.getVenddate().compareTo(billdate)>=0)||vso.getVstartdate().isSameDate(billdate)||vso.getVenddate().isSameDate(billdate)){
				pk_org_v=vso.getPk_vid();
			}
		}
	
		// 未匹配到可用版本，取历史较早版本
		int index = 0;
		if (pk_org_v == null) {
			for (int i = 1; i < versions.length; i++) {
				if(versions[index].getVstartdate()!=null
						&&versions[index].getVstartdate().after(versions[i].getVstartdate())){
					index = i;
				}
			}
			pk_org_v = versions[index].getPk_vid();
		}
	
		
		if(pk_org_v==null){
			pk_org_v=versions[0].getPk_vid();
		}
		return pk_org_v;
	}
	/**
	 * 判断是工作流还是审批流
	 * @param pk_org
	 * @return
	 */
	protected String getActionCode(String code,String pk_org) {
		if(code.equals(IPFActionName.SAVE)){
			String actionCode = IPFActionName.SAVE;
			try {
				String paraString = SysInitQuery.getParaString(pk_org, BXParamConstant.ER_FLOW_TYPE);
				if (BXParamConstant.ER_FLOW_TYPE_WORKFLOW.equals(paraString)) {
					//如果是工作流，应该用start来启动
					actionCode = IPFActionName.START;
				}
			} catch (BusinessException e) {
				ExceptionHandler.consume(e);
			}
			return actionCode;
		}else if(code.equals(IPFActionName.UNSAVE)){
			String actionCode = IPFActionName.UNSAVE;
			try {
				String paraString = SysInitQuery.getParaString(pk_org, BXParamConstant.ER_FLOW_TYPE);
				if (BXParamConstant.ER_FLOW_TYPE_WORKFLOW.equals(paraString)) {
					//如果是工作流，应该用start来启动
					actionCode = IPFActionName.RECALL;
				}
			} catch (BusinessException e) {
				ExceptionHandler.consume(e);
			}
			return actionCode;
		}else if(code.equals(IPFActionName.APPROVE)){
			String actionCode = IPFActionName.APPROVE;
			try {
				String paraString = SysInitQuery.getParaString(pk_org, BXParamConstant.ER_FLOW_TYPE);
				if (BXParamConstant.ER_FLOW_TYPE_WORKFLOW.equals(paraString)) {
					//如果是工作流，应该用start来启动
					actionCode = IPFActionName.SIGNAL;
				}
			} catch (BusinessException e) {
				ExceptionHandler.consume(e);
			}
			return actionCode;
		}else if(code.equals(IPFActionName.UNAPPROVE)){
			String actionCode = IPFActionName.UNAPPROVE;
			try {
				String paraString = SysInitQuery.getParaString(pk_org, BXParamConstant.ER_FLOW_TYPE);
				if (BXParamConstant.ER_FLOW_TYPE_WORKFLOW.equals(paraString)) {
					//如果是工作流，应该用start来启动
					actionCode = IPFActionName.ROLLBACK;
				}
			} catch (BusinessException e) {
				ExceptionHandler.consume(e);
			}
			return actionCode;
		}else{
			return null;
		}
	}
	public String commitJkbx(String userid,String pk_jkbx) throws BusinessException {
		  initEvn(userid);
		  try{
			  // 查询
			  List<JKBXVO> vos = NCLocator.getInstance().
			    lookup(IBXBillPrivate.class).queryVOsByPrimaryKeys(new String[]{pk_jkbx}, null);
			  if(vos == null || vos.isEmpty()){
				  throw new BusinessException("单据已被删除，请检查");
			  }
			  JKBXVO jkbxvo = vos.get(0);
			  // 执行提交
			  String actionType = ErUtil.getCommitActionCode(PK_ORG);
			  PfUtilPrivate.runAction(null, actionType, jkbxvo.getParentVO().getDjlxbm(), jkbxvo, null,
						null, null, null);
//			  PfUtilPrivate.runAction(null, IPFActionName.SAVE+ userid, jkbxvo.getParentVO().getDjlxbm(), jkbxvo, null,
//						null, null, null);
			  return "true";
			}catch(BusinessException e){
				String msg = e.getMessage();
				return msg;
			}
	}
	
	@SuppressWarnings("unchecked")
	protected void deleteAttachmentList(String bxpk,String userid) throws BusinessException{
		List<String> attatchmapList = new ArrayList<String>();
		
		IQueryFolderTreeNodeService fileservice = NCLocator.getInstance().lookup(IQueryFolderTreeNodeService.class);
		NCFileNode filenode = fileservice.getNCFileNodeTreeAndCreateAsNeed(bxpk, userid);
		if(filenode != null){
			Enumeration fileEnum = filenode.breadthFirstEnumeration();
			while (fileEnum.hasMoreElements()) {
				NCFileNode tempfile = (NCFileNode) fileEnum.nextElement();
				Collection<NCFileVO> files = tempfile.getFilemap().values();
				for (NCFileVO ncFileVO : files) {
					attatchmapList.add(ncFileVO.getFullPath());
				}
			}
		}
		if(!attatchmapList.isEmpty()){
			IFileSystemService service = NCLocator.getInstance().lookup(IFileSystemService.class);
			service.deleteNCFileNodes(attatchmapList.toArray(new String[0]));
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public List<Map> getFileList(String bxpk,String userid) throws BusinessException{
		// 获取附件列表
		List<Map> attatchmapList = new ArrayList<Map>();
		
		IQueryFolderTreeNodeService fileservice = NCLocator.getInstance().lookup(IQueryFolderTreeNodeService.class);
		NCFileNode filenode = fileservice.getNCFileNodeTreeAndCreateAsNeed(bxpk, userid);
		if(filenode != null){
			Enumeration fileEnum = filenode.breadthFirstEnumeration();
			while (fileEnum.hasMoreElements()) {
				NCFileNode tempfile = (NCFileNode) fileEnum.nextElement();
				Collection<NCFileVO> files = tempfile.getFilemap().values();
				for (NCFileVO ncFileVO : files) {
		 			LinkedHashMap<String, String> fileMap = new LinkedHashMap<String, String>();
					attatchmapList.add(fileMap);

					fileMap.put("name", ncFileVO.getName()); // 附件文件名称
					fileMap.put("type", ncFileVO.getFiletype()); // 文件类型
					fileMap.put("id", ncFileVO.getPk()); // 文件标识
					fileMap.put("size", ""+ncFileVO.getFileLen()); // 文件大小
					fileMap.put("path", ncFileVO.getFullPath()); // 文件路径
					fileMap.put("content", getFileContent(ncFileVO)); // 文件内容
				}
			}
		}
		
		
		return attatchmapList;
	}
	private String getFileContent(NCFileVO ncFileVO) throws BusinessException {
		IFileSystemService fileservice = NCLocator.getInstance().lookup(IFileSystemService.class);
		
		OutputStream out = new ByteArrayOutputStream();
		fileservice.downLoadFile(ncFileVO.getFullPath(), out);
		byte[] downloaded = ((ByteArrayOutputStream) out).toByteArray(); // 附件内容
		BASE64Encoder encoder = new BASE64Encoder();
		String content = encoder.encodeBuffer(downloaded);
		
		return content;
	}
	
}
