package nc.impl.erm.report;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.erm.pub.ErmReportUtil;
import nc.bs.erm.util.ErAccperiodUtil;
import nc.bs.erm.util.ErmReportSqlUtils;
import nc.bs.erm.util.TmpTableCreator;
import nc.bs.framework.common.NCLocator;
import nc.itf.erm.pub.IExpamortizeBO;
import nc.itf.erm.report.IErmReportConstants;
import nc.itf.fipub.report.IPubReportConstants;
import nc.itf.fipub.report.IReportQueryCond;
import nc.jdbc.framework.util.DBConsts;
import nc.md.persist.framework.IMDPersistenceQueryService;
import nc.md.persist.framework.MDPersistenceService;
import nc.pub.smart.context.SmartContext;
import nc.pub.smart.data.DataSet;
import nc.pub.smart.exception.SmartException;
import nc.pub.smart.script.statement.select.PlainSelect;
import nc.pub.smart.smartprovider.ExpamortizeDataProvider;
import nc.pubitf.erm.expamortize.IExpAmortizeprocQuery;
import nc.utils.fipub.FipubSqlExecuter;
import nc.utils.fipub.SmartProcessor;
import nc.vo.bd.period2.AccperiodmonthVO;
import nc.vo.erm.expamortize.AggExpamtinfoVO;
import nc.vo.erm.expamortize.ExpamtDetailVO;
import nc.vo.erm.expamortize.ExpamtinfoVO;
import nc.vo.erm.expamortize.ExpamtprocVO;
import nc.vo.erm.util.VOUtils;
import nc.vo.fipub.report.PubCommonReportMethod;
import nc.vo.fipub.utils.SqlBuilder;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.rs.MemoryResultSet;

import org.apache.commons.lang.StringUtils;

/**
 * 报销管理帐表查询
 *
 * @author chenshuaia
 *
 */
public class ExpamortizeBOImpl extends FipubSqlExecuter implements IExpamortizeBO {

	private StringBuffer fixedFields = null;

