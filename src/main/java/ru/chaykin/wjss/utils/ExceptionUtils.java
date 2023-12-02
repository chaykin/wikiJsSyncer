package ru.chaykin.wjss.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExceptionUtils {

    /**
     * Implements try-finally behaviour, but allows throw exception from finally block
     * (and not suppress original exception in catch block)
     */
    public static void tryFinally(IBlockExecutor tryBlock, IBlockExecutor finallyBlock) {
	Throwable throwable = null;
	try {
	    tryBlock.execute();
	} catch (Throwable t) {
	    throwable = t;
	}

	try {
	    finallyBlock.execute();
	} catch (Throwable t) {
	    if (throwable == null) {
		throwable = t;
	    } else {
		throwable.addSuppressed(t);
	    }
	}

	if (throwable != null) {
	    if (throwable instanceof RuntimeException ex) {
		throw ex;
	    }

	    throw new RuntimeException("Could not execute try-finally block", throwable);
	}
    }

    public interface IBlockExecutor {
	void execute() throws Throwable;
    }
}
