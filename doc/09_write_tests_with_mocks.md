# Mock 적용

## Mock을 사용하여 테스트 코드 리팩토링

### Step1. mock기능을 사용하여 Customer dummy 객체 생성

* BookingSchedulerWithMockTest 에서 Mockito를 사용한 테스트 작성

    * BookingSchedulerTest 에서 선언된 내용을 복사하여 사용

* CUSTOMER는 필드사용이 필요 없으므로 단순 mock을 통해 null 반환

```java
public class BookingSchedulerWithMockTest {
    public static final DateTime NOT_ON_THE_HOUR = new DateTime(2021, 6, 14, 13, 30);
    public static final DateTime ON_THE_HOUR = new DateTime(2021, 6, 14, 14, 0);
    public static final DateTime NOT_SUNDAY = new DateTime(2021, 6, 14, 14, 0);
    public static final Customer CUSTOMER = new Customer("user-name", "010-1234-5678");
    public static final int NUMBER_OF_PEOPLE_FOR_TABLE = 3;
    public static final int CAPACITY_PER_HOUR = 5;
}
```

* 테스트 클래스에 Mockito 적용

    * @RunWith 어노테이션 적용하여 테스트케이스가 MockitoJUnitRunner를 사용하는것을 정의

```java

@RunWith(MockitoJUnitRunner.class)
public class BookingSchedulerWithMockTest {

}
```

### Step2. setSmsSender를 없애기 위한 InjectMocks

* TestableSmsSender를 없애기 위해 @Mock 활용하여 SmsSender를 mocking

```java

@RunWith(MockitoJUnitRunner.class)
public class BookingSchedulerWithMockTest {

    @InjectMocks
    BookingScheduler bookingScheduler = new BookingScheduler(CAPACITY_PER_HOUR);

    @Mock
    SmsSender smsSender;
}
```

* verify를 이용하여 메소드 호출 테스트

```java
public class BookingSchedulerWithMockTest {
    @Test
    public void 예약완료시_SMS는_무조건_발송() {
        //given
        Schedule schedule = new Schedule((ON_THE_HOUR, NUMBER_OF_PEOPLE_FOR_TABLE, CUSTOMER));

        //when
        bookingScheduler.addSchedule(schedule);

        //then
        verify(smsSender, times(1)).send(schedule);
    }
}
```

### Step3. setMailSender를 없애기 위한 InjectMocks

* TestableMailSender를 없애기 위해 @Mock 활용하여 MailSender를 mocking

* CUSTOMER 는 Email이 있는 경우와 없는 경우를 테스트 해야하므로 @Mock을 활용하여 mocking

* Setup에서 Stub을 사용하여 CUSTOMER의 email을 반환하도록 설정

```java

@RunWith(MockitoJUnitRunner.class)
public class BookingSchedulerWithMockTest {

    @InjectMocks
    BookingScheduler bookingScheduler = new BookingScheduler(CAPACITY_PER_HOUR);

    @Mock
    MailSender mailSender;

    @Mock
    Customer CUSTOMER;

    @Before
    public void setUp() throws Exception {
        when(CUSTOMER.getEmail()).thenReturn("");
    }

}
```

* verify를 이용하여 메소드 호출 테스트

```java

@RunWith(MockitoJUnitRunner.class)
public class BookingSchedulerWithMockTest {
    @Test
    public void 이메일이_없는_경우에는_이메일_미발송() {
        //given
        when(CUSTOMER.getEmail()).thenReturn(null);
        Schedule schedule = new Schedule(ON_THE_HOUR, NUMBER_OF_PEOPLE_FOR_TABLE, CUSTOMER);

        //when
        bookingScheduler.addSchedule(schedule);

        //then
        verify(mailSender, never()).sendMail(schedule);
    }

    @Test
    public void 이메일이_있는_경우에는_이메일_발송() {
        //given
        when(CUSTOMER.getEmail()).thenReturn("email@google.com");
        Schedule schedule = new Schedule(ON_THE_HOUR, NUMBER_OF_PEOPLE_FOR_TABLE, CUSTOMER);

        //when
        bookingScheduler.addSchedule(schedule);

        //then
        verify(mailSender, times(1)).sendMail(schedule);
    }
}
```

