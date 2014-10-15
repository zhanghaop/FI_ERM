package nc.bs.er.initdata;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import nc.bs.arap.bx.JKBXDAO;
import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.pub.IInitData;
import nc.bs.sm.accountmanage.AbstractUpdateAccount;
import nc.itf.fi.pub.SysInit;
import nc.jdbc.framework.JdbcSession;
import nc.jdbc.framework.PersistenceManager;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.exception.DbException;
import nc.jdbc.framework.processor.ColumnListProcessor;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.jdbc.framework.processor.ResultSetProcessor;
import nc.md.innerservice.IMetaDataQueryService;
import nc.md.model.IBusinessEntity;
import nc.md.model.MetaDataException;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXParamConstant;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.BXFinItemVO;
import nc.vo.ep.bx.BXHeaderVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKHeaderVO;
import nc.vo.fi.pub.SqlUtils;
import nc.vo.fip.transtemplate.TemplateCellVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.pftemplate.SystemplateBaseVO;

/**
 * @author twei
 * 
 * nc.bs.er.initdata.ErInitDAtaBO
 */
public class ErInitDAtaBO extends AbstractUpdateAccount implements IInitData {// nc.bs.sm.install.IUpdateData{
	
	private static UFDouble ZERO = new UFDouble(0.00);
	
	private BaseDAO dao = null;
	
	private BaseDAO getBaseDAO() {
		if (dao == null) {
			dao = new BaseDAO();
		}
		return dao;
	}

	/**
	 * ArapInitDAta ������ע�⡣
	 */
	public ErInitDAtaBO() {
		super();
	}

	public void initAccountData(String dataSourceName)
			throws nc.bs.pub.InitDataException {
	}

	/**
	 * ���õ�λ��ʱ��Ԥ������
	 */
	public void initCorpData(String pkCorp) throws nc.bs.pub.InitDataException {

	}
	
	public void doBeforeUpdateDB(String oldVersion, String newVersion) throws Exception {
		updateFipItem(newVersion);
		updateSysTemplate(oldVersion,newVersion);
	}
	
	private void updateSysTemplate(String oldVersion, String newVersion) throws DAOException {
		if(is60Update261(oldVersion, newVersion)){
			String sql = "update pub_systemplate set sysflag=1 where (templateflag='Y' or pk_corp='@@@@')  and moduleid='2011' and (nodekey like '264X-%' or nodekey like '263X-%' or funnode like '20110ETEA_%' or funnode like '20110ETLB_%')";
			getBaseDAO().executeUpdate(sql);
			Logger.debug("sql="+sql);
		}
	}

	/**
	 * �Ƿ�60������61�汾
	 * @param oldVersion
	 * @param newVersion
	 */
	private static boolean is60Update261(String oldVersion, String newVersion){
		return oldVersion != null && oldVersion.startsWith("6.0")
				&& newVersion != null && newVersion.startsWith("6.1");
	}

	public void doAfterUpdateData(String oldVersion, String newVersion) throws Exception {
		
		//1.���µ������������Ϣ
		updateBillType(newVersion);
		
		//2.���½�ҵ��ҳǩ����
		updateJKBillTemplateTableCode(newVersion);
		
		//3.����ģ�����
		updateSysTemplet();
		
		//4.���²�ѯģ��
		updateQueryTemplet();
		
		if (!is60Update261(oldVersion, newVersion)) {
			return;
		}
		
		//5.�޸�ƾ֤ģ��
		updateTranstemplate(newVersion);
		
		//6.���³��õ��ݵĶ�汾��Ϣ
		updateInitBillVersion(newVersion);

		//7.���������ݵĲ����н��(ybje,bbje,zfybje,zfbbje,hkybje,hkbbje,cjkybje,cjkbbje,group...bbje,global...bbje��)��̯��ҵ������
		updateFin2BusiItem(newVersion);
		
		//8.�����Զ���ĵ���ģ���Ԫ��������
		updateBillTemplate(newVersion);
		
		//9.���ϲ���ҳǩ
		deleteFinBillTemplate(newVersion);
		
		//10.���½���У�����
		updateCrossChkRule(newVersion);
		
	}
	
	private void updateSysTemplet() throws DAOException {
		String sql = "insert into pub_systemplate_base (dr, funnode, moduleid, nodekey,pk_systemplate, templateid, tempstyle, ts) select dr, funnode, moduleid, nodekey, pk_systemplate, templateid, tempstyle, ts from pub_systemplate where pk_corp='@@@@' and pk_systemplate not in (select pk_systemplate from pub_systemplate_base) and funnode like '2011%'";
		String sql2 = "update pub_systemplate_base set nodekey=null where (funnode like '20110ETEA%' or funnode like '20110ETLB%') and tempstyle=0";
		getBaseDAO().executeUpdate(sql);
		getBaseDAO().executeUpdate(sql2);
	}

