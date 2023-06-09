
**'더 자바, 애플리케이션을 테스트하는 다양한 방법'**(백기선) 출처, 참고
<br/>
-   Junit5 : 자바 개발 테스트용 프레임워크
-   Mockito : 테스트 코드의 의존성을 가짜로 만들어 테스트
-   Docker : 테스트를 유용하게 하는 Test containers를 활용
-   JMeter : 성능 태스트.
-   Chaos Monkey : 운영 이슈를 로컬에서 재현하는 방법 
-   ArchUnit : 어플리케이션 아키텍처 테스트
-   JaCoCo : 코드 커버리지 체크 및 리포트 

<br/>

# <1-1. Junit5>
## 1\. JUnit5 소개
  
\- 자바 개발자가 가장 많이 사용하는 테스트용 프레임워크   
\- 스프링 부트 2.2로 오르면서 JUnit5로 버젼업 됨. 


### 1) Junit4와의 차이점 

\- Junit4는 하나의 Jar파일 디펜던시로 들어오는 형태였는데, Junit5는 그 자체로 모듈화가 되어있음. 

(1) 구성

3개 세부 모듈, Junit5는 Junit Platform 위에 Jupiter, Vintage가 올라감. 

-   Junit Platform : 실행 런처 제공. 콘솔, main 안, 툴 내 코드에서도 실행이 가능함. TestEngine API 제공
-   Jupiter: TestEngine API 구현체 (JUnit 5를 제공)
-   Vintage: TestEngine API 구현체 (JUnit 4와 3을 지원)

<br/>

### 2) 기초 예제 

 (1) 우선 SpringBoot 프로젝트를 만든다.

 최신 버젼을 사용해서 스프링부트 3.0.6을 사용했는데, 이때는 java 17버젼이 필요.

```
plugins {
    id 'java'
    id 'org.springframework.boot' version '3.0.6' // 2023-05-18. java 17 필요.
    id 'io.spring.dependency-management' version '1.1.0'
}

group = 'com.mc.testexample'
version = '0.0.1-SNAPSHOT'
sourceCompatibility = '17'

repositories {
    mavenCentral()
}

dependencies {
    implementation 'org.springframework.boot:spring-boot-starter' // 스프링 부트 2.2부터 Junit5으로 탑재됨. (기존은 4)
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
}

tasks.named('test') {
    useJUnitPlatform()
}
```

만약 스프링 부트를 사용하지 않는다면, 아래와 같이 디펜던시를 추가해준다.

```
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter-engine</artifactId>
    <version>5.5.2</version>
    <scope>test</scope>
</dependency>
```

```
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter' // 스프링 부트 2.2부터 Junit5으로 탑재됨. (기존은 4)
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
}
```

<br/>


 (2) 테스트 파일 만들기

스프링 부트 Main 파일을 써도 되고, 스프링 부트가 주가 되는 테스트는 아님으로 임의로 파일을 만들어도 된다.

```
public class Basic {
}
```

ctrl+shift+t : 테스트 파일 만들고 싶은 곳에서 단축키 누르면, test-java 패키지 안에 테스트 파일이 생긴다.




```
/**
 * Public 필요 없음 : Junit5부터는 class나 method가 public일 필요 없음 (Junit4는 public이었어야함)
 *                  - 자바 리플렉션 사용. 굳이 public 사용할 필요 없음
 */
class BasicTest {

    @Test
    void create1(){
        Basic basic = new Basic();
        assertNotNull(basic);
        System.out.println("create1");
    }

    @Test
    void create2(){
        System.out.println("create2");
    }

    @Test
    @Disabled // 테스트를 실행하지 않을 때(해당 소스가 Deprecated 된 경우). 자주 사용하지 않는 것이 좋음. Junit4의 @Ignored
    void create3(){
        System.out.println("create3");
    }

    /**
     * 테스트를 실행하기 전에 딱 한번만 실행
     * - static. 무조건 static void로 작성
     * - private X, default O
     * - return이 있으면 안됨.
     */
    @BeforeAll // Junit4의 @BeforeClass
    static void beforeAll(){
        System.out.println("before all");
    }

    /**
     * 테스트를 실행 후 딱 한번만 실행
     * 조건 사항은 @BeforeAll과 같음
     */
    @AfterAll // Junit4의 @AfterClass
    static void afterAll(){
        System.out.println("after all");
    }

    /**
     * 모든 테스트 실행 전, 각각의 클래스와 메소드를 실행할 때 한번씩 실행.
     * static일 필요 없음.
     */
    @BeforeEach // Junit4의 @Before
    void beforeEach(){
        System.out.println("before each");
    }

    /**
     * 모든 테스트 실행 후, 각각의 클래스와 메소드를 실행할 때 한번씩 실행.
     * static일 필요 없음.
     */
    @AfterEach // Junit4의 @After
    void afterEach(){
        System.out.println("after each");
    }

}
```


<br/>


### 3) 테스트 이름 표기 방법

기본적으로 아래 테스트 메소드명이 표시되는데, Camel보다는 \_를 쓰는 방식이 좀 더 편하다.

원하는 명으로 보여줄 때 @DisplayNameGeneration, @DisplayName를 사용한다.

(1) @DisplayNameGeneration

-   Method와 Class에 사용.
-   테스트 이름을 표기하는 전략 선택
-   기본 구현체로 ReplaceUnderscores

```
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class BasicTest {
```

<br/>

(2) @DisplayName

-   테스트 이름을 각각 지정.
-   @DisplayNameGeneration 보다 우선 순위가 높다.

```
    @Test
    @DisplayName("생성 3")
    @Disabled // 테스트를 실행하지 않을 때(해당 소스가 Deprecated 된 경우). 자주 사용하지 않는 것이 좋음. Junit4의 @Ignored
    void create_3(){
        System.out.println("create3");
    }
```


이 쪽이 테스트 네임을 더 상세하게 지정할 수 있어서 좋을 것 같다.

테스트 이름이 길어지면 알아보기도 쉽다.

