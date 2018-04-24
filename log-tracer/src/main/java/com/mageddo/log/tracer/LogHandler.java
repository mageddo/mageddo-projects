package com.mageddo.log.tracer;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StopWatch;

import java.util.Collection;

@Aspect
public class LogHandler {

	final Logger logger;

	public LogHandler() {
		logger = LoggerFactory.getLogger(getClass());
	}

	public LogHandler(final Logger logger) {
		this.logger = logger;
	}

	@Around("@annotation(LogTracer)")
	public Object handle(final ProceedingJoinPoint point) throws Throwable {
		final StopWatch timer = new StopWatch();
		timer.start();
		try {
			final Object r = point.proceed(point.getArgs());
			timer.stop();
			logger.info("h_status=success, h_time={}, {}", timer.getTotalTimeMillis(), traceInfo(point, r));
			return r;
		} catch (Throwable e) {
			timer.stop();
			logger.error("h_status=error, h_time={}, msg={}, {}", timer.getTotalTimeMillis(), e.getMessage(), traceInfo(point, null), e);
			throw e;
		}

	}

	static String traceInfo(JoinPoint joinPoint, Object r) {
		final Signature signature = joinPoint.getSignature();
		return String.format("h_clazz=%s, h_m=%s, h_args=%s, h_return=%s, h_line=%d",
			signature.getDeclaringType().getSimpleName(), signature.getName(), argsToString(joinPoint.getArgs()),
			getStringValue(r),
			0 /*joinPoint.getSourceLocation().getLine()*/
		);
	}

	static String argsToString(Object[] args) {

		final StringBuilder str = new StringBuilder("[");
		for (Object arg : args) {
			str.append(getStringValue(arg));
			str.append(", ");
		}
		if(args.length > 0){
			str.delete(str.length() - 2, str.length());
		}
		str.append("]");
		return str.toString();
	}

	static boolean isNative(Object o){
		return o == null || o instanceof Number || o instanceof Character || o instanceof Boolean || o instanceof String;
	}

	static String getStringValue(final Object arg) {
		if(isNative(arg)){
			final String str = String.valueOf(arg);
			return limitStr(str);
		}

		if (arg instanceof Collection){
			return String.valueOf(((Collection) arg).size());
		}

		final StringBuilder str = new StringBuilder();
		str.append("{");
		ReflectionUtils.doWithFields(arg.getClass(), field -> {
			field.setAccessible(true);
			final String name = field.getName().toLowerCase();
			if(name.endsWith("id") || name.indexOf("name") >= 0 || name.indexOf("email") >= 0){
				str.append(name);
				str.append(": ");
				str.append(limitStr(String.valueOf(field.get(arg))));
				str.append(", ");
			} else if (arg instanceof Collection){
				str.append(name);
				str.append(": ");
				str.append(((Collection) arg).size());
				str.append(", ");
			}
		});
		str.append("}");
		return str.toString();
	}

	static String limitStr(String str) {
		return str.substring(0, Math.min(str.length(), 100));
	}

}
