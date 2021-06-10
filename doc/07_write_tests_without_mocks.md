# 테스트 케이스 작성

## Mock을 사용하지 않고 테스트 케이스 작성

### Step6. "예약완료 시 SmsSender는 무조건 발송" 테스트 작성

* 실제 업무 클래스 실행하여 SMS가 발송되면 안됨

* 이를 방지하기 위해 SmsSender클래스를 상속받는 TestableSmsSender클래스 작성

```java
import com.santa.cleancode.restaurant.Schedule;
import com.santa.cleancode.restaurant.SmsSender;

public class TestableSmsSender extends SmsSender {
    private boolean sendMethodCalled;

    @Override
    public void send(Schedule schedule) {
        sendMethodCalled = true;
    }

    public boolean isSendMethodCalled() {
        return sendMethodCalled;
    }
}
```

* Testable클래스의 sendMethodCalled를 사용하여 메소드 호출 확인

```java
public class BookingSchedulerTest {
    @Test
    public void 예약완료시_SMS는_무조건_발송() {
        //given
        Schedule schedule = new Schedule(ON_THE_HOUR, NUMBER_OF_PEOPLE_FOR_TABLE, CUSTOMER);
        TestableSmsSender testableSmsSender = new TestableSmsSender();
        bookingScheduler.setSmsSender(testableSmsSender);

        //when
        bookingScheduler.addSchedule(schedule);

        //then
        assertThat(testableSmsSender.isSendMethodCalled(), is(true));
    }
}
```

### Step7. @Before를 활용해서 TestableSmsSender 활용부분 리팩토링

* @Before 어노테이션을 활용한 setUp 메서드에서 testableSmsSender 세팅

```java
public class BookingSchedulerTest {

    public static final DateTime NOT_ON_THE_HOUR = new DateTime(2021, 6, 14, 13, 30);
    public static final DateTime ON_THE_HOUR = new DateTime(2021, 6, 14, 14, 0);
    public static final Customer CUSTOMER = new Customer("user-name", "010-1234-5678");
    public static final int NUMBER_OF_PEOPLE_FOR_TABLE = 3;
    public static final int CAPACITY_PER_HOUR = 5;
    private BookingScheduler bookingScheduler;
    private TestableSmsSender testableSmsSender;

    @Before
    public void setUp() throws Exception {
        bookingScheduler = new BookingScheduler(CAPACITY_PER_HOUR);
        testableSmsSender = new TestableSmsSender();
        bookingScheduler.setSmsSender(testableSmsSender);
    }
}
```

### Step8. "email 없는 경우에는 이메일 미발송" 테스트 작성

* Email이 없는 경우 알림 메일을 보내지 않는 테스트

    * MailSender클래스를 상속받는 TestableMailSender클래스 작성

```java
import com.santa.cleancode.restaurant.MailSender;
import com.santa.cleancode.restaurant.Schedule;

public class TestableMailSender extends MailSender {
    private int sendMethodCallCount;

    @Override
    public void sendMail(Schedule schedule) {
        sendMethodCallCount++;
    }

    public int getSendMethodCallCount() {
        return sendMethodCallCount;
    }
}
```

* int sendMethodCallCount 사용하여 메소드 호출 횟수 확인

```java
public class BookingSchedulerTest {

    public static final DateTime NOT_ON_THE_HOUR = new DateTime(2021, 6, 14, 13, 30);
    public static final DateTime ON_THE_HOUR = new DateTime(2021, 6, 14, 14, 0);
    public static final Customer CUSTOMER = new Customer("user-name", "010-1234-5678");
    public static final int NUMBER_OF_PEOPLE_FOR_TABLE = 3;
    public static final int CAPACITY_PER_HOUR = 5;
    private BookingScheduler bookingScheduler;
    private TestableSmsSender testableSmsSender;
    private TestableMailSender testableMailSender;

    @Before
    public void setUp() throws Exception {
        bookingScheduler = new BookingScheduler(CAPACITY_PER_HOUR);
        testableSmsSender = new TestableSmsSender();
        testableMailSender = new TestableMailSender();
        bookingScheduler.setSmsSender(testableSmsSender);
        bookingScheduler.setMailSender(testableMailSender);
    }

    @Test
    public void 이메일이_없는_경우에는_이메일_미발송() {
        //given
        Schedule schedule = new Schedule(ON_THE_HOUR, NUMBER_OF_PEOPLE_FOR_TABLE, CUSTOMER);

        //when
        bookingScheduler.addSchedule(schedule);

        //then
        assertThat(testableMailSender.getSendMethodCallCount(), is(0));
    }
}
```

### Step9. "email이 있는 경우에 이메일 발송" 테스트 작성

* Email이 있는 경우 알림 메일을 보내는 테스트

