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
	 * ArapInitDAta 构造子注解。
	 */
	public ErInitDAtaBO() {
		super();
	}

	public void initAccountData(String dataSourceName)
			throws nc.bs.pub.InitDataException {
	}

	/**
	 * 启用单位账时，预制数据
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
	 * 是否60升级到61版本
	 * @param oldVersion
	 * @param newVersion
	 */
	private static boolean is60Update261(String oldVersion, String newVersion){
		return oldVersion != null && oldVersion.startsWith("6.0")
				&& newVersion != null && newVersion.startsWith("6.1");
	}

	public void doAfterUpdateData(String oldVersion, String newVersion) throws Exception {
		
		//1.更新单据类型相关信息
		updateBillType(newVersion);
		
		//2.更新借款单业务页签编码
		updateJKBillTemplateTableCode(newVersion);
		
		//3.更新模板分配
		updateSysTemplet();
		
		//4.更新查询模板
		updateQueryTemplet();
		
		if (!is60Update261(oldVersion, newVersion)) {
			return;
		}
		
		//5.修改凭证模版
		updateTranstemplate(newVersion);
		
		//6.更新常用单据的多版本信息
		updateInitBillVersion(newVersion);

		//7.将借款报销单据的财务行金额(ybje,bbje,zfybje,zfbbje,hkybje,hkbbje,cjkybje,cjkbbje,group...bbje,global...bbje等)分摊到业务行上
		updateFin2BusiItem(newVersion);
		
		//8.更新自定义的单据模版的元数据属性
		updateBillTemplate(newVersion);
		
		//9.作废财务页签
		deleteFinBillTemplate(newVersion);
		
		//10.更新交叉校验规则
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
		Logger.debug("******** 报销管理模块升级6.0升级到6.1删除单据模版财务页签开始"+getClass().getName()+"**********");
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
		Logger.debug("******** 报销管理模块升级6.0升级到6.1删除单据模版财务页签结束"+getClass().getName()+"**********");
		Logger.debug("*************************************");
	}
	/**
	 * 更新自定义的单据模版的元数据属性
	 * @author chendya
	 * @param newVersion
	 * @throws DAOException
	 */
	private void updateBillTemplate(String newVersion) throws DAOException{
		Logger.debug("*************************************");
		Logger.debug("******** 报销管理模块升级6.0升级到6.1更新单据模版元数据属性开始"+getClass().getName()+"**********");
		Logger.debug("*************************************");
		
		//报销单
		String sql = "update pub_billtemplet_b  set metadataproperty=replace(metadataproperty,'erm.busitem','erm.er_busitem') " +
				" where pk_billtemplet in (select pk_billtemplet from pub_billtemplet  where nodecode like '2011%' and " +
				" pk_billtypecode like '264%')  and metadataproperty like 'erm.busitem%' ";
		getBaseDAO().executeUpdate(sql);
		Logger.debug(sql);
		
		//借款单
		sql = "update pub_billtemplet_b  set metadataproperty=replace(metadataproperty,'erm.jkbusitem','erm.jk_busitem') " +
				" where pk_billtemplet in (select pk_billtemplet from pub_billtemplet  " +
				" where nodecode like '2011%' and pk_billtypecode like '263%')  and metadataproperty like 'erm.jkbusitem%'";
		getBaseDAO().executeUpdate(sql);
		Logger.debug(sql);
		
		Logger.debug("*************************************");
		Logger.debug("******** 报销管理模块升级6.0升级到6.1更新单据模版元数据属性结束"+getClass().getName()+"**********");
		Logger.debug("*************************************");
	}
	
	/**
	 * 更新自定义节点中，查询模板的设置，报销单时默认为差旅报销单的查询模板，借款单时为差旅借款单查询模板
	 * @throws DAOException
	 */
	private void updateQueryTemplet() throws BusinessException {
		Logger.debug("*************************************");
		Logger.debug("******** 报销管理模块升级6.0升级到6.1更新查询模板开始"+getClass().getName()+"**********");
		Logger.debug("*************************************");
		
		String sql = "select p.templateid from pub_systemplate_base p where p.funnode in ('20110ETEA') and p.tempstyle = 1 ";
		//差旅报销单查询模板ID
		String bxQueryTempletId = (String)getBaseDAO().executeQuery(sql, new ColumnProcessor());
		//差旅借款单查询模板ID
		sql = "select p.templateid from pub_systemplate_base p where p.funnode in ('20110ETLB') and p.tempstyle = 1 ";
		String jkQueryTempletId = (String)getBaseDAO().executeQuery(sql, new ColumnProcessor());
		
		sql = " funnode like '2011%' and templateid = '1001Z31000000001CIXD' and tempstyle = 1 ";
		@SuppressWarnings("unchecked")
        Collection<SystemplateBaseVO> needReplaceVo = getBaseDAO().retrieveByClause(SystemplateBaseVO.class, sql);
		
		if(needReplaceVo != null && needReplaceVo.size() > 0){
		    //begin批量查询
		    List<String> codeList = new LinkedList<String>();
		    for (@SuppressWarnings("rawtypes")
            Iterator iterator = needReplaceVo.iterator(); iterator.hasNext();) {
		        SystemplateBaseVO systemplateBaseVO = (SystemplateBaseVO) iterator.next();
                String funcode = systemplateBaseVO.getFunnode();
                codeList.add(funcode);
		    }
            String sWhere = SqlUtils.getInStr("funcode", codeList, true);
            //批量信息记录变量
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
            //end批量查询
	        
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
				if( temp!= null && temp.contains("借款")){/*-=notranslate=-*/
					systemplateBaseVO.setTemplateid(jkQueryTempletId);
//				}else if(temp!= null && temp.contains("报销")){/*-=notranslate=-*/
//					systemplateBaseVO.setTemplateid(bxQueryTempletId);
				}else {
					systemplateBaseVO.setTemplateid(bxQueryTempletId);
				}
			}
			
			getBaseDAO().updateVOArray(needReplaceVo.toArray(new SystemplateBaseVO[0]));
		}
		
		Logger.debug("*************************************");
		Logger.debug("******** 报销管理模块升级6.0升级到6.1更新查询模板结束"+getClass().getName()+"**********");
		Logger.debug("*************************************");
	}
	
	/**
	 * 参数ERX保存的财务行对业务行的对照关系
	 */
	private Map<String,Map<String,String>> ermGroupBusi2FinParamMap = new HashMap<String, Map<String,String>>();
	
	/**
	 * 单据类型对应的集团Pks
	 */
	private Map<String,List<String>> ermDjlxGroupPKMap = new HashMap<String, List<String>>();
	
	/**
	 * @since v6.1
	 * @author chendya
	 * @throws Exception
	 */
	private void updateTranstemplate(String newVersion) throws Exception{
		Logger.debug("*************************************");
		Logger.debug("******** 报销管理模块升级6.0升级到6.1开始更新凭证模版开始"+getClass().getName()+"**********");
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
		Logger.debug("******** 报销管理模块升级6.0升级到6.1开始更新凭证模版结束"+getClass().getName()+"**********");
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
		//1.将财务行的TemplateCellVO的CELLVALUE中有业务对照的行更新
		{
			Map<String, String> map = getBusi2FinParamMap(pk_group);
			if(map.size()==0){
				return;
			}
			//查询配有自定义项的财务行
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
						//有业务对照
						String newCellValue = prefix + busiDefItem + suffix;
						vo.setCellvalue(newCellValue);
						updateVOList.add(vo);
					}
				}
				int count = getBaseDAO().updateVOArray((TemplateCellVO[])updateVOList.toArray(new TemplateCellVO[0]), new String[]{TemplateCellVO.CELLVALUE});
				Logger.debug("====================================更新业务对照财务记录行数count="+count+"==================================");
			}
		}
		
		//2.将所有包含财务信息的替换成业务信息
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
			//报销
			int count = getBaseDAO().executeUpdate(sql,param);
			sql = "update fip_templatecell " 
				+ " set cellvalue=replace(cellvalue,'jk_finitem','jk_busitem') " 
				+ " where exists (select 1 from fip_templaterow fr "
				+ " where fr.pk_templaterow = fip_templatecell.pk_templaterow and exists (select 1 from fip_transtemplate ft "
				+ " where ft.pk_transtemplate = fr.pk_transtemplate and ft.src_billtype in " 
				+ " (select distinct bt.pk_billtypecode from bd_billtype bt where bt.systemcode = 'erm' and bt.pk_group=?) and ft.dr = 0)) "
				+ " and cellvalue like '$jk_finitem.%' and dr = 0 ";
			//借款
			count = getBaseDAO().executeUpdate(sql,param);
			Logger.debug("sql="+sql);
			Logger.debug("====================================更新财务行记录行数count="+count+"==================================");
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
	 * 作废6.0的财务页签信息，将财务信息的金额分摊到业务信息上 
	 * @throws Exception
	 */
	private void updateFin2BusiItem(String newVersion) throws Exception{
		Logger.debug("*************************************");
		Logger.debug("******** 报销管理模块升级6.0升级到6.1更新财务业务页签到业务页签费用分摊开始"+getClass().getName()+"**********");
		Logger.debug("*************************************");
		if (newVersion != null && newVersion.startsWith("6.1")) {
			updateFin2BusiData(BXConstans.JK_DJDL);
			updateFin2BusiData(BXConstans.BX_DJDL);
		}
		Logger.debug("*************************************");
		Logger.debug("******** 报销管理模块升级6.0升级到6.1更新财务业务页签到业务页签费用分摊结束"+getClass().getName()+"**********");
		Logger.debug("*************************************");
	}
	
	private void updateFin2BusiData(String djdl) throws Exception{
		//有借款报销单据的集团才升级
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
					Logger.error("==================单据号为:"+vo.getDjbh()+"的单据没有财务信息但有业务信息,未不合法单据,此张单据升级失败============================");
					break;
				}
				//先记录，再更新的业务行的原币金额为amount金额VO
				for(BXBusItemVO busiItemVO : busiItemVOs){
					busiItemVO.setYbje(busiItemVO.getAmount());
					updateBusiItemVOList.add(busiItemVO);
				}
				//1.找出财务行也业务行的对照关系
				Map<BXFinItemVO, List<BXBusItemVO>> map = createFin2BusiMap(finItemVOs,busiItemVOs,getBusi2FinParamMap(pk_group));
				
				//2.分摊费用
				balanceFee(map);
			}
		}
		//批量更新业务行字段
		getBaseDAO().updateVOArray((BXBusItemVO[])updateBusiItemVOList.toArray(new BXBusItemVO[0]), 
				new String[]{				
				//原币字段
				BXBusItemVO.YBJE,BXBusItemVO.BBJE,
				BXBusItemVO.ZFYBJE,BXBusItemVO.ZFBBJE,
				BXBusItemVO.HKYBJE, BXBusItemVO.HKBBJE,
				BXBusItemVO.CJKYBJE, BXBusItemVO.CJKBBJE,
				
				//本币字段
				BXBusItemVO.GROUPBBJE,BXBusItemVO.GLOBALBBJE,
				BXBusItemVO.GROUPZFBBJE,BXBusItemVO.GLOBALZFBBJE,
				BXBusItemVO.GROUPHKBBJE,BXBusItemVO.GLOBALHKBBJE,
				BXBusItemVO.GROUPCJKBBJE,BXBusItemVO.GLOBALCJKBBJE
		});
	}
	/**
	 * 分摊费用
	 * @param map
	 */
	private static void balanceFee(Map<BXFinItemVO, List<BXBusItemVO>> map){
		Iterator<Entry<BXFinItemVO, List<BXBusItemVO>>> iter = map.entrySet().iterator();
		while(iter.hasNext()){
			Entry<BXFinItemVO, List<BXBusItemVO>> entry = iter.next();
			BXFinItemVO finItemVO = entry.getKey();
			List<BXBusItemVO> busiItemVOs = entry.getValue();
			
			//分摊冲借款原币金额(同时计算支付或还款原币金额)
			balanceCjkybje(finItemVO,busiItemVOs);
			
			//分摊原币对本币，集团本币，全局本币的金额
			Map<String,String> ybField2bbFieldMap = new HashMap<String, String>();
			
			//根据原币金额分摊本币金额
			ybField2bbFieldMap.put(BXBusItemVO.YBJE, BXBusItemVO.BBJE);
			ybField2bbFieldMap.put(BXBusItemVO.ZFYBJE, BXBusItemVO.ZFBBJE);
			ybField2bbFieldMap.put(BXBusItemVO.HKYBJE, BXBusItemVO.HKBBJE);
			ybField2bbFieldMap.put(BXBusItemVO.CJKYBJE, BXBusItemVO.CJKBBJE);
			balanceBbje(finItemVO,busiItemVOs,ybField2bbFieldMap);
			
			//根据原币金额分摊集团本币金额
			ybField2bbFieldMap.clear();
			ybField2bbFieldMap.put(BXBusItemVO.YBJE, BXBusItemVO.GROUPBBJE);
			ybField2bbFieldMap.put(BXBusItemVO.ZFYBJE, BXBusItemVO.GROUPZFBBJE);
			ybField2bbFieldMap.put(BXBusItemVO.HKYBJE, BXBusItemVO.GROUPHKBBJE);
			ybField2bbFieldMap.put(BXBusItemVO.CJKYBJE, BXBusItemVO.GROUPCJKBBJE);
			balanceBbje(finItemVO,busiItemVOs,ybField2bbFieldMap);
			
			//根据原币金额分摊全局本币金额
			ybField2bbFieldMap.clear();
			ybField2bbFieldMap.put(BXBusItemVO.YBJE, BXBusItemVO.GLOBALBBJE);
			ybField2bbFieldMap.put(BXBusItemVO.ZFYBJE, BXBusItemVO.GLOBALZFBBJE);
			ybField2bbFieldMap.put(BXBusItemVO.HKYBJE, BXBusItemVO.GLOBALHKBBJE);
			ybField2bbFieldMap.put(BXBusItemVO.CJKYBJE, BXBusItemVO.GLOBALCJKBBJE);
			balanceBbje(finItemVO,busiItemVOs,ybField2bbFieldMap);
			
		}
	}
	
	private static void balanceCjkybje(BXFinItemVO finItemVO , List<BXBusItemVO> busiItemVOs){
		//财务行冲借款金额
		UFDouble fin_cjkybje_total = finItemVO.getCjkybje();
		if(fin_cjkybje_total==null){
			fin_cjkybje_total = ZERO;
		}
		//是否有过冲借款
		boolean hasCjk = true;
		if(fin_cjkybje_total==null||fin_cjkybje_total.equals(ZERO)){
			//没有冲借款，不分摊
			hasCjk = false;
		}
		//每次分摊后剩余的冲借款金额
		UFDouble left_fin_cjkybje = fin_cjkybje_total;
		
		//有冲借款的情况，记录是否继续分摊冲借款金额
		//此标记作用： (比如:财务行冲借款金额80,业务行有3行, 第一行原币金额100,第二行原币金额20,冲借款时第一行冲了80后，第二行就不继续冲了)
		boolean isContinueCjk = true;
		int index = 0;
		for(BXBusItemVO busItemVO : busiItemVOs){
			if(!hasCjk){
			//1.没有冲借款的情况
				UFDouble busi_ybje = busItemVO.getYbje();
				busItemVO.setCjkybje(ZERO);
				busItemVO.setZfybje(busi_ybje);
				busItemVO.setHkybje(ZERO);
				continue;
			}
			//2.有冲借款的情况
			UFDouble busi_ybje = busItemVO.getYbje();
			if(isContinueCjk){
				//原币金额
				busItemVO.setYbje(busItemVO.getAmount());
				if(left_fin_cjkybje.compareTo(busi_ybje)>=0){
					//财务行剩余的冲借款金额大于或等于当前行的原币金额,则把这一行全部冲掉，即此行的冲借款金额取此行的原币金额，此行没有支付或还款
					busItemVO.setCjkbbje(busi_ybje);
					busItemVO.setZfybje(ZERO);
					//剩余冲借款金额
					left_fin_cjkybje = left_fin_cjkybje.sub(busi_ybje);
					if(index == busiItemVOs.size()-1){
						//没有行再可冲借款了
						busItemVO.setHkybje(left_fin_cjkybje);
						break;
					}
				}else{
					//财务行剩余的冲借款金额小于当前行的原币金额，则此行的冲借款金额直接取余下的冲借款金额，并且不在继续往下一行冲借款
					busItemVO.setCjkybje(left_fin_cjkybje);
					busItemVO.setZfybje(busi_ybje.sub(left_fin_cjkybje));
					busItemVO.setHkybje(ZERO);
					//下一行不再冲借款了
					isContinueCjk = false;
				}
			}else{
				//不冲借款的行
				busItemVO.setCjkybje(ZERO);
				busItemVO.setZfybje(busi_ybje);
				busItemVO.setHkybje(ZERO);
			}
			index++;
		}
	}
	/**
	 * 根据原币金额分摊本币金额
	 * @param finItemVO
	 * @param busiItemVOs
	 * @param yb_bbFieldMap
	 */
	private static void balanceBbje(BXFinItemVO finItemVO,List<BXBusItemVO> busiItemVOs,Map<String,String> yb_bbFieldMap){
		int index = 0;
		//记录yb_bbFieldMap中每一个entry的bbje尾差
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
					//原币金额为0，则本币金额也为0
					busiItemVO.setAttributeValue(bbjeField, ZERO);
					continue;
				}
				//财务行本币金额
				if (busiItemVOs.size() == 1) {
					//业务行就一行的情况
					busiItemVO.setAttributeValue(bbjeField, fin_bbje_total);
				} else if (index == busiItemVOs.size() - 1) {
					// 将尾差补到最后一行
					busiItemVO.setAttributeValue(bbjeField, map.get(bbjeField));
				} else {
					UFDouble busi_ybje = (UFDouble) busiItemVO.getAttributeValue(ybjeField);
					if(busi_ybje==null){
						busi_ybje = ZERO;
					}
					// 本币金额按原币金额比例折算
					UFDouble busi_bbje = busi_ybje.div(fin_ybje_total).multiply(fin_bbje_total);
					// 剩余金额
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
	 * 财务行对照业务行
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
				//当前业务行是否是财务行要匹配的行
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
						//当前属性对应值不相等,不继续往下匹配其它属性,当前业务行不是财务行要匹配的行
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
	 * 返回财务字段和业务字段对照关系
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
			//内联财务行(没有财务行的单据直接过滤)
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
		Logger.debug("******** 报销管理模块升级6.0升级到6.1开始更新借款单和常用单据的业务行数据的页签编码开始"+getClass().getName()+"**********");
		Logger.debug("*************************************");
		
		if (newVersion != null && newVersion.startsWith("6.1")) {
			//6.1专用
			String sql = "update er_busitem set tablecode='jk_busitem' where tablecode='arap_bxbusitem' and pk_jkbx " 
				+ " in (select a.pk_jkbx from (select pk_jkbx from er_jkzb where dr=0 " 
				+ " union select pk_jkbx from er_jkbx_init where djdl='jk' and dr=0 ) a )";
			getBaseDAO().executeUpdate(sql);
			Logger.debug("sql="+sql);
		}
		
		Logger.debug("*************************************");
		Logger.debug("******** 报销管理模块升级6.0升级到6.1开始更新借款单和常用单据业务行数据的页签编码结束"+getClass().getName()+"**********");
		Logger.debug("*************************************");
	}
	
	private void updateBillType(String newVersion) throws Exception {
		Logger.debug("*************************************");
		Logger.debug("******** 报销管理模块升级6.0升级到6.1开始更新单据类型的DAO和CheckClass开始"+getClass().getName()+"**********");
		Logger.debug("*************************************");
		
		if (newVersion != null && newVersion.startsWith("6.1")) {
			//6.1专用
			String sql = "update bd_billtype set accountclass='nc.bs.arap.bx.JKBXDAO',checkclassname='nc.bs.arap.bx.JKBXDAO' where systemcode='erm'";
			getBaseDAO().executeUpdate(sql);
			Logger.debug("sql="+sql);
		}

		Logger.debug("*************************************");
		Logger.debug("******** 报销管理模块升级6.0升级到6.1开始更新单据类型的DAO和CheckClass结束"+getClass().getName()+"**********");
		Logger.debug("*************************************");
	}
	
	private void updateInitBillVersion(String newVersion) throws Exception {
		Logger.debug("*************************************");
		Logger.debug("******** 报销管理模块升级6.0升级到6.1开始更新常用单据多版本信息开始"+getClass().getName()+"**********");
		Logger.debug("*************************************");
		
		if (newVersion != null && newVersion.startsWith("6.1")) {
			//6.1专用
			
			//借款报销单位多版本
			String sql = "update er_jkbx_init set pk_org_v=(select org_orgs.pk_vid from org_orgs where org_orgs.pk_org=er_jkbx_init.pk_org) where isnull(pk_org_v,'~')='~'";
			getBaseDAO().executeUpdate(sql);
			Logger.debug("sql="+sql);
			
			//费用承担单位多版本
			sql = "update er_jkbx_init set fydwbm_v=(select org_orgs.pk_vid from org_orgs where org_orgs.pk_org=er_jkbx_init.fydwbm) where isnull(fydwbm_v,'~')='~'";
			getBaseDAO().executeUpdate(sql);
			Logger.debug("sql="+sql);
			
			//借款报销人单位多版本
			sql = "update er_jkbx_init set dwbm_v=(select org_orgs.pk_vid from org_orgs where org_orgs.pk_org=er_jkbx_init.dwbm) where isnull(dwbm_v,'~')='~'";
			getBaseDAO().executeUpdate(sql);
			Logger.debug("sql="+sql);
			
			//费用承担部门多版本
			sql = "update er_jkbx_init set fydeptid_v=(select pk_vid from org_dept where pk_dept=er_jkbx_init.fydeptid) where isnull(fydeptid_v,'~')='~'";
			getBaseDAO().executeUpdate(sql);
			Logger.debug("sql="+sql);
			
			//借款报销人部门多版本
			sql = "update er_jkbx_init set deptid_v=(select pk_vid from org_dept where pk_dept=er_jkbx_init.deptid) where deptid_v='~'";
			getBaseDAO().executeUpdate(sql);
			Logger.debug("sql="+sql);
			
			//自定义模板中的单位、部门转换为多版本显示，原单位、部门设置为不可见
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
		Logger.debug("******** 报销管理模块升级6.0升级到6.1开始更新常用单据多版本信息结束"+getClass().getName()+"**********");
		Logger.debug("*************************************");
	}

	private void updateFipItem(String newVersion) throws DAOException {
		Logger.debug("*************************************");
		Logger.debug("******** 报销管理模块升级6.0升级到6.1之前作废报销管理原有凭证模版开始"+getClass().getName()+"**********");
		Logger.debug("*************************************");
		
		if (newVersion != null && newVersion.startsWith("6.1")) {
			// 6.1专用
			String sql = "delete from fip_billitem where pk_billtype in ('263X','264X')  and  (attrcode not like 'zb.%' and  attrcode not like 'fb.%') ";
			getBaseDAO().executeUpdate(sql);
			Logger.debug("sql="+sql);
		}
		Logger.debug("*************************************");
		Logger.debug("******** 报销管理模块升级6.0升级到6.1之前作废报销管理原有凭证模版结束"+getClass().getName()+"**********");
		Logger.debug("*************************************");
	}
	
	/**
	 * 缓存借款报销单据业务行的元数据ID
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
		Logger.debug("******** 报销管理模块升级6.0升级到6.1之后更行交叉校验规则开始"+getClass().getName()+"**********");
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
		//循环各交易类型
		for(Object value : list){
			final String djlxbm = value.toString();
			
			//借款，报销表体业务行的元数据ID
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
		Logger.debug("******** 报销管理模块升级6.0升级到6.1之后更行交叉校验规则结束"+getClass().getName()+"**********");
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
		Logger.debug("******** 报销管理模块升级6.0升级到6.1开始更新自定义模板信息开始"+getClass().getName()+"**********");
		Logger.debug("*************************************");
		
		//对报销单自定义模板的处理：默认模板和自定的模板页签的名称相同，只需要修改元数据
		String sql="update pub_billtemplet_b  set metadataproperty=replace(metadataproperty,'erm.busitem','erm.er_busitem') "+
		"where pk_billtemplet in (select pk_billtemplet from pub_billtemplet  where nodecode like '2011%' and "+
		"pk_corp <> '@@@@' and bill_templetname <>'SYSTEM' and metadataclass in ('erm.bxzb')) and  metadataproperty like 'erm.busitem%'";
		getBaseDAO().executeUpdate(sql);
		Logger.debug("sql="+sql);
		
		//删除自定义模板的财务页签
		sql="delete  from  pub_billtemplet_b  where pk_billtemplet in (select pk_billtemplet from pub_billtemplet  where nodecode like '2011%' and "+
		"pk_corp <> '@@@@' and bill_templetname <>'SYSTEM' and metadataclass in ('erm.bxzb')) and table_code='er_finitem'";
		getBaseDAO().executeUpdate(sql);
		Logger.debug(sql);
		
		//对于借款单的处理,先处理元数据
		sql="update pub_billtemplet_b set metadataproperty=replace(metadataproperty,'erm.jkbusitem','erm.jk_busitem') "+
		"where pk_billtemplet in (select pk_billtemplet from pub_billtemplet  where nodecode like '2011%' and "+
		"pk_corp <> '@@@@' and bill_templetname <>'SYSTEM' and metadataclass in ('erm.jkzb')) and  metadataproperty like 'erm.jkbusitem%'";
		getBaseDAO().executeUpdate(sql);
		Logger.debug(sql);
		
		//修改借款模板的页签
		sql="update pub_billtemplet_b set table_code='jk_busitem' where pk_billtemplet in (select pk_billtemplet from pub_billtemplet  where nodecode like '2011%' and "+
		"pk_corp <> '@@@@' and bill_templetname <>'SYSTEM' and metadataclass in ('erm.jkzb')) and  table_code='arap_bxbusitem'";
		getBaseDAO().executeUpdate(sql);
		Logger.debug(sql);
		
		//修改借款模板的页签
		sql="update pub_billtemplet_t set tabcode='jk_busitem' where pk_billtemplet in (select pk_billtemplet from pub_billtemplet  where nodecode like '2011%' and "+
		"pk_corp <> '@@@@' and bill_templetname <>'SYSTEM' and metadataclass in ('erm.jkzb')) and  tabcode='arap_bxbusitem'";
		getBaseDAO().executeUpdate(sql);
		Logger.debug(sql);
		
		Logger.debug("*************************************");
		Logger.debug("******** 报销管理模块升级6.0升级到6.1开始更新自定义模板信息结束"+getClass().getName()+"**********");
		Logger.debug("*************************************");
		
	}
}
