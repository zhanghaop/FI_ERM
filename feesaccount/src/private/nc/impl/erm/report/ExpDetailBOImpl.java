package nc.impl.erm.report;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.erm.common.ErmBillConst;
import nc.bs.erm.pub.ErmReportUtil;
import nc.bs.erm.sql.ExpDetailSQLCreator;
import nc.bs.erm.util.ErUtil;
import nc.bs.logging.Logger;
import nc.itf.erm.pub.IExpDetailBO;
import nc.itf.erm.report.IErmReportConstants;
import nc.itf.fipub.report.IPubReportConstants;
import nc.jdbc.framework.processor.ResultSetProcessor;
import nc.pub.smart.context.SmartContext;
import nc.pub.smart.data.DataSet;
import nc.pub.smart.exception.SmartException;
import nc.pub.smart.script.statement.select.PlainSelect;
import nc.pub.smart.smartprovider.ExpDetailDataProvider;
import nc.utils.fipub.FipubSqlExecuter;
import nc.utils.fipub.ReportMultiVersionSetter;
import nc.utils.fipub.SmartProcessor;
import nc.vo.erm.expenseaccount.ExpenseAccountVO;
import nc.vo.fipub.report.PubCommonReportMethod;
import nc.vo.fipub.report.ReportQueryCondVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.rs.MemoryResultSet;

import org.apache.commons.lang.StringUtils;

/**
 * <p>
 * 报销管理帐表查询，费用明细账查询接口实现类。
 * </p>
 * 
 * @version V6.0
 * @since V6.0 创建时间：2010-12-7 下午02:34:59
 */
public class ExpDetailBOImpl extends FipubSqlExecuter implements IExpDetailBO {

	@Override
    public DataSet queryExpenseDetail(ReportQueryCondVO queryVO,
			SmartContext context) throws SmartException {
		setContext(context);
		DataSet resultDataSet = new DataSet();

		/***************************************************************/
		/********************** 屏蔽语义模型定义时的校验 ********************/
		if (queryVO == null) {
			// 定义语义模型时，会执行到这里进行校验，且queryVO为空
			try {
				resultDataSet.setMetaData(new ExpDetailDataProvider()
						.provideMetaData(null));
			} catch (SmartException e) {
				// 这里永远不会抛出异常，故吃掉异常也不会造成影响
			}
			return resultDataSet;
		}

		/****************************************************************/

		try {
            PlainSelect select = (PlainSelect)context.getAttribute("key_current_plain_select");
            select.setWhere(null);
			ExpDetailSQLCreator sqlCreator = new ExpDetailSQLCreator();
			// 设置查询对象VO的内容
			sqlCreator.setParams(queryVO);

			String[] arrangeSqls = sqlCreator.getArrangeSqls();
			String resultSql = sqlCreator.getResultSql();

			// 构建临时表数据
			for (String sql : arrangeSqls) {
				executeUpdate(sql);
			}

			// 从临时表取得结果
			MemoryResultSet resultSet = (MemoryResultSet) executeQuery(resultSql, getResultProcessor());

            //转换币种
            ErUtil.convertCurrtype(resultSet, queryVO);
			// 插入【币种】名称
			PubCommonReportMethod.insertNameColumn(resultSet, IPubReportConstants.CURRTYPE, "pk_currtype", "currtype");
			// 插入【交易类型】名称
            PubCommonReportMethod.insertNameColumn(resultSet,
                    IPubReportConstants.BILLTYPE, "pk_billtype", "billtype",
                    200);

			// 设置返回结果元数据
			resultDataSet.setMetaData(SmartProcessor.getMetaData(resultSet));

			Object[][] datas = getDatas(resultSet, queryVO);
			queryVO.setQueryDetail(true);
			datas = new ReportMultiVersionSetter(resultSet.getMetaData0(), queryVO).setOrg(datas, ExpenseAccountVO.BILLDATE );
			int dateIndex = resultSet.getMetaData0().getNameIndex(ExpenseAccountVO.BILLDATE );
			PubCommonReportMethod.convert2ClientTime(datas, context, dateIndex);
			PubCommonReportMethod.setVSeq(datas, resultDataSet.getMetaData().getIndex(IPubReportConstants.ORDER_MANAGE_VSEQ));

			// 设置返回结果数据集
			resultDataSet.setDatas(datas);
			ErmReportUtil.processDataSet(context, resultDataSet);
		} catch (Exception e) {
			String errMsg = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0053")/*@res "明细账查询出错！"*/;
			Logger.error(errMsg, e);
			throw new SmartException(errMsg, e);
		}

		return resultDataSet;

	}