```java
public class BookingSchedulerTest {
    @Test
    public void 이메일이_있는_경우에는_이메일_발송() {
        //given
        Customer customerWithMail = new Customer("user-name", "010-1234-5678", "email@google.com");
        Schedule schedule = new Schedule(ON_THE_HOUR, NUMBER_OF_PEOPLE_FOR_TABLE, customerWithMail);

        //when
        bookingScheduler.addSchedule(schedule);

        //then
        assertThat(testableMailSender.getSendMethodCallCount(), is(1));
    }
}

```

### TestableBookingScheduler 클래스 만들기

* BookingScheduler의 "일요일에는 시스템을 오픈하지 않는다." 는 요구사항 적용

* getNow() 메서드 생성

    * new DateTime() 을 getNow()로 Extract Method (Ctrl + Alt + M) 수행

```java
public class BookingScheduler {

    public void addSchedule(Schedule schedule) {
        // 일요일에는 시스템을 오픈하지 않는다.
        DateTime now = new DateTime();
        if (now.getDayOfWeek() == DateTimeConstants.SUNDAY) {
            throw new RuntimeException("Booking system is not available on sunday");
        }
    }

}
```

```java
public class BookingScheduler {
    public void addSchedule(Schedule schedule) {
        // 일요일에는 시스템을 오픈하지 않는다.
        DateTime now = getNow();
        if (now.getDayOfWeek() == DateTimeConstants.SUNDAY) {
            throw new RuntimeException("Booking system is not available on sunday");
        }
    }

    protected DateTime getNow() {
        return new DateTime();
    }

}
```

* BookingScheduler을 상속받는 TestableBookingScheduler 클래스 만들기

    * getNow() 메서드 오버라이딩을 통해 설정한 dateTime 리턴하도록 적용

```java
import org.joda.time.DateTime;

public class TestableBookingScheduler extends BookingScheduler {

    private DateTime dateTime;

    public TestableBookingScheduler(int capacityPerHour, DateTime dateTime) {
        super(capacityPerHour);
        this.dateTime = dateTime;
    }

    @Override
    protected DateTime getNow() {
        return dateTime;
    }
}
```

* BookingSchedulerTest 클래스가 TestableBookingSchduler 클래스를 사용하도록 변경

```java
public class BookingSchedulerTest {

    public static final DateTime NOT_ON_THE_HOUR = new DateTime(2021, 6, 14, 13, 30);
    public static final DateTime ON_THE_HOUR = new DateTime(2021, 6, 14, 14, 0);

    public static final DateTime NOT_SUNDAY = new DateTime(2021, 6, 14, 14, 0);

    public static final Customer CUSTOMER = new Customer("user-name", "010-1234-5678");
    public static final int NUMBER_OF_PEOPLE_FOR_TABLE = 3;
    public static final int CAPACITY_PER_HOUR = 5;
    private TestableBookingScheduler bookingScheduler;
    private TestableSmsSender testableSmsSender;

    private TestableMailSender testableMailSender;

    @Before
    public void setUp() throws Exception {
        bookingScheduler = new TestableBookingScheduler(CAPACITY_PER_HOUR, NOT_SUNDAY);

        testableSmsSender = new TestableSmsSender();
        testableMailSender = new TestableMailSender();
        bookingScheduler.setSmsSender(testableSmsSender);
        bookingScheduler.setMailSender(testableMailSender);
    }
}
```

```java
public class BookingSchedulerTest {
    @Test(expected = RuntimeException.class)
    public void 현재날짜가_일요일인_경우_예약불가_예외처리() {
        //given
        Schedule schedule = new Schedule(ON_THE_HOUR, NUMBER_OF_PEOPLE_FOR_TABLE, CUSTOMER);
        DateTime sunday = new DateTime(2021, 6, 13, 12, 0);
        TestableBookingScheduler testableBookingScheduler
                = new TestableBookingScheduler(CAPACITY_PER_HOUR, sunday);
        testableBookingScheduler.setSmsSender(testableSmsSender);
        testableBookingScheduler.setMailSender(testableMailSender);

        //when
        testableBookingScheduler.addSchedule(schedule);

        //then
    }

    @Test
    public void 현재날짜가_일요일이_아닌경우_예약가능() {
        //given
        Schedule schedule = new Schedule(ON_THE_HOUR, NUMBER_OF_PEOPLE_FOR_TABLE, CUSTOMER);
        TestableBookingScheduler testableBookingScheduler
                = new TestableBookingScheduler(CAPACITY_PER_HOUR, NOT_SUNDAY);
        testableBookingScheduler.setSmsSender(testableSmsSender);
        testableBookingScheduler.setMailSender(testableMailSender);

        //when
        bookingScheduler.addSchedule(schedule);

        //then
        assertThat(bookingScheduler.hasSchedule(schedule), is(true));
    }
}
```

[이전](06_test_double.md) [다음](08_mockito.md)