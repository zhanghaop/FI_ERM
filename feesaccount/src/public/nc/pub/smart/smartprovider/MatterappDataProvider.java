package nc.pub.smart.smartprovider;

import java.sql.Types;

import nc.bs.framework.common.NCLocator;
import nc.itf.erm.pub.IMatterappDataBO;
import nc.itf.fipub.report.IPubReportConstants;
import nc.pub.smart.context.SmartContext;
import nc.pub.smart.data.DataSet;
import nc.pub.smart.exception.SmartException;
import nc.pub.smart.metadata.Field;
import nc.pub.smart.metadata.MetaData;
import nc.smart.fipub.AbsReportDataProvider;
import nc.utils.fipub.SmartProcessor;
import nc.vo.erm.matterapp.MtAppDetailVO;
import nc.vo.fipub.report.ReportQueryCondVO;

/**
 * <p>
 *  �������������ϸ�����������ṩ�ߡ�
 * </p>
 *
 * �޸ļ�¼��<br>
 * <li>�޸��ˣ��޸����ڣ��޸����ݣ�</li> <br>
 * <br>
 *
 * @see
 * @author liansg
 * @version V6.0
 * @since V6.0 ����ʱ�䣺2010-11-22 ����09:42:13
 */
public class MatterappDataProvider extends AbsReportDataProvider {

	private static final long serialVersionUID = 1L;
	public static final String PREFIX_GR = "gr_";//����
	public static final String PREFIX_GL = "gl_";//ȫ��
	public static final String SUFFIX_ORI = "_ori";//ԭ��
	public static final String SUFFIX_LOC = "_loc";//����

	/** �ֶ����� */
	private static final String[] CAPTIONS = {
			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0063")/*@res "�������뵥����"*/,
			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
					"feesaccount_0", "02011001-0070")/* @res "����PK" */,
			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
					"feesaccount_0", "02011001-0071")/* @res "ҵ��ԪPK" */,
			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
					"feesaccount_0", "02011001-0072")/* @res "ҵ��ԪCODE" */,
			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common",
					"UCMD1-000396")/* @res "ҵ��Ԫ" */,

			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common",
					"UC000-0002313")/* @res "����" */,
			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
					"feesaccount_0", "02011001-0078")/* @res "��������PK" */,
			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common",
					"UC000-0000807")/* @res "��������" */,
			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
					"feesaccount_0", "02011001-0073")/* @res "����PK" */,
			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common",
					"UC000-0001755")/* @res "����" */,

			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common",
					"UCMD1-000144")/* @res "���ݱ��" */,

		    nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common",
		                            "UC000-0002185")/* @res "ժҪ" */,
		                    
			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0064")/*@res "ִ����"*/,
            nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0",
                    "0201109-0087")/* @res "ִ��������" */,
            nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0",
                    "0201109-0088")/* @res "ִ�������ű���" */,
            nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0",
                    "0201109-0089")/* @res "ִ����ȫ�ֱ���" */,
			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0065")/*@res "���ԭ��"*/,
			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0066")/*@res "����"*/,
			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0067")/*@res "���ű���"*/,
			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0068")/*@res "���ȫ�ֱ���"*/,
			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0069")/*@res "���"*/,
			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0070")/*@res "����"*/,
			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0071")/*@res "���ű���"*/,
			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0072")/*@res "���ȫ�ֱ���"*/,
			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0073")/*@res "�ر�״̬"*/,

            nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
                    "feesaccount_0", "02011001-0112") /* @res "���" */
	};

	/** �ֶα��� */
	private static final String[] FLDNAMES = {
		MtAppDetailVO.PK_MTAPP_BILL, // �������뵥����
		MtAppDetailVO.PK_GROUP, // ��������
		MtAppDetailVO.PK_ORG, // ҵ��Ԫ����
		"code_org", // ҵ��Ԫ����
		"org", // ҵ��Ԫ
		MtAppDetailVO.BILLDATE, // ����
		MtAppDetailVO.PK_BILLTYPE, // ������������
		"billtype", // ��������
		MtAppDetailVO.PK_CURRTYPE, // ��������
		"currtype", // ����
		MtAppDetailVO.BILLNO, // ���ݱ��
		"reason", //ժҪ
		MtAppDetailVO.EXE_AMOUNT+SUFFIX_ORI, // ִ����
		MtAppDetailVO.ORG_EXE_AMOUNT+SUFFIX_LOC, // ����
		PREFIX_GR+MtAppDetailVO.GROUP_EXE_AMOUNT+SUFFIX_LOC, // ���ű���
		PREFIX_GL+MtAppDetailVO.GLOBAL_EXE_AMOUNT+SUFFIX_LOC, // ���ȫ�ֱ���,
		MtAppDetailVO.ORIG_AMOUNT+SUFFIX_ORI, // ���ԭ��
		MtAppDetailVO.ORG_AMOUNT+SUFFIX_LOC, // ����
		PREFIX_GR+MtAppDetailVO.GROUP_AMOUNT+SUFFIX_LOC, // ���ű���
		PREFIX_GL+MtAppDetailVO.GLOBAL_AMOUNT+SUFFIX_LOC, // ���ȫ�ֱ���,
		MtAppDetailVO.REST_AMOUNT+SUFFIX_ORI, // ���ԭ��
		MtAppDetailVO.ORG_REST_AMOUNT+SUFFIX_LOC, // ����
		PREFIX_GR+MtAppDetailVO.GROUP_REST_AMOUNT+SUFFIX_LOC, // ���ű���
		PREFIX_GL+MtAppDetailVO.GLOBAL_REST_AMOUNT+SUFFIX_LOC, // ���ȫ�ֱ���,
		MtAppDetailVO.CLOSE_STATUS, // �ر�״̬
        IPubReportConstants.ORDER_MANAGE_VSEQ// ���
	};

	/** �ֶ����� */
	private static final int[] DBCOLUMNTYPES = { 
	        Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR,
			Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR,
			Types.VARCHAR, Types.VARCHAR, Types.DECIMAL,Types.DECIMAL, Types.DECIMAL, Types.DECIMAL,
			Types.DECIMAL, Types.DECIMAL, Types.DECIMAL, Types.DECIMAL, Types.DECIMAL, 
			Types.DECIMAL, Types.DECIMAL, Types.DECIMAL, Types.VARCHAR,Types.INTEGER 
			};

	/** �ֶγ��� */
	private static final int[] PRECISIONS = { 
	        20,20, 20, 50, 50, 
	        19, 20, 50, 20,50, 
	        50, 100, 28, 28, 28, 28, 
	        28, 28, 28, 28, 28, 
	        28, 28, 28, 20, 20 };

	/** �ֶξ��� */
	private static final int[] SCALES = { 
	        0, 0, 0, 0, 0, 
	        0, 0, 0, 0, 0, 
	        0, 0, 8, 8, 8, 8, 
	        8, 8, 8, 8, 8, 
	        8, 8, 8, 0, 0 };

	@Override
    protected DataSet execute(Object obj, SmartContext context)
			throws SmartException {
		ReportQueryCondVO queryVO = obj == null ? null
				: (ReportQueryCondVO) obj;
		return NCLocator.getInstance().lookup(IMatterappDataBO.class)
				.queryMatterappData(queryVO, context);
	}

	@Override
    public MetaData provideMetaData(SmartContext context) throws SmartException {
		MetaData metaData = new MetaData();
		// ���ö�̬��ѯ����
		metaData.addField(SmartProcessor.getSmartQueryObj().toArray(
				new Field[0]));

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