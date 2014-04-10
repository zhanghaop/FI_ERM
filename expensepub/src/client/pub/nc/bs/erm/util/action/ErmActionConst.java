package nc.bs.erm.util.action;

import nc.vo.ml.NCLangRes4VoTransl;

/**
 * 费用管理按钮编码
 * @author luolch
 *
 */
public abstract class ErmActionConst {
	public static final String FAIL_MSG = nc.ui.ml.NCLangRes.getInstance().getStrByID("2011000_0", "02011000-0040")/*
																													 * @
																													 * res
																													 * "失败！"
																													 */;

	public static final String SUCCESS_MSG = nc.ui.ml.NCLangRes.getInstance().getStrByID("2011000_0", "02011000-0039")/*
																													 * @
																													 * res
																													 * "成功！"
																													 */;

	public static final String BILLTYE  = "BillType";//交易类型
	public static String getBillTypeName() {
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011000_0","02011000-0000")/*@res "交易类型"*/;
	}

	public static final String CODEIMPORT  = "CodeImport";//条码输入
	public static String getCodeImportName() {
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0","0upp2012V575-0006")/*@res "条码输入"*/;
	}

	public static final String CONFIRM  = "Confirm";//确认
	public static String getConfirmName() {
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011000_0","02011000-0001")/*@res "确认"*/;
	}

	public static final String DOCUMENT  = "Document";//附件管理
	public static String getDocumentName() {
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011000_0","02011000-0002")/*@res "附件管理"*/;
	}

	public static final String LINKBUDGET  = "LinkBudget";//联查预算
	public static String getLinkBudgetName() {
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011000_0","02011000-0003")/*@res "联查预算"*/;
	}

	public static final String LINKBX  = "LinkBx";//联查报销单
	public static String getLinkBxName() {
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0","0upp2012V575-0012")/*@res "联查报销单"*/;
	}

	public static final String LINKVOUCHER  = "LinkVoucher";//联查凭证
	public static String getLinkVoucherName() {
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0","0upp2012V575-0013")/*@res "联查凭证"*/;
	}

	public static final String PRINTLIST  = "PrintList";//打印清单
	public static String getPrintListName() {
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0","0upp2012V575-0022")/*@res "打印清单"*/;
	}

	public static final String OFFICALPRINT  = "Officalprint";//正式打印
	public static String getOfficalprintName() {
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011000_0","02011000-0004")/*@res "正式打印"*/;
	}

	public static final String CANCELPRINT  = "Cancelprint";//取消打印
	public static String getCancelprintName() {
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011000_0","02011000-0005")/*@res "取消打印"*/;
	}

	public static final String RAPIDSHARE  = "RapidShare";//快速分摊
	public static String getRapidShareName() {
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011000_0","02011000-0006")/*@res "快速分摊"*/;
	}

	public static final String TEMPSAVE  = "TempSave";//暂存
	public static String getTempSaveName() {
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011000_0","02011000-0007")/*@res "暂存"*/;
	}

	public static final String UNCONFIRM  = "UnConfirm";//取消确认
	public static String getUnConfirmName() {
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011000_0","02011000-0008")/*@res "取消确认"*/;
	}

	public static final String VOUCHER  = "Voucher";//制单
	public static String getVoucherName() {
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011000_0","02011000-0009")/*@res "制单"*/;
	}

	public static final String INITUNCLOSE  = "UnCancel";//期初关闭
	public static String getInitUnCLose() {
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011000_0","02011000-0010")/*@res "取消关闭"*/;
	}

	public static final String CLOSELINE  = "closeline";//关闭行
	public static String getCloseLineName() {
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011000_0","02011000-0011")/*@res "关闭行"*/;
	}

	public static final String OPENLINE  = "openline";//关闭行
	public static String getOpenLineName() {
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011000_0","02011000-0012")/*@res "重启行"*/;
	}

	public static final String CLOSEBILL  = "closebill";//关闭单据
	public static String getCloseBillName() {
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011000_0","02011000-0013")/*@res "关闭"*/;
	}

	public static final String OPENBILL  = "openbill";//重启单据
	public static String getOpenBillName() {
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011000_0","02011000-0014")/*@res "重启"*/;
	}
	
	public static final String LINKAPPSTATUS  = "LinkAppStatus";//查看审批意见
	public static String getLinkAppStatusName() {
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0131")/*@res "查看审批意见"*/;
	}
	
	public static final String BATCHCONTRAST  = "BatchContrast";//批量冲借款
	public static String getBatchContrastName() {
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UPP2011-000075")/*@res "批量冲借款"*/;
	}
	
	public static final String CANCELBATCHCONTRAST  = "CancelBatchContrast";//取消批量冲借款
	public static String getCancelBatConName() {
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().
		getStrByID("upp2012v575_0","0upp2012V575-0121")/*@res ""取消批量冲借款""*/;
	}
	
	public static final String CONTRAST  = "Contrast";//冲借款
	public static String getContrastBame() {
		return NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UPTcommon-000360")/*@res "冲借款"*/;
	}
}