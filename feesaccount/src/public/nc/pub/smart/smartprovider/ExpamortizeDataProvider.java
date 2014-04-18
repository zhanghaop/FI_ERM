package nc.pub.smart.smartprovider;

import java.sql.Types;

import nc.bs.framework.common.NCLocator;
import nc.itf.erm.pub.IExpamortizeBO;
import nc.itf.fipub.report.IPubReportConstants;
import nc.itf.fipub.report.IReportQueryCond;
import nc.pub.smart.context.SmartContext;
import nc.pub.smart.data.DataSet;
import nc.pub.smart.exception.SmartException;
import nc.pub.smart.metadata.Field;
import nc.pub.smart.metadata.MetaData;
import nc.smart.fipub.AbsReportDataProvider;
import nc.utils.fipub.SmartProcessor;

/**
 * 摊销信息分析语义提供者
 * @author chenshuaia
 *
 */
public class ExpamortizeDataProvider extends AbsReportDataProvider{
	private static final long serialVersionUID = 1L;


	private static final String[] CAPTIONS = {
		nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0038")/*@res "集团pk"*/,//[1]
		nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0039")/*@res "财务组织pk"*/,//[2]
		nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0040")/*@res "单据类型"*/,//[3]
		nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0041")/*@res "单据编号"*/,//[4]
		nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0042")/*@res "单据日期"*/,//[5]
		nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0043")/*@res "总摊销金额"*/,//[6]
		nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0044")/*@res "开始摊销会计期"*/,//[7]
		nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0045")/*@res "摊销完成会计期"*/,//[8]
		nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0046")/*@res "已摊销金额"*/,//[9]
		nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0047")/*@res "未摊销金额"*/,//[10]
		nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0048")/*@res "已摊销期间数"*/,//[11]
		nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0049")/*@res "未摊销期间数"*/,//[12]
		nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0050")/*@res "摊销完成标志"*/,//[13]
		nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0073")/*@res "币种PK"*/,//[14]
		nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UC000-0001755")/*@res "币种"*/,
		nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0052")/*@res "本期摊销金额"*/,//[15]
		nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0053")/*@res "本期摊销本币金额"*/,//[16]
		nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0054")/*@res "本期摊销集团本币金额"*/,//[17]
		nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0055")/*@res "本期摊销全局本币金额"*/,//[18]
		nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0056")/*@res "已摊销本币金额"*/,//[19]
		nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0057")/*@res "已摊销集团本币金额"*/,//[20]
		nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0058")/*@res "已摊销全局本币金额"*/,//[21]
		nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0059")/*@res "未摊销本币金额"*/,//[22]
		nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0060")/*@res "未摊销集团本币金额"*/,//[23]
		nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0061")/*@res "未摊销全局本币金额"*/,//[24]
		nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0062")/*@res "总摊销期"*/,//[25]
		nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0040")/*@res "单据类型"*/,//[26]
		nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0016")/*@res "财务组织"*/,//[27]

        nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
                "feesaccount_0", "02011001-0112") /* @res "序号" */
		};

	/** 字段编码 */
	private static final String[] FLDNAMES = {
		"pk_group",//集团pk[1]
		"pk_org",//财务组织pk[2]
		"bx_pk_billtype",//单据类型[3]
		"bx_billno",//单据编号[4]
		"bx_djrq",//单据日期[5]
		"total_amount_ori",//总摊销金额[6]
		"start_period",//开始摊销会计期[7]
		"end_period",//摊销完成会计期[8]
		"accu_amount_ori",//已摊销金额[9]
		"res_amount_ori",//未摊销金额[10]
		"accu_period",//已摊销期间数[11]
		"res_period",//未摊销期间数[12]
		"amt_status",//摊销完成[13]
		"pk_currtype",   //币种主键
		"currtype",      //币种
		"curr_amount_ori",//本期金额[15]
		"curr_orgamount_loc",//本期金额[16]
		"gr_curr_groupamount_loc",//本期金额[17]
		"gl_curr_globalamount_loc",//累计金额[18]
		"accu_orgamount_loc",//累计本币金额[19]
		"gr_accu_groupamount_loc",//累计集团本币金额[20]
		"gl_accu_globalamount_loc",//累计全局本币金额[21]
		"res_orgamount_loc",//剩余本币金额[22]
		"gr_res_groupamount_loc",//剩余集团本币金额[23]
		"gl_res_globalamount_loc",//剩余全局本币金额[24]
		"total_period",//总摊销期[25]
		"billtype",//总摊销期[26]
		"org",//财务组织[26]
        IPubReportConstants.ORDER_MANAGE_VSEQ// 序号
	};

	/** 字段类型 */
	private static final int[] DBCOLUMNTYPES = {
		Types.VARCHAR,
		Types.VARCHAR,
		Types.VARCHAR,
		Types.VARCHAR,//[4]
		Types.VARCHAR, //[5]
		Types.DECIMAL,//[6]
		Types.VARCHAR,//[7]
		Types.VARCHAR,//[8]
		Types.DECIMAL,
		Types.DECIMAL,
		Types.INTEGER,//[11]
		Types.INTEGER,
		Types.VARCHAR,
		Types.VARCHAR,//[14]
        Types.VARCHAR,//[14]
		Types.DECIMAL,//[15]
		Types.DECIMAL,//[16]
		Types.DECIMAL,//[17]
		Types.DECIMAL,//[18]
		Types.DECIMAL,//[19]
		Types.DECIMAL,//[20]
		Types.DECIMAL,//[21]
		Types.DECIMAL,//[22]
		Types.DECIMAL,//[23]
		Types.DECIMAL,//[24]
		Types.INTEGER,//[25]
		Types.VARCHAR,//[26]
		Types.VARCHAR,//[27]
		Types.INTEGER
	};

	/** 字段长度 */
	private static final int[] PRECISIONS = {
			30, 30, 30, 30, 15,
			28, 30, 30, 28, 28,
			19, 19, 30,  30,  50, 28,
			28, 28, 28, 28, 28 ,
			28, 28, 28, 28, 19,
			30, 30, 20
	};


	/** 字段精度 */
	private static final int[] SCALES = {
		0, 0, 0, 0, 0,
		8, 0, 0, 8, 8,
		0, 0, 0, 0, 0, 8,
		8, 8, 8, 8, 8,
		8, 8, 8, 8, 0,
		0, 0, 0
	};

	protected DataSet execute(Object obj, SmartContext context) throws SmartException {
		IReportQueryCond queryVO = (IReportQueryCond) obj;
		return NCLocator.getInstance().lookup(IExpamortizeBO.class).queryExpamortize(queryVO, context);
	}

	public MetaData provideMetaData(SmartContext context) throws SmartException {
		MetaData metaData = new MetaData();
		// 设置动态查询对象
		metaData.addField(SmartProcessor.getSmartQueryObj().toArray(new Field[0]));

		Field fld = null;
		for (int i = 0; i < FLDNAMES.length; i++) {
			fld = new Field();
			fld.setCaption(CAPTIONS[i]);
			fld.setFldname(FLDNAMES[i]);
			fld.setDbColumnType(DBCOLUMNTYPES[i]);
			fld.setPrecision(PRECISIONS[i]);
			fld.setScale(SCALES[i]);
			metaData.addField(fld);
		}

		return metaData;
	}
}