package nc.vo.erm.common;

import java.io.Serializable;

import nc.bs.logging.Logger;
import nc.vo.arap.bx.util.ActionUtils;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;

import org.apache.commons.lang.StringUtils;

public class MessageVO implements Serializable {

    private static final long serialVersionUID = -4277550962139957543L; 

    private AggregatedValueObject successVO;

    private int messageType;//消息类型
    
    private String errorMessage;
    
    private boolean success = true;
    
    private String showField = null; //需要显示值的字段如：djbh,billno等
    
    public MessageVO(AggregatedValueObject successVO, int messageType) {
        super();
        this.successVO = successVO;
        this.messageType = messageType;
    }
    
    public MessageVO(AggregatedValueObject successVO, int messageType, boolean success, String errorMessage) {
        super();
        this.successVO = successVO;
        this.messageType = messageType;
        this.success = success;
        this.errorMessage = errorMessage;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public AggregatedValueObject getSuccessVO() {
        return successVO;
    }

    public void setSuccessVO(AggregatedValueObject successVO) {
        this.successVO = successVO;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String toString() {

        StringBuffer msg = new StringBuffer();

        String key = ActionUtils.getOperationName(messageType);

        if (success)
            msg.append(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000308",null,new String[]{key, getBh()})/*@res " 单据成功, "*/);
        else
            msg.append(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000309",null,new String[]{key, getBh()})/*@res " 单据结束, "*/);

        if(StringUtils.isNotEmpty(this.errorMessage)){
            msg .append(" : ").append(errorMessage);
        }

        return msg.toString();
    }
    
    public String getBh() {
        Object bh = null;
        if (showField != null)
            bh = successVO.getParentVO().getAttributeValue(showField);
        if (bh == null) {
            bh = successVO.getParentVO().getAttributeValue("djbh");
        }
        if (bh == null) {
            bh = successVO.getParentVO().getAttributeValue("billno");
        }
        if (bh == null) {
            try {
                bh = (String)successVO.getParentVO().getPrimaryKey();
            } catch (BusinessException e) {
                Logger.error(e.getMessage(), e);
            }
        }
        return bh == null ? StringUtils.EMPTY : bh.toString();
    }

	public void setShowField(String showField) {
		this.showField = showField;
	}
}