	private void deleteFinBillTemplate(String newVersion) throws DAOException{
		Logger.debug("*************************************");
		Logger.debug("******** ��������ģ������6.0������6.1ɾ������ģ�����ҳǩ��ʼ"+getClass().getName()+"**********");
		Logger.debug("*************************************");
		
		String sql = "delete from pub_billtemplet_b  where pk_billtemplet in (select pk_billtemplet from pub_billtemplet " +
				" where nodecode like '2011%' and pk_billtypecode like '26%') and table_code='er_finitem'";
		getBaseDAO().executeUpdate(sql);
		Logger.debug(sql);
		
		sql = "delete from pub_billtemplet_t where pk_billtemplet in (select pk_billtemplet from pub_billtemplet" +
				" where nodecode like '2011%' and pk_billtypecode like '26%') and tabcode='er_finitem'";
		getBaseDAO().executeUpdate(sql);
		Logger.debug(sql);
		
		Logger.debug("*************************************");
		Logger.debug("******** ��������ģ������6.0������6.1ɾ������ģ�����ҳǩ����"+getClass().getName()+"**********");
		Logger.debug("*************************************");
	}
	/**
	 * �����Զ���ĵ���ģ���Ԫ��������
	 * @author chendya
	 * @param newVersion
	 * @throws DAOException
	 */
	private void updateBillTemplate(String newVersion) throws DAOException{
		Logger.debug("*************************************");
		Logger.debug("******** ��������ģ������6.0������6.1���µ���ģ��Ԫ�������Կ�ʼ"+getClass().getName()+"**********");
		Logger.debug("*************************************");
		
		//������
		String sql = "update pub_billtemplet_b  set metadataproperty=replace(metadataproperty,'erm.busitem','erm.er_busitem') " +
				" where pk_billtemplet in (select pk_billtemplet from pub_billtemplet  where nodecode like '2011%' and " +
				" pk_billtypecode like '264%')  and metadataproperty like 'erm.busitem%' ";
		getBaseDAO().executeUpdate(sql);
		Logger.debug(sql);
		
		//��
		sql = "update pub_billtemplet_b  set metadataproperty=replace(metadataproperty,'erm.jkbusitem','erm.jk_busitem') " +
				" where pk_billtemplet in (select pk_billtemplet from pub_billtemplet  " +
				" where nodecode like '2011%' and pk_billtypecode like '263%')  and metadataproperty like 'erm.jkbusitem%'";
		getBaseDAO().executeUpdate(sql);
		Logger.debug(sql);
		
		Logger.debug("*************************************");
		Logger.debug("******** ��������ģ������6.0������6.1���µ���ģ��Ԫ�������Խ���"+getClass().getName()+"**********");
		Logger.debug("*************************************");
	}
	
	/**
	 * �����Զ���ڵ��У���ѯģ������ã�������ʱĬ��Ϊ���ñ������Ĳ�ѯģ�壬��ʱΪ���ý���ѯģ��
	 * @throws DAOException
	 */
	private void updateQueryTemplet() throws BusinessException {
		Logger.debug("*************************************");
		Logger.debug("******** ��������ģ������6.0������6.1���²�ѯģ�忪ʼ"+getClass().getName()+"**********");
		Logger.debug("*************************************");
		
		String sql = "select p.templateid from pub_systemplate_base p where p.funnode in ('20110ETEA') and p.tempstyle = 1 ";
		//���ñ�������ѯģ��ID
		String bxQueryTempletId = (String)getBaseDAO().executeQuery(sql, new ColumnProcessor());
		//���ý���ѯģ��ID
		sql = "select p.templateid from pub_systemplate_base p where p.funnode in ('20110ETLB') and p.tempstyle = 1 ";
		String jkQueryTempletId = (String)getBaseDAO().executeQuery(sql, new ColumnProcessor());
		
		sql = " funnode like '2011%' and templateid = '1001Z31000000001CIXD' and tempstyle = 1 ";
		@SuppressWarnings("unchecked")
        Collection<SystemplateBaseVO> needReplaceVo = getBaseDAO().retrieveByClause(SystemplateBaseVO.class, sql);
		
		if(needReplaceVo != null && needReplaceVo.size() > 0){
		    //begin������ѯ
		    List<String> codeList = new LinkedList<String>();
		    for (@SuppressWarnings("rawtypes")
            Iterator iterator = needReplaceVo.iterator(); iterator.hasNext();) {
		        SystemplateBaseVO systemplateBaseVO = (SystemplateBaseVO) iterator.next();
                String funcode = systemplateBaseVO.getFunnode();
                codeList.add(funcode);
		    }
            String sWhere = SqlUtils.getInStr("funcode", codeList, true);
            //������Ϣ��¼����
            final Map<String, String> mapInfo = new HashMap<String, String>();
	        String sqlReg = "select fun_desc, fun_name, funcode from sm_funcregister where " + sWhere;
	        getBaseDAO().executeQuery(sqlReg, new ResultSetProcessor() {
                private static final long serialVersionUID = 1L;

                public Object handleResultSet(ResultSet rs) throws SQLException {
                    while (rs.next()) {
                        String o = rs.getString(1) + rs.getString(2);
                        mapInfo.put(rs.getString(3), o);
                    }
                    return null;
                }
            });
            //end������ѯ
	        
			for (@SuppressWarnings("rawtypes")
            Iterator iterator = needReplaceVo.iterator(); iterator.hasNext();) {
				SystemplateBaseVO systemplateBaseVO = (SystemplateBaseVO) iterator.next();
				String funcode = systemplateBaseVO.getFunnode();
				
//				String temp = (String)getBaseDAO().executeQuery("select fun_desc, fun_name from sm_funcregister  where funcode = '" + funcode + "' ", new ResultSetProcessor() {
//					private static final long serialVersionUID = 1L;
//
//					public Object handleResultSet(ResultSet rs) throws SQLException {
//						String o = "";
//						if (rs.next()) {
//							o = rs.getString(1) + rs.getString(2);
//						}
//						return o;
//					}
//				});
				String temp = mapInfo.get(funcode);
				if( temp!= null && temp.contains("���")){/*-=notranslate=-*/
					systemplateBaseVO.setTemplateid(jkQueryTempletId);
//				}else if(temp!= null && temp.contains("����")){/*-=notranslate=-*/
//					systemplateBaseVO.setTemplateid(bxQueryTempletId);
				}else {
					systemplateBaseVO.setTemplateid(bxQueryTempletId);
				}
			}
			
			getBaseDAO().updateVOArray(needReplaceVo.toArray(new SystemplateBaseVO[0]));
		}
		
		Logger.debug("*************************************");
		Logger.debug("******** ��������ģ������6.0������6.1���²�ѯģ�����"+getClass().getName()+"**********");
		Logger.debug("*************************************");
	}
	
	/**
	 * ����ERX����Ĳ����ж�ҵ���еĶ��չ�ϵ
	 */
	private Map<String,Map<String,String>> ermGroupBusi2FinParamMap = new HashMap<String, Map<String,String>>();
	
	/**
	 * �������Ͷ�Ӧ�ļ���Pks
	 */
	private Map<String,List<String>> ermDjlxGroupPKMap = new HashMap<String, List<String>>();
	
