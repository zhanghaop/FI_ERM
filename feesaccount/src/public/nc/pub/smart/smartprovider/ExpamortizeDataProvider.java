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
 * ̯����Ϣ���������ṩ��
 * @author chenshuaia
 *
 */
public class ExpamortizeDataProvider extends AbsReportDataProvider{
	private static final long serialVersionUID = 1L;


	private static final String[] CAPTIONS = {
		nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0038")/*@res "����pk"*/,//[1]
		nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0039")/*@res "������֯pk"*/,//[2]
		nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0040")/*@res "��������"*/,//[3]
		nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0041")/*@res "���ݱ��"*/,//[4]
		nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0042")/*@res "��������"*/,//[5]
		nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0043")/*@res "��̯�����"*/,//[6]
		nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0044")/*@res "��ʼ̯�������"*/,//[7]
		nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0045")/*@res "̯����ɻ����"*/,//[8]
		nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0046")/*@res "��̯�����"*/,//[9]
		nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0047")/*@res "δ̯�����"*/,//[10]
		nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0048")/*@res "��̯���ڼ���"*/,//[11]
		nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0049")/*@res "δ̯���ڼ���"*/,//[12]
		nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0050")/*@res "̯����ɱ�־"*/,//[13]
		nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0073")/*@res "����PK"*/,//[14]
		nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UC000-0001755")/*@res "����"*/,
		nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0052")/*@res "����̯�����"*/,//[15]
		nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0053")/*@res "����̯�����ҽ��"*/,//[16]
		nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0054")/*@res "����̯�����ű��ҽ��"*/,//[17]
		nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0055")/*@res "����̯��ȫ�ֱ��ҽ��"*/,//[18]
		nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0056")/*@res "��̯�����ҽ��"*/,//[19]
		nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0057")/*@res "��̯�����ű��ҽ��"*/,//[20]
		nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0058")/*@res "��̯��ȫ�ֱ��ҽ��"*/,//[21]
		nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0059")/*@res "δ̯�����ҽ��"*/,//[22]
		nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0060")/*@res "δ̯�����ű��ҽ��"*/,//[23]
		nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0061")/*@res "δ̯��ȫ�ֱ��ҽ��"*/,//[24]
		nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0062")/*@res "��̯����"*/,//[25]
		nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0040")/*@res "��������"*/,//[26]
		nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0016")/*@res "������֯"*/,//[27]

        nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
                "feesaccount_0", "02011001-0112") /* @res "���" */
		};

	/** �ֶα��� */
	private static final String[] FLDNAMES = {
		"pk_group",//����pk[1]
		"pk_org",//������֯pk[2]
		"bx_pk_billtype",//��������[3]
		"bx_billno",//���ݱ��[4]
		"bx_djrq",//��������[5]
		"total_amount_ori",//��̯�����[6]
		"start_period",//��ʼ̯�������[7]
		"end_period",//̯����ɻ����[8]
		"accu_amount_ori",//��̯�����[9]
		"res_amount_ori",//δ̯�����[10]
		"accu_period",//��̯���ڼ���[11]
		"res_period",//δ̯���ڼ���[12]
		"amt_status",//̯�����[13]
		"pk_currtype",   //��������
		"currtype",      //����
		"curr_amount_ori",//���ڽ��[15]
		"curr_orgamount_loc",//���ڽ��[16]
		"gr_curr_groupamount_loc",//���ڽ��[17]
		"gl_curr_globalamount_loc",//�ۼƽ��[18]
		"accu_orgamount_loc",//�ۼƱ��ҽ��[19]
		"gr_accu_groupamount_loc",//�ۼƼ��ű��ҽ��[20]
		"gl_accu_globalamount_loc",//�ۼ�ȫ�ֱ��ҽ��[21]
		"res_orgamount_loc",//ʣ�౾�ҽ��[22]
		"gr_res_groupamount_loc",//ʣ�༯�ű��ҽ��[23]
		"gl_res_globalamount_loc",//ʣ��ȫ�ֱ��ҽ��[24]
		"total_period",//��̯����[25]
		"billtype",//��̯����[26]
		"org",//������֯[26]
        IPubReportConstants.ORDER_MANAGE_VSEQ// ���
	};

	/** �ֶ����� */
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

	/** �ֶγ��� */
	private static final int[] PRECISIONS = {
			30, 30, 30, 30, 15,
			28, 30, 30, 28, 28,
			19, 19, 30,  30,  50, 28,
			28, 28, 28, 28, 28 ,
			28, 28, 28, 28, 19,
			30, 30, 20
	};


	/** �ֶξ��� */
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
		// ���ö�̬��ѯ����
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