package ru.chaykin.wjss;

import ru.chaykin.wjss.calc.ChangesCalc;
import ru.chaykin.wjss.context.ContextManager;

public class App {

    public static void main(String[] args) {
	new ContextManager().execute(
			context -> new ChangesCalc(context).calculateChanges().forEach(System.out::println));
    }
}