	/**
	 * @since v6.1
	 * @author chendya
	 * @throws Exception
	 */
	private void updateTranstemplate(String newVersion) throws Exception{
		Logger.debug("*************************************");
		Logger.debug("******** ��������ģ������6.0������6.1��ʼ����ƾ֤ģ�濪ʼ"+getClass().getName()+"**********");
		Logger.debug("*************************************");
		if (newVersion != null && newVersion.startsWith("6.1")) {
			List<String> ermGroupPKs = getErmGroupPK(BXConstans.BX_DJDL);
			for(String pk_group : ermGroupPKs){
				updateFipTemplateCell(pk_group);
			}
		}
		
		BaseDAO baseDAO = new BaseDAO();
		baseDAO.executeUpdate("update fip_templatecell set cellvalue=replace(cellvalue,'jk_finitem','jk_busitem') where pk_templaterow in (select pk_templaterow from  fip_templaterow where pk_transtemplate in (select pk_transtemplate from fip_transtemplate where src_billtype in (select distinct pk_billtypecode  from bd_billtype where systemcode  in ('erm') and src_billtype like '263%')))"); 
		baseDAO.executeUpdate("update fip_templatecell set cellvalue=replace(cellvalue,'er_finitem','er_busitem') where pk_templaterow in (select pk_templaterow from  fip_templaterow where pk_transtemplate in (select pk_transtemplate from fip_transtemplate where src_billtype in (select distinct pk_billtypecode  from bd_billtype where systemcode  in ('erm') and src_billtype like '264%')))");
		
		Logger.debug("*************************************");
		Logger.debug("******** ��������ģ������6.0������6.1��ʼ����ƾ֤ģ�����"+getClass().getName()+"**********");
		Logger.debug("*************************************");
	}
	
	/**
	 * @since v6.1
	 * @author chendya
	 * @param pk_group
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private void updateFipTemplateCell(String pk_group) throws Exception{
		//1.�������е�TemplateCellVO��CELLVALUE����ҵ����յ��и���
		{
			Map<String, String> map = getBusi2FinParamMap(pk_group);
			if(map.size()==0){
				return;
			}
			//��ѯ�����Զ�����Ĳ�����
			String condition = " pk_templaterow in (select pk_templaterow from fip_templaterow where pk_transtemplate in " 
				+ " (select pk_transtemplate from fip_transtemplate where src_billtype in " 
				+ " (select distinct pk_billtypecode from bd_billtype where systemcode='erm') and isnull(dr,0)=0 and pk_group='"+pk_group+"')) " 
				+ " and cellvalue like '$er_finitem.defitem%@' ";
			List<TemplateCellVO> finItemTemplateCellVOList = (List<TemplateCellVO>) getBaseDAO().retrieveByClause(TemplateCellVO.class, condition, new String[]{TemplateCellVO.CELLVALUE});
			if(finItemTemplateCellVOList != null && finItemTemplateCellVOList.size() > 0){
				List<TemplateCellVO> updateVOList = new ArrayList<TemplateCellVO>();
				Map<String,String> fin2BusiDefItemMap = new HashMap<String, String>();
				for(TemplateCellVO vo : finItemTemplateCellVOList){
					String cellvalue = vo.getCellvalue();
					cellvalue = cellvalue.replace(".", "#");
					String[] values = cellvalue.split("#");
					String prefix = values[0];
					final String suffix = "@";
					String defItemKey = values[1].substring(0,values[1].indexOf(suffix));
					String busiDefItem = null;
					if(fin2BusiDefItemMap.containsKey(defItemKey)){
						busiDefItem = fin2BusiDefItemMap.get(defItemKey); 
					}else{
						fin2BusiDefItemMap.put(defItemKey, getBusiDefItem(defItemKey,map));
					}
					if(busiDefItem != null){
						//��ҵ�����
						String newCellValue = prefix + busiDefItem + suffix;
						vo.setCellvalue(newCellValue);
						updateVOList.add(vo);
					}
				}
				int count = getBaseDAO().updateVOArray((TemplateCellVO[])updateVOList.toArray(new TemplateCellVO[0]), new String[]{TemplateCellVO.CELLVALUE});
				Logger.debug("====================================����ҵ����ղ����¼����count="+count+"==================================");
			}
		}
		
		//2.�����а���������Ϣ���滻��ҵ����Ϣ
		{
			String sql = "update fip_templatecell " 
				+ " set cellvalue=replace(cellvalue,'er_finitem','er_busitem') " 
				+ " where exists (select 1 from fip_templaterow fr "
				+ " where fr.pk_templaterow = fip_templatecell.pk_templaterow and exists (select 1 from fip_transtemplate ft "
				+ " where ft.pk_transtemplate = fr.pk_transtemplate and ft.src_billtype in " 
				+ " (select distinct bt.pk_billtypecode from bd_billtype bt where bt.systemcode = 'erm' and bt.pk_group=?) and isnull(ft.dr, 0) = 0)) "
				+ " and cellvalue like '$er_finitem.%' and isnull(dr, 0) = 0 ";
			final SQLParameter param = new SQLParameter();
			param.addParam(pk_group);
			//����
			int count = getBaseDAO().executeUpdate(sql,param);
			sql = "update fip_templatecell " 
				+ " set cellvalue=replace(cellvalue,'jk_finitem','jk_busitem') " 
				+ " where exists (select 1 from fip_templaterow fr "
				+ " where fr.pk_templaterow = fip_templatecell.pk_templaterow and exists (select 1 from fip_transtemplate ft "
				+ " where ft.pk_transtemplate = fr.pk_transtemplate and ft.src_billtype in " 
				+ " (select distinct bt.pk_billtypecode from bd_billtype bt where bt.systemcode = 'erm' and bt.pk_group=?) and ft.dr = 0)) "
				+ " and cellvalue like '$jk_finitem.%' and dr = 0 ";
			//���
			count = getBaseDAO().executeUpdate(sql,param);
			Logger.debug("sql="+sql);
			Logger.debug("====================================���²����м�¼����count="+count+"==================================");
		}
	}
	
	/**
	 * @since v6.1
	 * @author chendya
	 * @param finDefItem
	 * @param map
	 * @return
	 */
	private static String getBusiDefItem(String finDefItem , Map<String, String> map){
		Iterator<Entry<String, String>> iter = map.entrySet().iterator();
		while(iter.hasNext()){
			Entry<String, String> entry = iter.next();
			String value = entry.getValue();
			String busiDefItem = entry.getKey();
			if(finDefItem.equals(value)){
				return busiDefItem;
			}
		}
		return null;
	}
	
