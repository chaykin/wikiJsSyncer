package ru.chaykin.wjss.option;

import com.beust.jcommander.IStringConverter;
import ru.chaykin.wjss.conflict.ResolveType;

public class ResolveTypeConverter implements IStringConverter<ResolveType> {

    @Override
    public ResolveType convert(String value) {
	ResolveType type = ResolveType.of(value);
	if (type == null) {
	    type = ResolveType.valueOf(value.toUpperCase());
	}

	return type;
    }
}