더 다양한 내용을 아래에 있다.
[https://junit.org/junit5/docs/current/user-guide/#writing-tests-display-names](https://junit.org/junit5/docs/current/user-guide/#writing-tests-display-names)

<br/>

### 4) Assertion

나는 org.junit.jupiter.api.Assertions.\* 전체를 import 해서 쓰면 편리하다.

-   assertEqulas(expected, actual) : 실제 값이 기대한 값과 같은지 확인
-   assertNotNull(actual) : 값이 null이 아닌지 확인
-   assertTrue(boolean) : 다음 조건이 참(true)인지 확인
-   assertAll(executables...) : 모든 확인 구문 확인
-   assertThrows(expectedType, executable) : 예외 발생 확인
-   assertTimeout(duration, executable) : 특정 시간 안에 실행이 완료되는지 확인

\- 마지막 파라미터로 메시지를 표현할 수 있는데, String을 직접 줄 수도 있고,  Supplier<String> 타입의 인스턴스를 람다형태로 전달할 수도 있다.

\- 한 테스트에서 걸리면, 아래 테스트는 수행하지 않는다.

<br/>

(1) assertEqulas(expected, actual)

기대값, 실제값, 메시지의 순서로 작성한다.

```
public class Robot {
    private RobotStatus status;

    public RobotStatus getStatus(){
        return this.status;
    }
}
```

```
public enum RobotStatus {
    AWAITING, MOVING, STOPPED, ERROR
}
```

로봇과 로봇 상태에 대한 클래스를 만들고

```
class RobotTest {

    @Test
    @DisplayName("로봇 초기상태 확인")
    void check_robot_init_status(){
        Robot robot = new Robot();
        assertEquals(RobotStatus.AWAITING, robot.getStatus(), "로봇 초기 상태는 무조건 Awaiting 이어야 합니다.");
    }
}
```

로봇 초기 상태를 확인할 수 있다. 이 때 로봇 초기 상태를 지정하지 않았기 때문에

아래처럼 결과가 나온다.

초기값을 아래와 같이 넣어주면

```
private RobotStatus status = RobotStatus.AWAITING;
```


성공 결과를 볼 수 있다.

이 때 메시지를 String으로 만들면 값을 조합할 경우, 테스트 성공여부와 상관없이 무조건 새로 생성하기 때문에

성능을 생각한다면 람다식 Supplier<String> 형태로 써주는 것이 좋다.

```
    @Test
    @DisplayName("로봇 초기상태 확인")
    void check_robot_init_status(){
        Robot robot = new Robot();

        // String : 테스트 성공여부와 상관없이 계속 String을 만듦.
        assertEquals(RobotStatus.AWAITING, robot.getStatus(), "로봇 초기 상태는 무조건 " + RobotStatus.AWAITING +"이어야 합니다.");

        // 람다식 : 필요할 때만 String을 만듦. (성능)
        assertEquals(RobotStatus.AWAITING, robot.getStatus(), () -> "로봇 초기 상태는 무조건" + RobotStatus.AWAITING +" 이어야 합니다.");

        // 에러 메시지를 만드는 방식이 복잡하면 Supplier<String>으로 작성, 간추리면 위와 같다.
        assertEquals(RobotStatus.AWAITING, robot.getStatus(), new Supplier<String>() {
            @Override
            public String get() {
                return "로봇 초기 상태는 무조건" + RobotStatus.AWAITING +" 이어야 합니다.";
            }
        });
    }
```
<br/>
(2) assertNotNull(actual) : 값이 null이 아닌지 확인

(3) assertTrue(boolean) : 다음 조건이 참(true)인지 확인

```
@NoArgsConstructor
@Getter
public class Robot {
    private RobotStatus status = RobotStatus.AWAITING;
    private int timeout;

    public Robot(int timeout){
        this.timeout = timeout;
    }
}
```

Getter, Setter를 더 만들기 싫으므로, lombok을 추가하고 Robot 클래스를 수정했다.

```
@Test
@DisplayName("로봇 초기 타임아웃 값 확인")
void check_initial_time_out(){
    Robot robot = new Robot(3);
    assertTrue(robot.getTimeout() > 10, ()-> "로봇 타임 아웃값은 10보다 커야합니다.");
}
```

초기값을 3으로 해서 다시 돌려보면, 아래 결과를 얻을 수 있다.

<br/>
(4) assertAll(executables...) : 모든 확인 구문 확인

\- 한 테스트에서 걸리면, 아래 테스트는 수행하지 않는데, 모든 테스트를 수행하기 위해서는 assertAll을 쓴다. 

```
@Test
@DisplayName("로봇 초기 전체값 확인")
void check_initial_values(){
    Robot robot = new Robot(3);
    assertAll(
            () -> assertNotNull(robot),
            () -> assertEquals(RobotStatus.AWAITING, robot.getStatus(), () -> "로봇 초기 상태는 무조건 " + RobotStatus.AWAITING +" 이어야 합니다."),
            () -> assertTrue(robot.getTimeout() > 10, ()-> "로봇 타임 아웃값은 10보다 커야합니다.")
    );
}
```

아래 2개 테스트를 실패가 나도록 했기 때문에 아래와 같은 결과를 얻을 수 있다.

<br/>
(5) assertThrows(expectedType, executable) : 예외 발생 확인

```
@NoArgsConstructor
@Getter
public class Robot {
    private RobotStatus status;
    private int timeout;

    public Robot(int timeout){
        if(timeout < 0) throw new IllegalArgumentException("timeout 설정값은 0보다 커야합니다.");
        this.timeout = timeout;
    }
}
```

타임아웃 설정값이 음수일 때 체크할 수 있는 방법으로, 생성자에서 음수가 들어오면 에러가 발생하도록 한다

```
    @Test
    @DisplayName("로봇 클래스 생성")
    void create_new_robot(){
        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> new Robot(-10));
        assertEquals("timeout 설정값은 0보다 커야합니다.", exception.getMessage());
    }
```

Test 클래스에서 해당 에러가 발생하는 지 확인한다. 실제 에러가 발생했기 때문에 True로 떨어진다.

(왠지 에러를 만들었으니 에러가 나야할 것 같지만 조건을 명확히 확인할 것)

여기서는 일부러 검증하기 위해 assertEquals로 같은 에러 문구를 내는지 확인했다.

```
assertDoesNotThrow(()->new Robot(-10)),
```

아니면 assertDoesNotThrow를 써서 에러와 에러 메시지를 직접 확인할 수도 있다.

<br/>
(6) assertTimeout(duration, executable) : 특정 시간 안에 실행이 완료되는지 확인

```
assertTimeout(Duration.ofMillis(100), () -> {
                    new Robot(10);
                    Thread.sleep(1000);
})
```

실행 시 100ms 안에 실행되어야하는데 sleep을 1000을 주었다.

문제는 테스트는 해당 로직이 다 끝날 때까지 계속되어, 거의 2초가 걸리게 된다.
따라서 100ms가 되었을 때 바로 테스트가 종료되게 하려면, **assertTimeoutPreemptievely** (선점제한시간)를 걸어준다.

```
assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
    new Robot(10);
    Thread.sleep(1000);
}
```
  
그러면 100ms가 넘었을때 바로 테스트가 종료되어 시간을 단축할 수 있다.

다만 코드블럭을 별도의 쓰레드에서 실행하기 때문에, **ThreadLocal**을 사용하는 코드가 있으면, 해당 코드 블럭이 예상치 못한 문제가 발생할 수 있다. 

스프링 트랜잭션은 ThreadLocal을 기본 전략으로 하는데,  다른 Thread에서 공유되지 않기 때문에 롤백이 안되고 DB에 반영될 수 있어서 조심해서 사용한다.

```
* <h2>Preemptive Timeouts</h2>
*
* <p>The various {@code assertTimeoutPreemptively()} methods in this class
* execute the provided {@code executable} or {@code supplier} in a different
* thread than that of the calling code. This behavior can lead to undesirable
* side effects if the code that is executed within the {@code executable} or
* {@code supplier} relies on {@link ThreadLocal} storage.
*
* <p>One common example of this is the transactional testing support in the Spring
* Framework. Specifically, Spring's testing support binds transaction state to
* the current thread (via a {@code ThreadLocal}) before a test method is invoked.
* Consequently, if an {@code executable} or {@code supplier} provided to
* {@code assertTimeoutPreemptively()} invokes Spring-managed components that
* participate in transactions, any actions taken by those components will not be
* rolled back with the test-managed transaction. On the contrary, such actions
* will be committed to the persistent store (e.g., relational database) even
* though the test-managed transaction is rolled back.
```

```
    @Test
    @DisplayName("로봇 클래스 생성")
    void create_new_robot() {
        Robot robot = new Robot(3);
        assertAll(
//                () -> {
//                    IllegalArgumentException exception =
//                            assertThrows(IllegalArgumentException.class, () -> new Robot(-10));
//                    assertEquals("timeout 설정값은 0보다 커야합니다.", exception.getMessage());
//                },
                () -> assertDoesNotThrow(() -> new Robot(-10)),
                () -> assertNotNull(robot),
                () -> assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
                    new Robot(10);
                    Thread.sleep(1000);
                }),
                () -> assertEquals(RobotStatus.AWAITING, robot.getStatus(), () -> "로봇 초기 상태는 무조건 " + RobotStatus.AWAITING + " 이어야 합니다."),
                () -> assertTrue(robot.getTimeout() > 10, () -> "로봇 타임 아웃값은 10보다 커야합니다.")
        );
    }
```

  
AssertJ, Hemcrest, Truth 등의 라이브러리를 사용할 수도 있는데, 

Jupiter와 비슷하지만 원하는 것을 쓰면 된다.  (AssertJ, Hemcrest도 기본 제공됨)

```
@Test
@DisplayName("AssertJ 예제")
void create_new_robot_with_assertJ() {
    Robot robot = new Robot(11);
    assertThat(robot.getTimeout()).isGreaterThan(12);
}
```

  
# <1-2. JUnit5 테스트 필터링, 테스트 반복> 
## **1\. 조건에 따른 테스트 실행**
#### **1) 시스템 환경변수에 따른 실행**
시스템 환경변수에 따라서 테스트 실행 여부를 결정할 수 있다.

```
class RobotConfigTest {

    @Test
    @DisplayName("시스템 환경변수 값에 따른 실행 테스트")
    void create_new_robot(){
        String test_env = System.getenv("ENV");
        System.out.println(test_env);
        assertEquals("LOCAL", test_env);
    }
}
```

실행해보면 값이 다르다.
이때 시스템 환경변수를 바꾸면 인텔리제이도 재시작해야한다.

환경변수 값을 Local로 바꾸고, 조건에 따라 실행을 다르게 했다. 

**(1) assumingThat으로 코드로 조건 분기**

```
    @Test
    @DisplayName("시스템 환경변수 값에 따른 실행 테스트")
    void create_new_robot(){
        String test_env = System.getenv("ENV");
        assumingThat(test_env.equalsIgnoreCase("LOCAL"), () -> {
            System.out.println("local");
        });

        assumingThat(test_env.equalsIgnoreCase("DEV"), () -> {
            System.out.println("dev");
        });
    }
```

이 값을 가지고 다시 테스트해보았다.


**(2) 어노테이션으로 조건 주기**

@EnabledIfEnvironmentVariable(named = "환경변수명", matches = "값")을 사용한다.

```
    @Test
    @EnabledIfEnvironmentVariable(named = "ENV", matches = "LOCAL")
    @DisplayName("로컬 환경변수일 때만 실행")
    void excute_local_env(){
        System.out.println("local");
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "ENV", matches = "DEV")
    @DisplayName("DEV 환경변수일 때만 실행")
    void excute_dev_env(){
        System.out.println("dev");
    }
```

기존의 @EnabledIf는 deprecated 됨.

#### **2) Os 따라 실행**

어노테이션 EnabledOnOs로 Os 조건에 따라 실행할 수도 있다.

Os는 배열로도 적을 수 있다.

```
    @Test
    @EnabledOnOs(OS.WINDOWS)
    @DisplayName("윈도우일 때만 실행")
    void execute_if_win(){
        System.out.println("win");
    }

    @Test
    @EnabledOnOs(OS.MAC)
    @DisplayName("맥일 때만 실행")
    void execute_if_mac(){
        System.out.println("mac");
    }

    @Test
    @EnabledOnOs({OS.MAC, OS.LINUX, OS.SOLARIS})
    @DisplayName("윈도우가 아닌 몇몇 경우만 실행")
    void execute_if_not_win(){
        System.out.println("not win");
    }
```


조건에 따라 실행되지 않은 테스트가 보인다.

#### **3) 자바 버젼에 따른 실행** 

```
    @Test
    @EnabledOnJre({JRE.JAVA_8, JRE.JAVA_9, JRE.JAVA_10, JRE.JAVA_11, JRE.JAVA_17})
    @DisplayName("java 8 이상")
    void execute_jre_above_8(){
        System.out.println("JRE above 8");
    }

    @Test
    @EnabledOnJre({JRE.OTHER})
    @DisplayName("java 8 이상이 아닌 경우")
    void execute_jre_under_8(){
        System.out.println("JRE under 8");
    }
```

#### **4) config 파일에 따른 실행** 

test 파일에서는 Java 파일로 binding하여 Autowired 하는 방식은 null 값이 떠서 아래 방법을 사용했는데

좀 더 나은 방법이 있을 지 확인 중이다.

```
server.address.ip: "192.168.0.1"
server.resources_path.imgs: "/imgs"

app:
  type: "EU"
```

application.yml 파일을 작성하고, test 파일에는

@EnableConfigurationProperties  
@SpringBootTest(classes=테스트용 클래스)를  넣어서 실행했다.

```
@EnableConfigurationProperties
@SpringBootTest(classes=Robot.class)
class ExecuteByConfigTest {

    @Value("${app.type}")
    private String type;

    @Test
    void execute_by_config(){
        assertEquals(Region.EU.name(), type);

        if(type.equals(Region.EU.name())){
            System.out.println(Region.EU.name());
        }else{
            System.out.println(Region.N_EU.name());
        }
    }
}
```

## **2\. 필터링 방법**

원하는 테스트 그룹만 필터링 해서 테스트 하는 기능이다.

\- 개발툴 (인텔리제이 등) 설정

\- 메이븐에서 플러그인을 설정

\- 그래들 타입 설정

으로 실행해볼 수 있다.

예를 들어 실행이 빠른 것과 느린 것을 구분하여, 빠른 것은 로컬, 느린 것은 개발 서버에서 테스트 할 수 있다. 

\- 메소드에 @Tag를 추가하여 필터링 하고, 여러 태그를 쓸 수 있다.

```
class TagingTest {

    @Test
    @Tag("fast")
    @Tag("multi")
    @DisplayName("속도가 빠른 테스트")
    void create_new_robot_fast(){
        assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
            new Robot(10);
            Thread.sleep(10);
        });
    }

    @Test
    @Tag("slow")
    @Tag("single")
    @DisplayName("속도 느린 테스트")
    void create_new_robot_slow(){
        assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
            new Robot(10);
            Thread.sleep(1000);
        });
    }
```

#### **1) 인텔리제이 태그 필터링 설정**

소스를 작성하고, 인텔리제이에서 설정해준다.

속도가 빠른 테스트 (fast)로 태깅한 것만 실행이 된다.

#### **2) 메이븐에서 테스트 필터링 하는 방법**

프로파일에서 디폴트 프로파일을 만들어서 플로그인 설정을 한다.

```
<profiles>
	<profile>
    	<id>default</id>
        <activation> 
        	<activeByDfault>true</activeByDefault>
        </activation>
        <build>
        	<plugin>
                <artifactId>원하는 이름</artifactId>
                <configuration>
                    <groups>fast | slow</groups>
                </configuration>
            </plugin>
        </build>
    </profile>
    <profile>
    	<id>ci</id>
        <build>
        	<plugin>
                <artifactId>원하는 이름</artifactId>
            </plugin>
        </build>
    </profile>
</profiles>
```

플러그인을 추가하면 된다. (ci는 운용용)

!는 not, &는 and, |는 or이다.

```
<plugin>
    <artifactId>원하는 이름</artifactId>
    <configuration>
        <groups>fast</groups>
    </configuration>
</plugin>
```

콘솔에서 ./mvnw test -P ci 형태로 실행한다.

#### **3) 그래들에서 테스트 필터링 하는 방법**

build.gradle 파일에 들어가면 기본적으로 아래 설정이 되어있다.

```
tasks.named('test') {
    useJUnitPlatform()
}
```

includeTags, excludeTags를 이용해서 포함하고 제외할 태그를 설정할 수 있다. 

```
tasks.named('test') {
    useJUnitPlatform {
        includeTags 'slow'
        excludeTags 'multi'
    }
}
```

그런데 이렇게 되면 test라는 이름의 하나의 task만 사용하게 되기 떄문에, 다른 task를 구성해서 구분해주는 것이  좋다.

```
// task 분리
// `gradle test`: integration tag가 붙은 테스트는 제외
tasks.named('test') {
    useJUnitPlatform {
        excludeTags 'dev', 'prod'

    }
}

task devTest(type: Test) {
    useJUnitPlatform {
        includeTags 'dev'
    }
}


task prodTest(type: Test) {
    useJUnitPlatform {
        includeTags 'prod'
    }
}
```

-   gradle test : dev, prod 제외하고 실행
-   gradle dev : devTest 실행

```
class TagingTest {

    @Test
    @Tag("local")
    @Tag("fast")
    @Tag("multi")
    @DisplayName("속도가 빠른 테스트")
    void create_new_robot_all(){
        System.out.println("tag - local 테스트");
        assertTrue(1==1, "테스트 용도");
    }

    @Test
    @Tag("dev")
    @Tag("fast")
    @Tag("multi")
    @DisplayName("속도가 빠른 테스트")
    void create_new_robot_fast(){
        System.out.println("tag - dev 테스트");
        assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
            new Robot(10);
            Thread.sleep(10);
        });
    }

    @Test
    @Tag("prod")
    @Tag("slow")
    @Tag("single")
    @DisplayName("속도 느린 테스트")
    void create_new_robot_slow(){
        System.out.println("tag - prod 테스트");
        assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
            new Robot(10);
            Thread.sleep(1000);
        });
    }
}
```

새 프로젝트를 만들면서 java17, spring boot 3.0을 썼기 때문에 기존 설치한 그래들과 자바버젼 변경은 번거로워서 결과 확인은 콘솔이 아닌 인텔리제이에서 했다.

gradle test로 실행하면 dev, prod를 제외한 모든 테스트가 실행되고
gradle dev로 실행하면, @Tag("dev")로 설정된 것만 테스트 된다.


\*\* 참고

\- [https://maven.apache.org/guides/introduction/introduction-to-profiles.html](https://maven.apache.org/guides/introduction/introduction-to-profiles.html)  
\- [https://junit.org/junit5/docs/current/user-guide/#running-tests-tag-expressions](https://junit.org/junit5/docs/current/user-guide/#running-tests-tag-expressions)

#### **3) 커스텀 태그 만들기**

\- 어노테이션을 추가하여 커스텀 태그를 만들 수 있다. 어노테이션 만드는 법은 스프링에서 전부 동일하다.

\- 커스텀 태그도 여러 태그를 동시에 쓸 수 있다.

\- @Tag("문자열")은 오타가 날 수도 있고 타입이 안전하지 않기 때문에, 커스텀 태그를 만드는 쪽이 안전하다.

```
@Test
@Target(ElementType.METHOD) // 메소드에 사용
@Retention(RetentionPolicy.RUNTIME) // 어노테이션 정보가 런타임까지 유지 
@Tag("dev") // "dev"는 문자열이므로 타입이 안전하지 않음. (오타 발생 많음)
public @interface DevTest {

}
```

```
class CustomTagTest {

    @Test
    @LocalTest
    void create_new_robot_all(){
        System.out.println("tag - local 테스트");
        assertTrue(1==1, "테스트 용도");
    }

    @Test
    @DevTest
    @DisplayName("속도가 빠른 테스트")
    void create_new_robot_fast(){
        System.out.println("tag - dev 테스트");
    }

    @Test
    @ProdTest
    @DisplayName("속도 느린 테스트")
    void create_new_robot_slow(){
        System.out.println("tag - prod 테스트");
    }
}
```

## **3\. 반복 테스트**

여러번 검증을 위해서 테스트를 반복할 수 있다.

\- @RepeatedTest  : 반복 횟수와 반복 테스트 이름을 설정할 수 있다.

\- @ParameterizedTest : 테스트에 여러 다른 매개변수를 대입해가며 반복 실행한다.

#### **@RepeatedTest**

\- 반복 횟수(value)와 테스트명(name)을 설정할 수 있고, name에 {displayName}, {currentRepetition}, {totalRepetitions}을 넣을 수 있다.

```
//    @RepeatedTest(10) // (횟수)
    @RepeatedTest(value=10, name = "{displayName}, {currentRepetition} / {totalRepetitions}")
    @DisplayName("반복 테스트")
    void repeatAfterMe(RepetitionInfo info){
        System.out.println("test : " + info.getCurrentRepetition() + "/" + info.getTotalRepetitions());
    }
```

#### **@ParameterizedTest** 

\- 여러 다른 매개변수를 대입해서 반복 실행 시에 사용한다.

\- 역시 테스트명(name)을 설정할 수 있고, name에 {displayName}, {index}, {arguments}, 파라미터 {0}, {1},을 넣을 수 있다.

\- 횟수는 매개변수에 따라 다르므로 설정x

\- 매개변수는 아래로 셋팅할 수 있다.

-   @ValueSource
-   @NullSource, @EmptySource, @NullAndEmptySource
-   @EnumSource
-   @MethodSource
-   @CsvSource
-   @CvsFileSource
-   @ArgumentSource

\- 위의 태그로 준 매개변수들은 태그 순서에 따라 위에서부터 순차적으로 실행된다

**(1) @ValueSource**

\- strings, boolean, classes 등 자료형 배열을 넣을 수 있다.

```
@ParameterizedTest(name = "{index} : {displayName}  message= {0}")
@ValueSource(strings = {"우리", "회사", "화이팅"} )
@DisplayName("반복 테스트 - Parameterized")
void repeatByParameters(String message){
    System.out.println(message);
    assertTrue(message.length()>2, () -> "길이가 두 글자를 초과해야합니다.");
}
```

**(2) @NullSource, @EmptySource, @NullAndEmptySource**

\- @NullSource : null 소스를 하나 더 넣어줌

```
@ParameterizedTest(name = "{index} : {displayName}  message= {0}")
@ValueSource(strings = {"우리", "회사", "화이팅"} )
@NullSource
@DisplayName("반복 테스트 - Parameterized")
void repeatByParameters(String message){
    System.out.println(message);
    assertTrue(message.length()>1, () -> "길이가 두 글자를 초과해야합니다.");
}
```
length체크를 하기 때문에, null을 마지막 인자로 넣어주면 테스트 실패

\- @EmptySource : 빈 소스를 하나 더 넣어줌.

```
@ParameterizedTest(name = "{index} : {displayName}  message= {0}")
@ValueSource(strings = {"우리", "회사", "화이팅"} )
@EmptySource
@NullSource
@DisplayName("반복 테스트 - Parameterized")
void repeatByParameters(String message){
    System.out.println(message);
    assertTrue(message.length()>1, () -> "길이가 두 글자를 초과해야합니다.");
}
```

\- @NullAndEmptySource : Null과 Empty를 순차적으로 넣어준다.

Null과 빈 값을 테스트할 때 유용할 것 같다.

```
    @ParameterizedTest(name = "{index} : {displayName}  message= {0}")
    @NullAndEmptySource
    @ValueSource(strings = {"우리", "회사", "화이팅"} )
//    @EmptySource
//    @NullSource
    @DisplayName("반복 테스트 - Parameterized")
    void repeatByParameters(String message){
        System.out.println(message);
        assertTrue(message.length()>1, () -> "길이가 두 글자를 초과해야합니다.");
    }
```

다만 @NullAndEmptySource와 @NullSource, @EmptySource들이 동시에 먹지는 않는다.

아래처럼 다 넣어서 테스트해도 위에서 한번만 실행된다.

```
@ParameterizedTest(name = "{index} : {displayName}  message= {0}")
@NullAndEmptySource
@ValueSource(strings = {"우리", "회사", "화이팅"} )
@EmptySource
@NullSource
@DisplayName("반복 테스트 - Parameterized")
void repeatByParameters(String message){
    System.out.println(message);
    assertTrue(message.length()>1, () -> "길이가 두 글자를 초과해야합니다.");
}
```

**(3) @EnumSource**

**(4) @MethodSource**

**(5) @CsvSource**

  - 여러 인자를 ,(comma)를 이용해서 넣어줄 수 있다. 

**(6) @CvsFileSource**

**(7) @ArgumentSource**

#### **인자 값 타입 변환**

\- @ParameterizedTest를 사용하면 파라미터를 원하는 대로 받을 수 있는데 이때 유용하다.

**(1) 암묵적 타입 변환**  
: 암묵적으로 타입을 내가 받고자 하는 타입으로 변환을 해준다.  
[https://junit.org/junit5/docs/current/user-guide/#writing-tests-parameterized-tests-argument-conversion-implicit](https://junit.org/junit5/docs/current/user-guide/#writing-tests-parameterized-tests-argument-conversion-implicit)

 [JUnit 5 User Guide

Although the JUnit Jupiter programming model and extension model do not support JUnit 4 features such as Rules and Runners natively, it is not expected that source code maintainers will need to update all of their existing tests, test extensions, and custo

junit.org](https://junit.org/junit5/docs/current/user-guide/#writing-tests-parameterized-tests-argument-conversion-implicit)

"true"로 값이 들어오면 파라미터 타입에 따라 boolean인 true로 암묵적으로 변환을 해준다.

**(2) 명시적 타입 변환 - 단일 인자**

기본 데이터형의 경우 그 값이 어떤 값인지 특정하기 어렵기 때문에, 특정 객체로 자료형을 나타내는 것이 좋다.

이때  SimpleArgumentConverter 상속 받아서 컨버터를 구현해서 사용하면 좋다. 

\- 변환하려는 파라미터 앞에 @ConvertWith를 붙여준다.

```
// SimpleArgumentConvert는 하나의 argument만 사용할 수 있음.
public class RobotConverter extends SimpleArgumentConverter {

    @Override
    protected Object convert(Object source, Class<?> targetType) throws ArgumentConversionException {
        assertEquals(Robot.class, targetType, "Can only convert to Robot");
        return new Robot(Integer.parseInt(source.toString()));
    }
}
```

```
// SimpleArgumentConvert로 단일 인자값을 받아서 convert (명시적 타입 지정)
@ParameterizedTest(name = "{index} : {displayName}  message= {0}")
@ValueSource(ints = {11, 12, 13})
@DisplayName("반복 Parameterized - SimpleArgumentConvert")
void repeatByMultiValues(@ConvertWith(RobotConverter.class) Robot robot){
    assertTrue(robot.getTimeout()>11, () -> "Timeout은 10보다 커야합니다.");
}
```

\- 다만 SimpleArgumentConverter는 하나의 argument에 대해서만 사용 가능.

  인자값을 여러개 쓰려면 ArgumentsAccessor를 이용한다.

**(3) 명시적 타입 변환 - 인자 여러개 조합**

\- ArgumentsAccessor를 이용해서 구현한다.

우선 로봇 객체에 name 변수를 하나 더 추가해준다.

```
@NoArgsConstructor
@Getter
@ToString
public class Robot {
    private String name = "unused";
    private RobotStatus status = RobotStatus.AWAITING;
    private int timeout;

    public Robot(int timeout){
        if(timeout < 0) throw new IllegalArgumentException("timeout 설정값은 0보다 커야합니다.");
        this.timeout = timeout;
    }

    public Robot(int timeout, String name){
        this.name = name;
        this.timeout = timeout;
    }
```

파라미터로 ArgumentsAggregator를 받는다.

```
@ParameterizedTest(name = "{index} : {displayName}  message= {0}")
@CsvSource({"11, '우리'", "12, '모두'","13, '직원'"} )
@DisplayName("반복 Parameterized - ArgumentsAggregator 사용")
void repeatUsingArgumentsAccessor(ArgumentsAccessor accessor){
    Robot robot = new Robot(accessor.getInteger(0), accessor.getString(1));
    System.out.println(robot.toString());
}
```
  
\- ArgumentsAggregator 인터페이스를 class로 분리해서 구현할 수도 있다.

  다만 public클래스나 static inner 클래스로 작성해야 한다.

```
// 파라미터 인자를 여러개 사용할 경우
// 제약 조건 : public 클래스 이거나 static inner 클래스로 만들어야 함
public class RobotAggregator implements ArgumentsAggregator {

    @Override
    public Robot aggregateArguments(ArgumentsAccessor accessor, ParameterContext context) throws ArgumentsAggregationException {
        return new Robot(accessor.getInteger(0), accessor.getString(1));
    }
}
```

ArgumentAggregator를 구현한 class를 작성한 후,

```
@ParameterizedTest(name = "{index} : {displayName}  message= {0}")
@CsvSource({"11, '우리'", "12, '모두'","13, '직원'"} )
@DisplayName("반복 Parameterized - custom ArgumentsAggregator 사용")
void repeatUsingConverter(@AggregateWith(RobotAggregator.class) Robot robot){
    System.out.println(robot.toString());
}
```

테스트 소스에서 @AggregateWith(클래스 명)을 적으면 된다.

# <1-3. JUnit5 테스트 인스턴스 & 순서 지정>

JUnit은 기본 전략으로 테스트 메소드마다 테스트 인스턴스를 새로 만든다. 테스트 메소드를 독립적으로 실행해서 예상하지 못한 부작용을 방지하기 위한 전략이다.

### **1\. 기본 전략  : 메소드마다 인스턴스 생성**

예를 들어, 전역변수를 생성해서 각 메소드에서 값을 변화해도, 초기값으로 계속 남아있다.

각 메소드에서 this를 출력해보면 객체가 다른 것도 확인이 가능하다.

```
public class InstanceTest {

    int value = 1;

    @Test
    void check_instance1(){
        System.out.println("1 : " + this.toString() +", value : " + (value++));
    }

    @Test
    void check_instance2(){
        System.out.println("2 : " + this.toString() +", value : " + (value++));
    }
}
```


테스트 간의 의존성을 없애기 위해서이며, 테스트 순서에 의해 인스턴스 값이 바뀌면 불안정해진다.

테스트 순서는 예측을 할 수가 없다. (JUnit5부터는 선언되는 순으로 실행되기는 한다.)

따라서 테스트간 의존성은 없애는 것이 좋다.

### **2\. @TestInstance(Lifecycle.PER\_CLASS) : 클래스 1개당 인스턴스 생성**

\- JUnit 5에서는 기본 전략을 변경할 수 있다.

  클래스에 @TestInstance(Lifecycle.PER\_CLASS)를 붙이면 클래스당 인스턴스를 하나만 만들어 사용한다.

\-  클래스당 인스턴스를 하나만 만들면 @BeforeAll과 @AfterAll를 default 메소드로 정의할 수 있다. 즉, 꼭 static일 필요가 없다.

테스트를 해보면

```
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class InstanceTest {

    int value = 1;

    @Test
    void check_instance1(){
        System.out.println("1 : " + this.toString() +", value : " + (value++));
    }

    @Test
    void check_instance2(){
        System.out.println("2 : " + this.toString() +", value : " + (value++));
    }
}
```


클래스 인스턴스를 하나만 만들어서 value값이 증가한 것을 볼 수 있다.

```
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class InstanceTest {

    int value = 1;

    @Test
    void check_instance1(){
        System.out.println("1 : " + this.toString() +", value : " + (value++));
    }

    @Test
    void check_instance2(){
        System.out.println("2 : " + this.toString() +", value : " + (value++));
    }

    @BeforeAll
    void beforeAll(){
        System.out.println("시작 시 한 번 실행");
    }

    @AfterAll
    void afterAll(){
        System.out.println("종료 시 한 번 실행");
    }
}
```

@BeforAll과 @AfterAll에 static을 적지 않아도 된다.


### **3\. @TestMethodOrder : 테스트 순서 지정**  

\- JUnit5부터는 작성한 테스트 메소드 순서대로 실행되기는 하지만, 이에 의존해서는 안된다.

  테스트 인스턴스를 메소드마다 새로 만드는 것처럼 테스트 간의 의존성을 막기 위함인데, JUnit5 내부 로직 구성에 따라 언제든지 변경될 수 있다.

\- 하지만 종종 **순서에 따라** 테스트를 수행해야하는 경우가 있다. 

  예) 인수테스트, 특정 기능, 시나리오 테스트(회원가입, use 케이스 등)

  이 때 **@TestMethodOrder**를 사용하는데 **@TestInstance(Lifecycle.PER\_CLASS)** 를 같이 사용하는 게 좋다. (필수x)

#### **@TestMethodOrder**

-   Alphanumeric :
-   OrderAnnotation : 순서에 따라 실행 (org.junit.jupiter.api)
-   Random :

```
// Order는 낮을 수록 우선순위가 높다.
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OrderTest {

    int value = 1;

    @Test
    @Order(3)
    void check_instance1(){
        System.out.println("1 : " + this.toString() +", value : " + (value++));
    }

    @Test
    @Order(1)
    void check_instance2(){
        System.out.println("2 : " + this.toString() +", value : " + (value++));
    }

    @Test
    @Order(2)
    void check_instance3() {
        System.out.println("3 : " + this.toString() + ", value : " + (value++));
    }

    @Test
    @Order(1)
    void check_instance4() {
        System.out.println("4 : " + this.toString() + ", value : " + (value++));
    }
}
```

우선순위가 같은 경우에는 무작위로 실행된다. (그래도 같은 숫자를 주지 말자)


<br/>
# <1-2. properties, 확장, 마이스레이션> 

## **1\. junit-platform.properties**

JUnit 설정 파일을 별개로 클래스패스 루트 (src/test/resources/)에 넣어서, 일괄적용을 시킬 수 있다.

```
#테스트 이름 표기 전략 설정
junit.jupiter.displayname.generator.default = \
    org.junit.jupiter.api.DisplayNameGenerator$ReplaceUnderscores

#테스트 인스턴스 라이프사이클 설정
junit.jupiter.testinstance.lifecycle.default = per_class

# 확장팩 자동 감지 기능
# junit5는 확장 기능이 많이 바뀌었는데, 자동감지할 수 있도록 하는 방식이다.
junit.jupiter.extensions.autodetection.enabled = true

# @Disabled 무시하고 실행하기 - 모든 테스트 실행
junit.jupiter.conditions.deactivate = org.junit.*DisabledCondition
```

## **2\. 확장 모델 (Extension)**

\- JUnit 4 확장 모델 : @RunWith(Runner), TestRule, MethodRule.  
\- JUnit 5 확장 모델 : Extension으로 통일(단순)  
  

기존 4에서는 방식이 다양했는데, 5에서는 Extension으로 사용되고 기존 방식은 쓸 수 없다.

#### **확장팩 등록 방법**

-   선언적인 등록  : @ExtendWith
-   프로그래밍 등록  : @RegisterExtension
-   자동 등록 자바 : ServiceLoader 이용

**선언적인 등록 (클래스)  : @ExtendWith**

```
@Test
@Target(ElementType.METHOD) // 메소드에 사용
@Retention(RetentionPolicy.RUNTIME) // 어노테이션 정보가 런타임까지 유지
public @interface SlowTest {
}
```

2개의 라이크사이클 콜백을 구현한다.

테스트하는데 걸리는 시간을 측정

ExtensionContext를 파라미터로 받는데, 여기에 값을 저장하는 Store 인터페이스가 있다.

Name정보를 만드는데, Context에서 클래스와 메소드 네임에서 빼온다.

before에서 시작 시간을 넣고, after에서 넣어둔 시작 시간을 가져와서 총 소요시간을 계산한다.

만약 1초 이상 시간이

```
public class FindSlowTestExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback {

    private static final long THRESHOLD = 1000L; // 1초
    private static final String START_TIME = "START_TIME";

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
```

```
@ExtendWith(FindSlowTestExtension.class) // 1. 선언적인 방법
public class ExtensionTest {

    @Test
    void slowTest() throws InterruptedException {
        Thread.sleep(1000);
        System.out.println("Slow Method for extension test");
    }

}
```

```
@SlowTest
void slowTest() throws InterruptedException {
    Thread.sleep(1000);
    System.out.println("Slow Method for extension test");
}
```

**프로그래밍 등록 (메소드)  : @RegisterExtension**

선언적으로 클래스에 @ExtendWith를 적용할 경우, 생성자를 만들어도 매번 실행 시간을 다르게 조작하기가 어렵다.

```
public class FindSlowTestExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback {

    // private static final long THRESHOLD = 1000L; // 1초
    private long THRESHOLD = 1000L;
    private static final String START_TIME = "START_TIME";

    // @RegisterExtension 사용을 위한 생성자
    public FindSlowTestExtension(long threshold){
        this.THRESHOLD = threshold;
    }
    
    
    // ....
```

이럴 때는 메소드에 @RegisterExtension을 붙여서 쓴다.

```
//@ExtendWith(FindSlowTestExtension.class) // 1. 선언적인 방법
public class ExtensionTest {

    @RegisterExtension  // 2. 프로그래밍적인 방법
    static FindSlowTestExtension findSlowTestExtension = new FindSlowTestExtension(500L);

    @Test
    void slowTest() throws InterruptedException {
        Thread.sleep(1000);
        System.out.println("Slow Method for extension test");
    }
}
```

**자동 등록 자바 : ServiceLoader 이용**

서비스로더를 이용해서 확장팩을 자동등록 시키려면, junit-platform.properties의 설정을 true로 해준다.

```
# 확장팩 자동 감지 기능
junit.jupiter.extensions.autodetection.enabled = true
```

[https://junit.org/junit5/docs/current/user-guide/#extensions-registration-automatic](https://junit.org/junit5/docs/current/user-guide/#extensions-registration-automatic)

원하지 않은 extension을 사용하게 되기 때문에, 명시적으로 해주는 쪽이 좋다.

#### **확장팩 만드는 방법**

-   테스트 실행 조건
-   테스트 인스턴스 팩토리
-   테스트 인스턴스 후-처리기
-   테스트 매개변수 리졸버
-   테스트 라이프사이클 콜백
-   예외 처리
-   ...

참고[https://junit.org/junit5/docs/current/user-guide/#extensions](https://junit.org/junit5/docs/current/user-guide/#extensions) 

 [JUnit 5 User Guide

Although the JUnit Jupiter programming model and extension model do not support JUnit 4 features such as Rules and Runners natively, it is not expected that source code maintainers will need to update all of their existing tests, test extensions, and custo

junit.org](https://junit.org/junit5/docs/current/user-guide/#extensions)

## **5\. JUnit4 -> 5마이그레이션  
**

junit-vintage-engine 의존성을 추가하면 JUnit3,4 테스트를 실행할 수 있다.

#### **Vintage engine  
**

2.2버젼 이하 스프링부트 프로젝트를 만들면 vintage-engine이 빠져있으므로 이를 추가해준다.

\- Maven  : exclusion 부분을 제외

```
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
<!--     <exclusions>
        <exclusion>
            <groupId>org.junit.vintage</groupId>
            <artifactId>junit-vintage-engine</artifactId>
        </exclusion>
    </exclusions>
 -->
</dependency>
```

\- Gradle : exclude 제외

```
testImplementation('org.springframework.boot:spring-boot-starter-test') {
    // exclude group: 'org.junit.vintage', module: 'junit-vintage-engine'
}
```

사실 스프링 부트 2.2부터는 JUnit5가 자동 탑재되기 때문에 이 부분을 쓸 필요는 거의 없다.

만약 마이그레이션을 해서 Vintage 엔진을 추가해주면, 태그에 따라 사용하는 라이브러리가 다르다.

\- Junit4 > junit

```
import org.junit.Test;
```

\- Junit5 > junit jupiter api

```
import org.junit.jupiter.api.Test;
```

#### **Rule 지원x**

@Rule을 지원하지 않지만 junit-jupiter-migrationsupport 모듈이 제공하는 @EnableRuleMigrationSupport를 사용할 수 있다. 다만 아래 3개만 지원하므로 완벽하게 지원하지는 않는다.

-   ExternalResource
-   Verifier
-   ExpectedException

왼쪽 태그들이 JUnit5에서는 오른쪽으로 바뀌었다.

~@Category(Class)~  ->  **@Tag(String)**  
~@RunWith, @Rule, @ClassRule~  -> **@ExtendWith, @RegisterExtension**  
~@Ignore~ -> **@Disabled**  
~@Before, @After, @BeforeClass, @AfterClass~ -> **@BeforeEach, @AfterEach, @BeforeAll, @AfterAll**

#### **마이그레이션 순서**

1\. @RunWIth 제거 : @SpringBootTest에 이미 @ExtendWith가 붙어있다.

2\. @Ignore 제거 : 클래스 무시 -> @Disabled는 메소드에도 사용 가능

3\. @Before -> @BeforeEach

4\. public 상관 x (놔둬도 됨

5\. @Test -> Jupiter.api로 변경

6\. Description 용 -> @DisplayName 변경, 추가

<br/>
<br/>
## <2\. 모키토>
<br/>
## **1\. Mockito 소개**

모키토는 Mock을 지원하는 프레임워크로, 진짜 객체처럼 동작하지만 그 동작을 프로그래머가 컨트롤 할 수 있다. 즉, Mock을 쉽게 만들고 관리, 검증하는 프레임워크가 Mockito이다.

\- EasyMock, JMock도 있지만 Mockito가 가장 많이 사용된다.

[https://www.jetbrains.com/lp/devecosystem-2021/java/](https://www.jetbrains.com/lp/devecosystem-2021/java/)


#### **Mockito 활용**

application에서 database, 외부 API, 다른 자바 api를 계속 사용하면, 매번 **외부 시스템**을 접근해서 사용할 수 없기 때문에

이를 Mock으로 만들고

\-> 어떻게 동작할지를 Mockito를 사용해서 코딩하고

\-> 실제 외부 시스템을 이용했을 때 어떻게 작동할지 가정하고

\-> 이를 검증, 체크할 수 있다.

#### **단위테스트 (Unit test)에 대한 고찰 **

Mockito를 쓰려면 단위테스트에 대한 고찰을 할 수 밖에 없는데, 마틴 파울러의 '유닛 테스트'의 정의에 언급 되어 있다.

[https://martinfowler.com/bliki/UnitTest.html](https://martinfowler.com/bliki/UnitTest.html)

 [bliki: UnitTest

Unit Tests are focused on small parts of a code-base, defined in regular programming tools, and fast. There is disagreement on whether units should be solitary or sociable.

martinfowler.com](https://martinfowler.com/bliki/UnitTest.html)

모든 의존성을 끊고 단위테스트를 강하게 해야한다고 하는 사람도 있지만, 이 부분에 집착할 필요는 없다고 한다.

\- 단위를 행동의 단위로 생각하고, 다른 부분도 같이 테스트되어도 된다..

\- 팀 내에서 단위테스트의 단위를 정하면 좋고, 어느 정도까지 Mock을 사용할 지 결정.


## **2\. Mockito 사용 **

\- Mock을 만드는 방법  
\- Mock stubbing : Mock이 어떻게 동작해야 하는지 관리하는 방법  
\- Mock의 행동을 검증하는 방법

으로 진행하면 된다.

Mockito 레퍼런스  
\- [https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)

 [Mockito - mockito-core 5.3.1 javadoc

Latest version of org.mockito:mockito-core https://javadoc.io/doc/org.mockito/mockito-core Current version 5.3.1 https://javadoc.io/doc/org.mockito/mockito-core/5.3.1 package-list path (used for javadoc generation -link option) https://javadoc.io/doc/org.m

javadoc.io](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)

#### **1) Mockito 추가  
**

스프링 부트 2.2버젼 이상은 기본적으로 spring-boot-start-test에 자동 추가되어있다.

스프링 부트 쓰지 않는다면, 의존성 직접 추가한다. mockito-core와 mockito-junit-jupiter 동시에 필요

-   mockito-core : 모키토 기본 기능
-   mockito-junit-jupiter : JUnit test에서 모키토를 사용할 수 있도록 하는 확장 구현체

(현 시점에서 5.3.1까지 나왔으나 스프링부트 3.3에서 4.8.1을 지원해서 해당 버젼으로 적음)

\- Maven

```
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <version>4.8.1</version>
    <scope>test</scope>
</dependency>

<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-junit-jupiter</artifactId>
    <version>4.8.1</version>
    <scope>test</scope>
</dependency>
```

\- Gradle

```
 testImplementation 'org.mockito:mockito-core:4.8.1
 testImplementation 'org.mockito:mockito-junit-jupiter:4.8.1'
```

#### **2) Mock 객체 생성**

우선 Robot과 그 안에 들어가는 Part로 구성된 객체들을 가정해서 생성해본다.

\- Robot 클래스 : Part만 일단 추가. (나중엔 List형태가 되겠지만) 

```
public class Robot {
    private String name = "unused";
    private RobotStatus status = RobotStatus.AWAITING;
    private int timeout;
    private Part part;
```

\- Robot 서비스 : 기등록한 파츠를 로봇에 등록한 후 조립된 새 로봇을 만든다는 가정.

```
public class RobotService {
    private final PartService partService;
    private final RobotRepository repository;

    public RobotService(PartService partService, RobotRepository repository) {
        // assert를 사용해서 null 체크
        assert partService != null;
        assert repository != null;
        this.partService = partService;
        this.repository = repository;
    }

    public Robot createNewRobot(Long partId, Robot robot){
        Optional<Part> part = partService.findById(partId);
        robot.setPart(part.orElseThrow(()-> new IllegalArgumentException("Parts doesn't exist for id :" + partId)));
        return repository.save(robot);
    }
}
```

\- Robot Repository : 기본

```
public interface RobotRepository extends JpaRepository<Robot, Long> {
}
```

\- Part와 PartService

: 아직 어떤 파츠가 들어올지 모르고, 어떤 로직으로 구현해야할지 모르므로 둘다 interface로 둔다.

```
public interface Part {

}
```

```
public interface PartService {
    Optional<Part> findById(Long partId);
}
```

이 때 테스트를 위해서 Part와 PartService를 구현할 수 없으니, Mock을 이용해서 임시 객체를 생성할 수 있다.

 **(1) Mockito.mock()** : 메소드로 만드는 방법

: mock 메소드를 이용해서 구현할 수 있다.

```
public class RobotServiceTest {

    @Test
    void createRobotService(){
        // 1. mock 메소드를 이용해서 구현
        PartService partService = mock(PartService.class);
        RobotRepository robotRepository = mock(RobotRepository.class);

        RobotService robotService = new RobotService(partService, robotRepository);
        assertNotNull(robotService, "Robot Service cannot be null");
    }
}
```

 (**2) @Mock 애노테이션**으로 만드는 방법

\- 클래스에 @ExtendWith(MockitoExtension.class)를 싸주고, @Mock 어노테이션으로 해당 객체들을 표시한다.

```
@ExtendWith(MockitoExtension.class)
public class RobotServiceTest {

    @Mock
    PartService partService;

    @Mock
    RobotRepository robotRepository;

    @Test
    void createRobotService(){
        // 1. mock 메소드를 이용해서 구현
//        RobotRepository robotRepository = mock(RobotRepository.class);
//        PartService partService = mock(PartService.class);

        RobotService robotService = new RobotService(partService, robotRepository);
        assertNotNull(robotService, "Robot Service cannot be null");
    }
}
```

여러 클래스를 사용할 때는 어노테이션 방식이 확실히 편리하다.

**(3) 메소드 파라미터에 어노테이션 사용**

class에 @ExtendWith(MockitoExtension.class)를 붙이고, 파라미터로 @Mock 객체를 받는다.

개인적으로 가장 선호하는 방식이다. 

```
@Test
void createRobotService(@Mock PartService partService, @Mock RobotRepository robotRepository){
    RobotService robotService = new RobotService(partService, robotRepository);
    assertNotNull(robotService, "Robot Service cannot be null");
}
```

#### **3) Mock Stubbing** (동작 관리)

Stubbing이 별 것은 아니고 그저 특정 값을 셋팅해서 결과값을 조절하는 방법이다.

**(1) 행동**

우선 모든 Mock 객체는 기본적으로 아래처럼 행동한다. 

-   Null을 리턴.  
    \- Optional 타입은 Optional.empty 리턴
-   Primitive 타입 : 기본 Primitive 값.  
    \- boolean -> false, inteager -> 0
-   콜렉션 : 빈 콜렉션.
-   Void 메소드 : 예외x, 아무 일도 발생x

```
public interface PartService {
    Optional<Part> findById(Long partId);
    void validate(Long partId);
}
```

void를 반환하는 메소드를 만들고, 테스트에 실행해보면 아무런 일도 일어나지 않는다.

```
    // 2. Mock Stubbing
    @Test
    void createRobotService(@Mock PartService partService, @Mock RobotRepository robotRepository){
        partService.validate(1L); // void를 반환하는 메소드는 아무 일도 일어나지 않음. 예외x
    }
```

**(2) Argument matchers**

\- Mock 개체에 내가 설정한 값에 대해서만 해당 행동을 보이도록 하는 것. 자료값만 맞으면 같은 결과값을 낸다.

-   when().thenReturn  : 값 반환
-    when().thenThrow() : 예외 반환 
-    doThrow().when() : 예외 반환

```
    @Test
    void createRobotService(@Mock PartService partService, @Mock RobotRepository robotRepository){
        RobotService robotService = new RobotService(partService, robotRepository);

        Part part = new Part(1L, "wheel");
        when(partService.findById(1L)).thenReturn(Optional.of(part));

        Robot robot = new Robot(1000, "robot1");
        robotService.createNewRobot(1L, robot);
    }
```

여기서 내가 partId로 1L을 넣든, 2L을 넣는 것은 상관이 없지만, 1L을 넣었을 때만 원하는 값을 반환한다.

\- 예외를 던지게도 할 수 있고, Void 메소드 특정 매개변수를 받거나 호출된 경우에 예외를 줄 수도 있다.

```
when(partService.findById(2L)).thenThrow(new RuntimeException()); // 예외 발생
doThrow(new IllegalArgumentException()).when(partService).validate(3L); // void 값이 호출 되었을 떄, 예외 발생
```

예외가 발생할 경우 assertThrows를 이용해서 확인할 수 있다 .

```
    @Test
    void createRobotService(@Mock PartService partService, @Mock RobotRepository robotRepository){
        // partService.validate(1L); // void를 반환하는 메소드는 아무 일도 일어나지 않음. 예외x

        RobotService robotService = new RobotService(partService, robotRepository);

        Part part = new Part(1L, "wheel");
        when(partService.findById(1L)).thenReturn(Optional.of(part));

        doThrow(new IllegalArgumentException()).when(partService).validate(1L); // void 값이 호출 되었을 떄, 예외 발생

        assertThrows(IllegalArgumentException.class, ()-> {
            partService.validate(1L);
        });
    }
```

\- 동일 매개변수로 여러번 메소드 호출 시, 결과를 다르게 할 수도 있다.

```
@Test
void createRobotService2(@Mock PartService partService){
    Part part = new Part(1L, "wheel");

    // 1번 정상, 2번 예외, 3번 빈값이 출력되도록 함.
    when(partService.findById(any()))
            .thenReturn(Optional.of(part))
            .thenThrow(new RuntimeException())
            .thenReturn(Optional.empty());

    assertEquals("wheel", partService.findById(1L).get().getCode());
    assertThrows(RuntimeException.class, () -> partService.findById(2L));
    assertEquals(Optional.empty(), partService.findById(3L));
}
```

테스트 2, 3번의 2L, 3L은 아무 값이나 넣어도 상관 없다.

아래처럼 when 문을 사용해서 원하는 값이 나오도록 하여 Mock 테스트를 할 수 있다.

```
// 2-3 Mock Stubbing
@Test
void createRobotService3(@Mock PartService partService, @Mock RobotRepository robotRepository){
    RobotService robotService = new RobotService(partService, robotRepository);

    Robot robot  = new Robot (1500, "test robot");
    Part part = new Part(1L, "LiDAR");

    // 해당 stubbing이 되어있어야지만 테스트가 성공함
    when(partService.findById(1L)).thenReturn(Optional.of(part));
    when(robotRepository.save(robot)).thenReturn(robot);

    // 로봇에 파츠 셋팅
    robotService.createNewRobot(1L, robot);

    assertEquals(part, robot.getPart());
}
```

#### **4) Mock 객체 확인 (행동 검증)** \- verifying

Mock 객체가 어떻게 사용이 됐는지 확인할 수 있다.

-   특정 메소드가 특정 매개변수로 몇번 호출 되었는지 (Verifying exact number of invocations)  
    \- 최소 한번은 호출 됐는지  
    \- 전혀 호출되지 않았는지
-   어떤 순서대로 호출했는지 (Verification in order)
-   특정 시간 이내에 호출됐는지 (Verification with timeout)
-   특정 시점 이후에 아무 일도 벌어지지 않았는지 (Finding redundant invocations)  
     

```
public Robot createNewRobot(Long partId, Robot robot){
    Optional<Part> part = partService.findById(partId);
    robot.setPart(part.orElseThrow(()-> new IllegalArgumentException("Parts doesn't exist for id :" + partId)));

    robot = repository.save(robot);
    partService.notify(robot); // verify 테스트용
    partService.notify(part.get()); // verify 테스트용
    partService.findById(partId); // verify 테스트용
    return robot;
}
```

로봇 생성 메소드에 테스트용 3줄을 추가해준다.

verify를 써서 verify를 써서 몇 번이상 호출되었는지, 호출되지 않았는지 확인할 수 있다. 

```
void verifyMock(@Mock PartService partService, @Mock RobotRepository robotRepository){
    RobotService robotService = new RobotService(partService, robotRepository);

    Robot robot  = new Robot (1500, "test robot");
    Part part = new Part(1L, "LiDAR");

    // 해당 stubbing이 되어있어야지만 테스트가 성공함
    when(partService.findById(1L)).thenReturn(Optional.of(part));
    when(robotRepository.save(robot)).thenReturn(robot);

    // 로봇에 파츠 셋팅
    robotService.createNewRobot(1L, robot);

    assertEquals(part, robot.getPart());

    // 3. 객체 검증 : createNewRobot에서 partService가 쓰인 횟수, 순서 등등
    verify(partService, times(1)).notify(robot); // 1번 이상 호출했는지
    verify(partService, times(1)).notify(part); // 한번도 불리지 않은 것
    // verify(partService, times(1)).validate(1L); // 한번도 불리지 않은 것
    verify(partService, never()).validate(any()); // 전혀 호출되지 않아야함
}
```

또 notify가 3번 불렸는데, 1번만 verify한 후 verifyNoMoreInteractions(서비스명)를 써서 이후에 인터랙션이 일어났는지 아닌지도 확인할 수도 있다.

```
verify(partService, times(1)).notify(robot); // 1번 이상 호출했는지
verifyNoMoreInteractions(partService); // 더는 partService가 호출되면 안됨 - 그러나 호출 된 것이 있어서 테스트 실패
```

뒤에 2번 더 인터렉션이 있기 떄문에 테스트가 실패한다.

또한 InOrder를 써서 메소드 호출 순서대로 확인도 가능하다

```
// 객체 검증 순서 확인 - 불린 순서대로 verify (순서 바꾸면 에러)
InOrder inOrder = inOrder(partService);
inOrder.verify(partService).notify(robot);
inOrder.verify(partService).notify(part);
```

만약 순서를 바꾸면 테스트에 실패한다.

특정 시간 내에 수행되었는지도 확인할 수있다

```
verify(partService, timeout(1).times(1)).notify(robot);
```

## **3\. Mock BDD 스타일 API**

BDD는 Behaviorl driven development로, 어플리케이션을 행동 중심으로 구성하는 방법이다. (TDD에서 창안)

\- 행동에 대한 스펙은 아래와 같다, 스펙을 창안하고 진행한다. 

-   Title
-   Narrative(As a  / I want / so that)
    -   As a : 역할
    -   I want : 테스트 내용
-   Acceptance criteria
    -   Given : 주어진 정보, 상황들 
    -   When : 실행 시점
    -   Then : 결과 확인 (verifying)

 -  Mockito는 BddMockito 클래스를 사용한다.

```
@ExtendWith(MockitoExtension.class)
public class BddTest {
    
    // RobotServiceMockTest의 verifyMock과 비교해보면서
    @Test
    void createRobotTest(@Mock PartService partService, @Mock RobotRepository robotRepository){
        // Given
        RobotService robotService = new RobotService(partService, robotRepository);
        Part part = new Part(1L, "wheel");
        Robot robot = new Robot(1000, "robot1");

        // Mockito의 when은 사실상 주어진 정보(Given)이라 표현이 좀 맞지 않는다.
        // 따라서 when을 사용하는 대신 Given을 쓰는 것이 보기 좋다.
        given(partService.findById(1L)).willReturn(Optional.of(part));
        given(robotRepository.save(robot)).willReturn(robot);
//        when(partService.findById(1L)).thenReturn(Optional.of(part));
//        when(robotRepository.save(robot)).thenReturn(robot);

        /* When */
        // 로봇에 파츠 셋팅
        robotService.createNewRobot(1L, robot);
        
        // Verify -> Then
        then(partService).should(times(1)).notify(robot);
        // then(partService).shouldHaveNoMoreInteractions(); // 더이상 인터렉션이 없는지 = verifyNoMoreInteractions(partService);
    }
}
```

이 때 Mockito와 BDD에서 조금 맞지 않는 부분이 있는데

**\- Mockito의 when**

  Mockito의 when은 사실상 주어진 정보이기 때문에, 실행일 이야기 하는 when에는 맞지 않는다.

  따라서 given().willReturn()문으로 바꿔준다.

\- **Mockito의 verify**

  verify의 검증 부분은 then으로 써주면 명확하다.

\- 참고

-   [https://javadoc.io/static/org.mockito/mockito-core/3.2.0/org/mockito/BDDMockito.html](https://javadoc.io/static/org.mockito/mockito-core/3.2.0/org/mockito/BDDMockito.html)
-   [https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html#BDD\_behavior\_verification](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html#BDD_behavior_verification)

<br/>
<br/>

<br/>
# <3. 테스트 컨테이너> 
## **1\. Testcontainers**

테스트에서 도커 컨테이너를 실행할 수 있는 라이브러리로,

\- 테스트 시 DB설정x, 별도 프로그램x, 스크립트x

\- 보다 상용 프로그램에 가까운 테스트를 할 수 있지만, 테스트 속도가 느려진다는 단점이 있다.

[https://www.testcontainers.org/](https://www.testcontainers.org/)

 [Testcontainers for Java

Testcontainers Not using Java? Here are other supported languages! About Testcontainers for Java Testcontainers for Java is a Java library that supports JUnit tests, providing lightweight, throwaway instances of common databases, Selenium web browsers, or

www.testcontainers.org](https://www.testcontainers.org/)

더이상 Mock 객체가 아닌 Repository를 통해서 DB접근을 해야 한다고 치자.

실제는 원 DB에, 테스트는 인메모리 H2를 이용해서 작업을 하는 경우가 많은데, 이럴 경우 문제가 날 수 있다.

\- @Transactional의 isolation, propagation 전략이 디비마다 다르다. (스프링은 DB에 따름)

\- 로컬에서는 동일하더라도, 상용에서 다르게 행동할 수 있다.

따라서 테스트도 실제 같은 환경의 DB에서 하고자 한다면, 도커 컨테이너 유용할 수 있다. 

그때 여러 설정을 하지 않고 Testcontainers를 쓰면 편리하다

#### **1) Testcontainers 설치**

**(1) Testcontainers JUnit 5 지원 모듈 설치**

\- test container가 아닌 junit5지원하는 것으로 설치한다.

[https://mvnrepository.com/artifact/org.testcontainers/junit-jupiter/1.18.1](https://mvnrepository.com/artifact/org.testcontainers/junit-jupiter/1.18.1)

\- Maven

```
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>1.18.1</version>
    <scope>test</scope>
</dependency>
```

\- Gradle

```
testImplementation group: 'org.testcontainers', name: 'junit-jupiter', version: '1.18.1'
```

**(2) DB 모듈 설치**

강의에서는 postgre를 사용했는데, 상용환경에 맞추려는 용도라서 mysql을 사용할 예정이다.

testcontainers 사이트에서 db 모듈을 추가해주어야 한다.

\- Maven

```
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>mysql</artifactId>
    <version>1.18.1</version>
    <scope>test</scope>
</dependency>
```

\- Gradle

```
testImplementation "org.testcontainers:mysql:1.18.1"
```

[https://www.testcontainers.org/modules/databases/mysql/](https://www.testcontainers.org/modules/databases/mysql/)

 [MySQL Module - Testcontainers for Java

MySQL Module See Database containers for documentation and usage that is common to all relational database container types. Overriding MySQL my.cnf settings For MySQL databases, it is possible to override configuration settings using resources on the class

www.testcontainers.org](https://www.testcontainers.org/modules/databases/mysql/)

**(3) Container 추가**

테스트 파일에 전역으로 컨테이너를 설정한다.

```
    static MySQLContainer mySQLContainer  = new MySQLContainer();

    @BeforeEach
    void beforeAll(){
        mySQLContainer.start();
    }

    @AfterEach
    void afterAll(){
        mySQLContainer.stop();
    }
```

static으로 가상 컨테이너를 만들어두면 직접 도커에서 컨테이너를 실행하지 않아도, 

테스트를 실행하면서 알아서 도커를 실행하고 종료한다.

```
# 테스트 컨테이너
spring:
  datasource:
    url: jdbc:mysql://localhost:15432/javatest?useSSL=false&serverTimezone=Asia/Seoul&autoReconnect=true
    driver-class-name: com.mysql.cj.jdbc.Driver
    username: 
    password: 
  jpa:
    hibernate:
      ddl-auto: update #테스트 시에는 create_drop으로 쓴다
    generate-ddl: true
```

연결할 테스트 컨테이너에 대한 내용은 test>resources>application-test.yml에 적어둔다.

그런데 딱히 위에 설정한 정도와 관련되어 컨테이너가 생기는 것은 아니다.

testcontainer와 spring은 관련이 없기 때문이다. 아래를 보면 테스트를 실행했을 때 자기 마음대로 원래 javatest가 아닌 새로운 컨테이너가 떴다가 사라진다. 


따라서 해당 드라이버를 설정할 때, tc라는 키워드로 주고 아래와 같이 application-test.yml을 설정한다.

```
# 테스트 컨테이너
spring:
  datasource:
    url: jdbc:tc:mysql:///javatest
    driver-class-name: org.testcontainers.jdbc.ContainerDatabaseDriver  #tc 사용드라이버를 jdbc로 설정
  jpa:
    hibernate:
      ddl-auto: create-drop #테스트 시에는 create_drop으로 쓴다
    generate-ddl: true
```

그리고 test 파일에서는 .withDatabaseName으로 database 이름을 설정해준다.

```
static MySQLContainer mySQLContainer  = new MySQLContainer().withDatabaseName("javatest");

@BeforeEach
void beforeAll(){
    mySQLContainer.start();
}

@AfterEach
void afterAll(){
    mySQLContainer.stop();
}
```

**4) @Testcontainers 추가**

@BeforeEach, @AfterEach에서 mySqLContainer start, stop으로 라이프사이클을 관리해줬다면,

@TestContainer를 클래스에 붙여주면 컨테이너가 알아서 라이프사이클 관련 메소드를 실행해준다.


@BeforeEach와 @AfterEach는 삭제해도 된다.

**5) @Container**  
\- 전역으로 컨테이너를 정의하면 모든 테스트 마다 컨테이너를 재시작하기 때문에,

스태틱 필드에 사용하고 @Container를 붙여서 클래스 내부 모든 테스트에서 동일한 컨테이너를 재사용하도록 한다.

또 @BeforeEach에서 테스트하려는 DB 내용의 데이터를 매번 지워가면서 하면 데이터 오류를 줄일 수 있다.

```
@Testcontainers // testContainer 테스트 용도 (DB 연동)
class TeamServiceTest {

    @Mock MemberService memberService;

    @Autowired TeamRepository teamRepository;

    //@Container
    static MySQLContainer mySQLContainer  = new MySQLContainer().withDatabaseName("javatest");

    @BeforeEach
    void beforeAll(){
        teamRepository.deleteAll();
    }
```

.

### **2\. Testcontainers 예제**  

db 연결을 테스트하기 위해 가상 컨테이너를 이용하므로, 기존에 Mock으로 두었던 repository를 실제로 하여 테스트 한다.

#### **1) 실제 db 연동**

\- main-java-resources-application.yml에 사용할 db를 적어준다.

\- 도커의 디비를 쓰고, 사양이 같은 동일 가상 도커 컨테이너로 테스트하면 더 좋다.

```
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/javatest?useSSL=false&serverTimezone=Asia/Seoul&autoReconnect=true
    driver-class-name: com.mysql.cj.jdbc.Driver
    username: 
    password: 
  jpa:
    hibernate:
      ddl-auto: update #테스트 시에는 create_drop으로 쓴다
```

#### **2) App main class**

TeamRepository를 Mock이 아닌 실제 구현체로 사용하기 위해서 빈으로 등록해주어야해서 

굳이 필요없어서 삭제했던 main를 생성하고 @SpringBootApplication을 달아준다.

@EnableJapAuditing은 데이터 확인을 위해 사용했다.

```
@EnableJpaAuditing
@SpringBootApplication
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
```

#### **3) 구현체 작성**


기존에 테스트하던 Robot 패키지는 놔두고, 일부만 구현한 team 패키지를 새로 만들었다.

Team 안에 Member가 있는 구조이다.

**(1) Team.class**

```
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name = "unused";
    @CreatedDate
    private LocalDateTime createdTime;

    @ManyToOne(cascade = CascadeType.ALL)
    private Member member;

    public Team(String name){
        this.name = name;
    }
}
```

\- 실제 작동해야하므로 @Entity 클래스로 수정

\- @EntityListeners(AuditingEntityListener.class)를 달아주면, 데이터 입력 시점에 createdTime을 셋팅해준다.

실제 db에 들어간 데이터인지, 아니면 Mock stubbing한 데이터인지 확인하기 위해서 사용했다.

사용을 위해서는 spring main class에 @EnableJapAuditing가 붙어있어야한다.

\- Member는 사실 List가 되어야하는데 어차피 MemberService는 Mock으로 쓴 예정이라서 단일 멤버로 두었다.

  그렇게 할거면 Embedded로 해도 상관없지만 일단은 Member도 Entity로 만들었다.

**(2) TeamService**

```
public class TeamService {
    private final MemberService memberService;
    private final TeamRepository repository;

    public TeamService(MemberService memberService, TeamRepository repository) {
        // assert를 사용해서 null 체크
        assert memberService != null;
        assert repository != null;
        this.memberService = memberService;
        this.repository = repository;
    }

    public Team createNewTeam(Long memberId, Team team){
        Optional<Member> member = memberService.findById(memberId);
        team.setMember(member.orElseThrow(()-> new IllegalArgumentException("Parts doesn't exist for id :" + memberId)));

        team = repository.save(team);
        memberService.notify(team); // verify 테스트용
        memberService.notify(member.get()); // verify 테스트용
        memberService.findById(memberId); // verify 테스트용
        return team;
    }
}
```

TeamService는 기존 RobotService와 유사하다. (Robot -> Team, Part -> Member)

**(3) TeamRepository**

TeamRepository가 이번에는 실제로 기능해야한다.

```
public interface TeamRepository extends JpaRepository<Team, Long> {
}
```

**(4) Member**

Member는 위에서 말한대로 @Embeddable로 해도 되지만 나중에 List로 바꿀 예정이라 @Entity로 해주고, id도 Autoincrement로 주었다.

```
@Getter
@Setter
@NoArgsConstructor
@ToString
@Entity
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String name;

    public Member(Long id, String name){
        this.id = id;
        this.name = name;
    }

    public Member(String name){
        this.name = name;
    }
}
```

**(5) MemberService**

해당 서비스는 Mock으로 쓸 예정이라서 기존 PartService에서 바꾸지 않았다.

```
public interface MemberService {
    Optional<Member> findById(Long partId);
    void validate(Long partId);

    void notify(Team team);

    void notify(Member member);
}
```

#### ****3) application-test.yml  
****

test-resources-application-test.yml은 테스트 용도로 쓰는 설정파일이다.

```
# 테스트 컨테이너
spring:
  datasource:
    url: jdbc:tc:mysql:///javatest
    driver-class-name: org.testcontainers.jdbc.ContainerDatabaseDriver  #tc 사용드라이버를 jdbc로 설정
  jpa:
    hibernate:
      ddl-auto: create-drop #테스트 시에는 create_drop으로 쓴다
    generate-ddl: true
```

가상으로 도커 컨테이너를 만들기 위해 tc 키워드를 사용해서 설정했다. ddl-auto를 'create-drop'으로 했는데, 중간에 drop 불가 에러가 뜰 순 있으나 무시해도 된다.

#### **4) Test 파일**

```
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@SpringBootTest
@Testcontainers // testContainer 테스트 용도 (DB 연동)
class TeamServiceTest {

    @Mock MemberService memberService;

    @Autowired TeamRepository teamRepository;

    @Container
    static MySQLContainer mySQLContainer  = new MySQLContainer().withDatabaseName("javatest");

    @BeforeEach
    void beforeAll(){
        teamRepository.deleteAll();
    }

    @Test
    void createTeamService(){
        // given
        TeamService teamService = new TeamService(memberService, teamRepository);

        Member member = new Member("kim");
        given(memberService.findById(1L)).willReturn(Optional.of(member));
        Team team = new Team("Software");
        System.out.println(team.toString());
        assertNull(team.getCreatedTime());

        // When
        team = teamService.createNewTeam(1L, team);

        // then
        System.out.println(team.toString());
        assertNotNull(team.getCreatedTime());
    }
}
```

\- TeamRepository는 실제 연동된 클래스이므로 @Autowired를 해주고, MemberService는 여전히 Mock객체로 두었다.

\- 테스트가 끝나면 해당 도커 컨테이너는 종료된다. 기존 테스트보다는 시간이 많이 걸린다는 단점이 있지만

테스트 컨테이너를 사용해서 사용과 같은 환경에서 테스트를 해볼 수 있다.


실행해보면 값이 잘 생성된 것을 볼 수 있다.

cf) new MySQLContainer().withDatabaseName()이 depecated 되었다.

```
@Container
static MySQLContainer mySQLContainer  = new MySQLContainer().withDatabaseName("javatest");
```

바뀐 버젼은 database이름이 아니라 image 이름을 넣어야한다.

```
@Container
static MySQLContainer mySQLContainer  = new MySQLContainer("mysql:5.7.38");
```

나는 특정 버젼을 넣어야해서 docker run할 때 넣듯이 버젼 태그를 넣었는데 잘 동작한다.

### **3\. Testcontainers 기능** 

### **1) Testcontainers 기능** 

(1) Testcontainer 만들기

지원하지 않는 DB의 모듈은 이미지 이름만 있으면 컨테이너를 만들 수 있다.

```
static GenericContainer customContainer  = new GenericContainer("mysql:5.7.38");
```

데이터베이스에 특화된 메소드는 사용하지 못하므로 환경변수를 설정해주어야한다.

(어차피 .withDatabaseName 등이 deprecate 되어서 image명을 쓰는 부분이 똑같아졌다)

```
@Container
static GenericContainer customContainer  = new GenericContainer("mysql:5.7.38")
        .withEnv("MYSQL", "javatest");
```

(2) 설정 값

\- 네트워크

-   withExposedPorts(int...) : 도커 컨테이너 노출 포트
-   getMappedPort(int) : 랜덤하게 맵핑해준 호스트 포트 확인

\- 환경 변수 설정

-   withEnv(key, value)

\- 명령어 실행

-   withCommand(String cmd...)

\- 사용 준비 확인

-   waitingFor(Wait) : 가용 확인. 특정 어플리케이션이 실행될 때까지 기다렸다가 테스트 실행
-   Wait.forHttp(String url)
-   Wait.forLogMessage(String message)

\- 로그

-   getLogs()
-   followOutput() : 스트리밍

#### **컨테이너 정보를 스프링 테스트에서 참조  
**

@ContextConfiguration  
\- 스프링이 제공하는 애노테이션으로, 스프링 테스트 컨텍스트가 사용할 설정 파일 또는 컨텍스트를 커스터마이징할 수 있는 방법을 제공한다.  
  
ApplicationContextInitializer  
\- 스프링 ApplicationContext를 프로그래밍으로 초기화 할 때 사용할 수 있는 콜백 인터페이스로, 특정 프로파일을 활성화 하거나, 프로퍼티 소스를 추가하는 등의 작업을 할 수 있다.  
  
TestPropertyValues  
\- 테스트용 프로퍼티 소스를 정의할 때 사용한다.  
  
Environment  
\- 스프링 핵심 API로, 프로퍼티와 프로파일을 담당한다.  
  
전체 흐름  
1. Testcontainer를 사용해서 컨테이너 생성  
2. ApplicationContextInitializer를 구현하여 생선된 컨테이너에서 정보를 축출하여 Environment에 넣어준다.  
3. @ContextConfiguration을 사용해서 ApplicationContextInitializer 구현체를 등록한다.  
4. 테스트 코드에서 Environment, @Value, @ConfigurationProperties 등 다양한 방법으로 해당 프로퍼티를 사용한다.

## **2\. Testcontainers, 도커 Compose 사용**

  
  

#### **테스트에서 (서로 관련있는) 여러 컨테이너를 사용해야 한다면?  
**

Docker Compose: [https://docs.docker.com/compose/](https://docs.docker.com/compose/)  
\- 여러 컨테이너를 한번에 띄우고 서로 간의 의존성 및 네트워크 등을 설정할 수 있는 방법  
\- docker-compose up / down  
  
Testcontainser의 docker compose 모듈을 사용할 수 있다.  
\- [https://www.testcontainers.org/modules/docker\_compose/](https://www.testcontainers.org/modules/docker_compose/)  
  
대체제: [https://github.com/palantir/docker-compose-rule](https://github.com/palantir/docker-compose-rule)  
\- 2019 가을 KSUG 발표 자료 참고  
\- [https://bit.ly/2q8S3Qo](https://bit.ly/2q8S3Qo)

#### **도커 Compose 서비스 정보 참조하기  
**

특정 서비스 Expose  
@Container  
static DockerComposeContainer composeContainer =  
        new DockerComposeContainer(new File("src/test/resources/docker-compose.yml"))  
        .withExposedService("study-db", 5432);  
  
Compose 서비스 정보 참조  
static class ContainerPropertyInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {  
  
    @Override  
    public void initialize(ConfigurableApplicationContext context) {  
        TestPropertyValues.of("container.port=" + composeContainer.getServicePort("study-db", 5432))  
                .applyTo(context.getEnvironment());  
    }  
}


<br/>
# <4. Jmeter> 

## **성능 테스트**

기본적으로는 Apache bench(ab)등으로 API 테스트를 해도 쉽다. 

[https://httpd.apache.org/docs/2.4/programs/ab.html](https://httpd.apache.org/docs/2.4/programs/ab.html)

 [ab - 아파치 웹서버 성능검사 도구 - Apache HTTP Server Version 2.4

ab - 아파치 웹서버 성능검사 도구 이 문서는 최신판 번역이 아닙니다. 최근에 변경된 내용은 영어 문서를 참고하세요. ab는 아파치 하이퍼텍스트 전송 프로토콜 (HTTP) 서버의 성능을 검사하는(benc

httpd.apache.org](https://httpd.apache.org/docs/2.4/programs/ab.html)

java의 경우 Jmeter를 많이 쓰는데, 성능 측정 및 부하 (load) 테스트를 할 수 있는 오픈 소스 자바 애플리케이션이다,

[https://jmeter.apache.org/](https://jmeter.apache.org/)

XML으로 테스트할 수 있고, 자바 개발자에게 가장 잘 알려져있다.

 [Apache JMeter - Apache JMeter™

Apache JMeter™ The Apache JMeter™ application is open source software, a 100% pure Java application designed to load test functional behavior and measure performance. It was originally designed for testing Web Applications but has since expanded to oth

jmeter.apache.org](https://jmeter.apache.org/)

#### **JMeter 기능  
**

\- 다양한 애플리케이션 테스트 지원

-   웹 - HTTP, HTTPS
-   SOAP / REST 웹 서비스
-   FTP
-   데이터베이스 (JDBC 사용)
-   Mail (SMTP, POP3, IMAP)

\- CLI 지원

-   CI 또는 CD 툴과 연동할 때 편리함.
-   UI 사용하는 것보다 메모리 등 시스템 리소스를 적게 사용.

\- 주요 개념

-   Thread Group: 한 쓰레드 당 유저 한명
-   Sampler: 각각의 유저가 해야 하는 액션 (예) Http 요청)
-   Listener: 요청에 따라 응답을 어떻게 처리할 것인가. (예) 리포팅, 검증, 그래프 그리기 등
-   Configuration: Sampler 또는 Listener(애매)가 사용할 설정 값 (HTTP헤더, 쿠키, JDBC 커넥션 등)
-   Assertion: 응답이 성공적인지 확인하는 방법 (응답 코드, 본문 내용 등)

\- 대체제:

-   Gatling : 코드로 테스트 작성 가능
-   nGrinder : [https://naver.github.io/ngrinder/](https://naver.github.io/ngrinder/) 네이버에서 만든 툴

#### **Jmeter 설치**

**1) 다운로드**

[https://jmeter.apache.org/download\_jmeter.cgi](https://jmeter.apache.org/download_jmeter.cgi)에서 Binary의 [apache-jmeter-5.5.zip](https://dlcdn.apache.org//jmeter/binaries/apache-jmeter-5.5.zip)를 다운받고 압축을 푼다.

**2) jmeter 실행**

\- 압축을 푼 폴더의 apache-jmeter-5.5\\bin로 이동

\- 맥, 우분투 : cmd에서 jmeter를 실행

\- 윈도우 : jmeter.bat을 실행

조금 기다리면 이런 GUI 툴이 실행된다.


## **Jmeter사용법**

우선 성능테스트를 할 RestAPI를 만든다.

```
@RestController
@RequiredArgsConstructor
public class TeamController {

    final TeamRepository repository;

    @GetMapping("/team/{id}")
    public Team getTeam(@PathVariable Long id){
        return repository.findById(id).orElseThrow(()->new IllegalArgumentException("Team not found for " + id));
    }

    @PostMapping("/team")
    public Team createTeam(@RequestBody Team team){
        return repository.save(team);
    }
}
```

이 때 성능테스트를 할 서버와 테스트를 돌리는 서버가 서로 달라야한다.  

같은 서버에서 성능 테스트와 api를 둘 다 돌리면 서로 자원을 먹기 때문에 정확한 성능 테스트가 어렵다.

테스트는

1\. 스레드 그룹 만들기 (유저 수 설정)

2\. 샘플러 만들기 (각각 유저가 해야할 일)

3\. 리스너 만들기

4\. 실행

#### **1) Thread Group 만들기**

Test를 우선 저장하고, Add -> Threads(Users) -> Thread Group을 클릭한다.

-   Number of Threads(users): 쓰레드 개수(유저 수)
-   Ramp-up period: 쓰레드 개수를 만드는데 소요할 시간
-   Loop Count:  
    \- infinite : 쓰레드(유저) 개수대로 계속 요청  
    \- 값 입력 시 (해당 쓰레드 개수x루프 개수) 만큼 요청

이 부분은 에러 발생 다음 액션으로, 에러 발생 여부는 Assertion으로 확인할 수 있다

#### **2) Sampler 만들기**

샘플러는 유저의 행동, 유저가 할 일, 즉, 테스트 내용이다  

다시 오른쪽 버튼을 눌러서 Add -> Sampler -> HTTP Request를 선택한다 (Rest Api를 테스트할 예정)
-   요청을 보낼 호스트, 포트, URI, 요청 본문(Body) 적기
-   여러 샘플러를 순서대로 등록도 가능



 위에서 만들었던 Get Request를 호출할 예정.

#### **3) Listener 만들기**

똑같이 테스트명 위에서 오른쪽 버튼을 눌러서 Add-Listner를 추가해주는데  

-   View Results Tree
-   View Resulrts in Table
-   Summary Report
-   Aggregate Report
-   Response Time Graph
-   Graph Results

여러 리스너를 선택할 수 있다.

#### **4) 실행  
**

상단 Play버튼을 누르면 테스트가 실행된다.  
View Result Tree를 보면 해당 테스트에 대한 상세 내용을 보여준다.
View Result in Table로 보면 내용을 요약해서 볼 수 있는데,

첫번째 Test Latency(Time)이 좀 긴 것을 볼 수 있다.

cf) 첫번째 요청은 서블릿을 만드느라 좀 오래걸린다. 이후에는 만들어져있기 때문에 시간이 적게 걸린다.

Summary를 보면 평균적으로 44m/s 걸렸고, 가장 빠른 것은 11, 가장 늦은 것은 308로 나온다.

Error는 없고, Throughput은 1.1/sec인데 보낸 요청이 적기 때문에 초당 1.1로 나오는데

많은 요청을 보내보아야 tpc(transaction per sec)를 알 수 있다. 


Aggregate는 summary와 비슷한데

Median(중앙값)은 데이터 셋의 중간값 (값 개수 짝수면 평균값)을 이야기한다.

90% line은 이 중 90%는 22m/s안에 처리되며

99% line은 이 중 99%는 308m/s안에 처리된다는 내용이다. (가장 첫 오래 걸린 내용이 포함됨)

Response 그래프는 실제 추이를 그래프로 확인할 수 있다.

횟수가 적은 것보다는, Thread group에서 횟수를 infinite로 두고, Graph setting의 Interval을 1000(1초)로 두면


이런 형태로 그래프를 그릴 수 있다. 실시간으로 갱신되지는 않아서 Settings 탭과 Graph를 번갈아서 눌러주면 갱신된 그래프를 확인할 수 있다.

지금은 로컬에서 테스트와 Api를 둘 다 돌리고 있어서 정확한 값은 나오지 않으나 그래도 10~12초 아래로 계속 확인되고 있다

#### **5) Assertion 만들기**

assertion으로 원하는 값이 나오는지를 확인할 수 있다.  

추가는 역시 테스트명 위에서 오른쪽 버튼 -> Add -> Assertion -> 원하는 assrtion 추가

-   응답 코드 확인 : Response Assertion
-   응답 본문 확인 : JSON Assertion

Response Assertion을 선택하면 Field Test에서 원하는 결과값을 선택할 수 있다

사진처럼 Response Code를 선택하고 아래 원하는 값을 입력해준다.

만약 401을 적으면 원하는 값이 나오지 않기 때문에 테스트가 실패하게 된다.

마찬가지로 JSON Assertion으로는 Json 결과값을 확인할 수 있는데

team의 Name값이 Sales로 나오는지 확인하기 위해 테스트를 실행하면

마찬가지로 name이 Software이기 때문에 테스트가 실패한다


#### **6) CLI 사용하기**

GUI 툴에서 사용하면 CI, CD와 연동이 어려우므로 CLI에서 사용하는 방식을 쓴다 .


Thread Group을 infinite로 두고, Aggregate Report만 두고 저장한 상태로 GUI는 종료한다.

cmd에서 저장된 test 파일 폴더로 이동해서 

**jmeter -n -t 설정 파일 -l 리포트 파일**

로 실행한다.

infinite로 실행했기 때문에 계속해서 테스트가 돌아간다


+는 그 기간동안의 내용, =는 총합을 알려준다.

그 기간동안 application의 내용이 확인된다.

응답시간을 일부러 느려지게 조작하거나, 실패하도록 조작하면 CLI에서 확인이 된다.

### **Continuous Test**

BlazeMeter라는 플러그인을 쓰면 계속해서 테스팅을 할 수 있다.

크롬에서 실행한하는 액션을 jmeter 파일로 떨어뜨려서 샘플 데이터로 만든다.

성능테스터의 자동화도 가능하다.

[https://www.blazemeter.com/](https://www.blazemeter.com/)

[https://chrome.google.com/webstore/detail/blazemeter-the-continuous/mbopgmdnpcbohhpnfglgohlbhfongabi](https://chrome.google.com/webstore/detail/blazemeter-the-continuous/mbopgmdnpcbohhpnfglgohlbhfongabi)

 [BlazeMeter | The Continuous Testing Platform

Record Selenium and HTTP traffic to create a load and functional tests in less than 10 minutes (Apache JMeter Compatible).

chrome.google.com](https://chrome.google.com/webstore/detail/blazemeter-the-continuous/mbopgmdnpcbohhpnfglgohlbhfongabi)


<br/>
# <5. 카오스 > 

운영 이슈는 로컬이 아닌 운영 환경에서 간혹 발생하지만 문제 여파가 크거나 복구하기 어려운 문제를 미리 제한된 운영환경에서 확인해보는 툴이다. 이를 Chaos 엔지니어링이라고 한다

## **1\. Chaos engineering  
**

운영 환경의 불확실한 예시로는 네트워크 지연, 서버 장애, 디스크 오작동, 메모리 누수  등이 있다.

이런 일들이 갑자기 쌓이거나 트래픽이 갑자기 몰리면 하위 다운 시스템에 무리를 주기도 한다.

이런 혼란을 막기 위해 해결방안을 모색하는 것을 카오스 엔지니어링이라고 한다.

카오스 엔지니어링의 원칙 [https://channy.creation.net/blog/1173](https://channy.creation.net/blog/1173)

 [카오스 엔지니어링의 원칙 :: Channy's Blog

차니 블로그(Channy Blog)는 웹기술, 오픈소스, 클라우드 컴퓨팅 등 다양한 IT 기술 주제에 대해 다루고 있습니다.

channy.creation.net](https://channy.creation.net/blog/1173)

## **2\. Chaos Monkey  
**

카오스 몽키는 프로덕션 환경, 특히 분산 시스템 환경에서 불확실성을 파악하고 해결 방안을 모색하는데 사용하는 대표적인 툴이다.

넷플릭스에서 만든 툴이다.

[https://netflix.github.io/chaosmonkey/](https://netflix.github.io/chaosmonkey/)

#### **카오스 멍키 스프링 부트**

[https://codecentric.github.io/chaos-monkey-spring-boot/latest/#getting-started](https://codecentric.github.io/chaos-monkey-spring-boot/latest/#getting-started)

카오스 멍키 스프링부트는 스프링 부트 애플리케이션에 카오스 멍키를 손쉽게 적용해 볼 수 있는 툴이다.

즉, 스프링 부트 애플리케이션을 망가트릴 수 있는 툴이다.

#### **주요 개념**

대상(Restcontroller, contoroller, service, repository, component)에 공격(응답지연, 예외발생, 어플리케이션 종료, 메모리누수)을 가해볼 수 있다. 이러한 상황을 재현을 해 본 후 대안을 찾는 방식이다.

예시로 컨트롤러 응답이 느려지면 다른 서비스나 respository쓴다든가 하는 방식으로 어플리케이션을 탄탄하게 만들 수 있다. 소스코드를 건드리지 않고 재현해볼 수 있어서 좋다.

**공격 대상 (Watcher)**

-   @RestController
-   @Controller
-   @Service
-   @Repository
-   @Component

**공격 유형 (Assaults)**

-   응답 지연 (Latency Assault)
-   예외 발생 (Exception Assault)
-   애플리케이션 종료 (AppKiller Assault)
-   메모리 누수 (Memory Assault)

## **3\. Chaos Monkey for Spring boot 설치**

#### **1) 의존성 추가  
**

(1)  Chaos-monkey-spring-boot : 스프링 부트용 카오스 멍키 제공  
(2) Spring-boot-starter-actuator

   - 스프링 부트 운영 툴로, 다양한 모듈들을 지원한다. 런타임 중에 카오스 멍키 설정을 변경할 수 있다.

  - 그밖에도 헬스 체크, 로그 레벨 변경, 매트릭스 데이터 조회 등 다양한 운영 툴로 사용 가능.

**\- Maven**

```
<dependency>
    <groupId>de.codecentric</groupId>
    <artifactId>chaos-monkey-spring-boot</artifactId>
    <version>2.1.1</version>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

**\- Gradle**

```
implementation group: 'de.codecentric', name: 'chaos-monkey-spring-boot', version: '3.0.1'
implementation group: 'org.springframework.boot', name: 'spring-boot-starter-actuator', version: '3.1.0'
```

#### **2) 활성화**

카오스 몽키를 활성화해야 실행이 된다

**(1) 카오스 몽키 활성화**

application.properties 파일에 아래 내용을 적어준다.

```
spring.profiles.active=chaos-monkey
```

또는 intellij나 개발툴의 실행 프로필(active profiels)에서 입력해준다.

**(2) 스프링 부트 Actuator 엔드 포인트 활성화**

역시 application properties에서 아래 내용을 추가한다.  
management.endpoint.chaosmonkey.enabled=true  
management.endpoints.web.exposure.include=health,info,chaosmonkey

```
management:
  endpoint:
    chaosmonkey:
      enabled: true
  endpoints:
    web:
      exposure:
        include: health,info,chaosmonkey
```

어플리케이션을 실행하면 카오스 몽키 로고가 나타난다


## **3\. 예시1 - 응답 지연**

우선 cmd에서 jmeter로 테스트를 실행시켜둔다.


#### **1)  Repository Watcher 활성화  
**

application.properties에 repository에 대한 watcher를 활성화해둔다

```
chaos:
  monkey:
    watcher:
      repository: true
```

```
chaos.monkey.watcher.repository=true
```

@repository 어노테이션이 붙은 빈들에 카오스 몽키를 적용한다.

JpaRepository 기본 구현체에 repositoy라는 어노테이션이 붙어있기 때문에, 실제로 적용이 된다.

####   
**2) 카오스 멍키 활성화**

postman등을 이용해서 해당 url을 실행한다

**POST**

```
localhost:8080/actuator/chaosmonkey/enable
```

{

    "enabledAt": "2023-05-25T16:43:32.741+09:00",

    "enabledFor": {

        "raw": "PT7M52.744S",

        "formatted": "7 minutes 52 seconds"

    },

    "enabled": true

}

#### **3) 카오스 멍키 활성화 확인  
**

**GET**

```
localhost:8080/actuator/chaosmonkey/status
```

{

    "enabledAt": "2023-05-25T16:43:32.741+09:00",

    "enabledFor": {

        "raw": "PT5M7.196S",

        "formatted": "5 minutes 07 seconds"

    },

    "enabled": true

}

####   
**4) 카오스 멍키 와처 확인**

**GET**

```
localhost:8080/actuator/chaosmonkey/watchers
```

{

    "controller": false,

    "restController": false,

    "service": false,

    "repository": true,

    "component": false,

    "restTemplate": false,

    "webClient": false,

    "actuatorHealth": false,

    "beans": \[\],

    "excludeClasses": \[\]

}

원래는 service도 기본 true로 되어있다는데 안되어있으므로, 필요하다면 application.properties에서 repository처럼 명시적으로 true로 변경해준다.

활성화는 runtime 중에 적용되지 않으나, 끄는 것은 가능하다. POST로 body를 넣어서 변경해준다.

{ 

    "service": false 

}  

```
localhost:8080/actuator/chaosmonkey/watchers
```

그래도 가능하면 watcher에 대한 설정은 properties에서 해준다.

####   
**5) 카오스 멍키 지연 공격 설정**

**http POST**

```
localhost:8080/actuator/chaosmonkey/assaults
```

{      

    "level": 2,      

    "latencyRangeStart": 2000,      

    "latencyRangeEnd": 5000,      

    "latencyActive": true    

}

level은 2번 요청할 때 마다 1번씩 공격,

LatencyRangeStart - LatendyRageEnd는 2초에서 5초 사이에 공격을 하라는 뜻이다

####   
**6) 테스트**

JMeter 확인해보면 시간 지연이 일어나는 것을 볼 수 있다.

\- 기존 테스트 결과


\- Choas monkey 공격 후 시간

Min 시간은 어차피 최소이기 떄문에 큰 차이가 나지 않고, max에서 차이가 난다

실행을 계속 하면 평균값이 올라가는 걸 확인할 수 있을 것이다.

이러한 응답 지연은 A서버에서 B1, B2서버를 호출 할 때, 응답이 빠른 서버 위주로 호출해야한다고 하면

B1 서버에 카오스 몽키로 지연을 일으켜서, B2서버를 호출하는지 확인하면 된다.

#### **cf) watchedCustomServices** 특정 메소드 지정

위에서는 Repository에 모두 적용하게 했는데, 특정 메소드에만 적용하게 하는 것도 가능하다.

  "watchedCustomServices"에 해당 메소드를 적어주면 된다.

```
{
  "level": 5,
  "latencyRangeStart": 2000,
  "latencyRangeEnd": 5000,
  "latencyActive": true,
  "exceptionsActive": true,
  "killApplicationActive": false,
  "watchedCustomServices": [
    "com.example.chaos.monkey.chaosdemo.controller.HelloController.sayHello",
    "com.example.chaos.monkey.chaosdemo.controller.HelloController.sayGoodbye"
  ]
}
```

또는 런타임 뿐만 아니라 config로 지정해줄 수도 있다.

```
chaos:
  monkey:
    watcher:
      repository: true
    assaults:
      watched-custom-services: "com.example.chaos.monkey.chaosdemo.controller.HelloController.sayHello"
```

## **4\. 예시2 - 에러 발생**

해당 url의 repository에서 에러가 발생하는 경우를 테스트한다고 가정한다.

```
@GetMapping("/team/{id}")
public Team getTeam(@PathVariable Long id){
    return repository.findById(id).orElseThrow(()->new IllegalArgumentException("Team not found for " + id));
}
```

이 경우도 A서버의 B1서비스를 쓸 떄 에러가 난다면, B2 서비스를 쓰는 식으로 대체할 수 있다.

이를 위해 B1 서비스에 에러를 내서 테스트를 해본다.

#### **에러 설정  
**

```
localhost:8080/actuator/chaosmonkey/assaults
```

{      

    "level": 2,      

    "exceptionsActive": true,      

    "exception.type": java.lang.RuntimeException,      

    "latencyActive": false

}

다시 설정을 해주면 50%의 확률로 에러가 나는 것을 볼 수 있다.


<br/>
# <6. 6. ArchUnit 아키텍처 테스트> 

아키텍처는 어플리케이션의 패키지 구조, 클래스 관계, 메소드 참조 등의 여러 형태를 가질 수 있다.

아래 글에 아키텍처를 소개해야하는 이유와 ArchUnit에 대한 내용이 실려있다.

[https://blogs.oracle.com/javamagazine/unit-test-your-architecture-with-archunit](https://blogs.oracle.com/javamagazine/unit-test-your-architecture-with-archunit)

 [Unit Test Your Architecture with ArchUnit

Discover architectural defects at build time.

blogs.oracle.com](https://blogs.oracle.com/javamagazine/unit-test-your-architecture-with-archunit)

## **1\. ArchUnit  
**

ArchUnit은 애플리케이션의 아키텍처를 테스트 할 수 있는 오픈 소스 라이브러리이다. 패키지, 클래스, 레이어, 슬라이스 간의 의존성을 확인할 수 있다.

**\- ArchUnit 가이드**

[https://www.archunit.org/userguide/html/000\_Index.html](https://www.archunit.org/userguide/html/000_Index.html)

 [ArchUnit User Guide

ArchUnit is a free, simple and extensible library for checking the architecture of your Java code. That is, ArchUnit can check dependencies between packages and classes, layers and slices, check for cyclic dependencies and more. It does so by analyzing giv

www.archunit.org](https://www.archunit.org/userguide/html/000_Index.html)

#### **아키텍처 테스트 유즈 케이스**

-   특정 패키지 사용 확인 : A 패키지가 B (또는 C, D) 패키지에서만 사용 되고 있는지 확인 가능.
    -   상호 참조가 생기는지 확인 가능 -> 상호참조 발생 시 코드 파악이 어렵기 떄문에 최대한 피해야한다
-   \*Serivce라는 이름의 클래스들이 \*Controller 또는 \*Service라는 이름의 클래스에서만 참조하고 있는지 확인.
-   \*Service라는 이름의 클래스들이 ..service.. 라는 패키지에 들어있는지 확인.
    -   Repository나 다른 패키지에 들어있지 않은지. 맞는 패키지에 들어가 있는지
-   A라는 애노테이션을 선언한 메소드만 특정 패키지 또는 특정 애노테이션을 가진 클래스를 호출하고 있는지 확인.
-   특정한 스타일의 아키텍처를 따르고 있는지 확인.
    -   계층형, Onion 아키텍처 등

#### **ArchUnit 설치**

JUnit 5용 ArchUnit을 설치한다

**\- Maven**

```
<dependency>
    <groupId>com.tngtech.archunit</groupId>
    <artifactId>archunit-junit5-engine</artifactId>
    <version>1.0.1</version>
    <scope>test</scope>
</dependency>
```

\- Gradle

```
testImplementation group: 'com.tngtech.archunit', name: 'archunit-junit5-engine', version: '1.0.1'
```

#### **ArchUnit 사용법**

**(1) 테스트 메서드 작성**

1\. 자바 클래스 읽기 : 특정 패키지에 해당하는 클래스를 (바이트코드를 통해) 읽어들이고  
2\. 규칙(Rule) 정의 : 확인할 규칙을 정의  
3\. 확인 : 읽어들인 클래스들이 그 규칙을 잘 따르는지 확인한다.

```
@Test
public void Services_should_only_be_accessed_by_Controllers() {
	// 1. 클래스 읽기 
    JavaClasses importedClasses = new ClassFileImporter().importPackages("com.mycompany.myapp");

	// 2. 룰 정의 
    ArchRule myRule = classes()
        .that().resideInAPackage("..service..")
        .should().onlyBeAccessed().byAnyPackage("..controller..", "..service..");

	// 3. 확인
    myRule.check(importedClasses);
}
```

**(2) JUnit 5 확장팩 사용  
**

어노테이션으로 사용도 가능하다.

\- @AnalyzeClasses: 클래스를 읽어들여서 확인할 패키지 설정

\- @ArchTest: 확인할 규칙 정의

```
@RunWith(ArchUnitRunner.class)
@AnalyzeClasses(
    packages = "com.company.app", 
    importOptions = 
        ImportOption.DoNotIncludeTests.class
)
public class ArchitectureRulesTest {

    @ArchTest
    public static final ArchRule ruleAsStaticField = 
              ArchRuleDefinition.classes()
                                .should()...

    @ArchTest
    public static void ruleAsStaticMethod(JavaClasses classes) {
        ArchRuleDefinition.classes()
                           .should()...
    }
}
```

## **2\. 예시 1 - 패키지 의존성 확인  
**

#### **테스트용 패키지 수정**

테스트를 위해서 패키지를 일부 정리했다.

Team 패키지가 Membe 패키지를 참조하고, Team과 Member가 Domain 패키지를 참조하는 구조를 테스트한다. 

(Robot, Part는 우선 제외)

-   domain 패키지에 있는 클래스는 team, member 클래스에서 참조 가능
-   member 패키지에 있는 클래스는 team, member 클래스에서만 참조 가능
-   domain 패키지는 team, member 패키지를 참조하지 못함
-   team 패키지에 있는 클래스는 team에서만 참조 가능
-   순환 참조 없어야 한다.

#### **테스트 소스  
**

위의 테스트 내용을 소스로 작성하면 이렇다.

```
public class TeamArchUnitTest {

    // ..xx.. 패키지
    @Test
    public void packageDependencyTests(){
        // 1. 클래스 읽기
        JavaClasses classes = new ClassFileImporter().importPackages("com.mc.testexample");

        // 2. 룰 정의
        ArchRule myRule = classes().that().resideInAPackage("..domain..")
                .should().onlyBeAccessed().byAnyPackage("..team..", "..member..", "..domain..");

        // 3. 확인
        myRule.check(classes);
    }
}
```


테스트를 해보면, domain의 Robot, Part가 다른 패키지에서도 참조중이기 때문에 에러가 뜨는 걸 볼 수 있다.

```
.byAnyPackage("..team..", "..member..", "..robot..", "..part..", "..domain..");
```

로봇과 part 패키지도 넣어주면 테스트가 성공한다. 


**\- 순환참조 확인**

slice().matching을 써서 각 calss들을 개별적인 패키지로 인식시키고, 그 값들이 순환참조 하는 지를 확인한다.

```
@Test
public void packageDependencyTests(){
    // 1. 클래스 읽기
    JavaClasses classes = new ClassFileImporter().importPackages("com.mc.testexample");

    // 순환참조 확인 - slice를 해서 개별적인 패키지로 인식을 시키고, 그 값들이 각각 순환참조를 하는지 확인
    ArchRule freeOfCycles = slices().matching("..testexample.(*)..")
            .should().beFreeOfCycles();
    freeOfCycles.check(classes);
}
```

순환참조를 일으키기 위해서 MemberService를 구현해서 임시로 teamService를 참조해준다.

```
public class DefaultMemberService implements MemberService{

    TeamService teamService;

    @Override
    public Optional<Member> findById(Long partId) {
        return Optional.empty();
    }

    @Override
    public void validate(Long partId) {
        teamService.createNewTeam(partId, null);
    }

    @Override
    public void notify(Team team) {

    }

    @Override
    public void notify(Member member) {

    }
}
```

결과는 아래와 같다


#### **ArchUnit & JUnit 5 연동하기**

**@AnalyzeClasses**

: 클래스를 읽어들여서 확인할 패키지 설정

```
@AnalyzeClasses(
        packages = "com.mc.testexample",
        importOptions = ImportOption.DoNotIncludeTests.class
)
```

패키지를 지정해줘도 되고, 아래처럼 application main 클래스가 있는 패키지를 전체 지정해줘도 된다.

```
@AnalyzeClasses(packagesOf = App.class)
```

  
**@ArchTest :** 확인할 규칙 정의

확실히 메소드를 만드는 것보다는 줄어들었으나, display name을 적어줄 수가 없다.

```
@AnalyzeClasses(packagesOf = App.class)
public class TeamArchUnitJUnitTest {

    @ArchTest
    ArchRule domainRule = classes().that().resideInAPackage("..domain..")
            .should().onlyBeAccessed().byAnyPackage("..team..", "..member..", "..robot..", "..part..", "..domain..");


    @ArchTest
    ArchRule memberPackageRule = classes().that().resideInAPackage("..domain..")
            .should().accessClassesThat().resideInAPackage("..member..");

    @ArchTest
    ArchRule teamPackageRule = classes().that().resideOutsideOfPackage("..team..")
            .should().accessClassesThat().resideInAnyPackage("..team..");

    @ArchTest
    ArchRule freeOfCycles = slices().matching("..testexample.(*)..")
            .should().beFreeOfCycles();
}
```

ArchUnit은 Junit 엔진을 확장해서 Junit 모듈을 만든 거라서 Jupiter로 실행하는 것이 아니라 Arch Unit 엔진을 실행한다.

## **3\. 예시 2 - 클래스 의존성 확인**

TeamController는 TeamService, TeamRepository를 참조하고, TeaService는 TeamRepository를 참조한다. 

-   TeamController는 TeamService와 TeamRepository를 사용할 수 있다.
-   Team\* 으로 시작하는 클래스는 ..team.. 패키지에 있어야 한다.
    -   Team domain 클래스는 ..domain.. 패키지에 있다.
-   TeamRepository는 TeamService와 TeamController를 사용할 수 없다.

#### **테스트 소스**

```
@AnalyzeClasses(packagesOf = App.class)
public class TeamArchUnitClassTest {

    // 컨트롤러는 서비스와 Repository를 참조할 수 있음.
    @ArchTest
    ArchRule controllerClassRule = classes().that().haveSimpleNameEndingWith("Controller")
            .should().accessClassesThat().haveSimpleNameEndingWith("Service")
            .orShould().accessClassesThat().haveSimpleNameEndingWith("Repository");

    // Repository는 서비스를 참조할 수 없음.
    @ArchTest
    ArchRule repositoryClassRule = classes().that().haveSimpleNameEndingWith("Repository")
            .should().accessClassesThat().haveSimpleNameEndingWith("Service");

    @ArchTest
    ArchRule teamClassesRule = classes().that().haveSimpleNameStartingWith("Team")
            .and().areNotEnums()
            .and().areNotAnnotatedWith(Entity.class)
            .should().resideInAnyPackage("..team..");
}
```

#### **추가적인 검증**

**Freezing Arch Rules**

만약 어플리케이션 개발이 많이 진행되었는데, 아키텍처 룰이 많이 깨진 상태라면,

추가적으로 깨지는 것만 점차적으로 맞춰 나갈 때 쓴다.

**Architecture Rule**

OnionArchitecture, LayeredArchitecture 등 원하는 아키텍처 구조를 따르고 있는지를 확인할 수도 있다.
