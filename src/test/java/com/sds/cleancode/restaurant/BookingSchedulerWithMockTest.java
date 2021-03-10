package com.sds.cleancode.restaurant;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BookingSchedulerWithMockTest {
    public static final DateTime ON_THE_HOUR = new DateTime(2021, 3, 15, 11, 0);
    public static final DateTime NOT_SUNDAY = new DateTime(2021, 3, 15, 0, 0);
    public static final Customer CUSTOMER = new Customer("user-name", "010-1234-5678");
    public static final int NUMBER_OF_PEOPLE_FOR_TABLE = 3;
    public static final int CAPACITY_PER_HOUR = 5;

    @Spy
    @InjectMocks
    BookingScheduler bookingScheduler = new BookingScheduler(CAPACITY_PER_HOUR);
    @Mock
    SmsSender smsSender = new SmsSender();
    @Mock
    MailSender mailSender = new MailSender();

    @Before
    public void setUp() {
        when(bookingScheduler.getNow()).thenReturn(NOT_SUNDAY);
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
        Schedule schedule = new Schedule(ON_THE_HOUR, NUMBER_OF_PEOPLE_FOR_TABLE, CUSTOMER);

        //when
        bookingScheduler.addSchedule(schedule);

        //then
        verify(mailSender, never()).sendMail(schedule);
    }

    @Test
    public void 이메일이_있는_경우에는_이메일_발송() {
        //given
        Customer customerWithEmail = new Customer("user-name", "010-1234-5678", "email@google.com");
        Schedule schedule = new Schedule(ON_THE_HOUR, NUMBER_OF_PEOPLE_FOR_TABLE, customerWithEmail);

        //when
        bookingScheduler.addSchedule(schedule);

        //then
        verify(mailSender, times(1)).sendMail(schedule);
    }

    @Test(expected = RuntimeException.class)
    public void 현재날짜가_일요일인_경우_예약불가_예외처리() {
        //given
        DateTime sunday = new DateTime(2021,3,14,0,0);
        when(bookingScheduler.getNow()).thenReturn(sunday);
        Schedule schedule = new Schedule(ON_THE_HOUR, NUMBER_OF_PEOPLE_FOR_TABLE, CUSTOMER);

        //when
        bookingScheduler.addSchedule(schedule);

        //then
    }

    @Test
    public void 현재날짜가_일요일이_아닌경우_예약가능() {
        //given
        when(bookingScheduler.getNow()).thenReturn(NOT_SUNDAY);
        Schedule schedule = new Schedule(ON_THE_HOUR, NUMBER_OF_PEOPLE_FOR_TABLE, CUSTOMER);

        //when
        bookingScheduler.addSchedule(schedule);

        //then
        assertThat(bookingScheduler.hasSchedule(schedule), is(true));
    }
}