	/**
	 * @since v6.1
	 * @author chendya
	 * ����6.0�Ĳ���ҳǩ��Ϣ����������Ϣ�Ľ���̯��ҵ����Ϣ�� 
	 * @throws Exception
	 */
	private void updateFin2BusiItem(String newVersion) throws Exception{
		Logger.debug("*************************************");
		Logger.debug("******** ��������ģ������6.0������6.1���²���ҵ��ҳǩ��ҵ��ҳǩ���÷�̯��ʼ"+getClass().getName()+"**********");
		Logger.debug("*************************************");
		if (newVersion != null && newVersion.startsWith("6.1")) {
			updateFin2BusiData(BXConstans.JK_DJDL);
			updateFin2BusiData(BXConstans.BX_DJDL);
		}
		Logger.debug("*************************************");
		Logger.debug("******** ��������ģ������6.0������6.1���²���ҵ��ҳǩ��ҵ��ҳǩ���÷�̯����"+getClass().getName()+"**********");
		Logger.debug("*************************************");
	}
	
	private void updateFin2BusiData(String djdl) throws Exception{
		//�н������ݵļ��Ų�����
		List<String> groupPKs = getErmGroupPK(djdl);
		if(groupPKs==null||groupPKs.size()==0){
			return;
		}
		List<BXBusItemVO> updateBusiItemVOList = new ArrayList<BXBusItemVO>();
		for(String pk_group : groupPKs){
			List<JKBXHeaderVO> headVOs = new JKBXDAO().queryHeadersByWhereSql(" where pk_group='"+pk_group+"'", djdl);
			for(JKBXHeaderVO vo : headVOs){
				List<BXFinItemVO> finItemVOs = queryFinItemVOs(vo.getPk_jkbx());
				if(finItemVOs == null || finItemVOs.size() == 0){
					continue;
				}
				List<BXBusItemVO> busiItemVOs = queryBusiItemVOs(vo.getPk_jkbx());
				if(finItemVOs.size()==0 && busiItemVOs.size()>0){
					Logger.error("==================���ݺ�Ϊ:"+vo.getDjbh()+"�ĵ���û�в�����Ϣ����ҵ����Ϣ,δ���Ϸ�����,���ŵ�������ʧ��============================");
					break;
				}
				//�ȼ�¼���ٸ��µ�ҵ���е�ԭ�ҽ��Ϊamount���VO
				for(BXBusItemVO busiItemVO : busiItemVOs){
					busiItemVO.setYbje(busiItemVO.getAmount());
					updateBusiItemVOList.add(busiItemVO);
				}
				//1.�ҳ�������Ҳҵ���еĶ��չ�ϵ
				Map<BXFinItemVO, List<BXBusItemVO>> map = createFin2BusiMap(finItemVOs,busiItemVOs,getBusi2FinParamMap(pk_group));
				
				//2.��̯����
				balanceFee(map);
			}
		}
		//��������ҵ�����ֶ�
		getBaseDAO().updateVOArray((BXBusItemVO[])updateBusiItemVOList.toArray(new BXBusItemVO[0]), 
				new String[]{				
				//ԭ���ֶ�
				BXBusItemVO.YBJE,BXBusItemVO.BBJE,
				BXBusItemVO.ZFYBJE,BXBusItemVO.ZFBBJE,
				BXBusItemVO.HKYBJE, BXBusItemVO.HKBBJE,
				BXBusItemVO.CJKYBJE, BXBusItemVO.CJKBBJE,
				
				//�����ֶ�
				BXBusItemVO.GROUPBBJE,BXBusItemVO.GLOBALBBJE,
				BXBusItemVO.GROUPZFBBJE,BXBusItemVO.GLOBALZFBBJE,
				BXBusItemVO.GROUPHKBBJE,BXBusItemVO.GLOBALHKBBJE,
				BXBusItemVO.GROUPCJKBBJE,BXBusItemVO.GLOBALCJKBBJE
		});
	}
	/**
	 * ��̯����
	 * @param map
	 */
	private static void balanceFee(Map<BXFinItemVO, List<BXBusItemVO>> map){
		Iterator<Entry<BXFinItemVO, List<BXBusItemVO>>> iter = map.entrySet().iterator();
		while(iter.hasNext()){
			Entry<BXFinItemVO, List<BXBusItemVO>> entry = iter.next();
			BXFinItemVO finItemVO = entry.getKey();
			List<BXBusItemVO> busiItemVOs = entry.getValue();
			
			//��̯����ԭ�ҽ��(ͬʱ����֧���򻹿�ԭ�ҽ��)
			balanceCjkybje(finItemVO,busiItemVOs);
			
			//��̯ԭ�ҶԱ��ң����ű��ң�ȫ�ֱ��ҵĽ��
			Map<String,String> ybField2bbFieldMap = new HashMap<String, String>();
			
			//����ԭ�ҽ���̯���ҽ��
			ybField2bbFieldMap.put(BXBusItemVO.YBJE, BXBusItemVO.BBJE);
			ybField2bbFieldMap.put(BXBusItemVO.ZFYBJE, BXBusItemVO.ZFBBJE);
			ybField2bbFieldMap.put(BXBusItemVO.HKYBJE, BXBusItemVO.HKBBJE);
			ybField2bbFieldMap.put(BXBusItemVO.CJKYBJE, BXBusItemVO.CJKBBJE);
			balanceBbje(finItemVO,busiItemVOs,ybField2bbFieldMap);
			
			//����ԭ�ҽ���̯���ű��ҽ��
			ybField2bbFieldMap.clear();
			ybField2bbFieldMap.put(BXBusItemVO.YBJE, BXBusItemVO.GROUPBBJE);
			ybField2bbFieldMap.put(BXBusItemVO.ZFYBJE, BXBusItemVO.GROUPZFBBJE);
			ybField2bbFieldMap.put(BXBusItemVO.HKYBJE, BXBusItemVO.GROUPHKBBJE);
			ybField2bbFieldMap.put(BXBusItemVO.CJKYBJE, BXBusItemVO.GROUPCJKBBJE);
			balanceBbje(finItemVO,busiItemVOs,ybField2bbFieldMap);
			
			//����ԭ�ҽ���̯ȫ�ֱ��ҽ��
			ybField2bbFieldMap.clear();
			ybField2bbFieldMap.put(BXBusItemVO.YBJE, BXBusItemVO.GLOBALBBJE);
			ybField2bbFieldMap.put(BXBusItemVO.ZFYBJE, BXBusItemVO.GLOBALZFBBJE);
			ybField2bbFieldMap.put(BXBusItemVO.HKYBJE, BXBusItemVO.GLOBALHKBBJE);
			ybField2bbFieldMap.put(BXBusItemVO.CJKYBJE, BXBusItemVO.GLOBALCJKBBJE);
			balanceBbje(finItemVO,busiItemVOs,ybField2bbFieldMap);
			
		}
	}
	
