package nc.vo.er.exception;

import nc.vo.pub.BusinessRuntimeException;

public class ErmBusinessRuntimeException extends BusinessRuntimeException {

    private static final long serialVersionUID = -5783747953644440089L;

    public ErmBusinessRuntimeException() {
        super();
    }

    public ErmBusinessRuntimeException(String msg, Throwable throwable) {
        super(msg, throwable);
    }

    public ErmBusinessRuntimeException(String msg) {
        super(msg);
    }

}
