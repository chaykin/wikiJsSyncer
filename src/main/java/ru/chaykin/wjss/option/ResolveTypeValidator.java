package ru.chaykin.wjss.option;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.ParameterException;

public class ResolveTypeValidator implements IParameterValidator {

    @Override
    public void validate(String name, String value) throws ParameterException {
	try {
	    new ResolveTypeConverter().convert(value);
	} catch (IllegalArgumentException e) {
	    throw new ParameterException("Unknown parameter '" + name + "' value: " + value);
	}
    }
}