	private static void balanceCjkybje(BXFinItemVO finItemVO , List<BXBusItemVO> busiItemVOs){
		//�����г�����
		UFDouble fin_cjkybje_total = finItemVO.getCjkybje();
		if(fin_cjkybje_total==null){
			fin_cjkybje_total = ZERO;
		}
		//�Ƿ��й�����
		boolean hasCjk = true;
		if(fin_cjkybje_total==null||fin_cjkybje_total.equals(ZERO)){
			//û�г������̯
			hasCjk = false;
		}
		//ÿ�η�̯��ʣ��ĳ�����
		UFDouble left_fin_cjkybje = fin_cjkybje_total;
		
		//�г�����������¼�Ƿ������̯������
		//�˱�����ã� (����:�����г�����80,ҵ������3��, ��һ��ԭ�ҽ��100,�ڶ���ԭ�ҽ��20,����ʱ��һ�г���80�󣬵ڶ��оͲ���������)
		boolean isContinueCjk = true;
		int index = 0;
		for(BXBusItemVO busItemVO : busiItemVOs){
			if(!hasCjk){
			//1.û�г�������
				UFDouble busi_ybje = busItemVO.getYbje();
				busItemVO.setCjkybje(ZERO);
				busItemVO.setZfybje(busi_ybje);
				busItemVO.setHkybje(ZERO);
				continue;
			}
			//2.�г�������
			UFDouble busi_ybje = busItemVO.getYbje();
			if(isContinueCjk){
				//ԭ�ҽ��
				busItemVO.setYbje(busItemVO.getAmount());
				if(left_fin_cjkybje.compareTo(busi_ybje)>=0){
					//������ʣ��ĳ�������ڻ���ڵ�ǰ�е�ԭ�ҽ��,�����һ��ȫ������������еĳ�����ȡ���е�ԭ�ҽ�����û��֧���򻹿�
					busItemVO.setCjkbbje(busi_ybje);
					busItemVO.setZfybje(ZERO);
					//ʣ�������
					left_fin_cjkybje = left_fin_cjkybje.sub(busi_ybje);
					if(index == busiItemVOs.size()-1){
						//û�����ٿɳ�����
						busItemVO.setHkybje(left_fin_cjkybje);
						break;
					}
				}else{
					//������ʣ��ĳ�����С�ڵ�ǰ�е�ԭ�ҽ�����еĳ�����ֱ��ȡ���µĳ�������Ҳ��ڼ�������һ�г���
					busItemVO.setCjkybje(left_fin_cjkybje);
					busItemVO.setZfybje(busi_ybje.sub(left_fin_cjkybje));
					busItemVO.setHkybje(ZERO);
					//��һ�в��ٳ�����
					isContinueCjk = false;
				}
			}else{
				//���������
				busItemVO.setCjkybje(ZERO);
				busItemVO.setZfybje(busi_ybje);
				busItemVO.setHkybje(ZERO);
			}
			index++;
		}
	}
	/**
	 * ����ԭ�ҽ���̯���ҽ��
	 * @param finItemVO
	 * @param busiItemVOs
	 * @param yb_bbFieldMap
	 */
	private static void balanceBbje(BXFinItemVO finItemVO,List<BXBusItemVO> busiItemVOs,Map<String,String> yb_bbFieldMap){
		int index = 0;
		//��¼yb_bbFieldMap��ÿһ��entry��bbjeβ��
		Map<String, UFDouble> map = new HashMap<String, UFDouble>();
		for (BXBusItemVO busiItemVO : busiItemVOs) {
			Iterator<Entry<String, String>> iter = yb_bbFieldMap.entrySet().iterator();
			while (iter.hasNext()) {
				Entry<String, String> entry = iter.next();
				String ybjeField = entry.getKey();
				String bbjeField = entry.getValue();
				UFDouble fin_ybje_total = (UFDouble) finItemVO.getAttributeValue(ybjeField);
				if(fin_ybje_total==null){
					fin_ybje_total = ZERO;
				}
				UFDouble fin_bbje_total = (UFDouble) finItemVO.getAttributeValue(bbjeField);
				if(fin_bbje_total==null){
					fin_bbje_total = ZERO;
				}
				if(fin_ybje_total.compareTo(ZERO)==0 || fin_bbje_total.compareTo(ZERO)==0){
					//ԭ�ҽ��Ϊ0���򱾱ҽ��ҲΪ0
					busiItemVO.setAttributeValue(bbjeField, ZERO);
					continue;
				}
				//�����б��ҽ��
				if (busiItemVOs.size() == 1) {
					//ҵ���о�һ�е����
					busiItemVO.setAttributeValue(bbjeField, fin_bbje_total);
				} else if (index == busiItemVOs.size() - 1) {
					// ��β������һ��
					busiItemVO.setAttributeValue(bbjeField, map.get(bbjeField));
				} else {
					UFDouble busi_ybje = (UFDouble) busiItemVO.getAttributeValue(ybjeField);
					if(busi_ybje==null){
						busi_ybje = ZERO;
					}
					// ���ҽ�ԭ�ҽ���������
					UFDouble busi_bbje = busi_ybje.div(fin_ybje_total).multiply(fin_bbje_total);
					// ʣ����
					UFDouble left = fin_ybje_total;
					if (map.containsKey(bbjeField)) {
						left = map.get(bbjeField).sub(busi_bbje);
						map.put(bbjeField, left);
					} else {
						left = left.sub(busi_bbje);
						map.put(bbjeField, left);
					}
					busiItemVO.setAttributeValue(bbjeField, busi_bbje);
				}
			}
			index++;
		}
	}
	
