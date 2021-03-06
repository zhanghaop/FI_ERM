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
import java.util.List;
import java.util.Map;

import nc.bs.arap.util.BillOrgVUtils;
import nc.bs.erm.util.ErUtil;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Log;
import nc.bs.pub.filesystem.IFileSystemService;
import nc.bs.pub.filesystem.IQueryFolderTreeNodeService;
import nc.bs.pub.filesystem.UploadFileIsExistException;
import nc.erm.mobile.util.BillTypeUtil;
import nc.imag.itf.IImagUtil;
import nc.itf.arap.prv.IBXBillPrivate;
import nc.itf.bd.psn.psndoc.IPsndocQueryService;
import nc.itf.org.IOrgVersionQryService;
import nc.itf.uap.rbac.IUserManageQuery;
import nc.itf.uap.sf.ICreateCorpQueryService;
import nc.pubitf.para.SysInitQuery;
import nc.vo.arap.bx.util.BXParamConstant;
import nc.vo.bd.psn.PsndocVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.pub.BusinessException;
import nc.vo.pub.filesystem.NCFileNode;
import nc.vo.pub.filesystem.NCFileVO;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.pf.workflow.IPFActionName;
import nc.vo.sm.UserVO;
import nc.vo.trade.pub.IBillStatus;
import nc.vo.vorg.DeptVersionVO;

import org.apache.commons.lang.ArrayUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.jdom.JDOMException;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;


public abstract class AbstractErmMobileCtrlBO {
	
