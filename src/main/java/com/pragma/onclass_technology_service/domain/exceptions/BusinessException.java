package com.pragma.onclass_technology_service.domain.exceptions;

import com.pragma.onclass_technology_service.domain.emuns.TechnicalMessage;
import lombok.Getter;

@Getter
public class BusinessException extends ProcessorException{

    public BusinessException(TechnicalMessage technicalMessage){
        super(technicalMessage.getMessage(), technicalMessage);
    }

    public BusinessException(String message, TechnicalMessage technicalMessage) {
        super(message, technicalMessage);
    }
}