	/**
	 * �����ж���ҵ����
	 * @param finItemVOs
	 * @param busiItemVOs
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	private static Map<BXFinItemVO, List<BXBusItemVO>> createFin2BusiMap(List<BXFinItemVO> finItemVOs,
			List<BXBusItemVO> busiItemVOs,Map<String,String> paramMap)
		throws Exception{
		Map<BXFinItemVO, List<BXBusItemVO>> map = new HashMap<BXFinItemVO, List<BXBusItemVO>>();
		Set<Entry<String, String>> entrySet = paramMap.entrySet();
		for(BXFinItemVO finItemVO: finItemVOs){
			List<BXBusItemVO> list = new ArrayList<BXBusItemVO>();
			for(BXBusItemVO busiItemVO : busiItemVOs){
				//��ǰҵ�����Ƿ��ǲ�����Ҫƥ�����
				boolean belong = true;
				for(Entry<String,String> entry : entrySet){
					String busiItemVOKey = entry.getKey();
					String finItemVOKey = entry.getValue();
					Object busiItemValue = busiItemVO.getAttributeValue(busiItemVOKey);
					Object finItemValue = finItemVO.getAttributeValue(finItemVOKey);
					if(busiItemValue==null){
						busiItemValue = "null";
					}
					if(finItemValue==null){
						finItemValue = "null";
					}
					if(!busiItemValue.equals(finItemValue)){
						//��ǰ���Զ�Ӧֵ�����,����������ƥ����������,��ǰҵ���в��ǲ�����Ҫƥ�����
						belong = false;
						break;
					}
				}
				if(!belong){
					continue;
				}
				list.add(busiItemVO);
			}
			map.put(finItemVO, list);
		}
		return map;
	}
	
	/**
	 * ���ز����ֶκ�ҵ���ֶζ��չ�ϵ
	 * @param pk_group
	 * @return
	 */
	public Map<String,String> getBusi2FinParamMap(String pk_group) throws Exception{
		if(!ermGroupBusi2FinParamMap.containsKey(pk_group)){
			String isDef = "defitem";
			Map<String, String> map = new HashMap<String, String>();
			map.put(BXBusItemVO.SZXMID, BXBusItemVO.SZXMID);
			String paraString = SysInit.getParaString(pk_group, BXParamConstant.PARAM_FIELD_BUSI2FIN);
			if (paraString != null) {
				String[] paramValues = paraString.split(";");
				for (String str : paramValues) {
					String[] values = str.split(":");
					if (values != null && values.length == 2) {
						String busiItemVODef = values[0];
						String finItemVODef = values[1];
						try {
							Integer.parseInt(busiItemVODef);
							busiItemVODef = isDef + busiItemVODef;
						} catch (NumberFormatException e1) {
						}
						try {
							Integer.parseInt(finItemVODef);
							finItemVODef = isDef + finItemVODef;
						} catch (NumberFormatException e1) {
						}
						map.put(busiItemVODef, finItemVODef);
					}
				}
			}
			ermGroupBusi2FinParamMap.put(pk_group, map);
		}
		return ermGroupBusi2FinParamMap.get(pk_group);
	}
	
	@SuppressWarnings("unchecked")
	private List<BXBusItemVO> queryBusiItemVOs(String pk_jkbx) throws Exception{
		return (List<BXBusItemVO>) getBaseDAO().retrieveByClause(BXBusItemVO.class, "pk_jkbx='"+pk_jkbx+"'");
	}
	
	@SuppressWarnings("unchecked")
	private List<BXFinItemVO> queryFinItemVOs(String pk_jkbx) throws Exception{
		return (List<BXFinItemVO>) getBaseDAO().retrieveByClause(BXFinItemVO.class, "pk_jkbx='"+pk_jkbx+"'");
	}

	@SuppressWarnings("unchecked")
	private List<String> getErmGroupPK(String djdl) throws Exception {
		if(!ermDjlxGroupPKMap.containsKey(djdl)){
			String headTableName = new BXHeaderVO().getTableName();
			final String finItemTableName = BXFinItemVO.getDefaultTableName();
			if(BXConstans.JK_DJDL.equals(djdl)){
				headTableName = new JKHeaderVO().getTableName();
			}
			StringBuffer sql = new StringBuffer();
			sql.append(" select a.pk_group from "+headTableName+" a ");
			//����������(û�в����еĵ���ֱ�ӹ���)
			sql.append(" inner join "+finItemTableName + " b ");
			sql.append(" on a.pk_jkbx=b.pk_jkbx ");
			sql.append(" where ");
			sql.append(" a.djdl='"+djdl+"' ");
			sql.append(" and a.dr=0 ");
			sql.append(" and b.dr=0 ");
			sql.append(" group by a.pk_group ");
			List<String> list = (List<String>) getBaseDAO().executeQuery(sql.toString(), new ColumnListProcessor());
			ermDjlxGroupPKMap.put(djdl, list);
		}
		return ermDjlxGroupPKMap.get(djdl);
	}
	

	private void updateJKBillTemplateTableCode(String newVersion) throws Exception {
		Logger.debug("*************************************");
		Logger.debug("******** ��������ģ������6.0������6.1��ʼ���½��ͳ��õ��ݵ�ҵ�������ݵ�ҳǩ���뿪ʼ"+getClass().getName()+"**********");
		Logger.debug("*************************************");
		
		if (newVersion != null && newVersion.startsWith("6.1")) {
			//6.1ר��
			String sql = "update er_busitem set tablecode='jk_busitem' where tablecode='arap_bxbusitem' and pk_jkbx " 
				+ " in (select a.pk_jkbx from (select pk_jkbx from er_jkzb where dr=0 " 
				+ " union select pk_jkbx from er_jkbx_init where djdl='jk' and dr=0 ) a )";
			getBaseDAO().executeUpdate(sql);
			Logger.debug("sql="+sql);
		}
		
		Logger.debug("*************************************");
		Logger.debug("******** ��������ģ������6.0������6.1��ʼ���½��ͳ��õ���ҵ�������ݵ�ҳǩ�������"+getClass().getName()+"**********");
		Logger.debug("*************************************");
	}
	
