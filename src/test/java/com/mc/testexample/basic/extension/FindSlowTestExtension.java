package com.mc.testexample.basic.extension;

import com.mc.testexample.basic.annotation.SlowTest;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.lang.reflect.Method;

public class FindSlowTestExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback {

    // private static final long THRESHOLD = 1000L; // 1초
    private long THRESHOLD = 1000L;
    private static final String START_TIME = "START_TIME";

    // @RegisterExtension 사용을 위한 생성자
    public FindSlowTestExtension(long threshold){
        this.THRESHOLD = threshold;
    }

    @Override
    public void beforeTestExecution(ExtensionContext context) throws Exception {
        ExtensionContext.Store store = getStore(context);
        store.put(START_TIME, System.currentTimeMillis());
    }

    @Override
    public void afterTestExecution(ExtensionContext context) throws Exception {

        // SlowTest 어노테이션 여부 확인
        Method requiredTest = context.getRequiredTestMethod();
        SlowTest annotation = requiredTest.getAnnotation(SlowTest.class);

        String testMethodName = context.getRequiredTestMethod().getName();
        ExtensionContext.Store store = getStore(context);
        long start_time = store.remove(START_TIME, long.class);
        long duration = System.currentTimeMillis() - start_time;    // 소요시간 측정
        if (duration > THRESHOLD && annotation == null){  // 1초 이상 되면, 안내 메시지를 띄움
            System.out.printf("Please consider mark method [%s] with @SlowTest.\n", testMethodName);
        }
    }

    private ExtensionContext.Store getStore(ExtensionContext context){
        String testClassName = context.getRequiredTestClass().getName(); // 클래스명 추출
        String testMethodName = context.getRequiredTestMethod().getName(); // 클래스명 추출
        return context.getStore(ExtensionContext.Namespace.create(testClassName, testMethodName));
    }
}