	@Override
	public DataSet queryExpamortize(IReportQueryCond queryVO, SmartContext context) throws SmartException {
		setContext(context);
		DataSet resultDataSet = new DataSet();
		/***************************************************************/
		/********************** 屏蔽语义模型定义时的校验 ********************/
		if (queryVO == null) {
			// 定义语义模型时，会执行到这里进行校验，且queryVO为空
			try {
				resultDataSet.setMetaData(new ExpamortizeDataProvider().provideMetaData(null));
			} catch (SmartException e) {
				// 这里永远不会抛出异常，故吃掉异常也不会造成影响
			}
			return resultDataSet;
		}
		/****************************************************************/

		try {
            PlainSelect select = (PlainSelect)context.getAttribute("key_current_plain_select");
            select.setWhere(null);
		    tmpTblName = null;
			String whereSql = queryVO.getWhereSql();//where条件

			Map<String, Object> userMap = queryVO.getUserObject();// 用户自定义
			String currAccMonthPk = (String) userMap.get(IErmReportConstants.ACC_PERIOD);
			String currAccMonth = null;
			if (StringUtils.isNotEmpty(currAccMonthPk)) {
	            currAccMonth = ErAccperiodUtil.getAccperiodmonthByPk(currAccMonthPk).getYearmth();//会计期间
			}

            String startAccMonthPk = (String) userMap.get(IErmReportConstants.PERIOD_START);
            String startAccMonth = null;//开始会计期间
            if (StringUtils.isNotEmpty(startAccMonthPk)) {
                startAccMonth = ErAccperiodUtil.getAccperiodmonthByPk(startAccMonthPk).getYearmth();//开始会计期间
            }

            String endAccMonthPk = (String) userMap.get(IErmReportConstants.PERIOD_END);
            String endAccMonth = null;//结束会计期间
            if (StringUtils.isNotEmpty(endAccMonthPk)) {
                endAccMonth = ErAccperiodUtil.getAccperiodmonthByPk(endAccMonthPk).getYearmth();//结束会计期间
            }
            
			String bulidSql = bulidSql(whereSql, currAccMonth, startAccMonth, endAccMonth).toString();
			
			//查询条件插入临时表
			String[] arrangeSqls = getArrangeSqls(queryVO,bulidSql);

			// 构建临时表数据
			for (String sql : arrangeSqls) {
				executeUpdate(sql);
			}
//			ErmReportSqlUtils.caseWhenSql
			StringBuilder sbSql = new StringBuilder();
			sbSql.append("select xx.*, 0 vseq from (select * from ").append(getTmpTblName())
			.append(") xx order by ")
            .append(ErmReportSqlUtils.caseWhenSql("xx.pk_group")).append(", ")
            .append(ErmReportSqlUtils.caseWhenSql("xx.pk_org")).append(", ")
			.append(ErmReportSqlUtils.caseWhenSql("xx.bx_pk_billtype")).append(", ")
			.append(ErmReportSqlUtils.caseWhenSql("xx.rn")).append(", ")
			.append(ErmReportSqlUtils.caseWhenSql("xx.bx_djrq")).append(", ")
			.append(ErmReportSqlUtils.caseWhenSql("xx.bx_billno"));
//			String resultSql  = "select * from " + getTmpTblName()+ "  order by pk_group,pk_org,bx_pk_billtype,rn";
			
			MemoryResultSet result = (MemoryResultSet) executeQuery(sbSql.toString(), getResultProcessor());

            // 插入【币种】名称
            PubCommonReportMethod.insertNameColumn(result, IPubReportConstants.CURRTYPE, "pk_currtype", "currtype");
			// 插入【交易类型】名称
            PubCommonReportMethod.insertNameColumn(result,
                    IPubReportConstants.BILLTYPE, ExpamtinfoVO.BX_PK_BILLTYPE,
                    "billtype", 200);
			// 插入【业务单元】名称
			PubCommonReportMethod.insertNameColumn(result, IPubReportConstants.BUSINESS_UNIT, ExpamtinfoVO.PK_ORG, "org");

			resultDataSet.setMetaData(SmartProcessor.getMetaData(result));

			setComputePropertys(result, currAccMonth);
			Object[][] datas = getDatas(result, queryVO);

			// 处理报销日期
			PubCommonReportMethod.convert2ClientTime(datas, context, resultDataSet.getMetaData().getIndex("BX_DJRQ"));
            PubCommonReportMethod.setVSeq(datas, resultDataSet.getMetaData().getIndex(IPubReportConstants.ORDER_MANAGE_VSEQ));

			// 设置返回结果数据集
			resultDataSet.setDatas(datas);
            ErmReportUtil.processDataSet(context, resultDataSet);
		} catch (Exception e) {
			String errMsg = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0026")/*@res "待摊帐表查询报错!"*/;
			throw new SmartException(errMsg, e);
		}

		return resultDataSet;
	}


	private String[] getArrangeSqls(IReportQueryCond queryVO,String bulidSql) throws SQLException {
		String[] sqlList = new String[2];;
		sqlList[0] = new StringBuffer(" insert into ").append(getTmpTblName()).append(" ").append(bulidSql).toString(); // 计算明细
		sqlList[1] = getSubTotalSql(queryVO); // 计算小计合计
		return sqlList;                      
	}