	private void updateBillType(String newVersion) throws Exception {
		Logger.debug("*************************************");
		Logger.debug("******** ��������ģ������6.0������6.1��ʼ���µ������͵�DAO��CheckClass��ʼ"+getClass().getName()+"**********");
		Logger.debug("*************************************");
		
		if (newVersion != null && newVersion.startsWith("6.1")) {
			//6.1ר��
			String sql = "update bd_billtype set accountclass='nc.bs.arap.bx.JKBXDAO',checkclassname='nc.bs.arap.bx.JKBXDAO' where systemcode='erm'";
			getBaseDAO().executeUpdate(sql);
			Logger.debug("sql="+sql);
		}

		Logger.debug("*************************************");
		Logger.debug("******** ��������ģ������6.0������6.1��ʼ���µ������͵�DAO��CheckClass����"+getClass().getName()+"**********");
		Logger.debug("*************************************");
	}
	
	private void updateInitBillVersion(String newVersion) throws Exception {
		Logger.debug("*************************************");
		Logger.debug("******** ��������ģ������6.0������6.1��ʼ���³��õ��ݶ�汾��Ϣ��ʼ"+getClass().getName()+"**********");
		Logger.debug("*************************************");
		
		if (newVersion != null && newVersion.startsWith("6.1")) {
			//6.1ר��
			
			//������λ��汾
			String sql = "update er_jkbx_init set pk_org_v=(select org_orgs.pk_vid from org_orgs where org_orgs.pk_org=er_jkbx_init.pk_org) where isnull(pk_org_v,'~')='~'";
			getBaseDAO().executeUpdate(sql);
			Logger.debug("sql="+sql);
			
			//���óе���λ��汾
			sql = "update er_jkbx_init set fydwbm_v=(select org_orgs.pk_vid from org_orgs where org_orgs.pk_org=er_jkbx_init.fydwbm) where isnull(fydwbm_v,'~')='~'";
			getBaseDAO().executeUpdate(sql);
			Logger.debug("sql="+sql);
			
			//�����˵�λ��汾
			sql = "update er_jkbx_init set dwbm_v=(select org_orgs.pk_vid from org_orgs where org_orgs.pk_org=er_jkbx_init.dwbm) where isnull(dwbm_v,'~')='~'";
			getBaseDAO().executeUpdate(sql);
			Logger.debug("sql="+sql);
			
			//���óе����Ŷ�汾
			sql = "update er_jkbx_init set fydeptid_v=(select pk_vid from org_dept where pk_dept=er_jkbx_init.fydeptid) where isnull(fydeptid_v,'~')='~'";
			getBaseDAO().executeUpdate(sql);
			Logger.debug("sql="+sql);
			
			//�����˲��Ŷ�汾
			sql = "update er_jkbx_init set deptid_v=(select pk_vid from org_dept where pk_dept=er_jkbx_init.deptid) where deptid_v='~'";
			getBaseDAO().executeUpdate(sql);
			Logger.debug("sql="+sql);
			
			//�Զ���ģ���еĵ�λ������ת��Ϊ��汾��ʾ��ԭ��λ����������Ϊ���ɼ�
			sql="update pub_billtemplet_b  set  showflag='1', listshowflag='Y' where pk_billtemplet in " +
					" (SELECT pk_billtemplet from pub_billtemplet where bill_templetcaption<>'SYSTEM' " +
					" AND nodecode like '2011%' and pk_corp<>'@@@@')  and itemkey in ('pk_org_v','fydwbm_v','dwbm_v','fydeptid_v','deptid_v')";
			getBaseDAO().executeUpdate(sql);
			Logger.debug("sql="+sql);
			
			sql="update pub_billtemplet_b set  showflag='0',listshowflag='N' where pk_billtemplet in " +
					" (SELECT pk_billtemplet from pub_billtemplet where bill_templetcaption<>'SYSTEM' AND nodecode like '2011%' and pk_corp<>'@@@@') " +
					" and itemkey in ('pk_org','fydwbm','dwbm','fydeptid','deptid')";
			getBaseDAO().executeUpdate(sql);
			Logger.debug("sql="+sql);
		}
		
		Logger.debug("*************************************");
		Logger.debug("******** ��������ģ������6.0������6.1��ʼ���³��õ��ݶ�汾��Ϣ����"+getClass().getName()+"**********");
		Logger.debug("*************************************");
	}

	private void updateFipItem(String newVersion) throws DAOException {
		Logger.debug("*************************************");
		Logger.debug("******** ��������ģ������6.0������6.1֮ǰ���ϱ�������ԭ��ƾ֤ģ�濪ʼ"+getClass().getName()+"**********");
		Logger.debug("*************************************");
		
		if (newVersion != null && newVersion.startsWith("6.1")) {
			// 6.1ר��
			String sql = "delete from fip_billitem where pk_billtype in ('263X','264X')  and  (attrcode not like 'zb.%' and  attrcode not like 'fb.%') ";
			getBaseDAO().executeUpdate(sql);
			Logger.debug("sql="+sql);
		}
		Logger.debug("*************************************");
		Logger.debug("******** ��������ģ������6.0������6.1֮ǰ���ϱ�������ԭ��ƾ֤ģ�����"+getClass().getName()+"**********");
		Logger.debug("*************************************");
	}
	
	/**
	 * �����������ҵ���е�Ԫ����ID
	 */
	private Map<String,String> ermMetaMap = new HashMap<String, String>();
	
	private String getBodyBusitemMetaDataID(String djdl) throws MetaDataException {
		if(!ermMetaMap.containsKey(djdl)){
			final String nameSpace = "erm";
			String entityName = "er_busitem";
			if (djdl.startsWith(BXConstans.JK_PREFIX)) {
				entityName = "jk_busitem";
			}
			IBusinessEntity entity = NCLocator.getInstance().lookup(IMetaDataQueryService.class).getBusinessEntityByName(nameSpace, entityName);
			ermMetaMap.put(djdl, entity.getID());
		}
		return ermMetaMap.get(djdl);
	}
	
