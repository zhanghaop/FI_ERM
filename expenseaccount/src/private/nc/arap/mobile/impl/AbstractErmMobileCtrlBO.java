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
	* ��������ģ��API(��Ԥ�����) 
	* @param pk_group ���� 
	* @param funcode ���ܽڵ� ������Դ��dap_dapsystem 
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
	 * �����װ�˹��������ͼ���ϴ���Ӱ�񣬷����ϴ�����������
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
			//�����װ�˹���������ϴ���Ӱ��
			String userID = InvocationInfoProxy.getInstance().getUserId();
			String[] fileNames = new String[attachment.size()];
			int[] fileSizes = new int[attachment.size()];
			String[] content = new String[attachment.size()];
			for(int i=0 ; i<attachment.size(); i++){
				Map map = attachment.get(i);
				fileNames[i] = (String) map.get("name");// �ļ�������
				fileSizes[i] = Integer.parseInt(map.get("size").toString()); // �ļ��Ĵ�С
				//file�Ǿ���base64�����
				String file = (String) map.get("content");
				content[i] = file;//decoder.decodeBuffer(file).toString();
			}
			boolean success = NCLocator.getInstance().lookup(IImagUtil.class)
					.UploadImag(userID, bxpk, fileNames, fileSizes, content);
			if(!success)
				throw new BusinessException("�ϴ�����ʧ��!");
		}else{
			//�����ϴ�����������
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
					//��String���н���
					in = new ByteArrayInputStream(decoder.decodeBuffer(file));
				} catch (IOException e3) {
					ExceptionHandler.handleException(e3);
				}
				String parentPath = bxpk;
				IFileSystemService service = NCLocator.getInstance().lookup(IFileSystemService.class);
				String filename = (String) map.get("name");// �ļ�������
				String size = (String) map.get("size").toString(); // �ļ��Ĵ�С
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
	 * �ջ��ύ�ĵ���
	 * 
	 * @throws BusinessException
	 */
	protected String commitCancle(String pk_jkbx) throws BusinessException{
		// ����pk��ѯ���� 
		List<JKBXVO> vos = null;
		try {
			vos = NCLocator.getInstance().
			    lookup(IBXBillPrivate.class).queryVOsByPrimaryKeys(new String[]{pk_jkbx}, null);
		} catch (BusinessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	    if(vos == null || vos.isEmpty()){
	        throw new BusinessException("�����ѱ�ɾ��������");
	    }
		JKBXVO jkbxvo = vos.get(0);
		initEvn(jkbxvo.getParentVO().getOperator());
		if(jkbxvo.getParentVO().getSpzt().equals(IBillStatus.FREE))
			return pk_jkbx;
		//�ջ��ύ
		String actionType = ErUtil.getUnCommitActionCode(PK_ORG);
		  PfUtilPrivate.runAction(actionType, jkbxvo.getParentVO().getDjlxbm(), jkbxvo, null,
					null, null, null);
	//	PfUtilPrivate.runAction(null, IPFActionName.UNSAVE
	//		 	+ InvocationInfoProxy.getInstance().getUserId(), jkbxvo.getParentVO().getDjlxbm(), jkbxvo, null,
	//			null, null, null); 
		return pk_jkbx;
	}
	
	/**
	 * �ع���ȡ��汾����id,��ǰ̨����
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
			Log.getInstance(BillOrgVUtils.class).error(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2006pub_0","02006pub-0582")/*@res "~~~~~~~~~~@@@@~~~~~~~~~��ʼ���ò��Ű汾��"*/+pk_org, BillOrgVUtils.class, "setDept_v");
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2006pub_0","02006pub-0583")/*@res "��ѯ���Ű汾���󣬸ò����޿��ð汾!"*/);
		}
	
		for(DeptVersionVO vso:versions){
			if(vso.getEnablestate()!=2){ //�Ƿ����
				continue;
			}
			if((vso.getVstartdate().compareTo(billdate)<=0 &&  vso.getVenddate().compareTo(billdate)>=0)||vso.getVstartdate().isSameDate(billdate)||vso.getVenddate().isSameDate(billdate)){
				pk_org_v=vso.getPk_vid();
			}
		}
	
		// δƥ�䵽���ð汾��ȡ��ʷ����汾
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
	 * �ж��ǹ���������������
	 * @param pk_org
	 * @return
	 */
	protected String getActionCode(String code,String pk_org) {
		if(code.equals(IPFActionName.SAVE)){
			String actionCode = IPFActionName.SAVE;
			try {
				String paraString = SysInitQuery.getParaString(pk_org, BXParamConstant.ER_FLOW_TYPE);
				if (BXParamConstant.ER_FLOW_TYPE_WORKFLOW.equals(paraString)) {
					//����ǹ�������Ӧ����start������
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
					//����ǹ�������Ӧ����start������
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
					//����ǹ�������Ӧ����start������
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
					//����ǹ�������Ӧ����start������
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
			  // ��ѯ
			  List<JKBXVO> vos = NCLocator.getInstance().
			    lookup(IBXBillPrivate.class).queryVOsByPrimaryKeys(new String[]{pk_jkbx}, null);
			  if(vos == null || vos.isEmpty()){
				  retjson.put("success", "false");
				  retjson.put("message", "�����ѱ�ɾ��������");
				  return retjson.toString();
			  }
			  JKBXVO jkbxvo = vos.get(0);
			  String actionType = IPFActionName.SAVE;//ErUtil.getCommitActionCode(PK_ORG);
			  if("uncommit".equals(flag)){
				  if(jkbxvo.getParentVO().getSpzt().equals(IBillStatus.FREE)){
					  retjson.put("success", "false");
					  retjson.put("message", "�õ�����δ�ύ");
					  return retjson.toString();
				  }else{
					  //ִ���ջ�
					  actionType = IPFActionName.UNSAVE;
					  PfUtilPrivate.runAction(actionType, djlxbm, jkbxvo, null,
								null, null, null);
					  retjson.put("success", "true");
					  retjson.put("flag", "uncommit");
					  retjson.put("message", "�ջسɹ�");
					  return retjson.toString();
				  }
			  }
			  // ִ���ύ
			  PfUtilPrivate.runAction(actionType, djlxbm, jkbxvo, null,
						null, null, null);
			  retjson.put("success", "true");
			  retjson.put("flag", "commit");
			  retjson.put("message", "�ύ�ɹ�");
			  return retjson.toString();
			}catch(BusinessException e){
				retjson.put("success", "false");
				retjson.put("message", e.getMessage());
				return retjson.toString();
			}
	}
	
	/**
	 * ɾ���������ϵ�Ӱ��
	 * @param bxpk
	 * @param userid
	 * @throws BusinessException
	 */
	protected void deleteAttachmentList(String bxpk,String userid) throws BusinessException{
		
		if(isProductInstalled(InvocationInfoProxy.getInstance().getGroupId(),"70")){
			//�����װ�˹�����񣬴�Ӱ��ɾ��ͼƬ
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
			//�����װ�˹���������Ӱ�����������ͼƬ
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
		//����attatchmapList
		JSONArray attacharray = new JSONArray();
		List<Map<String, String>> attatchmapList = new ArrayList<Map<String, String>>();
		if(isProductInstalled(InvocationInfoProxy.getInstance().getGroupId(),"70")){
			//�����װ�˹���������Ӱ�����������ͼƬ
			try {
				IImagUtil imageutil = NCLocator.getInstance().lookup(IImagUtil.class);
				attatchmapList = imageutil.DownloadImag(userid, bxpk);
				//��Ӱ��������ϵ�ͼ��ת��Ϊ�ֻ���ʶ���
				if(attatchmapList != null && attatchmapList.size()>0){
					for(int i=0; i<attatchmapList.size(); i++){
//						Map<String, String> map = attatchmapList.get(i);
//						String url = map.get("URL");
//						map.put("content", map.get("FILE_VALUE")); // �ļ�����
//						map.put("id", url.substring(url.indexOf("file_id=")+8)); // �ļ���ʶ
//						map.put("name", map.get("FILE_NAME")); // �����ļ�����
//						map.put("type", map.get("FILE_TYPE")); // �ļ�����
//						map.put("path", map.get("URL")); // �ļ�·��
//						map.put("size", map.get("FILE_SIZE")); // �ļ���С
//						map.remove("FILE_VALUE");
//						map.remove("URL");
						
						JSONObject picture = new JSONObject();
						Map<String, String> map = attatchmapList.get(i);
						String url = map.get("URL");
						picture.put("content", map.get("FILE_VALUE")); // �ļ�����
						picture.put("id", url.substring(url.indexOf("file_id=")+8)); // �ļ���ʶ
						picture.put("name", map.get("FILE_NAME")); // �����ļ�����
						picture.put("type", map.get("FILE_TYPE")); // �ļ�����
						picture.put("path", map.get("URL")); // �ļ�·��
						picture.put("size", map.get("FILE_SIZE")); // �ļ���С
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
			// ��ȡ�����б�
			IQueryFolderTreeNodeService fileservice = NCLocator.getInstance().lookup(IQueryFolderTreeNodeService.class);
			NCFileNode filenode = fileservice.getNCFileNodeTreeAndCreateAsNeed(bxpk, userid);
			if(filenode != null){
				Enumeration fileEnum = filenode.breadthFirstEnumeration();
				while (fileEnum.hasMoreElements()) {
					NCFileNode tempfile = (NCFileNode) fileEnum.nextElement();
					Collection<NCFileVO> files = tempfile.getFilemap().values();
					for (NCFileVO ncFileVO : files) {
//			 			LinkedHashMap<String, String> fileMap = new LinkedHashMap<String, String>();
//						fileMap.put("name", ncFileVO.getName()); // �����ļ�����
//						fileMap.put("type", ncFileVO.getFiletype()); // �ļ�����
//						fileMap.put("id", ncFileVO.getPk()); // �ļ���ʶ
//						fileMap.put("size", String.valueOf(ncFileVO.getFileLen())); // �ļ���С
//						fileMap.put("path", ncFileVO.getFullPath()); // �ļ�·��
//						fileMap.put("content", getFileContent(ncFileVO)); // �ļ�����
//						attatchmapList.add(fileMap);
						
						JSONObject picture = new JSONObject();
						picture.put("name", ncFileVO.getName()); // �����ļ�����
						picture.put("type", ncFileVO.getFiletype()); // �ļ�����
						picture.put("id", ncFileVO.getPk()); // �ļ���ʶ
						picture.put("size", String.valueOf(ncFileVO.getFileLen())); // �ļ���С
						picture.put("path", ncFileVO.getFullPath()); // �ļ�·��
						picture.put("content", getFileContent(ncFileVO)); // �ļ�����
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
		byte[] downloaded = ((ByteArrayOutputStream) out).toByteArray(); // ��������
		BASE64Encoder encoder = new BASE64Encoder();
		String content = encoder.encodeBuffer(downloaded);
		
		return content;
	}
	
}
