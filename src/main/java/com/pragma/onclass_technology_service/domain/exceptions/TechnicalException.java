package com.pragma.onclass_technology_service.domain.exceptions;

import com.pragma.onclass_technology_service.domain.emuns.TechnicalMessage;
import lombok.Getter;

@Getter
public class TechnicalException extends ProcessorException{

    public TechnicalException(Throwable cause, TechnicalMessage technicalMessage){
        super(cause, technicalMessage);
    }

    public TechnicalException(TechnicalMessage technicalMessage){
        super(technicalMessage.getMessage(), technicalMessage);
    }
}