	private void updateCrossChkRule(String newVersion) throws Exception{
		
		Logger.debug("*************************************");
		Logger.debug("******** ��������ģ������6.0������6.1֮����н���У�����ʼ"+getClass().getName()+"**********");
		Logger.debug("*************************************");
		
		String sql = "select distinct fr.pk_billtype from fipub_rulebind fr where (fr.pk_billtype like '263%' or fr.pk_billtype like '264%')";
		List list = (ArrayList)getBaseDAO().executeQuery(sql, new ColumnListProcessor());
		Logger.debug(sql);
		if (list == null || list.size() == 0) {
			return;
		}
		sql = "update fipub_rulebind set pk_bindmeta = (select id from md_property where displayname = name and classid = ?)" 
			+ " where ruletype = 6 and pk_billtype = ? " 
			+ " and not exists (select 1 from fipub_rulebind a, md_property b"
			+ " where a.pk_bindmeta = b.id"
			+ " and a.name = b.displayname "
			+ " and b.classid = ? "
			+ " and a.pk_billtype = ? "
			+ " and a.ruletype = 6 )";
        List<SQLParameter> parameterList = new ArrayList<SQLParameter>();
		//ѭ������������
		for(Object value : list){
			final String djlxbm = value.toString();
			
			//����������ҵ���е�Ԫ����ID
			final String busitemMetaDataID = (djlxbm!=null && djlxbm.startsWith(BXConstans.JK_PREFIX)) ? getBodyBusitemMetaDataID(BXConstans.JK_DJDL) : getBodyBusitemMetaDataID(BXConstans.BX_DJDL);
			SQLParameter param = new SQLParameter();

			param.addParam(busitemMetaDataID);
			param.addParam(djlxbm);
			param.addParam(busitemMetaDataID);
			param.addParam(djlxbm);
			parameterList.add(param);
			Logger.debug(sql);
		}
		batchUpdate(sql, parameterList);
		
		Logger.debug("*************************************");
		Logger.debug("******** ��������ģ������6.0������6.1֮����н���У��������"+getClass().getName()+"**********");
		Logger.debug("*************************************");
	}
	
	private int batchUpdate(String sql, List<SQLParameter> parameterList) throws DAOException {
        PersistenceManager manager = null;
        int value;
        try {
            manager = PersistenceManager.getInstance();
            manager.setMaxRows(100000);
            manager.setAddTimeStamp(true);
            JdbcSession session = manager.getJdbcSession();

            session.addBatch(sql, parameterList.toArray(new SQLParameter[parameterList.size()]));
            value = session.executeBatch();
        } catch (DbException e) {
            Logger.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        } finally {
            if (manager != null)
                manager.release();
        }
        return value;
    }
	
	public void doBeforeUpdateData(String oldVersion, String newVersion) throws Exception {
		
		Logger.debug("*************************************");
		Logger.debug("******** ��������ģ������6.0������6.1��ʼ�����Զ���ģ����Ϣ��ʼ"+getClass().getName()+"**********");
		Logger.debug("*************************************");
		
		//�Ա������Զ���ģ��Ĵ���Ĭ��ģ����Զ���ģ��ҳǩ��������ͬ��ֻ��Ҫ�޸�Ԫ����
		String sql="update pub_billtemplet_b  set metadataproperty=replace(metadataproperty,'erm.busitem','erm.er_busitem') "+
		"where pk_billtemplet in (select pk_billtemplet from pub_billtemplet  where nodecode like '2011%' and "+
		"pk_corp <> '@@@@' and bill_templetname <>'SYSTEM' and metadataclass in ('erm.bxzb')) and  metadataproperty like 'erm.busitem%'";
		getBaseDAO().executeUpdate(sql);
		Logger.debug("sql="+sql);
		
		//ɾ���Զ���ģ��Ĳ���ҳǩ
		sql="delete  from  pub_billtemplet_b  where pk_billtemplet in (select pk_billtemplet from pub_billtemplet  where nodecode like '2011%' and "+
		"pk_corp <> '@@@@' and bill_templetname <>'SYSTEM' and metadataclass in ('erm.bxzb')) and table_code='er_finitem'";
		getBaseDAO().executeUpdate(sql);
		Logger.debug(sql);
		
		//���ڽ��Ĵ���,�ȴ���Ԫ����
		sql="update pub_billtemplet_b set metadataproperty=replace(metadataproperty,'erm.jkbusitem','erm.jk_busitem') "+
		"where pk_billtemplet in (select pk_billtemplet from pub_billtemplet  where nodecode like '2011%' and "+
		"pk_corp <> '@@@@' and bill_templetname <>'SYSTEM' and metadataclass in ('erm.jkzb')) and  metadataproperty like 'erm.jkbusitem%'";
		getBaseDAO().executeUpdate(sql);
		Logger.debug(sql);
		
		//�޸Ľ��ģ���ҳǩ
		sql="update pub_billtemplet_b set table_code='jk_busitem' where pk_billtemplet in (select pk_billtemplet from pub_billtemplet  where nodecode like '2011%' and "+
		"pk_corp <> '@@@@' and bill_templetname <>'SYSTEM' and metadataclass in ('erm.jkzb')) and  table_code='arap_bxbusitem'";
		getBaseDAO().executeUpdate(sql);
		Logger.debug(sql);
		
		//�޸Ľ��ģ���ҳǩ
		sql="update pub_billtemplet_t set tabcode='jk_busitem' where pk_billtemplet in (select pk_billtemplet from pub_billtemplet  where nodecode like '2011%' and "+
		"pk_corp <> '@@@@' and bill_templetname <>'SYSTEM' and metadataclass in ('erm.jkzb')) and  tabcode='arap_bxbusitem'";
		getBaseDAO().executeUpdate(sql);
		Logger.debug(sql);
		
		Logger.debug("*************************************");
		Logger.debug("******** ��������ģ������6.0������6.1��ʼ�����Զ���ģ����Ϣ����"+getClass().getName()+"**********");
		Logger.debug("*************************************");
		
	}
}