### Step4. TestableBookingSchedule을 없애기 위해 Stub 사용

* 현재 날짜를 위한 SystemDateTime Class를 생성후 getNow() 이동

```java
import org.joda.time.DateTime;

public class SystemDateTime {
    public DateTime getNow() {
        return new DateTime();
    }
}
```

* BookingSchdule 에서 SystemDateTime의 getNow()를 사용 하도록 변경

```java
public class BookingScheduler {
    private SystemDateTime systemDateTime;

    public BookingScheduler(int capacityPerHour) {

        this.systemDateTime = new SystemDateTime();

    }

    public void addSchedule(Schedule schedule) {

        // 일요일에는 시스템을 오픈하지 않는다.
        DateTime now = systemDateTime.getNow();
        if (now.getDayOfWeek() == DateTimeConstants.SUNDAY) {
            throw new RuntimeException("Booking system is not available on sunday");
        }
    }
}
```

* @Mock 을 사용하여 SystemDateTime을 mocking 한후 stub 수행

* 정상적인 경우를 위해 SetUp에서 일요일이 아닌 경우로 Stub

```java

@RunWith(MockitoJUnitRunner.class)
public class BookingSchedulerWithMockTest {
    public static final DateTime NOT_SUNDAY = new DateTime(2021, 6, 14, 14, 0);

    @Mock
    SystemDateTime systemDateTime;

    @Before
    public void setUp() throws Exception {
        when(systemDateTime.getNow()).thenReturn(NOT_SUNDAY);
    }
}
```

* when().thenReturn을 사용하여 getNow 리턴값을 Sunday, Monday로 반환

```java

@RunWith(MockitoJUnitRunner.class)
public class BookingSchedulerWithMockTest {

    @Test(expected = RuntimeException.class)
    public void 현재날짜가_일요일인_경우_예약불가_예외처리() {
        //given
        DateTime sunday = new DateTime(2021, 6, 13, 12, 0);
        when(systemDateTime.getNow()).thenReturn(sunday);
        Schedule schedule = new Schedule(ON_THE_HOUR, NUMBER_OF_PEOPLE_FOR_TABLE, CUSTOMER);

        //when
        bookingScheduler.addSchedule(schedule);

        //then
    }

    @Test
    public void 현재날짜가_일요일이_아닌경우_예약가능() {
        //given
        DateTime monday = new DateTime(2021, 6, 14, 12, 0);
        when(systemDateTime.getNow()).thenReturn(monday);
        Schedule schedule = new Schedule(ON_THE_HOUR, NUMBER_OF_PEOPLE_FOR_TABLE, CUSTOMER);

        //when
        bookingScheduler.addSchedule(schedule);

        //then
        assertThat(bookingScheduler.hasSchedule(schedule), is(true));
    }
}
```

* TestableBookingScheduler 클래스 삭제 및 테스트 코드 수정