	@SuppressWarnings("unchecked")
	private Object[][] getDatas(MemoryResultSet mrs, ReportQueryCondVO queryVO) throws SQLException {
		boolean isMultiOrg = queryVO.getPk_orgs().length > 1;

		ArrayList<List<Object>> dataRowList = mrs.getResultArrayList();

		if (dataRowList == null || dataRowList.size() == 0) {
			return new Object[0][0];
		}

		if (!IPubReportConstants.ORG_LOCAL_CURRENCY.equals(queryVO.getLocalCurrencyType())) {
			String[] targetFields = new String[] { "ORG_AMOUNT_LOC" };
			String[] formulas = null;
			if (IPubReportConstants.GROUP_LOCAL_CURRENCY.equals(queryVO.getLocalCurrencyType())) {
				// 集团本币
				formulas = new String[] { "ORG_AMOUNT_LOC->GR_GROUP_AMOUNT_LOC" };
			} else if (IPubReportConstants.GLOBLE_LOCAL_CURRENCY.equals(queryVO.getLocalCurrencyType())) {
				// 全局本币
				formulas = new String[] { "ORG_AMOUNT_LOC->GL_GLOBAL_AMOUNT_LOC" };
			}
			mrs.setColumnByFormulate_type(targetFields, formulas);
		}

		Object datas[][] = new Object[dataRowList.size()][mrs.getMetaData().getColumnCount()];

		int rnIndex = mrs.getColumnIndex("rn");
		int orgIndex = mrs.getColumnIndex("org");
//		int currtypeIndex = mrs.getColumnIndex(ExpenseAccountVO.PK_CURRTYPE);

        int currtypeIndex = mrs.getColumnIndex("currtype");
		
		int tallydateIndex = mrs.getColumnIndex(ExpenseAccountVO.BILLDATE);
		int accperiod = mrs.getColumnIndex(ExpenseAccountVO.ACCPERIOD);
//		int briefIndex = mrs.getColumnIndex(ExpenseAccountVO.REASON);
		//单据编号
		int src_billnoI = mrs.getColumnIndex(ExpenseAccountVO.SRC_BILLNO);
		int src_billtypeI = mrs.getColumnIndex("pk_billtype");
		List<Integer> qryObjIndex = new ArrayList<Integer>();
		List<Integer> qryObjNameIndex = new ArrayList<Integer>();
		List<Integer> totalCol = new ArrayList<Integer>(); // 小计列
        List<Integer> totalCol2 = new ArrayList<Integer>(); // 小计列
		for (int i = 0; i < queryVO.getQryObjs().size(); i++) {
			qryObjIndex.add(mrs.getColumnIndex(IPubReportConstants.QRY_OBJ_PREFIX + i));
			qryObjNameIndex.add(mrs.getColumnIndex(IPubReportConstants.QRY_OBJ_PREFIX + i + "code"));
            totalCol2.add(qryObjNameIndex.get(i));
			totalCol.add(qryObjIndex.get(i));
		}
		totalCol.add(currtypeIndex);
        totalCol2.add(orgIndex);

        Map<String, Map<String, String>> countMap = new HashMap<String, Map<String, String>>();
        
		Object[] dataRow = null;
		int rn = -1;
		boolean isObj = false;
		
		boolean isCurrtype = false;
		String[] amount_ori = new String[] {"assume_amount_ori"};
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
//								dataRow[qryObjNameIndex.get(k) - 1] = dataRow[qryObjNameIndex.get(k) - 1]
//										+ IErmReportConstants.getConst_Sub_Total(); // 小计
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
//						dataRow[briefIndex - 1] = IErmReportConstants.getCONST_ALL_TOTAL(); // 总计
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
						dataRow[tallydateIndex - 1] = "";
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
			}
			//对分摊补单据编号以格式为:单据类型+日期
			datas[i] = dataRow;
			if(datas[i][src_billtypeI-1] != null && ErmBillConst.Expamoritize_BILLTYPE.equals(datas[i][src_billtypeI-1])){
					String accperiodval = (String) datas[i][accperiod-1];
					datas[i][src_billnoI-1] = ErmBillConst.Expamoritize_BILLTYPE+accperiodval;
			}
		}

		return datas;
	}

	@Override
	public Object queryExpenseDetailResultSet(ReportQueryCondVO queryVO,
			SmartContext context, ResultSetProcessor processor)
			throws BusinessException {
		                             
		return null;
	}

}

// /:~
