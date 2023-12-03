package ru.chaykin.wjss.command;

import com.beust.jcommander.Parameters;

public abstract class BaseCommand {

    public String getName() {
	return getClass().getAnnotation(Parameters.class).commandNames()[0];
    }

    public abstract void execute();
}