```java
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BookingSchedulerTest {
    public static final DateTime NOT_ON_THE_HOUR = new DateTime(2021, 6, 14, 13, 30);
    public static final DateTime ON_THE_HOUR = new DateTime(2021, 6, 14, 14, 0);
    public static final DateTime NOT_SUNDAY = new DateTime(2021, 6, 14, 14, 0);
    public static final int NUMBER_OF_PEOPLE_FOR_TABLE = 3;
    public static final int CAPACITY_PER_HOUR = 5;

    @InjectMocks
    BookingScheduler bookingScheduler = new BookingScheduler(CAPACITY_PER_HOUR);

    @Mock
    MailSender mailSender;

    @Mock
    SmsSender smsSender;

    @Mock
    Customer CUSTOMER;

    @Mock
    SystemDateTime systemDateTime;

    @Before
    public void setUp() throws Exception {
        when(CUSTOMER.getEmail()).thenReturn("email@google.com");
        when(systemDateTime.getNow()).thenReturn(NOT_SUNDAY);
    }

    @Test(expected = RuntimeException.class)
    public void 예약은_정시에만_가능하다_정시가_아닌경우_예약불가() {
        //given
        Schedule schedule = new Schedule(NOT_ON_THE_HOUR, NUMBER_OF_PEOPLE_FOR_TABLE, CUSTOMER);
        //when
        bookingScheduler.addSchedule(schedule);

        //then
    }

    @Test
    public void 예약은_정시에만_가능하다_정시인_경우_예약가능() {
        //given
        Schedule schdule = new Schedule(ON_THE_HOUR, NUMBER_OF_PEOPLE_FOR_TABLE, CUSTOMER);

        //when
        bookingScheduler.addSchedule(schdule);

        //then
        assertThat(bookingScheduler.hasSchedule(schdule), is(true));
    }

    @Test
    public void 시간대별_인원제한이_있다_같은_시간대에_Capacity_초과할_경우_예외발생() {
        //given
        Schedule schedule = new Schedule(ON_THE_HOUR, NUMBER_OF_PEOPLE_FOR_TABLE, CUSTOMER);
        bookingScheduler.addSchedule(schedule);

        //when
        try {
            bookingScheduler.addSchedule(schedule);
        } catch (RuntimeException e) {
            //then
            assertThat(e.getMessage(), is("Number of people is over restaurant capacity per hour"));
        }
    }

    @Test
    public void 시간대별_인원제한이_있다_시간대가_다르면_Capacity_차있어도_스케쥴_추가_성공() {
        //given
        Schedule schedule = new Schedule(ON_THE_HOUR, NUMBER_OF_PEOPLE_FOR_TABLE, CUSTOMER);
        bookingScheduler.addSchedule(schedule);

        //when
        DateTime diffrentHour = ON_THE_HOUR.plusHours(1);
        Schedule newSchdule = new Schedule(diffrentHour, NUMBER_OF_PEOPLE_FOR_TABLE, CUSTOMER);
        bookingScheduler.addSchedule(newSchdule);

        //then
        assertThat(bookingScheduler.hasSchedule(schedule), is(true));
    }

    @Test
    public void 예약완료시_SMS는_무조건_발송() {
        //given
        Schedule schedule = new Schedule(ON_THE_HOUR, NUMBER_OF_PEOPLE_FOR_TABLE, CUSTOMER);

        //when
        bookingScheduler.addSchedule(schedule);

        //then
        verify(smsSender, times(1)).send(schedule);
    }

    @Test
    public void 이메일이_없는_경우에는_이메일_미발송() {
        //given
        when(CUSTOMER.getEmail()).thenReturn(null);
        Schedule schedule = new Schedule(ON_THE_HOUR, NUMBER_OF_PEOPLE_FOR_TABLE, CUSTOMER);

        //when
        bookingScheduler.addSchedule(schedule);

        //then
        verify(mailSender, never()).sendMail(schedule);
    }

    @Test
    public void 이메일이_있는_경우에는_이메일_발송() {
        //given
        when(CUSTOMER.getEmail()).thenReturn("email@google.com");
        Schedule schedule = new Schedule(ON_THE_HOUR, NUMBER_OF_PEOPLE_FOR_TABLE, CUSTOMER);

        //when
        bookingScheduler.addSchedule(schedule);

        //then
        verify(mailSender, times(1)).sendMail(schedule);
    }

    @Test(expected = RuntimeException.class)
    public void 현재날짜가_일요일인_경우_예약불가_예외처리() {
        //given
        DateTime sunday = new DateTime(2021, 6, 13, 12, 0);
        when(systemDateTime.getNow()).thenReturn(sunday);
        Schedule schedule = new Schedule(ON_THE_HOUR, NUMBER_OF_PEOPLE_FOR_TABLE, CUSTOMER);

        //when
        bookingScheduler.addSchedule(schedule);

        //then
    }

    @Test
    public void 현재날짜가_일요일이_아닌경우_예약가능() {
        //given
        DateTime monday = new DateTime(2021, 6, 14, 12, 0);
        when(systemDateTime.getNow()).thenReturn(monday);
        Schedule schedule = new Schedule(ON_THE_HOUR, NUMBER_OF_PEOPLE_FOR_TABLE, CUSTOMER);

        //when
        bookingScheduler.addSchedule(schedule);

        //then
        assertThat(bookingScheduler.hasSchedule(schedule), is(true));
    }
}
```

[이전](08_mockito.md)