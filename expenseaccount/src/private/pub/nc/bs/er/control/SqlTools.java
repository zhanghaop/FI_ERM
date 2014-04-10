package nc.bs.er.control;

public class SqlTools extends ErmNtbSqlTools {
	private String[] selectFields;

	public SqlTools() {
		super();
	}

	public String getGroupBySql() throws Exception {
		String sql = " ";
		if (selectFields == null) {
			return "";
		}
		String[] names = selectFields;
		// QueryVO.getGroupCount加一，唉
		getSqlVO().setGroupCount(names.length);
		for (int i = 0; i < names.length; i++) {
			if (names[i] == null || names[i].trim().equals("")) {
				throw new Exception(nc.bs.ml.NCLangResOnserver.getInstance().getStrByID("20060504",
						"UPP20060504-000077")/* @res "错误！GroupBy中的字段不可为空！" */);
			}
			String colName = getcolName(names, i);
			if (i == 0) {
				sql += colName;
			} else {
				sql += " ," + colName;
			}

		}
		String s_bhxj = getBhxjStr(getFirstNtbVO());
		sql += s_bhxj;
		return sql;
	}

	// FIXME 下面这个类注销
	// public String getWhereFromVO(boolean bArapbill) throws Exception {
	// ArrayList array = getSqlVO().getSourceArr();
	// NtbParamVO ntbvo = (NtbParamVO) array
	// .get(0);
	//
	// String sql = " and ";
	// sql += getDwbmPartWhere();
	// if(getSqlVO().isDetail())
	// {
	// // String pk_corp = ntbvo.getPkcorp();
	// // sql += " and zb.dwbm='" + pk_corp + "' ";
	// //当初为什么注释它？
	// if (ntbvo.getPk_currency() != null &&
	// !ntbvo.getPk_currency().trim().equals("")) {
	// sql += " and fb.bzbm ='" + ntbvo.getPk_currency() + "' ";
	// }
	// }
	//
	// // boolean isUnInure = ntbvo.isUnInure();
	// String rq = "zb.djrq";
	// // if(isUnInure){//包含未审核，即包含未生效
	// // rq = "zb.djrq";
	// // }else{//不含未生效
	// // rq = "zb.shrq";
	// // }
	// //界面上分析日期有三个
	// String dataType = ntbvo.getDateType();
	// if(ntbvo.getDateType() != null)
	// // if(ntbvo.getDateType().trim().equals("单据日期"))
	// // rq = "zb.djrq";
	// // else if(ntbvo.getDateType().trim().equals("审核日期"))
	// // rq = "zb.shrq";
	// // else
	// if(ntbvo.getDateType().trim().equals("zb.sxrq"))
	// {
	// if(bArapbill)
	// rq = "zb.sxrq";
	// else
	// rq = "zb.shrq";
	// }
	// // 预算规范 2008-11-21
	// else if(dataType.trim().equals("zb.djrq") ||
	// dataType.trim().equals("zb.shrq"))
	// rq = dataType.trim();
	// else
	// throw new
	// Exception(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000391")/*@res
	// "预算传输日期类型错误！！"*/);
	// if (ntbvo.getBegDate() != null &&
	// ntbvo.getBegDate().toString().trim().length()>0) {
	// sql += " and "+rq+" >='" + ntbvo.getBegDate() + "' ";
	// }
	// if (ntbvo.getEndDate() != null &&
	// ntbvo.getEndDate().toString().trim().length()>0) {
	// sql += " and "+rq+" <='" + ntbvo.getEndDate() + "' ";
	// }
	// String strBillType = getDjlxbmPartWhere();
	// if(strBillType.trim().length() > 0){
	// sql += " and   " + strBillType;
	// }
	// return sql;
	// }

	/**
	 * @return the selectFields
	 */
	public String[] getSelectFields() {
		return selectFields;
	}

	/**
	 * @param selectFields
	 *            the selectFields to set
	 */
	public void setSelectFields(String[] selectFields) {
		this.selectFields = selectFields;
	}
}