	public String getBXbilltype(String userid,String flag) throws BusinessException{
			initEvn(userid);
			return BillTypeUtil.getBXbilltype(userid,flag);
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
	
	/** 
	* 调用启用模块API(非预算调用) 
	* @param pk_group 集团 
	* @param funcode 功能节点 数据来源于dap_dapsystem 
	* @return 
	* @throws BusinessException 
	*/ 
	public static boolean isProductInstalled(String strCorpPK,String pro) { 
		boolean value = false; 
		try { 
			value = NCLocator.getInstance().lookup(ICreateCorpQueryService.class).isEnabled(strCorpPK, pro); 
		} catch (BusinessException e) { 
			ExceptionHandler.consume(e); 
		} 
		return value ; 
	}
	/**
	 * 如果安装了共享服务，则将图像上传到影像，否则上传到附件管理
	 * @param bxpk
	 * @param valuemap
	 * @throws BusinessException
	 * @throws IOException 
	 */
	@SuppressWarnings("unchecked")
	protected void saveAttachment(String bxpk, Map valuemap) throws BusinessException{
		List<Map<String, Object>> attachment = (List<Map<String, Object>>) valuemap.get("attachment");
		if(attachment == null || attachment.isEmpty()){
			return ;
		}
		
		if(isProductInstalled(InvocationInfoProxy.getInstance().getGroupId(),"70")){
			//如果安装了共享服务，则上传到影像
			String userID = InvocationInfoProxy.getInstance().getUserId();
			String[] fileNames = new String[attachment.size()];
			int[] fileSizes = new int[attachment.size()];
			String[] content = new String[attachment.size()];
			for(int i=0 ; i<attachment.size(); i++){
				Map map = attachment.get(i);
				fileNames[i] = (String) map.get("name");// 文件的名称
				fileSizes[i] = Integer.parseInt(map.get("size").toString()); // 文件的大小
				//file是经过base64编码的
				String file = (String) map.get("content");
				content[i] = file;//decoder.decodeBuffer(file).toString();
			}
			boolean success = NCLocator.getInstance().lookup(IImagUtil.class)
					.UploadImag(userID, bxpk, fileNames, fileSizes, content);
			if(!success)
				throw new BusinessException("上传附件失败!");
		}else{
			//否则上传到附件管理
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
					//将String进行解码
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
						service.createNewFileNodeWithStream(parentPath, 
								filename, InvocationInfoProxy.getInstance().getUserId(), in, length);
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
		String actionType = ErUtil.getUnCommitActionCode(PK_ORG);
		  PfUtilPrivate.runAction(actionType, jkbxvo.getParentVO().getDjlxbm(), jkbxvo, null,
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
	public String commitJkbx(String userid,String pk_jkbx,String djlxbm,String flag) throws BusinessException, JSONException {
		  initEvn(userid);
		  JSONObject retjson = new JSONObject();
		  try{
			  // 查询
			  List<JKBXVO> vos = NCLocator.getInstance().
			    lookup(IBXBillPrivate.class).queryVOsByPrimaryKeys(new String[]{pk_jkbx}, null);
			  if(vos == null || vos.isEmpty()){
				  retjson.put("success", "false");
				  retjson.put("message", "单据已被删除，请检查");
				  return retjson.toString();
			  }
			  JKBXVO jkbxvo = vos.get(0);
			  String actionType = IPFActionName.SAVE;//ErUtil.getCommitActionCode(PK_ORG);
			  if("uncommit".equals(flag)){
				  if(jkbxvo.getParentVO().getSpzt().equals(IBillStatus.FREE)){
					  retjson.put("success", "false");
					  retjson.put("message", "该单据尚未提交");
					  return retjson.toString();
				  }else{
					  //执行收回
					  actionType = IPFActionName.UNSAVE;
					  PfUtilPrivate.runAction(actionType, djlxbm, jkbxvo, null,
								null, null, null);
					  retjson.put("success", "true");
					  retjson.put("flag", "uncommit");
					  retjson.put("message", "收回成功");
					  return retjson.toString();
				  }
			  }
			  // 执行提交
			  PfUtilPrivate.runAction(actionType, djlxbm, jkbxvo, null,
						null, null, null);
			  retjson.put("success", "true");
			  retjson.put("flag", "commit");
			  retjson.put("message", "提交成功");
			  return retjson.toString();
			}catch(BusinessException e){
				retjson.put("success", "false");
				retjson.put("message", e.getMessage());
				return retjson.toString();
			}
	}
	
	/**
	 * 删除服务器上的影像
	 * @param bxpk
	 * @param userid
	 * @throws BusinessException
	 */
	protected void deleteAttachmentList(String bxpk,String userid) throws BusinessException{
		
		if(isProductInstalled(InvocationInfoProxy.getInstance().getGroupId(),"70")){
			//如果安装了共享服务，从影像删除图片
		}else{
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
	}
	
	public String getFileNum(String bxpk,String userid) throws BusinessException{
		if(isProductInstalled(InvocationInfoProxy.getInstance().getGroupId(),"00")){
			//如果安装了共享服务，则从影像服务器下载图片
			IImagUtil imageutil = NCLocator.getInstance().lookup(IImagUtil.class);
			try {
				return String.valueOf(imageutil.DownloadImageNumber(userid, bxpk));
			} catch (JDOMException e) {
				return null;
			} catch (IOException e) {
				return null;
			}
		}else{
			IQueryFolderTreeNodeService fileservice = NCLocator.getInstance().lookup(IQueryFolderTreeNodeService.class);
			NCFileNode filenode = fileservice.getNCFileNodeTreeAndCreateAsNeed(bxpk, userid);
			return String.valueOf(filenode.getFileLen());
		}
	}
	
	
	@SuppressWarnings("rawtypes")
	public JSONArray getFileList(String bxpk,String userid) throws BusinessException, JSONException{
		//返回attatchmapList
		JSONArray attacharray = new JSONArray();
		List<Map<String, String>> attatchmapList = new ArrayList<Map<String, String>>();
		if(isProductInstalled(InvocationInfoProxy.getInstance().getGroupId(),"70")){
			//如果安装了共享服务，则从影像服务器下载图片
			try {
				IImagUtil imageutil = NCLocator.getInstance().lookup(IImagUtil.class);
				attatchmapList = imageutil.DownloadImag(userid, bxpk);
				//把影像服务器上的图像转换为手机可识别的
				if(attatchmapList != null && attatchmapList.size()>0){
					for(int i=0; i<attatchmapList.size(); i++){
//						Map<String, String> map = attatchmapList.get(i);
//						String url = map.get("URL");
//						map.put("content", map.get("FILE_VALUE")); // 文件内容
//						map.put("id", url.substring(url.indexOf("file_id=")+8)); // 文件标识
//						map.put("name", map.get("FILE_NAME")); // 附件文件名称
//						map.put("type", map.get("FILE_TYPE")); // 文件类型
//						map.put("path", map.get("URL")); // 文件路径
//						map.put("size", map.get("FILE_SIZE")); // 文件大小
//						map.remove("FILE_VALUE");
//						map.remove("URL");
						
						JSONObject picture = new JSONObject();
						Map<String, String> map = attatchmapList.get(i);
						String url = map.get("URL");
						picture.put("content", map.get("FILE_VALUE")); // 文件内容
						picture.put("id", url.substring(url.indexOf("file_id=")+8)); // 文件标识
						picture.put("name", map.get("FILE_NAME")); // 附件文件名称
						picture.put("type", map.get("FILE_TYPE")); // 文件类型
						picture.put("path", map.get("URL")); // 文件路径
						picture.put("size", map.get("FILE_SIZE")); // 文件大小
						picture.remove("FILE_VALUE");
						picture.remove("URL");
						attacharray.put(picture);
					}
				}
				return attacharray;
			} catch (Exception e) {
				return attacharray;
			}
		}else{
			// 获取附件列表
			IQueryFolderTreeNodeService fileservice = NCLocator.getInstance().lookup(IQueryFolderTreeNodeService.class);
			NCFileNode filenode = fileservice.getNCFileNodeTreeAndCreateAsNeed(bxpk, userid);
			if(filenode != null){
				Enumeration fileEnum = filenode.breadthFirstEnumeration();
				while (fileEnum.hasMoreElements()) {
					NCFileNode tempfile = (NCFileNode) fileEnum.nextElement();
					Collection<NCFileVO> files = tempfile.getFilemap().values();
					for (NCFileVO ncFileVO : files) {
//			 			LinkedHashMap<String, String> fileMap = new LinkedHashMap<String, String>();
//						fileMap.put("name", ncFileVO.getName()); // 附件文件名称
//						fileMap.put("type", ncFileVO.getFiletype()); // 文件类型
//						fileMap.put("id", ncFileVO.getPk()); // 文件标识
//						fileMap.put("size", String.valueOf(ncFileVO.getFileLen())); // 文件大小
//						fileMap.put("path", ncFileVO.getFullPath()); // 文件路径
//						fileMap.put("content", getFileContent(ncFileVO)); // 文件内容
//						attatchmapList.add(fileMap);
						
						JSONObject picture = new JSONObject();
						picture.put("name", ncFileVO.getName()); // 附件文件名称
						picture.put("type", ncFileVO.getFiletype()); // 文件类型
						picture.put("id", ncFileVO.getPk()); // 文件标识
						picture.put("size", String.valueOf(ncFileVO.getFileLen())); // 文件大小
						picture.put("path", ncFileVO.getFullPath()); // 文件路径
						picture.put("content", getFileContent(ncFileVO)); // 文件内容
						attacharray.put(picture);
					}
				}
			}
			return attacharray;
		}
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