	private String getSubTotalSql(IReportQueryCond queryVO) throws SQLException {
		StringBuffer sqlBuffer = new StringBuffer(" insert into ").append(getTmpTblName());
		sqlBuffer.append(" select ");
		sqlBuffer.append("null pk_expamtinfo ,");
		sqlBuffer.append("pk_group,");
		sqlBuffer.append("pk_org,");
		sqlBuffer.append("bx_pk_billtype ,");
		sqlBuffer.append("null bx_billno,");
		sqlBuffer.append("null bx_djrq,");
		sqlBuffer.append("grouping(pk_group) + grouping(pk_org) + grouping(bx_pk_billtype) + grouping(pk_currtype) + 1000000 + 1 rn,");
		sqlBuffer.append("sum(total_amount_ori),");
		sqlBuffer.append("null total_period,");
		sqlBuffer.append("null start_period,");
		sqlBuffer.append("null end_period,");
		sqlBuffer.append(" sum(res_amount_ori),");
		sqlBuffer.append("null res_period,");
		sqlBuffer.append("pk_currtype,");
		sqlBuffer.append("sum(res_orgamount_loc),");
		sqlBuffer.append("sum(gr_res_groupamount_loc),");
		sqlBuffer.append("sum(gl_res_globalamount_loc),");
		sqlBuffer.append("null accu_period,");
		sqlBuffer.append("sum(accu_amount_ori),");
		sqlBuffer.append("sum(curr_amount_ori),");
		sqlBuffer.append("null amt_status");
		sqlBuffer.append(" from ");
		sqlBuffer.append(getTmpTblName());
		sqlBuffer.append(" group by ");
		List<String> computed = new ArrayList<String>();
		computed.add("pk_group");
		computed.add("pk_org");
		computed.add("bx_pk_billtype");
        computed.add("pk_currtype");
		switch (SqlBuilder.getDatabaseType()) {
		case DBConsts.SQLSERVER:
			sqlBuffer.append(computed.get(0));
			int i = 1;
			for (; i < computed.size(); i++) {
				sqlBuffer.append(", ").append(computed.get(i));
			}
			sqlBuffer.append(" with cube ");
			break;
		case DBConsts.DB2:
		case DBConsts.ORACLE:
			sqlBuffer.append("cube(");
			sqlBuffer.append(computed.get(0));
			i = 1;
			for (; i < computed.size(); i++) {
				sqlBuffer.append(", ").append(computed.get(i));
			}
			sqlBuffer.append(")");
			break;
		default:
			break;
		}
		sqlBuffer.append(" having grouping(pk_group) <= grouping(pk_org) and grouping(pk_org) <= grouping(bx_pk_billtype) and grouping(bx_pk_billtype) <= grouping(pk_currtype) ");
		String[] orgs= (String[]) queryVO.getUserObject().get(ExpamtinfoVO.PK_ORG);
		sqlBuffer.append(" and grouping(pk_group) = 0  ");
		if(orgs.length==2){
			if(orgs[1].indexOf(",")==-1){
				sqlBuffer.append(" and grouping(pk_org) = 0  ");
			}
		}
		return sqlBuffer.toString();
	}

	/**
	 * 设置计算属性
	 * @param result
	 * @param currAccMonth
	 * @throws SQLException
	 * @throws BusinessException
	 */
	private void setComputePropertys(MemoryResultSet result, String currAccMonth) throws SQLException, BusinessException {
		if(result != null && currAccMonth != null){
            int rn = -1;
			while (result.next()) {
			    
			    rn = result.getInt("rn");
	            if (rn >= SmartProcessor.MAX_ROW) {
	                continue;
	            }
				String pk_expamtinfo = result.getString(ExpamtinfoVO.PK_EXPAMTINFO);
				String pk_org = result.getString(ExpamtinfoVO.PK_ORG);
				String startPeriod = result.getString(ExpamtinfoVO.START_PERIOD);
				String endPeriod = result.getString(ExpamtinfoVO.END_PERIOD);
				double resAmount = result.getDouble(ExpamtinfoVO.RES_AMOUNT + "_ori");
				int resPeriod = result.getInt(ExpamtinfoVO.RES_PERIOD);
				int totalPeriod = result.getInt(ExpamtinfoVO.TOTAL_PERIOD);

				UFBoolean amtStatus = getAmtStatus(currAccMonth, totalPeriod, resPeriod, startPeriod, pk_org);
				// 摊销状态
				if(amtStatus.equals(UFBoolean.TRUE)){
					updateMemoryResultSet(result,ExpamtinfoVO.AMT_STATUS, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0027")/*@res "是"*/);
				}
				else{
					updateMemoryResultSet(result,ExpamtinfoVO.AMT_STATUS, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0028")/*@res "否"*/);
				}
				UFDouble currentAmount = UFDouble.ZERO_DBL;// 当前摊销金额
				if (null != endPeriod&&endPeriod.compareTo(currAccMonth) >= 0) {
					currentAmount = getCurrAmount(amtStatus, pk_expamtinfo, currAccMonth, resPeriod, new UFDouble(resAmount));
				}
				updateMemoryResultSet(result,ExpamtinfoVO.CURR_AMOUNT + "_ori", currentAmount.toBigDecimal());
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void updateMemoryResultSet(MemoryResultSet result, String columnName, Object obj) throws SQLException {
		// 得到列ID
		int columnIndex = result.getColumnIndex(columnName) - 1;

		// 更新当前行数据
		List<Object> v = result.getRowArrayList();
		v.set(columnIndex, obj);
	}

	private static UFBoolean getAmtStatus(String currAccMonth, int totalPeriod, int resPeriod, String startPeriod, String pk_org)
			throws BusinessException {
		UFBoolean status = UFBoolean.FALSE;

		if (resPeriod > 0) {
			int accPeriod = totalPeriod - resPeriod;
			if ((accPeriod) == 0) {
				return UFBoolean.FALSE;
			}

			AccperiodmonthVO lastExpamPeriod = ErAccperiodUtil.getAddAccperiodmonth(pk_org, startPeriod, accPeriod);
			if (currAccMonth.compareTo(lastExpamPeriod.getYearmth()) <= 0) {
				status = UFBoolean.TRUE;
			}
		} else {// 摊销完以后，则记录该
			status = UFBoolean.TRUE;
		}
		return status;
	}

	/**
	 * 根据会计期间获取当期摊销金额
	 *
	 * @param amtStatus
	 *            摊销状态
	 * @param pk_expamtinfo
	 *            摊销pk
	 * @param currentAccMonth
	 *            会计期间
	 * @param resPeriod
	 *            剩余摊销期
	 * @param resAmount
	 *            剩余金额
	 * @return
	 * @throws BusinessException
	 */
	public static UFDouble getCurrAmount(UFBoolean amtStatus, String pk_expamtinfo, String currentAccMonth, Integer resPeriod, UFDouble resAmount)
			throws BusinessException {

		UFDouble currAmount = UFDouble.ZERO_DBL;
		if (amtStatus.equals(UFBoolean.TRUE)) {// 已摊销的情况下 ，构造记录中查询
			IMDPersistenceQueryService service = MDPersistenceService.lookupPersistenceQueryService();
			AggExpamtinfoVO vo = (AggExpamtinfoVO) service.queryBillOfVOByPK(AggExpamtinfoVO.class, pk_expamtinfo,
					false);

			IExpAmortizeprocQuery procService = NCLocator.getInstance().lookup(IExpAmortizeprocQuery.class);
			ExpamtprocVO[] procVos = procService.queryByInfoPksAndAccperiod(
					VOUtils.getAttributeValues(vo.getChildrenVO(), ExpamtDetailVO.PK_EXPAMTDETAIL), currentAccMonth);

			if (procVos != null && procVos.length > 0) {
				UFDouble curr_amount = UFDouble.ZERO_DBL;
				for (ExpamtprocVO proc : procVos) {
				    curr_amount = curr_amount.add(proc.getCurr_amount());
				}
				return curr_amount;
			}
		} else {
			if (resPeriod.intValue() == 1) {
				currAmount = resAmount;
			} else {
				currAmount = resAmount.div(resPeriod.intValue());
			}
		}

		return currAmount;
	}

	/**
	 * 构建二维数组
	 *
	 * @param result
	 * @param queryVO
	 * @return
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	private Object[][] getDatas(MemoryResultSet mrs, IReportQueryCond queryVO) throws SQLException {
		ArrayList<List<Object>> dataRowList = mrs.getResultArrayList();

		if (dataRowList == null || dataRowList.size() == 0) {
			return new Object[0][0];
		}

        // 处理摊销状态，本期摊销金额
        boolean isMultiOrg = true;
        String[] orgs = (String[]) queryVO.getUserObject().get(
                ExpamtinfoVO.PK_ORG);
        if (orgs.length == 2) {
            if (orgs[1].indexOf(",") == -1) {
                isMultiOrg = false;
            }
        }
        
        Object datas[][] = new Object[dataRowList.size()][mrs.getMetaData()
                                                          .getColumnCount()];

        Map<String, BigDecimal> currAmountMap = new HashMap<String, BigDecimal>();
        
        int pkOrgIndex = mrs.getColumnIndex("pk_org");
        int pkBilltype = mrs.getColumnIndex(ExpamtinfoVO.BX_PK_BILLTYPE);
        int pkCurrtype = mrs.getColumnIndex("pk_currtype");
        int currAmountOriIndex = mrs.getColumnIndex("curr_amount_ori");
        
        int rnIndex = mrs.getColumnIndex("rn");
        int orgIndex = mrs.getColumnIndex("org");
        int currtypeIndex = mrs.getColumnIndex("currtype");
        List<Integer> qryObjIndex = new ArrayList<Integer>();
        List<Integer> qryObjNameIndex = new ArrayList<Integer>();
        List<Integer> totalCol = new ArrayList<Integer>(); // 小计列
        List<Integer> totalCol2 = new ArrayList<Integer>(); // 小计列

        int nIndex = mrs.getColumnIndex("billtype");
        qryObjIndex.add(nIndex);
        qryObjNameIndex.add(nIndex);
        totalCol2.add(nIndex);
        totalCol.add(nIndex);

        totalCol.add(currtypeIndex);
        totalCol2.add(orgIndex);

        Map<String, Map<String, String>> countMap = new HashMap<String, Map<String, String>>();

        Object[] dataRow = null;
        int rn = -1;
        boolean isObj = false;

        boolean isCurrtype = false;
        String[] amount_ori = new String[] { "total_amount_ori",
                "accu_amount_ori", "res_amount_ori", "curr_amount_ori" };
        int[] currtypeCount = new int[amount_ori.length];
        int[] orgCurrtypeCount = new int[amount_ori.length];
        int[] amount_ori_pos = new int[amount_ori.length];
        for (int nPos = 0; nPos < amount_ori.length; nPos++) {
            amount_ori_pos[nPos] = mrs.getColumnIndex(amount_ori[nPos]);
        }
			
        for (int i = 0; i < dataRowList.size(); i++) {
            dataRow = dataRowList.get(i).toArray();
            rn = Integer.parseInt(dataRow[rnIndex - 1].toString());
            isObj = false;
            String pk_org = (String)dataRow[pkOrgIndex - 1];
            String pk_billtype = (String)dataRow[pkBilltype - 1];
            String pk_currtype = (String)dataRow[pkCurrtype - 1];
            BigDecimal currAmountOri = (BigDecimal)dataRow[currAmountOriIndex - 1];

            String keyToCurrType = pk_org + pk_billtype + pk_currtype;
            String keyToBillType = pk_org + pk_billtype;
            
            for (int nPos = 0; nPos < totalCol2.size(); nPos++) {
                String key = (String)dataRow[totalCol2.get(nPos) - 1];
                String currtype = (String)dataRow[currtypeIndex - 1];
                if (StringUtils.isEmpty(key)) {
                    continue;
                }
                Map<String, String> currtypeMap = countMap.get(key); 
                if (currtypeMap == null) {
                    currtypeMap = new HashMap<String, String>();
                    countMap.put(key, currtypeMap);
                }
                if (StringUtils.isNotEmpty(currtype)) {
                    currtypeMap.put(currtype, null);
                }
            }
            
            if (rn >= SmartProcessor.MAX_ROW) {
                // 处理合计行
                if (rn == SmartProcessor.MAX_ROW) {
                } else {
                    int k = totalCol.size() - 1;
                    int curRowQryObjIndex = 0;
                    for (; k >= 0; k--) {
                        if (dataRow[totalCol.get(k) - 1] != null && !"".equals(dataRow[totalCol.get(k) - 1])) {
                            if (k == totalCol.size() - 1) {
                                // 币种小计
                                dataRow[currtypeIndex - 1] = dataRow[currtypeIndex - 1]
                                        + IErmReportConstants.getConst_Sub_Total(); // 小计
                                dataRow[currAmountOriIndex - 1] = currAmountMap.get(keyToCurrType);
                                if (!isCurrtype) {
                                    for (int nPos = 0; nPos < orgCurrtypeCount.length; nPos++) {
                                        currtypeCount[nPos] = 0;
                                    }
                                }
                                for (int nPos = 0; nPos < orgCurrtypeCount.length; nPos++) {
                                    if (dataRow[amount_ori_pos[nPos] - 1] != null && 
                                            BigDecimal.ZERO.compareTo((BigDecimal)dataRow[amount_ori_pos[nPos] - 1]) != 0) {
                                        currtypeCount[nPos]++;
                                        orgCurrtypeCount[nPos]++;
                                    }
                                }
                                isCurrtype = true;
                            } else {
                                // 查询对象小计
                                dataRow[qryObjIndex.get(k) - 1] = dataRow[qryObjIndex.get(k) - 1]
                                        + IErmReportConstants.getConst_Sub_Total(); // 小计
                                dataRow[currAmountOriIndex - 1] = currAmountMap.get(keyToBillType);
//                            dataRow[qryObjNameIndex.get(k) - 1] = dataRow[qryObjNameIndex.get(k) - 1]
//                                    + IErmReportConstants.getConst_Sub_Total(); // 小计
                                isCurrtype = false;
                                curRowQryObjIndex = qryObjNameIndex.get(k);
                            }
                            isObj = true;
                            break;
                        }
                    }
                    
                    for (k--; k >= 0; k--) {
                        dataRow[qryObjIndex.get(k) - 1] = "";
                        dataRow[qryObjNameIndex.get(k) - 1] = "";
                    }

                    if (!isObj && isMultiOrg && (dataRow[orgIndex - 1] == null
                            || dataRow[orgIndex - 1].toString().length() == 0)) {
//                      dataRow[briefIndex - 1] = IErmReportConstants.CONST_ALL_TOTAL; // 总计
                        dataRow[orgIndex - 1] = IErmReportConstants.getCONST_ALL_TOTAL(); // 总计
                        // 多组织、多币种清空金额字段信息
                        if (!isCurrtype) {
                            // 多币种清空金额字段信息
                            for (int nPos = 0; nPos < amount_ori.length; nPos++) {
                                if (currtypeCount[nPos] > 1) {
                                    dataRow[amount_ori_pos[nPos] - 1] = null;
                                }
                            }
                        }
                    } else if (!isObj) {
                        String org = (String)dataRow[orgIndex - 1];
                        dataRow[orgIndex - 1] = dataRow[orgIndex - 1] + IErmReportConstants.getCONST_AGG_TOTAL(); // 合计
                        dataRow[currAmountOriIndex - 1] = currAmountMap.get(pk_org);
//                      dataRow[tallydateIndex - 1] = "";
                        if (!isCurrtype) {
                            // 多币种清空金额字段信息
                            Map<String, String> currtypeMap = countMap.get(org); 
                            for (int nPos = 0; nPos < amount_ori.length; nPos++) {
                                if (orgCurrtypeCount[nPos] > 1 && currtypeMap.keySet().size() > 1) {
                                    dataRow[amount_ori_pos[nPos] - 1] = null;
                                }
                                orgCurrtypeCount[nPos] = 0;
                            }
                        }
                    } else {
                        dataRow[orgIndex - 1] = "";
                    }
                    if (!isCurrtype && curRowQryObjIndex > 0) {
                        String curRowQryObj = (String)dataRow[curRowQryObjIndex - 1];
                        Map<String, String> currtypeMap = countMap.get(curRowQryObj); 
                        // 多币种清空金额字段信息
                        for (int nPos = 0; nPos < amount_ori.length; nPos++) {
                            if (currtypeCount[nPos] > 1 && currtypeMap != null && currtypeMap.keySet().size() > 1) {
                                dataRow[amount_ori_pos[nPos] - 1] = null;
                            }
                        }
                    }
                }
            } else {
                if(currAmountMap.get(keyToCurrType)==null){
                    currAmountMap.put(keyToCurrType, new BigDecimal(0));
                }
                currAmountMap.put(keyToCurrType, currAmountMap.get(keyToCurrType).add((currAmountOri)));
                if(currAmountMap.get(keyToBillType)==null){
                    currAmountMap.put(keyToBillType, new BigDecimal(0));
                }
                currAmountMap.put(keyToBillType, currAmountMap.get(keyToBillType).add((currAmountOri)));
                if(currAmountMap.get(pk_org)==null){
                    currAmountMap.put(pk_org, new BigDecimal(0));
                }
                currAmountMap.put(pk_org, currAmountMap.get(pk_org).add((currAmountOri)));
            }
            //对分摊补单据编号以格式为:单据类型+日期
            datas[i] = dataRow;
        }
		return datas;
	}

	private StringBuffer bulidSql(String whereSql, String currAccMonth, 
	        String startAccMonth, String endAccMonth) {
		StringBuffer resultSql = new StringBuffer();
		resultSql.append("select ").append(getFixedFields())
		// 计算属性
				.append(", (total_period - res_period) accu_period")// 累计摊销期
				.append(", (total_amount - res_amount) accu_amount_ori")// 累计金额
				.append(", 0 curr_amount_ori ")// 本期摊销金额
				.append(", 'N' amt_status ")// 摊销状态
				.append(" from er_expamtinfo ");

		if (!StringUtils.isEmpty(whereSql)) {
			resultSql.append(" where ");
			resultSql.append(whereSql);
		}

		if(currAccMonth != null){
			String accSql = "'" + currAccMonth +  "' between start_period and end_period";

			if(!StringUtils.isEmpty(whereSql)){
				resultSql.append(" and ").append(accSql);
			}else{
				resultSql.append(" where ");
				resultSql.append(accSql);
			}
		} else {
		    // 会计期间不存在，查不出数据
		    if(!StringUtils.isEmpty(whereSql)){
                resultSql.append(" and 1 = 2 ");
            }else{
                resultSql.append(" where 1 = 2 ");
            }
		}
		
		if (startAccMonth != null) {
            String accSql = " start_period = '" + startAccMonth + "' ";

            if(!StringUtils.isEmpty(whereSql)){
                resultSql.append(" and ").append(accSql);
            }else{
                resultSql.append(" where ");
                resultSql.append(accSql);
            }
		}
		
		if (endAccMonth != null) {
		    String accSql = " end_period = '" + endAccMonth + "' ";

            if(!StringUtils.isEmpty(whereSql)){
                resultSql.append(" and ").append(accSql);
            }else{
                resultSql.append(" where ");
                resultSql.append(accSql);
            }
		}

		return resultSql;
	}
	private String tmpTblName = null;
	/**
	 * 获取临时表名<br>
	 * 
	 * @return String<br>
	 * @throws SQLException<br>
	 */
	private String getTmpTblName() throws SQLException {
		if (StringUtils.isEmpty(tmpTblName)) {
			tmpTblName = TmpTableCreator.createTmpTable("tmp_erm_expmortize10",
					getTmpTblColNames(), getTmpTblColTypes());
		}

		return tmpTblName;
	}

	private Integer[] getTmpTblColTypes() {
		Integer[] colNamesStrings = new Integer[21];
		colNamesStrings [0] =  Types.VARCHAR;
		colNamesStrings [1] =  Types.VARCHAR;
		colNamesStrings [2] =  Types.VARCHAR;
		colNamesStrings [3] =  Types.VARCHAR;
		colNamesStrings [4] =  Types.VARCHAR;
		colNamesStrings [5] =  Types.VARCHAR;
		colNamesStrings [6] =  Types.INTEGER;
		colNamesStrings [7] =  Types.DECIMAL;
		colNamesStrings [8] =  Types.VARCHAR;
		colNamesStrings [9] =  Types.VARCHAR;
		colNamesStrings [10] = Types.VARCHAR;
		colNamesStrings [11] = Types.DECIMAL;
		colNamesStrings [12] = Types.VARCHAR;
		colNamesStrings [13] = Types.VARCHAR;
		colNamesStrings [14] = Types.DECIMAL;
		colNamesStrings [15] = Types.DECIMAL;
		colNamesStrings [16] = Types.DECIMAL;
		colNamesStrings [17] = Types.VARCHAR;
		colNamesStrings [18] = Types.DECIMAL;
		colNamesStrings [19] = Types.DECIMAL;
		colNamesStrings [20] = Types.VARCHAR;
//        colNamesStrings [21] = Types.INTEGER;
		return colNamesStrings;
	}
	
	/**
	 * 取得临时表字段名称
	 * @return
	 */
	private String[] getTmpTblColNames() {
		String[] colNamesStrings = new String[21];
		colNamesStrings [0] =  ExpamtinfoVO.PK_EXPAMTINFO;
		colNamesStrings [1] =  ExpamtinfoVO.PK_GROUP;
		colNamesStrings [2] =  ExpamtinfoVO.PK_ORG;
		colNamesStrings [3] =  ExpamtinfoVO.BX_PK_BILLTYPE;
		colNamesStrings [4] =  ExpamtinfoVO.BX_BILLNO;
		colNamesStrings [5] =  ExpamtinfoVO.BX_DJRQ;
		colNamesStrings [6] =  "rn";
		colNamesStrings [7] =  ExpamtinfoVO.TOTAL_AMOUNT + "_ori";
		colNamesStrings [8] =  ExpamtinfoVO.TOTAL_PERIOD;
		colNamesStrings [9] =  ExpamtinfoVO.START_PERIOD;
		colNamesStrings [10] = ExpamtinfoVO.END_PERIOD;
		colNamesStrings [11] = ExpamtinfoVO.RES_AMOUNT + "_ori";
		colNamesStrings [12] = ExpamtinfoVO.RES_PERIOD;
		colNamesStrings [13] = "pk_currtype";
		colNamesStrings [14] = ExpamtinfoVO.RES_ORGAMOUNT + "_loc";
		colNamesStrings [15] = "gr_" + ExpamtinfoVO.RES_GROUPAMOUNT + "_loc";
		colNamesStrings [16] = "gl_" + ExpamtinfoVO.RES_GLOBALAMOUNT + "_loc";
		colNamesStrings [17] =  ExpamtinfoVO.ACCU_PERIOD;
		colNamesStrings [18] =  ExpamtinfoVO.ACCU_AMOUNT+ "_ori";
		colNamesStrings [19] =  ExpamtinfoVO.CURR_AMOUNT+"_ori";
		colNamesStrings [20] =  ExpamtinfoVO.AMT_STATUS;
//        colNamesStrings [21] =  IPubReportConstants.ORDER_MANAGE_VSEQ;
		return colNamesStrings;
	}
	
	public StringBuffer getFixedFields() {
		if (fixedFields == null) {
			fixedFields = new StringBuffer();
			fixedFields.append(ExpamtinfoVO.PK_EXPAMTINFO);
			fixedFields.append(",");
			fixedFields.append(ExpamtinfoVO.PK_GROUP);
			fixedFields.append(",");
			fixedFields.append(ExpamtinfoVO.PK_ORG);
			fixedFields.append(",");
			fixedFields.append(ExpamtinfoVO.BX_PK_BILLTYPE);
			fixedFields.append(",");
			fixedFields.append(ExpamtinfoVO.BX_BILLNO);
			fixedFields.append(",");
			fixedFields.append(ExpamtinfoVO.BX_DJRQ);
			fixedFields.append(",");
			fixedFields.append("0 rn ");
			fixedFields.append(",");
			fixedFields.append(ExpamtinfoVO.TOTAL_AMOUNT).append(" " + ExpamtinfoVO.TOTAL_AMOUNT + "_ori");
			fixedFields.append(",");
			fixedFields.append(ExpamtinfoVO.TOTAL_PERIOD);
			fixedFields.append(",");
			fixedFields.append(ExpamtinfoVO.START_PERIOD);
			fixedFields.append(",");
			fixedFields.append(ExpamtinfoVO.END_PERIOD);
			fixedFields.append(",");
			fixedFields.append(ExpamtinfoVO.RES_AMOUNT).append(" " + ExpamtinfoVO.RES_AMOUNT + "_ori");
			fixedFields.append(",");
			fixedFields.append(ExpamtinfoVO.RES_PERIOD);
			fixedFields.append(",");
			fixedFields.append(ExpamtinfoVO.BZBM).append(" pk_currtype");
			fixedFields.append(",");
			fixedFields.append(ExpamtinfoVO.RES_ORGAMOUNT).append(" " + ExpamtinfoVO.RES_ORGAMOUNT + "_loc");
			fixedFields.append(",");
			fixedFields.append(ExpamtinfoVO.RES_GROUPAMOUNT).append(" gr_" + ExpamtinfoVO.RES_GROUPAMOUNT + "_loc");
			fixedFields.append(",");
			fixedFields.append(ExpamtinfoVO.RES_GLOBALAMOUNT).append(" gl_" + ExpamtinfoVO.RES_GLOBALAMOUNT + "_loc");
		}
		return fixedFields;
	}
	
	
	
	
	
}